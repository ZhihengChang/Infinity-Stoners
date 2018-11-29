package Server;
import java.net.*;
import java.util.*;

import java.io.*;
import game.*;

public class Server {
	
	//private Socket socket; 
    private ServerSocket server; 
    
    private int port_num;
    private int max_thread;
    private Socket[] sockets;
    private Thread [] threads;
    private Hashtable<String, Player> players; // id and player
    private Hashtable<Socket, String> Clients; // socket and id
    private Hashtable<Socket, DataInputStream> s_dis;
    private Hashtable<Socket, DataOutputStream> s_dos;
    private int client_counter;
    private int thread_counter;

    
   // final String work_dir; //server<->thread
    
    public Server(int port, int max_thread) {
    		this.port_num = port;
    		this.max_thread = max_thread;
    		this.sockets = new Socket[max_thread];
    		this.client_counter = 0;
    		this.threads =  new Thread[max_thread];
    		this.thread_counter = 0;
    		this.players = new Hashtable<String, Player>();
    		this.Clients = new Hashtable<Socket, String>();
    		this.s_dis = new Hashtable<Socket, DataInputStream>();
    		this.s_dos = new Hashtable<Socket, DataOutputStream>();
    		 
    		
    }
    
    
    public void establish() throws IOException {
    		server = new ServerSocket(port_num);
    		
    }
    
    public boolean acceptClient() throws IOException {
    		if(client_counter < max_thread-1 ) {
    			sockets[client_counter] = server.accept();
    			DataInputStream dis = new DataInputStream(sockets[client_counter].getInputStream()); 
    			//DataOutputStream dos = new DataOutputStream(sockets[client_counter].getOutputStream());
    			System.out.println("A new client is connected: " + sockets[client_counter] + "\n"
    					+ "Client number: " + (client_counter));
    			//get the id of the client
    			System.out.println("client Counter: " + client_counter);
    			System.out.println("client soket: " + sockets[client_counter]);
    			Clients.put(sockets[client_counter], dis.readUTF());
    			s_dis.put(sockets[client_counter], new DataInputStream(sockets[client_counter].getInputStream()));
    			
    			s_dos.put(sockets[client_counter], new DataOutputStream(sockets[client_counter].getOutputStream()));
    			
    			client_counter++;
    		}else {
    			return false;
    		}
    		return true;
    	
    }
    
    public void client_pair() throws Exception {
    		//while(thread_counter < max_thread) {
    		System.out.println("Client id: " + Clients.get(sockets[client_counter-1]));
    		DataInputStream dis = s_dis.get(sockets[client_counter-1]);
    		DataOutputStream dos = s_dos.get(sockets[client_counter-1]);
		threads[thread_counter] = new ClientController(sockets[client_counter-1], dis, dos, Clients.get(sockets[client_counter-1]));
		players.put(Clients.get(sockets[client_counter-1]), new Player());
		threads[thread_counter] .start();
		thread_counter++;

    		if(client_counter>0 && client_counter%2 == 0) {
    			System.out.println("socket1: " + sockets[client_counter-2].isConnected());
    			System.out.println("socket2: " + sockets[client_counter-1].isConnected());
    			
    			game_start(sockets[client_counter-2],sockets[client_counter-1]);
    		}
    }
    
    public String getClientFile(Socket client) {
    		return CommTool.FILE_PERFIX_CLIENT + Clients.get(client)  + ".txt";
    }
    public String getServerFile(Socket client) {
		return CommTool.FILE_PERFIX_SERVER + Clients.get(client)  + ".txt";
}
    public boolean do_newturn(Socket client1, Socket client2, int game_counter) throws Exception{
    		// check client if new turn
    		String client1_data_file = getClientFile(client1);
    		String client2_data_file = getClientFile(client2);
    		
    		String client1_data = CommTool.wait4return(client1_data_file, CommTool.ACTION_NEWT, 2);
    		String client2_data = CommTool.wait4return(client2_data_file, CommTool.ACTION_NEWT, 2);
    		
    		if(client1_data.equals("") || client1_data.equals("")) {
    			System.out.println("ERROR:client data file is not ready!");
    			return false;
    		}
    		
    		// num, string key, ""
    		if(client1_data.equals(CommTool.ACTION_NEWT) || client2_data.equals(CommTool.ACTION_NEWT)) {
    			System.out.println("ERROR:invalid request");
    			return false;
    		}

    		if(Integer.parseInt(client1_data) == game_counter && Integer.parseInt(client2_data) == game_counter) {
    			do_show_card(client1, client2);
    			do_result(client1, client2);
    			if(do_turn_end(client1, client2)) 
    				return true;
    			
    		}else {
    			System.out.println("ERROR:invalid turn");
    			return false;
    		}
    		
    		return false;

    		
    }
    
