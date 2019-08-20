package com.elitecore.elitesm.web.wsconfig.sessionmgmt;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.wsconfig.WebServiceConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSAttrFieldMapData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSConfigData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSDBFieldMapData;
import com.elitecore.elitesm.datamanager.wsconfig.data.WSAttrFieldMapData;
import com.elitecore.elitesm.datamanager.wsconfig.data.WSConfigData;
import com.elitecore.elitesm.datamanager.wsconfig.data.WSDBFieldMapData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.wsconfig.sessionmgmt.forms.SessionMgmtWSDatabaseConfigForm;


public class SessionMgmtWSDatabaseConfigAction extends BaseWebAction{
    private static final String SESSION_MGMT_WS_DATABASE_CONFIG_ACTION = "SessionMgmtWSDatabaseConfigAction";
    private static final String SUCCESS_FORWARD = "success";
    private static final String FAILURE_FORWARD = "failure";
    private static final String UPDATE_FORWARD = "updateSessionMgmtWSDatabaseConfig";
    private static final String ACTION_ALIAS=ConfigConstant.UPDATE_SESSION_MANAGEMENT_WSCONFIG;
    private static final String INFORMATION = "information";
	
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response ) throws Exception{
        Logger.logInfo(SESSION_MGMT_WS_DATABASE_CONFIG_ACTION, "Entered execute method of " + getClass().getName());
        SessionMgmtWSDatabaseConfigForm sessionMgmtWSDatabaseConfigForm = (SessionMgmtWSDatabaseConfigForm)form;
        ActionMessages messages = new ActionMessages();
        
        try {
         
	        	if(checkAccess(request,ACTION_ALIAS)){
	        	  
	        	  Logger.logDebug(SESSION_MGMT_WS_DATABASE_CONFIG_ACTION, "subscriberWSDatabaseConfigForm :"+sessionMgmtWSDatabaseConfigForm);
	        	                
	              if(sessionMgmtWSDatabaseConfigForm.getCheckAction().equalsIgnoreCase("Update") ){
	            	  WebServiceConfigBLManager configBLManager= new WebServiceConfigBLManager();
	            	  
					  String[] fields = request.getParameterValues("fieldval");
					  String[] keys = request.getParameterValues("keyval");
	            	  Set<IWSDBFieldMapData> wsDBFieldMapSet = new LinkedHashSet<IWSDBFieldMapData>();
	            	  if(keys != null && keys.length>0){
	            	  
		            	  int len=keys.length;
		            	  for (int i = 0; i < len; i++) {
		            		  
		            		  WSDBFieldMapData data = new WSDBFieldMapData();
		            		  
		            		  data.setFieldName(fields[i]);
		            		  data.setKey(keys[i]);
		            		  data.setWsConfigId(sessionMgmtWSDatabaseConfigForm.getWsConfigId());
		
		            		  wsDBFieldMapSet.add(data);
		            		  
						  }
	            	  } 
	            	  
						String[] attrKeys = request.getParameterValues("attrkeyval");
						String[] attrFields = request.getParameterValues("attrfieldval");
	            	  Set<IWSAttrFieldMapData> wsAttrFieldMapSet = new LinkedHashSet<IWSAttrFieldMapData>();
	            	  if(attrKeys != null && attrKeys.length>0){
	            		  int len=attrKeys.length;
	            		  
	            		  for(int i=0;i<len;i++){
	            			
	            			  WSAttrFieldMapData data = new WSAttrFieldMapData();
	            			  
	            			  data.setFieldName(attrFields[i]);
	            			  data.setAttribute(attrKeys[i]);
	            			  data.setWsConfigId(sessionMgmtWSDatabaseConfigForm.getWsConfigId());
	            			  
	            			  wsAttrFieldMapSet.add(data);
	            			  
	            		  }
	            	  }
	            	  
	            	  IWSConfigData sessionMgmtConfigData = convertFormToBean(sessionMgmtWSDatabaseConfigForm);
	            	  sessionMgmtConfigData.setWsDBFieldMapSet(wsDBFieldMapSet);
	            	  sessionMgmtConfigData.setWsAttrFieldMapSet(wsAttrFieldMapSet);
	            	 // subscriberDBConfigData.setDbFieldMapList((List)request.getSession().getAttribute("dbFieldMapList"));
	            	  IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
	            	  String actionAlias = ACTION_ALIAS;
	            	  //configBLManager.updateSubscriberConfiguration(subscriberConfigData,actionAlias,staffData);
	            	  configBLManager.updateSessionMgmtConfiguration(sessionMgmtConfigData, staffData);
	            	  doAuditing(staffData, actionAlias);
	            	  request.setAttribute("sessionMgmtWSDatabaseConfigForm",sessionMgmtWSDatabaseConfigForm);
	            	  request.setAttribute("responseUrl","/initSessionMgmtWSDatabaseConfig.do");
	            	  ActionMessage message = new ActionMessage("wsconfig.sessionmgmt.update.success");
	            	  messages.add(INFORMATION,message);
	            	  saveMessages(request,messages);
	
	            	  return mapping.findForward(SUCCESS_FORWARD);
	              }
	              
	        	}else {
        		
        		Logger.logError(SESSION_MGMT_WS_DATABASE_CONFIG_ACTION, "Error during Data Manager operation ");
     	        ActionMessage message = new ActionMessage("general.user.restricted");
     	        messages = new ActionMessages();
     	        messages.add(INFORMATION, message);
     	        saveErrors(request, messages);
     			
     			return mapping.findForward(INVALID_ACCESS_FORWARD);
        		
        		
        	}
	        
        }catch (DataManagerException e){
        	Logger.logError(SESSION_MGMT_WS_DATABASE_CONFIG_ACTION, "Error during Data Manager operation , Reason : " + e.getMessage());
            Logger.logTrace(SESSION_MGMT_WS_DATABASE_CONFIG_ACTION, e);
            ActionMessage message = new ActionMessage("wsconfig.sessionmgmt.update.failure");
            messages.add(INFORMATION, message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }catch (Exception e) {
            Logger.logError(SESSION_MGMT_WS_DATABASE_CONFIG_ACTION, "Error during Data Manager operation , Reason : " + e.getMessage());
            Logger.logTrace(SESSION_MGMT_WS_DATABASE_CONFIG_ACTION, e);
            ActionMessage message = new ActionMessage("wsconfig.sessionmgmt.update.failure");
            messages.add(INFORMATION, message);
            saveErrors(request, messages);
            return mapping.findForward(FAILURE_FORWARD);
        }
        return mapping.findForward(UPDATE_FORWARD); 
    }
    private IWSConfigData convertFormToBean(SessionMgmtWSDatabaseConfigForm form){
    	IWSConfigData data = new WSConfigData();
    	data.setDatabasedsId(form.getDatabaseId());
    	data.setTableName(form.getTableName());
    	//data.setUserIdentityFieldName(form.getUserIdentityFieldName());
    	data.setRecordFetchLimit(form.getRecordFetchLimit());
    	data.setWsconfigId(form.getWsConfigId());
    	return data;
    }

}
