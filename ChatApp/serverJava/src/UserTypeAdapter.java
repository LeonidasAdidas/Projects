import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class UserTypeAdapter implements JsonSerializer<User> {
    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("userID", jsonSerializationContext.serialize(user.userID));
        jsonObject.add("name", jsonSerializationContext.serialize(user.name));

        return jsonObject;
    }
}