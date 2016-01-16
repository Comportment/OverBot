package tk.microdroid.overbot;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

public class CommandHandler {

	public static void process(Parser p) throws Exception {
		if (!Res.isAllowed(p, false))
			return;
		String prefix = getPrefix();
		String chan = p.params[0];
		if (p.cmd.equals("%raw") && Res.isSuperuser(p)) {
			IO.raw(p._argv);
		} else if (p.cmd.equals("%block") && p.argc >= 2 && Res.isSuperuser(p)) {
			Res.block(p);
			IO.notice(p.nick, getPrefix(4) + p.argv[0]
					+ " has been blocked from using " + p.argv[1]);
		} else if (p.cmd.equals("%allow") && p.argc >= 2 && Res.isSuperuser(p)) {
			Res.allow(p);
			IO.notice(p.nick, getPrefix(3) + p.argv[0]
					+ " has been allowed to use " + p.argv[1]);
		} else if (p.cmd.equals("%resetpolicy") && Res.isSuperuser(p) && p.withArgs) {
			Res.resetPolicy(p);
			IO.notice(p.nick, getPrefix(7) + p.argv[0]
					+ "'s privileges has been reset");
		} else if (p.cmd.equals("%svc") && Res.isSuperuser(p) && p.argc >= 2) {
			if (p.argv[0].equals("start")) {
				Res.svcPolicy.put(p.argv[1], true);
				IO.notice(p.nick, getPrefix(3) + p.argv[1]
						+ " turned ON");
			}
			else if (p.argv[0].equals("stop")) {
				Res.svcPolicy.put(p.argv[1], false);
				IO.notice(p.nick, getPrefix(7) + p.argv[0]
						+ " turned OFF");
			}
		} else if (p.cmd.equals("%help")) {
			if (!p.withArgs)
				IO.notice(p.nick, "%help <command> for more info: %palette %crements %math");
			else
				IO.notice(p.nick, getHelp(p.argv[0]));
		} else if (p.cmd.equals("%join") && p.argc >= 1 && Res.isAllowed(p, false)) {
			IO.join(p.argv[0]);
		}
		// //
		else if (p.cmd.equals("%palette")) {
			IO.notice(chan, getIRCPalette());
		} else if (p.cmd.equals("%crements") && p.argc >= 1) {
			if (!Res.users.get(chan).containsKey(p.argv[0])) {
				IO.notice(chan, prefix + "User doesn't not exist");
				return;
			}
			User user = Res.users.get(chan).get(p.argv[0]);
			IO.notice(chan, String.format("%s: %d--'s %d++'s",
					p.argv[0], user.decrementCount, user.incrementCount));
		} else if (p.cmd.equals("%math") && p.argc >= 1) {
			Math.solve(p._argv);
		} else if (p.cmd.equals("%asm") && p.argc >= 1) {
			final String asmPath = System.getProperty("user.dir") + "\\asm\\";
			FileOutputStream fos = new FileOutputStream(asmPath + "assembly.asm");
			fos.write(("%include \"" + asmPath + "lib\\asm_io.inc\"\n"
					+ "segment .text\n"
					+ "global _asm_main\n"
					+ "_asm_main:\n"
					+ "enter 0,0\n"
					+ "pusha\n" 
					+ p._argv
					+ "\npopa\n"
					+ "mov eax, 0\n"
					+ "leave\n"
					+ "ret\n").replaceAll(";", "\n")
					.getBytes(Charset.forName("UTF-8")));
			fos.flush();
			fos.close();
			Process process = Runtime.getRuntime().exec(asmPath + "compile_asm.bat");
			process.waitFor(4, TimeUnit.SECONDS);
			if (process.exitValue() != 0) {
				IO.notice(chan, prefix + " \002\00304Error: \003" + IOUtils.toString(process.getErrorStream()) + "\002");
				return;
			}
			process = Runtime.getRuntime().exec(asmPath + "assembly.exe");
			process.waitFor(1, TimeUnit.SECONDS);
			IO.notice(chan, prefix + " \002Exit code:\002 " + process.exitValue() + ", \002Result:\002 " 
					+ IOUtils.toString(process.getInputStream()));
		}
	}

	private static String getPrefix() {
		return getPrefix(new Random().nextInt(14));
	}

	private static String getPrefix(int color) {
		return "\002\003" + color + "% \003\002";
	}

	private static String getIRCPalette() {
		StringBuilder palette = new StringBuilder();
		for (int i = 0; i < 35; i++) {
			String x = "" + i;
			if (x.length() < 2)
				x = "0" + x;
			palette.append("\003" + x + "," + x + "  \003" + " " + i + " ");
		}
		return palette.toString();
	}


	private static String getHelp(String cmd) {
		switch (cmd) {
		case "%palette":
			return "\002%palette\002 : Print IRC color palette";
		case "%crements":
			return "\002%crements\002 <user> : Shows how many ++'s and --'s are done to a user";
		case "%help":
			return "\002%help\002 : OverBot hates you";
		case "%math":
			return "\002%math\002 <expression> : The bot tries to solve whatever you've specified"
					+ ", it doesn't work in all cases, try to standardize stuff, it might work";
		case "%gath":
			return "\002%asm\002 <code> : Run assembly code -- uses NASM syntax";
		default:
			return "Google it";
		}
	}
}
