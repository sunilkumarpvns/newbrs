package com.elitecore.elitesm.web.diameter.sessionmanager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.ScenarioMappingData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.sessionmanager.form.UpdateDiameterSessionManagerForm;
import com.google.gson.Gson;

public class InitUpdateDiameterSessionManagerAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="SESSION-MGR-CONF";
	private static final String INITUPDATEDIAMETERSESSIONMGR = "initUpdateDiameterSessionManager"; 
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_SESSION_MANAGER;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			UpdateDiameterSessionManagerForm updateDiameterSessionManagerForm = (UpdateDiameterSessionManagerForm)form;
			
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			List<IDatabaseDSData> databaseDsDataList = databaseDSBLManager.getDatabaseDSList();
			updateDiameterSessionManagerForm.setLstDatasource(databaseDsDataList);
		
			DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
			List<DiameterSessionManagerData> diameterSessionManagerDatasList = diameterSessionManagerBLManager.getDiameterSessionManagerDatas();
			updateDiameterSessionManagerForm.setDiameterSessionMappingDataList(diameterSessionManagerDatasList);
			
			DiameterSessionManagerData diameterSessionManagerData = diameterSessionManagerBLManager.getDiameterSessionManagerDataById(updateDiameterSessionManagerForm.getSessionManagerId());
			
			Map<String, Set<String>> bindedValues = new HashMap<String, Set<String>>(); 
			Set<ScenarioMappingData> scenarioMappingDataSet = diameterSessionManagerData.getScenarioMappingDataSet();
			if(scenarioMappingDataSet != null){
				for (Iterator iterator = scenarioMappingDataSet.iterator(); iterator.hasNext();) {
					ScenarioMappingData scenarioMappingData = (ScenarioMappingData) iterator.next();
					
					String mappingName = scenarioMappingData.getMappingName();
					String criteria = scenarioMappingData.getCriteria();
					
					Set<String> bindedDbFieldValues;
					if(bindedValues.containsKey(mappingName)){
						bindedDbFieldValues = bindedValues.get(mappingName);
					} else {
						bindedDbFieldValues = new HashSet<String>();
					}
					
					String[] token = criteria.split("[,;]");
					int tokenSize = token.length;
					for (int i = 0; i < tokenSize; i++) {
						bindedDbFieldValues.add(token[i].trim());
					}
					
					bindedValues.put(mappingName, bindedDbFieldValues);
				}
			}
			convertBeanToForm(updateDiameterSessionManagerForm,diameterSessionManagerData);
			request.setAttribute("lstDatasource",databaseDsDataList);
			request.setAttribute("diameterSessionManagerData",diameterSessionManagerData);
			request.setAttribute("diameterSessionMappingDataList", diameterSessionManagerDatasList);
			request.setAttribute("updateDiameterSessionManagerForm", updateDiameterSessionManagerForm);
			
			Gson gson = new Gson();
			request.setAttribute("bindedValuesJson",gson.toJson(bindedValues));
			return mapping.findForward(INITUPDATEDIAMETERSESSIONMGR);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}                                                                                           
		return mapping.findForward(FAILURE_FORWARD); 
	}

	private void convertBeanToForm(UpdateDiameterSessionManagerForm updateDiameterSessionManagerForm,DiameterSessionManagerData diameterSessionManagerData) {
		updateDiameterSessionManagerForm.setSessionManagerId(diameterSessionManagerData.getSessionManagerId());
		updateDiameterSessionManagerForm.setName(diameterSessionManagerData.getName());
		updateDiameterSessionManagerForm.setDescription(diameterSessionManagerData.getDescription());
		updateDiameterSessionManagerForm.setDatabaseId(diameterSessionManagerData.getDatabaseDatasourceId());
		updateDiameterSessionManagerForm.setTablename(diameterSessionManagerData.getTableName());
		updateDiameterSessionManagerForm.setStartTimeField(diameterSessionManagerData.getStartTimeField());
		updateDiameterSessionManagerForm.setSequenceName(diameterSessionManagerData.getSequenceName());
		updateDiameterSessionManagerForm.setLastUpdatedTimeField(diameterSessionManagerData.getLastUpdatedTimeField());
		updateDiameterSessionManagerForm.setDbQueryTimeOut(diameterSessionManagerData.getDbQueryTimeout());
		updateDiameterSessionManagerForm.setMultiValueDelimeter(diameterSessionManagerData.getDelimeter());
		updateDiameterSessionManagerForm.setDbFailureAction(diameterSessionManagerData.getDbFailureAction());
		updateDiameterSessionManagerForm.setBatchEnabled(Boolean.parseBoolean(diameterSessionManagerData.getBatchEnabled()));
		updateDiameterSessionManagerForm.setBatchSize(diameterSessionManagerData.getBatchSize());
		updateDiameterSessionManagerForm.setBatchInterval(diameterSessionManagerData.getBatchInterval());
		updateDiameterSessionManagerForm.setBatchQueryTimeout(diameterSessionManagerData.getBatchQueryTimeout());
		updateDiameterSessionManagerForm.setBatchedDelete(Boolean.parseBoolean(diameterSessionManagerData.getBatchedDelete()));
		updateDiameterSessionManagerForm.setBatchedInsert(Boolean.parseBoolean(diameterSessionManagerData.getBatchedInsert()));
		updateDiameterSessionManagerForm.setBatchedUpdate(Boolean.parseBoolean(diameterSessionManagerData.getBatchedUpdate()));
		updateDiameterSessionManagerForm.setAuditUId(diameterSessionManagerData.getAuditUId());
	}
}
