package it.unibo.model.Map.api;

public interface ChunkFactory {

    /**
     * Creates a random chunk.
     * 
     * @param position Position of the chunk
     * @param width Width of the chunk
     * @return A randomly generated chunk
     */
    Chunk createRandomChunk(int position, int width);

    /**
     * Creates a road chunk with cars.
     * 
     * @param position Position of the chunk
     * @param width Width of the chunk
     * @return A road chunk
     */
    Chunk createRoadChunk(int position, int width);

    /**
     * Creates a railway chunk with trains.
     * 
     * @param position Position of the chunk
     * @param width Width of the chunk
     * @return A railway chunk
     */
    Chunk createRailwayChunk(int position, int width);

    /**
     * Creates a river chunk with water obstacles.
     * 
     * @param position Position of the chunk
     * @param width Width of the chunk
     * @return A river chunk
     */
    Chunk createRiverChunk(int position, int width);

    /**
     * Creates a grass chunk with tree obstacles.
     * 
     * @param position Position of the chunk
     * @param width Width of the chunk
     * @return A grass chunk
     */
    Chunk createGrassChunk(int position, int width);

}
