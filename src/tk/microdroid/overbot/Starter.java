package tk.microdroid.overbot;

public class Starter {
	public static void main(String[] args) throws Exception {
		new Starter().main();
	}
	
	public void main() {
		Core core = new Core("irc.subluminal.net", "OverBot", "OverCoder", "OverCoder");
		core.connect();
	}
}
