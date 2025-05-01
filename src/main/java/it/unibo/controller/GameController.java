package it.unibo.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.unibo.controller.Map.api.MapController;
import it.unibo.model.Map.api.GameMap;
import it.unibo.model.player.Player;
import it.unibo.view.GameView;
import it.unibo.view.Map.MapView;

/**
 * Controller principale che gestisce il ciclo di gioco e coordina
 * tutti gli altri controller e componenti.
 */
public class GameController {
    // Componenti del modello
    private GameMap gameMap;
    private Player player;
    
    // Controller e view
    private MapController mapController;
    private PlayerController playerController;
    private CollisionController collisionController;
    private ScoreController scoreController;
    private GameView gameView;
    
    // Gestione del game loop
    private GameLoopManager gameLoopManager;
    private boolean gameRunning;
    private boolean gamePaused;
    private long gameStartTime;
    private MovingObstacleController movingObstacleController; //GIULY
    
    /**
     * Costruttore per GameController.
     * 
     * @param gameView La view principale del gioco
     * @param mapView La view della mappa
     * @param gameMap La mappa di gioco
     * @param player Il giocatore
     */
    public GameController(GameView gameView, MapView mapView, GameMap gameMap, Player player) {
        this.gameView = gameView;
        this.gameMap = gameMap;
        this.player = player;
        
        // Inizializzazione dei controller
        this.mapController = new MapController(gameMap, mapView);
        this.playerController = new PlayerController(player);
        this.collisionController = new CollisionController(mapController, player);
        this.scoreController = new ScoreController(player, gameView);
        this.gameLoopManager = new GameLoopManager(this::gameLoopUpdate);
        
        // Stato iniziale del gioco
        this.gameRunning = false;
        this.gamePaused = false;
        
        // Configurazione degli input
        InputHandler inputHandler = new InputHandler(this, playerController);
        gameView.setKeyListener(inputHandler);

        this.movingObstacleController = new MovingObstacleController(gameMap); //GIULY
    }
    
    /**
     * Avvia il gioco.
     */
    public void startGame() {
        if (!gameRunning) {
            gameRunning = true;
            gamePaused = false;
            scoreController.resetScore();
            gameStartTime = System.currentTimeMillis();
            
            // Posiziona il giocatore nella posizione iniziale
            player.setPosition(gameMap.getViewportWidth() / 2, gameMap.getCurrentPosition() + 50);
            
            // Avvia il game loop
            gameLoopManager.startGameLoop();
            
            // Informa la view che il gioco è iniziato
            gameView.showGameScreen();
        }
    }
    
    /**
     * Mette in pausa o riprende il gioco.
     */
    public void togglePause() {
        gamePaused = !gamePaused;
        gameView.setPauseState(gamePaused);
    }
    
    /**
     * Termina il gioco e torna al menu principale.
     */
    public void endGame() {
        if (gameRunning) {
            gameRunning = false;
            gameLoopManager.stopGameLoop();
            
            // Aggiorna il punteggio massimo se necessario
            scoreController.updateHighScore();
            
            // Mostra la schermata di game over
            gameView.showGameOverScreen(scoreController.getScore());
        }
    }
    
    /**
     * Aggiorna lo stato del gioco ad ogni frame.
     */
    private void gameLoopUpdate() {
        try {
            if (gameRunning && !gamePaused) {
                movingObstacleController.update(); //GIULY

                // Aggiorna la mappa
                mapController.update();
                
                // Aggiorna il giocatore
                playerController.update();
                
                // Calcola il punteggio
                scoreController.updateScore();
                
                // Gestisce collisioni e oggetti collezionabili
                if (collisionController.checkCollisions() || movingObstacleController.checkCollision(player.getX(), player.getY())) { //GIULY
                    playerController.die();
                    endGame();
                    return;
                }
                
                // Controlla gli oggetti da raccogliere
                collisionController.checkCollectibles();
                
                // Controlla se il giocatore è fuori dai limiti
                if (collisionController.checkBoundaries()) {
                    playerController.die();
                    endGame();
                    return;
                }
                
                // Aumenta progressivamente la difficoltà
                updateDifficulty();
                
                // Aggiorna la view
                gameView.updateView();
            }
        } catch (Exception e) {
            // Cattura eventuali eccezioni per evitare che il game loop si interrompa
            System.err.println("Errore nel game loop: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Aggiorna la difficoltà del gioco con il passare del tempo.
     */
    private void updateDifficulty() {
        // Calcola il tempo di gioco in secondi
        long currentTime = System.currentTimeMillis();
        long gameTimeSeconds = (currentTime - gameStartTime) / 1000;
        
        // Aumenta la velocità di scorrimento ogni 30 secondi
        if (gameTimeSeconds > 0 && gameTimeSeconds % 30 == 0) {
            // Aumenta la velocità solo se non è già stata aumentata in questo secondo
            if (currentTime - gameStartTime - (gameTimeSeconds * 1000) < gameLoopManager.getFrameTime()) {
                gameMap.increaseScrollSpeed();
                movingObstacleController.increaseDifficulty(1); //GIULY
            }
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
}