package it.unibo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.model.Map.impl.ObstacleImpl;
import it.unibo.model.Map.util.ObstacleType;

class ObstacleTest {
    
    private static final int TEST_X = 100;
    private static final int TEST_Y = 200;
    private static final int TEST_WIDTH = 50;
    private static final int TEST_HEIGHT = 40;
    
    private ObstacleImpl tree;
    private ObstacleImpl water;
    private ObstacleImpl movingObstacle;
    
    @BeforeEach
    void setUp() {
        tree = new ObstacleImpl(TEST_X, TEST_Y, TEST_WIDTH, TEST_HEIGHT, ObstacleType.TREE, false);
        water = new ObstacleImpl(TEST_X + 100, TEST_Y, TEST_WIDTH * 2, TEST_HEIGHT, ObstacleType.WATER, false);
        movingObstacle = new ObstacleImpl(TEST_X + 200, TEST_Y, TEST_WIDTH, TEST_HEIGHT, ObstacleType.TREE, true);
        movingObstacle.setSpeed(3);
    }
    
    @Test
    void testInitialState() {
        assertEquals(TEST_X, tree.getX(), "X coordinate should be initialized correctly");
        assertEquals(TEST_Y, tree.getY(), "Y coordinate should be initialized correctly");
        assertEquals(TEST_WIDTH, tree.getWidth(), "Width should be initialized correctly");
        assertEquals(TEST_HEIGHT, tree.getHeight(), "Height should be initialized correctly");
        assertEquals(ObstacleType.TREE, tree.getType(), "Type should be initialized correctly");
        assertFalse(tree.isMovable(), "Static obstacle should not be movable");
        
        assertEquals(ObstacleType.WATER, water.getType(), "Water type should be set correctly");
        assertTrue(movingObstacle.isMovable(), "Moving obstacle should be movable");
        assertEquals(3, movingObstacle.getSpeed(), "Moving obstacle should have correct speed");
    }
    
    @Test
    void testCollisionDetection() {
        // Point inside the tree
        assertTrue(tree.collidesWith(TEST_X + 10, TEST_Y + 10), 
                  "Should detect collision with point inside tree");
        
        // Point inside water
        assertTrue(water.collidesWith(TEST_X + 120, TEST_Y + 10), 
                  "Should detect collision with point inside water");
        
        // Point outside both obstacles
        assertFalse(tree.collidesWith(TEST_X + 60, TEST_Y + 10), 
                   "Tree should not detect collision with point outside its bounds");
        assertFalse(water.collidesWith(TEST_X + 10, TEST_Y + 10), 
                   "Water should not detect collision with point inside tree");
    }
    
    @Test
    void testMovingObstacle() {
        // Check that the moving obstacle actually moves
        int initialX = movingObstacle.getX();
        movingObstacle.update(800);
        assertEquals(initialX + 3, movingObstacle.getX(), 
                    "Moving obstacle should move by its speed");
        
        // Static obstacles should not move
        initialX = tree.getX();
        tree.update(800);
        assertEquals(initialX, tree.getX(), "Static obstacle should not move");
        
        // Even if we set speed but not movable
        tree.setSpeed(5);
        tree.update(800);
        assertEquals(initialX, tree.getX(), 
                    "Obstacle with speed but not movable should not move");
    }
    
    @Test
    void testDifferentObstacleTypes() {
        assertEquals(ObstacleType.TREE, tree.getType(), "Tree should have TREE type");
        assertEquals(ObstacleType.WATER, water.getType(), "Water should have WATER type");
    }
    
    @Test
    void testObstacleInheritance() {
        // Obstacle extends GameObject, so it should have all GameObject functionality
        tree.setPlatform(true);
        assertTrue(tree.isPlatform(), "Tree can be set as a platform");
        
        // Move the tree by making it movable
        tree.setMovable(true);
        tree.setSpeed(5);
        
        int initialX = tree.getX();
        tree.update(800);
        assertEquals(initialX + 5, tree.getX(), "Tree should move when set to movable");
    }
}