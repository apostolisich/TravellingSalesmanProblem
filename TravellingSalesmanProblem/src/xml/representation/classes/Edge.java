package xml.representation.classes;

public class Edge implements Comparable<Edge>{

	private int id;
    private int cost;
    
    public Edge(int id, int cost) {
		this.id = id;
		this.cost = cost;
	}

    /**
    * Returns the id of the edge.
    */
    public int getId() {
        return id;
    }

    /**
     * Gets the value of the cost property.
     * 
     */
    public int getCost() {
        return cost;
    }

	@Override
	public int compareTo(Edge otherEdge) {
		/*
		 * Χρησιμοποιείται εσωτερικά κατά την ταξινόμηση της λίστας των ακμών ενός κόμβου. Συγκρίνει
		 * το κόστος του τρέχοντος κόμβου μέσω της αναφροάς this, και ενός άλλου κόμβου μέσω του 
		 * otherEdge αντικειμένου.
		 */
		if(this.cost < otherEdge.cost) return -1;
		if(this.cost > otherEdge.cost) return +1;
		return 0;
	}

}
