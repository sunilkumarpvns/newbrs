package com.elitecore.corenetvertex.constants;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public enum ValidityPeriodUnit {

    DAY("DAY", TimeUnit.DAYS),
    HOUR("HOUR", TimeUnit.HOURS),
    MINUTE("MINUTE", TimeUnit.MINUTES),
    MID_NIGHT("MID-NIGHT", TimeUnit.DAYS) {
        @Override
        public long addTime(long initialTimeInMs, int interval) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(initialTimeInMs);
            calendar.add(Calendar.DATE, interval - 1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);

            return calendar.getTimeInMillis();
        }
    };
    private static Map<String, ValidityPeriodUnit> valToUnit = null;

    static {
        valToUnit = new HashMap<>();
        for (ValidityPeriodUnit value : values()) {
            valToUnit.put(value.displayValue, value);
        }
    }

    public static ValidityPeriodUnit fromName(String value) {

        for (ValidityPeriodUnit unit : values()) {
            if (unit.displayValue.equalsIgnoreCase(value)) {

                return unit;
            }
        }
        return null;
    }

    public final String displayValue;
    public final TimeUnit timeUnit;

    ValidityPeriodUnit(String displayValue, TimeUnit timeUnit) {
        this.displayValue = displayValue;
        this.timeUnit = timeUnit;
    }

    public long addTime(long initialTimeInMs, int interval) {
        return initialTimeInMs + timeUnit.toMillis(interval);
    }
}
