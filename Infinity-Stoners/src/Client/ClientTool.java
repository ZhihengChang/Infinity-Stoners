package Client;

import java.util.Scanner;

public final class ClientTool {

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
    
    public static String get_input_from_screen(Scanner input, String msg) {
		System.out.println(msg);
		return input.nextLine().trim();
}
    
}
