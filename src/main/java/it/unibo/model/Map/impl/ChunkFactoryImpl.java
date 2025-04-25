package it.unibo.model.Map.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.ChunkFactory;
import it.unibo.model.Map.api.ObjectPlacer;
import it.unibo.model.Map.api.Collectible;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.api.PathValidator;
import it.unibo.model.Map.util.ChunkType;
import it.unibo.model.Map.util.CollectibleType;
import it.unibo.model.Map.util.ObstacleType;

public class ChunkFactoryImpl implements ChunkFactory {
    
    private final Random random;
    private final PathValidator pathValidator;
    private final ObjectPlacer objectPlacer;
    private final int cellSize;

    private static final int MIN_FREE_PATH_WIDTH = 80; // Larghezza minima del percorso libero
    private static final int MAX_OBSTACLES_PER_CHUNK = 5;
    private static final int MAX_COLLECTIBLES_PER_CHUNK = 3;
    
    /**
     * Constructor for the ChunkFactory class.
     */
    public ChunkFactoryImpl(final int cellSize) {
        this.random = new Random();
        this.pathValidator = new PathValidatorImpl(MIN_FREE_PATH_WIDTH);
        this.objectPlacer = new ObjectPlacerImpl();
        this.cellSize = cellSize;
    }
    
    public Chunk createRandomChunk(final int position, final int width) {
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
    
    public Chunk createRoadChunk(final int position, final int width) {
        Chunk chunk = new ChunkImpl(position, width, ChunkType.ROAD, cellSize);
        
        // Aggiungi collezionabili (monete e power-up)
        List<Collectible> collectibles = generateCollectibles(width, position);
        objectPlacer.placeObjectsRandomly(chunk, collectibles, MAX_COLLECTIBLES_PER_CHUNK);
        
        return chunk;
    }
    
    public Chunk createRailwayChunk(final int position, final int width) {
        Chunk chunk = new ChunkImpl(position, width, ChunkType.RAILWAY, cellSize);

        // Aggiungi collezionabili (monete e power-up)
        List<Collectible> collectibles = generateCollectibles(width, position);
        objectPlacer.placeObjectsRandomly(chunk, collectibles, MAX_COLLECTIBLES_PER_CHUNK);
        
        return chunk;
    }
    
    public Chunk createRiverChunk(final int position, final int width) {
        Chunk chunk = new ChunkImpl(position, width, ChunkType.RIVER, cellSize);
        
        // Genera tronchi come piattaforme
        List<GameObject> platforms = generateRiverPlatforms(width, position);
        
        // Posiziona le piattaforme
        objectPlacer.placeObjectsRandomly(chunk, platforms, MAX_OBSTACLES_PER_CHUNK);
        
        // Aggiungi collezionabili (monete e power-up)
        List<Collectible> collectibles = generateCollectibles(width, position);
        objectPlacer.placeObjectsRandomly(chunk, collectibles, MAX_COLLECTIBLES_PER_CHUNK);
        
        return chunk;
    }
    
    
    public Chunk createGrassChunk(final int position, final int width) {
        Chunk chunk = new ChunkImpl(position, width, ChunkType.GRASS, cellSize);
        
        // Genera alberi come ostacoli
        List<GameObject> grassObstacles = generateGrassObstacles(width, position);
        
        // Posiziona gli ostacoli
        objectPlacer.placeObjectsRandomly(chunk, grassObstacles, MAX_OBSTACLES_PER_CHUNK);
        
        // Aggiungi collezionabili (monete e power-up)
        List<Collectible> collectibles = generateCollectibles(width, position);
        objectPlacer.placeObjectsRandomly(chunk, collectibles, MAX_COLLECTIBLES_PER_CHUNK);
        
        return chunk;
    }
    
    private List<GameObject> generateRiverPlatforms(final int width, final int position) {
        return IntStream.range(0, 2 + random.nextInt(3))
            .mapToObj(i -> {
                int logWidth = 80 + random.nextInt(40);
                int logHeight = 30;
                int speed = (random.nextBoolean() ? 1 : -1) * (1 + random.nextInt(2));

                GameObjectImpl log = new GameObjectImpl(
                    random.nextInt(width),
                    position + random.nextInt(ChunkImpl.STANDARD_HEIGHT - logHeight),
                    logWidth,
                    logHeight
                );
                log.setMovable(true);
                log.setSpeed(speed);
                log.setPlatform(true);
                return log;
            })
            .collect(Collectors.toList());
    }
    
    private List<GameObject> generateGrassObstacles(final int width, final int position) {
        return IntStream.range(0, random.nextInt(6))
            .mapToObj(i -> {
                int treeSize = 30 + random.nextInt(20);
                return new ObstacleImpl(
                    random.nextInt(width),
                    position + random.nextInt(ChunkImpl.STANDARD_HEIGHT - treeSize),
                    treeSize,
                    treeSize,
                    ObstacleType.TREE,
                    false
                );
            })
            .collect(Collectors.toList());
    }

    private List<Collectible> generateCollectibles(final int width, final int position) {
        int numCollectibles = random.nextInt(4); // 0-3 collezionabili
        List<CollectibleType> types = new ArrayList<>(
            List.of(CollectibleType.COIN, CollectibleType.COIN,CollectibleType.COIN, CollectibleType.INVINCIBILITY));
        
        return IntStream.range(0, numCollectibles)
            .mapToObj(i -> new CollectibleImpl(
                random.nextInt(width),
                position + random.nextInt(ChunkImpl.STANDARD_HEIGHT),
                types.get(random.nextInt(types.size()))
            ))
            .collect(Collectors.toList());
    }
}
