package tools;

import java.util.*;

public class Time {

	private static final Calendar calendar = GregorianCalendar.getInstance();
	private static final Date d = new Date();

	/**
	 * convenience method. just calls System.currentTimeMillis(). Gets inlined very
	 * fast
	 */
	public static long millis() {
		return System.currentTimeMillis();
	}

	public static int getHour() {
		setC();
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute() {
		setC();
		return calendar.get(Calendar.MINUTE);
	}

	public static int getSecond() {
		setC();
		return calendar.get(Calendar.SECOND);
	}

	public static int getDayOfMonth() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static int getMonth() {
		return calendar.get(Calendar.MONTH);
	}

	public static int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	public static String getDate() {
		return getDayOfMonth() + "." + getMonth() + "." + getYear();
	}

	public static String getTime(boolean hours, boolean minutes, boolean seconds) {
		setC();
		StringBuilder ret = new StringBuilder();
		if (hours) {
			int m = calendar.get(Calendar.HOUR_OF_DAY);
			if (m < 10) {
				ret.append('0');
			}
			ret.append(m);
			if (minutes || seconds)
				ret.append(':');
		}
		if (minutes) {
			int m = calendar.get(Calendar.MINUTE);
			if (m < 10) {
				ret.append('0');
			}
			ret.append(m);
			if (seconds)
				ret.append(':');
		}
		if (seconds) {
			int s = calendar.get(Calendar.SECOND);
			if (s < 10) {
				ret.append('0');
			}
			ret.append(s);
		}
		return ret.toString();
	}

	private static void setC() {
		d.setTime(System.currentTimeMillis());
		calendar.setTime(d);
	}

}
