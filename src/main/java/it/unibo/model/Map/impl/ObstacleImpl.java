package it.unibo.model.Map.impl;

import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.util.ObstacleType;

public class ObstacleImpl extends GameObjectImpl implements Obstacle {
    
    private final ObstacleType type;
    
    /**
     * Constructor for the Obstacle class.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param width Width
     * @param height Height
     * @param type Type of obstacle
     * @param movable Whether the obstacle can move
     */
    public ObstacleImpl(int x, int y, int width, int height, ObstacleType type, boolean movable) {
        super(x, y, width, height);
        this.type = type;
        setMovable(movable);
    }
    
    public ObstacleType getType() {
        return this.type;
    }
}
