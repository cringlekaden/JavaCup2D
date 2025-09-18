package Physics2D.Components;

import Rendering.DebugDraw;
import org.joml.Vector2f;

public class Box2DCollider extends Collider {

    private Vector2f halfSize = new Vector2f(1.0f);
    private Vector2f origin = new Vector2f();

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(entity.transform.position).add(offset);
        DebugDraw.addBox2D(center, halfSize, entity.transform.rotation);
    }

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    public Vector2f getOrigin() {
        return origin;
    }

    public void setOrigin(Vector2f origin) {
        this.origin = origin;
    }
}
