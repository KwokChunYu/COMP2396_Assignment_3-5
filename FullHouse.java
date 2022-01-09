import java.util.ArrayList;

/**
 * The FullHouse class is a subclass of the Hand class, and is used to model a hand which type is "FullHouse".
 * 
 * @author Kwok Chun Yu
 */
public class FullHouse extends Hand {
	
	private int handRank = 3;
	
	/**
	 * @see Hand
	 */
	public int getHandRank() { return handRank; }

	/**
	 * @see Hand
	 */
	public FullHouse(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * @see Hand
	 */
	public boolean isValid() {
		ArrayList<Integer> typesOfRank = new ArrayList<Integer>();
		for (int i = 0; i < this.size(); i++) {
			if (!typesOfRank.contains(this.getCard(i).rank)) {
				typesOfRank.add(this.getCard(i).rank);
			}
		}
		if (typesOfRank.size() == 2) {
			int numberOfRankTypeOne = 0;
			for (int i = 0; i < this.size(); i++) {
				if (this.getCard(i).rank == typesOfRank.get(0)) {
					numberOfRankTypeOne++;
				}
			}
			if (numberOfRankTypeOne == 2 || numberOfRankTypeOne == 3) { return true; }
			else { return false; }
		} else { return false; }
		
	}

	/**
	 * @see Hand
	 */
	public String getType() { return "FullHouse"; }
}
