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
		 * τον υπολογισμό του κόστους της αρχικής λύσης και εκτύπωση της */
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
				int newSolutionCost = getSolutionCostAfterSwap(i, j, currentSolution, currentSolutionCost);
				Swap currentSwap = new Swap(i, j, newSolutionCost);
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
			int newSolutionCost = swap.getNewSolutionCost();
			if((newSolutionCost < currentSolutionCost) 
					|| (tabuList.getTabuValueForPair(swap.getVertexOnePosition(), swap.getVertexTwoPosition()) == 0)) {
				currentSolutionCost = newSolutionCost;
				doSwap(swap);
				break;
			}
		}
	}

	/**
	 * Υπολογίζει και επιστρέφει το νέο κόστος της λύσης μετά την τρέχουσα αλλαγή.
	 * 
	 * @param vertexOnePosition η θέση του πρώτου κόμβου στη λίστα των κόμβων της τρέχουσας λύσης
	 * @param vertexTwoPosition η θέση του δεύτερο κόμβου στη λίστα των κόμβων της τρέχουσας λύσης
	 * @param solution η λίστα των κόμβων της τρέχουσας λύσης 
	 * @param solutionCost το κόστης της τρέχουσας λύσης
	 * @return το νέο κόστος της λύσης μετά την τρέχουσα ανταλλαγή
	 */
	private int getSolutionCostAfterSwap(int vertexOnePosition, int vertexTwoPosition, List<Vertex> solution, int solutionCost) {
		Vertex vertexOne = solution.get(vertexOnePosition);
		/* Έχω τον έλεγχο με το 0 για την περίπτωση που ο vertexOne είναι ο πρώτος κόμβος της λύσης,
		 * επομένως δεν θα έχει προηγούμενο. */
		Vertex vertexOnePrevious = vertexOnePosition == 0 ? null : solution.get(vertexOnePosition - 1);
		Vertex vertexOneNext = solution.get(vertexOnePosition + 1);
		
		Vertex vertexTwo = solution.get(vertexTwoPosition);
		Vertex vertexTwoPrevious = solution.get(vertexTwoPosition - 1);
		/* Έχω τον έλεγχο με το graphSize - 2 για την περίπτωση που ο vertexTwo είναι ο τελευταίος κόμβος της λύσης,
		 * επομένως δεν θα έχει επόμενο. */
		Vertex vertexTwoNext = vertexTwoPosition == graphSize - 2 ? null : solution.get(vertexTwoPosition + 1);
		
		int vertexesInitialCostContribution = calculateVertexesInitialCostContribution(vertexOnePrevious, vertexOne, vertexOneNext,
																					   vertexTwoPrevious, vertexTwo, vertexTwoNext);
		
		int vertexesNewCostContribution = calculateVertexesNewCostContribution(vertexOnePrevious, vertexOne, vertexOneNext,
																			   vertexTwoPrevious, vertexTwo, vertexTwoNext,
																			   vertexOnePosition, vertexTwoPosition);
		
		return solutionCost - vertexesInitialCostContribution + vertexesNewCostContribution;
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
		/*
		 * Οι γραμμές παρακάτω υπολογίζουν το κόστος μεταξύ του προηγούμενου και του επόμενου κόμβου για κάθε
		 * έναν από τους δύο κόμβους προς αλλαγή.
		 */
		int vertexOnePrevCost = getEdgeCostFromVertexToVertex(vertexOnePrevious, vertexOne.getId());
		int vertexOneNextCost = getEdgeCostFromVertexToVertex(vertexOne, vertexOneNext.getId());
		int vertexTwoPrevCost = getEdgeCostFromVertexToVertex(vertexTwoPrevious, vertexTwo.getId());
		int vertexTwoNextCost = getEdgeCostFromVertexToVertex(vertexTwo, vertexTwoNext.getId());
		
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
		/*
		 * Υπολογίζεται το κόστος μεταξύ του προηγούμενου και του επόμενου κόμβου για κάθε έναν από τους δύο
		 * κόμβους μετά την αλλαγή. Η αλλαγή εδώ δεν πραγματοποείται, αλλά υπολόγιζεται το κόστος με βάση
		 * τη διάταξη των κόμβων αν τελικά πραγματοποιούνταν.
		 */
		int vertexOnePrevCostAfterSwap = getEdgeCostFromVertexToVertex(vertexOnePrevious, vertexTwo.getId());
		
		int vertexOneNextCostAfterSwap = 0;
		int vertexTwoPrevCostAfterSwap = 0;
		//Ο παρακάτω έλεγχος υπάρχει για να χειριζόμαστε τις περιπτώσεις που οι κόμβοι είναι ο ένας δίπλα στον άλλο.
		if(vertexTwoPosition - vertexOnePosition == 1) {
			int vertexOneNextCost = getEdgeCostFromVertexToVertex(vertexOne, vertexOneNext.getId());
			vertexOneNextCostAfterSwap = vertexOneNextCost;
			vertexTwoPrevCostAfterSwap = vertexOneNextCost;
		} else {
			vertexOneNextCostAfterSwap = getEdgeCostFromVertexToVertex(vertexTwo, vertexOneNext.getId());
			vertexTwoPrevCostAfterSwap = getEdgeCostFromVertexToVertex(vertexTwoPrevious, vertexOne.getId());
		}
		
		int vertexTwoNextCostAfterSwap = getEdgeCostFromVertexToVertex(vertexOne, vertexTwoNext.getId());
		
		return vertexOnePrevCostAfterSwap + vertexOneNextCostAfterSwap + vertexTwoPrevCostAfterSwap + vertexTwoNextCostAfterSwap;
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
		if(currentVertex == null) {
			return 0;
		}
		
		return currentVertex.getEdgeList().stream().filter(edge -> requestedVertexId == edge.getId()).findFirst().get().getCost();
	}
	
	/**
	 * Ανταλλάσει τους δύο κόμβους παίρνοντας τη θέση τους στη λίστα της λύσης από το αντικείμενο τύπου
	 * Swap. Επίσης μειώνει τους μετρητές των ζευγαριών και αρχικοποιεί τον μετρητή του τρέχοντος ζευγαριού
	 * στην tabu list
	 * 
	 * @param swap ένα αντικείμενο τύπου Swap το οποίο περιλαμβάνει τους δύο κόμβους 
	 * 		 	   προς εναλλαγή.
	 */
	private void doSwap(Swap swap) {
		int vertexOnePosition = swap.getVertexOnePosition();
		int vertexTwoPosition = swap.getVertexTwoPosition();
		
		Vertex vertexOne = currentSolution.get(vertexOnePosition);
		Vertex vertexTwo = currentSolution.get(vertexTwoPosition);
		currentSolution.set(vertexOnePosition, vertexTwo);
		currentSolution.set(vertexTwoPosition, vertexOne);
		
		tabuList.updateTabuCounters();
		tabuList.updateTenure(vertexOnePosition, vertexTwoPosition);
	}
	
	/**
	 * Αυξάνει το πλήθος των συνεχόμενων φορών που δεν έχει βρεθεί καλύτερη λύση ή τη θέτει ίση με 0 αν βρεθεί
	 * μέσω της δοσμένης μεταβλητής consecutiveWorseSolutionsCount.
	 * 
	 * @param consecutiveWorseSolutionsCount το πλήθος των συνεχόμενων φορών που δεν έχει βρεθεί καλύτερη λύση
	 */
	private void updateConsecutiveWorseSolutionsCount(int consecutiveWorseSolutionsCount) {
		if(isCurrentSolutionBetter()) {
			consecutiveWorseSolutionsCount = 0;
		} else {
			consecutiveWorseSolutionsCount++;
		}
	}
	
	/**
	 * Ελέγχει αν η τρέχουσα λύση είναι καλύτερη από την μέχρι στιγμής καλύτερη και αν είναι ορίζει την
	 * τρέχουσα ως βέλτιστη και επιστρέφει true. Αν δεν είναι καλύτερη, επιστρέφει false.
	 * 
	 * @return true αν η τρέχουσα λύση είναι καλύτερη από τη βέλτιστη και false διαφορετικά.
	 */
	private boolean isCurrentSolutionBetter() {
		if(currentSolutionCost < bestSolutionCost) {
			bestSolution = currentSolution;
			bestSolutionCost = currentSolutionCost;
			return true;
		}
		
		return false;
	}
}
