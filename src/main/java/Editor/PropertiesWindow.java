package Editor;

import Components.NonPickable;
import Components.Sprites.SpriteRenderer;
import Core.Entity;
import Core.MouseListener;
import Physics2D.Components.Box2DCollider;
import Physics2D.Components.Circle2DCollider;
import Physics2D.Components.RigidBody2D;
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
            int entityID = pickingTexture.readPixel(x, y);
            Entity pickedEntity = currentScene.getEntityByID(entityID);
            if(pickedEntity != null && pickedEntity.getComponent(NonPickable.class) == null)
                currentEntity = pickedEntity;
            else if(pickedEntity == null && !MouseListener.isDragging()) {
                currentEntity = null;
            }
            debounceTime = 0.2f;
        }
    }

    public void imgui() {
        if(currentEntity != null) {
            ImGui.begin("Properties");
            if(ImGui.beginPopupContextWindow("AddComponent")) {
                if(ImGui.menuItem("Add Rigidbody")) {
                    if(currentEntity.getComponent(RigidBody2D.class) == null)
                        currentEntity.addComponent(new RigidBody2D());
                }
                if(ImGui.menuItem("Add Box Collider")) {
                    if(currentEntity.getComponent(Box2DCollider.class) == null && currentEntity.getComponent(Circle2DCollider.class) == null)
                        currentEntity.addComponent(new Box2DCollider());
                }
                if(ImGui.menuItem("Add Circle Collider")) {
                    if(currentEntity.getComponent(Circle2DCollider.class) == null && currentEntity.getComponent(Box2DCollider.class) == null)
                        currentEntity.addComponent(new Circle2DCollider());
                }
                ImGui.endPopup();
            }
            currentEntity.imgui();
            ImGui.end();
        }
    }

    public void setCurrentEntity(Entity entity) {
        currentEntity = entity;
    }

    public Entity getCurrentEntity() {
        return currentEntity;
    }
}
