

public class Game {
	
	private Player player1;
	private Player player2;
	
	private Pile pile;

	public Game(String p1_name, String p2_name) {
		player1 = new Player();
		player1.setName(p1_name);
		player2 = new Player();
		player2.setName(p2_name);
		
		pile = new Pile();
	}
	
	public void start() {
		divDeckInHalf();
		
		
	}
	
	private void divDeckInHalf() {
		Deck deck1 = new Deck();
		Deck deck2 = new Deck();
		int times = pile.getSize();
		for(int i=0; i<times; i++) {
			if(i%2 == 0) {
				deck1.addCardToDeck(pile.remove());
			}else {
				deck2.addCardToDeck(pile.remove());
			}
		}
		setDecksToPlayers(deck1, deck2);
	}
	
	private void setDecksToPlayers(Deck d1, Deck d2) {
		player1.addDeck(d1);
		player2.addDeck(d2);
	}
	
	/*
	 * return 1 if p1's card > p2's card
	 * return -1 if p1's card < p2's card
	 * return 0 if p1's card = p2's card
	 */
	private int revealCardsAndCompare() {
		Card player1_card = player1.revealACard();
		Card player2_card = player2.revealACard();
		int result = player1_card.compareTo(player2_card);
		if(result == 0) {
			player1.setWar(true);
			player2.setWar(true);
		}
		System.out.println("Player " + player1.getName() + "'s card is " + player1_card.toString());
		System.out.println("Player " + player2.getName() + "'s card is " + player2_card.toString());
		return result;
		
	}
	
	/*
	 * return true if either one's deck is empty false otherwise
	 */
	private boolean eitherOnesDeckIsEmpty() {
		if(player1.noCardsLeft() || player2.noCardsLeft()) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return player1.toString() + "\n\n" + player2.toString() ;
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
