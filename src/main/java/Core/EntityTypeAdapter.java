package Core;

import Components.Component;
import Components.Transform;
import com.google.gson.*;

import java.lang.reflect.Type;

public class EntityTypeAdapter  implements JsonDeserializer<Entity> {
    @Override
    public Entity deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");
        Entity entity = new Entity(name);
        for(JsonElement e : components) {
            Component c = jsonDeserializationContext.deserialize(e, Component.class);
            entity.addComponent(c);
        }
        entity.transform = entity.getComponent(Transform.class);
        return entity;
    }
}
