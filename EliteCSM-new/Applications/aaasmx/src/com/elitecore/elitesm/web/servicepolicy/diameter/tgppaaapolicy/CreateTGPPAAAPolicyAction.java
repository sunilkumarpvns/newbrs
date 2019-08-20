package com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.TGPPAAAPolicyForm;


/**
 * @author nayana.rathod
 *
 */

public class CreateTGPPAAAPolicyAction extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "createTGPPAAAPolicy";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "CreateTGPPAAAPolicyAction";
	private static final String ACTION_ALIAS=ConfigConstant.CREATE_TGPP_AAA_SERVICE_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		try{
		
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			checkActionPermission(request, ACTION_ALIAS);
			request.getSession().removeAttribute("tgppAAAPolicyForm");
			
			TGPPAAAPolicyForm tgppAAAPolicyForm = (TGPPAAAPolicyForm)form;
			tgppAAAPolicyForm.setDescription(getDefaultDescription(userName));
			
			ServicePolicyBLManager 	servicePoilcyBLManager   = new ServicePolicyBLManager();
			
			DiameterConcurrencyBLManager concurrencyBLManager=new DiameterConcurrencyBLManager();
			
			List<AuthMethodTypeData> authMethodTypeDataList = servicePoilcyBLManager.getAuthMethodTypeList();
			List<DiameterConcurrencyData> diaConcurrencyInstanceDataList= concurrencyBLManager.getDiameterConcurrencyDataList();
            
            String[] selectedAuthMethodTypes = new String[authMethodTypeDataList.size()];
            List<String> selectedAuthMethodTypesList = new ArrayList<String>(); 
            
            for (int i = 0; i < authMethodTypeDataList.size(); i++) {
				long authTypeId=authMethodTypeDataList.get(i).getAuthMethodTypeId();
            	if(authTypeId != 5){
            		selectedAuthMethodTypesList.add(String.valueOf(authTypeId));
            	}
            	
			}
            selectedAuthMethodTypes=selectedAuthMethodTypesList.toArray(new String[0]); 
            tgppAAAPolicyForm.setSelectedAuthMethodTypes(selectedAuthMethodTypes);
            tgppAAAPolicyForm.setAuthMethodTypeDataList(authMethodTypeDataList);
            tgppAAAPolicyForm.setDiaconcurrencyInstanceDataList(diaConcurrencyInstanceDataList);
			
        	/* Driver Script and External Radius Script */
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
			List<ScriptInstanceData> externalScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT);
		
			tgppAAAPolicyForm.setDriverScriptList(driverScriptList);
			tgppAAAPolicyForm.setExternalScriptList(externalScriptList);
			
			request.getSession().setAttribute("tgppAAAPolicyForm", tgppAAAPolicyForm);
			return mapping.findForward(SUCCESS_FORWARD);

		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}
