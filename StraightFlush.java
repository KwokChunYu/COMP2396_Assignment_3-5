/** 
 * The StraightFlush class is a subclass of the Hand class, and is used to model a hand which type is "StraightFlush".
 * 
 * @author Kwok Chun Yu
 */
public class StraightFlush extends Hand {
	
	private int handRank = 5;
	
	/**
	 * @see Hand
	 */
	public int getHandRank() { return handRank; }

	/**
	 * @see Hand
	 */
	public StraightFlush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * return a dummy value since isValid() of type "StraightFlush" is not used
	 * @see Hand
	 */
	public boolean isValid() { return false; }

	/**
	 * @see Hand
	 */
	public String getType() { return "StraightFlush"; }

}
