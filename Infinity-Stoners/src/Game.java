

public class Game {
	
	private Player player1;
	private Player player2;
	
	private Pile pile;

	public Game(String p1_name, String p2_name) {
		player1 = new Player();
		player2 = new Player();
		
		pile = new Pile();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
