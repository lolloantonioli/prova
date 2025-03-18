package it.unibo.model.Map.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.ChunkFactory;
import it.unibo.model.Map.api.GameMap;

public class GameMapImpl implements GameMap {

    private final List<Chunk> chunks;
    private final int viewportHeight;
    private final int viewportWidth;
    private final ChunkFactory chunkFactory;
    private int currentPosition;
    private int scrollSpeed;
    
    // Number of chunks to keep ahead of the current view
    private static final int BUFFER_CHUNKS = 5;
    
    /**
     * Constructor for the Map class.
     * 
     * @param width Width of the viewport
     * @param height Height of the viewport
     * @param speed Initial scrolling speed
     */
    public GameMapImpl(final int width, final int height, final int speed) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.scrollSpeed = speed;
        this.currentPosition = 0;
        this.chunks = new ArrayList<>();
        this.chunkFactory = new ChunkFactoryImpl();
        
        // Initialize the map with starting chunks
        this.initializeMap();
    }
    
    /**
     * Initializes the map with starting chunks.
     */
    private void initializeMap() {
        // Add initial grass chunk (safe starting area)
        chunks.add(chunkFactory.createGrassChunk(0, viewportWidth));
        
        // Add initial chunks to fill the buffer
        for (int i = 1; i <= BUFFER_CHUNKS; i++) {
            generateNewChunk();
        }
    }
    
    public void update() {
        // Increment the current position by the scroll speed
        currentPosition -= scrollSpeed;
        
        // Remove chunks that are no longer visible and far behind
        cleanupChunks();
        
        // Generate new chunks as needed
        ensureBufferChunks();
    }
    
    /**
     * Removes chunks that are no longer visible and far behind.
     */
    private void cleanupChunks() {
        Iterator<Chunk> iterator = chunks.iterator();
        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            // If chunk is far behind current position (out of viewport + margin)
            if (chunk.getPosition() > currentPosition + viewportHeight) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Ensures there are enough buffer chunks ahead of the current view.
     */
    private void ensureBufferChunks() {
        int farthestPosition = getFarthestChunkPosition();
        int targetPosition = currentPosition - (BUFFER_CHUNKS * ChunkImpl.STANDARD_HEIGHT);
        
        while (farthestPosition > targetPosition) {
            generateNewChunk();
            farthestPosition = getFarthestChunkPosition();
        }
    }
    
    /**
     * Gets the position of the farthest chunk.
     * 
     * @return The position of the farthest chunk
     */
    private int getFarthestChunkPosition() {
        int farthest = Integer.MAX_VALUE;
        for (Chunk chunk : chunks) {
            if (chunk.getPosition() < farthest) {
                farthest = chunk.getPosition();
            }
        }
        return farthest == Integer.MAX_VALUE ? 0 : farthest;
    }
    
    public void generateNewChunk() {
        int nextPosition = getFarthestChunkPosition();
        if (nextPosition == Integer.MAX_VALUE) {
            nextPosition = 0; // First chunk after initial grass
        } else {
            nextPosition -= ChunkImpl.STANDARD_HEIGHT;
        }
        
        Chunk newChunk = chunkFactory.createRandomChunk(nextPosition, viewportWidth);
        chunks.add(newChunk);
    }
    
    public List<Chunk> getVisibleChunks() {
        List<Chunk> visibleChunks = new ArrayList<>();
        for (Chunk chunk : chunks) {
            if (chunk.isVisible(currentPosition, viewportHeight)) {
                visibleChunks.add(chunk);
            }
        }
        return visibleChunks;
    }
    
    public int getCurrentPosition() {
        return currentPosition;
    }
    
    public void increaseScrollSpeed() {
        scrollSpeed += 1;
        // Cap the speed at a reasonable maximum
        if (scrollSpeed > 10) {
            scrollSpeed = 10;
        }
    }
    
    public boolean isPositionOutOfBounds(final int x, final int y) {
        return x < 0 || x >= viewportWidth || y < currentPosition || y >= currentPosition + viewportHeight;
    }
    
    public List<Chunk> getAllChunks() {
        return chunks;
    }
    
    public int getViewportWidth() {
        return viewportWidth;
    }
    
    public int getViewportHeight() {
        return viewportHeight;
    }

}
