package it.unibo.model.Map.api;

import java.util.List;

import it.unibo.model.Map.Obstacles.api.MovingObstacleManager;

public interface GameMap {

    /**
     * Updates the map's state, scrolling and generating new chunks as needed.
     */
    void update();

    /**
     * Generates a new chunk and adds it to the map.
     */
    void generateNewChunk();

    boolean checkPlayerCollision(final int playerX, final int playerY);

    /**
     * Gets the list of currently visible chunks.
     * 
     * @return List of visible chunks
     */
    List<Chunk> getVisibleChunks();

    /**
     * Gets the current position of the map view.
     * 
     * @return Current position
     */
    int getCurrentPosition();

    /**
     * Increases the scroll speed of the map.
     */
    void increaseScrollSpeed();

    MovingObstacleManager getObstacleManager();

    /**
     * Checks if a position is out of bounds of the map.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return True if the position is out of bounds
     */
    boolean isPositionOutOfBounds(final int x, final int y);

    /**
     * Gets the total list of chunks.
     * 
     * @return List of all chunks
     */
    List<Chunk> getAllChunks();

    /**
     * Gets the viewport width.
     * 
     * @return Viewport width
     */
    int getMapWidth();

    /**
     * Gets the viewport height.
     * 
     * @return Viewport height
     */
    int getMapHeight();

}
