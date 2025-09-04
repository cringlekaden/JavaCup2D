package JavaCup2D;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Entity {

    private String name;
    private List<Component> components;

    public Transform transform;

    public Entity(String name) {
        this.name = name;
        components = new ArrayList<>();
        transform = new Transform();
    }

    public Entity(String name, Transform transform) {
        this.name = name;
        components = new ArrayList<>();
        this.transform = transform;
    }

    public void update(float dt) {
        for (Component component : components) component.update(dt);
    }

    public void start() {
        for (Component component : components) component.start();
    }

    public void addComponent(Component component) {
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
}
