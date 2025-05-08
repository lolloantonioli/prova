package it.unibo.model.Map.impl;

import java.util.*;

import it.unibo.model.Map.api.Cell;
import it.unibo.model.Map.api.Chunk;
import it.unibo.model.Map.api.GameObject;
import it.unibo.model.Map.api.PathValidator;

public class PathValidatorImpl implements PathValidator {

    private final int minFreePathWidth; // Larghezza minima del percorso in pixel
    
    /**
     * Costruttore del PathValidator object-oriented.
     * 
     * @param minFreePathWidth Larghezza minima del percorso libero in pixel
     */
    public PathValidatorImpl(final int minFreePathWidth) {
        this.minFreePathWidth = minFreePathWidth;
    }
    
    /**
     * Classe interna per rappresentare la posizione di una cella nella griglia.
     */
    private static class CellPosition {
        private final int row;
        private final int col;
        
        CellPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        @Override
        public boolean equals(Object o) {
            return this == o || (o instanceof CellPosition that && row == that.row && col == that.col);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }
    
    /**
     * Classe per rappresentare il grafo delle celle navigabili.
     */
    private static class NavigationGraph {
        private final Map<CellPosition, List<CellPosition>> adjacencyList;
        private final int rows;
        private final int cols;
        
        /**
         * Costruisce un grafo di navigazione basato sulla griglia di celle.
         * 
         * @param cells Lista di celle del chunk
         * @param cellsPerRow Numero di celle per riga
         */
        NavigationGraph(List<Cell> cells, int cellsPerRow) {
            this.adjacencyList = new HashMap<>();
            this.cols = cellsPerRow;
            this.rows = cells.size() / cellsPerRow;
            
            // Costruisci il grafo di adiacenza
            buildGraph(cells, cellsPerRow);
        }
        
        /**
         * Costruisce il grafo di adiacenza dalle celle.
         */
        private void buildGraph(List<Cell> cells, int cellsPerRow) {
            for (int i = 0; i < cells.size(); i++) {
                int row = i / cellsPerRow;
                int col = i % cellsPerRow;
                CellPosition pos = new CellPosition(row, col);
                
                // Se la cella contiene un ostacolo, non è navigabile
                Cell cell = cells.get(i);
                boolean isNotNavigable = cell.getContent()
                    .map(content -> !content.isPlatform())
                    .orElse(false);
                
                if (isNotNavigable) {
                    continue;
                }
                
                // Aggiungi la cella al grafo
                adjacencyList.put(pos, new ArrayList<>());
                
                // Aggiungi connessioni con celle adiacenti (su, giù, sinistra, destra)
                addEdge(pos, row - 1, col, cells, cellsPerRow); // Su
                addEdge(pos, row + 1, col, cells, cellsPerRow); // Giù
                addEdge(pos, row, col - 1, cells, cellsPerRow); // Sinistra
                addEdge(pos, row, col + 1, cells, cellsPerRow); // Destra
            }
        }
        
        /**
         * Aggiunge una connessione al grafo se la cella adiacente è navigabile.
         */
        private void addEdge(CellPosition from, int toRow, int toCol, List<Cell> cells, int cellsPerRow) {
            // Controlla se la posizione è valida
            if (toRow < 0 || toRow >= rows || toCol < 0 || toCol >= cols) {
                return;
            }
            
            int index = toRow * cellsPerRow + toCol;
            if (index >= 0 && index < cells.size()) {
                Cell adjacentCell = cells.get(index);
                
                // Se la cella adiacente contiene un ostacolo, non è navigabile
                boolean isNotNavigable = adjacentCell.getContent()
                    .map(content -> !content.isPlatform())
                    .orElse(false);
                
                if (isNotNavigable) {
                    return;
                }
                
                CellPosition to = new CellPosition(toRow, toCol);
                List<CellPosition> neighbors = adjacencyList.computeIfAbsent(from, k -> new ArrayList<>());
                neighbors.add(to);
            }
        }
        
        /**
         * Verifica se esiste un percorso tra la prima e l'ultima riga del chunk.
         * 
         * @return true se esiste un percorso, false altrimenti
         */
        public boolean hasPathFromTopToBottom() {
            // Trova tutte le celle navigabili nella prima riga
            List<CellPosition> startPositions = new ArrayList<>();
            for (int col = 0; col < cols; col++) {
                CellPosition startPos = new CellPosition(0, col);
                if (adjacencyList.containsKey(startPos)) {
                    startPositions.add(startPos);
                }
            }
            
            // BFS per trovare un percorso da qualsiasi cella nella prima riga
            // a qualsiasi cella nell'ultima riga
            for (CellPosition start : startPositions) {
                if (bfsHasPath(start)) {
                    return true;
                }
            }
            
            return false;
        }
        
