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
		//Η λίστα με τους κόμβους του γράφου
		List<Vertex> graphVertexList = graph.getVertexList();
		//Η λίστα των κόμβων που θα αποτελέσουν την αρχική λύση
		List<Vertex> solution = new ArrayList<Vertex>();
		
		/*
		 * Παρακάτω επιλέγω μέσω της κλάσης Random έναν τυχαίο ακέραιο μέσα από το πλήθος
		 * των κόμβων και τον χρησιμοποιώ στη συνέχεια για να επιλέξω τον αντίστοιχο κόμβο.
		 * Στη συνέχεια προσθέτω τον κόμβο στη λίστα με τους κόμβους της λύσης και θέτω
		 * ότι τον έχουμε επισκεφθεί.
		 */
		int startingVertexId = new Random().nextInt(graph.size());
		Vertex startingVertex = graphVertexList.get(startingVertexId);
		solution.add(startingVertex);
		startingVertex.setVisited(true);
		
		/*
		 * Στον παρακάτω βρόγχο παίρνω κάθε φορά τον τελευταίο κόμβο που προστέθηκε στη λύση
		 * και ελέγχω τις ακμές του, παίρνοντας κάθε φορά τον κόμβο στον οποίο οδηγούν. Αν δεν 
		 * τον έχουμε επισκεφθεί, τον προσθέτω στη λύση και θέτω ότι τον έχουμε επισκεφθεί
		 * για να μην χρησιμοποιηθεί ξανα. Αν τον έχουμε επισκεφθεί, προχωράω στην επόμενη
		 * ακμή.
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
		//Εδώ προσθέτω ξανά στο τέλος της λύσης τον αρχικό κόμβο για να επιστρέψουμε σε αυτόν.
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
		 * Ο παρακάτω βρόγχος περνάει από τους κόμβους της δοσμένης λίστας μέχρι και τον
		 * προτελευταίο (επειδή τους χειρίζεται ανά 2). Για τον κάθε ένα παίρνει τη
		 * λίστα με τις ακμές του και προσθέτει στο συνολικό κόστος το κόστος της ακμής
		 * που οδηγεί στον επόμενο κόμβο της λύσης.
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
