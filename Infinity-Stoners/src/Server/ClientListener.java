package Server;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Level;

import game.*;
public class ClientListener extends Thread {

	private Socket socket; 
	private String id;
	DataInputStream s_dis; 
    DataOutputStream s_dos; 
    
    private int turn_counter;
    private int cur_action_counter;
    
   // private File client_file;
    private String client_filename;
    private String server_filename;
    private String s_response;
    
    private String client_log_file;
    private String do_next;
	//Client Information
	/*
	 * Client ID, Card Pile
	 * 
	 * client_response: 
	 * 	keys: 
	 * 		key1:"CurState" - Values: "NoResponse", "Exit", "CardInfo": -1, 0, 1, 2, 3 ....
	 * 		key2:   
	 * 
	 */
	public ClientListener(Socket socket, DataInputStream dis, DataOutputStream dos, String client_id)throws Exception {
		this.socket = socket;
		//this.id = id;
		this.s_dis = dis;
		this.s_dos = dos;
		
		
		this.client_filename = STool.FILE_PERFIX_CLIENT + client_id + ".txt";
		this.server_filename = STool.FILE_PERFIX_SERVER + client_id + ".txt";
		this.client_log_file = STool.FILE_PERFIX_LOG + client_id + ".log";
		
		this.turn_counter = 0;
		//get server action from data file.
		this.cur_action_counter = 0;
		this.do_next = STool.INIT_START;
		STool.log_config(STool.clients_logger, this.client_log_file , Level.OFF);
		STool.server_logger.info("THREAD: Client id:[" + client_id + "]");
		
	}
	
	public boolean is_new_response(String[] response) throws Exception {	
	if(response != null && response.length > 1) {
		 
		int new_action_counter = Integer.parseInt(response[0]);
		if( this.cur_action_counter < new_action_counter ) {
			this.cur_action_counter = new_action_counter;
			return true;
		}
	}
	
		return false;
	}
 
	 
	public void new_client_action(String c_response) throws Exception{
		int client_counter = Integer.parseInt(c_response.split(":")[0]);
		if(client_counter > cur_action_counter) {
			//s_dos.writeUTF(c_response);
			STool.wirte2file(client_filename, c_response);
		}else {
			STool.server_logger.info("ERROR: It is not a new client action");
		}
		return;
	}
	
	public void receive_server_action() throws Exception{
		 
		// Wait for new action.
		if(do_next.equals(STool.IS_FAIL)){
			return;
		}
		String s_response = wait_new_action(server_filename,30);
		if(s_response.equals("")) {
			this.do_next = STool.IS_FAIL;
			STool.server_logger.info("TIMEOUT: NO FOUND new action from server!");
			return;
		}
		do_next = s_response.split(":")[1].trim();
		//if(do_next.equals(STool.ACTION_EXIT))
		//	return;
		//Server send init to client, the client send player name.
		s_dos.writeUTF(s_response);
		s_dos.flush();
		return;
	 
		
	}
	
	
	public String wait_new_action(String datafile, int read_times) throws Exception {
		String response = "";
		// TODO Auto-generated method stub
		if(STool.is_datafile_ready(datafile, 60)) {
			
			for(int i=0; i<read_times; i++){
				response = STool.readfile(datafile);
				if(is_new_response(response.split(":"))) {
					
					return response;
				}else
					STool.server_logger.info("WAITING: Wait for new action from:" + datafile);
				
				Thread.sleep(STool.SLEEP_SEC);
			}
			
		}
		return "";
	}
	

	public void receive_client_action() throws Exception{
		if(do_next.equals(STool.IS_FAIL)){
			return;
		}
		String c_response = s_dis.readUTF().trim();
		//String c_response = wait_new_action(client_filename,30);
		if(c_response.equals("")) {
			this.do_next = STool.IS_FAIL;
			STool.server_logger.info("TIMEOUT: NO FOUND new action from client!");
			return;
		}
		
		do_next = c_response.split(":")[1].trim();
		//if(do_next.equals(STool.ACTION_EXIT))
		//	return;
		//Save to client file with new action counter
		if(is_new_response(c_response.split(":")))
			STool.wirte2file(client_filename, c_response);
	 
		return;
		
	}
	
	public boolean do_time_out() throws Exception{
		
		return false;
	}

	public boolean do_game_over() throws Exception{
		
		return false;
	}
	
	
	@Override
	public void run() {
		
		try {

			while(true) {
				
				receive_server_action();
				if(do_next.equals(STool.IS_FAIL) ||
						do_next.equals(STool.ACTION_EXIT))  
					break;
				
				receive_client_action();
				if(do_next.equals(STool.IS_FAIL))  
					break;
				
				
			}
	
		} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
		}
	}
	

}
	
	
	
	
	
	
	


