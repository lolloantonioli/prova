package it.unibo.model.Map.impl;

import java.util.Optional;

import it.unibo.model.Map.api.Cell;
import it.unibo.model.Map.api.GameObject;

public class CellImpl implements Cell {
    
    private Optional<GameObject> content;
    private final int x;  // posizione x nella griglia
    private final int y;  // posizione y nella griglia
    private final int size; // dimensione del quadrato
    
    /**
     * Costruttore per la cella.
     * 
     * @param x Posizione X nella griglia
     * @param y Posizione Y nella griglia
     * @param size Dimensione del quadrato
     */
    public CellImpl(final int x, final int y, final int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.content = Optional.empty();
    }
    
    public boolean addObject(final GameObject obj) {
        if (!content.isPresent()) {
            content = Optional.of(obj);
            // Aggiorniamo la posizione dell'oggetto per allinearla alla cella
            obj.setPosition(x * size, y * size);
            return true;
        }
        return false;
    }
    
    public void removeObject() {
        content = Optional.empty();
    }
    
    public boolean hasObject() {
        return content.isPresent();
    }
    
    public Optional<GameObject> getContent() {
        return content;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getScreenX() {
        return x * size;
    }
    
    public int getScreenY() {
        return y * size;
    }
}