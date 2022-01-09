import java.util.ArrayList;

/**
 * The Straight class is a subclass of the Hand class, and is used to model a hand which type is "Straight".
 * 
 * @author Kwok Chun Yu
 */
public class Straight extends Hand {
	
	private int handRank = 1;
	
	/**
	 * @see Hand
	 */
	public int getHandRank() { return handRank; }
	
	/**
	 * @see Hand
	 */
	public Straight(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * @see Hand
	 */
	public boolean isValid() {
		ArrayList<Integer> ranks = new ArrayList<Integer>();
		for (int i = 0; i < this.size(); i++) {
			if (this.getCard(i).rank == 0 || this.getCard(i).rank == 1) {
				ranks.add(this.getCard(i).rank + 13);
			} else { ranks.add(this.getCard(i).rank); }
			ranks.sort(null);
		}
		int countStraight = 0;
		for (int i = 0; i < ranks.size()-1; i++) {
			if (ranks.get(i) == ranks.get(i + 1) - 1) {
				countStraight += 1;
			}
		}
		if (countStraight == 4) {
			return true;
		} else { return false; }

	}
	
	/**
	 * @see Hand
	 */
	public String getType() { return "Straight"; }
}
