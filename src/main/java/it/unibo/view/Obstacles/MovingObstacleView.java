package it.unibo.view.Obstacles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibo.model.Map.Obstacles.impl.MovingObstacles;
import it.unibo.model.Map.util.ObstacleType;
import it.unibo.view.ScaleManager;

/**
 * Component responsible for rendering moving obstacles in the game view.
 * This class handles the visual representation of cars and trains.
 */
public class MovingObstacleView {
    // Cache of loaded images
    private final Map<ObstacleType, Color> obstacleColors;
    private ScaleManager scaleManager;
    
    // Constants for obstacle visual dimensions
    private static final int CAR_WIDTH = 50;
    private static final int CAR_HEIGHT = 30;
    private static final int TRAIN_WIDTH = 200;
    private static final int TRAIN_HEIGHT = 40;
    
    /**
     * Constructor for MovingObstacleView.
     * 
     * @param scaleManager The scale manager to handle screen scaling
     */
    public MovingObstacleView(ScaleManager scaleManager) {
        this.scaleManager = scaleManager;
        
        // Initialize colors for different obstacle types
        obstacleColors = new HashMap<>();
        obstacleColors.put(ObstacleType.CAR, Color.RED);
        obstacleColors.put(ObstacleType.TRAIN, Color.BLUE);
    }
    
    /**
     * Renders all moving obstacles.
     * 
     * @param g Graphics context
     * @param obstacles List of moving obstacles to render
     * @param viewportOffset The Y offset of the viewport
     */
    public void renderObstacles(Graphics g, List<MovingObstacles> obstacles, int viewportOffset) {
        Graphics2D g2d = (Graphics2D) g;
        
        for (MovingObstacles obstacle : obstacles) {
            if (obstacle.isVisible()) {
                // Get screen coordinates
                int screenX = scaleManager.scaleX(obstacle.getX());
                int screenY = scaleManager.scaleY(obstacle.getY() - viewportOffset);
                
                // Draw the obstacle based on its type
                renderObstacle(g2d, obstacle, screenX, screenY);
            }
        }
    }
    
    /**
     * Renders a single obstacle.
     * 
     * @param g2d Graphics2D context
     * @param obstacle The obstacle to render
     * @param screenX The screen X coordinate
     * @param screenY The screen Y coordinate
     */
    private void renderObstacle(Graphics2D g2d, MovingObstacles obstacle, int screenX, int screenY) {
        ObstacleType type = obstacle.getType();
        
        // Get the appropriate color
        Color color = obstacleColors.getOrDefault(type, Color.GRAY);
        g2d.setColor(color);
        
        // Calculate dimensions based on obstacle type
        int width, height;
        if (type == ObstacleType.CAR) {
            width = scaleManager.scaleWidth(CAR_WIDTH);
            height = scaleManager.scaleHeight(CAR_HEIGHT);
            
            // Draw the car (simple rectangle for now)
            g2d.fillRect(screenX, screenY, width, height);
            
            // Add wheels
            g2d.setColor(Color.BLACK);
            int wheelSize = height / 3;
            
            // Front wheel
            g2d.fillOval(screenX + width - wheelSize - 5, 
                         screenY + height - wheelSize,
                         wheelSize, wheelSize);
            
            // Back wheel
            g2d.fillOval(screenX + 5, 
                         screenY + height - wheelSize,
                         wheelSize, wheelSize);
            
            // Direction indicator (headlights)
            g2d.setColor(Color.YELLOW);
            if (obstacle.getSpeed() > 0) { // Moving right
                g2d.fillRect(screenX + width - 5, screenY + 5, 5, 5);
                g2d.fillRect(screenX + width - 5, screenY + height - 10, 5, 5);
            } else { // Moving left
                g2d.fillRect(screenX, screenY + 5, 5, 5);
                g2d.fillRect(screenX, screenY + height - 10, 5, 5);
            }
            
        } else if (type == ObstacleType.TRAIN) {
            width = scaleManager.scaleWidth(TRAIN_WIDTH);
            height = scaleManager.scaleHeight(TRAIN_HEIGHT);
            
            // Draw the train body
            g2d.fillRect(screenX, screenY, width, height);
            
            // Add windows
            g2d.setColor(Color.LIGHT_GRAY);
            int windowWidth = width / 10;
            int windowHeight = height / 2;
            int windowY = screenY + (height - windowHeight) / 2;
            
            // Draw multiple windows along the train
            for (int i = 1; i < 10; i++) {
                if (i % 2 == 0) { // Every other section
                    g2d.fillRect(screenX + i * windowWidth, windowY, windowWidth - 2, windowHeight);
                }
            }
            
            // Draw the engine part (front or back depending on direction)
            g2d.setColor(Color.DARK_GRAY);
            if (obstacle.getSpeed() > 0) { // Moving right
                g2d.fillRect(screenX + width - width/5, screenY, width/5, height);
            } else { // Moving left
                g2d.fillRect(screenX, screenY, width/5, height);
            }
            
            // Draw wheels
            g2d.setColor(Color.BLACK);
            int wheelCount = width / 40;
            int wheelSize = height / 3;
            int wheelY = screenY + height - wheelSize;
            
            for (int i = 0; i < wheelCount; i++) {
                g2d.fillOval(screenX + (i * width/wheelCount) + width/(wheelCount*2) - wheelSize/2, 
                           wheelY, wheelSize, wheelSize);
            }
        }
        
        // Debug hitbox if needed (uncomment for debugging)
        // g2d.setColor(Color.GREEN);
        // g2d.drawRect(screenX, screenY, width, height);
    }
    
    /**
     * Updates the scale manager when screen dimensions change.
     * 
     * @param newScaleManager The updated scale manager
     */
    public void updateScaleManager(ScaleManager newScaleManager) {
        this.scaleManager = newScaleManager;
    }
    
    /**
     * Gets the collision rectangle for an obstacle in screen coordinates.
     * Useful for debugging and collision visualization.
     * 
     * @param obstacle The obstacle
     * @param viewportOffset The viewport's Y offset
     * @return Rectangle representing the obstacle's bounds on screen
     */
    public Rectangle getObstacleScreenBounds(MovingObstacles obstacle, int viewportOffset) {
        int width = obstacle.getType() == ObstacleType.CAR ? CAR_WIDTH : TRAIN_WIDTH;
        int height = obstacle.getType() == ObstacleType.CAR ? CAR_HEIGHT : TRAIN_HEIGHT;
        
        int screenX = scaleManager.scaleX(obstacle.getX());
        int screenY = scaleManager.scaleY(obstacle.getY() - viewportOffset);
        int screenWidth = scaleManager.scaleWidth(width);
        int screenHeight = scaleManager.scaleHeight(height);
        
        return new Rectangle(screenX, screenY, screenWidth, screenHeight);
    }
    
    /**
     * Customizes the color for a specific obstacle type.
     * 
     * @param type The obstacle type
     * @param color The new color
     */
    public void setObstacleColor(ObstacleType type, Color color) {
        obstacleColors.put(type, color);
    }
}
