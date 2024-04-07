import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ChatTypeAdapter implements JsonSerializer<Chat> {
    @Override
    public JsonElement serialize(Chat chat, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("chatID", jsonSerializationContext.serialize(chat.chatID));
        jsonObject.add("userList", jsonSerializationContext.serialize(chat.userList));
        jsonObject.add("messageHistory", jsonSerializationContext.serialize(chat.messageHistory));

        return jsonObject;
    }

}