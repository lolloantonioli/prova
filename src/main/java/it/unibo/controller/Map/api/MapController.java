package it.unibo.controller.Map.api;

import it.unibo.model.Map.api.Collectible;
import it.unibo.model.Map.util.CollectibleType;

public interface MapController {

    /**
     * Updates the map model and view.
     * This should be called in the game's main loop.
     */
    void update();

    /**
     * Checks if a point collides with any obstacles in the map.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return True if there is a collision
     */
    boolean isCollision(int x, int y);

    /**
     * Attempts to collect a collectible at a given position.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return The type of collectible if collected, null otherwise
     */
    CollectibleType tryCollectItem(int x, int y);

    /**
     * Places a collectible item on the map.
     * 
     * @param item Collectible to place
     */
    void placeCollectible(Collectible item);

    /**
     * Checks if a position is out of bounds.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return True if the position is out of bounds
     */
    boolean isPositionOutOfBounds(int x, int y);

    /**
     * Gets the current map position.
     * 
     * @return Current position
     */
    int getCurrentPosition();

}
