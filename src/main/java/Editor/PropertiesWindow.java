package Editor;

import Core.Entity;
import Core.MouseListener;
import Rendering.PickingTexture;
import Scenes.Scene;
import imgui.ImGui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {

    private Entity currentEntity = null;
    private PickingTexture pickingTexture;
    private float debounceTime = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene) {
        debounceTime -= dt;
        // Read pixel after rendering to the picking framebuffer
        if(MouseListener.mouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT) && debounceTime < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            currentEntity = currentScene.getEntityByID(pickingTexture.readPixel(x, y));
            debounceTime = 0.2f;
        }
    }

    public void imgui() {
        if(currentEntity != null) {
            ImGui.begin("Properties");
            currentEntity.imgui();
            ImGui.end();
        }
    }

    public Entity getCurrentEntity() {
        return currentEntity;
    }
}
