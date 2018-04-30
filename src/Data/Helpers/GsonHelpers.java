package Data.Helpers;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Base64;

public class GsonHelpers {

    /**
     * Método de ajuda a criar um objeto Gson que converta byte[] para Base64 String
     * @return Objeto Gson
     */
    public static Gson buildCustomGson(){
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>() {
            public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return Base64.getDecoder().decode(json.getAsString());
            }
        });

        builder.registerTypeAdapter(byte[].class, new JsonSerializer<byte[]>() {
            public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
            }
        });

        return builder.create();
    }
}
