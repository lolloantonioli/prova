package it.unibo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.impl.ChunkImpl;
import it.unibo.model.Map.impl.ObstacleImpl;
import it.unibo.model.Map.impl.PathValidatorImpl;
import it.unibo.model.Map.util.ChunkType;
import it.unibo.model.Map.util.ObstacleType;

class PathValidatorImplTest {
    
    private PathValidatorImpl pathValidator;
    private Chunk chunk;
    private final int minFreePathWidth = 50;
    private final int chunkWidth = 800;
    
    @BeforeEach
    void setUp() {
        pathValidator = new PathValidatorImpl(minFreePathWidth);
        chunk = new ChunkImpl(0, chunkWidth, ChunkType.ROAD);
    }
    
    @Test
    @DisplayName("Test path is valid with no obstacles")
    void testNoObstacles() {
        pathValidator.ensureTraversability(chunk, chunkWidth);
        // If no exception is thrown, test passes
        assertTrue(chunk.getObjects().isEmpty());
    }
    
    @Test
    @DisplayName("Test path is valid with movable obstacles only")
    void testMovableObstaclesOnly() {
        // Add a movable obstacle
        Obstacle movableObstacle = createObstacle(100, 0, 40, 40, true, ObstacleType.TREE);
        chunk.addObject(movableObstacle);
        
        pathValidator.ensureTraversability(chunk, chunkWidth);
        
        // Obstacle should still be there
        assertEquals(1, chunk.getObjects().size());
        assertSame(movableObstacle, chunk.getObjects().get(0));
    }
    
    @Test
    @DisplayName("Test path with fixed obstacles that allows traversal")
    void testFixedObstaclesWithValidPath() {
        // Add fixed obstacles that leave enough space between them
        Obstacle obstacle1 = createObstacle(0, 0, 100, 40, false, ObstacleType.TREE);
        Obstacle obstacle2 = createObstacle(200, 0, 100, 40, false, ObstacleType.TREE);
        
        chunk.addObject(obstacle1);
        chunk.addObject(obstacle2);
        
        pathValidator.ensureTraversability(chunk, chunkWidth);
        
        // Both obstacles should still be there
        assertEquals(2, chunk.getObjects().size());
        assertTrue(chunk.getObjects().contains(obstacle1));
        assertTrue(chunk.getObjects().contains(obstacle2));
    }
    
    @Test
    @DisplayName("Test path with fixed obstacles that blocks traversal")
    void testFixedObstaclesBlockingPath() {
        // Add fixed obstacles that don't leave enough space between them
        List<Obstacle> obstacles = new ArrayList<>();
        
        // Create obstacles that block the entire width with small gaps
        for (int x = 0; x < chunkWidth; x += 30) {
            Obstacle obstacle = createObstacle(x, 0, 25, 40, false, ObstacleType.TREE);
            obstacles.add(obstacle);
            chunk.addObject(obstacle);
        }
        
        int initialObstacleCount = chunk.getObjects().size();
        pathValidator.ensureTraversability(chunk, chunkWidth);
        
        // Validator should have removed some obstacles to create a path
        assertTrue(chunk.getObjects().size() < initialObstacleCount);
        
        // Confirm a free path exists now
        boolean hasFreePath = false;
        
        List<GameObject> fixedObstacles = chunk.getObjects().stream()
            .filter(obj -> obj instanceof Obstacle && !obj.isMovable())
            .sorted((a, b) -> Integer.compare(a.getX(), b.getX()))
            .toList();
        
        int lastObstacleEnd = 0;
        for (GameObject obstacle : fixedObstacles) {
            int freeSpace = obstacle.getX() - lastObstacleEnd;
            if (freeSpace >= minFreePathWidth) {
                hasFreePath = true;
                break;
            }
            lastObstacleEnd = obstacle.getX() + obstacle.getWidth();
        }
        
        // Check space after the last obstacle
        if ((chunkWidth - lastObstacleEnd) >= minFreePathWidth) {
            hasFreePath = true;
        }
        
        assertTrue(hasFreePath, "A free path should exist after validation");
    }
    
    private Obstacle createObstacle(int x, int y, int width, int height, boolean movable, ObstacleType type) {
        return new ObstacleImpl(x, y, width, height, type, movable);
    }
}