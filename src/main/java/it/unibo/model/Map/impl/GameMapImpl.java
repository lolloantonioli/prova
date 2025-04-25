package it.unibo.model.Map.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.ChunkFactory;
import it.unibo.model.Map.api.GameMap;

public class GameMapImpl implements GameMap {

    private final List<Chunk> chunks;
    private int viewportHeight;
    private int viewportWidth;
    private ChunkFactory chunkFactory;
    private int currentPosition;
    private int scrollSpeed;
    
    // Number of chunks to keep ahead of the current view
    private static final int BUFFER_CHUNKS = 5;
    
    /**
     * Constructor for the GameMap.
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
        this.chunkFactory = new ChunkFactoryImpl(viewportWidth / 8); // esempio 8 celle
        
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
        chunks.removeIf(chunk -> chunk.getPosition() > currentPosition + viewportHeight);
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
        return chunks.stream()
            .mapToInt(Chunk::getPosition)
            .min()
            .orElse(0);
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
        return chunks.stream()
            .filter(chunk -> chunk.isVisible(currentPosition, viewportHeight))
            .collect(Collectors.toList());
    }
    
    public int getCurrentPosition() {
        return currentPosition;
    }
    
    public void increaseScrollSpeed() {
        scrollSpeed = Math.min(scrollSpeed + 1, 10);
    }

    public void updateViewportDimensions(final int newWidth, final int newHeight) {
        this.viewportWidth = newWidth;
        this.viewportHeight = newHeight;
        
        // Ricrea il chunkFactory con nuova dimensione celle
        int cellSize = newWidth / 100;
        this.chunkFactory = new ChunkFactoryImpl(cellSize);
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
