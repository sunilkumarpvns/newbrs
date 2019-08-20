package com.elitecore.elitesm.web.diameter.sessionmanager;

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

import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionManagerFieldMappingData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.sessionmanager.form.UpdateDiameterSessionManagerForm;

public class UpdateDiameterSessionManagerAction extends BaseWebAction {

	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_SESSION_MANAGER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			UpdateDiameterSessionManagerForm updateDiameterSessionManagerForm = (UpdateDiameterSessionManagerForm) form;
			
			String strMappingIndex = request.getParameter("mappingIndex");
			int mappingIndex = Integer.parseInt(strMappingIndex);
			Set<DiameterSessionManagerMappingData> diameterSessionManagerMappingDataSet = new LinkedHashSet<DiameterSessionManagerMappingData>();
			
			for(int i=0;i<mappingIndex ;i++){
				int index = i+1;
				String mappingName = request.getParameter("mappingName"+index);
				if(mappingName!=null && mappingName.trim().length()>0){
					DiameterSessionManagerMappingData diameterSessionManagerData = new DiameterSessionManagerMappingData();
					
					diameterSessionManagerData.setMappingName(mappingName);
					
					Set<SessionManagerFieldMappingData> sessionManagerFieldMapDataSet = new LinkedHashSet<SessionManagerFieldMappingData>();
					
					sessionManagerFieldMapDataSet.addAll(getMappingDetailList(request,index));
				
					diameterSessionManagerData.setSessionManagerFieldMappingData(sessionManagerFieldMapDataSet);
					diameterSessionManagerMappingDataSet.add(diameterSessionManagerData);
				}
			}
			updateDiameterSessionManagerForm.setDiameterSessionManagerMappingDataSet(diameterSessionManagerMappingDataSet);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			
			DiameterSessionManagerData diameterSessionManagerData = new DiameterSessionManagerData();
			DiameterSessionManagerBLManager blManager = new DiameterSessionManagerBLManager();
			
			diameterSessionManagerData = blManager.getDiameterSessionManagerDataById(updateDiameterSessionManagerForm.getSessionManagerId());
			
			diameterSessionManagerData = convertFormToBean(diameterSessionManagerData,updateDiameterSessionManagerForm);
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String actionAlias=ACTION_ALIAS;
			staffData.setAuditId(diameterSessionManagerData.getAuditUId());
			staffData.setAuditName(diameterSessionManagerData.getName());
			
			blManager.update(diameterSessionManagerData,staffData,actionAlias);
			
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
	private List<SessionManagerFieldMappingData> getMappingDetailList(HttpServletRequest request, int index){
		List<SessionManagerFieldMappingData> sessionManagerFieldMappingDataList = new ArrayList<SessionManagerFieldMappingData>();

		String dbFieldName[] = request.getParameterValues("dbFieldName"+index);
		String referringAttributes[] = request.getParameterValues("referringAttribute"+index);
		String defaultValues[] = request.getParameterValues("defaultValue"+index);
		String dataTypeValues[] = request.getParameterValues("dataType"+index);

		if( dbFieldName!=null && referringAttributes!=null && dbFieldName.length==referringAttributes.length){
			
				for (int j = 0; j < dbFieldName.length; j++) {
						
					if(dbFieldName[j]!=null && dbFieldName[j].trim().length()>0){
						SessionManagerFieldMappingData sessionManagerFieldMappingData = new SessionManagerFieldMappingData();
						sessionManagerFieldMappingData.setDataType(Long.parseLong(dataTypeValues[j]));
						sessionManagerFieldMappingData.setDbFieldName(dbFieldName[j]);
						sessionManagerFieldMappingData.setDefaultValue(defaultValues[j]);
						sessionManagerFieldMappingData.setReferringAttr(referringAttributes[j]);
						
						sessionManagerFieldMappingDataList.add(sessionManagerFieldMappingData);
						Logger.logInfo(MODULE, "sessionManagerFieldMappingData: "+ sessionManagerFieldMappingData);
					}
			}
		}
		return sessionManagerFieldMappingDataList;
	}
	private DiameterSessionManagerData convertFormToBean(DiameterSessionManagerData diameterSessionManagerData,	UpdateDiameterSessionManagerForm updateDiameterSessionManagerForm) {
		diameterSessionManagerData.setSessionManagerId(updateDiameterSessionManagerForm.getSessionManagerId());
		diameterSessionManagerData.setName(updateDiameterSessionManagerForm.getName());
		
		if(updateDiameterSessionManagerForm.getDatabaseId().equals("0")){
			diameterSessionManagerData.setDatabaseDatasourceId(null);
		}else{
			diameterSessionManagerData.setDatabaseDatasourceId(updateDiameterSessionManagerForm.getDatabaseId());
		}
		
		diameterSessionManagerData.setDescription(updateDiameterSessionManagerForm.getDescription());
		diameterSessionManagerData.setTableName(updateDiameterSessionManagerForm.getTablename());
		diameterSessionManagerData.setSequenceName(updateDiameterSessionManagerForm.getSequenceName());
		diameterSessionManagerData.setStartTimeField(updateDiameterSessionManagerForm.getStartTimeField());
		diameterSessionManagerData.setLastUpdatedTimeField(updateDiameterSessionManagerForm.getLastUpdatedTimeField());
		diameterSessionManagerData.setDbQueryTimeout(updateDiameterSessionManagerForm.getDbQueryTimeOut());
		diameterSessionManagerData.setDelimeter(updateDiameterSessionManagerForm.getMultiValueDelimeter());
		diameterSessionManagerData.setDbFailureAction(updateDiameterSessionManagerForm.getDbFailureAction());
		diameterSessionManagerData.setBatchEnabled(String.valueOf(updateDiameterSessionManagerForm.isBatchEnabled()));
		diameterSessionManagerData.setBatchSize(updateDiameterSessionManagerForm.getBatchSize());
		diameterSessionManagerData.setBatchInterval(updateDiameterSessionManagerForm.getBatchInterval());
		diameterSessionManagerData.setBatchQueryTimeout(updateDiameterSessionManagerForm.getBatchQueryTimeout());
		diameterSessionManagerData.setBatchedInsert(String.valueOf(updateDiameterSessionManagerForm.isBatchedInsert()));
		diameterSessionManagerData.setBatchedDelete(String.valueOf(updateDiameterSessionManagerForm.isBatchedDelete()));
		diameterSessionManagerData.setBatchedUpdate(String.valueOf(updateDiameterSessionManagerForm.isBatchedUpdate()));
		diameterSessionManagerData.setDiameterSessionManagerMappingData(updateDiameterSessionManagerForm.getDiameterSessionManagerMappingDataSet());
		diameterSessionManagerData.setAuditUId(updateDiameterSessionManagerForm.getAuditUId());
		return diameterSessionManagerData;
	}

}
