/**
 * 
 */


/**
 * @author 
 *
 */
public class Player {
	private Deck deck;
	private Deck removedCards;
	private String name;
	private boolean win;
	private boolean atWar;


	/**
	 * 
	 */
	public Player() {
		deck = new Deck();
		removedCards = new Deck();
		win = false;
		atWar = false;
	}
	
	public void addDeck(Deck deck) {
		this.deck = deck;
	}
	
	public boolean noCardsLeft() {
		return deck.isEmpty();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Deck getRemovedCards() {
		return removedCards;
	}
	
	public int getNumberOfCardsInDeck() {
		return deck.getNumberOfCards();
	}
	
	public int getNumberOfRemovedCards() {
		return removedCards.getNumberOfCards();
	}
	
	public void clearRemovedCards() {
		removedCards.clearDeck();
	}
	
	public boolean isAtWar() {
		return atWar;
	}
	
	public void setWar(boolean atWar) {
		this.atWar = atWar;
	}
	
	public void gainCards(Deck removedCards) {
		for(int i=0; i<removedCards.getNumberOfCards(); i++) {
			this.removedCards.addCardToDeck(removedCards.removeTop());
		}
	}
	
	public void addAllRemovedCardsToDeck() {
		for(int i=0; i<this.removedCards.getNumberOfCards(); i++) {
			deck.addCardToDeck(this.removedCards.removeTop());
		}
	}
	
	
	public Card revealACard() {
		Card removedCard = deck.removeTop();
		removedCards.addCardToDeck(removedCard);
		return removedCard;
	}
	
	
	public boolean isWinner() {
		return win;
	}
	
	public String toString() {
		String str = "Player name: " + name + "\n";
		str += deck.toString() + "\n";
		str += "Number of cards in deck: " + deck.getNumberOfCards();
		return str;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
