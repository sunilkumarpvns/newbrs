package com.elitecore.netvertex.pm.qos.rnc.ratecard;

import java.util.List;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.pm.quota.QuotaReservation;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class DataRateCard extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard implements RateCard {
    private static final String MODULE = "RATE-CARD";

    public DataRateCard(String id, String name, String keyOne,
                        String keyTwo, List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion> dataRateCardVersions,
                        Uom pulseUom, Uom rateUom) {
        super(id, name, keyOne, keyTwo, dataRateCardVersions, pulseUom, rateUom);
    }

    public boolean isApplicable(PolicyContext policyContext, QoSInformation qoSInformation) {

        /* Intentionally, used only first version as in phase 1 we support only one version configuration only*/
        DataRateCardVersion latestVersion = (DataRateCardVersion) getRateCardVersions().get(0);

        if (latestVersion.isApplicable(policyContext, getKeyOne(), getKeyTwo()) == false) {

            if (getLogger().isInfoLogLevel()) {
                policyContext.getTraceWriter().println("Failed to rate request using Rate card: " + getName());
            }

            return false;
        }


        return true;
    }

    @Override
    public boolean applyReservation(PolicyContext policyContext, String packageId, Subscription subscription, QuotaReservation quotaReservation) {

        DataRateCardVersion latestVersion = (DataRateCardVersion) getRateCardVersions().get(0);
        if (latestVersion.isApplicable(policyContext, getKeyOne(), getKeyTwo()) == false) {
            return false;
        }

        return latestVersion.applyReservation(policyContext, getKeyOne(), getKeyTwo(), packageId, subscription, quotaReservation);
    }
}
