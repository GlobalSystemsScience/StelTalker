package edu.berkeley.lhs.digitarium;

import java.util.Calendar;
import java.util.TimeZone;

public class HorizonCalculator {

	private static final long millisPerDay = 86400000L;
	private static final long millisPerSiderealDay = 86164091L;
	private static final long janEpoch = getJanEpoch();
	private static final double MARGIN = 15;
	private static final double SIN_MARGIN = Math.sin(MARGIN * Math.PI / 180);
	private double latitudeRadians;
	private double longitudeRadians;
	private double sinDec, cosDec;

	public HorizonCalculator(double latitude, double longitude) {
		latitudeRadians = latitude * Math.PI / 180;
		longitudeRadians = longitude * Math.PI / 180;
		sinDec = Math.sin(latitudeRadians);
		cosDec = Math.cos(latitudeRadians);
	}

	public void setLatLong(double latitude, double longitude) {
		latitudeRadians = latitude * Math.PI / 180;
		longitudeRadians = longitude * Math.PI / 180;
		sinDec = Math.sin(latitudeRadians);
		cosDec = Math.cos(latitudeRadians);
	}

	private double getRadianTime(long time) {
		return (1.5581145465D + 2.0054758187D * (time - janEpoch)
				/ millisPerDay)
				* Math.PI + longitudeRadians;
	}

	private static long getJanEpoch() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(2000, Calendar.JANUARY, 1, 12, 0, 0);
		long time = cal.getTimeInMillis();
		return time + TimeZone.getDefault().getOffset(time);
	}

	public boolean isVisible(long time, double rightAscension,
			double declination) {
		double currentRA = getRadianTime(time);
		double theta = declination * Math.PI / 180;
		double phi = rightAscension * Math.PI / 12;
		return cosDec * Math.cos(theta) * Math.cos(phi - currentRA) + sinDec
				* Math.sin(theta) > SIN_MARGIN;
	}

	void setUpDownTime(Constellation c) {
		double theta = c.dec * Math.PI / 180;
		double phi = c.ra * Math.PI / 12;
		double upDown = cosDec * Math.cos(theta);
		double point = SIN_MARGIN - sinDec * Math.sin(theta) / upDown;
		double acosPoint = -1;
		if (point >= 1) {
			c.timeUp = -1;
			c.timeDown = 1;
		} else if (point <= -1) {
			c.timeUp = 1;
			c.timeDown = -1;
		} else {
			acosPoint = Math.acos(point);
			long t1 = (long) (((phi - acosPoint - longitudeRadians) / Math.PI - 1.5581145465D) / 2.0054758187D * millisPerDay)
					+ janEpoch;
			long t2 = (long) (((phi + acosPoint - longitudeRadians) / Math.PI - 1.5581145465D) / 2.0054758187D * millisPerDay)
					+ janEpoch;
			if (upDown > 0) {
				c.timeUp = t1 % millisPerSiderealDay;
				c.timeDown = t2 % millisPerSiderealDay;
			} else {
				c.timeUp = t1 % millisPerSiderealDay;
				c.timeDown = t2 % millisPerSiderealDay;
			}
		}
	}
}
