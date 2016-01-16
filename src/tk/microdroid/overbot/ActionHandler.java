package tk.microdroid.overbot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActionHandler {
	public static void handle(Parser p) throws IOException {
		if (p.action.equals("PRIVMSG")) {
			if (p.cmd.startsWith("\001")
					&& ((p.argv[0].isEmpty() && p.cmd.endsWith("\001")) || p.argv[p.argv.length]
							.endsWith("\001"))) {
				handleCTCP(p.cmd + p.argv, p.nick);
			} else {
				Res.checkNick(p);
				try {
					CommandHandler.process(p);
				} catch (Exception e) {
					IO.privmsg(p.params[0], "\002Internal error\002");
					e.printStackTrace();
				}
				Services.process(p);
			}
		} else if (p.action.equals("INVITE")) {
			IO.join(p.cmd);
		}
	}

	private static void handleCTCP(String msg, String nick) throws IOException {
		msg = msg.replaceAll("\001", "").toUpperCase();
		if (msg.equals("TIME")) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date now = new Date();
			IO.notice(nick, formatter.format(now));
		} else if (msg.equals("VERSION"))
			IO.notice(nick, "OverBot, by OverCoder, Java based IRC bot");
	}
}
