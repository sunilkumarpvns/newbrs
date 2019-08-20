package com.elitecore.nvsmx.policydesigner.controller.dataservicetype;

import com.elitecore.commons.base.Arrayz;
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
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.pkg.dataservicetype.*;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportEntityAccumulator;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportCTRL;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.policydesigner.model.pkg.dataservicetype.DataServiceTypeDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.SessionProviderImpl;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.hibernate.criterion.Order;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Controls DataServiceType related operations
 * @author Dhyani.Raval
 *
 */
public class DataServiceTypeCTRL extends ImportExportCTRL<DataServiceTypeData> {

	private static final long serialVersionUID = 1L;
	public static final String MODULE = DataServiceTypeCTRL.class.getSimpleName();
	private DataServiceTypeData dataServiceTypeData = new DataServiceTypeData();
	private static final String ACTION_SEARCH ="policydesigner/dataservicetype/DataServiceType/search";
	private static final String ACTION_VIEW ="policydesigner/dataservicetype/DataServiceType/view";
	private String actionChainUrl;
	private List<RatingGroupData> allRatingGroups = Collectionz.newArrayList();
	private List<GroupData> staffBelongingGroupList = Collectionz.newArrayList();
	Object []messageParameter = {Discriminators.DATA_SERVICE_TYPE};
	public static final String DATA_SERVICE_TYPE_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.description,dataList\\[\\d+\\]\\.serviceIdentifier,dataList\\[\\d+\\]\\.groups";
	private static ImportExportUtil importExportUtil = new ImportExportUtil(new SessionProviderImpl());

