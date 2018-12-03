package game;
/*
 * @author: Zhiheng Chang
 * @date: Dec/02/2018
 */

public enum Rank {
	//Element   Display Name    Graphic     Priority 
	ACE     (   "Ace",          "A",           1),
	TWO     (   "Duece",        "2",           2),
	THREE   (   "Three",        "3",           3),
	FOUR    (   "Four",         "4",           4),
	FIVE    (   "Five",         "5",           5),
	SIX     (   "Six",          "6",           6),
	SEVEN   (   "Seven",        "7",           7),
	EIGHT   (   "Eight",        "8",           8),
	NINE    (   "Nine",         "9",           9),
	TEN     (   "Ten",          "10",         10),
	JACK    (   "Jack",         "J",          11),
	QUEEN   (   "Queen",        "Q",          12),
	KING    (   "King",         "K",          13),
	JOKER   (   "Joker",        "R",          99);

	private final String displayName;
	private final String graphic;
	private final int priority;

	private Rank(String rankDisplayName, String rankGraphic,int rankPriority) {
		displayName =   rankDisplayName;
		graphic =       rankGraphic;
		priority =      rankPriority;
	} // end constructor

	public String getDisplayName() {
		return displayName;
	} // end getDisplayName

	public String getGraphic() {
		return graphic;
	} // end getGraphic

	public int getPriority() {
		return priority;
	} // end getPriority

	@Override
	public String toString() {
		return graphic ;
	}


    public static void main(String[] args) {
        
    }

} // end Rank