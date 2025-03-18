package it.unibo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.model.Map.impl.CollectibleImpl;
import it.unibo.model.Map.util.CollectibleType;

class CollectibleImplTest {
    
    private static final int TEST_X = 100;
    private static final int TEST_Y = 200;
    
    private CollectibleImpl coin;
    private CollectibleImpl powerUp;
    
    @BeforeEach
    void setUp() {
        coin = new CollectibleImpl(TEST_X, TEST_Y, CollectibleType.COIN);
        powerUp = new CollectibleImpl(TEST_X + 50, TEST_Y, CollectibleType.INVINCIBILITY);
    }
    
    @Test
    void testInitialState() {
        assertEquals(TEST_X, coin.getX(), "X coordinate should be initialized correctly");
        assertEquals(TEST_Y, coin.getY(), "Y coordinate should be initialized correctly");
        assertEquals(20, coin.getWidth(), "Width should have default value");
        assertEquals(20, coin.getHeight(), "Height should have default value");
        assertEquals(CollectibleType.COIN, coin.getType(), "Type should be initialized correctly");
        assertFalse(coin.isCollected(), "Collectible should not be collected initially");
    }
    
    @Test
    void testCollectingItem() {
        assertFalse(coin.isCollected(), "Coin should not be collected initially");
        
        coin.collect();
        assertTrue(coin.isCollected(), "Coin should be marked as collected");
        
        assertFalse(powerUp.isCollected(), "Power-up should not be affected by collecting coin");
    }
    
    @Test
    void testDifferentCollectibleTypes() {
        assertEquals(CollectibleType.COIN, coin.getType(), "Coin should have COIN type");
        assertEquals(CollectibleType.INVINCIBILITY, powerUp.getType(), "Power-up should have INVINCIBILITY type");
    }
    
    @Test
    void testCollisionDetection() {
        // Point inside the coin
        assertTrue(coin.collidesWith(TEST_X + 10, TEST_Y + 10), 
                  "Should detect collision with point inside coin");
        
        // Point outside the coin but inside power-up
        assertFalse(coin.collidesWith(TEST_X + 60, TEST_Y + 10), 
                   "Coin should not detect collision with point inside power-up");
        assertTrue(powerUp.collidesWith(TEST_X + 60, TEST_Y + 10), 
                  "Power-up should detect collision with point inside it");
    }
    
    @Test
    void testCollectibleInheritance() {
        // Collectible extends GameObject, so it should have all GameObject functionality
        coin.setMovable(true);
        coin.setSpeed(5);
        
        // Update should move the coin
        int initialX = coin.getX();
        coin.update(800);
        assertEquals(initialX + 5, coin.getX(), "Coin should move when movable and has speed");
        
        // Collection state should be preserved after moving
        assertFalse(coin.isCollected(), "Collection state should be preserved after moving");
        
        coin.collect();
        assertTrue(coin.isCollected(), "Coin should be collected after calling collect()");
        
        // Move again after collecting
        initialX = coin.getX();
        coin.update(800);
        assertEquals(initialX + 5, coin.getX(), "Collected coin should still move normally");
    }
}