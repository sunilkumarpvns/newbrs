package com.elitecore.elitesm.web.plugins;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
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

import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementData;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConfiguration;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.quotamgrplugin.data.QuotaMgtPluginData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.plugins.forms.QuotaManagementPluginForm;
import com.google.gson.Gson;

/**
 * @author nayana.rathod
 *
 */
public class UpdateQuotaManagementPluginAction extends BaseWebAction{

	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_PLUGIN;
	private static final String MODULE = UpdateQuotaManagementPluginAction.class.getSimpleName();
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_PLUGIN;
	private static final String OPEN_FORWARD = "quotaManagementPlugin";
	private String pluginBindedWith = new String();
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			QuotaManagementPluginForm quotaManagementPluginForm = (QuotaManagementPluginForm)form;
			
			if("update".equals(quotaManagementPluginForm.getAction())) {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			} else {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			}
			
			PluginBLManager pluginBLManager = new PluginBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			PluginInstData pluginInstanceData = pluginBLManager.getListOfPluginInstanceByPluginInstanceId(quotaManagementPluginForm.getPluginInstanceId());
			QuotaMgtPluginData quotaMgtPluginData = pluginBLManager.getQuotaMgtPluginDataByPluginInstanceId(quotaManagementPluginForm.getPluginInstanceId());
			
			if(quotaMgtPluginData!=null){
				quotaManagementPluginForm.setPluginId(quotaMgtPluginData.getPluginId());
			}
				
			request.getSession().setAttribute("pluginInstanceData", pluginInstanceData);

