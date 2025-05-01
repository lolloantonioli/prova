package it.unibo.view;

import java.awt.Dimension;
import java.awt.Font;

public class ScaleManager {
    private int baseWidth;
    private int baseHeight;
    private int currentWidth;
    private int currentHeight;
    private float scaleX;
    private float scaleY;
    
    /**
     * Constructor for the ScaleManager.
     * 
     * @param baseWidth The base width for which the game was designed
     * @param baseHeight The base height for which the game was designed
     */
    public ScaleManager(int baseWidth, int baseHeight) {
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
        this.currentWidth = baseWidth;
        this.currentHeight = baseHeight;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
    }
    
    /**
     * Updates the scale factors based on new dimensions.
     * 
     * @param newWidth The new width of the game window
     * @param newHeight The new height of the game window
     */
    public void updateScale(int newWidth, int newHeight) {
        this.currentWidth = newWidth;
        this.currentHeight = newHeight;
        this.scaleX = (float) newWidth / baseWidth;
        this.scaleY = (float) newHeight / baseHeight;
    }
    
    /**
     * Scales an X coordinate from logical to screen space.
     */
    public int scaleX(int x) {
        return Math.round(x * scaleX);
    }
    
    /**
     * Scales a Y coordinate from logical to screen space.
     */
    public int scaleY(int y) {
        return Math.round(y * scaleY);
    }
    
    /**
     * Scales a width from logical to screen space.
     */
    public int scaleWidth(int width) {
        return Math.round(width * scaleX);
    }
    
    /**
     * Scales a height from logical to screen space.
     */
    public int scaleHeight(int height) {
        return Math.round(height * scaleY);
    }
    
    /**
     * Scales a dimension from logical to screen space.
     */
    public Dimension scaleDimension(int width, int height) {
        return new Dimension(scaleWidth(width), scaleHeight(height));
    }
    
    /**
     * Converts a screen X coordinate back to logical space.
     */
    public int unscaleX(int screenX) {
        return Math.round(screenX / scaleX);
    }
    
    /**
     * Converts a screen Y coordinate back to logical space.
     */
    public int unscaleY(int screenY) {
        return Math.round(screenY / scaleY);
    }
    
    /**
     * Scales a font according to the current scale factors.
     * 
     * @param font The original font to scale
     * @return A new font with size adjusted according to scale
     */
    public Font scaleFont(Font font) {
        // Use the average of scaleX and scaleY for fonts to maintain proportions
        float scaleFactor = (scaleX + scaleY) / 2.0f;
        int newSize = Math.round(font.getSize() * scaleFactor);
        
        // Create a new font with the scaled size
        return new Font(font.getName(), font.getStyle(), newSize);
    }
    
    public float getScaleX() {
        return scaleX;
    }
    
    public float getScaleY() {
        return scaleY;
    }
    
    public int getCurrentWidth() {
        return currentWidth;
    }
    
    public int getCurrentHeight() {
        return currentHeight;
    }
    
    public int getBaseWidth() {
        return baseWidth;
    }
    
    public int getBaseHeight() {
        return baseHeight;
    }
}