package it.unibo.controller;

public class GameStateManager {
    
    private GameState currentState;
    private final GameInitializer gameInitializer;
    private final ShopInitializer shopInitializer;
    
    /**
     * Costruttore per il gestore dello stato del gioco.
     * 
     * @param gameInitializer L'inizializzatore del gioco
     * @param shopInitializer L'inizializzatore del negozio
     */
    public GameStateManager(GameInitializer gameInitializer, ShopInitializer shopInitializer) {
        this.currentState = GameState.MENU;
        this.gameInitializer = gameInitializer;
        this.shopInitializer = shopInitializer;
    }
    
    /**
     * Ottiene lo stato corrente del gioco.
     * 
     * @return Lo stato corrente
     */
    public GameState getCurrentState() {
        return currentState;
    }
    
    /**
     * Imposta lo stato del gioco.
     * 
     * @param state Il nuovo stato
     */
    public void setState(GameState state) {
        this.currentState = state;
    }
    
    /**
     * Avvia una nuova partita.
     */
    public void startGame() {
        currentState = GameState.PLAYING;
        gameInitializer.initGame();
    }
    
    /**
     * Termina la partita corrente.
     * 
     * @param score Il punteggio finale
     */
    public void endGame(int score) {
        currentState = GameState.GAME_OVER;
    }
    
    /**
     * Torna al menu principale.
     */
    public void returnToMenu() {
        currentState = GameState.MENU;
    }
    
    /**
     * Apre il negozio delle skin.
     */
    public void openShop() {
        currentState = GameState.SHOP;
        shopInitializer.initShop();
    }
    
    /**
     * Interfaccia per inizializzare il gioco.
     */
    public interface GameInitializer {
        void initGame();
    }
    
    /**
     * Interfaccia per inizializzare il negozio.
     */
    public interface ShopInitializer {
        void initShop();
    }
}
