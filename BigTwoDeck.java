/**
 * The BigTwoDeck class is a subclass of the Deck class, and is used to model a deck of cards used in a Big Two card game.
 * 
 * @author Kwok Chun Yu
 */
public class BigTwoDeck extends Deck {
	
	/**
	 * a method for initializing a deck of Big Two cards
	 */
	public void initialize() {
		super.initialize();
		shuffle();
	}
}
