package it.unibo.controller;

import java.util.List;

import it.unibo.model.Map.Obstacles.api.MovingObstacleFactory;
import it.unibo.model.Map.Obstacles.api.MovingObstacleManager;
import it.unibo.model.Map.Obstacles.impl.MovingObstacleFactoryImpl;
import it.unibo.model.Map.Obstacles.impl.MovingObstacleManagerImpl;
import it.unibo.model.Map.Obstacles.impl.MovingObstacles;
import it.unibo.model.Map.api.GameMap;
import it.unibo.model.Map.util.ObstacleType;

/**
 * Controller dedicato alla gestione degli ostacoli mobili.
 * Gestisce la creazione, posizionamento, e comportamento di tutti gli ostacoli mobili.
 */
public class MovingObstacleController {
    
    private final GameMap gameMap;
    private final MovingObstacleFactory obstacleFactory;
    private final MovingObstacleManager obstacleManager;
    
    private static final int MIN_DISTANCE_CARS = 100;
    private static final int MIN_DISTANCE_TRAINS = 300;
    
    /**
     * Costruttore per MovingObstacleController.
     * 
     * @param gameMap La mappa di gioco
     */
    public MovingObstacleController(GameMap gameMap) {
        this.gameMap = gameMap;
        this.obstacleFactory = new MovingObstacleFactoryImpl();
        
        // Recupera il manager esistente dalla mappa per evitare duplicazioni
        if (gameMap instanceof it.unibo.model.Map.impl.GameMapImpl gameMapImpl) {
            this.obstacleManager = gameMapImpl.getObstacleManager();
        } else {
            // Crea un nuovo manager se necessario
            this.obstacleManager = new MovingObstacleManagerImpl();
        }
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
     * Aumenta la difficoltà incrementando la velocità degli ostacoli.
     * 
     * @param factor Fattore di incremento della velocità
     */
    public void increaseDifficulty(int factor) {
        obstacleManager.increaseSpeed(factor);
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
     * Reimposta tutti gli ostacoli alla posizione iniziale.
     * Utile quando il gioco viene riavviato.
     */
    public void resetObstacles() {
        obstacleManager.resetAll();
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
}