    public void do_show_card(Socket client1, Socket client2) throws Exception {
    	 
    		Card player1_card = getPlayer(client1).revealACard();
		Card player2_card = getPlayer(client2).revealACard();	
		server_action(client1, CommTool.ACTION_SHOWC + ":" + player1_card.toString() + ":" + player2_card.toString());
		server_action(client2, CommTool.ACTION_SHOWC + ":" + player2_card.toString() + ":" + player1_card.toString());

    }
    
    public void do_result(Socket client1, Socket client2) throws Exception {
    		Player player1 = getPlayer(client1);
    		Player player2 = getPlayer(client2);
    		
    		Deck player1_removedCards = player1.getRemovedCards();
    		Deck player2_removedCards = player2.getRemovedCards();
    		
    		//check
    		System.out.println("player1 removed cards deck size:" + player1_removedCards.getSize());
    		Card card_to_compare_p1, card_to_compare_p2;
    		
    		if(player1_removedCards.getSize() > 0) {
    			card_to_compare_p1 = player1_removedCards.getCard(player1_removedCards.getSize()-1);
    		}else {
    			System.out.print("Player1: there is no removed cards");
    			card_to_compare_p1 = null;
    		}
    		
    		System.out.println("Player 2 removed cards deck size:" + player2_removedCards.getSize());
    		if(player2_removedCards.getSize() > 0) {
    			card_to_compare_p2 = player2_removedCards.getCard(player2_removedCards.getSize()-1);
    		}else {
    			System.out.print("Player2: there is no removed cards");
    			card_to_compare_p2 = null;
    		}
		int result = card_to_compare_p1.compareTo(card_to_compare_p2);
		
	
		String client1_result = "";
		String client2_result = "";

		if(result == 0) {
			player1.setWar(true);
			player2.setWar(true);
			client1_result = CommTool.RESULT_WAR;
			client2_result = CommTool.RESULT_WAR;
		}else {
			player1.setWar(false);
			player2.setWar(false);
			
			if(result > 0) {
				int cards_player1_gets = checkout(player1, player2);
				client1_result = CommTool.RESULT_WIN + ":" + cards_player1_gets;
				client2_result = CommTool.RESULT_LOST;
			}
			else if(result < 0) {
				int cards_player2_gets = checkout(player2, player1);
				client1_result = CommTool.RESULT_LOST;
				client2_result = CommTool.RESULT_WIN + ":" + cards_player2_gets;
			}
		}
		
		boolean result_sendto_player1 = false;
		boolean result_sendto_player2 = false;
		for(int i=0; i< 40; i++) {
			if(!result_sendto_player1 && CommTool.check_response(getClientFile(client1), CommTool.ACTION_RESULT)) {
				server_action(client1,client1_result);
				result_sendto_player1 = true;
			}
			if(!result_sendto_player1 && CommTool.check_response(getClientFile(client1), CommTool.ACTION_EXIT)) {
				result_sendto_player1 = true;
			}
			if(!result_sendto_player2 && CommTool.check_response(getClientFile(client2), CommTool.ACTION_RESULT)) {
				server_action(client2,client2_result);
				result_sendto_player2 = true;
			}
			if(!result_sendto_player2 && CommTool.check_response(getClientFile(client2), CommTool.ACTION_EXIT)) {
				result_sendto_player1 = true;
			}
			if(result_sendto_player1 && result_sendto_player2) {
				break;
			}
			Thread.sleep(2000);
		}
		//
		
		
		//send result
		
		
    }
    
    public boolean do_turn_end(Socket client1, Socket client2) throws InterruptedException {
    	
		String client1_file = getClientFile(client1);
		String client2_file = getClientFile(client2);
		while(true){
			if(CommTool.check_response(client1_file, CommTool.ACTION_TURNEND) && CommTool.check_response(client2_file, CommTool.ACTION_TURNEND)) {
				return true;
			}
			if(CommTool.check_response(client1_file, CommTool.ACTION_EXIT) || CommTool.check_response(client2_file, CommTool.ACTION_EXIT)) {
				break;
			}

			Thread.sleep(20000);
		}
		return false;
    }
    
    public Player getPlayer(Socket client) {
    		return players.get(Clients.get(client));
    }
    
    public void send_start(Socket client1, Socket client2) throws Exception{
    		CommTool.deletFile(getClientFile(client1));
    		CommTool.deletFile(getClientFile(client2));
    		CommTool.deletFile(getServerFile(client1));
    		CommTool.deletFile(getServerFile(client2));
    	
    		server_action(client1,CommTool.ACTION_START);
		server_action(client2,CommTool.ACTION_START);
		
    		Player player1 = players.get(Clients.get(client1));
		Player player2 = players.get(Clients.get(client2));
		setDecksToPlayer(player1, player2);
		//set player name
		set_player_name(player1, getClientFile(client1));
		set_player_name(player2, getClientFile(client2));
    	
    }
    
