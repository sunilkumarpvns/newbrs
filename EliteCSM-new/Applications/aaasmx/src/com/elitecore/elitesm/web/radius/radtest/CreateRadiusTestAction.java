/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CreateNtradAction.java                 		
 * ModualName radius    			      		
 * Created on 2 April, 2008
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.radius.radtest; 
  
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.radtest.RadiusTestBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestParamData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestParamData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.radtest.forms.CreateRadiusTestForm;
import com.elitecore.passwordutil.PasswordEncryption;
                                                                               
public class CreateRadiusTestAction extends BaseWebAction { 
	                                                                       
	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CreateRadiusTestAction";
	private static final String VIEW_FORWARD = "createRadiusPacket"; 
	private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.CREATE_RADIUS_PACKET;
	
 
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	    Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
            
            CreateRadiusTestForm createRadiusTestForm = (CreateRadiusTestForm)form;
            RadiusTestBLManager radiusBLManager = new RadiusTestBLManager();
            ActionMessages messages = new ActionMessages();
            
            try {
            	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
            	
            	String [] parameterName = request.getParameterValues("paramnm");
            	String [] paramValue = request.getParameterValues("paramval");
            	
            	List<RadiusTestParamData> paramList = new ArrayList<RadiusTestParamData>();
            	if(parameterName != null){
            		for(int i=0;i<parameterName.length;i++){
            			RadiusTestParamData paramData = new RadiusTestParamData();
            			paramData.setName(parameterName[i]);
            			paramData.setValue(paramValue[i]);
            			paramList.add(paramData);
            		}
            	}            	               
                if(createRadiusTestForm.getCheckAction().equalsIgnoreCase("Create") ) {
                    IRadiusTestData radData = new RadiusTestData();
                    radData.setName(createRadiusTestForm.getName());
                    radData.setAdminHost(createRadiusTestForm.getAdminHost());
                    radData.setAdminPort(createRadiusTestForm.getAdminPort());
                    radData.setReTimeOut(createRadiusTestForm.getReTimeOut());
                    radData.setRetries(createRadiusTestForm.getRetries());
                    radData.setScecretKey(createRadiusTestForm.getScecretKey());
                    radData.setUserName(createRadiusTestForm.getUserName());
                    radData.setUserPassword(createRadiusTestForm.getUserPassword());
                    IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
        			
                    String clientInterface = createRadiusTestForm.getHostAddress()+":"+createRadiusTestForm.getHostPort();
                    radData.setHostAddress(clientInterface);
                
                    if(createRadiusTestForm.getIsChap() == null) {
                        radData.setIsChap("N");
                    } else {
                        radData.setIsChap("Y");
                    }
                
                    if(createRadiusTestForm.getRequestType() == 0) {
                        radData.setRequestType(createRadiusTestForm.getCustomRequestType());
                    } else {
                        radData.setRequestType(createRadiusTestForm.getRequestType());
                    }
                     
                    /* Encrypt server password */
            		String encryptedPassword = PasswordEncryption.getInstance().crypt(radData.getUserPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
            		radData.setUserPassword(encryptedPassword);
                    
                    radData.setParamList(paramList);
                    radiusBLManager.create(radData);
                    doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
                    request.setAttribute("responseUrl", "/initListRadiusPacket");
                    ActionMessage message = new ActionMessage("radius.radtest.packet.create.success");
                    messages.add("information", message);
                    saveMessages(request, messages);
                    return mapping.findForward(SUCCESS_FORWARD);
                }
                
            } catch(ActionNotPermitedException e){
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
}
