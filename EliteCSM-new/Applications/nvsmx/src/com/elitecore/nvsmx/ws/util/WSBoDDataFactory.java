package com.elitecore.nvsmx.ws.util;

import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.bod.BoDQosMultiplier;
import com.elitecore.corenetvertex.pm.bod.BoDServiceMultiplier;
import com.elitecore.nvsmx.ws.subscription.data.BoDPackageData;
import com.elitecore.nvsmx.ws.subscription.data.BoDQoSMultiplierData;
import com.elitecore.nvsmx.ws.subscription.data.BoDServiceMultiplierData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WSBoDDataFactory {
    public static BoDPackageData createBoDPackageData(BoDPackage boDPackage){
        return new BoDPackageData(boDPackage.getId(),boDPackage.getName(), boDPackage.getDescription(),
                boDPackage.getPackageMode().val, boDPackage.getValidityPeriod(),boDPackage.getValidityPeriodUnit(),
                createQoSMultipliers(boDPackage.getFupLevelToBoDQosMultipliers()));
    }

    private static List<BoDQoSMultiplierData> createQoSMultipliers(Map<Integer, BoDQosMultiplier> qosMultiplierDatas){
        List<BoDQoSMultiplierData> multipliers = new ArrayList<>();
        for(Map.Entry<Integer, BoDQosMultiplier> multiplierEntry : qosMultiplierDatas.entrySet()){
            BoDQosMultiplier multiplier = multiplierEntry.getValue();
            multipliers.add(new BoDQoSMultiplierData(multiplierEntry.getKey(),multiplier.getSessionMultiplier(),
                    createServiceMultiplierData(multiplier.getDataServiceIdToServiceMultipliers())));
        }
        return multipliers;
    }

    private static List<BoDServiceMultiplierData> createServiceMultiplierData(Map<Long, BoDServiceMultiplier> serviceMultipliers){
        List<BoDServiceMultiplierData> sereviceMultipliers = new ArrayList<>();
        for(Map.Entry<Long, BoDServiceMultiplier> serviceMulEntry : serviceMultipliers.entrySet()){
            BoDServiceMultiplier multiplier= serviceMulEntry.getValue();
            sereviceMultipliers.add(new BoDServiceMultiplierData(multiplier.getDataServiceTypeID(),multiplier.getDataServicetyTypeName(),
                    multiplier.getMultiplier()));
        }
        return sereviceMultipliers;
    }
}