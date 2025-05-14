package it.unibo.model.Map.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unibo.model.Map.api.Cell;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.util.ChunkType;

public class ChunkImpl implements Chunk {

    private final int position;
    private final ChunkType type;
    private final List<Cell> cells;
    private final int cellsPerRow;

    public static final int STANDARD_HEIGHT = 200;
    
    /**
     * Constructor for the Chunk class.
     * 
     * @param position Y-position of the chunk
     * @param cellsPerRow Number of cells in this chunk row
     * @param type Type of the chunk
     */
    public ChunkImpl(final int position, final int cellsPerRow, final ChunkType type) {
        this.position = position;
        this.cellsPerRow = cellsPerRow;
        this.type = type;
        this.cells = IntStream.range(0, cellsPerRow)
            .mapToObj(x -> new CellImpl(x, position))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean addObjectAt(final GameObject obj, final int cellX) {
        return (cellX >= 0 && cellX < cellsPerRow) && cells.get(cellX).addObject(obj);
    }
    
    @Override
    public boolean addObject(final GameObject obj) {
        return cells.stream()
            .filter(cell -> !cell.hasObject())
            .findFirst()
            .map(cell -> cell.addObject(obj))
            .orElse(false);
    }
    
    @Override
    public List<GameObject> getObjects() {
        return cells.stream()
            .filter(Cell::hasObject)
            .map(Cell::getContent)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Cell> getCells() {
        return cells;
    }
    
    @Override
    public Cell getCellAt(final int cellX) {
        return (cellX >= 0 && cellX < cellsPerRow) ? cells.get(cellX) : null;
    }
    
    @Override
    public ChunkType getType() {
        return type;
    }
    
    @Override
    public int getPosition() {
        return position;
    }
    
    @Override
    public int getCellsPerRow() {
        return cellsPerRow;
    }
    
}