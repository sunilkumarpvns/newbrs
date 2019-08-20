package com.elitecore.corenetvertex.constants;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class RenewalIntervalUnitTest {

    private Calendar calendar = Calendar.getInstance();

    private long getFixedDate(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,2018);
        calendar.set(Calendar.MONTH,1);
        calendar.set(Calendar.DAY_OF_MONTH,5);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
    }

    @Test
    public void testsCorrectnessOfHourFor2Hours(){
        Assert.assertEquals(0+2*60*60*1000, RenewalIntervalUnit.HOUR.addTime(0,2));
    }

    @Test
    public void testsCorrectnessOfDayFor2Days(){
        Assert.assertEquals(0+2*24*60*60*1000, RenewalIntervalUnit.DAY.addTime(0,2));
    }

    @Test
    public void testsCorrectnessOfMonthFor2Months() {
        Calendar expected = calendar;
        expected.set(Calendar.YEAR, 2018);
        expected.set(Calendar.MONTH, 3);
        expected.set(Calendar.DAY_OF_MONTH, 5);
        expected.set(Calendar.HOUR_OF_DAY, 0);
        expected.set(Calendar.MINUTE, 0);
        expected.set(Calendar.SECOND, 0);
        expected.set(Calendar.MILLISECOND, 0);
        Assert.assertEquals(expected.getTimeInMillis(), RenewalIntervalUnit.MONTH.addTime(getFixedDate(), 2));
    }

    @Test
    public void testsCorrectnessOfTillBillDate() {
        Calendar expected = calendar;
        expected.set(Calendar.YEAR, 2018);
        expected.set(Calendar.MONTH, 2);
        expected.set(Calendar.DAY_OF_MONTH, 5);
        expected.set(Calendar.HOUR_OF_DAY, 0);
        expected.set(Calendar.MINUTE, 0);
        expected.set(Calendar.SECOND, 0);
        expected.set(Calendar.MILLISECOND, 0);
        Assert.assertEquals(expected.getTimeInMillis(), RenewalIntervalUnit.TILL_BILL_DATE.addTime(getFixedDate(), 1));
    }

    @Test
    public void testsCorrectnessOfMidNightFor2ndDayFromNow(){

        Calendar expected = calendar;
        expected = calendar;
        expected.set(Calendar.YEAR, 2018);
        expected.set(Calendar.MONTH, 1);
        expected.set(Calendar.DAY_OF_MONTH, 6);
        expected.set(Calendar.HOUR_OF_DAY, 23);
        expected.set(Calendar.MINUTE, 59);
        expected.set(Calendar.SECOND, 59);
        expected.set(Calendar.MILLISECOND, 999);
        Assert.assertEquals(expected.getTimeInMillis(), RenewalIntervalUnit.MID_NIGHT.addTime(getFixedDate(), 2));
    }

    @Test
    public void testsCorrectnessOfMonthEndFor2ndMonthFromNow(){
        Calendar expected = calendar;
        expected.set(Calendar.YEAR,2018);
        expected.set(Calendar.MONTH,3);
        expected.set(Calendar.DAY_OF_MONTH,1);
        expected.set(Calendar.HOUR_OF_DAY,0);
        expected.set(Calendar.MINUTE,0);
        expected.set(Calendar.SECOND,0);
        expected.set(Calendar.MILLISECOND,0);
        Assert.assertEquals(expected.getTimeInMillis()-1, RenewalIntervalUnit.MONTH_END.addTime(getFixedDate(),2));
    }

    @Test
    public void testsThatFromRenewalIntervalUnitMethodWorks(){
        Assert.assertEquals(RenewalIntervalUnit.DAY, RenewalIntervalUnit.fromRenewalIntervalUnit("DAY"));
    }
}
