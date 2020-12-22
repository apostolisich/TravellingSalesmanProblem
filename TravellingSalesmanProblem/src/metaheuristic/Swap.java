package metaheuristic;

public class Swap implements Comparable<Swap> {
	
	private int vertexOnePosition;
	private int vertexTwoPosition;
	private int newSolutionCost;
	
	public Swap(int vertexOnePosition, int vertexTwoPosition, int cost) {
		this.vertexOnePosition = vertexOnePosition;
		this.vertexTwoPosition = vertexTwoPosition;
		this.newSolutionCost = cost;
	}

	/**
	 * ���������� �� ���� ��� ������ ��� ��� ����� ��� ��������� �����.
	 * 
	 * @return �� ���� ��� ������ ��� ��� ����� ��� ��������� �����.
	 */
	public int getVertexOnePosition() {
		return vertexOnePosition;
	}

	/**
	 * ���������� �� ���� ��� ������ ��� ��� ����� ��� ��������� �����.
	 * 
	 * @return �� ���� ��� ������ ��� ��� ����� ��� ��������� �����.
	 */
	public int getVertexTwoPosition() {
		return vertexTwoPosition;
	}

	/**
	 * ���������� �� ��� ������ ��� ����� ���� �� ���������� ������.
	 * 
	 * @return �� ��� ������ ��� ����� ���� �� ���������� ������.
	 */
	public int getNewSolutionCost() {
		return newSolutionCost;
	}

	@Override
	public int compareTo(Swap other) {
		/*
		 * � ������� ���� ��������������� ��� �� �������� 2 ������������ swap, ��� ���������
		 * ��� ���� �����. 
		 */
		if(this.newSolutionCost < other.getNewSolutionCost()) {
			return -1;
		} else if(this.newSolutionCost > other.getNewSolutionCost()) {
			return +1;
		}
		return 0;
	}
	
	
}
