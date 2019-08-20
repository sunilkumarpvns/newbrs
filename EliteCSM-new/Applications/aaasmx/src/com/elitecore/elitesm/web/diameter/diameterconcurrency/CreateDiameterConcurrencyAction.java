package com.elitecore.elitesm.web.diameter.diameterconcurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.elitecore.elitesm.blmanager.diameter.diameterconcurrency.DiameterConcurrencyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData;
import com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyFieldMapping;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.diameterconcurrency.form.DiameterConcurrencyForm;

public class CreateDiameterConcurrencyAction extends BaseWebAction{
	protected static final String MODULE = "CreateDiameterConcurrencyAction";
	private static final String CREATE_FORWARD = "createDiameterConcurrency";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_CONCURRENCY;

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		try{
			   if(checkAccess(request, ACTION_ALIAS)){  
				   Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
				   DiameterConcurrencyForm diameterConcurrencyForm = (DiameterConcurrencyForm)form;
				   
				   if(diameterConcurrencyForm.getAction() == null){
					  
					   String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
					   diameterConcurrencyForm.setDescription(getDefaultDescription(userName));
					   
					   DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
					   List<IDatabaseDSData> lstDatasource = databaseDSBLManager.getDatabaseDSList();
					   
					   diameterConcurrencyForm.setLstDatasource(lstDatasource);
					   
					   request.setAttribute("diameterConcurrencyForm",diameterConcurrencyForm);
					   request.setAttribute("lstDatasource", lstDatasource);
					   
					   return mapping.findForward(CREATE_FORWARD);	
				   }else{
						IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						
						DiameterConcurrencyData diameterConcurrencyData = new DiameterConcurrencyData();
						convertFormToBean(diameterConcurrencyData, diameterConcurrencyForm);
						
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
						
						DiameterConcurrencyBLManager blManager = new DiameterConcurrencyBLManager();
						blManager.create(diameterConcurrencyData,staffData);
						doAuditing(staffData, ACTION_ALIAS);
						
						request.setAttribute("responseUrl", "/searchDiameterConcurrency");      
						ActionMessage message = new ActionMessage("diameterconcurrency.create.success");
						ActionMessages messages = new ActionMessages();          
						messages.add("information", message);                    
						saveMessages(request,messages);         				   
						Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 
						return mapping.findForward(SUCCESS);   
					}
			} else{
				Logger.logError(MODULE, "Error during Data Manager operation ");
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
	
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (DuplicateInstanceNameFoundException authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("diameterconcurrency.duplicatenamefound");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);     
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("diameterconcurrency.create.failure");                                                         		    
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}
		return mapping.findForward(FAILURE);
	}

	private void convertDBFieldMapping( DiameterConcurrencyData diameterConcurrencyData, List<ConcurrencyFieldMappingData> concurrencyFieldMappingDataList) {
		List<DiameterConcurrencyFieldMapping> diameterFieldMappingList = new  ArrayList<DiameterConcurrencyFieldMapping>();
		
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
			}
		}
		
		diameterConcurrencyData.setDiameterConcurrencyFieldMappingList(diameterFieldMappingList);
	}

	private void convertFormToBean(DiameterConcurrencyData diameterConcurrencyData, DiameterConcurrencyForm diameterConcurrencyForm) {
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
	}
}
