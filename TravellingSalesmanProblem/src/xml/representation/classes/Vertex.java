package xml.representation.classes;

import java.util.ArrayList;
import java.util.List;

public class Vertex {

	//��� ����� ��� �������� ���� ��� ����� ��� ������
    private List<Edge> edgeList;
    
    public Vertex() {
    	edgeList = new ArrayList<Edge>();
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

}
