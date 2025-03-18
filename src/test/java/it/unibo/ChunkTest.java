package it.unibo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.impl.ChunkImpl;
import it.unibo.model.Map.impl.GameObjectImpl;
import it.unibo.model.Map.util.ChunkType;

class ChunkImplTest {
    
    private ChunkImpl chunk;
    private final int position = 100;
    private final int width = 800;
    private final ChunkType type = ChunkType.ROAD;
    
    @BeforeEach
    void setUp() {
        chunk = new ChunkImpl(position, width, type);
    }
    
    @Test
    @DisplayName("Test chunk initialization")
    void testChunkInitialization() {
        assertEquals(position, chunk.getPosition());
        assertEquals(width, chunk.getWidth());
        assertEquals(ChunkImpl.STANDARD_HEIGHT, chunk.getHeight());
        assertEquals(type, chunk.getType());
        assertTrue(chunk.getObjects().isEmpty());
    }
    
    @Test
    @DisplayName("Test adding objects to chunk")
    void testAddObject() {
        // Create a simple GameObject implementation for testing
        GameObject obj = new GameObjectImpl(50, 50, 20, 20);
        
        chunk.addObject(obj);
        
        List<GameObject> objects = chunk.getObjects();
        assertEquals(1, objects.size());
        assertSame(obj, objects.get(0));
    }
    
    @Test
    @DisplayName("Test chunk visibility calculation")
    void testIsVisible() {
        // Completely visible
        assertTrue(chunk.isVisible(50, 200));
        
        // Partially visible from top
        assertTrue(chunk.isVisible(50, 100));
        
        // Partially visible from bottom
        assertTrue(chunk.isVisible(150, 100));
        
        // Not visible (above viewport)
        assertFalse(chunk.isVisible(position + ChunkImpl.STANDARD_HEIGHT + 10, 50));
        
        // Not visible (below viewport)
        assertFalse(chunk.isVisible(0, position - 10));
    }
}