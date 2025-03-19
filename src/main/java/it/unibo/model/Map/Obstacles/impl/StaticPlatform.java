package it.unibo.model.Map.Obstacles.impl;

import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.util.ObstacleType;

public class StaticPlatform implements Obstacle {
    private final int x, y;
    private final ObstacleType type;

    public StaticPlatform(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = ObstacleType.TREE; // Usato per rappresentare la ninfea ???????????????????
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
        // Le ninfee sono statiche, quindi non si muovono
    }
    
    @Override
    public boolean collidesWith(int px, int py) {
        return px >= x && px < x + getWidth() && py >= y && py < y + getHeight();
    }
    
    @Override
    public int getWidth() {
        return 50;
    }
    
    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public void setMovable(boolean movable) {
        // Non applicabile perché statico
    }

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public void setSpeed(int speed) {
        // Non applicabile perché statico
    }

    @Override
    public boolean isPlatform() {
        return true;
    }

    @Override
    public void setPlatform(boolean platform) {
        // Sempre una piattaforma, quindi non serve modificarlo
    }
}

