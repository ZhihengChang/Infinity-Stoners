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

import game.*;
public class ClientController extends Thread {

	private Socket socket; 
	private String id;
	DataInputStream s_dis; 
    DataOutputStream s_dos; 
    private int turn_counter;
    
   // private File client_file;
    private String client_filename;
    private String server_filename;
    private String s_response;
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
	public ClientController(Socket socket, DataInputStream dis, DataOutputStream dos, String client_id)throws Exception {
		this.socket = socket;
		//this.id = id;
		this.s_dis = dis;
		this.s_dos = dos;
		
		this.client_filename = CommTool.FILE_PERFIX_CLIENT + client_id + ".txt";
		this.server_filename = CommTool.FILE_PERFIX_SERVER + client_id +".txt";
		this.turn_counter = 0;
		CommTool.wirte2file(client_filename, "ACTION_INIT:Initialized");
		
	}
	

	public void do_start() throws Exception{
		if(!CommTool.is_data_ready(server_filename,3)) {
			System.out.println("ERROR:No found data file");
			return;
		}
		
		//Get response from server
		s_response = CommTool.readfile(server_filename);
		Thread.sleep(2000);
		System.out.println(server_filename + ": " + s_response);
	
		
		if(s_response.equals(CommTool.ACTION_START)) {
			System.out.println("1: start");
			System.out.println("player:start!");
			s_dos.writeUTF(CommTool.ACTION_START);
			s_dos.flush();
			String player_name = s_dis.readUTF();
			CommTool.wirte2file(client_filename, CommTool.PLAYER + player_name);
		}
	}
	//Client => thread: Exit, NewT
	public void do_ready() throws Exception{
		if(!CommTool.is_data_ready(server_filename,3)) {
			System.out.println("ERROR:No found data file");
			return;
		}
		
		//Get response from server
		s_response = CommTool.readfile(server_filename);
		Thread.sleep(2000);
		System.out.println(server_filename + ": " + s_response);
		
		if(s_response.equals(CommTool.ACTION_READY)) {
			System.out.println("2: ready");
			s_dos.writeUTF(CommTool.ACTION_READY);
			s_dos.flush();
			String user_response = s_dis.readUTF();
			if(user_response.equals(CommTool.ACTION_NEWT)) {
				turn_counter++;
				user_response += ":" + turn_counter;
			}		
			CommTool.wirte2file(client_filename, user_response );				
		}
		
	}

	public void do_show_card() throws Exception{
		if(!CommTool.is_data_ready(server_filename,3)) {
			System.out.println("ERROR:No found data file");
			return;
		}
		
		//Get response from server
		s_response = CommTool.readfile(server_filename);
		Thread.sleep(2000);
		System.out.println(server_filename + ": " + s_response);
	
		if(s_response.startsWith(CommTool.ACTION_SHOWC)) {
			System.out.println("4: showCard");
			s_dos.writeUTF(s_response);
			s_dos.flush();
		}
	}
	public void do_result() throws Exception{
		if(!CommTool.is_data_ready(server_filename,3)) {
			System.out.println("ERROR:No found data file");
			return;
		}
		
		//Get response from server
		s_response = CommTool.readfile(server_filename);
		Thread.sleep(2000);
		System.out.println(server_filename + ": " + s_response);	
		
		
		
		//else if(s_response.substring(0, CommTool.RESULT_WIN.length()).equals(CommTool.RESULT_WIN)) {
		if(s_response.startsWith(CommTool.RESULT_WIN) || s_response.startsWith(CommTool.RESULT_LOST) ||
			s_response.startsWith(CommTool.RESULT_WAR)) {
			s_dos.writeUTF(s_response);
			s_dos.flush();
		
		}

		
	}
	public boolean do_time_out() throws Exception{
		if(!CommTool.is_data_ready(server_filename,3)) {
			System.out.println("ERROR:No found data file");
			return false;
		}
		
		//Get response from server
		s_response = CommTool.readfile(server_filename);
		Thread.sleep(2000);
		System.out.println(server_filename + ": " + s_response);	
	
		if(s_response.startsWith(CommTool.GAME_TIMEOUT)) {
			s_dos.writeUTF(s_response);
			s_dos.flush();
			return true;
		}
		return false;
	}

	public boolean do_game_over() throws Exception{
		if(!CommTool.is_data_ready(server_filename,3)) {
			System.out.println("ERROR:No found data file");
			return false;
		}
		
		//Get response from server
		s_response = CommTool.readfile(server_filename);
		Thread.sleep(2000);
		System.out.println(server_filename + ": " + s_response);	
	
		if(s_response.startsWith(CommTool.GAME_END)){
			System.out.println("8: end");
			s_dos.writeUTF(s_response + ":" + turn_counter);
			s_dos.flush();
			turn_counter = 0;
			String user_response = s_dis.readUTF();
			if(user_response.startsWith(CommTool.ACTION_NEWG)) {
				CommTool.wirte2file(client_filename, CommTool.ACTION_NEWG);
			}else {
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public void run() {
		
		try {

			while(true) {
				
				do_start();
				
				do_ready(); // -> server: new turn
				
				do_show_card();
				
				do_result();
				
				if(do_time_out())
					break;
				
				if(do_game_over())
					break;

				
			}
	
		} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
		}
	}
	

}
	
	
	
	
	
	
	


