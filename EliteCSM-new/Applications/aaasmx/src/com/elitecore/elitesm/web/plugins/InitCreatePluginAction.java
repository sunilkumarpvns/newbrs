package com.elitecore.elitesm.web.plugins;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.IPluginServiceTypeData;
import com.elitecore.elitesm.util.constants.PluginTypesConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.plugins.forms.CreatePluginForm;

public class InitCreatePluginAction extends BaseWebAction {
	
	private static final String SUCCESS_FORWARD = "initCreatePlugin";
	private static final String MODULE = "INIT_CREATE_PLUGIN_ACTION";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
		
		PluginBLManager pluginBLManager = new PluginBLManager();
		CreatePluginForm pluginForm = (CreatePluginForm) form;
		
		boolean isRadiusTransactionLoggerEnabled = pluginBLManager.isTransactionLoggerEnabled(PluginTypesConstants.RADIUS_TRANSACTION_LOGGER);
		boolean isDiameterTransactionLoggerEnabled = pluginBLManager.isTransactionLoggerEnabled(PluginTypesConstants.DIAMETER_TRANSACTION_LOGGER);
		
		List<IPluginServiceTypeData> pluginServiceList = pluginBLManager.getListOfAllPluginsServiceData();	
		
		pluginForm.setPluginServiceList(pluginServiceList);
		pluginForm.setDescription(getDefaultDescription(userName));	
		pluginForm.setRadiusTransationLoggerEnabled(isRadiusTransactionLoggerEnabled);
		pluginForm.setDiameterTransactionLoggerEnabled(isDiameterTransactionLoggerEnabled);
		
		/* Clear FormFile Map from Session */
		request.getSession().removeAttribute("formFileMap");
		
		return mapping.findForward(SUCCESS_FORWARD);
	}

	
}
