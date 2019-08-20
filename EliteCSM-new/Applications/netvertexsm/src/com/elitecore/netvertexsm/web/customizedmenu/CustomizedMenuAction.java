package com.elitecore.netvertexsm.web.customizedmenu;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.customizedmenu.CustomizedMenuBLManager;
import com.elitecore.netvertexsm.datamanager.customizedmenu.CustomizedMenuData;
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

public class CustomizedMenuAction extends BaseWebDispatchAction implements ICreateAction, IUpdateAction, ISearchAction, IDeleteAction {
	private static final String SEARCH_PAGE = "searchCustomizedMenuPage";
	private static final String CREATE_PAGE = "createCustomizedMenuPage";
	private static final String VIEW_PAGE = "viewCustomizedMenuPage";
	private static final String EDIT_PAGE = "editCustomizedMenuPage";
	private static final String VIEW_URL_PAGE = "viewCustomizedMenuUrl";
	
	
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_CUSTOMIZED_MENU;
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_CUSTOMIZED_MENU;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_CUSTOMIZED_MENU;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_CUSTOMIZED_MENU;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_CUSTOMIZED_MENU;
	
	@Override
	public ActionForward initSearch(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Search Method");
		
		return search( mapping,  form,  request,  response);
	}
	
	@Override
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Search Method");
		if(checkAccess(request, SEARCH_ACTION_ALIAS)) {
			try{
				CustomizedMenuForm customizedMenuForm = (CustomizedMenuForm)form;
				CustomizedMenuBLManager customizedMenuBLManager = new CustomizedMenuBLManager();				
				CustomizedMenuData customizedMenuData = new CustomizedMenuData();
				List<CustomizedMenuData> menuList=customizedMenuBLManager.getCustomizeMenuList();
				List<String> titleList=new ArrayList<String>();
				if(menuList!=null&&!menuList.isEmpty()){
					 for(CustomizedMenuData temp:menuList){
						 titleList.add(temp.getTitle());
					 }
				}
				
			
				customizedMenuForm.setTitleList(titleList);
				
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(customizedMenuForm.getPageNumber()).intValue();
				}
				if (requiredPageNo == 0)
					requiredPageNo = 1;		

				convertFormToBean(customizedMenuForm, customizedMenuData);
	           
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = customizedMenuBLManager.search(customizedMenuData, requiredPageNo, pageSize,staffData, SEARCH_ACTION_ALIAS);
				
				customizedMenuForm.setCustomizedMenuList(pageList.getListData());
				customizedMenuForm.setPageNumber(pageList.getCurrentPage());
				customizedMenuForm.setTotalPages(pageList.getTotalPages());
				customizedMenuForm.setTotalRecords(pageList.getTotalItems());
				customizedMenuForm.setActionName(BaseConstant.LISTACTION);
			    request.setAttribute("customizedMenuForm", customizedMenuForm);
			    
				return mapping.findForward(SEARCH_PAGE);
				
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("customizedmenu.search.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("customizedmenu.error.heading","searching");
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
            message = new ActionMessage("customizedmenu.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);            						
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	
	@Override
	public ActionForward initCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Create Method");		
		if(checkAccess(request, CREATE_ACTION_ALIAS)){
			CustomizedMenuForm customizedMenuForm = (CustomizedMenuForm)form;
			CustomizedMenuBLManager customizedMenuBLManager = new CustomizedMenuBLManager();
			List<CustomizedMenuData> titleList = customizedMenuBLManager.getCustomizeMenuList();
			List<String> titleNameList=new ArrayList<String>();
			if(titleList!=null&&!titleList.isEmpty()){
				 for(CustomizedMenuData temp:titleList){
					 titleNameList.add(temp.getTitle());
				 }
			}
			List<CustomizedMenuData> parentIDList = customizedMenuBLManager.getCustomizedMenuList();
			customizedMenuForm.setTitleList(titleNameList);
			customizedMenuForm.setParentIDList(parentIDList);
			
			
			request.setAttribute("customizedMenuForm", customizedMenuForm);
			return mapping.findForward(CREATE_PAGE);
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("customizedmenu.error.heading","creating");
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
			CustomizedMenuForm customizedMenuForm = (CustomizedMenuForm)form;
			
			try{
				CustomizedMenuBLManager customizedMenuBLManager = new CustomizedMenuBLManager();
				CustomizedMenuData customizedMenuData = new CustomizedMenuData();
				convertFormToBean(customizedMenuForm, customizedMenuData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				customizedMenuBLManager.create(customizedMenuData,staffData, CREATE_ACTION_ALIAS);
               	
               	ActionMessage message = new ActionMessage("customizedmenu.create.success",customizedMenuData.getTitle());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/customizedMenumgmt.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("customizedmenu.create.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("customizedmenu.error.heading","creating");
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
            message = new ActionMessage("customizedmenu.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);            					            	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	@Override
	public ActionForward delete(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Delete Method");
		if((checkAccess(request, DELETE_ACTION_ALIAS))){
			try{
				CustomizedMenuBLManager customizedMenuBLManager = new CustomizedMenuBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				String[] strcustomizedMenuIds = (String[])request.getParameterValues("customizedMenuId");
				Long[] customizedMenuIDS = convertStringIdsToLong(strcustomizedMenuIds);
				List<CustomizedMenuData> menuList = customizedMenuBLManager.getCustomizeMenuList();
				customizedMenuBLManager.delete(customizedMenuIDS,menuList,staffData,DELETE_ACTION_ALIAS);

				ActionMessage message = new ActionMessage("customizedmenu.delete.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/customizedMenumgmt.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				if(e.getMessage().equals("PARENT_IS_NOT_DELETABLE")){
					messages.add("information", new ActionMessage("parent.menu.child.exist"));
				}else{	            
					messages.add("information", new ActionMessage("customizedmenu.delete.failure"));
				}
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("customizedmenu.error.heading","deleting");
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
            message = new ActionMessage("customizedmenu.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);            					            
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	/* (non-Javadoc)
	 * @see com.elitecore.netvertexsm.web.core.base.IUpdateAction#initUpdate(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Update Method");
		if((checkAccess(request,UPDATE_ACTION_ALIAS))){
			CustomizedMenuForm customizedMenuForm = (CustomizedMenuForm)form;
			
			try{
				String title = request.getParameter("title");
				CustomizedMenuBLManager customizedMenuBLManager = new CustomizedMenuBLManager();
				CustomizedMenuData customizedMenuData = customizedMenuBLManager.getCustomizedMenuDetailData(title);
				customizedMenuForm.setTitle(customizedMenuData.getTitle());
				customizedMenuForm.setCustomizedMenuId(customizedMenuData.getCustomizedMenuId());
				customizedMenuForm.setUrl(customizedMenuData.getURL());
				customizedMenuForm.setOpenMethod(customizedMenuData.getOpenMethod());
				customizedMenuForm.setParameters(customizedMenuData.getParameters());
				customizedMenuForm.setParentID(customizedMenuData.getParentID());
				customizedMenuForm.setOrder(customizedMenuData.getOrder());
				customizedMenuForm.setIsContainer(customizedMenuData.getIsContainer());
				List<CustomizedMenuData> parentIDList = customizedMenuBLManager.getCustomizedMenuList(title,customizedMenuForm.getCustomizedMenuId());
				customizedMenuForm.setParentIDList(parentIDList);
				
				
				boolean isHavingChild = false;
				if(customizedMenuData.getIsContainer().equals("Yes")){
					List<CustomizedMenuData> menuList = customizedMenuBLManager.getCustomizeMenuList();
					isHavingChild = isHavingChild(menuList, customizedMenuData.getCustomizedMenuId());
				}
				request.setAttribute("disableContainer", isHavingChild);
				//convertBeanToForm(customizedMenuForm, customizedMenuData);
				request.setAttribute("customizedMenuData", customizedMenuData);
				request.setAttribute("customizedMenuForm", customizedMenuForm);
               	return mapping.findForward(EDIT_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("customizedmenu.update.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("customizedmenu.error.heading","updating");
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
            message = new ActionMessage("customizedmenu.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);            					            
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
	}

	@Override
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Update Method");
		if((checkAccess(request, UPDATE_ACTION_ALIAS))){
			CustomizedMenuForm customizedMenuForm = (CustomizedMenuForm)form;
			
			try{
				CustomizedMenuBLManager customizedMenuBLManager = new CustomizedMenuBLManager();
				CustomizedMenuData customizedMenuData = new CustomizedMenuData();
				convertFormToBean(customizedMenuForm, customizedMenuData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				customizedMenuBLManager.update(customizedMenuData,staffData, UPDATE_ACTION_ALIAS);
               	
               	ActionMessage message = new ActionMessage("customizedmenu.update.success",customizedMenuData.getTitle());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/customizedMenumgmt.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("customizedmenu.update.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("customizedmenu.error.heading","updating");
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
            message = new ActionMessage("customizedmenu.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);            					            
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in View Method");
		if((checkAccess(request, VIEW_ACTION_ALIAS))){
			CustomizedMenuForm customizedMenuForm = (CustomizedMenuForm)form;
			
			try{
				CustomizedMenuBLManager customizedMenuBLManager = new CustomizedMenuBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				CustomizedMenuData customizedMenuData = customizedMenuBLManager.getCustomizedMenuData(customizedMenuForm.getTitle(),staffData,VIEW_ACTION_ALIAS);
				convertBeanToForm(customizedMenuForm, customizedMenuData);
				request.setAttribute("customizedMenuData", customizedMenuData);
				request.setAttribute("customizedMenuForm", customizedMenuForm);
				return mapping.findForward(VIEW_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("customizedmenu.view.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("customizedmenu.error.heading","viewing");
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
            message = new ActionMessage("customizedmenu.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
	}
	
	private void convertBeanToForm(CustomizedMenuForm form,CustomizedMenuData data){
		form.setTitle(data.getTitle());
		form.setUrl(data.getURL());
		form.setOpenMethod(data.getOpenMethod());
		form.setParameters(data.getParameters());
		form.setParentID(data.getParentID());
		form.setOrder(data.getOrder());
		form.setIsContainer(data.getIsContainer());
		}
	
	private void convertFormToBean(CustomizedMenuForm form,CustomizedMenuData data){
		data.setTitle(form.getTitle());
		data.setIsContainer(form.getIsContainer());
		data.setURL(form.getUrl());
		data.setOpenMethod(form.getOpenMethod());
		
		
		if(form.getParameters() != null && !form.getParameters().equalsIgnoreCase("0")){
			data.setParameters(form.getParameters());
		}
		
		if(form.getParentID()>= 0 ){
			data.setParentID(form.getParentID());
		}
		if(form.getOrder()>= 0 ){
			data.setOrder(form.getOrder());
		}
		
		if(form.getCustomizedMenuId()>=0){
			data.setCustomizedMenuId(form.getCustomizedMenuId());
		}	
		
	}

	public ActionForward viewURL(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in ViewURL Method");
		if((checkAccess(request, VIEW_ACTION_ALIAS))){
			
			try{
				String customizedMenuURL = (String) request.getParameter("customizedMenuURL");
				request.setAttribute("customizedMenuURL",customizedMenuURL);
				Logger.logDebug(MODULE, "customizedMenuURL : "+customizedMenuURL);				
				Logger.logDebug(MODULE, "FORWARDING TO : "+VIEW_URL_PAGE);
				return mapping.findForward(VIEW_URL_PAGE);
				
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("customizedmenu.view.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("customizedmenu.error.heading","viewing");
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
            message = new ActionMessage("customizedmenu.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
	}
	private boolean isHavingChild(List<CustomizedMenuData> menuList , long currentMenuId){
		
		for(CustomizedMenuData customizedMenuData : menuList){
			          if(customizedMenuData.getParentID() == currentMenuId){
			        	 return true;
			          }
		}
		return false;
	}
}
