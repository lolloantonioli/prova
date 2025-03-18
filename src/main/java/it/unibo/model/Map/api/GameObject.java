package it.unibo.model.Map.api;

public interface GameObject {

    /**
     * Updates the game object's state.
     * 
     * @param mapWidth Width of the map to handle wrapping
     */
    void update(int mapWidth);

    /**
     * Checks if this game object collides with a point.
     * 
     * @param px X-coordinate of the point
     * @param py Y-coordinate of the point
     * @return True if there is a collision
     */
    boolean collidesWith(int px, int py);

    /**
     * Gets the X-coordinate.
     * 
     * @return X-coordinate
     */
    int getX();

    /**
     * Gets the Y-coordinate.
     * 
     * @return Y-coordinate
     */
    int getY();

    /**
     * Gets the width.
     * 
     * @return Width
     */
    int getWidth();

    /**
     * Gets the height.
     * 
     * @return Height
     */
    int getHeight();

    /**
     * Checks if the object is movable.
     * 
     * @return True if the object is movable
     */
    boolean isMovable();

    /**
     * Sets whether the object is movable.
     * 
     * @param movable True if the object should be movable
     */
    void setMovable(boolean movable);

    /**
     * Gets the speed of the object.
     * 
     * @return Speed
     */
    int getSpeed();

    /**
     * Sets the speed of the object.
     * 
     * @param speed New speed
     */
    void setSpeed(int speed);

    /**
     * Checks if the object is a platform the player can stand on.
     * 
     * @return True if the object is a platform
     */
    boolean isPlatform();

    /**
     * Sets whether the object is a platform.
     * 
     * @param platform True if the object should be a platform
     */
    void setPlatform(boolean platform);

}
