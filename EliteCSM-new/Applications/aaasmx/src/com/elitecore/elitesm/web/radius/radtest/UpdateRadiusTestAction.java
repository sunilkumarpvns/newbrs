package com.elitecore.elitesm.web.radius.radtest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.radtest.RadiusTestBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
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
import com.elitecore.elitesm.web.radius.radtest.forms.UpdateRadiusTestForm;
import com.elitecore.passwordutil.PasswordEncryption;


public class UpdateRadiusTestAction extends BaseWebAction{
    
        private static final String SUCCESS_FORWARD = "success";               
        private static final String FAILURE_FORWARD = "failure";               
        private static final String MODULE ="UpdateRadiusTestAction";
        private static final String VIEW_FORWARD = "initListRadiusPacket"; 
        private static final String ACTION_ALIAS    = ConfigConstant.UPDATE_RADIUS_PACKET;

        public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
            Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        
            UpdateRadiusTestForm updateRadiusTestForm = (UpdateRadiusTestForm)form;
            RadiusTestBLManager radiusBLManager = new RadiusTestBLManager();
            ActionMessages messages = new ActionMessages();

            try {
            	if(updateRadiusTestForm.getCheckAction().equalsIgnoreCase("Add") ) {
            		IRadiusTestParamData radParamData = new RadiusTestParamData();
            		radParamData.setName(updateRadiusTestForm.getParamName());
            		radParamData.setValue(updateRadiusTestForm.getParamValue());
            		((List)request.getSession().getAttribute("radParamList")).add(radParamData);
            		updateRadiusTestForm.setParamName("");
            		updateRadiusTestForm.setParamValue("");
            	}

            	if(updateRadiusTestForm.getCheckAction().equalsIgnoreCase("EditParam") ) {
            		int index = updateRadiusTestForm.getItemIndex();
            		updateRadiusTestForm.setParamName(((IRadiusTestParamData)((List)request.getSession().getAttribute("radParamList")).get(index)).getName());
            		updateRadiusTestForm.setParamValue(((IRadiusTestParamData)((List)request.getSession().getAttribute("radParamList")).get(index)).getValue());
            	}

            	if(updateRadiusTestForm.getCheckAction().equalsIgnoreCase("UpdateParam") ) {
            		int index = updateRadiusTestForm.getItemIndex();
            		((IRadiusTestParamData)((List)request.getSession().getAttribute("radParamList")).get(index)).setName(updateRadiusTestForm.getParamName());
            		((IRadiusTestParamData)((List)request.getSession().getAttribute("radParamList")).get(index)).setValue(updateRadiusTestForm.getParamValue());
            		updateRadiusTestForm.setParamName("");
            		updateRadiusTestForm.setParamValue("");
            	}

            	if(updateRadiusTestForm.getCheckAction().equalsIgnoreCase("Remove") ) {
            		int index = updateRadiusTestForm.getItemIndex();
            		List paramList = (List)request.getSession().getAttribute("radParamList");
            		if(paramList != null && paramList.size() > 0) {
            			paramList.remove(index);
            		}
            	}

            	if(updateRadiusTestForm.getCheckAction().equalsIgnoreCase("Update") ) {
            		
            		String [] paramNames = request.getParameterValues("paranm");
            		String [] paraValues = request.getParameterValues("paramvl");
            		
            		List<RadiusTestParamData> radParamList = new ArrayList<RadiusTestParamData>();
            		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
           			
            		if(paramNames != null){
            			for (int i =0;i<paramNames.length;i++){
            				RadiusTestParamData paramData = new RadiusTestParamData();
            				paramData.setName(paramNames[i]);
            				paramData.setValue(paraValues[i]);
            				radParamList.add(paramData);
            			}
            		}
            		
            		Object objFieldId = request.getSession().getAttribute("fieldId");
            		
            		String strfieldId = objFieldId.toString(); 
            		IRadiusTestData radData = radiusBLManager.getRadiusObjById(strfieldId);

            		radData.setName(updateRadiusTestForm.getName());
            		radData.setAdminHost(updateRadiusTestForm.getAdminHost());
            		radData.setAdminPort(updateRadiusTestForm.getAdminPort());
            		radData.setReTimeOut(updateRadiusTestForm.getReTimeOut());
            		radData.setRetries(updateRadiusTestForm.getRetries());
            		radData.setScecretKey(updateRadiusTestForm.getScecretKey());
            		radData.setUserName(updateRadiusTestForm.getUserName());
            		radData.setUserPassword(updateRadiusTestForm.getUserPassword());
            		
            		String clientInterface = updateRadiusTestForm.getHostAddress()+":"+updateRadiusTestForm.getHostPort();
                    radData.setHostAddress(clientInterface);
            		
            		/* Encrypt server password */
            		String encryptedPassword = PasswordEncryption.getInstance().crypt(radData.getUserPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
            		radData.setUserPassword(encryptedPassword);
                  
            		if(updateRadiusTestForm.getIsChap() == null) {
            			radData.setIsChap("N");
            		} else {
            			radData.setIsChap("Y");
            		}

            		if(updateRadiusTestForm.getRequestType() == 0) {
            			radData.setRequestType(updateRadiusTestForm.getCustomRequestType());
            		} else {
            			radData.setRequestType(updateRadiusTestForm.getRequestType());
            		}
            		
            		radiusBLManager.updateRadiusData(radData,radParamList);
            		doAuditing(staffData, ACTION_ALIAS);
            		request.setAttribute("responseUrl", "/initListRadiusPacket");
            		ActionMessage message = new ActionMessage("radius.radtest.packet.update.success");
            		messages.add("information", message);
            		saveMessages(request, messages);
            		return mapping.findForward(SUCCESS_FORWARD);

            	}

            }  catch(DataManagerException e) {
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
