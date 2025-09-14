package Components;

import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import Core.Entity;
import Core.Prefabs;
import Core.Window;
import Editor.PropertiesWindow;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class TranslateGizmo extends Component {

    private Vector4f xAxisColor = new Vector4f(1, 0, 0, 1);
    private Vector4f xAxisColorHover = new Vector4f(0.8f, 0.1f, 0.1f, 1);
    private Vector4f yAxisColor = new Vector4f(0, 1, 0, 1);
    private Vector4f yAxisColorHover = new Vector4f(0.1f, 0.8f, 0.1f, 1);
    private Vector2f xAxisOffset = new Vector2f(64, -5);
    private Vector2f yAxisOffset = new Vector2f(16, 61);
    private Entity xAxisEntity;
    private Entity yAxisEntity;
    private Entity currentEntity;
    private SpriteRenderer xSprite;
    private SpriteRenderer ySprite;
    private PropertiesWindow propertiesWindow;

    public TranslateGizmo(Sprite gizmoSprite, PropertiesWindow propertiesWindow) {
        // Ensure gizmos render on top by assigning a high z-index
        xAxisEntity = Prefabs.generateSpriteObject(gizmoSprite, 16, 48, -1000);
        yAxisEntity = Prefabs.generateSpriteObject(gizmoSprite, 16, 48, -1000);
        xSprite = xAxisEntity.getComponent(SpriteRenderer.class);
        ySprite = yAxisEntity.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;
        Window.getScene().addEntityToScene(xAxisEntity);
        Window.getScene().addEntityToScene(yAxisEntity);
    }

    @Override
    public void start() {
        xAxisEntity.transform.rotation = 90;
        yAxisEntity.transform.rotation = 180;
        xAxisEntity.setNoSerialize();
        yAxisEntity.setNoSerialize();
    }

    @Override
    public void update(float dt) {
        if(currentEntity != null) {
            xAxisEntity.transform.position.set(currentEntity.transform.position);
            yAxisEntity.transform.position.set(currentEntity.transform.position);
            xAxisEntity.transform.position.add(xAxisOffset);
            yAxisEntity.transform.position.add(yAxisOffset);
        }
        currentEntity = propertiesWindow.getCurrentEntity();
        if(currentEntity != null)
            setActive();
        else
            setInactive();
    }

    private void setActive() {
        xSprite.setColor(xAxisColor);
        ySprite.setColor(yAxisColor);
    }

    private void setInactive() {
        xSprite.setColor(new Vector4f(0,0,0,0));
        ySprite.setColor(new Vector4f(0,0,0,0));
    }
}
