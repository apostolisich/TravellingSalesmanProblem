package metaheuristic;

public class Swap implements Comparable<Swap> {
	
	private int vertexOnePosition;
	private int vertexTwoPosition;
	private int newSolutionCost;
	
	public Swap(int vertexOnePosition, int vertexTwoPosition, int cost) {
		this.vertexOnePosition = vertexOnePosition;
		this.vertexTwoPosition = vertexTwoPosition;
		this.newSolutionCost = cost;
	}

	/**
	 * Επιστρέφει τη θέση του κόμβου ένα στη λίστα της τρέχουσας λύσης.
	 * 
	 * @return τη θέση του κόμβου ένα στη λίστα της τρέχουσας λύσης.
	 */
	public int getVertexOnePosition() {
		return vertexOnePosition;
	}

	/**
	 * Επιστρέφει τη θέση του κόμβου ένα στη λίστα της τρέχουσας λύσης.
	 * 
	 * @return τη θέση του κόμβου ένα στη λίστα της τρέχουσας λύσης.
	 */
	public int getVertexTwoPosition() {
		return vertexTwoPosition;
	}

	/**
	 * Επιστρέφει το νέο κόστος της λύσης μετά τη συγκριμένη αλλαγή.
	 * 
	 * @return το νέο κόστος της λύσης μετά τη συγκριμένη αλλαγή.
	 */
	public int getNewSolutionCost() {
		return newSolutionCost;
	}

	@Override
	public int compareTo(Swap other) {
		/*
		 * Η μέθοδος αυτή χρησιμοποιείται για τη σύγκριση 2 αντικειμένων swap, του τρέχοντος
		 * και ενός άλλου. 
		 */
		if(this.newSolutionCost < other.getNewSolutionCost()) {
			return -1;
		} else if(this.newSolutionCost > other.getNewSolutionCost()) {
			return +1;
		}
		return 0;
	}
	
	
}
