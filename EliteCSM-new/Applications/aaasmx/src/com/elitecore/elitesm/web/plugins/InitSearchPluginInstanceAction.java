package com.elitecore.elitesm.web.plugins;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.plugins.forms.SearchPluginInstanceForm;

/**
 * @author nayana.rathod
 *
 */
public class InitSearchPluginInstanceAction  extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "searchPluginInstance";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_PLUGIN;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
		
		List pluginsList = new ArrayList();
		PluginBLManager pluginBLManager = new PluginBLManager();
		SearchPluginInstanceForm pluginInstanceForm = (SearchPluginInstanceForm)form;
		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		
		List pluginServiceList = pluginBLManager.getListOfAllPluginsServiceData();		
		
		pluginInstanceForm.setAction("nolist");
		pluginInstanceForm.setPluginServiceList(pluginServiceList);
	
		request.getSession().setAttribute("pluginsList",pluginsList);
		request.getSession().setAttribute("searchPluginForm",pluginInstanceForm);
		doAuditing(staffData, ACTION_ALIAS);
		return mapping.findForward(SUCCESS_FORWARD);
	}

}
