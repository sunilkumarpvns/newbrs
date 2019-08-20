package com.elitecore.elitesm.web.diameter.sessionmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
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

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerMappingData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.SessionManagerFieldMappingData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.sessionmanager.form.CreateDiameterSessionManagerForm;

public class CreateDiameterSessionManagerScenarioAction extends BaseWebAction {

	private static final String CREATE_FORWARD = "createDiameterSessionManagerScenarioMapping";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_SESSION_MANAGER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			CreateDiameterSessionManagerForm createDiameterSessionManagerForm = (CreateDiameterSessionManagerForm) form;
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			List<IDatabaseDSData> databaseDsDataList = databaseDSBLManager.getDatabaseDSList();
			
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

			JSONObject tableFieldMappingsJson = new JSONObject();
			for (Iterator diameterSessionManagerMappingIterator = diameterSessionManagerMappingDataSet.iterator(); diameterSessionManagerMappingIterator.hasNext();) {
				DiameterSessionManagerMappingData diameterSessionManagerMappingData = (DiameterSessionManagerMappingData) diameterSessionManagerMappingIterator.next();
				
				JSONArray dbFieldMappingArray = new JSONArray();
				
				Set<SessionManagerFieldMappingData> sessionManagerFieldMappingDataSet = diameterSessionManagerMappingData.getSessionManagerFieldMappingData();
				for (Iterator sessionManagerFieldMappingIterator = sessionManagerFieldMappingDataSet.iterator(); sessionManagerFieldMappingIterator.hasNext();) {
					SessionManagerFieldMappingData sessionManagerFieldMappingData = (SessionManagerFieldMappingData) sessionManagerFieldMappingIterator.next();
					dbFieldMappingArray.add(sessionManagerFieldMappingData.getDbFieldName());
				}
				
				tableFieldMappingsJson.put(diameterSessionManagerMappingData.getMappingName(), dbFieldMappingArray);
			}
			
			createDiameterSessionManagerForm.setDiameterSessionManagerMappingDataSet(diameterSessionManagerMappingDataSet);
			request.setAttribute("createDiameterSessionManagerForm", createDiameterSessionManagerForm);
			request.setAttribute("databaseDsDataList", databaseDsDataList);
			request.setAttribute("tableFieldMappingsJson", tableFieldMappingsJson);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			return mapping.findForward(CREATE_FORWARD);
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

		if( dbFieldName!=null && referringAttributes!=null && defaultValues!=null && dbFieldName.length==referringAttributes.length && referringAttributes.length==defaultValues.length){
			
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

}
