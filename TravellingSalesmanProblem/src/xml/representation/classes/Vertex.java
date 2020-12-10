package xml.representation.classes;

import java.util.ArrayList;
import java.util.List;

public class Vertex {

	private int id;
	//Μια λίστα που περιέχει όλες τις ακμές του κόμβου
    private List<Edge> edgeList;
    
    public Vertex(int id) {
    	this.id = id;
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
	
	/**
	 * Επιστρέφει το id του συγκερκιμένου κόμβου
	 */
	public int getId() {
		return id;
	}

}
