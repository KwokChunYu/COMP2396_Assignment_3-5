/**
 * The Single class is a subclass of the Hand class, and is used to model a hand which type is "Single".
 * 
 * @author Kwok Chun Yu
 */
public class Single extends Hand {
	
	private int handRank = 0;
	
	/**
	 * @see Hand
	 */
	public int getHandRank() { return handRank; }
	

	/**
	 * a constructor for building a hand with the the type "Single"
	 * @param player the player who play this hand
	 * @param cards the cards played
	 */
	public Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * @see Hand
	 */
	public Card getTopCard() { 
		return new Card(this.getCard(0).suit, this.getCard(0).rank);
	}
	
	/**
	 * @see Hand
	 */
	public boolean isValid() {
		return true;
	}
	
	/**
	 * @see Hand
	 */
	public String getType() { return "Single"; }
}
