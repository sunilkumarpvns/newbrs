package com.elitecore.netvertexsm.web.servergroup;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.blmanager.core.system.group.GroupDataBLManager;
import com.elitecore.netvertexsm.blmanager.servergroup.ServerInstanceGroupBlManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupData;
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
import com.elitecore.netvertexsm.web.servergroup.form.ServerInstanceGroupForm;
import com.elitecore.netvertexsm.web.servergroup.form.ServerInstanceGroupRelationForm;

/**
 * Created by aditya on 11/5/16.
 */
public class ServerGroupManagementAction extends BaseWebDispatchAction implements ICreateAction,IUpdateAction,ISearchAction,IDeleteAction {


    private static final String SEARCH_PAGE = "searchServerGroup";
    private static final String CREATE_PAGE = "createServerGroup";
    private static final String VIEW_PAGE = "viewServerGroup";
    private static final String EDIT_PAGE = "editServerGroup";

    @Override
    public ActionForward initCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Logger.logDebug(MODULE, "Entered in Init Create Method");
    	ServerInstanceGroupForm serverInstanceGroupForm = (ServerInstanceGroupForm)form;
    	Set<GroupData> staffGroups =(Set<GroupData>)request.getSession().getAttribute("STAFF_BELONGING_GROUPS");
    	request.setAttribute("staffBelongingGroupList",staffGroups);
    	return mapping.findForward(CREATE_PAGE);
    }

    @Override
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Logger.logDebug(MODULE, "Entered in Create Method");
    	ServerInstanceGroupForm serverInstanceGroupForm = (ServerInstanceGroupForm) form;
    	try{
    		ServerInstanceGroupBlManager blManager = ServerInstanceGroupBlManager.getInstance();
    		ServerInstanceGroupData serverInstanceGroupData = new ServerInstanceGroupData();
    		String [] groupNamesArray = request.getParameterValues("accessGroups");
    		if(Arrayz.isNullOrEmpty(groupNamesArray)==true){
    			groupNamesArray = new String[1];
    			groupNamesArray[0] = CommonConstants.DEFAULT_GROUP_ID;
    		}
    		StringBuilder stringBuilder = new StringBuilder();
    		for(String string : groupNamesArray){
    			if((stringBuilder.length() != 0)){
    				stringBuilder.append(",");
    			}
    			stringBuilder.append(string);
    		}
    		if(Strings.isNullOrBlank(serverInstanceGroupForm.getAccessGroups())){
    			serverInstanceGroupForm.setAccessGroups(CommonConstants.DEFAULT_GROUP_ID);
    		}	
    		Integer maxOrder = blManager.getMaxOrder();
    		if(maxOrder != null) {
    			serverInstanceGroupData.setOrderNo(maxOrder+1);
    		}else{
    			serverInstanceGroupData.setOrderNo(1);
    		}
    		serverInstanceGroupForm.setGroupNameList(Arrays.asList(serverInstanceGroupForm.getAccessGroups().split(",")));
    		serverInstanceGroupForm.setAccessGroups(stringBuilder.toString());
    		convertFormToBean(serverInstanceGroupForm, serverInstanceGroupData);
    		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    		serverInstanceGroupData.setCreatedByStaff((StaffData) staffData);
    		serverInstanceGroupData.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    		blManager.create(serverInstanceGroupData,staffData);

    		ActionMessage message = new ActionMessage("server.group.create.success");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", message);
    		saveMessages(request,messages);
    		request.setAttribute("responseUrl","/serverGroupManagement.do?method=initSearch");
    		return mapping.findForward(SUCCESS);
    	}catch(Exception e){
    		Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
    		Logger.logTrace(MODULE,e);
    		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    		request.setAttribute("errorDetails", errorElements);
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", new ActionMessage("group.create.failure"));
    		saveErrors(request, messages);

    		ActionMessages errorHeadingMessage = new ActionMessages();
    		ActionMessage message = new ActionMessage("group.error.heading","creating");
    		errorHeadingMessage.add("errorHeading",message);
    		saveMessages(request,errorHeadingMessage);
    		return mapping.findForward(FAILURE);
    	}
    }

    @Override
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	try{
    		ServerInstanceGroupBlManager blManager = ServerInstanceGroupBlManager.getInstance();
    		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    		String serverInstanceGroupIds = request.getParameter("serverGroupId");
    		blManager.delete(serverInstanceGroupIds,staffData);

    		ActionMessage message = new ActionMessage("server.group.delete.success");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", message);
    		saveMessages(request,messages);
    		request.setAttribute("responseUrl","/serverGroupManagement.do?method=initSearch");
    		return mapping.findForward(SUCCESS);
    	}catch(Exception e){
    		Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
    		Logger.logTrace(MODULE,e);
    		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    		request.setAttribute("errorDetails", errorElements);
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", new ActionMessage("server.group.delete.failure"));
    		saveErrors(request, messages);

    		ActionMessages errorHeadingMessage = new ActionMessages();
    		ActionMessage message = new ActionMessage("server.group.error.heading","deleting");
    		errorHeadingMessage.add("errorHeading",message);
    		saveMessages(request,errorHeadingMessage);

    		return mapping.findForward(FAILURE);
    	}
    }

    @Override
    public ActionForward initSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger.logDebug(MODULE, "Entered in Init Search Method");
        return search( mapping,  form,  request,  response);
    }

    @Override
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger.logDebug(MODULE, "Entered in Search Method");
        try{
        	String staffBelongingGroups = (String) request.getSession().getAttribute("STAFF_BELONGING_GROUP_IDS");

            ServerInstanceGroupForm serverInstanceGroupForm = (ServerInstanceGroupForm) form;

            ServerInstanceGroupBlManager blManager = ServerInstanceGroupBlManager.getInstance();
            ServerInstanceGroupData serverInstanceGroupData = new ServerInstanceGroupData();

            Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
            int requiredPageNo;
            if(request.getParameter("pageNo") != null){
                requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
            }else{
                requiredPageNo = new Long(serverInstanceGroupForm.getPageNumber()).intValue();
            }
            if (requiredPageNo == 0)
                requiredPageNo = 1;

            convertFormToBean(serverInstanceGroupForm,serverInstanceGroupData);

            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
            PageList pageList = blManager.search(serverInstanceGroupData, requiredPageNo, pageSize,staffData,staffBelongingGroups);

            serverInstanceGroupForm.setServerInstanceGroupDataList(pageList.getListData());
            List<ServerInstanceGroupRelationForm> serverInstanceGroupRelationDatas = blManager.getServerInstanceGroupRelationDatas();
            for(ServerInstanceGroupData instanceGroupForm : serverInstanceGroupForm.getServerInstanceGroupDataList()){
            	List<ServerInstanceGroupRelationForm> tempServerInstanceGroupRelationDatas = Collectionz.newArrayList();
            	for(ServerInstanceGroupRelationForm serverInstanceGroupRelationForm : serverInstanceGroupRelationDatas){
            		if(instanceGroupForm.getId().equals(serverInstanceGroupRelationForm.getServerGroupId())){
            			tempServerInstanceGroupRelationDatas.add(serverInstanceGroupRelationForm);
            		}
            	}
            	Collections.sort(tempServerInstanceGroupRelationDatas, new Comparator<ServerInstanceGroupRelationForm>() {
        			@Override
        			public int compare(ServerInstanceGroupRelationForm q1, ServerInstanceGroupRelationForm q2) {
        				return Integer.valueOf(q2.getServerWeightage()) - Integer.valueOf(q1.getServerWeightage());
        			}
        		});
            	
            	instanceGroupForm.setServerInstanceGroupRelationForms(tempServerInstanceGroupRelationDatas);
            }
            
            Collections.sort(serverInstanceGroupForm.getServerInstanceGroupDataList(), new Comparator<ServerInstanceGroupData>() {
    			@Override
    			public int compare(ServerInstanceGroupData serverInstanceGroupData1, ServerInstanceGroupData serverInstanceGroupData2) {
    				return Integer.valueOf(serverInstanceGroupData1.getOrderNo()) - Integer.valueOf(serverInstanceGroupData2.getOrderNo());
    			}
    		});
           
            serverInstanceGroupForm.setPageNumber(pageList.getCurrentPage());
            serverInstanceGroupForm.setTotalPages(pageList.getTotalPages());
            serverInstanceGroupForm.setTotalRecords(pageList.getTotalItems());
            serverInstanceGroupForm.setActionName(BaseConstant.LISTACTION);
            request.setAttribute("serverInstanceGroupForm", serverInstanceGroupForm);

            return mapping.findForward(SEARCH_PAGE);
        }catch(Exception e){
            Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
            Logger.logTrace(MODULE,e);
            Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
            request.setAttribute("errorDetails", errorElements);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("network.management.search.failure"));
            saveErrors(request, messages);

            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("network.management.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
        }
        return mapping.findForward(FAILURE);
    }

    private void convertFormToBean(ServerInstanceGroupForm serverInstanceGroupForm, ServerInstanceGroupData serverInstanceGroupData) {
        serverInstanceGroupData.setAccessGroups(serverInstanceGroupForm.getAccessGroups());
        serverInstanceGroupData.setId(serverInstanceGroupForm.getId());
        serverInstanceGroupData.setName(serverInstanceGroupForm.getName());
    }

    private void convertBeanToForm( ServerInstanceGroupData serverInstanceGroupData,ServerInstanceGroupForm serverInstanceGroupForm) {
        serverInstanceGroupForm.setId(serverInstanceGroupData.getId());
        serverInstanceGroupForm.setName(serverInstanceGroupData.getName());
        serverInstanceGroupForm.setAccessGroups(serverInstanceGroupData.getAccessGroups());
        serverInstanceGroupForm.setOrderNo(serverInstanceGroupData.getOrderNo());
        if(Strings.isNullOrBlank(serverInstanceGroupData.getAccessGroups())){
        	serverInstanceGroupData.setAccessGroups(CommonConstants.DEFAULT_GROUP_ID);
		}	
        serverInstanceGroupForm.setGroupNameList(Arrays.asList(serverInstanceGroupData.getAccessGroups().split(",")));
    }


    @Override
    public ActionForward initUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Logger.logDebug(MODULE, "Entered in Init Update Method");
    	ServerInstanceGroupForm serverInstanceGroupForm = (ServerInstanceGroupForm)form;
    	try{
    		String serverGroupId = request.getParameter("serverGroupId");

    		ServerInstanceGroupBlManager blManager = ServerInstanceGroupBlManager.getInstance();
    		ServerInstanceGroupData serverInstanceGroupData = blManager.getServerInstanceGroupData(serverGroupId);
    		convertBeanToForm(serverInstanceGroupData,serverInstanceGroupForm);
    		Set<GroupData> staffGroups =(Set<GroupData>)request.getSession().getAttribute("STAFF_BELONGING_GROUPS");
    		request.setAttribute("staffBelongingGroupList",staffGroups);
    		request.setAttribute("serverInstanceGroupForm", serverInstanceGroupForm);
    		return mapping.findForward(EDIT_PAGE);
    	}catch(Exception e){
    		Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
    		Logger.logTrace(MODULE,e);
    		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    		request.setAttribute("errorDetails", errorElements);
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", new ActionMessage("server.group.update.failure"));
    		saveErrors(request, messages);

    		ActionMessages errorHeadingMessage = new ActionMessages();
    		ActionMessage message = new ActionMessage("server.group.error.heading","updating");
    		errorHeadingMessage.add("errorHeading",message);
    		saveMessages(request,errorHeadingMessage);

    		return mapping.findForward(FAILURE);
    	}
    }

    @Override
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Logger.logDebug(MODULE, "Entered in Update Method");
    	ServerInstanceGroupForm serverInstanceGroupForm = (ServerInstanceGroupForm)form;

    	try{
    		ServerInstanceGroupBlManager blManager= new ServerInstanceGroupBlManager();

    		ServerInstanceGroupData serverInstanceGroupData = new ServerInstanceGroupData();

    		String [] groupNamesArray = request.getParameterValues("accessGroups");
    		if(Arrayz.isNullOrEmpty(groupNamesArray)==true){
    			groupNamesArray = new String[1];
    			groupNamesArray[0] = CommonConstants.DEFAULT_GROUP_ID;
    		}
    		StringBuilder stringBuilder = new StringBuilder();
    		for(String string : groupNamesArray){
    			if((stringBuilder.length() != 0)){
    				stringBuilder.append(",");
    			}
    			stringBuilder.append(string);
    		}
    		if(Strings.isNullOrBlank(serverInstanceGroupForm.getAccessGroups())){
    			serverInstanceGroupForm.setAccessGroups(CommonConstants.DEFAULT_GROUP_ID);
    		}	
    		serverInstanceGroupForm.setGroupNameList(Arrays.asList(serverInstanceGroupForm.getAccessGroups().split(",")));
    		serverInstanceGroupForm.setAccessGroups(stringBuilder.toString());
    		convertFormToBean(serverInstanceGroupForm, serverInstanceGroupData);

    		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    		serverInstanceGroupData.setModifiedByStaff((StaffData) staffData);
    		serverInstanceGroupData.setModifiedDate(new Timestamp(System.currentTimeMillis()));
    		serverInstanceGroupData.setOrderNo(serverInstanceGroupForm.getOrderNo());

    		blManager.update(serverInstanceGroupData,staffData);

    		ActionMessage message = new ActionMessage("server.group.update.success");
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", message);
    		saveMessages(request,messages);
    		request.setAttribute("responseUrl","/serverGroupManagement.do?method=initSearch");
    		return mapping.findForward(SUCCESS);

    	}catch(Exception e){
    		Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
    		Logger.logTrace(MODULE,e);
    		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    		request.setAttribute("errorDetails", errorElements);
    		ActionMessages messages = new ActionMessages();
    		messages.add("information", new ActionMessage("server.group.update.failure"));
    		saveErrors(request, messages);

    		ActionMessages errorHeadingMessage = new ActionMessages();
    		ActionMessage message = new ActionMessage("server.group.error.heading","updating");
    		errorHeadingMessage.add("errorHeading",message);
    		saveMessages(request,errorHeadingMessage);
    		return mapping.findForward(FAILURE);
    	}
    }


    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger.logDebug(MODULE, "Entered in View Method");
        ServerInstanceGroupForm serverInstanceGroupForm = (ServerInstanceGroupForm)form;
        try{
            ServerInstanceGroupBlManager blManager = ServerInstanceGroupBlManager.getInstance();
            String serverGroupId = request.getParameter("serverGroupId");
            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
            ServerInstanceGroupData serverInstanceGroupData = blManager.getServerInstanceGroupData(serverGroupId);
            convertBeanToForm(serverInstanceGroupData,serverInstanceGroupForm);
            GroupDataBLManager groupDataBlManager = GroupDataBLManager.getInstance();
            String groupNames = groupDataBlManager.getGroupNames(Arrays.asList(serverInstanceGroupForm.getAccessGroups().split(",")));
            serverInstanceGroupForm.setGroupNames(groupNames);
            request.setAttribute("serverInstanceGroupForm", serverInstanceGroupForm);
            return mapping.findForward(VIEW_PAGE);
        }catch(Exception e){
            Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
            Logger.logTrace(MODULE,e);
            Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
            request.setAttribute("errorDetails", errorElements);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("server.group.view.failure"));
            saveErrors(request, messages);

            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("server.group.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
            return mapping.findForward(FAILURE);
        }
    }

	public ActionForward changeSessionMode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(Logger.getLogger().isDebugLogLevel()) {
			Logger.logDebug(MODULE, "Entered in changeSessionMode Method");
		}
		try{
			ServerInstanceGroupBlManager blManager = ServerInstanceGroupBlManager.getInstance();
			String serverGroupId = request.getParameter("serverGroupId");
			boolean sessionSyncMode = Boolean.parseBoolean(request.getParameter("sessionSyncMode"));
			//Session sync is disabled, so setting it to false.
			sessionSyncMode = false;
			if(Strings.isNullOrBlank(serverGroupId)== false) {
				ServerInstanceGroupData serverInstanceGroupData = blManager.getServerInstanceGroupData(serverGroupId);

				serverInstanceGroupData.setSessionSynchronization(sessionSyncMode);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				serverInstanceGroupData.setModifiedByStaff((StaffData) staffData);
				serverInstanceGroupData.setModifiedDate(new Timestamp(System.currentTimeMillis()));
				blManager.update(serverInstanceGroupData,staffData);
			}

		}catch(Exception e){
			Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("server.instance.change.wightage.failure"));
			saveErrors(request, messages);

			ActionMessages errorHeadingMessage = new ActionMessages();
			ActionMessage message = new ActionMessage("server.group.error.heading","changing");
			errorHeadingMessage.add("errorHeading",message);
			saveMessages(request,errorHeadingMessage);
		}
		return null;
	}
    /**
     * SwapInstances from primary to secondary and vice versa
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return null
     * @throws Exception
     * @author Dhyani.Raval
     */
    public ActionForward swapInstances(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger.logDebug(MODULE, "Entered in swapInstances Method");
            try{
            	ServerInstanceGroupBlManager blManager = ServerInstanceGroupBlManager.getInstance();
            	String serverInstanceGroupId = request.getParameter("serverInstanceGroupId");
            	blManager.swapInstances(serverInstanceGroupId);
            }catch(Exception e){
                Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
                Logger.logTrace(MODULE,e);
                Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
                request.setAttribute("errorDetails", errorElements);
                ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("server.instance.change.wightage.failure"));
                saveErrors(request, messages);

                ActionMessages errorHeadingMessage = new ActionMessages();
                ActionMessage message = new ActionMessage("group.error.heading","changing");
                errorHeadingMessage.add("errorHeading",message);
                saveMessages(request,errorHeadingMessage);
            }
            return null;
    }
    
    /**
     * Manage order of the server groups 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return null
     * @throws Exception
     * @author Dhyani.Raval
     */
    public ActionForward manageOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger.logDebug(MODULE, "Entered in manageOrder Method");
            try{
            	ServerInstanceGroupBlManager blManager = ServerInstanceGroupBlManager.getInstance();
            	String ids = request.getParameter("serverInstanceGroupIds");
            	String[] serverInstanceGroupIds = Strings.splitter(',').trimTokens().splitToArray(ids);
            	blManager.manageOrder(serverInstanceGroupIds);
            }catch(Exception e){
                Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
                Logger.logTrace(MODULE,e);
                Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
                request.setAttribute("errorDetails", errorElements);
                ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("server.instance.change.wightage.failure"));
                saveErrors(request, messages);

                ActionMessages errorHeadingMessage = new ActionMessages();
                ActionMessage message = new ActionMessage("group.error.heading","changing");
                errorHeadingMessage.add("errorHeading",message);
                saveMessages(request,errorHeadingMessage);
            }
            return null;
    }
}
