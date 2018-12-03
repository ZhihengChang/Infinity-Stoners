package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

import Client.CTool;
import game.Card;
import game.Deck;
import game.Pile;
import game.Player;

/*
 * @author: Zhiheng Chang
 * @date: Dec/02/2018
 */

public class GameServer {

    private ServerSocket server; 
    
    private Queue<Socket> sockets;

    private Hashtable<String, Player> players; // id and player
    private Hashtable<Socket, String> Clients; // socket and id
    
    private Hashtable<Socket, DataInputStream> s_dis;
    private Hashtable<Socket, DataOutputStream> s_dos;
    
    private Hashtable<Socket, String> next_action;
    private Hashtable<Socket, Integer> ser_action_counters; //key:clientId, value:action counter

    private int threads_counter;
    
   // final String work_dir; //server<->thread
    
    public GameServer() throws Exception{
    	 
    		this.sockets = new LinkedList<Socket>();   
    		this.players = new Hashtable<String, Player>();
    		this.Clients = new Hashtable<Socket, String>();
    		this.s_dis = new Hashtable<Socket, DataInputStream>();
    		this.s_dos = new Hashtable<Socket, DataOutputStream>();
    		this.ser_action_counters = new Hashtable<Socket, Integer>();
    		this.next_action = new Hashtable<Socket, String>();
    		this.threads_counter = 0;
    		STool.log_config(STool.server_logger, STool.FILE_PERFIX_LOG + "game.log", Level.ALL);
    		
    }
 
	public static void main(String[] args) {
		try {
			STool.server_logger.info("TODO: Start Server...");
			GameServer server = new GameServer();
			server.establish();
			STool.server_logger.info("DONE: Server started!");
			
			while(true) {
				Socket[] two_sks = new Socket[2];
				server.accept_client();
				two_sks = server.player_pair();
				if(two_sks != null) {
					STool.server_logger.info("CHECK: sockets number:" + Integer.toString(two_sks.length));
					//STool.server_logger.info(two_sks.toString());
					server.game_start(two_sks[0],two_sks[1]);
					
				}

				while(server.get_cur_sockets_num() >= STool.MAX_THREADS) {
					//Reach to max threads, so wait for release 
					STool.server_logger.info("WAITTING: reach to max threads, waitting for other players exit!");
					Thread.sleep(STool.SLEEP_SEC * 10);
				}
			}
		} catch (IOException e) {
			System.out.println("FAIL: Server did not start properly.");
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();

		}

	}
   public void establish() throws IOException {
		this.server = new ServerSocket(STool.SERVER_PORT);
		
    }	
   
   public boolean accept_client() throws IOException {
	   if(this.threads_counter + 1 > STool.MAX_THREADS) {
		   return false;
	   }
	   STool.server_logger.info("TODO: accept client...");
	   Socket sk = server.accept();
	   
	   //save socket, inputstream, outputstream and client id.
	   s_dis.put(sk, new DataInputStream(sk.getInputStream()));
	   s_dos.put(sk, new DataOutputStream(sk.getOutputStream()));
	   
	   String client_id = s_dis.get(sk).readUTF().trim();  //read id from client
	   Clients.put(sk, client_id);
	   sockets.add(sk);

	   STool.server_logger.info("ACCEPT: Client id:[" + Clients.get(sk) + "]");
	   return true;
   }
   
