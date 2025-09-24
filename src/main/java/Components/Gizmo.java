package Components;

import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import Core.*;
import Editor.PropertiesWindow;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component {

    private final Vector4f xAxisColor = new Vector4f(0.8f, 0.1f, 0.15f, 1.0f);
    private final Vector4f xAxisColorHover = new Vector4f(0.9f, 0.2f, 0.2f, 1.0f);
    private final Vector4f yAxisColor = new Vector4f(0.2f, 0.7f, 0.2f, 1.0f);
    private final Vector4f yAxisColorHover = new Vector4f(0.3f, 0.8f, 0.3f, 1.0f);
    private final Vector2f xAxisOffset = new Vector2f(24.0f / 80.0f, -6.0f / 80.0f);
    private final Vector2f yAxisOffset = new Vector2f(-7.0f / 80.0f, 21.0f / 80.0f);
    private final Entity xAxisEntity;
    private final Entity yAxisEntity;
    private final SpriteRenderer xSprite;
    private final SpriteRenderer ySprite;
    private final PropertiesWindow propertiesWindow;
    private final float gizmoWidth = 16 / 80.0f;
    private final float gizmoHeight = 48 / 80.0f;
    private boolean using = false;
    protected Entity currentEntity;
    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    public Gizmo(Sprite gizmoSprite, PropertiesWindow propertiesWindow) {
        // Ensure gizmos render on top by assigning a high z-index
        xAxisEntity = Prefabs.generateSpriteEntity(gizmoSprite, gizmoWidth, gizmoHeight);
        yAxisEntity = Prefabs.generateSpriteEntity(gizmoSprite, gizmoWidth, gizmoHeight);
        xSprite = xAxisEntity.getComponent(SpriteRenderer.class);
        ySprite = yAxisEntity.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;
        xAxisEntity.addComponent(new NonPickable());
        yAxisEntity.addComponent(new NonPickable());
        Window.getScene().addEntityToScene(xAxisEntity);
        Window.getScene().addEntityToScene(yAxisEntity);
    }

    @Override
    public void start() {
        xAxisEntity.transform.rotation = 90;
        yAxisEntity.transform.rotation = 180;
        xAxisEntity.transform.zIndex = 100;
        yAxisEntity.transform.zIndex = 100;
        xAxisEntity.setNoSerialize();
        yAxisEntity.setNoSerialize();
    }

    @Override
    public void update(float dt) {
        if(using)
            setInactive();
        xAxisEntity.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
        yAxisEntity.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
    }

    @Override
    public void editorUpdate(float dt) {
        if(!using) return;
        currentEntity = propertiesWindow.getCurrentEntity();
        if(currentEntity != null) {
            setActive();
            if(KeyListener.keyPressed(GLFW_KEY_LEFT_SUPER) && KeyListener.keyBeginPress(GLFW_KEY_D)) {
                Entity duplicate = currentEntity.duplicate();
                duplicate.transform.position.add(0.1f, 0.1f);
                Window.getScene().addEntityToScene(duplicate);
                propertiesWindow.setCurrentEntity(duplicate);
                return;
            } else if(KeyListener.keyBeginPress(GLFW_KEY_BACKSPACE)) {
                currentEntity.destroy();
                setInactive();
                propertiesWindow.setCurrentEntity(null);
                return;
            }
        }
        else {
            setInactive();
            return;
        }
        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();
        if((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = false;
            yAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }
        if(currentEntity != null) {
            xAxisEntity.transform.position.set(currentEntity.transform.position);
            yAxisEntity.transform.position.set(currentEntity.transform.position);
            xAxisEntity.transform.position.add(xAxisOffset);
            yAxisEntity.transform.position.add(yAxisOffset);
        }
    }

    public void setUsing(boolean using) {
        this.using = using;
        if(!using)
            setInactive();
    }

    private boolean checkXHoverState() {
        Vector2f mousePosition = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
        if(mousePosition.x <= xAxisEntity.transform.position.x + (gizmoHeight / 2.0f) &&
                mousePosition.x >= xAxisEntity.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePosition.y >= xAxisEntity.transform.position.y - (gizmoHeight / 2.0f) &&
                mousePosition.y <= xAxisEntity.transform.position.y + (gizmoWidth / 2.0f)) {
            xSprite.setColor(xAxisColorHover);
            return true;
        }
        xSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePosition = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
        if(mousePosition.x <= yAxisEntity.transform.position.x + (gizmoWidth / 2.0f) &&
                mousePosition.x >= yAxisEntity.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePosition.y <= yAxisEntity.transform.position.y + (gizmoHeight / 2.0f) &&
                mousePosition.y >= yAxisEntity.transform.position.y - (gizmoHeight / 2.0f)) {
            ySprite.setColor(yAxisColorHover);
            return true;
        }
        ySprite.setColor(yAxisColor);
        return false;
    }

    private void setActive() {
        xSprite.setColor(xAxisColor);
        ySprite.setColor(yAxisColor);
    }

    private void setInactive() {
        xSprite.setColor(new Vector4f(0,0,0,0.0f));
        ySprite.setColor(new Vector4f(0,0,0,0.0f));
    }
}
