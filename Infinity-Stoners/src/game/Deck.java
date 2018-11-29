package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 *
 */
public class Deck extends Pile implements Serializable {
	private List<Card> cards;
	
	public Deck() {
		cards = new ArrayList<Card>();
	}

	/*
	 * add a card object to the deck
	 * @param  Card object o
	 */
	public void addCardToDeck(Card o) {
		this.cards.add(o);
	}
	
	public Card getCard(int index) {
		return cards.get(index);
	}
	
	/*
	 * remove the card on the top of the deck
	 * @return removed card
	 */
	public Card removeTop() {
		return cards.remove(0);
	}
	
	/*
	 * clear the entire deck
	 */
	public void clearDeck() {
		List<Card> emptyDeck = new ArrayList<Card>();
		cards = emptyDeck;
	}
	
	/*
	 * get the number of cards in the deck
	 * @return the size of the deck
	 */
	public int getNumberOfCards() {
		return cards.size();
	}
	
	/*
	 * check is the deck empty or not
	 * @return true or false
	 */
	public boolean isEmpty() {
		return cards.size() == 0;
	}
	
	public int getSize() {
		return cards.size();
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
