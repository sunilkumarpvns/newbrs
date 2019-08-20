package com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servicepolicy.diameter.TGPPAAAPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.TGPPAAAPolicyForm;


/**
 * @author nayana.rathod
 *
 */

public class InitUpdateTGPPAAAPolicyFlowAction2 extends BaseWebAction{

	private static final String SUCCESS_FORWARD = "initUpdateTGPPAAAPolicyFlow";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "CreateTGPPAAAPolicyFlowAction";
	private static final String ACTION_ALIAS=ConfigConstant.CREATE_RADIUS_SERVICE_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		try{
			checkActionPermission(request, ACTION_ALIAS);
			
			TGPPAAAPolicyForm createTGPPAAAPolicyForm = (TGPPAAAPolicyForm)form;

			String tgppAAPolicyIdStr = request.getParameter("tgppAAAPolicyId");
			Logger.getLogger().info(MODULE, "tgppAAPolicyIdStr : " + tgppAAPolicyIdStr);
			
			TGPPAAAPolicyBLManager tgppAAAPolicyBLManager = new TGPPAAAPolicyBLManager();
			TGPPAAAPolicyData tgppAAAPolicyData = new TGPPAAAPolicyData();
			
			String tgppAAAPolicyId = createTGPPAAAPolicyForm.getTgppAAAPolicyId();
			Logger.getLogger().info(MODULE, "TGPP AAA Policy Id is  : " + tgppAAAPolicyId);
			
			tgppAAAPolicyData = tgppAAAPolicyBLManager.getTGPPAAAPolicyData(tgppAAAPolicyId);
			
			request.getSession().setAttribute("tgppAAAPolicyData", tgppAAAPolicyData);
			request.setAttribute("createTGPPAAAPolicyForm", createTGPPAAAPolicyForm);
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