        /**
         * Utilizza BFS per verificare se esiste un percorso dalla posizione di partenza
         * a qualsiasi cella nell'ultima riga.
         * 
         * @param start Posizione di partenza
         * @return true se esiste un percorso, false altrimenti
         */
        private boolean bfsHasPath(CellPosition start) {
            Queue<CellPosition> queue = new LinkedList<>();
            Set<CellPosition> visited = new HashSet<>();
            
            queue.add(start);
            visited.add(start);
            
            while (!queue.isEmpty()) {
                CellPosition current = queue.poll();
                
                // Se siamo arrivati all'ultima riga, abbiamo trovato un percorso
                if (current.row == rows - 1) {
                    return true;
                }
                
                // Esplora tutti i vicini
                List<CellPosition> neighbors = adjacencyList.getOrDefault(current, Collections.emptyList());
                for (CellPosition neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
            
            return false;
        }
    }
    
    @Override
    public void ensureTraversability(final Chunk chunk, final int width) {
        Chunk chunkImpl = chunk;
        List<Cell> cells = chunkImpl.getCells();
        int cellsPerRow = chunkImpl.getCellsPerRow();
        
        // Costruisci un grafo di navigazione
        NavigationGraph navGraph = new NavigationGraph(cells, cellsPerRow);
        
        // Se esiste già un percorso, non è necessario fare nulla
        if (navGraph.hasPathFromTopToBottom()) {
            return;
        }
        
        // In base al tipo di chunk, applica una strategia diversa
        switch (chunkImpl.getType()) {
            case RIVER -> ensureRiverTraversability(chunkImpl);
            default -> ensureStandardTraversability(chunkImpl);
        }
    }
    
    /**
     * Assicura che un chunk standard (non fiume) sia attraversabile.
     * 
     * @param chunk Il chunk da modificare
     */
    private void ensureStandardTraversability(Chunk chunk) {
        // Trova la colonna con meno ostacoli
        int bestCol = findColumnWithLeastObstacles(chunk);
        clearColumnObstacles(chunk, bestCol, minFreePathWidth / chunk.getCellSize());
    }
    
    /**
     * Trova la colonna con il minor numero di ostacoli.
     * 
     * @param chunk Il chunk da analizzare
     * @return L'indice della colonna migliore
     */
    private int findColumnWithLeastObstacles(Chunk chunk) {
        int cellsPerRow = chunk.getCellsPerRow();
        Map<Integer, Integer> obstacleCount = new HashMap<>();

        for (int i = 0; i < chunk.getCells().size(); i++) {
            int col = i % cellsPerRow;
            Cell cell = chunk.getCells().get(i);
            boolean isObstacle = cell.getContent()
                .map(content -> !content.isPlatform())
                .orElse(false);
                
            if (isObstacle) {
                obstacleCount.merge(col, 1, Integer::sum);
            }
        }

        return obstacleCount.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(0);
    }

    private void clearColumnObstacles(Chunk chunk, int col, int requiredFreeCells) {
        int cellsPerRow = chunk.getCellsPerRow();
        List<Cell> cells = chunk.getCells();

        for (int row = 0; row < chunk.getHeight() / chunk.getCellSize(); row++) {
            int index = row * cellsPerRow + col;
            if (index < cells.size()) cells.get(index).removeObject();
        }

        for (int i = 1; i < requiredFreeCells; i++) {
            int adjacentCol = (col + i) % cellsPerRow;
            for (int row = 0; row < chunk.getHeight() / chunk.getCellSize(); row++) {
                int index = row * cellsPerRow + adjacentCol;
                if (index < cells.size()) cells.get(index).removeObject();
            }
        }
    }
    
    /**
     * Assicura che un chunk di fiume sia attraversabile aggiungendo piattaforme.
     * 
     * @param chunk Il chunk di fiume da modificare
     */
    private void ensureRiverTraversability(Chunk chunk) {
        // Rimuovi tutte le piattaforme esistenti
        chunk.getCells().forEach(cell -> {
            boolean isPlatform = cell.getContent()
                .map(GameObject::isPlatform)
                .orElse(false);
                
            if (isPlatform) {
                cell.removeObject();
            }
        });

        int cellSize = chunk.getCellSize();
        int width = chunk.getWidth();
        int height = chunk.getHeight();
        int platformCount = 3 + height / (cellSize * 3);
        int platformSpacing = height / platformCount;
        int platformWidth = Math.max(minFreePathWidth, cellSize * 2);

        for (int i = 0; i < platformCount; i++) {
            int platformY = chunk.getPosition() + i * platformSpacing + platformSpacing / 2;
            int platformX = (i % 2 == 0) ? 0 : (width - platformWidth);

            GameObject platform = new GameObjectImpl(platformX, platformY, platformWidth, cellSize);
            platform.setMovable(true);
            platform.setSpeed((i % 2 == 0) ? 1 : -1);
            platform.setPlatform(true);
            chunk.addObject(platform);
        }
    }
}