package com.elitecore.elitesm.web.plugins;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.util.constants.PluginTypesConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.plugins.forms.CreatePluginForm;

/**
 * @author nayana.rathod
 *
 */

public class CreatePluginAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD						= "CreatePlugin";
	private static final String MODULE 								= "CREATE_PLUGIN_ACTION";
	
	private static final String UNIVERSAL_AUTH_PLUGIN_FORWARD 		= "createUniversalAuthPlugin";
	private static final String UNIVERSAL_ACCT_PLUGIN_FORWARD 		= "createUniversalAcctPlugin";
	private static final String UNIVERSAL_DIAMETER_PLUGIN_FORWARD 	= "createUniversalDiameterPlugin";
	private static final String RADIUS_GROOVY_PLUGIN_FORWARD 		= "createRadiusGroovyPlugin";
	private static final String DIAMETER_GROOVY_PLUGIN_FORWARD 		= "createDiameterGroovyPlugin";
	private static final String RADIUS_TRANSACTION_LOGGER_FORWARD 	= "createRadiusTransactionLoggerPlugin";
	private static final String DIAMETER_TRANSACTION_LOGGER_FORWARD = "createDiameterTransactionLoggerPlugin";
	private static final String QUOTA_MANAGEMENT_PLUGIN_FORWARD 	= "createQuotaManagementPlugin";
	private static final String USER_STATISTIC_POST_AUTH_PLUGIN    = "createUserStatisticPostAuthPlugin";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		CreatePluginForm pluginForm = (CreatePluginForm)form;
		String action = pluginForm.getAction();
	
		if (action != null) {
			if (action.equals("next")) {
				request.setAttribute("description", pluginForm.getDescription());
				request.setAttribute("pluginTypeId", pluginForm.getSelectedPlugin());
				request.setAttribute("name", pluginForm.getName());
				request.setAttribute("status", pluginForm.getStatus());
			}
		}
		
		if(pluginForm.getSelectedPlugin() != null){
			if(pluginForm.getSelectedPlugin().equals(PluginTypesConstants.UNIVERSAL_AUTH_PLUGIN)){
				return mapping.findForward(UNIVERSAL_AUTH_PLUGIN_FORWARD);
			}else if( pluginForm.getSelectedPlugin().equals(PluginTypesConstants.UNIVERSAL_ACCT_PLUGIN)){
				return mapping.findForward(UNIVERSAL_ACCT_PLUGIN_FORWARD);
			}else if( pluginForm.getSelectedPlugin().equals(PluginTypesConstants.UNIVERSAL_DIAMETER_PLUGIN)){
				return mapping.findForward(UNIVERSAL_DIAMETER_PLUGIN_FORWARD);
			}else if( pluginForm.getSelectedPlugin().equals(PluginTypesConstants.RADIUS_GROOVY_PLUGIN)){
				return mapping.findForward(RADIUS_GROOVY_PLUGIN_FORWARD);
			}else if( pluginForm.getSelectedPlugin().equals(PluginTypesConstants.DIAMETER_GROOVY_PLUGIN)){
				return mapping.findForward(DIAMETER_GROOVY_PLUGIN_FORWARD);
			}else if( pluginForm.getSelectedPlugin().equals(PluginTypesConstants.RADIUS_TRANSACTION_LOGGER)){
				return mapping.findForward(RADIUS_TRANSACTION_LOGGER_FORWARD);
			}else if( pluginForm.getSelectedPlugin().equals(PluginTypesConstants.DIAMETER_TRANSACTION_LOGGER)){
				return mapping.findForward(DIAMETER_TRANSACTION_LOGGER_FORWARD);
			}else if( pluginForm.getSelectedPlugin().equals(PluginTypesConstants.QUOTA_MANAGEMENT_PLUGIN)){
				return mapping.findForward(QUOTA_MANAGEMENT_PLUGIN_FORWARD);
			}else if( pluginForm.getSelectedPlugin().equals(PluginTypesConstants.USER_STATISTIC_POST_AUTH_PLUGIN)){
				return mapping.findForward(USER_STATISTIC_POST_AUTH_PLUGIN);
			}
		}
		return mapping.findForward(SUCCESS_FORWARD);
	}
}