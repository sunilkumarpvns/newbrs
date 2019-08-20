package com.elitecore.nvsmx.policydesigner.controller.ims;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.ImsPkgContainer;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportEntityAccumulator;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportCTRL;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.policydesigner.model.pkg.ims.IMSPkgDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.SessionProviderImpl;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.hibernate.criterion.Order;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class IMSPkgCTRL extends ImportExportCTRL<IMSPkgData> {

	private static final long serialVersionUID = 1L;
	private static final String MODULE = IMSPkgCTRL.class.getSimpleName();
	private IMSPkgData imsPkgData = new IMSPkgData();
	private List<String> groupList = Collectionz.newArrayList();
	private static final String ACTION_SEARCH = "policydesigner/ims/IMSPkg/search";
	private String actionChainUrl;
	Object [] messageParameter = {Discriminators.IMS_PACKAGE};
	public static final String IMS_PKG_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.status,dataList\\[\\d+\\]\\.price,dataList\\[\\d+\\]\\.packageMode,dataList\\[\\d+\\]\\.groups";
	private static ImportExportUtil importExportUtil = new ImportExportUtil(new SessionProviderImpl());

	@SkipValidation
	public String init() {

		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called init()");
		}

		String pkgId = getPkgId();
		if (Strings.isNullOrBlank(pkgId)) {
		imsPkgData.setDescription(NVSMXUtil.getDefaultDescription(request));
		return Results.CREATE.getValue();
	}
		request.setAttribute(Attributes.ACTION, ACTION_SEARCH);
		try {
			imsPkgData = CRUDOperationUtil.get(IMSPkgData.class, pkgId);

			if (imsPkgData == null) {
				LogManager.getLogger().error(MODULE, "Error while fetching IMS Package data for update operation. Reason: No IMS package found with given id: " + pkgId);
				return redirectError(Discriminators.IMS_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}
			return Results.UPDATE.getValue();

		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while fetching IMS Package data for update operation.", ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key, Results.LIST.getValue());
		}
	}


	private String getPkgId() {

		String pkgId = request.getParameter(Attributes.PKG_ID);
		if (Strings.isNullOrBlank(pkgId)) {
			pkgId = (String) request.getAttribute(Attributes.PKG_ID);
			if (Strings.isNullOrBlank(pkgId)) {
				pkgId = request.getParameter("imsPkgData.id");
			}
		}
		return pkgId;
	}

	@InputConfig(resultName = InputConfigConstants.CREATE)
	public String create(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called create()");
		}
		request.setAttribute(Attributes.ACTION, ACTION_SEARCH);
		try{
			imsPkgData.setCreatedDateAndStaff(getStaffData());
			if(Strings.isNullOrBlank(getGroupIds())){
				imsPkgData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				imsPkgData.setGroups(getGroupIds());
			}
			CRUDOperationUtil.save(imsPkgData);
			String message = Discriminators.IMS_PACKAGE + " <b><i>" + imsPkgData.getName() + "</i></b> " + "Created";  
			CRUDOperationUtil.audit(imsPkgData,imsPkgData.getName(),AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), imsPkgData.getHierarchy(), message);
			request.setAttribute(Attributes.PKG_ID, imsPkgData.getId());
			MessageFormat messageFormat = new MessageFormat(getText("create.success"));
			addActionMessage(messageFormat.format(messageParameter));
			setActionChainUrl(Results.VIEW.getValue());
			return Results.DISPATCH_VIEW.getValue();
			
 		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Failed to Create "+Discriminators.IMS_PACKAGE+".", ActionMessageKeys.CREATE_FAILURE.key, Results.LIST.getValue());
		}
	}


	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update(){	
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called update()");
		}
		String pkgId = imsPkgData.getId();
		request.setAttribute(Attributes.ACTION, ACTION_SEARCH);
		try {
			imsPkgData.setModifiedDateAndStaff(getStaffData());
			if(Strings.isNullOrBlank(getGroupIds())){
				imsPkgData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				imsPkgData.setGroups(getGroupIds());
			}
			CRUDOperationUtil.update(imsPkgData);

			String message = Discriminators.IMS_PACKAGE + " <b><i>" + imsPkgData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(imsPkgData,imsPkgData.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),imsPkgData.getHierarchy(), message);
			
			request.setAttribute(Attributes.PKG_ID, pkgId);
			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			setActionChainUrl(Results.VIEW.getValue());
			return Results.DISPATCH_VIEW.getValue();
			
 		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while updating ims package of PkgId: '"+pkgId+"'.", ActionMessageKeys.UPDATE_FAILURE.key, Results.LIST.getValue());
		}		
	}

	@SkipValidation
	public String view(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called view()");
		}

		String pkgId = getPkgId();
		request.setAttribute(Attributes.ACTION, ACTION_SEARCH);
		try {
			if(Strings.isNullOrEmpty(pkgId)==false){
				imsPkgData = CRUDOperationUtil.get(IMSPkgData.class, pkgId);

				if (imsPkgData == null) {
					LogManager.getLogger().error(MODULE, "Error while fetching IMS Package data for update operation. Reason: No IMS package found with given id: " + pkgId);
					return redirectError(Discriminators.IMS_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
				} else {
					String belonginsGroups = GroupDAO.getGroupNames(CommonConstants.COMMA_SPLITTER.split(imsPkgData.getGroups()));
					imsPkgData.setGroupNames(belonginsGroups);
					return Results.VIEW.getValue();
				}
			} else {
				return redirectError(Discriminators.IMS_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}
 		} catch (Exception e) {
			return generateErrorLogsAndRedirect(e, "Error while fetching IMS Package data for view operation.", ActionMessageKeys.VIEW_FAILURE.key, Results.LIST.getValue());
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
	public String updateMode(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called updateMode()");
		}
		PrintWriter out = null;
		try {
			out = response.getWriter();
			String pkgId = getPkgId();
			imsPkgData = CRUDOperationUtil.get(IMSPkgData.class, pkgId);
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "ImsPkgDataId: " + imsPkgData.getId());
			}
			String pkgModeVal = request.getParameter(Attributes.PKG_MODE);
			
			PkgMode pkgMode = PkgMode.getMode(pkgModeVal);
			PkgMode pkgNextMode = pkgMode.getNextMode();
			if(pkgNextMode!=null){
				imsPkgData.setPackageMode(pkgNextMode.val);
			}

			List<PolicyDetail> policyDetails = DefaultNVSMXContext.getContext().getPolicyRepository().getPolicyDetail(imsPkgData.getName());
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
			
			imsPkgData.setModifiedDateAndStaff(getStaffData());
			CRUDOperationUtil.update(imsPkgData);
		
			String message = Discriminators.IMS_PACKAGE + " <b><i>" + imsPkgData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(imsPkgData,imsPkgData.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),imsPkgData.getHierarchy(), message);

			request.setAttribute(Attributes.PKG_ID, imsPkgData.getId());
			object.addProperty("responseCode", String.valueOf(ResultCode.SUCCESS.code));
			out.print(object.toString());
			out.flush();
			return Results.DISPATCH_VIEW.getValue();
 		} catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_IMS_PKG_VIEW);
			return generateErrorLogsAndRedirect(e, "Error while updating ims package of PkgId: '"+imsPkgData.getId()+"'.", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		} finally {
			out.close();
		}
	}

	@SkipValidation
	public String updateStatus(){
		LogManager.getLogger().debug(MODULE, "Method called updateStatus()");
		
		try {
			
			String pkgId = request.getParameter("imsPkgData.id");
			String pkgStatusVal = request.getParameter("imsPkgData.status");
			
			imsPkgData = CRUDOperationUtil.get(IMSPkgData.class, pkgId);
			LogManager.getLogger().debug(MODULE, "IMSPkgId: "+imsPkgData.getId());
			
			PkgStatus pkgStatus = PkgStatus.fromVal(pkgStatusVal);
			
			if(pkgStatus!=null){
				imsPkgData.setStatus(pkgStatus.name());
			}
			
			imsPkgData.setModifiedDateAndStaff(getStaffData());
			CRUDOperationUtil.update(imsPkgData);
			String message = Discriminators.PACKAGE + " <b><i>" + imsPkgData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(imsPkgData,imsPkgData.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(),imsPkgData.getHierarchy(), message);
			
			request.setAttribute(Attributes.PKG_ID, imsPkgData.getId());
			
			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			setActionChainUrl(Results.VIEW.getValue());
			return Results.DISPATCH_VIEW.getValue();
 		} catch (Exception e) {
			setActionChainUrl(NVSMXCommonConstants.ACTION_IMS_PKG_VIEW);
			return generateErrorLogsAndRedirect(e, "Error while updating ims package of PkgId: '"+imsPkgData.getId()+"'.", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	@SkipValidation
	public String delete(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called delete()");
		}
		String[] pkgIds = request.getParameterValues(Attributes.IDS);
		request.setAttribute(Attributes.ACTION, ACTION_SEARCH);
		try {
			if( pkgIds != null){
				for(String pkgId: pkgIds){
					imsPkgData = CRUDOperationUtil.get(IMSPkgData.class, pkgId);
					imsPkgData.setDeletedStatus();
					CRUDOperationUtil.update(imsPkgData);
				}
				String message = Discriminators.IMS_PACKAGE + " <b><i>" + imsPkgData.getName() + "</i></b> " + " Deleted";
				CRUDOperationUtil.audit(imsPkgData,imsPkgData.getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),imsPkgData.getHierarchy(), message);
				MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
				addActionMessage(messageFormat.format(messageParameter));
				setActionChainUrl(ACTION_SEARCH);
				return Results.REDIRECT_ACTION.getValue();
			} else {
				return redirectError(Discriminators.IMS_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}
 		} catch (Exception e) {			
			return generateErrorLogsAndRedirect(e, "Error while fetching IMS Package data for delete operation.", ActionMessageKeys.DELETE_FAILURE.key, Results.LIST.getValue());
		}					
	}

	public IMSPkgData getImsPkgData() {
		return imsPkgData;
	}

	public void setImsPkgData(IMSPkgData imsPkgData) {
		this.imsPkgData = imsPkgData;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}

	@Override
	public IMSPkgData getModel() {
		return imsPkgData;
	}


	
	public String getActionChainUrl() {
		return actionChainUrl;
	}

	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

	@Override
	protected List<IMSPkgData> getSearchResult(String criteriaJson,Class<IMSPkgData> beanType, int startIndex, int maxRecords,String sortColumnName, String sortColumnOrder,String staffBelongingGroups) throws Exception {
		if (Strings.isNullOrEmpty(criteriaJson) == false) {
			Gson gson = GsonFactory.defaultInstance();
			IMSPkgData imsPkgData = gson.fromJson(criteriaJson, beanType);
			return IMSPkgDAO.searchByCriteria(imsPkgData, startIndex, maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups);
		}else{
			return CRUDOperationUtil.findAllWhichIsNotDeleted(beanType,startIndex, maxRecords, sortColumnName, sortColumnOrder,staffBelongingGroups);
		}
	}

	@Override
	public String getIncludeProperties(){
		return IMS_PKG_INCLUDE_PARAMETERS;
	}

	@SkipValidation
	public String export(){
		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called export()");
		}
		String[] imsPkgIds = request.getParameterValues(Attributes.IDS);
		List<IMSPkgData> imsPkgDatas = new ArrayList<IMSPkgData>();
		String FILENAME = "exportImsPkg_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date())+ XML;
		BufferedWriter writer = null;
		PrintWriter out = null;
		try {
			Map<String,String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
			boolean exportOperation = checkForImportExportOperation();
			if(exportOperation == true){
				return redirectError(Discriminators.IMS_PACKAGE, "ims.pkg.importexport.operation", Results.LIST.getValue(), false);
			}
			makeImportExportOperationInProgress(true);
			if( imsPkgIds == null){
				makeImportExportOperationInProgress(false);
				return redirectError(Discriminators.IMS_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
			}
			ImsPkgContainer imsPkgContainer = new ImsPkgContainer();
			imsPkgDatas = CRUDOperationUtil.getAllByIds(IMSPkgData.class, imsPkgIds);
			for(IMSPkgData imsPkg : imsPkgDatas){

				if(Strings.isNullOrBlank(imsPkg.getGroups())){
					imsPkg.setGroups(CommonConstants.DEFAULT_GROUP);
				}
				importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, imsPkg);
			}

			imsPkgContainer.setImsPkgData(imsPkgDatas);

			StringWriter stringWriter = new StringWriter();
			ConfigUtil.serialize(stringWriter, ImsPkgContainer.class, imsPkgContainer);

			String imsPkgInfo = stringWriter.toString();
			if(Strings.isNullOrBlank(imsPkgInfo)){
				if(LogManager.getLogger().isWarnLogLevel()){
					LogManager.getLogger().warn(MODULE, "Can not find content for the ims package.");
				}
				throw new Exception("Can not find data as string for the ims package");
			}
			response.setContentType("text/xml");
			response.setHeader("Content-Disposition", "attachment; filename=\""+FILENAME+"\"");

			out = response.getWriter();
			writer = new BufferedWriter(out);
			writer.write(imsPkgInfo, 0, imsPkgInfo.length());
			writer.flush();


			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "All the IMS Packages are exported successfully");
			}
			addActionMessage("IMS Packages are exported successfully");
			makeImportExportOperationInProgress(false);
			request.getSession().setAttribute(Attributes.LAST_URI, ACTION_SEARCH);
			return "EXPORT_COMPLETED";



		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while fetching IMS Package data for delete operation.", ActionMessageKeys.EXPORT_FAILURE.key, Results.LIST.getValue());
		}finally {
			Closeables.closeQuietly(writer);
			Closeables.closeQuietly(out);
		}
	}

	@SkipValidation
	public String exportAll(){
		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called exportAll()");
		}
		List<IMSPkgData> imsPkgDatas = new ArrayList<IMSPkgData>();
		String FILENAME = "exportImsPkg_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date())+ XML;
		BufferedWriter writer = null;
		PrintWriter out = null;
		try {
			Map<String,String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
			boolean exportOperation = checkForImportExportOperation();
			if(exportOperation == true){
				return redirectError(Discriminators.IMS_PACKAGE, "ims.pkg.importexport.operation", Results.LIST.getValue(), false);
			}
			makeImportExportOperationInProgress(true);
			ImsPkgContainer imsPkgContainer = new ImsPkgContainer();
			imsPkgDatas = CRUDOperationUtil.get(IMSPkgData.class, Order.asc("name"));

			importExportUtil.filterBasedOnStaffBelongingGroupIds(imsPkgDatas, getStaffBelongingGroups());
			if (Collectionz.isNullOrEmpty(imsPkgDatas)) {
				makeImportExportOperationInProgress(false);
				LogManager.getLogger().warn(MODULE, getText("ims.pkg.importexport.nopackagefound"));
				return redirectError(MODULE,"ims.pkg.importexport.nopackagefound", Results.LIST.getValue(),false);
			}
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Export All operation started for ims packages");
			}

			List<IMSPkgData> imsPkgDataToBeExported = new ArrayList<IMSPkgData>();
			for(IMSPkgData imsPkgData : imsPkgDatas){
				List<String> groups = SPLITTER.split(imsPkgData.getGroups());
				Reason reason = new Reason(imsPkgData.getName());
				boolean isExportAllowedForGroup = importExportUtil.filterStaffBelongingGroupsAndRoles(groups,getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(),reason, ACLModules.IMSPKG, ACLAction.EXPORT.name(),getStaffData().getUserName());
				if(isExportAllowedForGroup){
					imsPkgDataToBeExported.add(imsPkgData);
					importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, imsPkgData);
				}
			}

			if (Collectionz.isNullOrEmpty(imsPkgDataToBeExported)) {
				makeImportExportOperationInProgress(false);
				LogManager.getLogger().warn(MODULE, getText("ims.pkg.importexport.nopackagefound"));
				return redirectError(MODULE,"ims.pkg.importexport.nopackagefound", Results.LIST.getValue(),false);
			}
			imsPkgContainer.setImsPkgData(imsPkgDataToBeExported);

			StringWriter stringWriter = new StringWriter();
			ConfigUtil.serialize(stringWriter, ImsPkgContainer.class, imsPkgContainer);
			String pkgInfo = stringWriter.toString();
			if(Strings.isNullOrBlank(pkgInfo)){
				if(LogManager.getLogger().isWarnLogLevel()){
					LogManager.getLogger().warn(MODULE, "Can not find content for the ims package.");
				}
				throw new Exception("Can not find data as string for the ims package");
			}
			response.setContentType("text/xml");
			response.setHeader("Content-Disposition", "attachment; filename=\""+FILENAME+"\"");
			out = response.getWriter();
			writer = new BufferedWriter(out);
			writer.write(pkgInfo,0,pkgInfo.length());
			writer.flush();

			makeImportExportOperationInProgress(false);
			request.getSession().setAttribute(Attributes.LAST_URI, ACTION_SEARCH);
			LogManager.getLogger().debug(MODULE,"Export All operation ended for ims packages");
			return "EXPORT_COMPLETED";

		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while fetching IMS Package data for export operation.", ActionMessageKeys.EXPORT_FAILURE.key, Results.LIST.getValue());
		}finally {
			Closeables.closeQuietly(writer);
			Closeables.closeQuietly(out);
		}
	}

	@SkipValidation
	public String importImsPkg() throws ServletException {
		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called import()");
		}
		if(isImportExportOperationInProgress()){
			return redirectError(Discriminators.IMS_PACKAGE, "ims.pkg.importexport.operation", Results.LIST.getValue(), false);
		}
		if(getImportedFile() == null){
			LogManager.getLogger().error(MODULE,"Error while importing IMS Package data. Reason: File not found");
			return redirectError(Discriminators.IMS_PACKAGE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
		}
		try {
			if(TEXT_XML.equals(getImportedFileContentType()) == false ){
				LogManager.getLogger().error(MODULE,"Error while importing ims package data. Reason: Invalid File type is configured. Only XML File is supported for importing ims package");
				return redirectError(Discriminators.IMS_PACKAGE, "ims.pkg.importexport.invalidfiletype", Results.LIST.getValue(), false);
			}
			ImsPkgContainer imsPkgContainer =  ConfigUtil.deserialize(getImportedFile(), ImsPkgContainer.class);
			if(imsPkgContainer == null) {
				return Results.REDIRECT_ERROR.getValue();
			}
			List<IMSPkgData> imsPkgDatas = imsPkgContainer.getImsPkgData();
			skipIMSPackageHaveNullName(imsPkgDatas);
			request.getSession().setAttribute(Attributes.IMS_PKG_DATAS, imsPkgDatas);

			Gson gson = GsonFactory.defaultInstance();
			JsonArray importImsPkgJson = gson.toJsonTree(imsPkgDatas, new TypeToken<List<IMSPkgData>>() {}.getType()).getAsJsonArray();
			request.setAttribute("importedImsPkgs", importImsPkgJson);
			return Results.IMPORT_IMS_PKG.getValue();

		} catch (JAXBException e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while importing IMS Package data due to XML processing failure. ", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while importing IMS Package data. ", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
		}
	}

	private void skipIMSPackageHaveNullName(List<IMSPkgData> pkgDatas) {
        /*
        This method is used to remove the IMSPkgData have null name from the list of pkgDatas.
        It will also set proper message in request attribute(invalidEntityMessage) to display in GUI.
        */

		Predicate predicate = new Predicate<IMSPkgData>() {
			@Override
			public boolean apply(IMSPkgData input) {
				if(Strings.isNullOrBlank(input.getName())){
					LogManager.getLogger().info(MODULE, "Found IMSPkgData with null name. Skipping Import process for IMSPkgData: " + input);
					return false;
				}
				return true;
			};
		};

		String message = ImportExportUtil.removeInvalidEntitiesAndGetMessage(pkgDatas, predicate, Discriminators.IMS_PACKAGE);
		request.setAttribute(INVALID_ENTITY_MESSAGE,message);
	}

	@SkipValidation
	public String importData() {
		LogManager.getLogger().debug(MODULE, "Method called importData()");
		try {
			String selectedImsPkgIndexes = request.getParameter(Attributes.SELECTED_INDEXES);
			String action = request.getParameter(Attributes.USER_ACTION);
			if(isImportExportOperationInProgress()){
				return redirectError(Discriminators.IMS_PACKAGE, "ims.pkg.importexport.operation", Results.LIST.getValue(), false);
			}
			List<IMSPkgData> imsPkgs = (List<IMSPkgData>) request.getSession().getAttribute(Attributes.IMS_PKG_DATAS);
			List<IMSPkgData> selectedImsPkgs = Collectionz.newArrayList();

			if(Strings.isNullOrBlank(selectedImsPkgIndexes) == false && Collectionz.isNullOrEmpty(imsPkgs) == false) {
				makeImportExportOperationInProgress(true);
				selectedImsPkgs = new ImportEntityAccumulator<IMSPkgData>(imsPkgs, selectedImsPkgIndexes).get();
			}

			List<Reason> reasons = importImsPackages(selectedImsPkgs, action);
			Gson gson = GsonFactory.defaultInstance();
			JsonArray importPkgResultJson = gson.toJsonTree(reasons, new TypeToken<List<Reason>>() {}.getType()).getAsJsonArray();
			request.setAttribute("importStatus", importPkgResultJson);
			makeImportExportOperationInProgress(false);
		} catch (Exception e) {
			makeImportExportOperationInProgress(false);
			return generateErrorLogsAndRedirect(e, "Error while importing IMS Package data. ", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
		}
		return Results.IMPORT_STATUS_REPORT.getValue();
	}

	private List<Reason> importImsPackages(List<IMSPkgData> imsPkgDataForImport, String action)  {
		List<Reason> reasons  = Collectionz.newArrayList();
		for(IMSPkgData importImsPkg : imsPkgDataForImport){
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Import Operation started for ims package: " + importImsPkg.getName());
			}
			Reason reason = new Reason(importImsPkg.getName());
			try {
				//Setting staff information to the package
				StaffData staffData = (StaffData)request.getSession().getAttribute("staffData");
				importImsPkg.setCreatedDateAndStaff(staffData);
				importImsPkg.setModifiedDateAndStaff(staffData);
				boolean isExistGroup = true;
				List<String> groups = new ArrayList<String>();
				if(Strings.isNullOrBlank(importImsPkg.getGroupNames())){
					importImsPkg.setGroups(CommonConstants.DEFAULT_GROUP_ID);
					groups.add(CommonConstants.DEFAULT_GROUP_ID);
				}else {
					isExistGroup = importExportUtil.getGroupIdsFromName(importImsPkg, reason, groups);
				}
				if(isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
					List<String> staffBelongingGroups = CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
					importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(), reason, ACLModules.IMSPKG, ACLAction.IMPORT.name(), getStaffData().getUserName(), staffBelongingGroups, importImsPkg, importImsPkg.getName());


					boolean flag = importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(), reason, ACLModules.IMSPKG, ACLAction.IMPORT.name(),getStaffData().getUserName());

					if (CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
						importImsPkg.setGroups(Strings.join(",",groups));
						importExportUtil.validateAndImportInformation(importImsPkg, action, reason);
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Import Operation finished for ims package: " + importImsPkg.getName());
						}
					}
				}
				reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
				reasons.add(reason);

			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Error while importing ims package data: " + importImsPkg.getName());
				LogManager.getLogger().trace(MODULE,e);
				String errorMessage = "Failed to import ims package due to internal error. Kindly refer logs for further details";
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

	private String generateErrorLogsAndRedirect(Exception e, String message, String key, String result) {
		return super.generateErrorLogsAndRedirect(Discriminators.IMS_PACKAGE, e, message, key, result);
	}

}
