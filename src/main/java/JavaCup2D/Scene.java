package JavaCup2D;

import Rendering.Renderer;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Camera camera;
    protected Renderer renderer = new Renderer();
    protected List<Entity> entities = new ArrayList<>();
    private boolean isRunning = false;

    public Scene() {
    }

    public void start() {
        for(Entity entity : entities) {
            entity.start();
            renderer.add(entity);
        }
        isRunning = true;
    }

    public void addEntityToScene(Entity entity) {
        if(!isRunning)
            entities.add(entity);
        else {
            entities.add(entity);
            entity.start();
            renderer.add(entity);
        }
    }

    public Camera getCamera() {
        return camera;
    }

    protected abstract void init();

    protected abstract void update(float dt);
}
