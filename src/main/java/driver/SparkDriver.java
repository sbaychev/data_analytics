package driver;

import static java.lang.String.format;

import data.StationReading;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
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

        //Whether to discover the nodes within the ElasticSearch cluster or only to use the ones given
        conf.set("es.nodes.discovery", "false");

        // In this mode, the connector disables discovery and only connects through the declared es.nodes during all operations,
        // including reads and writes. Note that in this mode, performance is highly affected
        conf.set("es.nodes.wan.only", "true");

        //if we want based on the .csv file what we get to be what spark automatically pushes to ES
        conf.set("es.index.auto.create", "true");
        conf.set("spark.serializer","org.apache.spark.serializer.KryoSerializer");
        conf.set("spark.kryo.registrator", CustomKryoRegistrator.class.getName());
        conf.set("spark.kryoserializer.buffer", "128m");
//        conf.registerKryoClasses(new Class<?>[]{
//            Class.forName("com.databricks.spark.csv.DefaultSource")});
//        conf.set("spark.kryo.registrationRequired", "true");

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
        JavaRDD<StationReading> stationReadingJavaRDD = rowDataset
            .javaRDD()
            .map((Function<Row, StationReading>) row -> {

                String time = row.getTimestamp(0).toString();
                String geoHash = row.getString(1);
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
            });

        LOGGER.info(format("Number of Row Lines | Records submitted to ES: %d", stationReadingJavaRDD.count()));

        JavaEsSpark.saveToEs(stationReadingJavaRDD, "stations/day-readings");

        spark.stop();
    }
}
