package com.elitecore.elitesm.web.servicepolicy.dynauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthNasClientDetailData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthNasClientsData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynaAuthPolicyESIRelData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.constants.ScriptTypesConstants;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.acct.forms.ExternalSystemBean;
import com.elitecore.elitesm.web.servicepolicy.dynauth.forms.UpdateDynAuthServicePolicyBasicDetailForm;

public class UpdateDynAuthServicePolicyBasicDetailAction extends BaseDynAuthServicePolicyAction{
	private static final String UPDATE_FORWARD = "updateDynAuthPolicyBasicDetail";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="UpdateDynAuthServicePolicyBasicDetailAction";
	private static final String ACTION_ALIAS=ConfigConstant.UPDATE_DYNAUTH_POLICY_BASIC_DETAIL;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){		
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			UpdateDynAuthServicePolicyBasicDetailForm updateDynAuthSerivicePolicyForm = (UpdateDynAuthServicePolicyBasicDetailForm)form;
			
			try{
				ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
				String action = updateDynAuthSerivicePolicyForm.getAction();
				
				String strAuthPolicyID = request.getParameter("dynauthpolicyid");
				String authPolicyID;
				
				if(strAuthPolicyID != null){
					authPolicyID = strAuthPolicyID;
				}else{
					authPolicyID=updateDynAuthSerivicePolicyForm.getDynAuthPolicyId();
				}
				if(action==null || action.equals("")){
					if( Strings.isNullOrBlank(authPolicyID) == false ){
						
						ExternalSystemInterfaceBLManager externalSystemBLmanager = new ExternalSystemInterfaceBLManager();
						List<ExternalSystemInterfaceInstanceData> nasInstanceList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.NAS);
						String nasInstanceIds[] = new String[nasInstanceList.size()];
						String nasInstanceNames[][] = new String[nasInstanceList.size()][4]; 
						for(int i=0;i<nasInstanceList.size();i++) {
							ExternalSystemInterfaceInstanceData	externalSystemData = nasInstanceList.get(i);
							nasInstanceNames[i][0] = String.valueOf(externalSystemData.getName());
							nasInstanceNames[i][1] = String.valueOf(externalSystemData.getAddress());
							nasInstanceNames[i][2] = String.valueOf(externalSystemData.getMinLocalPort());
							nasInstanceNames[i][3] = String.valueOf(externalSystemData.getExpiredRequestLimitCount());
							nasInstanceIds[i] = String.valueOf(externalSystemData.getEsiInstanceId());
						}
											
						request.setAttribute("nasInstanceIds", nasInstanceIds);
						request.setAttribute("nasInstanceNames", nasInstanceNames);
						request.setAttribute("nasInstanceList", nasInstanceList);
						
						
						
						DynAuthPolicyInstData dynAuthPolicyInstData = new DynAuthPolicyInstData();
						DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
						updateDynAuthSerivicePolicyForm.setDatabaseDatasourceList(databaseDSBLManager.getDatabaseDSList());

						dynAuthPolicyInstData = servicePolicyBLManager.getDynAuthPolicyInstData(authPolicyID);
						request.setAttribute("dynAuthPolicyInstData",dynAuthPolicyInstData);
						
						List<DynaAuthPolicyESIRelData> nasClients=servicePolicyBLManager.getNASClients(dynAuthPolicyInstData.getDynAuthPolicyId());
						List<ExternalSystemBean> nasClientList=convertToNASClientExternalSystemBean(nasClients);
						updateDynAuthSerivicePolicyForm.setNasClientList(nasClientList);


						convertBeanToForm(dynAuthPolicyInstData,updateDynAuthSerivicePolicyForm);
						
						
						Set<DynAuthFieldMapData> tempFeildMapSet = dynAuthPolicyInstData.getDynAuthFeildMapSet();
						
						String attributeIds[] = null; 
						String dbFields[] = null;
						String defaultVal[] = null;
						String mandatory[] = null;

						if(tempFeildMapSet != null){

							attributeIds = new String[tempFeildMapSet.size()];
							dbFields = new String[tempFeildMapSet.size()];
							defaultVal = new String[tempFeildMapSet.size()];                            
							mandatory = new String[tempFeildMapSet.size()];

							Iterator<DynAuthFieldMapData> itr = tempFeildMapSet.iterator();
							int i = 0;
							while(itr.hasNext()){
								DynAuthFieldMapData data = itr.next();
								attributeIds[i] = data.getAttributeid();
								dbFields[i] = data.getDbfield();
								defaultVal[i] = data.getDefaultvalue();
								mandatory[i] = data.getMandatory();
								i++;
							}

							request.setAttribute("attrid", attributeIds);
							request.setAttribute("fieldList", dbFields);
							request.setAttribute("defVal", defaultVal);
							request.setAttribute("mandatory",mandatory);
						}
						
						Set<DynAuthNasClientsData> dynAuthNasClientsDataSet = dynAuthPolicyInstData.getDynAuthNasClientDataSet();
					    List<NasClients> nasList = new ArrayList<NasClients>();
						for(DynAuthNasClientsData dynAuthNasClientsData : dynAuthNasClientsDataSet){
							NasClients nasClientsObj = new NasClients();
							nasClientsObj.setRuleset(dynAuthNasClientsData.getRuleset());
							nasClientsObj.setScript(dynAuthNasClientsData.getScript());
							if(dynAuthNasClientsData.getTranslationMapConfigId()!= null){
								nasClientsObj.setTranslationMapConfigId(ConfigConstant.TRANSLATION_MAPPING + dynAuthNasClientsData.getTranslationMapConfigId());
							}else if(dynAuthNasClientsData.getCopyPacketMapConfigId() != null){
								nasClientsObj.setTranslationMapConfigId(ConfigConstant.COPY_PACKET_MAPPING + dynAuthNasClientsData.getCopyPacketMapConfigId());

							}else{
								nasClientsObj.setTranslationMapConfigId(null);
							}

							Set<DynAuthNasClientDetailData> dynaAuthNasClientDetailsDataSet = dynAuthNasClientsData.getDynaAuthNasClientDetailsData();
							List<ESIData> esiDatas = new  ArrayList<ESIData>();
							for(DynAuthNasClientDetailData dynAuthNasClientDetailData : dynaAuthNasClientDetailsDataSet){
								ESIData esiData = new ESIData();
								esiData.setEsiId(dynAuthNasClientDetailData.getEsiInstanceId());
								esiData.setLoadFactor(dynAuthNasClientDetailData.getLoadFactor());
								esiDatas.add(esiData);
							}
							
							nasClientsObj.setEsiListData(esiDatas);
							nasList.add(nasClientsObj);
						}
						updateDynAuthSerivicePolicyForm.setNasClientsList(nasList);
					}
					TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
					updateDynAuthSerivicePolicyForm.setTranslationMappingConfDataList(translationMappingConfBLManager.getRadiusToRadiusTranslationMapping());
					CopyPacketTransMapConfBLManager copyPacketMappingBLManager = new CopyPacketTransMapConfBLManager();
					List<CopyPacketTranslationConfData> copyPacketMapConfDataList = copyPacketMappingBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.RADIUS, TranslationMappingConfigConstants.RADIUS);
					updateDynAuthSerivicePolicyForm.setCopyPacketMappingConfDataList(copyPacketMapConfDataList);
					
					/* External Radius Script */
					ScriptBLManager scriptBLManager = new ScriptBLManager();
					List<ScriptInstanceData> externalScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT);
						
					updateDynAuthSerivicePolicyForm.setExternalScriptList(externalScriptList);
					
				}else if(action!=null && action.equals("update")){
					ServicePolicyBLManager servicePoilcyBLManager = new ServicePolicyBLManager();
					String actionAlias = ACTION_ALIAS;
					
					String nasClientsJson=updateDynAuthSerivicePolicyForm.getNasClientsJson();
					
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
					
					updateDynAuthSerivicePolicyForm.setNasClientsList(nasClientsList);
					LinkedHashSet<DynAuthNasClientsData> dynAuthNasClientsDataSet = new LinkedHashSet<DynAuthNasClientsData>();
					
					for(NasClients client : nasClientsList){
						
						DynAuthNasClientsData dyAuthNasClientsData = new DynAuthNasClientsData();
						LinkedHashSet<DynAuthNasClientDetailData> dynAuthNasClientDetailDataSet = new LinkedHashSet<DynAuthNasClientDetailData>();
						
						List<ESIData> esiDataList = client.getEsiListData();
						
						for(ESIData esiDataObj : esiDataList){
							DynAuthNasClientDetailData dynAuthNasClientDetailData= new DynAuthNasClientDetailData();
							dynAuthNasClientDetailData.setEsiInstanceId(esiDataObj.getEsiId());
							dynAuthNasClientDetailData.setLoadFactor(esiDataObj.getLoadFactor());
							
							ExternalSystemInterfaceBLManager blManager = new ExternalSystemInterfaceBLManager();
							dynAuthNasClientDetailData.setExternalSystemInstanceData(blManager.getExternalSystemInterfaceInstanceDataById(esiDataObj.getEsiId()));
							
							dynAuthNasClientDetailDataSet.add(dynAuthNasClientDetailData);
						}
						
						dyAuthNasClientsData.setDynaAuthNasClientDetailsData(dynAuthNasClientDetailDataSet);
						dyAuthNasClientsData.setRuleset(client.getRuleset());
						dyAuthNasClientsData.setScript(client.getScript());
						dyAuthNasClientsData.setOrderNumber(client.getOrderNumber());
						
						if(Strings.isNullOrBlank(client.getTranslationMapConfigId())){
							dyAuthNasClientsData.setTranslationMapConfigId(null);
							dyAuthNasClientsData.setCopyPacketMapConfigId(null);;

						}else{
							if(client.getTranslationMapConfigId().startsWith(ConfigConstant.COPY_PACKET_MAPPING)){
								dyAuthNasClientsData.setCopyPacketMapConfigId(client.getTranslationMapConfigId().substring(ConfigConstant.COPY_PACKET_MAPPING.length()));
								dyAuthNasClientsData.setTranslationMapConfigId(null);
								
								//Fetch CopyPacketConfData 
								CopyPacketTransMapConfBLManager blManager = new CopyPacketTransMapConfBLManager();
								CopyPacketTranslationConfData confData = blManager.getCopyPacketTransMapConfigData(client.getTranslationMapConfigId().substring(ConfigConstant.COPY_PACKET_MAPPING.length()));
								
								dyAuthNasClientsData.setCopyPacketTranslationConfData(confData);
								
							}else{
								dyAuthNasClientsData.setCopyPacketMapConfigId(null);
								dyAuthNasClientsData.setTranslationMapConfigId(client.getTranslationMapConfigId().substring(ConfigConstant.TRANSLATION_MAPPING.length()));
								
								//Fetch TranslationMappingConfData
								TranslationMappingConfBLManager blManager = new  TranslationMappingConfBLManager(); 
								TranslationMappingConfData translationMappingConfData = blManager.getTranslationMappingConfData(client.getTranslationMapConfigId().substring(ConfigConstant.TRANSLATION_MAPPING.length()));
								
								dyAuthNasClientsData.setTranslationMappingConfData(translationMappingConfData);
							}
						}
						
						if(dyAuthNasClientsData != null){
							dynAuthNasClientsDataSet.add(dyAuthNasClientsData);
						}
					}
					
					DynAuthPolicyInstData dynAuthPolicyInstData = convertFormToBean(updateDynAuthSerivicePolicyForm,dynAuthNasClientsDataSet);
					IStaffData staffData = getStaffObject((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm"));
					
					List<DynAuthFieldMapData> tempList = new ArrayList<DynAuthFieldMapData>();
					
					String attributeIds[] = request.getParameterValues("attributeidval");
					String dbFields[] = request.getParameterValues("dbfieldVal");
					String defaultVal[] = request.getParameterValues("defaultval");
					String mandatory[] = request.getParameterValues("mandatory");
					
					Logger.logDebug(MODULE,"request.getParameterValues(attrnmval) is"+request.getParameterValues("attrnmval"));
					
					if(attributeIds != null){
						for(int i=0;i<attributeIds.length;i++){
							DynAuthFieldMapData attrRelData = new DynAuthFieldMapData();
							attrRelData.setAttributeid(attributeIds[i]);
							attrRelData.setDbfield(dbFields[i]);
							attrRelData.setDefaultvalue(defaultVal[i]);					
							attrRelData.setMandatory(mandatory[i]);
							tempList.add(attrRelData);
						}
					}

					dynAuthPolicyInstData.setMappingList(tempList); 
					
					/*Get NAS Client List*/
					 
					String nasClients[] =request.getParameterValues("nasClients");
					List<DynaAuthPolicyESIRelData> nasClientList=getNASClientList(nasClients);
				    //dynAuthPolicyInstData.setNasClientList(nasClientList);
				 
				    staffData.setAuditId(dynAuthPolicyInstData.getAuditUId());
					staffData.setAuditName(dynAuthPolicyInstData.getName());
				    
				    servicePoilcyBLManager.updateDynAuthPolicyById(dynAuthPolicyInstData, staffData);
					
					Logger.logDebug(MODULE, "DynAuthPolicyInstData : "+dynAuthPolicyInstData);
	                request.setAttribute("responseUrl","/viewDynAuthServicePolicy.do?dynauthpolicyid="+dynAuthPolicyInstData.getDynAuthPolicyId());
	                ActionMessage message = new ActionMessage("dynauthpolicy.update.success");
	                ActionMessages messages = new ActionMessages();
	                messages.add("information", message);
	                saveMessages(request, messages);
	                return mapping.findForward(SUCCESS);
				}
				return mapping.findForward(UPDATE_FORWARD);
			}catch(DuplicateInstanceNameFoundException e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("dynauthpolicy.update.duplicate.failure",updateDynAuthSerivicePolicyForm.getName());
			    ActionMessages messages = new ActionMessages();
			    messages.add("information", message);
			    saveErrors(request, messages);
			    
			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("dynauthpolicy.update.failure");
			    ActionMessages messages = new ActionMessages();
			    messages.add("information", message);
			    saveErrors(request, messages);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private List<ExternalSystemBean> convertToNASClientExternalSystemBean(List<DynaAuthPolicyESIRelData> nasClients) {
		List<ExternalSystemBean> nasClientsList = new ArrayList<ExternalSystemBean>();
		for (Iterator<DynaAuthPolicyESIRelData> iterator = nasClients.iterator(); iterator.hasNext();) {
			DynaAuthPolicyESIRelData nasClientESIRelData =  iterator.next();
			ExternalSystemBean externalSystemBean = new ExternalSystemBean();
			externalSystemBean.setName(nasClientESIRelData.getExternalSystemData().getName());
			externalSystemBean.setValue(nasClientESIRelData.getEsiInstanceId().toString());
			nasClientsList.add(externalSystemBean);
		}

		return nasClientsList;

	}
	private DynAuthPolicyInstData convertFormToBean(UpdateDynAuthServicePolicyBasicDetailForm form,LinkedHashSet<DynAuthNasClientsData> dynAuthNasClientsDataSet){
		DynAuthPolicyInstData dynauthPolicyInstData = new DynAuthPolicyInstData();
		dynauthPolicyInstData.setDynAuthPolicyId(form.getDynAuthPolicyId());
		dynauthPolicyInstData.setName(form.getName());
		dynauthPolicyInstData.setDescription(form.getDescription());
		dynauthPolicyInstData.setRuleSet(form.getRuleSet());;
		dynauthPolicyInstData.setResponseAttributes(form.getResponseAttributes());
		dynauthPolicyInstData.setTableName(form.getTableName());
		dynauthPolicyInstData.setEligibleSession(form.getEligibleSessions());
		dynauthPolicyInstData.setEventTimestamp(form.getEventTimestamp());
		dynauthPolicyInstData.setValidatePacket(form.getValidatePacket());
		dynauthPolicyInstData.setDatabaseDatasourceId(form.getDatabaseDatasourceId());
		dynauthPolicyInstData.setDynAuthNasClientDataSet(dynAuthNasClientsDataSet);
		dynauthPolicyInstData.setDbFailureAction(form.getDbFailureAction());
		if(BaseConstant.SHOW_STATUS.equals(form.getStatus())){
			dynauthPolicyInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			dynauthPolicyInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}	
		
		dynauthPolicyInstData.setAuditUId(form.getAuditUId());
		return dynauthPolicyInstData;
		
	}
	private void convertBeanToForm(DynAuthPolicyInstData data,UpdateDynAuthServicePolicyBasicDetailForm form){
		if(data!=null && form!=null){
			form.setName(data.getName());
			form.setDescription(data.getDescription());
			form.setRuleSet(data.getRuleSet());;
			form.setResponseAttributes(data.getResponseAttributes());
			form.setEligibleSessions(data.getEligibleSession());
			form.setEventTimestamp(data.getEventTimestamp());
			form.setDatabaseDatasourceId(data.getDatabaseDatasourceId());
			form.setTableName(data.getTableName());
			form.setValidatePacket(data.getValidatePacket());
			form.setDbFailureAction(data.getDbFailureAction());
			
			if(BaseConstant.HIDE_STATUS_ID.equals(data.getStatus())){
				form.setStatus(BaseConstant.HIDE_STATUS);
			}else{
				form.setStatus(BaseConstant.SHOW_STATUS);
			}
			form.setAuditUId(data.getAuditUId());
		}
	}
}

