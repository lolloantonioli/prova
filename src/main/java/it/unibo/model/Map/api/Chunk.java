package it.unibo.model.Map.api;

import java.util.List;

import it.unibo.model.Map.util.ChunkType;

public interface Chunk {
    
    /**
     * Aggiunge un oggetto a una cella specifica.
     * 
     * @param obj Oggetto da aggiungere
     * @param cellX Posizione X della cella (indice)
     * @return true se l'oggetto è stato aggiunto, false altrimenti
     */
    boolean addObjectAt(final GameObject obj, final int cellX);

    /**
     * Aggiunge un oggetto alla prima cella libera.
     * 
     * @param obj Oggetto da aggiungere
     * @return true se l'oggetto è stato aggiunto, false altrimenti
     */
    boolean addObject(final GameObject obj);
    
    /**
     * Checks if the chunk is visible within the current viewport.
     * 
     * @param viewPosition Current view position
     * @param viewHeight Height of the viewport
     * @return True if the chunk is visible
     */
    boolean isVisible(final int viewPosition, final int viewHeight);

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

    List<Cell> getCells();

    Cell getCellAt(final int x, final int y);

    int getCellSize();

    int getCellsPerRow();

}
