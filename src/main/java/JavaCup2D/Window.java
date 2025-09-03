package JavaCup2D;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static Window instance;

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
    }

    public static Window getInstance() {
        if(instance == null)
            instance = new Window();
        return instance;
    }

    public void run() {
        System.out.println("LWJGL Version: " + Version.getVersion());
        while(!glfwWindowShouldClose(windowPointer)) {
            glfwPollEvents();
            if(KeyListener.keyPressed(GLFW_KEY_SPACE))
                System.out.println("Spacebar pressed!");
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            glfwSwapBuffers(windowPointer);
        }
        glfwFreeCallbacks(windowPointer);
        glfwDestroyWindow(windowPointer);
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }


}
