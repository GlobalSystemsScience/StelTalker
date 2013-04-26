/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.lhs.digitarium;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * 
 * @author terra
 */
public class MoonCalculator {

	long[][] newMoons = new long[74][2];

	public MoonCalculator() {
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				"moonTable.txt");
		long[][][] yearRiseSetTimes = getRiseSetTimes(2010);
		Scanner scan = new Scanner(is);
		Calendar cal = Calendar.getInstance();
		TimeZone localZone = cal.getTimeZone();
		cal.set(Calendar.YEAR, 2010);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		int i = 0;
		int lastMonth = -1;
		while (scan.hasNext()) {
			int month = parseMonth(scan.next());
			if (month == Calendar.JANUARY && lastMonth == Calendar.DECEMBER) {
				cal.add(Calendar.YEAR, 1);
				yearRiseSetTimes = getRiseSetTimes(cal.get(Calendar.YEAR));
			}
			lastMonth = month;
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, scan.nextInt());
			cal.set(Calendar.HOUR, scan.nextInt());
			cal.set(Calendar.MINUTE, scan.nextInt());
			if (scan.hasNext()) {
				scan.nextLine();
			}
			long time = cal.getTimeInMillis();
			cal.add(Calendar.MILLISECOND, localZone.getOffset(time));
			long[] dayRiseSetTimes = yearRiseSetTimes[cal.get(Calendar.MONTH)][cal
					.get(Calendar.DAY_OF_MONTH) - 1];
			newMoons[i][0] = dayRiseSetTimes[0];
			newMoons[i][1] = dayRiseSetTimes[1];
			if (newMoons[i][0] > cal.getTimeInMillis()) {
				cal.add(Calendar.DATE, -1);
				newMoons[i][1] = yearRiseSetTimes[cal.get(Calendar.MONTH)][cal
						.get(Calendar.DAY_OF_MONTH) - 1][1];
			} else if (newMoons[i][1] < cal.getTimeInMillis()) {
				cal.add(Calendar.DATE, 1);
				newMoons[i][0] = yearRiseSetTimes[cal.get(Calendar.MONTH)][cal
						.get(Calendar.DAY_OF_MONTH) - 1][1];
			}
		}
	}

	private static int parseMonth(String monthName) {
		switch (monthName.charAt(0)) {
		case 'J':
			switch (monthName.charAt(1)) {
			case 'A':
				return Calendar.JANUARY;
			case 'U':
				switch (monthName.charAt(2)) {
				case 'N':
					return Calendar.JUNE;
				case 'L':
					return Calendar.JULY;
				default:
					return -1;
				}
			default:
				return -1;
			}
		case 'F':
			return Calendar.FEBRUARY;
		case 'M':
			switch (monthName.charAt(2)) {
			case 'R':
				return Calendar.MARCH;
			case 'Y':
				return Calendar.MAY;
			default:
				return -1;
			}
		case 'A':
			switch (monthName.charAt(1)) {
			case 'P':
				return Calendar.APRIL;
			case 'U':
				return Calendar.AUGUST;
			default:
				return -1;
			}
		case 'S':
			return Calendar.SEPTEMBER;
		case 'O':
			return Calendar.OCTOBER;
		case 'N':
			return Calendar.NOVEMBER;
		case 'D':
			return Calendar.DECEMBER;
		default:
			return -1;
		}
	}

	public long[] nextNewMoon(long time) {
		int place = Arrays.binarySearch(newMoons, new long[] { time, time },
				new Comparator<long[]>() {

					public int compare(long[] o1, long[] o2) {
						if (o1[0] > o2[0]) {
							return 1;
						} else if (o1[0] < o2[0]) {
							return -1;
						} else {
							return 0;
						}
					}
				});
		if (place < 0) {
			place = -place - 1;
		}
		return new long[] { newMoons[place - 1][1], newMoons[place][0] };
	}

	public static void main(String[] args) {
		MoonCalculator mc = new MoonCalculator();
		Calendar testCal = Calendar.getInstance();
		System.out.println(new Date(
				mc.nextNewMoon(testCal.getTimeInMillis())[0]));
	}

	private long[][][] getRiseSetTimes(int yr) {
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				"riseSet" + (yr - 2000) + ".txt");
		Scanner scan = new Scanner(is);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, yr);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long[][][] result = new long[12][31][2];
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		scan.nextInt();
		for (int day = 1; day <= 31; day++) {
			for (int month = 0; month < 12; month++) {
				if (day <= cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
					int sunrise = scan.nextInt();
					cal.set(Calendar.HOUR, sunrise / 100);
					cal.set(Calendar.MINUTE, sunrise % 100);
					if (cal.getTimeZone().inDaylightTime(cal.getTime())) {
						cal.add(Calendar.HOUR, 1);
					}
					result[month][day - 1][0] = cal.getTimeInMillis();
					int sunset = scan.nextInt();
					cal.set(Calendar.HOUR, sunset / 100);
					cal.set(Calendar.MINUTE, sunset % 100);
					if (cal.getTimeZone().inDaylightTime(cal.getTime())) {
						cal.add(Calendar.HOUR, 1);
					}
					result[month][day - 1][1] = cal.getTimeInMillis();
				}
				cal.roll(Calendar.MONTH, 1);
			}
			cal.roll(Calendar.DAY_OF_MONTH, 1);
		}
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return result;
	}
}
