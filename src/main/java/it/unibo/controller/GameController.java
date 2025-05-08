package it.unibo.controller;

import it.unibo.controller.Map.api.MapController;
import it.unibo.controller.Map.impl.MapControllerImpl;
import it.unibo.model.Map.api.GameMap;
import it.unibo.view.GameView;
import it.unibo.view.Map.api.MapView;

/**
 * Controller principale che gestisce il ciclo di gioco e coordina
 * tutti gli altri controller e componenti.
 */
public class GameController {
    // Componenti del modello
    private final GameMap gameMap;
   // private final Player player;
    
    // Controller e view
    private final MapController mapController;
   // private final PlayerController playerController;
    //private final CollisionController collisionController;
    //rivate final ScoreController scoreController;
    private final GameView gameView;
    private final GameStateManager gameStateManager;
    private final MovingObstacleController movingObstacleController;
    
    // Gestione del game loop
    //private final GameLoopManager gameLoopManager;
    private boolean gameRunning;
    private boolean gamePaused;
    private long gameStartTime;

     // Configurazione della difficoltà
     private static final long DIFFICULTY_INCREASE_INTERVAL = 30000; // 30 secondi
     private long lastDifficultyIncrease;
    
    /**
     * Costruttore per GameController.
     * 
     * @param gameView La view principale del gioco
     * @param mapView La view della mappa
     * @param gameMap La mappa di gioco
     * @param player Il giocatore
     * @param gameStateManager Il gestore dello stato di gioco
     */
    public GameController(GameView gameView, MapView mapView, GameMap gameMap,/* Player player ,*/ GameStateManager gameStateManager) {
        this.gameView = gameView;
        this.gameMap = gameMap;
       // this.player = player;
        this.gameStateManager = gameStateManager;
        
        // Inizializzazione dei controller
        this.mapController = new MapControllerImpl(gameMap, mapView);
       // this.playerController = new PlayerController(player);
        //this.collisionController = new CollisionController(mapController, player);
        //this.scoreController = new ScoreController(player, gameView);
        //this.gameLoopManager = new GameLoopManager(this::gameLoopUpdate);

        // Controller degli ostacoli mobili
        this.movingObstacleController = new MovingObstacleController(gameMap);
        
        // Stato iniziale del gioco
        this.gameRunning = false;
        this.gamePaused = false;
        
        // Configurazione degli input
        //InputHandler inputHandler = new InputHandler(this, playerController);
        //gameView.setKeyListener(inputHandler);
    }
    
    /**
     * Avvia il gioco.
     */
    public void startGame() {
        if (!gameRunning) {
            gameRunning = true;
            gamePaused = false;
            //scoreController.resetScore();
            gameStartTime = System.currentTimeMillis();
            lastDifficultyIncrease = gameStartTime;
            
            // Posiziona il giocatore nella posizione iniziale
           // player.setPosition(gameMap.getViewportWidth() / 2, gameMap.getCurrentPosition() + 50);
            
            // Resetta e avvia la generazione degli ostacoli mobili
            movingObstacleController.resetObstacles();
            movingObstacleController.startObstacleGeneration();
            
            // Avvia il game loop
           // gameLoopManager.startGameLoop();
            
            // Informa la view che il gioco è iniziato
            gameView.showGameScreen();
            
            // Aggiorna lo stato del gioco
            gameStateManager.setState(GameState.PLAYING);
        }
    }
    
    /**
     * Mette in pausa o riprende il gioco.
     */
    public void togglePause() {
        gamePaused = !gamePaused;

        if (gamePaused) {
            // Quando in pausa, sospendi la generazione di ostacoli
            movingObstacleController.pauseObstacleGeneration();
        } else {
            // Quando riprendi, riavvia la generazione di ostacoli
            movingObstacleController.resumeObstacleGeneration();
        }
        
        gameView.setPauseState(gamePaused);
    }
    
