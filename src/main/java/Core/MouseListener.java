package Core;

import Editor.GameViewWindow;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static MouseListener instance;

    private final Vector2f gameViewportPosition;
    private final Vector2f gameViewportSize;
    private double scrollX, scrollY, x, y, lastX, lastY, worldX, worldY, lastWorldX, lastWorldY;
    private int mouseButtonsDown = 0;
    private final boolean[] mouseButtonPressed;
    private boolean isDragging;

    private MouseListener() {
        mouseButtonPressed = new boolean[9];
        scrollX = 0.0;
        scrollY = 0.0;
        x = 0.0;
        y = 0.0;
        lastX = 0.0;
        lastY = 0.0;
        gameViewportPosition = new Vector2f();
        gameViewportSize = new Vector2f();
    }

    public static void mousePositionCallback(long window, double x, double y) {
        if(getInstance().mouseButtonsDown > 0)
            getInstance().isDragging = true;
        getInstance().lastX = getInstance().x;
        getInstance().lastY = getInstance().y;
        getInstance().lastWorldX = getInstance().worldX;
        getInstance().lastWorldY = getInstance().worldY;
        getInstance().x = x;
        getInstance().y = y;
        calcOrthoX();
        calcOrthoY();
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        getInstance().mouseButtonPressed[button] = action == GLFW_PRESS;
        if(action == GLFW_PRESS)
            getInstance().mouseButtonsDown++;
        if(action == GLFW_RELEASE) {
            getInstance().mouseButtonsDown--;
            getInstance().isDragging = false;
        }
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
        getInstance().lastWorldX = getInstance().worldX;
        getInstance().lastWorldY = getInstance().worldY;
    }

    public static float getX() {
        return (float) getInstance().x;
    }

    public static float getY() {
        return (float) getInstance().y;
    }

    public static float getOrthoX() {
        return (float)getInstance().worldX;
    }

    public static float getOrthoY() {
        return (float)getInstance().worldY;
    }

    public static float getScreenX() {
        float currentX = getX() - getInstance().gameViewportPosition.x;
        // Map from viewport space to picking framebuffer resolution (matches Window's FBO size)
        currentX = currentX / getInstance().gameViewportSize.x * 2560.0f;
        return currentX;
    }

    public static float getScreenY() {
        float currentY = getY() - getInstance().gameViewportPosition.y;
        // Flip Y for OpenGL origin and map to picking framebuffer resolution
        currentY = 1440.0f - (currentY / getInstance().gameViewportSize.y * 1440.0f);
        return currentY;
    }

    public static float getDX() {
        return (float) (getInstance().lastX - getInstance().x);
    }

    public static float getDY() {
        return (float) (getInstance().lastY - getInstance().y);
    }

    public static float getWorldDX() {
        return (float) (getInstance().lastWorldX - getInstance().worldX);
    }

    public static float getWorldDY() {
        return (float) (getInstance().lastWorldY - getInstance().worldY);
    }

    public static float getScrollX() {
        if (ImGui.getIO().getWantCaptureMouse() && !GameViewWindow.isHovered())
            return 0.0f;
        return (float) getInstance().scrollX;
    }

    public static float getScrollY() {
        if (ImGui.getIO().getWantCaptureMouse() && !GameViewWindow.isHovered())
            return 0.0f;
        return (float) getInstance().scrollY;
    }

    public static boolean isDragging() {
        return getInstance().isDragging;
    }

    public static boolean mouseButtonPressed(int button) {
        if (ImGui.getIO().getWantCaptureMouse() && !GameViewWindow.isHovered())
            return false;
        return getInstance().mouseButtonPressed[button];
    }

    public static void setGameViewportPosition(Vector2f gameViewportPosition) {
        getInstance().gameViewportPosition.set(gameViewportPosition);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        getInstance().gameViewportSize.set(gameViewportSize);
    }

    public static MouseListener getInstance() {
        if(instance == null)
            instance = new MouseListener();
        return instance;
    }

    private static void calcOrthoX() {
        float currentX = getX() - getInstance().gameViewportPosition.x;
        currentX = currentX / getInstance().gameViewportSize.x * 2.0f - 1.0f;
        Vector4f temp = new Vector4f(currentX, 0, 0, 1);
        Matrix4f view = new Matrix4f();
        Window.getScene().getCamera().getInverseView().mul(Window.getScene().getCamera().getInverseProjection(), view);
        temp.mul(view);
        getInstance().worldX = temp.x;
    }

    private static void calcOrthoY() {
        float currentY = getY() - getInstance().gameViewportPosition.y;
        currentY = -(currentY / getInstance().gameViewportSize.y * 2.0f - 1.0f);
        Vector4f temp = new Vector4f(0, currentY, 0, 1);
        Matrix4f view = new Matrix4f();
        Window.getScene().getCamera().getInverseView().mul(Window.getScene().getCamera().getInverseProjection(), view);
        temp.mul(view);
        getInstance().worldY = temp.y;
    }
}
