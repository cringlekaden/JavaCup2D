package JavaCup2D;

import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import Components.Sprites.Spritesheet;
import Rendering.Texture;
import Util.AssetPool;
import org.joml.Vector2f;

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
        test1 = new Entity("Test1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        test1.addComponent(new SpriteRenderer(spritesheet.getSprite(15)));
        addEntityToScene(test1);
        Entity test2 = new Entity("Test2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        test2.addComponent(new SpriteRenderer(spritesheet.getSprite(10)));
        addEntityToScene(test2);
    }

    @Override
    public void update(float dt) {
        test1.transform.position.x += 10 * dt;
        System.out.println((int)(1.0f/dt) + " FPS");
        for(Entity e : entities)
            e.update(dt);
        renderer.render();
    }

    private void loadResources() {
        AssetPool.getShader("default");
        AssetPool.addSpritesheet("spritesheet.png", new Spritesheet(AssetPool.getTexture("spritesheet.png"), 16, 16, 26, 0));
    }
}
