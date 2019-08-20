package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(HierarchicalContextRunner.class)
public class AllowedUsageTest { // NOSONAR

    private AllowedUsage allowedUsage;

    @Before
    public void setUp() {
        allowedUsage = AllowedUsage.ALWAYS_ALLOWED;
    }

    public class ALWAYSALLOWED_shouldGiveUnlimitedQuota {

        @Test
        public void totalUnlimited() {
            assertEquals(CommonConstants.QUOTA_UNLIMITED, allowedUsage.getTotalInBytes());
            assertEquals(CommonConstants.QUOTA_UNLIMITED, allowedUsage.getTotal());
        }

        @Test
        public void downloadUnlimited() {
            assertEquals(CommonConstants.QUOTA_UNLIMITED, allowedUsage.getDownloadInBytes());
            assertEquals(CommonConstants.QUOTA_UNLIMITED, allowedUsage.getDownload());
        }

        @Test
        public void uploadUnlimited() {
            assertEquals(CommonConstants.QUOTA_UNLIMITED, allowedUsage.getUploadInBytes());
        }

        @Test
        public void timeUnlimited() {
            assertEquals(CommonConstants.QUOTA_UNLIMITED, allowedUsage.getTimeInSeconds());
            assertEquals(CommonConstants.QUOTA_UNLIMITED, allowedUsage.getTime());
        }
    }


    @Test
    public void test_getBalanceShouldGiveUNLIMITEDBalanceObjectWhenSubscriberUsageIsNull() {
        SubscriberUsage subscriberUsage = null;
        assertSame(Balance.UNLIMITED, allowedUsage.getBalance(subscriberUsage, Calendar.getInstance()));
    }

    @Test
    public void test_getBalanceShouldGiveUNLIMITEDBalanceObjectWhenServiceRgNonMonitoryBalanceIsNull() {
        NonMonetoryBalance nonMonetoryBalance = null;
        assertSame(Balance.UNLIMITED, allowedUsage.getBalance(nonMonetoryBalance, null));
    }
}