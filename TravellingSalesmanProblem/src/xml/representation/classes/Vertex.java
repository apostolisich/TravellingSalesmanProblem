package xml.representation.classes;

import java.util.ArrayList;
import java.util.List;

public class Vertex {

	//Μια λίστα που περιέχει όλες τις ακμές του κόμβου
    private List<Edge> edgeList;
    
    public Vertex() {
    	edgeList = new ArrayList<Edge>();
    }

    /**
     * Επιστρέφει τη λίστα με τις ακμές του κόμβου.
     * 
     * @return τη λίστα με τις ακμές.
     */
	public List<Edge> getEdgeList() {
        return edgeList;
    }
	
	/**
	 * Προσθέτει μια νέα ακμή στη λίστα με τις ακμές του κόμβου
	 * 
	 * @param edge η ακμή που θα προστεθεί
	 */
	public void addEdge(Edge edge) {
		edgeList.add(edge);
	}

}
