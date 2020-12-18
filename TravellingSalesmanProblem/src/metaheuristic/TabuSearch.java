package metaheuristic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xml.representation.classes.Graph;
import xml.representation.classes.Vertex;

public class TabuSearch {
	
	private List<Vertex> bestSolution;
	private int bestSolutionCost;
	private List<Vertex> currentSolution;
	private int currentSolutionCost;
	private int graphSize;
	private int iterationCount;
	private TabuList tabuList;
	
	public TabuSearch(int graphSize, List<Vertex> initialSolution, int initialSolutionCost) {
		this.graphSize = graphSize;
		this.bestSolution = initialSolution;
		this.bestSolutionCost = initialSolutionCost;
	}
	
	/**
	 * Αρχικοποιεί τις παραμέτρους που απαιτούνται για να τρέξουμε την tabu search.
	 */
	private void initialiseNeededParameters() {
		currentSolution = bestSolution;
		currentSolutionCost = bestSolutionCost;
		iterationCount = graphSize * 3;
		tabuList = new TabuList(graphSize);
	}
	
	/**
	 * Εκτελεί την Tabu Search και επιστρέφει την βελτιωμένη λύση που υπολογίζεται από αυτή.
	 * 
	 * @return μια λίστα με τους κόμβους της βελτιωμένης λύσης
	 */
	public List<Vertex> execute() {
		initialiseNeededParameters();
		
		/*
		 * Ως κριτήριο τερματισμού έχουμε είτε το iterationCount είτε την περίπτωση να μη βρούμε
		 * καλύτερη λύση 3 συνεχόμενες φορές.
		 */
		int consecutiveWorseSolutionsCount = 0;
		while(iterationCount > 0) {
			List<Swap> currentSwapsList = getCurrentSwapsList();
			
			performSwapsOnCurrentSolution(currentSwapsList);
			
			updateConsecutiveWorseSolutionsCount(consecutiveWorseSolutionsCount);
			if(consecutiveWorseSolutionsCount == 3)
				break;
			
			iterationCount--;
		}
		
		//Εδώ προσθέτω ξανά στο τέλος της λύσης τον αρχικό κόμβο για να επιστρέψουμε σε αυτόν.
		bestSolution.add(bestSolution.get(0));
		
		return bestSolution;
	}

	/**
	 * Επιστρέφει μια λίστα με όλα τα πιθανά swap που μπορούν να γίνουν στην τρέχουσα επανάληψη τα
	 * οποία και ταξινομεί ως προς το κόστος σε αύξουσα σειρά.
	 * 
	 * @return τη λίστα με όλα τα πιθανά swap
	 */
	private List<Swap> getCurrentSwapsList() {
		List<Swap> currentSwapNeighborhoud = new ArrayList<Swap>();
		
		for(int i = 0; i < graphSize - 2; i++) {
			for(int j = i + 1; j < graphSize - 1; j++) {
				int currentSwapCost = getCurrentSwapCost(i, j, currentSolution, currentSolutionCost);
				Swap currentSwap = new Swap(i, j, currentSwapCost);
				currentSwapNeighborhoud.add(currentSwap);
			}
		}
		Collections.sort(currentSwapNeighborhoud);
		
		return currentSwapNeighborhoud;
	}
	
	/**
	 * Ελέγχει όλα τα swap της δοσμένης λίστας και επιλέγει ποια θα εφαρμόσει ανάλογα με το αν παράγουν
	 * καλύτερη λύση (ή καλύτερη λύση με εφαρμογή του aspiration criteria) ή αν αυξάνουν τη λύση και δεν
	 * περιλαμβάνονται στο tabu list προκειμένου να απεγκλωβιστούμε από τοπικό βέλτιστο. 
	 * 
	 * @param currentSwapsList η λίστα με όλα τα πιθανά swap στην τρέχουσα επανάληψη
	 */
	private void performSwapsOnCurrentSolution(List<Swap> currentSwapsList) {
		//Η λίστα με τα swap είναι ταξινομημένη σε αύξουσα σειρά οπότε πάντα ξεκινάμε τον έλεγχο από τα καλύτερα swap.
		for(Swap swap: currentSwapsList) {
			/*
			 * Στον έλεγχο αριστερά του || το swap επιλέγεται αν η νέα λύση είναι καλύτερη ακόμα και αν
			 * το swap περιλαμβάνεται στην tabu list λόγω του aspiration criteria.
			 * Στον έλεγχο δεξιά του || επιλέγουμε το swap μόνο αν η λύση είναι χειρότερη αλλά δεν
			 * περιλαμβάνεται στην tabu list.
			 */
			if((swap.getCost() < currentSolutionCost) || (tabuList.getTabuValueForPair(swap.getNodeOnePosition(), swap.getNodeTwoPosition()) == 0)) {
				currentSolutionCost = swap.getCost();
				doSwap(swap);
				break;
			}
		}
	}
	
