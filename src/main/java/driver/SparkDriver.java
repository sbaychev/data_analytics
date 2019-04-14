package driver;

import static java.lang.String.format;

import data.StationReading;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.elasticsearch.common.geo.GeoHashUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Ingestion, posting and Serialization Main Driver Class.
 */
public class SparkDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkDriver.class);

    public static void main(final String... args) throws ClassNotFoundException {

        SparkConf conf = new SparkConf()
            .setAppName("spark ingestion and es processing")
            .setMaster("local[*]");

        conf.set("es.nodes", "localhost:9200");

        // Whether to discover the nodes within the ElasticSearch cluster or only to use the ones given
        conf.set("es.nodes.discovery", "false");

        // In this mode, the connector disables discovery and only connects through the declared es.nodes during all operations,
        // including reads and writes. Note that in this mode, performance is highly affected
        conf.set("es.nodes.wan.only", "true");

        // If we want based on the .csv file what we get to be what spark automatically pushes to ES index
        conf.set("es.index.auto.create", "true");
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
            .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
            .load("data_analytics/src/air_quality_2018_One_Day.csv");

        rowDataset.show();
        rowDataset.explain();
        rowDataset.schema();

        System.out.println(format("Number of Row Lines: %d", rowDataset.count()));

        LOGGER.info(format("Number of Row Lines: %d", rowDataset.count()));

        JavaRDD<StationReading> stationReadingJavaRDD = rowDataset
            .javaRDD()
            .map((Function<Row, StationReading>) SparkDriver::rowToObject)
            .filter(Objects::nonNull);

        LOGGER.info(format("Records submitted to ES: %d", stationReadingJavaRDD.count()));

        System.out.println(format("Records submitted to ES: %d", stationReadingJavaRDD.count()));

        stationReadingJavaRDD.foreach(stationReadings -> System.out.println(stationReadings.toString()));
        JavaEsSpark.saveToEs(stationReadingJavaRDD, "stations/day-readings");

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

//        [time#0,geohash#1,P1#2,P2#3,temperature#4,humidity#5,pressure#6]

        if (isGeoHashInSofia(row.getString(1))) {

            Timestamp time = row.getTimestamp(0);
            GeoPoint geoHash = GeoPoint.fromGeohash(row.getString(1));
            int P1 = row.getInt(2);
            int P2 = row.getInt(3);
            int temperature = row.getInt(4);
            int humidity = row.getInt(5);
            int pressure = row.getInt(6);

            LOGGER.info(format("time: %s geoHash: %s P1: %d P2: %d temperature: %d humidity: %d pressure: %d",
                time,
                geoHash,
                P1,
                P2,
                temperature,
                humidity,
                pressure));

            return new StationReading(time, geoHash, P1, P2, temperature, humidity, pressure);
        }
        return null;
    }
}
