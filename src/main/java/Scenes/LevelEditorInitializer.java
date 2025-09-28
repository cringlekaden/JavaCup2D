package Scenes;

import Audio.Track;
import Components.*;
import Components.Animations.StateMachine;
import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import Components.Sprites.Spritesheet;
import Core.*;
import Util.AssetPool;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

import java.io.File;
import java.util.Collection;

public class LevelEditorInitializer extends SceneInitializer {

    private Spritesheet sprites;
    private Entity levelEditorStuff;

    public LevelEditorInitializer() {}

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("default");
        AssetPool.addSpritesheet("decorationsAndBlocks.png", new Spritesheet(AssetPool.getTexture("decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.addSpritesheet("spritesheet.png", new Spritesheet(AssetPool.getTexture("spritesheet.png"), 16, 16, 26, 0));
        AssetPool.addSpritesheet("items.png", new Spritesheet(AssetPool.getTexture("items.png"), 16, 16, 43, 0));
        AssetPool.addSpritesheet("gizmos.png", new Spritesheet(AssetPool.getTexture("gizmos.png"), 24, 48, 3, 0));
        File audioFolder = new File("./assets/audio");
        File[] audioFiles = audioFolder.listFiles();
        for(File file : audioFiles)
            AssetPool.addTrack(file.getName(), false);
        for(Entity e : scene.getEntities()) {
            if(e.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spriteRenderer = e.getComponent(SpriteRenderer.class);
                if(spriteRenderer.getTexture() != null)
                    spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilename()));
            }
            if(e.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = e.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }

    @Override
    public void init(Scene scene) {
        sprites = AssetPool.getSpritesheet("decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpritesheet("gizmos.png");
        levelEditorStuff = scene.createEntity("LevelEditorStuff");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addEntityToScene(levelEditorStuff);
    }

    @Override
    public void imgui() {
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();
        ImGui.begin("Test window");
        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("Blocks")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);
                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size(); i++) {
                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTextureID();
                    Vector2f[] texCoords = sprite.getTextureCoords();
                    ImGui.pushID(i);
                    if (ImGui.imageButton("imageButton" + i, id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        Entity entity = Prefabs.generateSpriteEntity(sprite, 0.25f, 0.25f);
                        levelEditorStuff.getComponent(MouseControls.class).pickupEntity(entity);
                    }
                    ImGui.popID();
                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2)
                        ImGui.sameLine();
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Prefabs")) {
                Spritesheet playerSprites = AssetPool.getSpritesheet("spritesheet.png");
                Sprite sprite = playerSprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * 4;
                float spriteHeight = sprite.getHeight() * 4;
                int id = sprite.getTextureID();
                Vector2f[] texCoords = sprite.getTextureCoords();

                if (ImGui.imageButton("imageButton" + id, id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    Entity entity = Prefabs.generateMario();
                    levelEditorStuff.getComponent(MouseControls.class).pickupEntity(entity);
                }
                ImGui.sameLine();
                Spritesheet items = AssetPool.getSpritesheet("items.png");
                sprite = items.getSprite(0);
                id = sprite.getTextureID();
                texCoords = sprite.getTextureCoords();
                if (ImGui.imageButton("imageButton" + id, id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    Entity entity = Prefabs.generateQuestionBlock();
                    levelEditorStuff.getComponent(MouseControls.class).pickupEntity(entity);
                }
                ImGui.endTabItem();
            }
            if(ImGui.beginTabItem("Tracks")) {
                Collection<Track> tracks = AssetPool.getAllTracks();
                for(Track track : tracks) {
                    if(ImGui.button(track.getFilename())) {
                        if(!track.isPlaying())
                            track.play();
                        else
                            track.stop();
                    }
                    if(ImGui.getContentRegionAvailX() > 100)
                        ImGui.sameLine();
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }
}
