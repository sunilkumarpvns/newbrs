package com.elitecore.elitesm.web.sessionmanager;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISMConfigInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISMDBFieldMapData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMConfigInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMDBFieldMapData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SMSessionCloserESIRelData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.sessionmanager.forms.CreateSessionManagerDetailForm;

public class CreateSessionManagerDetailAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "success";
	private static final String VIEW_FORWARD = "viewSessionManagerDetail";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_SESSION_MANAGER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			try{
				CreateSessionManagerDetailForm createSessionManagerDetailForm = (CreateSessionManagerDetailForm)form;
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				SessionManagerBLManager blmanager = new SessionManagerBLManager();

				createSessionManagerDetailForm.setLstDatasource(databaseDSBLManager.getDatabaseDSList());
				if(createSessionManagerDetailForm.getCheckAction().equalsIgnoreCase("Add") ) {

					ISMDBFieldMapData dbFieldMapData = new SMDBFieldMapData();
					dbFieldMapData.setDbFieldName(createSessionManagerDetailForm.getDbFieldName());
					dbFieldMapData.setReferringEntity(createSessionManagerDetailForm.getReferringEntity());
					dbFieldMapData.setDataType(createSessionManagerDetailForm.getDataType());
					dbFieldMapData.setDefaultValue(createSessionManagerDetailForm.getDefaultValue());
					boolean flag=false;
					for(int i=0;i<createSessionManagerDetailForm.getLstDBFieldMapData().size();i++){
						String LstDBFieldName=createSessionManagerDetailForm.getLstDBFieldMapData().get(i).getDbFieldName();
						String addedDBFieldName=createSessionManagerDetailForm.getDbFieldName();
						if(addedDBFieldName.equalsIgnoreCase(LstDBFieldName)){
							flag=true;
							break;
						}
					}

					if(!flag){
						createSessionManagerDetailForm.getLstDBFieldMapData().add(dbFieldMapData);
					}

					createSessionManagerDetailForm.setCheckAction("");
					createSessionManagerDetailForm.setDbFieldName("");
					createSessionManagerDetailForm.setReferringEntity("");
					createSessionManagerDetailForm.setDataType(FieldMapping.STRING_TYPE);
					createSessionManagerDetailForm.setDefaultValue("");

					request.setAttribute("dbFieldMapList",createSessionManagerDetailForm.getLstDBFieldMapData());

				}
				if(createSessionManagerDetailForm.getCheckAction().equalsIgnoreCase("Remove") ) {

					createSessionManagerDetailForm.setCheckAction("");
					int index = createSessionManagerDetailForm.getItemIndex();
					List<ISMDBFieldMapData> lstDBFieldMapData = createSessionManagerDetailForm.getLstDBFieldMapData();
					if(lstDBFieldMapData != null && index < lstDBFieldMapData.size()){
						createSessionManagerDetailForm.getLstDBFieldMapData().remove(index);
					}

					request.setAttribute("dbFieldMapList",createSessionManagerDetailForm.getLstDBFieldMapData());

				}
				if(createSessionManagerDetailForm.getCheckAction().equalsIgnoreCase("Save")){

					String[] fieldName = request.getParameterValues("fieldVal");
					String[] referringEntityVl = request.getParameterValues("refEntVal");
					String[] dataTypeVl = request.getParameterValues("dtTypeVal");
					String[] defaultVl = request.getParameterValues("defaultValue");

					List<SMDBFieldMapData> tempfieldMapList = new ArrayList<SMDBFieldMapData>();
					if(fieldName != null){
						for(int i=0;i<fieldName.length;i++){
							SMDBFieldMapData tempData = new SMDBFieldMapData();
							tempData.setDbFieldName(fieldName[i]);
							tempData.setReferringEntity(referringEntityVl[i]);
							tempData.setDefaultValue(defaultVl[i]);
							tempData.setDataType(Integer.parseInt(dataTypeVl[i].trim()));

							tempfieldMapList.add(tempData);
						}
					}

					
					String[] dbField=request.getParameterValues("dbField");
					String[] field=request.getParameterValues("field");
					String[] referingAttrib=request.getParameterValues("referingAttrib");
					String[] mandatoryFieldDataType=request.getParameterValues("mandatoryFieldDataType");
					String[] defaultVal=request.getParameterValues("defaultVal");
					
					List<SMDBFieldMapData> tempMandatoryfieldMapList = new ArrayList<SMDBFieldMapData>();
					if(field != null){
						for(int i=0;i<field.length;i++){
							SMDBFieldMapData tempData = new SMDBFieldMapData();
							tempData.setField(field[i]);
							tempData.setDbFieldName(dbField[i]);
							tempData.setReferringEntity(referingAttrib[i]);
							tempData.setDefaultValue(defaultVal[i]);
							tempData.setDataType(Integer.parseInt(mandatoryFieldDataType[i].trim()));

							tempMandatoryfieldMapList.add(tempData);
						}
					}
					
					SessionManagerInstanceData sessionManagerInstanceData;
					SMConfigInstanceData smConfigInstanceData=new SMConfigInstanceData();
					
					sessionManagerInstanceData=(SessionManagerInstanceData)request.getSession().getAttribute("sessionManagerData");
					convertFormToBean(smConfigInstanceData,createSessionManagerDetailForm);
					smConfigInstanceData.setDbFieldMapDataList(tempfieldMapList);
					smConfigInstanceData.setLstMandatoryFieldMapData(tempMandatoryfieldMapList);
					List<SMSessionCloserESIRelData> smSessionCloserESIRelDataList = new ArrayList<SMSessionCloserESIRelData>();

					String nasClients[] = request.getParameterValues("nasClients");
					String acctServers[] = request.getParameterValues("acctServers");
                    if(acctServers != null && acctServers.length > 0)
                    {	
						List<SMSessionCloserESIRelData>  acctServerList = getExternalSystemList(acctServers);
						smSessionCloserESIRelDataList.addAll(acctServerList);
						smConfigInstanceData.setSmSessionCloserESIRelDataList(smSessionCloserESIRelDataList);
                    }
                    if(nasClients != null && nasClients.length > 0)
                    {
						List<SMSessionCloserESIRelData>  nasClientList = getExternalSystemList(nasClients);
						smSessionCloserESIRelDataList.addAll(nasClientList);
						smConfigInstanceData.setSmSessionCloserESIRelDataList(smSessionCloserESIRelDataList);
                    }	
					sessionManagerInstanceData.setSmConfigInstanceData(smConfigInstanceData);
					blmanager.create(sessionManagerInstanceData,staffData);
					request.getSession().removeAttribute("createSessionManagerDetailForm");
					request.getSession().removeAttribute("dbFieldMapList");
					request.getSession().removeAttribute("createSessionManagerForm");

					request.setAttribute("responseUrl","/initSearchSessionManager"); 
					ActionMessage message = new ActionMessage("sessionmanager.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
					return mapping.findForward(SUCCESS_FORWARD);

				}
				return mapping.findForward(VIEW_FORWARD);
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("sessionmanager.create.failure"));
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);
			}catch(Exception e){
				Logger.logError(MODULE, "Error, reason : " + e.getMessage());            
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("sessionmanager.create.failure"));
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);
			}

		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	private void convertFormToBean(ISMConfigInstanceData smConfigInstanceData,CreateSessionManagerDetailForm createSessionManagerDetailForm) {
		smConfigInstanceData.setDatabaseDatasourceId(createSessionManagerDetailForm.getDatabaseId());
		smConfigInstanceData.setTablename(createSessionManagerDetailForm.getTablename());
		smConfigInstanceData.setAutoSessionCloser(createSessionManagerDetailForm.getAutosessioncloser());
		smConfigInstanceData.setSessiontimeout(createSessionManagerDetailForm.getSessionTimeout());
		smConfigInstanceData.setCloseBatchCount(createSessionManagerDetailForm.getCloseBatchCount());
		smConfigInstanceData.setSessionThreadSleepTime(createSessionManagerDetailForm.getSessionThreadSleeptime());
		smConfigInstanceData.setSessionCloseAction(createSessionManagerDetailForm.getSessionCloseAction());
		smConfigInstanceData.setIdSequenceName(createSessionManagerDetailForm.getIdSequenceName());
		smConfigInstanceData.setStartTimeField(createSessionManagerDetailForm.getStartTimeField());
		smConfigInstanceData.setLastUpdatedTimeField(createSessionManagerDetailForm.getLastUpdatedTimeField());
		smConfigInstanceData.setServiceTypeField(createSessionManagerDetailForm.getServiceTypeField());
		smConfigInstanceData.setGroupNameField(createSessionManagerDetailForm.getGroupNameField());
		smConfigInstanceData.setConcurrencyIdentityField(createSessionManagerDetailForm.getConcurrencyIdentityField());
		smConfigInstanceData.setSearchAttribute(createSessionManagerDetailForm.getSearchAttribute());
		smConfigInstanceData.setBatchUpdateEnabled(createSessionManagerDetailForm.getBatchUpdateEnabled());
		smConfigInstanceData.setBatchSize(createSessionManagerDetailForm.getBatchSize());
		smConfigInstanceData.setBatchUpdateInterval(createSessionManagerDetailForm.getBatchUpdateInterval());
		smConfigInstanceData.setDbQueryTimeOut(createSessionManagerDetailForm.getDbQueryTimeOut());
		smConfigInstanceData.setBehaviour(createSessionManagerDetailForm.getBehaviour());
		smConfigInstanceData.setDbfailureaction(createSessionManagerDetailForm.getDbfailureaction());
		smConfigInstanceData.setSessionOverrideAction(createSessionManagerDetailForm.getSessionOverrideAction());
		smConfigInstanceData.setSessionOverrideColumn(createSessionManagerDetailForm.getSessionOverrideColumn());
		smConfigInstanceData.setSessionStopAction(createSessionManagerDetailForm.getSessionStopAction());
	}
	private List<SMSessionCloserESIRelData> getExternalSystemList(String externalSystemServers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException, DataManagerException{
		if(externalSystemServers!=null){
			List<SMSessionCloserESIRelData> externalSystemRelList= new ArrayList<SMSessionCloserESIRelData>();
			for (int i = 0; i < externalSystemServers.length; i++) {
				ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager=new ExternalSystemInterfaceBLManager();
				
				String splitStr[] = externalSystemServers[i].split("-");
				String esiInstanceId = splitStr[0];
				int weightage = Integer.parseInt(splitStr[1]);
				SMSessionCloserESIRelData externalSystemRelData = new SMSessionCloserESIRelData();
				externalSystemRelData.setEsiInstanceId(esiInstanceId);
				externalSystemRelData.setWeightage(weightage);
				ExternalSystemInterfaceInstanceData esiData=externalSystemInterfaceBLManager.getExternalSystemInterfaceInstanceDataById(esiInstanceId);
				externalSystemRelData.setExternalSystemData(esiData);
				externalSystemRelList.add(externalSystemRelData);
			}
			return externalSystemRelList;
		}
		return null;
	}
}
