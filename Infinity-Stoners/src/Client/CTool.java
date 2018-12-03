package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
/*
 * @author: Zhiheng Chang
 * @date: Dec/02/2018
 */

public class CTool {
	
		final static String SERVER_ADDRESS = "127.0.0.1";
		final static int SERVER_PORT = 5000;
	
		final static String KEY_PLAYER = "PLAYER";

		final static String INIT_START = "INIT_START"; //When two players have found, server send START to both player (S)

	    final static String ACTION_READY = "ACTION_READY"; //Server tells player to reveal card | param:[int turn_counter] (S)


	    final static String ACTION_SURRENDER = "ACTION_SURRENDER"; //(S&C)]
	    	
	    final static String ACTION_CONTINUE = "ACTION_CONTINUE"; //client tells server to continue to next action (C)

	    final static String SHOW_RESULT = "SHOW_RESULT"; //Server return the result to client | param:[-1:LOST, 0:WAR, 1:WIN] (S)

	    final static String GAME_OVER = "GAME_OVER"; //Server return the result to client | param:[0:LOST, 1:WIN] (S)
   
	    final static String ACTION_NEWGAME = "ACTION_NEWGAME"; //(C)
	    
	    final static String ACTION_EXIT = "ACTION_EXIT"; //(S&C)

	    //final static String RESULT_FAIL = "RESULT_FAIL";
	    
	    final static String TIMEOUT = "TIMEOUT";//(S)
		
	    final static String IS_FAIL = "FAIL";
		
	    final static int SLEEP_SEC = 2000;
	    
		final static Logger client_logger = Logger.getLogger("CLIENT_LOG");
		
		final static String FILE_PERFIX_LOG = "Client//log//client_";
		
		final static String HEADER = "    ";
	    
	    public static String get_input_from_screen(Scanner input, String msg) {
			System.out.print(msg);
			return input.nextLine().trim();
	    }
	
	    public static String client_response_for_return(String input_str) {

	    		if(input_str.equalsIgnoreCase("q"))
				return CTool.ACTION_EXIT;
	    		/*
	    		if(input_str.equalsIgnoreCase("s"))
	    			return CTool.ACTION_SURRENDER;
	    		*/
	    		
	    		return CTool.ACTION_CONTINUE; 
	    }
	    
	    public static String readfile(String filename) {
	 		String response = "";
	 		try {
 				BufferedReader br = new BufferedReader(new FileReader(filename)); 
 				response = br.readLine();
 				br.close();
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

	    public static void prt(String msg) {
	  
	    		System.out.println(msg);

	    }
	    
	    public static void prt_h1(String msg) {
	  	  
    			System.out.println(HEADER + msg);

	    }
	    public static void prt_h2(String msg) {
		  	  
    			System.out.println(HEADER + HEADER + msg);

	    }

	    public static void prt_header() {
	    		String header = "";
	    		String msg = "     !!! Wellcome to war !!!    ";
	    		int max = 40;
	    		header += fillstr(4, " ") + fillstr(msg.length(), "*") + fillstr(6, "*") + "\n";
	    		header += fillstr(4, " ") + "*" + fillstr(msg.length(), " ") + fillstr(4," ") + "*" + "\n";
	    		header += fillstr(4, " ") + "*     !!! Wellcome to war !!!    " + fillstr(4, " ") + "*" + "\n";
	    		header += fillstr(4, " ") + "*" + fillstr(msg.length(), " ") + fillstr(4," ") + "*" + "\n";
	    		header += fillstr(4, " ") + fillstr(msg.length(), "*") + fillstr(6,"*") +"\n";
	    		 
	    		//String header = "WELLCOME TO WAR"; 
	    		prt(header);
	    		 
	    }
	    public static String fillstr(int n, String s) {
	    		String p="";
	    		for(int i=0; i<n; i++) {
	    			p += s;
	    		}
	    		return p;
	    }
	    public static void log_config(Logger mylogger, String logfile, Level level) throws Exception{
    		Handler fh = new FileHandler(logfile);
    		SimpleFormatter formatter = new SimpleFormatter();
    		fh.setFormatter(formatter);
    		mylogger.addHandler(fh);
    		mylogger.setLevel(level);
    		

    }

}
