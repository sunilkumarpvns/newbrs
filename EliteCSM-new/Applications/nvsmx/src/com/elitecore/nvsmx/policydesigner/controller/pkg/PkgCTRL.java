package com.elitecore.nvsmx.policydesigner.controller.pkg;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pkg.PkgContainer;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.importpkg.PkgImportOperation;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.pkg.notification.UsageNotificationData;
import com.elitecore.corenetvertex.pkg.pccrule.GlobalPCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.pm.pkg.factory.FactoryUtils;
import com.elitecore.corenetvertex.pm.util.PackageValidator;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.collection.Lists;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.ratinggroup.RatingGroupUtil;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportEntityAccumulator;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportCTRL;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.policydesigner.controller.util.PkgTypeValidator;
import com.elitecore.nvsmx.policydesigner.model.pkg.PkgDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.qos.QosProfileDetailWrapper;
import com.elitecore.nvsmx.policydesigner.model.pkg.qos.QosProfileDetailWrapper.QosProfileDetailWrapperBuilder;
import com.elitecore.nvsmx.policydesigner.model.pkg.quota.QuotaProfileWrapper;
import com.elitecore.nvsmx.policydesigner.model.pkg.quota.QuotaProfileWrapper.QuotaProfileWrapperBuilder;
import com.elitecore.nvsmx.policydesigner.model.pkg.syquota.SyQuotaProfileWrapper;
import com.elitecore.nvsmx.policydesigner.model.pkg.syquota.SyQuotaProfileWrapper.SyQuotaProfileWrapperBuilder;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil;
import com.elitecore.nvsmx.system.hibernate.SessionProviderImpl;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.policydesigner.controller.util.GlobalPlanPredicates.createNonContainGroupPredicate;


/**
 * @author Kirpalsinh.raj
 *
 */
public class PkgCTRL extends ImportExportCTRL<PkgData> {

	private static final long serialVersionUID = 1L;
	private static final String MODULE  = "PKG-CTRL";
	private static final String EXPORT_PKG = "exportPkg_";
	private static final String EXPORT_PROMOTIONAL_PKG = "exportPromotionalPkg_";

	private PkgData pkgData = new PkgData();
	private String actionChainUrl;
	private List<DataServiceTypeData> dataServiceTypeData = Collectionz.newArrayList();
	private List<String> groupList = Collectionz.newArrayList();
	private List<NotificationTemplateData> emailTemplateDatas = Collectionz.newArrayList();
	private List<NotificationTemplateData> smsTemplateDatas = Collectionz.newArrayList();
	private List<AggregationKey> aggregationKeys = Collectionz.newArrayList();
	private List<GroupData> staffBelongingGroupList = Collectionz.newArrayList();
	Object [] messageParameter = {Discriminators.PACKAGE};
	private List<String> qosProfileDataNames = Collectionz.newArrayList();
	private String qosProfilesAsJsonString;
	private String syQuotaProfilesAsJsonString;
	private String usageMeteringQuotaProfilesAsJsonString;
	private boolean isCurrencyUpdateAllowed;

	private String rncProfileAsJsonString;
	private String rateCardAsJsonString;
	public static final String PKG_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.type,dataList\\[\\d+\\]\\.pkgTypeDisplayValue,dataList\\[\\d+\\]\\.status,dataList\\[\\d+\\]\\.price,dataList\\[\\d+\\]\\.packageMode,dataList\\[\\d+\\]\\.availabilityStartDate,dataList\\[\\d+\\]\\.availabilityEndDate,dataList\\[\\d+\\]\\.groups,dataList\\[\\d+\\]\\.quotaProfileType";

	public List<PkgData> pkgs = Collectionz.newArrayList();

	public Map<String,List<PkgGroupOrderData>> groupWiseOrderMap = Maps.newLinkedHashMap();
	private final EnumSet<QuotaProfileType> promotionalPkgQuotaProfileTypes = EnumSet.of(QuotaProfileType.USAGE_METERING_BASED);
	private String pccProfileValidationMessages;

	private EnumSet<RenewalIntervalUnit> validRenewalIntervals = EnumSet.of(RenewalIntervalUnit.MONTH,RenewalIntervalUnit.MONTH_END,RenewalIntervalUnit.TILL_BILL_DATE);
	public EnumSet<RenewalIntervalUnit> getValidRenewalIntervals() {
		return validRenewalIntervals;
	}

	public void setValidRenewalIntervals(EnumSet<RenewalIntervalUnit> validRenewalIntervals) {
		this.validRenewalIntervals = validRenewalIntervals;
	}

	public EnumSet<QuotaProfileType> getPromotionalPkgQuotaProfileTypes() {
		return promotionalPkgQuotaProfileTypes;
	}

	public void setGroupWiseOrderMap(Map<String, List<PkgGroupOrderData>> groupWiseOrderMap) {
		this.groupWiseOrderMap = groupWiseOrderMap;
	}

	public Map<String, List<PkgGroupOrderData>> getGroupWiseOrderMap() {
		return groupWiseOrderMap;
	}





	public PkgData getPkgData() {
		return pkgData;
	}

	public void setPkgData(PkgData pkgData) {
		this.pkgData = pkgData;
	}

	@SkipValidation
	public String detail(){
	    LogManager.getLogger().debug(MODULE,"Method called detail()");
	    try {

			String tableId = request.getParameter("tableId");
			String row = request.getParameter("rowData"+tableId);

			Gson gson = GsonFactory.defaultInstance();
			pkgData = gson.fromJson(row, PkgData.class);
			LogManager.getLogger().debug(MODULE, "Package Data:"+pkgData);
	    } catch (JsonSyntaxException e) {
	    	LogManager.getLogger().error(MODULE, "Error while making object from JSON data");
	    	LogManager.getLogger().trace(MODULE,e);
	    	pkgData = new PkgData();
	    }
	    return Results.DETAIL.getValue();
	}

	@SkipValidation
	public String init() {

		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called init()");
		}

		String pkgId = getPkgId();
		if (Strings.isNullOrBlank(pkgId)) {
			pkgData.setDescription(NVSMXUtil.getDefaultDescription(request));
			if (PkgType.PROMOTIONAL.name().equalsIgnoreCase(pkgData.getType())) {
				pkgData.setType(PkgType.PROMOTIONAL.name());

				request.getSession().setAttribute(Attributes.LAST_URI, NVSMXCommonConstants.PROMOTIONAL_PKG_INIT_METHOD);
			}
			return Results.CREATE.getValue();
		}

		try {
			pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);

