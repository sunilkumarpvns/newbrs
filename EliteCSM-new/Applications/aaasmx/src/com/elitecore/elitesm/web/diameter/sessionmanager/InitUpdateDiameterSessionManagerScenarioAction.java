package com.elitecore.elitesm.web.diameter.sessionmanager;

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

import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.sessionmanager.form.UpdateDiameterSessionManagerForm;

public class InitUpdateDiameterSessionManagerScenarioAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="SESSION-MGR-CONF";
	private static final String INITUPDATEDIAMETERSESSIONMGRSCENARIO = "initUpdateDiameterSessionManagerScenario"; 
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_SESSION_MANAGER;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			UpdateDiameterSessionManagerForm updateDiameterSessionManagerForm = (UpdateDiameterSessionManagerForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
		
			DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
			
			DiameterSessionManagerData diameterSessionManagerData = diameterSessionManagerBLManager.getDiameterSessionManagerDataById(updateDiameterSessionManagerForm.getSessionManagerId());
			
			List<DiameterSessionManagerData> diameterSessionManagerDatasList = diameterSessionManagerBLManager.getDiameterSessionManagerDatas();
			updateDiameterSessionManagerForm.setDiameterSessionMappingDataList(diameterSessionManagerDatasList);
			
			updateDiameterSessionManagerForm.setSessionOverideActionDataSet(diameterSessionManagerData.getSessionOverideActionDataSet());
			updateDiameterSessionManagerForm.setScenarioMappingDataSet(diameterSessionManagerData.getScenarioMappingDataSet());
			
			List<DiameterSessionManagerMappingData> diameterSessionManagerMappingDatas = diameterSessionManagerBLManager.getDiameterSessionManagerMappingList(updateDiameterSessionManagerForm.getSessionManagerId());
			Set<DiameterSessionManagerMappingData> diameterSessionManagerMappingDatasSet = new LinkedHashSet<DiameterSessionManagerMappingData>();
			
			diameterSessionManagerMappingDatasSet.addAll(diameterSessionManagerMappingDatas);
			updateDiameterSessionManagerForm.setDiameterSessionManagerMappingDataSet(diameterSessionManagerMappingDatasSet);
			convertBeanToForm(updateDiameterSessionManagerForm,diameterSessionManagerData);
			request.setAttribute("diameterSessionManagerData",diameterSessionManagerData);
			request.setAttribute("diameterSessionManagerMappingDataSet", diameterSessionManagerMappingDatas);
			request.setAttribute("updateDiameterSessionManagerForm", updateDiameterSessionManagerForm);
			return mapping.findForward(INITUPDATEDIAMETERSESSIONMGRSCENARIO);
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
