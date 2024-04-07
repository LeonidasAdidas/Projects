import com.google.gson.*;

import java.lang.reflect.Type;
public class DataSerializerTypeAdadpter implements JsonSerializer<Data>{
    @Override
    public JsonElement serialize(Data src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        if (src instanceof NameData) {
            NameData nameData = (NameData) src;
            jsonObject.addProperty("type", "NameData");
            jsonObject.addProperty("name", nameData.name);
        } else if (src instanceof ClientDumpData) {
            ClientDumpData clientDumpData = (ClientDumpData) src;
            jsonObject.addProperty("type", "ClientDumpData");
            jsonObject.add("user", context.serialize(clientDumpData.user));
            jsonObject.add("chatList", context.serialize(clientDumpData.chatList));
        }
        return jsonObject;
    }
}
