package JavaCup2D;

import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseCursor;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

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
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
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

        scene.sceneImgui();
        ImGui.begin("Hello, ImGui!");
        ImGui.text("We are running imgui-java v1.90.9");
        ImGui.text("FPS: " + ImGui.getIO().getFramerate());
        ImGui.end();

        endFrame();
    }

    public void destroyImGui() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }
}