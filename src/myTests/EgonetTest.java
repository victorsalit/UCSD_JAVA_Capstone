package myTests;

import java.util.HashMap;
import java.util.HashSet;

import graph.CapGraph;
import graph.Graph;
import util.GraphLoader;

public class EgonetTest {
    public static void main(String[] args) {
        Graph graph = new CapGraph();
        GraphLoader.loadGraph(graph, "data/facebook_ucsd.txt");
        HashMap<Integer, HashSet<Integer>>	resExp;
    	for (int i = 0; i < 10; i++){
//        int i = 8;
//            Graph res = graph.getEgonet(i);
            resExp =  graph.getEgonet(i).exportGraph();
            System.out.println("NODE " + i); 
            System.out.println(resExp.toString());
    		
    	}

    }

}
