package Core;

import Rendering.*;
import Scenes.LevelEditorScene;
import Scenes.LevelScene;
import Scenes.Scene;
import Util.AssetPool;
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

    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
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
        glfwWindowHint(GLFW_SOFT_FULLSCREEN, GLFW_TRUE);
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
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        framebuffer = new Framebuffer(2560, 1440);
        pickingTexture = new PickingTexture(2560, 1440);
        imGuiLayer = new ImGuiLayer(windowPointer, pickingTexture);
        imGuiLayer.init();
        glViewport(0, 0, 2560, 1440);
        Window.changeScene(0);
    }

    public static void changeScene(int scene) {
        switch (scene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Invalid scene: " + scene + "...";
                break;
        }
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Scene getScene() {
        return currentScene;
    }

    public static Window getInstance() {
        if(instance == null)
            instance = new Window();
        return instance;
    }

    public static Framebuffer getFramebuffer() {
        return getInstance().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public void run() {
        System.out.println("LWJGL Version: " + Version.getVersion());
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;
        Shader defaultShader = AssetPool.getShader("default");
        Shader pickingShader = AssetPool.getShader("picking");
        while(!glfwWindowShouldClose(windowPointer)) {
            glfwPollEvents();
            glDisable(GL_BLEND);
            pickingTexture.bind();
            glViewport(0, 0, 2560,1440);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            Renderer.bindShader(pickingShader);
            currentScene.render();
            pickingTexture.unbind();
            glEnable(GL_BLEND);
            DebugDraw.beginFrame();
            framebuffer.bind();
            glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            if(dt >= 0) {
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
                currentScene.update(dt);
                currentScene.render();
            }
            framebuffer.unbind();
            imGuiLayer.update(dt, currentScene);
            glfwSwapBuffers(windowPointer);
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
            MouseListener.endFrame();
        }
        currentScene.saveExit();
        imGuiLayer.destroyImGui();
        glfwFreeCallbacks(windowPointer);
        glfwDestroyWindow(windowPointer);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static int getWidth() {
        return getInstance().width;
    }

    public static int getHeight() {
        return getInstance().height;
    }
}
