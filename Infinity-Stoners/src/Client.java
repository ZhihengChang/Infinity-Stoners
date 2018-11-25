import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Client {
	private Socket socket;
    private InputStream input;
    private InputStreamReader isr;
    private BufferedReader br;
    private OutputStream output;
    private OutputStreamWriter osw;
    private BufferedWriter bw;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    
    private Player player;
    
	public Client(String id, String address, int port) {
		try {
			System.out.println("Connecting to server...");
			socket = new Socket(address, port);
			System.out.println("Connected");
		}catch(UnknownHostException u) {
			
		}catch(IOException i) {
			
		}
	}
	
	public Player getPlayer() {
		return player;
	}
	
	private void newPlayer(String name) {
		player = new Player();
		System.out.print("Player name: ");
		player.setName(name);
	}
	
	private String getServerMessage() {
		try {
			input = socket.getInputStream();
			isr = new InputStreamReader(input);
			br = new BufferedReader(isr);
			String str = br.readLine();
			socket.close();
			System.out.println("Message received from the server : " + str);
			return str;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void send(Card o) {
		try {
			output = socket.getOutputStream();
			os = new ObjectOutputStream(output);
			os.writeObject(o);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public static void main(String[] args) {
		if (args.length != 0) {
			Client client = new Client(args[0], "127.0.0.1", 5000);
			
			Scanner input = new Scanner(System.in);
			System.out.println("Enter Player name: ");
			client.newPlayer(input.nextLine());
			input.close();
			System.out.println("Player " + client.getPlayer().getName() + " is ready!");
			System.out.println("Searching for another player...");
			
			if(client.getServerMessage().equals("founded")) {
				System.out.println("Player founded!");
				System.out.println("Starting the game...");
				
			}else {
				System.out.println("No Player founded, try again later");
				System.out.println("Exiting...");
				System.exit(0);
			}
			
			//String command = input.nextLine();
			System.out.println("ahdjfha;kdfj;a");
			//while(command.equals("")) {
				
			//}
			
		}else {
			
		}
		

	}

}


