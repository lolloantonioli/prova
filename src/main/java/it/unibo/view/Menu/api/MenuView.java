package it.unibo.view.Menu.api;

import java.awt.Graphics;
import java.awt.Font;

/**
 * Interfaccia per la vista del menu principale.
 */
public interface MenuView {
    
    /**
     * Renderizza la schermata del menu.
     * 
     * @param g Il contesto grafico su cui disegnare
     */
    void render(Graphics g);
    
    /**
     * Aggiorna le dimensioni della schermata.
     * 
     * @param width La nuova larghezza
     * @param height La nuova altezza
     */
    void updateDimensions(int width, int height);
    /**
     * Scales a font according to the current scale factors.
     * 
     * @param font The original font to scale
     * @return A new font with size adjusted according to scale
     */
     public Font scaleFont(Font font) ;
   
   
     /**
     * Gestisce un click del mouse sulla schermata del menu.
     * 
     * @param x Coordinata X del click
     * @param y Coordinata Y del click
     * @return true se il click è stato gestito, false altrimenti
     */
    boolean handleClick(int x, int y);
    

    /**
     * Imposta il controller associato a questa vista.
     * 
     * @param controller Il controller da associare
     */
    void setController(it.unibo.controller.Menu.api.MenuController controller);
}