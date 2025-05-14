package it.unibo.model.Map.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unibo.model.Map.Obstacles.api.MovingObstacleManager;
import it.unibo.model.Map.Obstacles.impl.MovingObstacleManagerImpl;
import it.unibo.model.Map.Obstacles.impl.MovingObstacles;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.ChunkFactory;
import it.unibo.model.Map.api.GameMap;
import it.unibo.model.Map.api.GameObject;

public class GameMapImpl implements GameMap {

    private final List<Chunk> chunks;
    private ChunkFactory chunkFactory;
    private int currentPosition;
    private int scrollSpeed;
    private final MovingObstacleManager obstacleManager;
    private final int mapWidth; // Larghezza logica della mappa (in celle)
    private final int mapHeight; // Altezza logica della vista (in celle)
    
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
        this.mapWidth = width;
        this.mapHeight = height;
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
        chunks.add(chunkFactory.createGrassChunk(0, mapWidth));
        
        // Add initial chunks to fill the buffer
        for (int i = 1; i <= BUFFER_CHUNKS; i++) {
            generateNewChunk();
        }
    }
    
    @Override
    public void update() {
        // Aggiorna la posizione corrente
        currentPosition += scrollSpeed;
        
        // Aggiorna tutti gli ostacoli in movimento
        obstacleManager.updateAll(mapWidth);
         
        // Pulizia ostacoli fuori dallo schermo
        obstacleManager.cleanupOffscreenObstacles(
            currentPosition - 2, // Un po' sotto la vista corrente
            currentPosition + mapHeight + 2 // Un po' sopra la vista corrente
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
        chunks.removeIf(chunk -> chunk.getPosition() < currentPosition - mapHeight);
    }
    
    /**
     * Ensures there are enough buffer chunks ahead of the current view.
     */
    private void ensureBufferChunks() {
        int farthestPosition = getFarthestChunkPosition();
        int targetPosition = currentPosition + (BUFFER_CHUNKS * ChunkImpl.STANDARD_HEIGHT);
        
        while (farthestPosition < targetPosition) {
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
            .max()
            .orElse(0);
    }
    
    @Override
    public void generateNewChunk() {
        int nextPosition = getFarthestChunkPosition();
        if (nextPosition == Integer.MIN_VALUE) {
            nextPosition = 0; // First chunk after initial grass
        } else {
            nextPosition += ChunkImpl.STANDARD_HEIGHT;
        }
        
        Chunk newChunk = chunkFactory.createRandomChunk(nextPosition, mapWidth);
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
        final int viewStart = currentPosition;
        final int viewEnd = currentPosition + mapHeight;
        
        return chunks.stream()
            .filter(chunk -> {
                int chunkPos = chunk.getPosition();
                return chunkPos + ChunkImpl.STANDARD_HEIGHT > viewStart && chunkPos < viewEnd;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public int getCurrentPosition() {
        return currentPosition;
    }
    
    @Override
    public void increaseScrollSpeed() {
        scrollSpeed = Math.min(scrollSpeed + 1, 10);
        // Aumenta anche la velocità degli ostacoli
        obstacleManager.increaseSpeed(1);
    }

    // Getters per l'obstacle manager
    public MovingObstacleManager getObstacleManager() {
        return obstacleManager;
    }
    
    @Override
    public boolean isPositionOutOfBounds(final int x, final int y) {
        return x < 0 || x >= mapWidth || y < currentPosition || y >= currentPosition + mapHeight;
    }
    
    @Override
    public List<Chunk> getAllChunks() {
        return chunks;
    }
    
    @Override
    public int getMapWidth() {
        return mapWidth;
    }
    
    @Override
    public int getMapHeight() {
        return mapHeight;
    }

}