	@SkipValidation
	public String initCreate(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called initCreate()");
		}
		dataServiceTypeData.setDescription(NVSMXUtil.getDefaultDescription(request));
		setAllRatingGroups(getRatingGroups());
		return Results.CREATE.getValue();
	}

	@InputConfig(resultName = InputConfigConstants.CREATE)
	public String create(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called create()");
		}
		try {
			
			String []ratingGroupIds = request.getParameterValues(Attributes.RATING_GROUP_IDS);
			setDefaultServiceDataFlow();
			dataServiceTypeData.setCreatedDateAndStaff(getStaffData());

			dataServiceTypeData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
			if(Arrayz.isNullOrEmpty(ratingGroupIds) == false) {
				dataServiceTypeData.setRatingGroupDatas(CRUDOperationUtil.getAllByIds(RatingGroupData.class, ratingGroupIds));
			}
			CRUDOperationUtil.save(dataServiceTypeData);
			String message = Discriminators.DATA_SERVICE_TYPE + " <b><i>" + dataServiceTypeData.getName() + "</i></b> " + "Created";
			CRUDOperationUtil.audit(dataServiceTypeData, dataServiceTypeData.getName(),AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), dataServiceTypeData.getHierarchy(), message);
			request.getSession().setAttribute(Attributes.DATA_SERVICE_TYPE_ID, dataServiceTypeData.getId());
			
			MessageFormat messageFormat = new MessageFormat(getText("create.success"));
			addActionMessage(messageFormat.format(messageParameter));
			
			setActionChainUrl(ACTION_VIEW);
			return Results.REDIRECT_ACTION.getValue();
 		}catch(Exception e) {
			getLogger().error(MODULE,"Failed to Create Data Service Type. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			addActionError(Discriminators.DATA_SERVICE_TYPE +" "+ getText(ActionMessageKeys.CREATE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
			
		}
	}

	@SkipValidation
	public String initUpdate(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called initUpdate()");
		}
		setActionChainUrl(ACTION_VIEW);
		try {
			String serviceTypeId = request.getParameter(Attributes.DATA_SERVICE_TYPE_ID);
			if(Strings.isNullOrBlank(serviceTypeId)){
				serviceTypeId = (String) request.getSession().getAttribute(Attributes.DATA_SERVICE_TYPE_ID);
				if(Strings.isNullOrBlank(serviceTypeId)) {
					setActionChainUrl(ACTION_SEARCH);
				}
			}else {
				request.getSession().setAttribute(Attributes.DATA_SERVICE_TYPE_ID,serviceTypeId);
				setActionChainUrl(ACTION_VIEW);
			}
			dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class, serviceTypeId);
			
			setAllRatingGroups(getRatingGroups());
			return Results.UPDATE.getValue();
 		}catch(Exception e) {
			getLogger().error(MODULE,"Failed to fetch Data Service Type. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			addActionError(Discriminators.DATA_SERVICE_TYPE +" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
		}
	}
	
	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called update()");
		}
		
		request.setAttribute(Attributes.ACTION, ACTION_SEARCH);
		setActionChainUrl(ACTION_VIEW);
		try {
			String []ratingGroupIds = request.getParameterValues(Attributes.RATING_GROUP_IDS);
			DataServiceTypeData dataServiceTypeDataInDb = CRUDOperationUtil.get(DataServiceTypeData.class, dataServiceTypeData.getId());
			dataServiceTypeDataInDb.setDescription(dataServiceTypeData.getDescription());
			dataServiceTypeDataInDb.setName(dataServiceTypeData.getName());
			dataServiceTypeDataInDb.setServiceIdentifier(dataServiceTypeData.getServiceIdentifier());

			dataServiceTypeDataInDb.getDefaultServiceDataFlows().clear();
			setDefaultServiceDataFlow();
			dataServiceTypeDataInDb.getDefaultServiceDataFlows().addAll(dataServiceTypeData.getDefaultServiceDataFlows());

			if(Arrayz.isNullOrEmpty(ratingGroupIds) == false) {
				dataServiceTypeDataInDb.setRatingGroupDatas(CRUDOperationUtil.getAllByIds(RatingGroupData.class, ratingGroupIds));
			} else {
				dataServiceTypeDataInDb.getRatingGroupDatas().clear();
			}
			dataServiceTypeDataInDb.setModifiedDateAndStaff(getStaffData());
			CRUDOperationUtil.update(dataServiceTypeDataInDb);
			String message = Discriminators.DATA_SERVICE_TYPE + " <b><i>" + dataServiceTypeDataInDb.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(dataServiceTypeDataInDb, dataServiceTypeDataInDb.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), dataServiceTypeDataInDb.getHierarchy(), message);
			
			request.getSession().setAttribute(Attributes.DATA_SERVICE_TYPE_ID, dataServiceTypeData.getId());
			
			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			
			return Results.REDIRECT_ACTION.getValue();
 		}catch(Exception e) {
			getLogger().error(MODULE,"Failed to Update Data Service Type. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			addActionError(Discriminators.DATA_SERVICE_TYPE +" " + getText(ActionMessageKeys.UPDATE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
			
		}
	}

	@SkipValidation
	public String view(){
		
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called view()");
		}
		try {

			String serviceTypeId = request.getParameter(Attributes.DATA_SERVICE_TYPE_ID);
			if (Strings.isNullOrEmpty(serviceTypeId)) {
				serviceTypeId = (String) request.getAttribute(Attributes.DATA_SERVICE_TYPE_ID);
			}
			if (Strings.isNullOrEmpty(serviceTypeId)){
				serviceTypeId = (String) request.getSession().getAttribute(Attributes.DATA_SERVICE_TYPE_ID);
			}
			dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class, serviceTypeId);
			if(Strings.isNullOrBlank(dataServiceTypeData.getGroups()) == false){
				dataServiceTypeData.setGroupNames(GroupDAO.getGroupNames(SPLITTER.split(dataServiceTypeData.getGroups())));
			}
			request.getSession().setAttribute(Attributes.DATA_SERVICE_TYPE_ID, serviceTypeId);
 		}catch(Exception e) {
			getLogger().error(MODULE,"Failed to View Data Service Type. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			addActionError(Discriminators.DATA_SERVICE_TYPE +" " + getText(ActionMessageKeys.VIEW_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
			
		}
		return Results.VIEW.getValue();
		
	}

	@Override
	public String getIncludeProperties(){
		return DATA_SERVICE_TYPE_INCLUDE_PARAMETERS;
	}

	@SkipValidation
	public String search(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called search()");
		}
		return Results.LIST.getValue();
	}

	@SkipValidation
	public String delete(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE,"Method called delete()");
		}
		
		request.setAttribute(Attributes.ACTION, ACTION_SEARCH);
		setActionChainUrl(ACTION_SEARCH);
		try{
			String []serviceTypeIds = request.getParameterValues(Attributes.IDS);
			if(serviceTypeIds != null){
				for(String serviceTypeId : serviceTypeIds){
					dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class,serviceTypeId);
					String attachedPackages = DataServiceTypeDAO.AttachedWithPackages(dataServiceTypeData.getId());

					if(Strings.isNullOrBlank(attachedPackages) == false){
						request.getSession().setAttribute(Attributes.DATA_SERVICE_TYPE_ID,serviceTypeId);
						addActionError("Unable to delete '"+ dataServiceTypeData.getName()+"'. <br/>Reason :  "+Discriminators.DATA_SERVICE_TYPE +" is configured with one or more "+Discriminators.PACKAGE);
						LogManager.getLogger().error(MODULE,"Unable to delete "+ dataServiceTypeData.getName()+" DataServiceType is configured with packages: " + attachedPackages );
						setActionChainUrl(ACTION_VIEW);
						return Results.REDIRECT_ERROR.getValue();
					}

					String attachedPccRules = DataServiceTypeDAO.getAttachPccRules(dataServiceTypeData.getId());
					if(Strings.isNullOrBlank(attachedPccRules) == false){
						request.getSession().setAttribute(Attributes.DATA_SERVICE_TYPE_ID,serviceTypeId);
						addActionError("Unable to delete '"+ dataServiceTypeData.getName()+"'. <br/>Reason :  "+Discriminators.DATA_SERVICE_TYPE +" is configured with one or more "+Discriminators.GLOBAL_PCC_RULE);
						LogManager.getLogger().error(MODULE,"Unable to delete "+ dataServiceTypeData.getName()+" DataServiceType is configured with global pcc rules:  " + attachedPccRules);
						setActionChainUrl(ACTION_VIEW);
						return Results.REDIRECT_ERROR.getValue();
					}

					String attachedChargingRuleBaseName = DataServiceTypeDAO.getAttachChargingRuleBaseName(dataServiceTypeData.getId());
					if(Strings.isNullOrBlank(attachedChargingRuleBaseName) == false){
						request.getSession().setAttribute(Attributes.DATA_SERVICE_TYPE_ID,serviceTypeId);
						addActionError("Unable to delete '"+ dataServiceTypeData.getName()+"'. <br/>Reason :  "+Discriminators.DATA_SERVICE_TYPE +" is configured with one or more "+Discriminators.CHARGING_RULE_BASE_NAME);
						LogManager.getLogger().error(MODULE, "Unable to delete "+ dataServiceTypeData.getName()+" DataServiceType is configured with charging rule base name:  " + attachedChargingRuleBaseName );
						setActionChainUrl(ACTION_VIEW);
						return Results.REDIRECT_ERROR.getValue();
					}

					String message = Discriminators.DATA_SERVICE_TYPE + " <b><i>" + dataServiceTypeData.getName() + "</i></b> " + " Deleted";
					CRUDOperationUtil.audit(dataServiceTypeData, dataServiceTypeData.getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(), dataServiceTypeData.getHierarchy(), message);
					dataServiceTypeData.setStatus(CommonConstants.STATUS_DELETED);
					CRUDOperationUtil.update(dataServiceTypeData);
				}
			}
			
 		}catch(Exception ex){
			getLogger().error(MODULE,"Failed to delete Data Service Type. Reason: " + ex.getMessage());
			getLogger().trace(MODULE,ex);
			addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.DELETE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.LIST.getValue();
		}
		MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
		addActionMessage(messageFormat.format(messageParameter));
		
		return Results.REDIRECT_ACTION.getValue();
	}
	
	/**
	 * This method assigns service type into default service data flow
	 */
	private void setDefaultServiceDataFlow() {
		filterEmptyServiceDataFlowDatas(dataServiceTypeData.getDefaultServiceDataFlows());
		for(DefaultServiceDataFlowData defaultServiceDataFlowData : dataServiceTypeData.getDefaultServiceDataFlows()){
			defaultServiceDataFlowData.setDataServiceTypeData(dataServiceTypeData);
		}
	}
	
	private List<RatingGroupData> getRatingGroups(){
		return CRUDOperationUtil.findAllWhichIsNotDeleted(RatingGroupData.class);
	}
	
	/**
	 * Filter empty or null data from the list
	 * @param defaultServiceDataFlowDatas
	 */
	private void filterEmptyServiceDataFlowDatas(List<DefaultServiceDataFlowData> defaultServiceDataFlowDatas){
		Collectionz.filter(defaultServiceDataFlowDatas,new Predicate<DefaultServiceDataFlowData>() {
			@Override
			public boolean apply(DefaultServiceDataFlowData serviceDataFlowData) {
				if(serviceDataFlowData == null){
					return false;
				}
				if(Strings.isNullOrBlank(serviceDataFlowData.getFlowAccess())
						&& Strings.isNullOrBlank(serviceDataFlowData.getProtocol())
						&& Strings.isNullOrBlank(serviceDataFlowData.getSourceIP())
						&& Strings.isNullOrBlank(serviceDataFlowData.getSourcePort())
						&& Strings.isNullOrBlank(serviceDataFlowData.getDestinationIP())
						&& Strings.isNullOrBlank(serviceDataFlowData.getDestinationPort())){
					return false;
				}
				return  true;
			}});
	}
	

	
	@Override
	public DataServiceTypeData getModel() {
		return dataServiceTypeData;
	}

	public DataServiceTypeData getDataServiceTypeData() {
		return dataServiceTypeData;
	}

	public void setDataServiceTypeData(DataServiceTypeData dataServiceTypeData) {
		this.dataServiceTypeData = dataServiceTypeData;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

	public List<RatingGroupData> getAllRatingGroups() {
		return allRatingGroups;
	}

	public void setAllRatingGroups(List<RatingGroupData> allRatingGroups) {
		this.allRatingGroups = allRatingGroups;
	}

	public List<GroupData> getStaffBelongingGroupList() {
		return staffBelongingGroupList;
	}

	public void setStaffBelongingGroupList(List<GroupData> staffBelongingGroups) {
		this.staffBelongingGroupList = staffBelongingGroups;
	}

	@Override
	protected List<DataServiceTypeData> getSearchResult(String criteriaJson, Class beanType, int startIndex, int maxRecords, String sortColumnName, String sortColumnOrder, String staffBelongingGroups) throws Exception {
		return super.getSearchResult(criteriaJson, beanType, startIndex, maxRecords,sortColumnName, sortColumnOrder, null); //set staff belongign groups to null inorder to get all services for each user
	}

	@SkipValidation
	public String exportAll(){
		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called exportAll()");
		}
		List<DataServiceTypeDataExt> dataServiceTypeDatas = new ArrayList<>();
		String FILENAME = "exportDataServiceType_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date())+ XML;
		BufferedWriter writer = null;
		PrintWriter out = null;
		try {
			Map<String,String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
			boolean exportOperation = checkForImportExportOperation();
			if(exportOperation == true){
				addActionError(Discriminators.DATA_SERVICE_TYPE +" " + getText(ActionMessageKeys.EXPORT_FAILURE.key));
				addActionError(getText("dataservicetype.importexport.operation"));
				return Results.LIST.getValue();

			}
			makeImportExportOperationInProgress(true);
			DataServiceTypeContainer dataServiceTypeContainer = new DataServiceTypeContainer();
			dataServiceTypeDatas = CRUDOperationUtil.get(DataServiceTypeDataExt.class, Order.asc("name"));

			importExportUtil.filterBasedOnStaffBelongingGroupIds(dataServiceTypeDatas, getStaffBelongingGroups());
			if (Collectionz.isNullOrEmpty(dataServiceTypeDatas)) {
				LogManager.getLogger().warn(MODULE, "No Data Service Type Data found for Export ALL operation");
			}
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Export All operation started for data packages");
			}

			List<DataServiceTypeDataExt> dataServiceTypeToBeExported = new ArrayList<DataServiceTypeDataExt>();
			for (DataServiceTypeDataExt dataServiceTypeData : dataServiceTypeDatas) {
				List<String> groups = SPLITTER.split(dataServiceTypeData.getGroups());
				Reason reason = new Reason(dataServiceTypeData.getName());
				dataServiceTypeToBeExported.add(dataServiceTypeData);
				importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, dataServiceTypeData);
			}

			if (Collectionz.isNullOrEmpty(dataServiceTypeToBeExported)) {
				LogManager.getLogger().warn(MODULE, "No Data Service Type found for Export ALL operation");
			}

			dataServiceTypeContainer.setserviceType(dataServiceTypeToBeExported);

			StringWriter stringWriter = new StringWriter();
			ConfigUtil.serialize(stringWriter, DataServiceTypeContainer.class, dataServiceTypeContainer);
			String serviceTypeInfo = stringWriter.toString();
			if(Strings.isNullOrBlank(serviceTypeInfo)){
				if(LogManager.getLogger().isWarnLogLevel()){
					LogManager.getLogger().warn(MODULE, "Can not find content for Data Service Type.");
				}
				throw new Exception("Can not find data as string for the Data Service Type");
			}
			response.setContentType("text/xml");
			response.setHeader("Content-Disposition", "attachment; filename=\""+FILENAME+"\"");
			out = response.getWriter();
			writer = new BufferedWriter(out);
			writer.write(serviceTypeInfo, 0, serviceTypeInfo.length());
			writer.flush();
			makeImportExportOperationInProgress(false);
			request.getSession().setAttribute(Attributes.LAST_URI, ACTION_SEARCH);
			LogManager.getLogger().debug(MODULE,"Export All operation ended for Data Service Type");
			return "EXPORT_COMPLETED";

		} catch(IllegalArgumentException e){
			LogManager.getLogger().error(MODULE,"Error while fetching Data Service Type data for export operation. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			addActionError(Discriminators.DATA_SERVICE_TYPE + " " +getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			makeImportExportOperationInProgress(false);
			return Results.LIST.getValue();
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Error while fetching Data Service Type data for export operation. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			addActionError(Discriminators.DATA_SERVICE_TYPE + " " +getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			makeImportExportOperationInProgress(false);
			return Results.LIST.getValue();
		}finally {
			Closeables.closeQuietly(writer);
			Closeables.closeQuietly(out);
		}
	}

	@SkipValidation
	public String export(){
		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called export()");
		}
		String[] serviceTypeIds = request.getParameterValues("ids");
		List<DataServiceTypeDataExt> serviceTypeDatas = new ArrayList<DataServiceTypeDataExt>();
		String FILENAME = "exportDataServiceType_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date())+ XML;
		BufferedWriter writer = null;
		PrintWriter out = null;
		try {
			Map<String,String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();

			if( serviceTypeIds == null){
				addActionError(Discriminators.DATA_SERVICE_TYPE +" " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
				addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
				makeImportExportOperationInProgress(false);
				return Results.REDIRECT_ERROR.getValue();
			}

			if(isImportExportOperationInProgress()){
				addActionError(Discriminators.DATA_SERVICE_TYPE +" " + getText(ActionMessageKeys.EXPORT_FAILURE.key));
				addActionError(getText("dataservicetype.importexport.operation"));
				return Results.LIST.getValue();
			}
			makeImportExportOperationInProgress(true);

			DataServiceTypeContainer dataServiceTypeContainer = new DataServiceTypeContainer();
			serviceTypeDatas = CRUDOperationUtil.getAllByIds(DataServiceTypeDataExt.class, serviceTypeIds);
			for(DataServiceTypeDataExt dataServiceType : serviceTypeDatas){

				if(Strings.isNullOrBlank(dataServiceType.getGroups())){
					dataServiceType.setGroups(CommonConstants.DEFAULT_GROUP);
				}
				importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, dataServiceType);
			}

			dataServiceTypeContainer.setserviceType(serviceTypeDatas);

			StringWriter stringWriter = new StringWriter();
			ConfigUtil.serialize(stringWriter, DataServiceTypeContainer.class, dataServiceTypeContainer);

			String serviceTypeInfo = stringWriter.toString();
			if(Strings.isNullOrBlank(serviceTypeInfo)){
				if(LogManager.getLogger().isWarnLogLevel()){
					LogManager.getLogger().warn(MODULE, "Can not find content for Data Service Type.");
				}
				throw new Exception("Can not find data as string for Data Service Type");
			}
			response.setContentType("text/xml");
			response.setHeader("Content-Disposition", "attachment; filename=\""+FILENAME+"\"");

			out = response.getWriter();
			writer = new BufferedWriter(out);
			writer.write(serviceTypeInfo, 0, serviceTypeInfo.length());
			writer.flush();


			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "All Data Service Types are exported successfully");
			}
			addActionMessage("Data Service Type are exported successfully");
			makeImportExportOperationInProgress(false);
			request.getSession().setAttribute(Attributes.LAST_URI, ACTION_SEARCH);
			return "EXPORT_COMPLETED";



		} catch(IllegalArgumentException e){
			LogManager.getLogger().error(MODULE,"Error while fetching Data Service Type data for export operation. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			addActionError(Discriminators.DATA_SERVICE_TYPE + " " +getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			makeImportExportOperationInProgress(false);
			return Results.LIST.getValue();
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Error while fetching Data Service Type data for export operation. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			addActionError(Discriminators.DATA_SERVICE_TYPE + " " +getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			makeImportExportOperationInProgress(false);
			return Results.LIST.getValue();
		}finally {
			Closeables.closeQuietly(writer);
			Closeables.closeQuietly(out);
		}
	}

	@SkipValidation
	public String importDataServiceType() throws ServletException {
		if(LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called importDataServiceType()");
		}
		if(isImportExportOperationInProgress()){
			addActionError(Discriminators.DATA_SERVICE_TYPE +" " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
			addActionError(getText("dataservicetype.importexport.operation"));
			return Results.LIST.getValue();

		}
		if(getImportedFile() == null){
			LogManager.getLogger().error(MODULE,"Error while importing Data Service Type data. Reason: File not found");
			addActionError(Discriminators.DATA_SERVICE_TYPE + " " +getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			return Results.LIST.getValue();
		}
		try {
			if(TEXT_XML.equals(getImportedFileContentType()) == false ){
				LogManager.getLogger().error(MODULE,"Error while importing data service type data. Reason: Invalid File type is configured. Only XML_FILE_EXT File is supported for importing service type");
				addActionError(Discriminators.DATA_SERVICE_TYPE + " " +getText(ActionMessageKeys.IMPORT_FAILURE.key));
				addActionError("Invalid File type for import");
				makeImportExportOperationInProgress(false);
				return Results.LIST.getValue();
			}
			DataServiceTypeContainer dataServiceTypeContainer =  ConfigUtil.deserialize(getImportedFile(), DataServiceTypeContainer.class);
			if(dataServiceTypeContainer == null) {
				return Results.REDIRECT_ERROR.getValue();
			}
			List<DataServiceTypeDataExt> serviceTypes = dataServiceTypeContainer.getserviceType();
			skipServiceTypeHaveNullName(serviceTypes);

			request.getSession().setAttribute("serviceTypes",serviceTypes);

			Gson gson = GsonFactory.defaultInstance();
			JsonArray importDataServiceTypeJson = gson.toJsonTree(serviceTypes,new TypeToken<List<DataServiceTypeDataExt>>() {}.getType()).getAsJsonArray();
			request.setAttribute("importedDataServiceTypes", importDataServiceTypeJson);
			return Results.IMPORT_SERVICE.getValue();

		} catch (IOException e) {
			LogManager.getLogger().error(MODULE,"Error while importing Data Service Type data. Reason: File not found");
			LogManager.getLogger().trace(MODULE, e);
			addActionError(Discriminators.DATA_SERVICE_TYPE + " " +getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			makeImportExportOperationInProgress(false);
			return Results.LIST.getValue();
		}  catch (JAXBException e) {
			LogManager.getLogger().error(MODULE,"Error while importing Data Service Type data due to XML_FILE_EXT processing failure. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			addActionError(Discriminators.DATA_SERVICE_TYPE + " " +getText(ActionMessageKeys.IMPORT_FAILURE.key));
			addActionError(getText(ActionMessageKeys.IMPORT_FAILURE.key));
			makeImportExportOperationInProgress(false);
			return Results.LIST.getValue();
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Error while importing Data Service Type data. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			addActionError(Discriminators.DATA_SERVICE_TYPE + " " +getText(ActionMessageKeys.IMPORT_FAILURE.key));
			addActionError(getText(ActionMessageKeys.IMPORT_FAILURE.key));
			makeImportExportOperationInProgress(false);
			return Results.LIST.getValue();
		}
	}

	private void skipServiceTypeHaveNullName(List<DataServiceTypeDataExt> dataServiceTypeDataExts) {
        /*
        This method is used to remove the DataServiceTypeData have null name from the list of dataServiceTypeDataExts.
        It will also set proper message in request attribute(invalidEntityMessage) to display in GUI.
        */

		Predicate predicate = new Predicate<DataServiceTypeDataExt>() {
			@Override
			public boolean apply(DataServiceTypeDataExt input) {
				if(Strings.isNullOrBlank(input.getName())){
					LogManager.getLogger().info(MODULE, "Found DataServiceTypeData with null name. Skipping Import process for DataServiceTypeData: " + input);
					return false;
				}
				return true;
			};
		};

		String message = ImportExportUtil.removeInvalidEntitiesAndGetMessage(dataServiceTypeDataExts, predicate, Discriminators.DATA_SERVICE_TYPE);
		request.setAttribute(INVALID_ENTITY_MESSAGE,message);
	}

	@SkipValidation
	public String importData() {
		LogManager.getLogger().debug(MODULE, "Method called importData()");
		try {
			String selectedServiceTypeIndexes = request.getParameter(Attributes.SELECTED_INDEXES);
			String action = request.getParameter(Attributes.USER_ACTION);


			if(isImportExportOperationInProgress() == true){
				addActionError(Discriminators.DATA_SERVICE_TYPE +" " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
				addActionError(getText("dataservicetype.importexport.operation"));
				return Results.LIST.getValue();

			}

			List<DataServiceTypeDataExt> serviceTypes = (List<DataServiceTypeDataExt>) request.getSession().getAttribute(Attributes.SERVICE_TYPES);

			List<DataServiceTypeDataExt> dataServiceTypeDataExts = Collectionz.newArrayList();

			if(Strings.isNullOrBlank(selectedServiceTypeIndexes) == false && Collectionz.isNullOrEmpty(serviceTypes) == false) {
				makeImportExportOperationInProgress(true);
				dataServiceTypeDataExts = new ImportEntityAccumulator<DataServiceTypeDataExt>(serviceTypes, selectedServiceTypeIndexes).get();
			}

			List<Reason> reasons = importServices(dataServiceTypeDataExts, action);
			Gson gson = GsonFactory.defaultInstance();
			JsonArray importServiceTypeResultJson = gson.toJsonTree(reasons, new TypeToken<List<Reason>>(){}.getType()).getAsJsonArray();
			request.setAttribute("importStatus", importServiceTypeResultJson);
			makeImportExportOperationInProgress(false);

		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while importing Data Service Type data");
			LogManager.getLogger().trace(MODULE, e);
			addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
			addActionError(getText(ActionMessageKeys.IMPORT_FAILURE.key));
			makeImportExportOperationInProgress(false);
			return Results.LIST.getValue();
		}
		return Results.IMPORT_STATUS_REPORT.getValue();
	}


	private List<Reason> importServices(List<DataServiceTypeDataExt> dataServiceTypeDatasForImport, String action)  {

		List<Reason> reasons  = Collectionz.newArrayList();
		if(Collectionz.isNullOrEmpty(dataServiceTypeDatasForImport)) {
			return reasons;
		}
		for(DataServiceTypeDataExt importService : dataServiceTypeDatasForImport){
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Import Operation started for data service type: " + importService.getName());
			}
			Reason reason = new Reason(importService.getName());
			try {
				//Setting staff information to the package
				importService.setCreatedByStaff((StaffData)request.getSession().getAttribute("staffData"));
				importService.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				importService.setModifiedByStaff(importService.getCreatedByStaff());
				importService.setModifiedDate(importService.getCreatedDate());
				boolean isExistGroup = true;
				List<String> groups = new ArrayList<String>();
				if(Strings.isNullOrBlank(importService.getGroupNames())){
					importService.setGroups(CommonConstants.DEFAULT_GROUP_ID);
					groups.add(CommonConstants.DEFAULT_GROUP_ID);
				}else {
					isExistGroup = importExportUtil.getGroupIdsFromName(importService, reason, groups);
				}
				if(isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {

					/**
					 * this will import services based on staff belonging groups
					 */
					//boolean flag = importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(), reason, ACLModules.DATAPKG, ACLAction.IMPORT.name(),getStaffData().getUserName());
					if (CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
						importService.setGroups(Strings.join(",",groups));
						importExportUtil.validateAndImportInformation(importService, action, reason);
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Import Operation finished for data service type: " + importService.getName());
						}
					}
				}
				reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
				reasons.add(reason);

			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Error while importing data service type data: " + importService.getName());
				LogManager.getLogger().trace(MODULE,e);
				String errorMessage = "Failed to import data service type due to internal error. Kindly refer logs for further details";
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

}