    /**
     * Termina il gioco e torna al menu principale.
     */
    public void endGame() {
        if (gameRunning) {
            gameRunning = false;
           // gameLoopManager.stopGameLoop();

            // Ferma la generazione di ostacoli e rilascia le risorse
            movingObstacleController.dispose();
            
            // Aggiorna il punteggio massimo se necessario
           // scoreController.updateHighScore();
            
            // Mostra la schermata di game over
           // gameView.showGameOverScreen(scoreController.getScore());
            
            // Aggiorna lo stato del gioco
           // gameStateManager.endGame(scoreController.getScore());
        }
    }
    
    /**
     * Aggiorna lo stato del gioco ad ogni frame.
     */
    private void gameLoopUpdate() {
        try {
            if (gameRunning && !gamePaused) {
                // Aggiorna prima gli ostacoli mobili
                movingObstacleController.update();

                // Aggiorna la mappa
                mapController.update();
                
                // Aggiorna il giocatore
               // playerController.update();
                
                // Calcola il punteggio
                //scoreController.updateScore();
                
                // Gestisce collisioni con gli ostacoli fissi e mobili
            /*    if (collisionController.checkCollisions() || 
                    movingObstacleController.checkCollision(player.getX(), player.getY())) {
                    playerController.die();
                    endGame();
                    return;
                }
               */  
                // Controlla gli oggetti da raccogliere
                //collisionController.checkCollectibles();
                
                // Controlla se il giocatore è fuori dai limiti
                /*if (collisionController.checkBoundaries()) {
                    playerController.die();
                    endGame();
                    return;
                }*/
                
                // Aumenta progressivamente la difficoltà
                updateDifficulty();
                
                // Aggiorna la view
               // gameView.updateView();
            }
        } catch (Exception e) {
            // Cattura eventuali eccezioni per evitare che il game loop si interrompa
            System.err.println("Errore nel game loop: " + e.getMessage());
        }
    }
    
    /**
     * Aggiorna la difficoltà del gioco con il passare del tempo.
     */
    private void updateDifficulty() {
         // Calcola il tempo di gioco in millisecondi
         long currentTime = System.currentTimeMillis();
        
         // Aumenta la difficoltà ogni DIFFICULTY_INCREASE_INTERVAL millisecondi
         if (currentTime - lastDifficultyIncrease >= DIFFICULTY_INCREASE_INTERVAL) {
             // Aumenta la velocità di scorrimento della mappa
             gameMap.increaseScrollSpeed();
             
             // Aumenta la difficoltà degli ostacoli mobili
             movingObstacleController.increaseDifficulty(1);
             
             // Aggiorna il timestamp dell'ultimo aumento di difficoltà
             lastDifficultyIncrease = currentTime;
             
             // Opzionale: Notifica al giocatore l'aumento di difficoltà
             //gameView.showDifficultyIncrease(movingObstacleController.getCurrentDifficultyLevel());
         
        }
    }
    
    /**
     * Verifica se il gioco è in esecuzione.
     * 
     * @return true se il gioco è in esecuzione, false altrimenti
     */
    public boolean isGameRunning() {
        return gameRunning;
    }
    
    /**
     * Verifica se il gioco è in pausa.
     * 
     * @return true se il gioco è in pausa, false altrimenti
     */
    public boolean isGamePaused() {
        return gamePaused;
    }
    
    /**
     * Torna al menu principale.
     */
    public void returnToMenu() {
        if (gameRunning) {
            endGame();
        }
        gameStateManager.returnToMenu();
    }
    
    /**
     * Aggiorna le dimensioni della vista quando la finestra viene ridimensionata.
     * 
     * @param width Nuova larghezza
     * @param height Nuova altezza
     */
    public void updateViewDimensions(int width, int height) {
        if (mapController instanceof MapControllerImpl mapControllerImpl) {
            mapControllerImpl.updateViewDimensions(width, height);
        }
    }

    /**
     * Ottiene il controller degli ostacoli mobili.
     * 
     * @return Il controller degli ostacoli mobili
     */
    public MovingObstacleController getMovingObstacleController() {
        return movingObstacleController;
    }
}