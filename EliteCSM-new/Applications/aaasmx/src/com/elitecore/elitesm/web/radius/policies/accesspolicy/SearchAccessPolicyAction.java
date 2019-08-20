package com.elitecore.elitesm.web.radius.policies.accesspolicy;

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
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData;
import com.elitecore.elitesm.util.constants.AccessPolicyConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.SearchAccessPolicyForm;

public class SearchAccessPolicyAction extends BaseDictionaryAction {
    
    private static final String SUCCESS_FORWARD = "success";
    private static final String LIST_FORWARD    = "accessPolicySearchList";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS    = ConfigConstant.SEARCH_ACCESS_POLICY_ACTION;
    
    public ActionForward execute( ActionMapping mapping , ActionForm actionForm , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        
        Logger.logTrace(MODULE, "Enter Execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        try {
            if (!checkAccess(request, ACTION_ALIAS))
                throw new ActionNotPermitedException(ACTION_ALIAS);
            
            SearchAccessPolicyForm searchAccessPolicyForm = (SearchAccessPolicyForm) actionForm;
            AccessPolicyBLManager blManager = new AccessPolicyBLManager();
            Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
            int requiredPageNo;
            if(request.getParameter("pageNo") != null){
    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
    		}else{
    			requiredPageNo = new Long(searchAccessPolicyForm.getPageNumber()).intValue();
    		}
            if (requiredPageNo == 0)
                requiredPageNo = 1;
            
            IAccessPolicyData accessPolicySearchData = new AccessPolicyData();
            
            String strName = searchAccessPolicyForm.getName();
            if (strName != null)
                accessPolicySearchData.setName("%" + strName + "%");
            else
                accessPolicySearchData.setName("");
            Logger.logDebug(MODULE,"STATUS IS:"+searchAccessPolicyForm.getStatus());
            if(request.getParameter("resultStatus")!= null){
    			searchAccessPolicyForm.setStatus(request.getParameter("resultStatus"));
    		}
            if (searchAccessPolicyForm.getStatus() != null) {
                if (searchAccessPolicyForm.getStatus().equalsIgnoreCase("Show"))
                    accessPolicySearchData.setCommonStatusId("CST01");
                else if (searchAccessPolicyForm.getStatus().equalsIgnoreCase("Hide"))
                    accessPolicySearchData.setCommonStatusId("CST02");
                
            }
            
            
            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias = ACTION_ALIAS;
			
            PageList pageList = blManager.search(accessPolicySearchData, staffData, requiredPageNo, pageSize);
            
            searchAccessPolicyForm.setName(strName);
            searchAccessPolicyForm.setPageNumber(pageList.getCurrentPage());
            searchAccessPolicyForm.setTotalPages(pageList.getTotalPages());
            searchAccessPolicyForm.setTotalRecords(pageList.getTotalItems());
            searchAccessPolicyForm.setListAccessPolicy(pageList.getListData());
            searchAccessPolicyForm.setAction(AccessPolicyConstant.LISTACTION);
            
            return mapping.findForward(LIST_FORWARD);
            
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
        catch (BaseDatasourceException e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            this.indetifyUserDefineDatasourceException(e, messages);
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

            ActionMessage message = new ActionMessage("accesspolicy.search.failure");
            messages.add("information", message);
        }
        saveErrors(request, messages);
        return mapping.findForward(FAILURE_FORWARD);
    }
}
