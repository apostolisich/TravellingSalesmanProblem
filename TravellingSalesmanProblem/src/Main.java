import java.util.List;

import utilities.Parser;
import utilities.Solver;
import xml.representation.classes.Graph;
import xml.representation.classes.Vertex;

public class Main {
	
	//Ένας αριθμός από το 0 μέχρι το 11 που αντιστοιχεί στον πίνακα με τα προβλήματα
	private static final int PROBLEM_TO_BE_USED = 1;
	
	/*
	 * Ένας πίνακας από μερικά benchmark problems της TSPLIB. Τον δημιούργησα για την εύκολη επιλογή μεταξύ διαφόρων
	 * προβλημάτων TSP.
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
	
	//Ένας πίνακας με τις καλύτερες λύσεις για τα αντίστοιχα προβλήματα του παραπάνω πίνακα.
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
		
		List<Vertex> initialSolution = Solver.nearestNeighborHeuristic(graph);
//		printGraph(graph);
		calculateAndPrintSolutionInfo(initialSolution, "Initial solution");
		initialSolution.remove(graph.size() - 1);
		
		List<Vertex> improvedSolution = Solver.applyTabuSearch(graph, initialSolution, Solver.calculateTotalCost(initialSolution));
		calculateAndPrintSolutionInfo(improvedSolution, "Improved solution");
		
		System.out.println("Optimal Solution: " + BENCHMARK_PROBLEMS_BEST_KNOWN_SOLUTIONS[PROBLEM_TO_BE_USED]);
	}
	
	/**
	 * Helper method that prints the given Graph.
	 */
	private static void printGraph(Graph graph) {
		System.out.println("Graph");
		System.out.println("---------");
		System.out.print(graph.toString());
	}
	
	/**
	 * Helper method that calculates the cost of the 
	 * @param graph
	 * @param initialSolution
	 */
	private static void calculateAndPrintSolutionInfo(List<Vertex> solution, String title) {
		int initialSolutionCost = Solver.calculateTotalCost(solution);
		
		System.out.println(title + ": " + initialSolutionCost);
		for(Vertex vertex: solution) {
			System.out.print(vertex.getId() + " ");
		}
		System.out.println("\n");
	}

}
