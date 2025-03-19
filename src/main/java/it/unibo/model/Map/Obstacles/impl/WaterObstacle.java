package it.unibo.model.Map.Obstacles.impl;

import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.util.ObstacleType;

public class WaterObstacle implements Obstacle {
    private final int x, y;
    private final ObstacleType type = ObstacleType.WATER;
    private boolean hasPlatform;

    public WaterObstacle(int x, int y, boolean hasPlatform) {
        this.x = x;
        this.y = y;
        this.hasPlatform = hasPlatform;
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
    public boolean collidesWith(int px, int py) {
        return !hasPlatform && px >= x && px < x + getWidth() && py == y;
    }
    
    @Override
    public int getWidth() {
        return 150;
    }
    
    @Override
    public int getHeight() {
        return 30;
    }

    @Override
    public void update(int mapWidth) {
        // WATER itself does not move, platforms might
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public void setMovable(boolean movable) {
        // Do nothing, water is static
    }

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public void setSpeed(int speed) {
        // Do nothing, water does not move
    }

    @Override
    public boolean isPlatform() {
        return hasPlatform;
    }

    @Override
    public void setPlatform(boolean platform) {
        this.hasPlatform = platform;
    }
}

