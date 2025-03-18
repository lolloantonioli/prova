package it.unibo.model.Map.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.util.ChunkType;
import it.unibo.model.Map.util.Dimension;

public class ChunkImpl implements Chunk {

    private final int position;
    private final Dimension dimension;
    private final ChunkType type;
    private final List<GameObject> objects;
    
    // Standard height for chunks
    public static final int STANDARD_HEIGHT = 120;
    
    /**
     * Constructor for the Chunk class.
     * 
     * @param position Y-position of the chunk
     * @param width Width of the chunk
     * @param type Type of the chunk
     */
    public ChunkImpl(final int position, final int width, final ChunkType type) {
        this.position = position;
        this.dimension = new Dimension(width, STANDARD_HEIGHT);
        this.type = type;
        this.objects = new ArrayList<>();
    }
    
    public void addObject(final GameObject obj) {
        objects.add(obj);
    }
    
    public List<GameObject> getObjects() {
        return objects;
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
    
    public boolean isVisible(final int viewPosition, final int viewHeight) {
        // Check if any part of the chunk is within the visible range
        return (position + dimension.height() > viewPosition) && (position < viewPosition + viewHeight);
    }
}
