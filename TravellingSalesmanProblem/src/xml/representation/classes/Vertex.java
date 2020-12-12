package xml.representation.classes;

import java.util.ArrayList;
import java.util.List;

public class Vertex {

	private int id;
	//Μια λίστα που περιέχει όλες τις ακμές του κόμβου
    private List<Edge> edgeList;
    private boolean isVisited;
    
    public Vertex(int id) {
    	this.id = id;
    	edgeList = new ArrayList<Edge>();
    	isVisited = false;
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
	
	/**
     * Returns true if the edge is used and false otherwise.
     * 
     * @return true if edge is used; false otherwise
     */
	public boolean isVisited() {
		return isVisited;
	}

	/**
	 * Ορίζει αν τον κόμβο στον οποίο οδηγεί αυτή η ακμή τον έχουμε
	 * επισκευθεί.
	 * 
	 * @param isVisited
	 */
	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

}
