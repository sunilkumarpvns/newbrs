package com.elitecore.nvsmx.policydesigner.controller.emergency;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.Closeables;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pkg.EmergencyPkgContainer;
import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.commons.collection.Lists;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.ratinggroup.RatingGroupUtil;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportEntityAccumulator;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportCTRL;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.policydesigner.model.pkg.PkgDAO;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.hibernate.criterion.Order;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.policydesigner.controller.util.GlobalPlanPredicates.createNonContainGroupPredicate;

/**
 * @author Kirpalsinh.raj
 *
 */
public class EmergencyPkgCTRL extends ImportExportCTRL<PkgData> {

	private static final long serialVersionUID = 1L;
	private static final String MODULE  = EmergencyPkgCTRL.class.getSimpleName();

	private PkgData pkgData = new PkgData();
	private List<PkgData> pkgsList = new LinkedList<PkgData>();
	private String actionChainUrl;
	private List<DataServiceTypeData> dataServiceTypeDatas = Collectionz.newArrayList();
	private List<String> groupList = Collectionz.newArrayList();
	Object [] messageParameter = {Discriminators.PACKAGE};
	private List<String> qosProfileDataNames = Collectionz.newArrayList();
	private static final String MANAGE_ORDER = "manageOrder";
	private int totalEmergencyPackages;
	public Map<String,List<PkgGroupOrderData>> groupWiseOrderMap = Maps.newLinkedHashMap();

	public void setGroupWiseOrderMap(Map<String, List<PkgGroupOrderData>> groupWiseOrderMap) {
		this.groupWiseOrderMap = groupWiseOrderMap;
	}

	public Map<String, List<PkgGroupOrderData>> getGroupWiseOrderMap() {
		return groupWiseOrderMap;
	}

	public Timestamp getCurrentTime(){
		return new Timestamp(System.currentTimeMillis());
	}
	
	public PkgData getPkgData() {
		return pkgData;
	}

	public void setPkgData(PkgData pkgData) {
		this.pkgData = pkgData;
	}

