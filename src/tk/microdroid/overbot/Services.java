package tk.microdroid.overbot;

public class Services {
	public static void process(Parser p) {
		if (Res.isSvc("SVC_CREMENTER")) svcCrementer(p);
	}
	
	private static void svcCrementer(Parser p) {
		String[] words = p._msg.split(" ");
		for (String word : words) {
			if (word.endsWith("++")) {
				String nick = word.substring(0, word.length() - 2);
				if (Res.users.get(p.params[0]).containsKey(nick))
					++Res.users.get(p.params[0]).get(nick).incrementCount;
			} else if (word.endsWith("--")) {
				String nick = word.substring(0, word.length() - 2);
				if (Res.users.get(p.params[0]).containsKey(nick))
					++Res.users.get(p.params[0]).get(nick).decrementCount;
			}
		}
	}
}
