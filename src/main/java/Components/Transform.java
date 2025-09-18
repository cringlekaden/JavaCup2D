package Components;

import Editor.JCImGui;
import org.joml.Vector2f;

public class Transform extends Component {

    public Vector2f position;
    public float rotation;
    public Vector2f scale;
    public int zIndex;

    public Transform() {
        init(new Vector2f(), 0, new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, 0, new Vector2f());
    }

    public Transform(Vector2f position, float rotation) {
        init(position, rotation, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, 0, scale);
    }

    public Transform(Vector2f position, float rotation, Vector2f scale) {
        init(position, rotation, scale);
    }

    public void init(Vector2f position, float rotation, Vector2f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.zIndex = 0;
    }

    @Override
    public void imgui() {
        JCImGui.drawVec2Control("Position", position);
        JCImGui.drawVec2Control("Scale", scale, 32);
        rotation = JCImGui.drawFloatControl("Rotation", rotation);
        zIndex = JCImGui.drawIntControl("zIndex", zIndex);
    }

    public Transform copy() {
        return new Transform(new Vector2f(position), Float.valueOf(rotation), new Vector2f(scale));
    }

    public void copy(Transform to) {
        to.position.set(position);
        to.rotation = Float.valueOf(rotation);
        to.scale.set(scale);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!(o instanceof Transform other)) return false;
        return other.position.x == position.x && other.scale.x == scale.x && other.position.y == position.y && other.scale.y == scale.y
                && other.rotation == rotation && other.zIndex == zIndex;
    }
}
