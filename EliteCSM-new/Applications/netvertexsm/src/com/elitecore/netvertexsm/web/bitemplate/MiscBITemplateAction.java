package com.elitecore.netvertexsm.web.bitemplate;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.bitemplate.BITemplateBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.BaseDatasourceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.environmentsupport.DataManagerNotFoundException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.AccessPolicyConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.bitemplate.form.BITemplateForm;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class MiscBITemplateAction extends BaseWebAction {
	
	private static final String SUCCESS_FORWARD     = "success";
    private static final String LIST_FORWARD        = "searchBITemplate";
    private static final String FAILURE_FORWARD     = "failure";
    private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_BICEA_TEMPLATE;
    private static final String MODULE = "BI-TEMPLATE";
    
    public ActionForward execute( ActionMapping mapping , ActionForm actionForm , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        try {
         	BITemplateForm miscBIForm = (BITemplateForm) actionForm;
        	BITemplateBLManager biBLManager = new BITemplateBLManager();
            
            if (miscBIForm.getAction() != null) {
            	
            	List<Long> biTemplateIdList = new ArrayList<Long>();
				String[] biTemplateIds= request.getParameterValues("select"); 
				for(int i=0; i<biTemplateIds.length; i++) {
					biTemplateIdList.add(Long.parseLong(biTemplateIds[i]));
				}
				
//                if (miscBIForm.getAction().equalsIgnoreCase("delete")) {
                	checkActionPermission(request, ACTION_ALIAS_DELETE);
    				String actionAlias = ACTION_ALIAS_DELETE;
    				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    				biBLManager.deleteBITemplate(biTemplateIdList, staffData,actionAlias);
                   
//                    int strSelectedIdsLen = biTemplateIds.length;
//                    long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen, miscBIForm.getPageNumber(), miscBIForm.getTotalPages(),miscBIForm.getTotalRecords());
					
                    ActionMessage message = new ActionMessage("bitemplate.delete.success");
    	            ActionMessages messages1 = new ActionMessages();
    	            messages1.add("information", message);
    	            saveMessages(request,messages1);
    	            
    				request.setAttribute("responseUrl","/searchBITemplate.do");
					return mapping.findForward(SUCCESS_FORWARD);
//                }
            }
            BITemplateForm searBITemplateForm = new BITemplateForm();
                    
            searBITemplateForm.setPageNumber(miscBIForm.getPageNumber());
            searBITemplateForm.setTotalPages(miscBIForm.getTotalPages());
            searBITemplateForm.setTotalRecords(miscBIForm.getTotalRecords());
            searBITemplateForm.setAction(AccessPolicyConstant.LISTACTION);
            request.setAttribute("biTemplateForm", searBITemplateForm);
            
            return mapping.findForward(LIST_FORWARD);
            
        }catch (ActionNotPermitedException e) {
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
            this.indetifyUserDefineDatasourceException(e, messages);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("datasource.passed.value", e.getSourceField());
            messages.add("information", message);
            
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
        ActionMessages errorHeadingMessage = new ActionMessages();
        ActionMessage message = new ActionMessage("bitemplate.error.heading","deleting");
        errorHeadingMessage.add("errorHeading",message);
        saveMessages(request,errorHeadingMessage);	
        saveErrors(request, messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
}