			if (pkgData == null) {
				LogManager.getLogger().error(MODULE, "Error while fetching Package data for update operation. Reason: No package found with given id: " + pkgId);
				return redirectError(Discriminators.PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}

			boolean isRateCurrencyUpdateAllowed = validateDataPackageDetails(pkgId);
			setCurrencyUpdateAllowed(isRateCurrencyUpdateAllowed);
			return Results.UPDATE.getValue();

		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while fetching Package data for update operation.", ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue());
		}
	}

	private String getPkgId() {

		String pkgId = request.getParameter(Attributes.PKG_ID);
		if (Strings.isNullOrBlank(pkgId)) {
			pkgId = (String) request.getAttribute(Attributes.PKG_ID);
			if (Strings.isNullOrBlank(pkgId)) {
				pkgId = request.getParameter("pkgData.id");
			}
		}

		return pkgId;
	}

	@Override
	public String getIncludeProperties(){
		return PKG_INCLUDE_PARAMETERS;
	}

	@SkipValidation
	public String search(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE,"Method called search()");
		}
		String pkgType = request.getParameter("pkgType");
		if(Strings.isNullOrBlank(pkgType) == false && PkgType.PROMOTIONAL.name().equalsIgnoreCase(pkgType)){
            Integer noOfPromotionalPackages = PkgDAO.getNoOfPackagesOfType(PkgType.PROMOTIONAL);
			request.setAttribute("noOfPromotionalPackages",noOfPromotionalPackages);
		}
		return Results.LIST.getValue();
	}

	@InputConfig(resultName = InputConfigConstants.CREATE)
	public String create(){

		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE,"Method called create()");
		}
		try{

			pkgData.setCreatedDateAndStaff(getStaffData());

			if(Strings.isNullOrBlank(getGroupIds())){
				pkgData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				pkgData.setGroups(getGroupIds());
			}

			List<String> pkgGroupList = CommonConstants.COMMA_SPLITTER.split(pkgData.getGroups());
			pkgData.setGroupNames(GroupDAO.getGroupNames(pkgGroupList));
			if (isGlobalPlan(pkgData)) {
             	if (isGroupWiseLimitReach(pkgData,CRUDOperationUtil.MODE_CREATE,ActionMessageKeys.CREATE_FAILURE.key) == true) {
					return Results.CREATE.getValue();
				}
				setPkgGroupOrder(pkgGroupList);
			}

			CRUDOperationUtil.save(pkgData);
			String message = Discriminators.PACKAGE + " <b><i>" + pkgData.getName() + "</i></b> " + "Created";
			CRUDOperationUtil.audit(pkgData,pkgData.getName(),AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), pkgData.getHierarchy(), message);

			request.setAttribute(Attributes.PKG_ID, pkgData.getId());
			MessageFormat messageFormat = new MessageFormat(getText("create.success"));

			addActionMessage(messageFormat.format(messageParameter));
			setActionChainUrl(Results.VIEW.getValue());
			return Results.DISPATCH_VIEW.getValue();

 		}catch(Exception ex){
			return generateErrorLogsAndRedirect(ex, "Failed to Create Package.", ActionMessageKeys.CREATE_FAILURE.key, Results.LIST.getValue());
		}
	}

	private void setPkgGroupOrder(List<String> pkgGroupList) {
		for(String groupId : pkgGroupList) {
			int maxOrder = PkgDAO.getGroupWiseMaxOrder(groupId, pkgData.getType()) + 1;
			PkgGroupOrderData pkgGroupOrderData = new PkgGroupOrderData();
			String groupName = GroupDAO.getGroupName(groupId);
			pkgGroupOrderData.setPkgData(pkgData);
			pkgGroupOrderData.setGroupId(groupId);
			pkgGroupOrderData.setGroupName(groupName);
			pkgGroupOrderData.setOrderNumber(maxOrder);
			pkgGroupOrderData.setType(pkgData.getType());
			pkgData.getPkgGroupWiseOrders().add(pkgGroupOrderData);
		}
	}



	@SkipValidation
	public String 	view(){

		if(LogManager.getLogger().isDebugLogLevel()){
		   LogManager.getLogger().debug(MODULE,"Method called view()");
		}

		String pkgId = getPkgId();
		LogManager.getLogger().debug(MODULE,"View package of PkgId: "+pkgId);

		try {
			if (Strings.isNullOrBlank(pkgId)) {
				return redirectError(Discriminators.PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}
			pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);

			if (pkgData == null) {
				LogManager.getLogger().error(MODULE, "Error while fetching Package data for update operation. Reason: No package found with given id: " + pkgId);
				return redirectError(Discriminators.PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}

			String belongingsGroups = GroupDAO.getGroupNames(SPLITTER.split(pkgData.getGroups()));
			pkgData.setGroupNames(belongingsGroups);

				setDataServiceTypeData(getServiceTypes());
				setQosProfileDetailWrappers(pkgData.getQosProfiles());
				if(pkgData.getQuotaProfileType().equals(QuotaProfileType.USAGE_METERING_BASED)){
					setUsageMeteringQuotaprofileWrappers(pkgData.getQuotaProfiles());
					setUsageNotificationInJson(pkgId);

				} else if(QuotaProfileType.RnC_BASED.equals(pkgData.getQuotaProfileType())) {
					Gson gson = GsonFactory.defaultInstance();
					JsonArray balanceBasedQuotaProfileDataAsJsonArray = gson.toJsonTree(pkgData.getRncProfileDatas()).getAsJsonArray();
					setRncProfileAsJsonString(balanceBasedQuotaProfileDataAsJsonArray.toString());
					request.setAttribute(Attributes.RNC_PROFILE_DATA,balanceBasedQuotaProfileDataAsJsonArray);
					setQuotaNotificationInJson(pkgId);
					setDataRateCardsInJson();
				}else{
					setSyQuotaProfileWraperes(pkgData.getSyQuotaProfileDatas());
				}

		    return Results.VIEW.getValue();

		}catch(Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while fetching Package data for view operation.", ActionMessageKeys.VIEW_FAILURE.key, Results.LIST.getValue());
		}
	}

	private void validateQosProfileConfiguration(PkgData pkgData) {
		DeploymentMode deploymentMode = SystemParameterDAO.getDeploymentMode();
		StringBuilder reason = new StringBuilder();
		for (QosProfileData qosProfileData : pkgData.getQosProfiles()) {
			List<String> qosProfileFailReasons = new ArrayList<String>();
			PackageValidator.validPCCProfileWithDeploymentMode(deploymentMode, qosProfileData, qosProfileFailReasons);
			if(CollectionUtils.isNotEmpty(qosProfileFailReasons)){
			   	reason.append("Invalid PCC Profile(").append(qosProfileData.getName()).append(") configuration. ").append(FactoryUtils.format(qosProfileFailReasons)).append(NVSMXCommonConstants.BREAK_LINE);
			}
		}
		setPccProfileValidationMessages(reason.toString());
	}

	private void setDataRateCardsInJson() {
		Gson gson = GsonFactory.defaultInstance();
		JsonArray dataRateCardsAsJson = gson.toJsonTree(pkgData.getRateCards()).getAsJsonArray();
		setRateCardAsJsonString(dataRateCardsAsJson.toString());
	}

	private void setUsageNotificationInJson(String pkgId) {
		setTemplateDatas(pkgId);
		JsonArray usageNotificationDatajson = null;
		Gson gson = GsonFactory.defaultInstance();
		List<UsageNotificationData> usageNotificationDatas = Collectionz.newArrayList();
		for(UsageNotificationData usageNotificationData : pkgData.getUsageNotificationDatas()){
			if(usageNotificationData.getStatus().equals(CommonConstants.STATUS_DELETED) == false){
				usageNotificationDatas.add(usageNotificationData);
			}
		}
		usageNotificationDatajson = gson.toJsonTree(usageNotificationDatas, new TypeToken<List<UsageNotificationData>>() {}.getType()).getAsJsonArray();
		request.setAttribute(Attributes.USAGE_NOTIFICATION_DATA, usageNotificationDatajson);
	}

	private void setQuotaNotificationInJson(String pkgId) {
		setTemplateDatas(pkgId);
		JsonArray quotaNotificationDatajson = null;
		Gson gson = GsonFactory.defaultInstance();
		List<QuotaNotificationData> quotaNotificationDatas = Collectionz.newArrayList();
		for(QuotaNotificationData quotaNotificationData : pkgData.getQuotaNotificationDatas()){
			if(quotaNotificationData.getStatus().equals(CommonConstants.STATUS_DELETED) == false){
				quotaNotificationDatas.add(quotaNotificationData);
			}
		}
		sortByQuotaProfileFupLevelAndThreshold(quotaNotificationDatas);
		quotaNotificationDatajson = gson.toJsonTree(quotaNotificationDatas, new TypeToken<List<QuotaNotificationData>>() {}.getType()).getAsJsonArray();
		request.setAttribute(Attributes.QUOTA_NOTIFICATION_DATA, quotaNotificationDatajson);

		EnumSet aggregationKeys = EnumSet.allOf(AggregationKey.class);
		aggregationKeys.remove(AggregationKey.CUSTOM);
		setAggregationKeys(new ArrayList<>(aggregationKeys));

	}

	private void sortByQuotaProfileFupLevelAndThreshold(List<QuotaNotificationData> pkgNotificationList) {
		pkgNotificationList.sort(Comparator.comparing(QuotaNotificationData::getQuotaProfileName)
				.thenComparing(QuotaNotificationData::getFupLevel)
				.thenComparing(QuotaNotificationData::getThreshold));
	}

	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called update()");
		}
		String pkgId = pkgData.getId();
		setActionChainUrl(Results.VIEW.getValue());
		try {

			PkgData existingPkgData = CRUDOperationUtil.get(PkgData.class,pkgId);
			if ( existingPkgData!=null &&  existingPkgData.isExclusiveAddOn() ){
				if(pkgData.isExclusiveAddOn()==false){
					throw new Exception("Exclusive Package cannot be Changed to NonExclusive");
				}
			}

			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "PkgId: "+pkgId);
			}
			if(Strings.isNullOrBlank(getGroupIds())){
				pkgData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				pkgData.setGroups(getGroupIds());
			}
			String groupNames = getGroupNames(pkgData);
			if(Objects.isNull(groupNames)){
				throw new Exception("Group Ids not found");
			}
			pkgData.setGroupNames(groupNames);
			List<String> pkgGroupList = CommonConstants.COMMA_SPLITTER.split(pkgData.getGroups());

			if (isGlobalPlan(pkgData)){
				if (isGroupWiseLimitReach(pkgData, CRUDOperationUtil.MODE_UPDATE, ActionMessageKeys.UPDATE_FAILURE.key) == true) {
					return Results.UPDATE.getValue();
				}

				//existing group list
				final List<String> existingPkgGroupOrderDataList = PkgDAO.getListPkgGroupOrderString(pkgData);

				Predicate<String> removedGroupPredicate = createNonContainGroupPredicate(pkgGroupList);

				//delete group list which are removed while updating
				List<String> deleteGroupOrderList = Lists.copy(existingPkgGroupOrderDataList,removedGroupPredicate);

				//deleting removed list
                if(Collectionz.isNullOrEmpty(deleteGroupOrderList) == false){
					PkgDAO.deletePkgGroupOrderEntries(pkgData, deleteGroupOrderList);
				}

				//filter to find only new entries
				Predicate<String> newlyAddedGroupPredicate = createNonContainGroupPredicate(existingPkgGroupOrderDataList);
				Collectionz.filter(pkgGroupList,newlyAddedGroupPredicate);

				if(Collectionz.isNullOrEmpty(pkgGroupList) == false){
					setPkgGroupOrder(pkgGroupList);
				}
			}

			if(pkgData.getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED) {
				pkgData.setSyQuotaProfileDatas(existingPkgData.getSyQuotaProfileDatas());
			} else if(pkgData.getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {
				pkgData.setQuotaProfiles(existingPkgData.getQuotaProfiles());
			} else if(pkgData.getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
				pkgData.setRncProfileDatas(existingPkgData.getRncProfileDatas());
			}

			pkgData.setQosProfiles(existingPkgData.getQosProfiles());
			pkgData.setRateCards(existingPkgData.getRateCards());
			pkgData.setUsageNotificationDatas(existingPkgData.getUsageNotificationDatas());
			pkgData.setQuotaNotificationDatas(existingPkgData.getQuotaNotificationDatas());

			pkgData.setModifiedDateAndStaff(getStaffData());
			CRUDOperationUtil.merge(pkgData);
			String message = Discriminators.PACKAGE + " <b><i>" + pkgData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(pkgData,pkgData.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),pkgData.getHierarchy(), message);

			request.setAttribute(Attributes.PKG_ID, pkgId);

			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));


			return Results.DISPATCH_VIEW.getValue();
 		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while updating package of PkgId: '"+pkgId+"'.", ActionMessageKeys.UPDATE_FAILURE.key, Results.LIST.getValue());
		}
	}

	@SkipValidation
	public String copymodel() { // create
		if (getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "CopyModel() Called for: " + pkgData.getResourceName());
		}

		try {

			if (Strings.isNullOrBlank(pkgData.getId())) {
				getLogger().error(MODULE, "Error while creating Package Data with id: " + pkgData.getId() + ". Reason: Not found");
				return redirectError(Discriminators.PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}
			String resourceName = pkgData.getResourceName();//store update name
			pkgData = CRUDOperationUtil.get(PkgData.class, pkgData.getId());
			if (pkgData == null) {
				getLogger().error(MODULE, "Error while creating Package Data with id: " + pkgData.getId() + ". Reason: Not found");
				return redirectError(Discriminators.PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}

			pkgData.setCreatedDateAndStaff(getStaffData());
			PkgData newEntity = pkgData.copyModel(); //clone the new entity
			newEntity.setId(getIdForReplicatedPackage());
			String groupNames = getGroupNames(newEntity);
			if(Strings.isNullOrBlank(groupNames) == false){
				newEntity.setGroupNames(groupNames);
			}

			if(Objects.isNull(newEntity)){
				getLogger().error(MODULE, "Error while creating Package Data. Reason: copy operation not supported");
				addActionError("Fail to perform copy Operation. Reason: copy operation not supported");
				request.setAttribute(Attributes.PKG_ID, newEntity.getId());
				return Results.DISPATCH_VIEW.getValue();
			}
			newEntity.setName(resourceName);

			if (newEntity != null) {
				newEntity.setCreatedDateAndStaff(getStaffData());
			}

			setPCCRuleName(newEntity);

			JsonObject newEntityJson = newEntity.toJson();
			PkgImportOperation pkgImportOperation = new PkgImportOperation();
			SessionProvider sessionProvider = new SessionProviderImpl();

			pkgImportOperation.importData(newEntity, newEntity, newEntity, sessionProvider);
			HibernateSessionUtil.syncSession(sessionProvider.getSession());
			String message = Discriminators.PACKAGE + " <b><i>" + newEntity.getName() + "</i></b> " + " Created";
			JsonArray difference = ObjectDiffer.diff(CRUDOperationUtil.EMPTY_JSON_OBJECT, newEntityJson);
			CRUDOperationUtil.audit(newEntity,newEntity.getResourceName(),AuditActions.CREATE,getStaffData(),request.getRemoteAddr(),difference , newEntity.getHierarchy(), message);

			MessageFormat messageFormat = new MessageFormat(getText("create.success"));
			addActionMessage(messageFormat.format(messageParameter));
			LogManager.getLogger().debug(MODULE, "Package "+pkgData.getName()+" successfully replicated with ID: " + newEntity.getId());
			request.setAttribute(Attributes.PKG_ID, newEntity.getId());

			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW + "?" + Attributes.PKG_ID+"="+newEntity.getId());
			return Results.REDIRECT_ACTION.getValue();


		}catch(Exception ex){
			return generateErrorLogsAndRedirect(ex, "Failed to Create Package.", ActionMessageKeys.CREATE_FAILURE.key, Results.LIST.getValue());
		}
	}

	private void setPCCRuleName(PkgData pkgData) {
		String[] pccRuleIds = request.getParameterValues("pccRuleIds");
		Map<String, String> pccRuleIdName = new HashedMap();
		Map<String, String> pccRuleIdMonitoringKey = new HashedMap();
		if(pccRuleIds != null) {
			for (String pccRuleId : pccRuleIds) {
				pccRuleIdName.put(pccRuleId, request.getParameter(pccRuleId + "-name"));
				pccRuleIdMonitoringKey.put(pccRuleId, request.getParameter(pccRuleId + "-monitoringKey"));
			}

			for (QosProfileData qosProfileData : pkgData.getQosProfiles()) {
				for (QosProfileDetailData qosProfileDetailData : qosProfileData.getQosProfileDetailDataList()) {
					for (PCCRuleData pccRule : qosProfileDetailData.getPccRules()) {
						if (PCCRuleScope.LOCAL == pccRule.getScope() && pccRuleIdName.containsKey(pccRule.getName())) {
							String name = pccRule.getName();
							pccRule.setName(pccRuleIdName.get(name));
							pccRule.setMonitoringKey(pccRuleIdMonitoringKey.get(name));
						}
					}
				}
			}
		}
	}

	private String getIdForReplicatedPackage() {
		return UUID.randomUUID().toString();
	}

	private boolean isGlobalPlan(PkgData pkgData) {
		if(PkgType.getGlobalPlan().contains(PkgType.valueOf(pkgData.getType()))){
			return true;
		}
		return false;
	}

	@SkipValidation
	public String updateStatus(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called updateStatus()");
		}

		try {

			String pkgId = getPkgId();
			String pkgStatusVal = request.getParameter("pkgData.status");

			pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);
			if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "PkgId: "+pkgData.getId());
			}
			PkgStatus pkgStatus = PkgStatus.fromVal(pkgStatusVal);

			if(pkgStatus!=null){
				pkgData.setStatus(pkgStatus.name());
			}

			pkgData.setModifiedDateAndStaff(getStaffData());
			CRUDOperationUtil.update(pkgData);
			String message = Discriminators.PACKAGE + " <b><i>" + pkgData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(pkgData,pkgData.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),pkgData.getHierarchy(), message);

			request.setAttribute(Attributes.PKG_ID, pkgData.getId());
			setActionChainUrl(Results.VIEW.getValue());

			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));

			return Results.DISPATCH_VIEW.getValue();
 		} catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			return generateErrorLogsAndRedirect(e, "Error while updating package of PkgId: '"+pkgData.getId()+"'.", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	@SkipValidation
	public String updateMode(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called updateMode()");
		}

		PrintWriter out = null;
		try {
			out = response.getWriter();
			String pkgId = request.getParameter(Attributes.PKG_ID);
			String pkgModeVal = request.getParameter(Attributes.PKG_MODE);

			pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);
			LogManager.getLogger().debug(MODULE, "PkgId: "+pkgData.getId());

			PkgMode pkgMode = PkgMode.getMode(pkgModeVal);
			PkgMode pkgNextMode = pkgMode.getNextMode();
			if(pkgNextMode!=null){
				pkgData.setPackageMode(pkgNextMode.val);
			}

			List<PolicyDetail> policyDetails = DefaultNVSMXContext.getContext().getPolicyRepository().getPolicyDetail(pkgData.getName());
			JsonObject object = new JsonObject();
			if(pkgNextMode == PkgMode.LIVE) {
				if (Collectionz.isNullOrEmpty(policyDetails)) {
					getLogger().error(MODULE, "Unable to change package mode to "+ pkgNextMode +". Reason: Policy is not reloaded");
					object.addProperty("responseCode", String.valueOf(ResultCode.PRECONDITION_FAILED.code));
					object.addProperty("responseMessage", "You are recommended to reload policies before updating mode to " + pkgNextMode);
					out.print(object.toString());
					out.flush();
					return null;
				}

				for (PolicyDetail policyDetail : policyDetails) {
					String remark = policyDetail.getRemark();
					PolicyStatus status = policyDetail.getStatus();
					if ((status != PolicyStatus.SUCCESS || status != PolicyStatus.PARTIAL_SUCCESS) && Strings.isNullOrEmpty(remark) == false) {
						getLogger().error(MODULE, "Unable to change package mode to "+ pkgNextMode +".\n " +
								"Reason: Policy is failed with status " + status + ", Fail Reasons: " + remark);
						object.addProperty("responseCode", String.valueOf(ResultCode.PRECONDITION_FAILED.code));
						object.addProperty("responseMessage", "Unable to change package mode to "+ pkgNextMode +".\n " +
								"Reason: Policy is failed with status " + status + ", Fail Reasons: " + remark);
						out.print(object.toString());
						out.flush();
						return null;
					}
				}
			}

			pkgData.setModifiedDateAndStaff(getStaffData());
			String groupNames = getGroupNames(pkgData);
			if(Strings.isNullOrBlank(groupNames) == false){
				pkgData.setGroupNames(groupNames);
			}
			CRUDOperationUtil.update(pkgData);
			String message = Discriminators.PACKAGE + " <b><i>" + pkgData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(pkgData,pkgData.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),pkgData.getHierarchy(), message);

			request.setAttribute(Attributes.PKG_ID, pkgData.getId());
			object.addProperty("responseCode", String.valueOf(ResultCode.SUCCESS.code));
			out.print(object.toString());
			out.flush();
			return Results.REDIRECT_ACTION.getValue();
 		} catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_VIEW);
			return generateErrorLogsAndRedirect(e, "Error while updating package of PkgId: '"+pkgData.getId()+"'.", ActionMessageKeys.UPDATE_FAILURE.key, null);
		}finally {
			out.close();
		}
	}

	@SkipValidation
	public String delete(){
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called delete()");
		}
		String[] pkgIds = request.getParameterValues("ids");
		String pkgType = null;
		try {
			if( Arrayz.isNullOrEmpty(pkgIds) == true){
				return redirectError(Discriminators.PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}

			for(String pkgId: pkgIds){

				Criteria criteria = getHibernateProductOffer();
				criteria.add(Restrictions.eq("dataServicePkgData.id", pkgId));
                List<ProductOfferData> productOffers = criteria.list();

				pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);

				if(productOffers.isEmpty()){
					String message = Discriminators.PACKAGE + " <b><i>" + pkgData.getName() + "</i></b> " + " Deleted";
					CRUDOperationUtil.audit(pkgData,pkgData.getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),pkgData.getHierarchy(), message);
					pkgData.setDeletedStatus();
					pkgType = pkgData.getType();
					CRUDOperationUtil.update(pkgData);
				} else {
					if(pkgData!=null){

						addActionMessage("Could not delete package "+pkgData.getName()+". Reason: "+pkgData.getName()+" " +
                                "has reference in offer("+getOfferNames(productOffers)+")");
					}
				}

			}
			if(Strings.isNullOrBlank(pkgType) == false && PkgType.PROMOTIONAL.name().equalsIgnoreCase(pkgType)){
				setActionChainUrl(NVSMXCommonConstants.ACTION_PROMOTIONAL_PKG_SEARCH);
			}else{
				setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_SEARCH);
			}

			if(getActionMessages().isEmpty()){
				MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
				addActionMessage(messageFormat.format(messageParameter));
			}
			return Results.REDIRECT_ACTION.getValue();

		}catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while fetching Package data for delete operation.", ActionMessageKeys.DELETE_FAILURE.key, Results.LIST.getValue());
		}
	}

	private String getOfferNames(List<ProductOfferData> productOfferDataList){
	    StringBuilder names = new StringBuilder();

	    for(ProductOfferData data: productOfferDataList){
	        names.append(data.getName()+", ");
        }

        return names.substring(0,names.length()-2);
    }

	private Criteria getHibernateProductOffer(){
		return HibernateSessionFactory.getSession().createCriteria(ProductOfferData.class)
                .add(Restrictions.ne(CRUDOperationUtil.STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
	}

	@SkipValidation
	public String export(){
		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called export()");
		}
		String[] pkgIds = request.getParameterValues("ids");
		List<PkgData> pkgDatas = new ArrayList<PkgData>();

		BufferedWriter writer = null;
		PrintWriter out = null;
		try {
			if(isImportExportOperationInProgress()){
				return redirectError(Discriminators.PACKAGE , "pkg.importexport.operation", Results.LIST.getValue(),false);
			}
			makeImportExportOperationInProgress(true);
			if( Arrayz.isNullOrEmpty(pkgIds) ){
				makeImportExportOperationInProgress(false);
				return redirectError(Discriminators.PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
			Map<String,String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
			PkgContainer pkgContainer = new PkgContainer();
			pkgDatas = CRUDOperationUtil.getAllByIds(PkgData.class, pkgIds);
			for(PkgData pkg : pkgDatas){

				if(Strings.isNullOrBlank(pkg.getGroups())){
					pkg.setGroups(CommonConstants.DEFAULT_GROUP);
				}
				importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, pkg);
				setQuotaProfileForQosProfile(pkg);
				setQuotaProfileForUsageNotification(pkg);
			}

			pkgContainer.setPkgData(pkgDatas);

			StringWriter stringWriter = new StringWriter();
			ConfigUtil.serialize(stringWriter, PkgContainer.class, pkgContainer);

			String pkgInfo = stringWriter.toString();
			if(Strings.isNullOrBlank(pkgInfo)){
				if(LogManager.getLogger().isWarnLogLevel()){
					LogManager.getLogger().warn(MODULE, "Can not find content for the package.");
				}
				throw new Exception("Can not find data as string for the package");
			}
			response.setContentType("text/xml");
			response.setHeader("Content-Disposition", "attachment; filename=\""+getFileName(pkgData.getType())+"\"");

			out = response.getWriter();
			writer = new BufferedWriter(out);
			writer.write(pkgInfo,0,pkgInfo.length());
			writer.flush();


			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "All the Packages are exported successfully");
			}
			addActionMessage("Packages are exported successfully");
			makeImportExportOperationInProgress(false);
			request.getSession().setAttribute(Attributes.LAST_URI, NVSMXCommonConstants.ACTION_PKG_SEARCH);
			return 	Results.EXPORT_COMPLETED.getValue();
 		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while fetching Package data for export operation.", ActionMessageKeys.EXPORT_FAILURE.key, Results.LIST.getValue());
		}finally {
			Closeables.closeQuietly(writer);
			Closeables.closeQuietly(out);
		}
	}

	private String getFileName(String pkgType) {
		StringBuilder fileName = new StringBuilder();
		if(PkgType.PROMOTIONAL.name().equalsIgnoreCase(pkgType) == false){
			fileName.append(EXPORT_PKG);
		}
		else{
          fileName.append(EXPORT_PROMOTIONAL_PKG);
		}
		fileName.append(NVSMXUtil.simpleDateFormatPool.get().format(new Date())).append(XML);
		return fileName.toString();
	}

	private void setQuotaProfileForUsageNotification(PkgData pkg) {
		for(UsageNotificationData usageNotification : pkg.getUsageNotificationDatas()){
			QuotaProfileData quotaProfileData = usageNotification.getQuotaProfile();
			if(quotaProfileData != null){
				usageNotification.setQuotaProfileId(quotaProfileData.getId());
			}

		}
	}

	private void setQuotaProfileForQosProfile(PkgData pkgData) {

		for (QosProfileData qosProfile : pkgData.getQosProfiles()) {
			if (QuotaProfileType.USAGE_METERING_BASED == pkgData.getQuotaProfileType()) {
				QuotaProfileData quotaProfileData = qosProfile.getQuotaProfile();
				if (quotaProfileData != null) {
					qosProfile.setQuotaProfileId(quotaProfileData.getId());
					qosProfile.setQuotaProfileName(quotaProfileData.getName());
				}
			} else if (QuotaProfileType.SY_COUNTER_BASED == pkgData.getQuotaProfileType()) {
				SyQuotaProfileData quotaProfileData = qosProfile.getSyQuotaProfileData();
				if (quotaProfileData != null) {
					qosProfile.setQuotaProfileId(quotaProfileData.getId());
					qosProfile.setQuotaProfileName(quotaProfileData.getName());
				}
			} else if (QuotaProfileType.RnC_BASED == pkgData.getQuotaProfileType()) {
				RncProfileData quotaProfileData = qosProfile.getRncProfileData();
				if (quotaProfileData != null) {
					qosProfile.setQuotaProfileId(quotaProfileData.getId());
					qosProfile.setQuotaProfileName(quotaProfileData.getName());
				}
                if (PkgType.BASE.name().equalsIgnoreCase(pkgData.getType())) {
                    DataRateCardData dataRateCardData = qosProfile.getRateCardData();
                    if (dataRateCardData != null) {
                        qosProfile.setRateCardId(dataRateCardData.getId());
                        qosProfile.setRateCardName(dataRateCardData.getName());
                    }
                }
			}
			Iterator<QosProfileDetailData> iterator = qosProfile.getQosProfileDetailDataList().iterator();
			while (iterator.hasNext()) {

				QosProfileDetailData qosProfileDetail = iterator.next();
				Iterator<PCCRuleData> pccRuleIterator = qosProfileDetail.getPccRules().iterator();
				List<GlobalPCCRuleData> globalPCCRuleDatas = new ArrayList<GlobalPCCRuleData>();
				while (pccRuleIterator.hasNext()) {
					PCCRuleData pcc = pccRuleIterator.next();
					if (PCCRuleScope.GLOBAL == pcc.getScope()) {
						globalPCCRuleDatas.add(new GlobalPCCRuleData(pcc.getId(), pcc.getName(), pcc.getScope()));
						pccRuleIterator.remove();
					}

				}
				qosProfileDetail.setGlobalPCCRules(globalPCCRuleDatas);

				if(Collectionz.isNullOrEmpty(qosProfileDetail.getChargingRuleBaseNames()) == false) {
					List<ChargingRuleData> chargingRuleDatas = new ArrayList<ChargingRuleData>();
					for (ChargingRuleBaseNameData chargingRuleBaseNameData : qosProfileDetail.getChargingRuleBaseNames()) {
						chargingRuleDatas.add(new ChargingRuleData(chargingRuleBaseNameData.getId(), chargingRuleBaseNameData.getName()));
					}
					qosProfileDetail.getChargingRuleBaseNames().clear();
					qosProfileDetail.setChargingRuleDatas(chargingRuleDatas);
				}

				if ( Collectionz.isNullOrEmpty(qosProfileDetail.getPccRules())==false ) {
					RatingGroupUtil.setChargingKeyName(qosProfileDetail.getPccRules());
				}

			}
		}
	}

	private void setGroupNamesBasedOnId(Map<String,String> groupNamesBasedOnId,PkgData pkg){
		StringBuilder groupNames = new StringBuilder();
		List<String> groupIds = SPLITTER.split(pkg.getGroups());
		for(String groupId : groupIds){
			String groupName = groupNamesBasedOnId.get(groupId);
			if(Strings.isNullOrBlank(groupName) == false){
				groupNames.append(groupName);
				groupNames.append(CommonConstants.COMMA);
			}
		}
		if (Strings.isNullOrBlank(groupNames.toString()) == false) {
			groupNames.deleteCharAt(groupNames.lastIndexOf(","));
			pkg.setGroupNames(groupNames.toString());
		}

	}

	@SkipValidation
	public String exportAll(){
		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called exportAll()");
		}
		List<PkgData> pkgDatas = new ArrayList<PkgData>();
		BufferedWriter writer = null;
		PrintWriter out = null;
		try {
			if(isImportExportOperationInProgress() == true){
				return redirectError(Discriminators.PACKAGE, "pkg.importexport.operation", Results.LIST.getValue(), false);
			}

			Map<String,String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
			makeImportExportOperationInProgress(true);

			pkgDatas = PkgDAO.findPackages(getStaffBelongingGroups(), pkgData.getType());
			if (Collectionz.isNullOrEmpty(pkgDatas)) {
				makeImportExportOperationInProgress(false);
				return redirectError(Discriminators.PACKAGE, "pkg.importexport.nopackagefound", Results.LIST.getValue(), false);
			}
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Export All operation started for packages");
			}

			List<PkgData> pkgDataToBeExported = new ArrayList<PkgData>();
			for(PkgData pkgData : pkgDatas){
				List<String> groups = SPLITTER.split(pkgData.getGroups());
				Reason reason = new Reason(pkgData.getName());
				boolean isExportAllowedForGroup = importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(), reason, ACLModules.DATAPKG, ACLAction.EXPORT.name(), getStaffData().getUserName());
				if(isExportAllowedForGroup){
					pkgDataToBeExported.add(pkgData);
					importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, pkgData);
					setQuotaProfileForQosProfile(pkgData);
				}
			}
			if (Collectionz.isNullOrEmpty(pkgDataToBeExported)) {
				makeImportExportOperationInProgress(false);
				LogManager.getLogger().warn(MODULE, getText("pkg.importexport.nopackagefound") + ", Staff doesn't have export rights for any package.");
				return redirectError(MODULE, "pkg.importexport.nopackagefound", Results.LIST.getValue(),false);
			}

			PkgContainer pkgContainer = new PkgContainer();
			pkgContainer.setPkgData(pkgDataToBeExported);

			StringWriter stringWriter = new StringWriter();
			ConfigUtil.serialize(stringWriter, PkgContainer.class, pkgContainer);
			String pkgInfo = stringWriter.toString();
			if(Strings.isNullOrBlank(pkgInfo)){
				if(LogManager.getLogger().isWarnLogLevel()){
					LogManager.getLogger().warn(MODULE, "Can not find content for the package.");
				}
				throw new Exception("Can not find data as string for the package");
			}
			response.setContentType("text/xml");
			response.setHeader("Content-Disposition", "attachment; filename=\""+getFileName(pkgData.getType())+"\"");
			out = response.getWriter();
			writer = new BufferedWriter(out);
			writer.write(pkgInfo,0,pkgInfo.length());
			writer.flush();

			makeImportExportOperationInProgress(false);
			request.getSession().setAttribute(Attributes.LAST_URI, NVSMXCommonConstants.ACTION_PKG_SEARCH);
			LogManager.getLogger().debug(MODULE,"Export All operation ended for packages");
			return Results.EXPORT_COMPLETED.getValue();

 		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while exporting data packages.", ActionMessageKeys.EXPORT_FAILURE.key, Results.LIST.getValue());
		}finally {
			Closeables.closeQuietly(writer);
			Closeables.closeQuietly(out);
		}
	}

	@SkipValidation
	public String importPkg() throws ServletException {
		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called import()");
		}
		boolean importOperation = isImportExportOperationInProgress();
		if(importOperation){
			return redirectError(Discriminators.PACKAGE, "pkg.importexport.operation", Results.LIST.getValue(), false);
		}
		if(getImportedFile() == null){
			makeImportExportOperationInProgress(false);
			LogManager.getLogger().error(MODULE,"Error while importing Package data. Reason: File not found");
			return redirectError(Discriminators.PACKAGE,ActionMessageKeys.DATA_NOT_FOUND.key,Results.LIST.getValue(),false);
		}
		try {
			if(TEXT_XML.equals(getImportedFileContentType()) == false ){
				LogManager.getLogger().error(MODULE,"Error while importing package data. Reason: Invalid File type is configured. Only XML File is supported for importing package");
				makeImportExportOperationInProgress(false);
				return redirectError(Discriminators.PACKAGE, "pkg.importexport.invalidfiletype", Results.LIST.getValue(), false);
			}
			PkgContainer policyGroupContainer =  ConfigUtil.deserialize(getImportedFile(), PkgContainer.class);
		     if(policyGroupContainer == null) {
				 return Results.REDIRECT_ERROR.getValue();
			 }
		     	List<PkgData> pkgDatas = policyGroupContainer.getPkgData();

				skipPackageHaveNullName(pkgDatas);

		     	request.getSession().setAttribute(Attributes.PKG_DATAS,pkgDatas);

		     	Gson gson = GsonFactory.defaultInstance();
				JsonArray importPkgJson = gson.toJsonTree(pkgDatas,new TypeToken<List<PkgData>>() {}.getType()).getAsJsonArray();
				request.setAttribute("importedPkgs", importPkgJson);
		     	return Results.IMPORT_PKG.getValue();

		} catch (IOException e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while importing Package data.", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
		} catch (JAXBException e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while importing Package data due to XML processing failure.", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
	    } catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while importing Package data.", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
		}
	}

	private void skipPackageHaveNullName(List<PkgData> pkgDatas) {
        /*
        This method is used to remove the PkgData have null name from the list of pkgDatas.
        It will also set proper message in request attribute(invalidEntityMessage) to display in GUI.
        */

		Predicate predicate = new Predicate<PkgData>() {
			@Override
			public boolean apply(PkgData input) {
				if(Strings.isNullOrBlank(input.getName())){
					LogManager.getLogger().info(MODULE, "Found PkgData with null name. Skipping Import process for PkgData: " + input);
					return false;
				}
				return true;
			};
		};

		String message = ImportExportUtil.removeInvalidEntitiesAndGetMessage(pkgDatas, predicate, Discriminators.PACKAGE);
		request.setAttribute(INVALID_ENTITY_MESSAGE,message);
	}

	@SkipValidation
	public String importData() {
		LogManager.getLogger().debug(MODULE, "Method called importData()");
		try {
			String selectedEmergencyPkgIndexes = request.getParameter(Attributes.SELECTED_INDEXES);
			String action = request.getParameter(Attributes.USER_ACTION);

			boolean importOperation = isImportExportOperationInProgress();
			if(importOperation == true){
				return redirectError(Discriminators.PACKAGE, "pkg.importexport.operation", Results.LIST.getValue(), false);
			}
			List<PkgData> pkgDataList = (List<PkgData>) request.getSession().getAttribute(Attributes.PKG_DATAS);

			List<PkgData> pkgDatas = Collectionz.newArrayList();

			if(Strings.isNullOrBlank(selectedEmergencyPkgIndexes) == false && Collectionz.isNullOrEmpty(pkgDataList) == false) {
				makeImportExportOperationInProgress(true);
				pkgDatas = new ImportEntityAccumulator<PkgData>(pkgDataList, selectedEmergencyPkgIndexes).get();
								}


			List<Reason> reasons = importPackages(pkgDatas, action);
			Gson gson = GsonFactory.defaultInstance();
			JsonArray importPkgResultJson = gson.toJsonTree(reasons, new TypeToken<List<Reason>>() {
			}.getType()).getAsJsonArray();
			request.setAttribute("importStatus", importPkgResultJson);
			makeImportExportOperationInProgress(false);
		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while importing Package data.", ActionMessageKeys.IMPORT_FAILURE.key, Results.IMPORT_STATUS_REPORT.getValue());
		}
		return Results.IMPORT_STATUS_REPORT.getValue();
	}


	@Override
	public PkgData getModel() {
		return pkgData;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

	private List<DataServiceTypeData> getServiceTypes() {
		return CRUDOperationUtil.findAllWhichIsNotDeleted(DataServiceTypeData.class);

	}

	public List<DataServiceTypeData> getDataServiceTypeData() {
		return dataServiceTypeData;
	}

	public void setDataServiceTypeData(List<DataServiceTypeData> dataServiceTypeData) {
		this.dataServiceTypeData = dataServiceTypeData;
	}

	private void setTemplateDatas(String pkgId) {
		List<NotificationTemplateData> templateDatas = CRUDOperationUtil.findAllByStatus(NotificationTemplateData.class, null, null, null, null);
		PkgData pkgData = CRUDOperationUtil.get(PkgData.class,pkgId);
		CRUDOperationUtil.filterpackages(templateDatas, pkgData.getGroups());
		for (NotificationTemplateData notificationTemplateData : templateDatas) {
			switch (notificationTemplateData.getTemplateType()) {
				case EMAIL:
					emailTemplateDatas.add(notificationTemplateData);
					break;
				case SMS:
					smsTemplateDatas.add(notificationTemplateData);
					break;

			}
		}
	}

	public List<NotificationTemplateData> getEmailTemplateDatas() {
		return emailTemplateDatas;
	}

	public void setEmailTemplateDatas(List<NotificationTemplateData> emailTemplateDatas) {
		this.emailTemplateDatas = emailTemplateDatas;
	}

	public List<NotificationTemplateData> getSmsTemplateDatas() {
		return smsTemplateDatas;
	}

	public void setSmsTemplateDatas(List<NotificationTemplateData> smsTemplateDatas) {
		this.smsTemplateDatas = smsTemplateDatas;
	}

	public List<AggregationKey> getAggregationKeys() {
		return aggregationKeys;
	}

	public void setAggregationKeys(List<AggregationKey> aggregationKeys) {
		this.aggregationKeys = aggregationKeys;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groups) {
		this.groupList = groups;
	}

    @Override
	@SkipValidation
    public void validate() {

		if(PkgType.PROMOTIONAL.name().equalsIgnoreCase(pkgData.getType())){

			if (pkgData.getAvailabilityStartDate() == null) {
				addFieldError("pkgData.availabilityStartDate", getText("pkg.promotional.availability.start.Date.error"));
				return;
			}
			if (pkgData.getAvailabilityEndDate() == null) {
				addFieldError("pkgData.availabilityEndDate", getText("pkg.promotional.availability.end.Date.error"));
				return;
			}
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			if (pkgData.getAvailabilityEndDate().after(currentTime) == false) {
                addFieldError("pkgData.availabilityEndDate", getText("pkg.availability.enddate.greater.than.current.time"));
				return;
            }
			if (pkgData.getAvailabilityEndDate().after(pkgData.getAvailabilityStartDate()) == false) {
				addFieldError("pkgData.availabilityEndDate", getText("pkg.availability.enddate.greater.than.start.time"));
				return;
       	 	}
			if ((pkgData.getAvailabilityEndDate().getTime() - pkgData.getAvailabilityStartDate().getTime()) < CommonConstants.TEN_MINUTES) {
				addFieldError("pkgData.availabilityEndDate", getText("pkg.availability.mustbe.atleast.ten.minutes"));
			}
		}
    }

    public boolean validateDataPackageDetails(String pkgId){
		Criterion dataPackageFilterCrieteria = Restrictions.eq("dataServicePkgData.id",pkgId);
		List<ProductOfferData> productOfferDataList = CRUDOperationUtil.findAll(ProductOfferData.class, dataPackageFilterCrieteria);
		if(productOfferDataList == null || CollectionUtils.isEmpty(productOfferDataList)){
			return true;
		}
		return false;
	}

	public List<GroupData> getStaffBelongingGroupList() {
		return staffBelongingGroupList;
	}

	public void setStaffBelongingGroupList(List<GroupData> staffBelongingGroupList) {
		this.staffBelongingGroupList = staffBelongingGroupList;
	}

	public List<String> getQosProfileDataNames() {
		return qosProfileDataNames;
	}

	public void setQosProfileDataNames(List<String> qosProfileDataNames) {
		this.qosProfileDataNames = qosProfileDataNames;
	}


	@Override
	protected List<PkgData> getSearchResult(String criteriaJson,Class beanType, int startIndex, int maxRecords,String sortColumnName, String sortColumnOrder,String staffBelongingGroups) throws Exception {
		String pkgTypeVal = request.getParameter(Attributes.PKG_TYPE);
		if(PkgType.EMERGENCY.name().equalsIgnoreCase(pkgTypeVal)){
			sortColumnName = "orderNumber";
			sortColumnOrder = "asc";
		}else if(PkgType.PROMOTIONAL.name().equalsIgnoreCase(pkgTypeVal)){
			sortColumnName = "orderNumber";
			sortColumnOrder = "asc";
			Integer noOfPromotionalPackages = PkgDAO.getNoOfPackagesOfType(PkgType.PROMOTIONAL);
			request.setAttribute("noOfPromotionalPackages",noOfPromotionalPackages);
		}else{
			pkgTypeVal = PkgType.BASE.name();
		}

		request.getSession().setAttribute(Attributes.PKG_TYPE, pkgTypeVal);

		if(Strings.isNullOrBlank(criteriaJson)==false){
			Gson gson = GsonFactory.defaultInstance();
			PkgData pkgData = gson.fromJson(criteriaJson, PkgData.class);
			return PkgDAO.searchByCriteria(pkgData, startIndex, maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups);
		}else{
			sortColumnName=Strings.isNullOrBlank(sortColumnName)?"name":sortColumnName;
			sortColumnOrder=Strings.isNullOrBlank(sortColumnOrder)?"desc":sortColumnOrder;
			return PkgDAO.findPackages(startIndex, maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups, pkgTypeVal);
		}
	}

	/**
	 * Set list of QoSProfileData in list of QoSProfileWrapper and convert it into the JsonArray
	 * @param qosProfileDatas
	 * @author Dhyani.Raval
	 */
	private void setQosProfileDetailWrappers(List<QosProfileData> qosProfileDatas){
		List<QosProfileDetailWrapper> qosProfileDetailWrappers = Collectionz.newArrayList();
		for( QosProfileData qosProfile : qosProfileDatas ){
			QosProfileDetailWrapperBuilder qosProfileDetailWrapper = new QosProfileDetailWrapperBuilder(qosProfile.getId(),qosProfile.getName(), qosProfile.getOrderNo());
			qosProfileDetailWrapper.withQosProfileDetails(qosProfile.getQosProfileDetailDataList());
			if (qosProfile.getQuotaProfile() != null) {
					qosProfileDetailWrapper.withQuotaProfile(qosProfile.getQuotaProfile());
			} else if (qosProfile.getSyQuotaProfileData() != null){
				qosProfileDetailWrapper.withSyQuotaProfile(qosProfile.getSyQuotaProfileData());
			} else if( qosProfile.getRncProfileData() != null) {
				qosProfileDetailWrapper.withRncProfile(qosProfile.getRncProfileData());
			}
			qosProfileDetailWrappers.add(qosProfileDetailWrapper.build());


		}
		Gson gson = GsonFactory.defaultInstance();
		setQosProfilesAsJsonString(gson.toJsonTree(qosProfileDetailWrappers).getAsJsonArray().toString());
	}

	public String getQosProfilesAsJsonString() {
		return qosProfilesAsJsonString;
	}

	public void setQosProfilesAsJsonString(String qosProfilesInJsonString) {
		this.qosProfilesAsJsonString = qosProfilesInJsonString;
	}

	/**
	 * Set list of SyQuotaProfile in list of SyQuotaProfileWrapper and convert it into the JsonArray
	 * @param syQuotaProfileDatas
	 * @author Dhyani.Raval
	 */

	private void setSyQuotaProfileWraperes(List<SyQuotaProfileData> syQuotaProfileDatas){
		List<SyQuotaProfileWrapper> syQuotaProfileWrappers = Collectionz.newArrayList();
		for(SyQuotaProfileData syQuotaProfileData : syQuotaProfileDatas){
			SyQuotaProfileWrapper syQuotaProfileWrapper = new SyQuotaProfileWrapperBuilder().withQuotaProfileDetails(syQuotaProfileData).build();
			syQuotaProfileWrappers.add(syQuotaProfileWrapper);
		}
		Gson gson = GsonFactory.defaultInstance();
		setSyQuotaProfilesAsJsonString(gson.toJsonTree(syQuotaProfileWrappers).getAsJsonArray().toString());
	}

	public String getSyQuotaProfilesAsJsonString() {
		return syQuotaProfilesAsJsonString;
	}

	public void setSyQuotaProfilesAsJsonString(String syQuotaProfilesInJsonString) {
		this.syQuotaProfilesAsJsonString = syQuotaProfilesInJsonString;
	}

	/**
	 * Set list of Usage metering QuotaProfiles in list of QuotaProfileWrapper and convert it into the JsonArray
	 * @param quotaProfileDatas
	 * @author Dhyani.Raval
	 */
	private void setUsageMeteringQuotaprofileWrappers(List<QuotaProfileData> quotaProfileDatas){
		List<QuotaProfileWrapper> quotaProfileWrappers = Collectionz.newArrayList();

    	for(QuotaProfileData quotaProfile:quotaProfileDatas){
    	    	QuotaProfileWrapper wrapper = new QuotaProfileWrapperBuilder(quotaProfile.getId(), quotaProfile.getName()).withQuotaProfileDetails(quotaProfile.getQuotaProfileDetailDatas(),0).build();
    	    	quotaProfileWrappers.add(wrapper);
    	}
    	Gson gson = GsonFactory.defaultInstance();
    	setUsageMeteringQuotaProfilesAsJsonString(gson.toJsonTree(quotaProfileWrappers).getAsJsonArray().toString());
	}


	public String getUsageMeteringQuotaProfilesAsJsonString() {
		return usageMeteringQuotaProfilesAsJsonString;
	}

	public void setUsageMeteringQuotaProfilesAsJsonString(
			String usageMeteringQuotaProfilesInJsonString) {
		this.usageMeteringQuotaProfilesAsJsonString = usageMeteringQuotaProfilesInJsonString;
	}


	public List<PkgData> getPkgs() {
		return pkgs;
	}

	public void setPkgs(List<PkgData> pkgs) {
		this.pkgs = pkgs;
	}


	private List<Reason> importPackages(List<PkgData> pkgDataForImport,String action)  {
		List<Reason> reasons  = Collectionz.newArrayList();
		if (Collectionz.isNullOrEmpty(pkgDataForImport)) {
			LogManager.getLogger().warn(MODULE, "No Package found for Import operation");
			return reasons;
		}

		PkgTypeValidator pkgTypeValidator = PkgTypeValidator.create(request);

		for(PkgData importPkg : pkgDataForImport){
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Import Operation started for package: " + importPkg.getName());
			}
			Reason reason = new Reason(importPkg.getName());
			List<String> failedReasons = pkgTypeValidator.validate(importPkg);
			if (Collectionz.isNullOrEmpty(failedReasons) == false) {
				reason.setRemarks(getRemarksFromSubReasons(failedReasons));
				reason.setMessages(NVSMXCommonConstants.ENTITY_IMPORT_FAIL);
				reasons.add(reason);
				continue;
			}
			try {
				//Setting staff information to the package
				importPkg.setCreatedByStaff((StaffData)request.getSession().getAttribute("staffData"));
				importPkg.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				importPkg.setModifiedByStaff(importPkg.getCreatedByStaff());
				importPkg.setModifiedDate(importPkg.getCreatedDate());
				boolean isExistGroup = true;
				List<String> groups = new ArrayList<String>();
				if(Strings.isNullOrBlank(importPkg.getGroupNames())){
					importPkg.setGroups(CommonConstants.DEFAULT_GROUP_ID);
					groups.add(CommonConstants.DEFAULT_GROUP_ID);
				}else {
					isExistGroup = importExportUtil.getGroupIdsFromName(importPkg, reason, groups);
				}
				if(isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
					List<String> staffBelongingGroups = CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
					importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(), reason, pkgTypeValidator.getAclModule(), ACLAction.IMPORT.name(), getStaffData().getUserName(), staffBelongingGroups, importPkg, importPkg.getName());


					importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(), reason, pkgTypeValidator.getAclModule(), ACLAction.IMPORT.name(), getStaffData().getUserName());

					if (CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
						importPkg.setGroups(Strings.join(",",groups));
						importExportUtil.validateAndImportInformation(importPkg, action, reason);
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Import Operation finished for package: " + importPkg.getName());
						}
					}
				}
				reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
				reasons.add(reason);

			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Error while importing package data: " + importPkg.getName());
				LogManager.getLogger().trace(MODULE,e);
				String errorMessage = "Failed to import package due to internal error. Kindly refer logs for further details";
				List<String> errors = new ArrayList<String>();
				errors.add(errorMessage);
				reason.setMessages("FAIL");
				reason.addFailReason(errors);
				reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
				reasons.add(reason);
			}
		}

		Collections.sort(reasons, new Comparator<Reason>() {
			@Override
			public int compare(Reason reason1, Reason reason2) {
				return reason1.getMessages().compareTo(reason2.getMessages());
			}
		});
		return reasons;
	}

	private String getRemarksFromSubReasons(List<String> subReasons){
		StringBuilder sb = new StringBuilder();
		if(Collectionz.isNullOrEmpty(subReasons)){
			sb.append(" ---- ");
		}else {
			for (String str : subReasons) {
				sb.append(str);
				sb.append("<br/>");
			}
		}
		return sb.toString();
	}

	@SkipValidation
	public String manageOrder(){

		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Method called manageOrder()");
		}
		String staffBelongingGroups = (String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
		groupWiseOrderMap =PkgDAO.fetchGroupOrderWisePkgMap(PkgType.PROMOTIONAL, staffBelongingGroups);

		return Results.MANAGE_ORDER.getValue();
	}

	@SkipValidation
	public String manageOrderPromotionalPackages(){
		if( LogManager.getLogger().isDebugLogLevel() ){
			LogManager.getLogger().debug(MODULE, "Method called manageOrderPromotionalPackages()");
		}

		setActionChainUrl(NVSMXCommonConstants.ACTION_PROMOTIONAL_PKG_SEARCH);

		String[] pkgGroupDataIdArray = request.getParameterValues("pkgGroupDataIdArray");

		try{

			if(Arrayz.isNullOrEmpty(pkgGroupDataIdArray) == true){
				LogManager.getLogger().error(MODULE, "Failed to update Packages Order. Reason: Received NULL data for Packages Ids and Orders");
				return redirectError(Discriminators.PACKAGE, "pkg.promotional.manage.order.failure", Results.REDIRECT_ACTION.getValue());
			}

			for (int i = 0; i < pkgGroupDataIdArray.length; i++) {
                String pkgGroupDataId = pkgGroupDataIdArray[i];
				PkgGroupOrderData pkgGroupData = CRUDOperationUtil.get(PkgGroupOrderData.class,pkgGroupDataId);
				if(pkgGroupData == null){
					throw new NullPointerException("Package Not found for ID: "+pkgGroupDataId);
				}
				String groupName = GroupDAO.getGroupName(pkgGroupData.getGroupId());
				pkgGroupData.setGroupName(groupName);
				PkgData pkgOldData = pkgGroupData.getPkgData();

				JsonObject jsonOldObject = pkgGroupData.toJson();
				pkgGroupData.setOrderNumber(i+1);

				JsonObject jsonNewObject = pkgGroupData.toJson();
				CRUDOperationUtil.update(pkgGroupData);

				JsonArray difference = ObjectDiffer.diff(jsonOldObject, jsonNewObject);
				String message = Discriminators.PACKAGE + " <b><i>" + pkgOldData.getName() + "</i></b> " + "Updated";
				CRUDOperationUtil.audit(pkgOldData, pkgOldData.getName(), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference, pkgOldData.getHierarchy(), message);

				if( LogManager.getLogger().isDebugLogLevel() ){
					LogManager.getLogger().debug(MODULE, "Package '" + pkgOldData + "' order changed to " + pkgGroupData.getOrderNumber());
				}

			}

			MessageFormat messageFormat = new MessageFormat(getText("pkg.promotional.manage.order.success"));
			addActionMessage(messageFormat.format(messageParameter));

		} catch(Exception ex) {
			return generateErrorLogsAndRedirect(ex, "Error while Updating Promotional Packages Order.", ActionMessageKeys.IMPORT_FAILURE.key, Results.REDIRECT_ACTION.getValue());
		}
		return Results.REDIRECT_ACTION.getValue();
	}

	/**This method checks for group wise limit for packages.Flow of the method will be
	 *
	 * <p>Find out group wise packages Map </p>
	 *
	 * If Map is null or Empty i.e. no package created so far<br/>
	 * <pre>
	 *     return false
	 * </pre>
	 *
	 * If package has only one group & that is Default Group then
	 *<pre>
	 *	check for default group limit
	 *</pre>
	 *
	 *If packages has multiple groups but those group contains Default Group then
	 *  <pre>
	 *    check for default group limit
	 *  </pre>
	 *		else
	 *  <pre>
	 *      check for every group
	 *  </pre>
	 *
	 *
	 * @param pkgData package for which limit is to be check
	 * @param key    struts action key
	 * @param mode   action mode update/create
	 * @return
	 */

    private boolean isGroupWiseLimitReach(PkgData pkgData,String mode, String key) {
		Map<String, List<PkgData>> groupWisePkgs = PkgDAO.fetchGroupWisePkgMap(pkgData.getId(), mode, PkgType.valueOf(pkgData.getType()));

		if(Maps.isNullOrEmpty(groupWisePkgs) == true){
			return false;
		}

		// check if package has only one group & that is Default Group then check limit for Default Group
		if (CommonConstants.DEFAULT_GROUP_ID.equalsIgnoreCase(pkgData.getGroups()) == true) {
			return checkLimitForGroups(groupWisePkgs, CommonConstants.DEFAULT_GROUP_MAX_GLOBAL_PKGS, key, CommonConstants.DEFAULT_GROUP_ID);

		}

		// check if package has multiple groups & one of the group is Default Group then check limit for Default Group
		final List<String> groupIds = CommonConstants.COMMA_SPLITTER.split(pkgData.getGroups());
		if (groupIds.contains(CommonConstants.DEFAULT_GROUP_ID) == true) {
			return checkLimitForGroups(groupWisePkgs, CommonConstants.DEFAULT_GROUP_MAX_GLOBAL_PKGS, key, CommonConstants.DEFAULT_GROUP_ID);

		}

		// check limit for individual group
		String[] groupIdArray = new String[groupIds.size()];
		return checkLimitForGroups(groupWisePkgs, CommonConstants.GROUP_WISE_MAX_GLOBAL_PKGS, key, groupIds.toArray(groupIdArray));
	}

    private Boolean checkLimitForGroups(Map<String, List<PkgData>> groupWisePkgs, int groupWiseLimit, String key, String... groupIds) {
		for(String groupId : groupIds){
			List<PkgData> groupWiseList = groupWisePkgs.get(groupId);
			if (Collectionz.isNullOrEmpty(groupWiseList) == true) {
				continue;
			}
			if (groupWiseList.size() >= groupWiseLimit) {
				GroupData groupData = CRUDOperationUtil.get(GroupData.class,groupId);
				LogManager.getLogger().error(MODULE, "Group Wise Limit has been reached for Group" + groupData.getName());
				addActionError(Discriminators.PACKAGE + " " + getText(key));
				addActionError(getText("pkg.groupwise.limit.reach.error", new String[]{groupData.getName()}));
				return true;
			}
		}
		return false;
	}

	private String generateErrorLogsAndRedirect(Exception e, String message, String key, String result) {
		return super.generateErrorLogsAndRedirect(Discriminators.PACKAGE, e, message, key, result);
	}

	public String getRncProfileAsJsonString() {
		return rncProfileAsJsonString;
	}

	public void setRncProfileAsJsonString(String rncProfileAsJsonString) {
		this.rncProfileAsJsonString = rncProfileAsJsonString;
	}

	public String getRateCardAsJsonString() {
		return rateCardAsJsonString;
	}

	public void setRateCardAsJsonString(String rateCardAsJsonString) {
		this.rateCardAsJsonString = rateCardAsJsonString;
	}

	public String getPccProfileValidationMessages() {
		return pccProfileValidationMessages;
	}

	public void setPccProfileValidationMessages(String pccProfileValidationMessages) {
		this.pccProfileValidationMessages = pccProfileValidationMessages;
	}

	public boolean getCurrencyUpdateAllowed(){
		return isCurrencyUpdateAllowed;
	}

	public void setCurrencyUpdateAllowed(boolean isCurrencyUpdateAllowed){
		this.isCurrencyUpdateAllowed = isCurrencyUpdateAllowed;
	}
}