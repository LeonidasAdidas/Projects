import com.google.gson.*;

import java.lang.reflect.Type;

public class DataTypeAdapter implements JsonDeserializer<DataPacket> {

    @Override
    public DataPacket deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int senderID = jsonObject.get("senderID").getAsInt();
        DataType dataType = jsonDeserializationContext.deserialize(jsonObject.get("dataType"), DataType.class);

        Data content = null;
        if (dataType == DataType.LOGIN) {
            content = jsonDeserializationContext.deserialize(jsonObject.get("content"), NameData.class);
            return new DataPacket(senderID, dataType, content);
        }
        else if (dataType == DataType.MESSAGE) {
            content = jsonDeserializationContext.deserialize(jsonObject.get("content"), Message.class);
            return new DataPacket(senderID, dataType, content);
        }
        else if (dataType == DataType.CLIENTDATA) {
            content = jsonDeserializationContext.deserialize(jsonObject.get("content"), ClientDumpData.class);
            return new DataPacket(senderID, dataType, content);
        }
        else if (dataType == DataType.CHATIDDATA) {
            content = jsonDeserializationContext.deserialize(jsonObject.get("content"), ChatIDData.class);
            return new DataPacket(senderID, dataType, content);
        }
        else if (dataType == DataType.CHATREQUEST) {
            content = jsonDeserializationContext.deserialize(jsonObject.get("content"), ChatRequestData.class);
            return new DataPacket(senderID, dataType, content);
        }
        else if (dataType == DataType.NEWCHAT) {
            content = jsonDeserializationContext.deserialize(jsonObject.get("content"), NewChatData.class);
            return new DataPacket(senderID, dataType, content);
        }
        else {
            throw new JsonParseException("content is bad");
        }

    }

}
