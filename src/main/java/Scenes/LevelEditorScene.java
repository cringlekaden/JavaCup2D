package Scenes;

import Components.EditorCamera;
import Components.GridLines;
import Components.MouseControls;
import Components.Rigidbody;
import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import Components.Sprites.Spritesheet;
import Core.Camera;
import Core.Entity;
import Core.Prefabs;
import Core.Transform;
import Rendering.DebugDraw;
import Util.AssetPool;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene {

    private Spritesheet sprites;
    private Entity levelEditorStuff = new Entity("levelEditorStuff", new Transform(), 0);

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpriteSheet("decorationsAndBlocks.png");
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(camera));
        if(isLoaded) {
            return;
        }
    }

    @Override
    public void update(float dt) {
        levelEditorStuff.update(dt);
        for(Entity e : entities)
            e.update(dt);
    }

    @Override
    public void render() {
        renderer.render();
    }

    @Override
    public void imgui() {
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
                Entity entity = Prefabs.generateSpriteObject(sprite, 32, 32);
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

    private void loadResources() {
        AssetPool.getShader("default");
        AssetPool.addSpritesheet("decorationsAndBlocks.png", new Spritesheet(AssetPool.getTexture("decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.getTexture("blendImage2.png");
        for(Entity e : entities) {
            if(e.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spriteRenderer = e.getComponent(SpriteRenderer.class);
                if(spriteRenderer.getTexture() != null)
                    spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilename()));
            }
        }
    }
}
