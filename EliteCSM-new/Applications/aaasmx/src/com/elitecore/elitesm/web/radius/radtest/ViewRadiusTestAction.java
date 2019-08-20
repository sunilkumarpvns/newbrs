package com.elitecore.elitesm.web.radius.radtest;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.radtest.RadiusTestBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.radtest.forms.ViewRadiusTestForm;


public class ViewRadiusTestAction extends BaseWebAction{
    
        private static final String SUCCESS_FORWARD = "success";               
        private static final String FAILURE_FORWARD = "failure";               
        private static final String MODULE ="ViewRadiusTestAction";
        private static final String VIEW_FORWARD = "viewRadiusPacket";
        private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.VIEW_RADIUS_PACKET;
        private static final String[] serviceType = {"Authentication Request","Accounting Start","Accounting Stop","Accounting Update","Accounting On","Accounting Off","Status Server","User Define"}; 
        
        public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
            Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());

            ViewRadiusTestForm viewRadiusTestForm = (ViewRadiusTestForm)form;
            RadiusTestBLManager radiusBLManager = new RadiusTestBLManager();
            ActionMessages messages = new ActionMessages();
    		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
    		
            try {
            	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
                String strRadiusTestId = request.getParameter("fieldId");
                request.getSession().setAttribute("fieldId", strRadiusTestId);
                String radiusTestId = strRadiusTestId;
                if(Strings.isNullOrBlank(radiusTestId) == false) {
                    IRadiusTestData radiusTestData = radiusBLManager.getRadiusObjById(radiusTestId);
                    if(radiusTestData != null) {
                        this.beanToForm(radiusTestData, viewRadiusTestForm);
                        if(radiusTestData.getHostAddress()== null){ 
                            //String hostAddress= InetAddress.getLocalHost().getHostAddress();
                            viewRadiusTestForm.setHostAddress("-");
                            
                        }else{
                        	
                        	String[] clientInterface = radiusTestData.getHostAddress().split(":");
                        	Logger.logDebug(MODULE,"client Interface size is:"+clientInterface.length);
                        	if(clientInterface.length == 2){
                        		if("".equals(clientInterface[0])){                        		
                        		viewRadiusTestForm.setHostAddress("-");
                        		}else{
                        			viewRadiusTestForm.setHostAddress(clientInterface[0]);
                        		}
                        		viewRadiusTestForm.setHostPort(Integer.parseInt(clientInterface[1]));
                        		
                        	}else if(clientInterface.length == 1){
                        		viewRadiusTestForm.setHostAddress(clientInterface[0]); 
                        	}
                        	
                        	
                        }
                        request.setAttribute("radParamList", viewRadiusTestForm.getRadParamRel());
                    }
                    doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                }
                             
            }catch(ActionNotPermitedException e){
                Logger.logError(MODULE,"Error :-" + e.getMessage());
                printPermitedActionAlias(request);
                messages.add("information", new ActionMessage("general.user.restricted"));
                saveErrors(request, messages);
                return mapping.findForward(INVALID_ACCESS_FORWARD);
            }catch(DataManagerException e) {
                Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
                Logger.logTrace(MODULE, e);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);

                ActionMessage message = new ActionMessage("radius.radtest.datasource.failed");
                messages.add("information", message);
                saveErrors(request, messages);
                return mapping.findForward(FAILURE_FORWARD);
                
            } catch(Exception e) {
                Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
                Logger.logTrace(MODULE, e);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);

                ActionMessage message = new ActionMessage("general.error");
                messages.add("information", message);   
                saveErrors(request, messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
            return mapping.findForward(VIEW_FORWARD); 
        }
        
        private void beanToForm(IRadiusTestData radiusTestData ,ViewRadiusTestForm viewRadiusTestForm) {
            viewRadiusTestForm.setNtradId(radiusTestData.getNtradId());
            viewRadiusTestForm.setName(radiusTestData.getName());
            viewRadiusTestForm.setAdminHost(radiusTestData.getAdminHost());
            viewRadiusTestForm.setAdminPort(radiusTestData.getAdminPort());
            viewRadiusTestForm.setReTimeOut(radiusTestData.getReTimeOut());
            viewRadiusTestForm.setRetries(radiusTestData.getRetries());
            viewRadiusTestForm.setScecretKey(radiusTestData.getScecretKey());
            viewRadiusTestForm.setUserName(radiusTestData.getUserName());
            viewRadiusTestForm.setUserPassword(radiusTestData.getUserPassword());
            viewRadiusTestForm.setIsChap(radiusTestData.getIsChap());
            
            for(int i = 0;i<serviceType.length;i++) {
                if((radiusTestData.getRequestType()-1) == i) {
                    viewRadiusTestForm.setServiceType(serviceType[i]);
                    break;
                } else {
                    viewRadiusTestForm.setServiceType(serviceType[serviceType.length -1]);
                }
            }
            viewRadiusTestForm.setRequestType(radiusTestData.getRequestType());
            viewRadiusTestForm.setRadParamRel(radiusTestData.getRadParamRel());
        }
}
