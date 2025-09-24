package Editor;

import Core.Entity;
import Core.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneHierarchyWindow {

    private static String payloadType = "SceneHierarchy";

    public void imgui() {
        ImGui.begin("Scene Hierarchy");
        List<Entity> entities = Window.getScene().getEntities();
        int index = 0;
        for(Entity entity : entities) {
            if(!entity.doSerialize())
                continue;
            boolean treeNodeOpen = doTreeNode(entity, index);
            if(treeNodeOpen)
                ImGui.treePop();
            index++;
        }
        ImGui.end();
    }

    private boolean doTreeNode(Entity entity, int index) {
        ImGui.pushID(index);
        boolean treeNodeOpen = ImGui.treeNodeEx(entity.getName(),
                ImGuiTreeNodeFlags.DefaultOpen |
                        ImGuiTreeNodeFlags.FramePadding |
                        ImGuiTreeNodeFlags.OpenOnArrow |
                        ImGuiTreeNodeFlags.SpanAvailWidth, entity.getName());
        ImGui.popID();
        if(ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(payloadType, entity);
            ImGui.text(entity.getName());
            ImGui.endDragDropSource();
        }
        if(ImGui.beginDragDropTarget()) {
            Object payload = ImGui.acceptDragDropPayload(payloadType);
            if(payload != null) {
                if(payload.getClass().isAssignableFrom(Entity.class)) {
                    Entity payloadEntity = (Entity) payload;
                    System.out.println("Accepted payload: " + payloadEntity.getName());
                }
            }
            ImGui.endDragDropTarget();
        }
        return treeNodeOpen;
    }
}
