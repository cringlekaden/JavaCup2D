package Components;

import JavaCup2D.Component;
import Rendering.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Sprite extends Component {

    private Vector4f color;
    private Texture texture;

    public Sprite(Vector4f color) {
        this.color = color;
        texture = null;
    }

    public Sprite(Texture texture) {
        this.texture = texture;
        color = new Vector4f(1, 1, 1, 1);
    }

    @Override
    public void start() {
    }

    @Override
    public void update(float dt) {
    }

    public Vector4f getColor() {
        return color;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTexCoords() {
        Vector2f[] texCoords = {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
        return texCoords;
    }
}
