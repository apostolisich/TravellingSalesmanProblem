package xml.representation.classes;

public class Edge implements Comparable<Edge>{

	private int id;
    private int cost;
    private boolean isVisited;
    
    public Edge(int id, int cost) {
		this.id = id;
		this.cost = cost;
		isVisited = false;
	}

    /**
    * ���������� �� id ��� �����
    * 
    * @return �� id ��� �����
    */
    public int getId() {
        return id;
    }

    /**
     * ���������� �� ������ ��� ������.
     * 
     * @return �� ������ ��� ������
     */
    public int getCost() {
        return cost;
    }
    
    /**
     * Returns true if the edge is used and false otherwise.
     * 
     * @return true if edge is used; false otherwise
     */
	public boolean isVisited() {
		return isVisited;
	}

	/**
	 * ������ �� ��� ����� ���� ����� ������ ���� � ���� ��� ������
	 * ����������.
	 * 
	 * @param isVisited
	 */
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	@Override
	public int compareTo(Edge otherEdge) {
		/*
		 * ��������������� ��������� ���� ��� ���������� ��� ������ ��� ����� ���� ������. ���������
		 * �� ������ ��� ��������� ������ ���� ��� �������� this, ��� ���� ����� ������ ���� ��� 
		 * otherEdge ������������.
		 */
		if(this.cost < otherEdge.cost) return -1;
		if(this.cost > otherEdge.cost) return +1;
		return 0;
	}

}
