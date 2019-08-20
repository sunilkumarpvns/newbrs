package com.elitecore.elitesm.web.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.plugins.forms.SearchPluginInstanceForm;

/**
 * @author nayana.rathod
 *
 */

public class SearchPluginInstanceAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "pluginInstance";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String MODULE = "SearchPluginInstanceAction";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_PLUGIN;
	private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_PLUGIN;
	private static final String UPDATE_STATUS = ConfigConstant.CHANGE_PLUGIN_STATUS;
	PluginBLManager pluginBLManager = new PluginBLManager();
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		String pluginBindedWith = new String();
		String errorType="";
		String actionMessage="plugin.search.failure";
		try {
			checkActionPermission(request, ACTION_ALIAS);
	
			SearchPluginInstanceForm searchPluginInstanceForm = (SearchPluginInstanceForm)form;
			
			String[] pluginInstanceIds = (String[])request.getParameterValues("select");
			List<String> pluginInstanceNamesList = new ArrayList<String>();
			List<String> pluginInstanceNameArrayList = new  ArrayList<String>();
			
			if( pluginInstanceIds != null && pluginInstanceIds.length > 0){
				for( String pluginId : pluginInstanceIds ){
					pluginInstanceNameArrayList.add(pluginId);
				}
			}
			
			List<PluginInstData> pluginInstanceDataList = pluginBLManager.getListOfPluginInstData(pluginInstanceNameArrayList);
			
			if( pluginInstanceDataList != null && pluginInstanceDataList.size() > 0 ){
				for( PluginInstData pluginInstData : pluginInstanceDataList){
					pluginInstanceNamesList.add(pluginInstData.getName());
				}
			}
			
			/* requiredPageNo */
			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(searchPluginInstanceForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			
			if(request.getParameter("resultStatus")!= null){
				searchPluginInstanceForm.setStatus(request.getParameter("resultStatus"));
			}	
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(searchPluginInstanceForm.getAction() != null ){
				
				if((searchPluginInstanceForm.getAction().equals("show"))== false){
					Set<String> policyNames = new LinkedHashSet<String>();
					if (searchPluginInstanceForm.getAction().equals("delete")){
						errorType = "Delete";
					}else if (searchPluginInstanceForm.getAction().equals("hide")) {
						errorType = "Inactive";
					}
					policyNames.add(chechInServerInstance(pluginInstanceNamesList));
					policyNames.add(chechInServerInstanceService(pluginInstanceNamesList));
					policyNames.add(checkInNASpolicy(pluginInstanceNamesList));
					policyNames.add(checkInEAPpolicy(pluginInstanceNamesList));
					policyNames.add(checkInCCpolicy(pluginInstanceNamesList));
					policyNames.add(checkInTGPPpolicy(pluginInstanceNamesList));
					policyNames.add(checkinRadiuspolicy(pluginInstanceNamesList));
					
					if ( policyNames != null && policyNames.isEmpty() == false && policyNames.size() > 1){
						pluginBindedWith = pluginBLManager.getFormatedStringForPlugin(policyNames);
						throw new ConstraintViolationException("Plugin is bound in " + pluginBindedWith + ". You are not allowed to "+ errorType+" this plugin .",null, "ConstraintViolationException");
					}
			
				}
				
				if(searchPluginInstanceForm.getAction().equals("delete")){
					actionMessage="plugin.delete.failure";
					checkActionPermission(request, ACTION_ALIAS_DELETE);

					pluginBLManager.deletePluginById(Arrays.asList(pluginInstanceIds),staffData);
					  
					int strSelectedIdsLen = pluginInstanceIds.length;
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,searchPluginInstanceForm.getPageNumber(),searchPluginInstanceForm.getTotalPages(),searchPluginInstanceForm.getTotalRecords());
					searchPluginInstanceForm.setAction("list");
					request.setAttribute("responseUrl","/searchPlugin.do?name="+searchPluginInstanceForm.getName()+"&pageNo="+currentPageNumber+"&totalPages="+searchPluginInstanceForm.getTotalPages()+"&totalRecords="+searchPluginInstanceForm.getTotalRecords()+"&pluginType="+searchPluginInstanceForm.getSelectedPlugin());
					ActionMessage message = new ActionMessage("plugin.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS_FORWARD);
					
				}else if(searchPluginInstanceForm.getAction().equalsIgnoreCase("show")){
					actionMessage = "plugin.statuschange.failure";
					String actionAlias = UPDATE_STATUS;
					pluginBLManager.updatePluginStatus( Arrays.asList(pluginInstanceIds), BaseConstant.SHOW_STATUS_ID);
					doAuditing(staffData, actionAlias);
				}else if(searchPluginInstanceForm.getAction().equalsIgnoreCase("hide")){
					actionMessage = "plugin.statuschange.failure";
					checkActionPermission(request, UPDATE_STATUS);
					String actionAlias = UPDATE_STATUS;
					
					pluginBLManager.updatePluginStatus( Arrays.asList(pluginInstanceIds), BaseConstant.HIDE_STATUS_ID);
					doAuditing(staffData, actionAlias);
				}
			}
			
			PluginInstData pluginInstanceData = new PluginInstData();
			pluginInstanceData.setStatus("All");
			
			/* Plugin Status */
			if(searchPluginInstanceForm.getStatus() != null){
				if(searchPluginInstanceForm.getStatus().equals("Active")){				
					pluginInstanceData.setStatus("CST01");

				}else if(searchPluginInstanceForm.getStatus().equals("Inactive")){
					pluginInstanceData.setStatus("CST02");
				}
			}
			
			/*name*/
			String strPluginName =request.getParameter("name");
			if(strPluginName != null){
				pluginInstanceData.setName(strPluginName);
			}else if(searchPluginInstanceForm.getName() != null){
				pluginInstanceData.setName(searchPluginInstanceForm.getName());
			}else{
				pluginInstanceData.setName("");
			}
			
			/*plugin type*/
			String strPluginType =request.getParameter("pluginType");
			if(strPluginType != null){
				pluginInstanceData.setPluginTypeId(strPluginType);
			}else if(searchPluginInstanceForm.getSelectedPlugin() != null){
				pluginInstanceData.setPluginTypeId(searchPluginInstanceForm.getSelectedPlugin());
			}else{
				pluginInstanceData.setPluginTypeId("");
			}
			
			PageList pageList = pluginBLManager.search(pluginInstanceData,requiredPageNo,pageSize,staffData);
			
			searchPluginInstanceForm.setPageNumber(pageList.getCurrentPage());
			searchPluginInstanceForm.setTotalPages(pageList.getTotalPages());
			searchPluginInstanceForm.setTotalRecords(pageList.getTotalItems());
			searchPluginInstanceForm.setPluginInstanceDataList(pageList.getListData());
			searchPluginInstanceForm.setName(pluginInstanceData.getName());
			searchPluginInstanceForm.setSelectedPlugin(String.valueOf(pluginInstanceData.getPluginTypeId()));
			
			List serviceList = pluginBLManager.getListOfAllPluginsServiceData();		
			searchPluginInstanceForm.setAction("list");
			searchPluginInstanceForm.setPluginServiceList(serviceList);
			request.getSession().setAttribute("searchPluginForm",searchPluginInstanceForm);
			
		}catch(ActionNotPermitedException e){
			
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
			
		}catch (ConstraintViolationException e) {
			
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message=null;
			if(errorType.equals("Delete")){
				message = new ActionMessage("plugin.delete.failure",pluginBindedWith);
			}
			if(errorType.equals("Inactive")){
				if(pluginBindedWith !=null && pluginBindedWith.isEmpty() == false ){
					message = new ActionMessage("plugin.statuschange.failure",pluginBindedWith);
				}
	        }
			ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);
			
		} catch (DataManagerException e) {
			
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage(actionMessage);
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE_FORWARD);
			
		} 
		return mapping.findForward(LIST_FORWARD);
	}
	/**
	 * This method is used To check whether plugin is binded with NAS policy or not .
	 * @param pluginInstanceNames contains list of plugin name
	 * @return in Which Type policy plugin is binded
	 * @throws DataManagerException
	 */
	private String checkInNASpolicy(final List<String> pluginInstanceNames) throws DataManagerException{
		if(Collectionz.isNullOrEmpty(pluginInstanceNames) == false){
			for(String pluginName : pluginInstanceNames){
					return pluginBLManager.checkPluginBindInNASPolicy(pluginName);
			}
		}
		return "";
	}
	/**
	 * This method is used To check whether plugin is binded with EAP policy or not .
	 * @param pluginInstanceNames contains list of plugin name
	 * @return in Which Type policy plugin is binded
	 * @throws DataManagerException
	 */
	private String checkInEAPpolicy(final List<String> pluginInstanceNames) throws DataManagerException{
		if(Collectionz.isNullOrEmpty(pluginInstanceNames) == false){
			for(String pluginName : pluginInstanceNames){
				return pluginBLManager.checkPluginBindInEAPPolicy(pluginName);
			}
		}
		return "";
	}
	/**
	 * This method is used To check whether plugin is binded with CC policy or not .
	 * @param pluginInstanceNames contains list of plugin name
	 * @return in Which Type policy plugin is binded
	 * @throws DataManagerException
	 */
	private String checkInCCpolicy(final List<String> pluginInstanceNames) throws DataManagerException{
		if(Collectionz.isNullOrEmpty(pluginInstanceNames) == false){
			for(String pluginName : pluginInstanceNames){
				return pluginBLManager.checkPluginBindInCCPolicy(pluginName);
			}
		}
		return "";
	}
	/**
	 * This method is used To check whether plugin is binded with TGPP policy or not .
	 * @param pluginInstanceNames contains list of plugin name
	 * @return in Which Type policy plugin is binded
	 * @throws DataManagerException
	 */
	private String checkInTGPPpolicy(final List<String> pluginInstanceNames) throws DataManagerException{
		if(Collectionz.isNullOrEmpty(pluginInstanceNames) == false){
			for(String pluginName : pluginInstanceNames){
				return pluginBLManager.checkPluginBindInTGPPPolicy(pluginName);
			}
		}
		return "";
	}
	/**
	 * This method is used To check whether plugin is binded with Radius policy or not .
	 * @param pluginInstanceNames contains list of plugin name
	 * @return in Which Type policy plugin is binded
	 * @throws DataManagerException
	 */
	private String checkinRadiuspolicy(final List<String> pluginInstanceNames)throws DataManagerException{
		if(Collectionz.isNullOrEmpty(pluginInstanceNames) == false){
			for(String pluginName : pluginInstanceNames){
				return pluginBLManager.checkPluginBindInRadiusPolicy(pluginName);
			}
		}
		return "";
	}
	
	/**
	 * This method is used To check whether plugin is binded with Server Instance or not .
	 * @param pluginInstanceNames contains list of plugin name
	 * @return where plugin is binded 
	 * @throws DataManagerException
	 */
	private String chechInServerInstance(final List<String> pluginInstanceNames) throws DataManagerException{
		if(Collectionz.isNullOrEmpty(pluginInstanceNames) == false){
			for(String pluginName : pluginInstanceNames){
				return pluginBLManager.checkPluginBindInServerInstance(pluginName);
			}
		}
		return "";
	}
	/**
	 * This method is used To check whether plugin is binded with Configured Service or not .
	 * @param pluginInstanceNames contains list of plugin name
	 * @return where plugin is binded
	 * @throws DataManagerException
	 */
	private String chechInServerInstanceService(final List<String> pluginInstanceNames) throws DataManagerException{
		if(Collectionz.isNullOrEmpty(pluginInstanceNames) == false){
			for(String pluginName : pluginInstanceNames){
				return pluginBLManager.checkPluginBindInServerInstanceServices(pluginName);
			}
		}
		return "";
	}
	
}
