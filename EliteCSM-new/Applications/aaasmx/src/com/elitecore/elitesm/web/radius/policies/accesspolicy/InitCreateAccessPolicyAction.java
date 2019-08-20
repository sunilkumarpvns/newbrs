package com.elitecore.elitesm.web.radius.policies.accesspolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.policies.accesspolicy.AccessPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.BaseDatasourceException;
import com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport.DataManagerNotFoundException;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.SearchAccessPolicyForm;

public class InitCreateAccessPolicyAction extends BaseDictionaryAction {
    
    private static final String SUCCESS_FORWARD = "initCreateAccessPolicy";
    private static final String FAILURE_FORWARD = "failure";
    //private static final String UPDATE_ACCESSPOLICY = "updateAccessPolicy";
    private static final String ACTION_ALIAS    = "CREATE_ACCESS_POLICY_ACTION";
    private static final String MODULE    = "InitCreateAccessPolicyAction";
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        try {
            if (!checkAccess(request,ACTION_ALIAS))
                throw new ActionNotPermitedException(ACTION_ALIAS);
            String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
            AccessPolicyBLManager accessPolicyBLManager = new AccessPolicyBLManager();
            SearchAccessPolicyForm searchAccessPolicyForm = (SearchAccessPolicyForm) form;
            searchAccessPolicyForm.setStatus("1");
            searchAccessPolicyForm.setAccessstatus("A");
            searchAccessPolicyForm.setDescription(getDefaultDescription(userName));
            String Action = searchAccessPolicyForm.getAction();
            
            List lstParameterValue = accessPolicyBLManager.getStartWeekDayList();
            request.setAttribute("lstParameterValue", lstParameterValue);
            return mapping.findForward(SUCCESS_FORWARD);
            
        } catch (ActionNotPermitedException e) {
            Logger.logError(MODULE, "Error during Data Manager operation ");
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("general.user.restricted");
            messages.add("information", message);
            
        } catch (DataManagerNotFoundException e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("accesspolicy.datamanager.notfound");
            messages.add("information", message);
            
        } catch (BaseDatasourceException e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE,e);
            this.indetifyUserDefineDatasourceException(e,messages);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("datasource.passed.value",e.getSourceField());
            messages.add("information",message);
            
        } catch (DataManagerException e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);
            
        } catch (Exception e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);
        }
        saveErrors(request, messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
}
