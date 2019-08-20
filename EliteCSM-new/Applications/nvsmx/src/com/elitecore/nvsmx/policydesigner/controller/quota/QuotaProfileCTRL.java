package com.elitecore.nvsmx.policydesigner.controller.quota;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.notification.UsageNotificationData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.LongToStringGsonAdapter;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.policydesigner.model.pkg.quota.QuotaProfileDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.quota.QuotaProfileWrapper;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Provides functionality of Creating,editing,deleting and viewing QuotaProfile
 * and its details
 *
 * @author ishani.bhatt
 *
 */
public class QuotaProfileCTRL extends EliteGenericCTRL<QuotaProfileData>{

	private static final long serialVersionUID = 1L;
	private static final String MODULE = QuotaProfileCTRL.class.getSimpleName();
	private static final String QUOTAPROFILEDETAIL = " QuotaProfileDetail ";
	private static final String QUOTAPROFILE_DETAIL_VIEW= "quotaProfileDetailView";
	private QuotaProfileData quotaProfile = new QuotaProfileData();
	public String pkgId;
	public List<DataServiceTypeData> dataServiceTypes = new ArrayList<DataServiceTypeData>();
	private List<QuotaProfileWrapper> fupLevelList = Collectionz.newArrayList();

	private Map<Integer, Map<String, QuotaProfileDetailData>> fupLevelDetailMap = new HashMap<>();

	private String actionChainUrl;
	private Object [] messageParameter = {Discriminators.QUOTA_PROFILE};
	public static final String QUOTA_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.description,dataList\\[\\d+\\]\\.pkgData,dataList\\[\\d+\\]\\.pkgData.name";

	private transient Map<Integer, JsonArray> quotaProfileDetailJsonMap;

