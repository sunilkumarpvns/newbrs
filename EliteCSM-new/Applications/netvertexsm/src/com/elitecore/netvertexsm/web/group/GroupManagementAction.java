package com.elitecore.netvertexsm.web.group;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.blmanager.core.system.group.GroupDataBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.base.ICreateAction;
import com.elitecore.netvertexsm.web.core.base.IDeleteAction;
import com.elitecore.netvertexsm.web.core.base.ISearchAction;
import com.elitecore.netvertexsm.web.core.base.IUpdateAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.group.form.GroupDataForm;

public class GroupManagementAction extends BaseWebDispatchAction implements ICreateAction, IUpdateAction, ISearchAction, IDeleteAction{

	private static final String SEARCH_PAGE = "searchGroup";
	private static final String CREATE_PAGE = "createGroup";
	private static final String VIEW_PAGE = "viewGroup";
	private static final String EDIT_PAGE = "editGroup";
	
	
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_GROUP_INFORMATION;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_GROUP_INFORMATION;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_GROUP_INFORMATION;
	private static final String LIST_ACTION_ALIAS = ConfigConstant.LIST_GROUP_INFORMATION;
	
	@Override
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(LogManager.getLogger().isDebugLogLevel()){
			Logger.logDebug(MODULE, "Entered in Delete Method");
		}
		if((checkAccess(request, DELETE_ACTION_ALIAS))){
			try{
				GroupDataBLManager groupDataBLManager = GroupDataBLManager.getInstance();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				String[] groupDataIds = (String[])request.getParameterValues("groupDataIds");
				//check for group association exists with role
				for(String groupId : groupDataIds){
					IRoleData roleData = groupDataBLManager.getRoleDataForGroupData(groupId);
					if(roleData != null){
						throw new ConstraintViolationException("Group is associated with Role: " + roleData.getName());
					}
				}

				groupDataBLManager.delete(groupDataIds,staffData,DELETE_ACTION_ALIAS);

				ActionMessage message = new ActionMessage("group.delete.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/groupManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			} catch (ConstraintViolationException e) {
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("group.delete.failure"));
				messages.add("information", new ActionMessage("group.association.exists"));
				saveErrors(request, messages);

				ActionMessages errorHeadingMessage = new ActionMessages();
				ActionMessage message = new ActionMessage("group.error.heading","deleting");
				errorHeadingMessage.add("errorHeading",message);
				saveMessages(request,errorHeadingMessage);
				return mapping.findForward(FAILURE);
			} catch(Exception e) {
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("group.delete.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("group.error.heading","deleting");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            	            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("group.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	@Override
	public ActionForward initSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(LogManager.getLogger().isDebugLogLevel()){
			Logger.logDebug(MODULE, "Entered in Init Search Method");
		}
		return search( mapping,  form,  request,  response);
	}

	@Override
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(LogManager.getLogger().isDebugLogLevel()){
			Logger.logDebug(MODULE, "Entered in Search Method");
		}
		if(checkAccess(request, LIST_ACTION_ALIAS)) {
			try{
				GroupDataForm groupDataForm = (GroupDataForm)form;
				GroupDataBLManager groupDataBLManager = GroupDataBLManager.getInstance();
				
				GroupData groupData = new GroupData();
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(groupDataForm.getPageNumber()).intValue();
				}
				if (requiredPageNo == 0)
					requiredPageNo = 1;		

				convertFormToBean(groupDataForm, groupData);
	           
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = groupDataBLManager.search(groupData, requiredPageNo, pageSize,staffData, LIST_ACTION_ALIAS);
				
				groupDataForm.setGroupDatas(pageList.getListData());
				groupDataForm.setPageNumber(pageList.getCurrentPage());
				groupDataForm.setTotalPages(pageList.getTotalPages());
				groupDataForm.setTotalRecords(pageList.getTotalItems());
				groupDataForm.setActionName(BaseConstant.LISTACTION);
				
			    request.setAttribute("groupDataForm", groupDataForm);
			    
				return mapping.findForward(SEARCH_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("group.search.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("group.error.heading","searching");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            
			}
			return mapping.findForward(FAILURE);
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);

            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("group.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);            
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}



	@Override
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(LogManager.getLogger().isDebugLogLevel()){
			Logger.logDebug(MODULE, "Entered in Init Update Method");
		}
		if((checkAccess(request,UPDATE_ACTION_ALIAS))){
			GroupDataForm groupDataForm = (GroupDataForm)form;
			
			try{
				GroupDataBLManager groupDataBLManager = GroupDataBLManager.getInstance();
				String groupId = request.getParameter("groupId");
				GroupData groupData = groupDataBLManager.getGroupData(groupId);
				convertBeanToForm(groupDataForm, groupData);
				request.setAttribute("groupData", groupData);
				request.setAttribute("groupDataForm", groupDataForm);
               	return mapping.findForward(EDIT_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("group.update.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("group.error.heading","updating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("group.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);		        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
	}

	

	@Override
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Update Method");
		if((checkAccess(request, UPDATE_ACTION_ALIAS))){
			GroupDataForm groupDataForm = (GroupDataForm)form;
			
			try{
				GroupDataBLManager groupDataBLManager = GroupDataBLManager.getInstance();
				GroupData groupData = new GroupData();
				convertFormToBean(groupDataForm, groupData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				groupDataBLManager.update(groupData,staffData, UPDATE_ACTION_ALIAS);
               	
               	ActionMessage message = new ActionMessage("group.update.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/groupManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("group.update.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("group.error.heading","updating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);		            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("group.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);		                        	       
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in View Method");
		if((checkAccess(request, LIST_ACTION_ALIAS))){
			GroupDataForm groupDataForm = (GroupDataForm)form;
			
			try{
				GroupDataBLManager groupDataBLManager = GroupDataBLManager.getInstance();
				String groupId = request.getParameter("groupId");
				GroupData groupData = groupDataBLManager.getGroupData(groupId);
				convertBeanToForm(groupDataForm, groupData);
				request.setAttribute("groupData", groupData);
				request.setAttribute("groupDataForm", groupDataForm);
				return mapping.findForward(VIEW_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("general.view.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("group.error.heading","viewing");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);			            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("group.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);			        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
	}
	@Override
	public ActionForward initCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Create Method");
		if(checkAccess(request, CREATE_ACTION_ALIAS)){
			GroupDataForm groupForm = (GroupDataForm) form; 
			groupForm.setDescription(getDefaultDescription(request));
			return mapping.findForward(CREATE_PAGE);
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("group.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
		
	}

	@Override
	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Create Method");
		if((checkAccess(request, CREATE_ACTION_ALIAS))){
			GroupDataForm groupDataForm = (GroupDataForm)form;
			
			try{
				GroupDataBLManager groupDataBLManager = GroupDataBLManager.getInstance();
				GroupData groupData = new GroupData();
				convertFormToBean(groupDataForm, groupData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				//TODO need to change the staffData
				//groupData.setCreatedByStaffId(staffData.getStaffId());
				groupData.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				groupDataBLManager.create(groupData,staffData, CREATE_ACTION_ALIAS);
               	
               	ActionMessage message = new ActionMessage("group.create.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/groupManagement.do?method=initSearch");
               	
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
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("group.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	            	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	private void convertFormToBean(GroupDataForm groupDataForm,
			GroupData groupData) {
		groupData.setId(groupDataForm.getId());
	    groupData.setName(groupDataForm.getName());
	    groupData.setDescription(groupDataForm.getDescription());
	    groupData.setCreatedDate(new Timestamp(System.currentTimeMillis()));
	}
	
	private void convertBeanToForm(GroupDataForm groupDataForm,
			GroupData groupData) {
		if(Strings.isNullOrBlank(groupData.getId()) == false){
			groupDataForm.setId(groupData.getId());
		}
		groupDataForm.setName(groupData.getName());
		groupDataForm.setDescription(groupData.getDescription());
	}
	
	private void setCreatedDateAndStaff(HttpServletRequest request, GroupData groupData){
		StaffData staffData = (StaffData) request.getSession().getAttribute("groupData");
		
	}
	

}