	/**
	 * Αυξάνει το πλήθος των συνεχόμενων φορών που δεν έχει βρεθεί καλύτερη λύση ή τη θέτει ίση με 0 αν βρεθεί
	 * μέσω της δοσμένης μεταβλητής consecutiveWorseSolutionsCount.
	 * 
	 * @param consecutiveWorseSolutionsCount το πλήθος των συνεχόμενων φορών που δεν έχει βρεθεί καλύτερη λύση
	 */
	private void updateConsecutiveWorseSolutionsCount(int consecutiveWorseSolutionsCount) {
		if(currentSolutionCost < bestSolutionCost) {
			bestSolution = currentSolution;
			bestSolutionCost = currentSolutionCost;
			consecutiveWorseSolutionsCount = 0;
		} else {
			consecutiveWorseSolutionsCount++;
		}
	}

	private int getCurrentSwapCost(int vertexOnePosition, int vertexTwoPosition, List<Vertex> solution, int solutionCost) {
		Vertex vertexOne = solution.get(vertexOnePosition);
		Vertex vertexOnePrevious = vertexOnePosition == 0 ? null : solution.get(vertexOnePosition - 1);
		Vertex vertexOneNext = solution.get(vertexOnePosition + 1);
		
		Vertex vertexTwo = solution.get(vertexTwoPosition);
		Vertex vertexTwoPrevious = solution.get(vertexTwoPosition - 1);
		Vertex nodeTwoNext = vertexTwoPosition == graphSize - 2 ? null : solution.get(vertexTwoPosition + 1);
		
		int vertexOnePrevCost = vertexOnePrevious == null ? 0 : getEdgeCostFromVertexToVertex(vertexOnePrevious, vertexOne.getId());
		int vertexOneNextCost = getEdgeCostFromVertexToVertex(vertexOne, vertexOneNext.getId());
		int vertexTwoPrevCost = getEdgeCostFromVertexToVertex(vertexTwoPrevious, vertexTwo.getId());
		int vertexTwoNextCost = nodeTwoNext == null ? 0 : getEdgeCostFromVertexToVertex(vertexTwo, nodeTwoNext.getId());
		int vertexesInitialCostContribution = vertexOnePrevCost + vertexOneNextCost + vertexTwoPrevCost + vertexTwoNextCost;
		
		int vertexOneSwapPrevCost = vertexOnePrevious == null ? 0: getEdgeCostFromVertexToVertex(vertexOnePrevious, vertexTwo.getId());
		int vertexOneSwapNextCost, vertexTwoSwapPrevCost = 0;
		if(vertexTwoPosition - vertexOnePosition == 1) {
			vertexOneSwapNextCost = vertexOneNextCost;
			vertexTwoSwapPrevCost = vertexOneNextCost;
		} else {
			vertexOneSwapNextCost = getEdgeCostFromVertexToVertex(vertexTwo, vertexOneNext.getId());
			vertexTwoSwapPrevCost = getEdgeCostFromVertexToVertex(vertexTwoPrevious, vertexOne.getId());
		}
		int vertexTwoSwapNextCost = nodeTwoNext == null ? 0: getEdgeCostFromVertexToVertex(vertexOne, nodeTwoNext.getId());
		int vertexesNewCostContribution = vertexOneSwapPrevCost + vertexOneSwapNextCost + vertexTwoSwapPrevCost + vertexTwoSwapNextCost;
		
		return solutionCost - vertexesInitialCostContribution + vertexesNewCostContribution;
	}
	
	/**
	 * Ανταλλάσει τους δύο κόμβους παίρνωντας τη θέση τους στη λίστα της λύσης από το αντικείμενο τύπου
	 * Swap. Επίσης μειώνει τους μετρητές των ζευγαριών και αρχικοποιεί τον μετρητή του τρέχοντος ζευγαριού
	 * στην tabu list
	 * 
	 * @param swap ένα αντικείμενο τύπου Swap το οποίο περιλαμβάνει τους δύο κόμβους 
	 * 		 	   προς εναλλαγή.
	 */
	private void doSwap(Swap swap) {
		int vertexOnePosition = swap.getNodeOnePosition();
		int vertexTwoPosition = swap.getNodeTwoPosition();
		
		Vertex temp = currentSolution.get(vertexOnePosition);
		currentSolution.set(vertexOnePosition, currentSolution.get(vertexTwoPosition));
		currentSolution.set(vertexTwoPosition, temp);
		
		tabuList.updateTabuCounters();
		tabuList.updateTenure(vertexOnePosition, vertexTwoPosition);
	}
	
	/**
	 * Επιστρέφει το κόστος της ακμής που ξεκινά από τον δοσμένο τρέχοντα κόμβο (currentVertex) και καταλήγει
	 * στον κόμβο με id ίσο με το δοσμένο id (requestedVertexId).
	 * 
	 * @param currentVertex ο τρέχων κόμβος από τον οποίο ξεκινάει η ακμή
	 * @param requestedVertexId το id του κόμβου που θα καταλήγει η ακμή
	 * @return το κόστος της ζητούμενης ακμής
	 */
	private int getEdgeCostFromVertexToVertex(Vertex currentVertex, int requestedVertexId) {
		return currentVertex.getEdgeList().stream().filter(edge -> requestedVertexId == edge.getId()).findFirst().get().getCost();
	}
}
