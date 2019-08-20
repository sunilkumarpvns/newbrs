package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile

import com.elitecore.corenetvertex.spr.NonMonetoryBalance
import org.junit.Ignore
import spock.lang.Specification

import static com.elitecore.corenetvertex.constants.DataUnit.*
import static com.elitecore.corenetvertex.constants.TimeUnit.MINUTE
import static com.elitecore.corenetvertex.constants.TimeUnit.SECOND
import static com.elitecore.corenetvertex.spr.NonMonitoryBalanceBuilder.balance

@Ignore
class BillingCycleAllowedUsageGetBalanceOnUserBalanceTest extends Specification {

    private BillingCycleAllowedUsage billingCycleAllowedUsage;
    private Calendar calendar;

    public void setup() {

        billingCycleAllowedUsage = AllowedUsageBuilder.billingCycle{
            total 100, KB;
            upload 100, GB
            download 200, MB
            time 100, MINUTE
        }

        print billingCycleAllowedUsage
        calendar = Calendar.getInstance();
    }


    def "Balance from ServiceRgBalance when reset time is not passed"() {

        given:

        NonMonetoryBalance balance = balance { billingCycle upload:200l,
                download:200l, total:100l, time:100, resetOn:1.seconds.from.now}

        calendar.setTimeInMillis(balance.getBillingCycleResetTime());
        BillingCycleBalance expectedBalance = new BillingCycleBalance(100, 200l, 200l, 100);

        when:

        BillingCycleBalance remainingBalance = billingCycleAllowedUsage.getBalance(balance, calendar)


        then:
        expectedBalance == remainingBalance


    }

    def "getBalance set balance from ServiceRgBalance when reset time equal to current time"() {

        given:

        NonMonetoryBalance balance = balance { billingCycle upload:200l, download:200l, total:100l, time:100, resetOn: 1.second.from.now }

        BillingCycleBalance expectedBalance = new BillingCycleBalance(100, 200l, 200l, 100);

        when:

        BillingCycleBalance remainingBalance = billingCycleAllowedUsage.getBalance(balance, calendar)


        then:
        expectedBalance == remainingBalance


    }


    def "Allowed usage as balance when reset time is passed"() {

        given:

        billingCycleAllowedUsage = AllowedUsageBuilder.billingCycle{
            total 1, BYTE;
            upload 1, BYTE
            download 1, BYTE
            time 1, SECOND
        }

        NonMonetoryBalance balance = balance { billingCycle upload:200l, download:200l, total:100l, time:100, resetOn:5.seconds.ago}
        BillingCycleBalance expectedBalance = new BillingCycleBalance(1, 1, 1, 1);

        when:

        BillingCycleBalance remainingBalance = billingCycleAllowedUsage.getBalance(balance, calendar)


        then:
        expectedBalance == remainingBalance

    }

    def "Allowed usage as balance when balance not found"() {

        given:

        billingCycleAllowedUsage = AllowedUsageBuilder.billingCycle{
            total 1, BYTE;
            upload 1, BYTE
            download 1, BYTE
            time 1, SECOND
        }

        NonMonetoryBalance balance = null;
        BillingCycleBalance expectedBalance = new BillingCycleBalance(1, 1, 1, 1);

        when:

        BillingCycleBalance remainingBalance = billingCycleAllowedUsage.getBalance((NonMonetoryBalance)balance, calendar)


        then:
        expectedBalance == remainingBalance

    }


}
