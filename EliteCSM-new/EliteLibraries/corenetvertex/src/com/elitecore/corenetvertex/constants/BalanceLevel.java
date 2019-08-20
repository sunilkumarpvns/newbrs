package com.elitecore.corenetvertex.constants;

import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aditya on 7/6/16.
 */
public enum BalanceLevel {

    HSQ("HSQ",0) {
        @Override
        public Map<String, QuotaProfileDetail> getBalanceLevelQuotaProfileDetail(QuotaProfile umBaseQuotaProfile) {
            return umBaseQuotaProfile.getHsqLevelServiceWiseQuotaProfileDetails();
        }
    },
    FUP1("FUP Level1",1) {
        @Override
        public Map<String, QuotaProfileDetail> getBalanceLevelQuotaProfileDetail(QuotaProfile umBaseQuotaProfile) {
            return umBaseQuotaProfile.getAllLevelServiceWiseQuotaProfileDetails().get(1);
        }
    },
    FUP2("FUP Level2",2) {
        @Override
        public Map<String, QuotaProfileDetail> getBalanceLevelQuotaProfileDetail(QuotaProfile umBaseQuotaProfile) {
            return umBaseQuotaProfile.getAllLevelServiceWiseQuotaProfileDetails().get(2);
        }
    };

    public final String displayVal;
    public final int fupLevel;
    protected static final Map<Integer,BalanceLevel> balanceLevelMap = new HashMap<>();

    static{
        for(BalanceLevel balanceLevel : BalanceLevel.values()){
            balanceLevelMap.put(balanceLevel.fupLevel, balanceLevel);
        }
    }

    BalanceLevel(String displayVal,int fupLevel){
        this.displayVal = displayVal;
        this.fupLevel = fupLevel;
    }

    public String getDisplayVal(){
        return displayVal;
    }

    public int getFupLevel(){
        return fupLevel;
    }

    public abstract  Map<String, QuotaProfileDetail> getBalanceLevelQuotaProfileDetail(QuotaProfile umBaseQuotaProfile);

    public static BalanceLevel fromName(String name){
        if(HSQ.name().equals(name)) {
            return HSQ;
        } else if(FUP1.name().equals(name)) {
            return FUP1;
        } else if(FUP2.name().equals(name)) {
            return FUP2;
        }
        return null;
    }


    public static BalanceLevel fromVal(Integer fupLevel) {
        return balanceLevelMap.get(fupLevel);
    }
}
