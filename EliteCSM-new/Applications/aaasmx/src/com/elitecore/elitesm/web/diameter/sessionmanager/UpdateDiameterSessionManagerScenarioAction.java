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
import com.elitecore.elitesm.web.diameter.sessionmanager.form.UpdateDiameterSessionManagerForm;


public class UpdateDiameterSessionManagerScenarioAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_SESSION_MANAGER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			UpdateDiameterSessionManagerForm updateDiameterSessionManagerForm = (UpdateDiameterSessionManagerForm) form;
			DiameterSessionManagerBLManager blManager = new DiameterSessionManagerBLManager();
			
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
							scenarioMappingData.setDescription(scenarioDescription[j]);;
								
							scenarioMappingDataSet.add(scenarioMappingData);
							Logger.logInfo(MODULE, "scenarioMappingData: "+ scenarioMappingData);
						}
					}
			}
			
			updateDiameterSessionManagerForm.setScenarioMappingDataSet(scenarioMappingDataSet);
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
			updateDiameterSessionManagerForm.setSessionOverideActionDataSet(sessionOverideActionDataSet);
			DiameterSessionManagerData diameterSessionManagerData = new DiameterSessionManagerData();
		
			diameterSessionManagerData = blManager.getDiameterSessionManagerDataById(updateDiameterSessionManagerForm.getSessionManagerId());
			
			diameterSessionManagerData = convertFormToBean(diameterSessionManagerData,updateDiameterSessionManagerForm);
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias=ACTION_ALIAS;
			staffData.setAuditId(diameterSessionManagerData.getAuditUId());
			staffData.setAuditName(diameterSessionManagerData.getName());
			
			blManager.updateScenarioData(diameterSessionManagerData,staffData,actionAlias);
			
			request.setAttribute("responseUrl","/viewDiameterSessionManager.do?sessionManagerId="+updateDiameterSessionManagerForm.getSessionManagerId()); 
			ActionMessage message = new ActionMessage("diametersessionmanager.update.success");
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
	private DiameterSessionManagerData convertFormToBean(DiameterSessionManagerData diameterSessionManagerData,	UpdateDiameterSessionManagerForm updateDiameterSessionManagerForm) {
		diameterSessionManagerData.setSessionManagerId(updateDiameterSessionManagerForm.getSessionManagerId());
		diameterSessionManagerData.setScenarioMappingDataSet(updateDiameterSessionManagerForm.getScenarioMappingDataSet());
		diameterSessionManagerData.setSessionOverideActionDataSet(updateDiameterSessionManagerForm.getSessionOverideActionDataSet());
		diameterSessionManagerData.setAuditUId(updateDiameterSessionManagerForm.getAuditUId());
		return diameterSessionManagerData;
	}

}
