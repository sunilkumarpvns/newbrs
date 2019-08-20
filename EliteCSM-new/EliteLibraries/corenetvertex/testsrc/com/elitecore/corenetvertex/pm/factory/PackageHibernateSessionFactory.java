package com.elitecore.corenetvertex.pm.factory;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.core.hibernate.HibernateSessionFactory;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.pd.sliceconfig.SliceConfigData;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.pkg.ims.MediaTypeData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.sm.ResourceData;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.internal.ThreadLocalSessionContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class will provide functionality of performing create operation on package and its sub-hierarchy
 * Created by Ishani on 2/5/17.
 */
public class PackageHibernateSessionFactory {
    private static final String MODULE = "PKG-HIBERNATE-SESS-FACTORY";
    private HibernateSessionFactory hibernateSessionFactory;

    public PackageHibernateSessionFactory(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    public static PackageHibernateSessionFactory  create(String configFile) {
        Configuration cfg = new Configuration();
        cfg.configure(configFile);
        HibernateConfigurationUtil.setConfigurationClasses(cfg);
        return new PackageHibernateSessionFactory(HibernateSessionFactory.create(cfg));
    }

    public Session getSession() {
        return hibernateSessionFactory.getSession();
    }

    public SessionFactory getSessionFactory() {
        return hibernateSessionFactory.getSessionFactory();
    }

    public void saveAndCommit(List<PkgGroupOrderData> pkgGroupOrderDatas) {

        for (PkgGroupOrderData pkgGroupOrderData : pkgGroupOrderDatas) {
            saveAndCommit(pkgGroupOrderData);
        }
    }

    public void saveAndCommit(Object object) {
        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();
        newSession.save(object);
        syncWithDB(newSession);
        newSession.getTransaction().commit();
        newSession.close();
    }

    public void saveAndCommit(IMSPkgData imsPkgData) {
        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();

        for(MediaTypeData mediaTypeData : findMediaTypeData(imsPkgData)){

            if(mediaTypeData.getId() != null) {
                MediaTypeData alreadyExistMediaTypeData = load(newSession, mediaTypeData.getId(), MediaTypeData.class);
                if(alreadyExistMediaTypeData != null) {
                    newSession.merge(mediaTypeData);
                } else {
                    newSession.save(mediaTypeData);
                }
            } else {
                newSession.save(mediaTypeData);
            }

            syncWithDB(newSession);
        }

        if(imsPkgData.getId() != null) {
            IMSPkgData alreadyExistIMSPkgData = load(newSession, imsPkgData.getId(), IMSPkgData.class);
            if(alreadyExistIMSPkgData != null) {
                newSession.merge(imsPkgData);
            } else {
                newSession.save(imsPkgData);
            }
        } else {
            newSession.save(imsPkgData);
        }

        syncWithDB(newSession);

        newSession.getTransaction().commit();
        newSession.close();
    }

    public void saveAndCommit(DataTopUpData dataTopUpData) {
        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();

        /*for(MediaTypeData mediaTypeData : findMediaTypeData(imsPkgData)){

            if(mediaTypeData.getId() != null) {
                MediaTypeData alreadyExistMediaTypeData = load(newSession, mediaTypeData.getId(), MediaTypeData.class);
                if(alreadyExistMediaTypeData != null) {
                    newSession.merge(mediaTypeData);
                } else {
                    newSession.save(mediaTypeData);
                }
            } else {
                newSession.save(mediaTypeData);
            }

            syncWithDB(newSession);
        }*/

        if(dataTopUpData.getId() != null) {
            DataTopUpData alreadyExistDataTopUpData = load(newSession, dataTopUpData.getId(), DataTopUpData.class);
            if(alreadyExistDataTopUpData != null) {
                newSession.merge(dataTopUpData);
            } else {
                newSession.save(dataTopUpData);
            }
        } else {
            newSession.save(dataTopUpData);
        }

        syncWithDB(newSession);

        newSession.getTransaction().commit();
        newSession.close();
    }

    private List<MediaTypeData> findMediaTypeData(IMSPkgData imsPkgData) {

        ArrayList<MediaTypeData> mediaTypeDatas = new ArrayList<MediaTypeData>();

        for(IMSPkgServiceData serviceData : imsPkgData.getImsPkgServiceDatas()) {
            mediaTypeDatas.add(serviceData.getMediaTypeData());
        }

        return mediaTypeDatas;
    }

    public void updateAndCommit(Object object) {
        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();

        newSession.update(object);
        syncWithDB(newSession);

        newSession.getTransaction().commit();
        newSession.close();
    }

    public void saveAndCommit(PkgData pkgData){

        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();

        Collection<RatingGroupData> ratingGroups = getRatingGroups(pkgData);
        for (RatingGroupData ratingGroup : ratingGroups) {
            RatingGroupData alreadyExistRatingGroup = load(newSession, ratingGroup.getId(), RatingGroupData.class);
            if (alreadyExistRatingGroup != null) {
                newSession.merge(ratingGroup);
            } else {
                newSession.save(ratingGroup);
            }

            syncWithDB(newSession);
        }

        for (DataServiceTypeData dataServiceTypeData : findServiceTypes(pkgData)) {

            if (dataServiceTypeData.getId() != null) {
                DataServiceTypeData alreadyExistServiceType = load(newSession, dataServiceTypeData.getId(), DataServiceTypeData.class);
                if (alreadyExistServiceType != null) {
                    newSession.merge(dataServiceTypeData);
                } else {
                    newSession.save(dataServiceTypeData);
                }
            } else {
                newSession.save(dataServiceTypeData);
            }

            syncWithDB(newSession);
        }

        for (PCCRuleData pccRuleData : findPCCRules(pkgData)) {

            if (pccRuleData.getId() != null) {
                PCCRuleData alreadyExistPCCRule = load(newSession, pccRuleData.getId(), PCCRuleData.class);
                if (alreadyExistPCCRule != null) {
                    newSession.merge(pccRuleData);
                } else {
                    newSession.save(pccRuleData);
                }
            } else {
                newSession.save(pccRuleData);
            }

            syncWithDB(newSession);
        }

        for (ChargingRuleBaseNameData chargingRuleBaseName : findCRBN(pkgData)) {

            if (chargingRuleBaseName.getId() != null) {
                ChargingRuleBaseNameData alreadyExistChargingRuleBaseName = load(newSession, chargingRuleBaseName.getId(), ChargingRuleBaseNameData.class);
                if (alreadyExistChargingRuleBaseName != null) {
                    newSession.merge(chargingRuleBaseName);
                } else {
                    newSession.save(chargingRuleBaseName);
                }
            } else {
                newSession.save(chargingRuleBaseName);
            }

            syncWithDB(newSession);
        }

        if(pkgData.getId() != null) {
            PkgData alreadyExistPCCRule = load(newSession, pkgData.getId(), PkgData.class);
            if(alreadyExistPCCRule != null) {
                newSession.merge(pkgData);
            } else {
                newSession.save(pkgData);
            }
        } else {
            newSession.save(pkgData);
        }

        syncWithDB(newSession);

        newSession.getTransaction().commit();
        newSession.close();
    }

    public void saveAndCommit(ServiceData serviceData){
        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();
        newSession.save(serviceData);
        syncWithDB(newSession);
        newSession.getTransaction().commit();
        newSession.close();
    }

    public void saveListAndCommit(List<? extends ResourceData> resources){
        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();
        for(Object resource:resources){
            newSession.save(resource);
        }
        syncWithDB(newSession);
        newSession.getTransaction().commit();
        newSession.close();
    }

    public void updateListAndCommit(List<? extends ResourceData> resources){
        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();
        for(Object resource:resources){
            newSession.update(resource);
        }
        syncWithDB(newSession);
        newSession.getTransaction().commit();
        newSession.close();
    }

    public void deleteListAndCommit(List<? extends ResourceData> resources){
        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();
        for(Object resource:resources){
            newSession.delete(resource);
        }
        syncWithDB(newSession);
        newSession.getTransaction().commit();
        newSession.close();
    }

    private List<ChargingRuleBaseNameData> findCRBN(PkgData pkgData) {
        List<ChargingRuleBaseNameData> crbns = new ArrayList<>();
        for (QosProfileData qosProfileData : pkgData.getQosProfiles()) {
            List<QosProfileDetailData> qosProfileDetailDataList = qosProfileData.getQosProfileDetailDataList();
            if(Collectionz.isNullOrEmpty(qosProfileDetailDataList) == false) {
                for (QosProfileDetailData qosProfileDetail : qosProfileDetailDataList) {
                    if (Collectionz.isNullOrEmpty(qosProfileDetail.getChargingRuleBaseNames()) == false) {
                        crbns.addAll(qosProfileDetail.getChargingRuleBaseNames());
                    }
                }
            }
            return crbns;
        }

        return crbns;
    }


    private void syncWithDB(Session newSession) {
        newSession.flush();
        newSession.clear();
    }

    private <T> T load(Session newSession, String id, Class<T> object) {
        return (T) newSession.get(object,id);
    }

    public void save(RatingGroupData ratingGroupData){

        LogManager.getLogger().debug(MODULE, "Creating rating group");
        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();
        newSession.save(ratingGroupData);
        newSession.getTransaction().commit();
        newSession.close();
        LogManager.getLogger().debug(MODULE, "Rating group creation completed");
    }

    private Collection<PCCRuleData> findPCCRules(PkgData pkgData) {
        List<PCCRuleData> pccRuleDatas = new ArrayList<PCCRuleData>();
        for (QosProfileData qosProfileData : pkgData.getQosProfiles()) {
            pccRuleDatas.addAll(getPCCRuleDataForQosProfileDetail(qosProfileData));
        }

        return pccRuleDatas;
    }

    private Collection<DataServiceTypeData> findServiceTypes(PkgData pkgData) {
        List<DataServiceTypeData> dataServiceTypeDatas = new ArrayList<DataServiceTypeData>();

        for(PCCRuleData pccRuleData : findPCCRules(pkgData)) {
            DataServiceTypeData serviceTypesForPCC = pccRuleData.getDataServiceTypeData();
            if (serviceTypesForPCC != null){
                dataServiceTypeDatas.add(serviceTypesForPCC);
            }
        }

        for (QuotaProfileData quotaProfileData : pkgData.getQuotaProfiles()) {
            List<QuotaProfileDetailData> quotaProfileDetailDataList = quotaProfileData.getQuotaProfileDetailDatas();
            if(Collectionz.isNullOrEmpty(quotaProfileDetailDataList) == false){
                for(QuotaProfileDetailData quotaProfileDetail : quotaProfileDetailDataList){
                    DataServiceTypeData dataServiceTypeData = quotaProfileDetail.getDataServiceTypeData();
                    if (dataServiceTypeData != null){
                        dataServiceTypeDatas.add(dataServiceTypeData);
                    }
                }
            }
        }
        return dataServiceTypeDatas;
    }

    private Collection<RatingGroupData> getRatingGroups(PkgData pkgData) {
        List<RatingGroupData> ratingGroupDatas = new ArrayList<RatingGroupData>();
        for(DataServiceTypeData dataServiceTypeData : findServiceTypes(pkgData)) {

            List<RatingGroupData> ratingGroups = dataServiceTypeData.getRatingGroupDatas();

            if(Collectionz.isNullOrEmpty(ratingGroups)  == false) {
                ratingGroupDatas.addAll(ratingGroups);
            }
        }
        return  ratingGroupDatas;
    }

    private Collection<PCCRuleData> getPCCRuleDataForQosProfileDetail(QosProfileData qosProfileData) {
        List<PCCRuleData> pccRuleDatas = new ArrayList<PCCRuleData>();
        List<QosProfileDetailData> qosProfileDetailDataList = qosProfileData.getQosProfileDetailDataList();
        if(Collectionz.isNullOrEmpty(qosProfileDetailDataList) == false) {
            for (QosProfileDetailData qosProfileDetail : qosProfileDetailDataList) {
                if (Collectionz.isNullOrEmpty(qosProfileDetail.getPccRules()) == false) {
                    pccRuleDatas.addAll(qosProfileDetail.getPccRules());
                }
            }
        }
        return pccRuleDatas;
    }

    public void shutdown() {
        try {
            if(getSession().isOpen() && getSession().getTransaction().isActive()) {
                getSession().getTransaction().rollback();
                getSession().close();
            }
        } finally {
            hibernateSessionFactory.shutdown();
            ThreadLocalSessionContext.unbind(hibernateSessionFactory.getSessionFactory());
        }


    }

    public void saveAndCommitPkgData(PkgData... pkgDatas) {

        for (PkgData data : pkgDatas) {
            saveAndCommit(data);
        }
    }

    public void updateAndCommit(List<PkgGroupOrderData> pkgGroupOrderDatas) {
        for (PkgGroupOrderData pkgGroupOrderData : pkgGroupOrderDatas) {
            updateAndCommit(pkgGroupOrderData);
        }
    }

    public void deleteAndCommit(Object object) {

        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();
        newSession.delete(object);
        syncWithDB(newSession);
        newSession.getTransaction().commit();
        newSession.close();
    }

    public void mergeListAndCommit(List<? extends ResourceData> resources){
        Session newSession = hibernateSessionFactory.getNewSession();
        newSession.beginTransaction();
        for(Object resource:resources){
            newSession.merge(resource);
        }
        syncWithDB(newSession);
        newSession.getTransaction().commit();
        newSession.close();
    }
}
