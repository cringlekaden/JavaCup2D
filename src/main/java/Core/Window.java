package Core;

import Editor.ImGuiLayer;
import Observers.EventSystem;
import Observers.Events.Event;
import Observers.Observer;
import Rendering.*;
import Scenes.LevelEditorInitializer;
import Scenes.Scene;
import Scenes.SceneInitializer;
import Util.AssetPool;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    private static Window instance;
    private static Scene currentScene;

    private final ImGuiLayer imGuiLayer;
    private final Framebuffer framebuffer;
    private final PickingTexture pickingTexture;
    private final String title;
    private final int width;
    private final int height;
    private final long windowPointer, audioContext, audioDevice;
    private boolean isSceneRunning = false;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "JavaCup2D | 0.1.3a";
        EventSystem.addObserver(this);
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
        String defaultAudioDevice = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultAudioDevice);
        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);
        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
        if(!alCapabilities.OpenAL10)
            assert false : "OpenAL not supported...";
        GL.createCapabilities();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        framebuffer = new Framebuffer(2560, 1440);
        pickingTexture = new PickingTexture(2560, 1440);
        imGuiLayer = new ImGuiLayer(windowPointer, pickingTexture);
        imGuiLayer.init();
        glViewport(0, 0, 2560, 1440);
    }

    public static void changeScene(SceneInitializer sceneInitializer) {
        if(currentScene != null)
            currentScene.destroy();
        getImGuiLayer().getPropertiesWindow().setCurrentEntity(null);
        currentScene = new Scene(sceneInitializer);
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
        Window.changeScene(new LevelEditorInitializer());
        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));
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
                if(isSceneRunning)
                    currentScene.update(dt);
                else
                    currentScene.editorUpdate(dt);
                currentScene.render();
            }
            framebuffer.unbind();
            imGuiLayer.update(dt, currentScene);
            glfwSwapBuffers(windowPointer);
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
        imGuiLayer.destroyImGui();
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);
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

    public static ImGuiLayer getImGuiLayer() {
        return getInstance().imGuiLayer;
    }

    @Override
    public void onNotify(Entity entity, Event event) {
        switch (event.eventType) {
            case EngineStartPlay -> {
                isSceneRunning = true;
                currentScene.save();
                Window.changeScene(new LevelEditorInitializer());
            }
            case EngineStopPlay -> {
                isSceneRunning = false;
                Window.changeScene(new LevelEditorInitializer());
            }
            case SceneSave -> currentScene.save();
            case SceneLoad -> Window.changeScene(new LevelEditorInitializer());
        }
    }
}
