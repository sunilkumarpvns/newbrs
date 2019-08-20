package com.elitecore.corenetvertex.pm.bod;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pd.bod.BoDQosMultiplierData;
import com.elitecore.corenetvertex.pd.bod.BoDServiceMultiplierData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BoDPackageFactory
 * @author Prakashkumar Pala
 * @since 29-Nov-2018
 * This factory class is responsible for creating BoDPackage object which is used for policy reload,
 * based on BoDData with possible validations.
 * @see BoDData
 */
public class BoDPackageFactory {
    private BoDFactory bodFactory;
    private DeploymentMode deploymentMode;

    public BoDPackageFactory(BoDFactory bodFactory, DeploymentMode deploymentMode) {
        this.bodFactory = bodFactory;
        this.deploymentMode = deploymentMode;
    }

    public BoDPackage create(BoDData boDData){
        List<String> failReasons = new ArrayList<>();
        PolicyStatus policyStatus = PolicyStatus.SUCCESS;
        List<String> applicableQosProfiles = new ArrayList<>();
        Map<Integer, BoDQosMultiplier> fupLevelToBoDQosMultipliers = new HashMap<Integer, BoDQosMultiplier>();
        List<String> groupIds = new ArrayList<>();

        /**
         * Check for Deployment Mode
         */
        if (DeploymentMode.OCS == deploymentMode) {
            failReasons.add("BoD package is not supported with deployment mode: " + deploymentMode);
        }

        /**
         * QoS Profile Data
         */
        if(Strings.isNullOrBlank(boDData.getApplicableQosProfiles())==false){
            applicableQosProfiles = CommonConstants.COMMA_SPLITTER.split(boDData.getApplicableQosProfiles());
        }

        /**
         * QoS Multiplier Data
         */
        if(Collectionz.isNullOrEmpty(boDData.getBodQosMultiplierDatas())){
            failReasons.add("No BoD QoS Profile Multiplier Found");
        } else {
            for(BoDQosMultiplierData boDQosMultiplierData : boDData.getBodQosMultiplierDatas()){
                fupLevelToBoDQosMultipliers.put(boDQosMultiplierData.getFupLevel(), createBoDQosMultiplier(boDQosMultiplierData));
            }
        }

        if(failReasons.isEmpty()==false){
            policyStatus = PolicyStatus.FAILURE;
        }

        String failReason = null;
        if(failReasons.isEmpty()==false){
            failReason = "BoD package parsing failed. Reason: "+failReasons.toString();
        }

        if(Strings.isNullOrBlank(boDData.getGroups())==false){
            groupIds = CommonConstants.COMMA_SPLITTER.split(boDData.getGroups());
        }

        return bodFactory.createBodPkg(boDData.getId(), boDData.getName(), boDData.getDescription(), PkgMode.getMode(boDData.getPackageMode())
                , PkgStatus.fromVal(boDData.getStatus()), boDData.getValidityPeriod(), ValidityPeriodUnit.fromName(boDData.getValidityPeriodUnit())
                , applicableQosProfiles,fupLevelToBoDQosMultipliers, groupIds, failReason, policyStatus, boDData.getPrice()
                , boDData.getAvailabilityStartDate(), boDData.getAvailabilityEndDate(), boDData.getParam1(), boDData.getParam2());

    }

    private BoDQosMultiplier createBoDQosMultiplier(BoDQosMultiplierData boDQosMultiplierData){
        BoDQosMultiplier boDQosMultiplier = new BoDQosMultiplier(boDQosMultiplierData.getMultiplier());
        if(Collectionz.isNullOrEmpty(boDQosMultiplierData.getBodServiceMultiplierDatas()) == false){
            for(BoDServiceMultiplierData boDServiceMultiplierData : boDQosMultiplierData.getBodServiceMultiplierDatas()){
                DataServiceTypeData dataServiceType = boDServiceMultiplierData.getServiceTypeData();
                boDQosMultiplier.addDataServiceIdToServiceMultiplier(
                        boDServiceMultiplierData.getServiceTypeData().getServiceIdentifier()
                        , new BoDServiceMultiplier(dataServiceType.getId(),dataServiceType.getName(),
                                boDServiceMultiplierData.getMultiplier(),dataServiceType.getServiceIdentifier())
                );
            }
        }
        return boDQosMultiplier;
    }

}