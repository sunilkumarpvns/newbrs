package com.elitecore.corenetvertex.constants;

import java.util.Calendar;

public enum  RenewalIntervalUnit {
    MONTH("MONTH"){
        @Override
        public long addTime(long initialTimeInMs, int interval) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(initialTimeInMs);
            calendar.add(Calendar.MONTH, interval);

            return calendar.getTimeInMillis();
        }
    },
    MONTH_END("MONTH-END"){
        @Override
        public long addTime(long initialTimeInMs, int interval) {

            if(interval==0){
                return initialTimeInMs;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(initialTimeInMs);
            calendar.add(Calendar.MONTH, interval-1);

            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            calendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);

            return calendar.getTimeInMillis();
        }
    },
    TILL_BILL_DATE("TILL-BILL DATE"){
        @Override
        public long addTime(long initialTimeInMs, int interval) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(initialTimeInMs);
            calendar.add(Calendar.MONTH, 1);

            return calendar.getTimeInMillis();
        }
    },
    DAY("DAY"){
        @Override
        public long addTime(long initialTimeInMs, int interval){
            return initialTimeInMs + java.util.concurrent.TimeUnit.DAYS.toMillis(interval);
        }
    },
    HOUR("HOUR"){
        @Override
        public long addTime(long initialTimeInMs, int interval){
            return initialTimeInMs + java.util.concurrent.TimeUnit.HOURS.toMillis(interval);
        }
    },
    MID_NIGHT("MID-NIGHT") {
        @Override
        public long addTime(long initialTimeInMs, int interval) {
            return ValidityPeriodUnit.MID_NIGHT.addTime(initialTimeInMs,interval);
        }
    };

    private static String names = "";
    static {
        StringBuilder nameBuilder = new StringBuilder();
        for (RenewalIntervalUnit renewalIntervalUnit: values()) {
            nameBuilder.append(renewalIntervalUnit.name()+", ");
        }
        if(nameBuilder.length()>2){
            names = nameBuilder.toString().substring(0,nameBuilder.length()-2);
        }
    }

    public final String displayValue;
    RenewalIntervalUnit(String displayValue){
        this.displayValue = displayValue;
    }

    public abstract long addTime(long initialTimeInMs, int interval);
    public static RenewalIntervalUnit fromRenewalIntervalUnit(String value) {
        for(RenewalIntervalUnit unit : RenewalIntervalUnit.values()){
            if(unit.name().equals(value)){
                return unit;
            }
        }
        return null;
    }
    public static String getAllNames() {
        return names;
    }

    public String value() {
        return displayValue;
    }
}
