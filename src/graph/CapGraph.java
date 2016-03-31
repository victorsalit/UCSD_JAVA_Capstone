/**
 * 
 */
package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import util.GraphLoader;



/**
 * @author Victor Salit.
 * 
 *
 */
public class CapGraph implements Graph {

	private final Map<Integer,GraphNode> vertices = 
			new HashMap<Integer,GraphNode>();
	
	public CapGraph(){
	}
	
	public CapGraph(Map<Integer, GraphNode> vertices2){
		vertices.putAll(vertices2);
	}

	public int getNumEdges(){
		int numEdges = 0;
		for (GraphNode n : vertices.values()){
			numEdges += n.getNumNeighbors();
		}
		return numEdges;
	}
	
	
	/* (non-Javadoc)
	 * @see graph.Graph#addVertex(int)
	 */
	@Override
	public void addVertex(int num) {
		if (!vertices.containsKey(num)) {
			GraphNode vertex = new GraphNode(num);
			vertices.put(num, vertex);			
		}
	}

	/* (non-Javadoc)
	 * @see graph.Graph#addEdge(int, int)
	 */
	@Override
	public void addEdge(int from, int to) {
		// TODO Auto-generated method stub
		GraphNode f;
		GraphNode t;
		if (!vertices.containsKey(from)){
			f = new GraphNode(from);
			vertices.put(from, f);
		}
		if (!vertices.containsKey(to)){
			t = new GraphNode(to);
			vertices.put(to, t);			
		}
		vertices.get(from).addNeighbor(to);
		vertices.get(to).addIncomming(from);
	}

	public GraphNode getVertex(int id){
		return vertices.get(id);
	}
	
	public void removeVertex(int id){
		vertices.remove(id);
		for (GraphNode v : vertices.values()){
			v.removeIncomming(id);
			v.removeNeighbor(id);
		}
	}
	
	/* (non-Javadoc)
	 * @see graph.Graph#getEgonet(int)
	 */
	@Override
	public Graph getEgonet(int center) {
		// TODO Auto-generated method stub
		if (!vertices.containsKey(center)){
			return null;
		}
		CapGraph egonet = new CapGraph();
		GraphNode centerNode = getVertex(center);
		
		// all the neighbors belong to the node's egonet:
		Set<Integer> egonetNodes = centerNode.getNeighbors();
		
		// the center node belongs to its own egonet:
		egonetNodes.add(center); 

		// graph creation:
		// we loop through the egonetNodes Set and add them to the graph
		// then instead of looping on their edges, we find the intersection 
		// of the current neighbors with the center node neighbors 
		// and add the intersection nodes as the neighbors of the current node
		// in the egonet graph.
		for (int curr : egonetNodes){
			egonet.addVertex(curr);
			Set<Integer> currNeighbors = getVertex(curr).getNeighbors();
			currNeighbors.retainAll(egonetNodes);
			GraphNode currNode = egonet.getVertex(curr);			
			currNode.addAllNeighbors(currNeighbors);
//			egonet.updateNumEdges(currNeighbors.size());
		}		
		return egonet;
	}

	/* (non-Javadoc)
	 * @see graph.Graph#getSCCs()
	 */
	@Override
	public List<Graph> getSCCs() {
		// TODO Auto-generated method stub
		
		List<Graph> SCCs = new ArrayList<Graph>();
		CapGraph scc1v;

		
		boolean second = false;
		CapGraph g = new CapGraph(this.vertices); // <= copy of the graph!
		System.out.println("input Graph: " + g.exportGraph().toString());
		
		// Step 0: some vertices are SCCs
		if (!g.vertices.isEmpty()){
			for (GraphNode v : this.vertices.values()){
				if (v.getNeighbors().isEmpty() || v.getIncomming().isEmpty()){
					scc1v = new CapGraph();
					scc1v.addVertex(v.getId());
					scc1v.getVertex(v.getId()).addAllIncomming(v.getIncomming());
					scc1v.getVertex(v.getId()).addAllNeighbors(v.getNeighbors());
					g.removeVertex(v.getId());
					SCCs.add(scc1v);
					scc1v = null;
				}
			}
		
			Stack<Integer> vertStack = new Stack<Integer>();
			Stack<Integer> finished;
			for (int key : g.vertices.keySet()){
				vertStack.push(key);
			}
			System.out.println("Stack vertices: " + vertStack.toString());
			// step 1:
			List<Graph> dummy = new ArrayList<Graph>();
			finished = DFS(g,vertStack,dummy,second);
			System.out.println("Stack finished: " + finished.toString());
			
			// step 2:
			CapGraph gTransposed = g.transpose();
			System.out.println("transposed Graph: " + gTransposed.exportGraph().toString());
			
			// step 3:
			second = true;
			finished = DFS(gTransposed,finished,SCCs,second);
			System.out.println("Stack finished 2nd: " + finished.toString());
		}
		return SCCs;
	}

