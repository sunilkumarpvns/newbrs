package com.elitecore.nvsmx.sm.controller.systemparameter;

import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.base.Collectionz;

public class TimezoneFormate {

	private static final String SPACE = " ";

	public static List<String> getTimeZone() {

		List<String> timezoneList = Collectionz.newArrayList();
		String[] ids = TimeZone.getAvailableIDs();
		for (String id : ids) {
			timezoneList.add(calculateTimeZone(TimeZone.getTimeZone(id)));
		}
		return timezoneList;
	}

	private static String calculateTimeZone(TimeZone tz) {

		String timeZone;
		long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
		long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);

		// avoid -4:-30 issue
		minutes = Math.abs(minutes);

		String result = "";
		if (hours > 0) {
			result = String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID());
			timeZone =formateTimezone(result);
		} else {
			result = String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
			timeZone = formateTimezone(result);
		}
		return timeZone;
	}

	private static String formateTimezone(String result){

		String timeZoneFormate = "";
		String[] split = result.split(SPACE);
		timeZoneFormate = split[1] + SPACE +split[0].trim();
		return timeZoneFormate;
	}
}
