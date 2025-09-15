package Components;

import Components.Sprites.Sprite;
import Core.MouseListener;
import Editor.PropertiesWindow;

public class ScaleGizmo extends Gizmo {

    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void update(float dt) {
        if(currentEntity != null) {
            if(xAxisActive && !yAxisActive)
                currentEntity.transform.scale.x -= MouseListener.getWorldDX();
            else if(yAxisActive)
                currentEntity.transform.scale.y -= MouseListener.getWorldDY();
        }
        super.update(dt);
    }
}
