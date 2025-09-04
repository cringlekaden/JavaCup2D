package Rendering;

import Components.Sprite;
import JavaCup2D.Entity;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 50000;

    private List<RenderBatch> batches;

    public Renderer() {
        batches = new ArrayList<>();
    }

    public void render() {
        for(RenderBatch batch : batches)
            batch.render();
    }

    public void add(Entity entity) {
        Sprite sprite = entity.getComponent(Sprite.class);
        if(sprite != null)
            add(sprite);
    }

    public void add(Sprite sprite) {
        boolean added = false;
        for(RenderBatch batch : batches) {
            if(!batch.isFull()) {
                batch.addSprite(sprite);
                added = true;
                break;
            }
        }
        if(!added) {
            RenderBatch batch = new RenderBatch(MAX_BATCH_SIZE);
            batch.start();
            batch.addSprite(sprite);
            batches.add(batch);
        }
    }
}