   public Socket[] player_pair() throws Exception {
	   STool.server_logger.info("TODO: player pair...");
	   if(sockets.isEmpty() || sockets.size() < 2) {
		   STool.server_logger.info("WAITTING: need two players to play game...");
		   return null;
	   }
	   
	   Socket sk1 = sockets.remove(); 
	   
	   Thread td1 = new ClientListener(sk1, s_dis.get(sk1), s_dos.get(sk1), Clients.get(sk1)); 
	   players.put(Clients.get(sk1), new Player());
	   ser_action_counters.put(sk1, 0);
	   td1.start();
	   
	   Socket sk2 = sockets.remove();
	   Thread td2 = new ClientListener(sk2, s_dis.get(sk2), s_dos.get(sk2), Clients.get(sk2)); 
	   players.put(Clients.get(sk2), new Player());
	   ser_action_counters.put(sk2, 0);
	   td2.start();
	   
	   STool.server_logger.info("STATUS: Found two players:");
	   STool.server_logger.info(td1.getName()+" status is " + td1.getState().toString());
	   STool.server_logger.info(td2.getName()+" status is " + td2.getState().toString());
	   Socket[] two_sks = new Socket[2];
	   two_sks[0] = sk1;
	   two_sks[1] = sk2;
	   return two_sks;
	   
   }
	
   public String get_client_file(Socket client) {
			return STool.FILE_PERFIX_CLIENT + Clients.get(client)  + ".txt";
	}
	
   public String get_server_file(Socket client) {
		return STool.FILE_PERFIX_SERVER + Clients.get(client)  + ".txt";
	}   
	
   
	public void game_start(Socket sk1, Socket sk2) throws Exception {
		
		set_next_action(sk1, sk2, STool.INIT_START);
		
		init_start(sk1, sk2);
		if(next_action.get(sk1).equals(STool.IS_FAIL) || 
				next_action.get(sk2).equals(STool.IS_FAIL))
			return;

		while(true) {
			
			play_new_game(sk1,sk2);
			if(is_game_fail(sk1, sk2)) 
				break;
			
			do_ready(sk1,sk2);
			if(is_game_fail(sk1, sk2)) 
				break;		
			
			do_result(sk1,sk2);
			if(is_game_fail(sk1, sk2)) 
				break;	
			
			if(is_exit(sk1,sk2))
				break;
			if(is_game_fail(sk1, sk2)) 
				break;
		}
		
		
		
		
	}
	
	
	
   public void init_start(Socket client1, Socket client2) throws Exception{

		if( !next_action.get(client1).equals(STool.INIT_START) ||
			!next_action.get(client2).equals(STool.INIT_START)){
			return;
		}

 		new_server_action(client1, STool.INIT_START);
		new_server_action(client2, STool.INIT_START);
 		
		//set player name
		receive_player_name(client1);
		receive_player_name(client2);
		
		STool.server_logger.info("Player1 name: " + players.get(Clients.get(client1)).getName());
		STool.server_logger.info("Player2 name: " + players.get(Clients.get(client2)).getName());

		if(!players.get(Clients.get(client1)).getName().equals("") &&
				!players.get(Clients.get(client2)).getName().equals(""))
			set_next_action(client1, client2, STool.ACTION_NEWGAME);
	
		return;
		
	 }
	 
