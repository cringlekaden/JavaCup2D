package Components.Sprites;

import JavaCup2D.Component;
import JavaCup2D.Transform;
import Rendering.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Vector4f color;
    private Sprite sprite;
    private Transform lastTransform;
    private boolean isDirty = false;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
        sprite = new Sprite(null);
    }

    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;
        color = new Vector4f(1, 1, 1, 1);
    }

    @Override
    public void start() {
        lastTransform = entity.transform.copy();
    }

    @Override
    public void update(float dt) {
        if(!lastTransform.equals(entity.transform)) {
            entity.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTextureCoords();
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        isDirty = true;
    }

    public void setColor(Vector4f color) {
        if(!this.color.equals(color)) {
            this.color.set(color);
            isDirty = true;
        }
    }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void isClean() {
        isDirty = false;
    }
}
