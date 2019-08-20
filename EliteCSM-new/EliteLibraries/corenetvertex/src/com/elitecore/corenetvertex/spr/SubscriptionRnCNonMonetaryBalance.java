package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.io.IndentingPrintWriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class SubscriptionRnCNonMonetaryBalance {

    private String packageID;
    private Map<String, RnCNonMonetaryBalance> ratecardWiseBalance;

    public SubscriptionRnCNonMonetaryBalance(String packageID) {
        this.packageID = packageID;
        this.ratecardWiseBalance = new HashMap<>();
    }

    public RnCNonMonetaryBalance getBalance(String ratecardId){
        return ratecardWiseBalance.get(ratecardId);
    }

    public SubscriptionRnCNonMonetaryBalance copy() {

        SubscriptionRnCNonMonetaryBalance subscriptionNonMonitoryBalance = new SubscriptionRnCNonMonetaryBalance(packageID);

        ratecardWiseBalance.forEach((quotaProfileId, balance) -> subscriptionNonMonitoryBalance.addBalance(balance.copy()));

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

        if (ratecardWiseBalance == null || ratecardWiseBalance.isEmpty()) {
            stringWriter.println("No Balance found");
        } else {
            for(Map.Entry<String, RnCNonMonetaryBalance> entry: ratecardWiseBalance.entrySet()) {
                stringWriter.println("Rate Card: ("  + entry.getKey() + ")");
                entry.getValue().toString(stringWriter);
            }
        }
    }

    public void addBalance(RnCNonMonetaryBalance nonMonetoryBalance) {
        ratecardWiseBalance.put(nonMonetoryBalance.getRatecardId(), nonMonetoryBalance);
    }

    public Map<String, RnCNonMonetaryBalance> getAllRateCardBalance(){
        return ratecardWiseBalance;
    }

}
