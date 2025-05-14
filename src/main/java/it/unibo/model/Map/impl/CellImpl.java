package it.unibo.model.Map.impl;

import java.util.Optional;

import it.unibo.model.Map.api.Cell;
import it.unibo.model.Map.api.GameObject;

public class CellImpl implements Cell {
    
    private Optional<GameObject> content;
    private final int x;  // posizione x nella griglia
    private final int y;  // posizione y nella griglia
    
    /**
     * Costruttore per la cella.
     * 
     * @param x Posizione X nella griglia
     * @param y Posizione Y nella griglia
     */
    public CellImpl(final int x, final int y) {
        this.x = x;
        this.y = y;
        this.content = Optional.empty();
    }
    
    public boolean addObject(final GameObject obj) {
        if (!content.isPresent()) {
            content = Optional.of(obj);
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
    
}