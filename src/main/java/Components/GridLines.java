package Components;

import Core.Camera;
import Core.Window;
import Rendering.DebugDraw;
import Util.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GridLines extends Component {

    @Override
    public void update(float dt) {
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraPosition = camera.getPosition();
        Vector2f projectionSize = camera.getProjectionSize();
        int firstX = ((int)(cameraPosition.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_WIDTH;
        int firstY = ((int)(cameraPosition.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;
        int verticalLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        int horizontalLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;
        int height = (int)(projectionSize.y * camera.getZoom()) + Settings.GRID_WIDTH * 2;
        int width = (int)(projectionSize.x * camera.getZoom()) + Settings.GRID_HEIGHT * 2;
        int maxLines = Math.max(verticalLines, horizontalLines);
        for(int i = 0; i < maxLines; i++) {
            int x = firstX + (Settings.GRID_WIDTH * i);
            int y = firstY + (Settings.GRID_HEIGHT * i);
            if(i < verticalLines)
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), new Vector3f(0, 0, 0));
            if(i < horizontalLines)
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), new Vector3f(0, 0, 0));
        }
    }
}
