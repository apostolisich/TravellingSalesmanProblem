import java.util.List;

import xml.representation.classes.Graph;
import xml.representation.classes.Vertex;

public class Main {
	
	//���� ������� ��� �� 0 ����� �� 11 ��� ����������� ���� ������ �� �� ����������
	private static final int PROBLEM_TO_BE_USED = 9;
	
	/*
	 * ���� ������� ��� ������ benchmark problems ��� TSPLIB. ��� ����������� ��� ��� ������ ������� ������ ��������
	 * ����������� TSP.
	 */
	private static final String[] BENCHMARK_PROBLEMS_FILE_ARRAY = { 
			"bechmark_problems/a280.xml",
			"bechmark_problems/att48.xml",
			"bechmark_problems/att532.xml",
			"bechmark_problems/berlin52.xml",
			"bechmark_problems/bier127.xml",
			"bechmark_problems/brazil58.xml",
			"bechmark_problems/brg180.xml",
			"bechmark_problems/ch130.xml",
			"bechmark_problems/ch150.xml",
			"bechmark_problems/swiss42.xml",
			"bechmark_problems/tsp225.xml",
			"bechmark_problems/vm1084.xml"
	};
	
	//���� ������� �� ��� ��������� ������ ��� �� ���������� ���������� ��� �������� ������.
	private static final int[] BENCHMARK_PROBLEMS_BEST_KNOWN_SOLUTIONS = {
			2579,
			10628,
			27686,
			7542,
			118282,
			25395,
			1950,
			6110,
			6528,
			1273,
			3919,
			239297
	};

	public static void main(String[] args) {
		Graph graph = new Graph();
		Parser.fillTspGraphFromFile(graph, BENCHMARK_PROBLEMS_FILE_ARRAY[PROBLEM_TO_BE_USED]);
		
		System.out.println("Graph");
		System.out.println("---------");
		System.out.print(graph.toString());
		
		List<Vertex> initialSolution = Solver.nearestNeighborHeuristic(graph);
		int initialSolutionCost = Solver.calculateTotalCost(initialSolution);
		System.out.println("Initial Solution: " + initialSolutionCost);
		System.out.print("Optimal Solution: " + BENCHMARK_PROBLEMS_BEST_KNOWN_SOLUTIONS[PROBLEM_TO_BE_USED]);
	}

}
