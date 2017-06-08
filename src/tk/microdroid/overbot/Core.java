package tk.microdroid.overbot;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Core {
	String botServer = "";
	int botPort = 6667;
	String botNick = "";
	String botRealname = "";
	String botUsername = "";
	String botSuperuser = "";

	static boolean isConnected = false;
	static List<String> queue = new ArrayList<String>();

	public Core(String botServer, String botNick, String botRealname, String botUsername, String botSuperuser) {
		this.botServer = botServer;
		this.botNick = botnick;
		this.botRealname = botRealname;
		this.botUsername = botUsername;
		this.botSuperuser = botSuperuser;
	}
	
	public void connect() throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					IO.raw("QUIT Most likely crashed");
				} catch (IOException e) {
					// Nothing
				}
			}
		}));
		Socket socket = new Socket(botServer, botPort);
		IO.init(socket);
		IO.read();
		IO.register();
		while (!socket.isClosed()) {
			try {
				if (!queue.isEmpty()) {
					IO.line = queue.get(0);
					queue.remove(0);
				} else if (IO.read() == null)
					return;
				if (IO.handleServer()) {
					continue;
				}
				if (isConnected) {
					Parser p = new Parser(IO.line);
					if (p.empty)
						continue;
					ActionHandler.handle(p);
				}
			} catch (SocketException e) {
				System.err.println(e.getMessage());
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
