package metaheuristic;

public class Swap implements Comparable<Swap> {
	
	private int nodeOnePosition;
	private int nodeTwoPosition;
	private int cost;
	
	public Swap(int nodeOnePosition, int nodeTwoPosition, int cost) {
		this.nodeOnePosition = nodeOnePosition;
		this.nodeTwoPosition = nodeTwoPosition;
		this.cost = cost;
	}

	public int getNodeOnePosition() {
		return nodeOnePosition;
	}

	public int getNodeTwoPosition() {
		return nodeTwoPosition;
	}

	public int getCost() {
		return cost;
	}

	@Override
	public int compareTo(Swap other) {
		if(this.cost < other.getCost()) {
			return -1;
		} else if(this.cost > other.getCost()) {
			return +1;
		}
		return 0;
	}
	
	
}
