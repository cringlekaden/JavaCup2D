package Editor;

import Core.MouseListener;
import Core.Window;
import Observers.EventSystem;
import Observers.Events.Event;
import Observers.Events.EventType;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class GameViewWindow {

    private static boolean hovered = false;
    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying = false;

    public void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);
        ImGui.beginMenuBar();
        if(ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.EngineStartPlay));
        }
        if(ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.EngineStopPlay));
        }
        ImGui.endMenuBar();
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPosition = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPosition.x, windowPosition.y);
        leftX = windowPosition.x;
        rightX = windowPosition.x + windowSize.x;
        bottomY = windowPosition.y;
        topY = windowPosition.y + windowSize.y;
        int textureID = Window.getFramebuffer().getTextureID();
        ImGui.image(textureID, windowSize.x, windowSize.y, 0, 1, 1, 0);
        // The game viewport position must be the absolute position of the image inside the GLFW window
        ImVec2 absWinPos = ImGui.getWindowPos();
        MouseListener.setGameViewportPosition(new Vector2f(absWinPos.x + windowPosition.x, absWinPos.y + windowPosition.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));
        // Use item-hovered (the image) rather than the whole window, for precise mouse capture gating
        hovered = ImGui.isItemHovered(ImGuiHoveredFlags.None);
        ImGui.end();
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if(aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);
        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }

    public static boolean isHovered() {
        return hovered;
    }
}
