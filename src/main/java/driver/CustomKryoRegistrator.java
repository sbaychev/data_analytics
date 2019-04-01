package driver;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import data.StationReading;
import org.apache.spark.serializer.KryoRegistrator;

/**
 * Custom Serialization of Data.
 */
public class CustomKryoRegistrator implements KryoRegistrator {

    @Override
    public void registerClasses(Kryo kryo) {

        kryo.register(StationReading.class, new FieldSerializer(kryo, StationReading.class));

    }
}
