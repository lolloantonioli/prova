package it.unibo.model.Map.impl;

import it.unibo.model.Map.api.Collectible;
import it.unibo.model.Map.util.CollectibleType;

public class CollectibleImpl extends GameObjectImpl implements Collectible {
    
    private final CollectibleType type;
    private boolean collected;
    
    /**
     * Constructor for the Collectible class.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param type Type of collectible
     */
    public CollectibleImpl(final int x, final int y, final CollectibleType type) {
        // Standard size for collectibles
        super(x, y, 20, 20);
        this.type = type;
        this.collected = false;
    }
    
    public CollectibleType getType() {
        return type;
    }
    
    public void collect() {
        this.collected = true;
    }
    
    public boolean isCollected() {
        return collected;
    }
}
