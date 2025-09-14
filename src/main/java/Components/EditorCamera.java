package Components;

import Core.Camera;
import Core.KeyListener;
import Core.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component {

    private Camera camera;
    private Vector2f clickOrigin;
    private float dragDebounce = 0.03f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.05f;
    private float lerpTime = 0.0f;
    private boolean reset = false;

    public EditorCamera(Camera camera) {
        this.camera = camera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void update(float dt) {
        if(MouseListener.mouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT) && dragDebounce > 0) {
            clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= dt;
            return;
        } else if(MouseListener.mouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT)) {
            Vector2f currentPosition = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f delta = new Vector2f(currentPosition).sub(clickOrigin);
            camera.getPosition().sub(delta.mul(dt).mul(dragSensitivity));
            clickOrigin.lerp(currentPosition, dt);
        }
        if(dragDebounce <= 0.0f && !MouseListener.mouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
            dragDebounce = 0.03f;
        if(MouseListener.getScrollY() != 0.0f) {
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity), 1 / camera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            camera.addZoom(addValue);
        }
        if(KeyListener.keyPressed(GLFW_KEY_SPACE))
            reset = true;
        if(reset) {
            camera.getPosition().lerp(new Vector2f(0, 0), lerpTime);
            camera.setZoom(camera.getZoom() + (1.0f - camera.getZoom()) * lerpTime);
            lerpTime += 0.1f * dt;
            if(Math.abs(camera.getPosition().x) <= 5.0f && Math.abs(camera.getPosition().y) <= 5.0f) {
                camera.getPosition().set(0, 0);
                camera.setZoom(1.0f);
                reset = false;
                lerpTime = 0.0f;
            }
        }
    }
}
