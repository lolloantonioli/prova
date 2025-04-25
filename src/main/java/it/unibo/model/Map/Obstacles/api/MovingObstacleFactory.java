package it.unibo.model.Map.Obstacles.api;

import it.unibo.model.Map.Obstacles.impl.MovingObstacles;

/**
 * Factory interface for creating moving obstacles.
 */
public interface MovingObstacleFactory {

    /**
     * Creates a car obstacle moving at a specified speed.
     *
     * @param x Starting X-coordinate
     * @param y Y-coordinate
     * @param speed Movement speed (positive for right, negative for left)
     * @return A new car obstacle
     */
    MovingObstacles createCar(int x, int y, int speed);
    
    /**
     * Creates a train obstacle moving at a specified speed.
     *
     * @param x Starting X-coordinate
     * @param y Y-coordinate
     * @param speed Movement speed (positive for right, negative for left)
     * @return A new train obstacle
     */
    MovingObstacles createTrain(int x, int y, int speed);
    
    /**
     * Creates a random car with random speed within safe limits.
     *
     * @param y Y-coordinate
     * @param mapWidth Width of the map for positioning
     * @param leftToRight Direction of movement (true for left to right)
     * @return A randomly configured car
     */
    MovingObstacles createRandomCar(int y, int mapWidth, boolean leftToRight);
    
    /**
     * Creates a random train with random speed within safe limits.
     *
     * @param y Y-coordinate
     * @param mapWidth Width of the map for positioning
     * @param leftToRight Direction of movement (true for left to right)
     * @return A randomly configured train
     */
    MovingObstacles createRandomTrain(int y, int mapWidth, boolean leftToRight);
    
    /**
     * Creates a set of cars evenly distributed across a road.
     *
     * @param y Y-coordinate of the road
     * @param mapWidth Width of the map
     * @param count Number of cars to create
     * @param minDistance Minimum distance between cars
     * @param leftToRight Direction of movement
     * @return Array of car obstacles
     */
    MovingObstacles[] createCarSet(int y, int mapWidth, int count, int minDistance, boolean leftToRight);
    
    /**
     * Creates a set of trains for a railway chunk.
     *
     * @param y Y-coordinate of the railway
     * @param mapWidth Width of the map
     * @param count Number of trains to create
     * @param minDistance Minimum distance between trains
     * @param leftToRight Direction of movement
     * @return Array of train obstacles
     */
    MovingObstacles[] createTrainSet(int y, int mapWidth, int count, int minDistance, boolean leftToRight);
}