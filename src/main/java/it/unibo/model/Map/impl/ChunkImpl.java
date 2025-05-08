package it.unibo.model.Map.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unibo.model.Map.api.Cell;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.util.ChunkType;
import it.unibo.model.Map.util.Dimension;

public class ChunkImpl implements Chunk {

    private final int position;
    private final Dimension dimension;
    private final ChunkType type;
    private final List<Cell> cells;
    private final int cellSize;
    // Numero di celle per riga (chunk)
    private final int cellsPerRow;
    // Standard height for chunks
    public static final int STANDARD_HEIGHT = 120;
    
    /**
     * Constructor for the Chunk class.
     * 
     * @param position Y-position of the chunk
     * @param width Width of the chunk
     * @param type Type of the chunk
     * @param cellSize Dimensione di ogni cella
     */
    public ChunkImpl(final int position, final int width, final ChunkType type, final int cellSize) {
        this.position = position;
        this.dimension = new Dimension(width, STANDARD_HEIGHT);
        this.type = type;
        this.cellSize = cellSize;
        this.cellsPerRow = width / cellSize;
        
        // Inizializziamo la riga di celle
        this.cells = IntStream.range(0, cellsPerRow)
            .mapToObj(x -> new CellImpl(x, position / cellSize, cellSize))
            .collect(Collectors.toList());
    }
    
    public boolean addObjectAt(final GameObject obj, final int cellX) {
        return (cellX >= 0 && cellX < cellsPerRow) && cells.get(cellX).addObject(obj);
    }
    
    public boolean addObject(final GameObject obj) {
        return cells.stream()
            .filter(cell -> !cell.hasObject())
            .findFirst()
            .map(cell -> cell.addObject(obj))
            .orElse(false);
    }

    public boolean isVisible(final int viewPosition, final int viewHeight) {
        // Check if any part of the chunk is within the visible range
        return (position + dimension.height() > viewPosition) && (position < viewPosition + viewHeight);
    }
    
    public List<GameObject> getObjects() {
        return cells.stream()
            .filter(Cell::hasObject)
            .map(Cell::getContent)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    }
    
    public List<Cell> getCells() {
        return cells;
    }
    
    public Cell getCellAt(final int x, final int y) {
        int cellX = x / cellSize;
        return (cellX >= 0 && cellX < cellsPerRow) ? cells.get(cellX) : null;
    }
    
    public ChunkType getType() {
        return type;
    }
    
    public int getPosition() {
        return position;
    }
    
    public int getHeight() {
        return dimension.height();
    }
    
    public int getWidth() {
        return dimension.width();
    }
    
    public int getCellSize() {
        return cellSize;
    }
    
    public int getCellsPerRow() {
        return cellsPerRow;
    }
    
}