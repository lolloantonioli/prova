package it.unibo.view.Map.impl;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import it.unibo.controller.MovingObstacleController;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.Collectible;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.util.ChunkType;
import it.unibo.model.Map.util.CollectibleType;
import it.unibo.view.Map.api.MapView;
import it.unibo.view.Obstacles.MovingObstacleViewManager;
import it.unibo.view.ScaleManager;

/**
 * View class responsible for rendering the map and collectible objects using images.
 * This implementation supports dynamic screen resizing.
 */
public class MapViewImpl implements MapView {
    
    private static final Logger LOGGER = Logger.getLogger(MapViewImpl.class.getName());
    private ScaleManager scaleManager;
    private MovingObstacleViewManager obstacleViewManager; //GIULY
    
    // Base dimensions for which the game was designed
    private static final int BASE_WIDTH = 800;
    private static final int BASE_HEIGHT = 600;
    
    // Viewport information (provided by the controller)
    private List<Chunk> visibleChunks;
    private int currentPosition;
    private int viewportWidth;
    
    // Image resources for different elements
    private Map<ChunkType, BufferedImage> chunkImages;
    private Map<CollectibleType, BufferedImage> collectibleImages;
    private Map<String, BufferedImage> obstacleImages;
    
    // Path to image resources
    private static final String SEP = File.separator;
    private static final String IMAGE_PATH = "src" + SEP + "main" + SEP + "resources" + SEP;
    
    /**
     * Constructor for the MapView class.
     */
    public MapViewImpl() {
        this.scaleManager = new ScaleManager(BASE_WIDTH, BASE_HEIGHT);
        this.viewportWidth = BASE_WIDTH;

        // Inizializza il manager degli ostacoli, GIULY
        this.obstacleViewManager = new MovingObstacleViewManager(this.scaleManager);
        
        // Load image resources
        loadImageResources();
    }
    
    /**
     * Loads all image resources for map and collectible elements.
     */
    private void loadImageResources() {
        // Initialize image maps
        chunkImages = new HashMap<>();
        collectibleImages = new HashMap<>();
        obstacleImages = new HashMap<>();
        
        try {
            // Load chunk images
            chunkImages.put(ChunkType.ROAD, ImageIO.read(new File(IMAGE_PATH + "road.png")));
            chunkImages.put(ChunkType.RAILWAY, ImageIO.read(new File(IMAGE_PATH + "rails.png")));
            chunkImages.put(ChunkType.RIVER, ImageIO.read(new File(IMAGE_PATH + "river.png")));
            chunkImages.put(ChunkType.GRASS, ImageIO.read(new File(IMAGE_PATH + "grass.png")));
            
            // Load collectible images
            collectibleImages.put(CollectibleType.COIN, ImageIO.read(new File(IMAGE_PATH + "coin.png")));
            collectibleImages.put(CollectibleType.INVINCIBILITY, ImageIO.read(new File(IMAGE_PATH + "invincibility.png")));
            
            // Load obstacle images if needed
            obstacleImages.put("log", ImageIO.read(new File(IMAGE_PATH + "log.png")));
            obstacleImages.put("tree", ImageIO.read(new File(IMAGE_PATH + "tree.png")));
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load game images: " + e.getMessage(), e);
            LOGGER.info("Make sure the images are in the resources folder");
        }
    }
    
    /**
     * Updates the view dimensions when the window is resized.
     * 
     * @param width The new width of the window
     * @param height The new height of the window
     */
    public void updateDimensions(int width, int height) {
        scaleManager.updateScale(width, height);
        obstacleViewManager.updateScaleManager(scaleManager);
    }
    
    /**
     * Sets the visible chunks to be rendered.
     * This is provided by the controller.
     * 
     * @param chunks List of visible chunks
     */
    public void setVisibleChunks(List<Chunk> chunks) {
        this.visibleChunks = chunks;
    }
    
    /**
     * Sets the current position of the viewport.
     * This is provided by the controller.
     * 
     * @param position Current viewport position
     */
    public void setCurrentPosition(int position) {
        this.currentPosition = position;
    }
    
    /**
     * Sets the width of the viewport.
     * This is provided by the controller.
     * 
     * @param width Viewport width
     */
    public void setViewportWidth(int width) {
        this.viewportWidth = width;
    }
    
    @Override
    public void updateViewport() {
        // This can be used for specific view updates if needed
        // Data is now provided through setter methods
    }
    
