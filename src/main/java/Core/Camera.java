package Core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private Matrix4f inverseProjection;
    private Matrix4f inverseView;
    private final Vector2f position;
    private final Vector2f projectionSize;
    private float zoom;
    private float projectionWidth = 6;
    private float projectionHeight = 3;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        this.projectionSize = new Vector2f(projectionWidth, projectionHeight);
        zoom = 1.0f;
        setProjection();
    }

    public void setProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom, 0.0f, projectionSize.y * zoom, 0.0f, 100.0f);
        inverseProjection = new Matrix4f(projectionMatrix).invert();
    }

    public Matrix4f getView() {
        Vector3f forward = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f), forward.add(position.x, position.y, 0.0f), up);
        inverseView = new Matrix4f(viewMatrix).invert();
        return viewMatrix;
    }

    public Matrix4f getInverseProjection() {
        return inverseProjection;
    }

    public Matrix4f getInverseView() {
        return inverseView;
    }

    public Matrix4f getProjection() {
        return projectionMatrix;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void addZoom(float zoom) {
        this.zoom += zoom;
    }
}
