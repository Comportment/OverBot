package tk.microdroid.overbot;

import java.util.ArrayList;
import java.util.List;

public class User {
	String nick = "";
	String realname = "";
	String host = "";
	String login = "";
	List<String> chans = new ArrayList<String>();
	boolean isBot;
	boolean isIdentified;
	boolean isSSL;
	long messageCount = 0;
	long decrementCount = 0;
	long incrementCount = 0;
}
