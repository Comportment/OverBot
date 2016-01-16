package tk.microdroid.overbot;


public class Parser {
	String nick = "";
	String realname = "";
	String host = "";
	String action = "";
	String[] params; // These are the ones who come after IRC protocol command
					 // params[0] resolves to channel in PRIVMSGs
	String cmd = "";
	String[] argv; // These are the ones who come after bot command
	
	String _argv; // Arguments in one String
	String _msg; // Command + Arguments
	
	int argc;
	
	boolean empty = false;
	boolean withArgs = false;

	public Parser(String line) {
		try {
			clearValues();
			if (!line.startsWith(":")) {
				clearValues();
				return;
			}
			line = line.substring(1);
			String[] splitter = line.split(" :", 2);
			String[] splitter2 = splitter[1].split(" ", 2);
			String[] splitter3 = splitter[0].split(" ", 3);
			String[] splitter4 = splitter3[0].split("(\\!|\\@|\\.)");
			params = splitter3[2].split(" ");

			nick = splitter4[0];
			realname = splitter4[1];
			host = splitter4[2];
			action = splitter3[1];
			if (splitter2.length == 2) {
				cmd = splitter2[0].toLowerCase();
				if ((argv = splitter2[1].split(" ")).length <= 1) {
					argv = new String[1];
					argv[0] = splitter2[1];
				}
				_argv = splitter2[1];
			} else {
				cmd = splitter[1];
			}
			if (argv == null) {
				argv = new String[1];
				argv[0] = "";
				_argv = "";
			} else {
				withArgs = true;
				argc = argv.length;
			}
			if (params == null) {
				params = new String[1];
				params[0] = "";
			}
			_msg = cmd + _argv;
			empty = false;
		} catch (Exception e) {
			clearValues();
		}
	}

	private void clearValues() {
		nick = realname = host = action = cmd = "";
		argv = params = null;
		empty = withArgs = false;
		argc = 0;
	}
}