	private Stack<Integer> DFS(CapGraph G, Stack<Integer> nodes, List<Graph> graphsList, boolean secondPass){
		Stack<Integer> finished = new Stack<Integer>();
		Set<Integer> visited = new HashSet<Integer>();
		while (!nodes.isEmpty()){
			CapGraph scc = new CapGraph();
			int currentV = nodes.pop();
			if(!visited.contains(currentV)){
				DFSvisit(G,currentV,visited,finished,scc);
			}
			if (secondPass){
				if (!scc.vertices.isEmpty()){
					graphsList.add(scc);			
				}
			}
		}
		return finished;
	}
	
	private void DFSvisit(CapGraph g, int v, Set<Integer> visited, Stack<Integer> finished, CapGraph scc){
		//CapGraph res = new CapGraph();
		visited.add(v);
		for (int n : g.getVertex(v).getNeighbors()){
			if (!visited.contains(n)){
				DFSvisit(g,n,visited,finished,scc);
			}
		}
		finished.push(v);
		scc.addVertex(v);
		scc.getVertex(v).addAllNeighbors(g.getVertex(v).getIncomming());
		scc.getVertex(v).addAllIncomming(g.getVertex(v).getNeighbors());
		//return res;
	}
	
	
	private CapGraph transpose(){
		CapGraph res = new CapGraph();
		GraphNode current;
		for (int key : vertices.keySet()){
			current = getVertex(key);
			current.flipDirections();
			res.addVertex(key);
			res.getVertex(key).addAllIncomming(current.getIncomming());
			res.getVertex(key).addAllNeighbors(current.getNeighbors());
		}
		return res;
	}
	
	
	/* (non-Javadoc)
	 * @see graph.Graph#exportGraph()
	 */
	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		// TODO Auto-generated method stub
		HashMap<Integer, HashSet<Integer>> graph2export = 
				new HashMap<Integer, HashSet<Integer>>();
		for (Integer key : vertices.keySet()){
			graph2export.put(key, (HashSet<Integer>) getVertex(key).getNeighbors());
		}
		return graph2export;
	}

	public void printGraph(){
		System.out.println("Graph with " + vertices.size() + " vertices");// and " + 
//	numEdges +" edges.");
		for (Integer key : vertices.keySet()){
			System.out.println(vertices.get(key).toString());
		}
		HashMap<Integer, HashSet<Integer>> graph2exp = exportGraph();
		for (Integer key : graph2exp.keySet()){
			System.out.println(graph2exp.get(key).toString());
		}
		
	}
	public static void main(String[] args)
	{
/*		System.out.print("Making a new map...");
		CapGraph small_test = new CapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadGraph(small_test, "data/small_test_graph.txt");
		System.out.print("DONE. \nsmall_test_graph: ");
		small_test.printGraph();
*/
		System.out.print("Making a new map...");
		CapGraph ucsd = new CapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadGraph(ucsd, "data/facebook_ucsd.txt");
		System.out.println("Number of vertices: " + ucsd.vertices.size());
		System.out.println("Number of edges: " + ucsd.getNumEdges());
/*		System.out.print("DONE. \nfacebook_ucsd: ");
        HashMap<Integer, HashSet<Integer>> res;// = graph.getEgonet(i).exportGraph();
    	for (int i = 3; i < 9; i++){
          System.out.println("\n\n\nNODE " + i); 
              res =  graph.getEgonet(i).exportGraph();
              System.out.println(res.toString());
      		
      	}
*/
        Graph scc2out = new CapGraph();
        GraphLoader.loadGraph(scc2out, "data/scc/test_3.txt");
        List<Graph> graphSCCs = scc2out.getSCCs();
        for(Graph graphi : graphSCCs) {
        	System.out.println(graphi.exportGraph().toString());
        }

	}
}
