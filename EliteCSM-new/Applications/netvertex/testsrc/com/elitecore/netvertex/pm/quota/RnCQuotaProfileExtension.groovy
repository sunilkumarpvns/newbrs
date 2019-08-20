package com.elitecore.netvertex.pm.quota

import com.elitecore.corenetvertex.spr.NonMonetoryBalance

import static com.elitecore.corenetvertex.spr.NonMonitoryBalanceBuilder.balance

public class RnCQuotaProfileExtension {
    public static NonMonetoryBalance noBillingCycleBalance(final RnCQuotaProfileDetail self) {

        return balance { billingCycleUsage << [upload:0, download:0, total:0, time:0]
                            dayUsage  << [upload:0, download:0, total:0, time:0]
                            weekUsage << [upload:0, download:0, total:0, time:0]
                            info {
                                serviceId = self.serviceId
                                rgId = self.ratingGroupId
                                quotaProfileId = self.quotaProfileId
                                level = self.fupLevel
                            }
                        }





    }


}
