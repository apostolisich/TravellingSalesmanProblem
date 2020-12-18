package metaheuristic;

public class TabuList {

	private int size;
	private long tabuTenure;
	private int[][] tabuList;
	
	public TabuList(int size) {
		this.size = size;
		/*
		 * ��� ���� �� tenure ��� �� �� ���� ��� �������� ��� ����������� ������� 
		 * �� ��� ����� ��� �������.
		 */
		this.tabuTenure = Math.round(Math.sqrt((double) size));
		this.tabuList = new int[size][size];
	}
	
	/**
	 * ���������� ��� ���� ��� ���� �� ������� ������� ������ ��� ����� tabu.
	 * 
	 * @param nodeOnePosition � ���� ������ ������
	 * @param nodeTwoPosition � ���� ��� ������� ������
	 * @return
	 */
	public int getTabuValueForPair(int nodeOnePosition, int nodeTwoPosition) {
		return tabuList[nodeOnePosition][nodeTwoPosition];
	}
	
	/**
	 * ���������� �������� ��� tabu list ��� ��������� ���� �������� ��� ��������
	 * ��� ����� ����������� ������������ � ����� ������� swap.
	 */
	public void updateTabuCounters() {
		for(int i = 0; i < size - 1; i++) {
			for(int j = i + 1; j < size; j++) {
				/*
				 * ��� ������� ����� ��� ternary operator ���� ���� �� �� ������ �������� ��� tabu list
				 * ����� 0 �� ���������� 0, ����������� �� ���������� 1.
				 */
				tabuList[i][j] = tabuList[i][j] - (tabuList[i][j] == 0 ? 0 : 1);
			}
		}
	}
	
	/**
	 * ����� ��� ���� ���� ���� ��� ��� ������ ��� ������ swap ��� �� �� tenure.
	 * 
	 * @param nodeOnePosition � ���� ��� ������ ������ ���� ������
	 * @param nodeTwoPosition � ���� ��� �������� ������ ���� ������
	 */
	public void updateTenure(int nodeOnePosition, int nodeTwoPosition) {
		tabuList[nodeOnePosition][nodeTwoPosition] += tabuTenure;
	}
}
