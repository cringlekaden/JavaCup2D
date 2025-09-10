package Core;

import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import org.joml.Vector2f;

public class Prefabs {

    public static Entity generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        Entity entity = new Entity("GeneratedSpriteEntity", new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)), 0);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        entity.addComponent(renderer);
        return entity;
    }
}
