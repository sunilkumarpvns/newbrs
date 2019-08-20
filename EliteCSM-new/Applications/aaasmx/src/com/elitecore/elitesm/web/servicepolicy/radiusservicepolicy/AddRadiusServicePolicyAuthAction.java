package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import com.elitecore.aaa.core.conf.impl.ImdgConfigData;
import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.inmemorydatagrid.InMemoryDataGridBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.inmemorydatagrid.data.InMemoryDataGridData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.*;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.radiusesigroup.RadiusEsiType;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.CreateRadiusServicePolicyForm;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class AddRadiusServicePolicyAuthAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "addAuthenticationHandler";
	private static String ACTION_ALIAS = ConfigConstant.CREATE_RADIUS_SERVICE_POLICY;

	private static final String MODULE = "AddRadiusServicePolicyAuthAction";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			
			HttpSession session = request.getSession(true);
			HttpSessionBindingEvent bind = null;
			bind = new HttpSessionBindingEvent(session, "sessionbinding");
			bind.getSession().getAttribute("test");
			
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
			CreateRadiusServicePolicyForm createRadiusServicePolicyForm=(CreateRadiusServicePolicyForm)form;
			Logger.logDebug(MODULE, "createRadiusServicePolicyForm     : "+createRadiusServicePolicyForm);
			
			DriverBLManager driverBlManager = new DriverBLManager();
			List<DriverInstanceData> driverList = driverBlManager.getDriverInstanceList(ServiceTypeConstants.AUTHENTICATION_SERVICE);
			createRadiusServicePolicyForm.setDriversList(driverList);
			
			List<DriverInstanceData> cacheableDriverInstDataList = driverBlManager.getCacheableDriverData();
			createRadiusServicePolicyForm.setCacheableDriverList(cacheableDriverInstDataList);
			
			String[] driverInstanceIds = new String [driverList.size()];
			String[][] driverInstanceNames = new String[driverList.size()][2]; 
			
			for(int i=0;i<driverList.size();i++){
				DriverInstanceData data = driverList.get(i);				
					driverInstanceNames[i][0] = String.valueOf(data.getName());
					driverInstanceNames[i][1] = String.valueOf(data.getDescription());
				driverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
			}
			
			SessionManagerBLManager sessionManagerBLManager=new SessionManagerBLManager();
			List<ISessionManagerInstanceData> sessionManagerList=sessionManagerBLManager.getSessionManagerInstanceList();
			
			/*translationMappingBLManager*/
			TranslationMappingConfBLManager translationMappingConfBLManager=new TranslationMappingConfBLManager();
			List<TranslationMappingConfData> translationMappingConfDataList=translationMappingConfBLManager.getRadiusToRadiusTranslationMapping();


			CopyPacketTransMapConfBLManager copyPacketTranslationConfBLManager = new CopyPacketTransMapConfBLManager();
			List<CopyPacketTranslationConfData> copyPacketMappingConfDataList = copyPacketTranslationConfBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.RADIUS, TranslationMappingConfigConstants.RADIUS);
			
			ExternalSystemInterfaceBLManager externalSystemBLmanager=new ExternalSystemInterfaceBLManager();
			List<ExternalSystemInterfaceInstanceData> externalSystemInstanceList = new ArrayList<ExternalSystemInterfaceInstanceData>();
			List<ExternalSystemInterfaceInstanceData> nasESIList=new ArrayList<ExternalSystemInterfaceInstanceData>();
				
			externalSystemInstanceList = externalSystemBLmanager.getAllExternalSystemInstanceDataList();
			
			createRadiusServicePolicyForm.setAuthBroadcastServerList(externalSystemInstanceList);
			
			nasESIList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.NAS);
			createRadiusServicePolicyForm.setDynaAuthRelDataList(nasESIList);
			
			createRadiusServicePolicyForm.setSessionManagerInstanceDataList(sessionManagerList);
			createRadiusServicePolicyForm.setTranslationMappingConfDataList(translationMappingConfDataList);
			createRadiusServicePolicyForm.setCopyPacketMappingConfDataList(copyPacketMappingConfDataList);
			
			/* Redirect to add accounting action instead of JSP */
			if(createRadiusServicePolicyForm.isAuthentication() == false){
				ActionRedirect redirect = new ActionRedirect(mapping.findForward("toRadiusServiceAcct"));
				return redirect;
			}
			
			PluginBLManager pluginBLManager = new PluginBLManager();
			List<PluginInstData> prePluginList  = pluginBLManager.getAuthPluginList();
			request.setAttribute("prePluginList", prePluginList);
			
			/* Driver Script and External Radius Script */
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			List<ScriptInstanceData> driverScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.DRIVER_SCRIPT);
			List<ScriptInstanceData> externalScriptList = (List<ScriptInstanceData>) scriptBLManager.getScriptInstanceDataByTypeId(ScriptTypesConstants.EXTERNAL_RADIUS_SCRIPT);
				
			createRadiusServicePolicyForm.setDriverScriptList(driverScriptList);
			createRadiusServicePolicyForm.setExternalScriptList(externalScriptList);
			
			String[] cacheableDriverInstIds = new String [cacheableDriverInstDataList.size()];
			String[][] cacheableDriverInstanceNames = new String[cacheableDriverInstDataList.size()][2]; 
			
			for(int i=0;i<cacheableDriverInstDataList.size();i++){
				DriverInstanceData data = cacheableDriverInstDataList.get(i);				
				cacheableDriverInstanceNames[i][0] = String.valueOf(data.getName());
				cacheableDriverInstanceNames[i][1] = String.valueOf(data.getDescription());
				cacheableDriverInstIds[i] = String.valueOf(data.getDriverInstanceId());
			}
			
			request.setAttribute("cacheableDriverInstIds", cacheableDriverInstIds);
			request.setAttribute("cacheableDriverInstanceNames", cacheableDriverInstanceNames);
			request.setAttribute("cacheableDriverInstDataList", cacheableDriverInstDataList);
			
			List<DriverInstanceData> listOfAcctDriver = driverBlManager.getDriverInstanceList(ServiceTypeConstants.ACCOUNTING_SERVICE);
			List<DriverTypeData> driverTypeList=driverBlManager.getDriverTypeList(ServiceTypeConstants.ACCOUNTING_SERVICE);

			imdgConfigurationData(createRadiusServicePolicyForm);

			setRadiusEsiGroupData(createRadiusServicePolicyForm);

			request.setAttribute("driverInstanceIds", driverInstanceIds);
			request.setAttribute("driverInstanceNames", driverInstanceNames);
			request.setAttribute("listOfDriver", driverList);
			request.setAttribute("listOfAcctDriver", listOfAcctDriver);
			request.setAttribute("translationMappingConfDataList", translationMappingConfDataList);
			request.setAttribute("copyPacketMappingConfDataList", copyPacketMappingConfDataList);
			request.setAttribute("driverTypeList", driverTypeList);
			request.getSession().setAttribute("createRadiusServicePolicyForm", createRadiusServicePolicyForm);
			
			return mapping.findForward(SUCCESS_FORWARD);
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
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

		}
		return mapping.findForward(FAILURE);
	}

	//This function is used to add Radius ESI Group data in CreateRadiusServicePolicyForm
	private void setRadiusEsiGroupData(CreateRadiusServicePolicyForm form) throws DataManagerException, JAXBException {
		RadiusESIGroupBLManager esiBlManager = new RadiusESIGroupBLManager();
		List<RadiusESIGroupData> radiusESIGroupDataList = esiBlManager.getRadiusESIGroupDataList();
		List<String> radiusEsiGroupNames = new ArrayList<>();

		if(Collectionz.isNullOrEmpty(radiusESIGroupDataList) == false){
			for (RadiusESIGroupData esiData:radiusESIGroupDataList) {

				String xmlDatas = new String(esiData.getEsiGroupDataXml());
				StringReader stringReader =new StringReader(xmlDatas.trim());

				RadiusEsiGroupConfigurationData esiConfigurationData = ConfigUtil.deserialize(stringReader, RadiusEsiGroupConfigurationData.class);
				if(RadiusEsiType.ACCT.name.equalsIgnoreCase(esiConfigurationData.getEsiType()) == false){
					radiusEsiGroupNames.add(esiData.getName());
				}
			}
		}
		form.setRadiusEsiGroupNames(radiusEsiGroupNames);
	}

	// This function is used to add Imdg Confifuration data in createRadiusServicePolicyForm
	private void imdgConfigurationData(CreateRadiusServicePolicyForm form) throws Exception {
		InMemoryDataGridBLManager inMemoryDataGridBLManager = new InMemoryDataGridBLManager();
		InMemoryDataGridData imdgData = inMemoryDataGridBLManager.getInMemoryDatagridConfiguration();
		ImdgConfigData existingIMDGData;
		if(imdgData == null){
			form.setImdgEnable(false);
		} else {
			String existingxmlDatas = new String(imdgData.getImdgXml());
			StringReader stringReader =new StringReader(existingxmlDatas.trim());

			//Convert into relevant POJO
			JAXBContext context = JAXBContext.newInstance(ImdgConfigData.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			existingIMDGData = (ImdgConfigData) unmarshaller.unmarshal(stringReader);

			if(existingIMDGData.isActive()){
				List<String> imdgFieldNames = new ArrayList<>();
				List<ImdgIndexDetail> imdgIndexDetailList =  existingIMDGData.getImdgRadiusConfig().getRadiusIMDGFieldMapping();

				for (ImdgIndexDetail imdgIndexDetail : imdgIndexDetailList) {
					imdgFieldNames.add(imdgIndexDetail.getImdgFieldValue());
				}
				form.setImdgEnable(true);
				form.setImdgFieldNames(imdgFieldNames);
			}else {
				form.setImdgEnable(false);
			}
		}
	}

}
