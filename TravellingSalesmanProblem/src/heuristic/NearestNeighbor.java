package heuristic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xml.representation.classes.Edge;
import xml.representation.classes.Graph;
import xml.representation.classes.Vertex;

public class NearestNeighbor {
	
	private Graph graph;
	
	public NearestNeighbor(Graph graph) {
		this.graph = graph;
	}

	/**
	 * Uses the nearest neighbor heuristic to the given Graph and constructs an initial solution
	 * for the TSP problem.
	 * 
	 * @param graph the graph of the current TSP problem.
	 * @return a list of vertexes that represents the constructed solution
	 */
	public List<Vertex> execute() {
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

}
