package Rendering;

import Components.Sprite;
import JavaCup2D.Window;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {

    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private Sprite[] sprites;
    private Shader shader;
    private int numSprites, vaoID, vboID, maxBatchSize;
    private float[] vertices;
    private boolean isFull;

    public RenderBatch(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
        shader = new Shader("default");
        sprites = new Sprite[maxBatchSize];
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
        numSprites = 0;
        isFull = false;
    }

    public void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
        int iboID = glGenBuffers();
        int[] indices = generateIndices();
        IntBuffer intBuffer = BufferUtils.createIntBuffer(indices.length);
        intBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
    }

    public void render() {
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        shader.bind();
        shader.setUniform("uProjection", Window.getScene().getCamera().getProjection());
        shader.setUniform("uView", Window.getScene().getCamera().getView());
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        shader.unbind();
    }

    public void addSprite(Sprite sprite) {
        int index = numSprites;
        sprites[index] = sprite;
        numSprites++;
        loadVertexProperties(index);
        if(numSprites >= maxBatchSize)
            isFull = true;
    }

    public boolean isFull() {
        return isFull;
    }

    private void loadVertexProperties(int index) {
        Sprite sprite = sprites[index];
        int offset = index * 4 * VERTEX_SIZE;
        Vector4f color = sprite.getColor();
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for(int i = 0; i < 4; i++) {
            if(i == 1) yAdd = 0.0f;
            if(i == 2) xAdd = 0.0f;
            if(i == 3) yAdd = 1.0f;
            vertices[offset] = sprite.entity.transform.position.x + (xAdd * sprite.entity.transform.scale.x);
            vertices[offset + 1] = sprite.entity.transform.position.y + (yAdd * sprite.entity.transform.scale.y);
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;
            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        int[] elements = new int[6 * maxBatchSize];
        for(int i = 0; i < maxBatchSize; i++)
            loadElementIndices(elements, i);
        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset;
        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }
}
