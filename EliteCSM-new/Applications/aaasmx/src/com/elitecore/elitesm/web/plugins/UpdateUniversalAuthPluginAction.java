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
import com.elitecore.elitesm.web.plugins.forms.UpdateUniversalAuthPluginForm;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.AAAUniversalPluginPolicyDetail;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.RadiusParamDetails;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.UniversalPluginConfigurationImpl;
import com.google.gson.Gson;

/**
 * @author nayana.rathod
 *
 */
public class UpdateUniversalAuthPluginAction extends BaseWebAction{


	private static final String OPEN_FORWARD = "universalAuthPlugin";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_PLUGIN;
	private static final String MODULE = UpdateUniversalAuthPluginAction.class.getSimpleName();
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_PLUGIN;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		String pluginBindedWith = new String();
		try{
			
			UpdateUniversalAuthPluginForm updateUniversalAuthPluginForm = (UpdateUniversalAuthPluginForm)form;
			
			if("update".equals(updateUniversalAuthPluginForm.getAction())) {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			} else {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			}
			
			PluginBLManager pluginBLManager = new PluginBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			PluginInstData pluginInstanceData = pluginBLManager.getListOfPluginInstanceByPluginInstanceId(updateUniversalAuthPluginForm.getPluginInstanceId());
			UniversalPluginData universalAuthPlugin = pluginBLManager.getUniversalAuthPluginDataByPluginInstanceId(updateUniversalAuthPluginForm.getPluginInstanceId());
			
			if(universalAuthPlugin!=null){
				updateUniversalAuthPluginForm.setPluginId(universalAuthPlugin.getPluginId());
			}
			
			request.getSession().setAttribute("pluginInstanceData", pluginInstanceData);

			if(updateUniversalAuthPluginForm.getAction() != null){
				
				if(updateUniversalAuthPluginForm.getAction().equals("update")){

					PluginInstData updatedPluginInstData = new  PluginInstData();
					UniversalPluginData updatedUniversalPluginData = new UniversalPluginData();
					
					/* Getting Json String from UI */
					String universalPluginPolicyJSON=updateUniversalAuthPluginForm.getUniversalPluginPolicyJson();
					
					Logger.getLogger().info(MODULE, "Received JSON String from UI is : " + universalPluginPolicyJSON);
					
					/* Convert JSON String to XML Data */
					String xmlData = getXMLData(universalPluginPolicyJSON);
					UniversalPluginConfigurationImpl universalPluginConfigurationImplOldObj = getObjectFromString(universalAuthPlugin.getPluginData());
					UniversalPluginConfigurationImpl universalPluginConfigurationImplNewObj = getObjectFromString(xmlData.getBytes());

					convertFromFormToData(updateUniversalAuthPluginForm,updatedPluginInstData,updatedUniversalPluginData, xmlData);
				    
					Set<String> policyNames = new LinkedHashSet<String>();
					if(updatedPluginInstData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
						String pluginName = pluginInstanceData.getName();
						policyNames.add(pluginBLManager.checkPluginBindInServerInstance(pluginName));
						policyNames.add(pluginBLManager.checkPluginBindInServerInstanceServices(pluginName));
						policyNames.add(pluginBLManager.checkPluginBindInRadiusPolicy(pluginName));
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

                     	pluginBLManager.updateRadiusUniversalPlugin(updatedPluginInstData,updatedUniversalPluginData, staffData,universalPluginConfigurationImplOldObj,universalPluginConfigurationImplNewObj);
						
						request.setAttribute("responseUrl", "/viewPluginInstance.do?pluginInstanceId=" + updateUniversalAuthPluginForm.getPluginInstanceId());
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
			updateUniversalAuthPluginForm.setPluginName(pluginInstanceData.getName());
			updateUniversalAuthPluginForm.setDescription(pluginInstanceData.getDescription());
			updateUniversalAuthPluginForm.setAuditUId(pluginInstanceData.getAuditUId());
			
			if(BaseConstant.HIDE_STATUS_ID.equals(pluginInstanceData.getStatus())){
				updateUniversalAuthPluginForm.setStatus(BaseConstant.HIDE_STATUS);
			}else{
				updateUniversalAuthPluginForm.setStatus(BaseConstant.SHOW_STATUS);
			}
			
			String universalAuthPluginJson = getUniversalPluginJsonDataString(universalAuthPlugin.getPluginData());
			
			/* Setting Universal Auth Plugin Data */
			updateUniversalAuthPluginForm.setPluginInstanceId(universalAuthPlugin.getPluginInstanceId());
			updateUniversalAuthPluginForm.setPluginId(universalAuthPlugin.getPluginId());
			
			if( universalAuthPluginJson != null && universalAuthPluginJson.length() > 0 )
				updateUniversalAuthPluginForm.setUniversalPluginPolicyJson(universalAuthPluginJson);
		
			request.getSession().setAttribute("pluginInstance",pluginInstanceData);
			
			return mapping.findForward(OPEN_FORWARD);
			
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

	private UniversalPluginConfigurationImpl getObjectFromString(byte[] xmlData) {
		UniversalPluginConfigurationImpl  universalPluginConfigurationImpl = null;
		
		try {
			String xmlDatas = new String(xmlData);
			StringReader stringReader = new StringReader(xmlDatas.trim());
			JAXBContext context = JAXBContext.newInstance(UniversalPluginConfigurationImpl.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			universalPluginConfigurationImpl = (UniversalPluginConfigurationImpl) unmarshaller.unmarshal(stringReader);
		} catch (Exception e) {
			Logger.getLogger().error(MODULE, "Error occured while converting byte to json object : " + e.getMessage());
		}
	
		return universalPluginConfigurationImpl;
	}

	private String getXMLData(String universalPluginPolicyJSON) throws JAXBException {
		String xmlDatas = null;
		if (universalPluginPolicyJSON != null && universalPluginPolicyJSON.length() > 0) {
			
			JSONArray universalPluginArray = JSONArray.fromObject(universalPluginPolicyJSON);
			
			for (Object universalObject : universalPluginArray) {
				
				Map<String,Class> configObj = new HashMap<String, Class>();
				configObj.put("prePolicyLists", AAAUniversalPluginPolicyDetail.class);
				configObj.put("postPolicyLists",AAAUniversalPluginPolicyDetail.class);
				configObj.put("parameterDetailsForPlugin", RadiusParamDetails.class);
				
				UniversalPluginConfigurationImpl universalPluginConfImpl = (UniversalPluginConfigurationImpl)JSONObject.toBean((JSONObject) universalObject, UniversalPluginConfigurationImpl.class, configObj); 
				
				/* Convert UniversalPluginConfigurationImpl Bean to XML Format */
				JAXBContext jaxbContext = JAXBContext.newInstance(UniversalPluginConfigurationImpl.class);
			    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			    java.io.StringWriter xmlObj = new StringWriter();
			    jaxbMarshaller.marshal(universalPluginConfImpl,xmlObj);
				
			    /* Print Generated XML in Console (Info Mode) */
			    Logger.getLogger().info(MODULE, "Following is Generated XML from UniversalPluginConfigurationImpl Bean, ");
			   
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
			JAXBContext context = JAXBContext.newInstance(UniversalPluginConfigurationImpl.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			UniversalPluginConfigurationImpl universalPluginConfigurationImpl = (UniversalPluginConfigurationImpl) unmarshaller.unmarshal(stringReader);

			Gson gson = new Gson();
			universalAuthPluginJson = gson.toJson(universalPluginConfigurationImpl);
			Logger.getLogger().info(MODULE, "Universal Plugin JSON String is :" + universalAuthPluginJson);
			
		} catch (Exception e) {
			Logger.getLogger().error(MODULE, "Error occured while converting byte to json object : " + e.getMessage());
		}
		
		return universalAuthPluginJson;
	
	}

	private void convertFromFormToData( UpdateUniversalAuthPluginForm updateUniversalAuthPluginForm, PluginInstData updatedPluginInstData, UniversalPluginData updatedUniversalPluginData, String xmlData) {
		
		updatedPluginInstData.setName(updateUniversalAuthPluginForm.getPluginName());
		updatedPluginInstData.setDescription(updateUniversalAuthPluginForm.getDescription());
		updatedPluginInstData.setPluginInstanceId(updateUniversalAuthPluginForm.getPluginInstanceId());
		updatedPluginInstData.setAuditUId(updateUniversalAuthPluginForm.getAuditUId());
		
		if(BaseConstant.SHOW_STATUS.equals(updateUniversalAuthPluginForm.getStatus())){
			updatedPluginInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			updatedPluginInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}	
		
		updatedUniversalPluginData.setPluginId(updateUniversalAuthPluginForm.getPluginId());
		updatedUniversalPluginData.setPluginInstanceId(updateUniversalAuthPluginForm.getPluginInstanceId());
		updatedUniversalPluginData.setPluginData(xmlData.getBytes());
		
	}
}
