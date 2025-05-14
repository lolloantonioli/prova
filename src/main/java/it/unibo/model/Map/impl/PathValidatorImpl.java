package it.unibo.model.Map.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.unibo.model.Map.api.Cell;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.api.PathValidator;
import it.unibo.model.Map.util.ChunkType;

/**
 * Implementazione semplificata di PathValidator che garantisce 
 * la percorribilità tra chunk consecutivi di tipo GRASS.
 */
public class PathValidatorImpl implements PathValidator {

    private Chunk lastGrassChunk = null;
    
    @Override
    public void ensureTraversability(final Chunk chunk) {
        // Applica la validazione solo ai chunk di tipo GRASS
        if (chunk.getType() == ChunkType.GRASS) {
            // Se c'è un chunk di erba precedente, assicura un percorso tra i due
            if (lastGrassChunk != null) {
                ensurePathBetweenChunks(lastGrassChunk, chunk);
            }
            
            // Aggiorna il riferimento all'ultimo chunk di erba
            lastGrassChunk = chunk;
        }
    }
    
    /**
     * Garantisce che ci sia un percorso percorribile tra due chunk consecutivi di erba.
     * 
     * @param previousChunk Il chunk di erba precedente
     * @param currentChunk Il chunk di erba corrente
     */
    private void ensurePathBetweenChunks(final Chunk previousChunk, final Chunk currentChunk) {
        int width = Math.min(previousChunk.getCellsPerRow(), currentChunk.getCellsPerRow());

        List<Boolean> previousBlocked = new ArrayList<>(width);
        List<Boolean> currentBlocked = new ArrayList<>(width);

        // Inizializza liste con false
        for (int i = 0; i < width; i++) {
            previousBlocked.add(false);
            currentBlocked.add(false);
        }

        // Blocchi nel chunk precedente
        for (int x = 0; x < width; x++) {
            Cell cell = previousChunk.getCellAt(x);
            if (cell != null && cell.hasObject()) {
                Optional<GameObject> obj = cell.getContent();
                if (obj.isPresent() && !obj.get().isPlatform()) {
                    previousBlocked.set(x, true);
                }
            }
        }

        // Blocchi nel chunk corrente
        for (int x = 0; x < width; x++) {
            Cell cell = currentChunk.getCellAt(x);
            if (cell != null && cell.hasObject()) {
                Optional<GameObject> obj = cell.getContent();
                if (obj.isPresent() && !obj.get().isPlatform()) {
                    currentBlocked.set(x, true);
                }
            }
        }

        // Verifica presenza di colonna libera verticale
        boolean pathExists = false;
        for (int x = 0; x < width; x++) {
            if (!previousBlocked.get(x) && !currentBlocked.get(x)) {
                pathExists = true;
                break;
            }
        }

        // Se non esiste, crea un percorso
        if (!pathExists) {
            createVerticalPath(previousBlocked, currentChunk, currentBlocked);
        }
    }

    
    /**
     * Crea un percorso nel chunk corrente rimuovendo strategicamente un ostacolo.
     * 
     * @param chunk Il chunk dove creare il percorso
     * @param blockedColumns Array che rappresenta le colonne bloccate
     */
    private void createVerticalPath(final List<Boolean> previousBlocked, final Chunk currentChunk, final List<Boolean> currentBlocked) {
        int width = currentBlocked.size();

        // Cerca una colonna libera sopra dove sbloccare anche sotto
        for (int x = 0; x < width; x++) {
            if (!previousBlocked.get(x)) {
                Cell cell = currentChunk.getCellAt(x);
                if (cell != null && cell.hasObject()) {
                    cell.removeObject();
                    return;
                }
            }
        }

        // Se tutte le colonne sopra sono bloccate, sblocca una colonna a caso
        for (int x = 0; x < width; x++) {
            if (currentBlocked.get(x)) {
                Cell cell = currentChunk.getCellAt(x);
                if (cell != null && cell.hasObject()) {
                    cell.removeObject();
                    return;
                }
            }
        }
    }

}