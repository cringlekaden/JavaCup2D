package JavaCup2D;

public abstract class Component {

    public Entity entity = null;

    public Component() {}

    public void start() {}

    public abstract void update(float dt);
}
