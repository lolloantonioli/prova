package it.unibo.controller.Menu.api;

/**
 * Interfaccia per il controller del menu principale.
 */
public interface MenuController {
    
    /**
     * Avvia una nuova partita.
     */
    void startGame();
    
    /**
     * Apre il negozio delle skin.
     */
    void openShop();
    
    /**
     * Esce dal gioco.
     */
    void exitGame();
    
    /**
     * Aggiorna le dimensioni della vista.
     * 
     * @param width La nuova larghezza
     * @param height La nuova altezza
     */
    void updateViewDimensions(int width, int height);
    
    /**
     * Gestisce un click del mouse.
     * 
     * @param x Coordinata X del click
     * @param y Coordinata Y del click
     */
    void handleClick(int x, int y);
}
