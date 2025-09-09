package Core;

import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static MouseListener instance;

    private double scrollX, scrollY, x, y, lastX, lastY;
    private boolean[] mouseButtonPressed;
    private boolean isDragging;

    private MouseListener() {
        mouseButtonPressed = new boolean[10];
        scrollX = 0.0;
        scrollY = 0.0;
        x = 0.0;
        y = 0.0;
        lastX = 0.0;
        lastY = 0.0;
    }

    public static void mousePositionCallback(long window, double x, double y) {
        getInstance().lastX = getInstance().x;
        getInstance().lastY = getInstance().y;
        getInstance().x = x;
        getInstance().y = y;
        for(int i = 0; i < getInstance().mouseButtonPressed.length; i++) {
            if(getInstance().mouseButtonPressed[i])
                getInstance().isDragging = true;
        }
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        getInstance().mouseButtonPressed[button] = action == GLFW_PRESS;
        if(action == GLFW_RELEASE)
            getInstance().isDragging = false;
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        getInstance().scrollX = xOffset;
        getInstance().scrollY = yOffset;
    }

    public static void endFrame() {
        getInstance().scrollX = 0;
        getInstance().scrollY = 0;
        getInstance().lastX = getInstance().x;
        getInstance().lastY = getInstance().y;
        getInstance().x = 0;
    }

    public static float getX() {
        return (float) getInstance().x;
    }

    public static float getY() {
        return (float) getInstance().y;
    }

    public static float getOrthoX() {
        float currentX = getX();
        currentX = (currentX / (float) Window.getWidth() * 2.0f) - 1.0f;
        Vector4f temp = new Vector4f(currentX, 0, 0, 1);
        temp.mul(Window.getScene().getCamera().getInverseProjection()).mul(Window.getScene().getCamera().getInverseView());
        currentX = temp.x;
        return currentX;
    }

    public static float getOrthoY() {
        float currentY = getY();
        currentY = (currentY / (float) Window.getHeight() * 2.0f) - 1.0f;
        Vector4f temp = new Vector4f(0, currentY, 0, 1);
        temp.mul(Window.getScene().getCamera().getInverseProjection()).mul(Window.getScene().getCamera().getInverseView());
        currentY = temp.y;
        return currentY;
    }

    public static float getDX() {
        return (float) (getInstance().lastX - getInstance().x);
    }

    public static float getDY() {
        return (float) (getInstance().lastY - getInstance().y);
    }

    public static float getScrollX() {
        return (float) getInstance().scrollX;
    }

    public static float getScrollY() {
        return (float) getInstance().scrollY;
    }

    public static boolean isDragging() {
        return getInstance().isDragging;
    }

    public static boolean mouseButtonPressed(int button) {
        return getInstance().mouseButtonPressed[button];
    }

    public static MouseListener getInstance() {
        if(instance == null)
            instance = new MouseListener();
        return instance;
    }
}
