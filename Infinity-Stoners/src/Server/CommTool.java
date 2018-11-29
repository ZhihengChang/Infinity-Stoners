package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public final class CommTool {
	
	final static String FILE_PERFIX_CLIENT = "Server//Data//Client_"; //Thread to Server
	final static String FILE_PERFIX_SERVER = "Server//Data//Server_"; //Server to Thread
	
	final static String PLAYER = "PLAYER";
	
    final static String ACTION_READY = "ACTION:Ready";
    final static String ACTION_START = "ACTION:Start";
    final static String ACTION_NEWT = "ACTION:NewTurn";
    final static String ACTION_SHOWC = "ACTION:ShowCard";
    final static String ACTION_EXIT = "ACTION:Exit";
    final static String ACTION_NEWG = "ACTION:NewGame";
    final static String ACTION_RESULT = "ACTION:Result";
    final static String ACTION_TURNEND = "ACTION:TurnEnd";
    
    final static String RESULT_WIN = "RESULT:Win";
    final static String RESULT_LOST = "RESULT:Lost";
    final static String RESULT_WAR = "RESULT:War";
    
    final static String GAME_END = "GAME:End";
    final static String GAME_TIMEOUT = "GAME:TimeOut";
    // Format data: key:message
    /*
     * For example:
     * 		PLAYER:player name
     * 		ACTION: Quit, Exit, Wait, Ready
     * 		NEWTURN: 1: card info
     */
    public static String[] wait4message(String filename, String identify_key, int wait_minutes) {
    		
		for(int i=0; i < wait_minutes*30; i++) {
			if(new File(filename).exists()) {

				String[] message = readfile(filename).split(":");
				if(message[0].equals(identify_key)) {
					return message;
				}
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


		}
		System.out.println("Client response timeout");
		return null;


    	}
    public static boolean wait4key(String filename, String identify_key, int wait_minutes) {
		
  		for(int i=0; i < wait_minutes*30; i++) {
  			if(new File(filename).exists()) {

  				if(readfile(filename).startsWith(identify_key)) {
  					return true;
  				}
  				 
  			}
  			try {
  				Thread.sleep(2000);
  			} catch (InterruptedException e) {
  				e.printStackTrace();
  			}

  		}
  		System.out.println("Client response timeout");
  		return false;


      	}    
   // If no key, then return key;
   public static String wait4return(String filename, String identify_key, int wait_minutes) throws Exception{
		
  		for(int i=0; i < wait_minutes*30; i++) {
  			if(new File(filename).exists()) {
  				String return_str = readfile(filename);
  				if(return_str.startsWith(identify_key)) {
  					//if string return length > string key length, then return string behind key.
  					if(return_str.length() > identify_key.length())
  						// string = 1:2, len =3, substring(1, 3) = :2 
  						return return_str.substring(identify_key.length()+1, return_str.length());
  					else
  						return identify_key;
  				}
  				 
  			}
  			
  			Thread.sleep(2000);
  			 

  		}
  		System.out.println("Client response timeout");
  		return "";


      	} 
   public static boolean check_response(String filename, String identify_key) {
	   if(readfile(filename).startsWith(identify_key))
			   return true;	   
	   return false;
   }
    
    public static void wirte2file(String filename, String msg) throws IOException {
 		File client_file = new File(filename);
 		FileWriter fw = new FileWriter(client_file);
 		fw.write(msg + "\n");
 		fw.flush();
 		fw.close();
 	}
	public static boolean is_data_ready(String client_file, int time_out) {
		for(int i = 0; i < 30 * time_out; i++) {
			try {
				if(new File(client_file).exists())
					return true;
				else
					Thread.sleep(2000);
				
			}catch(Exception e) {
				System.out.println("ERROR:Failed to find data file.");
			}
		}
		return false;
	}
     public static String readfile(String filename) {//throws IOException {
 		String response = "";
 		try {
 			//if(new File(filename).exists()) {
 				BufferedReader br = new BufferedReader(new FileReader(filename)); 
 				response = br.readLine();
 				br.close();
 			//}
 		}catch(Exception e){
 			System.out.println(e.getMessage());
 		} 
 			 
 		
 		return response;
 	}
     
     public static void deletFile(String filename) {
    	 	new File(filename).delete();

     }

}
