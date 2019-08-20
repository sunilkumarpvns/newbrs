package com.elitecore.elitesm.web.radius.policies.accesspolicy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
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
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.util.constants.AccessPolicyConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.MiscAccessPolicyForm;
import com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.SearchAccessPolicyForm;

public class MiscAccessPolicyAction extends BaseDictionaryAction {
    
    private static final String SUCCESS_FORWARD     = "success";
    private static final String LIST_FORWARD        = "accessPolicySearchList";
    private static final String FAILURE_FORWARD     = "failure";
    private static final String ACTION_ALIAS        =ConfigConstant.CHANGE_ACCESS_POLICY_STATUS_ACTION;
    private static final String ACTION_ALIAS_DELETE = ConfigConstant.DELETE_ACCESS_POLICY_ACTION;
    
    public ActionForward execute( ActionMapping mapping , ActionForm actionForm , HttpServletRequest request , HttpServletResponse response ) throws Exception {
        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
        ActionMessages messages = new ActionMessages();
        
        try {
          
                MiscAccessPolicyForm miscAccessPolicyForm = (MiscAccessPolicyForm) actionForm;
                IAccessPolicyData iAccessPolicyData = new AccessPolicyData();
                miscAccessPolicyForm.setStatus("All");
                
                AccessPolicyBLManager blManager = new AccessPolicyBLManager();
                String accessPolicyId = miscAccessPolicyForm.getAccessPolicyId();
                
                if (miscAccessPolicyForm.getAction() != null) {
                	List<String> listSelectedIDs = new ArrayList<String>();
                    String[] strSelectedIds = request.getParameterValues("select");
                    
                    if (miscAccessPolicyForm.getAction().equalsIgnoreCase("show")) {
                    	checkActionPermission(request, ACTION_ALIAS);
                    	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
        				String actionAlias = ACTION_ALIAS;
                    	
                        blManager.updateStatus(listSelectedIDs, AccessPolicyConstant.SHOW_STATUS_ID);
                        doAuditing(staffData, actionAlias);
                    } else if (miscAccessPolicyForm.getAction().equalsIgnoreCase("hide")) {
                    	checkActionPermission(request, ACTION_ALIAS);
                    	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
        				String actionAlias = ACTION_ALIAS;
                    	
                        blManager.updateStatus(listSelectedIDs, AccessPolicyConstant.HIDE_STATUS_ID);
                        doAuditing(staffData, actionAlias);
                    } else if (miscAccessPolicyForm.getAction().equalsIgnoreCase("delete")) {
                    	checkActionPermission(request, ACTION_ALIAS_DELETE);
                    	IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
        				String actionAlias = ACTION_ALIAS_DELETE;
        				
        				blManager.deleteAccessPolicyById(Arrays.asList(strSelectedIds), staffData);
        				
                        int strSelectedIdsLen = strSelectedIds.length;
                        long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen, miscAccessPolicyForm.getPageNumber(), miscAccessPolicyForm.getTotalPages(), miscAccessPolicyForm.getTotalRecords());
                    	String status = "All";
						
						request.setAttribute("responseUrl","/searchAccessPolicy.do?name="+miscAccessPolicyForm.getName()+"&pageNumber="+currentPageNumber+"&totalPages="+miscAccessPolicyForm.getTotalPages()+"&totalRecords="+miscAccessPolicyForm.getTotalRecords()+"&status="+status);
						ActionMessage message = new ActionMessage("radius.accesspolicy.delete.success");
						ActionMessages messages1 = new ActionMessages();
						messages1.add("information",message);
						saveMessages(request,messages1);
						
						System.out.println("MiscAccess Policy Action");
						return mapping.findForward(SUCCESS_FORWARD);
                    }
                }
            
                if (miscAccessPolicyForm.getAction().equalsIgnoreCase("download")) {
                    List lstAccesspolicies = null;
                    if (Strings.isNullOrBlank(accessPolicyId) == true)
                        accessPolicyId = miscAccessPolicyForm.getAccessPolicyId();
                    if (Strings.isNullOrBlank(accessPolicyId) == false) {
                        iAccessPolicyData.setAccessPolicyId(accessPolicyId);
                        lstAccesspolicies = blManager.getList(iAccessPolicyData);
                    }
                    download(request, response, (IAccessPolicyData) lstAccesspolicies.get(0));
                }
            
                SearchAccessPolicyForm searchAccessPolicyForm = new SearchAccessPolicyForm();
                searchAccessPolicyForm.setName(miscAccessPolicyForm.getName());
                searchAccessPolicyForm.setPageNumber(miscAccessPolicyForm.getPageNumber());
                searchAccessPolicyForm.setTotalPages(miscAccessPolicyForm.getTotalPages());
                searchAccessPolicyForm.setTotalRecords(miscAccessPolicyForm.getTotalRecords());
                searchAccessPolicyForm.setAction(AccessPolicyConstant.LISTACTION);
                searchAccessPolicyForm.setStatus("All");
                request.setAttribute("searchAccessPolicyForm", searchAccessPolicyForm);
                
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
    
    private void download( HttpServletRequest request , HttpServletResponse response , IAccessPolicyData accessPolicyData ) {
        ServletOutputStream out = null;
        Collection colAccessPolicyDetailData = null;
        Iterator iteColAccessPolicyDetailData = null;
        AccessPolicyDetailData accessPolicyDetailData = null;
        String strFileData = null;
        String strDatePattern = "hh:mm";
        SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
        int count = 1;
        try {
            response.setHeader("Content-Disposition", "attachment;filename=\"AccessPolicySample.csv\"");
            out = response.getOutputStream();
            
            if (Strings.isNullOrBlank(accessPolicyData.getAccessPolicyId()) == false)
                out.println(AccessPolicyConstant.SERIALNO + "," + AccessPolicyConstant.DAY + "," + AccessPolicyConstant.STARTTIME + "," + AccessPolicyConstant.ENDTIME + "," + AccessPolicyConstant.ACCESS);
            
            colAccessPolicyDetailData = accessPolicyData.getAccessPolicyDetailDataList();
            
            if (colAccessPolicyDetailData != null && colAccessPolicyDetailData.isEmpty() == false) {
                
                iteColAccessPolicyDetailData = colAccessPolicyDetailData.iterator();
                
                while (iteColAccessPolicyDetailData.hasNext()) {
                    accessPolicyDetailData = (AccessPolicyDetailData) iteColAccessPolicyDetailData.next();
                    
                    if (accessPolicyDetailData.getAccessStatus().equalsIgnoreCase("D"))
                        accessPolicyDetailData.setAccessStatus("Denied");
                    else if (accessPolicyDetailData.getAccessStatus().equalsIgnoreCase("A"))
                        accessPolicyDetailData.setAccessStatus("Allowed");
                    
                    strFileData = (count + "," +
                    dateForm.format(accessPolicyDetailData.getStartTime()) + "," + dateForm.format(accessPolicyDetailData.getStopTime()) + "," + accessPolicyDetailData.getAccessStatus());
                    
                    count = count + 1;
                    out.println(strFileData);
                }
                out.println("");
            }
            out.println("");
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
