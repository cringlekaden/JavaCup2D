package Rendering;

import Components.Sprites.SpriteRenderer;
import Core.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;

    private static Shader currentShader;
    private List<RenderBatch> batches;

    public Renderer() {
        batches = new ArrayList<>();
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getCurrentShader() {
        return currentShader;
    }

    public void render() {
        currentShader.bind();
        for(RenderBatch batch : batches)
            batch.render();
    }

    public void addSpriteEntity(Entity entity) {
        SpriteRenderer spriteRenderer = entity.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null)
            addSpriteEntity(spriteRenderer);
    }

    public void addSpriteEntity(SpriteRenderer spriteRenderer) {
        boolean added = false;
        for(RenderBatch batch : batches) {
            if(!batch.isFull() && batch.zIndex() == spriteRenderer.entity.transform.zIndex) {
                Texture texture = spriteRenderer.getTexture();
                if(texture == null || batch.hasTexture(texture) || !batch.isAtTextureLimit()) {
                    batch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }
        if(!added) {
            RenderBatch batch = new RenderBatch(MAX_BATCH_SIZE, spriteRenderer.entity.transform.zIndex);
            batch.start();
            batches.add(batch);
            batch.addSprite(spriteRenderer);
            Collections.sort(batches);
        }
    }

    public void destroyEntity(Entity entity) {
        if(entity.getComponent(SpriteRenderer.class) == null) return;
        for(RenderBatch batch : batches) {
            if(batch.destroyIfExists(entity))
                return;
        }
    }
}
