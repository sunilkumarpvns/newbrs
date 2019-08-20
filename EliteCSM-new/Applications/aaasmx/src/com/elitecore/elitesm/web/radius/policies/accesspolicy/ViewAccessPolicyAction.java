package com.elitecore.elitesm.web.radius.policies.accesspolicy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.policies.accesspolicy.AccessPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.BaseDatasourceException;
import com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport.DataManagerNotFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.ViewAccessPolicyForm;

public class ViewAccessPolicyAction extends BaseDictionaryAction {
    
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String LIST_FORWARD    = "viewAccessPolicy";
    private static final String ACTION_ALIAS    = ConfigConstant.VIEW_ACCESS_POLICY_ACTION;
    
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logTrace(MODULE, "Enter the execute method of :" + getClass().getName());
        ActionMessages messages = new ActionMessages();
      
        try {
            if (!checkAccess(request, ACTION_ALIAS)) throw new ActionNotPermitedException(ACTION_ALIAS);
            
                ViewAccessPolicyForm viewAccessPolicyForm = (ViewAccessPolicyForm) form;
                AccessPolicyBLManager blManager = new AccessPolicyBLManager();
                IAccessPolicyData iAccessPolicyData = new AccessPolicyData();
                
                String strAccessPolicyId = request.getParameter("accesspolicyid");
                String accessPolicyId= null;
                if(strAccessPolicyId!=null){
                	accessPolicyId = strAccessPolicyId;
                }
                
                if (Strings.isNullOrBlank(accessPolicyId) == true) {
                    accessPolicyId = viewAccessPolicyForm.getAccessPolicyId();
                }
                
                if (Strings.isNullOrBlank(accessPolicyId) == false) {
                    
                    IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    				String actionAlias = ACTION_ALIAS;
    				
    				iAccessPolicyData=blManager.getAccessPolicyById(accessPolicyId);
    				
                    request.setAttribute("AccessPolicyData", iAccessPolicyData);
                        
                    doAuditing(staffData, actionAlias);
                }
                return mapping.findForward(LIST_FORWARD);
                
            }catch (ActionNotPermitedException e) {
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
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);

                this.indetifyUserDefineDatasourceException(e,messages);
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

                ActionMessage message = new ActionMessage("accesspolicy.view.failure");
                messages.add("information", message);
            }
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
    }
}    
