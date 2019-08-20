package com.elitecore.netvertex.pm.qos.rnc.ratecard;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.netvertex.pm.PolicyContext;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class FlatRating extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatRating implements VersionDetail {
    private static final String MODULE = "FLATE-RATE";

    public FlatRating(String keyOneValue, String keyTwoValue, List<RateSlab> slabs, String revenueDetail) {
        super(keyOneValue, keyTwoValue, slabs, revenueDetail);
    }

    @Override
    public boolean isApplicable(PolicyContext policyContext, String keyOne, String keyTwo) {
        return isKeyApplied(policyContext, keyOne, getKeyOneValue()) && isKeyApplied(policyContext, keyTwo, getKeyTwoValue());
    }

    private boolean isKeyApplied(PolicyContext policyContext, String key, String keyValue) {

        if (Strings.isNullOrBlank(key)) {
            if (getLogger().isDebugLogLevel()) {
                policyContext.getTraceWriter().println("Satisfied. Reason: Key is not configured in rate card");
            }
            return true;
        }

        if (Strings.isNullOrBlank(keyValue)) {
            if (getLogger().isDebugLogLevel()) {
                policyContext.getTraceWriter().println("Satisfied. Reason: value not configured");
            }
            return true;
        }

        String keyAttributeValue = policyContext.getPCRFResponse().getAttribute(key);
        if (Strings.isNullOrBlank(keyAttributeValue)) {
            if (getLogger().isDebugLogLevel()) {
                policyContext.getTraceWriter().println("Not Satisfied. Reason: Value not found for PCRF key: " + key + " from PCRF Response");
            }
            return false;
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Value found from PCRF response: " + key + "=" + keyAttributeValue
                    + ", Value Configured: " + keyValue);
        }

        return keyValue.equals(keyAttributeValue);
    }
}
