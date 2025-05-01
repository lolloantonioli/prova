package it.unibo;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

//import it.unibo.controller.GameState;
import it.unibo.controller.GameStateManager;
import it.unibo.controller.Map.impl.MapControllerImpl;
import it.unibo.controller.Menu.impl.MenuControllerImpl;
import it.unibo.controller.Shop.ShopController;
import it.unibo.model.Map.impl.GameMapImpl;
import it.unibo.view.Menu.impl.MenuViewImpl;
import it.unibo.view.Shop.ShopView;
import it.unibo.view.ScaleManager;
import it.unibo.view.Map.impl.MapViewImpl;

/**
 * Pannello principale del gioco che gestisce il rendering e l'input.
 */
public class MainGamePanel extends JPanel implements GameStateManager.GameInitializer, GameStateManager.ShopInitializer {
    
    private GameStateManager gameStateManager;
    private ScaleManager scaleManager;
    
    // Componenti del menu
    private MenuViewImpl menuView;
    private MenuControllerImpl menuController;
    
    // Componenti del gioco
    private MapControllerImpl mapController;
    private GameMapImpl gameMap;
    private MapViewImpl mapView;
    
    // Componenti del negozio (da implementare)
    private ShopView shopView;
    private ShopController shopController;
    
    // Componenti della schermata di fine partita (da implementare)
    //private GameOverView gameOverView;
    //private GameOverController gameOverController;
    
    /**
     * Costruttore del pannello principale.
     */
    public MainGamePanel(int width, int height) {
        this.setSize(width, height);
        this.setFocusable(true);
        
        // Inizializza lo ScaleManager
        scaleManager = new ScaleManager(800, 600);
        scaleManager.updateScale(width, height);
        
        // Inizializza il GameStateManager
        gameStateManager = new GameStateManager(this, this);
        
        // Inizializza le componenti del menu
        initMenu();
        
        // Aggiungi i listener per il mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }
    
    /**
     * Inizializza le componenti del menu.
     */
    private void initMenu() {
        menuView = new MenuViewImpl(scaleManager);
        menuController = new MenuControllerImpl(menuView, gameStateManager);
    }
    
    @Override
    public void initGame() {
        // Inizializza le componenti del gioco
        if (mapController == null) {
            gameMap = new GameMapImpl(scaleManager.getBaseWidth(), scaleManager.getBaseHeight(), 3);
            mapView = new MapViewImpl();
            mapController = new MapControllerImpl(gameMap, mapView);
        }
    }
    
    @Override
    public void initShop() {
        // TODO: Inizializza le componenti del negozio
        // Questa parte verrà implementata in seguito
    }
    
    /**
     * Gestisce il click del mouse.
     * 
     * @param x Coordinata X
     * @param y Coordinata Y
     */
    private void handleMouseClick(int x, int y) {
        switch (gameStateManager.getCurrentState()) {
            case MENU:
                menuController.handleClick(x, y);
                break;
            case PLAYING:
                // Gestione click durante il gioco
                break;
            case SHOP:
                // Gestione click nel negozio
                break;
            case GAME_OVER:
                // Gestione click nella schermata di fine partita
                break;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        switch (gameStateManager.getCurrentState()) {
            case MENU:
                menuView.render(g);
                break;
            case PLAYING:
                mapView.render(g);
                break;
            case SHOP:
                // Renderizza la schermata del negozio
                break;
            case GAME_OVER:
                // Renderizza la schermata di fine partita
                break;
        }
    }
    
    /**
     * Ridimensiona il pannello.
     * 
     * @param width Nuova larghezza
     * @param height Nuova altezza
     */
    public void resize(int width, int height) {
        scaleManager.updateScale(width, height);
        
        // Aggiorna le dimensioni delle varie viste
        menuController.updateViewDimensions(width, height);
        
        if (mapController != null) {
            mapController.updateViewDimensions(width, height);
        }
        
        // Aggiorna le altre viste quando verranno implementate
    }
    
    /**
     * Aggiorna lo stato del gioco.
     */
    public void update() {
        // Aggiorna le componenti in base allo stato del gioco
        switch (gameStateManager.getCurrentState()) {
            case PLAYING:
                mapController.update();
                break;
            case SHOP:
                // Aggiorna il negozio
                break;
            case GAME_OVER:
                // Aggiorna la schermata di fine partita
                break;
            default:
                break;
        }
        
        // Forza il ridisegno del pannello
        repaint();
    }
}