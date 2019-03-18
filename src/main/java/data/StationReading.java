package data;

import java.io.Serializable;
import java.time.LocalDateTime;
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
     * time of entry .
     */
    private LocalDateTime time;

    /**
     * geohash location of station.
     */
    private String geoHash;

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
