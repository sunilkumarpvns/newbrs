package com.elitecore.corenetvertex.util.commons;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.core.validator.GlobalPkgValidations;
import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.util.SessionProvider;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Map;

/**
 * Created by Ishani on 27/6/16.
 */
public class ImportExportCRUDOperationUtil {

    public static final String STATUS_PROPERTY = "status";
    public static final String TBLM_QOS_PROFILE = " TBLM_QOS_PROFILE ";
    public static final String TBLM_QOS_PROFILE_DETAIL = " TBLM_QOS_PROFILE_DETAIL ";
    public static final String TBLM_QOS_PROFILE_PCC_RELATION = " TBLM_QOS_PROFILE_PCC_RELATION ";
    public static final String TBLM_QOS_PROFILE_CRBN_RELATION = " TBLM_QOS_PROFILE_CRBN_REL ";
    public static final String TBLM_TIME_PERIOD = " TBLM_TIME_PERIOD ";
    public static final String TBLM_SERVICE_DATA_FLOW = " TBLM_SERVICE_DATA_FLOW ";
    public static final String TBLM_DATA_SERVICE_TYPE = " TBLM_DATA_SERVICE_TYPE ";
    public static final String TBLM_PCC_RULE = " TBLM_PCC_RULE ";
    public static final String TBLM_PACKAGE = " TBLM_PACKAGE ";
    public static final String TBLM_QUOTA_PROFILE_DETAIL = " TBLM_QUOTA_PROFILE_DETAIL ";
    public static final String TBLM_USAGE_NOTIFICATION = " TBLM_USAGE_NOTIFICATION ";
    public static final String TBLM_QUOTA_PROFILE = " TBLM_QUOTA_PROFILE ";
    public static final String TBLM_SY_QUOTA_PROFILE = " TBLM_SY_QUOTA_PROFILE ";
    public static final String TBLM_SY_QUOTA_PROFILE_DETAIL = " TBLM_SY_QUOTA_PROFILE_DETAIL ";
    public static final String PACKAGE_ID = "PACKAGE_ID";
    public static final String QUOTA_PROFILE_ID = "QUOTA_PROFILE_ID";
    public static final String ID = "ID";
    public static final String QOS_PROFILE_ID = "QOS_PROFILE_ID";
    public static final String PCC_RULE_ID = "PCC_RULE_ID";
    public static final String QOS_PROFILE_DETAIL_ID = "QOS_PROFILE_DETAIL_ID";
    public static final String SY_QUOTA_PROFILE_ID = "SY_QUOTA_PROFILE_ID";
    public static final String TBLM_IMS_PACKAGE = "TBLM_IMS_PACKAGE";
    public static final String TBLM_IMS_PACKAGE_SERVICE ="TBLM_IMS_PACKAGE_SERVICE";
    public static final String TBLM_IMS_PACKAGE_PCC_ATTIBUTE ="TBLM_IMS_PACKAGE_PCC_ATTRIBUTE";
    public static final String IMS_PACKAGE_ID = "IMS_PACKAGE_ID";
    public static final String IMS_PACKAGE_SERVICE_ID = "IMS_PACKAGE_SERVICE_ID";
    public static final String DATA_SERVICE_TYPE_ID = "DATA_SERVICE_TYPE_ID";
    public static final String TBLM_DEF_SERVICE_DATA_FLOW = "TBLM_DEF_SERVICE_DATA_FLOW ";
    public static final String TBLM_DATA_SRV_RG_REL = "TBLM_DATA_SRV_RG_REL ";

