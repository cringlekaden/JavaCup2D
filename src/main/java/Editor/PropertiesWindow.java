package Editor;

import Core.Entity;
import Core.MouseListener;
import Rendering.PickingTexture;
import Scenes.Scene;
import imgui.ImGui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {

    private Entity activeEntity = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene) {
        // Read pixel after rendering to the picking framebuffer
        if(MouseListener.mouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            activeEntity = currentScene.getEntityByID(pickingTexture.readPixel(x, y));
        }
    }

    public void imgui() {
        if(activeEntity != null) {
            ImGui.begin("Properties");
            activeEntity.imgui();
            ImGui.end();
        }
    }
}
