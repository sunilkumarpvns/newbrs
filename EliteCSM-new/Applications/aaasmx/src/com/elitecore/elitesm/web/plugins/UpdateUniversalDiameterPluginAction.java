package com.elitecore.elitesm.web.plugins;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.UniversalPluginData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.plugins.forms.UpdateUniversalDiameterPluginForm;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin.DiameterParamDetail;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin.DiameterUniversalPluginDetails;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin.DiameterUniversalPluginPolicyDetail;
import com.google.gson.Gson;

/**
 * @author nayana.rathod
 *
 */
public class UpdateUniversalDiameterPluginAction extends BaseWebAction{

	private static final String MAPPING_FORWARD = "universalDiameterPlugin";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_PLUGIN;
	private static final String MODULE = UpdateUniversalDiameterPluginAction.class.getSimpleName();
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_PLUGIN;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		String pluginBindedWith = new String();
		try{
			UpdateUniversalDiameterPluginForm updateUniversalDiameterPluginForm = (UpdateUniversalDiameterPluginForm)form;
			
			if("update".equals(updateUniversalDiameterPluginForm.getAction())) {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			} else {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			}
			
			PluginBLManager pluginBLManager = new PluginBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			PluginInstData pluginInstanceData = pluginBLManager.getListOfPluginInstanceByPluginInstanceId(updateUniversalDiameterPluginForm.getPluginInstanceId());
			UniversalPluginData universalPlugin = pluginBLManager.getUniversalAuthPluginDataByPluginInstanceId(updateUniversalDiameterPluginForm.getPluginInstanceId());
			
			if(universalPlugin!=null){
				updateUniversalDiameterPluginForm.setPluginId(universalPlugin.getPluginId());
			}
			
			request.getSession().setAttribute("pluginInstanceData", pluginInstanceData);

			if(updateUniversalDiameterPluginForm.getAction() != null){
				
				if(updateUniversalDiameterPluginForm.getAction().equals("update")){

					PluginInstData updatedPluginInstData = new  PluginInstData();
					UniversalPluginData updatedUniversalPluginData = new UniversalPluginData();
					
					/* Getting Json String from UI */
					String universalPluginPolicyJSON=updateUniversalDiameterPluginForm.getUniversalPluginPolicyJson();
					
					Logger.getLogger().info(MODULE, "Received JSON String from UI is : " + universalPluginPolicyJSON);
					
					/* Convert JSON String to XML Data */
					String xmlData = getXMLData(universalPluginPolicyJSON);

					DiameterUniversalPluginDetails universalPluginConfigurationImplOldObj = getObjectFromString(universalPlugin.getPluginData());
					DiameterUniversalPluginDetails universalPluginConfigurationImplNewObj = getObjectFromString(xmlData.getBytes());
					
					convertFromFormToData(updateUniversalDiameterPluginForm,updatedPluginInstData,updatedUniversalPluginData, xmlData);
					
					Set<String> policyNames = new LinkedHashSet<String>();
					if(updatedPluginInstData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
						String pluginName = pluginInstanceData.getName();
						policyNames.add(pluginBLManager.checkPluginBindInServerInstance(pluginName));
						policyNames.add(pluginBLManager.checkPluginBindInServerInstanceServices(pluginName));
						policyNames.add(pluginBLManager.checkPluginBindInNASPolicy(pluginName));
						policyNames.add(pluginBLManager.checkPluginBindInEAPPolicy(pluginName));
						policyNames.add(pluginBLManager.checkPluginBindInCCPolicy(pluginName));
						policyNames.add(pluginBLManager.checkPluginBindInTGPPPolicy(pluginName));
						if (policyNames != null && policyNames.isEmpty() == false && policyNames.size() > 1){
							pluginBindedWith = pluginBLManager.getFormatedStringForPlugin(policyNames);
							throw new ConstraintViolationException("Plugin is bound in " + pluginBindedWith + ". You are not allowed to Inactive this plugin .",null, "ConstraintViolationException");
						}
					}
				
					updatedPluginInstData.setLastModifiedDate(getCurrentTimeStemp());
					updatedPluginInstData.setLastModifiedByStaffId(currentUser);

					try{
						staffData.setAuditId(updatedPluginInstData.getAuditUId());
						staffData.setAuditName(updatedPluginInstData.getName());
						
						pluginBLManager.updateDiameterUniversalPlugin(updatedPluginInstData,
								updatedUniversalPluginData, staffData,
								universalPluginConfigurationImplOldObj,
								universalPluginConfigurationImplNewObj);
						
						doAuditing(staffData, UPDATE_ACTION_ALIAS);
						
						request.setAttribute("responseUrl", "/viewPluginInstance.do?pluginInstanceId=" + updateUniversalDiameterPluginForm.getPluginInstanceId());
						ActionMessage message = new ActionMessage("plugin.update.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveMessages(request, messages);
						
						return mapping.findForward(SUCCESS);
						
					}catch(DataManagerException e){

						Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
						Logger.logTrace(MODULE,e);
						Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
						request.setAttribute("errorDetails", errorElements);
						ActionMessage message = new ActionMessage("plugin.update.failure");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE);

					}
				}
			}

			/* Setting PluginInstanceData */
			updateUniversalDiameterPluginForm.setPluginName(pluginInstanceData.getName());
			updateUniversalDiameterPluginForm.setDescription(pluginInstanceData.getDescription());
			updateUniversalDiameterPluginForm.setAuditUId(pluginInstanceData.getAuditUId());
			
			if(BaseConstant.HIDE_STATUS_ID.equals(pluginInstanceData.getStatus())){
				updateUniversalDiameterPluginForm.setStatus(BaseConstant.HIDE_STATUS);
			}else{
				updateUniversalDiameterPluginForm.setStatus(BaseConstant.SHOW_STATUS);
			}
			
			String universalPluginJson = getUniversalPluginJsonDataString(universalPlugin.getPluginData());
			
			/* Setting Universal Auth Plugin Data */
			updateUniversalDiameterPluginForm.setPluginInstanceId(universalPlugin.getPluginInstanceId());
			updateUniversalDiameterPluginForm.setPluginId(universalPlugin.getPluginId());
			
			if( universalPluginJson != null && universalPluginJson.length() > 0 )
				updateUniversalDiameterPluginForm.setUniversalPluginPolicyJson(universalPluginJson);
		
			request.getSession().setAttribute("pluginInstance",pluginInstanceData);
			
			return mapping.findForward(MAPPING_FORWARD);
			
		}catch (ConstraintViolationException e) {
			
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message ;
			if(pluginBindedWith != null && pluginBindedWith.isEmpty() == false){
				message = new ActionMessage("plugin.statuschange.failure",pluginBindedWith);
			}else{
				message = new ActionMessage("plugin.statuschange.failure","");
			}
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE);
			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("plugin.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE);
		}
	}

	private DiameterUniversalPluginDetails getObjectFromString( byte[] pluginData) {
		DiameterUniversalPluginDetails  universalPluginConfigurationImpl = null;
		
		try {
			String xmlDatas = new String(pluginData);
			StringReader stringReader = new StringReader(xmlDatas.trim());
			JAXBContext context = JAXBContext.newInstance(DiameterUniversalPluginDetails.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			universalPluginConfigurationImpl = (DiameterUniversalPluginDetails) unmarshaller.unmarshal(stringReader);
		} catch (Exception e) {
			Logger.getLogger().error(MODULE, "Error occured while converting byte to Object : " + e.getMessage());
		}
	
		return universalPluginConfigurationImpl;
	}

	private String getXMLData(String universalPluginPolicyJSON) throws JAXBException {
		String xmlDatas = null;
		if (universalPluginPolicyJSON != null && universalPluginPolicyJSON.length() > 0) {
			
			JSONArray universalPluginArray = JSONArray.fromObject(universalPluginPolicyJSON);
			
			for (Object universalObject : universalPluginArray) {
				
				Map<String,Class> configObj = new HashMap<String, Class>();
				configObj.put("inPluginList", DiameterUniversalPluginPolicyDetail.class);
				configObj.put("outPluginList",DiameterUniversalPluginPolicyDetail.class);
				configObj.put("parameterDetailsForPlugin", DiameterParamDetail.class);
				
				DiameterUniversalPluginDetails universalDiameterPlugin = (DiameterUniversalPluginDetails)JSONObject.toBean((JSONObject) universalObject, DiameterUniversalPluginDetails.class, configObj); 
				
				/* Convert UniversalPluginConfigurationImpl Bean to XML Format */
				JAXBContext jaxbContext = JAXBContext.newInstance(DiameterUniversalPluginDetails.class);
			    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			    java.io.StringWriter xmlObj = new StringWriter();
			    jaxbMarshaller.marshal(universalDiameterPlugin,xmlObj);
				
			    /* Print Generated XML in Console (Info Mode) */
			    Logger.getLogger().info(MODULE, "Following is Generated XML from DiameterUniversalPluginDetails Bean, ");
			   
			    xmlDatas = xmlObj.toString().trim();
			    Logger.getLogger().info(MODULE, xmlDatas.toString());
			}
		}
		return xmlDatas.toString();
	}

	private String getUniversalPluginJsonDataString(byte[] pluginData) {
		String universalAuthPluginJson = null;
		
		try {
			String xmlDatas = new String(pluginData);
			StringReader stringReader = new StringReader(xmlDatas.trim());
			JAXBContext context = JAXBContext.newInstance(DiameterUniversalPluginDetails.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			DiameterUniversalPluginDetails diameterUniversalPluginDetails = (DiameterUniversalPluginDetails) unmarshaller.unmarshal(stringReader);

			Gson gson = new Gson();
			universalAuthPluginJson = gson.toJson(diameterUniversalPluginDetails);
			Logger.getLogger().info(MODULE, "Universal Plugin JSON String is :" + universalAuthPluginJson);
			
		} catch (Exception e) {
			Logger.getLogger().error(MODULE, "Error occured while converting byte to json object : " + e.getMessage());
		}
	
		return universalAuthPluginJson;
	
	}

	private void convertFromFormToData( UpdateUniversalDiameterPluginForm updateUniversalDiameterPluginForm, PluginInstData updatedPluginInstData, UniversalPluginData updatedUniversalPluginData, String xmlData) {
		
		updatedPluginInstData.setName(updateUniversalDiameterPluginForm.getPluginName());
		updatedPluginInstData.setDescription(updateUniversalDiameterPluginForm.getDescription());
		updatedPluginInstData.setPluginInstanceId(updateUniversalDiameterPluginForm.getPluginInstanceId());
		updatedPluginInstData.setAuditUId(updateUniversalDiameterPluginForm.getAuditUId());
		
		updatedUniversalPluginData.setPluginId(updateUniversalDiameterPluginForm.getPluginId());
		updatedUniversalPluginData.setPluginInstanceId(updateUniversalDiameterPluginForm.getPluginInstanceId());
		updatedUniversalPluginData.setPluginData(xmlData.getBytes());
		
		if(BaseConstant.SHOW_STATUS.equals(updateUniversalDiameterPluginForm.getStatus())){
			updatedPluginInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			updatedPluginInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}
	}
}
