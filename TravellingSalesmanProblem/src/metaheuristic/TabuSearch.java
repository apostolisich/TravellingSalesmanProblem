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
		 * ��� ���������� ��� ������� ��� ������� ����� ��� �������� ��� */
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
				int newSolutionCost = getSolutionCostAfterSwap(i, j, currentSolution, currentSolutionCost);
				Swap currentSwap = new Swap(i, j, newSolutionCost);
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
	 * ���������� ��� ���������� �� ��� ������ ��� ����� ���� ��� �������� ������.
	 * 
	 * @param vertexOnePosition � ���� ��� ������ ������ ��� ����� ��� ������ ��� ��������� �����
	 * @param vertexTwoPosition � ���� ��� ������� ������ ��� ����� ��� ������ ��� ��������� �����
	 * @param solution � ����� ��� ������ ��� ��������� ����� 
	 * @param solutionCost �� ������ ��� ��������� �����
	 * @return �� ��� ������ ��� ����� ���� ��� �������� ���������
	 */
	private int getSolutionCostAfterSwap(int vertexOnePosition, int vertexTwoPosition, List<Vertex> solution, int solutionCost) {
		Vertex vertexOne = solution.get(vertexOnePosition);
		/* ��� ��� ������ �� �� 0 ��� ��� ��������� ��� � vertexOne ����� � ������ ������ ��� �����,
		 * �������� ��� �� ���� �����������. */
		Vertex vertexOnePrevious = vertexOnePosition == 0 ? null : solution.get(vertexOnePosition - 1);
		Vertex vertexOneNext = solution.get(vertexOnePosition + 1);
		
		Vertex vertexTwo = solution.get(vertexTwoPosition);
		Vertex vertexTwoPrevious = solution.get(vertexTwoPosition - 1);
		/* ��� ��� ������ �� �� graphSize - 2 ��� ��� ��������� ��� � vertexTwo ����� � ���������� ������ ��� �����,
		 * �������� ��� �� ���� �������. */
		Vertex vertexTwoNext = vertexTwoPosition == graphSize - 2 ? null : solution.get(vertexTwoPosition + 1);
		
		int vertexesInitialCostContribution = calculateVertexesInitialCostContribution(vertexOnePrevious, vertexOne, vertexOneNext,
																					   vertexTwoPrevious, vertexTwo, vertexTwoNext);
		
		int vertexesNewCostContribution = calculateVertexesNewCostContribution(vertexOnePrevious, vertexOne, vertexOneNext,
																			   vertexTwoPrevious, vertexTwo, vertexTwoNext,
																			   vertexOnePosition, vertexTwoPosition);
		
		return solutionCost - vertexesInitialCostContribution + vertexesNewCostContribution;
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
		/*
		 * �� ������� �������� ����������� �� ������ ������ ��� ������������ ��� ��� �������� ������ ��� ����
		 * ���� ��� ���� ��� ������� ���� ������.
		 */
		int vertexOnePrevCost = getEdgeCostFromVertexToVertex(vertexOnePrevious, vertexOne.getId());
		int vertexOneNextCost = getEdgeCostFromVertexToVertex(vertexOne, vertexOneNext.getId());
		int vertexTwoPrevCost = getEdgeCostFromVertexToVertex(vertexTwoPrevious, vertexTwo.getId());
		int vertexTwoNextCost = getEdgeCostFromVertexToVertex(vertexTwo, vertexTwoNext.getId());
		
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
		/*
		 * ������������ �� ������ ������ ��� ������������ ��� ��� �������� ������ ��� ���� ���� ��� ���� ���
		 * ������� ���� ��� ������. � ������ ��� ��� ���������������, ���� ������������ �� ������ �� ����
		 * �� ������� ��� ������ �� ������ �����������������.
		 */
		int vertexOnePrevCostAfterSwap = getEdgeCostFromVertexToVertex(vertexOnePrevious, vertexTwo.getId());
		
		int vertexOneNextCostAfterSwap = 0;
		int vertexTwoPrevCostAfterSwap = 0;
		//� �������� ������� ������� ��� �� ������������ ��� ����������� ��� �� ������ ����� � ���� ����� ���� ����.
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
	 * ���������� �� ������ ��� ����� ��� ������ ��� ��� ������� �������� ����� (currentVertex) ��� ���������
	 * ���� ����� �� id ��� �� �� ������� id (requestedVertexId).
	 * 
	 * @param currentVertex � ������ ������ ��� ��� ����� �������� � ����
	 * @param requestedVertexId �� id ��� ������ ��� �� ��������� � ����
	 * @return �� ������ ��� ���������� �����
	 */
	private int getEdgeCostFromVertexToVertex(Vertex currentVertex, int requestedVertexId) {
		if(currentVertex == null) {
			return 0;
		}
		
		return currentVertex.getEdgeList().stream().filter(edge -> requestedVertexId == edge.getId()).findFirst().get().getCost();
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
	 * ������� �� ������ ��� ����������� ����� ��� ��� ���� ������ �������� ���� � �� ����� ��� �� 0 �� ������
	 * ���� ��� �������� ���������� consecutiveWorseSolutionsCount.
	 * 
	 * @param consecutiveWorseSolutionsCount �� ������ ��� ����������� ����� ��� ��� ���� ������ �������� ����
	 */
	private void updateConsecutiveWorseSolutionsCount(int consecutiveWorseSolutionsCount) {
		if(isCurrentSolutionBetter()) {
			consecutiveWorseSolutionsCount = 0;
		} else {
			consecutiveWorseSolutionsCount++;
		}
	}
	
	/**
	 * ������� �� � �������� ���� ����� �������� ��� ��� ����� ������� �������� ��� �� ����� ������ ���
	 * �������� �� �������� ��� ���������� true. �� ��� ����� ��������, ���������� false.
	 * 
	 * @return true �� � �������� ���� ����� �������� ��� �� �������� ��� false �����������.
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
