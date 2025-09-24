package Scenes;

import Components.Component;
import Components.ComponentTypeAdapter;
import Components.Transform;
import Core.Camera;
import Core.Entity;
import Core.EntityTypeAdapter;
import Physics2D.Physics2D;
import Rendering.Renderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {

    private Camera camera;
    private final Renderer renderer;
    private final Physics2D physics2D;
    private final SceneInitializer sceneInitializer;
    private final List<Entity> entities = new ArrayList<>();
    private boolean isRunning = false;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        physics2D = new Physics2D();
        renderer = new Renderer();
    }

    public void start() {
        for(int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.start();
            renderer.addSpriteEntity(entity);
            physics2D.add(entity);
        }
        isRunning = true;
    }

    public void init() {
        this.camera = new Camera(new Vector2f());
        sceneInitializer.loadResources(this);
        sceneInitializer.init(this);
    }

    public void update(float dt) {
        camera.setProjection();
        // Ensure inverse view is current before any world-space calculations during update
        camera.getView();
        physics2D.update(dt);
        for(int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.update(dt);
            if(entity.isDead()) {
                entities.remove(i);
                renderer.destroyEntity(entity);
                physics2D.destroyEntity(entity);
                i--;
            }
        }
    }

    public void editorUpdate(float dt) {
        camera.setProjection();
        // Ensure inverse view is current before any world-space calculations during editor update
        camera.getView();
        for(int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.editorUpdate(dt);
            if(entity.isDead()) {
                entities.remove(i);
                renderer.destroyEntity(entity);
                physics2D.destroyEntity(entity);
                i--;
            }
        }
    }

    public void render() {
        renderer.render();
    }

    public void imgui() {
        sceneInitializer.imgui();
    }

    public void destroy() {
        for(Entity entity : entities) {
            entity.destroy();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                .registerTypeAdapter(Entity.class, new EntityTypeAdapter())
                .create();
        String file;
        try {
            file = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(!file.equals("")) {
            int maxEntityID = -1;
            int maxComponentID = -1;
            Entity[] entityArray = gson.fromJson(file, Entity[].class);
            for(int i = 0; i < entityArray.length; i++) {
                addEntityToScene(entityArray[i]);
                for(Component component : entityArray[i].getAllComponents()) {
                    if(component.getID() > maxComponentID)
                        maxComponentID = component.getID();
                }
                if(entityArray[i].getID() > maxEntityID)
                    maxEntityID = entityArray[i].getID();
            }
            maxEntityID++;
            maxComponentID++;
            Entity.init(maxEntityID);
            Component.init(maxComponentID);
        }
    }

    public void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                .registerTypeAdapter(Entity.class, new EntityTypeAdapter())
                .create();
        try {
            FileWriter writer = new FileWriter("level.txt");
            List<Entity> serializedEntities = new ArrayList<>();
            for(Entity e : entities) {
                if(e.doSerialize())
                    serializedEntities.add(e);
            }
            writer.write(gson.toJson(serializedEntities));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Entity createEntity(String name) {
        Entity entity = new Entity(name);
        entity.addComponent(new Transform());
        entity.transform = entity.getComponent(Transform.class);
        return entity;
    }

    public void addEntityToScene(Entity entity) {
        if(!isRunning)
            entities.add(entity);
        else {
            entities.add(entity);
            entity.start();
            renderer.addSpriteEntity(entity);
            physics2D.add(entity);
        }
    }

    public Entity getEntityByID(int id) {
        Optional<Entity> result = entities.stream().filter(entity -> entity.getID() == id).findFirst();
        return result.orElse(null);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Camera getCamera() {
        return camera;
    }
}
