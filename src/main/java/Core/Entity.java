package Core;

import Components.Component;
import Components.ComponentTypeAdapter;
import Components.Sprites.SpriteRenderer;
import Components.Transform;
import Util.AssetPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;

import java.util.*;

public class Entity {

    private static int ID_COUNTER = 0;
    private final String name;
    private final List<Component> components;
    private int uid = -1;
    private boolean doSerialize = true;
    private boolean isDead = false;
    public transient Transform transform;

    public Entity(String name) {
        this.name = name;
        components = new ArrayList<>();
        uid = ID_COUNTER++;
    }

    public void start() {
        for(int i = 0; i < components.size(); i++)
            components.get(i).start();
    }

    public void update(float dt) {
        for (Component component : components)
            component.update(dt);
    }

    public void editorUpdate(float dt) {
        for (Component component : components)
            component.editorUpdate(dt);
    }

    public void imgui() {
        for(Component c : components) {
            if(ImGui.collapsingHeader(c.getClass().getSimpleName()))
                c.imgui();
        }
    }

    public void destroy() {
        isDead = true;
        for(int i = 0; i < components.size(); i++) {
            components.get(i).destroy();
        }
    }

    public Entity duplicate() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Component.class, new ComponentTypeAdapter()).registerTypeAdapter(Entity.class, new EntityTypeAdapter()).create();
        String entityJson = gson.toJson(this);
        Entity duplicate = gson.fromJson(entityJson, Entity.class);
        duplicate.generateID();
        for(Component component : duplicate.getAllComponents())
            component.generateID();
        SpriteRenderer spriteRenderer = duplicate.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null && spriteRenderer.getTexture() != null)
            spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilename()));
        return duplicate;
    }

    public void addComponent(Component component) {
        component.generateID();
        components.add(component);
        component.entity = this;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for(Component c : components) {
            if(componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error casting component...";
                }
            }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for(int i = 0; i < components.size(); i++) {
            if(componentClass.isAssignableFrom(components.get(i).getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public List<Component> getAllComponents() {
        return components;
    }

    public void setNoSerialize() {
        doSerialize = false;
    }

    public boolean doSerialize() {
        return doSerialize;
    }

    public int getID() {
        return uid;
    }

    public void generateID() {
        uid = ID_COUNTER++;
    }

    public static void init(int maxID) {
        ID_COUNTER = maxID;
    }

    public String getName() {
        return name;
    }
}
