package com.elitecore.netvertexsm.web.servermgr.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.GroupInfo;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.netvertexsm.blmanager.core.system.group.GroupDataBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.sprdriver.SPRBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData;
import com.elitecore.netvertexsm.util.EliteAssert;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.base.ICreateAction;
import com.elitecore.netvertexsm.web.core.base.IDeleteAction;
import com.elitecore.netvertexsm.web.core.base.ISearchAction;
import com.elitecore.netvertexsm.web.core.base.IUpdateAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.spr.form.SPRForm;
import com.elitecore.netvertexsm.ws.db.DBConnectionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SPRDataAction extends BaseWebDispatchAction implements ISearchAction,ICreateAction,IUpdateAction,IDeleteAction  {

    private static final String SPR_ERROR_HEADING = "spr.error.heading";
    private static final String ERROR_DETAILS = "errorDetails";
    private static final String INFORMATION = "information";
    private static final String ERROR_HEADING = "errorHeading";
    
    private static final String ID = "id";

    private static final String MODULE = "SPR-DATA";

    private static final String LIST_FORWARD = "searchSPR";
    private static final String CREATE_PAGE = "createSPR";
    private static final String VIEW_PAGE = "viewSPR";
    private static final String EDIT_PAGE = "editSPR";
	private static final String ENTITTY_OLD_GROUPS = "entityOldGroups" ;


	@Override
    public ActionForward delete(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response)
		    throws Exception {
	if(LogManager.getLogger().isDebugLogLevel()){
	    Logger.logDebug(MODULE, "Entered in delete Method of "+getClass().getName());
	}
	if((checkAccessRight(request, ACLAction.DELETE.name()))){
	    try{
		SPRBLManager sprBLManager = new SPRBLManager();
		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

		String[] sprIds =request.getParameterValues(ID);
		
		sprBLManager.delete(sprIds,staffData,ACLAction.DELETE.name());

		ActionMessage message = new ActionMessage("spr.delete.success");
		ActionMessages messages = new ActionMessages();
		messages.add(INFORMATION, message);
		saveMessages(request,messages);
		request.setAttribute("responseUrl","sprData.do?method=initSearch");

		return mapping.findForward(SUCCESS);
	    }catch(Exception e){
		Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
		Logger.logTrace(MODULE,e);
		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
		request.setAttribute(ERROR_DETAILS, errorElements);
		ActionMessages messages = new ActionMessages();
		messages.add(INFORMATION, new ActionMessage("spr.delete.failure"));
		saveErrors(request, messages);
		ActionMessages errorHeadingMessage = new ActionMessages();
		ActionMessage message = new ActionMessage(SPR_ERROR_HEADING,"deleting");
		errorHeadingMessage.add(ERROR_HEADING,message);
		saveMessages(request,errorHeadingMessage);
		return mapping.findForward(FAILURE);
	    }
	}else{
	    Logger.logError(MODULE, "Error during Data Manager operation ");
	    ActionMessage message = new ActionMessage("general.user.restricted");
	    ActionMessages messages = new ActionMessages();
	    messages.add(INFORMATION, message);
	    saveErrors(request, messages);
	    ActionMessages errorHeadingMessage = new ActionMessages();
	    message = new ActionMessage(SPR_ERROR_HEADING,"deleting");
	    errorHeadingMessage.add(ERROR_HEADING,message);
	    saveMessages(request,errorHeadingMessage);
	    return mapping.findForward(INVALID_ACCESS_FORWARD);
	}
    }

    @Override
    public ActionForward initUpdate(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response)
		    throws Exception {
	if(LogManager.getLogger().isDebugLogLevel()){
	    Logger.logDebug(MODULE, "Entered in Init Update Method of "+getClass().getName());
	}

	    SPRForm sprForm = (SPRForm)form;
	    try{

			SPRBLManager sprBLManager = new SPRBLManager();
			String sprId = request.getParameter(ID);
			SPRData sprData = sprBLManager.getSPRData(sprId);

			Set<GroupData> staffGroups = getStaffBelongingGroups(request);

			request.setAttribute("staffBelongingGroupList", getStaffBelongingGroups(request));

			sprForm.setAccessGroups(getStaffBelongingGroupIdList(request));

			SPInterfaceBLManager spInterfaceBLManager = new SPInterfaceBLManager();

			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			DatabaseDSData databaseDSData = databaseDSBLManager.getDatabaseDS(sprData.getDatabaseDSId());

			sprData.setDatabaseDSData(databaseDSData);

			List<DriverInstanceData> spInterfaceList = spInterfaceBLManager.getDriverInstanceList();
			List<DatabaseDSData> databaseDSList = databaseDSBLManager.getDatabaseDSList();


			convertBeanToForm(sprForm, sprData);

			if (Strings.isNullOrBlank(sprData.getGroups())) {
				sprData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
			}


			List<String> sprGroupIDs = CommonConstants.COMMA_SPLITTER.split(sprData.getGroups());
			List<GroupData> sprGroupDataList = GroupDataBLManager.getInstance().getGroupDataFromIds(sprGroupIDs);
			String sprGroupNames = GroupDataBLManager.getInstance().getGroupNamesOrderedByGroupID(sprGroupIDs, sprGroupDataList);
			sprData.setGroups(sprGroupNames);

			sprForm.setGroups(sprData.getGroups());

			List<String> entityGroups = CommonConstants.COMMA_SPLITTER.split(sprData.getGroups());
			sprForm.setGroupNameList(CommonConstants.COMMA_SPLITTER.split(sprData.getGroups()));

			List<GroupInfo> combinedGroupInfoList = GroupInfoSelectionUtil.getCombinedGroupInfoList(staffGroups, entityGroups);
			sprForm.setGroupInfoList(combinedGroupInfoList);


			sprForm.setSelectedGroups(sprData.getGroups());
			sprForm.setSpInterfaceList(spInterfaceList);
			sprForm.setDatabaseList(databaseDSList);
			request.setAttribute("sprData", sprData);
			request.setAttribute("sprForm", sprForm);

			return mapping.findForward(EDIT_PAGE);

		}catch(Exception e){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, new ActionMessage("spr.update.failure"));
			saveErrors(request, messages);
			ActionMessages errorHeadingMessage = new ActionMessages();
			ActionMessage message = new ActionMessage(SPR_ERROR_HEADING, "updating");
			errorHeadingMessage.add(ERROR_HEADING, message);
			saveMessages(request, errorHeadingMessage);
			return mapping.findForward(FAILURE);
		}
	}

	private void convertBeanToForm(SPRForm sprForm,SPRData sprData) {
		//sprForm.setSprId(sprData.getId());
		sprForm.setSprName(sprData.getSprName());
		sprForm.setDescription(sprData.getDescription());
		sprForm.setBatchSize(sprData.getBatchSize());
		if (sprData.getDriverInstanceId() != null) {
			sprForm.setSpInterfaceId(sprData.getDriverInstanceId());
		} else {
			sprForm.setSpInterfaceId(0l);
		}
		sprForm.setDatabaseDSId(sprData.getDatabaseDSId());
		sprForm.setAlternateIdField(sprData.getAlternateIdField());

	}

    @Override
    public ActionForward update(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response)
		    throws Exception {
		if (LogManager.getLogger().isDebugLogLevel()) {
			Logger.logDebug(MODULE, "Entered in Update Method of " + getClass().getName());
		}
		if ((checkAccessRight(request, ACLAction.UPDATE.name()))) {
			SPRForm sprForm = (SPRForm) form;

			try {

				String selectedGroups = sprForm.getSelectedGroups();
				List<String> groups = CommonConstants.COMMA_SPLITTER.split(selectedGroups);

				Map<String, RoleData> groupIdVsStaffGroupRoleRelMap = getGroupIdVsStaffGroupRoleRelMap(request);
				List<String> staffBelongingGroupIds = getStaffBelongingGroupIdList(request);

				String oldGroupsString = request.getParameter(ENTITTY_OLD_GROUPS);

				List<String> oldGroups = CommonConstants.COMMA_SPLITTER.split(oldGroupsString);
				if (Collectionz.isNullOrEmpty(groups)) {
					groups = Collectionz.newArrayList();
					groups.add(CommonConstants.DEFAULT_GROUP_ID);
					selectedGroups = CommonConstants.DEFAULT_GROUP_ID;
				}
				List<String> notAllowedGroupNames = findNotAllowedGroups(request,ACLModules.SUBSCRIBER,ACLAction.UPDATE.name(), staffBelongingGroupIds,groupIdVsStaffGroupRoleRelMap,groups,oldGroups);

				if(Collectionz.isNullOrEmpty(notAllowedGroupNames) == false){
					String groupNames = GroupDataBLManager.getInstance().getGroupNames(notAllowedGroupNames);
					Logger.logError(MODULE, "User doesn't have update rights for groups: " + notAllowedGroupNames.toString());
					ActionMessages messages = new ActionMessages();
					messages.add(INFORMATION, new ActionMessage("spr.update.failure.with.group", groupNames));
					saveErrors(request, messages);
					ActionMessages errorHeadingMessage = new ActionMessages();
					ActionMessage message = new ActionMessage(SPR_ERROR_HEADING, "updating");
					errorHeadingMessage.add(ERROR_HEADING, message);
					saveMessages(request, errorHeadingMessage);
					return mapping.findForward(INVALID_ACCESS_FORWARD);
                   }


				SPRData sprData = new SPRData();
				convertFormToBean(sprForm, sprData);

				SPRBLManager sprBLManager = new SPRBLManager();
				sprData.setGroups(selectedGroups);
				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));

				//sprData.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				// to do need to change or modify staff Data
				//com.elitecore.corenetvertex.pkg.StaffData staff = new com.elitecore.corenetvertex.pkg.StaffData();
				//staff.setStaffId(staffData.getStaffId());
				//sprData.setModifiedByStaff(staff);

				sprBLManager.update(sprData, staffData, ACLAction.UPDATE.name());
				ActionMessage message = new ActionMessage("spr.update.success", sprData.getSprName());
				ActionMessages messages = new ActionMessages();
				messages.add(INFORMATION, message);
				saveMessages(request, messages);
				request.setAttribute("responseUrl", "/sprData.do?method=initSearch");
				return mapping.findForward(SUCCESS);


			} catch (Exception e) {

				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute(ERROR_DETAILS, errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add(INFORMATION, new ActionMessage("spr.update.failure"));
				saveErrors(request, messages);
				ActionMessages errorHeadingMessage = new ActionMessages();
				ActionMessage message = new ActionMessage(SPR_ERROR_HEADING, "updating");
				errorHeadingMessage.add(ERROR_HEADING, message);
				saveMessages(request, errorHeadingMessage);
				return mapping.findForward(FAILURE);
			}
		} else {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveErrors(request, messages);
			ActionMessages errorHeadingMessage = new ActionMessages();
			message = new ActionMessage(SPR_ERROR_HEADING, "updating");
			errorHeadingMessage.add(ERROR_HEADING, message);
			saveMessages(request, errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	@Override
    public ActionForward initCreate(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		if (LogManager.getLogger().isDebugLogLevel()) {
			Logger.logDebug(MODULE, "Entered in Init Create Method of " + getClass().getName());
		}

		request.setAttribute("staffBelongingGroupList", getStaffBelongingGroups(request));
		SPRForm sprForm = (SPRForm) form;
		sprForm.setAccessGroups(getStaffBelongingGroupIdList(request));
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		SPInterfaceBLManager spInterfaceBLManager = new SPInterfaceBLManager();
		List<DriverInstanceData> spInterfaceList = spInterfaceBLManager.getDriverInstanceList();
		List<DatabaseDSData> databaseDSList = databaseDSBLManager.getDatabaseDSList();
		sprForm.setDatabaseList(databaseDSList);
		sprForm.setSpInterfaceList(spInterfaceList);
		sprForm.setDescription(getDefaultDescription(request));
		request.setAttribute("sprForm", sprForm);
		return mapping.findForward(CREATE_PAGE);

	}


    @Override
    public ActionForward create(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (LogManager.getLogger().isDebugLogLevel()) {
			Logger.logDebug(MODULE, "Entered in Create Method of " + getClass().getName());
		}

		try {
			SPRForm sprForm = (SPRForm) form;

			String selectedGroups = sprForm.getSelectedGroups();
			List<String> groups = CommonConstants.COMMA_SPLITTER.split(selectedGroups);
			Map<String, RoleData> groupIdVsStaffGroupRoleRelMap = getGroupIdVsStaffGroupRoleRelMap(request);

			List<String> staffBelongingGroupIds =  getStaffBelongingGroupIdList(request);
			if (Collectionz.isNullOrEmpty(groups)) {
				groups=Collectionz.newArrayList();
				groups.add(CommonConstants.DEFAULT_GROUP_ID);
				selectedGroups=CommonConstants.DEFAULT_GROUP_ID;
			}
			List<String> notAllowedGroupNames  =  findNotAllowedGroups(request,ACLModules.SUBSCRIBER,ACLAction.CREATE.name(),staffBelongingGroupIds,groupIdVsStaffGroupRoleRelMap,groups,Collectionz.<String>newArrayList());

		   if(Collectionz.isNullOrEmpty(notAllowedGroupNames) == false){
			   Logger.logError(MODULE, "User doesn't have create rights for groups: " + notAllowedGroupNames.toString());
			   String groupNames = GroupDataBLManager.getInstance().getGroupNames(notAllowedGroupNames);
			   ActionMessages messages = new ActionMessages();
			   messages.add(INFORMATION, new ActionMessage("spr.create.failure.with.group", groupNames));
			   saveErrors(request, messages);
			   ActionMessages errorHeadingMessage = new ActionMessages();
			   ActionMessage message = new ActionMessage(SPR_ERROR_HEADING, "creating");
			   errorHeadingMessage.add(ERROR_HEADING, message);
			   saveMessages(request, errorHeadingMessage);
			   return mapping.findForward(INVALID_ACCESS_FORWARD);


		   }

			SPRData sprData = new SPRData();
			SPRBLManager sprBLManager = new SPRBLManager();
			convertFormToBean(sprForm, sprData);
			sprData.setGroups(selectedGroups);

			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			//sprData.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			// to do need to change or modify staff Data
			//com.elitecore.corenetvertex.pkg.StaffData staff = new com.elitecore.corenetvertex.pkg.StaffData();
			//staff.setStaffId(staffData.getStaffId());
			//sprData.setCreatedByStaff(staff);
			sprBLManager.create(sprData, staffData, ACLAction.CREATE.name());
			ActionMessage message = new ActionMessage("spr.create.success", sprData.getSprName());
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveMessages(request, messages);
			request.setAttribute("responseUrl", "/sprData.do?method=initSearch");
			return mapping.findForward(SUCCESS);

		} catch (Exception e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, new ActionMessage("spr.create.failure"));
			saveErrors(request, messages);
			ActionMessages errorHeadingMessage = new ActionMessages();
			ActionMessage message = new ActionMessage(SPR_ERROR_HEADING, "creating");
			errorHeadingMessage.add(ERROR_HEADING, message);
			saveMessages(request, errorHeadingMessage);
			return mapping.findForward(FAILURE);
		}
	}

    @Override
    public ActionForward initSearch(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response)
		    throws Exception {
	if(LogManager.getLogger().isDebugLogLevel()){
	    Logger.logDebug(MODULE, "Entered in Init Search Method of "+getClass().getName());
	}
	return search( mapping,  form,  request,  response);
    }

    @Override
    public ActionForward search(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response)
		    throws Exception {
	if(LogManager.getLogger().isDebugLogLevel()){
	    Logger.logDebug(MODULE,"Enter search method of "+getClass().getName());
	}

	try{
	    SPRForm searchSPRForm = (SPRForm) form;
	    SPRBLManager sprBLManager = new SPRBLManager();

	    SPRData sprData = new SPRData();
	    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	    int requiredPageNo;
	    if(request.getParameter("pageNo") != null){
		requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    }else{
		requiredPageNo = new Long(searchSPRForm.getPageNumber()).intValue();
	    }
	    if(requiredPageNo == 0)
		requiredPageNo = 1;

	    String strName = searchSPRForm.getSprName();
	    if(Strings.isNullOrEmpty(strName) == false){
		sprData.setSprName(strName);
	    }else{
		sprData.setSprName("");
		searchSPRForm.setSprName("");
	    }


	    PageList pageList = sprBLManager.search(sprData, requiredPageNo, pageSize);
	    searchSPRForm.setSprDataList(pageList.getListData());
	    searchSPRForm.setPageNumber(pageList.getCurrentPage());
	    searchSPRForm.setTotalPages(pageList.getTotalPages());
	    searchSPRForm.setTotalRecords(pageList.getTotalItems());
	    searchSPRForm.setSprName(sprData.getSprName());
	    searchSPRForm.setAction(BaseConstant.LISTACTION);
	    request.setAttribute("searchSPRForm", searchSPRForm);
	}catch(Exception e){
	    Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
	    Logger.logTrace(MODULE, e);
	    Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
	    request.setAttribute(ERROR_DETAILS, errorElements);
	    ActionMessages messages = new ActionMessages();
	    messages.add(INFORMATION, new ActionMessage("spr.search.failure"));
	    saveErrors(request, messages);

	    ActionMessages errorHeadingMessage = new ActionMessages();
	    ActionMessage message = new ActionMessage(SPR_ERROR_HEADING,"searching");
	    errorHeadingMessage.add(ERROR_HEADING,message);
	    saveMessages(request,errorHeadingMessage);
	}
	return mapping.findForward(LIST_FORWARD);
    }

    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	if(LogManager.getLogger().isDebugLogLevel()){
	    Logger.logDebug(MODULE, "Entered in View Method of " + getClass().getName());
	}
	SPRForm sprForm = (SPRForm) form;
	String groups = (String)request.getSession().getAttribute("STAFF_BELONGING_GROUP_IDS");
	try{
	    SPRBLManager sprBLManager = new SPRBLManager();
	    SPInterfaceBLManager spInterfaceBLManager = new SPInterfaceBLManager();
	    DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
	    String sprId = request.getParameter(ID);
	    SPRData sprData = sprBLManager.getSPRData(sprId);
	    //setting sp interface data
	    if(sprData.getDriverInstanceId() != null){
		DriverInstanceData driverInstance = new DriverInstanceData();
		driverInstance.setDriverInstanceId(sprData.getDriverInstanceId());
		DriverInstanceData driverInstanceData = spInterfaceBLManager.getDriverInstanceData(driverInstance);
		sprData.setDriverInstanceData(driverInstanceData);
		request.setAttribute("driverInstanceData",driverInstance);
	    }
	    //setting database data
	    DatabaseDSData databaseDSData = databaseDSBLManager.getDatabaseDS(sprData.getDatabaseDSId());
	    
	    sprData.setDatabaseDSData(databaseDSData);
	    sprForm.setDatabaseDsData(databaseDSData);
	    convertBeanToForm(sprForm, sprData);
	    
        GroupDataBLManager groupDataBlManager = GroupDataBLManager.getInstance();
        if(Strings.isNullOrBlank(sprData.getGroups())){
        	sprData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
        }

		List<String> groupIds = CommonConstants.COMMA_SPLITTER.split(sprData.getGroups());
		List<GroupData> groupDataList = groupDataBlManager.getGroupDataFromIds(groupIds);
		String groupNames = groupDataBlManager.getGroupNamesOrderedByGroupID(groupIds, groupDataList);

		//sprData.setGroupNames(groupNames);

		sprForm.setGroupNameList(Arrays.asList(sprData.getGroups().split(",")));

	    request.setAttribute("databaseDSData", databaseDSData);
	    request.setAttribute("sprData", sprData);
	    request.setAttribute("sprForm", sprForm);
	    return mapping.findForward(VIEW_PAGE);
	}catch(Exception e){
	    Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
	    Logger.logTrace(MODULE,e);
	    Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
	    request.setAttribute(ERROR_DETAILS, errorElements);
	    ActionMessages messages = new ActionMessages();
	    messages.add(INFORMATION, new ActionMessage("spr.view.failure"));
	    saveErrors(request, messages);
	    ActionMessages errorHeadingMessage = new ActionMessages();
	    ActionMessage message = new ActionMessage(SPR_ERROR_HEADING,"viewing");
	    errorHeadingMessage.add(ERROR_HEADING,message);
	    saveMessages(request,errorHeadingMessage);	            
	    return mapping.findForward(FAILURE);
	}
    }

    private void convertFormToBean(SPRForm sprForm, SPRData sprData){
	//sprData.setId(sprForm.getSprId());
	sprData.setSprName(sprForm.getSprName());
	sprData.setDescription(sprForm.getDescription());
	sprData.setDatabaseDSId(sprForm.getDatabaseDSId());
	sprData.setAlternateIdField(sprForm.getAlternateIdField());
	sprData.setBatchSize(sprForm.getBatchSize());
	if (sprForm.getSpInterfaceId() != 0) {
	    sprData.setDriverInstanceId(sprForm.getSpInterfaceId());
	} else {
	    sprData.setDriverInstanceId(null);
	}
    }

    private boolean checkAccessRight(HttpServletRequest request,String action) throws ActionNotPermitedException{
    	String userName = null;
    	try {
			EliteAssert.notNull(request,"request must be specified.");
			EliteAssert.notNull(action,"Action must be specified.");
	        EliteAssert.valiedWebSession(request);
	        SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
	        EliteAssert.notNull(radiusLoginForm,"radiusLoginForm must be specified in session.");
	        userName = radiusLoginForm.getUserName();
			if (isAdminUser(userName)) {
				return true;
			}
	        IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	        StaffBLManager staffBLManager = new StaffBLManager();
	        List<StaffGroupRoleRelData> staffGroupRoleRelationList = staffBLManager.getStaffGroupRoleRelList(staffData.getStaffId());
			List<String> setOfActions = new ArrayList<String>();
	        if(Collectionz.isNullOrEmpty(staffGroupRoleRelationList) == false){
	        	for(StaffGroupRoleRelData staffGroupRoleRel : staffGroupRoleRelationList){
					setOfActions.add(getSetOfActions(staffGroupRoleRel.getRoleData().getRoleId()));
	        	}
	        }
			if(Collectionz.isNullOrEmpty(setOfActions) == false){
				for(String actionData : setOfActions){
					String actions[] = CommonConstants.COMMA_SPLITTER.splitToArray(actionData);
					for(String availableAction : actions) {
						if (availableAction.equalsIgnoreCase(action)) {
							return true;
	        		 }
	        	 }
	         }
			}
		} catch (Exception e) {
			Logger.logTrace(MODULE,e);
	        throw new ActionNotPermitedException("[ "+ action +" ] is not permitted to [ "+ userName +" ]");
		}
		return false;
         
    }

	private String getSetOfActions(long roleId) throws DataManagerException {
		Connection connection = null;
		PreparedStatement psForSetOfActions = null;
		ResultSet rsForSetOfActions = null;
		String query = "SELECT ACTIONS FROM TBLM_ROLE_MODULE_ACTIONS_REL WHERE MODULE_NAME = '"+ ACLModules.SUBSCRIBER.name()+"' AND ROLEID = "+roleId;
		try {
			connection = DBConnectionManager.getInstance().getSMDatabaseConection();
			if(connection==null){
				throw new DataManagerException("Database connection not found");
			}
			psForSetOfActions = connection.prepareStatement(query);
			rsForSetOfActions = psForSetOfActions.executeQuery();
			while(rsForSetOfActions.next()){
				String actions = rsForSetOfActions.getString("ACTIONS");
				if(Strings.isNullOrBlank(actions) == false){
					return actions;
				}

			}
		} catch (SQLException e) {
			Logger.logError(MODULE, "Error while fetching Actions, Reason: "+e.getMessage());
			Logger.logTrace(MODULE,e);
		} finally{
			DBUtility.closeQuietly(rsForSetOfActions);
			DBUtility.closeQuietly(psForSetOfActions);
			DBUtility.closeQuietly(connection);
			
		}
		return null;
	}
}
