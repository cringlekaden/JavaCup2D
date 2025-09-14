package Core;

import Editor.GameViewWindow;
import Editor.PropertiesWindow;
import Rendering.PickingTexture;
import Scenes.Scene;
import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

    private final long windowPtr;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private GameViewWindow gameViewWindow;
    private PropertiesWindow propertiesWindow;

    public ImGuiLayer(long windowPtr, PickingTexture pickingTexture) {
        this.windowPtr = windowPtr;
        this.gameViewWindow = new GameViewWindow();
        this.propertiesWindow = new PropertiesWindow(pickingTexture);
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
        // Font setup
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setPixelSnapH(true);
        File sfPro = new File("/System/Library/Fonts/SFNS.ttf");
        File menlo = new File("/System/Library/Fonts/Supplemental/Menlo.ttc");
        File helvetica = new File("/System/Library/Fonts/Supplemental/Helvetica.ttc");
        ImFont font = null;
        if (sfPro.isFile()) {
            font = fontAtlas.addFontFromFileTTF(sfPro.getAbsolutePath(), 24, fontConfig);
        } else if (menlo.isFile()) {
            font = fontAtlas.addFontFromFileTTF(menlo.getAbsolutePath(), 24, fontConfig);
        } else if (helvetica.isFile()) {
            font = fontAtlas.addFontFromFileTTF(helvetica.getAbsolutePath(), 24, fontConfig);
        } else {
            System.err.println("No custom font found, adding default ImGui font.");
            font = fontAtlas.addFontDefault();
        }
        // Ensure a valid default font is set to avoid Glyphs.Size == 0 assertion
        if (font == null)
            font = fontAtlas.addFontDefault();
        io.setFontDefault(font);
        fontConfig.destroy();
        fontAtlas.build();
        setStyle();
        imGuiGlfw.init(windowPtr, true);
        imGuiGl3.init("#version 410 core");
    }

    public void startFrame(float dt) {
        imGuiGlfw.newFrame();
        imGuiGl3.newFrame();
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
        scene.imgui();
        gameViewWindow.imgui();
        propertiesWindow.update(dt, scene);
        propertiesWindow.imgui();
        ImGui.end();
        endFrame();
    }

    public void destroyImGui() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }

    public PropertiesWindow getPropertiesWindow() {
        return propertiesWindow;
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

    private void setStyle() {
        ImGuiStyle style = ImGui.getStyle();

        // Rounding
        style.setWindowRounding(3.0f);
        style.setFrameRounding(2.0f);
        style.setGrabRounding(2.0f);
        style.setScrollbarRounding(3.0f);

        // Borders & padding
        style.setWindowBorderSize(1.0f);
        style.setFrameBorderSize(1.0f);
        style.setPopupBorderSize(1.0f);

        style.setWindowPadding(8.0f, 8.0f);
        style.setFramePadding(4.0f, 2.0f);
        style.setItemSpacing(6.0f, 4.0f);

        // Colors
        ImVec4[] colors = style.getColors();

        // Dockspace & window bg
        colors[ImGuiCol.WindowBg]        = new ImVec4(0.11f, 0.11f, 0.12f, 1.0f);
        colors[ImGuiCol.ChildBg]         = new ImVec4(0.15f, 0.15f, 0.16f, 1.0f);
        colors[ImGuiCol.PopupBg]         = new ImVec4(0.11f, 0.11f, 0.12f, 0.94f);
        colors[ImGuiCol.DockingEmptyBg]  = new ImVec4(0.15f, 0.15f, 0.16f, 1.0f);

        // Borders & separators
        colors[ImGuiCol.Border]          = new ImVec4(0.20f, 0.20f, 0.22f, 0.50f);
        colors[ImGuiCol.Separator]       = new ImVec4(0.25f, 0.25f, 0.28f, 0.60f);
        colors[ImGuiCol.SeparatorHovered]= new ImVec4(0.26f, 0.59f, 0.98f, 0.78f);
        colors[ImGuiCol.SeparatorActive] = new ImVec4(0.26f, 0.59f, 0.98f, 1.0f);

        // Menu bar
        colors[ImGuiCol.MenuBarBg]       = new ImVec4(0.14f, 0.14f, 0.15f, 1.0f);

        // Headers
        colors[ImGuiCol.Header]          = new ImVec4(0.18f, 0.18f, 0.20f, 1.0f);
        colors[ImGuiCol.HeaderHovered]   = new ImVec4(0.26f, 0.59f, 0.98f, 0.80f);
        colors[ImGuiCol.HeaderActive]    = new ImVec4(0.26f, 0.59f, 0.98f, 1.0f);

        // Buttons
        colors[ImGuiCol.Button]          = new ImVec4(0.20f, 0.22f, 0.24f, 1.0f);
        colors[ImGuiCol.ButtonHovered]   = new ImVec4(0.26f, 0.59f, 0.98f, 0.80f);
        colors[ImGuiCol.ButtonActive]    = new ImVec4(0.26f, 0.59f, 0.98f, 1.0f);

        // Frames
        colors[ImGuiCol.FrameBg]         = new ImVec4(0.16f, 0.16f, 0.18f, 1.0f);
        colors[ImGuiCol.FrameBgHovered]  = new ImVec4(0.26f, 0.59f, 0.98f, 0.78f);
        colors[ImGuiCol.FrameBgActive]   = new ImVec4(0.26f, 0.59f, 0.98f, 1.0f);

        // Tabs
        colors[ImGuiCol.Tab]             = new ImVec4(0.15f, 0.15f, 0.17f, 1.0f);
        colors[ImGuiCol.TabHovered]      = new ImVec4(0.26f, 0.59f, 0.98f, 0.80f);
        colors[ImGuiCol.TabActive]       = new ImVec4(0.20f, 0.22f, 0.24f, 1.0f);
        colors[ImGuiCol.TabUnfocused]    = new ImVec4(0.15f, 0.15f, 0.17f, 1.0f);
        colors[ImGuiCol.TabUnfocusedActive] = new ImVec4(0.20f, 0.22f, 0.24f, 1.0f);

        // Title bar
        colors[ImGuiCol.TitleBg]         = new ImVec4(0.11f, 0.11f, 0.12f, 1.0f);
        colors[ImGuiCol.TitleBgActive]   = new ImVec4(0.11f, 0.11f, 0.12f, 1.0f);
        colors[ImGuiCol.TitleBgCollapsed]= new ImVec4(0.11f, 0.11f, 0.12f, 1.0f);

        // Scrollbars
        colors[ImGuiCol.ScrollbarBg]     = new ImVec4(0.11f, 0.11f, 0.12f, 1.0f);
        colors[ImGuiCol.ScrollbarGrab]   = new ImVec4(0.26f, 0.59f, 0.98f, 0.50f);
        colors[ImGuiCol.ScrollbarGrabHovered] = new ImVec4(0.26f, 0.59f, 0.98f, 0.75f);
        colors[ImGuiCol.ScrollbarGrabActive]  = new ImVec4(0.26f, 0.59f, 0.98f, 1.0f);

        // Other accents
        colors[ImGuiCol.ResizeGrip]      = new ImVec4(0.26f, 0.59f, 0.98f, 0.20f);
        colors[ImGuiCol.ResizeGripHovered]= new ImVec4(0.26f, 0.59f, 0.98f, 0.67f);
        colors[ImGuiCol.ResizeGripActive] = new ImVec4(0.26f, 0.59f, 0.98f, 0.95f);

        colors[ImGuiCol.CheckMark]       = new ImVec4(0.26f, 0.59f, 0.98f, 1.0f);
        colors[ImGuiCol.SliderGrab]      = new ImVec4(0.26f, 0.59f, 0.98f, 0.78f);
        colors[ImGuiCol.SliderGrabActive]= new ImVec4(0.26f, 0.59f, 0.98f, 1.0f);
    }
}