	 public void play_new_game(Socket client1, Socket client2) throws Exception{
		 
		 if(!is_next_action(client1, client2, STool.ACTION_NEWGAME)) 
			 return;
		 
		 STool.server_logger.info("Entering new game...");
		 
		 Player player1 = players.get(Clients.get(client1));
		 Player player2 = players.get(Clients.get(client2));

		 STool.server_logger.info("client1 next action:" + next_action.get(client1));
		 STool.server_logger.info("client2 next action:" + next_action.get(client2));
		 
		 setDecksToPlayer(player1, player2);
		 
		 if(!next_action.get(client1).equals(STool.IS_FAIL)){
			 next_action.put(client1, STool.ACTION_READY);
		 }
		 if(!next_action.get(client2).equals(STool.IS_FAIL)){
			 next_action.put(client2, STool.ACTION_READY);
		 }
		 
	 }
	 
	 
	 public void do_ready(Socket client1, Socket client2) throws Exception{
		 
		 if(!is_next_action(client1, client2, STool.ACTION_READY)) 
			 return;
		 
		 new_server_action(client1, STool.ACTION_READY);
		 new_server_action(client2, STool.ACTION_READY);
		 
		 //wait for client to continue
		 String[] client1_response = wait4response(client1, get_client_file(client1), 60);
		 String[] client2_response = wait4response(client2, get_client_file(client1), 60);
		 
		 if(client1_response == null || client2_response == null) {
			 set_next_action(client1, client2, STool.IS_FAIL);
			 return;
		 }
		 if(!client1_response[1].equals(STool.ACTION_CONTINUE) || 
				 !client2_response[1].equals(STool.ACTION_CONTINUE)) {
			 set_next_action(client1, client2, STool.ACTION_EXIT);
			 return;
		 }			 
		 
		 set_next_action(client1, client2, STool.SHOW_RESULT);
	 }
	 
	 
	 public void do_result(Socket client1, Socket client2) throws Exception{
		 
		 if(!is_next_action(client1, client2, STool.SHOW_RESULT)) 
			 return;
		 
		Player player1 = players.get(Clients.get(client1));
	 	Player player2 = players.get(Clients.get(client2));
	 		
		Card player1_card = player1.revealACard();
		Card player2_card = player2.revealACard();	
 		
		int result = player1_card.compareTo(player2_card);
		
		String client1_result = STool.SHOW_RESULT + ":" + player1_card.toString() + ":" + player2_card.toString();
		String client2_result = STool.SHOW_RESULT + ":" + player2_card.toString() + ":" + player1_card.toString();

		//format: SHOW_RESULT:UR_CARD:OPP_CARD:#_of_removed_cards:int -1,0,1
		if(result == 0) {
			player1.setWar(true);
			player2.setWar(true);
			client1_result += ":0:0";
			client2_result += ":0:0";
		}else {
			player1.setWar(false);
			player2.setWar(false);
			
			if(result > 0) {
				int cards_player1_gets = checkout(player1, player2);
				client1_result += ":" + Integer.toString(cards_player1_gets) + ":1";
				client2_result += ":0:-1";
			}
			else if(result < 0) {
				int cards_player2_gets = checkout(player2, player1);
				client1_result += ":0:-1";
				client2_result += ":" + Integer.toString(cards_player2_gets) + ":1";
			}
		}
		STool.server_logger.info("RESULT:client1:" + client1_result);
		STool.server_logger.info("RESULT:client2:" + client2_result);
		new_server_action(client1, client1_result);
		new_server_action(client2, client2_result);
		
		//wait4response
		String[] client1_response = wait4response(client1, get_client_file(client1), 60);
		String[] client2_response = wait4response(client2, get_client_file(client1), 60);
		 
		if(client1_response == null || client2_response == null) {
			set_next_action(client1, client2, STool.IS_FAIL);
			return;
		}
		
		if(client1_response[1].equals(STool.ACTION_SURRENDER)) {
			 next_action.put(client2, STool.ACTION_SURRENDER);
			 return;
		}else if(client2_response[1].equals(STool.ACTION_SURRENDER)) {
			 next_action.put(client2, STool.ACTION_SURRENDER);
			 return;
		}
		
		if(!client1_response[1].equals(STool.ACTION_CONTINUE) || 
				 !client2_response[1].equals(STool.ACTION_CONTINUE)) {
			 set_next_action(client1, client2, STool.ACTION_EXIT);
			 return;
		 }	
		
		set_next_action(client1, client2, STool.ACTION_READY);
		
	 }
	 
	 
	 public boolean is_exit(Socket client1, Socket client2) throws Exception {
		 String exit_msg = STool.ACTION_EXIT;
		 if(next_action.get(client1).equals(STool.ACTION_EXIT)) {
			 exit_msg += ":" + players.get(Clients.get(client1)).getName();
		 }else if(next_action.get(client2).equals(STool.ACTION_EXIT)) {
			 exit_msg += ":" + players.get(Clients.get(client2)).getName();
		 }else
			 return false;
		 
		 
		 new_server_action(client1, exit_msg);
		 new_server_action(client2, exit_msg);
		 
		 Thread.sleep(5000);
		 STool.delete_file(get_client_file(client1));
		 STool.delete_file(get_client_file(client2));
		 STool.delete_file(get_server_file(client1));
		 STool.delete_file(get_server_file(client2));
		 
		 return true;
		 
	 }
	 
	 
	 //return how many cards winner gets
	 //p1 is the winner: gets all p2's removedCards
	 public int checkout(Player p1, Player p2) {
		 p1.gainCards(p2.getRemovedCards());
		 int cards_amount = p1.getNumberOfRemovedCards();
		 p1.addAllRemovedCardsToDeck();
		 return cards_amount;
	 }
	 
