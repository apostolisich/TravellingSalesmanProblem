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
	 * ����������� ��� ����������� ��� ����������� ��� �� �������� ��� tabu search.
	 */
	private void initialiseNeededParameters() {
		currentSolution = bestSolution;
		currentSolutionCost = bestSolutionCost;
		graphSize = graph.size();
		iterationCount = graphSize * 3;
		tabuList = new TabuList(graphSize);
	}
	
	/**
	 * ������� ��� Tabu Search ��� ���������� ��� ���������� ���� ��� ������������ ��� ����.
	 * 
	 * @return ��� ����� �� ���� ������� ��� ����������� �����
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
				//��� ������ ���� �� ��������, ���� �� �������� ��� ����� tabu ���� aspiration criteria.
				//��� || ������ ��� � ������ ��� �������������� ��� tabu list, ������ tabuList[i][j] == 0
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
	 * ���������� ���� ��� ������� ���������� �� ���� ���� ��� ����� ��� ����� ��� �� ����������� �����
	 * Swap. ������ ������� ���� �������� ��� ��������� ��� ����������� ��� ������� ��� ��������� ���������
	 * ���� tabu list
	 * 
	 * @param swap ��� ����������� ����� Swap �� ����� ������������ ���� ��� ������� 
	 * 		 	   ���� ��������.
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
	 * ���������� �� ������ ��� ����� ��� ������ ��� ��� ������� �������� ����� (currentVertex) ��� ���������
	 * ���� ����� �� id ��� �� �� ������� id (requestedVertexId).
	 * 
	 * @param currentVertex � ������ ������ ��� ��� ����� �������� � ����
	 * @param requestedVertexId �� id ��� ������ ��� �� ��������� � ����
	 * @return �� ������ ��� ���������� �����
	 */
	private int getEdgeCostFromNodeToNode(Vertex currentVertex, int requestedVertexId) {
		return currentVertex.getEdgeList().stream().filter(edge -> requestedVertexId == edge.getId()).findFirst().get().getCost();
	}
}
