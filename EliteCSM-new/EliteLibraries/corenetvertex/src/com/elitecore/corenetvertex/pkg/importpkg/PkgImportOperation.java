	package com.elitecore.corenetvertex.pkg.importpkg;

    import com.elitecore.commons.base.Strings;
    import com.elitecore.corenetvertex.constants.QuotaProfileType;
    import com.elitecore.corenetvertex.core.imports.ImportOperation;
    import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
    import com.elitecore.corenetvertex.core.validator.GlobalPkgValidations;
    import com.elitecore.corenetvertex.pkg.PkgData;
    import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
    import com.elitecore.corenetvertex.pkg.dataservicetype.ServiceDataFlowData;
    import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
    import com.elitecore.corenetvertex.pkg.notification.UsageNotificationData;
    import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
    import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
    import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
    import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
    import com.elitecore.corenetvertex.pkg.qos.TimePeriodData;
    import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
    import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
    import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
    import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionDetailData;
    import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionRelationData;
    import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
    import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData;
    import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
    import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileDetailData;
    import com.elitecore.corenetvertex.util.SessionProvider;

    import javax.annotation.Nonnull;
    import javax.annotation.Nullable;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    /**
 * Created by Ishani on 24/6/16.
 */
public class PkgImportOperation implements ImportOperation<PkgData, PkgData, PkgData> {


        @Override
    public void importData(@Nonnull PkgData pkgData, @Nullable PkgData parentObject, @Nullable PkgData superObject, @Nonnull SessionProvider session) throws ImportOperationFailedException {
        try {

            PkgData pkgToBeImported = new PkgData();

            importPackageData(pkgToBeImported, pkgData, session);

            //import quota profiles for a given package
            importQuotaProfiles(pkgToBeImported,pkgData,session);

            importRateCards(pkgToBeImported,pkgData,session);
            //import qos profiles for a given package
            importQosProfiles(pkgToBeImported,pkgData,session);

            //import usage notification for a given package
            importUsageNotificationData(pkgToBeImported,pkgData,session);

            //import usage notification for a given package
            importQuotaNotificationData(pkgToBeImported,pkgData,session);


            if(GlobalPkgValidations.isGlobalPlan(pkgData)){
                importGroupPkgOrder(pkgToBeImported,pkgData,session);
            }

        } catch (Exception e) {
            throw new ImportOperationFailedException("Failed to import package: " + pkgData.getName() + ". Reason: " + e.getMessage(), e);
        }
    }

    private void importGroupPkgOrder(PkgData pkgToBeImported,PkgData pkgData,SessionProvider session)  {
        for(PkgGroupOrderData pkgGroupOrderData : pkgData.getPkgGroupWiseOrders()) {
             pkgGroupOrderData.setPkgData(pkgToBeImported);
             session.getSession().persist(pkgGroupOrderData);
        }
    }

    private void importUsageNotificationData(PkgData pkgToBeImported, PkgData pkgData, SessionProvider session) {
        for (UsageNotificationData usageNotificationData : pkgData.getUsageNotificationDatas()) {
            usageNotificationData.setPkgData(pkgToBeImported);
            if (Strings.isNullOrBlank(usageNotificationData.getQuotaProfileId()) == false) {
                QuotaProfileData quotaProfileToBeSet = new QuotaProfileData();
                quotaProfileToBeSet.setId(usageNotificationData.getQuotaProfileId());
                usageNotificationData.setQuotaProfile(quotaProfileToBeSet);
            } else if (Strings.isNullOrBlank(usageNotificationData.getQuotaProfileName()) == false) {
                for (QuotaProfileData quotaProfileData : pkgData.getQuotaProfiles()) {
                    if (usageNotificationData.getQuotaProfileName().equalsIgnoreCase(quotaProfileData.getName())) {
                        usageNotificationData.setQuotaProfile(quotaProfileData);
                        break;
                    }
                }
            }

            if(Strings.isNullOrBlank(usageNotificationData.getId())){
                usageNotificationData.setId(null);
                session.getSession().persist(usageNotificationData);
            }else {
                session.getSession().merge(usageNotificationData);
            }
        }
    }

        private void importQuotaNotificationData(PkgData pkgToBeImported, PkgData pkgData, SessionProvider session) {
            for (QuotaNotificationData quotaNotificationData : pkgData.getQuotaNotificationDatas()) {
                quotaNotificationData.setPkgData(pkgToBeImported);
                if (Strings.isNullOrBlank(quotaNotificationData.getQuotaProfileId()) == false) {
                    RncProfileData rncProfileToBeSet = new RncProfileData();
                    rncProfileToBeSet.setId(quotaNotificationData.getQuotaProfileId());
                    quotaNotificationData.setQuotaProfile(rncProfileToBeSet);
                } else if (Strings.isNullOrBlank(quotaNotificationData.getQuotaProfileName()) == false) {
                    for (RncProfileData rncProfileData : pkgData.getRncProfileDatas()) {
                        if (quotaNotificationData.getQuotaProfileName().equalsIgnoreCase(rncProfileData.getName())) {
                            quotaNotificationData.setQuotaProfile(rncProfileData);
                            break;
                        }
                    }
                }

                if(Strings.isNullOrBlank(quotaNotificationData.getId())){
                    quotaNotificationData.setId(null);
                    session.getSession().persist(quotaNotificationData);
                }else {
                    session.getSession().merge(quotaNotificationData);
                }
            }
        }

