package com.elitecore.netvertex.pm.quota;

import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pd.topup.TopUpType;
import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp;
import com.elitecore.corenetvertex.spr.MonetaryBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;

public class RncTopUpQuotaProfileDetailTestUtil {

    public static MonetaryBalance createMonetaryBalance(String subscriberId) {
        int totalBalance = nextInt(3, 1000);
        int availableBalance = nextInt(2, totalBalance);
        int reservation = nextInt(1, availableBalance);
        return new MonetaryBalance(UUID.randomUUID().toString(),
                subscriberId,
                null,
                availableBalance,
                totalBalance,
                reservation, 0, 0,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(), null,
                System.currentTimeMillis(),
                0,
                "", "");
    }

    public static NonMonetoryBalance createNonMonetaryBalance(SPRInfo sprInfo, RnCQuotaProfileDetail rnCQuotaProfileDetail, String packageId, String subscriptionId) {

        long billingCycleTotalVolume = nextLong(2, 1000);
        long billingCycleTotalTime = nextLong(2, 1000);
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder(
                UUID.randomUUID().toString(), rnCQuotaProfileDetail.getDataServiceType().getServiceIdentifier(),
                packageId,
                rnCQuotaProfileDetail.getRatingGroup().getIdentifier(),
                sprInfo.getSubscriberIdentity(),
                subscriptionId,
                0,
                rnCQuotaProfileDetail.getQuotaProfileId(), ResetBalanceStatus.NOT_RESET, null, null).
                withBillingCycleVolumeBalance(billingCycleTotalVolume, nextLong(2, billingCycleTotalVolume)).
                withBillingCycleTimeBalance(billingCycleTotalTime, nextLong(2, billingCycleTotalTime))
                .withDailyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withWeeklyUsage(nextLong(2, billingCycleTotalVolume), nextLong(2, billingCycleTotalTime))
                .withDailyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .withWeeklyResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(6))
                .withBillingCycleResetTime(System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1))
                .build();
    }


    public static  MockQuotaTopUp createTopUp(TopUpType topUpType) {

        return createTopUp(topUpType, Collections.emptyList());
    }

    public static MockQuotaTopUp createTopUp(TopUpType topUpType, List<String> applicablePCCProfiles) {
        return MockQuotaTopUp.create(UUID.randomUUID().toString(), UUID.randomUUID().toString() + "name", applicablePCCProfiles).withPackageType(topUpType).quotaProfileTypeIsRnC();
    }


    public static RnCQuotaProfileDetail createRnCQuotaDetail(String quotaProfileId1) {
        return new RnCQuotaProfileFactory(quotaProfileId1, UUID.randomUUID().toString()).randomBalanceWithRate().create();
    }
}
