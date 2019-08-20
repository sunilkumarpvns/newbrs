package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile

import com.elitecore.corenetvertex.constants.CommonConstants
import com.elitecore.corenetvertex.constants.DataUnit
import com.elitecore.corenetvertex.constants.TimeUnit
import com.elitecore.corenetvertex.spr.NonMonetoryBalance
import spock.lang.Specification
import spock.lang.Unroll

import static com.elitecore.corenetvertex.constants.DataUnit.*
import static com.elitecore.corenetvertex.constants.TimeUnit.*
import static com.elitecore.corenetvertex.spr.NonMonitoryBalanceBuilder.balance

class DailyAllowedUsageGetBalanceOnUserBalanceTest extends Specification {

    private DailyAllowedUsage dailyAllowedUsage;
    private Calendar calendar;

    public void setup() {

        dailyAllowedUsage = AllowedUsageBuilder.daily{
            total 100.kb;
            upload 100, GB
            download 200, MB
            time 100, MINUTE
        }

        print dailyAllowedUsage
        calendar = Calendar.getInstance();
    }


    @Unroll
    def 'Difference between Allowed Usage and ServiceRgBalance as Balance when reset time is not passed[currentTime(#currentTime), resetTime(#resetTime)]'() {

        given:

        NonMonetoryBalance balance = balance { day volume:200l, time:100l}
        balance.setDailyResetTime(resetTime)

        calendar.setTimeInMillis(currentTime);

        when:

        Balance remainingBalance = dailyAllowedUsage.getBalance(balance, calendar)


        then:
        dailyAllowedUsage.minusTotalAndTime(balance) == remainingBalance

        where:
        currentTime | resetTime
        100 | 100
        100 | 101
    }

    def "Allowed usage as balance when reset time is passed"() {

        given:

        dailyAllowedUsage = AllowedUsageBuilder.daily {
            total 1, BYTE;
            upload 1, BYTE
            download 1, BYTE
            time 1, SECOND
        }

        NonMonetoryBalance balance = balance { day volume:200l, time:100l}
        Balance expectedBalance = new BalanceImpl(1, 1, 1, 1);

        calendar.setTimeInMillis(balance.getDailyResetTime() + 1);

        when:

        Balance remainingBalance = dailyAllowedUsage.getBalance(balance, calendar)


        then:
        expectedBalance == remainingBalance

    }

    def "Allowed usage as balance when balance not found"() {

        given:

        dailyAllowedUsage = AllowedUsageBuilder.daily{
            total 1, BYTE;
            upload 1, BYTE
            download 1, BYTE
            time 1, SECOND
        }

        NonMonetoryBalance balance = null;
        Balance expectedBalance = new BalanceImpl(1, 1, 1, 1);

        when:

        Balance remainingBalance = dailyAllowedUsage.getBalance((NonMonetoryBalance)balance, calendar)


        then:
        expectedBalance == remainingBalance

    }

    def "getInBytes should give UNLIMITED value if UNLIMITED value is provided in Data Units"() {

        given:
        DataUnit dataUnit = GB
        TimeUnit timeUnit = MONTH
        long totalValue = CommonConstants.QUOTA_UNLIMITED
        long downloadValue = CommonConstants.QUOTA_UNLIMITED
        long uploadValue= CommonConstants.QUOTA_UNLIMITED
        long timeValue = CommonConstants.QUOTA_UNLIMITED

        when:
        dailyAllowedUsage = AllowedUsageBuilder.daily{
            total totalValue, dataUnit
            upload uploadValue, dataUnit
            download downloadValue, dataUnit
            time timeValue, timeUnit
        }

        then:
        CommonConstants.QUOTA_UNLIMITED == dailyAllowedUsage.totalInBytes
        CommonConstants.QUOTA_UNLIMITED == dailyAllowedUsage.uploadInBytes
        CommonConstants.QUOTA_UNLIMITED == dailyAllowedUsage.downloadInBytes
        CommonConstants.QUOTA_UNLIMITED == dailyAllowedUsage.timeInSeconds
    }
}
