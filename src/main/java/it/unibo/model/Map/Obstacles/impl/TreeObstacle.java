package it.unibo.model.Map.Obstacles.impl;

import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.util.ObstacleType;

public class TreeObstacle implements Obstacle {
    private final int x, y;
    private final ObstacleType type = ObstacleType.TREE;

    public TreeObstacle(int x, int y) {
        this.x = x;
        this.y = y;
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
        return px >= x && px < x + getWidth() && py == y;
    }
    
    @Override
    public int getWidth() {
        return 40;
    }
    
    @Override
    public int getHeight() {
        return 60;
    }

    @Override
    public void update(int mapWidth) {
        // TREE is static, no movement
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public void setMovable(boolean movable) {
        throw new UnsupportedOperationException("TreeObstacle is static.");
    }

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public void setSpeed(int speed) {
        throw new UnsupportedOperationException("TreeObstacle does not move.");
    }

    @Override
    public boolean isPlatform() {
        return false;
    }

    @Override
    public void setPlatform(boolean platform) {
        throw new UnsupportedOperationException("TreeObstacle cannot be a platform.");
    }
}