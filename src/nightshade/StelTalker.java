package nightshade;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class StelTalker implements Runnable {

	Scanner scan;
	Socket s;
	PrintStream sockOut;
	InputStream is;

	public static void main(String[] args) throws IOException {
		StelTalker st = new StelTalker(args[0], System.in);
		st.run();
	}

	public StelTalker(String host, InputStream is) throws IOException {
		scan = new Scanner(is);
		s = new Socket(host, 51283);
		sockOut = new PrintStream(s.getOutputStream());
		this.is = s.getInputStream();
	}

	public void run() {
		new Thread() {

			@Override
			public void run() {
				Scanner scan = new Scanner(is);
				while (scan.hasNext()) {
					System.out.println(scan.nextLine());
				}
			}
		}.start();
		String line = scan.nextLine();
		while (!line.equals("end")) {
			sockOut.println(line);
			line = scan.nextLine();
		}
		sockOut.close();
		scan.close();
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