	 public boolean is_game_fail(Socket client1, Socket client2) {
		if(next_action.get(client1).equals(STool.IS_FAIL) || 
					next_action.get(client2).equals(STool.IS_FAIL)) {
				STool.server_logger.info("ERROR: Got falure from players");
				return true;
		}
		return false;
	 }
		 
	 public void set_next_action(Socket client1, Socket client2, String action) {
		 next_action.put(client1, action);
		 next_action.put(client2, action);
	 }
	 
	 public boolean is_next_action(Socket client1, Socket client2, String action) {
		 if(next_action.get(client1).equals(action) && 
				 next_action.get(client1).equals(action)) {
			 return true;
		 }	
		 return false;
	 }
	 
	 public void new_server_action(Socket client, String action) throws Exception {

		int cur_counter = ser_action_counters.get(client).intValue() + 1;
		ser_action_counters.put(client, cur_counter);

		String str_send = Integer.toString(ser_action_counters.get(client)) + ":" + action;

		STool.wirte2file(get_server_file(client), str_send);
	 }
	 
	 public boolean is_new_response(Socket client, String[] client_response) throws Exception {	
		if(client_response != null && client_response.length > 1) {
			int server_cur_counter = ser_action_counters.get(client);
			int client_new_counter = Integer.parseInt(client_response[0]);
			if( client_new_counter > server_cur_counter ) {
				//set new counter to server
				ser_action_counters.put(client, client_new_counter);
				return true;
			}
		}
		
		return false;
	}
	 
	 public String[] wait4response(Socket client, String client_file, int read_times) throws Exception{
		
		 
		 if(!STool.is_datafile_ready(client_file, read_times))
			 return null;
		 
		 String[] client_response = null;
		 for(int i = 0; i < read_times; i++) {
			  {
				  client_response = STool.readfile(client_file).split(":");
				  STool.server_logger.info("Client response: ");
				  //STool.server_logger.info(client_response[2]);
				 if(is_new_response(client, client_response)) {
					 break;
				 } 
			 }
			 Thread.sleep(STool.SLEEP_SEC);
		 }
		 return client_response;
 
	 }
	 
	public void receive_player_name(Socket client) throws Exception{
		
		if(!next_action.get(client).equals(STool.INIT_START))
			return;
		
		String client_file = get_client_file(client);
		
		STool.server_logger.info("Todo: Check client data file: " + client_file );
		
		String[] client_response = wait4response(client, client_file,  60);
		
		if(client_response == null ||
			client_response[2] == null || 
			client_response[2].length() == 0) {
			STool.server_logger.info("ERROR:no player name found!");
			next_action.put(client, STool.IS_FAIL);
			
			return;
		}
		
		players.get(Clients.get(client)).setName(client_response[2].trim());
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
		
		STool.server_logger.info("Deck assigned to client 1: " + player1.getName() +" - "+ deck1.toString());
		STool.server_logger.info("Deck assigned to client 2: " + player2.getName() +" - "+ deck2.toString());
		
	}
	
	public int get_cur_sockets_num() {
		return Clients.size();
	}
	
}
