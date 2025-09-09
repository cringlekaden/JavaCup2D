package Components.Sprites;

import Rendering.Texture;
import org.joml.Vector2f;

public class Sprite {

    private Texture texture = null;
    private Vector2f[] textureCoords = new Vector2f[]{new Vector2f(1, 1), new Vector2f(1, 0), new Vector2f(0, 0), new Vector2f(0, 1)};
    private float width, height;

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector2f[] getTextureCoords() {
        return textureCoords;
    }

    public void setTextureCoords(Vector2f[] textureCoords) {
        this.textureCoords = textureCoords;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getTextureID() {
        return texture == null ? -1 : texture.getTextureID();
    }
}
