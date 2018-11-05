
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
	
	public void addCard(Card o) {
		cards.add(o);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
