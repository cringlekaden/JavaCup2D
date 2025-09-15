package Physics2D.Components;

import Components.Component;
import org.joml.Vector2f;

public class Box2DCollider extends Component {

    private Vector2f halfSize = new Vector2f(1.0f);

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }
}
