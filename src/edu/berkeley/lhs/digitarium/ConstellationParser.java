package edu.berkeley.lhs.digitarium;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class ConstellationParser {

	public final InputStream names;
	public HashMap<String, Constellation> nameMap = new HashMap<String, Constellation>();

	public ConstellationParser(String culture) {
		names = getClass().getClassLoader().getResourceAsStream(
				culture + "_constellation_names.eng.fab");
		Scanner scan = new Scanner(names).useDelimiter("\n|\t+");
		System.out.println(culture);
		while (scan.hasNext()) {
			String abbrev = scan.next();
			String name = scan.next();
			if (culture.equals("western")) {
				nameMap.put(name, new Constellation(name, abbrev, scan
						.nextInt()
						+ scan.nextDouble() / 60, scan.nextInt()
						+ scan.nextDouble() / 60));
			} else {
				nameMap.put(name, new Constellation(name, abbrev));
			}
			scan.nextLine();
		}
	}

	public String getAbbrev(String n) {
		return nameMap.get(n).abbrev;
	}
}

class Constellation {

	final String name;
	final String abbrev;
	final double ra;
	final double dec;
	final boolean hasLocation;
	long timeUp, timeDown;

	Constellation(String name, String abbrev, double ra, double dec) {
		this.name = name;
		this.abbrev = abbrev;
		this.ra = ra;
		this.dec = dec;
		hasLocation = true;
	}

	Constellation(String name, String abbrev) {
		hasLocation = false;
		this.name = name;
		this.abbrev = abbrev;
		ra = 0;
		dec = 0;
	}

	boolean toggleBetween(long time1, long time2) {
		long smallerTime, largerTime;
		if (timeUp < timeDown) {
			smallerTime = timeUp;
			largerTime = timeDown;
		} else {
			smallerTime = timeDown;
			largerTime = timeUp;
		}
		return ((time1 < smallerTime || time1 >= largerTime)
				&& time2 >= smallerTime && time2 < largerTime)
				|| ((time2 < smallerTime || time2 >= largerTime)
						&& time1 >= smallerTime && time1 < largerTime);
	}

	@Override
	public String toString() {
		return name;
	}
}
