package it.unicam.cs.asdl2122.mp2;

import java.util.*;

/**
 * Implementazione dell'interfaccia <code>DisjointSets<E></code> tramite una
 * foresta di alberi ognuno dei quali rappresenta un insieme disgiunto. Si
 * vedano le istruzioni o il libro di testo Cormen et al. (terza edizione)
 * Capitolo 21 Sezione 3.
 *
 * @param <E> il tipo degli elementi degli insiemi disgiunti
 * @author Luca Tesei (template) Antonio Gassner, antoniobimbo.gassner@studenti.unicam.it (implementazione)
 */
public class ForestDisjointSets<E> implements DisjointSets<E> {


    /*
     * Mappa che associa ad ogni elemento inserito il corrispondente nodo di un
     * albero della foresta. La variabile è protected unicamente per permettere
     * i test JUnit.
     */
    protected Map<E, Node<E>> currentElements;

    /*
     * Classe interna statica che rappresenta i nodi degli alberi della foresta.
     * Gli specificatori sono tutti protected unicamente per permettere i test
     * JUnit.
     */
    protected static class Node<E> {
        /*
         * L'elemento associato a questo nodo
         */
        protected E item;

        /*
         * Il parent di questo nodo nell'albero corrispondente. Nel caso in cui
         * il nodo sia la radice allora questo puntatore punta al nodo stesso.
         */
        protected Node<E> parent;

        /*
         * Il rango del nodo definito come limite superiore all'altezza del
         * (sotto)albero di cui questo nodo è radice.
         */
        protected int rank;

        /**
         * Costruisce un nodo radice con parent che punta a se stesso e rango
         * zero.
         *
         * @param item l'elemento conservato in questo nodo
         */
        public Node(E item) {
            this.item = item;
            this.parent = this;
            this.rank = 0;
        }

    }

    /**
     * Costruisce una foresta vuota di insiemi disgiunti rappresentati da
     * alberi.
     */
    public ForestDisjointSets() {
        currentElements = new HashMap<E, Node<E>>();
    }

    @Override
    public boolean isPresent(E e) {
        if (e == null)
            throw new NullPointerException("E is null");
        for (E element : currentElements.keySet()) {
            if (element == e)
                return true;
        }
        return false;
    }

    @Override
    public void makeSet(E e) {
        if (e == null)
            throw new NullPointerException("E is null");
        if (isPresent(e))
            throw new IllegalArgumentException("E already in set");
        currentElements.put(e, new Node<E>(e));
    }

    @Override
    public E findSet(E e) {
        if (e == null)
            throw new NullPointerException("E is null");
        if (!isPresent(e))
            return null;

        Node<E> current = currentElements.get(e);
        Node<E> root = current.parent;
        Node<E> temp = root;

        while (root != root.parent)
            root = root.parent;

        while (temp != root) {
            current.parent = root;
            current = temp;
            temp = temp.parent;
        }
        return root.item;
    }

    @Override
    public void union(E e1, E e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("One of the nodes is null");
        if (!isPresent(e1) || !isPresent(e2))
            throw new IllegalArgumentException("One of the nodes not in map");
        if (currentElements.get(e1).parent != currentElements.get(e2).parent) {
            Node<E> root1 = currentElements.get(findSet(e1));
            Node<E> root2 = currentElements.get(findSet(e2));
            if (root1.rank > root2.rank)
                root2.parent = root1;
            else {
                if (root1.rank == root2.rank)
                    root2.rank++;
                root1.parent = root2;
            }
        }
    }

    @Override
    public Set<E> getCurrentRepresentatives() {
        Set<E> set = new HashSet<E>();
        Iterator<E> iter = currentElements.keySet().iterator();
        while (iter.hasNext()) {
            E element = iter.next();
            if (findSet(element) == element)
                set.add(element);
        }
        return set;
    }

    @Override
    public Set<E> getCurrentElementsOfSetContaining(E e) {
        if (e == null)
            throw new NullPointerException("E is null");
        if (!isPresent(e))
            throw new IllegalArgumentException("E not in set");
        Set<E> set = new HashSet<E>();
        Node<E> parent = currentElements.get(e).parent;
        Iterator<E> iter = currentElements.keySet().iterator();
        while (iter.hasNext()) {
            E element = iter.next();
            if (findSet(element) == parent.item)
                set.add(element);
        }
        return set;
    }

    @Override
    public void clear() {
        currentElements.clear();
    }

}
