/**
 * The Triple class is a subclass of the Hand class, and is used to model a hand which type is "Triple".
 * 
 * @author Kwok Chun Yu
 */
public class Triple extends Hand {
	
	private int handRank = 0;
	
	/**
	 * @see Hand
	 */
	public int getHandRank() { return handRank; }
	
	/**
	 * @see Hand
	 */
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * @see Hand
	 */
	public boolean isValid() {
		if (this.getCard(0).rank == this.getCard(1).rank && this.getCard(0).rank == this.getCard(2).rank) {
			return true;
		} else { return false; }
	}
	
	/**
	 * @see Hand
	 */
	public String getType() { return "Triple"; }
}
