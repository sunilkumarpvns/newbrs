package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SubscriberMonetaryBalance implements ToStringable {

    private Map<String, List<MonetaryBalance>> serviceWiseBalance;
    private Map<String, MonetaryBalance> idWiseActiveBalance;
    private Map<String, MonetaryBalance> idWiseExpiredBalance;
    private long currentTime;

    private static Comparator<MonetaryBalance> SORT_ACCORDING_TO_EXPIRY_DATE = Comparator.comparingLong(MonetaryBalance::getValidToDate);

    public SubscriberMonetaryBalance(TimeSource timeSource) {
        this( new HashMap<>(4), new HashMap<>(), new HashMap<>(), timeSource.currentTimeInMillis());
    }

    public SubscriberMonetaryBalance(Map<String, List<MonetaryBalance>> serviceWiseBalance,
                                     Map<String, MonetaryBalance> idWiseBalance,
                                     Map<String, MonetaryBalance> idWiseExpiredBalance, long currentTime) {
        this.serviceWiseBalance = serviceWiseBalance == null ? new HashMap<>(4) : serviceWiseBalance;
        this.idWiseActiveBalance = idWiseBalance == null ? new HashMap<>() : idWiseBalance;
        this.idWiseExpiredBalance = idWiseExpiredBalance == null ? new HashMap<>() : idWiseExpiredBalance;
        this.currentTime = currentTime;
        sortServiceBalancesAccordingToExpiryTime();
    }

    private void sortServiceBalancesAccordingToExpiryTime(){
        for(List<MonetaryBalance> balanceList: serviceWiseBalance.values()){
            if(balanceList!=null){
                Collections.sort(balanceList, SORT_ACCORDING_TO_EXPIRY_DATE);
            }
        }
    }

    public void addMonitoryBalances(MonetaryBalance monetaryBalance) {

        long validToDate = monetaryBalance.getValidToDate();

        if(validToDate>0 && validToDate<currentTime){
            idWiseExpiredBalance.put(monetaryBalance.getId(), monetaryBalance);
            return;
        }

        serviceWiseBalance.putIfAbsent(monetaryBalance.getServiceId(),new ArrayList<>());
        serviceWiseBalance.get(monetaryBalance.getServiceId()).add(monetaryBalance);

        Collections.sort(serviceWiseBalance.get(monetaryBalance.getServiceId()), SORT_ACCORDING_TO_EXPIRY_DATE);

        idWiseActiveBalance.put(monetaryBalance.getId(), monetaryBalance);
    }

    public MonetaryBalance getServiceBalance(String serviceId) {
        List<MonetaryBalance> monetaryBalanceList = serviceWiseBalance.get(serviceId);
        if(Collectionz.isNullOrEmpty(monetaryBalanceList) == false){
            for(MonetaryBalance monetaryBalance : monetaryBalanceList){
                if(monetaryBalance.isExist()){
                    return monetaryBalance;
                }
            }
        }
        return getMainBalanceIfExist();
    }

    public MonetaryBalance getMainBalanceIfExist() {
        List<MonetaryBalance> monetaryBalanceList = serviceWiseBalance.get(null);
        if(Collectionz.isNullOrEmpty(monetaryBalanceList) == false){
            for(MonetaryBalance monetaryBalance : monetaryBalanceList){
                if(monetaryBalance.isExist()){
                    return monetaryBalance;
                }
            }
        }
        return null;
    }

    public MonetaryBalance getMainBalance() {
        List<MonetaryBalance> monetaryBalanceList = serviceWiseBalance.get(null);
        if(Collectionz.isNullOrEmpty(monetaryBalanceList) == false){
            for(MonetaryBalance monetaryBalance : monetaryBalanceList){
                return monetaryBalance;
            }
        }
        return null;
    }

    public Set<Entry<String, MonetaryBalance>> getBalances() {
        return idWiseActiveBalance.entrySet();
    }

    /**
     * Checks for data balance first, if not exist checks for main balance
     *
     * @return
     */
    public boolean isDataBalanceExist() {
        List<MonetaryBalance> dataBalance = serviceWiseBalance.get(PCRFKeyValueConstants.DATA_SERVICE_ID.val);
        if (Collectionz.isNullOrEmpty(dataBalance)==false) {
            for(MonetaryBalance monetaryBalance : dataBalance){
                if(monetaryBalance.isExist()){
                    return true;
                }
            }
        }

        return isMainBalanceExist();
    }

    /**
     * Checks for main balance
     *
     * @return
     */
    public boolean isMainBalanceExist(){
        List<MonetaryBalance> mainBalance = serviceWiseBalance.get(null);
        if (Collectionz.isNullOrEmpty(mainBalance)==false) {
            for(MonetaryBalance monetaryBalance : mainBalance){
                if(monetaryBalance.isExist()){
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks for voice balance first, if not exist checks for main balance
     *
     * @return
     */
    public boolean isServiceBalanceExist(String serviceId) {
        List<MonetaryBalance> voiceBalance = serviceWiseBalance.get(serviceId);
        if (Collectionz.isNullOrEmpty(voiceBalance) == false) {
            for(MonetaryBalance monetaryBalance : voiceBalance){
                if(monetaryBalance.isExist()){
                    return true;
                }
            }
        }

        return isMainBalanceExist();
    }
    
    @Override
    public String toString() {

        IndentingToStringBuilder indentingPrintWriter = new IndentingToStringBuilder();
        indentingPrintWriter.appendHeading("Subscriber Monetary Balance");

        toString(indentingPrintWriter);
        return indentingPrintWriter.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder out) {
        out.incrementIndentation();
        out.appendChildObject(" -- Monetary Balance -- ", idWiseActiveBalance.values());
        out.decrementIndentation();
    }

    public SubscriberMonetaryBalance copy() {
        HashMap<String, List<MonetaryBalance>> serviceWiseBalance = new HashMap<>(this.serviceWiseBalance.size());
        HashMap<String, MonetaryBalance> idWiseActiveBalance = new HashMap<>(this.idWiseActiveBalance.size());
        HashMap<String, MonetaryBalance> idWiseExpiredBalance = new HashMap<>(this.idWiseExpiredBalance.size());
        this.serviceWiseBalance.forEach((service, monetaryBalanceList) -> {

            for (MonetaryBalance balance : monetaryBalanceList) {
                MonetaryBalance monetaryBalanceCopy = balance.copy();

                serviceWiseBalance.putIfAbsent(service, new ArrayList<>());
                serviceWiseBalance.get(service).add(monetaryBalanceCopy);

                idWiseActiveBalance.put(monetaryBalanceCopy.getId(), monetaryBalanceCopy);
            }
        });
        for(MonetaryBalance balance : this.idWiseExpiredBalance.values()){
            MonetaryBalance monetaryBalanceCopy = balance.copy();
            idWiseExpiredBalance.put(monetaryBalanceCopy.getId(), monetaryBalanceCopy);
        }
        return new SubscriberMonetaryBalance(serviceWiseBalance, idWiseActiveBalance, idWiseExpiredBalance, currentTime);
    }

    public MonetaryBalance getBalanceById(String id) {
        MonetaryBalance monetaryBalance = idWiseActiveBalance.get(id);

        if(monetaryBalance == null){
            return idWiseExpiredBalance.get(id);
        } else {
            return monetaryBalance;
        }

    }
    public Collection<MonetaryBalance> getAllBalance(){
        return idWiseActiveBalance.values();
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
