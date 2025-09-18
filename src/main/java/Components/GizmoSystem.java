package Components;

import Components.Sprites.Spritesheet;
import Core.KeyListener;
import Core.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class GizmoSystem extends Component {

    private Spritesheet gizmos;
    private int usingGizmo = 0;

    public GizmoSystem(Spritesheet gizmos) {
        this.gizmos = gizmos;
    }

    @Override
    public void start() {
        entity.addComponent(new TranslateGizmo(gizmos.getSprite(1), Window.getImGuiLayer().getPropertiesWindow()));
        entity.addComponent(new ScaleGizmo(gizmos.getSprite(2), Window.getImGuiLayer().getPropertiesWindow()));
    }

    @Override
    public void editorUpdate(float dt) {
        if(usingGizmo == 0) {
            entity.getComponent(TranslateGizmo.class).setUsing(true);
            entity.getComponent(ScaleGizmo.class).setUsing(false);
        } else if(usingGizmo == 1) {
            entity.getComponent(TranslateGizmo.class).setUsing(false);
            entity.getComponent(ScaleGizmo.class).setUsing(true);
        }
        if(KeyListener.keyPressed(GLFW_KEY_E)) {
            usingGizmo = 0;
        } else if(KeyListener.keyPressed(GLFW_KEY_R)) {
            usingGizmo = 1;
        }
    }
}
