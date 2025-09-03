package JavaCup2D;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class KeyListener {

    private static KeyListener instance;

    private boolean[] keys;

    private KeyListener() {
        keys = new boolean[350];
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        getInstance().keys[key] = action == GLFW_PRESS;
    }

    public static boolean keyPressed(int key) {
        return getInstance().keys[key];
    }

    public static KeyListener getInstance() {
        if(instance == null)
            instance = new KeyListener();
        return instance;
    }
}
