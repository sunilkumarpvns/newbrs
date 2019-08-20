package com.elitecore.corenetvertex.util;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.util.RenewalIntervalUtility;
import com.elitecore.corenetvertex.spr.util.ResetTimeUtility;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.Calendar;

@RunWith(HierarchicalContextRunner.class)
public class RenewalIntervalUtilityTest {

    private static Calendar calendar = Calendar.getInstance();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    private long getFixedDate(){
        return getFixedDateCalendar().getTimeInMillis();
    }

    private Calendar getFixedDateCalendar(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,2018);
        calendar.set(Calendar.MONTH,1);
        calendar.set(Calendar.DAY_OF_MONTH,5);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }


    public class ExpiryDate{
        @Test
        public void calculateExpiryBasedOnHour() throws OperationFailedException {
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(2,RenewalIntervalUnit.HOUR,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(2, RenewalIntervalUnit.HOUR,resetTime,startTime,null);
            Calendar expected = calendar;
            expected.set(Calendar.YEAR, 2018);
            expected.set(Calendar.MONTH, 1);
            expected.set(Calendar.DAY_OF_MONTH, 5);
            expected.set(Calendar.HOUR_OF_DAY, 2);
            expected.set(Calendar.MINUTE, 0);
            expected.set(Calendar.MILLISECOND,0);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);


        }


        @Test
        public void calculateExpiryBasedOnMonth() throws OperationFailedException {
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(1,RenewalIntervalUnit.MONTH,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(1, RenewalIntervalUnit.MONTH,resetTime,startTime,null);
            Calendar expected = calendar;
            expected.set(Calendar.YEAR, 2018);
            expected.set(Calendar.MONTH, 2);
            expected.set(Calendar.DAY_OF_MONTH, 5);
            expected.set(Calendar.HOUR_OF_DAY, 0);
            expected.set(Calendar.MINUTE, 0);
            expected.set(Calendar.MILLISECOND, 0);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);


        }

        @Test
        public void calculateExpiryBasedOnMonthEnd() throws OperationFailedException {
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(1,RenewalIntervalUnit.MONTH_END,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(1, RenewalIntervalUnit.MONTH_END,resetTime,startTime,null);
            Calendar expected = calendar;
            expected.set(Calendar.YEAR, 2018);
            expected.set(Calendar.MONTH, 1);
            expected.set(Calendar.DAY_OF_MONTH, 28);
            expected.set(Calendar.HOUR_OF_DAY, 23);
            expected.set(Calendar.MINUTE, 59);
            expected.set(Calendar.SECOND, 59);
            expected.set(Calendar.MILLISECOND,999);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);


        }


        @Test
        public void calculateExpiryBasedOnMonthEndEvenThoughBillDateIsConfigured() throws OperationFailedException {
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(1,RenewalIntervalUnit.MONTH_END,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(1, RenewalIntervalUnit.MONTH_END,resetTime,startTime,5);
            Calendar expected = Calendar.getInstance();
            expected.set(Calendar.YEAR, 2018);
            expected.set(Calendar.MONTH, 1);
            expected.set(Calendar.DAY_OF_MONTH, 28);
            expected.set(Calendar.HOUR_OF_DAY, 23);
            expected.set(Calendar.MINUTE, 59);
            expected.set(Calendar.SECOND, 59);
            expected.set(Calendar.MILLISECOND,999);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);


        }


        @Test
        public void calculateExpiryBasedOnMidNight() throws OperationFailedException {
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(5,RenewalIntervalUnit.MID_NIGHT,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(5, RenewalIntervalUnit.MID_NIGHT,resetTime,startTime,null);
            Calendar expected = Calendar.getInstance();
            expected.set(Calendar.YEAR, 2018);
            expected.set(Calendar.MONTH, 1);
            expected.set(Calendar.DAY_OF_MONTH, 9);
            expected.set(Calendar.HOUR_OF_DAY, 23);
            expected.set(Calendar.MINUTE, 59);
            expected.set(Calendar.SECOND, 59);
            expected.set(Calendar.MILLISECOND,999);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);


        }

        @Test
        public void calculateExpiryBasedOnTillBillDateThatThrowsOperationNotSupportedException() throws OperationFailedException {
            expectedException.expect(OperationFailedException.class);
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(0,RenewalIntervalUnit.TILL_BILL_DATE,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(0, RenewalIntervalUnit.TILL_BILL_DATE,resetTime,startTime,null);
            Calendar expected = Calendar.getInstance();
            expected.set(Calendar.YEAR, 2018);
            expected.set(Calendar.MONTH, 1);
            expected.set(Calendar.DAY_OF_MONTH, 9);
            expected.set(Calendar.HOUR_OF_DAY, 23);
            expected.set(Calendar.MINUTE, 59);
            expected.set(Calendar.SECOND, 59);
            expected.set(Calendar.MILLISECOND,999);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);

        }


        @Test
        public void calculateExpiryBasedOnTillBillDate() throws OperationFailedException {
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(0,RenewalIntervalUnit.TILL_BILL_DATE,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(0, RenewalIntervalUnit.TILL_BILL_DATE,resetTime,startTime,29);
            Calendar expected = Calendar.getInstance();
            expected.set(Calendar.YEAR, 2018);
            expected.set(Calendar.MONTH, 1);
            expected.set(Calendar.DAY_OF_MONTH, 28);
            expected.set(Calendar.HOUR_OF_DAY, 23);
            expected.set(Calendar.MINUTE, 59);
            expected.set(Calendar.SECOND, 59);
            expected.set(Calendar.MILLISECOND,999);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);

        }



        @Test
        public void calculateExpiryBasedOnTillBillDateProvidedAsTodayDate() throws OperationFailedException {
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(0,RenewalIntervalUnit.TILL_BILL_DATE,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(0, RenewalIntervalUnit.TILL_BILL_DATE,resetTime,startTime,21);
            Calendar expected = Calendar.getInstance();
            expected.set(Calendar.YEAR, 2018);
            expected.set(Calendar.MONTH, 1);
            expected.set(Calendar.DAY_OF_MONTH, 20);
            expected.set(Calendar.HOUR_OF_DAY, 23);
            expected.set(Calendar.MINUTE, 59);
            expected.set(Calendar.SECOND, 59);
            expected.set(Calendar.MILLISECOND,999);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);

        }


        @Test
        public void calculateExpiryBasedOnConfigurationProvidedGreaterThanRestTime() throws OperationFailedException {
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(888,RenewalIntervalUnit.MONTH,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(888, RenewalIntervalUnit.MONTH,resetTime,startTime,null);
            Calendar expected = Calendar.getInstance();
            expected.set(Calendar.YEAR, 2092);
            expected.set(Calendar.MONTH, 1);
            expected.set(Calendar.DAY_OF_MONTH, 05);
            expected.set(Calendar.HOUR_OF_DAY, 00);
            expected.set(Calendar.MINUTE, 00);
            expected.set(Calendar.MILLISECOND,0);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);

        }


        @Test
        public void calculateExpiryBasedOnMonthEndValueGivenAs_4() throws OperationFailedException {
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(4,RenewalIntervalUnit.MONTH_END,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(4, RenewalIntervalUnit.MONTH_END,resetTime,startTime,null);
            Calendar expected = calendar;
            expected.set(Calendar.YEAR, 2018);
            expected.set(Calendar.MONTH, 4);
            expected.set(Calendar.DAY_OF_MONTH, 31);
            expected.set(Calendar.HOUR_OF_DAY, 23);
            expected.set(Calendar.MINUTE, 59);
            expected.set(Calendar.SECOND, 59);
            expected.set(Calendar.MILLISECOND,999);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);


        }

        @Test
        public void calculateExpiryBasedOnRenewalIntervalNotProvided() throws OperationFailedException {
            Timestamp startTime = new Timestamp(getFixedDate());
            Timestamp resetTime = getRestTime(0,RenewalIntervalUnit.MONTH,startTime);
            Timestamp balanceExpiry = RenewalIntervalUtility.calculateExpiryTime(0, RenewalIntervalUnit.MONTH,resetTime,startTime,null);
            Calendar expected = Calendar.getInstance();
            expected.setTimeInMillis(System.currentTimeMillis());
            expected.add(Calendar.YEAR,20);
            expected.set(Calendar.HOUR_OF_DAY, 23);
            expected.set(Calendar.MINUTE, 59);
            expected.set(Calendar.SECOND, 59);
            expected.set(Calendar.MILLISECOND, 0);
            Assert.assertEquals(new Timestamp(expected.getTimeInMillis()),balanceExpiry);

        }
    }

    public class Proration{
        public class NotApplicable{
            @Test
            public void whenRenewalIntervalIsDay(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.DAY_OF_MONTH, 15);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(1000l, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.DAY, 25);
                Assert.assertEquals(1000l,proratedQuota);
            }
            @Test
            public void whenRenewalIntervalIsMidNight(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.HOUR_OF_DAY, 15);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(2000l, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.MID_NIGHT, 25);
                Assert.assertEquals(2000l,proratedQuota);
            }
            @Test
            public void whenRenewalIntervalIsHour(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.MINUTE, 15);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(3000l, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.HOUR, 25);
                Assert.assertEquals(3000l,proratedQuota);
            }
            @Test
            public void whenQuotaIsUnlimited(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.DAY_OF_MONTH, 25);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(CommonConstants.QUOTA_UNLIMITED, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.MONTH_END, 25);
                Assert.assertEquals(CommonConstants.QUOTA_UNLIMITED,proratedQuota);
            }
            @Test
            public void whenQuotaIsUndefined(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.DAY_OF_MONTH, 15);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(CommonConstants.QUOTA_UNDEFINED, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.MONTH, 25);
                Assert.assertEquals(CommonConstants.QUOTA_UNDEFINED, proratedQuota);
            }
        }

        public class Applicable{
            @Test
            public void givesProRatedDataWhenRenewalIntervalUnitIsMonth(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.DAY_OF_MONTH, 15);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(4000l, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.MONTH, 1);
                Assert.assertEquals(1572l,proratedQuota);

            }

            @Test
            public void givesProRatedDataWhenRenewalIntervalUnit2Month(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.DAY_OF_MONTH, 15);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(5000l, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.MONTH, 2);
                Assert.assertEquals(917l,proratedQuota);

            }

            @Test
            public void givesProRatedDataWhenRenewalIntervalUnitIsMonthEnd(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.DAY_OF_MONTH, 28);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(6000l, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.MONTH_END, 1);
                Assert.assertEquals(5143l,proratedQuota);

            }

            @Test
            public void givesProRatedDataWhenRenewalIntervalUnit3MonthEnd(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.DAY_OF_MONTH, 15);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(7000l, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.MONTH_END, 3);
                Assert.assertEquals(866l,proratedQuota);

            }

            @Test
            public void givesProRatedDataWhenRenewalIntervalUnitIsBillDate(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.DAY_OF_MONTH, 15);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(8000l, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.TILL_BILL_DATE, 0);
                Assert.assertEquals(3143l,proratedQuota);

            }

            @Test
            public void givesActualQuotaWhenDurationIsLongerThanRenewalInterval(){
                Timestamp startTime = new Timestamp(getFixedDate());
                Calendar endTime = getFixedDateCalendar();
                endTime.set(Calendar.MONTH, 4);
                long proratedQuota = RenewalIntervalUtility.getProratedQuota(8000l, startTime, new Timestamp(endTime.getTimeInMillis()), RenewalIntervalUnit.MONTH, 1);
                Assert.assertEquals(8000l,proratedQuota);

            }
        }
    }

    private Timestamp getRestTime(int renewalInterval, RenewalIntervalUnit renewalIntervalUnit, Timestamp startTime){
        if(renewalInterval!= 0) {
            return new Timestamp(renewalIntervalUnit.addTime(startTime.getTime(), renewalInterval));
        }else{
            return new Timestamp(ResetTimeUtility.calculateQuotaResetTime());
        }
    }

}
