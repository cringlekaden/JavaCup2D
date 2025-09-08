package JavaCup2D;

import org.joml.Vector2f;

public class Transform {

    public Vector2f position;
    public Vector2f scale;

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }

    public Transform copy() {
        return new Transform(new Vector2f(position), new Vector2f(scale));
    }

    public void copy(Transform to) {
        to.position.set(position);
        to.scale.set(scale);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!(o instanceof Transform)) return false;
        Transform other = (Transform) o;
        return other.position.x == position.x && other.scale.x == scale.x && other.position.y == position.y && other.scale.y == scale.y;
    }
}
