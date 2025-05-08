package it.unibo.controller;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.unibo.model.Map.Obstacles.api.MovingObstacleFactory;
import it.unibo.model.Map.Obstacles.api.MovingObstacleManager;
import it.unibo.model.Map.Obstacles.impl.MovingObstacleFactoryImpl;
import it.unibo.model.Map.Obstacles.impl.MovingObstacleManagerImpl;
import it.unibo.model.Map.Obstacles.impl.MovingObstacles;
import it.unibo.model.Map.api.GameMap;
import it.unibo.model.Map.util.ObstacleType;

/**
 * Controller dedicato alla gestione degli ostacoli mobili.
 * Gestisce la creazione, posizionamento, comportamento e spawn automatico di tutti gli ostacoli mobili.
 */
public class MovingObstacleController {
    
    private final GameMap gameMap;
    private final MovingObstacleFactory obstacleFactory;
    private final MovingObstacleManager obstacleManager;
    private final Random random;
    
    // Parametri di configurazione
    private static final int MIN_DISTANCE_CARS = 100;
    private static final int MIN_DISTANCE_TRAINS = 300;
    private static final int CAR_SPAWN_INTERVAL_MS = 5000; // 5 secondi
    private static final int TRAIN_SPAWN_INTERVAL_MS = 15000; // 15 secondi
    
    // Generazione automatica di ostacoli
    private ScheduledExecutorService obstacleSpawner;
    
    // Difficoltà
    private int currentDifficultyLevel = 1;
    private int obstacleSpawnRate = 1;
    
    /**
     * Costruttore per MovingObstacleController.
     * 
     * @param gameMap La mappa di gioco
     */
    public MovingObstacleController(GameMap gameMap) {
        this.gameMap = gameMap;
        this.obstacleFactory = new MovingObstacleFactoryImpl();
        this.random = new Random();
        
        // Recupera il manager esistente dalla mappa per evitare duplicazioni
        if (gameMap instanceof it.unibo.model.Map.impl.GameMapImpl gameMapImpl) {
            this.obstacleManager = gameMapImpl.getObstacleManager();
        } else {
            // Crea un nuovo manager se necessario
            this.obstacleManager = new MovingObstacleManagerImpl();
        }
    }
    
    /**
     * Inizializza e avvia la generazione automatica di ostacoli.
     * Da chiamare quando il gioco inizia.
     */
    public void startObstacleGeneration() {
        if (obstacleSpawner != null && !obstacleSpawner.isShutdown()) {
            obstacleSpawner.shutdown();
        }
        
        obstacleSpawner = Executors.newScheduledThreadPool(2);
        
        // Pianifica la generazione periodica di auto
        obstacleSpawner.scheduleAtFixedRate(
            this::spawnRandomCars,
            2000, // Ritardo iniziale
            CAR_SPAWN_INTERVAL_MS / obstacleSpawnRate,
            TimeUnit.MILLISECONDS
        );
        
        // Pianifica la generazione periodica di treni
        obstacleSpawner.scheduleAtFixedRate(
            this::spawnRandomTrains,
            5000, // Ritardo iniziale
            TRAIN_SPAWN_INTERVAL_MS / obstacleSpawnRate, 
            TimeUnit.MILLISECONDS
        );
    }
    
