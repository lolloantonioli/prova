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
    
    public boolean collidesWith(int px, int py) {
        return px >= x && px < x + dimension.width() && py >= y && py < y + dimension.height();
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return dimension.width();
    }
    
    public int getHeight() {
        return dimension.height();
    }
    
    public boolean isMovable() {
        return movable;
    }
    
    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public boolean isPlatform() {
        return platform;
    }
    
    public void setPlatform(boolean platform) {
        this.platform = platform;
    }
}
