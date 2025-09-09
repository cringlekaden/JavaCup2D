package JavaCup2D;

import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import Components.Sprites.Spritesheet;
import Rendering.Texture;
import Util.AssetPool;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class LevelEditorScene extends Scene {

    private Entity test1;

    public LevelEditorScene() {

    }

    @Override
    protected void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        Spritesheet spritesheet = AssetPool.getSpriteSheet("spritesheet.png");
        assert spritesheet != null;
        test1 = new Entity("Test1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 0);
        test1.addComponent(new SpriteRenderer(new Vector4f(1, 0, 1, 1)));
        addEntityToScene(test1);
        Entity test2 = new Entity("Test2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 1);
        test2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("blendImage2.png"))));
        addEntityToScene(test2);
        activeEntity = test1;
    }

    @Override
    public void update(float dt) {
        System.out.println((int)(1.0f/dt) + " FPS");
        for(Entity e : entities)
            e.update(dt);
        renderer.render();
    }

    @Override public void imgui() {}

    private void loadResources() {
        AssetPool.getShader("default");
        AssetPool.addSpritesheet("spritesheet.png", new Spritesheet(AssetPool.getTexture("spritesheet.png"), 16, 16, 26, 0));
    }
}
