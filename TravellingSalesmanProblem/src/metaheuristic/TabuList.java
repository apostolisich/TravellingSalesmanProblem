package metaheuristic;

public class TabuList {

	private int size;
	private long tabuTenure;
	private int[][] tabuList;
	
	public TabuList(int size) {
		this.size = size;
		/*
		 * Εδώ θέτω το tenure ίσο με τη ρίζα του μεγέθους του προβλήματος σύμφωνα 
		 * με ένα άρθρο που διάβασα.
		 */
		this.tabuTenure = Math.round(Math.sqrt((double) size));
		this.tabuList = new int[size][size];
	}
	
	/**
	 * Επιστρέφει την τιμή που έχει το δοσμένο ζευγάρι κόμβων στη λίστα tabu.
	 * 
	 * @param nodeOnePosition η θέση πρώτου κόμβου
	 * @param nodeTwoPosition η θέση του δεύτερο κόμβου
	 * @return
	 */
	public int getTabuValueForPair(int nodeOnePosition, int nodeTwoPosition) {
		return tabuList[nodeOnePosition][nodeTwoPosition];
	}
	
	/**
	 * Διαπερνάει ολόκληρη την tabu list και ανανεώνει τους μετρητές που ελέγχουν
	 * για πόσες επαναλήψεις απαγορεύεται η χρήση κάποιου swap.
	 */
	public void updateTabuCounters() {
		for(int i = 0; i < size - 1; i++) {
			for(int j = i + 1; j < size; j++) {
				/*
				 * Εδώ γίνεται χρήση του ternary operator έτσι ώστε αν το τρέχον στοιχείο της tabu list
				 * είναι 0 να επιστρέφει 0, διαφορετικά να επιστρέψει 1.
				 */
				tabuList[i][j] = tabuList[i][j] - (tabuList[i][j] == 0 ? 0 : 1);
			}
		}
	}
	
	/**
	 * Θέτει την τιμή στην τομή των δύο κόμβων που έγιναν swap ίση με το tenure.
	 * 
	 * @param vertexOnePosition η θέση του πρώτου κόμβου στον πίνακα
	 * @param vertexTwoPosition η θέση του δεύτερου κόμβου στον πίνακα
	 */
	public void updateTenure(int vertexOnePosition, int vertexTwoPosition) {
		tabuList[vertexOnePosition][vertexTwoPosition] += tabuTenure;
	}
}
