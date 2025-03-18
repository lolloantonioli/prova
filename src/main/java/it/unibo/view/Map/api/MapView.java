package it.unibo.view.Map.api;

import java.awt.Graphics;

public interface MapView {

    /**
     * Updates the viewport based on the current map position.
     */
    void updateViewport();

    /**
     * Renders the map and all visible chunks to the screen.
     * 
     * @param g Graphics context to render to
     */
    void render(Graphics g);

}
