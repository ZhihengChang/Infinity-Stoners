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
    			System.out.println();
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
    public void do_newturn(Socket client1, Socket client2, int pre_turn) throws Exception{
    		// check client if new turn
    		String client1_data_file = getClientFile(client1);
    		String client2_data_file = getClientFile(client2);
    		
    		String client1_data = CommTool.wait4return(client1_data_file, CommTool.ACTION_NEWT, 2);
    		String client2_data = CommTool.wait4return(client2_data_file, CommTool.ACTION_NEWT, 2);
    		
    		if(client1_data.equals("") || client1_data.equals("")) {
    			System.out.println("ERROR:client data file is not ready!");
    			return;
    		}
    		
    		// num, string key, ""
    		if(client1_data.equals(CommTool.ACTION_NEWT) || client2_data.equals(CommTool.ACTION_NEWT)) {
    			System.out.println("ERROR:invalid request");
    			return;
    		}

    		if(Integer.parseInt(client1_data) > pre_turn && Integer.parseInt(client2_data) > pre_turn) {
    			do_show_card(client1, client2);
    			do_result(client1, client2);
    		}else {
    			System.out.println("ERROR:invalid turn");
    			return;
    		}

    		
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
    		System.out.println("player1 cards size:" + player1_removedCards.getSize());
    		Card card_to_compare_p1, card_to_compare_p2;
    		
    		if(player1_removedCards.getSize() > 0)
    			card_to_compare_p1 = player1_removedCards.getCard(player1_removedCards.getSize()-1);
    		else
    			card_to_compare_p1 = null;
    		
    		System.out.println("player2 cards size:" + player2_removedCards.getSize());
    		if(player2_removedCards.getSize() > 0)
    			card_to_compare_p2 = player2_removedCards.getCard(player2_removedCards.getSize()-1);
    		else
    			card_to_compare_p2 = null;
		
		int result = card_to_compare_p1.compareTo(card_to_compare_p2);
		
		if(result == 0) {
			player1.setWar(true);
			player2.setWar(true);
			server_action(client1,CommTool.RESULT_WAR);
			server_action(client2,CommTool.RESULT_WAR);
		}else {
			player1.setWar(false);
			player2.setWar(false);
			
			if(result > 0) {
				int cards_player1_gets = checkout(player1, player2);
				server_action(client1,CommTool.RESULT_WIN + ":" + cards_player1_gets);
				server_action(client2,CommTool.RESULT_LOST);
			}
			else if(result < 0) {
				int cards_player2_gets = checkout(player2, player1);
				server_action(client2,CommTool.RESULT_WIN + ":" + cards_player2_gets);
				server_action(client1,CommTool.RESULT_LOST);
			}
		}
		
		
    }
    
    public Player getPlayer(Socket client) {
    		return players.get(Clients.get(client));
    }
    
    public void game_start(Socket client1, Socket client2) throws Exception {
    		initializeTheGame(client1, client2);
    		
    		//Get two players
    		Player player1 = players.get(Clients.get(client1));
		Player player2 = players.get(Clients.get(client2));
    		
		//Counter for game
		int game_counter = 0;
		
		while(true) {
			// Step 1: Send ready to client
			System.out.println("To Ready");
			server_action(client1,CommTool.ACTION_READY);
			server_action(client2,CommTool.ACTION_READY);
			//new turn
			do_newturn(client1,client2,game_counter);
			game_counter++;
			
			// Step 2:Send showcard

			// Step 3: Send Result

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
				
				if(CommTool.wait4key(getClientFile(client1), CommTool.ACTION_NEWG, 2) &&
						CommTool.wait4key(getClientFile(client2), CommTool.ACTION_NEWG, 2)) {
					//new game
					game_counter++;
					
					continue;
					
				}	
				if(CommTool.wait4key(getClientFile(client1), CommTool.ACTION_EXIT, 1) ||
						CommTool.wait4key(getClientFile(client2), CommTool.ACTION_EXIT, 1)) {
					//Exit
 
					break;
				}else {//break;
					System.out.println("No get response from client...!");
					break;
					
				}

			}
		}
		
		// Step 4: Game end
		//do_game_end
		closeConnections(client1);
		closeConnections(client2);
		
		
    }
    
    public void initializeTheGame(Socket client1, Socket client2) throws Exception {
    		
		//send start
    		server_action(client1,CommTool.ACTION_START);
    		server_action(client2,CommTool.ACTION_START);
		//set player name
		initialize_players(client1, client2);
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
    		String player_name = CommTool.wait4return(config, CommTool.PLAYER, 3);
		if(player_name != null && player_name.length() !=0) {
			System.out.println("Player name: " + player_name);
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
   
   
    
    /*
	 * start -> ready -> loop [ enter -> server:result -> client:result -> enter ... -> result:end]
	 * 
	 */
    
    public void game_start2(Socket client1, Socket client2) {
    		boolean new_game = true;
	    	while(new_game) {
	    		//initializeTheGame(client1, client2);
	    		try {
	    			int pre_turn = 0;
	    			
	    			String client1_data_file = getClientFile(client1);
	        		String client2_data_file = getClientFile(client2);
	        		
	        		Player player1 = players.get(Clients.get(client1));
				Player player2 = players.get(Clients.get(client2));
	        		
	        		while(player1.hasCardsLeft() && player2.hasCardsLeft()) {
	        			System.out.println("game_start comf");
	        			CommTool.wirte2file(getClientFile(client1), CommTool.ACTION_READY);
	        			CommTool.wirte2file(getClientFile(client2), CommTool.ACTION_READY);
	        			
	        			//NewTurn
	        			boolean is_new_turn_p1 = CommTool.wait4key(client1_data_file, CommTool.ACTION_NEWT, 3);
	        			boolean is_new_turn_p2 = CommTool.wait4key(client2_data_file, CommTool.ACTION_NEWT, 3);
	        			if(!is_new_turn_p1 || !is_new_turn_p2) {
	        				break;
	        			}
	        			
	        			int player1_turn_counter = Integer.parseInt(CommTool.readfile(client1_data_file).split(":")[2]);
	        			int player2_turn_counter = Integer.parseInt(CommTool.readfile(client2_data_file).split(":")[2]);
	        			
	        			
	        			//String[] reveal_card_player1 = CommTool.wait4message(client1_data_file, "ACTION", 3);
	        			//String[] reveal_card_player2 = CommTool.wait4message(client1_data_file, "ACTION", 3);
	        	
        				if(player1_turn_counter > pre_turn && player2_turn_counter > pre_turn) {
        					if(player1_turn_counter != player2_turn_counter) {
        						System.out.println("Invalid turn");
        						break;
        					}
        					Card player1_card = player1.revealACard();
        					Card player2_card = player2.revealACard();
        					CommTool.wirte2file(client1_data_file, CommTool.ACTION_SHOWC + ":" + player1_card.toString() + ":" + player2_card.toString());
        					CommTool.wirte2file(client2_data_file, CommTool.ACTION_SHOWC + ":" + player2_card.toString() + ":" + player1_card.toString());
        					
        					
        					int result = player1_card.compareTo(player2_card);
        					if(result == 0) {
        						player1.setWar(true);
        						player2.setWar(true);
        						CommTool.wirte2file(client1_data_file, CommTool.RESULT_WAR);
            					CommTool.wirte2file(client2_data_file, CommTool.RESULT_WAR);
        					}else {
        						player1.setWar(false);
        						player2.setWar(false);
        						
        						if(result > 0) {
        							int cards_player1_gets = checkout(player1, player2);
        							CommTool.wirte2file(client1_data_file, CommTool.RESULT_WIN + ":" + cards_player1_gets);
                					CommTool.wirte2file(client2_data_file, CommTool.RESULT_LOST);
        						}
        						else if(result < 0) {
        							int cards_player2_gets = checkout(player2, player1);
        							CommTool.wirte2file(client1_data_file, CommTool.RESULT_LOST);
                					CommTool.wirte2file(client2_data_file, CommTool.RESULT_WIN + ":" + cards_player2_gets);
        						}
        					}
        					
        					pre_turn = player1_turn_counter;
        				
        				}
	        		}
	        		//Game ends
	        		//p1 wins
	        		if(player1.hasCardsLeft() && !player2.hasCardsLeft()) {
	        			CommTool.wirte2file(client1_data_file, CommTool.GAME_END + ":" + player1.getName());
	        			String[] player1_response = CommTool.wait4message(client1_data_file, "ACTION", 3);
		    			String[] player2_response = CommTool.wait4message(client1_data_file, "ACTION", 3);
		    			if(!player1_response[1].equals("NewGame") || !player2_response[1].equals("NewGame")) {
		    				new_game = false;
		    			}
	        		}else {
	        			CommTool.wirte2file(client1_data_file, CommTool.GAME_TIMEOUT);
	        			CommTool.wirte2file(client1_data_file, CommTool.GAME_TIMEOUT);
	        			new_game = false;
	        		}
	        		
			} catch (IOException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	}
	    
	    	try {
	    		closeConnections(client1);
	    		closeConnections(client2);
		} catch (IOException e) {	
			e.printStackTrace();
		}
	    		
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


