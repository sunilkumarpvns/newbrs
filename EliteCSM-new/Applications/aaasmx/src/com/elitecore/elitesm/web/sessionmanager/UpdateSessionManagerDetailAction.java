
package com.elitecore.elitesm.web.sessionmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISMConfigInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISMDBFieldMapData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMConfigInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMDBFieldMapData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMSessionCloserESIRelData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.acct.forms.ExternalSystemBean;
import com.elitecore.elitesm.web.sessionmanager.forms.UpdateSessionManagerDetailForm;


public class UpdateSessionManagerDetailAction extends BaseWebAction {

	private static final String UPDATE_FORWARD  = "updateSessionManagerInstance";
	private static final String FAILURE_FORWARD = "failure";
	private static final String SUCCESS_FORWARD = "success";
    private static final String ACTION_ALIAS    = ConfigConstant.UPDATE_SESSION_MANAGER;
    private static final String MODULE          ="UPDATE_SESSION_MANAGER";
    private static final String UPDATE_SESSIONMANAGER_LOCAL="localsessionmanagerdata";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			checkActionPermission(request, ACTION_ALIAS);
			UpdateSessionManagerDetailForm  updateSessionManagerDetailForm =  (UpdateSessionManagerDetailForm) form;
			Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
			
			ExternalSystemInterfaceBLManager externalSystemBLmanager = new ExternalSystemInterfaceBLManager();
			List<ExternalSystemInterfaceInstanceData> sessionManagerInstanceList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.SESSION_MANAGER);
			String sessionManagerInstanceIds[] = new String[sessionManagerInstanceList.size()];
			String sessionManagerInstanceNames[][] = new String[sessionManagerInstanceList.size()][4]; 
			for(int i=0;i<sessionManagerInstanceList.size();i++) {
				ExternalSystemInterfaceInstanceData	externalSystemData = sessionManagerInstanceList.get(i);
				sessionManagerInstanceNames[i][0] = String.valueOf(externalSystemData.getName());
				sessionManagerInstanceNames[i][1] = String.valueOf(externalSystemData.getAddress());
				sessionManagerInstanceNames[i][2] = String.valueOf(externalSystemData.getMinLocalPort());
				sessionManagerInstanceNames[i][3] = String.valueOf(externalSystemData.getExpiredRequestLimitCount());
				sessionManagerInstanceIds[i] = String.valueOf(externalSystemData.getEsiInstanceId());
			}
								
			request.setAttribute("sessionManagerInstanceIds", sessionManagerInstanceIds);
			request.setAttribute("sessionManagerInstanceNames", sessionManagerInstanceNames);
			request.setAttribute("sessionManagerInstanceList", sessionManagerInstanceList);
			
			List<ExternalSystemInterfaceInstanceData> nasInstanceList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.NAS);
			String nasInstanceIds[] = new String[nasInstanceList.size()];
			String nasInstanceNames[][] = new String[nasInstanceList.size()][4]; 
			for(int i=0;i<nasInstanceList.size();i++) {
				ExternalSystemInterfaceInstanceData	externalSystemData = nasInstanceList.get(i);
				nasInstanceNames[i][0] = String.valueOf(externalSystemData.getName());
				nasInstanceNames[i][1] = String.valueOf(externalSystemData.getAddress());
				nasInstanceNames[i][2] = String.valueOf(externalSystemData.getMinLocalPort());
				nasInstanceNames[i][3] = String.valueOf(externalSystemData.getExpiredRequestLimitCount());
				nasInstanceIds[i] = String.valueOf(externalSystemData.getEsiInstanceId());
			}
								
			request.setAttribute("nasInstanceIds", nasInstanceIds);
			request.setAttribute("nasInstanceNames", nasInstanceNames);
			request.setAttribute("nasInstanceList", nasInstanceList);
			
			List<ExternalSystemInterfaceInstanceData> externalSystemInstanceList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.ACCT_PROXY);
			String esiInstanceIds[] = new String[externalSystemInstanceList.size()];
			String esiInstanceNames[][] = new String[externalSystemInstanceList.size()][4]; 
			for(int i=0;i<externalSystemInstanceList.size();i++) {
				ExternalSystemInterfaceInstanceData	externalSystemData = externalSystemInstanceList.get(i);
				esiInstanceNames[i][0] = String.valueOf(externalSystemData.getName());
				esiInstanceNames[i][1] = String.valueOf(externalSystemData.getAddress());
				esiInstanceNames[i][2] = String.valueOf(externalSystemData.getMinLocalPort());
				esiInstanceNames[i][3] = String.valueOf(externalSystemData.getExpiredRequestLimitCount());
				esiInstanceIds[i] = String.valueOf(externalSystemData.getEsiInstanceId());
			}
			
			request.setAttribute("esiInstanceIds", esiInstanceIds);
			request.setAttribute("esiInstanceNames", esiInstanceNames);
			request.setAttribute("proxyServerRelList", externalSystemInstanceList);
			
			String strSessionManagerId = request.getParameter("sminstanceid");
			
			String sessionManagerId;
			if(strSessionManagerId == null){
				sessionManagerId = updateSessionManagerDetailForm.getSminstanceid();
			}else{
				sessionManagerId = strSessionManagerId;
			}
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias = ACTION_ALIAS;
			SessionManagerBLManager blManager = new SessionManagerBLManager(); 
			if(updateSessionManagerDetailForm.getAction() == null){
			
				if(sessionManagerId != null){
					
					ISessionManagerInstanceData sessionManagerInstanceData = blManager.getSessionManagerDataById(sessionManagerId);
              						
					ISMConfigInstanceData configInstanceData =(ISMConfigInstanceData) sessionManagerInstanceData.getSmConfigInstanceData();
					updateSessionManagerDetailForm = convertBeanToForm(configInstanceData);		
					updateSessionManagerDetailForm.setSminstanceid(sessionManagerInstanceData.getSmInstanceId());
					List<SMDBFieldMapData> smDBFieldMap =configInstanceData.getDbFieldMapDataList(); 
					int dbLength=0;
					for(Iterator iterator =smDBFieldMap.iterator();iterator.hasNext();){
						ISMDBFieldMapData fieldMapData = (ISMDBFieldMapData) iterator.next();
						if(fieldMapData.getField() == null){
							dbLength++;
						}
					}
						
					int len=dbLength;
					String[] fieldNameArray = new String[len];
	                String[] referringEntityValueArray= new String[len];
	                String[] dataTypeValueArray= new String[len];
	                String[] defaultValueArray= new String[len];
                    int i=0;
                    ArrayList<ISMDBFieldMapData> tempMandatoryfieldMapList = new ArrayList<ISMDBFieldMapData>();
                     
	               	for (Iterator iterator = smDBFieldMap.iterator(); iterator.hasNext();) {
						  
	               		ISMDBFieldMapData fieldMapData = (ISMDBFieldMapData) iterator.next();
	               		if(fieldMapData.getField() != null){
	               			SMDBFieldMapData mandFieldMapData=new SMDBFieldMapData(); 
	               			mandFieldMapData.setField(fieldMapData.getField());
	               			mandFieldMapData.setDbFieldName(fieldMapData.getDbFieldName());
	               			mandFieldMapData.setReferringEntity(fieldMapData.getReferringEntity());
	               			mandFieldMapData.setDataType(fieldMapData.getDataType());
	               			mandFieldMapData.setDefaultValue(fieldMapData.getDefaultValue());
	               			
	               			tempMandatoryfieldMapList.add(mandFieldMapData);
	               		}else{
	               			fieldNameArray[i]=fieldMapData.getDbFieldName();
		               		referringEntityValueArray[i]=fieldMapData.getReferringEntity();
							dataTypeValueArray[i]=String.valueOf(fieldMapData.getDataType());
							defaultValueArray[i]=fieldMapData.getDefaultValue();
							i++;
	                	}
					}
	                	
	                List<SMSessionCloserESIRelData> nasEsiRelList = blManager.getNASSessionCloserESIRelList(configInstanceData.getSmConfigId());
	                List<SMSessionCloserESIRelData> acctEsiRelList = blManager.getAcctSessionCloserESIRelList(configInstanceData.getSmConfigId());

	                List<ExternalSystemBean> nasClients = convertToSessionCloserExternalSystemBean(nasEsiRelList);
	                List<ExternalSystemBean> acctServers = convertToSessionCloserExternalSystemBean(acctEsiRelList);
	                	
	                updateSessionManagerDetailForm.setNasClientList(nasClients);
	                updateSessionManagerDetailForm.setAcctServerList(acctServers);
	                updateSessionManagerDetailForm.setLstMandatoryFieldMapData(tempMandatoryfieldMapList);
	                DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
					List<IDatabaseDSData> lstdatasource = databaseDSBLManager.getDatabaseDSList();
                    updateSessionManagerDetailForm.setLstDatasource(lstdatasource);
	                request.setAttribute("fieldNameArray",fieldNameArray);
	                request.setAttribute("referringEntityVlArray",referringEntityValueArray);
	                request.setAttribute("dataTypeVlArray",dataTypeValueArray);
	                request.setAttribute("defaultVlArray",defaultValueArray);
	                request.setAttribute("sessionManagerInstanceData",sessionManagerInstanceData);
	                request.setAttribute("updateSessionManagerDetailForm",updateSessionManagerDetailForm);
	                request.setAttribute("lstMandatoryFieldMapData", tempMandatoryfieldMapList);
	                
	               	return mapping.findForward(UPDATE_SESSIONMANAGER_LOCAL);
						
				}
			}else if("update".equalsIgnoreCase(updateSessionManagerDetailForm.getAction())){
				ISessionManagerInstanceData sessionManagerInstanceData = blManager.getSessionManagerDataById(sessionManagerId);
				
			    sessionManagerInstanceData = convertFormToBean(updateSessionManagerDetailForm,request,sessionManagerInstanceData);
				
			    blManager.updateSessionManagerDetails(sessionManagerInstanceData,staffData,actionAlias);
				request.setAttribute("sessionManagerInstanceData",sessionManagerInstanceData);
				
				request.setAttribute("responseUrl","/viewSessionManager.do?sminstanceid="+sessionManagerId); 
				ActionMessage message = new ActionMessage("sessionmanager.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS_FORWARD);
				
			}
			return mapping.findForward(FAILURE_FORWARD);
			
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch(DataManagerException managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("sessionmanager.update.failure"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		
		}catch(Exception e){
			Logger.logError(MODULE, "Error, reason : " + e.getMessage());            
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("sessionmanager.update.failure"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		}

	}

	
	private ISessionManagerInstanceData convertFormToBean(UpdateSessionManagerDetailForm updateSessionManagerDetailForm, HttpServletRequest request,ISessionManagerInstanceData sessionManagerInstanceData) throws ArrayIndexOutOfBoundsException, NumberFormatException, DataManagerException {
		
		SMConfigInstanceData smConfigInstanceData = new SMConfigInstanceData();
		
		smConfigInstanceData.setDatabaseDatasourceId(updateSessionManagerDetailForm.getDatabaseId());
		smConfigInstanceData.setTablename(updateSessionManagerDetailForm.getTablename());
		smConfigInstanceData.setAutoSessionCloser(updateSessionManagerDetailForm.getAutosessioncloser());
		smConfigInstanceData.setSessiontimeout(updateSessionManagerDetailForm.getSessionTimeout());
		smConfigInstanceData.setCloseBatchCount(updateSessionManagerDetailForm.getCloseBatchCount());
		smConfigInstanceData.setSessionThreadSleepTime(updateSessionManagerDetailForm.getSessionThreadSleeptime());
		smConfigInstanceData.setSessionCloseAction(updateSessionManagerDetailForm.getSessionCloseAction());
		smConfigInstanceData.setIdentityField(updateSessionManagerDetailForm.getIdentityField());
		smConfigInstanceData.setIdSequenceName(updateSessionManagerDetailForm.getIdSequenceName());
		smConfigInstanceData.setStartTimeField(updateSessionManagerDetailForm.getStartTimeField());
		smConfigInstanceData.setLastUpdatedTimeField(updateSessionManagerDetailForm.getLastUpdatedTimeField());
		smConfigInstanceData.setSessionIdField(updateSessionManagerDetailForm.getSessionIdField());
		smConfigInstanceData.setSessionIdRefEntity(updateSessionManagerDetailForm.getSessionIdRefEntity());
		smConfigInstanceData.setServiceTypeField(updateSessionManagerDetailForm.getServiceTypeField());
		smConfigInstanceData.setGroupNameField(updateSessionManagerDetailForm.getGroupNameField());
		smConfigInstanceData.setSmInstanceId(updateSessionManagerDetailForm.getSminstanceid());
		smConfigInstanceData.setSmConfigId(updateSessionManagerDetailForm.getSmConfigInstanceId());
		smConfigInstanceData.setConcurrencyIdentityField(updateSessionManagerDetailForm.getConcurrencyIdentityField());
		smConfigInstanceData.setSearchAttribute(updateSessionManagerDetailForm.getSearchAttribute());
		smConfigInstanceData.setBatchUpdateEnabled(updateSessionManagerDetailForm.getBatchUpdateEnabled());
		smConfigInstanceData.setBatchSize(updateSessionManagerDetailForm.getBatchSize());
		smConfigInstanceData.setBatchUpdateInterval(updateSessionManagerDetailForm.getBatchUpdateInterval());
		smConfigInstanceData.setDbQueryTimeOut(updateSessionManagerDetailForm.getDbQueryTimeOut());
		smConfigInstanceData.setBehaviour(updateSessionManagerDetailForm.getBehaviour());
		smConfigInstanceData.setDbfailureaction(updateSessionManagerDetailForm.getDbfailureaction());
		smConfigInstanceData.setSessionOverrideAction(updateSessionManagerDetailForm.getSessionOverrideAction());
		smConfigInstanceData.setSessionOverrideColumn(updateSessionManagerDetailForm.getSessionOverrideColumn());
		smConfigInstanceData.setSessionStopAction(updateSessionManagerDetailForm.getSessionStopAction());
		
		String [] fieldValues = request.getParameterValues("fieldVal");
		String [] refEntValues= request.getParameterValues("refEntVal");
		String [] dtTypeValues = request.getParameterValues("dtTypeVal");
		String [] defaultValues = request.getParameterValues("defaultVal");
		List<SMDBFieldMapData> smdbFieldMapList = new ArrayList<SMDBFieldMapData>();
		
		if(fieldValues != null && fieldValues.length>0)
		{
			int len = fieldValues.length;
			
			for(int i=0;i<len;i++){
				SMDBFieldMapData dbFieldMapData = new SMDBFieldMapData();
				
				dbFieldMapData.setDbFieldName(fieldValues[i]);
				dbFieldMapData.setReferringEntity(refEntValues[i]);
				dbFieldMapData.setDataType(Integer.parseInt(dtTypeValues[i]));
				dbFieldMapData.setDefaultValue(defaultValues[i]);
				dbFieldMapData.setSmConfigId(updateSessionManagerDetailForm.getSmConfigInstanceId());
				smdbFieldMapList.add(dbFieldMapData);
			}
		}
		
		String[] field=request.getParameterValues("field");
		String[] dbField=request.getParameterValues("dbFieldName");
		String[] referringEntity=request.getParameterValues("referringEntity");
		String[] dataType=request.getParameterValues("dataType");
		String[] defaultValue=request.getParameterValues("defaultValue");
		
		if(field !=null && field.length>0){
			int len=field.length;
			for(int i=0;i<len;i++){
				SMDBFieldMapData dbFieldMapData = new SMDBFieldMapData();
				dbFieldMapData.setField(field[i]);
				dbFieldMapData.setDbFieldName(dbField[i]);
				dbFieldMapData.setReferringEntity(referringEntity[i]);
				dbFieldMapData.setDataType(Integer.parseInt(dataType[i]));
				dbFieldMapData.setDefaultValue(defaultValue[i]);
				dbFieldMapData.setSmConfigId(updateSessionManagerDetailForm.getSmConfigInstanceId());
				smdbFieldMapList.add(dbFieldMapData);
			}
		}
		
		
		smConfigInstanceData.setDbFieldMapDataList(smdbFieldMapList);
		sessionManagerInstanceData.setSmConfigInstanceData(smConfigInstanceData);
		List<SMSessionCloserESIRelData> smSessionCloserESIRelDataList = new ArrayList<SMSessionCloserESIRelData>();
		String nasClients[] = request.getParameterValues("nasClients");
        String acctServers[] = request.getParameterValues("acctServers");
        
        if(acctServers != null && acctServers.length > 0){	
			List<SMSessionCloserESIRelData>  acctServerList = getSessionCloserExternalSystemList(acctServers,smConfigInstanceData.getSmConfigId());
			smSessionCloserESIRelDataList.addAll(acctServerList);
			smConfigInstanceData.setSmSessionCloserESIRelDataList(smSessionCloserESIRelDataList);
        }
        
        if(nasClients != null && nasClients.length > 0){ 	
				List<SMSessionCloserESIRelData>  nasClientList = getSessionCloserExternalSystemList(nasClients,smConfigInstanceData.getSmConfigId());
				smSessionCloserESIRelDataList.addAll(nasClientList);
				smConfigInstanceData.setSmSessionCloserESIRelDataList(smSessionCloserESIRelDataList);
        }
        sessionManagerInstanceData.setSmConfigInstanceData(smConfigInstanceData);
			
		sessionManagerInstanceData.setSmInstanceId(updateSessionManagerDetailForm.getSminstanceid());
		sessionManagerInstanceData.setLastmodifieddate(getCurrentTimeStemp());
		return sessionManagerInstanceData;
		
	}

	private UpdateSessionManagerDetailForm convertBeanToForm(ISMConfigInstanceData smConfigInstanceData) {
		
		UpdateSessionManagerDetailForm form= new UpdateSessionManagerDetailForm();
		if(smConfigInstanceData!=null){

			form.setDatabaseId(smConfigInstanceData.getDatabaseDatasourceId());
			form.setTablename(smConfigInstanceData.getTablename());
			form.setAutosessioncloser(smConfigInstanceData.getAutoSessionCloser());
			form.setSessionTimeout(smConfigInstanceData.getSessiontimeout());
			form.setCloseBatchCount(smConfigInstanceData.getCloseBatchCount());
			form.setSessionThreadSleeptime(smConfigInstanceData.getSessionThreadSleepTime());
			form.setSessionCloseAction(smConfigInstanceData.getSessionCloseAction());
			form.setIdentityField(smConfigInstanceData.getIdentityField());
			form.setIdSequenceName(smConfigInstanceData.getIdSequenceName());
			form.setStartTimeField(smConfigInstanceData.getStartTimeField());
			form.setLastUpdatedTimeField(smConfigInstanceData.getLastUpdatedTimeField());
			form.setSessionIdField(smConfigInstanceData.getSessionIdField());
			form.setSessionIdRefEntity(smConfigInstanceData.getSessionIdRefEntity());
			form.setServiceTypeField(smConfigInstanceData.getServiceTypeField());
			form.setGroupNameField(smConfigInstanceData.getGroupNameField());
            form.setSmConfigInstanceId(smConfigInstanceData.getSmConfigId());
            form.setConcurrencyIdentityField(smConfigInstanceData.getConcurrencyIdentityField());
            form.setSearchAttribute(smConfigInstanceData.getSearchAttribute());
            form.setBatchUpdateEnabled(smConfigInstanceData.getBatchUpdateEnabled());
            form.setBatchSize(smConfigInstanceData.getBatchSize());
            form.setBatchUpdateInterval(smConfigInstanceData.getBatchUpdateInterval());
            form.setDbQueryTimeOut(smConfigInstanceData.getDbQueryTimeOut());
            form.setBehaviour(smConfigInstanceData.getBehaviour());
            form.setDbfailureaction(smConfigInstanceData.getDbfailureaction());
            form.setSessionOverrideAction(smConfigInstanceData.getSessionOverrideAction());
            form.setSessionOverrideColumn(smConfigInstanceData.getSessionOverrideColumn());
            form.setSessionStopAction(smConfigInstanceData.getSessionStopAction());
		}
		return form;
	}
	
	
	private List<ExternalSystemBean> convertToSessionCloserExternalSystemBean(List<SMSessionCloserESIRelData> sessionCloserESIRelList) {

		List<ExternalSystemBean> serverList = new ArrayList<ExternalSystemBean>();
		for (Iterator<SMSessionCloserESIRelData> iterator = sessionCloserESIRelList.iterator(); iterator.hasNext();) {
			SMSessionCloserESIRelData smSessionCloserESIRelData =  iterator.next();
			ExternalSystemBean externalSystemBean = new ExternalSystemBean();
			externalSystemBean.setName(smSessionCloserESIRelData.getExternalSystemData().getName()+"-W-"+smSessionCloserESIRelData.getWeightage());
			externalSystemBean.setValue(smSessionCloserESIRelData.getEsiInstanceId()+"-"+smSessionCloserESIRelData.getWeightage());
			serverList.add(externalSystemBean);
		}

		return serverList;

	}

	private List<SMSessionCloserESIRelData> getSessionCloserExternalSystemList(String externalSystemServers[], String smInstanceId) throws ArrayIndexOutOfBoundsException,NumberFormatException, DataManagerException{
		if(externalSystemServers!=null){
			List<SMSessionCloserESIRelData> externalSystemRelList= new ArrayList<SMSessionCloserESIRelData>();
			for (int i = 0; i < externalSystemServers.length; i++) {
				ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager=new ExternalSystemInterfaceBLManager();
				String splitStr[] = externalSystemServers[i].split("-");
				String esiInstanceId = splitStr[0];
				int weightage = Integer.parseInt(splitStr[1]);
				SMSessionCloserESIRelData externalSystemRelData = new SMSessionCloserESIRelData();
				externalSystemRelData.setEsiInstanceId(esiInstanceId);
				
				ExternalSystemInterfaceInstanceData esiData=externalSystemInterfaceBLManager.getExternalSystemInterfaceInstanceDataById(esiInstanceId);
				externalSystemRelData.setExternalSystemData(esiData);
				externalSystemRelData.setWeightage(weightage);

				externalSystemRelList.add(externalSystemRelData);
			}
			return externalSystemRelList;
		}
		return null;
	}
}
