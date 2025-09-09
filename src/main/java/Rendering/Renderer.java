package Rendering;

import Components.Sprites.SpriteRenderer;
import JavaCup2D.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;

    private List<RenderBatch> batches;

    public Renderer() {
        batches = new ArrayList<>();
    }

    public void render() {
        for(RenderBatch batch : batches)
            batch.render();
    }

    public void add(Entity entity) {
        SpriteRenderer spriteRenderer = entity.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null)
            add(spriteRenderer);
    }

    public void add(SpriteRenderer spriteRenderer) {
        boolean added = false;
        for(RenderBatch batch : batches) {
            if(!batch.isFull() && batch.zIndex() == spriteRenderer.entity.zIndex()) {
                Texture texture = spriteRenderer.getTexture();
                if(texture == null || batch.hasTexture(texture) || !batch.isAtTextureLimit()) {
                    batch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }
        if(!added) {
            RenderBatch batch = new RenderBatch(MAX_BATCH_SIZE, spriteRenderer.entity.zIndex());
            batch.start();
            batches.add(batch);
            batch.addSprite(spriteRenderer);
            Collections.sort(batches);
        }
    }
}
