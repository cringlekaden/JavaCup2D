package Components;

import Components.Sprites.Sprite;
import Core.MouseListener;
import Editor.PropertiesWindow;

public class ScaleGizmo extends Gizmo {

    private float lastMouseX = 0.0f;
    private float lastMouseY = 0.0f;
    private boolean wasDragging = false;

    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if (currentEntity != null) {
            boolean dragging = MouseListener.isDragging() && MouseListener.mouseButtonPressed(org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT);
            if (!wasDragging) {
                lastMouseX = MouseListener.getWorldX();
                lastMouseY = MouseListener.getWorldY();
            }
            if (dragging) {
                float mouseX = MouseListener.getWorldX();
                float mouseY = MouseListener.getWorldY();
                float dx = mouseX - lastMouseX;
                float dy = mouseY - lastMouseY;
                if (xAxisActive && !yAxisActive) {
                    currentEntity.transform.scale.x += dx;
                } else if (yAxisActive) {
                    currentEntity.transform.scale.y += dy;
                }
                lastMouseX = mouseX;
                lastMouseY = mouseY;
            }
            wasDragging = dragging;
        }
        super.editorUpdate(dt);
    }
}
