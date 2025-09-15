package Scenes;

import Components.Component;
import Components.ComponentTypeAdapter;
import Components.Transform;
import Core.Camera;
import Core.Entity;
import Core.EntityTypeAdapter;
import Rendering.Renderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Scene {

    protected Camera camera;
    protected Renderer renderer = new Renderer();
    protected List<Entity> entities = new ArrayList<>();
    protected boolean isLoaded = false;
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

    public Entity getEntityByID(int id) {
        Optional<Entity> result = entities.stream().filter(entity -> entity.getID() == id).findFirst();
        return result.orElse(null);
    }

    public Camera getCamera() {
        return camera;
    }

    public abstract void init();

    public abstract void update(float dt);

    public abstract void render();

    public void imgui() {}

    public Entity createEntity(String name) {
        Entity entity = new Entity(name);
        entity.addComponent(new Transform());
        entity.transform = entity.getComponent(Transform.class);
        return entity;
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
            isLoaded = true;
        }
    }

    public void saveExit() {
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
}
