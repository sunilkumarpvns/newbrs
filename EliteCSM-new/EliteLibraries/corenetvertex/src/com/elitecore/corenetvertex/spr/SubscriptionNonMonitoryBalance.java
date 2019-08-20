package com.elitecore.corenetvertex.spr;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;

public class SubscriptionNonMonitoryBalance {

    private String packageID;
    private Map<String, QuotaProfileBalance> quotaProfileWiseBalance;

    public SubscriptionNonMonitoryBalance(String packageID) {
        this.packageID = packageID;
        this.quotaProfileWiseBalance = new HashMap<>();
    }

    public QuotaProfileBalance getBalance(String quotaProfileId){
        return quotaProfileWiseBalance.get(quotaProfileId);
    }

    public NonMonetoryBalance getBalance(String quotaProfileId, long serviceId, long ratingGroup, int level) {

        QuotaProfileBalance quotaProfileBalance = quotaProfileWiseBalance.get(quotaProfileId);

        if(quotaProfileBalance == null) {
            return null;
        }

        ArrayList<NonMonetoryBalance> quotaProfileDetailBalances;


        switch (level) {

            case 0:
                quotaProfileDetailBalances = quotaProfileBalance.getHsqBalance();
                break;
            case 1:
                quotaProfileDetailBalances = quotaProfileBalance.getFupLevel1Balance();
                break;
            case 2:
                quotaProfileDetailBalances = quotaProfileBalance.getFupLevel2Balance();
                break;
            default:
                return null;

        }



        for (int index = 0; index < quotaProfileDetailBalances.size(); index++) {
            NonMonetoryBalance serviceRgNonMonitoryBalance = quotaProfileDetailBalances.get(index);

            if (serviceRgNonMonitoryBalance.getRatingGroupId() != ratingGroup) {
                continue;
            }

            if (serviceRgNonMonitoryBalance.getServiceId() != serviceId) {
                continue;
            }

            return serviceRgNonMonitoryBalance;
        }

        return null;
    }

    public SubscriptionNonMonitoryBalance copy() {

        SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance = new SubscriptionNonMonitoryBalance(packageID);


        quotaProfileWiseBalance.forEach((quotaProfileId, balance) -> {

            if(Objects.nonNull(balance.getHsqBalance())) {
                for (int index = 0; index < balance.getHsqBalance().size(); index++) {
                    subscriptionNonMonitoryBalance.addBalance(balance.getHsqBalance().get(index).copy());
                }
            }

            if(Objects.nonNull(balance.getFupLevel1Balance())) {
                for (int index = 0; index < balance.getFupLevel1Balance().size(); index++) {
                    subscriptionNonMonitoryBalance.addBalance(balance.getFupLevel1Balance().get(index).copy());
                }
            }

            if(Objects.nonNull(balance.getFupLevel2Balance())) {
                for (int index = 0; index < balance.getFupLevel2Balance().size(); index++) {
                    subscriptionNonMonitoryBalance.addBalance(balance.getFupLevel2Balance().get(index).copy());
                }
            }

        });

        return subscriptionNonMonitoryBalance;
    }


    public String getPackageID() {
        return packageID;
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingPrintWriter out = new IndentingPrintWriter(new PrintWriter(stringWriter));
        toString(out);
        out.close();
        return stringWriter.toString();
    }

    public void toString(IndentingPrintWriter stringWriter) {

        if (quotaProfileWiseBalance == null || quotaProfileWiseBalance.isEmpty()) {
            stringWriter.println("No Balance found");
        } else {
            for(Map.Entry<String,QuotaProfileBalance> entry: quotaProfileWiseBalance.entrySet()) {
                QuotaProfile quotaProfile = PolicyManager.getInstance().getQuotaProfile(packageID, entry.getKey());
                if (quotaProfile != null) {
                    stringWriter.println("Quota Profile: " + quotaProfile.getName() + " ("  + entry.getKey() + ")");
                } else {
                    stringWriter.println("Quota Profile: ("  + entry.getKey() + ")");
                }
                entry.getValue().toString(stringWriter);
            }
        }
    }

    public void addBalance(NonMonetoryBalance nonMonetoryBalance) {
        QuotaProfileBalance quotaProfileBalance = quotaProfileWiseBalance.get(nonMonetoryBalance.getQuotaProfileId());
        if(quotaProfileBalance == null){
            quotaProfileBalance = new QuotaProfileBalance();
            quotaProfileWiseBalance.put(nonMonetoryBalance.getQuotaProfileId(),quotaProfileBalance);
        }
        quotaProfileBalance.addBalance(nonMonetoryBalance);
    }

    public Map<String, QuotaProfileBalance> getAllQuotaProfileBalance(){
        return quotaProfileWiseBalance;
    }

}
