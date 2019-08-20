package com.elitecore.corenetvertex.pkg.importemergencypkg;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.core.imports.ImportOperation;
import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.dataservicetype.ServiceDataFlowData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.qos.TimePeriodData;
import com.elitecore.corenetvertex.util.SessionProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ishani on 30/11/16.
 */
public class EmergencyPkgImportOperation implements ImportOperation<EmergencyPkgDataExt,EmergencyPkgDataExt,EmergencyPkgDataExt> {
    @Override
    public void importData(@Nonnull EmergencyPkgDataExt emergencyPkgDataExt, @Nullable EmergencyPkgDataExt emergencyPkgData, @Nullable EmergencyPkgDataExt superObject, @Nonnull SessionProvider session) throws ImportOperationFailedException {
        try {

            PkgData pkgDataImported = new PkgData();
            importEmergencyPackageData(pkgDataImported, emergencyPkgDataExt,session);


            //import qos profiles for a given package
            importQosProfiles(pkgDataImported,emergencyPkgDataExt,session);

            importGroupPkgOrder(pkgDataImported,emergencyPkgDataExt,session);

        }catch (Exception e) {
            throw new ImportOperationFailedException("Failed to import emergency package: " + emergencyPkgDataExt.getName() + ". Reason: " + e.getMessage(), e);
        }
    }

    private void importQosProfiles(PkgData pkgData, EmergencyPkgDataExt emergencyPkgData,SessionProvider session) {

        for (QosProfileData qosProfile : emergencyPkgData.getQosProfiles()) {

            List<QosProfileDetailData> qosProfileDetailList = qosProfile.getQosProfileDetailDataList();
            List<TimePeriodData> timePeriodDatas = qosProfile.getTimePeriodDataList();
            qosProfile.setPkgData(pkgData);
            qosProfile.setQosProfileDetailDataList(null);
            qosProfile.setTimePeriodDataList(null);

            if(Strings.isNullOrBlank(qosProfile.getId())){
                qosProfile.setId(null);
                session.getSession().persist(qosProfile);
            }else {
                session.getSession().merge(qosProfile);
            }
            for(TimePeriodData timePeriod : timePeriodDatas){
                if(Strings.isNullOrBlank(timePeriod.getDom()) && Strings.isNullOrBlank(timePeriod.getMoy()) && Strings.isNullOrBlank(timePeriod.getDow()) && Strings.isNullOrBlank(timePeriod.getTimePeriod())){
                    continue;
                }
                timePeriod.setQosProfile(qosProfile);
                if(Strings.isNullOrBlank(timePeriod.getId())){
                    timePeriod.setId(null);
                    session.getSession().persist(timePeriod);
                }else{
                    session.getSession().merge(timePeriod);
                }
            }

            importQoSProfileDetails(qosProfileDetailList, qosProfile, session);

        }
    }


    private void importQoSProfileDetails(List<QosProfileDetailData> qosProfileDetailList, QosProfileData qosProfile, SessionProvider session) {
        for (QosProfileDetailData detail : qosProfileDetailList) {
            detail.setQosProfile(qosProfile);
            List<PCCRuleData> pccRuleList = detail.getPccRules();
            List<PCCRuleData> globalPCCS = new ArrayList<PCCRuleData>();
            for(PCCRuleData pccData : pccRuleList){
                if(PCCRuleScope.GLOBAL == pccData.getScope()){
                    globalPCCS.add(pccData);
                }
            }
            detail.setPccRules(globalPCCS);
            if (Strings.isNullOrBlank(detail.getId())) {
                detail.setId(null);
                session.getSession().persist(detail);

            } else {
                session.getSession().merge(detail);
            }
            importPCCRule(pccRuleList, detail, session);
        }
    }

    private void importPCCRule(List<PCCRuleData> pccRuleList, QosProfileDetailData detail, SessionProvider session) {
        for (PCCRuleData pcc : pccRuleList) {
            if (PCCRuleScope.GLOBAL == pcc.getScope()) {
                continue;
            }
            pcc.getQosProfileDetails().add(detail);
            List<ServiceDataFlowData> serviceDataFlowDatas = pcc.getServiceDataFlowList();
            pcc.setServiceDataFlowList(null);
            if (Strings.isNullOrBlank(pcc.getId())) {
                pcc.setId(null);
                session.getSession().persist(pcc);
            } else {
                session.getSession().merge(pcc);
            }

            for(ServiceDataFlowData sdf : serviceDataFlowDatas){
                sdf.setPccRule(pcc);
                if(Strings.isNullOrBlank(sdf.getServiceDataFlowId())){
                    sdf.setServiceDataFlowId(null);
                    session.getSession().persist(sdf);
                }else{
                    session.getSession().merge(sdf);
                }
            }
        }
    }

    private void importEmergencyPackageData(PkgData pkgData, EmergencyPkgDataExt emergencyPkgData,SessionProvider session) {
        pkgData.setId(emergencyPkgData.getId());
        pkgData.setName(emergencyPkgData.getName());
        pkgData.setDescription(emergencyPkgData.getDescription());
        pkgData.setAvailabilityStartDate(emergencyPkgData.getAvailabilityStartDate());
        pkgData.setAvailabilityEndDate(emergencyPkgData.getAvailabilityEndDate());
        pkgData.setCreatedByStaff(emergencyPkgData.getCreatedByStaff());
        pkgData.setCreatedDate(emergencyPkgData.getCreatedDate());
        pkgData.setModifiedByStaff(emergencyPkgData.getModifiedByStaff());
        pkgData.setModifiedDate(emergencyPkgData.getModifiedDate());
        pkgData.setType(emergencyPkgData.getType());
        pkgData.setPackageMode(emergencyPkgData.getPackageMode());
        pkgData.setGroups(emergencyPkgData.getGroups());
        pkgData.setParam1(emergencyPkgData.getParam1());
        pkgData.setParam2(emergencyPkgData.getParam2());
        pkgData.setStatus(emergencyPkgData.getStatus());
        pkgData.setOrderNumber(emergencyPkgData.getOrderNumber());


        if (Strings.isNullOrBlank(pkgData.getId())) {
            pkgData.setId(null);
            session.getSession().persist(pkgData);
        } else {
            session.getSession().merge(pkgData);
        }
    }

    private void importGroupPkgOrder(PkgData pkgToBeImported,EmergencyPkgDataExt pkgData,SessionProvider session) throws Exception {
        for(PkgGroupOrderData pkgGroupOrderData : pkgData.getPkgGroupWiseOrders()) {
            pkgGroupOrderData.setPkgData(pkgToBeImported);
            session.getSession().persist(pkgGroupOrderData);
        }
    }
}
