package it.unicam.cs.asdl2122.mp2;

import java.util.*;

/**
 * Classe singoletto che implementa l'algoritmo di Prim per trovare un Minimum
 * Spanning Tree di un grafo non orientato, pesato e con pesi non negativi.
 * 
 * L'algoritmo richiede l'uso di una coda di min priorità tra i nodi che può
 * essere realizzata con una semplice ArrayList (non c'è bisogno di ottimizzare
 * le operazioni di inserimento, di estrazione del minimo, o di decremento della
 * priorità).
 * 
 * Si possono usare i colori dei nodi per registrare la scoperta e la visita
 * effettuata dei nodi.
 * 
 * @author Luca Tesei (template) Antonio Gassner, antoniobimbo.gassner@studenti.unicam.it (implementazione)
 * 
 * @param <L>
 *                tipo delle etichette dei nodi del grafo
 *
 */
public class PrimMSP<L> {

    ArrayList<GraphEdge<L>> outgoingEdges;
    Set<GraphNode<L>> blackNodes;

    /*
     * In particolare: si deve usare una coda con priorità che può semplicemente
     * essere realizzata con una List<GraphNode<L>> e si deve mantenere un
     * insieme dei nodi già visitati
     */

    /**
     * Crea un nuovo algoritmo e inizializza la coda di priorità con una coda
     * vuota.
     */
    public PrimMSP() {
        this.outgoingEdges = new ArrayList<GraphEdge<L>>();
        this.blackNodes = new HashSet<GraphNode<L>>();
    }

    /**
     * Utilizza l'algoritmo goloso di Prim per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. Dopo l'esecuzione del metodo nei nodi del grafo il campo
     * previous deve contenere un puntatore a un nodo in accordo all'albero di
     * copertura minimo calcolato, la cui radice è il nodo sorgente passato.
     * 
     * @param g
     *              un grafo non orientato, pesato, con pesi non negativi
     * @param s
     *              il nodo del grafo g sorgente, cioè da cui parte il calcolo
     *              dell'albero di copertura minimo. Tale nodo sarà la radice
     *              dell'albero di copertura trovato
     * 
     * @throws NullPointerException se il grafo g o il nodo sorgente s sono nulli
     * @throws IllegalArgumentException se il nodo sorgente s non esiste in g
     * @throws IllegalArgumentException se il grafo g è orientato, non pesato o
     *        con pesi negativi
     */
    public void computeMSP(Graph<L> g, GraphNode<L> s) {
        if(g == null || s == null)
            throw new NullPointerException("Node or Graph are null");
        if(g.getNode(s) == null)
            throw new IllegalArgumentException("Node S is not present in Graph G");
        if(g.isDirected())
            throw new IllegalArgumentException("Directed graph");
        if(!checkWeights(g))
            throw new IllegalArgumentException("Graph is not weighted or has negative weights");

        //Cleans up graph for successive runs
        cleanupGraph(g);

        int n = g.nodeCount();
        s.setColor(2);
        s.setFloatingPointDistance(0);
        blackNodes.add(s);

        for(int i = 1; i < n; i++){
            Iterator<GraphNode<L>> iterBlack = blackNodes.iterator();
            while(iterBlack.hasNext()){
                GraphNode<L> temp = iterBlack.next();
                outgoingEdges.addAll(g.getEdgesOf(temp));
            }
            cleanupOutgoingEdges();


            GraphEdge<L> edge = getEdgeToAdd();
            if(edge != null) {

                if (edge.getNode1().getColor() == 2) {
                    edge.getNode2().setPrevious(edge.getNode1());
                    edge.getNode2().setColor(2);
                    edge.getNode2().setFloatingPointDistance(edge.getWeight());
                    blackNodes.add(edge.getNode2());
                } else if (edge.getNode2().getColor() == 2) {
                    edge.getNode1().setPrevious(edge.getNode2());
                    edge.getNode1().setColor(2);
                    blackNodes.add(edge.getNode1());
                    edge.getNode1().setFloatingPointDistance(edge.getWeight());
                }
            }
        }
    }

    public void cleanupGraph(Graph<L> g){
        this.blackNodes.clear();
        this.outgoingEdges.clear();

        Iterator<GraphNode<L>> iterWhite = g.getNodes().iterator();

        while(iterWhite.hasNext()){
            GraphNode<L> temp = iterWhite.next();
            temp.setColor(0);
            temp.setFloatingPointDistance(Double.POSITIVE_INFINITY);
        }
    }

    private void cleanupOutgoingEdges(){
        Iterator<GraphEdge<L>> iter = outgoingEdges.iterator();
        while(iter.hasNext()) {
            GraphEdge<L> temp = iter.next();
            if (temp.getNode1().getColor() == 2 && temp.getNode2().getColor() == 2)
                iter.remove();
        }
    }

    private GraphEdge<L> getEdgeToAdd(){
        Iterator<GraphEdge<L>> iter = outgoingEdges.iterator();
        GraphEdge<L> leastWeight = null;
        double n = Double.MAX_VALUE;

        for(GraphEdge<L> edge : outgoingEdges){
            if (edge.getWeight() < n) {
                n = edge.getWeight();
                leastWeight = edge;
            }
        }
        return leastWeight;
    }

    //helps with exception throwing, checks the graph for negative or NaN weights
    private boolean checkWeights(Graph<L> g){
        Set<GraphEdge<L>> set = g.getEdges();
        for(GraphEdge<L> edge : set){
            if(!edge.hasWeight())
                return false;
            if(edge.getWeight() < 0)
                return false;
        }
        return true;
    }

}
