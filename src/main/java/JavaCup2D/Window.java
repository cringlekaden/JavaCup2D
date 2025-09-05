package JavaCup2D;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static Window instance;
    private static Scene currentScene;

    private String title;
    private int width, height;
    private long windowPointer;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "JavaCup2D | 0.0.0a";
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit())
            throw new IllegalStateException("GLFW initialization failed...");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac")) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        } else {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        }
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        windowPointer = glfwCreateWindow(width, height, title, NULL, NULL);
        if(windowPointer == NULL)
            throw new IllegalStateException("GLFW window creation failed...");
        glfwSetCursorPosCallback(windowPointer, MouseListener::mousePositionCallback);
        glfwSetMouseButtonCallback(windowPointer, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(windowPointer, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(windowPointer, KeyListener::keyCallback);
        glfwMakeContextCurrent(windowPointer);
        glfwSwapInterval(1);
        glfwShowWindow(windowPointer);
        GL.createCapabilities();
        Window.changeScene(0);
    }

    public static void changeScene(int scene) {
        switch (scene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false : "Invalid scene: " + scene + "...";
                break;
        }
    }

    public static Scene getScene() {
        return currentScene;
    }

    public static Window getInstance() {
        if(instance == null)
            instance = new Window();
        return instance;
    }

    public void run() {
        System.out.println("LWJGL Version: " + Version.getVersion());
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;
        while(!glfwWindowShouldClose(windowPointer)) {
            glfwPollEvents();
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            if(dt >= 0)
                currentScene.update(dt);
            glfwSwapBuffers(windowPointer);
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
        glfwFreeCallbacks(windowPointer);
        glfwDestroyWindow(windowPointer);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
