package com.elitecore.netvertexsm.web.wsconfig.sprmgmt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.wsconfig.WebServiceConfigBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.wsconfig.data.ProvisioningAPIConfiguration;
import com.elitecore.netvertexsm.datamanager.wsconfig.data.WSConfigData;
import com.elitecore.netvertexsm.util.WSConfig;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.wsconfig.sprmgmt.forms.SubscriberWSDatabaseConfigForm;


public class SubscriberWSDatabaseConfigAction extends BaseWebAction{
    private static final String MODULE = "SubscriberWSDatabaseConfigAction";
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String UPDATE_FORWARD = "updateSubscriberWSDatabaseConfig";
    private static final String ACTION_ALIAS=ConfigConstant.UPDATE_SUBSCRIBER_PROFILE_WSCONFIG;
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
        SubscriberWSDatabaseConfigForm subscriberWSDatabaseConfigForm = (SubscriberWSDatabaseConfigForm)form;
        ActionMessages messages = new ActionMessages();
        
        try {
        	
        	if(checkAccess(request,ACTION_ALIAS)){

        		if(subscriberWSDatabaseConfigForm.getCheckAction().equalsIgnoreCase("Update") ){
	            	  WebServiceConfigBLManager configBLManager= new WebServiceConfigBLManager();
	            	  
	            	  WSConfigData subscriberConfigData = convertFormToBean(subscriberWSDatabaseConfigForm);
	            	  IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	            	  String actionAlias = ACTION_ALIAS;
	            	  configBLManager.updateSubscriberConfiguration(subscriberConfigData,actionAlias,staffData);
	            	  WSConfig.init();
	            	  request.setAttribute("subscriberWSDatabaseConfigForm", subscriberWSDatabaseConfigForm);
	            	  request.setAttribute("responseUrl","/initSubscriberWSDatabaseConfig.do");
	            	  ActionMessage message1 = new ActionMessage("wsconfig.update.success");
	                  ActionMessage message2 = new ActionMessage("wsconfig.update.refresh");
	                  messages.add("information", message1);
	                  messages.add("information", message2);
	            	  saveMessages(request,messages);	            	  
	            	  return mapping.findForward(SUCCESS_FORWARD);
	              }
              
        	}else{
        		
        		Logger.logError(MODULE, "Error during Data Manager operation ");
     	        ActionMessage message = new ActionMessage("general.user.restricted");
     	        messages = new ActionMessages();
     	        messages.add("information", message);
     	        saveErrors(request, messages);
     	        
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("wsconfig.error.heading","updating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);     	        
     			return mapping.findForward(INVALID_ACCESS_FORWARD);
        	}
        	  
        }catch (DataManagerException e){
        	Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
            ActionMessage message = new ActionMessage("wsconfig.update.failure");
            messages.add("information", message);
            saveErrors(request, messages);
            
	         ActionMessages errorHeadingMessage = new ActionMessages();
	         message = new ActionMessage("wsconfig.error.heading","updating");
	         errorHeadingMessage.add("errorHeading",message);
	         saveMessages(request,errorHeadingMessage);            
            return mapping.findForward(FAILURE_FORWARD);
        }catch (Exception e) {
            Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
            Logger.logTrace(MODULE, e);
            ActionMessage message = new ActionMessage("wsconfig.update.failure");
            messages.add("information", message);
            saveErrors(request, messages);
            
	         ActionMessages errorHeadingMessage = new ActionMessages();
	         message = new ActionMessage("wsconfig.error.heading","updating");
	         errorHeadingMessage.add("errorHeading",message);
	         saveMessages(request,errorHeadingMessage);
            return mapping.findForward(FAILURE_FORWARD);
        }
        return mapping.findForward(UPDATE_FORWARD); 
    }
    private WSConfigData convertFormToBean(SubscriberWSDatabaseConfigForm form){
    	WSConfigData data = new WSConfigData();
    	if(form.getDatabaseId()!=null && form.getDatabaseId().intValue()>0){
    		data.setDatabasedsId(form.getDatabaseId());
    	}
    	data.setTableName(form.getTableName());
    	data.setUserIdentityFieldName(form.getUserIdentityFieldName());
    	data.setRecordFetchLimit(form.getRecordFetchLimit());
    	data.setWsconfigId(form.getWsConfigId());
    	data.setPrimaryKeyColumn(form.getPrimaryKeyColumn());
    	data.setSequenceName(form.getSequenceName());
    	
    	data.setSubscriberIdentity(form.getSubscriberIdentity());
    	data.setUsageMonitoringDatabaseId(form.getUsageMonitoringDatabaseId());
    	
    	
    	if(form.getBodCDRDriverId()!=null && form.getBodCDRDriverId().intValue()>0){
    		data.setBodCDRDriverId(form.getBodCDRDriverId());
    	}
    	if(form.getDynaSprDatabaseId()!=null && form.getDynaSprDatabaseId().intValue()>0){
    		data.setDynaSprDatabaseId(form.getDynaSprDatabaseId());
    	}
    	return data;
    }

}
