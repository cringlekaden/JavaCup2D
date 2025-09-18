package Physics2D.Components;

import Components.Component;

public class Circle2DCollider extends Collider {

    private float radius = 1.0f;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
