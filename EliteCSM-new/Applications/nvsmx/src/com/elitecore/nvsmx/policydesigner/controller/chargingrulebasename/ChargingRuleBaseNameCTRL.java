package com.elitecore.nvsmx.policydesigner.controller.chargingrulebasename;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportEntityAccumulator;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportCTRL;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename.ChargingRuleBaseNameContainer;
import com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename.ChargingRuleBaseNameDAO;
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
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Controls Charging-Rule-Base-Name related operations
 * @author Kirpalsinh.Raj
 *
 */

public class ChargingRuleBaseNameCTRL extends ImportExportCTRL<ChargingRuleBaseNameData> implements ServletResponseAware {

	public  static final String MODULE = ChargingRuleBaseNameCTRL.class.getSimpleName();

	private static final long 	serialVersionUID = 1L;
	private static final String ACTION_VIEW 	=	"policydesigner/chargingrulebasename/ChargingRuleBaseName/view";
	private static final String ACTION_SEARCH 	=	"policydesigner/chargingrulebasename/ChargingRuleBaseName/search";

	private ChargingRuleBaseNameData 	chargingRuleBaseNameData = new ChargingRuleBaseNameData();
	private List<GroupData> 			staffBelongingGroupList = Collectionz.newArrayList();
	private List<DataServiceTypeData>   dataServiceTypeDataList = Collectionz.newArrayList();
	private List<String> 				showSelectedGroupsForUpdate = Collectionz.newArrayList();
	private Object [] 					messageParameter = {Discriminators.CHARGING_RULE_BASE_NAME};
	private String 						actionChainUrl;
	public static final String CRBN_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.description,dataList\\[\\d+\\]\\.groups";

	public String init() {

		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called init()");
		}
		String chargingRuleBaseNameId = getChargingRuleBaseNameId();
		setDataServiceTypeDataList(getServiceTypes());

		if (Strings.isNullOrBlank(chargingRuleBaseNameId)) {
			chargingRuleBaseNameData.setDescription(NVSMXUtil.getDefaultDescription(request));
			return Results.CREATE.getValue();
		}
		try {
			setActionChainUrl(ACTION_VIEW);
			chargingRuleBaseNameData = CRUDOperationUtil.get(ChargingRuleBaseNameData.class, chargingRuleBaseNameId);

			if(chargingRuleBaseNameData == null){
				LogManager.getLogger().error(MODULE, "Error while fetching ChargingRuleBaseName for update operation. Reason: ChargingRuleBaseName Id not found");
				return redirectError(Discriminators.PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}
			request.setAttribute("requestfromQosProfileView", request.getParameter("requestfromQosProfileView"));

			return Results.UPDATE.getValue();
		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Failed to fetch ChargingRuleBaseName.", ActionMessageKeys.UPDATE_FAILURE.key, Results.LIST.getValue());
		}
	}

	public String getChargingRuleBaseNameId(){

		String id = request.getParameter(Attributes.CHARGING_RULE_BASE_NAME_ID);
		if (Strings.isNullOrBlank(id)) {
			id = (String) request.getAttribute(Attributes.CHARGING_RULE_BASE_NAME_ID);
			if (Strings.isNullOrBlank(id)) {
				id = request.getParameter("chargingRuleBaseNameData.id");
			}
		}
		return id;
	}

	@InputConfig(resultName = InputConfigConstants.CREATE)
	public String create(){

		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called create()");
		}

		try {

			chargingRuleBaseNameData.setCreatedDateAndStaff(getStaffData());

			if(Strings.isNullOrBlank(getGroupIds())){
				chargingRuleBaseNameData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				chargingRuleBaseNameData.setGroups(getGroupIds());
			}

			setServiceTypes();

			CRUDOperationUtil.save(chargingRuleBaseNameData);

			String message = Discriminators.CHARGING_RULE_BASE_NAME + " <b><i>" + chargingRuleBaseNameData.getName() + "</i></b> " + "Created";
			CRUDOperationUtil.audit(chargingRuleBaseNameData, chargingRuleBaseNameData.getName(), AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), chargingRuleBaseNameData.getHierarchy(), message);
			request.setAttribute(Attributes.CHARGING_RULE_BASE_NAME_ID, chargingRuleBaseNameData.getId());

