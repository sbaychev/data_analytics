package data;

import java.io.Serializable;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Class.
 */
@Data
@AllArgsConstructor
public final class StationReading implements Serializable {

    private static final long serialVersionUID = 7993796581494284298L;

    /**
     * lat&lon location of station.
     */
    private GeoPoint geopoint;

    /**
     * humidity.
     */
    private long humidity;

    /**
     * PM1.
     */
    private long P1;

    /**
     * PM2.
     */
    private long P2;

    /**
     * pressure.
     */
    private long pressure;

    /**
     * temperature.
     */
    private long temperature;

    /**
     * time of entry.
     */
    private Timestamp time;
}
