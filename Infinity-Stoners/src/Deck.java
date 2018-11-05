/**
 * 
 */


import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 *
 */
public class Deck extends Pile {
	private List<Card> cards;
	
	/**
	 * 
	 */
	public Deck() {
		cards = new ArrayList<Card>();
	}

	public void addCardToDeck(Card o) {
		this.cards.add(o);
	}
	
	public Card removeTop() {
		return cards.remove(0);
	}
	
	public void clearDeck() {
		List<Card> emptyDeck = new ArrayList<Card>();
		cards = emptyDeck;
	}
	
	public int getNumberOfCards() {
		return cards.size();
	}
	
	public boolean isEmpty() {
		return cards.size() == 0;
	}
	
	
	public String toString() {
		String str = "Deck: ";
		for(Card o : cards) {
			str += o.toString() + " ";
		}
		return str;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