    @Override
    public void render(Graphics g) {
        if (visibleChunks == null) {
            return; // Nothing to render yet
        }
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother visuals at different scales
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Render each visible chunk
        for (Chunk chunk : visibleChunks) {
            renderChunk(g2d, chunk, currentPosition);
        }

         //Render moving obstacles, GIULY
        obstacleViewManager.updateViewportOffset(currentPosition);
        obstacleViewManager.render(g2d);
    }

    // Aggiungi un metodo per impostare il controller degli ostacoli, GIULY
    public void setObstacleController(MovingObstacleController controller) {
        this.obstacleViewManager.setObstacleController(controller);
    }
    
    /**
     * Renders a single chunk to the screen, with scaling applied.
     * 
     * @param g2d Graphics context
     * @param chunk Chunk to render
     * @param offset Y-offset for scrolling
     */
    private void renderChunk(Graphics2D g2d, Chunk chunk, int offset) {
        int chunkPosition = chunk.getPosition();
        
        // Calculate the y-position in the viewport
        int logicalScreenY = (chunkPosition - offset);
        
        // Scale the coordinates and dimensions
        int screenY = scaleManager.scaleY(logicalScreenY);
        int visibleHeight = scaleManager.scaleHeight(chunk.getHeight());
        
        // Adjust visibility calculations
        if (screenY < 0) {
            visibleHeight += screenY;
            screenY = 0;
        }
        
        if (screenY + visibleHeight > scaleManager.getCurrentHeight()) {
            visibleHeight = scaleManager.getCurrentHeight() - screenY;
        }
        
        // Only render if the chunk is at least partially visible
        if (visibleHeight <= 0) {
            return;
        }
        
        // Get the scaled chunk width
        int scaledChunkWidth = scaleManager.scaleWidth(viewportWidth);
        
        // Render chunk using image
        BufferedImage chunkImage = chunkImages.get(chunk.getType());
        if (chunkImage != null) {
            // Tile the image horizontally and vertically if needed
            int imgWidth = chunkImage.getWidth();
            int imgHeight = chunkImage.getHeight();
            
            for (int x = 0; x < scaledChunkWidth; x += imgWidth) {
                for (int y = screenY; y < screenY + visibleHeight; y += imgHeight) {
                    // Calculate the source rectangle (for partial images at edges)
                    int srcWidth = Math.min(imgWidth, scaledChunkWidth - x);
                    int srcHeight = Math.min(imgHeight, screenY + visibleHeight - y);
                    
                    // Draw the appropriate portion of the image
                    g2d.drawImage(
                        chunkImage.getSubimage(0, 0, srcWidth, srcHeight),
                        x, y, srcWidth, srcHeight, null
                    );
                }
            }
        }
        
        // Render game objects in the chunk
        for (GameObject obj : chunk.getObjects()) {
            // Scale the object's position and dimensions
            int scaledX = scaleManager.scaleX(obj.getX());
            int scaledY = scaleManager.scaleY(obj.getY() - offset);
            int scaledWidth = scaleManager.scaleWidth(obj.getWidth());
            int scaledHeight = scaleManager.scaleHeight(obj.getHeight());
            
            // Skip if not visible on screen
            if (scaledY + scaledHeight < 0 || scaledY > scaleManager.getCurrentHeight()) {
                continue;
            }
            
            // Render different types of objects
            if (obj instanceof Collectible) {
                renderCollectible(g2d, (Collectible) obj, scaledX, scaledY, scaledWidth, scaledHeight);
            } else if (obj.isPlatform()) {
                // Render platforms like logs
                BufferedImage logImage = obstacleImages.get("log");
                if (logImage != null) {
                    g2d.drawImage(logImage, scaledX, scaledY, scaledWidth, scaledHeight, null);
                }
            } else {
                // Render other game objects if needed
                // This could be expanded based on object types
            }
        }
    }
    
    /**
     * Renders a collectible to the screen with scaling.
     * 
     * @param g2d Graphics context
     * @param collectible Collectible to render
     * @param x Scaled X-position on screen
     * @param y Scaled Y-position on screen
     * @param width Scaled width
     * @param height Scaled height
     */
    private void renderCollectible(Graphics2D g2d, Collectible collectible, int x, int y, int width, int height) {
        // Skip if already collected
        if (collectible.isCollected()) {
            return;
        }
        
        CollectibleType type = collectible.getType();
        BufferedImage collectibleImage = collectibleImages.get(type);
        
        if (collectibleImage != null) {
            // Draw the collectible image with scaled dimensions
            g2d.drawImage(collectibleImage, x, y, width, height, null);
        }
    }
}