package graph;

import java.util.HashSet;
import java.util.Set;

public class GraphNode {
	private final int id;
	
	private final Set<Integer> neighbors = new HashSet<Integer>();
	private final Set<Integer> incomming = new HashSet<Integer>();

	//private final Map<GraphNode,GraphEdge> neighbors = new HashMap<GraphNode,GraphEdge>();

	public GraphNode(int num){
		id = num;
	}

	public GraphNode(int num, Set<Integer> out, Set<Integer> in){
		id = num;
		neighbors.addAll(out);
		incomming.addAll(in);
	}
	
	public int getId(){
		return id;
	}

	/**
	 * @return the neighbors
	 */
	public Set<Integer> getNeighbors() {
		Set<Integer> res = new HashSet<Integer>();
		res.addAll(neighbors);
		return res;
	}

	public Set<Integer> getIncomming() {
		Set<Integer> res = new HashSet<Integer>();
		res.addAll(incomming);
		return res;
	}


	public int getNumNeighbors(){
		return neighbors.size();
	}
	
	public int getNumIncomming(){
		return incomming.size();
	}
	
	/**
	 * 
	 * @param v
	 */
	public void addNeighbor(int v){
		neighbors.add(v);
	}

	public void removeNeighbor(int v){
		neighbors.remove(v);
	}
	
	public void removeIncomming(int v){
		incomming.remove(v);
	}
	
	public void addAllNeighbors(Set<Integer> newNeighbors){
		neighbors.addAll(newNeighbors);
	}
	/** 
	 * A textual representation of the MapJunction object 
	 * @return String text
	 */
	
	/**
	 * 
	 * @param v
	 */
	public void addIncomming(int v){
		incomming.add(v);
	}

	public void addAllIncomming(Set<Integer> newIncomming){
		incomming.addAll(newIncomming);
	}
	
	public void flipDirections(){
		Set<Integer> temp = new HashSet<Integer>();
		temp.addAll(incomming);
		incomming.removeAll(getIncomming());
		incomming.addAll(getNeighbors());
		neighbors.removeAll(getNeighbors());
		addAllNeighbors(temp);
	}

	public String toString(){
		String text = "\nVertex " + id
						+ ". It has " + neighbors.size() + " neighbors: " +
		neighbors.toString() + " and " + incomming.size() + 
		" incomming nodes: " + incomming.toString();;
		return text;
	}
}
