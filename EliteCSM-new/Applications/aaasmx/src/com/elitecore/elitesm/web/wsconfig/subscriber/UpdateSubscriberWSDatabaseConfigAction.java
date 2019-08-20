package com.elitecore.elitesm.web.wsconfig.subscriber;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.wsconfig.WebServiceConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSConfigData;
import com.elitecore.elitesm.datamanager.wsconfig.data.WSConfigData;
import com.elitecore.elitesm.datamanager.wsconfig.data.WSKeyMappingData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.wsconfig.subscriber.forms.UpdateSubscriberWSDatabaseConfigForm;
import com.elitecore.elitesm.ws.subscriber.SubscriberProfileWebServiceBLManager;


public class UpdateSubscriberWSDatabaseConfigAction extends BaseWebAction{
	private static final String INFORMATION = "information";
	private static final String UPDATE_SUBSCRIBER_WS_DATABASE_CONFIGACTION = "UpdateSubscriberWSDatabaseConfigAction";
	private static final String UPDATE_FORWARD = "updateSubscriberWSDatabaseConfig";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS=ConfigConstant.UPDATE_SUBSCRIBER_PROFILE_WSCONFIG;
	
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logInfo(UPDATE_SUBSCRIBER_WS_DATABASE_CONFIGACTION, "Entered execute method of " + getClass().getName());
		try {
			checkActionPermission(request, ACTION_ALIAS);
			
			UpdateSubscriberWSDatabaseConfigForm subscriberWSDatabaseConfigForm = (UpdateSubscriberWSDatabaseConfigForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			if(subscriberWSDatabaseConfigForm.getAction() == null) {
				initUpdateSubscriberWSHandler(subscriberWSDatabaseConfigForm);
			}else if("refresh".equalsIgnoreCase(subscriberWSDatabaseConfigForm.getAction())) {
				SubscriberProfileWebServiceBLManager.setConfiguration();
				initUpdateSubscriberWSHandler(subscriberWSDatabaseConfigForm);
			}else if("update".equalsIgnoreCase(subscriberWSDatabaseConfigForm.getAction())){
				updateSubscriberWSHandler(subscriberWSDatabaseConfigForm, request);
				doAuditing(staffData, ACTION_ALIAS);
				request.setAttribute("subscriberWSDatabaseConfigForm", subscriberWSDatabaseConfigForm);
				request.getSession().removeAttribute("dbFieldMapList");
				request.setAttribute("responseUrl","/initSubscriberWSDatabaseConfig.do");
				ActionMessages messages = new ActionMessages();
				messages.add(INFORMATION,new ActionMessage("wsconfig.dbconfig.update.success"));
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS);
			}
			return mapping.findForward(UPDATE_FORWARD);
		}catch (ActionNotPermitedException e) {
			Logger.logError(UPDATE_SUBSCRIBER_WS_DATABASE_CONFIGACTION, "Error during Data Manager operation, Reason :" + e.getMessage());
			ActionMessages messages = new ActionMessages();
 	        messages.add(INFORMATION, new ActionMessage("general.user.restricted"));
 	        saveErrors(request, messages);
 			return mapping.findForward(INVALID_ACCESS_FORWARD);
		} catch (DataManagerException e){
			Logger.logError(UPDATE_SUBSCRIBER_WS_DATABASE_CONFIGACTION, "Error during Data Manager operation, Reason : " + e.getMessage());
			Logger.logTrace(UPDATE_SUBSCRIBER_WS_DATABASE_CONFIGACTION, e);
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, new ActionMessage("general.error"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}catch (Exception e) {
			Logger.logError(UPDATE_SUBSCRIBER_WS_DATABASE_CONFIGACTION, "Error during Data Manager operation, Reason : " + e.getMessage());
			Logger.logTrace(UPDATE_SUBSCRIBER_WS_DATABASE_CONFIGACTION, e);
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, new ActionMessage("general.error"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
	
	private void initUpdateSubscriberWSHandler(UpdateSubscriberWSDatabaseConfigForm subscriberWSDatabaseConfigForm) throws DataManagerException {
		DatabaseDSBLManager databaseDSBLManager= new DatabaseDSBLManager();
		List<IDatabaseDSData> lstDatasource=databaseDSBLManager.getDatabaseDSList();
		subscriberWSDatabaseConfigForm.setLstDatasource(lstDatasource);
		
		WebServiceConfigBLManager configBLManager= new WebServiceConfigBLManager();
		IWSConfigData subscriberDBConfigData = configBLManager.getSubscriberConfiguration();
		convertBeanToForm(subscriberWSDatabaseConfigForm, subscriberDBConfigData);
	}
	
	private void updateSubscriberWSHandler(UpdateSubscriberWSDatabaseConfigForm subscriberWSDatabaseConfigForm, HttpServletRequest request) throws DataManagerException {
		IWSConfigData wsConfigData = convertFormToBean(subscriberWSDatabaseConfigForm);
		String[] wsKey = request.getParameterValues("wsKey");
		String[] dbField = request.getParameterValues("dbField");
		String[] req = request.getParameterValues("request");
		String[] resp = request.getParameterValues("response");
		if(wsKey != null) {
			Logger.logInfo(UPDATE_SUBSCRIBER_WS_DATABASE_CONFIGACTION, "Mapping Data, wsKey:"+wsKey.length+"\tdbField:"+dbField+"\treq:"+req+"\tresp:"+resp); 
			Set<WSKeyMappingData> wsKeyMappingSet = new LinkedHashSet<WSKeyMappingData>(wsKey.length);
	 		if(dbField != null && req!=null && resp!=null) {
				for(int index=0; index<wsKey.length; index++) {
					if(wsKey[index] != null && wsKey[index].trim().length() > 0 && dbField[index] != null && dbField[index].trim().length() >0) {
						WSKeyMappingData wsKeyMapping = new WSKeyMappingData();
						wsKeyMapping.setWsKey(wsKey[index].trim());
						wsKeyMapping.setDbField(dbField[index].trim());
						wsKeyMapping.setRequest(req[index]);
						wsKeyMapping.setResponse(resp[index]);
						wsKeyMappingSet.add(wsKeyMapping);
					}
				}
			}
	 		wsConfigData.setWsKeyMappingSet(wsKeyMappingSet);
		}
 		WebServiceConfigBLManager configBLManager= new WebServiceConfigBLManager();
 		IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
 		
 		configBLManager.updateSubscriberConfiguration(wsConfigData ,staffData);
	}
	
	private void convertBeanToForm(UpdateSubscriberWSDatabaseConfigForm form, IWSConfigData data){
		if(data != null){
			form.setDatabaseId(data.getDatabasedsId());	
			form.setTableName(data.getTableName());
			form.setUserIdentityFieldName(data.getUserIdentityFieldName());
			form.setRecordFetchLimit(data.getRecordFetchLimit());
			form.setWsConfigId(data.getWsconfigId());
			form.setPrimaryKeyColumn(data.getPrimaryKeyColumn());
			form.setSequenceName(data.getSequenceName());
			form.setWsKeyMappingList(new ArrayList<WSKeyMappingData>(data.getWsKeyMappingSet()));
		}
	}
	
	 private IWSConfigData convertFormToBean(UpdateSubscriberWSDatabaseConfigForm form){
	    	IWSConfigData data = new WSConfigData();
	    	data.setDatabasedsId(form.getDatabaseId());
	    	data.setTableName(form.getTableName());
	    	data.setUserIdentityFieldName(form.getUserIdentityFieldName());
	    	data.setRecordFetchLimit(form.getRecordFetchLimit());
	    	data.setWsconfigId(form.getWsConfigId());
	    	data.setPrimaryKeyColumn(form.getPrimaryKeyColumn());
	    	data.setSequenceName(form.getSequenceName());
	    	return data;
	    }
}
