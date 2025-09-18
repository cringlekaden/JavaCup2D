package Rendering;

import Core.Window;
import Util.AssetPool;
import Util.JCMath;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {

    private static final int MAX_LINES = 5000;
    private static final List<Line2D> lines = new ArrayList<>();
    private static final float[] vertices = new float[MAX_LINES * 12];
    private static final Shader shader = AssetPool.getShader("debug");
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
        addLine2D(from, to, new Vector3f(0.8f, 0.2f, 0.2f));
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        if(lines.size() >= MAX_LINES) return;
        lines.add(new Line2D(from, to, color, lifetime));
    }

    public static void addBox2D(Vector2f center, Vector2f size) {
        addBox2D(center, size, 0);
    }

    public static void addBox2D(Vector2f center, Vector2f size, Vector3f color) {
            addBox2D(center, size, 0, color);
    }

    public static void addBox2D(Vector2f center, Vector2f size, float rotation) {
        addBox2D(center, size, rotation, new Vector3f(0.8f, 0.2f, 0.2f));
    }

    public static void addBox2D(Vector2f center, Vector2f size, float rotation, Vector3f color) {
        addBox2D(center, size, rotation, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f size, float rotation, Vector3f color, int lifetime) {
        Vector2f min = new Vector2f(center).sub(new Vector2f(size).div(2.0f));
        Vector2f max = new Vector2f(center).add(new Vector2f(size).div(2.0f));
        Vector2f[] vertices = new Vector2f[] {
                new Vector2f(min.x, min.y),
                new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y),
                new Vector2f(max.x, min.y)
        };
        if(rotation != 0.0f) {
            for(Vector2f vertex : vertices)
                JCMath.rotate(vertex, rotation, center);
        }
        addLine2D(vertices[0], vertices[1], color, lifetime);
        addLine2D(vertices[1], vertices[2], color, lifetime);
        addLine2D(vertices[2], vertices[3], color, lifetime);
        addLine2D(vertices[3], vertices[0], color, lifetime);
    }

    public static void addCircle2D(Vector2f center, float radius) {
        addCircle2D(center, radius, new Vector3f(0.8f, 0.2f, 0.2f));
    }

    public static void addCircle2D(Vector2f center, float radius, Vector3f color) {
        addCircle2D(center, radius, color, 1);
    }

    public static void addCircle2D(Vector2f center, float radius, Vector3f color, int lifetime) {
        Vector2f[] points = new Vector2f[32];
        int increment = 360 / points.length;
        int currentAngle = 0;
        for(int i = 0; i < points.length; i++) {
            Vector2f current = new Vector2f(radius, 0);
            JCMath.rotate(current, currentAngle, new Vector2f());
            points[i] = new Vector2f(current).add(center);
            if(i > 0)
                addLine2D(points[i - 1], points[i], color, lifetime);
            currentAngle += increment;
        }
        addLine2D(points[points.length - 1], points[0], color, lifetime);
    }
}
