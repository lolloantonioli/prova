package it.unibo.model.Map.Obstacles.impl;

import java.util.Random;

import it.unibo.model.Map.Obstacles.api.MovingObstacleFactory;
import it.unibo.model.Map.util.ObstacleType;

/**
 * Implementation of the MovingObstacleFactory interface.
 * Handles creation of different types of moving obstacles.
 */
public class MovingObstacleFactoryImpl implements MovingObstacleFactory {
    
    private final Random random;
    
    // Costanti per i limiti di velocità
    public static final int MIN_CAR_SPEED = 1;
    public static final int MAX_CAR_SPEED = 3;
    public static final int MIN_TRAIN_SPEED = 2;
    public static final int MAX_TRAIN_SPEED = 5;
    
    public MovingObstacleFactoryImpl() {
        this.random = new Random();
    }
    
    @Override
    public MovingObstacles createCar(int x, int y, int speed) {
        return new MovingObstacles(x, y, ObstacleType.CAR, speed);
    }
    
    @Override
    public MovingObstacles createTrain(int x, int y, int speed) {
        return new MovingObstacles(x, y, ObstacleType.TRAIN, speed);
    }
    
    @Override
    public MovingObstacles createRandomCar(int y, int mapWidth, boolean leftToRight) {
        int speed = MIN_CAR_SPEED + random.nextInt(MAX_CAR_SPEED - MIN_CAR_SPEED + 1);
        if (!leftToRight) {
            speed = -speed;
        }
        
        // Posiziona l'auto fuori dallo schermo se si muove da sinistra a destra,
        // o vicino al bordo destro se si muove da destra a sinistra
        int x = leftToRight ? -50 - random.nextInt(100) : mapWidth + random.nextInt(100);
        
        return createCar(x, y, speed);
    }
    
    @Override
    public MovingObstacles createRandomTrain(int y, int mapWidth, boolean leftToRight) {
        int speed = MIN_TRAIN_SPEED + random.nextInt(MAX_TRAIN_SPEED - MIN_TRAIN_SPEED + 1);
        if (!leftToRight) {
            speed = -speed;
        }
        
        // Posiziona il treno fuori dallo schermo
        int x = leftToRight ? -200 - random.nextInt(200) : mapWidth + random.nextInt(200);
        
        return createTrain(x, y, speed);
    }
    
    @Override
    public MovingObstacles[] createCarSet(int y, int mapWidth, int count, int minDistance, boolean leftToRight) {
        MovingObstacles[] cars = new MovingObstacles[count];
        
        // Calcoliamo la spaziatura totale disponibile
        int carWidth = 50; // Larghezza standard di un'auto
        int totalSpace = mapWidth + 2 * carWidth; // Spazio totale includendo aree fuori schermo
        int spacing = totalSpace / count; // Spaziatura uniforme
        
        // Assicuriamo una spaziatura minima
        if (spacing < minDistance) {
            count = totalSpace / minDistance;
            spacing = totalSpace / count;
        }
        
        for (int i = 0; i < count; i++) {
            // Aggiungiamo un po' di randomizzazione alla posizione
            int randomOffset = random.nextInt(spacing / 2) - spacing / 4;
            int x = i * spacing + randomOffset;
            
            // Se si muovono da destra a sinistra, invertiamo il posizionamento
            if (!leftToRight) {
                x = mapWidth - x;
            }
            
            // Randomizziamo un po' la velocità di ogni auto
            int speedVariation = random.nextInt(2); // 0 o 1
            int speed = MIN_CAR_SPEED + random.nextInt(MAX_CAR_SPEED - MIN_CAR_SPEED + 1) + speedVariation;
            if (!leftToRight) {
                speed = -speed;
            }
            
            cars[i] = createCar(x, y, speed);
        }
        
        return cars;
    }
    
    @Override
    public MovingObstacles[] createTrainSet(int y, int mapWidth, int count, int minDistance, boolean leftToRight) {
        MovingObstacles[] trains = new MovingObstacles[count];
        
        // I treni sono più grandi e richiedono più spazio
        int trainWidth = 200; // Larghezza standard di un treno
        int totalSpace = mapWidth + 2 * trainWidth;
        int spacing = totalSpace / count;
        
        // Assicuriamo una spaziatura minima
        if (spacing < minDistance) {
            count = totalSpace / minDistance;
            spacing = totalSpace / count;
        }
        
        for (int i = 0; i < count; i++) {
            // I treni richiedono una spaziatura più precisa, meno randomizzazione
            int randomOffset = random.nextInt(spacing / 3) - spacing / 6;
            int x = i * spacing + randomOffset;
            
            // Se si muovono da destra a sinistra, invertiamo il posizionamento
            if (!leftToRight) {
                x = mapWidth - x;
            }
            
            int speedVariation = random.nextInt(2);
            int speed = MIN_TRAIN_SPEED + random.nextInt(MAX_TRAIN_SPEED - MIN_TRAIN_SPEED + 1) + speedVariation;
            if (!leftToRight) {
                speed = -speed;
            }
            
            trains[i] = createTrain(x, y, speed);
        }
        
        return trains;
    }
}