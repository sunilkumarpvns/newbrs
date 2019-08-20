package com.elitecore.nvsmx.policydesigner.controller.qos;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyType;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.qos.TimePeriodData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.policydesigner.model.pkg.qos.QosProfileDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.qos.QosProfileDetailWrapper;
import com.elitecore.nvsmx.policydesigner.model.pkg.quota.QuotaProfileDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.rnc.RncProfileDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.syquota.SyQuotaProfileDAO;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QosProfileCTRL extends EliteGenericCTRL<QosProfileData> {

	private static final String MANAGE_ORDER = "manageOrder";
	private static final String QOS_PROFILE_MANAGE_ORDER_FAILURE = "qos.manage.order.failure";
	private static final long serialVersionUID = 1L;
	private static final String MODULE = QosProfileCTRL.class.getSimpleName();
	private static final String QOSPROFILEDETAIL = " QosProfileDetail ";
	private QosProfileData qosProfile = new QosProfileData();
	private String pkgId;
	private List quotaProfileList = Collectionz.newArrayList();
	private List<DataRateCardData> dataRateCardList = Collectionz.newArrayList();
	private List<String> pcrfKeyValueConstants = Collectionz.newArrayList();
	private List<String> showSelectedAccessNetworkForUpdate = Collectionz.newArrayList();
	public static final String QOS_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.pkgData,dataList\\[\\d+\\]\\.pkgData.name,dataList\\[\\d+\\]\\.pkgData.type,dataList\\[\\d+\\]\\.quotaProfile,dataList\\[\\d+\\]\\.quotaProfile.name";
	Object [] messageParameter = {Discriminators.QOS_PROFILE};
	private static final Predicate<PCCRuleData> GLOBALPCCRULEFILTER =  new Predicate<PCCRuleData>() {
		@Override
		public boolean apply(PCCRuleData pccRuleData) {
			if(pccRuleData.getScope()== PCCRuleScope.GLOBAL) {
				return false;
			}
			return true;
		}
	};
	private QosProfileDetailData qosProfileDetail = new QosProfileDetailData();

	private String actionChainUrl;

	public String viewQosProfileData() {
		try{
			String tableId = request.getParameter(Attributes.TABLE_ID);
			String rowData = request.getParameter(Attributes.ROW_DATA + tableId);

			Gson gson = GsonFactory.defaultInstance();
		
			QosProfileDetailWrapper currentRowData = gson.fromJson(rowData, QosProfileDetailWrapper.class);
			String qosProfileId;
	        if(currentRowData != null){
                 qosProfileId = currentRowData.getId();
			}else{
				qosProfileId = getQosProfileId();
			}
			if(Strings.isNullOrBlank(qosProfileId) == true ){
				return "qosProfileDetailView";
			}
			qosProfile = CRUDOperationUtil.get(QosProfileData.class, qosProfileId);
			if (qosProfile != null && Collectionz.isNullOrEmpty(qosProfile.getQosProfileDetailDataList()) == false) {
				filterHsqLevelQos(qosProfile);
				sortQosProfileDetails(qosProfile);
			}
			return "qosProfileDetailView";

		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Failed to view qos profile." ,ActionMessageKeys.VIEW_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	private void filterHsqLevelQos(QosProfileData qosProfileTemp) {
		Collectionz.filter(qosProfileTemp.getQosProfileDetailDataList(), new Predicate<QosProfileDetailData>() {
			@Override
			public boolean apply(QosProfileDetailData qosProfileDetail) {
				return qosProfileDetail.getFupLevel() != 0;
			}});

	}

	public String view() {
		if (LogManager.getLogger().isDebugLogLevel()) { 
			LogManager.getLogger().debug(MODULE, "Method called view()");
		}
		String qosProfileId = getQosProfileId();
		
		setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
		try {
			   if(Strings.isNullOrEmpty(qosProfileId) == true) {
				   return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			   }
				qosProfile = CRUDOperationUtil.get(QosProfileData.class, qosProfileId);
                if(qosProfile==null){
					return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
				}
				pkgId = qosProfile.getPkgData().getId();
				qosProfile.setGroupNames(GroupDAO.getGroupNames(SPLITTER.split(qosProfile.getGroups())));
				sortQosProfileDetails(qosProfile);
				Gson gson = GsonFactory.defaultInstance();
				JsonArray timePeriodjson = gson.toJsonTree(qosProfile.getTimePeriodDataList(),new TypeToken<List<TimePeriodData>>() {}.getType()).getAsJsonArray();
				request.setAttribute(Attributes.TIME_PERIOD, timePeriodjson);
				return Results.DETAIL.getValue();

 		} catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			return generateErrorLogsAndRedirect(e, "Failed to view qos profile." ,ActionMessageKeys.VIEW_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}

	}



	@SuppressWarnings("unchecked")
	private void sortQosProfileDetails(QosProfileData qosProfileData){
		List<QosProfileDetailData> qosProfileDetailDatas = new ArrayList<QosProfileDetailData>();
		if(Collectionz.isNullOrEmpty(qosProfileData.getQosProfileDetailDataList())==false){
			qosProfileDetailDatas.addAll(qosProfileData.getQosProfileDetailDataList());
			qosProfileData.setQosProfileDetailDataList(qosProfileDetailDatas);
		}
		Collections.sort(qosProfileDetailDatas, new SortMe());
	}

	private String getQosProfileId() {

		String qosProfileId = request.getParameter(Attributes.QOS_PROFILE_ID);
		if (Strings.isNullOrBlank(qosProfileId)) {
			qosProfileId = (String) request.getAttribute(Attributes.QOS_PROFILE_ID);
			if (Strings.isNullOrBlank(qosProfileId)) {
				qosProfileId = request.getParameter("qosProfile.id");
			}
		}
		return qosProfileId;
	}

	private String getFUPLevelFromRequest(){
		String fupLevel = request.getParameter("fuplevel");
		if(Strings.isNullOrBlank(fupLevel)){
			fupLevel = request.getParameter("qosProfileDetail.fupLevel");
		}
		return fupLevel;
	}

	private String getPkgIdFromRequest() {

		String pkgId = request.getParameter(Attributes.PKG_ID);
		if (Strings.isNullOrBlank(pkgId)) {
			pkgId = (String) request.getAttribute(Attributes.PKG_ID);
			if (Strings.isNullOrBlank(pkgId)) {
				pkgId = request.getParameter("quotaProfile.pkgData.id");
			}
		}
		return pkgId;
	}


	public String init() {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called init()");
		}
		String qosProfileId = getQosProfileId();

		if(Strings.isNullOrBlank(qosProfileId)){
			try{
					pkgId = getPkgIdFromRequest();

				PkgData pkgData = CRUDOperationUtil.get(PkgData.class,pkgId);
				qosProfile.setDescription(NVSMXUtil.getDefaultDescription(request));
				qosProfile.setPkgData(pkgData);

				if (QuotaProfileType.USAGE_METERING_BASED.equals(pkgData.getQuotaProfileType())) {
					setQuotaProfileList(QuotaProfileDAO.getQuotaProfileOfPkgData(pkgData));
				} else if(QuotaProfileType.SY_COUNTER_BASED.equals(pkgData.getQuotaProfileType())){
					setQuotaProfileList(SyQuotaProfileDAO.getSyQuotaProfileDetails(pkgId));
				} else if(QuotaProfileType.RnC_BASED.equals(pkgData.getQuotaProfileType())){
					setQuotaProfileList(RncProfileDAO.getRncProfileDetails(pkgData));
					setDataRateCardList(QosProfileDAO.getDataRateCardDataList(pkgData));
				}

				setPcrfKeyValueConstants(getAccessNetworkAutoSuggestions());
				request.setAttribute(Attributes.ADVANCE_CONDITIONS, getAdvanceConditionAutoSuggestions());

				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Creating Qos profile for the package id : " + pkgId);
				}
				return Results.CREATE.getValue();
			} catch (Exception e) {
				return generateErrorLogsAndRedirect(e, "Failed to view create page of Qos Profile." ,ActionMessageKeys.CREATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
			}
		}

		String fromViewPage = request.getParameter(Attributes.FROM_VIEW_PAGE);
		try {
			if (Strings.isNullOrBlank(fromViewPage)) {
				setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			} else {
				setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
			}
			qosProfile = CRUDOperationUtil.get(QosProfileData.class, qosProfileId);
			if (qosProfile == null) {
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
			if (QuotaProfileType.USAGE_METERING_BASED.equals(qosProfile.getPkgData().getQuotaProfileType())) {
				if (qosProfile.getQuotaProfile() != null) {
					qosProfile.setQuotaProfileId(qosProfile.getQuotaProfile().getId());
				}
				setQuotaProfileList(QuotaProfileDAO.getQuotaProfileOfPkgData(qosProfile.getPkgData()));
			} else if(QuotaProfileType.SY_COUNTER_BASED.equals(qosProfile.getPkgData().getQuotaProfileType())) {
				if (qosProfile.getSyQuotaProfileData() != null) {
					qosProfile.setQuotaProfileId(qosProfile.getSyQuotaProfileData().getId());
				}
				setQuotaProfileList(SyQuotaProfileDAO.getSyQuotaProfileDetails(qosProfile.getPkgData().getId()));
			} else if(QuotaProfileType.RnC_BASED.equals(qosProfile.getPkgData().getQuotaProfileType())) {
				if (qosProfile.getRncProfileData() != null) {
					qosProfile.setQuotaProfileId(qosProfile.getRncProfileData().getId());
				}
				setQuotaProfileList(RncProfileDAO.getRncProfileDetails(qosProfile.getPkgData()));

				if(qosProfile.getRateCardData() != null) {
					qosProfile.setRateCardId(qosProfile.getRateCardData().getId());
				}

				setDataRateCardList(QosProfileDAO.getDataRateCardDataList(qosProfile.getPkgData()));
			}

			qosProfile.setTimePeriodDataList(qosProfile.getTimePeriodDataList());

			setShowSelectedAccessNetworkForUpdate(Strings.splitter(',').trimTokens().split(qosProfile.getAccessNetwork()));
			setPcrfKeyValueConstants(getAccessNetworkAutoSuggestions());
			request.setAttribute("advanceConditions", getAdvanceConditionAutoSuggestions());
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Updating Qos profile for the package id : " + pkgId);
			}
			sortQosProfileDetails(qosProfile);
			return Results.UPDATE.getValue();
		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Failed to Update Qos Profile." ,ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}

	}

	/**
	 * get all suggestions for the access network from PCRFKeyValueConstants 
	 * @return List of String
	 */
	private List<String> getAccessNetworkAutoSuggestions() {
		List<String> pcrfKeyValueConstants = Collectionz.newArrayList();
		for(PCRFKeyValueConstants keyValueConstants : PCRFKeyValueConstants.values(PCRFKeyConstants.CS_ACCESS_NETWORK)){
			pcrfKeyValueConstants.add(keyValueConstants.val);
		}
		return pcrfKeyValueConstants;
	}

	/**
	 * get all suggestions for the advance condition from PCRFKeyConstants
	 * @return json String
	 */
	private String getAdvanceConditionAutoSuggestions() {
		Gson gson = GsonFactory.defaultInstance();
		List<PCRFKeyConstants> pcrfKeyConstants = PCRFKeyConstants.values(PCRFKeyType.RULE);
		String [] autoSuggestion = new String[pcrfKeyConstants.size()];
		short index= 0;
		for(PCRFKeyConstants keyConstants : pcrfKeyConstants){    	
			autoSuggestion[index] = keyConstants.getVal();
			index++;
		}
		return gson.toJson(autoSuggestion);
	}


	@InputConfig(resultName = InputConfigConstants.CREATE)
	public String create(){
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called Create()");
		} 
		if (LogManager.getLogger().isInfoLogLevel()){
			LogManager.getLogger().info(MODULE, "creating Qos Profile with name : "+qosProfile.getName() + "\n"+qosProfile.toString());
		}
		try {
			pkgId = getPkgIdFromRequest();
			PkgData pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);

			if(Strings.isNullOrEmpty(qosProfile.getQuotaProfileId())==false){
				if (QuotaProfileType.USAGE_METERING_BASED.equals(pkgData.getQuotaProfileType())) {
					QuotaProfileData quotaProfileToBeSet = new QuotaProfileData();
					quotaProfileToBeSet.setId(qosProfile.getQuotaProfileId());
					qosProfile.setQuotaProfile(quotaProfileToBeSet);
				} else if (QuotaProfileType.SY_COUNTER_BASED.equals(pkgData.getQuotaProfileType())) {
					SyQuotaProfileData syQuotaProfileDataToBeSet = new SyQuotaProfileData();
					syQuotaProfileDataToBeSet.setId(qosProfile.getQuotaProfileId());
					qosProfile.setSyQuotaProfileData(syQuotaProfileDataToBeSet);
				} else if (QuotaProfileType.RnC_BASED.equals(pkgData.getQuotaProfileType())) {
					RncProfileData rncProfileDataToBeSet = new RncProfileData();
					rncProfileDataToBeSet.setId(qosProfile.getQuotaProfileId());
					qosProfile.setRncProfileData(rncProfileDataToBeSet);
				}
			}

			if(Strings.isNullOrBlank(qosProfile.getRateCardId()) == false){
				qosProfile.setRateCardData(CRUDOperationUtil.get(DataRateCardData.class,qosProfile.getRateCardId()));
			}
			qosProfile.setPkgData(pkgData);
			setTimePeriods(qosProfile);
			Integer highestOrderNumber = getHighestOrderNumber(pkgData);
			if(highestOrderNumber==null){
				highestOrderNumber = 0;
			}
			Integer orderNo = highestOrderNumber + 1;
			qosProfile.setOrderNo(orderNo);
			qosProfile.setCreatedDateAndStaff(getStaffData());

			CRUDOperationUtil.save(qosProfile);
			String message = Discriminators.QOS_PROFILE + " <b><i>" + qosProfile.getName() + "</i></b> " + "Created";
			CRUDOperationUtil.audit(qosProfile,qosProfile.getPkgData().getName(),AuditActions.CREATE, getStaffData(), request.getRemoteAddr(),qosProfile.getHierarchy(), message);
			
			request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfile.getId());
			request.setAttribute(Attributes.PKG_ID, pkgId);
			setActionChainUrl(Results.VIEW.getValue());
			MessageFormat messageFormat = new MessageFormat(getText("create.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.DISPATCH_VIEW.getValue();
			
 		}catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			return generateErrorLogsAndRedirect(e, "Failed to Create Qos Profile." ,ActionMessageKeys.CREATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());

		}
	}

	public Integer getHighestOrderNumber(PkgData pkgData){
		List<QosProfileData> qosProfiles = pkgData.getQosProfiles();
		if(Collectionz.isNullOrEmpty(qosProfiles)){
			return 0;
		}
		Collections.sort(qosProfiles,new OrderComparator());
		return qosProfiles.get(0).getOrderNo();
	}

	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update(){
		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called update()");
		} 
		try {
			QosProfileData qosProfileInDB = CRUDOperationUtil.get(QosProfileData.class, qosProfile.getId());
			setTimePeriods(qosProfile);
			if(Strings.isNullOrEmpty(qosProfile.getQuotaProfileId())==false){
				if (QuotaProfileType.USAGE_METERING_BASED.equals(qosProfileInDB.getPkgData().getQuotaProfileType())) {
					QuotaProfileData quotaProfileToBeSet = new QuotaProfileData();
					quotaProfileToBeSet.setId(qosProfile.getQuotaProfileId());
					qosProfile.setQuotaProfile(quotaProfileToBeSet);
				} else if (QuotaProfileType.SY_COUNTER_BASED.equals(qosProfileInDB.getPkgData().getQuotaProfileType())) {
					SyQuotaProfileData syQuotaProfileDataToBeSet = new SyQuotaProfileData();
					syQuotaProfileDataToBeSet.setId(qosProfile.getQuotaProfileId());
					qosProfile.setSyQuotaProfileData(syQuotaProfileDataToBeSet);
				} else if (QuotaProfileType.RnC_BASED.equals(qosProfileInDB.getPkgData().getQuotaProfileType())) {
					RncProfileData rncProfileDataToBeSet = new RncProfileData();
					rncProfileDataToBeSet.setId(qosProfile.getQuotaProfileId());
					qosProfile.setRncProfileData(rncProfileDataToBeSet);
				}
			}
			if(Strings.isNullOrBlank(qosProfile.getRateCardId()) == false){
				qosProfile.setRateCardData(CRUDOperationUtil.get(DataRateCardData.class,qosProfile.getRateCardId()));
			}
			pkgId = qosProfileInDB.getPkgData().getId();
			JsonObject  jsonOldObject = qosProfileInDB.toJson();
			if(LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Updating Qos Profile with name : "+qosProfile.getName() + "\n"+qosProfile.toString());
			}
			qosProfileInDB.setName(qosProfile.getName());
			qosProfileInDB.setDescription(qosProfile.getDescription());
			qosProfileInDB.setAccessNetwork(qosProfile.getAccessNetwork());
			qosProfileInDB.setAdvancedCondition(qosProfile.getAdvancedCondition());
			qosProfileInDB.setDuration(qosProfile.getDuration());
			qosProfileInDB.setQuotaProfile(qosProfile.getQuotaProfile());
			qosProfileInDB.setSyQuotaProfileData(qosProfile.getSyQuotaProfileData());
			qosProfileInDB.setRncProfileData(qosProfile.getRncProfileData());
			qosProfileInDB.setRateCardData(qosProfile.getRateCardData());
			qosProfileInDB.getTimePeriodDataList().clear();
			qosProfileInDB.getTimePeriodDataList().addAll(qosProfile.getTimePeriodDataList());
			qosProfileInDB.setModifiedDateAndStaff(getStaffData());
			CRUDOperationUtil.update(qosProfileInDB);
			JsonObject  jsonNewObject = qosProfileInDB.toJson();
			JsonArray difference = ObjectDiffer.diff(jsonOldObject, jsonNewObject);
			String message = Discriminators.QOS_PROFILE + " <b><i>" + qosProfileInDB.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(qosProfileInDB,qosProfileInDB.getPkgData().getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference , qosProfileInDB.getHierarchy(), message);
			request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfile.getId());
			request.setAttribute(Attributes.PKG_ID, pkgId);

			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			setActionChainUrl(Results.VIEW.getValue());
			return Results.DISPATCH_VIEW.getValue();
 		} catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			return generateErrorLogsAndRedirect(e, "Failed to Update Qos Profile." ,ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}


	public String initCreateHSQDetail(){
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called initCreateHSQDetail()");
		}
		String qosProfileId = getQosProfileId();
		String fupLevelValue = getFUPLevelFromRequest();
		setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
		try{
			if(Strings.isNullOrEmpty(qosProfileId) == false && Strings.isNullOrEmpty(fupLevelValue) == false){
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Creating HSQ information for Qos Profile Id : " + qosProfileId);
				}
				
				if(Strings.isNullOrEmpty(fupLevelValue)==false){
					getQosProfileDetail().setFupLevel(Integer.parseInt(fupLevelValue));
				}
				
				qosProfile = CRUDOperationUtil.get(QosProfileData.class,qosProfileId);
 				
				request.getSession().setAttribute("qosProfile", qosProfile);
				request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfileId);
				return Results.CREATE_DETAIL.getValue();
			}else{
				addActionError(QOSPROFILEDETAIL + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
				addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
				return Results.REDIRECT_ERROR.getValue();
			}
 		}catch(Exception e){
			return generateErrorLogsAndRedirect(e, "Error while fetching QosProfile data for create operation." ,ActionMessageKeys.CREATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
		
	}

	public String initUpdateHSQDetail(){
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called initUpdateHSQDetail()");
		}
		String qosProfileDetailId = request.getParameter(Attributes.QOS_PROFILE_DETAIL_ID);
		setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
		try{
			if(Strings.isNullOrEmpty(qosProfileDetailId) == false){
				qosProfileDetail = CRUDOperationUtil.get(QosProfileDetailData.class, qosProfileDetailId);
			}else{
				addActionError(QOSPROFILEDETAIL + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
				addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
				return Results.REDIRECT_ERROR.getValue();
			}
			return Results.UPDATE_DETAIL.getValue();
 		}catch(Exception e){
			return generateErrorLogsAndRedirect(e, "Error while fetching QosProfile data for update operation." ,ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	public String createQosDetailInformation(){
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "inside createQosDetailInformation() method");
		}
		
		String qosProfileId = getQosProfileId();
		setActionChainUrl(Results.VIEW.getValue());
		try{
			if(Strings.isNullOrEmpty(qosProfileId) == true){
				setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
				QosProfileData qosProfileInDB = CRUDOperationUtil.get(QosProfileData.class, qosProfileId);
				JsonObject jsonObjectOld = qosProfileInDB.toJson();
				qosProfileDetail.setQosProfile(qosProfileInDB);
				List<QosProfileDetailData> qosProfileDetails = qosProfileInDB.getQosProfileDetailDataList();
				if(Collectionz.isNullOrEmpty(qosProfileDetails) == false){
					QosProfileDetailData qosProfileDetailData = getQosProfileDetail();
					for(QosProfileDetailData qosProfileData : qosProfileDetails){
						Integer fupLevel = qosProfileData.getFupLevel();
						if(fupLevel == qosProfileDetailData.getFupLevel()){
							String fupLevelStr = getFUPLevel(fupLevel);
							LogManager.getLogger().error(MODULE,"Error while creating QosProfileDetail data for "+fupLevelStr+". Reason: "+ fupLevelStr +"is configured with the QosProfile");
							setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
							addActionError(Discriminators.QOS_PROFILE + " " +getText(ActionMessageKeys.CREATE_FAILURE.key));
							addActionError(fupLevelStr+" is already configured with QoSProfileDetail");
							return Results.REDIRECT_ERROR.getValue();
						}
					}
				}
				qosProfileInDB.getQosProfileDetailDataList().add(getQosProfileDetail());
				JsonObject jsonObjectNew = qosProfileInDB.toJson();
				qosProfileInDB.setModifiedDateAndStaff(getStaffData());
				CRUDOperationUtil.update(qosProfileInDB);
				JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);
				
				String message = Discriminators.QOS_PROFILE + " <b><i>" + qosProfileInDB.getName() + "</i></b> " + "Updated";
				CRUDOperationUtil.audit(qosProfileInDB,qosProfileInDB.getPkgData().getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference , qosProfileInDB.getHierarchy(), message);
				MessageFormat messageFormat = new MessageFormat(getText("update.success"));
				addActionMessage(messageFormat.format(messageParameter));
				return Results.DISPATCH_VIEW.getValue();

 		}catch(Exception e){
			return generateErrorLogsAndRedirect(e, "Error while fetching QosProfile data for create operation." ,ActionMessageKeys.CREATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	public String manageOrder(){
		
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called manageOrder()");
		}
		setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
		pkgId = request.getParameter(Attributes.PKG_ID);
		groupIds = request.getParameter(Attributes.GROUPIDS);
		request.setAttribute(Attributes.PKG_ID, pkgId);
		request.setAttribute(Attributes.GROUPIDS, groupIds);

		return MANAGE_ORDER;
	}

	public String manageOrderQosProfiles(){
		if( LogManager.getLogger().isDebugLogLevel() ){
			LogManager.getLogger().debug(MODULE, "Method called manageOrderQosProfiles()");
		}
		setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
		String qosProfilesIdsAndOrders =  request.getParameter("qosProfilesIdsAndOrders");
		
		try{
			
			if( Strings.isNullOrBlank(qosProfilesIdsAndOrders) == false ){
				String[] profilesIdsAndOrders = qosProfilesIdsAndOrders.split(",");
				
				if( profilesIdsAndOrders != null && profilesIdsAndOrders.length != 0 ){
					
					for( String  idAndOrder : profilesIdsAndOrders ){
						String[] idAndOrderArray = idAndOrder.split("=");
						
						if(idAndOrderArray != null && idAndOrderArray.length == 2){
							
							QosProfileData qosObj = CRUDOperationUtil.get(QosProfileData.class, idAndOrderArray[0]);
							
							if(qosObj == null){
								throw new NullPointerException("QoS Profile Not found for ID: "+idAndOrderArray[0]);
							}
							
							JsonObject  jsonOldObject = qosObj.toJson();
							 
							qosObj.setOrderNo(Integer.parseInt(idAndOrderArray[1]));
							CRUDOperationUtil.update(qosObj);
							
							JsonObject  jsonNewObject = qosObj.toJson();
							JsonArray difference = ObjectDiffer.diff(jsonOldObject, jsonNewObject);
							String message = Discriminators.QOS_PROFILE + " <b><i>" + qosObj.getName() + "</i></b> " + "Updated";
							CRUDOperationUtil.audit(qosObj,qosObj.getPkgData().getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference , qosObj.getHierarchy(), message);
							
							if( LogManager.getLogger().isDebugLogLevel() ){
								LogManager.getLogger().debug(MODULE, "QoSProfile '"+qosObj.getName()+"' order changed to "+qosObj.getOrderNo());
							}
 						}
					}
					
					MessageFormat messageFormat = new MessageFormat(getText("qos.manage.order.success"));
					addActionMessage(messageFormat.format(messageParameter));
					
				}else{
					
					LogManager.getLogger().info(MODULE, "Failed to update QoSProfiles Order. Reason: Received Invalid data for QoSProfiles Ids and Orders");
					addActionError(getText(QOS_PROFILE_MANAGE_ORDER_FAILURE));
				}
			}else{
				
				LogManager.getLogger().error(MODULE, "Failed to update QoSProfiles Order. Reason: Received NULL data for QoSProfiles Ids and Orders");
				addActionError(getText(QOS_PROFILE_MANAGE_ORDER_FAILURE));
 			}
		
 		}catch(Exception e){
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			return generateErrorLogsAndRedirect(e, "Error while Updating QoSProfiles Order." ,QOS_PROFILE_MANAGE_ORDER_FAILURE, Results.REDIRECT_TO_PARENT.getValue());
		}  
		setParentIdKey(Attributes.PKG_ID);
		setParentIdValue(pkgId);
		
		return Results.REDIRECT_TO_PARENT.getValue();
		
	}

	@InputConfig(resultName = InputConfigConstants.VIEW)
	public String removeChargingRuleBaseName(){

		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().info(MODULE, "Called removeChargingRuleBaseName()");
		}

		String qosProfileDetailId 	= request.getParameter(Attributes.QOS_PROFILE_DETAIL_ID);
		String chargingRuleBaseNameId = request.getParameter(Attributes.CHARGING_RULE_BASE_NAME_ID);

		try{

			if(Strings.isNullOrBlank(qosProfileDetailId)==true){
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}

			if(Strings.isNullOrEmpty(chargingRuleBaseNameId)==true){
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}

			ChargingRuleBaseNameData chargingRuleBaseNameData = CRUDOperationUtil.get(ChargingRuleBaseNameData.class,chargingRuleBaseNameId);
			if(chargingRuleBaseNameData==null){
				LogManager.getLogger().error(MODULE, "Charging-Rule-Base-Name data not found for ID: " + chargingRuleBaseNameId);
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}

			QosProfileDetailData qosProfileDetailData = CRUDOperationUtil.get(QosProfileDetailData.class,qosProfileDetailId);

			if(qosProfileDetailData==null){
				LogManager.getLogger().error(MODULE, "QosProfile data not found for ID: "+qosProfileDetailId);
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}

			QosProfileDetailData qosProfileDetailDataOld = CRUDOperationUtil.get(QosProfileDetailData.class, qosProfileDetailId);
			JsonObject jsonObjectOld = qosProfileDetailDataOld.getQosProfile().toJson();


			int index = qosProfileDetailData.getChargingRuleBaseNames().indexOf(chargingRuleBaseNameData);
			qosProfileDetailData.getChargingRuleBaseNames().remove(index);

			JsonObject jsonObjectNew = qosProfileDetailData.getQosProfile().toJson();
			JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);

			String message = Discriminators.QOS_PROFILE + " <b><i>" + qosProfileDetailData.getQosProfile().getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(qosProfileDetailData.getQosProfile(), qosProfileDetailData.getQosProfile().getName(), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference, qosProfileDetailData.getHierarchy(), message);

			CRUDOperationUtil.update(qosProfileDetailData);
			request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfileDetailData.getQosProfile().getId());
			setActionChainUrl(Results.VIEW.getValue());

			Object [] messageParameter = {Discriminators.CHARGING_RULE_BASE_NAME};
			MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
			addActionMessage(messageFormat.format(messageParameter));

		}catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while removing Charging-Rule-Base-Name." ,ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
		return Results.DISPATCH_VIEW.getValue();
	}


	public String updateQosDetailInformation(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "inside updateQosDetailInformation() method");
		}
		String qosProfileDetailId = request.getParameter(Attributes.QOS_PROFILE_DETAIL_ID);
		setActionChainUrl(Results.VIEW.getValue());
		try{
			if(Strings.isNullOrEmpty(qosProfileDetailId) == false){
				QosProfileDetailData qosProfileDetailInDB = CRUDOperationUtil.get(QosProfileDetailData.class,qosProfileDetailId);
			
				JsonArray difference = new JsonArray();
				JsonObject jsonObjectOld = qosProfileDetailInDB.getQosProfile().toJson();
				qosProfileDetailInDB.setQci(qosProfileDetail.getQci());
				qosProfileDetailInDB.setAambrdl(qosProfileDetail.getAambrdl());
				qosProfileDetailInDB.setAambrdlUnit(qosProfileDetail.getAambrdlUnit());
				qosProfileDetailInDB.setAambrul(qosProfileDetail.getAambrul());
				qosProfileDetailInDB.setAambrulUnit(qosProfileDetail.getAambrulUnit());
				qosProfileDetailInDB.setMbrdl(qosProfileDetail.getMbrdl());
				qosProfileDetailInDB.setMbrdlUnit(qosProfileDetail.getMbrdlUnit());
				qosProfileDetailInDB.setMbrul(qosProfileDetail.getMbrul());
				qosProfileDetailInDB.setMbrulUnit(qosProfileDetail.getMbrulUnit());
				qosProfileDetailInDB.setPreCapability(qosProfileDetail.getPreCapability());
				qosProfileDetailInDB.setPreVulnerability(qosProfileDetail.getPreVulnerability());
				qosProfileDetailInDB.setAction(qosProfileDetail.getAction());
				qosProfileDetailInDB.setRejectCause(qosProfileDetail.getRejectCause());
				qosProfileDetailInDB.setPriorityLevel(qosProfileDetail.getPriorityLevel());
				qosProfileDetailInDB.setRedirectUrl(qosProfileDetail.getRedirectUrl());
				
				qosProfileDetailInDB.setUsageMonitoring(qosProfileDetail.getUsageMonitoring());
				
				qosProfileDetailInDB.setSliceTotal(qosProfileDetail.getSliceTotal());
				qosProfileDetailInDB.setSliceTotalUnit(qosProfileDetail.getSliceTotalUnit());

				qosProfileDetailInDB.setSliceUpload(qosProfileDetail.getSliceUpload());
				qosProfileDetailInDB.setSliceUploadUnit(qosProfileDetail.getSliceUploadUnit());
				
				qosProfileDetailInDB.setSliceDownload(qosProfileDetail.getSliceDownload());
				qosProfileDetailInDB.setSliceDownloadUnit(qosProfileDetail.getSliceDownloadUnit());
				
				qosProfileDetailInDB.setSliceTime(qosProfileDetail.getSliceTime());
				qosProfileDetailInDB.setSliceTimeUnit(qosProfileDetail.getSliceTimeUnit());
				
				qosProfileDetailInDB.getQosProfile().setModifiedDateAndStaff(getStaffData());
				CRUDOperationUtil.update(qosProfileDetailInDB);
				JsonObject jsonObjectNew = qosProfileDetailInDB.getQosProfile().toJson();
				difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);
				
				String message = Discriminators.QOS_PROFILE + " <b><i>" + qosProfileDetailInDB.getQosProfile().getName() + "</i></b> " + "Updated";
				CRUDOperationUtil.audit(qosProfileDetailInDB.getQosProfile(),qosProfileDetailInDB.getQosProfile().getPkgData().getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference ,qosProfileDetailInDB.getQosProfile().getHierarchy(), message);
				request.getSession().setAttribute(Attributes.QOS_PROFILE_ID, qosProfileDetailInDB.getQosProfile().getId());
				request.setAttribute(Attributes.QOS_PROFILE_ID,qosProfileDetailInDB.getQosProfile().getId());
				MessageFormat messageFormat = new MessageFormat(getText("update.success"));
				addActionMessage(messageFormat.format(messageParameter));
				return Results.DISPATCH_VIEW.getValue();
			}else {
				setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
 		}catch(Exception e){
			return generateErrorLogsAndRedirect(e, "Error while fetching QosProfileDetail data for update operation." ,ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}


	public String delete(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called delete()");
		}
		String qosProfileId = getQosProfileId();
		String fromViewPage = request.getParameter(Attributes.FROM_VIEW_PAGE);
		try {
			if(Strings.isNullOrBlank(fromViewPage)){
				setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			}else{
				setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
			}
			if( Strings.isNullOrEmpty(qosProfileId) == true){
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
              }
			QosProfileData qosProfile = CRUDOperationUtil.get(QosProfileData.class, qosProfileId);
			pkgId = qosProfile.getPkgData().getId();
			String message = Discriminators.QOS_PROFILE + " <b><i>" + qosProfile.getName() + "</i></b> " + "Deleted";
			CRUDOperationUtil.audit(qosProfile,qosProfile.getPkgData().getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),qosProfile.getHierarchy(), message);
			qosProfile.setStatus(CommonConstants.STATUS_DELETED);
			qosProfile.setRateCardData(null); //removed relation because we are actually removed the entity

			if(Collectionz.isNullOrEmpty(qosProfile.getQosProfileDetailDataList()) == false){
				for(QosProfileDetailData qosProfileDetail : qosProfile.getQosProfileDetailDataList()){
					qosProfileDetail.setDeletedStatus();
					CRUDOperationUtil.delete(qosProfileDetail);
				}
				qosProfile.getQosProfileDetailDataList().clear();
            }
			MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
			addActionMessage(messageFormat.format(messageParameter));
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			return Results.REDIRECT_TO_PARENT.getValue();

 		} catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			return generateErrorLogsAndRedirect(e, "Error while fetching QosProfileDetail data for delete operation." ,ActionMessageKeys.DELETE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}


	public String deleteFupLevels(){
		if (LogManager.getLogger().isDebugLogLevel()) { 
			LogManager.getLogger().debug(MODULE, "Method called deleteFupLevels()");
		}
		String qosProfileDetailId = request.getParameter(Attributes.QOS_PROFILE_DETAIL_ID);
		String qosProfileId = request.getParameter(Attributes.QOS_PROFILE_ID);
		setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
		try{
			if(Strings.isNullOrEmpty(qosProfileDetailId) == false){
				QosProfileDetailData qosProfileDetail = CRUDOperationUtil.get(QosProfileDetailData.class , qosProfileDetailId);
				QosProfileData qosProfileData = qosProfileDetail.getQosProfile();
				JsonObject jsonObjectOld = qosProfileData.toJson();
				if(qosProfileDetail != null){
					qosProfileDetail.getQosProfile().getQosProfileDetailDataList().remove(qosProfileDetail);
					if(Collectionz.isNullOrEmpty(qosProfileDetail.getPccRules())==false) {
						Collectionz.filter(qosProfileDetail.getPccRules(), GLOBALPCCRULEFILTER);
					}
					qosProfileDetail.setDeletedStatus();
					CRUDOperationUtil.delete(qosProfileDetail);
					JsonObject jsonObjectNew = qosProfileDetail.getQosProfile().toJson();
					JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);
					String message = Discriminators.QOS_PROFILE + " <b><i>" + qosProfileData.getName() + "</i></b> " + "Updated";
					CRUDOperationUtil.audit(qosProfileData,qosProfileData.getPkgData().getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference ,qosProfileData.getHierarchy(), message);
					Object [] messageParameterForLevel = {FieldValueConstants.FUP_LEVEL};
					MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
					addActionMessage(messageFormat.format(messageParameterForLevel));
					setParentIdKey(Attributes.QOS_PROFILE_ID);
					setParentIdValue(qosProfileId);
				}
			}else {
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
			return Results.REDIRECT_TO_PARENT.getValue();
 		}catch(Exception e){
			return generateErrorLogsAndRedirect(e, "Error while fetching QosProfileDetail data for delete operation." ,ActionMessageKeys.DELETE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
		
	}

	@InputConfig(resultName = InputConfigConstants.VIEW)
	public String attachPccRule(){
		LogManager.getLogger().info(MODULE,"Method called attachPccRule()");
		String qosProfileDetailId=request.getParameter(Attributes.QOS_PROFILE_DETAIL_ID);
		String[] pccRuleIds=request.getParameterValues("ids");
		try{
			if(Strings.isNullOrBlank(qosProfileDetailId)==true){
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
			if(Arrayz.isNullOrEmpty(pccRuleIds)==true){
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
			QosProfileDetailData qosProfileDetailData = CRUDOperationUtil.get(QosProfileDetailData.class,qosProfileDetailId);
			if(qosProfileDetailData==null){
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}

			JsonObject jsonObjectOld = qosProfileDetailData.getQosProfile().toJson();

			for(String pccRuleId:pccRuleIds){
				PCCRuleData pccRuleDataTobeAttach = CRUDOperationUtil.get(PCCRuleData.class, pccRuleId);
				if(pccRuleDataTobeAttach!=null){
					qosProfileDetailData.getPccRules().add(pccRuleDataTobeAttach);
				}
			}

			JsonObject jsonObjectNew = qosProfileDetailData.getQosProfile().toJson();
			JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);

			String message = Discriminators.QOS_PROFILE + " <b><i>" + qosProfileDetailData.getQosProfile().getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(qosProfileDetailData.getQosProfile(), qosProfileDetailData.getQosProfile().getName(), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference, qosProfileDetailData.getHierarchy(), message);
			CRUDOperationUtil.update(qosProfileDetailData);
			request.setAttribute(Attributes.QOS_PROFILE_ID,qosProfileDetailData.getQosProfile().getId());
			setActionChainUrl(Results.VIEW.getValue());
		}catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while Attaching PCC Rules." ,ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
		return Results.DISPATCH_VIEW.getValue();
	}

	@InputConfig(resultName = InputConfigConstants.VIEW)
	public String attachChargingRuleBaseName(){
		LogManager.getLogger().info(MODULE,"Method called attachChargingRuleBaseName()");

		String qosProfileDetailId 		 = request.getParameter(Attributes.QOS_PROFILE_DETAIL_ID);
		String[] chargingRuleBaseNameIds = request.getParameterValues("ids");

		try{

			if(Strings.isNullOrBlank(qosProfileDetailId)==true){
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}

			if(Arrayz.isNullOrEmpty(chargingRuleBaseNameIds)==true){
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}

			QosProfileDetailData qosProfileDetailData = CRUDOperationUtil.get(QosProfileDetailData.class,qosProfileDetailId);
			if(qosProfileDetailData==null){
				return redirectError(Discriminators.QOS_PROFILE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}

			JsonObject jsonObjectOld = qosProfileDetailData.getQosProfile().toJson();

			for(String chargingRuleBaseNameId :chargingRuleBaseNameIds){
				ChargingRuleBaseNameData chargingRuleBaseNameTobeAttach = CRUDOperationUtil.get(ChargingRuleBaseNameData.class, chargingRuleBaseNameId);
				if(chargingRuleBaseNameTobeAttach!=null){
					qosProfileDetailData.getChargingRuleBaseNames().add(chargingRuleBaseNameTobeAttach);
				}
			}

			JsonObject jsonObjectNew = qosProfileDetailData.getQosProfile().toJson();
			JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);

			String message = Discriminators.QOS_PROFILE + " <b><i>" + qosProfileDetailData.getQosProfile().getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(qosProfileDetailData.getQosProfile(), qosProfileDetailData.getQosProfile().getName(), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference, qosProfileDetailData.getHierarchy(), message);

			CRUDOperationUtil.update(qosProfileDetailData);
			request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfileDetailData.getQosProfile().getId());
			setActionChainUrl(Results.VIEW.getValue());

		}catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while Attaching Charging-Rule-Base-Name." ,ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
		return Results.DISPATCH_VIEW.getValue();
	}

	private void setTimePeriods(QosProfileData qosProfileData){
		filterEmptyTimePeriods(qosProfileData.getTimePeriodDataList());
		List<TimePeriodData> timePeriodDatas = Collectionz.newArrayList();
		for (TimePeriodData timePeriod : qosProfileData.getTimePeriodDataList()) {
			timePeriod.setQosProfile(qosProfileData);
			timePeriodDatas.add(timePeriod);
		}
		qosProfileData.setTimePeriodDataList(timePeriodDatas);
	}

	private void filterEmptyTimePeriods(List<TimePeriodData> timePeriods){
		Collectionz.filter(timePeriods,new Predicate<TimePeriodData>() {
			@Override
			public boolean apply(TimePeriodData timePeriod) {
				if(timePeriod == null){
					return false;
				}
				return !(Strings.isNullOrBlank(timePeriod.getMoy())
						&& Strings.isNullOrBlank(timePeriod.getDom())
						&& Strings.isNullOrBlank(timePeriod.getDow())
						&& Strings.isNullOrBlank(timePeriod.getTimePeriod()));
			}});
	}




	class SortMe implements Comparator{

		@Override
		public int compare(Object o1, Object o2) {
			 QosProfileDetailData q1 = (QosProfileDetailData) o1;
			 QosProfileDetailData q2 = (QosProfileDetailData) o2;

			return q1.getFupLevel()<q2.getFupLevel() ? -1 : q1.getFupLevel()>q2.getFupLevel() ? 1 : 0;
		}

	}

	public QosProfileData  getQosProfile() {
		return qosProfile;
	}

	public void setQosProfile( QosProfileData qosProfile) {
		this.qosProfile = qosProfile;
	}

	public String getPkgId() {
		return pkgId;
	}

	public void setPkgId(String pkgId) {
		this.pkgId = pkgId;
	}

	@Override
	public QosProfileData  getModel() {
		return qosProfile;
	}

	public List getQuotaProfileList() {
		return quotaProfileList;
	}

	public void setQuotaProfileList(List quotaProfileList) {
		this.quotaProfileList = quotaProfileList;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String url) {
		this.actionChainUrl = url;

	}

	public  QosProfileDetailData getQosProfileDetail() {
		return qosProfileDetail;
	}

	public void setQosProfileDetail( QosProfileDetailData qosProfileDetail) {
		this.qosProfileDetail = qosProfileDetail;

	}

	public List<String> getPcrfKeyValueConstants() {
		return pcrfKeyValueConstants;
	}

	public void setPcrfKeyValueConstants(
			List<String> pcrfKeyValueConstants) {
		this.pcrfKeyValueConstants = pcrfKeyValueConstants;
	}

	public List<String> getShowSelectedAccessNetworkForUpdate() {
		return showSelectedAccessNetworkForUpdate;
	}

	public void setShowSelectedAccessNetworkForUpdate(
			List<String> showSelectedAccessNetworkForUpdate) {
		this.showSelectedAccessNetworkForUpdate = showSelectedAccessNetworkForUpdate;
	}

	public class OrderComparator implements Comparator<QosProfileData> {

		@Override
		public int compare(QosProfileData qosProfile1, QosProfileData qosProfile2) {
			if(qosProfile1.getOrderNo() > qosProfile2.getOrderNo()){
				return -1;
			}else if(qosProfile1.getOrderNo() < qosProfile2.getOrderNo()){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	private String getFUPLevel(int level){
		switch (level){
			case 0:
				return "HSQ";
			case 1:
				return "FUP1";
			case 2:
				return "FUP2";
			default:
				return null;
    	}
	}

	private String generateErrorLogsAndRedirect(Exception e, String message, String key, String result) {
		return super.generateErrorLogsAndRedirect(Discriminators.QOS_PROFILE, e, message, key, result);
	}

	@Override
	protected List<QosProfileData> getSearchResult(String criteriaJson,Class<QosProfileData> beanType, int startIndex, int maxRecords,String sortColumnName, String sortColumnOrder,String staffBelongingGroups) throws Exception {
		return CRUDOperationUtil.searchByNameAndStatus(beanType, criteriaJson, startIndex, maxRecords, sortColumnName, sortColumnOrder, staffBelongingGroups);
	}

	@Override
	public String getIncludeProperties(){
		return QOS_INCLUDE_PARAMETERS;
	}

	public List<DataRateCardData> getDataRateCardList() {
		return dataRateCardList;
	}

	public void setDataRateCardList(List<DataRateCardData> dataRateCardList) {
		this.dataRateCardList = dataRateCardList;
	}
}