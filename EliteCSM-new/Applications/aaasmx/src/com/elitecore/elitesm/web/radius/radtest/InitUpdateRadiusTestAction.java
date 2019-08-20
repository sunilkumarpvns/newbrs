package com.elitecore.elitesm.web.radius.radtest;

import java.util.Iterator;

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
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestParamData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.radtest.forms.UpdateRadiusTestForm;
import com.elitecore.passwordutil.PasswordEncryption;

public class InitUpdateRadiusTestAction extends BaseWebAction {
    private static final String SUCCESS_FORWARD = "success";               
    private static final String FAILURE_FORWARD = "failure";               
    private static final String MODULE ="InitUpdateRadiusTestAction";
    private static final String VIEW_FORWARD = "updateRadiusPacket";
    
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());

        UpdateRadiusTestForm updateRadiusTestForm = (UpdateRadiusTestForm)form;
        RadiusTestBLManager radiusBLManager = new RadiusTestBLManager();
        ActionMessages messages = new ActionMessages();
        
        try {
            String strRadiusTestId = request.getParameter("fieldId");
            String radiusTestId = strRadiusTestId;
            request.getSession().setAttribute("fieldId", radiusTestId);
            
            if(Strings.isNullOrBlank(radiusTestId) == false) {
                updateRadiusTestForm.setNtradId(radiusTestId);
                IRadiusTestData radiusTestData = radiusBLManager.getRadiusObjById(radiusTestId);
                if(radiusTestData != null) {
                    this.beanToForm(radiusTestData, updateRadiusTestForm);
                 	                                     
                    if(radiusTestData.getHostAddress()== null){ 
                    	updateRadiusTestForm.setHostAddress("");             
                        
                    }else{
                    	
                    	String[] clientInterface = radiusTestData.getHostAddress().split(":");
                       	if(clientInterface.length == 2){
                    		if("".equals(clientInterface[0])){                        		
                    			updateRadiusTestForm.setHostAddress("");
                    		}else{
                    			updateRadiusTestForm.setHostAddress(clientInterface[0]);
                    		}
                    		updateRadiusTestForm.setHostPort(Integer.parseInt(clientInterface[1]));
                    		
                    	}else if(clientInterface.length == 1){
                    		updateRadiusTestForm.setHostAddress(clientInterface[0]); 
                    	}
                    	
                    	
                    }
                    
                    String [] paramnames = null;
                    String [] paramval = null;
                    
                    if(updateRadiusTestForm.getRadParamRel() != null){
                    	paramnames = new String[updateRadiusTestForm.getRadParamRel().size()];
                    	paramval = new String[updateRadiusTestForm.getRadParamRel().size()];
                    	int i=0;
                    	Iterator<RadiusTestParamData> itr = updateRadiusTestForm.getRadParamRel().iterator();
                    	while(itr.hasNext()){
                    		RadiusTestParamData paramData = itr.next();
                    		paramnames[i] = paramData.getName();
                    		paramval[i] = paramData.getValue();                    		
                    		i++;
                    	}
                    }         
                   	/* decrypt server password */
            		String encryptedPassword = PasswordEncryption.getInstance().decrypt(updateRadiusTestForm.getUserPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
            		updateRadiusTestForm.setUserPassword(encryptedPassword);
            			
                    request.setAttribute("paramnames",paramnames);
                    request.setAttribute("paramvalues",paramval);                    
                    request.setAttribute("radiusTestData", updateRadiusTestForm);
                }
            }
        } catch(DataManagerException e) {
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
    
    private void beanToForm(IRadiusTestData radiusTestData ,UpdateRadiusTestForm updateRadiusTestForm) {
        updateRadiusTestForm.setNtradId(radiusTestData.getNtradId());
        updateRadiusTestForm.setName(radiusTestData.getName());
        updateRadiusTestForm.setAdminHost(radiusTestData.getAdminHost());
        updateRadiusTestForm.setAdminPort(radiusTestData.getAdminPort());
        updateRadiusTestForm.setReTimeOut(radiusTestData.getReTimeOut());
        updateRadiusTestForm.setRetries(radiusTestData.getRetries());
        updateRadiusTestForm.setScecretKey(radiusTestData.getScecretKey());
        updateRadiusTestForm.setUserName(radiusTestData.getUserName());
        updateRadiusTestForm.setUserPassword(radiusTestData.getUserPassword());
        
        if(radiusTestData.getIsChap().equalsIgnoreCase("Y")) {
            updateRadiusTestForm.setIsChap("1");
        }
        
        if(radiusTestData.getRequestType() > 7) {
            updateRadiusTestForm.setRequestType(0);
            updateRadiusTestForm.setCustomRequestType(radiusTestData.getRequestType());
        } else {
            updateRadiusTestForm.setRequestType(radiusTestData.getRequestType());
        }
        updateRadiusTestForm.setRadParamRel(radiusTestData.getRadParamRel());
    }
}
