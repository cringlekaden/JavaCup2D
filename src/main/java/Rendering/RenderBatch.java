package Rendering;

import Components.Sprites.SpriteRenderer;
import Core.Entity;
import Core.Window;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {

    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEXCOORD_SIZE = 2;
    private final int TEXID_SIZE = 1;
    private final int ENTITYID_SIZE = 1;
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    private final int TEXCOORD_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXID_OFFSET = TEXCOORD_OFFSET + TEXCOORD_SIZE * Float.BYTES;
    private final int ENTITYID_OFFSET = TEXID_OFFSET + TEXID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private final List<Texture> textures;
    private final SpriteRenderer[] spriteRenderers;
    private Renderer renderer;
    private int numSprites;
    private int vaoID;
    private int vboID;
    private final int maxBatchSize;
    private final int zIndex;
    private final float[] vertices;
    private final int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};
    private boolean isFull;

    public RenderBatch(Renderer renderer, int maxBatchSize, int zIndex) {
        this.renderer = renderer;
        this.maxBatchSize = maxBatchSize;
        this.zIndex = zIndex;
        spriteRenderers = new SpriteRenderer[maxBatchSize];
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
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
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
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glVertexAttribPointer(2, TEXCOORD_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXCOORD_OFFSET);
        glVertexAttribPointer(3, TEXID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXID_OFFSET);
        glVertexAttribPointer(4, ENTITYID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITYID_OFFSET);
    }

    public void render() {
        boolean needsRebuffer = false;
        for(int i = 0; i < numSprites; i++) {
            SpriteRenderer sprite = spriteRenderers[i];
            if(sprite.isDirty()) {
                loadVertexProperties(i);
                sprite.isClean();
                needsRebuffer = true;
            }
            if(sprite.entity.transform.zIndex != zIndex) {
                destroyIfExists(sprite.entity);
                renderer.addSpriteEntity(sprite.entity);
                i--;
            }
        }
        if(needsRebuffer) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
        Shader shader = Renderer.getCurrentShader();
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
        glEnableVertexAttribArray(4);
        glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(4);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        for (Texture texture : textures) texture.unbind();
        shader.unbind();
    }

    public void addSprite(SpriteRenderer spriteRenderer) {
        int index = numSprites;
        spriteRenderers[index] = spriteRenderer;
        numSprites++;
        if(spriteRenderer.getTexture() != null)
            if(!textures.contains(spriteRenderer.getTexture()))
                textures.add(spriteRenderer.getTexture());
        loadVertexProperties(index);
        if(numSprites >= maxBatchSize)
            isFull = true;
    }

    public boolean destroyIfExists(Entity entity) {
        SpriteRenderer spriteRenderer = entity.getComponent(SpriteRenderer.class);
        for(int i = 0; i < numSprites; i++) {
            if(spriteRenderers[i] == spriteRenderer) {
                for(int j = i; j < numSprites - 1; j++) {
                    spriteRenderers[j] = spriteRenderers[j + 1];
                    spriteRenderers[j].setDirty();
                }
                numSprites--;
                return true;
            }
        }
        return false;
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

    public int zIndex() {
        return zIndex;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer spriteRenderer = spriteRenderers[index];
        int offset = index * 4 * VERTEX_SIZE;
        Vector4f color = spriteRenderer.getColor();
        int texID = 0;
        if(spriteRenderer.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if(textures.get(i).equals(spriteRenderer.getTexture())) {
                    texID = i + 1;
                    break;
                }
            }
        }
        boolean hasRotation = spriteRenderer.entity.transform.rotation != 0.0f;
        Matrix4f transform = new Matrix4f().identity();
        if(hasRotation) {
            transform.translate(spriteRenderer.entity.transform.position.x, spriteRenderer.entity.transform.position.y, 0);
            transform.rotate((float)Math.toRadians(spriteRenderer.entity.transform.rotation), 0, 0, 1);
            transform.scale(spriteRenderer.entity.transform.scale.x, spriteRenderer.entity.transform.scale.y, 1);
        }
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for(int i = 0; i < 4; i++) {
            if(i == 1) yAdd = -0.5f;
            if(i == 2) xAdd = -0.5f;
            if(i == 3) yAdd = 0.5f;
            Vector4f currentPosition = new Vector4f(spriteRenderer.entity.transform.position.x + (xAdd * spriteRenderer.entity.transform.scale.x),
                    spriteRenderer.entity.transform.position.y + (yAdd * spriteRenderer.entity.transform.scale.y), 0, 1);
            if(hasRotation)
                currentPosition = new Vector4f(xAdd, yAdd, 0, 1).mul(transform);
            vertices[offset] = currentPosition.x;
            vertices[offset + 1] = currentPosition.y;
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;
            vertices[offset + 6] = spriteRenderer.getTexCoords()[i].x;
            vertices[offset + 7] = spriteRenderer.getTexCoords()[i].y;
            vertices[offset + 8] = texID;
            vertices[offset + 9] = spriteRenderer.entity.getID() + 1;
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

    @Override
    public int compareTo(@NotNull RenderBatch o) {
        return Integer.compare(zIndex, o.zIndex());
    }
}
