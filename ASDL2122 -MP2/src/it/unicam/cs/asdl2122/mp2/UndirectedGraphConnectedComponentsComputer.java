package it.unicam.cs.asdl2122.mp2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Classe singoletto che realizza un calcolatore delle componenti connesse di un
 * grafo non orientato utilizzando una struttura dati efficiente (fornita dalla
 * classe {@ForestDisjointSets<GraphNode<L>>}) per gestire insiemi disgiunti di
 * nodi del grafo che sono, alla fine del calcolo, le componenti connesse.
 * 
 * @author Luca Tesei (template) Antonio Gassner, antoniobimbo.gassner@studenti.unicam.it (implementazione)
 *
 * @param <L>
 *                il tipo delle etichette dei nodi del grafo
 */
public class UndirectedGraphConnectedComponentsComputer<L> {

    /*
     * Struttura dati per gli insiemi disgiunti.
     */
    private ForestDisjointSets<GraphNode<L>> f;

    /**
     * Crea un calcolatore di componenti connesse.
     */
    public UndirectedGraphConnectedComponentsComputer() {
        this.f = new ForestDisjointSets<GraphNode<L>>();
    }

    /**
     * Calcola le componenti connesse di un grafo non orientato utilizzando una
     * collezione di insiemi disgiunti.
     * 
     * @param g
     *              un grafo non orientato
     * @return un insieme di componenti connesse, ognuna rappresentata da un
     *         insieme di nodi del grafo
     * @throws NullPointerException
     *                                      se il grafo passato è nullo
     * @throws IllegalArgumentException
     *                                      se il grafo passato è orientato
     */
    public Set<Set<GraphNode<L>>> computeConnectedComponents(Graph<L> g) {
        if(g == null)
            throw new NullPointerException("Graph is null");
        if(g.isDirected())
            throw new IllegalArgumentException("Graph is oriented");
        cleanup();

        for(GraphNode<L> node : g.getNodes())
            f.makeSet(node);

        Set<GraphEdge<L>> edgeSet = g.getEdges();
        Iterator<GraphEdge<L>> edgeIterator = edgeSet.iterator();
        while(edgeIterator.hasNext()){
            GraphEdge<L> temp = edgeIterator.next();
            // Non vuole entrare in questo if a nessun costo
            // f.findSet(node1) ritorna sempre null nonostante Node1 sia contenuto in f...
            if(f.findSet(temp.getNode1()) != f.findSet(temp.getNode2()))
                f.union(temp.getNode1(), temp.getNode2());
        }

        Set<Set<GraphNode<L>>> connectedComponents = new HashSet<Set<GraphNode<L>>>();

        Set<GraphNode<L>> representatives = f.getCurrentRepresentatives();


        Iterator<GraphNode<L>> repIter = representatives.iterator();
        while(repIter.hasNext()){
            connectedComponents.add(f.getCurrentElementsOfSetContaining(repIter.next()));
        }

        return connectedComponents;
    }

    private void cleanup(){
        f.clear();
    }
}

/*
for (GraphNode<L> node : g.getNodes()) {
            f.makeSet(node);
        }
        Set<GraphEdge<L>> edgeSet = g.getEdges();
        Iterator<GraphEdge<L>> edgeIterator = edgeSet.iterator();

        while(edgeIterator.hasNext()){
            GraphEdge<L> temp = edgeIterator.next();

        }

        Set<Set<GraphNode<L>>> connectedComponents = new HashSet<Set<GraphNode<L>>>();
        Set<GraphNode<L>> representatives = f.getCurrentRepresentatives();

        Iterator<GraphNode<L>> nodeIterator = representatives.iterator();
        while(nodeIterator.hasNext()){
            GraphNode<L> rep = nodeIterator.next();
            connectedComponents.add(f.getCurrentElementsOfSetContaining(rep));
        }
 */
