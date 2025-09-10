package Components;

import Core.Entity;
import Core.MouseListener;
import Core.Window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {

    private Entity holdingEntity = null;

    public void pickupEntity(Entity entity) {
        holdingEntity = entity;
        Window.getScene().addEntityToScene(entity);
    }

    public void place() {
        holdingEntity = null;
    }

    @Override
    public void update(float dt) {
        if(holdingEntity != null) {
            holdingEntity.transform.position.x = MouseListener.getOrthoX() - 32;
            holdingEntity.transform.position.y = MouseListener.getOrthoY() - 32;
            if(MouseListener.mouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
                place();
        }
    }
}
