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
     * Gets the list of game objects in the chunk.
     * 
     * @return List of game objects
     */
    List<GameObject> getObjects();
    
    /**
     * Gets the list of cells in the chunk.
     * 
     * @return List of cells
     */
    List<Cell> getCells();
    
    /**
     * Gets the cell at the specified X position.
     * 
     * @param cellX X position of the cell
     * @return Cell at the specified position
     */
    Cell getCellAt(final int cellX);

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
     * Gets the number of cells in a row.
     * 
     * @return Number of cells in a row
     */
    int getCellsPerRow();

}
