package Components;

import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import Core.Entity;
import Core.MouseListener;
import Core.Prefabs;
import Core.Window;
import Editor.PropertiesWindow;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component {

    private Vector4f xAxisColor = new Vector4f(0.8f, 0.1f, 0.15f, 1.0f);
    private Vector4f xAxisColorHover = new Vector4f(0.9f, 0.2f, 0.2f, 1.0f);
    private Vector4f yAxisColor = new Vector4f(0.2f, 0.7f, 0.2f, 1.0f);
    private Vector4f yAxisColorHover = new Vector4f(0.3f, 0.8f, 0.3f, 1.0f);
    private Vector2f xAxisOffset = new Vector2f(64, -5);
    private Vector2f yAxisOffset = new Vector2f(16, 61);
    private Entity xAxisEntity;
    private Entity yAxisEntity;
    private SpriteRenderer xSprite;
    private SpriteRenderer ySprite;
    private PropertiesWindow propertiesWindow;
    private int gizmoWidth = 16;
    private int gizmoHeight = 48;
    private boolean using = false;
    protected Entity currentEntity;
    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    public Gizmo(Sprite gizmoSprite, PropertiesWindow propertiesWindow) {
        // Ensure gizmos render on top by assigning a high z-index
        xAxisEntity = Prefabs.generateSpriteEntity(gizmoSprite, 16, 48, -1000);
        yAxisEntity = Prefabs.generateSpriteEntity(gizmoSprite, 16, 48, -1000);
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
    }

    @Override
    public void editorUpdate(float dt) {
        if(!using) return;
        currentEntity = propertiesWindow.getCurrentEntity();
        if(currentEntity != null)
            setActive();
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
        Vector2f mousePosition = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if(mousePosition.x <= xAxisEntity.transform.position.x && mousePosition.x >= xAxisEntity.transform.position.x - gizmoHeight &&
                mousePosition.y >= xAxisEntity.transform.position.y && mousePosition.y <= xAxisEntity.transform.position.y + gizmoWidth) {
            xSprite.setColor(xAxisColorHover);
            return true;
        }
        xSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePosition = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if(mousePosition.x <= yAxisEntity.transform.position.x && mousePosition.x >= yAxisEntity.transform.position.x - gizmoWidth &&
                mousePosition.y <= yAxisEntity.transform.position.y && mousePosition.y >= yAxisEntity.transform.position.y - gizmoHeight) {
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
