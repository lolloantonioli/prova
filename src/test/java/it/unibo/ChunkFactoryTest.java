package it.unibo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.impl.ChunkFactoryImpl;
import it.unibo.model.Map.impl.ChunkImpl;
import it.unibo.model.Map.util.ChunkType;
import it.unibo.model.Map.util.ObstacleType;

class ChunkFactoryImplTest {
    
    private static final int TEST_POSITION = 100;
    private static final int TEST_WIDTH = 800;
    
    private ChunkFactoryImpl chunkFactory;
    
    @BeforeEach
    void setUp() {
        chunkFactory = new ChunkFactoryImpl();
    }
    
    @Test
    void testCreateRandomChunk() {
        Chunk chunk = chunkFactory.createRandomChunk(TEST_POSITION, TEST_WIDTH);
        
        // Verify basic properties
        assertNotNull(chunk, "Chunk should not be null");
        assertEquals(TEST_POSITION, chunk.getPosition(), "Chunk should have the correct position");
        assertEquals(TEST_WIDTH, chunk.getWidth(), "Chunk should have the correct width");
        assertEquals(ChunkImpl.STANDARD_HEIGHT, chunk.getHeight(), "Chunk should have the standard height");
    }
    
    @Test
    void testCreateRoadChunk() {
        Chunk chunk = chunkFactory.createRoadChunk(TEST_POSITION, TEST_WIDTH);
        
        // Verify chunk properties
        assertEquals(ChunkType.ROAD, chunk.getType(), "Chunk should be of ROAD type");
        assertEquals(TEST_POSITION, chunk.getPosition(), "Road chunk should have correct position");
        assertEquals(TEST_WIDTH, chunk.getWidth(), "Road chunk should have correct width");
        
        // Road chunks should not have obstacles by default
        boolean hasObstacle = false;
        for (GameObject obj : chunk.getObjects()) {
            if (obj instanceof Obstacle) {
                hasObstacle = true;
                break;
            }
        }
        assertFalse(hasObstacle, "Road chunk should not have obstacles by default");
    }
    
    @Test
    void testCreateRiverChunk() {
        Chunk chunk = chunkFactory.createRiverChunk(TEST_POSITION, TEST_WIDTH);
        
        // Verify chunk properties
        assertEquals(ChunkType.RIVER, chunk.getType(), "Chunk should be of RIVER type");
        
        // River chunks should have water as an obstacle
        boolean hasWater = false;
        boolean hasLogs = false;
        
        for (GameObject obj : chunk.getObjects()) {
            if (obj instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) obj;
                if (obstacle.getType() == ObstacleType.WATER) {
                    hasWater = true;
                }
            } else if (obj.isPlatform()) {
                // Check for log platforms
                hasLogs = true;
            }
        }
        
        assertTrue(hasWater, "River chunk should have water obstacle");
        assertTrue(hasLogs, "River chunk should have log platforms");
    }
    
    @Test
    void testCreateGrassChunk() {
        Chunk chunk = chunkFactory.createGrassChunk(TEST_POSITION, TEST_WIDTH);
        
        // Verify chunk properties
        assertEquals(ChunkType.GRASS, chunk.getType(), "Chunk should be of GRASS type");
        
        // Check for possible trees (though they're random, so might not always be present)
        boolean hasTree = false;
        for (GameObject obj : chunk.getObjects()) {
            if (obj instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) obj;
                if (obstacle.getType() == ObstacleType.TREE) {
                    hasTree = true;
                    break;
                }
            }
        }
        
        // We don't assert hasTree is true since it's random and might be false
        // but we can verify other properties
        assertEquals(TEST_POSITION, chunk.getPosition(), "Grass chunk should have correct position");
        assertEquals(TEST_WIDTH, chunk.getWidth(), "Grass chunk should have correct width");
    }
    
    @Test
    void testCreateRailwayChunk() {
        Chunk chunk = chunkFactory.createRailwayChunk(TEST_POSITION, TEST_WIDTH);
        
        // Verify chunk properties
        assertEquals(ChunkType.RAILWAY, chunk.getType(), "Chunk should be of RAILWAY type");
        assertEquals(TEST_POSITION, chunk.getPosition(), "Railway chunk should have correct position");
        assertEquals(TEST_WIDTH, chunk.getWidth(), "Railway chunk should have correct width");
    }
    
    @Test
    void testChunksHaveTraversablePath() {
        // Create multiple chunks and verify they all have a traversable path
        for (int i = 0; i < 10; i++) {
            Chunk roadChunk = chunkFactory.createRoadChunk(TEST_POSITION, TEST_WIDTH);
            Chunk grassChunk = chunkFactory.createGrassChunk(TEST_POSITION, TEST_WIDTH);
            Chunk riverChunk = chunkFactory.createRiverChunk(TEST_POSITION, TEST_WIDTH);
            Chunk railwayChunk = chunkFactory.createRailwayChunk(TEST_POSITION, TEST_WIDTH);
            
            // Testing exact traversability is complex, but we can check that:
            // 1. River chunks must have platforms
            boolean riverHasPlatforms = riverChunk.getObjects().stream()
                .anyMatch(GameObject::isPlatform);
            
            assertTrue(riverHasPlatforms, "River chunks must have platforms for traversability");
            
            // 2. For other chunks, ensure non-platform obstacles don't cover the entire width
            // This is a simplified check and may need adjustment for your specific game logic
            checkObstaclesDoNotBlockPath(grassChunk);
            checkObstaclesDoNotBlockPath(roadChunk);
            checkObstaclesDoNotBlockPath(railwayChunk);
        }
    }
    
    private void checkObstaclesDoNotBlockPath(Chunk chunk) {
        // Get all obstacles that are not platforms and are not movable
        boolean hasFullWidthObstacle = chunk.getObjects().stream()
            .filter(obj -> obj instanceof Obstacle && !obj.isPlatform() && !obj.isMovable())
            .anyMatch(obj -> obj.getWidth() >= TEST_WIDTH);
        
        assertFalse(hasFullWidthObstacle, 
                   "Chunk should not have non-platform obstacles covering the entire width");
    }
}