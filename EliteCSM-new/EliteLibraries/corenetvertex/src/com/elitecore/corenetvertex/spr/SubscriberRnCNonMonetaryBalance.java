package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.pm.PolicyManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubscriberRnCNonMonetaryBalance {

    private Map<String, SubscriptionRnCNonMonetaryBalance> subscriptionWiseBalance;
    private Map<String, RnCNonMonetaryBalance> idWiseBalance;
    private Map<String, RnCNonMonetaryBalance> idWiseExpiredBalance;

    public SubscriberRnCNonMonetaryBalance(List<RnCNonMonetaryBalance> nonMonetaryBalances) {
        this.subscriptionWiseBalance = new HashMap<>();
        this.idWiseBalance = new HashMap<>();
        this.idWiseExpiredBalance = new HashMap<>();

        if (nonMonetaryBalances == null) {
            return;
        }

        for (int index = 0; index < nonMonetaryBalances.size(); index++) {
            RnCNonMonetaryBalance nonMonetoryBalance = nonMonetaryBalances.get(index);
            addBalance(nonMonetoryBalance);
        }

    }

    private SubscriberRnCNonMonetaryBalance(Map<String, SubscriptionRnCNonMonetaryBalance> subscriptionWiseBalance, Map<String, RnCNonMonetaryBalance> idWiseExpiredBalance) {
        this.idWiseBalance = new HashMap<>();

        if(idWiseExpiredBalance == null) {
            this.idWiseExpiredBalance = new HashMap<>();
        }else{
            this.idWiseExpiredBalance = idWiseExpiredBalance;
        }

        if (subscriptionWiseBalance == null) {
            this.subscriptionWiseBalance = new HashMap<>();
        } else {
            this.subscriptionWiseBalance = subscriptionWiseBalance;
            subscriptionWiseBalance.values().forEach(subscriptionNonMonitoryBalance -> {
                Map<String, RnCNonMonetaryBalance> allRateCardBalance = subscriptionNonMonitoryBalance.getAllRateCardBalance();
                allRateCardBalance.values().forEach(nonMonetoryBalance -> idWiseBalance.put(nonMonetoryBalance.getId(), nonMonetoryBalance));
            });
        }


    }

    public void addBalance(RnCNonMonetaryBalance nonMonetoryBalance) {

        if(System.currentTimeMillis() > nonMonetoryBalance.getBalanceExpiryTime()){
            idWiseExpiredBalance.put(nonMonetoryBalance.getId(), nonMonetoryBalance);
            return;
        }

        String packageIdOrSubscriptionId = nonMonetoryBalance.getSubscriptionId();
        if (packageIdOrSubscriptionId == null) {
            packageIdOrSubscriptionId = nonMonetoryBalance.getPackageId();
        }
        SubscriptionRnCNonMonetaryBalance subscriptionNonMonitoryBalance = subscriptionWiseBalance.computeIfAbsent(packageIdOrSubscriptionId, s -> new SubscriptionRnCNonMonetaryBalance(nonMonetoryBalance.getPackageId()));
        subscriptionNonMonitoryBalance.addBalance(nonMonetoryBalance);

        idWiseBalance.put(nonMonetoryBalance.getId(), nonMonetoryBalance);

    }

    public SubscriptionRnCNonMonetaryBalance getPackageBalance(String packageOrSubscriptionId) {
        return subscriptionWiseBalance.get(packageOrSubscriptionId);
    }

    public Map<String, SubscriptionRnCNonMonetaryBalance> getPackageBalances() {
        return subscriptionWiseBalance;
    }

    public SubscriberRnCNonMonetaryBalance copy() {
        Map<String, SubscriptionRnCNonMonetaryBalance> clonedSubscriptionWiseBalance = new HashMap<>();

        subscriptionWiseBalance.forEach((subscriptionId, subscriptionBalances) -> clonedSubscriptionWiseBalance.put(subscriptionId, subscriptionBalances.copy()));

        Map<String, RnCNonMonetaryBalance> clonedIdWiseExpiredBalance = new HashMap<>();

        idWiseExpiredBalance.forEach((id, expiredBalance) -> clonedIdWiseExpiredBalance.put(id, expiredBalance.copy()));

        return new SubscriberRnCNonMonetaryBalance(clonedSubscriptionWiseBalance, clonedIdWiseExpiredBalance);

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

        stringWriter.println();
        stringWriter.println(" -- Subscriber Balance (Active)-- ");
        stringWriter.incrementIndentation();

        if (Maps.isNullOrEmpty(this.subscriptionWiseBalance)) {
            stringWriter.println("No Balance found");
        } else {
            for (Map.Entry<String, SubscriptionRnCNonMonetaryBalance> entry : subscriptionWiseBalance.entrySet()) {

                stringWriter.println("Package Name: " + PolicyManager.getInstance().getPackageName(entry.getValue().getPackageID()) + " (" + entry.getValue().getPackageID() + ")");
                if (entry.getKey().equals(entry.getValue().getPackageID()) == false) {
                    stringWriter.println("Subcription ID : " + entry.getKey());
                }
                entry.getValue().toString(stringWriter);
            }
        }
        stringWriter.decrementIndentation();


        stringWriter.println(" -- Subscriber Balance (Expired)-- ");
        stringWriter.incrementIndentation();

        if (Maps.isNullOrEmpty(this.idWiseExpiredBalance)) {
            stringWriter.println("No Balance found");
        } else {
            for (Map.Entry<String, RnCNonMonetaryBalance> entry : idWiseExpiredBalance.entrySet()) {

                stringWriter.println("Package Name: " + PolicyManager.getInstance().getPackageName(entry.getValue().getPackageId()) + " (" + entry.getValue().getPackageId() + ")");
                entry.getValue().toString(stringWriter);
            }
        }
        stringWriter.decrementIndentation();
    }

    public RnCNonMonetaryBalance getBalanceById(String id) {
        RnCNonMonetaryBalance nonMonetoryBalance = idWiseBalance.get(id);
        if(nonMonetoryBalance != null) {
            return nonMonetoryBalance;
        }
        return idWiseExpiredBalance.get(id);
    }

    public Set<Map.Entry<String, RnCNonMonetaryBalance>> getBalances() {
        return idWiseBalance.entrySet();
    }

    public RnCNonMonetaryBalance getIdWiseExpiredBalance(String id) { return idWiseExpiredBalance.get(id); }

    public void addBalance(SubscriptionRnCNonMonetaryBalance rnCBalance) {
        rnCBalance.getAllRateCardBalance().values().forEach(this::addBalance);
    }
}
