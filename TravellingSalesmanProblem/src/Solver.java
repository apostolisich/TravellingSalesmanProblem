import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xml.representation.classes.Edge;
import xml.representation.classes.Graph;
import xml.representation.classes.Vertex;

public class Solver {
	
	private Solver() { }
	
	public static List<Vertex> nearestNeighborHeuristic(Graph graph) {
		List<Vertex> vertexList = graph.getVertexList();
		int startingVertexId = new Random().nextInt(graph.size());
		Vertex startingVertex = vertexList.get(startingVertexId);
		
		List<Vertex> solution = new ArrayList<Vertex>();
		solution.add(startingVertex);
		for(int i = 0; i < vertexList.size() - 2; i++) {
			Vertex currentVertex = solution.get(i);
			for(Edge edge: currentVertex.getEdgeList()) {
				if(!edge.isVisited()) {
					Vertex neighborVertex = vertexList.get(edge.getId());
					solution.add(neighborVertex);
					break;
				}
			}
		}
		solution.add(startingVertex);
		
		return solution;
	}
	
	public static int calculateTotalCost(List<Vertex> vertexes) {
		int totalCost = 0;
		
		for(int i = 0; i < vertexes.size() - 2; i++) {
			Vertex currentVertex = vertexes.get(i);
			Vertex nextVertex = vertexes.get(i + 1);
			for(Edge edge: currentVertex.getEdgeList()) {
				if(nextVertex.getId() == edge.getId()) {
					totalCost += edge.getCost();
					break;
				}
			}
		}
		
		return totalCost;
	}

}
