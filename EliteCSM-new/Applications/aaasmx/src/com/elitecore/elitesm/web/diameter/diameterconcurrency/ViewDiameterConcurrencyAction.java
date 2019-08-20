package com.elitecore.elitesm.web.diameter.diameterconcurrency;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyFieldMapping;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.diameterconcurrency.form.DiameterConcurrencyForm;

public class ViewDiameterConcurrencyAction extends BaseWebAction{
	protected static final String MODULE = "ViewDiameterConcurrencyAction";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_DIAMETER_CONCURRENCY;
	private static final String VIEW_FORWARD = "viewDiameterConcurrency";

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			try {
				Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
				DiameterConcurrencyBLManager diameterConcurrencyBLManager = new DiameterConcurrencyBLManager();
				DiameterConcurrencyForm diameterConcurrencyForm = (DiameterConcurrencyForm)form;
				String strDiaConConfigId = request.getParameter("diaConConfigId");
				String diaConConfigId = "";
				if(Strings.isNullOrBlank(strDiaConConfigId) == false){
					diaConConfigId = strDiaConConfigId;
				}
				if(Strings.isNullOrBlank(diaConConfigId) == false){
					
					DiameterConcurrencyData diameterConcurrencyData = new  DiameterConcurrencyData();
					diameterConcurrencyData.setDiaConConfigId(diaConConfigId);

					diameterConcurrencyData = diameterConcurrencyBLManager.getDiameterConcurrencyDataById(diaConConfigId);
					convertBeanDataToMappings(diameterConcurrencyData, diameterConcurrencyForm);
					
					/* Retrive Database Datasource Details*/
					DatabaseDSBLManager databaseDSBLManager= new DatabaseDSBLManager();
					IDatabaseDSData databaseDSData= new DatabaseDSData();
					databaseDSData = databaseDSBLManager.getDatabaseDSDataById(diameterConcurrencyData.getDatabaseDsId());
					request.setAttribute("databaseDSData",databaseDSData);
					request.setAttribute("diameterConcurrencyData",diameterConcurrencyData);
					request.setAttribute("mandatoryFieldMappingsList", diameterConcurrencyForm.getMandatoryFieldMappingsList());
					request.setAttribute("additionalFieldMappingsList", diameterConcurrencyForm.getAdditionalFieldMappingsList());
				}
			}catch (DataManagerException managerExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("diameterconcurrency.view.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);

			}
			return mapping.findForward(VIEW_FORWARD);
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
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
}
