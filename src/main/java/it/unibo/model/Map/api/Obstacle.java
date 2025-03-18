package it.unibo.model.Map.api;

import it.unibo.model.Map.util.ObstacleType;

public interface Obstacle extends GameObject {

    /**
     * Gets the type of obstacle.
     * 
     * @return Obstacle type
     */
    ObstacleType getType();
    
}
