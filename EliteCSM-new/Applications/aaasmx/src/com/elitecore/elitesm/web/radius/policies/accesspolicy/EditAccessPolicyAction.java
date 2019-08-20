package com.elitecore.elitesm.web.radius.policies.accesspolicy;


import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport.DataManagerNotFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyDetailData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.exception.DuplicateAccessPolicyNameException;
import com.elitecore.elitesm.util.constants.AccessPolicyConstant;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.EditAccessPolicyForm;

public class EditAccessPolicyAction extends BaseDictionaryAction {
    
    private static final String SUCCESS_FORWARD      = "success";
    private static final String FAILURE_FORWARD      = "failure";
    private static final String LIST_FORWARD         = "editAccessPolicy";
    private static final String LIST_DOWNLOAD        = "downloadAccessPolicy";
    private static final String ACTION_ALIAS         = ConfigConstant.UPDATE_ACCESS_POLICY_ACTION;
 
    public ActionForward execute( ActionMapping mapping , ActionForm form , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logTrace(MODULE, "Enter the execute method of :" + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        try {
            if (!checkAccess(request, ACTION_ALIAS))
                throw new ActionNotPermitedException(ACTION_ALIAS);
            
            Date currentDate = new Date();
            EditAccessPolicyForm editAccessPolicyForm = (EditAccessPolicyForm) form;
            AccessPolicyBLManager blManager = new AccessPolicyBLManager();
            IAccessPolicyData iAccessPolicyData = new AccessPolicyData();
            
          
            List lstWeeKDay = blManager.getStartWeekDayList();
            request.setAttribute("lstWeeKDay", lstWeeKDay);
            String strAccessPolicyId = request.getParameter("accesspolicyid");
            
			String accessPolicyId = null;
            if(strAccessPolicyId!=null){
            	accessPolicyId = strAccessPolicyId;
            }else{
            
                accessPolicyId = editAccessPolicyForm.getAccessPolicyId();
            }
            
            String action = editAccessPolicyForm.getAction();
            
            if (action != null && action.equalsIgnoreCase("list")) {
                if (Strings.isNullOrBlank(accessPolicyId) == false) {
                    editAccessPolicyForm.setAccessPolicyId(accessPolicyId);
                    
                    iAccessPolicyData=blManager.getAccessPolicyById(accessPolicyId);
                    
                    editAccessPolicyForm.setName(iAccessPolicyData.getName());
                    editAccessPolicyForm.setDescription(iAccessPolicyData.getDescription());
                    if (iAccessPolicyData.getCommonStatusId() != null && iAccessPolicyData.getCommonStatusId().equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID)) {
                        editAccessPolicyForm.setStatus(BaseConstant.SHOW_STATUS);
                    } else {
                    	editAccessPolicyForm.setStatus(BaseConstant.HIDE_STATUS);
                    }
                    editAccessPolicyForm.setAssigned("N");
                    editAccessPolicyForm.setAccessstatus(iAccessPolicyData.getAccessStatus());
                    editAccessPolicyForm.setLastUpdated(new Timestamp(new Date().getTime()));
                    
                    editAccessPolicyForm.getLstWeeKDay().addAll(iAccessPolicyData.getAccessPolicyDetailDataList());
                    Collections.sort(editAccessPolicyForm.getLstWeeKDay());
                        
                    request.setAttribute("accessdata", iAccessPolicyData);
                    return mapping.findForward(LIST_FORWARD);
                } else {
                    return mapping.findForward(FAILURE_FORWARD);
                }
            } else if (action.equalsIgnoreCase("addDetail")) {
                
                AccessPolicyDetailData accessPolicyDataTemp = new AccessPolicyDetailData();
          
            	if(editAccessPolicyForm.getAccessstatus().equals(AccessPolicyConstant.ALLOWED_VALUE)){
                	accessPolicyDataTemp.setAccessStatus(AccessPolicyConstant.DENIED_VALUE);
                }else{
                	accessPolicyDataTemp.setAccessStatus(AccessPolicyConstant.ALLOWED_VALUE);
                }
                editAccessPolicyForm.getLstWeeKDay().add(accessPolicyDataTemp);
                request.setAttribute("accessdata", iAccessPolicyData);
                return mapping.findForward(LIST_FORWARD);
                
            } else if (action.equalsIgnoreCase("Remove")) {
                List lstLoginPolicyDetail = editAccessPolicyForm.getLstWeeKDay();
                lstLoginPolicyDetail.remove(editAccessPolicyForm.getItemIndex());
                request.setAttribute("accessdata", iAccessPolicyData);
                return mapping.findForward(LIST_FORWARD);
            } else if (action != null && action.equalsIgnoreCase("update")) {
          
            	iAccessPolicyData.setAccessPolicyId(accessPolicyId);
                iAccessPolicyData.setName(editAccessPolicyForm.getName());
                iAccessPolicyData.setDescription(editAccessPolicyForm.getDescription());
                iAccessPolicyData.setAccessStatus(editAccessPolicyForm.getAccessstatus());
                
                if (editAccessPolicyForm.getStatus() != null && editAccessPolicyForm.getStatus().equalsIgnoreCase("1")) {
                	iAccessPolicyData.setCommonStatusId(BaseConstant.SHOW_STATUS_ID);
                } else {
                	iAccessPolicyData.setCommonStatusId(BaseConstant.HIDE_STATUS_ID);
                }
 
                List lstAccessPolicyDetail = (List) editAccessPolicyForm.getLstWeeKDay();
                Collections.sort(lstAccessPolicyDetail);
 
                iAccessPolicyData.setAccessPolicyDetailDataList(lstAccessPolicyDetail);
                
                blManager.verifyRadiusPolicyName(accessPolicyId, iAccessPolicyData.getName());
                
                IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String actionAlias = ACTION_ALIAS;
				
				blManager.updateAccessPolicyById(iAccessPolicyData, staffData);
				
                request.setAttribute("responseUrl", "/initSearchAccessPolicy");
                ActionMessage message = new ActionMessage("accesspolicy.update.success");
                messages.add("information", message);
                saveMessages(request, messages);
                request.setAttribute("accessdata", iAccessPolicyData);
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
        catch (DuplicateAccessPolicyNameException e) {
            Logger.logError(MODULE, "Error during data Manager operation,reason :" + e.getMessage());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("accesspolicy.update.failure");
            ActionMessage message1 = new ActionMessage("accesspolicy.create.duplicateaccesspolicyname");
            messages.add("error", message);
            messages.add("error", message1);
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

            ActionMessage message = new ActionMessage("accesspolicy.update.failure");
            messages.add("information", message);
        }
        saveErrors(request, messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
}
