package it.unibo.model.Map.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.api.PathValidator;

public class PathValidatorImpl implements PathValidator {

    private final int minFreePathWidth;

    public PathValidatorImpl(final int minFreePathWidth) {
        this.minFreePathWidth = minFreePathWidth;
    }

    @Override
    public void ensureTraversability(final Chunk chunk, final int width) {
        List<GameObject> obstacles = chunk.getObjects().stream()
            .filter(obj -> obj instanceof Obstacle)
            .toList();
        
        // Se non ci sono ostacoli, non serve fare nulla
        if (obstacles.isEmpty()) {
            return;
        }
        
        // Trova gli ostacoli fissi (non mobili)
        List<GameObject> fixedObstacles = obstacles.stream()
            .filter(obj -> !obj.isMovable())
            .toList();
        
        // Se non ci sono ostacoli fissi, non serve fare nulla
        if (fixedObstacles.isEmpty()) {
            return;
        }
        
        // Verifica se esiste un percorso libero
        if (!hasFreePath(fixedObstacles, width)) {
            // Rimuovi alcuni ostacoli per creare un percorso
            removeObstacles(chunk, fixedObstacles, width);
        }
    }

    private boolean hasFreePath(final List<GameObject> obstacles, final int width) {
        // Ordina gli ostacoli in base alla loro posizione X
        List<GameObject> sortedObstacles = new ArrayList<>(obstacles);
        sortedObstacles.sort((a, b) -> Integer.compare(a.getX(), b.getX()));
        
        // Controlla se c'è uno spazio libero sufficiente
        int lastObstacleEnd = 0;
        for (GameObject obstacle : sortedObstacles) {
            // Spazio libero tra ostacoli
            int freeSpace = obstacle.getX() - lastObstacleEnd;
            if (freeSpace >= minFreePathWidth) {
                return true;
            }
            lastObstacleEnd = obstacle.getX() + obstacle.getWidth();
        }
        
        // Controlla lo spazio dopo l'ultimo ostacolo
        return (width - lastObstacleEnd) >= minFreePathWidth;
    }
    
    private void removeObstacles(final Chunk chunk, final List<GameObject> obstacles, final int width) {
        // Ordina gli ostacoli in base alla loro posizione X
        List<GameObject> sortedObstacles = new ArrayList<>(obstacles);
        sortedObstacles.sort((a, b) -> Integer.compare(a.getX(), b.getX()));
        
        // Rimuovi gli ostacoli più piccoli fino a creare un percorso libero
        while (!hasFreePath(sortedObstacles, width) && !sortedObstacles.isEmpty()) {
            // Trova l'ostacolo più piccolo
            GameObject smallestObstacle = sortedObstacles.stream()
                .min((a, b) -> Integer.compare(a.getWidth() * a.getHeight(), b.getWidth() * b.getHeight()))
                .orElse(null);
            
            if (smallestObstacle != null) {
                chunk.getObjects().remove(smallestObstacle);
                sortedObstacles.remove(smallestObstacle);
            }
        }
    }

}
