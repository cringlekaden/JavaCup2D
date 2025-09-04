package Rendering;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class Shader {

    private String filename;
    private String vertexSource;
    private String fragmentSource;
    private boolean isBound = false;
    private int programID;

    public Shader(String filename) {
        this.filename = filename;
        try {
            this.vertexSource = new String(Files.readAllBytes(Paths.get("./assets/shaders/"+filename+".vsh")));
            this.fragmentSource = new String(Files.readAllBytes(Paths.get("./assets/shaders/"+filename+".fsh")));
        } catch(IOException e) {
            e.printStackTrace();
            assert false : "Error opening shader file: " + filename;
        }
        compile();
    }

    public void setUniform(String name, Matrix4f matrix) {
        bind();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.get(buffer);
        glUniformMatrix4fv(glGetUniformLocation(programID, name), false, buffer);
    }

    public void setUniform(String name, Matrix3f matrix) {
        bind();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        matrix.get(buffer);
        glUniformMatrix3fv(glGetUniformLocation(programID, name), false, buffer);
    }

    public void setUniform(String name, Vector4f vector) {
        bind();
        glUniform4f(glGetUniformLocation(programID, name), vector.x, vector.y, vector.z, vector.w);
    }

    public void setUniform(String name, Vector3f vector) {
        bind();
        glUniform3f(glGetUniformLocation(programID, name), vector.x, vector.y, vector.z);
    }

    public void setUniform(String name, Vector2f vector) {
        bind();
        glUniform2f(glGetUniformLocation(programID, name), vector.x, vector.y);
    }

    public void setUniform(String name, float value) {
        bind();
        glUniform1f(glGetUniformLocation(programID, name), value);
    }

    public void setUniform(String name, int value) {
        bind();
        glUniform1i(glGetUniformLocation(programID, name), value);
    }

    public void setTextureSlot(String name, int slot) {
        bind();
        glUniform1i(glGetUniformLocation(programID, name), slot);
    }

    private void compile() {
        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        if(glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }
        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
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
    }

    public void bind() {
        if(!isBound) {
            glUseProgram(programID);
            isBound = true;
        }
    }

    public void unbind() {
        if(isBound) {
            glUseProgram(0);
            isBound = false;
        }
    }
}
