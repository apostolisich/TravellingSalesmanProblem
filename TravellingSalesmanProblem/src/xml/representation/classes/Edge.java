package xml.representation.classes;

public class Edge implements Comparable<Edge>{

	private int id;
    private int cost;
    
    public Edge(int id, int cost) {
		this.id = id;
		this.cost = cost;
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