			if(quotaManagementPluginForm.getAction() != null){
				
				if(quotaManagementPluginForm.getAction().equals("update")){
					
					convertFromFormToData(quotaManagementPluginForm,pluginInstanceData);
					Set<String> policyNames = new LinkedHashSet<String>();
					if(pluginInstanceData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
						String pluginName = pluginInstanceData.getName();
						policyNames.add(pluginBLManager.checkPluginBindInServerInstance(pluginName));
						policyNames.add(pluginBLManager.checkPluginBindInServerInstanceServices(pluginName));
						policyNames.add(pluginBLManager.checkPluginBindInRadiusPolicy(pluginName));
						if (policyNames != null && policyNames.isEmpty() == false && policyNames.size() > 1){
							pluginBindedWith = pluginBLManager.getFormatedStringForPlugin(policyNames);
							throw new ConstraintViolationException("Plugin is bound in " + pluginBindedWith + ". You are not allowed to Inactive this plugin .",null, "ConstraintViolationException");
						}
					}
					
					String quotaMgtJSON = quotaManagementPluginForm.getQuotaMgtJson();

					Logger.getLogger().info(MODULE, "Received JSON String from UI is : "+ quotaMgtJSON);
					
					QuotaManagementPluginConfiguration quotaMgtPluginOldObj = new  QuotaManagementPluginConfiguration();
					QuotaManagementPluginConfiguration quotaMgtPluginNewObj = new  QuotaManagementPluginConfiguration();
					
					if (quotaMgtJSON != null && quotaMgtJSON.length() > 0) {
						
						 JSONArray quotaMgtPluginArray = JSONArray.fromObject(quotaMgtJSON);
						
						 for (Object obj : quotaMgtPluginArray) {
							 
							 Map<String,Class> configObj = new HashMap<String, Class>();
							 configObj.put("pluginsData", QuotaManagementData.class);
							 
							 QuotaManagementPluginConfiguration quotaManagentPlugins = (QuotaManagementPluginConfiguration) JSONObject.toBean((JSONObject) obj, QuotaManagementPluginConfiguration.class, configObj);
							 quotaManagentPlugins.setStatus(pluginInstanceData.getStatus());	
							 
							 JAXBContext jaxbContext = JAXBContext.newInstance(QuotaManagementPluginConfiguration.class);
							 Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
							 jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
							 java.io.StringWriter xmlObj = new StringWriter();
							 jaxbMarshaller.marshal(quotaManagentPlugins,xmlObj);
							 
							 JSONObject pluginJSONObj = (JSONObject) obj;
							 JSONArray jsonArray = pluginJSONObj.getJSONArray("pluginsData");
							 pluginJSONObj.remove("pluginsData");
							 pluginJSONObj.put("pluginsData", EliteUtility.convertJsonFieldValueIntoHtmlEntity(
									 jsonArray, "ruleset"));
							 
							 quotaManagentPlugins = (QuotaManagementPluginConfiguration) JSONObject.toBean((JSONObject) pluginJSONObj, QuotaManagementPluginConfiguration.class, configObj);
							 quotaManagentPlugins.setStatus(pluginInstanceData.getStatus());	
							 
							 java.io.StringWriter decodedXmlObj = new StringWriter();
							 jaxbMarshaller.marshal(quotaManagentPlugins,decodedXmlObj);
								
							 /* Print Generated XML in Console (Info Mode) */
							 Logger.getLogger().info(MODULE, "Following is Generated XML from QuotaManagentPlugins Bean, ");
							   
							 String xmlDatas = xmlObj.toString().trim();
							 String decodedXmlDatas = decodedXmlObj.toString().trim();
							  
							 quotaMgtPluginOldObj = getObjectFromString(quotaMgtPluginData.getPluginData());
							 List<QuotaManagementData> quotaManagementDatas = quotaMgtPluginOldObj.getPluginsData();
							 int quotaManagementDatasSize = quotaManagementDatas.size();
							 for (int i = 0; i < quotaManagementDatasSize ; i++) {
								 QuotaManagementData quotaManagementData = quotaManagementDatas.get(i);
								 String oldRulset =quotaManagementData.getRuleset();
								 if(oldRulset != null && oldRulset.isEmpty() == false){
									 quotaManagementData.setRuleset(EliteUtility.encodeHtmlEntity(oldRulset));
								 }
							 }
							 quotaMgtPluginNewObj = getObjectFromString(xmlDatas.getBytes());
							 
							 quotaMgtPluginData.setPluginData(decodedXmlDatas.getBytes());
							 Logger.getLogger().info(MODULE, decodedXmlDatas.toString());
						}
					}
					
					pluginInstanceData.setLastModifiedDate(getCurrentTimeStemp());
					pluginInstanceData.setLastModifiedByStaffId(currentUser);
						
					try{
						staffData.setAuditId(pluginInstanceData.getAuditUId());
						staffData.setAuditName(pluginInstanceData.getName());
						
						pluginBLManager.updateQuotaMgtPlugin(pluginInstanceData, quotaMgtPluginData, staffData, UPDATE_ACTION_ALIAS, quotaMgtPluginOldObj, quotaMgtPluginNewObj);
							
						doAuditing(staffData, UPDATE_ACTION_ALIAS);
							
						request.setAttribute("responseUrl", "/viewPluginInstance.do?pluginInstanceId=" + quotaManagementPluginForm.getPluginInstanceId());
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
			quotaManagementPluginForm.setPluginName(pluginInstanceData.getName());
			quotaManagementPluginForm.setDescription(pluginInstanceData.getDescription());
			quotaManagementPluginForm.setAuditUId(pluginInstanceData.getAuditUId());
			
			if(BaseConstant.HIDE_STATUS_ID.equals(pluginInstanceData.getStatus())){
				quotaManagementPluginForm.setStatus(BaseConstant.HIDE_STATUS);
			}else{
				quotaManagementPluginForm.setStatus(BaseConstant.SHOW_STATUS);
			}
			
			/* Setting Universal Auth Plugin Data */
			quotaManagementPluginForm.setPluginInstanceId(quotaMgtPluginData.getPluginInstanceId());
			quotaManagementPluginForm.setPluginId(quotaMgtPluginData.getPluginId());
			
			String quotaMgtPluginJson = getQuotaMgtJsonDataString(quotaMgtPluginData.getPluginData());
			if( quotaMgtPluginJson != null && quotaMgtPluginJson.length() > 0 ){
				quotaManagementPluginForm.setQuotaMgtJson(quotaMgtPluginJson);
			}
			
			request.getSession().setAttribute("quotaManagementPluginForm", quotaManagementPluginForm);
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
			pluginBindedWith = "";
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

	private String getQuotaMgtJsonDataString(byte[] pluginData) {
		String universalAuthPluginJson = null;
		
		try {
			String xmlDatas = new String(pluginData);
			StringReader stringReader = new StringReader(xmlDatas.trim());
			JAXBContext context = JAXBContext.newInstance(QuotaManagementPluginConfiguration.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			QuotaManagementPluginConfiguration quotaManagentPlugins = (QuotaManagementPluginConfiguration) unmarshaller.unmarshal(stringReader);

			Gson gson = new Gson();
			universalAuthPluginJson = gson.toJson(quotaManagentPlugins);
			Logger.getLogger().info(MODULE, "QuotaManagentPlugins JSON String is :" + universalAuthPluginJson);
			
		} catch (Exception e) {
			Logger.getLogger().error(MODULE, "Error occured while converting byte to json object : " + e.getMessage());
		}
	
		return universalAuthPluginJson;
	
	}
	
	private void convertFromFormToData( QuotaManagementPluginForm quotaManagementPluginForm, PluginInstData pluginInstanceData) {
		pluginInstanceData.setName(quotaManagementPluginForm.getPluginName());
		pluginInstanceData.setDescription(quotaManagementPluginForm.getDescription());
		pluginInstanceData.setPluginInstanceId(quotaManagementPluginForm.getPluginInstanceId());
		pluginInstanceData.setAuditUId(quotaManagementPluginForm.getAuditUId());
		
		if(BaseConstant.SHOW_STATUS.equals(quotaManagementPluginForm.getStatus())){
			pluginInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			pluginInstanceData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}
	}
	
	private QuotaManagementPluginConfiguration getObjectFromString(byte[] pluginData) {
		QuotaManagementPluginConfiguration  quotaManagentPlugins = null;
		
		try {
			String xmlDatas = new String(pluginData);
			StringReader stringReader = new StringReader(xmlDatas.trim());
			JAXBContext context = JAXBContext.newInstance(QuotaManagementPluginConfiguration.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			quotaManagentPlugins = (QuotaManagementPluginConfiguration) unmarshaller.unmarshal(stringReader);
		} catch (Exception e) {
			Logger.getLogger().error(MODULE, "Error occured while converting byte to json object : " + e.getMessage());
		}
	
		return quotaManagentPlugins;	
	}
}
