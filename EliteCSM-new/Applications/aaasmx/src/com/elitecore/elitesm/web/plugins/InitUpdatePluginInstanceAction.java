package com.elitecore.elitesm.web.plugins;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.util.constants.PluginTypesConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.plugins.forms.UpdatePluginInstanceForm;

public class InitUpdatePluginInstanceAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "InitUpdatePluginInstance";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UNIVERSAL_AUTH_PLUGIN_FORWARD = "universalAuthPlugin";
	private static final String UNIVERSAL_ACCT_PLUGIN_FORWARD = "universalAcctPlugin";
	private static final String UNIVERSAL_DIAMETER_PLUGIN_FORWARD = "universalDiameterPlugin";
	private static final String RADIUS_GROOVY_PLUGIN_FORWARD = "radiusGroovyPlugin";
	private static final String DIAMETER_GROOVY_PLUGIN_FORWARD = "diameterGroovyPlugin";
	private static final String RADIUS_TRANSACTION_LOGGER_FORWARD = "radiusTransactionLogger";
	private static final String DIAMETER_TRANSACTION_LOGGER_FORWARD = "diameterTransactionLogger";
	private static final String QUOTA_MGT_PLUGIN_FORWARD = "quotaManagementPlugin";
	private static final String USER_STATISTIC_POST_AUTH_PLUGIN ="userStatisticPostAuthPlugin";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdatePluginInstanceForm updatePluginInstanceForm = (UpdatePluginInstanceForm) form;

			String pluginInstanceId = updatePluginInstanceForm.getPluginInstanceId();

			if (Strings.isNullOrBlank(pluginInstanceId) == true) {
				pluginInstanceId = request.getParameter("pluginInstanceId");
			}

			PluginBLManager blManager = new PluginBLManager();
			PluginInstData tempPluginInstanceData = blManager.getPluginInstanceData(pluginInstanceId);

			String forwardPath = null;

			if (tempPluginInstanceData.getPluginTypeId().equals(PluginTypesConstants.UNIVERSAL_AUTH_PLUGIN)) {
				forwardPath = mapping.findForward(UNIVERSAL_AUTH_PLUGIN_FORWARD).getPath();
			} else if (tempPluginInstanceData.getPluginTypeId().equals(PluginTypesConstants.UNIVERSAL_ACCT_PLUGIN)) {
				forwardPath = mapping.findForward(UNIVERSAL_ACCT_PLUGIN_FORWARD).getPath();
			} else if (tempPluginInstanceData.getPluginTypeId().equals(PluginTypesConstants.UNIVERSAL_DIAMETER_PLUGIN)) {
				forwardPath = mapping.findForward(UNIVERSAL_DIAMETER_PLUGIN_FORWARD).getPath();
			} else if (tempPluginInstanceData.getPluginTypeId().equals(PluginTypesConstants.RADIUS_GROOVY_PLUGIN)) {
				forwardPath = mapping.findForward(RADIUS_GROOVY_PLUGIN_FORWARD).getPath();
			} else if (tempPluginInstanceData.getPluginTypeId().equals(PluginTypesConstants.DIAMETER_GROOVY_PLUGIN)) {
				forwardPath = mapping.findForward(DIAMETER_GROOVY_PLUGIN_FORWARD).getPath();
			} else if (tempPluginInstanceData.getPluginTypeId().equals(PluginTypesConstants.RADIUS_TRANSACTION_LOGGER)) {
				forwardPath = mapping.findForward(RADIUS_TRANSACTION_LOGGER_FORWARD).getPath();
			} else if (tempPluginInstanceData.getPluginTypeId().equals(PluginTypesConstants.DIAMETER_TRANSACTION_LOGGER)) {
				forwardPath = mapping.findForward(DIAMETER_TRANSACTION_LOGGER_FORWARD).getPath();
			} else if (tempPluginInstanceData.getPluginTypeId().equals(PluginTypesConstants.QUOTA_MANAGEMENT_PLUGIN)) {
				forwardPath = mapping.findForward(QUOTA_MGT_PLUGIN_FORWARD).getPath();
			}else if (tempPluginInstanceData.getPluginTypeId().equals(PluginTypesConstants.USER_STATISTIC_POST_AUTH_PLUGIN)){
				forwardPath = mapping.findForward(USER_STATISTIC_POST_AUTH_PLUGIN).getPath();
			}
			request.getSession().setAttribute("pluginInstance",tempPluginInstanceData);

			/* Clear FormFile Map from Session */
			request.getSession().removeAttribute("formFileMap");
			
			if(forwardPath!=null){
				ActionForward actionForward = new ActionForward();
				actionForward.setPath(forwardPath + "?pluginInstanceId=" + pluginInstanceId);
				return actionForward;
			}
			return mapping.findForward(SUCCESS_FORWARD);
		}catch(Exception e){
				e.printStackTrace();
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}