			setActionChainUrl(Results.VIEW.getValue());
			return Results.DISPATCH_VIEW.getValue();
 		}catch(Exception e) {
			return generateErrorLogsAndRedirect(e, "Failed to Create ChargingRuleBaseName.", ActionMessageKeys.CREATE_FAILURE.key, Results.LIST.getValue());
		}
	}
/*
	protected List<ChargingRuleBaseNameData> getSearchResult(String criteriaJson, Class beanType, int startIndex, int maxRecords, String sortColumnName, String sortColumnOrder, String staffBelongingGroups) throws Exception {
		setIncludeSearchProperties(NVSMXCommonConstants.DATATABLE_PARAMETERS + CRBN_INCLUDE_PARAMETERS);
		if (Strings.isNullOrEmpty(criteriaJson) == false) {
			Gson gson = GsonFactory.defaultInstance();
			Object object = gson.fromJson(criteriaJson, beanType);
			return (List)CRUDOperationUtil.searchByCriteria(object,startIndex, maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups);
		} else {
			return CRUDOperationUtil.findAllWhichIsNotDeleted(beanType,startIndex, maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups);
		}
		//return  super.getSearchResult(criteriaJson, beanType, startIndex, maxRecords, sortColumnName, sortColumnOrder, staffBelongingGroups);
	}*/

	@Override
	protected List<ChargingRuleBaseNameData> getSearchResult(String criteriaJson, Class beanType, int startIndex, int maxRecords, String sortColumnName, String sortColumnOrder, String staffBelongingGroups) throws Exception {
		return  super.getSearchResult(criteriaJson, beanType, startIndex, maxRecords, sortColumnName, sortColumnOrder, staffBelongingGroups);
	}

	@Override
	public String getIncludeProperties(){
		return CRBN_INCLUDE_PARAMETERS;
	}

	@SkipValidation
	public String initUpdate(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called initUpdate()");
		}
		try {

			String chargingRuleBaseNameId = request.getParameter(Attributes.CHARGING_RULE_BASE_NAME_ID);

			if(Strings.isNullOrBlank(chargingRuleBaseNameId)){
				setActionChainUrl(ACTION_SEARCH);
			} else {
				setActionChainUrl(ACTION_VIEW);
			}

			if( Strings.isNullOrBlank(chargingRuleBaseNameId) == true ){
				LogManager.getLogger().error(MODULE, "Error while fetching ChargingRuleBaseName for update operation. Reason: ChargingRuleBaseName Id not found");
				return redirectError(Discriminators.PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}

			setDataServiceTypeDataList(getServiceTypes());

			chargingRuleBaseNameData = CRUDOperationUtil.get(ChargingRuleBaseNameData.class, chargingRuleBaseNameId);

			setShowSelectedGroupsForUpdate(Strings.splitter(',').trimTokens().split(chargingRuleBaseNameData.getGroups()));

			List<GroupData> staffBelongingGroups = (List<GroupData>) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP);
			staffBelongingGroupList.addAll(staffBelongingGroups);

			request.setAttribute(Attributes.DATA_PACKAGE_GROUP_IDS, chargingRuleBaseNameData.getGroups());
			request.setAttribute("requestfromQosProfileView", request.getParameter("requestfromQosProfileView"));

			return Results.UPDATE.getValue();

 		}catch(Exception e) {
			return generateErrorLogsAndRedirect(e, "Failed to fetch ChargingRuleBaseName.", ActionMessageKeys.UPDATE_FAILURE.key, Results.LIST.getValue());
		}
	}
	
	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update(){
		if(getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called update()");
		}

		String requestFrom = (String)request.getParameter("requestFromQosProfile");

		request.setAttribute(Attributes.ACTION, ACTION_VIEW);
		
		if("true".equalsIgnoreCase(requestFrom)){
			setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
		}else {
			setActionChainUrl(Results.VIEW.getValue());
		}

		try {

			ChargingRuleBaseNameData chargingRuleBaseNameDataFromDB = CRUDOperationUtil.get(ChargingRuleBaseNameData.class, chargingRuleBaseNameData.getId());

			JsonObject oldJsonObject = chargingRuleBaseNameDataFromDB.toJson();
			chargingRuleBaseNameData.setModifiedDateAndStaff(getStaffData());

			if(Strings.isNullOrBlank(getGroupIds())){
				chargingRuleBaseNameData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				chargingRuleBaseNameData.setGroups(getGroupIds());
			}

			List<QosProfileDetailData> qosProfileDetailDataList = chargingRuleBaseNameDataFromDB.getQosProfileDetails();
			if(chargingRuleBaseNameData.getQosProfileDetails()!=null) {
				chargingRuleBaseNameData.getQosProfileDetails().clear();
				chargingRuleBaseNameData.getQosProfileDetails().addAll(qosProfileDetailDataList);
			}else{
				chargingRuleBaseNameData.setQosProfileDetails(qosProfileDetailDataList);
			}

			setServiceTypes();

			chargingRuleBaseNameData.setModifiedDateAndStaff(getStaffData());
			CRUDOperationUtil.merge(chargingRuleBaseNameData);

			JsonObject newJsonObject= chargingRuleBaseNameData.toJson();
			JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);

			String message = Discriminators.CHARGING_RULE_BASE_NAME + " <b><i>" + chargingRuleBaseNameData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(chargingRuleBaseNameData,	chargingRuleBaseNameData.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference ,	chargingRuleBaseNameData.getHierarchy(), message);

			request.getSession().setAttribute(Attributes.CHARGING_RULE_BASE_NAME_ID, chargingRuleBaseNameData.getId());

			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			request.setAttribute(Attributes.CHARGING_RULE_BASE_NAME_ID, chargingRuleBaseNameData.getId());

			setActionChainUrl(Results.VIEW.getValue());

			return Results.DISPATCH_VIEW.getValue();

 		}catch(Exception e) {
			return generateErrorLogsAndRedirect(e, "Failed to Update ChargingRuleBaseName.", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	@SkipValidation
	public String view(){
		
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called view()");
		}

		try {

			String chargingRuleBaseNameId = getChargingRuleBaseNameId();

			chargingRuleBaseNameData = CRUDOperationUtil.get(ChargingRuleBaseNameData.class, chargingRuleBaseNameId);

			if(chargingRuleBaseNameData == null){
				LogManager.getLogger().error(MODULE, "Error while fetching ChargingRuleBaseName for update operation. Reason: ChargingRuleBaseName Id not found");
				return redirectError(Discriminators.CHARGING_RULE_BASE_NAME, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue());
			}

			if(Strings.isNullOrBlank(chargingRuleBaseNameData.getGroups()) == false){
				chargingRuleBaseNameData.setGroupNames(GroupDAO.getGroupNames(SPLITTER.split(chargingRuleBaseNameData.getGroups())));
			}

			createChargingRuleAssociationList(chargingRuleBaseNameData);
			return Results.VIEW.getValue();

 		}catch(Exception e) {
			return generateErrorLogsAndRedirect(e, "Failed to Update ChargingRuleBaseName.", ActionMessageKeys.VIEW_FAILURE.key, Results.LIST.getValue());
		}

	}

	@SkipValidation
	public String search(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called search()");
		}
		return Results.LIST.getValue();
	}

	@SkipValidation
	public String delete() {
		if (getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called delete()");
		}
		
		request.setAttribute(Attributes.ACTION, ACTION_SEARCH);
		setActionChainUrl(ACTION_SEARCH);

		try{

			String [] chargingRuleBaseNameIds = request.getParameterValues(Attributes.IDS);

			if(chargingRuleBaseNameIds != null){
				for(String chargingRuleId : chargingRuleBaseNameIds){
					chargingRuleBaseNameData = CRUDOperationUtil.get(ChargingRuleBaseNameData.class,chargingRuleId);

					createChargingRuleAssociationList(chargingRuleBaseNameData);
					if (getChargingRuleDetailsjson().size() != 0) {
						addActionError(Discriminators.CHARGING_RULE_BASE_NAME + " " + getText(ActionMessageKeys.DELETE_FAILURE.key));
						addActionError("ChargingRuleBaseName is Configured with packages " + getAttachPkgList());
						return Results.LIST.getValue();
					}

					String message = Discriminators.CHARGING_RULE_BASE_NAME + " <b><i>" + chargingRuleBaseNameData.getName() + "</i></b> " + " Deleted";
					CRUDOperationUtil.audit(chargingRuleBaseNameData,chargingRuleBaseNameData.getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),chargingRuleBaseNameData.getHierarchy(), message);
					chargingRuleBaseNameData.setStatus(CommonConstants.STATUS_DELETED);
					chargingRuleBaseNameData.getChargingRuleDataServiceTypeDatas().clear();
					CRUDOperationUtil.update(chargingRuleBaseNameData);

				}
			}

 		}catch(Exception ex){
			return generateErrorLogsAndRedirect(ex, "Failed to delete ChargingRuleBaseName.", ActionMessageKeys.DELETE_FAILURE.key, Results.LIST.getValue());
		}

		MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
		addActionMessage(messageFormat.format(messageParameter));

		return Results.REDIRECT_ACTION.getValue();
	}

	private String getAttachPkgList() {

		String attachPackages = null;
		if((getChargingRuleDetailsjson().isJsonNull()==false
				&& getChargingRuleDetailsjson().size() !=0 )){
			attachPackages = Strings.join(",", getChargingRuleDetailsjson(), new Function<Object, String>() {
				@Override
				public String apply(Object input) {
					return ((JsonObject)input).get("Package").getAsString();
				}
			});
		}

		return attachPackages;
	}

	private JsonArray chargingRuleDetailsjson = null;
	private void createChargingRuleAssociationList(ChargingRuleBaseNameData chargingRuleBaseNameData){

		chargingRuleDetailsjson = new JsonArray();
		JsonObject jsonObject =null;
		for(QosProfileDetailData qosProfileDetailData:chargingRuleBaseNameData.getQosProfileDetails()){

			if((CommonConstants.STATUS_DELETED.equalsIgnoreCase(qosProfileDetailData.getQosProfile().getStatus()))==false ){

				jsonObject = new JsonObject();
				jsonObject.addProperty("Qos Profile",qosProfileDetailData.getQosProfile().getName());
				jsonObject.addProperty("qosProfileId",qosProfileDetailData.getQosProfile().getId());
				jsonObject.addProperty("Package",qosProfileDetailData.getQosProfile().getPkgData().getName());
				jsonObject.addProperty("pkgId", qosProfileDetailData.getQosProfile().getPkgData().getId());
				if(chargingRuleDetailsjson.contains(jsonObject)==false) {
					chargingRuleDetailsjson.add(jsonObject);
				}
			}
		}
	}

	public JsonArray getChargingRuleDetailsjson() {
		return chargingRuleDetailsjson;
	}

	private void setServiceTypes() {

		List<ChargingRuleDataServiceTypeData> chargingRuleDataServiceTypeDatas = new ArrayList<ChargingRuleDataServiceTypeData>();

		filterEmptyChargingRuleServiceTypeDatas(chargingRuleBaseNameData.getChargingRuleDataServiceTypeDatas());

		for(ChargingRuleDataServiceTypeData data : chargingRuleBaseNameData.getChargingRuleDataServiceTypeDatas()){
			DataServiceTypeData dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class,data.getDataServiceTypeData().getId());
			data.setDataServiceTypeData(dataServiceTypeData);
			data.setChargingRuleBaseName(chargingRuleBaseNameData);
			chargingRuleDataServiceTypeDatas.add(data);
		}

		chargingRuleBaseNameData.setChargingRuleDataServiceTypeDatas(chargingRuleDataServiceTypeDatas);
	}


	private void filterEmptyChargingRuleServiceTypeDatas(List<ChargingRuleDataServiceTypeData> chargingRuleDataServiceTypeDatas){
		Collectionz.filter(chargingRuleDataServiceTypeDatas,new Predicate<ChargingRuleDataServiceTypeData>() {
			@Override
			public boolean apply(ChargingRuleDataServiceTypeData chargingRuleDataServiceTypeData) {
				if(chargingRuleDataServiceTypeData == null){
					return false;
				}

				if(Strings.isNullOrBlank(chargingRuleDataServiceTypeData.getDataServiceTypeData().getId())){
					return false;
				}

				return true;
			}});
	}

	public List<DataServiceTypeData> getDataServiceTypeDataList() {
		return dataServiceTypeDataList;
	}

	public void setDataServiceTypeDataList(List<DataServiceTypeData> dataServiceTypeDataList) {
		this.dataServiceTypeDataList = dataServiceTypeDataList;
	}

	public ChargingRuleBaseNameData getChargingRuleBaseNameData() {
		return chargingRuleBaseNameData;
	}

	public void setChargingRuleBaseNameData(ChargingRuleBaseNameData chargingRuleBaseNameData) {
		this.chargingRuleBaseNameData = chargingRuleBaseNameData;
	}

	public List<DataServiceTypeData> getServiceTypes() {
		return CRUDOperationUtil.findAllWhichIsNotDeleted(DataServiceTypeData.class);
	}

	public List<GroupData> getStaffBelongingGroupList() {
		return staffBelongingGroupList;
	}

	public void setStaffBelongingGroupList(List<GroupData> staffBelongingGroupList) {
		this.staffBelongingGroupList = staffBelongingGroupList;
	}
	


	public List<String> getShowSelectedGroupsForUpdate() {
		return showSelectedGroupsForUpdate;
	}

	public void setShowSelectedGroupsForUpdate( List<String> showSelectedGroupsForUpdate ) {
		this.showSelectedGroupsForUpdate = showSelectedGroupsForUpdate;
	}


	public ChargingRuleBaseNameData getModel() {
		return chargingRuleBaseNameData;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

	@SkipValidation
	public String export() {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called export()");
		}


		String[] ids = request.getParameterValues("ids");
		String FILENAME = "exportChargingRuleBaseNames_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date()) + CommonConstants.XML;
		BufferedWriter writer = null;
		PrintWriter out = null;

		try {

			Map<String, String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
			boolean exportOperation = checkForImportExportOperation();

			if (exportOperation == true) {
				return redirectError(Discriminators.CHARGING_RULE_BASE_NAME, "importexport.operation.inprogress.error", Results.LIST.getValue(), false);
			}
            makeImportExportOperationInProgress(true);
			if (Arrayz.isNullOrEmpty(ids) == true) {
				makeImportExportOperationInProgress(false);
				return redirectError(Discriminators.CHARGING_RULE_BASE_NAME, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}

			List<ChargingRuleBaseNameData> chargingRuleBaseNames = CRUDOperationUtil.getAllByIds(ChargingRuleBaseNameData.class, ids);
			setGroupsInChargingRuleBaseNames(chargingRuleBaseNames, groupNamesBasedOnId);

			ChargingRuleBaseNameContainer chargingRuleBaseNameContainer = new ChargingRuleBaseNameContainer();
			chargingRuleBaseNameContainer.setChargingRuleBaseNameDatas(chargingRuleBaseNames);

			StringWriter stringWriter = new StringWriter();
			ConfigUtil.serialize(stringWriter, ChargingRuleBaseNameContainer.class, chargingRuleBaseNameContainer);

			String chargingRuleBaseNameInfo = stringWriter.toString();
			if (Strings.isNullOrBlank(chargingRuleBaseNameInfo)) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Can not find content for the ChargingRuleBaseNames");
				}
				throw new Exception("Can not find data as string for the ChargingRuleBaseName");
			}
			response.setContentType(CommonConstants.TEXT_XML_TYPE);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + FILENAME + "\"");

			out = response.getWriter();
			writer = new BufferedWriter(out);
			writer.write(chargingRuleBaseNameInfo, 0, chargingRuleBaseNameInfo.length());
			writer.flush();


			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "All the ChargingRuleBaseNames are exported successfully");
			}
			addActionMessage("ChargingRuleBaseNames are exported successfully");
			makeImportExportOperationInProgress(false);
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_SEARCH);
			return "EXPORT_COMPLETED";

		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while fetching ChargingRuleBaseNames data for export operation.", ActionMessageKeys.EXPORT_FAILURE.key, Results.LIST.getValue());
		} finally {
			Closeables.closeQuietly(writer);
			Closeables.closeQuietly(out);
		}
	}

	private void setGroupsInChargingRuleBaseNames(List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas, Map<String, String> groupNamesBasedOnId) {
		for (ChargingRuleBaseNameData chargingRuleBaseNameData : chargingRuleBaseNameDatas) {
			if (Strings.isNullOrBlank(chargingRuleBaseNameData.getGroups())) {
				chargingRuleBaseNameData.setGroups(CommonConstants.DEFAULT_GROUP);
			}
			importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, chargingRuleBaseNameData);
		}
	}

	@SkipValidation
	public String exportAll() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called exportAll()");
		}

		String FILENAME = "exportChargingRuleBaseNames_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date()) + CommonConstants.XML;

		BufferedWriter writer = null;
		PrintWriter out = null;

		try {

			Map<String, String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
			boolean exportOperation = checkForImportExportOperation();
			if (exportOperation == true) {
				return redirectError(Discriminators.CHARGING_RULE_BASE_NAME, "importexport.operation.inprogress.error", Results.LIST.getValue(), false);
			}

			String staffBelongingGroupList = (String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);

			makeImportExportOperationInProgress(true);

			ChargingRuleBaseNameData chargingRuleBaseNameData = new ChargingRuleBaseNameData();

			List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas;
			chargingRuleBaseNameDatas = ChargingRuleBaseNameDAO.searchByCriteria(chargingRuleBaseNameData, -1, -1, "name", "asc", staffBelongingGroupList);

			importExportUtil.filterBasedOnStaffBelongingGroupIds(chargingRuleBaseNameDatas, getStaffBelongingGroups());
			if (Collectionz.isNullOrEmpty(chargingRuleBaseNameDatas)) {
				makeImportExportOperationInProgress(false);
				LogManager.getLogger().warn(MODULE, getText("chargingrulebasename.importexport.nochargingrulebasename"));
				return redirectError(MODULE,"chargingrulebasename.importexport.nochargingrulebasename", Results.LIST.getValue(), false);
			}
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Export All operation started for ChargingRuleBaseNames");
			}

			List<ChargingRuleBaseNameData> chargingRuleBaseNameToBeExported = new ArrayList<ChargingRuleBaseNameData>();
			for (ChargingRuleBaseNameData chargingRuleBaseNameDataObj : chargingRuleBaseNameDatas) {
				List<String> groups = SPLITTER.split(chargingRuleBaseNameDataObj.getGroups());
				Reason reason = new Reason(chargingRuleBaseNameDataObj.getName());
				boolean isExportAllowedForGroup = importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(), reason, ACLModules.CHARGINGRULEBASENAME, ACLAction.EXPORT.name(), getStaffData().getUserName());
				if (isExportAllowedForGroup) {
					chargingRuleBaseNameToBeExported.add(chargingRuleBaseNameDataObj);
					importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, chargingRuleBaseNameDataObj);
				}
			}

			if (Collectionz.isNullOrEmpty(chargingRuleBaseNameToBeExported)) {
				makeImportExportOperationInProgress(false);
				LogManager.getLogger().warn(MODULE, getText("chargingrulebasename.importexport.nochargingrulebasename"));
				return redirectError(MODULE,"chargingrulebasename.importexport.nochargingrulebasename", Results.LIST.getValue(), false);
			}

			ChargingRuleBaseNameContainer chargingRuleBaseNameContainer = new ChargingRuleBaseNameContainer();
			chargingRuleBaseNameContainer.setChargingRuleBaseNameDatas(chargingRuleBaseNameToBeExported);
			LogManager.getLogger().debug(MODULE, "Started exporting ChargingRuleBaseNames");
			StringWriter stringWriter = new StringWriter();
			ConfigUtil.serialize(stringWriter, ChargingRuleBaseNameContainer.class, chargingRuleBaseNameContainer);

			String chargingRuleBaseNameInfo = stringWriter.toString();

			if (Strings.isNullOrBlank(chargingRuleBaseNameInfo)) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Can't find content for the ChargingRuleBaseNames.");
				}
				throw new Exception("Can not find data as string for the ChargingRuleBaseNames");
			}
			response.setContentType("text/xml");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + FILENAME + "\"");
			out = response.getWriter();
			writer = new BufferedWriter(out);
			writer.write(chargingRuleBaseNameInfo, 0, chargingRuleBaseNameInfo.length());
			writer.flush();

			makeImportExportOperationInProgress(false);
			setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_SEARCH);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "End of operation for exporting ChargingRuleBaseNames");
			}
			return "EXPORT_COMPLETED";

		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while fetching ChargingRuleBaseNames data for export operation.", ActionMessageKeys.EXPORT_FAILURE.key, Results.LIST.getValue());
		} finally {
			Closeables.closeQuietly(writer);
			Closeables.closeQuietly(out);
		}
	}

	@SkipValidation
	public String importData() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called importData()");
		}

		try {
			String selectedChargingRuleNBaseNameIndexes = request.getParameter(Attributes.SELECTED_INDEXES);
			String action = request.getParameter(Attributes.USER_ACTION);

			if (isImportExportOperationInProgress() == true)
			{
				return redirectError(Discriminators.CHARGING_RULE_BASE_NAME, "chargingrulebasename.importexport.operation", Results.LIST.getValue());
			}

			List<ChargingRuleBaseNameData> chargingRuleBaseNameDataList = (List<ChargingRuleBaseNameData>) request.getSession().getAttribute(Attributes.CHARGING_RULE_BASE_NAME);
			List<ChargingRuleBaseNameData> selectedChargingRuleBaseNames = Collectionz.newArrayList();

			if(Strings.isNullOrBlank(selectedChargingRuleNBaseNameIndexes) == false && Collectionz.isNullOrEmpty(chargingRuleBaseNameDataList) == false) {
				makeImportExportOperationInProgress(true);
				selectedChargingRuleBaseNames = new ImportEntityAccumulator<ChargingRuleBaseNameData>(chargingRuleBaseNameDataList, selectedChargingRuleNBaseNameIndexes).get();
			}

			List<Reason> reasons = importChargingRuleBaseNames(selectedChargingRuleBaseNames,action);
			Gson gson = GsonFactory.defaultInstance();
			JsonArray importChargingRuleBaseNamesResultJson = gson.toJsonTree(reasons, new TypeToken<List<Reason>>() {}.getType()).getAsJsonArray();

			request.setAttribute("importStatus", importChargingRuleBaseNamesResultJson);
			makeImportExportOperationInProgress(false);

		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while importing ChargingRuleBaseName Data.", ActionMessageKeys.IMPORT_FAILURE.key, Results.IMPORT_STATUS_REPORT.getValue());
		}

		return Results.IMPORT_STATUS_REPORT.getValue();
	}


	private List<Reason> importChargingRuleBaseNames(List<ChargingRuleBaseNameData> chargingRulesForImport,String action) {

		List<Reason> reasons = Collectionz.newArrayList();
		for (ChargingRuleBaseNameData importChargingRuleBaseName : chargingRulesForImport) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Import Operation started for ChargingRuleBaseName: " + importChargingRuleBaseName.getName());
			}
			Reason reason = new Reason(importChargingRuleBaseName.getName());
			try {

				importChargingRuleBaseName.setCreatedByStaff(getStaffData());
				importChargingRuleBaseName.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				importChargingRuleBaseName.setModifiedByStaff(importChargingRuleBaseName.getCreatedByStaff());
				importChargingRuleBaseName.setModifiedDate(importChargingRuleBaseName.getCreatedDate());

				boolean isExistGroup = true;
				List<String> groups = new ArrayList<String>();
				if (Strings.isNullOrBlank(importChargingRuleBaseName.getGroupNames())) {
					importChargingRuleBaseName.setGroups(CommonConstants.DEFAULT_GROUP_ID);
					groups.add(CommonConstants.DEFAULT_GROUP_ID);
				} else {
					isExistGroup = importExportUtil.getGroupIdsFromName(importChargingRuleBaseName, reason, groups);
				}
				if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
					List<String> staffBelongingGroups = CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
					importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(), reason, ACLModules.CHARGINGRULEBASENAME, ACLAction.IMPORT.name(), getStaffData().getUserName(), staffBelongingGroups, importChargingRuleBaseName, importChargingRuleBaseName.getName());


					importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(), reason, ACLModules.CHARGINGRULEBASENAME, ACLAction.IMPORT.name(), getStaffData().getUserName());


					if (CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
						importChargingRuleBaseName.setGroups(Strings.join(",", groups));

						importExportUtil.validateAndImportInformation(importChargingRuleBaseName, action, reason);
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Import Operation finished for ChargingRuleBaseName: " + importChargingRuleBaseName.getName());
						}
					}
				}
				reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
				reasons.add(reason);

			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Error while importing ChargingRuleBaseNames : " + importChargingRuleBaseName.getName() + " Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				String errorMessage = "Failed to import ChargingRuleBaseName due to internal error. Kindly refer logs for further details";
				List<String> errors = new ArrayList<String>(2);
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

	private String getRemarksFromSubReasons(List<String> subReasons) {
		StringBuilder sb = new StringBuilder();
		if (Collectionz.isNullOrEmpty(subReasons)) {
			sb.append(" ---- ");
		} else {
			for (String str : subReasons) {
				sb.append(str);
				sb.append("<br/>");
			}
		}
		return sb.toString();
	}

	@SkipValidation
	public String importChargingRuleBaseName() throws ServletException {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called importChargingRuleBaseName()");
		}

		if (isImportExportOperationInProgress() == true) {
			return redirectError(Discriminators.CHARGING_RULE_BASE_NAME, "chargingrulebasename.importexport.operation", Results.LIST.getValue(), false);
		}
		if (getImportedFile() == null) {
			LogManager.getLogger().error(MODULE, "Error while importing ChargingRuleBaseNames. Reason: File not found");
			return redirectError(Discriminators.CHARGING_RULE_BASE_NAME, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
		}
		try {
			if (CommonConstants.TEXT_XML_TYPE.equals(getImportedFileContentType()) == false) {
				LogManager.getLogger().error(MODULE, "Error while importing ChargingRuleBaseName. Reason: Invalid File type is configured. Only XML_FILE_EXT File is supported for importing ChargingRuleBaseName");
				makeImportExportOperationInProgress(false);
				return redirectError(Discriminators.CHARGING_RULE_BASE_NAME, "chargingrulebasename.importexport.invalidfiletype", Results.LIST.getValue(), false);
			}
			ChargingRuleBaseNameContainer chargingRuleBaseNameContainer = ConfigUtil.deserialize(getImportedFile(), ChargingRuleBaseNameContainer.class);
			if (chargingRuleBaseNameContainer == null) {
				return Results.REDIRECT_ERROR.getValue();
			}

			List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas = chargingRuleBaseNameContainer.getChargingRuleBaseNameDatas();
			skipCRBNHaveNullName(chargingRuleBaseNameDatas);

			request.getSession().setAttribute("chargingRuleBaseNames", chargingRuleBaseNameDatas);

			Gson gson = GsonFactory.defaultInstance();
			JsonArray importChargingRuleBaseNameJson = gson.toJsonTree(chargingRuleBaseNameDatas, new TypeToken<List<ChargingRuleBaseNameData>>() {
			}.getType()).getAsJsonArray();

			request.setAttribute("importedChargingRuleBaseNames", importChargingRuleBaseNameJson);
			return Results.IMPORT_CHARGING_RULE_BASE_NAME.getValue();

		} catch (JAXBException e) {
			LogManager.getLogger().error(MODULE, "Error while importing ChargingRuleBaseName due to XML_FILE_EXT processing failure. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			addActionError(Discriminators.CHARGING_RULE_BASE_NAME + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
			addActionError(getText(ActionMessageKeys.IMPORT_FAILURE.key));
			makeImportExportOperationInProgress(false);
			return Results.LIST.getValue();

		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while importing ChargingRuleBaseName Data.", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
		}
	}

	private void skipCRBNHaveNullName(List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas) {
        /*
        This method is used to remove the ChargingRuleBaseNameData have null name from the list of chargingRuleBaseNameDatas.
        It will also set proper message in request attribute(invalidEntityMessage) to display in GUI.
        */

		Predicate predicate = new Predicate<ChargingRuleBaseNameData>() {
			@Override
			public boolean apply(ChargingRuleBaseNameData input) {
				if(Strings.isNullOrBlank(input.getName())){
					LogManager.getLogger().info(MODULE, "Found ChargingRuleBaseNameData with null name. Skipping Import process for ChargingRuleBaseNameData: " + input);
					return false;
				}
				return true;
			};
		};

		String message = ImportExportUtil.removeInvalidEntitiesAndGetMessage(chargingRuleBaseNameDatas, predicate, Discriminators.CHARGING_RULE_BASE_NAME);
		request.setAttribute(INVALID_ENTITY_MESSAGE,message);
	}

	private String generateErrorLogsAndRedirect(Exception e, String message, String key, String result) {
		return super.generateErrorLogsAndRedirect(Discriminators.RATING_GROUP, e, message, key, result);
	}

}
