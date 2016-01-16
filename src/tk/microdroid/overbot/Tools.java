package tk.microdroid.overbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.validator.routines.UrlValidator;

public class Tools {
	static HashMap<String, String> cachedPrefs = new HashMap<>();

	public static String getPrivatePreference(String key) throws IOException {
		if (cachedPrefs.containsKey(key))
			return cachedPrefs.get(key);
		File file = new File(System.getProperty("user.dir") + File.separator
				+ "config.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] splitter = line.split(";;", 2);
			if (splitter[0].equals(key)) {
				reader.close();
				cachedPrefs.put(key, splitter[1]);
				return splitter[1];
			}
		}
		reader.close();
		throw new IllegalStateException("Preference key not found!");
	}

	public static String generateRandomNick() {
		StringBuilder nickname = new StringBuilder();
		char[] alpha = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		Random random = new Random();
		for (int i = 0; i < 11; i++)
			nickname.append(alpha[random.nextInt(26)]);
		return nickname.toString();
	}

	/*public static String uploadToImgur(byte[] bytes) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(
					"https://api.imgur.com/3/image").openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Authorization",
					"Client-ID be41da645ee419d");

			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(URLEncoder.encode("image", "UTF-8") + "="
					+ URLEncoder.encode(Base64.encode(bytes), "UTF-8"));
			writer.flush();
			writer.close();
			BufferedReader input = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			JSONObject json = new JSONObject(IOUtils.toString(input));
			connection.disconnect();

			return json.getJSONObject("data").getString("link");
		} catch (Exception e) {
			return e.getMessage();
		}
	}*/

	public static String containsURL(String line) {
		UrlValidator validator = new UrlValidator();
		String[] splitter = line.split(" ");
		if (splitter.length < 2) {
			if (validator.isValid(line))
				return line;
			else
				return "";
		}
		for (String part : splitter) {
			if (validator.isValid(part))
				return part;
		}
		return "";
	}

	public static String getSafe(String str) {
		String newStr = "";
		if (str.length() > 420)
			newStr = str.substring(0, 420);
		else
			newStr = str;
		return newStr.replaceAll("(\n|\r)", ";");
	}

	public static String multiplyString(String str, int count) {
		return new String(new char[count]).replaceAll("\0", str);
	}

}
