/**
 * The Flush class is a subclass of the Hand class, and is used to model a hand which type is "Flush".
 * 
 * @author Kwok Chun Yu
 */
public class Flush extends Hand {
	
	private int handRank = 2;
	
	/**
	 * @see Hand
	 */
	public int getHandRank() { return handRank; }

	/**
	 * @see Hand
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * @see Hand
	 */
	public boolean isValid() {
		int countSuit = 0;
		for (int i = 1; i < this.size()-1; i++) {
			if (this.getCard(i).suit == this.getCard(0).suit) {
				countSuit++;
			}
		}
		if (countSuit == 4) { return true; }
		else { return false; }
	}

	/**
	 * @see Hand
	 */
	public String getType() { return "Flush"; }
}
