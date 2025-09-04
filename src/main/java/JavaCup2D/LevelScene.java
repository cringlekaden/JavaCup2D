package JavaCup2D;

public class LevelScene extends Scene {

    public LevelScene() {
    }

    @Override
    protected void init() {

    }

    @Override
    public void update(float dt) {
        System.out.println((int)(1.0f/dt) + " FPS");
    }
}
