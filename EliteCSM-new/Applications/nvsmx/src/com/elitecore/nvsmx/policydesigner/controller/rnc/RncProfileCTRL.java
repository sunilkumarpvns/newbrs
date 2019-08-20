package com.elitecore.nvsmx.policydesigner.controller.rnc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.LongToStringGsonAdapter;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PccRuleDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.rnc.RncProfileDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.rnc.RncProfileDetailWrapper;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.hibernate.criterion.Order;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil.audit;
import static com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil.findAllWhichIsNotDeleted;
import static com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil.get;
import static com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil.save;
import static com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil.saveOrUpdate;


/**
 * Used to manage all the operations for Rating And charging Based Quota Profile.
 *
 * Created by dhyani on 19/7/17.
 */
public class RncProfileCTRL extends EliteGenericCTRL {

    public static final String MODULE = "RnC-PROFILE";
    private RncProfileData rncProfileData = new RncProfileData();
    private RncProfileDetailData rncProfileDetailData = new RncProfileDetailData();
    private String actionChainUrl;
    private transient Object [] messageParameter = {Discriminators.RnC_PROFILE};
    private Map<Integer, JsonArray> rncProfileDetailMap;
    private Map<Integer, List<RncProfileDetailWrapper>> rncProfileDetailWrapperMap = new HashMap<Integer,List<RncProfileDetailWrapper>>();
    private List<DataServiceTypeData> dataServiceTypeData = Collectionz.newArrayList();
    private List<RatingGroupData> ratingGroupDatas = Collectionz.newArrayList();
    private static final String ACTION_VIEW = "policydesigner/rnc/RncProfile/view";
    private String serviceTypeRatingGroupMapJson;
    private String staffBelongingRatingGroupJson;
    private EnumSet<RenewalIntervalUnit> validRenewalIntervals = EnumSet.of(RenewalIntervalUnit.MONTH,RenewalIntervalUnit.MONTH_END,RenewalIntervalUnit.TILL_BILL_DATE);
    private List<RevenueDetailData> revenueDetails;
    private String currency;

    public EnumSet<RenewalIntervalUnit> getValidRenewalIntervals() {
        return validRenewalIntervals;
    }

    public void setValidRenewalIntervals(EnumSet<RenewalIntervalUnit> validRenewalIntervals) {
        this.validRenewalIntervals = validRenewalIntervals;
    }


