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

    @Override
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

        // Aggiungi l'oggetto alla cella (presumendo che venga centrato da chi la gestisce)
        return randomCell.addObject(object);
    }

    @Override
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
