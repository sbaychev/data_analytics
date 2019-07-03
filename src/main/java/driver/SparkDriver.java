package driver;

import static java.lang.String.format;

import data.GeoPoint;
import data.StationReading;
import java.sql.Timestamp;
import java.util.Objects;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.elasticsearch.common.geo.GeoHashUtils;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Ingestion, posting and Serialization Main Driver Class.
 */
public class SparkDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkDriver.class);

    public static void main(final String... args) {


        SparkConf conf = new SparkConf()
            .setAppName("spark ingestion and es processing")
            .setMaster("local[*]");

        conf.set("es.nodes", "localhost:9200");
        conf.set("es.nodes.discovery", "false");
        conf.set("es.nodes.wan.only", "true");
        conf.set("es.index.auto.create", "false");
        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        conf.set("spark.kryo.registrator", CustomKryoRegistrator.class.getName());
        conf.set("spark.kryoserializer.buffer", "128m");

        SparkSession spark = SparkSession.builder()
            .config(conf)
            .getOrCreate();

        Dataset<Row> rowDataset = spark
            .read()
            .format("com.databricks.spark.csv")
            .option("header", "true")
            .option("inferSchema", "true")
            .option("mode", "DROPMALFORMED")
            .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
            .load("src/main/resources/air_quality_2018_One_Year.csv");


        LOGGER.info(format("Number of Row Lines: %d", rowDataset.count()));

        JavaRDD<StationReading> stationReadingJavaRDD = rowDataset
            .javaRDD()
            .map((Function<Row, StationReading>) SparkDriver::rowToObject)
            .filter(Objects::nonNull);

        LOGGER.info(format("Records submitted to ES: %d", stationReadingJavaRDD.count()));

        JavaEsSpark.saveToEs(stationReadingJavaRDD, "stations-year-reading/year-readings");

        spark.stop();
    }

    private static boolean isGeoHashInSofia(String geohash) {

        double latitudeSofiaCity = 42.698334;
        double longitudeSofiaCity = 23.319941;
        // Sofia Area ~ 492 km2
        double sofiaAreaRootDistance = 22;

        return distance(latitudeSofiaCity, longitudeSofiaCity,
            GeoHashUtils.decodeLatitude(geohash),
            GeoHashUtils.decodeLongitude(geohash)) <= sofiaAreaRootDistance;
    }

    private static double distance(double lat1, double lng1, double lat2, double lng2) {

        // convert latitude/longitude degrees for both coordinates
        // to radians: radian = degree * π / 180
        lat1 = Math.toRadians(lat1);
        lng1 = Math.toRadians(lng1);
        lat2 = Math.toRadians(lat2);
        lng2 = Math.toRadians(lng2);

        // calculate great-circle distance
        double distance = Math
            .acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lng1 - lng2));

        // distance in human-readable format:
        // earth's radius in km = ~6371
        return 6371 * distance;
    }

    private static StationReading rowToObject(Row row) {

        try {
            if (isGeoHashInSofia(row.getString(1))) {

                Timestamp time = row.getTimestamp(0);

                GeoPoint geopoint = new GeoPoint(
                    org.elasticsearch.common.geo.GeoPoint.fromGeohash(row.getString(1)).getLat(),
                    org.elasticsearch.common.geo.GeoPoint.fromGeohash(row.getString(1)).getLon());
                long P1 = row.getInt(2);
                long P2 = row.getInt(3);
                long temperature = row.getInt(4);
                long humidity = row.getInt(5);
                long pressure = row.getInt(6);

                return new StationReading(geopoint, humidity, P1, P2, pressure, temperature, time);
            }
        } catch (Throwable exception) { }
        return null;
    }
}


