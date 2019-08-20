package com.elitecore.elitesm.web.servicepolicy.dynauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthNasClientDetailData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthNasClientsData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynaAuthPolicyESIRelData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.dynauth.forms.AddDynAuthServicePolicyForm;

public class AddDynAuthServicePolicyAction extends BaseDynAuthServicePolicyAction{
	private static String ACTION_ALIAS = ConfigConstant.CREATE_DYNAUTH_POLICY;

	private static final String MODULE = "AddDynAuthServicePolicyAction";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		AddDynAuthServicePolicyForm addDynAuthServicePolicyForm = (AddDynAuthServicePolicyForm)form;
		try{
			checkActionPermission(request, ACTION_ALIAS);
			
			LinkedHashSet<DynAuthFieldMapData> tempFeildMapSet = new LinkedHashSet<DynAuthFieldMapData>();

			String attributeIds[] = request.getParameterValues("attributeidval");
			String dbFields[] = request.getParameterValues("dbfieldVal");
			String defaultVal[] = request.getParameterValues("defaultval");
			String mandatory[] = request.getParameterValues("mandatory");

			if(attributeIds != null){

				for(int i= 0;i<dbFields.length;i++){

					DynAuthFieldMapData tempFeildMapData = new DynAuthFieldMapData();
					tempFeildMapData.setAttributeid(attributeIds[i]);
					tempFeildMapData.setDbfield(dbFields[i]);
					tempFeildMapData.setDefaultvalue(defaultVal[i]);
					tempFeildMapData.setMandatory(mandatory[i]);

					tempFeildMapSet.add(tempFeildMapData);
				}
			}			
			
			
		    	/*Get NAS Client List*/
			 
			  String nasClients[] =request.getParameterValues("nasClients");
			  List<DynaAuthPolicyESIRelData> nasClientList=getNASClientList(nasClients);
			
			

			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		
			Logger.logDebug(MODULE, "addDynAuthServicePolicyForm   : "+addDynAuthServicePolicyForm);

			String statusCheckBox = addDynAuthServicePolicyForm.getStatus();
			if(statusCheckBox == null || statusCheckBox.equalsIgnoreCase("")) {
				addDynAuthServicePolicyForm.setStatus("0");
			} else {
				addDynAuthServicePolicyForm.setStatus("1");   
			}
			
			String nasClientsJson=addDynAuthServicePolicyForm.getNasClientsJson();
			
			List<NasClients> nasClientsList = new ArrayList<NasClients>();
			
			if(nasClientsJson != null && nasClientsJson.length() > 0){
				 JSONArray nasClientArray = JSONArray.fromObject(nasClientsJson);
				 for(Object  obj: nasClientArray){
					 Map<String,Class> configObj = new HashMap<String, Class>();
					 
					 configObj.put("esiListData", ESIData.class);
					
					 NasClients nasClientsDetails = (NasClients) JSONObject.toBean((JSONObject) obj, NasClients.class,configObj);
					 nasClientsList.add(nasClientsDetails);
				 }
			}
			
			addDynAuthServicePolicyForm.setNasClientsList(nasClientsList);
			LinkedHashSet<DynAuthNasClientsData> dynAuthNasClientsDataSet = new LinkedHashSet<DynAuthNasClientsData>();
			
			for(NasClients client : nasClientsList){
				
				DynAuthNasClientsData dyAuthNasClientsData = new DynAuthNasClientsData();
				LinkedHashSet<DynAuthNasClientDetailData> dynAuthNasClientDetailDataSet = new LinkedHashSet<DynAuthNasClientDetailData>();
				
				List<ESIData> esiDataList = client.getEsiListData();
				
				for(ESIData esiDataObj : esiDataList){
					DynAuthNasClientDetailData dynAuthNasClientDetailData= new DynAuthNasClientDetailData();
					dynAuthNasClientDetailData.setEsiInstanceId(esiDataObj.getEsiId());
					dynAuthNasClientDetailData.setLoadFactor(esiDataObj.getLoadFactor());
					dynAuthNasClientDetailDataSet.add(dynAuthNasClientDetailData);
				}
				
				dyAuthNasClientsData.setDynaAuthNasClientDetailsData(dynAuthNasClientDetailDataSet);
				dyAuthNasClientsData.setRuleset(client.getRuleset());
				dyAuthNasClientsData.setScript(client.getScript());
				dyAuthNasClientsData.setOrderNumber(client.getOrderNumber());

				if(Strings.isNullOrBlank(client.getTranslationMapConfigId())){
					dyAuthNasClientsData.setTranslationMapConfigId(null);
					dyAuthNasClientsData.setCopyPacketMapConfigId(null);

				}else{
					if(client.getTranslationMapConfigId().startsWith(ConfigConstant.COPY_PACKET_MAPPING)){
						dyAuthNasClientsData.setCopyPacketMapConfigId(client.getTranslationMapConfigId().substring(ConfigConstant.COPY_PACKET_MAPPING.length()));
						dyAuthNasClientsData.setTranslationMapConfigId(null);

					}else{
						dyAuthNasClientsData.setCopyPacketMapConfigId(null);
						dyAuthNasClientsData.setTranslationMapConfigId(client.getTranslationMapConfigId().substring(ConfigConstant.TRANSLATION_MAPPING.length()));
					}
				}

				if(dyAuthNasClientsData != null){
					dynAuthNasClientsDataSet.add(dyAuthNasClientsData);
				}
			}
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			DynAuthPolicyInstData authPolicyInstData = convertFormToBean(addDynAuthServicePolicyForm,tempFeildMapSet,nasClientList,dynAuthNasClientsDataSet);
			
			ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();

			servicePolicyBLManager.createDynAuthPolicy(authPolicyInstData, staffData);
			doAuditing(staffData, ACTION_ALIAS);
			request.setAttribute("responseUrl","/initSearchDynAuthServicePolicy.do");
			ActionMessage message = new ActionMessage("dynauthpolicy.create.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveMessages(request,messages);
			request.getSession().removeAttribute("addAuthServicePolicyForm");

			return mapping.findForward(SUCCESS);
			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DuplicateInstanceNameFoundException e){
			e.printStackTrace();
			Logger.logTrace(MODULE,e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("dynauthpolicy.create.duplicate.failure",addDynAuthServicePolicyForm.getName());
		    ActionMessages messages = new ActionMessages();
		    messages.add("information", message);
		    saveErrors(request, messages);
		    
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		}
		return mapping.findForward(FAILURE);
	}
	
}
