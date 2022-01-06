/**
 * 
 */
package it.unicam.cs.asdl2122.mp2;

import java.util.*;

// TODO completare gli import necessari

// ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Classe che implementa un grafo non orientato tramite matrice di adiacenza.
 * Non sono accettate etichette dei nodi null e non sono accettate etichette
 * duplicate nei nodi (che in quel caso sono lo stesso nodo).
 * 
 * I nodi sono indicizzati da 0 a nodeCount() - 1 seguendo l'ordine del loro
 * inserimento (0 è l'indice del primo nodo inserito, 1 del secondo e così via)
 * e quindi in ogni istante la matrice di adiacenza ha dimensione nodeCount() *
 * nodeCount(). La matrice, sempre quadrata, deve quindi aumentare di dimensione
 * ad ogni inserimento di un nodo. Per questo non è rappresentata tramite array
 * ma tramite ArrayList.
 * 
 * Gli oggetti GraphNode<L>, cioè i nodi, sono memorizzati in una mappa che
 * associa ad ogni nodo l'indice assegnato in fase di inserimento. Il dominio
 * della mappa rappresenta quindi l'insieme dei nodi.
 * 
 * Gli archi sono memorizzati nella matrice di adiacenza. A differenza della
 * rappresentazione standard con matrice di adiacenza, la posizione i,j della
 * matrice non contiene un flag di presenza, ma è null se i nodi i e j non sono
 * collegati da un arco e contiene un oggetto della classe GraphEdge<L> se lo
 * sono. Tale oggetto rappresenta l'arco. Un oggetto uguale (secondo equals) e
 * con lo stesso peso (se gli archi sono pesati) deve essere presente nella
 * posizione j, i della matrice.
 *
 * Questa classe supporta i metodi di cancellazione di nodi e archi e
 * supporta tutti i metodi che usano indici, utilizzando l'indice assegnato a
 * ogni nodo in fase di inserimento.
 * 
 * @author Luca Tesei (template) **INSERIRE NOME, COGNOME ED EMAIL
 *         xxxx@studenti.unicam.it DELLO STUDENTE** (implementazione)
 *
 * 
 */
public class AdjacencyMatrixUndirectedGraph<L> extends Graph<L> {

    /**
     * Variable to keep track of the last index for the Map
     */

    private int index = 0;

    /*
     * Le seguenti variabili istanza sono protected al solo scopo di agevolare
     * il JUnit testing
     */

    /*
     * Insieme dei nodi e associazione di ogni nodo con il proprio indice nella
     * matrice di adiacenza
     */
    protected Map<GraphNode<L>, Integer> nodesIndex;
    
    /*
     * Matrice di adiacenza, gli elementi sono null o oggetti della classe
     * GraphEdge<L>. L'uso di ArrayList permette alla matrice di aumentare di
     * dimensione gradualmente ad ogni inserimento di un nuovo nodo e di
     * ridimensionarsi se un nodo viene cancellato.
     */
    protected ArrayList<ArrayList<GraphEdge<L>>> matrix;

    /**
     * Crea un grafo vuoto.
     */
    public AdjacencyMatrixUndirectedGraph() {
        this.matrix = new ArrayList<ArrayList<GraphEdge<L>>>();
        this.nodesIndex = new HashMap<GraphNode<L>, Integer>();
    }

    @Override
    public int nodeCount() {
        return nodesIndex.size();
    }

    //tests not green
    @Override
    public int edgeCount() {
        int edgeCounter = 0;
        for (ArrayList<GraphEdge<L>> list : this.matrix) {
            for(GraphEdge<L> edge : list)
                if(edge != null)
                edgeCounter++;
        }
        return edgeCounter/2;
    }


    @Override
    public void clear() {
       matrix.clear();
       nodesIndex.clear();
    }

    @Override
    public boolean isDirected() {
        for (ArrayList<GraphEdge<L>> list : this.matrix) {
            for (GraphEdge<L> edge : list) {
                if(edge != null)
                    if (edge.isDirected())
                    return true;
            }
        }
        return false;
    }

    /*
     * Gli indici dei nodi vanno assegnati nell'ordine di inserimento a partire
     * da zero
     */
    @Override
    public boolean addNode(GraphNode<L> node) {
        if(node == null){
            throw new NullPointerException("Node is null");
        }
        if(nodesIndex.containsKey(node)){
            return false;
        } else
        nodesIndex.put(node, index);
        matrix.add(index, new ArrayList<>());
        addNodeUpdateMatrix();
        return true;
    }

    /*
     * Gli indici dei nodi vanno assegnati nell'ordine di inserimento a partire
     * da zero
     */
    @Override
    public boolean addNode(L label) {
        if(label == null){
            throw new NullPointerException("Label is null");
        }
        for(GraphNode<L> node : nodesIndex.keySet()){
            if(node.getLabel() == label){
                return false;
            }
        }
        matrix.add(index, new ArrayList<>());
        nodesIndex.put(new GraphNode<L>(label), index);
        addNodeUpdateMatrix();
        return true;
    }

    private void addNodeUpdateMatrix(){
        for(int i = 0; i < index; i++)
            matrix.get(index).add(null);
        for(int i = 0; i < matrix.size(); i++)
            matrix.get(i).add(null);
        index++;
    }

    private void removeNodeUpdateMatrix(int pos){
        for(int i = 0; i < matrix.size(); i++)
            matrix.get(i).remove(pos);
        index--;
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(GraphNode<L> node) {
        if(node == null){
            throw new NullPointerException("Node is null");
        }
        if(!nodesIndex.containsKey(node)){
            throw new IllegalArgumentException("Node not in map");
        }
        int pos = nodesIndex.remove(node);
        matrix.remove(pos);
        Iterator<GraphNode<L>> mapItr = nodesIndex.keySet().iterator();
        for(int i = 0; i < nodesIndex.size(); i++){
            nodesIndex.put(mapItr.next(), i);
        }
        removeNodeUpdateMatrix(pos);
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(L label) {
        if(label == null){
            throw new NullPointerException("Label is null");
        }
        if(getNode(label) == null){
            throw new IllegalArgumentException("Node not in map");
        }
        int pos = nodesIndex.remove(getNode(label));
        matrix.remove(pos);
        Iterator<GraphNode<L>> mapItr = nodesIndex.keySet().iterator();
        for(int i = 0; i < nodesIndex.size(); i++){
            nodesIndex.put(mapItr.next(), i);
        }
        removeNodeUpdateMatrix(pos);
    }

    /*
     * Gli indici dei nodi il cui valore sia maggiore dell'indice del nodo da
     * cancellare devono essere decrementati di uno dopo la cancellazione del
     * nodo
     */
    @Override
    public void removeNode(int i) {
        if(i < 0 || i > nodesIndex.size()){
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        int pos = nodesIndex.remove(getNode(i));
        matrix.remove(pos);
        Iterator<GraphNode<L>> mapItr = nodesIndex.keySet().iterator();
        for(int j = 0; j < nodesIndex.size(); j++){
            nodesIndex.put(mapItr.next(), j);
        }
        removeNodeUpdateMatrix(pos);
    }

    @Override
    public GraphNode<L> getNode(GraphNode<L> node) {
        if(node == null){
            throw new NullPointerException("Node is null");
        }
        for(GraphNode<L> returnNode : nodesIndex.keySet()){
            if(returnNode.equals(node))
                return returnNode;
        }
        return null;
    }

    @Override
    public GraphNode<L> getNode(L label) {
        if(label == null){
            throw new NullPointerException("Label is null");
        }
        for(GraphNode<L> returnNode : nodesIndex.keySet()){
            if(returnNode.getLabel().equals(label))
                return returnNode;
        }
        return null;
    }

    @Override
    public GraphNode<L> getNode(int i) {
        if(i < 0 || i >= nodesIndex.size()){
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        for(GraphNode<L> returnNode : nodesIndex.keySet()){
            if(nodesIndex.get(returnNode) == i)
                return returnNode;
        }
        return null;
    }

    @Override
    public int getNodeIndexOf(GraphNode<L> node) {
        if(node == null){
            throw new NullPointerException("Node is null");
        }
        if(!nodesIndex.containsKey(node)){
            throw new IllegalArgumentException("Node not in map");
        }
        return nodesIndex.get(node);
    }

    @Override
    public int getNodeIndexOf(L label) {
        if(label == null){
            throw new NullPointerException("Label is null");
        }
        if(getNode(label) == null){
            throw new IllegalArgumentException("Node with given label not in map");
        }
        return nodesIndex.get(getNode(label));
    }

    @Override
    public Set<GraphNode<L>> getNodes() {
        return nodesIndex.keySet();
    }

    //tests not green
    @Override
    public boolean addEdge(GraphEdge<L> edge) {
        if (edge == null)
            throw new NullPointerException("Edge is null");
        if (getNode(edge.getNode1()) == null || getNode(edge.getNode2()) == null)
            throw new IllegalArgumentException("One of the nodes of Edge is null");
        if(edge.isDirected())
            throw new IllegalArgumentException("Edge is directed");
        GraphNode<L> node1 = edge.getNode1();
        GraphNode<L> node2 = edge.getNode2();
        int i = nodesIndex.get(node1);
        int j = nodesIndex.get(node2);
        if (matrix.get(i).get(j) != null && matrix.get(j).get(i) != null) {
            if (matrix.get(i).get(j).equals(edge) && matrix.get(j).get(i).equals(edge)) {
                return false;
            }
        }
        matrix.get(i).remove(j);
        matrix.get(i).add(j, edge);
        matrix.get(j).remove(i);
        matrix.get(j).add(i, edge);
        return true;
    }

    @Override
    public boolean addEdge(GraphNode<L> node1, GraphNode<L> node2) {
        if(node1 == null || node2 == null)
            throw new NullPointerException("One of the nodes is null");
        if(getNode(node1) == null || getNode(node2) == null)
            throw new IllegalArgumentException("One of the nodes is not in Map");
        int i = nodesIndex.get(node1);
        int j = nodesIndex.get(node2);
        GraphEdge<L> edge = new GraphEdge<L>(node1, node2, false);
        if (matrix.get(i).get(j) != null && matrix.get(j).get(i) != null) {
            if (matrix.get(i).get(j).equals(edge) && matrix.get(j).get(i).equals(edge)) {
                return false;
            }
        }
        matrix.get(i).remove(j);
        matrix.get(i).add(j, edge);
        matrix.get(j).remove(i);
        matrix.get(j).add(i, edge);
        return true;
    }
    @Override
    public boolean addWeightedEdge(GraphNode<L> node1, GraphNode<L> node2, double weight) {
        if(node1 == null || node2 == null)
            throw new NullPointerException("One of the nodes is null");
        if(getNode(node1) == null || getNode(node2) == null)
            throw new IllegalArgumentException("One of the nodes is not in Map");
        int i = nodesIndex.get(node1);
        int j = nodesIndex.get(node2);
        GraphEdge<L> edge = new GraphEdge<L>(node1, node2, false, weight);
        if (matrix.get(i).get(j) != null && matrix.get(j).get(i) != null) {
            if (matrix.get(i).get(j).equals(edge) && matrix.get(j).get(i).equals(edge)) {
                return false;
            }
        }
        matrix.get(i).remove(j);
        matrix.get(i).add(j, edge);
        matrix.get(j).remove(i);
        matrix.get(j).add(i, edge);
        return true;
    }

    @Override
    public boolean addEdge(L label1, L label2) {
        if(label1 == null || label2 == null)
            throw new NullPointerException("One of the labels is null");
        if(getNode(label1) == null || getNode(label2) == null)
            throw new IllegalArgumentException("One of the nodes is not in Map");
        int i = nodesIndex.get(getNode(label1));
        int j = nodesIndex.get(getNode(label2));
        GraphEdge<L> edge = new GraphEdge<L>(getNode(label1), getNode(label2), false);
        if (matrix.get(i).get(j) != null && matrix.get(j).get(i) != null) {
            if (matrix.get(i).get(j).equals(edge) && matrix.get(j).get(i).equals(edge)) {
                return false;
            }
        }
        matrix.get(i).remove(j);
        matrix.get(i).add(j, edge);
        matrix.get(j).remove(i);
        matrix.get(j).add(i, edge);
        return true;
    }

    @Override
    public boolean addWeightedEdge(L label1, L label2, double weight) {
        if(label1 == null || label2 == null)
            throw new NullPointerException("One of the labels is null");
        if(getNode(label1) == null || getNode(label2) == null)
            throw new IllegalArgumentException("One of the nodes is not in Map");
        int i = nodesIndex.get(getNode(label1));
        int j = nodesIndex.get(getNode(label2));
        GraphEdge<L> edge = new GraphEdge<L>(getNode(label1), getNode(label2), false, weight);
        if (matrix.get(i).get(j) != null && matrix.get(j).get(i) != null) {
            if (matrix.get(i).get(j).equals(edge) && matrix.get(j).get(i).equals(edge)) {
                return false;
            }
        }
        matrix.get(i).remove(j);
        matrix.get(i).add(j, edge);
        matrix.get(j).remove(i);
        matrix.get(j).add(i, edge);
        return true;
    }

    //Needs a check if already contained to return false if its the case. Would like to use getEdge for that so maybe later
    @Override
    public boolean addEdge(int i, int j) {
        if(i < 0 || j < 0 || i >= nodesIndex.size() || j >= nodesIndex.size())
            throw new IndexOutOfBoundsException("Index out of bound");
        if(getNode(i) == null || getNode(j) == null)
            throw new IndexOutOfBoundsException("Node not in map");
        GraphNode<L> node1 = getNode(i);
        GraphNode<L> node2 = getNode(j);
        GraphEdge<L> edge = new GraphEdge<>(node1, node2, false);
        matrix.get(i).remove(j);
        matrix.get(i).add(j, edge);
        matrix.get(j).remove(i);
        matrix.get(j).add(i, edge);
        return true;
    }

    // Same as above
    @Override
    public boolean addWeightedEdge(int i, int j, double weight) {
        if(i < 0 || j < 0 || i >= nodesIndex.size() || j >= nodesIndex.size())
            throw new IndexOutOfBoundsException("Index out of bound");
        if(getNode(i) == null || getNode(j) == null)
            throw new IndexOutOfBoundsException("Node not in map");
        GraphNode<L> node1 = getNode(i);
        GraphNode<L> node2 = getNode(j);
        GraphEdge<L> edge = new GraphEdge<>(node1, node2, false, weight);
        matrix.get(i).remove(j);
        matrix.get(i).add(j, edge);
        matrix.get(j).remove(i);
        matrix.get(j).add(i, edge);
        return true;
    }

    @Override
    public void removeEdge(GraphEdge<L> edge) {
        if(edge == null)
            throw new NullPointerException("Edge is null");
        if(getEdge(edge) == null)
            throw new IllegalArgumentException("Edge not in matrix");
        int i = nodesIndex.get(edge.getNode1());
        int j = nodesIndex.get(edge.getNode2());
        matrix.get(i).set(j, null);
        matrix.get(j).set(i, null);
    }

    @Override
    public void removeEdge(GraphNode<L> node1, GraphNode<L> node2) {
        if(node1 == null || node2 == null)
            throw new NullPointerException("One of the nodes is null");
        if(getEdge(node1, node2) == null)
            throw new IllegalArgumentException("Edge not in matrix");
        int i = nodesIndex.get(node1);
        int j = nodesIndex.get(node2);
        matrix.get(i).set(j, null);
        matrix.get(j).set(i, null);
    }

    @Override
    public void removeEdge(L label1, L label2) {
        if(label1 == null || label2 == null)
            throw new NullPointerException("One of the labels is null");
        if(getEdge(label1, label2) == null)
            throw new IllegalArgumentException("Edge not in matrix");
        int i = nodesIndex.get(getNode(label1));
        int j = nodesIndex.get(getNode(label2));
        matrix.get(i).set(j, null);
        matrix.get(j).set(i, null);
    }

    @Override
    public void removeEdge(int i, int j) {
        if(i < 0 || j < 0 || i >= nodesIndex.size() || j >= nodesIndex.size())
            throw new IndexOutOfBoundsException("Index out of bound");
        if(getNode(i) == null || getNode(j) == null)
            throw new IndexOutOfBoundsException("Edge not in matrix");
        matrix.get(i).set(j, null);
        matrix.get(j).set(i, null);
    }

    @Override
    public GraphEdge<L> getEdge(GraphEdge<L> edge) {
        if (edge == null)
            throw new NullPointerException("Edge is null");
        if (getNode(edge.getNode1()) == null || getNode(edge.getNode2()) == null)
            throw new IllegalArgumentException("One of the nodes not in map");
        for (ArrayList<GraphEdge<L>> list : this.matrix) {
            for (GraphEdge<L> edge1 : list) {
                if (edge.equals(edge1)) {
                    return edge;
                }
            }
        }
        return null;
    }

    @Override
    public GraphEdge<L> getEdge(GraphNode<L> node1, GraphNode<L> node2) {
        if(node1 == null || node2 == null)
            throw new NullPointerException("One of the nodes is null");
        if(getNode(node1) == null || getNode(node2) == null)
            throw new IllegalArgumentException("One of the nodes not in map");
        int i = nodesIndex.get(node1);
        int j = nodesIndex.get(node2);
        return matrix.get(i).get(j);
    }

    @Override
    public GraphEdge<L> getEdge(L label1, L label2) {
        if(label1 == null || label2 == null)
            throw new NullPointerException("One of the labels is null");
        if(getNode(label1) == null || getNode(label2) == null)
            throw new IllegalArgumentException("One of the nodes with given label not in map");
        int i = nodesIndex.get(getNode(label1));
        int j = nodesIndex.get(getNode(label2));
        return matrix.get(i).get(j);
    }

    @Override
    public GraphEdge<L> getEdge(int i, int j) {
        if (i < 0 || j < 0 || i >= nodesIndex.size() || j >= nodesIndex.size())
            throw new IndexOutOfBoundsException("Index out of bound");
        if (getNode(i) == null || getNode(j) == null)
            throw new IndexOutOfBoundsException("One of the nodes not in map");
        return matrix.get(i).get(j);
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(GraphNode<L> node) {
        if(node == null)
            throw new NullPointerException("Node is null");
        if(getNode(node) == null)
            throw new IllegalArgumentException("Node not in map");
        Set<GraphNode<L>> returnSet = new HashSet<>();
        Set<GraphNode<L>> set = getNodes();
        for(GraphNode<L> mapNode : set){
            if(getEdge(node, mapNode) != null)
                returnSet.add(mapNode);
        }
        return returnSet;
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(L label) {
        if(label == null)
            throw new NullPointerException("Label is null");
        if(getNode(label) == null)
            throw new IllegalArgumentException("Node not in map");
        Set<GraphNode<L>> returnSet = new HashSet<>();
        Set<GraphNode<L>> set = getNodes();
        for(GraphNode<L> mapNode : set){
            if(getEdge(label, mapNode.getLabel()) != null)
                returnSet.add(mapNode);
        }
        return returnSet;
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(int i) {
        if (i < 0 || i >= nodesIndex.size())
            throw new IndexOutOfBoundsException("Index out of bound");
        if (getNode(i) == null)
            throw new IndexOutOfBoundsException("Node not in map");
        Set<GraphNode<L>> returnSet = new HashSet<>();
        Set<GraphNode<L>> set = getNodes();
        for(GraphNode<L> mapNode : set){
            if(getEdge(getNode(i), mapNode) != null)
                returnSet.add(mapNode);
        }
        return returnSet;
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(GraphNode<L> node) {
        if(node == null)
            throw new NullPointerException("Node is null");
        if(getNode(node) == null)
            throw new IllegalArgumentException("Node not in map");
        Set<GraphEdge<L>> returnSet = new HashSet<>();
        Set<GraphNode<L>> set = getAdjacentNodesOf(node);
        for(GraphNode<L> mapNode : set)
            returnSet.add(getEdge(node, mapNode));
        return returnSet;
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(L label) {
        if(label == null)
            throw new NullPointerException("Label is null");
        if(getNode(label) == null)
            throw new IllegalArgumentException("Node not in map");
        Set<GraphEdge<L>> returnSet = new HashSet<>();
        Set<GraphNode<L>> set = getAdjacentNodesOf(label);
        for(GraphNode<L> mapNode : set)
            returnSet.add(getEdge(label, mapNode.getLabel()));
        return returnSet;
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(int i) {
        if (i < 0 || i >= nodesIndex.size())
            throw new IndexOutOfBoundsException("Index out of bound");
        if (getNode(i) == null)
            throw new IndexOutOfBoundsException("Node not in map");
        Set<GraphEdge<L>> returnSet = new HashSet<>();
        Set<GraphNode<L>> set = getAdjacentNodesOf(i);
        for(GraphNode<L> mapNode : set)
            returnSet.add(getEdge(getNode(i), mapNode));
        return returnSet;
    }

    @Override
    public Set<GraphEdge<L>> getEdges() {
        Set<GraphEdge<L>> returnSet = new HashSet<>();
        for (ArrayList<GraphEdge<L>> list : this.matrix) {
            for (GraphEdge<L> edge : list) {
                if (edge != null)
                   returnSet.add(edge);
            }
        }
        return returnSet;
    }



    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(L label) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(int i) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(L label) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(int i) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }
}

/*
ERROR PRINTS

Node is null
Label is null
Edge is null
One of the labels is null
One of the nodes is null
One of the nodes of Edge is null

Node not in map
Node with given label not in map
One of the nodes not in Map

Index is out of bounds
 */