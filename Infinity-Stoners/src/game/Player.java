package game;

public class Player {
	private String id;
	private Deck deck;
	private Deck removedCards;
	private String name;
	private boolean win;
	private boolean atWar;

	public Player(String id) {
		this.id = id;
		deck = new Deck();
		removedCards = new Deck();
		win = false;
		atWar = false;
	}
	
	public Player() {
		this.id = null;
		deck = new Deck();
		removedCards = new Deck();
		win = false;
		atWar = false;
	}
	
	/*
	 * add a deck to the player
	 * @param a deck object
	 */
	public void addDeck(Deck deck) {
		this.deck = deck;
	}
	
	/*
	 * check is there any card left in player's deck
	 * @return true or false
	 */
	public boolean hasCardsLeft() {
		return !deck.isEmpty();
	}
	
	/*
	 * get player's name
	 * @return player's name
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * set player's name
	 * @param player's name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/*
	 * reveal a card for comparison
	 * remove the top card of the player's deck
	 * add this card into removed cards deck
	 * @return top card (removed card)
	 */
	public Card revealACard() {
		Card removedCard = deck.removeTop();
		removedCards.addCardToDeck(removedCard);
		return removedCard;
	}
	
	/*
	 * get all the removed cards
	 * @return removed cards deck
	 */
	public Deck getRemovedCards() {
		return removedCards;
	}
	
	/*
	 * get the number of cards in player's deck
	 * @return the number of cards in player's deck
	 */
	public int getNumberOfCardsInDeck() {
		return deck.getNumberOfCards();
	}
	
	/*
	 * get number of cards in the removed cards deck
	 * @return number of cards in removed cards deck
	 */
	public int getNumberOfRemovedCards() {
		return removedCards.getNumberOfCards();
	}
	
	/*
	 * check is player at war
	 * @return true or false
	 */
	public boolean isAtWar() {
		return atWar;
	}
	
	/*
	 * set the status of war for player
	 * @param true or false
	 */
	public void setWar(boolean atWar) {
		this.atWar = atWar;
	}
	
	/*
	 * if player wins the war, player will get another player's removed cards deck
	 * and add all the cards in removed cards deck to this player's removed cards deck
	 * @param opponent's removed cards deck
	 */
	public void gainCards(Deck removedCards) {
		while(!removedCards.isEmpty()) {
			this.removedCards.addCardToDeck(removedCards.removeTop());
		}
	}
	
	/*
	 * add all the cards in the removed cards to player's deck
	 */
	public void addAllRemovedCardsToDeck() {
		while(!this.removedCards.isEmpty()) {
			deck.addCardToDeck(this.removedCards.removeTop());
		}
	}
	
	/*
	 * check is the player the winner of the game
	 * @return true or false
	 */
	public boolean isWinner() {
		return win;
	}
	
	/*
	 * set this player as the winner of the game
	 */
	public void setWinner() {
		win = true;
	}
	
	public String toString() {
		String str = "Player name: " + name + "\n";
		str += deck.toString() + "\n";
		str += "Number of cards in deck: " + deck.getNumberOfCards();
		return str;
	}

	public static void main(String[] args) {

	}

}