package data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeoPoint implements Serializable {

    private static final long serialVersionUID = 1193791281494284298L;

    private double lat;
    private double lon;

}
