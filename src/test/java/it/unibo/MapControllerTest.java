package it.unibo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unibo.controller.Map.impl.MapControllerImpl;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.Collectible;
import it.unibo.model.Map.api.GameMap;
import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.impl.ChunkImpl;
import it.unibo.model.Map.util.ChunkType;
import it.unibo.model.Map.util.CollectibleType;
import it.unibo.model.Map.util.ObstacleType;
import it.unibo.view.Map.api.MapView;
import it.unibo.view.Map.impl.MapViewImpl;

class MapControllerImplTest {
    
    private MapControllerImpl controller;
    private TestGameMap gameMap;
    private TestMapView mapView;
    
    @BeforeEach
    void setUp() {
        gameMap = new TestGameMap();
        mapView = new TestMapView();
        controller = new MapControllerImpl(gameMap, mapView);
    }
    
    @Test
    @DisplayName("Test controller initialization")
    void testInitialization() {
        // Check if the view was initialized with model data
        assertEquals(gameMap.getCurrentPosition(), mapView.getCurrentPosition());
        assertEquals(gameMap.getViewportWidth(), mapView.getViewportWidth());
        assertSame(gameMap.getVisibleChunks(), mapView.getVisibleChunks());
    }
    
    @Test
    @DisplayName("Test controller update")
    void testUpdate() {
        controller.update();
        
        // Check if model was updated
        assertTrue(gameMap.updateCalled);
        
        // Check if view was updated with model data
        assertEquals(gameMap.getCurrentPosition(), mapView.getCurrentPosition());
        assertSame(gameMap.getVisibleChunks(), mapView.getVisibleChunks());
        assertTrue(mapView.updateViewportCalled);
        
        // Check if game objects were updated
        for (Chunk chunk : gameMap.getVisibleChunks()) {
            for (var obj : chunk.getObjects()) {
                if (obj instanceof TestGameObject) {
                    TestGameObject testObj = (TestGameObject) obj;
                    if (testObj.isMovable()) {
                        assertTrue(testObj.updateCalled);
                        assertEquals(gameMap.getViewportWidth(), testObj.lastViewportWidth);
                    }
                }
            }
        }
    }
    
    @Test
    @DisplayName("Test collision detection")
    void testIsCollision() {
        // Add a test chunk with an obstacle
        ChunkImpl chunk = new ChunkImpl(0, 800, ChunkType.ROAD);
        
        // Add a normal obstacle
        TestObstacle obstacle = new TestObstacle(100, 100, 50, 50, false, ObstacleType.TREE);
        chunk.addObject(obstacle);
        
        // Add a water obstacle
        TestObstacle water = new TestObstacle(200, 100, 100, 50, false, ObstacleType.WATER);
        chunk.addObject(water);
        
        // Add a platform in water
        TestGameObject platform = new TestGameObject(250, 100, 50, 50, true) {
            @Override
            public boolean isPlatform() {
                return true;
            }
        };
        chunk.addObject(platform);
        
        gameMap.visibleChunks.add(chunk);
        
        // Test collision with normal obstacle
        assertTrue(controller.isCollision(110, 110));
        
        // Test no collision outside of obstacles
        assertFalse(controller.isCollision(50, 50));
        
        // Test collision with water (no platform)
        assertTrue(controller.isCollision(210, 110));
        
        // Test no collision with water when on platform
        assertFalse(controller.isCollision(260, 110));
    }
    
    @Test
    @DisplayName("Test collectible collection")
    void testTryCollectItem() {
        // Add a test chunk with collectibles
        ChunkImpl chunk = new ChunkImpl(0, 800, ChunkType.ROAD);
        
        // Add an uncollected coin
        TestCollectible coin = new TestCollectible(100, 100, 30, 30, CollectibleType.COIN);
        chunk.addObject(coin);
        
        // Add an already collected item
        TestCollectible collectedItem = new TestCollectible(200, 100, 30, 30, CollectibleType.INVINCIBILITY);
        collectedItem.collect(); // Pre-collect this item
        chunk.addObject(collectedItem);
        
        gameMap.visibleChunks.add(chunk);
        
        // Test collecting the coin
        CollectibleType collectedType = controller.tryCollectItem(110, 110);
        assertEquals(CollectibleType.COIN, collectedType);
        assertTrue(coin.isCollected());
        
        // Test trying to collect an already collected item
        collectedType = controller.tryCollectItem(210, 110);
        assertNull(collectedType);
        
        // Test trying to collect where there's no collectible
        collectedType = controller.tryCollectItem(50, 50);
        assertNull(collectedType);
    }
    
    @Test
    @DisplayName("Test placing collectible")
    void testPlaceCollectible() {
        // Add a test chunk
        ChunkImpl chunk = new ChunkImpl(0, 800, ChunkType.ROAD);
        gameMap.visibleChunks.add(chunk);
        
        // Create a collectible to place
        TestCollectible coin = new TestCollectible(0, 0, 30, 30, CollectibleType.COIN);
        
        // Place it
        controller.placeCollectible(coin);
        
        // Check if it was added to the farthest visible chunk
        assertTrue(chunk.getObjects().contains(coin));
    }
    
