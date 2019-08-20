package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.pm.PolicyManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SubscriberNonMonitoryBalance {

    private Map<String, SubscriptionNonMonitoryBalance> subscriptionWiseBalance;
    private Map<String, NonMonetoryBalance> idWiseBalance;
    private Map<String, NonMonetoryBalance> idWiseExpiredBalance;

    public SubscriberNonMonitoryBalance(List<NonMonetoryBalance> nonMonetoryBalanceList) {
        this.subscriptionWiseBalance = new HashMap<>();
        this.idWiseBalance = new HashMap<>();
        this.idWiseExpiredBalance = new HashMap<>();

        if (nonMonetoryBalanceList == null) {
            return;
        }

        for (int index = 0; index < nonMonetoryBalanceList.size(); index++) {
            NonMonetoryBalance nonMonetoryBalance = nonMonetoryBalanceList.get(index);
            addBalance(nonMonetoryBalance);
        }

    }

    private SubscriberNonMonitoryBalance(Map<String, SubscriptionNonMonitoryBalance> subscriptionWiseBalance, Map<String, NonMonetoryBalance> idWiseExpiredBalance) {
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
                Map<String, QuotaProfileBalance> allQuotaProfileBalance = subscriptionNonMonitoryBalance.getAllQuotaProfileBalance();
                allQuotaProfileBalance.values().forEach(quotaProfileBalance -> {
                    quotaProfileBalance.getHsqBalance().forEach(nonMonetoryBalance -> idWiseBalance.put(nonMonetoryBalance.getId(), nonMonetoryBalance));

                    if(Objects.nonNull(quotaProfileBalance.getFupLevel1Balance())) {
                        quotaProfileBalance.getFupLevel1Balance().forEach(nonMonetoryBalance -> idWiseBalance.put(nonMonetoryBalance.getId(), nonMonetoryBalance));
                    }

                    if(Objects.nonNull(quotaProfileBalance.getFupLevel2Balance())) {
                        quotaProfileBalance.getFupLevel2Balance().forEach(nonMonetoryBalance -> idWiseBalance.put(nonMonetoryBalance.getId(), nonMonetoryBalance));
                    }
                });
            });
        }


    }

    public void addBalance(NonMonetoryBalance nonMonetoryBalance) {

        if(System.currentTimeMillis() > nonMonetoryBalance.getBillingCycleResetTime()){
            idWiseExpiredBalance.put(nonMonetoryBalance.getId(), nonMonetoryBalance);
            return;
        }

        if(System.currentTimeMillis() < nonMonetoryBalance.getStartTime()){
            return;
        }

        String subscriptionId = nonMonetoryBalance.getSubscriptionId();
        if (subscriptionId == null) {
            subscriptionId = nonMonetoryBalance.getPackageId();
        }
        SubscriptionNonMonitoryBalance subscriptionNonMonitoryBalance = subscriptionWiseBalance.get(subscriptionId);
        if (subscriptionNonMonitoryBalance == null) {
            subscriptionNonMonitoryBalance = new SubscriptionNonMonitoryBalance(nonMonetoryBalance.getPackageId());
            subscriptionWiseBalance.put(subscriptionId, subscriptionNonMonitoryBalance);
        }

        subscriptionNonMonitoryBalance.addBalance(nonMonetoryBalance);

        idWiseBalance.put(nonMonetoryBalance.getId(), nonMonetoryBalance);

    }

    public SubscriptionNonMonitoryBalance getPackageBalance(String packageOrSubscriptionId) {
        return subscriptionWiseBalance.get(packageOrSubscriptionId);
    }

    public Map<String, SubscriptionNonMonitoryBalance> getPackageBalances() {
        return subscriptionWiseBalance;
    }

    public SubscriberNonMonitoryBalance copy() {
        Map<String, SubscriptionNonMonitoryBalance> clonedSubscriptionWiseBalance = new HashMap<>();

        subscriptionWiseBalance.forEach((subscriptionId, subscriptionBalances) -> clonedSubscriptionWiseBalance.put(subscriptionId, subscriptionBalances.copy()));

        Map<String, NonMonetoryBalance> clonedIdWiseExpiredBalance = new HashMap<>();

        idWiseExpiredBalance.forEach((id, expiredBalance) -> clonedIdWiseExpiredBalance.put(id, expiredBalance.copy()));

        return new SubscriberNonMonitoryBalance(clonedSubscriptionWiseBalance, clonedIdWiseExpiredBalance);

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
            for (Map.Entry<String, SubscriptionNonMonitoryBalance> entry : subscriptionWiseBalance.entrySet()) {

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
            for (Map.Entry<String, NonMonetoryBalance> entry : idWiseExpiredBalance.entrySet()) {

                stringWriter.println("Package Name: " + PolicyManager.getInstance().getPackageName(entry.getValue().getPackageId()) + " (" + entry.getValue().getPackageId() + ")");
                entry.getValue().toString(stringWriter);
            }
        }
        stringWriter.decrementIndentation();
    }

    public NonMonetoryBalance getBalanceById(String id) {
        NonMonetoryBalance nonMonetoryBalance = idWiseBalance.get(id);
        if(nonMonetoryBalance != null) {
            return nonMonetoryBalance;
        }
        return idWiseExpiredBalance.get(id);
    }

    public Set<Map.Entry<String, NonMonetoryBalance>> getBalances() {
        return idWiseBalance.entrySet();
    }


    public NonMonetoryBalance getIdWiseExpiredBalance(String id) { return idWiseExpiredBalance.get(id); }

    public void addBalance(SubscriptionNonMonitoryBalance rnCBalance) {
        rnCBalance.getAllQuotaProfileBalance().values().forEach(quotaProfileBalance -> {
            quotaProfileBalance.getHsqBalance().forEach(this::addBalance);

            if(Objects.nonNull(quotaProfileBalance.getFupLevel1Balance())) {
                quotaProfileBalance.getFupLevel1Balance().forEach(this::addBalance);
            }

            if(Objects.nonNull(quotaProfileBalance.getFupLevel2Balance())) {
                quotaProfileBalance.getFupLevel2Balance().forEach(this::addBalance);
            }
        });
    }
}
