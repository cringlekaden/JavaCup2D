package Editor;

import Core.Window;
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
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class ImGuiLayer {

    private final long windowPtr;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final GameViewWindow gameViewWindow;
    private final PropertiesWindow propertiesWindow;
    private final SceneHierarchyWindow sceneHierarchyWindow;
    private final MenuBar menuBar;

    public ImGuiLayer(long windowPtr, PickingTexture pickingTexture) {
        this.windowPtr = windowPtr;
        gameViewWindow = new GameViewWindow();
        propertiesWindow = new PropertiesWindow(pickingTexture);
        sceneHierarchyWindow = new SceneHierarchyWindow();
        menuBar = new MenuBar();
    }

    public void init() {
        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setBackendPlatformName("imgui_java_impl_glfw");
        setStyle();
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
        imGuiGlfw.init(windowPtr, true);
        imGuiGl3.init("#version 410 core");
    }

    public void startFrame(float dt) {
        imGuiGlfw.newFrame();
        imGuiGl3.newFrame();
        ImGui.newFrame();
    }

    public void endFrame() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Window.getWidth(), Window.getHeight());
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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
        sceneHierarchyWindow.imgui();
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
        ImGuiViewport mainViewPort = ImGui.getMainViewport();
        ImGui.setNextWindowPos(mainViewPort.getWorkPosX(), mainViewPort.getWorkPosY());
        ImGui.setNextWindowSize(mainViewPort.getWorkSizeX(), mainViewPort.getWorkSizeY());
        ImGui.setNextWindowViewport(mainViewPort.getID());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                        ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                        ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
        ImGui.begin("Dockspace", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);
        ImGui.dockSpace(ImGui.getID("Dockspace"));
        menuBar.imgui();
        ImGui.end();
    }

    private void setStyle() {
        ImGuiStyle style = ImGui.getStyle();

        // Rounding for flat style
        style.setWindowRounding(0.0f);
        style.setFrameRounding(0.0f);
        style.setGrabRounding(0.0f);
        style.setScrollbarRounding(0.0f);
        style.setPopupRounding(0.0f);

        // Borders & padding
        style.setWindowBorderSize(0.0f);
        style.setFrameBorderSize(0.0f);
        style.setPopupBorderSize(0.0f);
        style.setWindowPadding(8.0f, 8.0f);
        style.setFramePadding(5.0f, 3.0f);
        style.setItemSpacing(8.0f, 4.0f);

        // Colors (Hazel-inspired dark gray/black theme with subtle gray-green accents)
        style.setColor(ImGuiCol.WindowBg, 0.10f, 0.105f, 0.11f, 1.00f);
        style.setColor(ImGuiCol.ChildBg, 0.10f, 0.105f, 0.11f, 1.00f);
        style.setColor(ImGuiCol.PopupBg, 0.08f, 0.085f, 0.09f, 0.94f);
        style.setColor(ImGuiCol.DockingEmptyBg, 0.20f, 0.205f, 0.21f, 1.00f);

        style.setColor(ImGuiCol.Border, 0.20f, 0.205f, 0.21f, 0.50f);
        style.setColor(ImGuiCol.Separator, 0.43f, 0.43f, 0.50f, 0.50f);
        style.setColor(ImGuiCol.SeparatorHovered, 0.25f, 0.28f, 0.24f, 0.78f);  // Muted gray-green
        style.setColor(ImGuiCol.SeparatorActive, 0.25f, 0.28f, 0.24f, 1.00f);  // Muted gray-green

        style.setColor(ImGuiCol.MenuBarBg, 0.14f, 0.14f, 0.14f, 1.00f);

        style.setColor(ImGuiCol.Header, 0.20f, 0.205f, 0.21f, 1.00f);
        style.setColor(ImGuiCol.HeaderHovered, 0.25f, 0.28f, 0.24f, 1.00f);  // Muted gray-green
        style.setColor(ImGuiCol.HeaderActive, 0.25f, 0.28f, 0.24f, 1.00f);  // Muted gray-green (to fix tab top highlight)

        style.setColor(ImGuiCol.Button, 0.20f, 0.205f, 0.21f, 1.00f);
        style.setColor(ImGuiCol.ButtonHovered, 0.25f, 0.28f, 0.24f, 1.00f);  // Muted gray-green
        style.setColor(ImGuiCol.ButtonActive, 0.15f, 0.1505f, 0.151f, 1.00f);

        style.setColor(ImGuiCol.FrameBg, 0.20f, 0.205f, 0.21f, 1.00f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.25f, 0.28f, 0.24f, 1.00f);  // Muted gray-green
        style.setColor(ImGuiCol.FrameBgActive, 0.15f, 0.1505f, 0.151f, 1.00f);

        // Tab colors (with muted gray-green for active/hovered to avoid blue and tone down green)
        style.setColor(ImGuiCol.Tab, 0.15f, 0.1505f, 0.151f, 1.00f);
        style.setColor(ImGuiCol.TabHovered, 0.25f, 0.28f, 0.24f, 1.00f);  // Muted gray-green
        style.setColor(ImGuiCol.TabActive, 0.25f, 0.28f, 0.24f, 1.00f);  // Muted gray-green
        style.setColor(ImGuiCol.TabUnfocused, 0.15f, 0.1505f, 0.151f, 1.00f);
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.20f, 0.205f, 0.21f, 1.00f);

        style.setColor(ImGuiCol.TitleBg, 0.15f, 0.1505f, 0.151f, 1.00f);
        style.setColor(ImGuiCol.TitleBgActive, 0.15f, 0.1505f, 0.151f, 1.00f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.15f, 0.1505f, 0.151f, 1.00f);

        style.setColor(ImGuiCol.ScrollbarBg, 0.02f, 0.02f, 0.02f, 0.53f);
        style.setColor(ImGuiCol.ScrollbarGrab, 0.31f, 0.31f, 0.31f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.41f, 0.41f, 0.41f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.51f, 0.51f, 0.51f, 1.00f);

        style.setColor(ImGuiCol.ResizeGrip, 0.20f, 0.205f, 0.21f, 0.25f);
        style.setColor(ImGuiCol.ResizeGripHovered, 0.25f, 0.28f, 0.24f, 0.67f);  // Muted gray-green
        style.setColor(ImGuiCol.ResizeGripActive, 0.38f, 0.3805f, 0.381f, 0.95f);

        style.setColor(ImGuiCol.CheckMark, 0.25f, 0.28f, 0.24f, 1.00f);  // Muted gray-green
        style.setColor(ImGuiCol.SliderGrab, 0.24f, 0.245f, 0.25f, 1.00f);
        style.setColor(ImGuiCol.SliderGrabActive, 0.26f, 0.265f, 0.27f, 1.00f);

        // Verify tab colors
        ImVec4 tabActive = new ImVec4();
        style.getColor(ImGuiCol.TabActive, tabActive);
        System.out.println("TabActive color set to: R=" + tabActive.x + ", G=" + tabActive.y + ", B=" + tabActive.z + ", A=" + tabActive.w);
        ImVec4 tabHovered = new ImVec4();
        style.getColor(ImGuiCol.TabHovered, tabHovered);
        System.out.println("TabHovered color set to: R=" + tabHovered.x + ", G=" + tabHovered.y + ", B=" + tabHovered.z + ", A=" + tabHovered.w);
    }
}