package JavaCup2D;

import Components.Sprite;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {

    }

    @Override
    protected void init() {
        camera = new Camera(new Vector2f());
        int xOffset = 10;
        int yOffset = 10;
        float width = (float) (600 - xOffset * 2);
        float height = (float) (300 - yOffset * 2);
        float sizeX = width / 100.0f;
        float sizeY = height / 100.0f;
        for(int x = 0; x < 100; x++) {
            for(int y = 0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX);
                float yPos = yOffset + (y * sizeY);
                Entity entity = new Entity("Object" + x + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                entity.addComponent(new Sprite(new Vector4f(xPos / width, yPos / height, 1, 1)));
                addEntityToScene(entity);
            }
        }
    }

    @Override
    public void update(float dt) {
        System.out.println((int)(1.0f/dt) + " FPS");
        for(Entity e : entities)
            e.update(dt);
        renderer.render();
    }
}
