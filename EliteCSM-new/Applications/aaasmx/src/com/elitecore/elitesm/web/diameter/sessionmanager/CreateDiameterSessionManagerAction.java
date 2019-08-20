package com.elitecore.elitesm.web.diameter.sessionmanager;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.ScenarioMappingData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionOverideActionData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.sessionmanager.form.CreateDiameterSessionManagerForm;


public class CreateDiameterSessionManagerAction extends BaseWebAction {

	private static final String CREATE_FORWARD = "createDiameterSessionManagerScenarioMapping";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_SESSION_MANAGER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			CreateDiameterSessionManagerForm createDiameterSessionManagerForm = (CreateDiameterSessionManagerForm) form;
			
			Set<ScenarioMappingData> scenarioMappingDataSet = new LinkedHashSet<ScenarioMappingData>();

			String scenarioName[] = request.getParameterValues("scenarioName");
			String scenarioDescription[] = request.getParameterValues("scenarioDescription");
			String scenarioRuleset[] = request.getParameterValues("scenarioRuleset");
			String scenarioCriteria[] = request.getParameterValues("scenarioCriteria");
			String scenarioMapping[] = request.getParameterValues("scenarioMapping");

			if( scenarioRuleset!=null && scenarioCriteria!=null && scenarioMapping!=null && scenarioRuleset.length==scenarioCriteria.length && scenarioCriteria.length==scenarioMapping.length){
					for (int j = 0; j < scenarioRuleset.length; j++) {
						if(scenarioRuleset[j]!=null && scenarioRuleset[j].trim().length()>0){
							ScenarioMappingData scenarioMappingData = new ScenarioMappingData();
							scenarioMappingData.setCriteria(scenarioCriteria[j]);
							scenarioMappingData.setRuleset(scenarioRuleset[j]);
							scenarioMappingData.setMappingName(scenarioMapping[j]);
							scenarioMappingData.setName(scenarioName[j]);
							scenarioMappingData.setDescription(scenarioDescription[j]);
								
							scenarioMappingDataSet.add(scenarioMappingData);
							Logger.logInfo(MODULE, "scenarioMappingData: "+ scenarioMappingData);
						}
					}
			}
			
			createDiameterSessionManagerForm.setScenarioMappingDataSet(scenarioMappingDataSet);
			
			Set<SessionOverideActionData> sessionOverideActionDataSet = new LinkedHashSet<SessionOverideActionData>();

			String sessionName[] = request.getParameterValues("sessionName");
			String sessionDescription[] = request.getParameterValues("sessionDescription");
			String sessionRuleset[] = request.getParameterValues("sessionRuleset");
			String sessionCriteria[] = request.getParameterValues("sessionCriteria");

			if( sessionRuleset!=null && sessionCriteria!=null && sessionRuleset.length==sessionCriteria.length){
					for (int j = 0; j < sessionRuleset.length; j++) {
						if(sessionRuleset[j]!=null && sessionRuleset[j].trim().length()>0){
							SessionOverideActionData sessionOverideActionData = new SessionOverideActionData();
							sessionOverideActionData.setActions(sessionCriteria[j]);
							sessionOverideActionData.setRuleset(sessionRuleset[j]);
							sessionOverideActionData.setDescription(sessionDescription[j]);
							sessionOverideActionData.setName(sessionName[j]);
								
							sessionOverideActionDataSet.add(sessionOverideActionData);
							Logger.logInfo(MODULE, "sessionOverideActionData: "+ sessionOverideActionData);
						}
					}
			}
			createDiameterSessionManagerForm.setSessionOverideActionDataSet(sessionOverideActionDataSet);
			DiameterSessionManagerData diameterSessionManagerData = new DiameterSessionManagerData();
			
			diameterSessionManagerData = convertFormToBean(diameterSessionManagerData,createDiameterSessionManagerForm);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			DiameterSessionManagerBLManager blManager=new DiameterSessionManagerBLManager();
			blManager.create(diameterSessionManagerData,staffData);
			
			request.setAttribute("responseUrl","/searchDiameterSessionManager.do");
			ActionMessage message = new ActionMessage("diametersessionmanager.create.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveMessages(request,messages);
			return mapping.findForward(SUCCESS);
			
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private DiameterSessionManagerData convertFormToBean(DiameterSessionManagerData diameterSessionManagerData,	CreateDiameterSessionManagerForm createDiameterSessionManagerForm) {
		diameterSessionManagerData.setName(createDiameterSessionManagerForm.getName());
		
		if(createDiameterSessionManagerForm.getDatabaseId().equals("0")){
			diameterSessionManagerData.setDiameterSessionManagerMappingData(null);
		}else{
			diameterSessionManagerData.setDatabaseDatasourceId(createDiameterSessionManagerForm.getDatabaseId());
		}
		
		diameterSessionManagerData.setDescription(createDiameterSessionManagerForm.getDescription());
		diameterSessionManagerData.setTableName(createDiameterSessionManagerForm.getTablename());
		diameterSessionManagerData.setSequenceName(createDiameterSessionManagerForm.getSequenceName());
		diameterSessionManagerData.setStartTimeField(createDiameterSessionManagerForm.getStartTimeField());
		diameterSessionManagerData.setLastUpdatedTimeField(createDiameterSessionManagerForm.getLastUpdatedTimeField());
		diameterSessionManagerData.setDbQueryTimeout(createDiameterSessionManagerForm.getDbQueryTimeOut());
		diameterSessionManagerData.setDelimeter(createDiameterSessionManagerForm.getMultiValueDelimeter());
		diameterSessionManagerData.setDbFailureAction(createDiameterSessionManagerForm.getDbFailureAction());
		diameterSessionManagerData.setBatchEnabled(String.valueOf(createDiameterSessionManagerForm.isBatchEnabled()));
		diameterSessionManagerData.setBatchSize(createDiameterSessionManagerForm.getBatchSize());
		diameterSessionManagerData.setBatchInterval(createDiameterSessionManagerForm.getBatchInterval());
		diameterSessionManagerData.setBatchQueryTimeout(createDiameterSessionManagerForm.getBatchQueryTimeout());
		diameterSessionManagerData.setBatchedInsert(String.valueOf(createDiameterSessionManagerForm.isBatchedInsert()));
		diameterSessionManagerData.setBatchedDelete(String.valueOf(createDiameterSessionManagerForm.isBatchedDelete()));
		diameterSessionManagerData.setBatchedUpdate(String.valueOf(createDiameterSessionManagerForm.isBatchedUpdate()));
		diameterSessionManagerData.setDiameterSessionManagerMappingData(createDiameterSessionManagerForm.getDiameterSessionManagerMappingDataSet());
		diameterSessionManagerData.setScenarioMappingDataSet(createDiameterSessionManagerForm.getScenarioMappingDataSet());
		diameterSessionManagerData.setSessionOverideActionDataSet(createDiameterSessionManagerForm.getSessionOverideActionDataSet());
		return diameterSessionManagerData;
	}
}