    public static final String CHARGING_RULE_BASE_NAME_ID = "CHARGING_RULE_BASE_NAME_ID";
    public static final String TBLM_CHARGING_RULE_BASE_NAME = "TBLM_CHARGING_RULE_BASE_NAME";
    public static final String TBLM_CHRG_RULE_DATA_SRV_TYPE = "TBLM_CHRG_RULE_DATA_SRV_TYPE";
    public static final String MODE_UPDATE = "UPDATE";
    public static final String MODE_CREATE = "CREATE";
    private static final String TBLM_PKG_GROUP_ORDER = "TBLM_PKG_GROUP_ORDER" ;
    private static final String PKG_ID = "PKG_ID";
    public static final String ID_PROPERTY = "id";
    private static final String ENTITY_ID = "id";
    public static final String TBLM_RNC_QUOTA_PROFILE = " TBLM_RNC_QUOTA_PROFILE";
    public static final String TBLM_RNC_QUOTA_PROFILE_DETAIL = " TBLM_RNC_QUOTA_PROFILE_DETAIL";
    public static final String TBLM_QUOTA_NOTIFICATION = " TBLM_QUOTA_NOTIFICATION";
    private static final String TBLM_DATA_RATE_CARD = " TBLM_DATA_RATE_CARD ";
    private static final String TBLM_DATA_RATE_CARD_VRSN_REL = " TBLM_DATA_RATE_CARD_VRSN_REL ";


