package nightshade;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Scanner;
import java.net.Authenticator;

public class RealTalker implements Runnable {

	Scanner scan;
	String command;

	public static void main(String[] args) throws IOException {
		RealTalker st = new RealTalker(args[0], System.in);
		st.run();
	}

	public RealTalker(String host, InputStream is) throws IOException {
		scan = new Scanner(is);
		command = "http://" + host + "/nightshade/command/";
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("nsuser", "1111"
						.toCharArray());
			}
		});
	}

	public void run() {
		String line = scan.nextLine();
		HttpURLConnection huc = null;
		while (!line.equals("end")) {
			try {
				huc = (HttpURLConnection) new URL(command
						+ line.replace(" ", "%20")).openConnection();
				huc.connect();
				InputStream is = huc.getInputStream();
				Scanner scan = new Scanner(is);
				while (scan.hasNext())
					System.out.println(scan.nextLine());
				scan.close();
				huc.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			line = scan.nextLine();
		}
		scan.close();
	}
}