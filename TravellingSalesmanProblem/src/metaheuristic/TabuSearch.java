package metaheuristic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xml.representation.classes.Edge;
import xml.representation.classes.Graph;
import xml.representation.classes.Vertex;

public class TabuSearch {
	
	private Graph graph;
	private List<Vertex> bestSolution;
	private int bestSolutionCost;
	private List<Vertex> currentSolution;
	private int currentSolutionCost;
	private int graphSize;
	private int iterationCount;
	private TabuList tabuList;
	
	public TabuSearch(Graph graph, List<Vertex> initialSolution, int initialSolutionCost) {
		this.graph = graph;
		this.bestSolution = initialSolution;
		this.bestSolutionCost = initialSolutionCost;
	}
	
	/**
	 * Αρχικοποιεί τις παραμέτρους που απαιτούνται για να τρέξουμε την tabu search.
	 */
	private void initialiseNeededParameters() {
		currentSolution = bestSolution;
		currentSolutionCost = bestSolutionCost;
		graphSize = graph.size();
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
		
		int consecutiveWorseSolutionsCount = 0;
		while(iterationCount > 0) {
			List<Swap> currentSwapNeighborhoud = new ArrayList<Swap>();
			
			for(int i = 0; i < graphSize - 2; i++) {
				for(int j = i + 1; j < graphSize - 1; j++) {
					int currentSwapCost = getCurrentSwapCost(i, j, currentSolution, currentSolutionCost);
					Swap currentSwap = new Swap(i, j, currentSwapCost);
					currentSwapNeighborhoud.add(currentSwap);
				}
			}
			Collections.sort(currentSwapNeighborhoud);
			
			for(Swap swap: currentSwapNeighborhoud) {
				//Εδώ πιάνει είτε το καλύτερο, είτε το καλύτερο ενώ είναι tabu λόγω aspiration criteria.
				//Στο || έχουμε ότι η κίνηση δεν περιλαμβάνεται στο tabu list, δηλαδη tabuList[i][j] == 0
				if((swap.getCost() < currentSolutionCost) || (tabuList.getTabuValueForPair(swap.getNodeOnePosition(), swap.getNodeTwoPosition()) == 0)) {
					currentSolutionCost = swap.getCost();
					doSwap(swap);
					break;
				}
			}
			
			if(currentSolutionCost < bestSolutionCost) {
				bestSolution = currentSolution;
				bestSolutionCost = currentSolutionCost;
				consecutiveWorseSolutionsCount = 0;
			} else {
				if(consecutiveWorseSolutionsCount == 3)
					break;
				
				consecutiveWorseSolutionsCount++;
			}
			
			iterationCount--;
		}
		
		bestSolution.add(bestSolution.get(0));
		return bestSolution;
	}

	private int getCurrentSwapCost(int nodeOnePosition, int nodeTwoPosition, List<Vertex> solution, int solutionCost) {
		Vertex nodeOne = solution.get(nodeOnePosition);
		Vertex nodeOnePrevious = nodeOnePosition == 0 ? null : solution.get(nodeOnePosition - 1);
		Vertex nodeOneNext = solution.get(nodeOnePosition + 1);
		
		Vertex nodeTwo = solution.get(nodeTwoPosition);
		Vertex nodeTwoPrevious = solution.get(nodeTwoPosition - 1);
		Vertex nodeTwoNext = nodeTwoPosition == graphSize - 2 ? null : solution.get(nodeTwoPosition + 1);
		
		int nodeOnePrevCost = nodeOnePrevious == null ? 0 : getEdgeCostFromNodeToNode(nodeOnePrevious, nodeOne.getId());
		int nodeOneNextCost = getEdgeCostFromNodeToNode(nodeOne, nodeOneNext.getId());
		int nodeTwoPrevCost = getEdgeCostFromNodeToNode(nodeTwoPrevious, nodeTwo.getId());
		int nodeTwoNextCost = nodeTwoNext == null ? 0 : getEdgeCostFromNodeToNode(nodeTwo, nodeTwoNext.getId());
		int nodesInitialCostContribution = nodeOnePrevCost + nodeOneNextCost + nodeTwoPrevCost + nodeTwoNextCost;
		
		int nodeOneSwapPrevCost = nodeOnePrevious == null ? 0: getEdgeCostFromNodeToNode(nodeOnePrevious, nodeTwo.getId());
		int nodeOneSwapNextCost, nodeTwoSwapPrevCost = 0;
		if(nodeTwoPosition - nodeOnePosition == 1) {
			nodeOneSwapNextCost = nodeOneNextCost;
			nodeTwoSwapPrevCost = nodeOneNextCost;
		} else {
			nodeOneSwapNextCost = getEdgeCostFromNodeToNode(nodeTwo, nodeOneNext.getId());
			nodeTwoSwapPrevCost = getEdgeCostFromNodeToNode(nodeTwoPrevious, nodeOne.getId());
		}
		int nodeTwoSwapNextCost = nodeTwoNext == null ? 0: getEdgeCostFromNodeToNode(nodeOne, nodeTwoNext.getId());
		int nodesNewCostContribution = nodeOneSwapPrevCost + nodeOneSwapNextCost + nodeTwoSwapPrevCost + nodeTwoSwapNextCost;
		
		return solutionCost - nodesInitialCostContribution + nodesNewCostContribution;
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
		int nodeOnePosition = swap.getNodeOnePosition();
		int nodeTwoPosition = swap.getNodeTwoPosition();
		
		Vertex temp = currentSolution.get(nodeOnePosition);
		currentSolution.set(nodeOnePosition, currentSolution.get(nodeTwoPosition));
		currentSolution.set(nodeTwoPosition, temp);
		
		tabuList.updateTabuCounters();
		tabuList.updateTenure(nodeOnePosition, nodeTwoPosition);
	}
	
	/**
	 * Επιστρέφει το κόστος της ακμής που ξεκινά από τον δοσμένο τρέχοντα κόμβο (currentVertex) και καταλήγει
	 * στον κόμβο με id ίσο με το δοσμένο id (requestedVertexId).
	 * 
	 * @param currentVertex ο τρέχων κόμβος από τον οποίο ξεκινάει η ακμή
	 * @param requestedVertexId το id του κόμβου που θα καταλήγει η ακμή
	 * @return το κόστος της ζητούμενης ακμής
	 */
	private int getEdgeCostFromNodeToNode(Vertex currentVertex, int requestedVertexId) {
		return currentVertex.getEdgeList().stream().filter(edge -> requestedVertexId == edge.getId()).findFirst().get().getCost();
	}
}
