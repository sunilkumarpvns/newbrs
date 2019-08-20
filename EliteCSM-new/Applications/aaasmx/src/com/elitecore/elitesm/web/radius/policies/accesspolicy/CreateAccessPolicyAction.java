package com.elitecore.elitesm.web.radius.policies.accesspolicy;

import java.util.Date;
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
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport.DataManagerNotFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyDetailData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.exception.DuplicateAccessPolicyNameException;
import com.elitecore.elitesm.util.constants.AccessPolicyConstant;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.SearchAccessPolicyForm;

public class CreateAccessPolicyAction extends BaseDictionaryAction {
    
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String LIST_FORWARD    = "createAccessPolicy";
    private static final String ACTION_ALIAS    = ConfigConstant.CREATE_ACCESS_POLICY_ACTION;
    private static final String MODULE          = "CreateAccessPolicyAction";

   
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logTrace(MODULE, "Enter the execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        try {
            if (!checkAccess(request, ACTION_ALIAS))
                throw new ActionNotPermitedException(ACTION_ALIAS);
            
            Date currentDate = new Date();
            
            AccessPolicyData accessPolicyData = new AccessPolicyData();
            
            AccessPolicyBLManager accessPolicyBLManager = new AccessPolicyBLManager();
            SearchAccessPolicyForm searchAccessPolicyForm = (SearchAccessPolicyForm) form;
            String action = searchAccessPolicyForm.getAction();
         
            List lstWeeKDay = accessPolicyBLManager.getStartWeekDayList();
            request.setAttribute("lstWeeKDay", lstWeeKDay);
            action = searchAccessPolicyForm.getAction();
            
            if (action.equalsIgnoreCase("addDetail")) {
            	AccessPolicyDetailData accessPolicyDataTemp = new AccessPolicyDetailData();
            	if(searchAccessPolicyForm.getAccessstatus().equals(AccessPolicyConstant.ALLOWED_VALUE)){
                	accessPolicyDataTemp.setAccessStatus(AccessPolicyConstant.DENIED_VALUE);
                }else{
                	accessPolicyDataTemp.setAccessStatus(AccessPolicyConstant.ALLOWED_VALUE);
                }
            	searchAccessPolicyForm.getLstWeeKDay().add(accessPolicyDataTemp);
                
                return mapping.findForward(LIST_FORWARD);
            } else if (action.equalsIgnoreCase("Remove")) {
                List lstLoginPolicyDetail = searchAccessPolicyForm.getLstWeeKDay();
                lstLoginPolicyDetail.remove(searchAccessPolicyForm.getItemIndex());
                
                return mapping.findForward(LIST_FORWARD);
            } else if (action.equalsIgnoreCase("update")) {
                
                accessPolicyData.setName(searchAccessPolicyForm.getName());
                accessPolicyData.setDescription(searchAccessPolicyForm.getDescription());
                accessPolicyData.setAccessStatus(searchAccessPolicyForm.getAccessstatus());
                String status=request.getParameter("status");
                if(status == null || status.equalsIgnoreCase("")){
                	accessPolicyData.setCommonStatusId(BaseConstant.HIDE_STATUS_ID);
                }else{
                	accessPolicyData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
                }
                
                
                List lstAccessPolicyDetail = (List) searchAccessPolicyForm.getLstWeeKDay();
                
                accessPolicyData.setAccessPolicyDetailDataList(lstAccessPolicyDetail);
                
                accessPolicyBLManager.verifyRadiusPolicyName(searchAccessPolicyForm.getName());
                
                IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
                
				accessPolicyBLManager.createAccessPolicy(accessPolicyData, staffData, false);
				
                request.setAttribute("responseUrl", "/initSearchAccessPolicy");
                ActionMessage message = new ActionMessage("accesspolicy.create.success");
                messages.add("information", message);
                saveMessages(request, messages);
                return mapping.findForward(SUCCESS_FORWARD);
            }
            
        }
        catch (ActionNotPermitedException e) {
            Logger.logError(MODULE, "Error during Data Manager operation ");
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("general.user.restricted");
            messages.add("information", message);
            
        }
        catch (DataManagerNotFoundException e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("accesspolicy.datamanager.notfound");
            messages.add("information", message);
            
        }
        catch (DataValidationException e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message1 = new ActionMessage("invalid.data");
            ActionMessage message2 = new ActionMessage("invalid.data.field", e.getSourceField());
            messages.add("information", message1);
            messages.add("information", message2);
            
        }
        catch (BaseDatasourceException e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            this.indetifyUserDefineDatasourceException(e, messages);
            ActionMessage message = new ActionMessage("datasource.passed.value", e.getSourceField());
            messages.add("information", message);
            
        }
        catch (DuplicateAccessPolicyNameException dapException) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + dapException.getMessage());
            Logger.logTrace(MODULE, dapException);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dapException);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("accesspolicy.create.failure");
            ActionMessage message1 = new ActionMessage("accesspolicy.create.duplicateaccesspolicyname");
            messages.add("information", message);
            messages.add("information", message1);
            
        }
        catch (DataManagerException e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);
            
        }
        catch (Exception e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("general.error");
            messages.add("information", message);
        }
        saveErrors(request, messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
    
}
