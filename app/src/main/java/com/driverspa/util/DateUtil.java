package com.driverspa.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

	/**
	 * Difference in years between two dates. Can be used to calculate age 
	 * @param date - date to compare to
	 * @return
	 */
	public static int yearsSince(Date date) {
		Date now = new Date();
		Calendar cDate = Calendar.getInstance();
		cDate.setTime(date);
		Calendar cNow = Calendar.getInstance();
		cNow.setTime(now);
		
		int result = cNow.get(Calendar.YEAR) - cDate.get(Calendar.YEAR);
		return result;
	}
	
	public static String getTime(Date date) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
		return timeFormat.format(date);
	}
	
}
