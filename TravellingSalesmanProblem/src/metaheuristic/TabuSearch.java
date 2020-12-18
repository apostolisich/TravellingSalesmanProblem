package metaheuristic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utilities.Solver;
import xml.representation.classes.Vertex;

public class TabuSearch {
	
	private List<Vertex> bestSolution;
	private int bestSolutionCost;
	private List<Vertex> currentSolution;
	private int currentSolutionCost;
	private int graphSize;
	private int iterationCount;
	private TabuList tabuList;
	
	public TabuSearch(List<Vertex> initialSolution, int graphSize) {
		this.graphSize = graphSize;
		/* Εδώ αφαιρώ τον τελευταίο κόμβο ο οποίος είναι ίδιος με τον πρώτο και είχε προστεθεί για
		 * τον υπολογισμό του κόστους και εκτύπωσητης λύσης */
		initialSolution.remove(graphSize - 1);
		this.bestSolution = initialSolution;
		this.bestSolutionCost = Solver.calculateTotalCost(initialSolution);
	}
	
	/**
	 * Αρχικοποιεί τις παραμέτρους που απαιτούνται για να τρέξουμε την tabu search.
	 */
	private void initialiseNeededParameters() {
		currentSolution = bestSolution;
		currentSolutionCost = bestSolutionCost;
		iterationCount = graphSize * 5;
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

	/**
	 * Υπολογίζει και επιστρέφει το κόστος της τρέχουσας αλλαγής.
	 * 
	 * @param vertexOnePosition η θέση του πρώτου κόμβου στη λίστα των κόμβων της τρέχουσας λύσης
	 * @param vertexTwoPosition η θέση του δεύτερο κόμβου στη λίστα των κόμβων της τρέχουσας λύσης
	 * @param solution η λίστα των κόμβων της τρέχουσας λύσης 
	 * @param solutionCost το κόστης της τρέχουσας λύσης
	 * @return το νέο κόστος της λύσης με την τρέχουσα ανταλλαγή
	 */
	private int getCurrentSwapCost(int vertexOnePosition, int vertexTwoPosition, List<Vertex> solution, int solutionCost) {
		Vertex vertexOne = solution.get(vertexOnePosition);
		Vertex vertexOnePrevious = vertexOnePosition == 0 ? null : solution.get(vertexOnePosition - 1);
		Vertex vertexOneNext = solution.get(vertexOnePosition + 1);
		
		Vertex vertexTwo = solution.get(vertexTwoPosition);
		Vertex vertexTwoPrevious = solution.get(vertexTwoPosition - 1);
		Vertex vertexTwoNext = vertexTwoPosition == graphSize - 2 ? null : solution.get(vertexTwoPosition + 1);
		
		int vertexesInitialCostContribution = calculateVertexesInitialCostContribution(vertexOnePrevious, vertexOne, vertexOneNext,
																					   vertexTwoPrevious, vertexTwo, vertexTwoNext);
		
		int vertexexNewCostContribution = calculateVertexesNewCostContribution(vertexOnePrevious, vertexOne, vertexOneNext,
																			   vertexTwoPrevious, vertexTwo, vertexTwoNext,
																			   vertexOnePosition, vertexTwoPosition);
		
		return solutionCost - vertexesInitialCostContribution + vertexexNewCostContribution;
	}
	
	/**
	 * Υπολογίζει και επιστρέφει τη συνολική συνεισφορά των κόμβων στο κόστος της αρχικής λύσης 
	 * πριν την ανταλλαγή.
	 * 
	 * @param vertexOnePrevious ο προηγούμενος κατά σειρά κόμβος του κόμβου ένα
	 * @param vertexOne ο κόμβος ένα
	 * @param vertexOneNext ο επόμενος κατά σειρά κόμβος του κόμβου ένα
	 * @param vertexTwoPrevious ο προηγούμενος κατά σειρά κόμβος του κόμβου δύο
	 * @param vertexTwo ο κόμβος δύο
	 * @param vertexTwoNext ο επόμενος κατά σειρά κόμβος του κόμβου δύο
	 * @return τη συνεισφορά των κόμβων δύο κόμβων πριν την ανταλλαγή
	 */
	private int calculateVertexesInitialCostContribution(Vertex vertexOnePrevious, Vertex vertexOne, Vertex vertexOneNext,
														 Vertex vertexTwoPrevious, Vertex vertexTwo, Vertex vertexTwoNext) {
		int vertexOnePrevCost = vertexOnePrevious == null ? 0 : getEdgeCostFromVertexToVertex(vertexOnePrevious, vertexOne.getId());
		int vertexOneNextCost = getEdgeCostFromVertexToVertex(vertexOne, vertexOneNext.getId());
		int vertexTwoPrevCost = getEdgeCostFromVertexToVertex(vertexTwoPrevious, vertexTwo.getId());
		int vertexTwoNextCost = vertexTwoNext == null ? 0 : getEdgeCostFromVertexToVertex(vertexTwo, vertexTwoNext.getId());
		
		return vertexOnePrevCost + vertexOneNextCost + vertexTwoPrevCost + vertexTwoNextCost;
	}
	
	/**
	 * Υπολογίζει και επιστρέφει τη συνολική συνεισφορά των κόμβων στο κόστος της αρχικής λύσης 
	 * μετά την ανταλλαγή.
	 * 
	 * @param vertexOnePrevious ο προηγούμενος κατά σειρά κόμβος του κόμβου ένα
	 * @param vertexOne ο κόμβος ένα
	 * @param vertexOneNext ο επόμενος κατά σειρά κόμβος του κόμβου ένα
	 * @param vertexTwoPrevious ο προηγούμενος κατά σειρά κόμβος του κόμβου δύο
	 * @param vertexTwo ο κόμβος δύο
	 * @param vertexTwoNext ο επόμενος κατά σειρά κόμβος του κόμβου δύο
	 * @param vertexOnePosition η θέση του πρώτου κόμβου στη λίστα των κόμβων της τρέχουσας λύσης
	 * @param vertexTwoPosition η θέση του δεύτερο κόμβου στη λίστα των κόμβων της τρέχουσας λύσης
	 * @return τη συνεισφορά των κόμβων δύο κόμβων μετά την ανταλλαγή
	 */
	private int calculateVertexesNewCostContribution(Vertex vertexOnePrevious, Vertex vertexOne, Vertex vertexOneNext,
			 										 Vertex vertexTwoPrevious, Vertex vertexTwo, Vertex vertexTwoNext,
			 										 int vertexOnePosition, int vertexTwoPosition) {
		int vertexOneSwapPrevCost = vertexOnePrevious == null ? 0: getEdgeCostFromVertexToVertex(vertexOnePrevious, vertexTwo.getId());
		int vertexOneSwapNextCost = 0;
		int vertexTwoSwapPrevCost = 0;
		if(vertexTwoPosition - vertexOnePosition == 1) {
			int vertexOneNextCost = getEdgeCostFromVertexToVertex(vertexOne, vertexOneNext.getId());
			vertexOneSwapNextCost = vertexOneNextCost;
			vertexTwoSwapPrevCost = vertexOneNextCost;
		} else {
			vertexOneSwapNextCost = getEdgeCostFromVertexToVertex(vertexTwo, vertexOneNext.getId());
			vertexTwoSwapPrevCost = getEdgeCostFromVertexToVertex(vertexTwoPrevious, vertexOne.getId());
		}
		int vertexTwoSwapNextCost = vertexTwoNext == null ? 0: getEdgeCostFromVertexToVertex(vertexOne, vertexTwoNext.getId());
		
		return vertexOneSwapPrevCost + vertexOneSwapNextCost + vertexTwoSwapPrevCost + vertexTwoSwapNextCost;
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
