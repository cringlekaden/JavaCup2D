package Scenes;

import Components.Component;
import Components.ComponentTypeAdapter;
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

public abstract class Scene {

    protected Camera camera;
    protected Renderer renderer = new Renderer();
    protected List<Entity> entities = new ArrayList<>();
    protected Entity activeEntity = null;
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

    public Camera getCamera() {
        return camera;
    }

    public abstract void init();

    public abstract void update(float dt);

    public void sceneImgui() {
        if(activeEntity != null) {
            ImGui.begin("Inspector");
            activeEntity.imgui();
            ImGui.end();
        }
        imgui();
    }

    public void imgui() {}

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                .registerTypeAdapter(Entity.class, new EntityTypeAdapter())
                .create();
        String file = "";
        try {
            file = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(!file.equals("")) {
            Entity[] entityArray = gson.fromJson(file, Entity[].class);
            for(int i = 0; i < entityArray.length; i++) {
                addEntityToScene(entityArray[i]);
            }
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
            writer.write(gson.toJson(entities));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
