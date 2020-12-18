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
		/* ��� ������ ��� ��������� ����� � ������ ����� ����� �� ��� ����� ��� ���� ��������� ���
		 * ��� ���������� ��� ������� ��� ����������� ����� */
		initialSolution.remove(graphSize - 1);
		this.bestSolution = initialSolution;
		this.bestSolutionCost = Solver.calculateTotalCost(initialSolution);
	}
	
	/**
	 * ����������� ��� ����������� ��� ����������� ��� �� �������� ��� tabu search.
	 */
	private void initialiseNeededParameters() {
		currentSolution = bestSolution;
		currentSolutionCost = bestSolutionCost;
		iterationCount = graphSize * 5;
		tabuList = new TabuList(graphSize);
	}
	
	/**
	 * ������� ��� Tabu Search ��� ���������� ��� ���������� ���� ��� ������������ ��� ����.
	 * 
	 * @return ��� ����� �� ���� ������� ��� ����������� �����
	 */
	public List<Vertex> execute() {
		initialiseNeededParameters();
		
		/*
		 * �� �������� ����������� ������ ���� �� iterationCount ���� ��� ��������� �� �� ������
		 * �������� ���� 3 ����������� �����.
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
		
		//��� �������� ���� ��� ����� ��� ����� ��� ������ ����� ��� �� ������������ �� �����.
		bestSolution.add(bestSolution.get(0));
		
		return bestSolution;
	}

	/**
	 * ���������� ��� ����� �� ��� �� ������ swap ��� ������� �� ������ ���� �������� ��������� ��
	 * ����� ��� ��������� �� ���� �� ������ �� ������� �����.
	 * 
	 * @return �� ����� �� ��� �� ������ swap
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
	 * ������� ��� �� swap ��� �������� ������ ��� �������� ���� �� ��������� ������� �� �� �� ��������
	 * �������� ���� (� �������� ���� �� �������� ��� aspiration criteria) � �� �������� �� ���� ��� ���
	 * ��������������� ��� tabu list ����������� �� ��������������� ��� ������ ��������. 
	 * 
	 * @param currentSwapsList � ����� �� ��� �� ������ swap ���� �������� ���������
	 */
	private void performSwapsOnCurrentSolution(List<Swap> currentSwapsList) {
		//� ����� �� �� swap ����� ������������ �� ������� ����� ����� ����� �������� ��� ������ ��� �� �������� swap.
		for(Swap swap: currentSwapsList) {
			/*
			 * ���� ������ �������� ��� || �� swap ���������� �� � ��� ���� ����� �������� ����� ��� ��
			 * �� swap �������������� ���� tabu list ���� ��� aspiration criteria.
			 * ���� ������ ����� ��� || ���������� �� swap ���� �� � ���� ����� ��������� ���� ���
			 * �������������� ���� tabu list.
			 */
			if((swap.getCost() < currentSolutionCost) || (tabuList.getTabuValueForPair(swap.getNodeOnePosition(), swap.getNodeTwoPosition()) == 0)) {
				currentSolutionCost = swap.getCost();
				doSwap(swap);
				break;
			}
		}
	}
	
	/**
	 * ������� �� ������ ��� ����������� ����� ��� ��� ���� ������ �������� ���� � �� ����� ��� �� 0 �� ������
	 * ���� ��� �������� ���������� consecutiveWorseSolutionsCount.
	 * 
	 * @param consecutiveWorseSolutionsCount �� ������ ��� ����������� ����� ��� ��� ���� ������ �������� ����
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
	 * ���������� ��� ���������� �� ������ ��� ��������� �������.
	 * 
	 * @param vertexOnePosition � ���� ��� ������ ������ ��� ����� ��� ������ ��� ��������� �����
	 * @param vertexTwoPosition � ���� ��� ������� ������ ��� ����� ��� ������ ��� ��������� �����
	 * @param solution � ����� ��� ������ ��� ��������� ����� 
	 * @param solutionCost �� ������ ��� ��������� �����
	 * @return �� ��� ������ ��� ����� �� ��� �������� ���������
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
	 * ���������� ��� ���������� �� �������� ���������� ��� ������ ��� ������ ��� ������� ����� 
	 * ���� ��� ���������.
	 * 
	 * @param vertexOnePrevious � ������������ ���� ����� ������ ��� ������ ���
	 * @param vertexOne � ������ ���
	 * @param vertexOneNext � �������� ���� ����� ������ ��� ������ ���
	 * @param vertexTwoPrevious � ������������ ���� ����� ������ ��� ������ ���
	 * @param vertexTwo � ������ ���
	 * @param vertexTwoNext � �������� ���� ����� ������ ��� ������ ���
	 * @return �� ���������� ��� ������ ��� ������ ���� ��� ���������
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
	 * ���������� ��� ���������� �� �������� ���������� ��� ������ ��� ������ ��� ������� ����� 
	 * ���� ��� ���������.
	 * 
	 * @param vertexOnePrevious � ������������ ���� ����� ������ ��� ������ ���
	 * @param vertexOne � ������ ���
	 * @param vertexOneNext � �������� ���� ����� ������ ��� ������ ���
	 * @param vertexTwoPrevious � ������������ ���� ����� ������ ��� ������ ���
	 * @param vertexTwo � ������ ���
	 * @param vertexTwoNext � �������� ���� ����� ������ ��� ������ ���
	 * @param vertexOnePosition � ���� ��� ������ ������ ��� ����� ��� ������ ��� ��������� �����
	 * @param vertexTwoPosition � ���� ��� ������� ������ ��� ����� ��� ������ ��� ��������� �����
	 * @return �� ���������� ��� ������ ��� ������ ���� ��� ���������
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
	 * ���������� ���� ��� ������� ���������� �� ���� ���� ��� ����� ��� ����� ��� �� ����������� �����
	 * Swap. ������ ������� ���� �������� ��� ��������� ��� ����������� ��� ������� ��� ��������� ���������
	 * ���� tabu list
	 * 
	 * @param swap ��� ����������� ����� Swap �� ����� ������������ ���� ��� ������� 
	 * 		 	   ���� ��������.
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
	 * ���������� �� ������ ��� ����� ��� ������ ��� ��� ������� �������� ����� (currentVertex) ��� ���������
	 * ���� ����� �� id ��� �� �� ������� id (requestedVertexId).
	 * 
	 * @param currentVertex � ������ ������ ��� ��� ����� �������� � ����
	 * @param requestedVertexId �� id ��� ������ ��� �� ��������� � ����
	 * @return �� ������ ��� ���������� �����
	 */
	private int getEdgeCostFromVertexToVertex(Vertex currentVertex, int requestedVertexId) {
		return currentVertex.getEdgeList().stream().filter(edge -> requestedVertexId == edge.getId()).findFirst().get().getCost();
	}
}
