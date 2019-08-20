package com.elitecore.netvertex.pm.quota;

import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QuotaReservation {

    private Map<Long, MSCC> rgWiseMSCC;


    public QuotaReservation() {
        this.rgWiseMSCC = new HashMap<>();
    }

    public void put(MSCC mscc){
        rgWiseMSCC.put(mscc.getRatingGroup(), mscc);
    }

    public MSCC get(Long rgId){
        return rgWiseMSCC.get(rgId);
    }

    public MSCC remove(Long rgId) {
        return rgWiseMSCC.remove(rgId);
    }

    public Set<Map.Entry<Long, MSCC>> get(){
        return rgWiseMSCC.entrySet();
    }

    public String getAsJson() {

        return GsonFactory.defaultInstance().toJson(this);
    }

    public boolean isReservationExist() {
        for(MSCC mscc : rgWiseMSCC.values()){
            GyServiceUnits grangedServiceUnits = mscc.getGrantedServiceUnits();
            if (grangedServiceUnits == null) {
                continue;
            }

            if (mscc.getGrantedServiceUnits().isNonMonetaryReservationRequired() || mscc.getGrantedServiceUnits().getReservedMonetaryBalance() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isMonetaryBalanceReserved(){

        if(rgWiseMSCC.isEmpty()){
            return true;
        }

        for(MSCC mscc : rgWiseMSCC.values()){
            GyServiceUnits grangedServiceUnits = mscc.getGrantedServiceUnits();

            if (grangedServiceUnits == null) {
                continue;
            }

            if(mscc.getGrantedServiceUnits().isMonetaryBalanceReserved()){
                return true;
            }
        }

        return false;
    }

    public boolean isNonMonetaryBalanceReserved() {
        if(rgWiseMSCC.isEmpty()){
            return true;
        }

        for(MSCC mscc : rgWiseMSCC.values()){
            GyServiceUnits grangedServiceUnits = mscc.getGrantedServiceUnits();

            if (grangedServiceUnits == null) {
                continue;
            }

            if(mscc.getGrantedServiceUnits().isNonMonetaryReservationRequired()){
                return true;
            }
        }

        return false;
    }



    @Override
    public String toString() {
        IndentingToStringBuilder stringBuilder = new IndentingToStringBuilder();
        stringBuilder.appendHeading("Quota Reservation");
        stringBuilder.incrementIndentation();
        toString(stringBuilder);
        return stringBuilder.toString();

    }

    public void toString(IndentingToStringBuilder stringBuilder) {
        rgWiseMSCC.entrySet().forEach(entry -> stringBuilder.append(entry.getKey().toString(), entry.getValue()));
    }

    public QuotaReservation copy() {
        QuotaReservation quotaReservation = new QuotaReservation();
        rgWiseMSCC.forEach((rgId, mscc) -> quotaReservation.put(mscc.copy()));
        return quotaReservation;
    }
}
