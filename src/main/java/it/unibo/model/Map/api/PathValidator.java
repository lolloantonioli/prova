package it.unibo.model.Map.api;

public interface PathValidator {

    void ensureTraversability(final Chunk chunk, final int width);

}
