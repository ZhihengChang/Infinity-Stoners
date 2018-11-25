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
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String message = br.readLine();
			//System.out.println("Message received from the server: " + str);
			return message;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void send(Card o) {
		try {
			os = new ObjectOutputStream(socket.getOutputStream());
			os.writeObject(o);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	private void send(String message) {
		try {
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(message + "\n");
            bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public static void main(String[] args) {
		if (args.length != 0) {
			Client client = new Client(args[0], "127.0.0.1", 5000);
			client.send(args[0]);
			
			Scanner input = new Scanner(System.in);
			System.out.println("Enter Player name: ");
			client.newPlayer(input.nextLine());
			input.close();
			System.out.println("Player " + client.getPlayer().getName() + " is ready!");
			System.out.println("Searching for another player...");
			String message = client.getServerMessage();
			while(!message.equals("founded")) {
				System.out.println("...");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Player founded!");
			System.out.println("Starting the game...");
			
			//String command = input.nextLine();
			//while(command.equals("")) {
				
			//}
			
		}else {
			
		}
		

	}

}


