package Editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class JCImGui {

    private static final float DEFAULT_COLUMN_WIDTH = 160.0f;

    public static void drawVec2Control(String label, Vector2f values) {
        drawVec2Control(label, values, 0.0f);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue) {
        drawVec2Control(label, values, resetValue, DEFAULT_COLUMN_WIDTH);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue, float columnWidth) {
        ImGui.pushID(label);
        if (ImGui.beginTable("##table", 2, ImGuiTableFlags.BordersInnerV)) {
            ImGui.tableSetupColumn("Label", ImGuiTableColumnFlags.WidthFixed, columnWidth);
            ImGui.tableSetupColumn("Values", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableNextColumn();
            ImGui.text(label);
            ImGui.tableNextColumn();
            float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
            float buttonSize = lineHeight + 3.0f;
            float availWidth = ImGui.getContentRegionAvailX();
            float widthEach = (availWidth - buttonSize * 2.0f - ImGui.getStyle().getItemSpacingX() * 3) / 2.0f;
            // X
            ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
            if (ImGui.button("X", buttonSize, lineHeight))
                values.x = resetValue;
            ImGui.popStyleColor(3);
            ImGui.sameLine();
            ImGui.setNextItemWidth(widthEach);
            float[] vx = {values.x};
            ImGui.dragFloat("##x", vx, 0.1f);
            values.x = vx[0];
            // Y
            ImGui.sameLine();
            ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
            if (ImGui.button("Y", buttonSize, lineHeight))
                values.y = resetValue;
            ImGui.popStyleColor(3);
            ImGui.sameLine();
            ImGui.setNextItemWidth(widthEach);
            float[] vy = {values.y};
            ImGui.dragFloat("##y", vy, 0.1f);
            values.y = vy[0];
            ImGui.endTable();
        }
        ImGui.popID();
    }

    public static void drawVec3Control(String label, Vector3f values) {
        drawVec3Control(label, values, 0.0f);
    }

    public static void drawVec3Control(String label, Vector3f values, float resetValue) {
        drawVec3Control(label, values, resetValue, DEFAULT_COLUMN_WIDTH);
    }

    public static void drawVec3Control(String label, Vector3f values, float resetValue, float columnWidth) {
        ImGui.pushID(label);
        if (ImGui.beginTable("##table", 2, ImGuiTableFlags.BordersInnerV)) {
            ImGui.tableSetupColumn("Label", ImGuiTableColumnFlags.WidthFixed, columnWidth);
            ImGui.tableSetupColumn("Values", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableNextColumn();
            ImGui.text(label);
            ImGui.tableNextColumn();
            float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
            float buttonSize = lineHeight + 3.0f;
            float availWidth = ImGui.getContentRegionAvailX();
            float widthEach = (availWidth - buttonSize * 3.0f - ImGui.getStyle().getItemSpacingX() * 4) / 3.0f;
            // X
            ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
            if (ImGui.button("X", buttonSize, lineHeight))
                values.x = resetValue;
            ImGui.popStyleColor(3);
            ImGui.sameLine();
            ImGui.setNextItemWidth(widthEach);
            float[] vx = {values.x};
            ImGui.dragFloat("##x", vx, 0.1f);
            values.x = vx[0];
            // Y
            ImGui.sameLine();
            ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
            if (ImGui.button("Y", buttonSize, lineHeight))
                values.y = resetValue;
            ImGui.popStyleColor(3);
            ImGui.sameLine();
            ImGui.setNextItemWidth(widthEach);
            float[] vy = {values.y};
            ImGui.dragFloat("##y", vy, 0.1f);
            values.y = vy[0];
            // Z
            ImGui.sameLine();
            ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.25f, 0.8f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.35f, 0.9f, 1.0f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.25f, 0.8f, 1.0f);
            if (ImGui.button("Z", buttonSize, lineHeight))
                values.z = resetValue;
            ImGui.popStyleColor(3);
            ImGui.sameLine();
            ImGui.setNextItemWidth(widthEach);
            float[] vz = {values.z};
            ImGui.dragFloat("##z", vz, 0.1f);
            values.z = vz[0];
            ImGui.endTable();
        }
        ImGui.popID();
    }

    public static float drawFloatControl(String label, float value) {
        ImGui.pushID(label);
        float[] val = {value};
        if (ImGui.beginTable("##table", 2, ImGuiTableFlags.BordersInnerV)) {
            ImGui.tableSetupColumn("Label", ImGuiTableColumnFlags.WidthFixed, DEFAULT_COLUMN_WIDTH);
            ImGui.tableSetupColumn("Value", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableNextColumn();
            ImGui.text(label);
            ImGui.tableNextColumn();
            ImGui.setNextItemWidth(-1); // take full available width
            ImGui.dragFloat("##df", val, 0.1f);
            ImGui.endTable();
        }
        ImGui.popID();
        return val[0];
    }

    public static int drawIntControl(String label, int value) {
        ImGui.pushID(label);
        int[] val = {value};
        if (ImGui.beginTable("##table", 2, ImGuiTableFlags.BordersInnerV)) {
            ImGui.tableSetupColumn("Label", ImGuiTableColumnFlags.WidthFixed, DEFAULT_COLUMN_WIDTH);
            ImGui.tableSetupColumn("Value", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableNextColumn();
            ImGui.text(label);
            ImGui.tableNextColumn();
            ImGui.setNextItemWidth(-1);
            ImGui.dragInt("##di", val, 0.1f);

            ImGui.endTable();
        }
        ImGui.popID();
        return val[0];
    }

    public static boolean drawColorControl4(String label, Vector4f color) {
        boolean result = false;
        ImGui.pushID(label);
        if (ImGui.beginTable("##table", 2, ImGuiTableFlags.BordersInnerV)) {
            ImGui.tableSetupColumn("Label", ImGuiTableColumnFlags.WidthFixed, DEFAULT_COLUMN_WIDTH);
            ImGui.tableSetupColumn("Value", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableNextColumn();
            ImGui.text(label);
            ImGui.tableNextColumn();
            ImGui.setNextItemWidth(-1);
            float[] imColor = {color.x, color.y, color.z, color.w};
            if (ImGui.colorEdit4("##ce", imColor)) {
                color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
                result = true;
            }
            ImGui.endTable();
        }
        ImGui.popID();
        return result;
    }

    public static String drawTextControl(String label, String text) {
        ImGui.pushID(label);
        if (ImGui.beginTable("##table", 2, ImGuiTableFlags.BordersInnerV)) {
            ImGui.tableSetupColumn("Label", ImGuiTableColumnFlags.WidthFixed, DEFAULT_COLUMN_WIDTH);
            ImGui.tableSetupColumn("Value", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableNextColumn();
            ImGui.text(label);
            ImGui.tableNextColumn();
            ImGui.setNextItemWidth(-1);
            ImString result = new ImString(text, 256);
            if(ImGui.inputText("##" + label, result)) {
                ImGui.endTable();
                ImGui.popID();
                return result.get();
            }
            ImGui.endTable();
        }
        ImGui.popID();
        return text;
    }
}