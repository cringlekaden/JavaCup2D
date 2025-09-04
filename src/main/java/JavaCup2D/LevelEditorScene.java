package JavaCup2D;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private String vertexShaderSource = "#version 410 core\n" +
            "\n" +
            "layout (location = 0) in vec3 vPosition;\n" +
            "layout (location = 1) in vec4 vColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = vColor;\n" +
            "    gl_Position = vec4(vPosition, 1.0);\n" +
            "}";
    private String fragmentShaderSource = "#version 410 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";
    private int vertexID, fragmentID, programID;
    private int vaoID, vboID, iboID;
    private float[] vertices = { 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                                    -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
                                    0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                                    -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f };
    private int[] indices = { 2, 1, 0, 0, 1, 3 };

    public LevelEditorScene() {
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexShaderSource);
        glCompileShader(vertexID);
        if(glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentShaderSource);
        glCompileShader(fragmentID);
        if(glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }
        programID = glCreateProgram();
        glAttachShader(programID, vertexID);
        glAttachShader(programID, fragmentID);
        glLinkProgram(programID);
        if(glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            int len = glGetProgrami(programID, GL_INFO_LOG_LENGTH);
            System.out.println(glGetProgramInfoLog(programID, len));
            assert false : "";
        }
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(vertices.length);
        vertexData.put(vertices).flip();
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
        IntBuffer indexData = BufferUtils.createIntBuffer(indices.length);
        indexData.put(indices).flip();
        iboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 28, 0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 28, 12);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
    }

    @Override
    public void update(float dt) {
        System.out.println((int)(1.0f/dt) + " FPS");
        glUseProgram(programID);
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        glUseProgram(0);
    }
}
