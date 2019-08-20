package com.elitecore.corenetvertex.pm.factory;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;

import java.util.Collections;

@Deprecated
public class BoDDataFactory {

    private String id;
    private String name;
    private String status;
    private String packageMode;
    private String groups;
    private String validityPeriod;
    private String validityPeriodUnit;

    public BoDDataFactory(){

    }

    public BoDDataFactory withId(String id){
        this.id = id;
        return this;
    }

    public BoDDataFactory withGroups(String groups){
        this.groups = groups;
        return this;
    }

    public BoDDataFactory withName(String name){
        this.name = name;
        return this;
    }

    public BoDDataFactory withStatus(String status){
        this.status = status;
        return this;
    }

    public BoDDataFactory withMode(String mode){
        this.packageMode = mode;
        return this;
    }

    public BoDDataFactory withValidityPeriod(String validityPeriod){
        this.validityPeriod = validityPeriod;
        return this;
    }

    public BoDDataFactory withValidityPeriodUnit(String validityPeriodUnit){
        this.validityPeriodUnit = validityPeriodUnit;
        return this;
    }


    public BoDData build(){
        BoDData bodData = new BoDData();
        bodData.setId(id);
        bodData.setName(name);
        bodData.setPackageMode(packageMode);
        bodData.setStatus(status);
        bodData.setGroups(groups);
        bodData.setValidityPeriod(Integer.valueOf(validityPeriod));
        bodData.setValidityPeriodUnit(validityPeriodUnit);
        return bodData;
    }

    public static BoDPackage createBoDPkg(BoDData bodData, PolicyStatus policyStatus){
        return new BoDPackage(bodData.getId(),
                bodData.getName(),
                bodData.getDescription(),
                PkgMode.valueOf(bodData.getPackageMode()),
                bodData.getStatus()==null? PkgStatus.ACTIVE:PkgStatus.fromVal(bodData.getStatus()),
                bodData.getValidityPeriod(),
                ValidityPeriodUnit.fromName(bodData.getValidityPeriodUnit()),
                Collections.emptyList(),
                Collections.emptyMap(),
                CommonConstants.COMMA_SPLITTER.split(bodData.getGroups()),
                null,
                policyStatus, bodData.getPrice(),
                bodData.getAvailabilityStartDate(),
                bodData.getAvailabilityEndDate(),
                bodData.getParam1(),
                bodData.getParam2()
        );
    }

}