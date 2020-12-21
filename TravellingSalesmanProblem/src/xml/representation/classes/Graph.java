package xml.representation.classes;

import java.util.ArrayList;
import java.util.List;

public class Graph {

	//Μια λίστα που περιέχει όλους τους κόμβους του γράφου
    private List<Vertex> vertexList;
    
    public Graph() {
    	vertexList = new ArrayList<Vertex>();
    }

    /**
     * Επιστρέφει τη λίστα με τους κόμβους του γράφου.
     * 
     * @return τη λίστα με τους κόμβους
     */
	public List<Vertex> getVertexList() {
        return vertexList;
    }
    
	/**
	 * Προσθέτει έναν κόμβο στη λίστα με τους κόμβους του γράφου.
	 * 
	 * @param vertex ο κόμβος που θα προστεθεί.
	 */
    public void addVertex(Vertex vertex) {
    	vertexList.add(vertex);
    }
    
    /**
     * Επιστρέφει το πλήθος των κόμβων του γράφου
     * 
     * @return το πλήθος των κόμβων
     */
    public int size() {
    	return vertexList.size();
    }
    
    /**
     * Κάνει override τη μέθοδο toString() και ορίζει έναν συγκεκριμένο τρόπο
     * εκτύπωσης για τα περιεχόμενα του γράφου.
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
