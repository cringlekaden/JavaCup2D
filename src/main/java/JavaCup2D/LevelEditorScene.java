package JavaCup2D;

import Components.Sprite;
import Util.AssetPool;
import org.joml.Vector2f;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {

    }

    @Override
    protected void init() {
        this.camera = new Camera(new Vector2f(-250, 0));
        Entity test1 = new Entity("Test1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        test1.addComponent(new Sprite(AssetPool.getTexture("testImage.png")));
        addEntityToScene(test1);
        Entity test2 = new Entity("Test2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        test2.addComponent(new Sprite(AssetPool.getTexture("testImage2.png")));
        addEntityToScene(test2);
        loadResources();
    }

    @Override
    public void update(float dt) {
        System.out.println((int)(1.0f/dt) + " FPS");
        for(Entity e : entities)
            e.update(dt);
        renderer.render();
    }

    private void loadResources() {
        AssetPool.getShader("default");
    }
}
