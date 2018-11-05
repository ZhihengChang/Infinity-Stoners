

public class Player {
	private Deck deck;
	private Deck removedCards;
	private String name;
	private boolean win;
	private boolean atWar;

	public Player() {
		deck = new Deck();
		removedCards = new Deck();
		win = false;
		atWar = false;
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