    @InputConfig(resultName = NVSMXCommonConstants.ACTION_PKG_VIEW)
    public String create() {

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE,"Method called create()");
        }
        setActionChainUrl(Results.VIEW.getValue());

        try {

            String pkgId = getPkgId();
            PkgData pkgData = get(PkgData.class,pkgId);
            rncProfileData.setPkgData(pkgData);
            rncProfileData.setCreatedDateAndStaff(getStaffData());
            save(rncProfileData);
            request.setAttribute(Attributes.PKG_ID, pkgId);
            MessageFormat messageFormat = new MessageFormat(getText("create.success"));
            addActionMessage(messageFormat.format(messageParameter));
            String message = Discriminators.RnC_PROFILE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG + rncProfileData.getName() + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + "Created";
            audit(rncProfileData,rncProfileData.getPkgData().getName(), AuditActions.CREATE, getStaffData(), request.getRemoteAddr(),rncProfileData.getHierarchy(), message);
            request.setAttribute(Attributes.QUOTA_PROFILE_ID, rncProfileData.getId());
            return Results.DISPATCH_VIEW.getValue();

        } catch (Exception e) {
            return generateErrorLogsAndRedirect(e, "Failed to Create "+Discriminators.RnC_PROFILE +".", ActionMessageKeys.CREATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
        }
    }

    @SkipValidation
    public String createOrUpdateDetail(){

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE,"Method called createOrUpdateDetail()");
        }

        setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
        try{
            if(Strings.isNullOrBlank(rncProfileDetailData.getId())) {
                rncProfileDetailData.setId(null); //because we are using same dialog box for Balance Configuration
            }
            rncProfileData = get(RncProfileData.class, rncProfileData.getId());
            JsonObject jsonObjectOld = rncProfileData.toJson();
            rncProfileData.getRncProfileDetailDatas().remove(rncProfileDetailData);
            setRncProfileDetailDatas();
            rncProfileData.getRncProfileDetailDatas().add(getRncProfileDetailData());
            rncProfileData.setModifiedDateAndStaff(getStaffData());
            saveOrUpdate(rncProfileData);
            JsonObject jsonObjectNew = rncProfileData.toJson();
            JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);
            String message = Discriminators.RnC_PROFILE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG  + rncProfileData.getName() + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG  + "Updated";
            audit(rncProfileData,rncProfileData.getPkgData().getName(), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),difference,rncProfileData.getHierarchy(), message);
            request.setAttribute(Attributes.QUOTA_PROFILE_ID, rncProfileData.getId());

        }catch(Exception e){
            return generateErrorLogsAndRedirect(e, "Failed to create/update "+Discriminators.RnC_PROFILE +".", ActionMessageKeys.CREATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
        }
        setActionChainUrl(ACTION_VIEW);
        setParentIdKey(Attributes.QUOTA_PROFILE_ID);
        setParentIdValue(rncProfileData.getId());
        setCurrency(rncProfileData.getPkgData().getCurrency());
        return Results.REDIRECT_TO_PARENT.getValue();
    }

    @SkipValidation
    public String deleteDetail(){

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE,"Method called deleteDetail()");
        }

        setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
        try{
            String quotaProfileDetailId  = request.getParameter(Attributes.QUOTA_PROFILE_DETAIL_ID);
            MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
            if(Strings.isNullOrBlank(quotaProfileDetailId) == false) {

                rncProfileDetailData = get(RncProfileDetailData.class, quotaProfileDetailId);
                rncProfileData = rncProfileDetailData.getRncProfileData();
                JsonObject jsonObjectOld = rncProfileData.toJson();
                rncProfileData.setModifiedDateAndStaff(getStaffData());
                CRUDOperationUtil.delete(rncProfileDetailData);
                rncProfileData.getRncProfileDetailDatas().remove(rncProfileDetailData); //REQUIRED TO REMOVE ASSOCIATION
                JsonObject jsonObjectNew = rncProfileData.toJson();
                request.setAttribute(Attributes.QUOTA_PROFILE_ID, rncProfileData.getId());
                Object [] messageParameterFupLevel = {getText("rnc.configuration")};
                JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);
                String message = Discriminators.RnC_PROFILE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG  + rncProfileData.getName() + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG  + "Updated";
                audit(rncProfileData,rncProfileData.getPkgData().getName(), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),difference,rncProfileData.getHierarchy(), message);
                addActionMessage(messageFormat.format(messageParameterFupLevel));
                setActionChainUrl(ACTION_VIEW);
                setParentIdKey(Attributes.QUOTA_PROFILE_ID);
                setParentIdValue(rncProfileData.getId());
                return Results.REDIRECT_TO_PARENT.getValue();
            }

            String quotaProfileId  = request.getParameter(Attributes.QUOTA_PROFILE_ID);
            String fupLevel  = request.getParameter(Attributes.FUP_LEVEL);
            if(Strings.isNullOrEmpty(fupLevel) == false && Strings.isNullOrEmpty(quotaProfileId) ==false){
                rncProfileData = get(RncProfileData.class,quotaProfileId);
                JsonObject jsonObjectOld = rncProfileData.toJson();
                List<RncProfileDetailData> quotaProfileDetailDatas = RncProfileDAO.getFupLevelQuotaProfileDetails(RncProfileDetailData.class, fupLevel, rncProfileData);
                if(Collectionz.isNullOrEmpty(quotaProfileDetailDatas) == false){
                    rncProfileData = quotaProfileDetailDatas.get(0).getRncProfileData();
                    rncProfileData.getRncProfileDetailDatas().removeAll(quotaProfileDetailDatas);
                    CRUDOperationUtil.update(rncProfileData);
                    JsonObject jsonObjectNew = rncProfileData.toJson();

                    Object [] messageParameterFupLevel = {FieldValueConstants.FUP_LEVEL};
                    addActionMessage(messageFormat.format(messageParameterFupLevel));
                    JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);
                    String message = Discriminators.RnC_PROFILE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG  + rncProfileData.getName() + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG  + "Updated";
                    audit(rncProfileData,rncProfileData.getPkgData().getName(), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),difference,rncProfileData.getHierarchy(), message);
                }
            }else {
                return redirectError(Discriminators.RnC_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
            }

        }catch(Exception e){
            return generateErrorLogsAndRedirect(e, "Failed to view "+Discriminators.RnC_PROFILE +".", ActionMessageKeys.VIEW_FAILURE.key, Results.REDIRECT_ERROR.getValue());
        }
        setActionChainUrl(ACTION_VIEW);
        setParentIdKey(Attributes.QUOTA_PROFILE_ID);
        setParentIdValue(rncProfileData.getId());
        return Results.REDIRECT_TO_PARENT.getValue();


    }

    @InputConfig(resultName = NVSMXCommonConstants.ACTION_PKG_VIEW)
    public String update(){

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE,"Method called update()");
        }
        setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
        try{

            rncProfileData.setRncProfileDetailDatas(Collectionz.newArrayList());
            RncProfileData rncProfileDataFromDB = get(RncProfileData.class, rncProfileData.getId());
            rncProfileDataFromDB.setName(rncProfileData.getName());
            rncProfileDataFromDB.setDescription(rncProfileData.getDescription());
            rncProfileDataFromDB.setQuotaType(rncProfileData.getQuotaType());
            rncProfileDataFromDB.setUnitType(rncProfileData.getUnitType());
            rncProfileDataFromDB.setBalanceLevel(rncProfileData.getBalanceLevel());
            rncProfileDataFromDB.setRenewalInterval(rncProfileData.getRenewalInterval());
            rncProfileDataFromDB.setRenewalIntervalUnit(rncProfileData.getRenewalIntervalUnit());
            rncProfileDataFromDB.setProration(rncProfileData.getProration());
            rncProfileDataFromDB.setCarryForward(rncProfileData.getCarryForward());
            rncProfileDataFromDB.setModifiedDateAndStaff(getStaffData());

            CRUDOperationUtil.update(rncProfileDataFromDB);
            request.setAttribute(Attributes.PKG_ID, rncProfileDataFromDB.getPkgData().getId());
            request.setAttribute(Attributes.QUOTA_PROFILE_ID, rncProfileDataFromDB.getId());

            String message = Discriminators.RnC_PROFILE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG  + rncProfileDataFromDB.getName() + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG  + "Updated";
            audit(rncProfileDataFromDB,rncProfileDataFromDB.getPkgData().getName(), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),rncProfileDataFromDB.getHierarchy(), message);

        }catch(Exception e){
            return generateErrorLogsAndRedirect(e, "Failed to update "+Discriminators.RnC_PROFILE +".", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
        }
        setActionChainUrl(Results.VIEW.getValue());
        return Results.DISPATCH_VIEW.getValue();
    }


    @SkipValidation
    public String delete(){
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method called delete()");
        }
        setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
        try {
            String quotaProfileId = getQuotaProfileId();
            if(Strings.isNullOrEmpty(quotaProfileId) == true){
                return redirectError(Discriminators.RnC_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
            }
            rncProfileData = get(RncProfileData.class, quotaProfileId);
            if(rncProfileData == null){
                return redirectError(Discriminators.RnC_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
            }
            PkgData pkgData = rncProfileData.getPkgData();
            setParentIdKey(Attributes.PKG_ID);
            setParentIdValue(pkgData.getId());

            if(Collectionz.isNullOrEmpty(rncProfileData.getQosProfiles())){
                rncProfileData.setStatus(CommonConstants.STATUS_DELETED);
                String message = Discriminators.RnC_PROFILE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG  + rncProfileData.getName() + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG  + "Deleted";
                audit(rncProfileData,rncProfileData.getPkgData().getName(), AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),rncProfileData.getHierarchy(), message);
                for(QuotaNotificationData quotaNotificationData : rncProfileData.getQuotaNotificationDatas()){
                    quotaNotificationData.setStatus(CommonConstants.STATUS_DELETED);
                }
            }else{
                for(QosProfileData qosProfileData : rncProfileData.getQosProfiles()){
                    if(qosProfileData.getStatus().equals(CommonConstants.STATUS_DELETED)){
                        rncProfileData.setStatus(CommonConstants.STATUS_DELETED);
                        String message = Discriminators.RnC_PROFILE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG  + rncProfileData.getName() + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG  + "Deleted";
                        audit(rncProfileData,rncProfileData.getPkgData().getName(), AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),rncProfileData.getHierarchy(), message);
                    }else{
                        addActionError(Discriminators.RnC_PROFILE + " " +getText(ActionMessageKeys.DELETE_FAILURE.key));
                        addActionError("QosProfile is Configured with '"+ rncProfileData.getName() + "' " + Discriminators.RnC_PROFILE);
                        return Results.REDIRECT_TO_PARENT.getValue();
                    }
                }
            }
            MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
            addActionMessage(messageFormat.format(messageParameter));
            return Results.REDIRECT_TO_PARENT.getValue();
        } catch (Exception e) {
            return generateErrorLogsAndRedirect(e, "Failed to delete "+Discriminators.RnC_PROFILE +".", ActionMessageKeys.DELETE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
        }
    }

    private String getPkgId() {

        String pkgId = request.getParameter(Attributes.PKG_ID);
        if (Strings.isNullOrBlank(pkgId)) {
            pkgId = (String) request.getAttribute(Attributes.PKG_ID);
            if (Strings.isNullOrBlank(pkgId)) {
                pkgId = request.getParameter("rncProfileData.pkgData.id");
            }
        }
        return pkgId;
    }

    private String getQuotaProfileId() {

        String quotaProfileId = request.getParameter(Attributes.QUOTA_PROFILE_ID);
        if (Strings.isNullOrBlank(quotaProfileId)) {
            quotaProfileId = (String) request.getAttribute(Attributes.QUOTA_PROFILE_ID);
            if (Strings.isNullOrBlank(quotaProfileId)) {
                quotaProfileId = request.getParameter("rncProfileData.id");
            }
        }
        return quotaProfileId;
    }

    private void setRncProfileDetailDatas() {

        //Setting ServiceType
        DataServiceTypeData dataServiceType = get(DataServiceTypeData.class,rncProfileDetailData.getServiceTypeId());
        rncProfileDetailData.setDataServiceTypeData(dataServiceType);

        //Setting RatingGroup
        RatingGroupData ratingGroupData = get(RatingGroupData.class,rncProfileDetailData.getRatingGroupId());
        rncProfileDetailData.setRatingGroupData(ratingGroupData);

        //Setting Parent Object (RncProfileData)
        rncProfileDetailData.setRncProfileData(rncProfileData);

    }

    @SkipValidation
    public String view() {

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE,"Method called view()");
        }
        String quotaProfileId;
        setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
        try{
            quotaProfileId = getQuotaProfileId();

            setDataServiceTypeData(get(DataServiceTypeData.class, Order.asc("serviceIdentifier")));
            setRatingGroupDatas(findAllWhichIsNotDeleted(RatingGroupData.class));
            List<String> staffBelongingGroups = CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
            setServiceTypeRatingGroupMapJson(PccRuleDAO.getStaffBelongingServiceTypeJson(staffBelongingGroups));
            setStaffBelongingRatingGroupJson(PccRuleDAO.getStaffBelongingRatingGroupJson(staffBelongingGroups));

            List<RevenueDetailData> revenueDetailDataList = CRUDOperationUtil.findAll(RevenueDetailData.class);
            setRevenueDetails(revenueDetailDataList);
            rncProfileData = get(RncProfileData.class,quotaProfileId);
            if(rncProfileData == null) {
                addActionError("Failed to display full details of "+Discriminators.RnC_PROFILE);
                return Results.REDIRECT_ERROR.getValue();
            }
            PkgData pkgData = new PkgData();
            pkgData.setCurrency(rncProfileData.getPkgData().getCurrency());
            rncProfileData.setGroupNames(GroupDAO.getGroupNames(SPLITTER.split(rncProfileData.getPkgData().getGroups())));

            //----------The RNC_PROFILE_DETAIL_DATA Attribute will be used while RnC Configuration Dialog---------------------//
            Gson gson =new GsonBuilder().registerTypeAdapter(Long.class, new LongToStringGsonAdapter()).create(); // GsonFactory.defaultInstance();
            JsonArray rncProfileDataAsJsonArray = gson.toJsonTree(rncProfileData.getRncProfileDetailDatas()).getAsJsonArray();
            request.setAttribute(Attributes.RNC_PROFILE_DETAIL_DATA,rncProfileDataAsJsonArray);
            //------------------------------------------------------------------------------------------------//


            //---------- used to display data in wrapper---------------------//
            List<RncProfileDetailWrapper> rncProfileDetailWrappers = setRncProfileWrappers(rncProfileData.getRncProfileDetailDatas());
            setRncProfileDetailWrapperMap(fupLevelWiseRncConfiguration(rncProfileDetailWrappers));
            setRncProfileDetailJsonMap();
            //-------------------------------------------------------------//

            request.setAttribute(Attributes.QUOTA_PROFILE_ID, rncProfileData.getId());
        }catch(Exception e){
            return generateErrorLogsAndRedirect(e, "Failed to view "+Discriminators.RnC_PROFILE +".", ActionMessageKeys.VIEW_FAILURE.key, Results.REDIRECT_ERROR.getValue());
        }
        return Results.DETAIL.getValue();

    }

    /**
     * This method will get List of RncProfileDetailWrapper and Convert that list into map. map contains key as fupLevel and value as list of level wise detail
     * @param rncProfileDetailWrappers
     * @return Map<Integer,List<RncProfileDetailWrapper>>
     */
    private Map<Integer,List<RncProfileDetailWrapper>> fupLevelWiseRncConfiguration(List<RncProfileDetailWrapper> rncProfileDetailWrappers){

        if(Collectionz.isNullOrEmpty(rncProfileDetailWrappers)){
            return new HashMap<Integer,List<RncProfileDetailWrapper>>();
        }

        Map<Integer,List<RncProfileDetailWrapper>> fupLevelWiseMap= new HashMap<Integer, List<RncProfileDetailWrapper>>();
        for(RncProfileDetailWrapper quotaProfileDetail : rncProfileDetailWrappers){
            List<RncProfileDetailWrapper> quotaProfileDetailList = Collectionz.newLinkedList();
            quotaProfileDetailList.add(quotaProfileDetail);
            if(fupLevelWiseMap.get(Integer.valueOf(quotaProfileDetail.getFupLevel()))==null){
                fupLevelWiseMap.put(Integer.valueOf(quotaProfileDetail.getFupLevel()), quotaProfileDetailList);
            }else{
                fupLevelWiseMap.get(Integer.valueOf(quotaProfileDetail.getFupLevel())).add(quotaProfileDetail);
            }
        }
        return fupLevelWiseMap;
    }

    /**
     * convert RncProfileDetailData into RncProfileDetailWrapper
     * @param rncProfileDetailDatas
     * @return List<RncProfileDetailWrapper>
     */
    private List<RncProfileDetailWrapper> setRncProfileWrappers(List<RncProfileDetailData> rncProfileDetailDatas){
		List<RncProfileDetailWrapper> basedQuotaProfileWrappers = Collectionz.newArrayList();

		for(RncProfileDetailData balanceBasedQuotaProfileDetailData : rncProfileDetailDatas){
            RncProfileDetailWrapper wrapper = new RncProfileDetailWrapper.RncProfileDetailWrapperBuilder(balanceBasedQuotaProfileDetailData.getId()).withQuotaProfileDetails(balanceBasedQuotaProfileDetailData).build();
			basedQuotaProfileWrappers.add(wrapper);
		}
        return  basedQuotaProfileWrappers;
	}

    /**
     * Convert FupLevel Map to JsonArray Map
     */
    private void setRncProfileDetailJsonMap() {

        if(rncProfileDetailWrapperMap == null) {
            return;
        }
        Gson gson = GsonFactory.defaultInstance();
        rncProfileDetailMap = new HashMap<Integer,JsonArray>();
        for (Map.Entry<Integer, List<RncProfileDetailWrapper>> fupLevelDetail : getRncProfileDetailWrapperMap().entrySet()) {
            JsonArray rncProfileDetailJson = gson.toJsonTree(fupLevelDetail.getValue(), new TypeToken<List<RncProfileDetailWrapper>>() {}.getType()).getAsJsonArray();
             rncProfileDetailMap.put(fupLevelDetail.getKey(), rncProfileDetailJson);
        }
        setRncProfileDetailMap(rncProfileDetailMap);

    }

    @SkipValidation
    public String rncConfigurationSubView() {

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(MODULE,"Method Called rncConfigurationSubView()");
        }
        String tableId = request.getParameter(Attributes.TABLE_ID);
        String rowData = request.getParameter(Attributes.ROW_DATA+tableId);

        Gson gson = GsonFactory.defaultInstance();
        RncProfileData rncProfile = gson.fromJson(rowData, RncProfileData.class);
        rncProfile = get(RncProfileData.class, rncProfile.getId());
        List<RncProfileDetailWrapper> rncProfileDetailWrappers = setRncProfileWrappers(rncProfile.getRncProfileDetailDatas());
        setRncProfileDetailWrapperMap(fupLevelWiseRncConfiguration(rncProfileDetailWrappers));
        setParentIdKey(Attributes.PKG_ID);
        setParentIdValue(rncProfile.getPkgData().getId());
        setCurrency(rncProfile.getPkgData().getCurrency());
        return Results.SUBTABLEURL.getValue();
    }

    @Override
    public String getIncludeProperties() {
        return null;
    }





    @Override
    public RncProfileData getModel() {
        return rncProfileData;
    }

    public RncProfileData getRncProfileData() {
        return rncProfileData;
    }

    public void setRncProfileData(RncProfileData rncProfileData) {
        this.rncProfileData = rncProfileData;
    }

    public String getActionChainUrl() {
        return actionChainUrl;
    }

    @ActionChain(name = "actionChainUrlMethod")
    public void setActionChainUrl(String actionChainUrl) {
        this.actionChainUrl = actionChainUrl;
    }

    private String generateErrorLogsAndRedirect(Exception e, String message, String key, String result) {
        return super.generateErrorLogsAndRedirect(Discriminators.RnC_PROFILE, e, message, key, result);
    }

    public RncProfileDetailData getRncProfileDetailData() {
        return rncProfileDetailData;
    }

    public void setRncProfileDetailData(RncProfileDetailData rncProfileDetailData) {
        this.rncProfileDetailData = rncProfileDetailData;
    }

    public Map<Integer, JsonArray> getRncProfileDetailMap() {
        return rncProfileDetailMap;
    }

    public void setRncProfileDetailMap(Map<Integer, JsonArray> rncProfileDetailMap) {
        this.rncProfileDetailMap = rncProfileDetailMap;
    }

    public Map<Integer, List<RncProfileDetailWrapper>> getRncProfileDetailWrapperMap() {
        return rncProfileDetailWrapperMap;
    }

    public void setRncProfileDetailWrapperMap(Map<Integer, List<RncProfileDetailWrapper>> rncProfileDetailWrapperMap) {
        this.rncProfileDetailWrapperMap = rncProfileDetailWrapperMap;
    }

    public void setDataServiceTypeData(List<DataServiceTypeData> dataServiceTypeData) {
        this.dataServiceTypeData = dataServiceTypeData;
    }

    public void setRatingGroupDatas(List<RatingGroupData> ratingGroupDatas) {
        this.ratingGroupDatas = ratingGroupDatas;
    }

    public List<DataServiceTypeData> getDataServiceTypeData() {
        return dataServiceTypeData;
    }

    public List<RatingGroupData> getRatingGroupDatas() {
        return ratingGroupDatas;
    }

    public String getServiceTypeRatingGroupMapJson() {
        return serviceTypeRatingGroupMapJson;
    }

    public void setServiceTypeRatingGroupMapJson(String serviceTypeRatingGroupMapJson) {
        this.serviceTypeRatingGroupMapJson = serviceTypeRatingGroupMapJson;
    }

    public String getStaffBelongingRatingGroupJson() {
        return staffBelongingRatingGroupJson;
    }

    public void setStaffBelongingRatingGroupJson(String staffBelongingRatingGroupJson) {
        this.staffBelongingRatingGroupJson = staffBelongingRatingGroupJson;
    }

    public List<RevenueDetailData> getRevenueDetails() {
        return revenueDetails;
    }

    public void setRevenueDetails(List<RevenueDetailData> revenueDetails) {
        this.revenueDetails = revenueDetails;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
