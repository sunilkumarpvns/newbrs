package com.elitecore.corenetvertex.pm.bod;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pd.bod.BoDQosMultiplierData;
import com.elitecore.corenetvertex.pd.bod.BoDServiceMultiplierData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BodDataDummyBuilder {
    public static BoDData createEmptyBoDData(String appender){
        BoDData boDData = new BoDData();
        boDData.setId("Test-BoD-Pkg-ID-"+appender);
        boDData.setName("Test BoD Package-"+appender);
        boDData.setDescription("Test BoD Description.");
        boDData.setPackageMode(PkgMode.TEST.val);
        boDData.setValidityPeriod(30);
        boDData.setValidityPeriodUnit(ValidityPeriodUnit.DAY.displayValue);
        boDData.setGroups("GROUP_1");

        return boDData;
    }

    public static BoDData createBoDDataWithQoSProfilesOnly(String appender){
        return addQoSProfilesToBoDData(createEmptyBoDData(appender));
    }

    public static BoDData createBoDDataWithQoSMultipliersOnly(String appender){
        return addBoDQosMultiplierDataToBoDData(createBoDDataWithQoSProfilesOnly(appender), appender);
    }

    public static BoDData createBoDDataWithAllValues(String appender){
        return addBoDServiceMultiplierData(addBoDQosMultiplierDataToBoDData(createBoDDataWithQoSProfilesOnly(appender), appender));
    }


    public static BoDData addQoSProfilesToBoDData(BoDData boDData){
        boDData.setApplicableQosProfiles("Dummy_PCC_Profile_1, Dummy_PCC_Profile_2");
        return boDData;
    }

    public static BoDData addBoDQosMultiplierDataToBoDData(BoDData boDData, String appender){
        BoDQosMultiplierData boDQosMultiplierDataHSQ = new BoDQosMultiplierData();
        boDQosMultiplierDataHSQ.setId("Test-Bod-QoS-Multiplier-HSQ-"+appender);
        boDQosMultiplierDataHSQ.setBodPackageId(boDData.getId());
        boDQosMultiplierDataHSQ.setBodData(boDData);
        boDQosMultiplierDataHSQ.setFupLevel(0);
        boDQosMultiplierDataHSQ.setMultiplier(1.5D);
        boDQosMultiplierDataHSQ.setStatus(PkgStatus.ACTIVE.val);

        List<BoDQosMultiplierData> boDQosMultiplierDatas = Collectionz.newArrayList();
        boDQosMultiplierDatas.add(boDQosMultiplierDataHSQ);

        boDData.setBodQosMultiplierDatas(boDQosMultiplierDatas);
        return boDData;
    }


    public static BoDData addBoDServiceMultiplierData(BoDData boDData){
        BoDQosMultiplierData boDQosMultiplierDataHSQ = boDData.getBodQosMultiplierDatas().get(0);

        BoDServiceMultiplierData boDServiceMultiplierDataHSQ = new BoDServiceMultiplierData();
        boDServiceMultiplierDataHSQ.setBodQosMultiplierData(boDQosMultiplierDataHSQ);
        boDServiceMultiplierDataHSQ.setMultiplier(1.25D);

        DataServiceTypeData dataServiceTypeData = new DataServiceTypeData();
        dataServiceTypeData.setServiceIdentifier(1L);
        boDServiceMultiplierDataHSQ.setServiceTypeData(dataServiceTypeData);

        boDQosMultiplierDataHSQ.setBodServiceMultiplierDatas(Stream.of(boDServiceMultiplierDataHSQ).collect(Collectors.toList()));

        boDData.getBodQosMultiplierDatas().set(0, boDQosMultiplierDataHSQ);
        return boDData;
    }
}
