package utilities;
import java.util.List;

import heuristic.NearestNeighbor;
import metaheuristic.TabuSearch;
import xml.representation.classes.Edge;
import xml.representation.classes.Graph;
import xml.representation.classes.Vertex;

public class Solver {
	
	private Solver() { }
	
	/**
	 * ������� �� Nearest Neighbor Heuristic ��� �� ���������� ��� ������ ���� ��� ����������� TSP.
	 * @param graph
	 * @return
	 */
	public static List<Vertex> applyNearestNeighbor(Graph graph) {
		NearestNeighbor nearestNeighbor = new NearestNeighbor(graph);
		return nearestNeighbor.execute();
	}
	
	/**
	 * ������� ��� ������ Tabu Search ��� ������� ������ ���� ��� ���������� ��� ���������� ����
	 * ��� ����������� TSP.
	 * 
	 * @param graphSize �� ������ ��� ������ ��� ������
	 * @param initialSolution ��� ������ ���� ��� �����������
	 * @param initialSolutionCost �� ������ ��� �������� ������� �����
	 * @return ��� ���������� ����
	 */
	public static List<Vertex> applyTabuSearch(List<Vertex> initialSolution, int graphSize) {
		TabuSearch tabuSearch = new TabuSearch(initialSolution, graphSize);
		return tabuSearch.execute();
	}
	
	/**
	 * ��������� ������� � ����� ��������� ��� �����.
	 */
	public static void printGraph(Graph graph) {
		System.out.println("Graph");
		System.out.println("---------");
		System.out.print(graph.toString());
	}
	
	/**
	 * ��������� ������� � ����� ���������� ��� ��������� �� ������� ���� ��� �� ������ ���.
	 * 
	 * @param solution � ���� ���� ��������
	 * @param title � ������ ��� �����
	 */
	public static void calculateAndPrintSolutionInfo(List<Vertex> solution, String title) {
		int initialSolutionCost = calculateTotalCost(solution);
		
		System.out.println(title + ": " + initialSolutionCost);
		for(Vertex vertex: solution) {
			System.out.print(vertex.getId() + " ");
		}
		System.out.println("\n");
	}
	
	/**
	 * ���������� �� �������� ������ ��� �������� ����� ����������� ���� ������� ���
	 * ��� ������������ �� ����� ����.
	 * 
	 * @param solution ��� ����� �� ���� ������� ��� ��������� �� ���� ��� �����������.
	 * @return �� �������� ������ ��� �������� �����
	 */
	public static int calculateTotalCost(List<Vertex> solution) {
		int totalCost = 0;
		
		/*
		 * � �������� ������� ������� ��� ���� ������� ��� �������� ������ ����� ��� ���
		 * ������������ (������ ���� ���������� ��� 2). ��� ��� ���� ��� ������� ��
		 * ����� �� ��� ����� ��� ��� ��������� ��� �������� ������ �� ������ ��� �����
		 * ��� ������ ���� ������� ����� ��� �����.
		 */
		for(int i = 0; i < solution.size() - 2; i++) {
			Vertex currentVertex = solution.get(i);
			Vertex nextVertex = solution.get(i + 1);
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