	@SkipValidation
	public String init() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called init()");
		}
		String quotaProfileId = getQuotaprofileId();

		if (Strings.isNullOrBlank(quotaProfileId)) {

			pkgId = getPkgId();
		PkgData pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);
		quotaProfile.setDescription(NVSMXUtil.getDefaultDescription(request));
		quotaProfile.setPkgData(pkgData);

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Creating Quota profile for the package id : " + pkgId);
		}

		dataServiceTypes = getDataServiceTypes();
			request.setAttribute(Attributes.PKG_ID, pkgId);
		return Results.CREATE.getValue();
	}

		setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
		try {
			quotaProfile = CRUDOperationUtil.get(QuotaProfileData.class, quotaProfileId);
			if(quotaProfile == null){
				return redirectError(Discriminators.QUOTA_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
			dataServiceTypes = getDataServiceTypes();
			List<QuotaProfileDetailData> lstQuotaProfileDetails = new ArrayList<QuotaProfileDetailData>();
			List<QuotaProfileDetailData> serviceLevelQuotaProfileDetails = getServiceWiseQuotaProfileDetails(quotaProfile);
			quotaProfile.setFupLevelMap(getDefaultServices(quotaProfile.getQuotaProfileDetailDatas()));

			for (QuotaProfileDetailData detailData : quotaProfile.getFupLevelMap().values()) {
				lstQuotaProfileDetails.add(detailData);
			}
			quotaProfile.setQuotaProfileDetailDatas(lstQuotaProfileDetails);
			request.setAttribute("serviceLevelDataForQuotaProfileDetails", serviceLevelQuotaProfileDetails);
			return Results.UPDATE.getValue();

		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while fetching Quota Profile data for update operation." ,ActionMessageKeys.VIEW_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}

	}

	private String getQuotaprofileId() {

		String quotaProfileId = request.getParameter(Attributes.QUOTA_PROFILE_ID);
		if (Strings.isNullOrBlank(quotaProfileId)) {
			quotaProfileId = (String) request.getAttribute(Attributes.QUOTA_PROFILE_ID);
			if (Strings.isNullOrBlank(quotaProfileId)) {
				quotaProfileId = request.getParameter("quotoProfile.id");
			}
		}
		return quotaProfileId;
	}

	private String getPkgId() {

		String pkgId = request.getParameter(Attributes.PKG_ID);
		if (Strings.isNullOrBlank(pkgId)) {
			pkgId = (String) request.getAttribute(Attributes.PKG_ID);
			if (Strings.isNullOrBlank(pkgId)) {
				pkgId = request.getParameter("quotaProfile.pkgData.id");
			}
		}
		return pkgId;
	}

	/**
	 * Create Quota profile with its details
	 *
	 * @return result code as success or failure
	 */
	@InputConfig(resultName = InputConfigConstants.CREATE)
	public String create() {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called create()");
		}
		setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
		try {
			pkgId = getPkgId();
			PkgData pkgData = CRUDOperationUtil.get(PkgData.class,pkgId);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgData.getId());

			quotaProfile.setPkgData(pkgData);

			List<QuotaProfileDetailData> quotaProfiles = quotaProfile.getQuotaProfileDetailDatas();
			quotaProfile.setQuotaProfileDetailDatas(getQuotaProfileDetails(quotaProfiles));
			quotaProfile.setCreatedDateAndStaff(getStaffData());
			CRUDOperationUtil.save(quotaProfile);
			String message = Discriminators.QUOTA_PROFILE + " <b><i>" + quotaProfile.getName() + "</i></b> " + "Created";
			CRUDOperationUtil.audit(quotaProfile,quotaProfile.getPkgData().getName(),AuditActions.CREATE, getStaffData(), request.getRemoteAddr(),quotaProfile.getHierarchy(), message);

			request.setAttribute(Attributes.QUOTA_PROFILE_ID,quotaProfile.getId());
			request.setAttribute(Attributes.PKG_ID, pkgId);
			MessageFormat messageFormat = new MessageFormat(getText("create.success"));
			addActionMessage(messageFormat.format(messageParameter));

			return Results.REDIRECT_TO_PARENT.getValue();

		} catch (Exception ex) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			return generateErrorLogsAndRedirect(ex, "Failed to Create Quota Profile." ,ActionMessageKeys.CREATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}

	}




	private List<QuotaProfileDetailData> getQuotaProfileDetails(List<QuotaProfileDetailData> quotaProfileDetails) {
		List<QuotaProfileDetailData> quotaProfileDetailDatas = Collectionz.newArrayList();
		for (QuotaProfileDetailData  quotaProfileDetail : quotaProfileDetails) {
			if (isNull(quotaProfileDetail)) {
				continue;
			}
			if (quotaProfileDetail.getServiceId() == null) {
				setAnyService(quotaProfileDetail);
			} else {
				DataServiceTypeData dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class, quotaProfileDetail.getServiceId());
				quotaProfileDetail.setDataServiceTypeData(dataServiceTypeData);
			}
			quotaProfileDetail.setQuotaProfile(quotaProfile);
			quotaProfileDetailDatas.add(quotaProfileDetail);
		}
		return quotaProfileDetailDatas;
	}

	private void setAnyService(QuotaProfileDetailData quotaProfileDetail) {
		DataServiceTypeData anyServiceData = CRUDOperationUtil.get(DataServiceTypeData.class, CommonConstants.ALL_SERVICE_ID);
		quotaProfileDetail.setDataServiceTypeData(anyServiceData);
		quotaProfileDetail.setAggregationKey(AggregationKey.BILLING_CYCLE.name());
	}

	/**
	 * Checks whether service is deleted from UI or not
	 *
	 * @param quotaProfileDetail
	 * @return boolean
	 */
	private static boolean isNull(QuotaProfileDetailData quotaProfileDetail) {
		return quotaProfileDetail == null;
	}

	@SkipValidation
	public String view(){
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE,"Method called view()");
		}
		String quotaProfileId = null;
		setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
		try{
			quotaProfileId = getQuotaprofileId();

			quotaProfile = CRUDOperationUtil.get(QuotaProfileData.class,quotaProfileId);
            if(quotaProfile == null) {
				addActionError("Failed to display full details of quota Profile");
				return Results.REDIRECT_ERROR.getValue();
			}
			pkgId = quotaProfile.getPkgData().getId();
			createFupLevelServiceQuotaMap(quotaProfile);
			quotaProfile.setGroupNames(GroupDAO.getGroupNames(SPLITTER.split(quotaProfile.getPkgData().getGroups())));
		}catch(Exception e){
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			return generateErrorLogsAndRedirect(e, "Failed to view quota profile: "+ quotaProfileId +"." ,ActionMessageKeys.VIEW_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
		return Results.DETAIL.getValue();
	}

	@SkipValidation
	public String delete(){
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called delete()");
		}
		setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
		String quotaProfileId = getQuotaprofileId();
		try {
			if(Strings.isNullOrEmpty(quotaProfileId) == true){
				return redirectError(Discriminators.QUOTA_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
			quotaProfile = CRUDOperationUtil.get(QuotaProfileData.class, quotaProfileId);
			if(quotaProfile==null){
				return redirectError(Discriminators.QUOTA_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
			PkgData pkgData = quotaProfile.getPkgData();
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgData.getId());
			pkgId = pkgData.getId();

			String message = Discriminators.QUOTA_PROFILE + " <b><i>" + quotaProfile.getName() + "</i></b> " + "Deleted";
			if(Collectionz.isNullOrEmpty(quotaProfile.getQosProfiles())){
				CRUDOperationUtil.audit(quotaProfile,quotaProfile.getPkgData().getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),quotaProfile.getHierarchy(), message);
				quotaProfile.setStatus(CommonConstants.STATUS_DELETED);
				for(UsageNotificationData usageNotificationData : quotaProfile.getUsageNotificationDatas()){
					usageNotificationData.setStatus(CommonConstants.STATUS_DELETED);
				}
			}else{
				for(QosProfileData qosProfileData : quotaProfile.getQosProfiles()){
					if(qosProfileData.getStatus().equals(CommonConstants.STATUS_DELETED)){
						CRUDOperationUtil.audit(quotaProfile,quotaProfile.getPkgData().getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),quotaProfile.getHierarchy(), message);
						quotaProfile.setStatus(CommonConstants.STATUS_DELETED);
					}else{
						addActionError(Discriminators.QUOTA_PROFILE + " " +getText(ActionMessageKeys.DELETE_FAILURE.key));
						addActionError("QosProfile is Configured with '"+ quotaProfile.getName() + "' " + Discriminators.QUOTA_PROFILE);
						return Results.REDIRECT_ERROR.getValue();

					}
				}
			}
			MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.REDIRECT_TO_PARENT.getValue();
		} catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			return generateErrorLogsAndRedirect(e, "Error while fetching QuotaProfile data for delete operation." ,ActionMessageKeys.DELETE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	/**
	 * It will delete last fupLevel
	 * @return String
	 */
	@SkipValidation
	public String deleteFupLevels(){
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called deleteLevels()");
		}
		String fupLevel = request.getParameter(Attributes.FUP_LEVEL);
		String qoutaProfileId = request.getParameter(Attributes.QUOTA_PROFILE_ID);
		try{
			if(Strings.isNullOrEmpty(fupLevel) == false){
				QuotaProfileData quotaProfileData = new QuotaProfileData();
				quotaProfileData.setId(qoutaProfileId);

				List<QuotaProfileDetailData> quotaProfileDetailDatas = QuotaProfileDAO.getFupLevelQuotaProfileDetails(QuotaProfileDetailData.class, fupLevel, quotaProfileData);
				if(Collectionz.isNullOrEmpty(quotaProfileDetailDatas) == false){
					quotaProfileData = quotaProfileDetailDatas.get(0).getQuotaProfile();
					JsonObject jsonObjectOld = quotaProfileData.toJson();
					quotaProfileData.getQuotaProfileDetailDatas().removeAll(quotaProfileDetailDatas);
					for(QuotaProfileDetailData quotaProfileDetailData : quotaProfileDetailDatas){
						CRUDOperationUtil.delete(quotaProfileDetailData);
					}
					JsonObject jsonObjectNew = quotaProfileData.toJson();
					String message = Discriminators.QUOTA_PROFILE + " <b><i>" + quotaProfileData.getName() + "</i></b> " + "Updated";
					JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);
					CRUDOperationUtil.audit(quotaProfileData,quotaProfileData.getPkgData().getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference ,quotaProfileData.getHierarchy(), message);
				}
			}else {
				return redirectError(Discriminators.QUOTA_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}

			Object [] messageParameterFupLevel = {FieldValueConstants.FUP_LEVEL};
			MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
			addActionMessage(messageFormat.format(messageParameterFupLevel));
			setActionChainUrl(Results.VIEW.getValue());
			return Results.DISPATCH_VIEW.getValue();
		}catch(Exception e){
			return generateErrorLogsAndRedirect(e, "Error while fetching QuotaProfile data for delete operation." ,ActionMessageKeys.DELETE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	/**
	 * It will return the default service which has aggregation key "any"
	 * @param quotaProfileDetailDatas
	 * @return Map<Integer,QuotaProfileDetail>
	 */
	private Map<Integer,QuotaProfileDetailData> getDefaultServices(List<QuotaProfileDetailData> quotaProfileDetailDatas){
		Map<Integer, QuotaProfileDetailData> fupLevelDetailsMap = new HashMap<Integer, QuotaProfileDetailData>();
		for(QuotaProfileDetailData  profileDetail : quotaProfileDetailDatas){
			if(isDefaultService(profileDetail)){
				fupLevelDetailsMap.put(profileDetail.getFupLevel(), profileDetail);
			}
		}
		return fupLevelDetailsMap;
	}

	public QuotaProfileData getQuotaProfile() {
		return quotaProfile;
	}

	public void setQuotaProfile(QuotaProfileData quotaProfile) {
		this.quotaProfile = quotaProfile;
	}

	@Override
	public QuotaProfileData getModel() {
		return quotaProfile;
	}

	private List<DataServiceTypeData> getDataServiceTypes() {
		return CRUDOperationUtil.findAllWhichIsNotDeleted(DataServiceTypeData.class);

	}

	/**
	 * This method creates the list for QuotaProfileWrapper i.e. List of FUP levels 
	 * for current quotaprofile 
	 *
	 * @return
	 */
	@SkipValidation
	public String viewQuotaProfileDetail(){

		LogManager.getLogger().info(MODULE, "Inside viewQuotaProfileDetail");

		String tableId = request.getParameter("tableId");
		String rowData = request.getParameter("rowData"+tableId);

		Gson gson = GsonFactory.defaultInstance();

		QuotaProfileWrapper currentRowData = gson.fromJson(rowData, QuotaProfileWrapper.class);

		LogManager.getLogger().info(MODULE,"Quota Profile Wrapper/current row Data "+ currentRowData);

		QuotaProfileData quotaProfileForDetailPage = CRUDOperationUtil.get(QuotaProfileData.class, currentRowData.getId());

		if(quotaProfileForDetailPage != null){
			LogManager.getLogger().info(MODULE,"Quota Profile size ");
			createFupLevelList(quotaProfileForDetailPage);
		}

		return QUOTAPROFILE_DETAIL_VIEW;
	}

	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update(){
		LogManager.getLogger().debug(MODULE, "Method called update()");
		setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
		try {
			LogManager.getLogger().debug(MODULE, "Quota Profile Id: "+quotaProfile.getId());
			pkgId = quotaProfile.getPkgData().getId();
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			List<QuotaProfileDetailData> quotaProfileDetails = getQuotaProfileDetails(quotaProfile.getQuotaProfileDetailDatas());
			quotaProfile.setQuotaProfileDetailDatas(quotaProfileDetails);
			QuotaProfileData quotaProfileInDb = CRUDOperationUtil.get(QuotaProfileData.class,quotaProfile.getId());
			JsonObject oldJsonObject = quotaProfileInDb.toJson();

			quotaProfileInDb.getQuotaProfileDetailDatas().clear();
			quotaProfileInDb.getQuotaProfileDetailDatas().addAll(quotaProfileDetails);
			quotaProfileInDb.setName(quotaProfile.getName());
			quotaProfileInDb.setDescription(quotaProfile.getDescription());
			quotaProfileInDb.setUsagePresence(quotaProfile.getUsagePresence());
			quotaProfileInDb.setBalanceLevel(quotaProfile.getBalanceLevel());
			quotaProfileInDb.setRenewalInterval(quotaProfile.getRenewalInterval());
			quotaProfileInDb.setRenewalIntervalUnit(quotaProfile.getRenewalIntervalUnit());

			quotaProfileInDb.setModifiedDateAndStaff(getStaffData());
			CRUDOperationUtil.update(quotaProfileInDb);
			JsonObject jsonObjectForNew= quotaProfileInDb.toJson();
			JsonArray difference = ObjectDiffer.diff(oldJsonObject,jsonObjectForNew);
			String message = Discriminators.QUOTA_PROFILE + " <b><i>" + quotaProfileInDb.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(quotaProfileInDb,quotaProfileInDb.getPkgData().getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference ,quotaProfileInDb.getHierarchy(), message);
			request.setAttribute(Attributes.QUOTA_PROFILE_ID,quotaProfile.getId());
			request.setAttribute(Attributes.PKG_ID, pkgId);

			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.REDIRECT_TO_PARENT.getValue();

		}catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			return generateErrorLogsAndRedirect(e, "Error while Update operation." ,ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}

	}

	private List<QuotaProfileDetailData> getServiceWiseQuotaProfileDetails(QuotaProfileData quotaProfile) {
		List<QuotaProfileDetailData> lstQuotaProfileDetails = new ArrayList<QuotaProfileDetailData>();
		for(QuotaProfileDetailData  detail : quotaProfile.getQuotaProfileDetailDatas()){
			if(isDefaultService(detail) == false){
				detail.setServiceId(detail.getDataServiceTypeData().getName());
				lstQuotaProfileDetails.add(detail);
			}
		}
		return lstQuotaProfileDetails;
	}


	private boolean isDefaultService(QuotaProfileDetailData  detail){
		return CommonConstants.ALL_SERVICE_ID.equalsIgnoreCase(detail.getDataServiceTypeData().getId()) &&
				detail.getAggregationKey().equalsIgnoreCase(AggregationKey.BILLING_CYCLE.name());

	}

	public List<QuotaProfileWrapper> getFupLevelList() {
		return fupLevelList;
	}


	public void setFupLevelList(List<QuotaProfileWrapper> quotaProfileWrappers) {
		this.fupLevelList = quotaProfileWrappers;

	}


	private void createFupLevelList (QuotaProfileData quotaProfile) {

		if (Collectionz.isNullOrEmpty(quotaProfile.getQuotaProfileDetailDatas()) == true) {
			return;
		}

		for (QuotaProfileDetailData quotaProfileDetail : quotaProfile.getQuotaProfileDetailDatas()) {
			if (quotaProfileDetail.getFupLevel() == 0) {
				continue;
			}
			if (CommonConstants.ALL_SERVICE_ID.equalsIgnoreCase(quotaProfileDetail.getDataServiceTypeData().getId()) == false) {
				continue;
			}
			Map<String, QuotaProfileDetailData> aggregationKeyMap = fupLevelDetailMap.computeIfAbsent(quotaProfileDetail.getFupLevel(), k -> Maps.newHashMap());
			aggregationKeyMap.put(quotaProfileDetail.getAggregationKey(), quotaProfileDetail);
		}

	}


	private void createFupLevelServiceQuotaMap(QuotaProfileData quotaProfile){

		if(Collectionz.isNullOrEmpty(quotaProfile.getQuotaProfileDetailDatas()) == true){
			return ;
		}

		Map<Integer,List<QuotaProfileDetailData>> tempMap= new HashMap<Integer, List<QuotaProfileDetailData>>();
		Map<Integer, QuotaProfileDetailData> fupLevelDetailsMap = new HashMap<Integer, QuotaProfileDetailData>();
		quotaProfileDetailJsonMap= new HashMap<Integer,JsonArray>();
		for(QuotaProfileDetailData  quotaProfileDetail : quotaProfile.getQuotaProfileDetailDatas()){
			if(tempMap.get(quotaProfileDetail.getFupLevel())==null){
				tempMap.put(quotaProfileDetail.getFupLevel(), Collectionz.<QuotaProfileDetailData>newLinkedList());
			}
			if(isDefaultService(quotaProfileDetail)){
				fupLevelDetailsMap.put(quotaProfileDetail.getFupLevel(), quotaProfileDetail);
			}else{
				quotaProfileDetail.setServiceId(quotaProfileDetail.getDataServiceTypeData().getId());
				quotaProfileDetail.setServiceName(quotaProfileDetail.getDataServiceTypeData().getName());
					tempMap.get(quotaProfileDetail.getFupLevel()).add(quotaProfileDetail);
				}
			}
		setQuotaProfileDetailJsonMapValue(tempMap);
		quotaProfile.setFupLevelMap(fupLevelDetailsMap);
	}

	private void setQuotaProfileDetailJsonMapValue(Map<Integer, List<QuotaProfileDetailData>> tempMap) {
		Gson gson = new GsonBuilder().registerTypeAdapter(Long.class, new LongToStringGsonAdapter()).create();
		quotaProfileDetailJsonMap= new HashMap<Integer,JsonArray>();
		ServiceNameComparator comparator = new ServiceNameComparator();
		for (Entry<Integer, List<QuotaProfileDetailData>> fupLevelDetail : tempMap.entrySet()) {
			Collections.sort(fupLevelDetail.getValue(),comparator);
			quotaProfileDetailJsonMap.put(fupLevelDetail.getKey(),gson.toJsonTree(fupLevelDetail.getValue()).getAsJsonArray());
		}
       setQuotaProfileDetailJsonMap(quotaProfileDetailJsonMap);
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String urlAction) {
		this.actionChainUrl = urlAction;

	}

	public Map<Integer, JsonArray> getQuotaProfileDetailJsonMap() {
		return quotaProfileDetailJsonMap;
	}

	public void setQuotaProfileDetailJsonMap(
			Map<Integer, JsonArray> quotaProfileDetailJsonMap) {
		this.quotaProfileDetailJsonMap = quotaProfileDetailJsonMap;
	}

	private String generateErrorLogsAndRedirect(Exception e, String message, String key, String result) {
		return super.generateErrorLogsAndRedirect(Discriminators.QOS_PROFILE, e, message, key, result);
	}

	private class ServiceNameComparator implements Comparator<QuotaProfileDetailData> {
		@Override
		public int compare(QuotaProfileDetailData o1, QuotaProfileDetailData o2) {
			return o1.getDataServiceTypeData().getName().compareTo(o2.getDataServiceTypeData().getName());
		}
	}

	@Override
	protected List<QuotaProfileData> getSearchResult(String criteriaJson,Class<QuotaProfileData> beanType, int startIndex, int maxRecords,String sortColumnName, String sortColumnOrder,String staffBelongingGroups) throws Exception {
		return CRUDOperationUtil.searchByNameAndStatus(beanType, criteriaJson, startIndex, maxRecords, sortColumnName, sortColumnOrder, staffBelongingGroups);
	}

	@Override
	public String getIncludeProperties(){
		return QUOTA_INCLUDE_PARAMETERS;
	}

	public Map<Integer, Map<String, QuotaProfileDetailData>> getFupLevelDetailMap() {
		return fupLevelDetailMap;
	}

	public void setFupLevelDetailMap(Map<Integer, Map<String, QuotaProfileDetailData>> fupLevelDetailMap) {
		this.fupLevelDetailMap = fupLevelDetailMap;
	}
}