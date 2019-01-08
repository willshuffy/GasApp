package dev.salgino.gasapp.graphing;

import android.content.Intent;

/**
 * Created by Guto on 15/08/2015.
 */
public class Controller {

    static Graph graph;

    public static Graph getGraph() {
        return graph;
    }

    public static void destroy (){
        if (graph!=null){
            graph.clearGraph();
        }
    }

    public static void initiate(Intent i){
        graph = new Graph();
        graph.setDirected(i.getBooleanExtra("directed", true));
    }

    public static int main(Intent i){
        switch (i.getIntExtra("previous",1)) { // This value shows which is the previous activity, with: 0-MainActivity/1-VertexActivity/2-EdgeActivity/3-Any other
            case 0:
                initiate(i);
                break;
            case 1:
                switch (graph.addVertex(i.getStringExtra("vertex"))) {
                    case -1:
                        return 1;
                    case -2:
                        return 6;
                    default:
                        return 2;
                }
            case 2:
                switch (graph.addEdge(i.getIntExtra("weight", -1),i.getStringExtra("start"),i.getStringExtra("end"))){
                    case -1:
                        return 1;
                    case -2:
                        return 3;
                    case -3:
                        return 4;
                    case -4:
                        return 7; //Do not accept weight 0
                    default:
                        return 5;
                }
        }
        return 0;
    }
}