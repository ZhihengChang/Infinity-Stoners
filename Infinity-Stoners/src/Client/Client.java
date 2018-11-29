package Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {

	/*
	 * turn_counter
	 * new game
	 */
    
    private Socket socket;
    private String client_id;
    private String server_address;
    private int server_port;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Scanner input;
    private String do_next;
    private int client_turn_counter;
    private String player_name;
    
	public Client(String id, String address, int port) {
		this.client_id = id;
		this.server_address = address;
		this.server_port = port;
	}
	
	private void connect_server() throws Exception {
			socket = new Socket(server_address, server_port);
    			dis = new DataInputStream(socket.getInputStream()); 
			dos = new DataOutputStream(socket.getOutputStream());
			input = new Scanner(System.in);
			System.out.println("Connecting to server...");
			System.out.println("Connection Status: " + socket.isConnected());
			send(client_id);
	}
	
	private void closeConnection() {
		try {
			socket.close();
			dis.close();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getName() {
		return player_name;
	}
	
	private void setName(String player_name) {
		this.player_name = player_name;
	}
	
	private String getServerMessage() throws Exception{
	
			return dis.readUTF();
			 
		
	}
	
	
	private void send(String message) throws Exception {
 
			dos.writeUTF(message);
			dos.flush();
	 
        
	}
	
	public void check_and_set_doNext(String c_response, String msg_to_server) {
		if(c_response.trim().equals("")) { // Type enter
			
			do_next = msg_to_server;			
		}else if(c_response.equalsIgnoreCase("exit")) {
			do_next = ClientTool.ACTION_EXIT	;			
		}
	}
	 
	public void do_ready(String response) throws Exception{
		// user input: enter, exit
		if(response.startsWith(ClientTool.ACTION_READY)) {
			String result = ClientTool.get_input_from_screen(input, "press Enter to reveal the top card of your deck:");
			check_and_set_doNext(result, ClientTool.ACTION_NEWT);

			send(do_next);
		
		}
		
	}
	public void do_show_card(String response) throws Exception{
		if(response.startsWith(ClientTool.ACTION_SHOWC)) {
			
			String your_card = response.split(":")[2];
			String opp_card = response.split(":")[3];
			System.out.println("Your card is: " + your_card);
			System.out.println("Your opponent's card is: " + opp_card);
			
			String result = ClientTool.get_input_from_screen(input, "press Enter to continue:");
			
			check_and_set_doNext(result, ClientTool.ACTION_RESULT);
			send(do_next);
		}
		
	}
	
	public void do_result(String response) throws Exception{
		//do win
		if(response.startsWith(ClientTool.RESULT_WIN)) {
			 
			System.out.println("*You WIN the turn!");
			System.out.println("You gets All " + response.split(":")[2] + " cards");
			
		}
		//do lost
		if(response.startsWith(ClientTool.RESULT_LOST)) {
				System.out.println("*You LOST the turn.");
		}
		//do war
		if(response.startsWith(ClientTool.RESULT_WAR)) {
			System.out.println("!!!YOU AND YOUR OPPONENT ARE AT WAR!!!");
		}
		
		String result = ClientTool.get_input_from_screen(input, "press Enter to continue:");
		check_and_set_doNext(result, ClientTool.ACTION_TURNEND);
		send(do_next);
		
	}
	
	public void do_timeout(String response) throws Exception{
		if(response.equals(ClientTool.GAME_TIMEOUT)) {
			System.out.println("Players opperation time out");
			System.out.println("Exiting...");
			do_next = ClientTool.ACTION_EXIT;
		}
	}
	
	
		
	public void do_end(String response) throws Exception{
		if(response.startsWith(ClientTool.GAME_END)) {
			//GAME : End : Player_name : total_turn
		 
				if(response.split(":")[2].equals(getName())) {
					System.out.println("CONGRATULATIONS! You WIN the game!");
				}else {
					System.out.println("SORRY. You LOST the game.");
				}
				System.out.println("Total number of turns: " + response.split(":")[3]);
		
				String result = ClientTool.get_input_from_screen(input, "Enter Exit to exit game | press Enter to starts a new game: ");
				 
				if(result.equalsIgnoreCase("")) {
					do_next = ClientTool.ACTION_NEWG;
					
				}else if(result.equalsIgnoreCase("exit")){
					do_next = ClientTool.ACTION_EXIT;
				}
				send(do_next);
			}
		 

	}
	public static void main(String[] args) throws Exception{
		if (args.length <= 0) {
			System.out.println("Please run with client id!");
			return;
		}
			Client client = new Client(args[0], "127.0.0.1", 5000);
			client.connect_server();
			 
			Scanner input = new Scanner(System.in);
			String player_name = ClientTool.get_input_from_screen(input,"Enter Player name: ");
			
			client.setName(player_name);
			 
			System.out.println("Player " + client.getName() + " is ready!");
			System.out.println("Searching for another player...");
			String response = client.getServerMessage();
			
			
			if(!response.trim().equals("")) {
				if(response.startsWith(ClientTool.ACTION_START)){
					System.out.println("send player name");
					client.send(client.getName());
				}
			}else {
				System.out.println("Response is null!");
				return;
			}
			
			
			System.out.println("Player founded!");
			System.out.println("Starting the game...");
			
			
			while (true) { 
				response = "";
				try {
					response = client.getServerMessage();
					if(response != null) {
						client.do_next="";
						client.do_ready(response);
						if(client.do_next == ClientTool.ACTION_EXIT) {
							break;
						}
						client.do_next="";
						client.do_show_card(response);
						if(client.do_next == ClientTool.ACTION_EXIT) {
							break;
						}
						client.do_next="";
						client.do_result(response);
						if(client.do_next == ClientTool.ACTION_EXIT) {
							break;
						}
						client.do_next="";
						client.do_timeout(response);
						if(client.do_next == ClientTool.ACTION_EXIT) {
							break;
						}
						client.do_next="";
						client.do_end(response);
						if(client.do_next == ClientTool.ACTION_EXIT) {
							Thread.sleep(2000);
							break;
						}
						
						Thread.sleep(2000);
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
                
                  
            }
			input.close();
			client.closeConnection();

		

	}

}


