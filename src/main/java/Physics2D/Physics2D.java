package Physics2D;

import Components.Transform;
import Core.Entity;
import Physics2D.Components.Box2DCollider;
import Physics2D.Components.Circle2DCollider;
import Physics2D.Components.RigidBody2D;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

public class Physics2D {

    private Vec2 gravity = new Vec2(0, -10);
    private World world = new World(gravity);
    private int velocityIterations = 16;
    private int positionIterations = 6;
    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1/60.0f;

    public void add(Entity entity) {
        RigidBody2D rb = entity.getComponent(RigidBody2D.class);
        if(rb != null && rb.getRawBody() == null) {
            Transform transform = entity.transform;
            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            bodyDef.bullet = rb.isContinuousCollision();
            switch(rb.getBodyType()) {
                case Kinematic:
                    bodyDef.type = BodyType.KINEMATIC;
                    break;
                case Dynamic:
                    bodyDef.type = BodyType.DYNAMIC;
                    break;
                case Static:
                    bodyDef.type = BodyType.STATIC;
                    break;
            }
            PolygonShape shape = new PolygonShape();
            Circle2DCollider circle;
            Box2DCollider box;
            if((circle = entity.getComponent(Circle2DCollider.class)) != null) {
                shape.setRadius(circle.getRadius());
            } else if((box = entity.getComponent(Box2DCollider.class)) != null) {
                Vector2f halfSize = new Vector2f(box.getHalfSize()).mul(0.5f);
                Vector2f offset = box.getOffset();
                Vector2f origin = new Vector2f(box.getOrigin());
                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);
                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos, yPos);
            }
            Body body = world.createBody(bodyDef);
            rb.setRawBody(body);
            body.createFixture(shape, rb.getMass());
        }
    }

    public void update(float dt) {
        physicsTime += dt;
        if(physicsTime >= 0) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }

    public void destroyEntity(Entity entity) {
        RigidBody2D rb = entity.getComponent(RigidBody2D.class);
        if(rb != null) {
            if(rb.getRawBody() != null) {
                world.destroyBody(rb.getRawBody());
                rb.setRawBody(null);
            }
        }
    }
}
