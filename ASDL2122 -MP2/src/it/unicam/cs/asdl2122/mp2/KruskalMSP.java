package it.unicam.cs.asdl2122.mp2;

import java.util.*;

/**
 * Classe singoletto che implementa l'algoritmo di Kruskal per trovare un
 * Minimum Spanning Tree di un grafo non orientato, pesato e con pesi non
 * negativi. L'algoritmo implementato si avvale della classe
 * {@code ForestDisjointSets<GraphNode<L>>} per gestire una collezione di
 * insiemi disgiunti di nodi del grafo.
 *
 * @param <L> tipo delle etichette dei nodi del grafo
 * @author Luca Tesei (template) Antonio Gassner, antoniobimbo.gassner@studenti.unicam.it (implementazione)
 */
public class KruskalMSP<L> {

    /**
     * Struttura dati per rappresentare gli insiemi disgiunti utilizzata
     * dall'algoritmo di Kruskal.
     */
    private ForestDisjointSets<GraphNode<L>> disjointSets;

    /**
     * Costruisce un calcolatore di un albero di copertura minimo che usa
     * l'algoritmo di Kruskal su un grafo non orientato e pesato.
     */
    public KruskalMSP() {
        this.disjointSets = new ForestDisjointSets<GraphNode<L>>();
    }

    /**
     * Utilizza l'algoritmo goloso di Kruskal per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. L'albero restituito non è radicato, quindi è rappresentato
     * semplicemente con un sottoinsieme degli archi del grafo.
     *
     * @param g un grafo non orientato, pesato, con pesi non negativi
     * @return l'insieme degli archi del grafo g che costituiscono l'albero di
     * copertura minimo trovato
     * @throws NullPointerException     se il grafo g è null
     * @throws IllegalArgumentException se il grafo g è orientato, non pesato o
     *                                  con pesi negativi
     */
    public Set<GraphEdge<L>> computeMSP(Graph<L> g) {
        if (g == null)
            throw new NullPointerException("Graph is null");
        if (g.isDirected())
            throw new IllegalArgumentException("Directed graph");
        if (!checkWeights(g))
            throw new IllegalArgumentException("Graph is not weighted or has negative weights");

        cleanup();

        Set<GraphEdge<L>> mst = new HashSet<GraphEdge<L>>();
        for (GraphNode<L> node : g.getNodes()) {
            disjointSets.makeSet(node);
        }
        ArrayList<GraphEdge<L>> orderedEdges = orderEdgesByWeight(g.getEdges());

        while(!orderedEdges.isEmpty()){
            GraphEdge<L> temp = orderedEdges.get(0);
            GraphNode<L> rep1 = disjointSets.findSet(temp.getNode1());
            GraphNode<L> rep2 = disjointSets.findSet(temp.getNode2());
            if(!rep1.equals(rep2)) {
                mst.add(temp);
                disjointSets.union(rep1, rep2);
            }
            orderedEdges.remove(0);
        }
        return mst;
    }

    private ArrayList<GraphEdge<L>> orderEdgesByWeight(Set<GraphEdge<L>> edgeSet) {
        ArrayList<GraphEdge<L>> orderedEdges = new ArrayList<GraphEdge<L>>(edgeSet);
        int n = orderedEdges.size();
        for (int i = 1; i < n; i++) {
            GraphEdge<L> key = orderedEdges.get(i);
            int j = i - 1;
            while (j >= 0 && orderedEdges.get(j).getWeight() > key.getWeight()) {
                orderedEdges.set(j + 1, orderedEdges.get(j));
                j--;
            }
            orderedEdges.set(j + 1, key);
        }
        return orderedEdges;
    }

    //helps with exception throwing, checks the graph for negative or NaN weights
    private boolean checkWeights(Graph<L> g) {
        Set<GraphEdge<L>> set = g.getEdges();
        for (GraphEdge<L> edge : set) {
            if (!edge.hasWeight())
                return false;
            if (edge.getWeight() < 0)
                return false;
        }
        return true;
    }

    private void cleanup(){
        disjointSets.clear();
    }
}
