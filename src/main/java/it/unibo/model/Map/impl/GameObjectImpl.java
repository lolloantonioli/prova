package it.unibo.model.Map.impl;

import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.util.Dimension;

public class GameObjectImpl implements GameObject {
    
    private final Dimension dimension;
    private int x;
    private int y;
    private boolean movable;
    private int speed;
    private boolean platform;
    
    /**
     * Constructor for the GameObject class.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param width Width
     * @param height Height
     */
    public GameObjectImpl(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.dimension = new Dimension(width, height);
        this.movable = false;
        this.speed = 0;
        this.platform = false;
    }
    
    @Override
    public void update(int mapWidth) {
        if (movable) {
            x += speed;
            
            // Wrap around at map boundaries
            if (speed > 0 && x > mapWidth) {
                x = -dimension.width();
            } else if (speed < 0 && x + dimension.width() < 0) {
                x = mapWidth;
            }
        }
    }
    
    @Override
    public boolean collidesWith(int px, int py) {
        return px >= x && px < x + dimension.width() && py >= y && py < y + dimension.height();
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
    public int getWidth() {
        return dimension.width();
    }
    
    @Override
    public int getHeight() {
        return dimension.height();
    }
    
    @Override
    public boolean isMovable() {
        return movable;
    }
    
    @Override
    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
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
        return platform;
    }
    
    @Override
    public void setPlatform(boolean platform) {
        this.platform = platform;
    }
}
