package Server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.*;

public final class STool {
	
    //turn 1: S1, A0, B0; A2, B2; S3
    //S1(A&B):A2(SUR), S2(A&B):SUR(A2.B1) B3:new game (S2 A2), S4:ready, A5 B5
	
	//(s) server only
	//(c) client only
	
	final static int SERVER_PORT	 = 5000;
	final static int MAX_THREADS = 10;
	
	final static Logger server_logger = Logger.getLogger("SERVER_LOG");
	final static Logger clients_logger = Logger.getLogger("CLIENTS_LOG");
	
	
	final static String FILE_PERFIX_CLIENT = "Server//Data//Client_"; //Thread to Server
	final static String FILE_PERFIX_SERVER = "Server//Data//Server_"; //Server to Thread
	final static String FILE_PERFIX_LOG = "Server//log//server_";
	
	final static String KEY_PLAYER = "PLAYER";

	
	//Client A and Client B
	//Server -> client: start[found two player], server <- client: player name to server
	final static String INIT_START = "INIT_START"; //When two players have found, server send START to both player (S)
	//wait for both player name
	
	//WHILE LOOP
	
	//If(server <- NEWGAME){if((A==S||N) && (B==S||N))} reset both player's deck && reset both player's removed cards && reset turn_counter
	//Server -> client: READY, 
	//turn_counter ++;
    final static String ACTION_READY = "ACTION_READY"; //Server tells player to reveal card | param:[int turn_counter] (S)
    //client print: Turn + turn_counter:
	//client print: press enter to reveal card
    //client -> server: CONTINUE || EXIT || SURRENDER
    
    	//if(server <- EXIT) server -> another client: EXIT (another client print: your opponent's has left)
    //if(server <- A's SURRENDER), server -> client B: SURRENDER
    final static String ACTION_SURRENDER = "ACTION_SURRENDER"; //(S&C)
    		//if(server <- Client B NEWGAME) 
    			//Server goto reset (->READY)
    		//if(server <- Client B EXIT)
    			//Server -> Client A: EXIT
    	
    //if(server <- CONTINUE) server -> client: RESULT
    final static String ACTION_CONTINUE = "ACTION_CONTINUE"; //client tells server to continue to next action (C)
    
    
    //if(client A hasCardLeft && client B hasCardLeft) 
    	//reveal card
    	//server compare to get result
    //client <- RESULT || OVER: 
    final static String SHOW_RESULT = "SHOW_RESULT"; //Server return the result to client | param:[-1:LOST, 0:WAR, 1:WIN] (S)
    //client print his card && opponent's card
    //client print wins && lost
    // 
    final static String GAME_OVER = "GAME_OVER"; //Server return the result to client | param:[0:LOST, 1:WIN] (S)
    //client print: WIN || LOST
    //client print: Total turn: + turn_counter
     
    //RESULT: client -> server: CONTINUE || EXIT || SURRENDER
    //OVER: client -> server: NEWGAME || EXIT 
    

    //BACK TO READY
    
    //LOOP END
    
    final static String ACTION_NEWGAME = "ACTION_NEWGAME"; //(C)
    
    final static String ACTION_EXIT = "ACTION_EXIT"; //(S&C)
	
    final static String IS_FAIL = "FAIL";
    
    final static int SLEEP_SEC = 2000;
    

    
    public static String readfile(String filename) {
 		String response = "";
 		try {
 			//if(new File(filename).exists()) {
 				BufferedReader br = new BufferedReader(new FileReader(filename)); 
 				response = br.readLine().trim();
 				br.close();
 			//}
 		}catch(Exception e){
 			System.out.println(e.getMessage());
 		} 
 		
 		return response;
    }

    public static void wirte2file(String filename, String msg) throws IOException {
 		File data_file = new File(filename);
 		FileWriter fw = new FileWriter(data_file);
 		fw.write(msg + "\n");
 		fw.flush();
 		fw.close();
 	}
	
    public static boolean is_datafile_ready(String client_file, int read_times) {
		for(int i = 0; i < read_times; i++) {
			try {
				if(new File(client_file).exists())
					return true;
				else
					Thread.sleep(SLEEP_SEC);
				
			}catch(Exception e) {
				System.out.println("ERROR:Failed to find data file.");
			}
		}
		return false;
	}
 
    
  
    
    public static void log_config(Logger mylogger, String logfile, Level level) throws Exception{
    		Handler fh = new FileHandler(logfile);
    		SimpleFormatter formatter = new SimpleFormatter();
    		fh.setFormatter(formatter);
    		mylogger.addHandler(fh);
    		mylogger.setLevel(level);
    		

    }
    public static void delete_file(String pathfile) {
    		try {
    			Files.deleteIfExists(Paths.get(pathfile));
    		}catch(Exception e) {
    			
    		}
    }
    


}
