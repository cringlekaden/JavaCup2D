package Core;

import Editor.GameViewWindow;
import Scenes.Scene;
import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

    private final long windowPtr;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public ImGuiLayer(long windowPtr) {
        this.windowPtr = windowPtr;
    }

    public void init() {
        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.setBackendPlatformName("imgui_java_impl_glfw");
        // Clipboard support
        io.setSetClipboardTextFn(new ImStrConsumer() {@Override public void accept(final String s) {glfwSetClipboardString(windowPtr, s);}});
        io.setGetClipboardTextFn(new ImStrSupplier() {private final StringBuilder clipboard = new StringBuilder();@Override public String get() {String text = glfwGetClipboardString(windowPtr);if (text != null) {clipboard.setLength(0);clipboard.append(text);return clipboard.toString();}return "";}});
        //Font setup
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setPixelSnapH(true);
        File sfPro = new File("/System/Library/Fonts/SFNS.ttf");
        File menlo = new File("/System/Library/Fonts/Supplemental/Menlo.ttc");
        File helvetica = new File("/System/Library/Fonts/Supplemental/Helvetica.ttc");
        if (sfPro.isFile())
            fontAtlas.addFontFromFileTTF(sfPro.getAbsolutePath(), 24, fontConfig);
        else if (menlo.isFile())
            fontAtlas.addFontFromFileTTF(menlo.getAbsolutePath(), 24, fontConfig);
        else if (helvetica.isFile())
            fontAtlas.addFontFromFileTTF(helvetica.getAbsolutePath(), 24, fontConfig);
        else
            System.err.println("No custom font found, using default ImGui font.");
        fontConfig.destroy();
        fontAtlas.build(); // bake atlas
        imGuiGlfw.init(windowPtr, true);
        imGuiGl3.init("#version 410 core");
    }

    public void startFrame(float dt) {
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
        if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            long backupWindow = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindow);
        }
    }

    public void update(float dt, Scene scene) {
        startFrame(dt);

        setupDockspace();
        scene.sceneImgui();
        GameViewWindow.imgui();
        ImGui.end();
        endFrame();
    }

    public void destroyImGui() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }

    private void setupDockspace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                        ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                        ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
        ImGui.begin("Dockspace", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);
        ImGui.dockSpace(ImGui.getID("Dockspace"));
    }
}