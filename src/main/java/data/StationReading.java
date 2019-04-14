package data;

import java.io.Serializable;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.elasticsearch.common.geo.GeoPoint;

/**
 * Data Class.
 */
@Data
@AllArgsConstructor
public final class StationReading implements Serializable {

    private static final long serialVersionUID = 7993796581494284298L;

    /**
     * time of entry .
     */
    private Timestamp time;

    /**
     * geohash location of station.
     */
    private GeoPoint geoHash;

    /**
     * PM1 .
     */
    private int P1;

    /**
     * PM2.
     */
    private int P2;

    /**
     * temperature.
     */
    private int temperature;

    /**
     * humidity.
     */
    private int humidity;

    /**
     * pressure.
     */
    private int pressure;

}
