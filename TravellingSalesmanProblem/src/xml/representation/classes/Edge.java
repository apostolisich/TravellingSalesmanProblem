package xml.representation.classes;

public class Edge {

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

}
