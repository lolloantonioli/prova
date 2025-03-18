package it.unibo;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Dimension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unibo.view.ScaleManager;

class ScaleManagerTest {
    
    private ScaleManager scaleManager;
    private final int baseWidth = 800;
    private final int baseHeight = 600;
    
    @BeforeEach
    void setUp() {
        scaleManager = new ScaleManager(baseWidth, baseHeight);
    }
    
    @Test
    @DisplayName("Test initial scale values")
    void testInitialValues() {
        assertEquals(baseWidth, scaleManager.getBaseWidth());
        assertEquals(baseHeight, scaleManager.getBaseHeight());
        assertEquals(baseWidth, scaleManager.getCurrentWidth());
        assertEquals(baseHeight, scaleManager.getCurrentHeight());
        assertEquals(1.0f, scaleManager.getScaleX());
        assertEquals(1.0f, scaleManager.getScaleY());
    }
    
    @Test
    @DisplayName("Test scale update with larger dimensions")
    void testUpdateScaleWithLargerDimensions() {
        int newWidth = 1600;
        int newHeight = 900;
        
        scaleManager.updateScale(newWidth, newHeight);
        
        assertEquals(newWidth, scaleManager.getCurrentWidth());
        assertEquals(newHeight, scaleManager.getCurrentHeight());
        assertEquals(2.0f, scaleManager.getScaleX());
        assertEquals(1.5f, scaleManager.getScaleY());
    }
    
    @Test
    @DisplayName("Test scale update with smaller dimensions")
    void testUpdateScaleWithSmallerDimensions() {
        int newWidth = 400;
        int newHeight = 300;
        
        scaleManager.updateScale(newWidth, newHeight);
        
        assertEquals(newWidth, scaleManager.getCurrentWidth());
        assertEquals(newHeight, scaleManager.getCurrentHeight());
        assertEquals(0.5f, scaleManager.getScaleX());
        assertEquals(0.5f, scaleManager.getScaleY());
    }
    
    @Test
    @DisplayName("Test coordinate scaling")
    void testCoordinateScaling() {
        scaleManager.updateScale(1600, 900);
        
        // Test X scaling
        assertEquals(200, scaleManager.scaleX(100));
        
        // Test Y scaling
        assertEquals(150, scaleManager.scaleY(100));
        
        // Test width scaling
        assertEquals(400, scaleManager.scaleWidth(200));
        
        // Test height scaling
        assertEquals(300, scaleManager.scaleHeight(200));
    }
    
    @Test
    @DisplayName("Test dimension scaling")
    void testDimensionScaling() {
        scaleManager.updateScale(1600, 900);
        
        Dimension scaled = scaleManager.scaleDimension(100, 200);
        assertEquals(200, scaled.width);
        assertEquals(300, scaled.height);
    }
    
    @Test
    @DisplayName("Test coordinate unscaling")
    void testCoordinateUnscaling() {
        scaleManager.updateScale(1600, 900);
        
        // Test X unscaling
        assertEquals(50, scaleManager.unscaleX(100));
        
        // Test Y unscaling
        assertEquals(67, scaleManager.unscaleY(100));
    }
    
    @Test
    @DisplayName("Test roundtrip scaling")
    void testRoundtripScaling() {
        scaleManager.updateScale(1600, 900);
        
        int originalX = 123;
        int originalY = 456;
        
        int scaledX = scaleManager.scaleX(originalX);
        int scaledY = scaleManager.scaleY(originalY);
        
        int unscaledX = scaleManager.unscaleX(scaledX);
        int unscaledY = scaleManager.unscaleY(scaledY);
        
        // Due to rounding, there might be a small difference
        assertTrue(Math.abs(originalX - unscaledX) <= 1);
        assertTrue(Math.abs(originalY - unscaledY) <= 1);
    }
}