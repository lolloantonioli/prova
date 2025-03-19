package it.unibo.model.Map.Obstacles.impl;

import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.util.ObstacleType;

public class MovingObstacles implements Obstacle{
    private int x;
    private final int y;
    private final ObstacleType type;
    private int speed;
    private boolean movable;

    public MovingObstacles(int x, int y, ObstacleType type, int speed) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.speed = speed;
        this.movable = true;
    }
    
    @Override
    public int getX() {
        return x;
    }
    
    @Override
    public int getY() {
        return y;
    }
    
    @Override
    public ObstacleType getType() {
        return type;
    }

    @Override
    public void update(int mapWidth) {
        if (movable) {
            x += speed;
            if (x > mapWidth) {
                x = -getWidth(); // Ricompare dall'altro lato
            }
        }
    }

    @Override
    public boolean collidesWith(int px, int py) {
        return px >= x && px < x + getWidth() && py >= y && py < y + getHeight();
    }
    
    @Override
    public int getWidth() {
        return type == ObstacleType.TRAIN ? 200 : 50;
    }
    
    @Override
    public int getHeight() {
        return 40;
    }

    @Override
    public boolean isMovable() {
        return movable;
    }

    @Override
    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public boolean isPlatform() {
        return false;
    }

    @Override
    public void setPlatform(boolean platform) {
        // non applicabile per CAR e TRAIN
    }

}
