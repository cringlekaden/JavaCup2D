package Scenes;

public class LevelScene extends Scene {

    public LevelScene() {
    }

    @Override
    public void init() {

    }

    @Override
    public void update(float dt) {
        System.out.println((int)(1.0f/dt) + " FPS");
    }

    @Override
    public void render() {}
}
