package Core;

import Editor.GameViewWindow;
import imgui.ImGui;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class KeyListener {

    private static KeyListener instance;

    private final boolean[] keys;
    private final boolean[] keysBegin;

    private KeyListener() {
        keys = new boolean[350];
        keysBegin = new boolean[350];
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        getInstance().keys[key] = action == GLFW_PRESS;
        getInstance().keysBegin[key] = action == GLFW_PRESS;
    }

    public static boolean keyPressed(int key) {
        if (ImGui.getIO().getWantCaptureMouse() && !GameViewWindow.isHovered())
            return false;
        return getInstance().keys[key];
    }

    public static boolean keyBeginPress(int key) {
        boolean result = getInstance().keysBegin[key];
        if(result)
            getInstance().keysBegin[key] = false;
        return result;
    }

    public static KeyListener getInstance() {
        if(instance == null)
            instance = new KeyListener();
        return instance;
    }
}
