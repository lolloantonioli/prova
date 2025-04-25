package it.unibo.model.Map.impl;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.ObjectPlacer;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.api.Cell;

import java.util.List;
import java.util.Random;

public class ObjectPlacerImpl implements ObjectPlacer {

    private final Random random;
    
    public ObjectPlacerImpl() {
        this.random = new Random();
    }
    
    public boolean placeObjectRandomly(Chunk chunk, GameObject object) {
        // Ottieni tutte le celle del chunk
        var cells = chunk.getCells();
        
        // Filtra le celle vuote
        var emptyCells = cells.stream()
            .filter(cell -> !cell.hasObject())
            .toList();
        
        if (emptyCells.isEmpty()) {
            return false;
        }
        
        // Scegli una cella casuale tra quelle vuote
        Cell randomCell = emptyCells.get(random.nextInt(emptyCells.size()));
        
        // Calcola una posizione casuale all'interno della cella
        int cellX = randomCell.getX();
        int cellY = randomCell.getY();
        int cellSize = randomCell.getSize();
        
        // Genera offset casuale se l'oggetto è più piccolo della cella
        int offsetX = (object.getWidth() < cellSize) ? random.nextInt(cellSize - object.getWidth()) : 0;
        int offsetY = (object.getHeight() < cellSize) ? random.nextInt(cellSize - object.getHeight()) : 0;
        
        // Posiziona l'oggetto con l'offset calcolato
        object.setPosition(
            cellX * cellSize + offsetX, 
            cellY * cellSize + offsetY
        );
        
        // Aggiungi l'oggetto alla cella
        return randomCell.addObject(object);
    }
    
    public void placeObjectsRandomly(Chunk chunk, List<? extends GameObject> objects, int maxObjects) {
        int objectsPlaced = 0;
        
        for (GameObject object : objects) {
            if (objectsPlaced >= maxObjects) {
                break;
            }
            
            if (placeObjectRandomly(chunk, object)) {
                objectsPlaced++;
            }
        }
    }
}