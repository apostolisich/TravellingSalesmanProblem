package xml.representation.classes;

import java.util.ArrayList;
import java.util.List;

public class Graph {

	//��� ����� ��� �������� ����� ���� ������� ��� ������
    private List<Vertex> vertexList;
    
    public Graph() {
    	vertexList = new ArrayList<Vertex>();
    }

    /**
     * ���������� �� ����� �� ���� ������� ��� ������.
     * 
     * @return �� ����� �� ���� �������
     */
	public List<Vertex> getVertexList() {
        return vertexList;
    }
    
	/**
	 * ��������� ���� ����� ��� ����� �� ���� ������� ��� ������.
	 * 
	 * @param vertex � ������ ��� �� ���������.
	 */
    public void addVertex(Vertex vertex) {
    	vertexList.add(vertex);
    }
    
    /**
     * ���������� �� ������ ��� ������ ��� ������
     * 
     * @return �� ������ ��� ������
     */
    public int size() {
    	return vertexList.size();
    }
    
    /**
     * ����� override �� ������ toString() ��� ������ ���� ������������ �����
     * ��������� ��� �� ����������� ��� ������.
     */
    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	for(int vertexIndex = 0; vertexIndex < vertexList.size(); vertexIndex++) {
    		builder.append("Vertex " + vertexIndex + "\n");
    		
    		Vertex currentVertex = vertexList.get(vertexIndex);
    		List<Edge> edges = currentVertex.getEdgeList();
    		
    		builder.append("Edges:");
    		for(int edgeIndex = 0; edgeIndex < edges.size(); edgeIndex++) {
    			Edge currentEdge = edges.get(edgeIndex);
    			builder.append(" " + currentEdge.getId() + " (" + currentEdge.getCost() + "),");
    		}
    		builder.append("\n\n");
    	}
    	
    	return builder.toString();
    }

}