	@SkipValidation
	public String detail(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE,"Method called detail()");
		}
	    try {
			
			String tableId = request.getParameter("tableId");
			String row = request.getParameter("rowData"+tableId);
			
			Gson gson = GsonFactory.defaultInstance();		
			pkgData = gson.fromJson(row, PkgData.class);
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Package Data:"+pkgData);
			}
	    } catch (JsonSyntaxException e) {
	    	getLogger().error(MODULE, "Error while making object from JSON data");
	    	getLogger().trace(MODULE,e);
	    	pkgData = new PkgData();		
	    }
	    return Results.DETAIL.getValue();
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

	@SkipValidation
	public String init() {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called init()");
	}
		String pkgId = getPkgId();

		//initCreate when no pkg id found
		if(Strings.isNullOrBlank(pkgId)){
			pkgData.setDescription(NVSMXUtil.getDefaultDescription(request));
			return Results.CREATE.getValue();
			}

		//initUpdate when pkg id found
		try {
				pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);

			if (pkgData == null) {
				getLogger().error(MODULE, "Error while fetching Emergency Package data for update operation. Reason: No package found with given id: " + pkgId);
				return redirectError(Discriminators.EMERGENCY_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}
			return Results.UPDATE.getValue();
				
 		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while fetching Package data for update operation.", ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	@SkipValidation
	public String search(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE,"Method called search()");
		}
		int values = PkgDAO.getNoOfPackagesOfType(PkgType.EMERGENCY);
		setGroupIds((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
		request.getSession().setAttribute("totalEmergencyPackages",values);
		return Results.LIST.getValue();
	}

	@SkipValidation
	public String searchCriteria(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE,"Method called searchCriteria()");
		}
		setActionChainUrl(NVSMXCommonConstants.ACTION_EMERGENCY_PKG_SEARCH);
		try {
			String json = GsonFactory.defaultInstance().toJson(getPkgData());
			request.setAttribute(Attributes.CRITERIA, json);
			return Results.LIST.getValue();
		}catch(Exception ex){
			return generateErrorLogsAndRedirect(ex, "Failed to search PkgData.", ActionMessageKeys.SEARCH_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}
	
	@InputConfig(resultName = InputConfigConstants.CREATE)
	public String create(){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE,"Method called create()");
		}

		try{
			
			pkgData.setCreatedDateAndStaff(getStaffData());
			
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

			pkgData.setType(PkgType.EMERGENCY.name());
			if (isGroupWiseLimitReach(pkgData,CRUDOperationUtil.MODE_CREATE,ActionMessageKeys.CREATE_FAILURE.key) == true) {
				return Results.CREATE.getValue();
			}

			List<String> pkgGroupList = CommonConstants.COMMA_SPLITTER.split(pkgData.getGroups());

			setPkgGroupOrder(pkgGroupList);

			pkgData.setOrderNumber(PkgDAO.getMaxOrderNumber(PkgType.EMERGENCY)+1);
			
			CRUDOperationUtil.save(pkgData);
			String message = Discriminators.PACKAGE + " <b><i>" + pkgData.getName() + "</i></b> " + "Created";  
			CRUDOperationUtil.audit(pkgData,pkgData.getName(),AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), pkgData.getHierarchy(), message);
			request.setAttribute(Attributes.PKG_ID, pkgData.getId());
			
			MessageFormat messageFormat = new MessageFormat(getText("create.success"));
			addActionMessage(messageFormat.format(messageParameter));
			setActionChainUrl(Results.VIEW.getValue());
			return Results.DISPATCH_VIEW.getValue();
			
 		}catch(Exception ex){
			return generateErrorLogsAndRedirect(ex, "Failed to Create Emergency Package.", ActionMessageKeys.CREATE_FAILURE.key, Results.LIST.getValue());
		}
	}

	private void setPkgGroupOrder(List<String> pkgGroupList) {
		for (String groupId : pkgGroupList) {
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
	public String view(){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE,"Method called view()");
		}
		String pkgId = getPkgId();

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE,"View Emergency Package of PkgId: "+pkgId);
		}
		
		try {
			if(Strings.isNullOrEmpty(pkgId)==false){
				pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);
				String pkgGroupNames = GroupDAO.getGroupNames(CommonConstants.COMMA_SPLITTER.split(pkgData.getGroups()));
				pkgData.setGroupNames(pkgGroupNames);
				return Results.VIEW.getValue();
				
			} else {
				return redirectError(Discriminators.EMERGENCY_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}
 		}catch(Exception e) {			
			return generateErrorLogsAndRedirect(e, "Error while fetching Emergency Package data for view operation.", ActionMessageKeys.VIEW_FAILURE.key, Results.LIST.getValue());
		}
	}

	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called update()");
		}

		String pkgId = pkgData.getId();
		setActionChainUrl(Results.VIEW.getValue());
		try {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Updating package with id: "+pkgId);
			}
			
			pkgData.setModifiedDateAndStaff(getStaffData());
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

			if (isGroupWiseLimitReach(pkgData,CRUDOperationUtil.MODE_UPDATE,ActionMessageKeys.UPDATE_FAILURE.key) == true) {
				return Results.UPDATE.getValue();
			}

		    //current group list
			final List<String> pkgGroupList = CommonConstants.COMMA_SPLITTER.split(pkgData.getGroups());

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

			if(Collectionz.isNullOrEmpty(pkgGroupList) == false ){
				setPkgGroupOrder(pkgGroupList);
			}
			pkgData.setModifiedDateAndStaff(getStaffData());
			CRUDOperationUtil.merge(pkgData);
			String message = Discriminators.PACKAGE + " <b><i>" + pkgData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(pkgData, pkgData.getName(), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), pkgData.getHierarchy(), message);
			request.setAttribute(Attributes.PKG_ID, pkgData.getId());

			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.DISPATCH_VIEW.getValue();
 		} catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_EMERGENCY_PKG_VIEW);
			return generateErrorLogsAndRedirect(e, "Error while updating Emergency package of PkgId: '"+pkgId+"'.", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}		
	}

	@SkipValidation
	public String updateStatus(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called updateStatus()");
		}

		setActionChainUrl(Results.VIEW.getValue());
		try {
			
			String pkgId = request.getParameter("pkgData.id");
			String pkgStatusVal = request.getParameter("pkgData.status");
			 
			pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "PkgId: "+pkgData.getId());
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
			setActionChainUrl(NVSMXCommonConstants.ACTION_EMERGENCY_PKG_VIEW);
			return generateErrorLogsAndRedirect(e, "Error while updating Emergency package of PkgId: '"+pkgData.getId()+"'.", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	@SkipValidation
	public String updateMode(){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called updateMode()");
		}

		PrintWriter out = null;
		try {
			out = response.getWriter();
			String pkgId = request.getParameter(Attributes.PKG_ID);
			String pkgModeVal = request.getParameter(Attributes.PKG_MODE);

			pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "PkgId: "+pkgData.getId());
			}
			
			PkgMode pkgMode = PkgMode.getMode(pkgModeVal);
			PkgMode pkgNextMode = pkgMode.getNextMode();
			if(pkgNextMode!=null){
				pkgData.setPackageMode(pkgNextMode.val);
			}

			List<PolicyDetail> policyDetails = DefaultNVSMXContext.getContext().getPolicyRepository().getPolicyDetail(pkgData.getName());
			JsonObject object = new JsonObject();
			if(pkgNextMode == PkgMode.LIVE) {
				if (Collectionz.isNullOrEmpty(policyDetails)) {
					getLogger().error(MODULE, "Unable to change package mode. Reason: Policy is not reloaded");
					object.addProperty("responseCode", String.valueOf(ResultCode.PRECONDITION_FAILED.code));
					object.addProperty("responseMessage", "You are recommended to reload policies before updating mode to "+ pkgNextMode);
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
			CRUDOperationUtil.update(pkgData);
			String message = Discriminators.PACKAGE + " <b><i>" + pkgData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(pkgData,pkgData.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),pkgData.getHierarchy(), message);

			object.addProperty("responseCode", String.valueOf(ResultCode.SUCCESS.code));
			out.print(object.toString());
			out.flush();
			return Results.REDIRECT_ACTION.getValue();
 		} catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_EMERGENCY_PKG_VIEW);
			return generateErrorLogsAndRedirect(e, "Error while updating Emergency package of PkgId: '"+pkgData.getId()+"'.", ActionMessageKeys.UPDATE_FAILURE.key, null);
		} finally {
			out.close();
		}
	}

	@SkipValidation
	public String delete(){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called delete()");
		}
		String[] pkgIds = request.getParameterValues("ids");
		try {
			if( pkgIds != null){

				for(String pkgId: pkgIds){

					pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);
					String message = Discriminators.PACKAGE + " <b><i>" + pkgData.getName() + "</i></b> " + " Deleted";
					CRUDOperationUtil.audit(pkgData,pkgData.getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),pkgData.getHierarchy(), message);
					pkgData.setDeletedStatus();
					CRUDOperationUtil.update(pkgData);
				}
				
				MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
				addActionMessage(messageFormat.format(messageParameter));
				setActionChainUrl(NVSMXCommonConstants.ACTION_EMERGENCY_PKG_SEARCH);
				return Results.REDIRECT_ACTION.getValue();
			} else {
				return redirectError(Discriminators.EMERGENCY_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}
 		}catch (Exception e) {			
			return generateErrorLogsAndRedirect(e, "Error while fetching Emergency Package data for delete operation.", ActionMessageKeys.DELETE_FAILURE.key, Results.LIST.getValue());
		}						
	}

	@SkipValidation
	public String manageOrder(){
		
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called manageOrder()");
		}

		String staffBelongingGroups = (String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
		groupWiseOrderMap = PkgDAO.fetchGroupOrderWisePkgMap(PkgType.EMERGENCY, staffBelongingGroups);

		return MANAGE_ORDER;
	}

	@SkipValidation
	public String manageOrderEmergencyPackages(){
		if( getLogger().isDebugLogLevel() ){
			getLogger().debug(MODULE, "Method called manageOrderEmergencyPackages()");
		}
		
		setActionChainUrl("/policydesigner/emergency/EmergencyPkg/search");
		
		String[] pkgGroupDataIdArray = request.getParameterValues("pkgGroupDataIdArray");

		try{

			if(Arrayz.isNullOrEmpty(pkgGroupDataIdArray) == true){
				getLogger().error(MODULE, "Failed to update Packages Order. Reason: Received NULL data for Packages Ids and Orders");
				return redirectError(Discriminators.EMERGENCY_PACKAGE, "pkg.promotional.manage.order.failure", Results.REDIRECT_ACTION.getValue(), false);
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

				if( getLogger().isDebugLogLevel() ){
					getLogger().debug(MODULE, "Package '" + pkgOldData + "' order changed to " + pkgGroupData.getOrderNumber());
				}

			}
			MessageFormat messageFormat = new MessageFormat(getText("emergency.pkg.manage.order.success"));
			addActionMessage(messageFormat.format(messageParameter));
 		} catch(Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while Updating Emergency Packages Order.", "emergency.pkg.manage.order.failure", Results.REDIRECT_ACTION.getValue());
		}  
		return Results.REDIRECT_ACTION.getValue();
	}

	@SkipValidation
	public String export(){
		if(getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called export()");
		}
		String[] emergencyPkgIds = request.getParameterValues("ids");
		String FILENAME = "exportEmergencyPkg_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date())+ XML;
		BufferedWriter writer = null;
		PrintWriter out = null;
		try {
			Map<String,String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
			if(isImportExportOperationInProgress() == true) {
				return redirectError(Discriminators.EMERGENCY_PACKAGE, "emergency.pkg.importexport.operation", Results.LIST.getValue(), false);
			}
			makeImportExportOperationInProgress(true);
			if( emergencyPkgIds == null){
				makeImportExportOperationInProgress(false);
				return redirectError(Discriminators.EMERGENCY_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
			EmergencyPkgContainer emergencyPkgContainer = new EmergencyPkgContainer();
			List<EmergencyPkgDataExt> emergencyPkgData = CRUDOperationUtil.getAllByIds(EmergencyPkgDataExt.class, emergencyPkgIds);
			setChargingKeyNameInPccRule(emergencyPkgData);

			for(EmergencyPkgDataExt emergencyPkg : emergencyPkgData){

				if(Strings.isNullOrBlank(emergencyPkg.getGroups())){
					emergencyPkg.setGroups(CommonConstants.DEFAULT_GROUP);
				}
				importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, emergencyPkg);
			}

			emergencyPkgContainer.setEmergencyPkgData(emergencyPkgData);

			StringWriter stringWriter = new StringWriter();
			ConfigUtil.serialize(stringWriter, EmergencyPkgContainer.class, emergencyPkgContainer);

			String emergencyPkgInfo = stringWriter.toString();
			if(Strings.isNullOrBlank(emergencyPkgInfo)){
				if(getLogger().isWarnLogLevel()){
					getLogger().warn(MODULE, "Can not find content for emergency package.");
				}
				throw new Exception("Can not find data as string for the Emergency Package");
			}
			response.setContentType(TEXT_XML);
			response.setHeader("Content-Disposition", "attachment; filename=\""+FILENAME+"\"");

			out = response.getWriter();
			writer = new BufferedWriter(out);
			writer.write(emergencyPkgInfo, 0, emergencyPkgInfo.length());
			writer.flush();


			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "All the Emergency Packages are exported successfully");
			}
			addActionMessage("Emergency Packages are exported successfully");
			makeImportExportOperationInProgress(false);
			request.getSession().setAttribute(Attributes.LAST_URI, NVSMXCommonConstants.ACTION_EMERGENCY_PKG_SEARCH);
			return "EXPORT_COMPLETED";
		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while fetching Emergency Package data for export operation", ActionMessageKeys.EXPORT_FAILURE.key,Results.LIST.getValue());
		}finally {
			Closeables.closeQuietly(writer);
			Closeables.closeQuietly(out);
		}
	}

	private String generateErrorLogsAndRedirect(Exception e, String message, String key, String result) {
		return super.generateErrorLogsAndRedirect(Discriminators.EMERGENCY_PACKAGE, e, message, key, result);
	}


	@SkipValidation
	public String exportAll(){
		if(getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called exportAll()");
		}
		String FILENAME = "exportEmergencyPkg_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date())+ XML;
		BufferedWriter writer = null;
		PrintWriter out = null;
		try {
			Map<String,String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
			if(isImportExportOperationInProgress()){
				return redirectError(Discriminators.EMERGENCY_PACKAGE, "emergency.pkg.importexport.operation", Results.LIST.getValue(), false);
			}
			makeImportExportOperationInProgress(true);
			EmergencyPkgContainer emergencyPkgContainer = new EmergencyPkgContainer();
			List<EmergencyPkgDataExt> emergencyPkgDatas = CRUDOperationUtil.get(EmergencyPkgDataExt.class, Order.asc("name"));
			setChargingKeyNameInPccRule(emergencyPkgDatas);

			Collectionz.filter(emergencyPkgDatas, new Predicate<EmergencyPkgDataExt>() {
				@Override
				public boolean apply(EmergencyPkgDataExt emergencyPkgData) {
					return PkgType.EMERGENCY.name().equalsIgnoreCase(emergencyPkgData.getType());
				}
			});

			importExportUtil.filterBasedOnStaffBelongingGroupIds(emergencyPkgDatas, getStaffBelongingGroups());
			if (Collectionz.isNullOrEmpty(emergencyPkgDatas)) {
				makeImportExportOperationInProgress(false);
				return redirectError(MODULE, "emergency.pkg.importexport.nopackagefound", Results.LIST.getValue(), false);
			}
			if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Export All operation started for emergency packages");
			}

			List<EmergencyPkgDataExt> emergencyPkgDataToBeExported = new ArrayList<EmergencyPkgDataExt>();
			for(EmergencyPkgDataExt emergencyPkgData : emergencyPkgDatas){
				List<String> groups = CommonConstants.COMMA_SPLITTER.split(emergencyPkgData.getGroups());
				Reason reason = new Reason(emergencyPkgData.getName());
				boolean isExportAllowedForGroup = importExportUtil.filterStaffBelongingGroupsAndRoles(groups,getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(),reason, ACLModules.EMERGENCYPKG, ACLAction.EXPORT.name(),getStaffData().getUserName());
				if(isExportAllowedForGroup){
					emergencyPkgDataToBeExported.add(emergencyPkgData);
					importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, emergencyPkgData);
				}
			}
			if (Collectionz.isNullOrEmpty(emergencyPkgDataToBeExported)) {
				makeImportExportOperationInProgress(false);
				getLogger().warn(MODULE, getText("emergency.pkg.importexport.nopackagefound")+", Staff doesn't have export rights for any package.");
				return redirectError(MODULE, "emergency.pkg.importexport.nopackagefound", Results.LIST.getValue(), false);
			}

			emergencyPkgContainer.setEmergencyPkgData(emergencyPkgDataToBeExported);

			StringWriter stringWriter = new StringWriter();
			ConfigUtil.serialize(stringWriter, EmergencyPkgContainer.class, emergencyPkgContainer);
			String emergencyPkgInfo = stringWriter.toString();
			if(Strings.isNullOrBlank(emergencyPkgInfo)){
				if(getLogger().isWarnLogLevel()){
					getLogger().warn(MODULE, "Can not find content for emergency package.");
				}
				throw new Exception("Can not find data as string for emergency package");
			}
			response.setContentType(TEXT_XML);
			response.setHeader("Content-Disposition", "attachment; filename=\""+FILENAME+"\"");
			out = response.getWriter();
			writer = new BufferedWriter(out);
			writer.write(emergencyPkgInfo, 0, emergencyPkgInfo.length());
			writer.flush();

			makeImportExportOperationInProgress(false);
			request.getSession().setAttribute(Attributes.LAST_URI, NVSMXCommonConstants.ACTION_EMERGENCY_PKG_SEARCH);
			getLogger().debug(MODULE,"Export All operation ended for emergency packages");
			return "EXPORT_COMPLETED";

		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e,"Error while fetching emergency Package data for export operation.",ActionMessageKeys.EXPORT_FAILURE.key,Results.LIST.getValue());
		}finally {
			Closeables.closeQuietly(writer);
			Closeables.closeQuietly(out);
		}
	}

	@SkipValidation
	public String importEmergencyPkg() throws ServletException {
		if(getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called import()");
		}
		if(isImportExportOperationInProgress()){
			return redirectError(Discriminators.EMERGENCY_PACKAGE, "emergency.pkg.importexport.operation", Results.LIST.getValue(), false);
		}
		if(getImportedFile() == null){
			getLogger().error(MODULE,"Error while importing Emergency Package data. Reason: File not found");
			return redirectError(Discriminators.EMERGENCY_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
		}
		try {
			if(TEXT_XML.equals(getImportedFileContentType()) == false ){
				getLogger().error(MODULE,"Error while importing emergency package data. Reason: Invalid File type is configured. Only XML File is supported for importing emergency package");
				addActionError(Discriminators.PACKAGE + " " +getText(ActionMessageKeys.IMPORT_FAILURE.key));
				addActionError("Invalid File type for import");
				makeImportExportOperationInProgress(false);
				return Results.LIST.getValue();
			}
			EmergencyPkgContainer emergencyPkgContainer =  ConfigUtil.deserialize(getImportedFile(), EmergencyPkgContainer.class);
			if(emergencyPkgContainer == null) {
				return Results.REDIRECT_ERROR.getValue();
			}
			List<EmergencyPkgDataExt> emergencyPkgDatas = emergencyPkgContainer.getEmergencyPkgData();
			skipEmergencyPackageHaveNullName(emergencyPkgDatas);

			request.getSession().setAttribute(Attributes.EMERGENCY_PKGS, emergencyPkgDatas);

			Gson gson = GsonFactory.defaultInstance();
			JsonArray importEmergencyPkgJson = gson.toJsonTree(emergencyPkgDatas,new TypeToken<List<EmergencyPkgDataExt>>() {}.getType()).getAsJsonArray();
			request.setAttribute("importedEmergencyPkgs", importEmergencyPkgJson);
			return Results.IMPORT_EMERGENCY_PKG.getValue();

		} catch (IOException e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e,"Error while importing Emergency Package data. Reason: File not found",ActionMessageKeys.DATA_NOT_FOUND.key,Results.LIST.getValue());
		}  catch (JAXBException e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e,"Error while importing Emergency Package data due to XML processing failure.",ActionMessageKeys.IMPORT_FAILURE.key,Results.LIST.getValue());
		}catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e,"Error while importing Emergency Package data.",ActionMessageKeys.IMPORT_FAILURE.key,Results.LIST.getValue());
		}
	}

	private void setChargingKeyNameInPccRule(List<EmergencyPkgDataExt> emergencyPkgDatas) {
		if(Collectionz.isNullOrEmpty(emergencyPkgDatas)==false) {

            for (EmergencyPkgDataExt emergencyPkgDataExt : emergencyPkgDatas) {

                List<QosProfileData>  qosProfileDatas = emergencyPkgDataExt.getQosProfiles();
                if ( Collectionz.isNullOrEmpty(qosProfileDatas)==false ) {

                    for ( QosProfileData qosProfileData: qosProfileDatas ) {

                        List<QosProfileDetailData> qosProfileDetailDatas = qosProfileData.getQosProfileDetailDataList();
                        if(Collectionz.isNullOrEmpty(qosProfileDetailDatas)==false){

                            for(QosProfileDetailData qosProfileDetailData : qosProfileDetailDatas){

                                List<PCCRuleData> pccRuleDatas = qosProfileDetailData.getPccRules();
                                if( Collectionz.isNullOrEmpty(pccRuleDatas)==false ){

									RatingGroupUtil.setChargingKeyName(pccRuleDatas);
                                }
                            }
                        }
                    }
                }
            }
        }
	}

	private void skipEmergencyPackageHaveNullName(List<EmergencyPkgDataExt> pkgDatas) {
        /*
        This method is used to remove the EmergencyPkgData have null name from the list of pkgDatas.
        It will also set proper message in request attribute(invalidEntityMessage) to display in GUI.
        */

		Predicate predicate = new Predicate<EmergencyPkgDataExt>() {
			@Override
			public boolean apply(EmergencyPkgDataExt input) {
				if(Strings.isNullOrBlank(input.getName())){
					getLogger().info(MODULE, "Found EmergencyPkgData with null name. Skipping Import process for EmergencyPkgData: " + input);
					return false;
				}
				return true;
			};
		};

		String message = ImportExportUtil.removeInvalidEntitiesAndGetMessage(pkgDatas, predicate, Discriminators.EMERGENCY_PACKAGE);
		request.setAttribute(INVALID_ENTITY_MESSAGE,message);
	}

	@SkipValidation
	public String importData() {
		getLogger().debug(MODULE, "Method called importData()");
		try {
			String selectedEmergencyPkgIndexes = request.getParameter(Attributes.SELECTED_INDEXES);
			String action = request.getParameter(Attributes.USER_ACTION);

			if(isImportExportOperationInProgress()){
				return redirectError(Discriminators.EMERGENCY_PACKAGE, "emergency.pkg.importexport.operation", Results.LIST.getValue());
			}

			List<EmergencyPkgDataExt> emergencyPkgDatas = (List<EmergencyPkgDataExt>) request.getSession().getAttribute(Attributes.EMERGENCY_PKGS);

			List<EmergencyPkgDataExt> emergencyPkgDataExts = Collectionz.newArrayList();

			if(Strings.isNullOrBlank(selectedEmergencyPkgIndexes) == false && Collectionz.isNullOrEmpty(emergencyPkgDatas) == false) {
				makeImportExportOperationInProgress(true);
				emergencyPkgDataExts = new ImportEntityAccumulator<EmergencyPkgDataExt>(emergencyPkgDatas, selectedEmergencyPkgIndexes).get();
								}

			List<Reason> reasons = importEmergencyPackages(emergencyPkgDataExts, action);
			Gson gson = GsonFactory.defaultInstance();
			JsonArray importServiceTypeResultJson = gson.toJsonTree(reasons, new TypeToken<List<Reason>>(){}.getType()).getAsJsonArray();
			request.setAttribute("importStatus", importServiceTypeResultJson);
			makeImportExportOperationInProgress(false);

		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e,"Error while importing Emergency Package data",ActionMessageKeys.IMPORT_FAILURE.key,Results.IMPORT_STATUS_REPORT.getValue());
		}
		return Results.IMPORT_STATUS_REPORT.getValue();
	}

	private List<Reason> importEmergencyPackages(List<EmergencyPkgDataExt> emergencyPkgDataForImport, String action)  {
		List<Reason> reasons  = Collectionz.newArrayList();
		for(EmergencyPkgDataExt importEmergencyPkg : emergencyPkgDataForImport){
			if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Import Operation started for emergency package: " + importEmergencyPkg.getName());
			}
			Reason reason = new Reason(importEmergencyPkg.getName());
			try {
				//Setting staff information to the emergency package
				importEmergencyPkg.setCreatedByStaff((StaffData)request.getSession().getAttribute("staffData"));
				importEmergencyPkg.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				importEmergencyPkg.setModifiedByStaff(importEmergencyPkg.getCreatedByStaff());
				importEmergencyPkg.setModifiedDate(importEmergencyPkg.getCreatedDate());
				boolean isExistGroup = true;
				List<String> groups = new ArrayList<String>();
				if(Strings.isNullOrBlank(importEmergencyPkg.getGroupNames())){
					importEmergencyPkg.setGroups(CommonConstants.DEFAULT_GROUP_ID);
					groups.add(CommonConstants.DEFAULT_GROUP_ID);
				}else {
					isExistGroup = importExportUtil.getGroupIdsFromName(importEmergencyPkg, reason, groups);
				}
				if(isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
					List<String> staffBelongingGroups = CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
					importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(), reason, ACLModules.EMERGENCYPKG, ACLAction.IMPORT.name(), getStaffData().getUserName(), staffBelongingGroups, importEmergencyPkg, importEmergencyPkg.getName());

					boolean flag = importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(), reason, ACLModules.EMERGENCYPKG, ACLAction.IMPORT.name(),getStaffData().getUserName());
					if (CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
						importEmergencyPkg.setGroups(Strings.join(",",groups));
						importExportUtil.validateAndImportInformation(importEmergencyPkg, action, reason);
						if (getLogger().isDebugLogLevel()) {
							getLogger().debug(MODULE, "Import Operation finished for emergency package: " + importEmergencyPkg.getName());
						}
					}
				}
				reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
				reasons.add(reason);

			} catch (Exception e) {
				getLogger().error(MODULE, "Error while importing emergency package data: " + importEmergencyPkg.getName());
				getLogger().trace(MODULE,e);
				String errorMessage = "Failed to import emergency package due to internal error. Kindly refer logs for further details";
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


	public List<PkgData> getPkgsList() {
	    return pkgsList;
	}

	public void setPkgsList(List<PkgData> pkgsList) {
	    this.pkgsList = pkgsList;
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

	public List<DataServiceTypeData> getDataServiceTypeDatas() {
		return dataServiceTypeDatas;
	}

	public void setDataServiceTypeDatas(List<DataServiceTypeData> dataServiceTypeDatas) {
		this.dataServiceTypeDatas = dataServiceTypeDatas;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groups) {
		this.groupList = groups;
	}

	@SkipValidation
	@Override
	public void validate() {
		
		Timestamp endDate 	 = pkgData.getAvailabilityEndDate();
		if(endDate!=null){
				long startDateTime = pkgData.getAvailabilityStartDate().getTime();

				if((endDate.getTime() - startDateTime) < CommonConstants.TEN_MINUTES){
					addFieldError("pkgData.availabilityEndDate", getText("pkg.availability.mustbe.atleast.ten.minutes"));
				}
		}
	}

	public List<String> getQosProfileDataNames() {
		return qosProfileDataNames;
	}

	public void setQosProfileDataNames(List<String> qosProfileDataNames) {
		this.qosProfileDataNames = qosProfileDataNames;
	}

	public int getTotalEmergencyPackages() {
		return totalEmergencyPackages;
	}

	public void setTotalEmergencyPackages(int totalEmergencyPackages) {
		this.totalEmergencyPackages = totalEmergencyPackages;
	}

	/**This method checks for group wise limit for packages.Flow of the method will be
	 *
	 * <p>Find out group wise packages Map </p>
	 *
	 *
	 * If Map is null or Empty i.e. no package created so far<br/>
	 *  <pre>
	 *    return false
	 *</pre>
	 * <p>If package has only one group & that is Default Group then</p>
	 *   <pre>
	 *    check for default group limit
	 *   </pre>
	 *
	 * <p>If packages has multiple groups but those group contains Default Group then</p>
	 *  <pre>
	 *    check for default group limit
	 *  </pre>
	 *<p>else</p>
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
				getLogger().error(MODULE, "Group Wise Limit has been reached for Group" + groupData.getName());
				addActionError(Discriminators.EMERGENCY_PACKAGE + " " + getText(key));
				addActionError(getText("pkg.groupwise.limit.reach.error", new String[]{groupData.getName()}));
				return true;
			}
		}
		return false;
	}


	@Override
	public String getIncludeProperties() {
		return null;
	}

}