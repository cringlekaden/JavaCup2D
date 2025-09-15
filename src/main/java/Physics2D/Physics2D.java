package Physics2D;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Physics2D {

    private Vec2 gravity = new Vec2(0, -10);
    private World world = new World(gravity);
    private int velocityIterations = 16;
    private int positionIterations = 6;
    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1/60.0f;

    public void update(float dt) {
        physicsTime += dt;
        if(physicsTime >= 0) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }
}
