/**
 * The Hand class is a subclass of the CardList class, and is used to model a hand of cards.
 * 
 * @author Kwok Chun Yu
 */
public abstract class Hand extends CardList  {
	
	/**
	 * a constructor for building a hand with the specified player and list of cards.
	 * @param player the player who play this hand
	 * @param cards the cards played
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		for (int i = 0; i < cards.size(); i++) {
			this.addCard(cards.getCard(i));
		}
	}
	
	private CardGamePlayer player;
	
	/**
	 * a method for retrieving the player of this hand.
	 * @return the player of this hand
	 */
	public CardGamePlayer getPlayer() { return player; }
	
	/**
	 * a method for retrieving the top card of this hand.
	 * @return the top card of this hand
	 */
	public Card getTopCard() {
		Card topCard = this.getCard(0);
		BigTwoCard bigTwoTopCard = new BigTwoCard(topCard.suit, topCard.rank);
		BigTwoCard thisCard;
		for (int i = 1; i < this.size(); i++) {
			thisCard = new BigTwoCard(this.getCard(i).suit, this.getCard(i).rank);
			if (thisCard.compareTo(bigTwoTopCard) == 1) {
				topCard = this.getCard(i);
			}
		}
		return topCard;
	}
	
	/**
	 * a method for checking if this hand beats a specified hand.
	 * @param hand the cards played
	 * @return a boolean value on whether this hand beats the previous hand
	 */
	public boolean beats(Hand hand) {
		if (hand.size() == this.size()) {
			if (hand.getHandRank() > this.getHandRank()) { return true; }
			else if (hand.getHandRank() < this.getHandRank()) { return false; }
			else {
				BigTwoCard handCard = new BigTwoCard(hand.getTopCard().suit, hand.getTopCard().rank);
				BigTwoCard thisCard = new BigTwoCard(this.getTopCard().suit, this.getTopCard().rank);
				if (handCard.compareTo(thisCard) == 1) { return true; }
				else { return false; }
			}
		} else { return false; }
	}
	
	/**
	 * a method for returning the rank of this hand.
	 * @return the rank of this hand
	 */
	public abstract int getHandRank();
	
	/**
	 * a method for checking if this is a valid hand.
	 * @return the validity of this hand
	 */
	public abstract boolean isValid();//a method for checking if this is a valid hand
	
	/**
	 *  a method for returning a string specifying the type of this hand.
	 * @return the type of this hand
	 */
	public abstract String getType();//a method for returning a string specifying the type of this hand.
	
}
