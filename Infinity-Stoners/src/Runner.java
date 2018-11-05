

import java.util.Scanner;

public class Runner {

	public Runner() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("Please Enter player 1's name: ");
		String p1_name = input.nextLine();
		System.out.println("Please Enter player 2's name: ");
		String p2_name = input.nextLine();
		Game war = new Game(p1_name, p2_name);
		
		System.out.println("Help:");
		System.out.println("*Enter START to start a (new)game.");
		System.out.println("*Enter QUIT to quit the game.");
		String command = input.nextLine();
		while(falseCommand(command)) {
			System.out.println("->ERRO: Unrecognized Command! Please enter again: ");
			command = input.nextLine();
		}
		while(command.equals("START")) {
			war.start();
		}
		System.out.println(war.toString());

	}
	
	private static boolean falseCommand(String command) {
		if(command.equals("START") || command.equals("QUIT")) {
			return false;
		}
		return true;
	}

}
