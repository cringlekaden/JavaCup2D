package Core;

import Components.Component;

import java.util.*;

public class Entity {

    private static int ID_COUNTER = 0;
    private int uid = -1;
    private String name;
    private List<Component> components;
    private int zIndex;
    public Transform transform;

    public Entity(String name, Transform transform, int zIndex) {
        this.name = name;
        this.transform = transform;
        this.zIndex = zIndex;
        components = new ArrayList<>();
        uid = ID_COUNTER++;
    }

    public void update(float dt) {
        for (Component component : components) component.update(dt);
    }

    public void start() {
        for (Component component : components) component.start();
    }

    public void imgui() {
        for(Component c : components)
            c.imgui();
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

    public List<Component> getAllComponents() {
        return components;
    }

    public int zIndex() {
        return zIndex;
    }

    public int getID() {
        return uid;
    }

    public static void init(int maxID) {
        ID_COUNTER = maxID;
    }
}