    @Test
    @DisplayName("Test position bounds checking")
    void testIsPositionOutOfBounds() {
        controller.isPositionOutOfBounds(100, 100);
        assertTrue(gameMap.isPositionOutOfBoundsCalled);
        assertEquals(100, gameMap.lastCheckX);
        assertEquals(100, gameMap.lastCheckY);
    }
    
    @Test
    @DisplayName("Test view dimensions update")
    void testUpdateViewDimensions() {
        controller.updateViewDimensions(1024, 768);
        assertTrue(mapView.updateDimensionsCalled);
        assertEquals(1024, mapView.lastWidth);
        assertEquals(768, mapView.lastHeight);
    }
    
    // Test implementations of required interfaces
    
    private static class TestGameMap implements GameMap {
        private int currentPosition = 0;
        private int viewportWidth = 800;
        private List<Chunk> visibleChunks = new ArrayList<>();
        private boolean updateCalled = false;
        private boolean isPositionOutOfBoundsCalled = false;
        private int lastCheckX = 0;
        private int lastCheckY = 0;
        
        @Override
        public int getCurrentPosition() {
            return currentPosition;
        }
        
        @Override
        public int getViewportWidth() {
            return viewportWidth;
        }
        
        @Override
        public List<Chunk> getVisibleChunks() {
            return visibleChunks;
        }
        
        @Override
        public void update() {
            updateCalled = true;
        }
        
        @Override
        public boolean isPositionOutOfBounds(int x, int y) {
            isPositionOutOfBoundsCalled = true;
            lastCheckX = x;
            lastCheckY = y;
            return false;
        }
    }
    
    private static class TestMapView implements MapView {
        private int currentPosition = 0;
        private int viewportWidth = 800;
        private List<Chunk> visibleChunks = null;
        private boolean updateViewportCalled = false;
        private boolean updateDimensionsCalled = false;
        private int lastWidth = 0;
        private int lastHeight = 0;
        
        @Override
        public void updateViewport() {
            updateViewportCalled = true;
        }
        
        @Override
        public void render(java.awt.Graphics g) {
            // Not needed for this test
        }
        
        public void setCurrentPosition(int position) {
            this.currentPosition = position;
        }
        
        public void setViewportWidth(int width) {
            this.viewportWidth = width;
        }
        
        public void setVisibleChunks(List<Chunk> chunks) {
            this.visibleChunks = chunks;
        }
        
        public int getCurrentPosition() {
            return currentPosition;
        }
        
        public int getViewportWidth() {
            return viewportWidth;
        }
        
        public List<Chunk> getVisibleChunks() {
            return visibleChunks;
        }
        
        public void updateDimensions(int width, int height) {
            updateDimensionsCalled = true;
            lastWidth = width;
            lastHeight = height;
        }
    }
    
    private static class TestGameObject implements it.unibo.model.Map.api.GameObject {
        private final int posX;
        private final int posY;
        private final int oWidth;
        private final int oHeight;
        private final boolean isMovable;
        protected boolean updateCalled = false;
        protected int lastViewportWidth = 0;
        
        public TestGameObject(int x, int y, int width, int height, boolean movable) {
            this.posX = x;
            this.posY = y;
            this.oWidth = width;
            this.oHeight = height;
            this.isMovable = movable;
        }
        
        @Override
        public int getX() { return posX; }
        
        @Override
        public int getY() { return posY; }
        
        @Override
        public int getWidth() { return oWidth; }
        
        @Override
        public int getHeight() { return oHeight; }
        
        @Override
        public boolean isMovable() { return isMovable; }
        
        @Override
        public boolean isPlatform() { return false; }
        
        @Override
        public void update(int viewportWidth) {
            updateCalled = true;
            lastViewportWidth = viewportWidth;
        }
        
        @Override
        public boolean collidesWith(int x, int y) {
            return x >= posX && x < posX + oWidth && y >= posY && y < posY + oHeight;
        }
    }
    
    private static class TestObstacle extends TestGameObject implements Obstacle {
        private final ObstacleType obstacleType;
        
        public TestObstacle(int x, int y, int width, int height, boolean movable, ObstacleType type) {
            super(x, y, width, height, movable);
            this.obstacleType = type;
        }
        
        @Override
        public ObstacleType getType() {
            return obstacleType;
        }
    }
    
    private static class TestCollectible extends TestGameObject implements Collectible {
        private final CollectibleType collectibleType;
        private boolean collected = false;
        
        public TestCollectible(int x, int y, int width, int height, CollectibleType type) {
            super(x, y, width, height, false);
            this.collectibleType = type;
        }
        
        @Override
        public CollectibleType getType() {
            return collectibleType;
        }
        
        @Override
        public boolean isCollected() {
            return collected;
        }
        
        @Override
        public void collect() {
            collected = true;
        }
    }
}