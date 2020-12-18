package utilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import metaheuristic.TabuSearch;
import xml.representation.classes.Edge;
import xml.representation.classes.Graph;
import xml.representation.classes.Vertex;

public class Solver {
	
	private Solver() { }
	
	/**
	 * Uses the nearest neighbor heuristic to the given Graph and constructs an initial solution
	 * for the TSP problem.
	 * 
	 * @param graph the graph of the current TSP problem.
	 * @return a list of vertexes that represents the constructed solution
	 */
	public static List<Vertex> nearestNeighborHeuristic(Graph graph) {
		//� ����� �� ���� ������� ��� ������
		List<Vertex> graphVertexList = graph.getVertexList();
		//� ����� ��� ������ ��� �� ����������� ��� ������ ����
		List<Vertex> solution = new ArrayList<Vertex>();
		
		/*
		 * �������� ������� ���� ��� ������ Random ���� ������ ������� ���� ��� �� ������
		 * ��� ������ ��� ��� ����������� ��� �������� ��� �� ������� ��� ���������� �����.
		 * ��� �������� �������� ��� ����� ��� ����� �� ���� ������� ��� ����� ��� ����
		 * ��� ��� ������ ����������.
		 */
		int startingVertexId = new Random().nextInt(graph.size());
		Vertex startingVertex = graphVertexList.get(startingVertexId);
		solution.add(startingVertex);
		startingVertex.setVisited(true);
		
		/*
		 * ���� �������� ������ ������ ���� ���� ��� ��������� ����� ��� ���������� ��� ����
		 * ��� ������ ��� ����� ���, ���������� ���� ���� ��� ����� ���� ����� �������. �� ��� 
		 * ��� ������ ����������, ��� �������� ��� ���� ��� ���� ��� ��� ������ ����������
		 * ��� �� ��� �������������� ����. �� ��� ������ ����������, �������� ���� �������
		 * ����.
		 */
		for(int i = 0; i < graphVertexList.size() - 2; i++) {
			Vertex currentVertex = solution.get(i);
			for(Edge edge: currentVertex.getEdgeList()) {
				Vertex neighborVertex = graphVertexList.get(edge.getId());
				if(!neighborVertex.isVisited()) {
					solution.add(neighborVertex);
					neighborVertex.setVisited(true);
					break;
				}
			}
		}
		//��� �������� ���� ��� ����� ��� ����� ��� ������ ����� ��� �� ������������ �� �����.
		solution.add(startingVertex);
		
		return solution;
	}
	
	/**
	 * Calculates the total cost by parsing the vertexes of the computed solution
	 * and adding their costs.
	 * 
	 * @param vertexes a list of the solution vertexes
	 * @return the total cost of the given solution
	 */
	public static int calculateTotalCost(List<Vertex> vertexes) {
		int totalCost = 0;
		
		/*
		 * � �������� ������� ������� ��� ���� ������� ��� �������� ������ ����� ��� ���
		 * ������������ (������ ���� ���������� ��� 2). ��� ��� ���� ��� ������� ��
		 * ����� �� ��� ����� ��� ��� ��������� ��� �������� ������ �� ������ ��� �����
		 * ��� ������ ���� ������� ����� ��� �����.
		 */
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

	public static List<Vertex> applyTabuSearch(Graph graph, List<Vertex> initialSolution, int initialSolutionCost) {
		TabuSearch tabuSearch = new TabuSearch(graph, initialSolution, initialSolutionCost);
		return tabuSearch.execute();
	}
}