    //Get action from client
    public String get_user_action(Socket client) throws Exception{
    		return CommTool.readfile(getClientFile(client));
    }
    
    
    public boolean is_game_end(Socket client1, Socket client2, int game_counter) throws Exception{
    		Player player1 = players.get(Clients.get(client1));
		Player player2 = players.get(Clients.get(client2));
		
		System.out.println("check game end: game counter" + game_counter);
		System.out.println("player1 deck status: " + player1.hasCardsLeft());
		System.out.println("player2 deck status: " + player2.hasCardsLeft());
    
		// Step 4: If no card, game over
		if(!player1.hasCardsLeft() || !player2.hasCardsLeft()) {
			String winner;;
			if(player1.hasCardsLeft() || !player2.hasCardsLeft()) { //player2 win
				//String winner = player1.getName();
				//server_action(client1, CommTool.GAME_END + ":" + winner + ":" + game_counter);
				winner = player2.getName();
			}else {
				winner = player1.getName();
			}
			server_action(client1, CommTool.GAME_END + ":" + winner + ":" + game_counter);
			server_action(client2, CommTool.GAME_END + ":" + winner + ":" + game_counter);
			
			//game keep runing -> ready
			//reset player's deck
			//reset counter
			if(CommTool.wait4key(getClientFile(client1), CommTool.ACTION_NEWG, 2) &&
					CommTool.wait4key(getClientFile(client2), CommTool.ACTION_NEWG, 2)) {
				//new game
				//game_counter++;
				System.out.println("Do new game: uninitialized");
				return false;
				
			}	
			
			if(CommTool.wait4key(getClientFile(client1), CommTool.ACTION_EXIT, 1) ||
					CommTool.wait4key(getClientFile(client2), CommTool.ACTION_EXIT, 1)) {
				System.out.println("Clients ask to exit!");
				return true;
 
			}else {//break;
				System.out.println("No get response from client...!");
				return true;
			}
		}
		//game end
		//print 
		return false;
    }
    public void game_start(Socket client1, Socket client2) throws Exception {
    		
		send_start(client1, client2);

		//Init Counter for game
		int game_counter = 0;
		
		while(true) {
			game_counter++;
			// Step 1: Send ready to client
			System.out.println("To Ready");
			server_action(client1,CommTool.ACTION_READY);
			server_action(client2,CommTool.ACTION_READY);
			//If user want to exit?
			//new turn
			System.out.println("Game Start counter:" + game_counter);
			if(!do_newturn(client1,client2,game_counter)) {
				break;
			}
			System.out.println("newturn done! - game counter:" + game_counter);
			if(is_game_end(client1,client2, game_counter)) {
				break;
			};

		}
		// Step 4: Game end
		//do_game_end
		try {
			closeConnections(client1);
			closeConnections(client2);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
    }
    
  
    
    public void initialize_players(Socket client1, Socket client2) throws Exception{
    		Player player1 = players.get(Clients.get(client1));
		Player player2 = players.get(Clients.get(client2));
		setDecksToPlayer(player1, player2);
		//set player name
		set_player_name(player1, getClientFile(client1));
		set_player_name(player2, getClientFile(client2));
    }
    
    public void set_player_name(Player p, String config) throws Exception{
    		String player_name = CommTool.wait4return(config, CommTool.PLAYER, 30);
		if(player_name != null && player_name.length() !=0) {
			System.out.println();
			p.setName(player_name);	

		}else {
			System.out.println("ERROR:no player name found!");
		}
    }
    
    public void setDecksToPlayer(Player player1, Player player2) {
    		Pile pile = new Pile();
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
		player1.addDeck(deck1);
		player2.addDeck(deck2);
		
		System.out.println("Deck assigned to client 1: " + deck1.toString());
		System.out.println("Deck assigned to client 2: " + deck2.toString());
		
    }
    
    //write == true -> write
    //else -> read
    public void server_action(Socket client, String action) throws Exception {
    	 
		CommTool.wirte2file(getServerFile(client), action);
		 
 
    }

    //return how many cards winner gets
    //p1 is the winner: gets all p2's removedCards
    public int checkout(Player p1, Player p2) {
		p1.gainCards(p2.getRemovedCards());
		int cards_amount = p1.getNumberOfRemovedCards();
		p1.addAllRemovedCardsToDeck();
		return cards_amount;
    }
    
    public void closeConnections(Socket client) throws IOException {
    		players.remove(Clients.get(client));
		
		Clients.remove(client);
		
		s_dis.remove(client);
	
		s_dos.remove(client);
		
		s_dis.get(client).close();
		
		s_dos.get(client).close();		
		
		client.close();
		
    }

	public static void main(String[] args) {
		try {
			System.out.println("Server starting...");
			Server server = new Server(5000, 10);
			server.establish();
			System.out.println("Server started");
			while(true) {
				server.acceptClient();
				server.client_pair();
				
			}
		} catch (IOException e) {
			System.out.println("Server did not start properly.");
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}


