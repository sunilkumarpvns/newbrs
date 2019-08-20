package com.elitecore.elitesm.web.diameter.diameterconcurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyFieldMapping;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.exception.DuplicateRadiusPolicyNameException;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.diameterconcurrency.form.DiameterConcurrencyForm;

public class UpdateDiameterConcurrencyAction extends BaseWebAction{
	protected static final String MODULE = "UpdateDiameterConcurrencyAction";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_CONCURRENCY;
	private static final String INIT_UPDATE_FORWARD="updateDiameterConcurrency";

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			DiameterConcurrencyBLManager blManager = new DiameterConcurrencyBLManager();
			try{
				DiameterConcurrencyForm diameterConcurrencyForm = (DiameterConcurrencyForm)form;
				String diameterConConfigId = "";
				if(diameterConcurrencyForm.getDiaConConfigId() != null)
					diameterConConfigId = diameterConcurrencyForm.getDiaConConfigId();
					
				if(diameterConcurrencyForm.getAction() == null){
					
					if(Strings.isNullOrBlank(diameterConConfigId) == false){
						
						DiameterConcurrencyData diameterConcurrencyData = new DiameterConcurrencyData();
						diameterConcurrencyData.setDiaConConfigId(diameterConConfigId);
						
						diameterConcurrencyData = blManager.getDiameterConcurrencyDataById(diameterConConfigId);
						
						convertBeantoForm(diameterConcurrencyData, diameterConcurrencyForm);
						convertBeanDataToMappings(diameterConcurrencyData, diameterConcurrencyForm);
						
						DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
						List<IDatabaseDSData> lstDatasource = databaseDSBLManager.getDatabaseDSList();
						   
						diameterConcurrencyForm.setLstDatasource(lstDatasource);
						
						request.setAttribute("diameterConcurrencyData",diameterConcurrencyData);
						request.setAttribute("diameterConcurrencyForm",diameterConcurrencyForm);
						request.setAttribute("lstDatasource", lstDatasource);
						request.setAttribute("mandatoryFieldMappingsList", diameterConcurrencyForm.getMandatoryFieldMappingsList());
						request.setAttribute("additionalFieldMappingsList", diameterConcurrencyForm.getAdditionalFieldMappingsList());
					}

					return mapping.findForward(INIT_UPDATE_FORWARD);
					
				}else if(diameterConcurrencyForm.getAction().equalsIgnoreCase("update")){

					DiameterConcurrencyData diameterConcurrencyData = new DiameterConcurrencyData();
					diameterConcurrencyData.setDiaConConfigId(diameterConConfigId);
					
					diameterConcurrencyData = blManager.getDiameterConcurrencyDataById(diameterConConfigId);
					
					String lstFieldMappingJson = diameterConcurrencyForm.getLstFieldMapping();
					List<ConcurrencyFieldMappingData> concurrencyFieldMappingDataList = new  ArrayList<ConcurrencyFieldMappingData>();
					
					if(lstFieldMappingJson != null && lstFieldMappingJson.length() > 0){
						 JSONArray cdrGenerationArray = JSONArray.fromObject(lstFieldMappingJson);
						 for(Object  obj: cdrGenerationArray){
							 Map<String,Class> configObj = new HashMap<String, Class>();
							 ConcurrencyFieldMappingData concurrencyFieldMappingData = (ConcurrencyFieldMappingData) JSONObject.toBean((JSONObject) obj, ConcurrencyFieldMappingData.class,configObj);
							 concurrencyFieldMappingDataList.add(concurrencyFieldMappingData);
						 }
					}
					
					convertDBFieldMapping(diameterConcurrencyData,concurrencyFieldMappingDataList);
					
					convertFormtoBean(diameterConcurrencyData , diameterConcurrencyForm);
					blManager.verifyDiameterConcurrencyName(diameterConcurrencyData.getDiaConConfigId(), diameterConcurrencyData.getName());

					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
					staffData.setAuditName(diameterConcurrencyData.getName());
					staffData.setAuditId(diameterConcurrencyData.getAuditUId());
					
					blManager.updateDiameterConcurrencyById(diameterConcurrencyData, staffData);
					doAuditing(staffData, ACTION_ALIAS);
					
					request.setAttribute("responseUrl","/viewDiameterConcurrency.do?diaConConfigId="+diameterConcurrencyData.getDiaConConfigId());
		            ActionMessage message = new ActionMessage("diameterconcurrency.update.success");
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", message);
		            saveMessages(request, messages);
		            return mapping.findForward(SUCCESS);
				}
			}catch(DuplicateRadiusPolicyNameException drpException){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + drpException.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(drpException);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("diameterconcurrency.update.failure");
				ActionMessage message1 = new ActionMessage("diameterconcurrency.duplicatenamefound");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				messages.add("information", message1);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("diameterconcurrency.update.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
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
		return mapping.findForward(FAILURE);
	}

	private void convertDBFieldMapping(DiameterConcurrencyData diameterConcurrencyData,List<ConcurrencyFieldMappingData> concurrencyFieldMappingDataList) {
		List<DiameterConcurrencyFieldMapping> diameterFieldMappingList = new  ArrayList<DiameterConcurrencyFieldMapping>();
		
		/*set is used for audit history purpose*/
		Set<DiameterConcurrencyFieldMapping> diameterFieldMapSet = new LinkedHashSet<DiameterConcurrencyFieldMapping>();
		
		if(concurrencyFieldMappingDataList != null && concurrencyFieldMappingDataList.size() > 0){
			for(ConcurrencyFieldMappingData concurrencyFieldMappingData : concurrencyFieldMappingDataList){
				
				DiameterConcurrencyFieldMapping diameterConcurrencyFieldMapping = new  DiameterConcurrencyFieldMapping();
				diameterConcurrencyFieldMapping.setLogicalField(concurrencyFieldMappingData.getField());
				diameterConcurrencyFieldMapping.setDbFieldName(concurrencyFieldMappingData.getDbFieldName());
				diameterConcurrencyFieldMapping.setDataType(concurrencyFieldMappingData.getDataType());
				diameterConcurrencyFieldMapping.setDefaultValue(concurrencyFieldMappingData.getDefaultValue());
				diameterConcurrencyFieldMapping.setIncludeInASR(concurrencyFieldMappingData.getIncludeInASR());
				diameterConcurrencyFieldMapping.setReferringAttribute(concurrencyFieldMappingData.getReferringAttribute());
				
				diameterFieldMappingList.add(diameterConcurrencyFieldMapping);
				diameterFieldMapSet.add(diameterConcurrencyFieldMapping);
			}
		}
		
		diameterConcurrencyData.setDiameterConcurrencyFieldMappingList(diameterFieldMappingList);
	}

	private void convertBeanDataToMappings( DiameterConcurrencyData diameterConcurrencyData, DiameterConcurrencyForm diameterConcurrencyForm) {
		if(Collectionz.isNullOrEmpty(diameterConcurrencyData.getDiameterConcurrencyFieldMappingList()) == false){
			
			List<DiameterConcurrencyFieldMapping> mandatoryFieldMappingList = new ArrayList<DiameterConcurrencyFieldMapping>();
			List<DiameterConcurrencyFieldMapping> additionalFieldMappingList = new ArrayList<DiameterConcurrencyFieldMapping>();
			
			for(DiameterConcurrencyFieldMapping diameterFieldMapping : diameterConcurrencyData.getDiameterConcurrencyFieldMappingList()){
				if(diameterFieldMapping.getLogicalField() != null && diameterFieldMapping.getLogicalField().length()>0){
					mandatoryFieldMappingList.add(diameterFieldMapping);
				}else{
					additionalFieldMappingList.add(diameterFieldMapping);
				}
			}
			
			diameterConcurrencyForm.setMandatoryFieldMappingsList(mandatoryFieldMappingList);
			diameterConcurrencyForm.setAdditionalFieldMappingsList(additionalFieldMappingList);
		}
	}

	private void convertFormtoBean(DiameterConcurrencyData diameterConcurrencyData, DiameterConcurrencyForm diameterConcurrencyForm) {
		diameterConcurrencyData.setDiaConConfigId(diameterConcurrencyForm.getDiaConConfigId());
		diameterConcurrencyData.setName(diameterConcurrencyForm.getName());
		diameterConcurrencyData.setDescription(diameterConcurrencyForm.getDescription());
		diameterConcurrencyData.setDatabaseDsId(diameterConcurrencyForm.getDatabaseDsId());
		diameterConcurrencyData.setTableName(diameterConcurrencyForm.getTableName());
		diameterConcurrencyData.setStartTimeField(diameterConcurrencyForm.getStartTimeField());
		diameterConcurrencyData.setLastUpdateTimeField(diameterConcurrencyForm.getLastUpdateTimeField());
		diameterConcurrencyData.setConcurrencyIdentityField(diameterConcurrencyForm.getConcurrencyIdentityField());
		diameterConcurrencyData.setDbFailureAction(diameterConcurrencyForm.getDbFailureAction());
		diameterConcurrencyData.setSessionOverrideAction(diameterConcurrencyForm.getSessionOverrideAction());
		diameterConcurrencyData.setSessionOverrideFields(diameterConcurrencyForm.getSessionOverrideFields());
		diameterConcurrencyData.setAuditUId(diameterConcurrencyData.getAuditUId());
	}

	private void convertBeantoForm(DiameterConcurrencyData diameterConcurrencyData, DiameterConcurrencyForm diameterConcurrencyForm) {
		diameterConcurrencyForm.setDiaConConfigId(diameterConcurrencyData.getDiaConConfigId());
		diameterConcurrencyForm.setName(diameterConcurrencyData.getName());
		diameterConcurrencyForm.setDescription(diameterConcurrencyData.getDescription());
		diameterConcurrencyForm.setDatabaseDsId(diameterConcurrencyData.getDatabaseDsId());
		diameterConcurrencyForm.setTableName(diameterConcurrencyData.getTableName());
		diameterConcurrencyForm.setStartTimeField(diameterConcurrencyData.getStartTimeField());
		diameterConcurrencyForm.setLastUpdateTimeField(diameterConcurrencyData.getLastUpdateTimeField());
		diameterConcurrencyForm.setConcurrencyIdentityField(diameterConcurrencyData.getConcurrencyIdentityField());
		diameterConcurrencyForm.setDbFailureAction(diameterConcurrencyData.getDbFailureAction());
		diameterConcurrencyForm.setSessionOverrideAction(diameterConcurrencyData.getSessionOverrideAction());
		diameterConcurrencyForm.setSessionOverrideFields(diameterConcurrencyData.getSessionOverrideFields());
		diameterConcurrencyForm.setAuditUId(diameterConcurrencyData.getAuditUId());
	}
}
