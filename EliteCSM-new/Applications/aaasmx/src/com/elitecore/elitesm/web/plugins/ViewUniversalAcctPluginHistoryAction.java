package com.elitecore.elitesm.web.plugins;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.plugins.forms.UpdateUniversalAcctPluginForm;

public class ViewUniversalAcctPluginHistoryAction extends BaseWebAction{
	private static String MODULE = "ViewUniversalDiameterPluginHistoryAction";
	private static String ACTION_ALIAS = ConfigConstant.VIEW_PLUGIN;
	private static String VIEW_HISTORY_FORWARD ="viewUniversalAcctPluginHistory";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest  request, HttpServletResponse response) throws IOException{
		Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
		
		try{
			checkActionPermission(request, ACTION_ALIAS);
			UpdateUniversalAcctPluginForm pluginForm = (UpdateUniversalAcctPluginForm) form;
			String strSmInstancId = request.getParameter("pluginInstanceId");
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String pluginInstanceId;
			if(strSmInstancId!=null){
				pluginInstanceId = strSmInstancId;
			}else{
				pluginInstanceId = pluginForm.getPluginInstanceId();
			}
			
			PluginBLManager pluginBLManager = new  PluginBLManager();
			PluginInstData pluginInstData = pluginBLManager.getPluginInstanceData(pluginInstanceId);
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			
			String strAuditUid = request.getParameter("auditUid");
			String strSytemAuditId=request.getParameter("systemAuditId");
			String name=request.getParameter("name");
			
			if(strSytemAuditId != null){
				request.setAttribute("systemAuditId", strSytemAuditId);
			}
			
			if(pluginInstanceId != null && Strings.isNullOrBlank(strAuditUid) == false){
				
				staffData.setAuditName(pluginInstData.getName());
				staffData.setAuditId(pluginInstData.getAuditUId());
				
				List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
				
				request.setAttribute("name", name);
				request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
			}
			
			Logger.logDebug(MODULE,"pluginInstance: "+pluginInstData);
			request.setAttribute("pluginInstance",pluginInstData);
			request.setAttribute("updateUniversalDiameterPluginForm", pluginForm);
			return mapping.findForward(VIEW_HISTORY_FORWARD);
			
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch(DataManagerException managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("sessionmanager.view.failure"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		}catch(Exception e){
			Logger.logError(MODULE, "Error, reason : " + e.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("sessionmanager.view.failure"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		}
	}
	
}
