package com.elitecore.elitesm.web.radius.radiusesigroup;

import java.io.StringReader;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.radius.correlatedradius.CorrelatedRadiusBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.correlatedradius.data.CorrelatedRadiusData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.ActivePassiveCommunicatorData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.CommunicatorData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.radiusesigroup.form.RadiusESIGroupForm;
/**
 * 
 * @author Tejas Shah
 *
 */
public class UpdateRadiusESIGroupAction  extends BaseWebAction{
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_RADIUS_ESI_GROUP;
	private static final String MODULE = UpdateRadiusESIGroupAction.class.getSimpleName();
	private static final String UPDATE_FORWARD = "updateRadiusESIGroup";
	public static final String PRIMARY = "primary";
	public static final String SECONDARY = "secondary";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of :- "+getClass().getName());
		try{
			/* Check Permission of Radius ESI Group Module */
			checkActionPermission(request, ACTION_ALIAS);
			RadiusESIGroupForm radiusESIGroupForm = (RadiusESIGroupForm)form;

			RadiusESIGroupBLManager blManager = new RadiusESIGroupBLManager();
			RadiusESIGroupData radiusESIGroupData = new RadiusESIGroupData();
			ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager =new ExternalSystemInterfaceBLManager();
					
			String strESIGroupId = request.getParameter("id");
			Logger.getLogger().info(MODULE, "Esi Group Id is : " + strESIGroupId);
					
			String esiGroupId = "";
			if(Strings.isNullOrBlank(strESIGroupId) == false){
				esiGroupId = strESIGroupId;
			}
			
			if("update".equals(radiusESIGroupForm.getAction())){
				try{
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

					if((RedundancyMode.NM.redundancyModeName).equalsIgnoreCase(radiusESIGroupForm.getRedundancyMode())){
						String[] primaryEsis = request.getParameterValues("selectedPrimaryEsi");
						String[] secondaryEsis = request.getParameterValues("selectedSecondaryEsi");

						setEsiDataInList(Arrays.asList(primaryEsis),radiusESIGroupForm, PRIMARY);
						if(secondaryEsis != null && secondaryEsis.length > 0){
							setEsiDataInList(Arrays.asList(secondaryEsis),radiusESIGroupForm, SECONDARY);
						}
					}else if((RedundancyMode.ACTIVE_PASSIVE.redundancyModeName).equalsIgnoreCase(radiusESIGroupForm.getRedundancyMode())){
						String activePassiveTblJson = request.getParameter("activePassiveEsiJson");

						if(Strings.isNullOrEmpty(activePassiveTblJson) == false){
							JSONArray esiDataArray = JSONArray.fromObject(activePassiveTblJson);
							RadiusEsiGroupConfigurationData activePassiveEsiData = new RadiusEsiGroupConfigurationData();
							Map<String,Class> configObj = new HashMap<String, Class>();
							configObj.put("activePassiveEsiList", ActivePassiveCommunicatorData.class);
							for(Object  obj: esiDataArray){
								activePassiveEsiData = (RadiusEsiGroupConfigurationData) JSONObject.toBean((JSONObject) obj, RadiusEsiGroupConfigurationData.class,configObj);
							}
							radiusESIGroupForm.setActivePassiveEsiList(activePassiveEsiData.getActivePassiveEsiList());
						}
					}
					convertFromFormToData(radiusESIGroupForm,radiusESIGroupData);

					staffData.setAuditName(radiusESIGroupData.getName());
					staffData.setAuditId(radiusESIGroupData.getAuditUId());

					blManager.updateRadiusESIGroupById(radiusESIGroupData, staffData);
					
					Logger.getLogger().info(MODULE, "Radius Esi Group [" + radiusESIGroupData.getName() + "] Updated Successfully");
					
					request.setAttribute("responseUrl", "/viewRadiusESIGroup?id="+radiusESIGroupData.getId());
					ActionMessage message = new ActionMessage("radiusesigroup.update.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request, messages);
					
					return mapping.findForward(SUCCESS);
				}catch(DuplicateParameterFoundExcpetion dpf){
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,dpf);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("radiusesigroup.update.duplicate.failure",radiusESIGroupData.getName());
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}catch(Exception e) {					
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("radiusesigroup.update.failure");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}

			}else{
				
				Logger.getLogger().info(MODULE, "Enter in Else method of Update Radius ESI Group method : ");
				
				RadiusESIGroupData radiusESIGroup = blManager.getRadiusESIGroupById(esiGroupId);
				
				convertBeanToForm(radiusESIGroupForm,radiusESIGroup);

				List<String> authTypeEsiDataList = new ArrayList<>();
				List<String> acctTypeEsiDataList = new ArrayList<>();
				List<String> chargingGatewayEsiDataList = new ArrayList<>();
				List<String> correlatedRadiusEsiDataList = new ArrayList<>();

				List<ExternalSystemInterfaceInstanceData> externalSystemInterfaceInstanceDataList = externalSystemInterfaceBLManager.getAllExternalSystemInstanceDataList();

				for (ExternalSystemInterfaceInstanceData esiData:externalSystemInterfaceInstanceDataList) {
					if(ExternalSystemConstants.AUTH_PROXY == esiData.getEsiTypeId()){
						authTypeEsiDataList.add(esiData.getName());
					}else if(ExternalSystemConstants.ACCT_PROXY == esiData.getEsiTypeId()){
						acctTypeEsiDataList.add(esiData.getName());
					}else if(ExternalSystemConstants.CHARGING_GATEWAY_COMMUNICATION == esiData.getEsiTypeId()){
						chargingGatewayEsiDataList.add(esiData.getName());
					}
				}
				radiusESIGroupForm.setAuthTypeEsiDataList(authTypeEsiDataList);
				radiusESIGroupForm.setAcctTypeEsiDataList(acctTypeEsiDataList);
				radiusESIGroupForm.setChargingGatewayEsiDataList(chargingGatewayEsiDataList);

				CorrelatedRadiusBLManager correlatedRadiusBLManager = new CorrelatedRadiusBLManager();
				for (CorrelatedRadiusData esiData:correlatedRadiusBLManager.getCorrelatedRadiusDataList()) {
					correlatedRadiusEsiDataList.add(esiData.getName());
				}
				radiusESIGroupForm.setCorrelatedRadiusEsiDataList(correlatedRadiusEsiDataList);
				request.setAttribute("radiusESIGroupForm", radiusESIGroupForm);
				request.setAttribute("radiusESIGroup", radiusESIGroup);
				
				return mapping.findForward(UPDATE_FORWARD);

			}

		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("radiusesigroup.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}

	private void convertBeanToForm(RadiusESIGroupForm radiusESIGroupForm,RadiusESIGroupData radiusESIGroup) throws JAXBException {
		radiusESIGroupForm.setDescription(radiusESIGroup.getDescription());
		radiusESIGroupForm.setEsiGroupId(radiusESIGroup.getId());
		radiusESIGroupForm.setEsiGroupName(radiusESIGroup.getName());
		radiusESIGroupForm.setAuditUId(radiusESIGroup.getAuditUId());

		String xmlDatas = new String(radiusESIGroup.getEsiGroupDataXml());
		StringReader stringReader =new StringReader(xmlDatas.trim());

		RadiusEsiGroupConfigurationData esiConfigurationData = ConfigUtil.deserialize(stringReader, RadiusEsiGroupConfigurationData.class);

		radiusESIGroupForm.setRedundancyMode(esiConfigurationData.getRedundancyMode());
		radiusESIGroupForm.setEsiType(esiConfigurationData.getEsiType());
		radiusESIGroupForm.setStickySession(Boolean.parseBoolean(esiConfigurationData.getStateful()));
		radiusESIGroupForm.setSwitchBack(Boolean.parseBoolean(esiConfigurationData.getSwitchBackEnable()));

		radiusESIGroupForm.setActivePassiveEsiList(esiConfigurationData.getActivePassiveEsiList());
		radiusESIGroupForm.setPrimaryEsiValues(esiConfigurationData.getPrimaryEsiList());
		radiusESIGroupForm.setSecondaryEsiValues(esiConfigurationData.getFailOverEsiList());
	}

	private void convertFromFormToData(RadiusESIGroupForm radiusESIGroupForm, RadiusESIGroupData radiusESIGroupData) throws DataManagerException {
		radiusESIGroupData.setId(radiusESIGroupForm.getEsiGroupId());
		radiusESIGroupData.setName(radiusESIGroupForm.getEsiGroupName());
		radiusESIGroupData.setDescription(radiusESIGroupForm.getDescription());
		radiusESIGroupData.setAuditUId(radiusESIGroupForm.getAuditUId());

		RadiusEsiGroupConfigurationData esiGroupConfigurationData = new RadiusEsiGroupConfigurationData();

		esiGroupConfigurationData.setName(radiusESIGroupForm.getEsiGroupName());
		esiGroupConfigurationData.setDescription(radiusESIGroupForm.getDescription());
		esiGroupConfigurationData.setRedundancyMode(radiusESIGroupForm.getRedundancyMode());
		esiGroupConfigurationData.setEsiType(radiusESIGroupForm.getEsiType());
		esiGroupConfigurationData.setSwitchBackEnable(String.valueOf(radiusESIGroupForm.isSwitchBack()));

		if((RedundancyMode.NM.redundancyModeName).equalsIgnoreCase(radiusESIGroupForm.getRedundancyMode())){
			esiGroupConfigurationData.setPrimaryEsiList(radiusESIGroupForm.getPrimaryEsiValues());
			esiGroupConfigurationData.setFailOverEsiList(radiusESIGroupForm.getSecondaryEsiValues());
		}else if((RedundancyMode.ACTIVE_PASSIVE.redundancyModeName).equalsIgnoreCase(radiusESIGroupForm.getRedundancyMode())){
			esiGroupConfigurationData.setActivePassiveEsiList(radiusESIGroupForm.getActivePassiveEsiList());
		}

		if(RadiusEsiType.ACCT.name.equalsIgnoreCase(radiusESIGroupForm.getEsiType())){
			esiGroupConfigurationData.setStateful(String.valueOf(radiusESIGroupForm.isStickySession()));
		}else{
			esiGroupConfigurationData.setStateful("true");
		}

		try {
			String serialize = ConfigUtil.serialize(RadiusEsiGroupConfigurationData.class, esiGroupConfigurationData);
			String xmlDatas = new String(serialize.getBytes());
			System.out.println("************************Created XML Data*******************************");
			System.out.println(xmlDatas);
			System.out.println("***********************************************************************");
			System.out.println("XML Length : "+ xmlDatas.length());
			System.out.println("***********************************************************************");

			radiusESIGroupData.setEsiGroupDataXml(serialize.getBytes());

		} catch(JAXBException e) {
			throw new DataManagerException(e.getMessage(), e);
		}
	}

	public static void setEsiDataInList(List<String> esiNames, RadiusESIGroupForm radiusESIGroupForm,String esi){
		List<CommunicatorData> esiList = new ArrayList<CommunicatorData>();
		if(Collectionz.isNullOrEmpty(esiNames) == false){
			for(String esiName : esiNames){
				String[] esiNameAndWeightage = esiName.split("-W-");
				if(esiNameAndWeightage != null && esiNameAndWeightage.length >= 1){
					CommunicatorData esiRelationWithEsiGroupData = new CommunicatorData();
					esiRelationWithEsiGroupData.setName(esiNameAndWeightage[0].trim());
					esiRelationWithEsiGroupData.setLoadFactor(Integer.parseInt(esiNameAndWeightage[1].trim()));
					esiList.add(esiRelationWithEsiGroupData);
				}
			}
			if(PRIMARY.equalsIgnoreCase(esi)){
				radiusESIGroupForm.setPrimaryEsiValues(esiList);
			}else if(SECONDARY.equalsIgnoreCase(esi)){
				radiusESIGroupForm.setSecondaryEsiValues(esiList);
			}
		}
	}
}
