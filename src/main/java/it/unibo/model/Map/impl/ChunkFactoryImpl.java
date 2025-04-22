package it.unibo.model.Map.impl;

import java.util.Random;

import it.unibo.model.Map.Obstacles.api.MovingObstacleFactory;
import it.unibo.model.Map.Obstacles.impl.MovingObstacleFactoryImpl;
import it.unibo.model.Map.Obstacles.impl.MovingObstacles;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.ChunkFactory;
import it.unibo.model.Map.api.Collectible;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.api.Obstacle;
import it.unibo.model.Map.api.PathValidator;
import it.unibo.model.Map.util.ChunkType;
import it.unibo.model.Map.util.CollectibleType;
import it.unibo.model.Map.util.ObstacleType;

public class ChunkFactoryImpl implements ChunkFactory {
    
    private final Random random;
    private final PathValidator pathValidator;
    //private static final int CHUNK_WIDTH = 400;
    //private static final int CHUNK_HEIGHT = 120;
    private static final int MIN_FREE_PATH_WIDTH = 80; // Larghezza minima del percorso libero
    private final MovingObstacleFactory obstacleFactory;
    
    /**
     * Constructor for the ChunkFactory class.
     */
    public ChunkFactoryImpl() {
        this.random = new Random();
        this.pathValidator = new PathValidatorImpl(MIN_FREE_PATH_WIDTH);
        this.obstacleFactory = new MovingObstacleFactoryImpl();
    }
    
    @Override
    public Chunk createRandomChunk(int position, int width) {
        int type = random.nextInt(4);
        Chunk chunk = switch (type) {
            case 0 -> createRoadChunk(position, width);
            case 1 -> createRailwayChunk(position, width);
            case 2 -> createRiverChunk(position, width);
            default -> createGrassChunk(position, width);
        };

        // Assicura sempre un percorso libero
        pathValidator.ensureTraversability(chunk, width);
        
        return chunk;
    }
    
    @Override
    public Chunk createRoadChunk(int position, int width) {
        Chunk chunk = new ChunkImpl(position, width, ChunkType.ROAD);
        
        // Determina la direzione delle auto
        boolean leftToRight = random.nextBoolean();
        
        // Crea le auto con la factory
        int carCount = 2 + random.nextInt(3); // 2-4 auto
        int minDistance = 100; // Distanza minima tra auto
        MovingObstacles[] cars = obstacleFactory.createCarSet(
            position + ChunkImpl.STANDARD_HEIGHT / 2, // Y centrato nel chunk
            width,
            carCount,
            minDistance,
            leftToRight
        );
        
        // Aggiungi le auto al chunk come GameObject
        for (MovingObstacles car : cars) {
            chunk.addObject(car);
        }
        
        // Randomly add a coin (30% chance)
        if (random.nextInt(10) < 3) {
            Collectible coin = new CollectibleImpl(
                random.nextInt(width), 
                position + random.nextInt(ChunkImpl.STANDARD_HEIGHT), 
                CollectibleType.COIN
            );
            chunk.addObject(coin);
        }
        
        return chunk;
    }
    
    @Override
    public Chunk createRailwayChunk(int position, int width) {
        Chunk chunk = new ChunkImpl(position, width, ChunkType.RAILWAY);
        
        // Determina la direzione dei treni
        boolean leftToRight = random.nextBoolean();
        
        // Crea i treni con la factory
        int trainCount = 1 + random.nextInt(2); // 1-2 treni (sono più grandi)
        int minDistance = 300; // Distanza minima tra treni
        MovingObstacles[] trains = obstacleFactory.createTrainSet(
            position + ChunkImpl.STANDARD_HEIGHT / 2, // Y centrato nel chunk
            width,
            trainCount,
            minDistance,
            leftToRight
        );
        
        // Aggiungi i treni al chunk come GameObject
        for (MovingObstacles train : trains) {
            chunk.addObject(train);
        }
        
        // Rarely add a power-up (10% chance)
        if (random.nextInt(10) < 1) {
            CollectibleType[] powerUps = {
                CollectibleType.INVINCIBILITY
            };
            Collectible powerUp = new CollectibleImpl(
                random.nextInt(width), 
                position + random.nextInt(ChunkImpl.STANDARD_HEIGHT), 
                powerUps[random.nextInt(powerUps.length)]
            );
            chunk.addObject(powerUp);
        }
        
        return chunk;
    }
    
    @Override
    public Chunk createRiverChunk(int position, int width) {
        Chunk chunk = new ChunkImpl(position, width, ChunkType.RIVER);
        
        // Add water as a continuous obstacle
        Obstacle water = new ObstacleImpl(
            0, 
            position, 
            width, 
            ChunkImpl.STANDARD_HEIGHT, 
            ObstacleType.WATER, 
            false
        );
        chunk.addObject(water);
        
        // Add log platforms (2-4)
        int logCount = 2 + random.nextInt(3);
        boolean rightToLeft = random.nextBoolean();
        int speed = 1 + random.nextInt(2); // Logs are slow: 1-2
        
        for (int i = 0; i < logCount; i++) {
            int logX = random.nextInt(width);
            int logWidth = 80 + random.nextInt(40); // Logs of varying sizes
            int logHeight = 30;
            
            // Logs are safe platforms, not obstacles
            GameObjectImpl log = new GameObjectImpl(
                logX, 
                position + (ChunkImpl.STANDARD_HEIGHT / (logCount + 1)) * (i + 1) - logHeight / 2, 
                logWidth, 
                logHeight
            );
            log.setMovable(true);
            log.setSpeed(rightToLeft ? -speed : speed);
            log.setPlatform(true); // Logs are platforms player can stand on
            chunk.addObject(log);
        }
        
        // Add a coin on one of the logs (50% chance)
        if (random.nextBoolean() && !chunk.getObjects().isEmpty()) {
            // Find a log to place the coin on
            GameObject log = null;
            for (var obj : chunk.getObjects()) {
                if (obj.isPlatform()) {
                    log = obj;
                    break;
                }
            }
            
            if (log != null) {
                Collectible coin = new CollectibleImpl(
                    log.getX() + log.getWidth() / 2, 
                    log.getY() - 15, // Just above the log
                    CollectibleType.COIN
                );
                chunk.addObject(coin);
            }
        }
        
        return chunk;
    }
    
    @Override
    public Chunk createGrassChunk(int position, int width) {
        Chunk chunk = new ChunkImpl(position, width, ChunkType.GRASS);
        
        // Add 0-5 trees as obstacles
        int treeCount = random.nextInt(6);
        
        for (int i = 0; i < treeCount; i++) {
            int treeX = random.nextInt(width);
            int treeSize = 30 + random.nextInt(20); // Trees of varying sizes
            
            Obstacle tree = new ObstacleImpl(
                treeX, 
                position + random.nextInt(ChunkImpl.STANDARD_HEIGHT - treeSize), 
                treeSize, 
                treeSize, 
                ObstacleType.TREE, 
                false
            );
            chunk.addObject(tree);
        }
        
        // Higher chance for coins in grass (40%)
        if (random.nextInt(10) < 4) {
            Collectible coin = new CollectibleImpl(
                random.nextInt(width), 
                position + random.nextInt(ChunkImpl.STANDARD_HEIGHT), 
                CollectibleType.COIN
            );
            chunk.addObject(coin);
        }
        
        return chunk;
    }
}
