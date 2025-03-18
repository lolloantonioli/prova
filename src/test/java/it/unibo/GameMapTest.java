package it.unibo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.impl.GameMapImpl;

class GameMapTest {
    
    private static final int TEST_WIDTH = 800;
    private static final int TEST_HEIGHT = 600;
    private static final int INITIAL_SPEED = 3;
    
    private GameMapImpl gameMap;
    
    @BeforeEach
    void setUp() {
        gameMap = new GameMapImpl(TEST_WIDTH, TEST_HEIGHT, INITIAL_SPEED);
    }
    
    @Test
    void testInitialState() {
        // Verify the initial state of the map
        assertEquals(TEST_WIDTH, gameMap.getViewportWidth());
        assertEquals(TEST_HEIGHT, gameMap.getViewportHeight());
        assertEquals(0, gameMap.getCurrentPosition());
        assertFalse(gameMap.getAllChunks().isEmpty(), "Map should have initial chunks");
        
        // First chunk should be a grass chunk (safe starting area)
        assertEquals(it.unibo.model.Map.util.ChunkType.GRASS, 
                     gameMap.getAllChunks().get(0).getType());
    }
    
    @Test
    void testMapScrolling() {
        int initialPosition = gameMap.getCurrentPosition();
        gameMap.update();
        assertEquals(initialPosition - INITIAL_SPEED, gameMap.getCurrentPosition(), 
                    "Map should scroll by the initial speed amount");
        
        // Increase scroll speed and check it works
        gameMap.increaseScrollSpeed();
        int positionAfterIncrease = gameMap.getCurrentPosition();
        gameMap.update();
        assertEquals(positionAfterIncrease - (INITIAL_SPEED + 1), gameMap.getCurrentPosition(), 
                    "Map should scroll faster after speed increase");
    }
    
    @Test
    void testVisibleChunks() {
        // Get all chunks and visible chunks
        List<Chunk> allChunks = gameMap.getAllChunks();
        List<Chunk> visibleChunks = gameMap.getVisibleChunks();
        
        // Initially, some chunks should be visible
        assertFalse(visibleChunks.isEmpty(), "There should be visible chunks");
        
        // All visible chunks should be in the full chunks list
        for (Chunk chunk : visibleChunks) {
            assertTrue(allChunks.contains(chunk), "Visible chunk should exist in all chunks");
        }
        
        // Verify visible chunks are actually within viewport
        for (Chunk chunk : visibleChunks) {
            assertTrue(chunk.isVisible(gameMap.getCurrentPosition(), gameMap.getViewportHeight()), 
                      "Chunk marked as visible should be within viewport");
        }
    }
    
    @Test
    void testChunkGeneration() {
        int initialChunkCount = gameMap.getAllChunks().size();
        
        // Force generation of a new chunk
        gameMap.generateNewChunk();
        
        // Check that a new chunk was added
        assertEquals(initialChunkCount + 1, gameMap.getAllChunks().size(), 
                    "A new chunk should be added");
        
        // The new chunk should be at the farthest position
        Chunk newChunk = gameMap.getAllChunks().get(gameMap.getAllChunks().size() - 1);
        for (Chunk chunk : gameMap.getAllChunks()) {
            if (chunk != newChunk) {
                assertTrue(chunk.getPosition() >= newChunk.getPosition(), 
                          "New chunk should be at the farthest position");
            }
        }
    }
    
    @Test
    void testPositionBoundaryChecks() {
        // Inside viewport
        assertFalse(gameMap.isPositionOutOfBounds(TEST_WIDTH / 2, gameMap.getCurrentPosition() + TEST_HEIGHT / 2), 
                   "Position inside viewport should not be out of bounds");
        
        // Outside viewport (left)
        assertTrue(gameMap.isPositionOutOfBounds(-1, gameMap.getCurrentPosition() + TEST_HEIGHT / 2), 
                  "Position left of viewport should be out of bounds");
        
        // Outside viewport (right)
        assertTrue(gameMap.isPositionOutOfBounds(TEST_WIDTH + 1, gameMap.getCurrentPosition() + TEST_HEIGHT / 2), 
                  "Position right of viewport should be out of bounds");
        
        // Outside viewport (top)
        assertTrue(gameMap.isPositionOutOfBounds(TEST_WIDTH / 2, gameMap.getCurrentPosition() - 1), 
                  "Position above viewport should be out of bounds");
        
        // Outside viewport (bottom)
        assertTrue(gameMap.isPositionOutOfBounds(TEST_WIDTH / 2, gameMap.getCurrentPosition() + TEST_HEIGHT + 1), 
                  "Position below viewport should be out of bounds");
    }
    
    @Test
    void testChunkCleanup() {        
        // Simulate many updates to move chunks far behind viewport
        for (int i = 0; i < 100; i++) {
            gameMap.update();
        }
        
        // Some chunks should have been removed and new ones added
        // This is hard to test precisely due to the dynamic nature, but we can check that:
        // 1. We still have chunks
        assertFalse(gameMap.getAllChunks().isEmpty(), "Map should still have chunks after updates");
        
        // 2. No chunk is too far behind the current position
        int currentPos = gameMap.getCurrentPosition();
        for (Chunk chunk : gameMap.getAllChunks()) {
            assertFalse(chunk.getPosition() > currentPos + TEST_HEIGHT, 
                       "No chunk should be too far behind the current position");
        }
    }
}