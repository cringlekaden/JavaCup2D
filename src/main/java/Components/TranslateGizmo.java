package Components;

import Components.Sprites.Sprite;
import Core.MouseListener;
import Editor.PropertiesWindow;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite gizmoSprite, PropertiesWindow propertiesWindow) {
        super(gizmoSprite, propertiesWindow);
    }

    @Override
    public void update(float dt) {
        if(currentEntity != null) {
            if(xAxisActive && !yAxisActive)
                currentEntity.transform.position.x -= MouseListener.getWorldDX();
            else if(yAxisActive)
                currentEntity.transform.position.y -= MouseListener.getWorldDY();
        }
        super.update(dt);
    }
}
