package it.unibo.model.Map.api;

import java.util.List;

public interface ObjectPlacer {

    /**
     * Posiziona un oggetto in modo randomico in un chunk.
     * 
     * @param chunk Chunk in cui posizionare l'oggetto
     * @param object Oggetto da posizionare
     * @return true se l'oggetto è stato posizionato con successo, false altrimenti
     */
    boolean placeObjectRandomly(final Chunk chunk, final GameObject object);

    /**
     * Posiziona più oggetti in modo randomico nel chunk.
     * 
     * @param chunk Chunk in cui posizionare gli oggetti
     * @param objects Oggetti da posizionare
     * @param maxObjects Numero massimo di oggetti da posizionare
     */
    void placeObjectsRandomly(final Chunk chunk, final List<? extends GameObject> objects, final int maxObjects);

}
