package xml.representation.classes;

import java.util.ArrayList;
import java.util.List;

public class Vertex {

	private int id;
	//��� ����� ��� �������� ���� ��� ����� ��� ������
    private List<Edge> edgeList;
    private boolean isVisited;
    
    public Vertex(int id) {
    	this.id = id;
    	edgeList = new ArrayList<Edge>();
    	isVisited = false;
    }

    /**
     * ���������� �� ����� �� ��� ����� ��� ������.
     * 
     * @return �� ����� �� ��� �����.
     */
	public List<Edge> getEdgeList() {
        return edgeList;
    }
	
	/**
	 * ��������� ��� ��� ���� ��� ����� �� ��� ����� ��� ������
	 * 
	 * @param edge � ���� ��� �� ���������
	 */
	public void addEdge(Edge edge) {
		edgeList.add(edge);
	}
	
	/**
	 * ���������� �� id ��� ������������� ������
	 */
	public int getId() {
		return id;
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

}
