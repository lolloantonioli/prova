package it.unibo.model.Map.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unibo.model.Map.Obstacles.api.MovingObstacleManager;
import it.unibo.model.Map.Obstacles.impl.MovingObstacleManagerImpl;
import it.unibo.model.Map.Obstacles.impl.MovingObstacles;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.ChunkFactory;
import it.unibo.model.Map.api.GameMap;
import it.unibo.model.Map.api.GameObject;

public class GameMapImpl implements GameMap {

    private final List<Chunk> chunks;
    private final int viewportHeight;
    private final int viewportWidth;
    private final ChunkFactory chunkFactory;
    private int currentPosition;
    private int scrollSpeed;
    private final MovingObstacleManager obstacleManager;
    
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
        this.obstacleManager = new MovingObstacleManagerImpl();
        
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
    
    @Override
    public void update() {
        // Aggiorna la posizione corrente
        currentPosition -= scrollSpeed;
        
        // Aggiorna tutti gli ostacoli in movimento
        obstacleManager.updateAll(viewportWidth);
         
        // Pulizia ostacoli fuori dallo schermo
        obstacleManager.cleanupOffscreenObstacles(
            currentPosition - 200, // Un po' sotto la vista corrente
            currentPosition + viewportHeight + 200 // Un po' sopra la vista corrente
        );
         
        // Rimuovi chunk non più visibili
        cleanupChunks();
         
        // Genera nuovi chunk se necessario
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
    
    @Override
    public void generateNewChunk() {
        int nextPosition = getFarthestChunkPosition();
        if (nextPosition == Integer.MAX_VALUE) {
            nextPosition = 0; // First chunk after initial grass
        } else {
            nextPosition -= ChunkImpl.STANDARD_HEIGHT;
        }
        
        Chunk newChunk = chunkFactory.createRandomChunk(nextPosition, viewportWidth);
        chunks.add(newChunk);
        
        // Aggiungi gli ostacoli mobili del nuovo chunk al manager
        for (GameObject obj : newChunk.getObjects()) {
            if (obj instanceof MovingObstacles) {
                obstacleManager.addObstacle((MovingObstacles) obj);
            }
        }
    }

    // Metodo per verificare le collisioni con il player
    public boolean checkPlayerCollision(int playerX, int playerY) {
        return obstacleManager.checkCollision(playerX, playerY);
    }
    
    @Override
    public List<Chunk> getVisibleChunks() {
        List<Chunk> visibleChunks = new ArrayList<>();
        for (Chunk chunk : chunks) {
            if (chunk.isVisible(currentPosition, viewportHeight)) {
                visibleChunks.add(chunk);
            }
        }
        return visibleChunks;
    }
    
    @Override
    public int getCurrentPosition() {
        return currentPosition;
    }
    
    @Override
    public void increaseScrollSpeed() {
        scrollSpeed += 1;
        // Cap su velocità massima
        if (scrollSpeed > 10) {
            scrollSpeed = 10;
        }
        
        // Aumenta anche la velocità degli ostacoli
        obstacleManager.increaseSpeed(1);
    }

    // Getters per l'obstacle manager
    public MovingObstacleManager getObstacleManager() {
        return obstacleManager;
    }
    
    @Override
    public boolean isPositionOutOfBounds(final int x, final int y) {
        return x < 0 || x >= viewportWidth || y < currentPosition || y >= currentPosition + viewportHeight;
    }
    
    @Override
    public List<Chunk> getAllChunks() {
        return chunks;
    }
    
    @Override
    public int getViewportWidth() {
        return viewportWidth;
    }
    
    @Override
    public int getViewportHeight() {
        return viewportHeight;
    }

}
