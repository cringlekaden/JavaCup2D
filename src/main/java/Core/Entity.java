package Core;

import Components.Component;
import Components.Transform;
import imgui.ImGui;

import java.util.*;

public class Entity {

    private static int ID_COUNTER = 0;
    private String name;
    private List<Component> components;
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

    public static void init(int maxID) {
        ID_COUNTER = maxID;
    }
}
