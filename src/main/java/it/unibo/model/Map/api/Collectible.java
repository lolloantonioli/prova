package it.unibo.model.Map.api;

import it.unibo.model.Map.util.CollectibleType;

public interface Collectible extends GameObject {

    /**
     * Gets the type of collectible.
     * 
     * @return Collectible type
     */
    CollectibleType getType();

    /**
     * Marks the collectible as collected.
     */
    void collect();

    /**
     * Checks if the collectible has been collected.
     * 
     * @return True if the collectible has been collected
     */
    boolean isCollected();

}
