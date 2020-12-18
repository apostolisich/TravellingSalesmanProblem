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
	 * Εκτελεί το Nearest Neighbor Heuristic για να υπολογίσει μια αρχική λύση του προβλήματος TSP.
	 * @param graph
	 * @return
	 */
	public static List<Vertex> applyNearestNeighbor(Graph graph) {
		NearestNeighbor nearestNeighbor = new NearestNeighbor(graph);
		return nearestNeighbor.execute();
	}
	
	/**
	 * Εκτελεί την μέθοδο Tabu Search στη δοσμένη αρχική λύση και επιστρέφει μια βελτιωμένη λύση
	 * του προβλήματος TSP.
	 * 
	 * @param graphSize το πλήθος των κόμβων του γράφου
	 * @param initialSolution μια αρχική λύση του προβλήματος
	 * @param initialSolutionCost το κόστος της δοσμένης αρχικής λύσης
	 * @return μια βελτιωμένη λύση
	 */
	public static List<Vertex> applyTabuSearch(List<Vertex> initialSolution, int graphSize) {
		TabuSearch tabuSearch = new TabuSearch(initialSolution, graphSize);
		return tabuSearch.execute();
	}
	
	/**
	 * Βοηθητική μέθοδος η οποία εκτυπώνει τον γράφο.
	 */
	public static void printGraph(Graph graph) {
		System.out.println("Graph");
		System.out.println("---------");
		System.out.print(graph.toString());
	}
	
	/**
	 * Βοηθητική μέθοδος η οποία υπολογίζει και εκτυπώνει τη δοσμένη λύση και το κόστος της.
	 * 
	 * @param solution η λύση προς εκτύπωση
	 * @param title ο τίτλος της λύσης
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
	 * Υπολογίζει το συνολικό κόστος της δοσμένης λύσης διαβάζοντας τους κόμβους της
	 * και προσθέτοντας τα κόστη τους.
	 * 
	 * @param solution μια λίστα με τους κόμβους που αποτελούν τη λύση του προβλήματος.
	 * @return το συνολικό κόστος της δοσμένης λύσης
	 */
	public static int calculateTotalCost(List<Vertex> solution) {
		int totalCost = 0;
		
		/*
		 * Ο παρακάτω βρόγχος περνάει από τους κόμβους της δοσμένης λίστας μέχρι και τον
		 * προτελευταίο (επειδή τους χειρίζεται ανά 2). Για τον κάθε ένα παίρνει τη
		 * λίστα με τις ακμές του και προσθέτει στο συνολικό κόστος το κόστος της ακμής
		 * που οδηγεί στον επόμενο κόμβο της λύσης.
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
