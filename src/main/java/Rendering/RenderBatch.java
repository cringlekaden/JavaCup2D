package Rendering;

import Components.Sprite;
import JavaCup2D.Window;
import Util.AssetPool;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {

    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEXCOORD_SIZE = 2;
    private final int TEXID_SIZE = 1;
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    private final int TEXCOORD_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXID_OFFSET = TEXCOORD_OFFSET + TEXCOORD_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private List<Texture> textures;
    private Sprite[] sprites;
    private Shader shader;
    private int numSprites, vaoID, vboID, maxBatchSize;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};
    private boolean isFull;

    public RenderBatch(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
        shader = AssetPool.getShader("default");
        sprites = new Sprite[maxBatchSize];
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
        textures = new ArrayList<>();
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
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glVertexAttribPointer(2, TEXCOORD_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXCOORD_OFFSET);
        glVertexAttribPointer(3, TEXID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXID_OFFSET);
    }

    public void render() {
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        shader.bind();
        shader.setUniform("uProjection", Window.getScene().getCamera().getProjection());
        shader.setUniform("uView", Window.getScene().getCamera().getView());
        for(int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.setTextureSlot("uTextures", texSlots);
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        for (Texture texture : textures) texture.unbind();
        shader.unbind();
    }

    public void addSprite(Sprite sprite) {
        int index = numSprites;
        sprites[index] = sprite;
        numSprites++;
        if(sprite.getTexture() != null)
            if(!textures.contains(sprite.getTexture()))
                textures.add(sprite.getTexture());
        loadVertexProperties(index);
        if(numSprites >= maxBatchSize)
            isFull = true;
    }

    public boolean isFull() {
        return isFull;
    }

    public boolean isAtTextureLimit() {
        return textures.size() > 7;
    }

    public boolean hasTexture(Texture texture) {
        return textures.contains(texture);
    }

    private void loadVertexProperties(int index) {
        Sprite sprite = sprites[index];
        int offset = index * 4 * VERTEX_SIZE;
        Vector4f color = sprite.getColor();
        int texID = 0;
        if(sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if(textures.get(i) == sprite.getTexture()) {
                    texID = i + 1;
                    break;
                }
            }
        }
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
            vertices[offset + 6] = sprite.getTexCoords()[i].x;
            vertices[offset + 7] = sprite.getTexCoords()[i].y;
            vertices[offset + 8] = texID;
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
