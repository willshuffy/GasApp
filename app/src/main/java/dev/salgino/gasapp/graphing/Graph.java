package dev.salgino.gasapp.graphing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Guto on 19/06/2015.
 */

public class Graph {
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    private boolean cycle = false;
    private boolean directed = false;
    private boolean connected = false;

    public void clearGraph() {
        if (edges!=null)
            edges.clear();

        if (vertices!=null)
            vertices.clear();

        cycle = false;
        directed = false;
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean d) {
        directed = d;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public String printGraph() {
        String stuart = "";

        for (Edge edge: getEdges()){
            stuart = stuart + edge.getStart().getName() + " - "
                    + edge.getEnd().getName()
                    + " (" + edge.getWeight() +")" + " | ";
            edge.getStart().setVisited(true);
            edge.getEnd().setVisited(true);
        }
        for (Vertex v: vertices){
            if(!v.isVisited()){
                stuart = stuart + v.getName()+ " | ";
                v.setVisited(true);
            }
        }
        stuart = stuart + "\n";

        cleanVisitedVertex();

        return stuart;
    }

    public void cleanVisitedVertex() {
        if (getVertices()!=null && !getVertices().isEmpty()){
            for (Vertex v: getVertices())
                v.setVisited(false);
        }
    }


    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public int addEdge(int weight, String start, String end) {
        int i, j, k;

        if (start.equals(end))
            return -2;
        if (findEdge(new Vertex(start), new Vertex(end))!=null) //Edge already exists
            return -3;
        if (weight == 0)
            return -4;

        i=vertexLocation(start);
        j=vertexLocation(end);
        k = vertices.size();

        //Checking if the Vertices can be inserted (the maximum number of vertices is 10)
        if(vertices.size()>=9){
            if (i==vertices.size())
                k++;
            if (j==vertices.size())
                k++;
            if (k>10)
                return -1;
        }

        // adding vertices and getting the position of each one
        if (i==k)
            i = addVertex(start);
        if (j==k)
            j = addVertex(end);

        // add edge in the list
        Edge a = new Edge(weight, vertices.get(i), vertices.get(j));

        if (!cycle)
            hasCycle(a);

        edges.add(a);
        k = edges.size();

        // add edge in the list of incident edges of each vertex
        vertices.get(i).addIncidents(edges.get(k - 1));
        vertices.get(j).addIncidents(edges.get(k - 1));

        if (!connected)
            setConnected();

        return (edges.size() -1);
    }

    public boolean setConnected(){
        String start = vertices.get(0).getName();

        ArrayList<Vertex> breadthTree = new ArrayList<Vertex>();

        for (Vertex v : vertices) {
            v.setColor("white");
        }

        Vertex current = findVertex(start);
        current.setColor("grey");

        LinkedList<Vertex> queue = new LinkedList<Vertex>();
        queue.add(current);

        while (queue.size() > 0) {
            current = queue.remove();
            current.setColor("black");

            for (Vertex neighbor : current.getNeighbors()) {
                Edge edge = findEdge(current, neighbor);
                if (neighbor.getColor().equals("white") &&
                        (edge != null) ) {

                    neighbor.setColor("grey");
                    queue.add(neighbor);
                    breadthTree.add(current);
                    breadthTree.add(neighbor);
                }
            }
        }

        if(breadthTree.size()<vertices.size())
            return false;

        connected = true;
        return true;
    }

    // add a vertex returning its position or '-1' if the insertion was not possible
    public int addVertex(String nome) {
        int i = vertexLocation(nome);

        //Checking if the Vertex already exist
        if(i!=vertices.size())
            return -2;
        //Checking if the Vertex can be inserted (the maximum number of vertices is 10)
        if (vertices.size()==10)
            return -1;

        if (i == vertices.size()) {
            vertices.add(new Vertex(nome));
            connected = false;
            return (vertices.size() - 1);
        }

        return i;

    }

    // returns the location of a vertex in the list
    public int vertexLocation(String nome) {
        int i;

        for (i = 0; i < vertices.size(); i++)
            if (vertices.get(i).getName().equals(nome))
                return i;

        // if it is not found, returns the list's size
        return vertices.size();

    }

    public Vertex findVertex(String name) {
        return vertices.get(vertexLocation(name));
    }

    public Edge findEdge(Vertex vet1, Vertex vet2) {
        if (!vet1.equals(vet2)) {
            if (directed) {
                for (Edge e : edges)
                    if (((e.getStart().getName().equals(vet1.getName())) &&
                            (e.getEnd().getName().equals(vet2.getName()))))
                        return e;
            } else {
                for (Edge e : edges)
                    if (((e.getStart().getName().equals(vet1.getName())) &&
                            (e.getEnd().getName().equals(vet2.getName()))) ||
                            ((e.getStart().getName().equals(vet2.getName())) &&
                                    (e.getEnd().getName().equals(vet1.getName()))))
                        return e;
            }
        }
        return null;

    }

    public Graph graphCreator (ArrayList<Edge> pathEdge){
        Graph graph = new Graph();
        graph.setDirected(directed);

        for (Edge e : pathEdge)
            graph.addEdge(e.getWeight(), e.getStart().getName(), e.getEnd().getName());


        return graph;
    }


    // method that returns whether a certain new edge can create a cycle or not
    // in the currentVertex graph
    public boolean hasCycle(Edge edge) {

        String start = edge.getStart().getName();
        String end = edge.getEnd().getName();

        for (int i = 0; i < getEdges().size(); i++) {

            if (isDirected()){

                for (Edge edge2: getEdges()) {

                    if (edge != edge2) {

                        if (end.equals(edge2.getStart().getName())) {

                            if (start.equals(edge2.getEnd()
                                    .getName())) {
                                cycle = true;
                                return true;
                            } else
                                end = edge2.getEnd().getName();
                        }
                    }
                }
            }else{

                for (Edge edge2: getEdges()) {

                    if (edge != edge2) {

                        if (end.equals(edge2.getStart().getName())) {

                            if (start.equals(edge2.getEnd()
                                    .getName())) {
                                cycle = true;
                                return true;
                            } else
                                end = edge2.getEnd().getName();

                        } else if (end.equals(edge2.getEnd()
                                .getName())) {

                            if (start.equals(edge2.getStart()
                                    .getName())) {
                                cycle = true;
                                return true;
                            } else
                                end = edge2.getStart().getName();
                        }
                    }
                }
            }

        }
        cycle = false;
        return false;
    }

    //Recursive method that return a boolean as response by the search for a vertex and sets as visited all the vertices and edges on the way.
    public void recursiveSearch(String start){

        int startIndex = vertexLocation(start);
        Edge edge;
        Vertex vertex;

        vertices.get(startIndex).setVisited(true);


        for(Vertex v: vertices.get(startIndex).getNeighbors()){

            if (!v.isVisited()){
                //Finds the edge between the vertices start and v.
                vertex = vertices.get(startIndex);
                edge = findEdge(vertex, v);
                //Sets this edge as visited and keep searching recursively considering if the graph is directed
                if (edge != null){
                    edge.setVisited(true);
                    recursiveSearch(v.getName());
                }


            }
        }
    }

    //------------------TRANSITIVE-CLOSURE---------------------------------------

    public Graph transitiveClosure (){
        Graph graph;
        Integer[][] weightsFW = floydWarshall();
        int weight;

        graph = graphCreator(edges);

        for(Vertex v1 : getVertices()){
            recursiveSearch(v1.getName());
            for(Vertex v2 : getVertices()){
                Edge edge = graph.findEdge(v1, v2);
                if (v2.isVisited()
                        && !(v1.getName().equals(v2.getName()))
                        && (edge==null) ){
                    weight = weightsFW[vertexLocation(v1.getName())][vertexLocation(v2.getName())];
                    graph.addEdge(weight, v1.getName(), v2.getName());
                }
            }
            //GUTOSSAURO DELICIA DA JAC

            cleanVisitedVertex();
        }

        for (Vertex v: vertices){
            if(graph.vertexLocation(v.getName())==graph.getVertices().size()){
                graph.addVertex(v.getName());
            }
        }
        return graph;
    }

    //------------------FLOYD-WARSHALL----------------------------------

    public Integer[][] createGraphMatrix(){
        int n = vertices.size();
        Integer[][] matrix = new Integer[n][n];

        for(int i = 0; i < vertices.size(); i++){
            for(int j = 0; j < vertices.size(); j++){
                if(i==j){
                    matrix[i][j] = 0;
                } else {
                    Vertex v = vertices.get(i);
                    Edge e = findEdge(v , vertices.get(j));

                    if(e != null){
                        matrix[i][j] = e.getWeight();
                    } else {
                        matrix[i][j] = 9999999;//infinity
                    }
                }
            }
        }

        return matrix;
    }

    public Integer[][] floydWarshall(){
        int n = vertices.size();

        Integer[][] dist = createGraphMatrix();
        Integer[][] pred = new Integer[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                pred[i][j] = 9;

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if(dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        pred[i][j] = k;
                    }
                }
            }
        }

        return dist;
    }

}