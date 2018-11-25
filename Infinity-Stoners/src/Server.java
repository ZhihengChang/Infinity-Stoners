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
    private BufferedWriter bw;
    
	public Server(int port) {
		
			try {
				server = new ServerSocket(port);
				System.out.println("Server starting...");
				
				while(true) {
				socket = server.accept();
				System.out.println("Server started");
				Thread.sleep(10000);
				
				send("founded");
				}
			}catch(IOException i) {
				
			}catch(InterruptedException e) {
				
			}
		
	}
	
	private void send(String message) {
		System.out.println("is "+message);
		try {
			out = socket.getOutputStream();
            	osw = new OutputStreamWriter(out);
            	bw = new BufferedWriter(osw);
            	bw.write(message);
            	System.out.println("Message sent to the client is "+message);
            	bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}

	public static void main(String[] args) {
		Server server = new Server(5000);

	}

}


