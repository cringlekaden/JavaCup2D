package Components.Sprites;

import Components.Component;
import Core.Transform;
import Rendering.Texture;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();
    private transient Transform lastTransform;
    private transient boolean isDirty = true;

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

    @Override
    public void imgui() {
        float[] imColor = { color.x, color.y, color.z, color.w };
        if(ImGui.colorPicker4("Color Picker: ", imColor))
            setColor(new Vector4f(imColor[0], imColor[1], imColor[2], imColor[3]));
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