    private void importQosProfiles(PkgData pkgToBeImported, PkgData pkgData, SessionProvider session) {
        for (QosProfileData qosProfile : pkgData.getQosProfiles()) {

            setQuotaProfileRelation(qosProfile,pkgData);
            List<QosProfileDetailData> qosProfileDetailList = qosProfile.getQosProfileDetailDataList();
            List<TimePeriodData> timePeriodDatas = qosProfile.getTimePeriodDataList();
            qosProfile.setPkgData(pkgToBeImported);
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
            List<PCCRuleData> globalPCCS = new ArrayList<>();
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

    private void importQuotaProfiles(PkgData pkgToBeImported, PkgData pkgData, SessionProvider session) {
        if (pkgToBeImported.getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {
            importUMBasedQuotaProfiles(pkgToBeImported, pkgData, session);
        } else if (pkgToBeImported.getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED) {
            importSYCounterBaseQuotaProfiles(pkgToBeImported, pkgData, session);
        } else if (QuotaProfileType.RnC_BASED == pkgToBeImported.getQuotaProfileType()) {
            importRnCBasedQuotaProfiles(pkgToBeImported, pkgData, session);
        }
    }

        private void importRnCBasedQuotaProfiles(PkgData pkgToBeImported, PkgData pkgData, SessionProvider session) {
            for (RncProfileData rncProfileData : pkgData.getRncProfileDatas()) {
                rncProfileData.setPkgData(pkgToBeImported);
                List<RncProfileDetailData> rncProfileDetailDataList = rncProfileData.getRncProfileDetailDatas();
                rncProfileData.setRncProfileDetailDatas(null);
                if(Strings.isNullOrBlank(rncProfileData.getId())){
                    rncProfileData.setId(null);
                    session.getSession().persist(rncProfileData);
                }else {

                    session.getSession().merge(rncProfileData);
                }

                for(RncProfileDetailData detailData : rncProfileDetailDataList){
                    detailData.setRncProfileData(rncProfileData);
                    if(Strings.isNullOrBlank(detailData.getId())){
                        detailData.setId(null);
                        session.getSession().persist(detailData);
                    }else{
                        session.getSession().merge(detailData);
                    }
                }
            }
        }

        private void importSYCounterBaseQuotaProfiles(PkgData pkgToBeImported, PkgData pkgData, SessionProvider session) {
            for (SyQuotaProfileData syQuotaProfile : pkgData.getSyQuotaProfileDatas()) {
                syQuotaProfile.setPkgData(pkgToBeImported);
                List<SyQuotaProfileDetailData> syQuotaProfileDetailDatas = syQuotaProfile.getSyQuotaProfileDetailDatas();
                syQuotaProfile.setSyQuotaProfileDetailDatas(null);
                if(Strings.isNullOrBlank(syQuotaProfile.getId())){
                    syQuotaProfile.setId(null);
                    session.getSession().persist(syQuotaProfile);
                }else {

                    session.getSession().merge(syQuotaProfile);
                }

                for(SyQuotaProfileDetailData detailData : syQuotaProfileDetailDatas){
                    detailData.setSyQuotaProfileData(syQuotaProfile);
                    if(Strings.isNullOrBlank(detailData.getId())){
                            detailData.setId(null);
                        session.getSession().persist(detailData);
                    }else{
                        session.getSession().merge(detailData);
                    }
                }
            }
        }

        private void importUMBasedQuotaProfiles(PkgData pkgToBeImported, PkgData pkgData, SessionProvider session) {
            for (QuotaProfileData quotaProfile : pkgData.getQuotaProfiles()) {
                quotaProfile.setPkgData(pkgToBeImported);
                List<QuotaProfileDetailData> quotaProfoileDetails = quotaProfile.getQuotaProfileDetailDatas();
                quotaProfile.setQuotaProfileDetailDatas(null);
                if(Strings.isNullOrBlank(quotaProfile.getId())){
                    quotaProfile.setId(null);
                    session.getSession().persist(quotaProfile);
                }else {

                    session.getSession().merge(quotaProfile);
                }

                for(QuotaProfileDetailData detailData : quotaProfoileDetails){
                    detailData.setQuotaProfile(quotaProfile);
                    if(Strings.isNullOrBlank(detailData.getId())){
                        detailData.setId(null);
                        session.getSession().persist(detailData);
                    }else{
                        session.getSession().merge(detailData);
                    }
                }
            }
        }


        private void importPackageData(PkgData pkgToBeImported, PkgData pkgData,SessionProvider session) {
        pkgToBeImported.setId(pkgData.getId());
        pkgToBeImported.setName(pkgData.getName());
        pkgToBeImported.setQuotaProfileType(pkgData.getQuotaProfileType());
        pkgToBeImported.setDescription(pkgData.getDescription());
        pkgToBeImported.setAvailabilityStartDate(pkgData.getAvailabilityStartDate());
        pkgToBeImported.setAvailabilityEndDate(pkgData.getAvailabilityEndDate());
        pkgToBeImported.setPrice(pkgData.getPrice());
        pkgToBeImported.setExclusiveAddOn(pkgData.isExclusiveAddOn());
        pkgToBeImported.setMultipleSubscription(pkgData.isMultipleSubscription());
        pkgToBeImported.setCreatedByStaff(pkgData.getCreatedByStaff());
        pkgToBeImported.setCreatedDate(pkgData.getCreatedDate());
        pkgToBeImported.setModifiedByStaff(pkgData.getModifiedByStaff());
        pkgToBeImported.setModifiedDate(pkgData.getModifiedDate());
        pkgToBeImported.setType(pkgData.getType());
        pkgToBeImported.setPackageMode(pkgData.getPackageMode());
        pkgToBeImported.setValidityPeriod(pkgData.getValidityPeriod());
        pkgToBeImported.setValidityPeriodUnit(pkgData.getValidityPeriodUnit());
        pkgToBeImported.setGroups(pkgData.getGroups());
        pkgToBeImported.setApplicableTopUps(null);
        pkgToBeImported.setParam1(pkgData.getParam1());
        pkgToBeImported.setParam2(pkgData.getParam2());
        pkgToBeImported.setStatus(pkgData.getStatus());
        pkgToBeImported.setTariffSwitchSupport(pkgData.getTariffSwitchSupport());
        pkgToBeImported.setAlwaysPreferPromotionalQoS(pkgData.getAlwaysPreferPromotionalQoS());
        pkgToBeImported.setCurrency(pkgData.getCurrency());

        if (Strings.isNullOrBlank(pkgToBeImported.getId())) {
            pkgToBeImported.setId(null);
            session.getSession().persist(pkgToBeImported);
        } else {
            session.getSession().merge(pkgToBeImported);
        }
    }

    private void setQuotaProfileRelation(QosProfileData qosProfile,PkgData pkgData) {
        if (QuotaProfileType.USAGE_METERING_BASED == pkgData.getQuotaProfileType()) {
            setUMBasedQuotaProfileInQosProfile(qosProfile, pkgData);
        } else if (QuotaProfileType.SY_COUNTER_BASED == pkgData.getQuotaProfileType()) {
            setSyCounterBasedQuotaProfileInQosProfile(qosProfile, pkgData);
        } else if (QuotaProfileType.RnC_BASED == pkgData.getQuotaProfileType()) {
            setRncBaseQuotaProfileInQosProfile(qosProfile, pkgData);
            setRateCardInQosProfile(qosProfile, pkgData);
        }
    }

        private void setRncBaseQuotaProfileInQosProfile(QosProfileData qosProfile, PkgData pkgData) {
            if (Strings.isNullOrBlank(qosProfile.getQuotaProfileId()) == false) {
                RncProfileData quotaProfileToBeSet = new RncProfileData();
                quotaProfileToBeSet.setId(qosProfile.getQuotaProfileId());
                qosProfile.setRncProfileData(quotaProfileToBeSet);
            } else if (Strings.isNullOrBlank(qosProfile.getQuotaProfileName()) == false) {
                for (RncProfileData rncProfileData : pkgData.getRncProfileDatas()) {
                    if (qosProfile.getQuotaProfileName().equalsIgnoreCase(rncProfileData.getName())) {
                        qosProfile.setRncProfileData(rncProfileData);
                        break;
                    }
                }
            }


        }

        private void setRateCardInQosProfile(QosProfileData qosProfile, PkgData pkgData) {
            if (Strings.isNullOrBlank(qosProfile.getRateCardId()) == false) {
                DataRateCardData dataRateCardDataToBeSet = new DataRateCardData();
                dataRateCardDataToBeSet.setId(qosProfile.getRateCardId());
                qosProfile.setRateCardData(dataRateCardDataToBeSet);
            } else if (Strings.isNullOrBlank(qosProfile.getRateCardName()) == false) {
                Optional<DataRateCardData> rateCardBasedOnName = getRateCardBasedOnName(qosProfile, pkgData);
                if (rateCardBasedOnName.isPresent()) {
                    qosProfile.setRateCardData(rateCardBasedOnName.get());
                }
            }
        }

        private void setSyCounterBasedQuotaProfileInQosProfile(QosProfileData qosProfile, PkgData pkgData) {
            if (Strings.isNullOrBlank(qosProfile.getQuotaProfileId()) == false) {
                SyQuotaProfileData quotaProfileToBeSet = new SyQuotaProfileData();
                quotaProfileToBeSet.setId(qosProfile.getQuotaProfileId());
                qosProfile.setSyQuotaProfileData(quotaProfileToBeSet);
            } else if (Strings.isNullOrBlank(qosProfile.getQuotaProfileName()) == false) {
                for (SyQuotaProfileData syQuotaProfileData : pkgData.getSyQuotaProfileDatas()) {
                    if (qosProfile.getQuotaProfileName().equalsIgnoreCase(syQuotaProfileData.getName())) {
                        qosProfile.setSyQuotaProfileData(syQuotaProfileData);
                        break;
                    }
                }
            }
        }

        private void setUMBasedQuotaProfileInQosProfile(QosProfileData qosProfile, PkgData pkgData) {
            if (Strings.isNullOrBlank(qosProfile.getQuotaProfileId()) == false) {
                QuotaProfileData quotaProfileToBeSet = new QuotaProfileData();
                quotaProfileToBeSet.setId(qosProfile.getQuotaProfileId());
                qosProfile.setQuotaProfile(quotaProfileToBeSet);
            } else if (Strings.isNullOrBlank(qosProfile.getQuotaProfileName()) == false) {
                for (QuotaProfileData quotaProfileData : pkgData.getQuotaProfiles()) {
                    if (qosProfile.getQuotaProfileName().equalsIgnoreCase(quotaProfileData.getName())) {
                        qosProfile.setQuotaProfile(quotaProfileData);
                        break;
                    }
                }
            }
        }

        private Optional<DataRateCardData> getRateCardBasedOnName(QosProfileData qosProfile, PkgData pkgData) {
            return pkgData.getRateCards().stream().filter(dataRateCardData -> dataRateCardData.getName().equalsIgnoreCase(qosProfile.getRateCardName())).findFirst();
        }


        private void importRateCards(PkgData pkgToBeImported, PkgData pkgData, SessionProvider session) {
            for (DataRateCardData dataRateCardData : pkgData.getRateCards()) {
                dataRateCardData.setPkgData(pkgToBeImported);
                List<DataRateCardVersionRelationData> dataRateCardVersionRelations = dataRateCardData.getDataRateCardVersionRelationData();
                dataRateCardData.setDataRateCardVersionRelationData(null);
                if (Strings.isNullOrBlank(dataRateCardData.getId())) {
                    dataRateCardData.setId(null);
                    session.getSession().persist(dataRateCardData);
                } else {

                    session.getSession().merge(dataRateCardData);
                }

                importRateCardVersionRelations(dataRateCardVersionRelations,dataRateCardData,session);
            }

        }

        private void importRateCardVersionRelations(List<DataRateCardVersionRelationData> dataRateCardVersionRelations, DataRateCardData dataRateCardData, SessionProvider session) {
            for(DataRateCardVersionRelationData dataRateCardVersionRelationData :  dataRateCardVersionRelations){
                List<DataRateCardVersionDetailData> dataRateCardVersionDetailDataList = dataRateCardVersionRelationData.getDataRateCardVersionDetailDataList();
                dataRateCardVersionRelationData.setDataRateCardVersionDetailDataList(null);
                dataRateCardVersionRelationData.setDataRateCardData(dataRateCardData);
                if (Strings.isNullOrBlank(dataRateCardVersionRelationData.getId())) {
                    dataRateCardVersionRelationData.setId(null);
                    session.getSession().persist(dataRateCardVersionRelationData);
                } else {
                    session.getSession().merge(dataRateCardVersionRelationData);
                }

                importDataRateCardVersionDetails(dataRateCardVersionDetailDataList, dataRateCardVersionRelationData, session);
            }
        }

        private void importDataRateCardVersionDetails(List<DataRateCardVersionDetailData> dataRateCardVersionDetailDataList, DataRateCardVersionRelationData dataRateCardVersionRelationData, SessionProvider session) {
            for(DataRateCardVersionDetailData dataRateCardVersionDetailData : dataRateCardVersionDetailDataList){
                dataRateCardVersionDetailData.setDataRateCardVersionRelationData(dataRateCardVersionRelationData);
                if (Strings.isNullOrBlank(dataRateCardVersionDetailData.getId())) {
                    dataRateCardVersionDetailData.setId(null);
                    session.getSession().persist(dataRateCardVersionDetailData);
                } else {
                    session.getSession().merge(dataRateCardVersionDetailData);
                }
            }
        }
    }
