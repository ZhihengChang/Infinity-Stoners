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

public class Server {
	
	private Socket socket; 
    private ServerSocket server; 
    private InputStream in; 
    private OutputStream out;
    private OutputStreamWriter osw;
    private BufferedReader br;
    private BufferedWriter bw;
    
    
	public Server(int port) {
		try {
			server = new ServerSocket(port);
			System.out.println("Server starting...");
			while(true) {
				System.out.println("waiting for client...");
				socket = server.accept();
				System.out.println("Server started");
				//Thread.sleep(10000);
				String message = receive();
				send("waiting");
				System.out.println("waiting message sent to client");

			}
		}catch(IOException i) {
				
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
	
	private String receive() {
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String message = br.readLine();
			return message;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		Server server = new Server(5000);

	}

}


