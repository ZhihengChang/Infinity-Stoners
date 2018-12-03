package game;

import java.util.ArrayList;
import java.util.List;
/*
 * @author: Zhiheng Chang
 * @date: Dec/02/2018
 */

public class Pile {
	private static List<Card> cards;
	/**
	 * 
	 */
	public Pile() {
		cards = new ArrayList<Card>(52);
		for(Rank aRank : Rank.values()) {
			for(int i=0; i<4; i++) {
				if(aRank.getDisplayName().equals("Joker")) {
					break;
				}
				cards.add(new Card(aRank.getGraphic(), aRank.getPriority()));
			}
		}
		shuffle();
		
	}
	private void shuffle() {
		ArrayList<Card> shuffledPile = new ArrayList<Card>();
		
		while (cards.size() > 0) {
			int index = (int) (Math.random() * cards.size());
	        shuffledPile.add(cards.remove(index));   
			}
				
		cards = shuffledPile;
	}
	
	public void add(Card o) {
		cards.add(o);
	}
	
	public Card remove() {
		return cards.remove(0);
	}
	
	
	public int getSize() {
		return cards.size();
	}
	
	public String toString() {
		String str = "";
		for(Card o : cards) {
			str += o.getGraphic() + " ";
		}
		return str;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	}

}
