package it.unibo.model.Map.Obstacles.api;

import java.util.List;

import it.unibo.model.Map.Obstacles.impl.MovingObstacles;

/**
 * Interface for managing moving obstacles in the game.
 * Handles collision detection, updates, and difficulty scaling.
 */
public interface MovingObstacleManager {
    
    /**
     * Adds a moving obstacle to be managed.
     * 
     * @param obstacle The obstacle to add
     */
    void addObstacle(MovingObstacles obstacle);
    
    /**
     * Adds multiple obstacles at once.
     * 
     * @param obstacles Array of obstacles to add
     */
    void addObstacles(MovingObstacles[] obstacles);
    
    /**
     * Removes an obstacle from management.
     * 
     * @param obstacle The obstacle to remove
     */
    void removeObstacle(MovingObstacles obstacle);
    
    /**
     * Updates the state of all managed obstacles.
     * 
     * @param mapWidth Width of the map for wrapping
     */
    void updateAll(int mapWidth);
    
    /**
     * Gets all currently active obstacles.
     * 
     * @return List of active obstacles
     */
    List<MovingObstacles> getActiveObstacles();
    
    /**
     * Gets obstacles of a specific type.
     * 
     * @param type Type of obstacles to get
     * @return List of obstacles of the specified type
     */
    List<MovingObstacles> getObstaclesByType(String type);
    
    /**
     * Checks if a point collides with any managed obstacle.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return True if there is a collision
     */
    boolean checkCollision(int x, int y);
    
    /**
     * Increases the speed of all obstacles by a factor.
     * Used when difficulty increases.
     * 
     * @param factor Speed increase factor
     */
    void increaseSpeed(int factor);
    
    /**
     * Removes obstacles that are outside the visible area.
     * 
     * @param minY Minimum Y coordinate to keep obstacles
     * @param maxY Maximum Y coordinate to keep obstacles
     */
    void cleanupOffscreenObstacles(int minY, int maxY);
    
    /**
     * Gets the total number of managed obstacles.
     * 
     * @return Count of obstacles
     */
    int getObstacleCount();
}
