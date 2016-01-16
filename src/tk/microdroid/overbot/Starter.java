package tk.microdroid.overbot;

public class Starter {
	public static void main(String[] args) throws Exception {
		Core.botServer = "irc.subluminal.net";
		Core.botPort = 6667;
		Core.botNick = "OverBot";
		Core.botUsername = "OverBot";
		Core.botRealname = "OverCoder";
		Core.botSuperuser = "OverCoder";
		Core core = new Core();
		core.connect();
	}
}
