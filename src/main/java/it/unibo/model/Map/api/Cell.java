package it.unibo.model.Map.api;

public interface Cell {

    /**
     * Aggiunge un oggetto alla cella, se la cella è vuota.
     * 
     * @param obj Oggetto da aggiungere
     * @return true se l'oggetto è stato aggiunto, false altrimenti
     */
    public boolean addObject(GameObject obj);

    /**
     * Rimuove l'oggetto dalla cella.
     */
    public void removeObject();

    /**
     * Verifica se la cella contiene un oggetto.
     * 
     * @return true se la cella contiene un oggetto, false altrimenti
     */
    public boolean hasObject();

    /**
     * Ottiene l'oggetto contenuto nella cella.
     * 
     * @return L'oggetto contenuto nella cella, o null se la cella è vuota
     */
    public GameObject getContent();
    
    public int getX();
    
    public int getY();
    
    public int getSize();
    
    public int getScreenX();
    
    public int getScreenY();

}
