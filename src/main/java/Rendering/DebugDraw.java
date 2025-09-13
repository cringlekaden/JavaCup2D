package Rendering;

import Core.Window;
import Util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {

    private static int MAX_LINES = 500;
    private static List<Line2D> lines = new ArrayList<>();
    private static float[] vertices = new float[MAX_LINES * 12];
    private static Shader shader = AssetPool.getShader("debugLine2D");
    private static int vaoID;
    private static int vboID;
    private static boolean started = false;

    public static void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public static void beginFrame() {
        if(!started) {
            start();
            started = true;
        }
        for(int i = 0; i < lines.size(); i++) {
            if(lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw() {
        if(lines.isEmpty()) return;
        int index = 0;
        for(Line2D line : lines) {
            for(int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();
                vertices[index++] = position.x;
                vertices[index++] = position.y;
                vertices[index++] = -10.0f;
                vertices[index++] = color.x;
                vertices[index++] = color.y;
                vertices[index++] = color.z;
            }
        }
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertices, 0, lines.size() * 12));
        shader.bind();
        shader.setUniform("uProjection", Window.getScene().getCamera().getProjection());
        shader.setUniform("uView", Window.getScene().getCamera().getView());
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawArrays(GL_LINES, 0, lines.size() * 12);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        shader.unbind();
    }

    public static void addLine2D(Vector2f from, Vector2f to) {
        addLine2D(from, to, new Vector3f(0, 0, 0));
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        if(lines.size() >= MAX_LINES) return;
        lines.add(new Line2D(from, to, color, lifetime));
    }
}
