package Components;

import Components.Animations.StateMachine;
import Components.Sprites.SpriteRenderer;
import Core.Entity;
import Core.KeyListener;
import Core.MouseListener;
import Core.Window;
import Util.Settings;
import org.joml.Vector4f;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component {

    private Entity holdingEntity = null;
    private float debounceTime = 0.05f;
    private float debounce = debounceTime;

    // Track which grid cells have been placed during the current mouse press
    private final Set<Long> placedCells = new HashSet<>();
    private boolean wasMouseDown = false;

    public void pickupEntity(Entity entity) {
        if(holdingEntity != null)
            holdingEntity.destroy();
        Window.getImGuiLayer().getPropertiesWindow().setCurrentEntity(null);
        holdingEntity = entity;
        holdingEntity.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        holdingEntity.addComponent(new NonPickable());
        Window.getScene().addEntityToScene(entity);
    }

    public void place() {
        Entity entity = holdingEntity.duplicate();
        if(entity.getComponent(StateMachine.class) != null) {
            entity.getComponent(StateMachine.class).refreshTextures();
        }
        entity.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));
        entity.removeComponent(NonPickable.class);
        Window.getScene().addEntityToScene(entity);
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;
        if(holdingEntity != null) {
            float x = MouseListener.getWorldX();
            float y = MouseListener.getWorldY();
            // Compute integer cell indices for robust snapping
            int cellX = (int)Math.floor(x / Settings.GRID_WIDTH);
            int cellY = (int)Math.floor(y / Settings.GRID_HEIGHT);
            // Snap ghost entity to the center of the current cell
            holdingEntity.transform.position.x = (cellX + 0.5f) * Settings.GRID_WIDTH;
            holdingEntity.transform.position.y = (cellY + 0.5f) * Settings.GRID_HEIGHT;
            boolean mouseDown = MouseListener.mouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT);
            // On mouse press/hold: place only once per unique grid cell
            if (mouseDown) {
                long key = (((long)cellX) << 32) ^ (cellY & 0xffffffffL);
                if (!placedCells.contains(key) && !isCellOccupied(cellX, cellY)) {
                    place();
                    placedCells.add(key);
                }
            }
            // On mouse release: clear visited cells to start fresh for the next drag
            if (!mouseDown && wasMouseDown)
                placedCells.clear();
            wasMouseDown = mouseDown;
            if(KeyListener.keyPressed(GLFW_KEY_ESCAPE)) {
                holdingEntity.destroy();
                holdingEntity = null;
                placedCells.clear();
            }
        }
    }

    private boolean isCellOccupied(int cellX, int cellY) {
        float targetCenterX = (cellX + 0.5f) * Settings.GRID_WIDTH;
        float targetCenterY = (cellY + 0.5f) * Settings.GRID_HEIGHT;
        final float epsilon = 0.0001f;
        for (Core.Entity e : Window.getScene().getEntities()) {
            if (e == holdingEntity) continue;
            if (e.getComponent(NonPickable.class) != null) continue;
            float ex = e.transform.position.x;
            float ey = e.transform.position.y;
            if (Math.abs(ex - targetCenterX) <= epsilon && Math.abs(ey - targetCenterY) <= epsilon) {
                return true;
            }
        }
        return false;
    }
}
