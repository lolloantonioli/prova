package it.unibo.model.Map.api;

import java.util.List;

import it.unibo.model.Map.util.ChunkType;

public interface Chunk {

    /**
     * Adds a game object to the chunk.
     * 
     * @param obj Game object to add
     */
    void addObject(GameObject obj);

    /**
     * Gets the list of game objects in the chunk.
     * 
     * @return List of game objects
     */
    List<GameObject> getObjects();

    /**
     * Gets the type of the chunk.
     * 
     * @return Chunk type
     */
    ChunkType getType();

    /**
     * Gets the position of the chunk.
     * 
     * @return Position
     */
    int getPosition();

    /**
     * Gets the height of the chunk.
     * 
     * @return Height
     */
    int getHeight();

    /**
    * Gets the width of the chunk.
    * 
    * @return Width
    */
    int getWidth();

    /**
     * Checks if the chunk is visible within the current viewport.
     * 
     * @param viewPosition Current view position
     * @param viewHeight Height of the viewport
     * @return True if the chunk is visible
     */
    boolean isVisible(int viewPosition, int viewHeight);

}
