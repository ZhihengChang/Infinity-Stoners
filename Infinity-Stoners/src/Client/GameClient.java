package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;

import Server.STool;

public class GameClient {
	private Socket socket;
    private String client_id;
    private String server_address;
    private int server_port;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Scanner input;
    private String do_next;
    private int cur_action_counter;
    private int server_new_counter;
    private String player_name;
    
	public GameClient(String id) throws Exception {
		this.client_id = id;
		this.server_address = CTool.SERVER_ADDRESS;
		this.server_port = CTool.SERVER_PORT;
		this.cur_action_counter = 0;
		this.do_next = "";
		CTool.log_config(CTool.client_logger, CTool.FILE_PERFIX_LOG + id + ".log", Level.OFF);
	}
	
	private void connect_server() throws Exception {
		socket = new Socket(server_address, server_port);
		dis = new DataInputStream(socket.getInputStream()); 
		dos = new DataOutputStream(socket.getOutputStream());
		input = new Scanner(System.in);
		System.out.println("Connecting to server...");
		System.out.println("Connection Status: " + socket.isConnected());
		send_id_to_server(client_id);
		
	}	 

	public static void main(String[] args) throws Exception{
		
		String client_id = "";
		 
		if (args.length <= 0) {
			CTool.prt("==> Please run with client id!");
			return;
		}else {
			client_id = args[0];
		}
	
		
		GameClient client = new GameClient(client_id);
		
		client.connect_server();
		/*
		 * Search for player, and start game
		 */
		CTool.prt_header();
		
		
		
		client.start_game();
		
		CTool.prt("---GAME OVER---");
		client.input.close();
		client.dis.close();
		client.dos.close();
		client.socket.close();
		
		
	}
	
	public void cleanup() {
		try {
			input.close();
			dis.close();
			dos.close();
			socket.close();
			
		}catch(Exception e) {
			
		}
		
	}
	
	public void set_playername(String name) {
		this.player_name = name;
	}
	
	public void start_game() throws Exception {
		
		CTool.prt("\n Waiting for another player to enter the game...");
		
		while(true) {
			
			String[] server_response = receive_new_response();

			if(server_response == null) {
				//do_next = CTool.IS_FAIL;
				CTool.client_logger.info("FAIL: No response from server!");
				break;
			}
			
			do_next = server_response[1];
			
			do_client_init(server_response);
			
			do_ready(server_response);

			do_result(server_response);

			if(is_fail() || is_exit(server_response))
				break;
		}
		
	
	}
	
	
	
	
	public void do_client_init(String[] server_response) throws Exception{
		if(!this.do_next.equals(CTool.INIT_START))
			return;
		
		CTool.client_logger.info("To do client init and get player name!");
		 
		
		CTool.client_logger.info("Entering player name");
		String player_name = CTool.get_input_from_screen(input,"Please enter Player name: ");
		CTool.get_input_from_screen(input,"Please press Enter to get result: ");
		
		set_playername(player_name); 		
		String key_player = CTool.KEY_PLAYER + ":" + this.player_name;	
		
		send_action_to_server(key_player);
		
		//this.do_next = CTool.ACTION_READY;
		// next: wait for server to response: READY
			 
		
	}
	

	
	public void do_ready(String[] server_response) throws Exception{
		//Server to client: READY, EXIT, NEWGAME
		if(!this.do_next.equals(CTool.ACTION_READY)){
			return;
		}
	
		//String client_response = CTool.get_input_from_screen(input,"Press Enter to continue (Q to exit):");
		String client_response = CTool.ACTION_CONTINUE;
		client_response = CTool.client_response_for_return(client_response);
		
		send_action_to_server(client_response);
		
		 
				
	}
	
	
	
	public void do_result(String[] server_response) throws Exception{
		
		if(!this.do_next.equals(CTool.SHOW_RESULT)) {
			return;
		}

		//format: SHOW_RESULT:UR_CARD:OPP_CARD:#_of_removed_cards:int -1,0,1
		//Client print: your card and opp's card
		CTool.prt_h1( "Your card is: " + server_response[2]);
		CTool.prt_h1("Your opponent's card is: " + server_response[3]);
	
		//Client print: LOST | WAR | WIN
		if(Integer.parseInt(server_response[5]) == -1) {
			//You lost
			CTool.prt_h1("*** You LOST this turn! ***");
		}else if(Integer.parseInt(server_response[5]) == 0) {
			//Tie
			CTool.prt_h1("!!!YOU AND YOUR OPPONENT ARE AT WAR!!!");
			
		}else if(Integer.parseInt(server_response[5]) == 1) {
			//You win
			CTool.prt_h1("*** You WIN the turn! ***");
			CTool.prt_h1("You get total " + server_response[4] + " cards");
		}
		
		//String client_response = CTool.get_input_from_screen(input,"Press Enter to continue (S to surrender and start a new game, Q to exit):");
		String client_response = CTool.get_input_from_screen(input,"=> Press Enter to continue (Q to exit):");
		client_response = CTool.client_response_for_return(client_response);
		
		send_action_to_server(client_response);

		//next: wait for server to response: READY
	}
	
	
	
	public boolean is_fail() throws Exception{
		if(this.do_next.equals(CTool.IS_FAIL)) {
			CTool.prt("=> Program got fail, so exit the game!");
			return true;
		}
		return false;
	}
	
	public boolean is_exit(String[] server_response) throws Exception{
		if(server_response[1].equals(CTool.ACTION_EXIT)) {
			
			if(server_response.length > 2) {
				CTool.prt("### Player " + server_response[2] + " has left the game ###");
			}
			//close socket, input, dis, dos
			/*
	
			*/
			this.input.close();

			//CTool.prt("Program exited");
			return true;
		}
		return false;
	}	
	
	
	public String[] receive_new_response() throws Exception {	
		String response[] = dis.readUTF().trim().split(":");
		if(response != null && response.length >= 2) {	
			this.server_new_counter = Integer.parseInt(response[0]);
			if( this.server_new_counter > this.cur_action_counter ) {
				CTool.client_logger.info("recieved server new action: " + response[1]);
				this.cur_action_counter = this.server_new_counter;
				return response;
			}
		} 
		
		return null;
	}
	
	public void send_action_to_server(String msg) throws IOException {
		this.cur_action_counter++;
		dos.writeUTF(Integer.toString(this.cur_action_counter) + ":" + msg + "\n");
		dos.flush();
	}
	public void send_id_to_server(String id) throws IOException {
		dos.writeUTF(id + "\n");
		dos.flush();
		CTool.client_logger.info("SUCCESS:Send id to server!");
	}
	

}
