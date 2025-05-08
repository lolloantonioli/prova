package it.unibo.view.Obstacles;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import it.unibo.controller.MovingObstacleController;
import it.unibo.model.Map.Obstacles.impl.MovingObstacles;
import it.unibo.model.Map.util.ObstacleType;
import it.unibo.view.ScaleManager;

/**
 * Manager class that handles interaction between the MovingObstacleController and MovingObstacleView.
 * This class is responsible for coordinating the rendering of all moving obstacles in the game.
 */
public class MovingObstacleViewManager {
    private final MovingObstacleView obstacleView;
    private MovingObstacleController obstacleController;
    private int viewportOffset;
    
    /**
     * Constructor for MovingObstacleViewManager.
     * 
     * @param scaleManager Scale manager for handling screen scaling
     */
    public MovingObstacleViewManager(ScaleManager scaleManager) {
        this.obstacleView = new MovingObstacleView(scaleManager);
        this.viewportOffset = 0;
    }
    
    /**
     * Sets the obstacle controller for this view manager.
     * 
     * @param controller The moving obstacle controller
     */
    public void setObstacleController(MovingObstacleController controller) {
        this.obstacleController = controller;
    }
    
    /**
     * Updates the viewport offset for proper rendering.
     * 
     * @param offset Current viewport Y offset
     */
    public void updateViewportOffset(int offset) {
        this.viewportOffset = offset;
    }
    
    /**
     * Renders all moving obstacles.
     * 
     * @param g Graphics context
     */
    public void render(Graphics g) {
        if (obstacleController == null) {
            return; // Can't render without a controller
        }
        
        // Get all active obstacles from the controller
        List<MovingObstacles> allObstacles = new ArrayList<>();
        
        // Add cars
        allObstacles.addAll(obstacleController.getObstaclesByType(ObstacleType.CAR));
        
        // Add trains
        allObstacles.addAll(obstacleController.getObstaclesByType(ObstacleType.TRAIN));
        
        // Render the obstacles
        obstacleView.renderObstacles(g, allObstacles, viewportOffset);
    }
    
    /**
     * Updates the scale manager when screen dimensions change.
     * 
     * @param scaleManager The updated scale manager
     */
    public void updateScaleManager(ScaleManager scaleManager) {
        obstacleView.updateScaleManager(scaleManager);
    }
    
    /**
     * Gets the obstacle view component.
     * 
     * @return The moving obstacle view
     */
    public MovingObstacleView getObstacleView() {
        return obstacleView;
    }
    
    /**
     * Checks if debugging visualizations should be enabled.
     * Could be used to toggle debug features like hitbox visualization.
     * 
     * @param debug Whether debug mode is enabled
     */
    public void setDebugMode(boolean debug) {
        // Implementation could enable visual debugging features
        // For example, showing collision hitboxes
    }
}