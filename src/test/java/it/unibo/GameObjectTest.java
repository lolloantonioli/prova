package it.unibo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.model.Map.impl.GameObjectImpl;

class GameObjectImplTest {
    
    private static final int TEST_X = 100;
    private static final int TEST_Y = 200;
    private static final int TEST_WIDTH = 40;
    private static final int TEST_HEIGHT = 40;
    private static final int MAP_WIDTH = 800;
    
    private GameObjectImpl gameObject;
    
    @BeforeEach
    void setUp() {
        gameObject = new GameObjectImpl(TEST_X, TEST_Y, TEST_WIDTH, TEST_HEIGHT);
    }
    
    @Test
    void testInitialState() {
        assertEquals(TEST_X, gameObject.getX(), "X coordinate should be initialized correctly");
        assertEquals(TEST_Y, gameObject.getY(), "Y coordinate should be initialized correctly");
        assertEquals(TEST_WIDTH, gameObject.getWidth(), "Width should be initialized correctly");
        assertEquals(TEST_HEIGHT, gameObject.getHeight(), "Height should be initialized correctly");
        assertFalse(gameObject.isMovable(), "GameObject should not be movable by default");
        assertEquals(0, gameObject.getSpeed(), "Speed should be 0 by default");
        assertFalse(gameObject.isPlatform(), "GameObject should not be a platform by default");
    }
    
    @Test
    void testCollision() {
        // Point inside the GameObject
        assertTrue(gameObject.collidesWith(TEST_X + 10, TEST_Y + 10), 
                  "Should detect collision with point inside");
        
        // Point at the edge
        assertTrue(gameObject.collidesWith(TEST_X, TEST_Y), 
                  "Should detect collision with point at the edge");
        assertTrue(gameObject.collidesWith(TEST_X + TEST_WIDTH - 1, TEST_Y + TEST_HEIGHT - 1), 
                  "Should detect collision with point at the far edge");
        
        // Point outside
        assertFalse(gameObject.collidesWith(TEST_X - 1, TEST_Y), 
                   "Should not detect collision with point outside (left)");
        assertFalse(gameObject.collidesWith(TEST_X, TEST_Y - 1), 
                   "Should not detect collision with point outside (top)");
        assertFalse(gameObject.collidesWith(TEST_X + TEST_WIDTH, TEST_Y), 
                   "Should not detect collision with point outside (right)");
        assertFalse(gameObject.collidesWith(TEST_X, TEST_Y + TEST_HEIGHT), 
                   "Should not detect collision with point outside (bottom)");
    }
    
    @Test
    void testMovement() {
        // Make object movable with positive speed
        gameObject.setMovable(true);
        gameObject.setSpeed(5);
        
        // Update should move the object
        int initialX = gameObject.getX();
        gameObject.update(MAP_WIDTH);
        assertEquals(initialX + 5, gameObject.getX(), "Object should move by its speed");
        
        // Test wrap-around (right to left)
        gameObject.setSpeed(10);
        // Move to just before the edge
        int newX = MAP_WIDTH - 5;
        // Create a new object to set position directly
        GameObjectImpl wrapObject = new GameObjectImpl(newX, TEST_Y, TEST_WIDTH, TEST_HEIGHT);
        wrapObject.setMovable(true);
        wrapObject.setSpeed(10);
        
        wrapObject.update(MAP_WIDTH);
        // Object should wrap to the left side
        assertTrue(wrapObject.getX() < 0, "Object should wrap from right to left");
        
        // Test wrap-around (left to right)
        GameObjectImpl wrapLeftObject = new GameObjectImpl(5, TEST_Y, TEST_WIDTH, TEST_HEIGHT);
        wrapLeftObject.setMovable(true);
        wrapLeftObject.setSpeed(-10);
        
        wrapLeftObject.update(MAP_WIDTH);
        // Object should wrap to the right side
        assertTrue(wrapLeftObject.getX() > 0, "Object should wrap from left to right");
    }
    
    @Test
    void testSetters() {
        // Test setMovable
        gameObject.setMovable(true);
        assertTrue(gameObject.isMovable(), "isMovable should be updated");
        
        // Test setSpeed
        gameObject.setSpeed(10);
        assertEquals(10, gameObject.getSpeed(), "Speed should be updated");
        
        // Test setPlatform
        gameObject.setPlatform(true);
        assertTrue(gameObject.isPlatform(), "isPlatform should be updated");
    }
}