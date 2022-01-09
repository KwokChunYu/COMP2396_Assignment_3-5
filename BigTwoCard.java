/**
 * The BigTwoCard class is a subclass of the Card class, and is used to model a card used in a Big Two card game.
 * 
 * @author Kwok Chun Yu
 */
public class BigTwoCard extends Card {
	
	/**
	 * a constructor for building a card with the specified suit and rank. 
	 * @param suit the suit of the card
	 * @param rank the rank of the card
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit,rank);
	}
	
	/**
	 * a method for comparing the order of this card with the specified card.
	 */
	public int compareTo(Card card) {
		int thisRank = this.rank;
		int cardRank = card.rank;
		if (thisRank == 0 || thisRank == 1) {
			thisRank += 13;
		}
		if (cardRank == 0 || cardRank == 1) {
			cardRank += 13;
		}
		if (thisRank > cardRank) {
			return 1;
		} else if (thisRank < cardRank) {
			return -1;
		} else if (this.suit > card.suit) {
			return 1;
		} else if (this.suit < card.suit) {
			return -1;
		} else {
			return 0;
		}
	}
}
