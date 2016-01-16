package tk.microdroid.overbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

public class IO {
	static BufferedWriter writer;
	static BufferedReader reader;
	static String line;

	public static void init(Socket socket) throws IOException {
		writer = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream(), StandardCharsets.UTF_8));
		reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
	}

	public static String read() throws IOException {
		line = reader.readLine();
		if (line != null) {
			line.replaceAll("\007", "");
			System.out.println(line);
		}
		return line;
	}

	public static void register() throws IOException {
		writer.write("PONG " + reader.readLine().substring(5) + "\r\n");
		writer.write("NICK " + Core.botNick + "\r\n");
		writer.write("USER " + Core.botUsername + " 0 * :" + Core.botRealname
				+ "\r\n");
		writer.flush();
	}

	public static void admin(String server) throws IOException {
		writer.write("ADMIN " + server + "\r\n");
		writer.flush();
	}

	public static void away(String message) throws IOException {
		writer.write("AWAY " + message + "\r\n");
		writer.flush();
	}

	public static void invite(String nick, String channel) throws IOException {
		writer.write("INVITE " + nick + " " + channel + "\r\n");
		writer.flush();
	}

	public static void join(String channel) throws IOException {
		writer.write("JOIN " + channel + "\r\n");
		writer.flush();
	}

	public static void kick(String channel, String client, String message)
			throws IOException {
		if (message.isEmpty())
			writer.write("KICK " + channel + " " + client + "\r\n");
		else
			writer.write("KICK " + channel + " " + client + " " + message
					+ "\r\n");
		writer.flush();
	}

	public static void mode(String target, String flags) throws IOException {
		writer.write("MODE " + target + " " + flags + "\r\n");
		writer.flush();
	}

	public static void motd(String server) throws IOException {
		if (server.isEmpty())
			writer.write("MOTD\r\n");
		else
			writer.write("MOTD " + server + "\r\n");
		writer.flush();
	}

	public static void names(String params) throws IOException {
		writer.write("NAMES" + params + "\r\n");
		writer.flush();
	}

	public static void nick(String nick) throws IOException {
		writer.write("NICK " + nick + "\r\n");
		writer.flush();
	}

	public static void notice(String target, String message) throws IOException {
		writer.write("NOTICE " + target + " :" + message + "\r\n");
		writer.flush();
	}

	public static void pass(String password) throws IOException {
		writer.write("PASS " + password + "\r\n");
		writer.flush();
	}

	public static void ping(String server) throws IOException {
		writer.write("PING " + server + "\r\n");
		writer.flush();
	}

	public static void pong() throws IOException {
		writer.write("PONG " + line.substring(5) + "\r\n");
		writer.flush();
	}

	public static void privmsg(String target, String message)
			throws IOException {
		writer.write("PRIVMSG " + target + " :" + Tools.getSafe(message) + "\r\n");
		writer.flush();
	}

	public static void quit(String message) throws IOException {
		writer.write("QUIT " + message + "\r\n");
		writer.flush();
	}

	public static void time(String server) throws IOException {
		if (server.isEmpty())
			writer.write("TIME\r\n");
		else
			writer.write("TIME " + server + "\r\n");
		writer.flush();
	}

	public static void topic(String channel, String topic) throws IOException {
		if (topic.isEmpty())
			writer.write("TOPIC " + channel + "\r\n");
		else
			writer.write("TOPIC " + channel + " " + topic + "\r\n");
		writer.flush();
	}

	public static void users(String server) throws IOException {
		if (server.isEmpty())
			writer.write("USERS\r\n");
		else
			writer.write("USERS :" + server + "\r\n");
		writer.flush();
	}

	public static void who(String nick) throws IOException {
		if (nick.isEmpty())
			writer.write("WHO\r\n");
		else
			writer.write("WHO " + nick + "\r\n");
		writer.flush();
	}

	public static User whois(String nick, User user) throws IOException {
		IO.raw("WHOIS " + nick);
		String line = "";
		while (!(line = reader.readLine()).split(" ")[1].matches("(318|431|461)")) {
			System.out.println(line);
			String[] splitter = line.split(" ");
			if (splitter[1].equals("307"))
				user.isIdentified = true;
			else if (splitter[1].equals("319"))
				for (String channel : line.split(" :")[1].split(" "))
					if (!user.chans.contains(channel))
						user.chans.add(channel.replaceAll("[\\~\\&]", ""));
					else if (splitter[1].equals("335"))
						user.isBot = true;
					else if (splitter[1].equals("671"))
						user.isSSL = true;
			if (splitter[1].equals("330"))
				user.login = splitter[4];
			else Core.queue.add(line);
		}
		return user;
	}

	public static void whowas(String nick, String params) throws IOException {
		if (params.isEmpty())
			writer.write("WHOWAS " + nick + "\r\n");
		else
			writer.write("WHOWAS " + nick + " " + params + "\r\n");
		writer.flush();
	}

	public static void raw(String raw) throws IOException {
		writer.write(raw + "\r\n");
		writer.flush();
	}

	public static void identify(String password) throws IOException {
		Core.isConnected = true;
		writer.write("MODE " + Core.botNick + " +B\r\n");
		writer.write("MSG NickServ :IDENTIFY " + password + "\r\n");
		writer.flush();
	}

	public static void ghost(String nick, String password) throws IOException {
		privmsg("NickServ", "GHOST" + nick + " " + password + "\r\n");
	}

	public static boolean handleServer() throws IOException {
		boolean isServerMessage = false;
		String[] splitter = line.split(" ");
		if (StringUtils.isNumeric(splitter[1])) {
			isServerMessage = true;
			if (splitter[1].equals("001") && !Core.isConnected) {
				Core.isConnected = true;
				identify(Tools.getPrivatePreference("nickserv"));
			} else if (splitter[1].equals("433")) {
				nick(Tools.generateRandomNick());
				register();
				ghost(Core.botNick, Tools.getPrivatePreference("nickserv"));
				nick(Core.botNick);
			}
		} else if (line.startsWith("PING")) {
			isServerMessage = true;
			pong();
		}
		return isServerMessage;
	}
	
	public static boolean isIdentified(String nick) throws IOException {
		privmsg("NickServ", "ACC " + Core.botSuperuser);
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			if (line.startsWith(":NickServ!NickServ")) {
				String[] splitter = line.split(" ");
				if (splitter[5].equals("3"))
					return true;
				else
					return false;
			}
			else Core.queue.add(line);
		}
		return false;
	}
}
