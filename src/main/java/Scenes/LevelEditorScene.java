package Scenes;

import Components.MouseControls;
import Components.Rigidbody;
import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import Components.Sprites.Spritesheet;
import Core.Camera;
import Core.Entity;
import Core.Prefabs;
import Core.Transform;
import Util.AssetPool;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene {

    private Spritesheet sprites;
    private Entity test1;
    private MouseControls mouseControls = new MouseControls();

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpriteSheet("decorationsAndBlocks.png");
        if(isLoaded) {
            activeEntity = entities.getFirst();
            return;
        }
        test1 = new Entity("Test1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 0);
        SpriteRenderer test1sprite = new SpriteRenderer();
        test1sprite.setColor(new Vector4f(1, 0, 1, 1));
        test1.addComponent(test1sprite);
        test1.addComponent(new Rigidbody());
        addEntityToScene(test1);
        Entity test2 = new Entity("Test2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 1);
        SpriteRenderer test2sprite = new SpriteRenderer();
        Sprite testsprite = new Sprite();
        testsprite.setTexture(AssetPool.getTexture("blendImage2.png"));
        test2sprite.setSprite(testsprite);
        test2.addComponent(test2sprite);
        addEntityToScene(test2);
        activeEntity = test1;
    }

    @Override
    public void update(float dt) {
        mouseControls.update(dt);
        for(Entity e : entities)
            e.update(dt);
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
            if(ImGui.imageButton("imageButton"+i, id, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {
                Entity entity = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                mouseControls.pickupEntity(entity);
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
    }
}
