package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class ClientHandler extends Thread{
	
	final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket socket; 
    
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
		this.socket = s;
		this.dis = dis;
		this.dos = dos;
	}

	  @Override
	    public void run()  
	    { 
	        String received; 
	        String toreturn; //output to client
	        while (true)  
	        { 
	            try { 
	  
	                // Ask user what he wants 
	                dos.writeUTF("What do you want?[Date | Time]..\n"+ 
	                            "Type Exit to terminate connection."); 
	                  
	                // receive the answer from client 
	                received = dis.readUTF(); 
	                  
	                if(received.equals("Exit")) 
	                {  
	                    System.out.println("Client " + this.socket + " sends exit..."); 
	                    System.out.println("Closing this connection."); 
	                    this.socket.close(); 
	                    System.out.println("Connection closed"); 
	                    break; 
	                } 
	                  
	                // write on output stream based on the 
	                // answer from the client 
	                switch (received) {
	                          
	                    default: 
	                        dos.writeUTF("Invalid input"); 
	                        break; 
	                } 
	            } catch (IOException e) { 
	                e.printStackTrace(); 
	            } 
	        } 
	          
	        try
	        { 
	            // closing resources 
	            this.dis.close(); 
	            this.dos.close(); 
	              
	        }catch(IOException e){ 
	            e.printStackTrace(); 
	        } 
	    }
	  
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
