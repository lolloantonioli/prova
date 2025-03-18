package it.unibo.model.Map.api;

public interface PathValidator {

    void ensureTraversability(Chunk chunk, int width);

}
