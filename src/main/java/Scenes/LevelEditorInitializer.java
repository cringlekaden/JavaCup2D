package Scenes;

import Components.*;
import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import Components.Sprites.Spritesheet;
import Core.*;
import Util.AssetPool;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

public class LevelEditorInitializer extends SceneInitializer {

    private Spritesheet sprites;
    private Entity levelEditorStuff;

    public LevelEditorInitializer() {

    }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("default");
        AssetPool.addSpritesheet("decorationsAndBlocks.png", new Spritesheet(AssetPool.getTexture("decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.addSpritesheet("gizmos.png", new Spritesheet(AssetPool.getTexture("gizmos.png"), 24, 48, 3, 0));
        for(Entity e : scene.getEntities()) {
            if(e.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spriteRenderer = e.getComponent(SpriteRenderer.class);
                if(spriteRenderer.getTexture() != null)
                    spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilename()));
            }
        }
    }

    @Override
    public void init(Scene scene) {
        sprites = AssetPool.getSpriteSheet("decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpriteSheet("gizmos.png");
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
        ImGui.begin("Assets:");
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);
        float windowX2 = windowPos.x + windowSize.x;
        for(int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int id = sprite.getTextureID();
            Vector2f[] texCoords = sprite.getTextureCoords();
            if(ImGui.imageButton("imageButton"+i, id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                Entity entity = Prefabs.generateSpriteEntity(sprite, 32, 32);
                levelEditorStuff.getComponent(MouseControls.class).pickupEntity(entity);
            }
            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if(i + 1 < sprites.size() && nextButtonX2 < windowX2)
                ImGui.sameLine();
        }
        ImGui.end();
    }
}
