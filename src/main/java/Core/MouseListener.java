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
    private double scrollX, scrollY, x, y, worldX, worldY;
    private int mouseButtonsDown = 0;
    private final boolean[] mouseButtonPressed;
    private boolean isDragging;

    private MouseListener() {
        mouseButtonPressed = new boolean[9];
        scrollX = 0.0;
        scrollY = 0.0;
        x = 0.0;
        y = 0.0;
        gameViewportPosition = new Vector2f();
        gameViewportSize = new Vector2f();
    }

    public static void mousePositionCallback(long window, double x, double y) {
        if(getInstance().mouseButtonsDown > 0)
            getInstance().isDragging = true;
        getInstance().x = x;
        getInstance().y = y;
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

    public static float getX() {
        // Use ImGui's mouse position, which shares the same coordinate space as ImGui.getWindowPos()
        return ImGui.getMousePosX();
    }

    public static float getY() {
        return ImGui.getMousePosY();
    }

    public static float getScreenX() {
        return getScreen().x;
    }

    public static float getScreenY() {
        return getScreen().y;
    }

    public static Vector2f getScreen() {
        float currentX = getX() - getInstance().gameViewportPosition.x;
        // Map from viewport space to picking framebuffer resolution (matches Window's FBO size)
        float fbWidth = Window.getFramebuffer().getWidth();
        float fbHeight = Window.getFramebuffer().getHeight();
        currentX = currentX / getInstance().gameViewportSize.x * fbWidth;
        float currentY = getY() - getInstance().gameViewportPosition.y;
        // Flip Y for OpenGL origin and map to picking framebuffer resolution
        currentY = fbHeight - (currentY / getInstance().gameViewportSize.y * fbHeight);
        return new Vector2f(currentX, currentY);
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

    public static float getWorldX() {
        return getWorld().x;
    }

    public static float getWorldY() {
        return getWorld().y;
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

    public static Vector2f getWorld() {
        float currentX = getX() - getInstance().gameViewportPosition.x;
        currentX = currentX / getInstance().gameViewportSize.x * 2.0f - 1.0f;
        float currentY = getY() - getInstance().gameViewportPosition.y;
        currentY = -(currentY / getInstance().gameViewportSize.y * 2.0f - 1.0f);
        Vector4f temp = new Vector4f(currentX, currentY, 0, 1);
        Camera camera = Window.getScene().getCamera();
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        // Unproject from NDC to world: apply inverse projection first, then inverse view
        temp.mul(inverseProjection);
        temp.mul(inverseView);
        if (temp.w != 0.0f) {
            temp.div(temp.w);
        }
        return new Vector2f(temp.x, temp.y);
    }
}
