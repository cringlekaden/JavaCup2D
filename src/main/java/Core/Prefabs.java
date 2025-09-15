package Core;

import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;

public class Prefabs {

    public static Entity generateSpriteEntity(Sprite sprite, float sizeX, float sizeY) {
        return generateSpriteEntity(sprite, sizeX, sizeY, 0);
    }

    public static Entity generateSpriteEntity(Sprite sprite, float sizeX, float sizeY, int zIndex) {
        Entity entity = Window.getScene().createEntity("GeneratedSpriteEntity");
        entity.transform.scale.x = sizeX;
        entity.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        entity.addComponent(renderer);
        return entity;
    }
}
