package Core;

import Components.Animations.AnimationState;
import Components.Animations.StateMachine;
import Components.Sprites.Sprite;
import Components.Sprites.SpriteRenderer;
import Components.Sprites.Spritesheet;
import Util.AssetPool;

public class Prefabs {

    public static Entity generateSpriteEntity(Sprite sprite, float sizeX, float sizeY) {
        return generateSpriteEntity(sprite, sizeX, sizeY, 0);
    }

    public static Entity generateSpriteEntity(Sprite sprite, float sizeX, float sizeY, int zIndex) {
        Entity entity = Window.getScene().createEntity("GeneratedSpriteEntity");
        entity.transform.scale.x = sizeX;
        entity.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        entity.addComponent(renderer);
        return entity;
    }

    public static Entity generateMario() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("spritesheet.png");
        Entity mario = generateSpriteEntity(playerSprites.getSprite(0), 0.25f, 0.25f);
        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        mario.addComponent(stateMachine);
        return mario;
    }

    public static Entity generateQuestionBlock() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("items.png");
        Entity questionBlock = generateSpriteEntity(playerSprites.getSprite(0), 0.25f, 0.25f);
        AnimationState block = new AnimationState();
        block.title = "Question";
        float defaultFrameTime = 0.23f;
        block.addFrame(playerSprites.getSprite(0), 0.57f);
        block.addFrame(playerSprites.getSprite(1), defaultFrameTime);
        block.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        block.setLoop(true);
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(block);
        stateMachine.setDefaultState(block.title);
        questionBlock.addComponent(stateMachine);
        return questionBlock;
    }
}
