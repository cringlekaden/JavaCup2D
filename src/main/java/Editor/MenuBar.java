package Editor;

import Observers.EventSystem;
import Observers.Events.Event;
import Observers.Events.EventType;
import imgui.ImGui;

public class MenuBar {

    public void imgui() {
        ImGui.beginMainMenuBar();
        if(ImGui.beginMenu("File")) {
            if(ImGui.menuItem("Save", "Ctrl+S")) {
                EventSystem.notify(null, new Event(EventType.SceneSave));
            }
            if(ImGui.menuItem("Load", "Ctrl+O")) {
                EventSystem.notify(null, new Event(EventType.SceneLoad));
            }
            ImGui.endMenu();
        }
        ImGui.endMainMenuBar();
    }
}