    /**
     * This method is used to get entity by id
     * @param type
     * @param id
     * @return entity of type
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type,String id,SessionProvider sessionProvider) throws Exception  {
        Criteria criteria = sessionProvider.getSession().createCriteria(type);
        criteria.add(Restrictions.eq("id", id));
        List<T> lst = criteria.list();
        if(Collectionz.isNullOrEmpty(lst) == false){
            return lst.get(0);
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getNotDeleted(Class<T> type,String id,SessionProvider sessionProvider) throws Exception  {
        Criteria criteria = sessionProvider.getSession().createCriteria(type);
        criteria.add(Restrictions.eq(ID_PROPERTY, id));
        criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
        List<T> lst = criteria.list();
        if(Collectionz.isNullOrEmpty(lst) == false){
            return lst.get(0);
        }
        return null;
    }

    /**
     * This method is used to get entity present in DB
     * @param type
     * @param order
     * @return entity of type
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> get(Class<T> type,Order order,SessionProvider session) throws Exception  {
        Criteria criteria = session.getSession().createCriteria(type);
        criteria.add(Restrictions.ne("status", CommonConstants.STATUS_DELETED));
        criteria.addOrder(order);
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
	public static <T> List<T> get(Class<T> type,String property,String value,SessionProvider session) throws Exception  {
        Criteria criteria = session.getSession().createCriteria(type);
        criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
        criteria.add(Restrictions.eq(property,value));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getAll(Class<T> type,String property,String value,SessionProvider session) throws Exception  {
        Criteria criteria = session.getSession().createCriteria(type);
        criteria.add(Restrictions.eq(property,value));
        return criteria.list();
    }

    public static <T> List<T> filterEntityBasedOnProperty(Class<T> type, String property, Long value, SessionProvider session) throws Exception  {
        Criteria criteria = session.getSession().createCriteria(type);
        criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
        criteria.add(Restrictions.eq(property,value));
        return criteria.list();
    }

    /**
     * This method will return the objects based on the name provided.  It will enforce the case-sensitivity on name
     * @param type
     * @param name
     * @param session
     * @param <T>
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public static <T> List<T> getByName(Class<T> type , String name,SessionProvider session) throws Exception{

        Criteria criteria = session.getSession().createCriteria(type);
        criteria.add(Restrictions.eq("name", name));
        if(type != GroupData.class) {
            criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
        }
        return ((List<T>)criteria.list());
    }

    /**
     * This method will return objects based on name parameter provided. It will eliminate the case-sensitivity on name
     * @param type
     * @param name
     * @param session
     * @param <T>
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public static <T> List<T> getName(Class<T> type , String name,SessionProvider session) throws Exception{

        Criteria criteria = session.getSession().createCriteria(type);
        criteria.add(Restrictions.ilike("name", name));
        if(type != GroupData.class) {
            criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
        }
        return ((List<T>)criteria.list());
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getNameBasedOnParentId(Class<T> type , String name,String parentId, String parentProperty, SessionProvider session) throws Exception{

        Criteria criteria = session.getSession().createCriteria(type);
        criteria.add(Restrictions.ilike("name", name)).add(Restrictions.like(parentProperty,parentId));
        if(type != GroupData.class) {
            criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
        }
        return ((List<T>)criteria.list());
    }

	public static <T> List<T> getPackageByName(Class<T> type , String name,SessionProvider session) throws Exception {

        Criteria criteria = session.getSession().createCriteria(type);
        criteria.add(Restrictions.eq("name", name));
        return ((List<T>) criteria.list());

    }


    public static void deleteQosProfiles(String pkgId, SessionProvider session) throws Exception{
         List<String> qosProfiles = getSelectedChildren(TBLM_QOS_PROFILE, PACKAGE_ID, pkgId, session);
            for (String qosData : qosProfiles) {
                deleteQosProfile(qosData,session);
            }
    }
    public static void deleteQosProfile(String qosProfileId, SessionProvider session) throws Exception{
        List<String> qosProfileDetails = getSelectedChildren(TBLM_QOS_PROFILE_DETAIL, QOS_PROFILE_ID, qosProfileId, session);
        for (String detailData : qosProfileDetails) {
            List<String> pccRules = getLocalPCCIds(detailData, session);
            for (String pcc : pccRules) {
                deletePCCRule(pcc,session);
            }
            String removeRelQuery = deleteQuery(TBLM_QOS_PROFILE_PCC_RELATION, QOS_PROFILE_DETAIL_ID, detailData);
            deleteData(removeRelQuery, session);

            String removeCRBNRelQuery = deleteQuery(TBLM_QOS_PROFILE_CRBN_RELATION, QOS_PROFILE_DETAIL_ID, detailData);
            deleteData(removeCRBNRelQuery, session);

            String queryString = deleteQuery(TBLM_QOS_PROFILE_DETAIL, ID, detailData);
            deleteData(queryString, session);
        }
        List<String> timePeriods = getSelectedChildren(TBLM_TIME_PERIOD, QOS_PROFILE_ID, qosProfileId, session);
        for (String timePeriodData : timePeriods) {
            String queryString = deleteQuery(TBLM_TIME_PERIOD, ID, timePeriodData);
            deleteData(queryString, session);
        }

        String queryString = deleteQuery(TBLM_QOS_PROFILE, ID, qosProfileId);
        deleteData(queryString, session);
    }

    public static void deletePCCRule(String pccRuleId, SessionProvider session) throws Exception{
        List<String> serviceData = getServiceDataFlowForLocalPCC(pccRuleId, session);
        for (String sdf : serviceData) {
            String queryString = deleteQuery(TBLM_SERVICE_DATA_FLOW, ID, sdf);
            deleteData(queryString, session);
        }
        String queryString = deleteQuery(TBLM_QOS_PROFILE_PCC_RELATION, PCC_RULE_ID, pccRuleId);
        deleteData(queryString, session);
        String queryStringForPCC = deleteQuery(TBLM_PCC_RULE, ID, pccRuleId, " AND SCOPE != 'GLOBAL'");
        deleteData(queryStringForPCC, session);
    }
    public static void deleteGlobalPCCRule(String pccRuleId, SessionProvider session) throws Exception{
        List<String> serviceData = getServiceDataFlowForGlobalPCC(pccRuleId, session);
        for (String sdf : serviceData) {
            String queryString = deleteQuery(TBLM_SERVICE_DATA_FLOW, ID, sdf);
            deleteData(queryString, session);
        }
        String queryString = deleteQuery(TBLM_QOS_PROFILE_PCC_RELATION, PCC_RULE_ID, pccRuleId);
        deleteData(queryString, session);
        String queryStringForPCC = deleteQuery(TBLM_PCC_RULE, ID, pccRuleId, " AND SCOPE = 'GLOBAL'");
        deleteData(queryStringForPCC, session);
    }

    public static void deleteChargingRuleBaseName(String chargingRuleId, SessionProvider session) throws Exception{
        List<String> serviceTypeRowIds = getServiceTypeRowIdsForChargingRuleBaseName(chargingRuleId, session);
        for (String idVal : serviceTypeRowIds) {
            String queryString = deleteQuery(TBLM_CHRG_RULE_DATA_SRV_TYPE, ID, idVal);

            deleteData(queryString, session);
        }

        String queryString = deleteQuery(TBLM_QOS_PROFILE_CRBN_RELATION, CHARGING_RULE_BASE_NAME_ID, chargingRuleId);
        deleteData(queryString, session);

        String queryStringForPCC = deleteQuery(TBLM_CHARGING_RULE_BASE_NAME, ID, chargingRuleId);
        deleteData(queryStringForPCC, session);
    }


    public static void deleteIMSServiceData(String pkgId, SessionProvider session) throws Exception{
        List<String> imsServices = getSelectedChildren(TBLM_IMS_PACKAGE_SERVICE, IMS_PACKAGE_ID, pkgId, session);
        for (String imsServiceData : imsServices) {
            deleteImsService(imsServiceData, session);
        }
    }
    public static void deleteImsService(String imsServiceId, SessionProvider session) throws Exception{
        List<String> imsPkgPCCAttributes = getSelectedChildren(TBLM_IMS_PACKAGE_PCC_ATTIBUTE, IMS_PACKAGE_SERVICE_ID, imsServiceId, session);
        for (String imsPkgPCCAttribute : imsPkgPCCAttributes) {
            deleteIMSPkgPCCAttribute(imsPkgPCCAttribute, session);

        }
        String queryString = deleteQuery(TBLM_IMS_PACKAGE_SERVICE, ID, imsServiceId);
        deleteData(queryString, session);
    }

    public static void deleteIMSPkgPCCAttribute(String imsPkgPCCAttribute, SessionProvider session) throws Exception{
        String queryStringForPCC = deleteQuery(TBLM_IMS_PACKAGE_PCC_ATTIBUTE, ID, imsPkgPCCAttribute,null);
        deleteData(queryStringForPCC, session);
    }

    public static void deleteDefaultServiceDataFlowInformation(String serviceTypeId, SessionProvider session) throws Exception{
        List<String> serviceDataFlowList = getSelectedChildren(TBLM_DEF_SERVICE_DATA_FLOW, DATA_SERVICE_TYPE_ID, serviceTypeId, session);
        for (String sdf : serviceDataFlowList) {
            String queryString = deleteQuery(TBLM_DEF_SERVICE_DATA_FLOW, ID, sdf);
            deleteData(queryString, session);
        }
    }

    public static void deleteServiceTypeRatingGroupRel(String serviceTypeId, SessionProvider session) throws Exception{
        String queryString = deleteQuery(TBLM_DATA_SRV_RG_REL, DATA_SERVICE_TYPE_ID, serviceTypeId);
        deleteData(queryString, session);
    }


    public static void removePackages(PkgData pkg, SessionProvider session) throws Exception {
        removePackageHierarchy(pkg,session);

        String queryString = deleteQuery(TBLM_PACKAGE, ID, pkg.getId());
        deleteData(queryString, session);

    }

    public static void removePackageHierarchy(PkgData pkg, SessionProvider session) throws Exception {
        if (session.getSession().getTransaction().isActive() == false) {
            session.getSession().getTransaction().begin();
        }

        deleteQosProfiles(pkg.getId(),session);
        deleteQuotaProfiles(pkg.getId(), pkg.getQuotaProfileType(), session);
        deleteRateCards(pkg.getId(),session);
        if(GlobalPkgValidations.isGlobalPlan(pkg) == true ){
            deletePkgGroupOrder(pkg.getId(),session);
        }
    }

    //delete rate card
    public static void deleteRateCards(String pkgId, SessionProvider session) throws Exception {
        List<String> rateCardIds = getSelectedChildren(TBLM_DATA_RATE_CARD, PACKAGE_ID, pkgId, session);
        for(String rateCardId:rateCardIds){
            deleteRateCard(rateCardId,session);
        }
    }

    public static void deleteRateCard(String rateCardId,SessionProvider session) throws Exception {
             deleteRateCardVersions(rateCardId,session);
             deleteData(deleteQuery(TBLM_DATA_RATE_CARD, ID, rateCardId),session);


    }

    private static void deleteRateCardVersions(String rateCardId, SessionProvider session) throws Exception {
        List<String> versionIds = getSelectedChildren(TBLM_DATA_RATE_CARD_VRSN_REL,"RATE_CARD_ID",rateCardId,session);
        for(String versionId : versionIds){
            //delete associated version details
            deleteVersionDetails(versionId,session);
            //delete version information
            deleteData(deleteQuery(TBLM_DATA_RATE_CARD_VRSN_REL,"RATE_CARD_ID",rateCardId),session);
        }
    }

    private static void deleteVersionDetails(String versionId, SessionProvider session) {
        Query deleteVersionDetailQuery = session.getSession().createSQLQuery("DELETE FROM TBLM_DATA_RATE_CARD_VRSN_DTL WHERE VER_REL_ID =:VERSION_ID");
        deleteVersionDetailQuery.setParameter("VERSION_ID", versionId);
        deleteVersionDetailQuery.executeUpdate();
    }

    public static void removeEmergencyPackages(EmergencyPkgDataExt emergencyPkg, SessionProvider session) throws Exception {
        if (session.getSession().getTransaction().isActive() == false) {
            session.getSession().getTransaction().begin();
        }

        deleteQosProfiles(emergencyPkg.getId(),session);
        deletePkgGroupOrder(emergencyPkg.getId(),session);
        String queryString = deleteQuery(TBLM_PACKAGE, ID, emergencyPkg.getId());
        deleteData(queryString, session);

    }

    private static void deletePkgGroupOrder(String id,SessionProvider session) throws Exception {
            String queryString = deleteQuery(TBLM_PKG_GROUP_ORDER,PKG_ID,id);
            deleteData(queryString,session);
    }

    public static void removeIMSPackages(IMSPkgData imsPkg, SessionProvider session) throws Exception {
        if (session.getSession().getTransaction().isActive() == false) {
            session.getSession().getTransaction().begin();
        }

        deleteIMSServiceData(imsPkg.getId(), session);
        String queryString = deleteQuery(TBLM_IMS_PACKAGE, ID, imsPkg.getId());
        deleteData(queryString, session);

    }

    public static void removeServiceType(DataServiceTypeDataExt serviceTypeData, SessionProvider session) throws Exception {
        if (session.getSession().getTransaction().isActive() == false) {
            session.getSession().getTransaction().begin();
        }
        deleteDefaultServiceDataFlowInformation(serviceTypeData.getId(), session);
        deleteServiceTypeRatingGroupRel(serviceTypeData.getId(),session);
        String queryString = deleteQuery(TBLM_DATA_SERVICE_TYPE, ID, serviceTypeData.getId());
        deleteData(queryString, session);

    }

    public static void deleteUsageMeteringBasedQuotaProfile(String quotaProfileId, String pkgId, SessionProvider session) throws Exception{
            List<String> quotaProfileDetails = getSelectedChildren(TBLM_QUOTA_PROFILE_DETAIL, QUOTA_PROFILE_ID, quotaProfileId, session);
            for (String detail : quotaProfileDetails) {
                deleteQuotaProfileDetailData(detail,session);
            }
            // Removing usage notification details
            List<String> usageNotifications =getSelectedChildren(TBLM_USAGE_NOTIFICATION, PACKAGE_ID, pkgId, session);
            for (String usageNotification : usageNotifications) {
                String queryString = deleteQuery(TBLM_USAGE_NOTIFICATION, ID, usageNotification);
                deleteData(queryString, session);
            }

            String queryString = deleteQuery(TBLM_QUOTA_PROFILE, ID, quotaProfileId);
            deleteData(queryString, session);
    }

    public static void deleteQuotaProfileDetailData(String quotaProfileDetailId,SessionProvider session) throws Exception{
        String queryString = deleteQuery(TBLM_QUOTA_PROFILE_DETAIL, ID, quotaProfileDetailId);
        deleteData(queryString, session);
    }
    public static void deleteQuotaProfiles(String pkgId, QuotaProfileType quotaProfileType, SessionProvider session) throws Exception{
        if (QuotaProfileType.USAGE_METERING_BASED == quotaProfileType) {
            List<String> quotaProfiles = getSelectedChildren(TBLM_QUOTA_PROFILE, PACKAGE_ID, pkgId, session);
            for (String qpData : quotaProfiles) {
                deleteUsageMeteringBasedQuotaProfile(qpData, pkgId, session);
            }
        } else if (QuotaProfileType.SY_COUNTER_BASED == quotaProfileType) {
            List<String> quotaProfiles = getSelectedChildren(TBLM_SY_QUOTA_PROFILE, PACKAGE_ID, pkgId, session);
            for (String qpData : quotaProfiles) {
                deleteSyBasedQuotaProfileData(qpData, session);

            }
        } else if (QuotaProfileType.RnC_BASED == quotaProfileType) {
            List<String> quotaProfiles = getSelectedChildren(TBLM_RNC_QUOTA_PROFILE, PACKAGE_ID, pkgId, session);
            for (String qpData : quotaProfiles) {
                deleteRncBasedQuotaProfileData(qpData, pkgId, session);

            }
        }
    }

    public static void deleteSyBasedQuotaProfileData(String syQuotaProfileId, SessionProvider session) throws Exception {
        List<String> quotaProfileDetails = getSelectedChildren(TBLM_SY_QUOTA_PROFILE_DETAIL, SY_QUOTA_PROFILE_ID, syQuotaProfileId, session);
        for (String detail : quotaProfileDetails) {
            String queryString = deleteQuery(TBLM_SY_QUOTA_PROFILE_DETAIL, ID, detail);
            deleteData(queryString, session);
        }
        String queryString = deleteQuery(TBLM_SY_QUOTA_PROFILE, ID, syQuotaProfileId);
        deleteData(queryString, session);
    }

    public static void deleteRncBasedQuotaProfileData(String rncQuotaProfileId, String pkgId, SessionProvider session) throws Exception {
        List<String> quotaProfileDetails = getSelectedChildren(TBLM_RNC_QUOTA_PROFILE_DETAIL, QUOTA_PROFILE_ID, rncQuotaProfileId, session);
        for (String detail : quotaProfileDetails) {
            String queryString = deleteQuery(TBLM_RNC_QUOTA_PROFILE_DETAIL, ID, detail);
            deleteData(queryString, session);
        }

        // Removing quota notification details
        List<String> quotaNotifications =getSelectedChildren(TBLM_QUOTA_NOTIFICATION, PACKAGE_ID, pkgId, session);
        for (String quotaNotification : quotaNotifications) {
            String queryString = deleteQuery(TBLM_QUOTA_NOTIFICATION, ID, quotaNotification);
            deleteData(queryString, session);
        }

        String queryString = deleteQuery(TBLM_RNC_QUOTA_PROFILE, ID, rncQuotaProfileId);
        deleteData(queryString, session);
    }

    public static String deleteQuery(String tableName, String columName, String id, String andClause) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(tableName);
        sb.append(" WHERE ");
        sb.append(columName);
        sb.append(CommonConstants.EQUAL);
        sb.append(CommonConstants.SINGLE_QUOTE);
        sb.append(id);
        sb.append(CommonConstants.SINGLE_QUOTE);
        if (Strings.isNullOrBlank(andClause) == false) {
            sb.append(andClause);
        }
        return sb.toString();

    }

    public static String deleteQuery(String tableName, String columName, String id) {
        return deleteQuery(tableName, columName, id, null);
    }

    private static void deleteData(String query, SessionProvider session) throws Exception {
        Query qry = session.getSession().createSQLQuery(query);
        qry.executeUpdate();
    }

    @SuppressWarnings("unchecked")
	private static List<String> getSelectedChildren(String tableName, String columnName, String fetchColumn, String id, SessionProvider session) {

        if (Strings.isNullOrBlank(fetchColumn)) {
            fetchColumn = ID;
        }
        Query q = session.getSession().createSQLQuery("SELECT " + fetchColumn + " FROM " + tableName + " WHERE " + columnName + " = '" + id + "'");
        return q.list();
    }

    @SuppressWarnings("unchecked")
	private static  List<String> getLocalPCCIds(String id, SessionProvider session) {
        Query q = session.getSession().createSQLQuery("SELECT ID FROM TBLM_PCC_RULE WHERE ID IN (SELECT PCC_RULE_ID FROM TBLM_QOS_PROFILE_PCC_RELATION WHERE QOS_PROFILE_DETAIL_ID= :id ) AND SCOPE != 'GLOBAL'");
        q.setParameter("id",id);
        return q.list();
    }


    @SuppressWarnings("unchecked")
	private static List<String> getServiceDataFlowForLocalPCC(String pccRuleId, SessionProvider session) {


        Query q = session.getSession().createSQLQuery("select ID from TBLM_SERVICE_DATA_FLOW WHERE PCC_RULE_ID IN(SELECT ID FROM TBLM_PCC_RULE WHERE SCOPE != 'GLOBAL' AND ID= :pccRuleId)");
        q.setParameter("pccRuleId",pccRuleId);
        return q.list();
    }

    @SuppressWarnings("unchecked")
	private static List<String> getServiceDataFlowForGlobalPCC(String pccRuleId, SessionProvider session) {


        Query q = session.getSession().createSQLQuery("select ID from TBLM_SERVICE_DATA_FLOW WHERE PCC_RULE_ID IN(SELECT ID FROM TBLM_PCC_RULE WHERE SCOPE = 'GLOBAL' AND ID= :pccRuleId)");
        q.setParameter("pccRuleId",pccRuleId);
        return q.list();
    }

    @SuppressWarnings("unchecked")
	private static List<String> getServiceTypeRowIdsForChargingRuleBaseName(String chargingRuleId, SessionProvider session) {

        Query q = session.getSession().createSQLQuery("select ID from TBLM_CHRG_RULE_DATA_SRV_TYPE WHERE CHARGING_RULE_BASE_NAME_ID IN(SELECT ID FROM TBLM_CHARGING_RULE_BASE_NAME WHERE ID = :chargingRuleId)");
        q.setParameter("chargingRuleId",chargingRuleId);
        return q.list();
    }

    private static List<String> getSelectedChildren(String tableName, String columnName, String id, SessionProvider session) {
        return getSelectedChildren(tableName, columnName, "", id, session);
    }

    public static void deleteQosProfileForQuotaProfile(String quotaProfileId,SessionProvider sessionProvider) throws Exception {
        List<String> qosProfilesForQuota = getSelectedChildren("TBLM_QOS_PROFILE","QUOTA_PROFILE_ID",null,quotaProfileId,sessionProvider);
        if(Collectionz.isNullOrEmpty(qosProfilesForQuota) == false){
            for(String qosId : qosProfilesForQuota){
                deleteQosProfile(qosId,sessionProvider);
            }
        }
    }


    public static Map<String,List<ResourceData>> fetchGroupWisePkgMap(Class clazz, String pkgId, String pkgName, String mode, String pkgType, SessionProvider sessionProvider) throws Exception {
        List<PkgData> packages = findListOfPackages(clazz,pkgId,pkgName,mode,pkgType,sessionProvider);

        if (Collectionz.isNullOrEmpty(packages) == true) {
            return null;
}

        Map<String, List<ResourceData>> groupWisePackageMap = Maps.newHashMap();
        for (PkgData pkg : packages) {

            //if package has only one group & that is DEFAULT GROUP
            if (CommonConstants.DEFAULT_GROUP_ID.equalsIgnoreCase(pkg.getGroups()) == true) {
                addToGroupWiseMap(groupWisePackageMap, pkg, CommonConstants.DEFAULT_GROUP_ID);
                continue;
            }

            final List<String> groupIds = CommonConstants.COMMA_SPLITTER.split(pkg.getGroups());

            // if package has multiple group but one of the group is DEFAULT GROUP than that will be consider in DEFAULT GROUP LIMIT
            if (groupIds.contains(CommonConstants.DEFAULT_GROUP_ID)) {
                addToGroupWiseMap(groupWisePackageMap, pkg, CommonConstants.DEFAULT_GROUP_ID);
                continue;
            }

            //Consider individual GROUP wise limit
            for (String groupId : groupIds) {
                addToGroupWiseMap(groupWisePackageMap, pkg, groupId);
            }
        }
        return groupWisePackageMap;
    }

    private static List<PkgData> findListOfPackages(Class clazz,String pkgId,String pkgName, String mode, String pkgType,SessionProvider sessionProvider) throws Exception{
        List<PkgData> pkgDatas;

        Criteria criteria = sessionProvider.getSession().createCriteria(clazz);

        criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));

        criteria.add(Restrictions.eq("type", pkgType));
        if(MODE_UPDATE.equalsIgnoreCase(mode)){
            if(Strings.isNullOrBlank(pkgId) == false ) {
                criteria.add(Restrictions.ne("id", pkgId));
            }else{
                criteria.add(Restrictions.ne("name", pkgName));
            }
        }
        pkgDatas = criteria.list();

        return pkgDatas;
    }

    private static void addToGroupWiseMap(Map<String, List<ResourceData>> groupWisePackageMap, PkgData pkg, String group) {
        if(Collectionz.isNullOrEmpty(groupWisePackageMap.get(group)) == true){
            List<ResourceData> pkgList = Collectionz.newArrayList();
            pkgList.add(pkg);
            groupWisePackageMap.put(group,pkgList);
            return;
        }
        groupWisePackageMap.get(group).add(pkg);
    }

    public static int getGroupWiseMaxOrder(String groupId, String type,SessionProvider sessionProvider) throws Exception{
        int maxOrder = 0;
        Session session = sessionProvider.getSession();
        Criteria criteria = session.createCriteria(PkgGroupOrderData.class);
        criteria.setProjection(Projections.property("orderNumber"));
        criteria.add(Restrictions.and(Restrictions.eq("type", type),Restrictions.eq("groupId",groupId)));
        criteria.addOrder(Order.desc("orderNumber"));
        List<Integer> list = criteria.list();
        if (Collectionz.isNullOrEmpty(list) == false) {
            maxOrder = list.get(0) ;
        }
        return maxOrder;
    }

    public static int getPkgGroupOrderNo(PkgData pkgData, String groupId, SessionProvider sessionProvider) throws Exception {
        Session session = sessionProvider.getSession();
        Criteria criteria = session.createCriteria(PkgGroupOrderData.class);
        criteria.setProjection(Projections.property("orderNumber"));
        criteria.add(Restrictions.and(Restrictions.eq("pkgData", pkgData),Restrictions.eq("groupId",groupId)));
        List list = criteria.list();
        if(Collectionz.isNullOrEmpty(list) == false){
            return (Integer)list.get(0);
        }
       return getGroupWiseMaxOrder(groupId,pkgData.getType(),sessionProvider)+1;
    }

    public static ResourceData getExistingEntity(String id, String name, ResourceData entity, SessionProvider sessionProvider) throws Exception {

        ResourceData existingEntity = null;
        if (Strings.isNullOrBlank(id) == false) {
            List<? extends ResourceData> entityList = ImportExportCRUDOperationUtil.get(entity.getClass(), ENTITY_ID, id, sessionProvider);
            if (Collectionz.isNullOrEmpty(entityList) == false) {
                existingEntity = entityList.get(0);
            }
        }
        if (existingEntity == null && Strings.isNullOrBlank(name) == false) {
            List<ResourceData> existingEntityList = (List<ResourceData>) ImportExportCRUDOperationUtil.getByName(entity.getClass(), name, sessionProvider);
            if (Collectionz.isNullOrEmpty(existingEntityList) == false) {
                return existingEntityList.get(0);
            }
        }

        return existingEntity;

    }

    public static void save(Object object, SessionProvider sessionProvider) {
        sessionProvider.getSession().save(object);
    }
}

