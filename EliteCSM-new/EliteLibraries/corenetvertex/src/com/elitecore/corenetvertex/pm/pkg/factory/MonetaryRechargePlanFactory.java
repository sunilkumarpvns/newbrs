package com.elitecore.corenetvertex.pm.pkg.factory;

import com.elitecore.commons.base.Splitter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.monetaryrechargeplan.MonetaryRechargePlanData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class MonetaryRechargePlanFactory {
    private static final String MODULE = "MON-RECHARGE-PLAN-FCTRY";
    private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(CommonConstants.COMMA).trimTokens();

    public List<MonetaryRechargePlan> create(List<MonetaryRechargePlanData> monetaryRechargePlanDatas) {
        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Creating Monetary Recharge Plan Monetary Recharge Plan data started");
        }

        List<MonetaryRechargePlan> tempMonetaryRechargePlans = new ArrayList<>();

        for (MonetaryRechargePlanData monetaryRechargePlanData : monetaryRechargePlanDatas) {
            List<String> monetaryRechargePlanFailReasons = new ArrayList<>();
            List<String> monetaryRechargePlanPartialFailReasons = new ArrayList<>();
            String id = monetaryRechargePlanData.getId();
            String name = monetaryRechargePlanData.getName();

            PkgMode mode = PkgMode.getMode(monetaryRechargePlanData.getPackageMode());

            if (mode == null) {
                monetaryRechargePlanFailReasons.add("Invalid package mode(" + monetaryRechargePlanData.getPackageMode() + ") given for package: " + monetaryRechargePlanData.getName());
            }

            BigDecimal price = monetaryRechargePlanData.getPrice();

            BigDecimal amount = BigDecimal.ZERO;

            if (monetaryRechargePlanData.getAmount() != null) {
                amount = monetaryRechargePlanData.getAmount();
            }

            int validityPeriod = 0;

            if (monetaryRechargePlanData.getValidity() != null) {
                validityPeriod = monetaryRechargePlanData.getValidity();
            }
            ValidityPeriodUnit validityPeriodUnit = monetaryRechargePlanData.getValidityPeriodUnit();

            if (validityPeriodUnit == null) {
                validityPeriodUnit = ValidityPeriodUnit.MID_NIGHT;
            }

            String description = monetaryRechargePlanData.getDescription();



            PkgStatus availabilityStatus = PkgStatus.valueOf(monetaryRechargePlanData.getStatus());

            String groups = monetaryRechargePlanData.getGroups();

            PolicyStatus policyStatus = PolicyStatus.SUCCESS;
            String monetaryRechargePlanFailReason = null;
            if (monetaryRechargePlanFailReasons.isEmpty() == false) {
                policyStatus = PolicyStatus.FAILURE;
                monetaryRechargePlanFailReason = monetaryRechargePlanFailReasons.toString();
            }

            String monetaryRechargePlanPartialFailReason = null;
            if (monetaryRechargePlanPartialFailReasons.isEmpty() == false) {
                monetaryRechargePlanPartialFailReason = monetaryRechargePlanPartialFailReasons.toString();
                policyStatus = PolicyStatus.PARTIAL_SUCCESS;
            }

            MonetaryRechargePlan monetaryRechargePlan = new MonetaryRechargePlan(id,
                    name,
                    price,
                    amount,
                    validityPeriod,
                    validityPeriodUnit,
                    description,
                    mode,
                    policyStatus,
                    COMMA_BASE_SPLITTER.split(groups),
                    availabilityStatus,
                    monetaryRechargePlanFailReason,
                    monetaryRechargePlanPartialFailReason);

            if (monetaryRechargePlanFailReason == null) {

                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Monetary Recharge Plan(" + name + ") parsed successfully");
                }
            }

            tempMonetaryRechargePlans.add(monetaryRechargePlan);
        }

        getLogger().info(MODULE, "Creating Monetary Recharge Plan from Monetary Recharge Plan data completed");
        return tempMonetaryRechargePlans;
    }
}