    /**
     * Ferma la generazione automatica di ostacoli.
     * Da chiamare quando il gioco viene messo in pausa o terminato.
     */
    public void stopObstacleGeneration() {
        if (obstacleSpawner != null && !obstacleSpawner.isShutdown()) {
            obstacleSpawner.shutdown();
            try {
                obstacleSpawner.awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Genera auto casuali sulla mappa.
     * Chiamato automaticamente dall'obstacleSpawner.
     */
    private void spawnRandomCars() {
        // Trova le posizioni Y delle strade nella mappa visibile
        List<Integer> roadPositions = findRoadPositions();
        
        if (!roadPositions.isEmpty()) {
            // Scegli una strada casuale
            int roadY = roadPositions.get(random.nextInt(roadPositions.size()));
            
            // Il numero di auto dipende dal livello di difficoltà
            int carCount = Math.min(1 + currentDifficultyLevel / 2, 3);
            boolean leftToRight = random.nextBoolean();
            
            // Crea le auto
            createCarSet(roadY, carCount, leftToRight);
        }
    }
    
    /**
     * Genera treni casuali sulla mappa.
     * Chiamato automaticamente dall'obstacleSpawner.
     */
    private void spawnRandomTrains() {
        // Trova le posizioni Y delle ferrovie nella mappa visibile
        List<Integer> railwayPositions = findRailwayPositions();
        
        if (!railwayPositions.isEmpty()) {
            // Scegli una ferrovia casuale
            int railwayY = railwayPositions.get(random.nextInt(railwayPositions.size()));
            
            // I treni sono più pericolosi, quindi ne generiamo meno
            int trainCount = Math.min(1 + currentDifficultyLevel / 3, 2);
            boolean leftToRight = random.nextBoolean();
            
            // Crea i treni
            createTrainSet(railwayY, trainCount, leftToRight);
        }
    }
    
    /**
     * Trova le posizioni Y delle strade nella mappa visibile.
     * 
     * @return Lista di coordinate Y delle strade
     */
    private List<Integer> findRoadPositions() {
        // Implementazione semplificata - in un'implementazione reale, 
        // bisognerebbe analizzare i chunk della mappa per trovare le strade
        
        // Assumiamo alcune posizioni predefinite per esempio
        return List.of(
            gameMap.getCurrentPosition() + 200,
            gameMap.getCurrentPosition() + 400,
            gameMap.getCurrentPosition() + 600
        );
    }
    
    /**
     * Trova le posizioni Y delle ferrovie nella mappa visibile.
     * 
     * @return Lista di coordinate Y delle ferrovie
     */
    private List<Integer> findRailwayPositions() {
        // Implementazione semplificata
        
        // Assumiamo alcune posizioni predefinite per esempio
        return List.of(
            gameMap.getCurrentPosition() + 300,
            gameMap.getCurrentPosition() + 500
        );
    }
    
    /**
     * Crea e aggiunge un nuovo ostacolo mobile alla mappa.
     * 
     * @param type Tipo di ostacolo (CAR, TRAIN)
     * @param x Posizione X
     * @param y Posizione Y
     * @param speed Velocità (positiva = destra, negativa = sinistra)
     * @return L'ostacolo creato
     */
    public MovingObstacles createObstacle(ObstacleType type, int x, int y, int speed) {
        MovingObstacles obstacle = null;
        
        switch (type) {
            case CAR -> obstacle = obstacleFactory.createCar(x, y, speed);
            case TRAIN -> obstacle = obstacleFactory.createTrain(x, y, speed);
            default -> throw new IllegalArgumentException("Tipo di ostacolo non supportato: " + type);
        }
        
        if (obstacle != null) {
            obstacleManager.addObstacle(obstacle);
        }
        
        return obstacle;
    }
    
    /**
     * Crea un gruppo di auto sulla strada.
     * 
     * @param y Posizione Y della strada
     * @param count Numero di auto da creare
     * @param leftToRight Direzione di movimento
     * @return Array di ostacoli creati
     */
    public MovingObstacles[] createCarSet(int y, int count, boolean leftToRight) {
        MovingObstacles[] cars = obstacleFactory.createCarSet(
            y, 
            gameMap.getViewportWidth(),
            count,
            MIN_DISTANCE_CARS,
            leftToRight
        );
        
        obstacleManager.addObstacles(cars);
        return cars;
    }
    
    /**
     * Crea un gruppo di treni sulla ferrovia.
     * 
     * @param y Posizione Y della ferrovia
     * @param count Numero di treni da creare
     * @param leftToRight Direzione di movimento
     * @return Array di ostacoli creati
     */
    public MovingObstacles[] createTrainSet(int y, int count, boolean leftToRight) {
        MovingObstacles[] trains = obstacleFactory.createTrainSet(
            y, 
            gameMap.getViewportWidth(),
            count,
            MIN_DISTANCE_TRAINS,
            leftToRight
        );
        
        obstacleManager.addObstacles(trains);
        return trains;
    }
    
    /**
     * Aggiorna tutti gli ostacoli mobili.
     * Da chiamare ad ogni ciclo di gioco.
     */
    public void update() {
        obstacleManager.updateAll(gameMap.getViewportWidth());
        cleanupOffscreenObstacles();
    }
    
    /**
     * Rimuove gli ostacoli che non sono più visibili.
     */
    private void cleanupOffscreenObstacles() {
        obstacleManager.cleanupOffscreenObstacles(
            gameMap.getCurrentPosition() - 200, 
            gameMap.getCurrentPosition() + gameMap.getViewportHeight() + 200
        );
    }
    
    /**
     * Aumenta la difficoltà incrementando la velocità degli ostacoli e
     * la frequenza di generazione.
     * 
     * @param factor Fattore di incremento della difficoltà
     */
    public void increaseDifficulty(int factor) {
        currentDifficultyLevel += factor;
        
        // Aumenta la velocità degli ostacoli esistenti
        obstacleManager.increaseSpeed(factor);
        
        // Aumenta la frequenza di spawn
        obstacleSpawnRate = Math.min(obstacleSpawnRate + 1, 5);
        
        // Riavvia la generazione di ostacoli con la nuova frequenza
        if (obstacleSpawner != null && !obstacleSpawner.isShutdown()) {
            stopObstacleGeneration();
            startObstacleGeneration();
        }
    }
    
    /**
     * Verifica se c'è una collisione con un ostacolo mobile.
     * 
     * @param x Coordinata X
     * @param y Coordinata Y
     * @return true se c'è una collisione
     */
    public boolean checkCollision(int x, int y) {
        return obstacleManager.checkCollision(x, y);
    }
    
    /**
     * Ottiene tutti gli ostacoli attivi di un determinato tipo.
     * 
     * @param type Tipo di ostacolo
     * @return Lista di ostacoli del tipo specificato
     */
    public List<MovingObstacles> getObstaclesByType(ObstacleType type) {
        return obstacleManager.getObstaclesByType(type.toString());
    }
    
    /**
     * Ottiene tutti gli ostacoli attivi.
     * 
     * @return Lista di tutti gli ostacoli
     */
    public List<MovingObstacles> getAllObstacles() {
        return obstacleManager.getActiveObstacles();
    }
    
    /**
     * Reimposta tutti gli ostacoli e riavvia la generazione.
     * Utile quando il gioco viene riavviato.
     */
    public void resetObstacles() {
        // Ferma la generazione automatica
        stopObstacleGeneration();
        
        // Reimposta gli ostacoli esistenti
        obstacleManager.resetAll();
        
        // Reimposta la difficoltà
        currentDifficultyLevel = 1;
        obstacleSpawnRate = 1;
        
        // Riavvia la generazione automatica
        startObstacleGeneration();
    }
    
    /**
     * Sospende temporaneamente la generazione di ostacoli.
     * Utile quando il gioco viene messo in pausa.
     */
    public void pauseObstacleGeneration() {
        stopObstacleGeneration();
    }
    
    /**
     * Riprende la generazione di ostacoli.
     * Utile quando il gioco viene ripreso dopo una pausa.
     */
    public void resumeObstacleGeneration() {
        startObstacleGeneration();
    }
    
    /**
     * Crea un ostacolo casuale di un tipo specifico in una posizione casuale.
     * 
     * @param type Tipo di ostacolo
     * @param y Posizione Y approssimativa
     * @return L'ostacolo creato
     */
    public MovingObstacles createRandomObstacle(ObstacleType type, int y) {
        MovingObstacles obstacle = null;
        boolean leftToRight = Math.random() > 0.5;
        
        switch (type) {
            case CAR -> obstacle = obstacleFactory.createRandomCar(y, gameMap.getViewportWidth(), leftToRight);
            case TRAIN -> obstacle = obstacleFactory.createRandomTrain(y, gameMap.getViewportWidth(), leftToRight);
            default -> throw new IllegalArgumentException("Tipo di ostacolo non supportato: " + type);
        }
        
        if (obstacle != null) {
            obstacleManager.addObstacle(obstacle);
        }
        
        return obstacle;
    }
    
    /**
     * Ottiene il livello di difficoltà corrente.
     * 
     * @return Il livello di difficoltà
     */
    public int getCurrentDifficultyLevel() {
        return currentDifficultyLevel;
    }
    
    /**
     * Rilascia le risorse utilizzate dal controller.
     * Da chiamare quando il gioco viene terminato.
     */
    public void dispose() {
        stopObstacleGeneration();
    }
}