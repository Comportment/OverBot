package tk.microdroid.overbot;

import java.io.IOException;
import java.util.HashMap;

public class Res {
	
	// <Chan, <Nick, UserObj>>
	static HashMap<String, HashMap<String, User>> users = new HashMap<>();
	static HashMap<String, HashMap<String, Boolean>> privileges = new HashMap<>();
	static HashMap<String, Boolean> svcPolicy = new HashMap<>();
	
	static boolean isSvc(String name) {
		return Res.svcPolicy.containsKey(name) ? Res.svcPolicy.get(name) : true;
	}
	
	static boolean isAllowed(Parser p, boolean strict)
			throws IOException {
		if (p.nick.equals(Core.botNick)) {
			if (!strict)
				return true;
			else
				return isSuperuser(p);
		}
		if (Res.privileges.containsKey(p.nick)) {
			HashMap<String, Boolean> user = Res.privileges.get(p.nick);
			if (user.containsKey(p.cmd)) {
				return user.get(p.cmd);
			} else
				return !strict;
		} else
			return !strict;
	}

	static boolean isSuperuser(Parser p) throws IOException {
		if (p.nick.equals(Core.botSuperuser))
			return IO.isIdentified(p.nick);
		else
			return false;
	}

	static void block(Parser p) {
		if (Res.privileges.containsKey(p.argv[0])) {
			Res.privileges.get(p.argv[0]).put(p.argv[1], false);
		} else {
			HashMap<String, Boolean> cmdsPolicy = new HashMap<>();
			cmdsPolicy.put(p.argv[1], false);
			Res.privileges.put(p.argv[0], cmdsPolicy);
		}
	}

	static void allow(Parser p) {
		if (Res.privileges.containsKey(p.argv[0])) {
			Res.privileges.get(p.argv[0]).put(p.argv[1], true);
		} else {
			HashMap<String, Boolean> cmdsPolicy = new HashMap<>();
			cmdsPolicy.put(p.argv[1], true);
			Res.privileges.put(p.argv[0], cmdsPolicy);
		}
	}

	static void resetPolicy(Parser p) {
		Res.privileges.remove(p.argv[0]);
	}

	static void checkNick(Parser p) throws IOException {
		if (!users.containsKey(p.params[0]))
			users.put(p.params[0], new HashMap<String, User>());
		if (!users.get(p.params[0]).containsKey(p.nick)) {
			User user = new User();
			user.nick = p.nick;
			user.host = p.host;
			user.realname = p.realname;
			++user.messageCount;
			IO.whois(p.nick, user);
			users.get(p.params[0]).put(p.nick, user);
		} else ++users.get(p.params[0]).get(p.nick).messageCount;
	}
}
