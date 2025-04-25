package it.unibo.controller.Map.impl;

import java.util.List;

import it.unibo.controller.Map.api.MapController;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.Collectible;
import it.unibo.model.Map.api.GameMap;
import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.util.CollectibleType;
import it.unibo.model.Map.util.ObstacleType;
import it.unibo.view.Map.api.MapView;
import it.unibo.view.Map.impl.MapViewImpl;

public class MapControllerImpl implements MapController {

    private final GameMap model;
    private final MapView view;
    
    /**
     * Constructor for the MapController class.
     * 
     * @param model Map model
     * @param view Map view
     */
    public MapControllerImpl(GameMap model, MapView view) {
        this.model = model;
        this.view = view;
        
        // Initialize the view with current model data
        if (view instanceof MapViewImpl viewImpl) {
            viewImpl.setViewportWidth(model.getViewportWidth());
            viewImpl.setCurrentPosition(model.getCurrentPosition());
            viewImpl.setVisibleChunks(model.getVisibleChunks());
        }
    }
    
    @Override
    public void update() {
        // Update the model
        model.update();
        
        // Update all game objects in visible chunks
        updateGameObjects();
        
        // Update the view with new data from the model
        if (view instanceof MapViewImpl viewImpl) {
            viewImpl.setCurrentPosition(model.getCurrentPosition());
            viewImpl.setVisibleChunks(model.getVisibleChunks());
        }
        
        // Update the view
        view.updateViewport();
    }
    
    /**
     * Updates all game objects in visible chunks.
     */
    private void updateGameObjects() {
        List<Chunk> visibleChunks = model.getVisibleChunks();
        for (Chunk chunk : visibleChunks) {
            for (var obj : chunk.getObjects()) {
                if (obj.isMovable()) {
                    obj.update(model.getViewportWidth());
                }
            }
        }
    }
    
    @Override
    public boolean isCollision(int screenX, int screenY) {
        List<Chunk> chunks = model.getVisibleChunks();
        for (Chunk chunk : chunks) {
            for (var obj : chunk.getObjects()) {
                if (obj instanceof Obstacle && obj.collidesWith(screenX, screenY)) {
                    Obstacle obstacle = (Obstacle) obj;
                    
                    // Special case for water - player can be on water if standing on a platform
                    if (obstacle.getType() == ObstacleType.WATER) {
                        return !isOnPlatform(screenX, screenY, chunk);
                    }
                    
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Checks if a point is on a platform (like a log in water).
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param chunk The chunk to check
     * @return True if the point is on a platform
     */
    private boolean isOnPlatform(int x, int y, Chunk chunk) {
        for (var obj : chunk.getObjects()) {
            if (obj.isPlatform() && obj.collidesWith(x, y)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public CollectibleType tryCollectItem(int x, int y) {
        List<Chunk> chunks = model.getVisibleChunks();
        for (Chunk chunk : chunks) {
            for (var obj : chunk.getObjects()) {
                if (obj instanceof Collectible && obj.collidesWith(x, y)) {
                    Collectible collectible = (Collectible) obj;
                    if (!collectible.isCollected()) {
                        collectible.collect();
                        return collectible.getType();
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public void placeCollectible(Collectible item) {
        // Find a suitable chunk to place the collectible
        List<Chunk> visibleChunks = model.getVisibleChunks();
        if (!visibleChunks.isEmpty()) {
            // Place in the farthest visible chunk
            Chunk targetChunk = visibleChunks.get(visibleChunks.size() - 1);
            targetChunk.addObject(item);
        }
    }
    
    @Override
    public boolean isPositionOutOfBounds(int x, int y) {
        return model.isPositionOutOfBounds(x, y);
    }
    
    @Override
    public int getCurrentPosition() {
        return model.getCurrentPosition();
    }
    
    /**
     * Updates the view dimensions when the window is resized.
     * 
     * @param width The new width of the window
     * @param height The new height of the window
     */
    public void updateViewDimensions(int width, int height) {
        if (view instanceof MapViewImpl mapViewImpl) {
            mapViewImpl.updateDimensions(width, height);
        }
    }
}