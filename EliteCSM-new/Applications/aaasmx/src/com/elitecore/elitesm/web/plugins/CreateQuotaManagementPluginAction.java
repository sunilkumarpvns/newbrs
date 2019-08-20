package com.elitecore.elitesm.web.plugins;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementData;
import com.elitecore.aaa.radius.plugins.quotamgmt.conf.QuotaManagementPluginConfiguration;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
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

/**
 * @author nayana.rathod
 *
 */
public class CreateQuotaManagementPluginAction extends BaseWebAction{

	private static final String ACTION_ALIAS = ConfigConstant.CREATE_PLUGIN;
	private static final String MODULE = CreateQuotaManagementPluginAction.class.getSimpleName();
	private static final String CREATE_FORWARD = "quotaManagementPlugin";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logInfo(MODULE,"Enter execute method of :- " + getClass().getName());
		
		try{
			
			/* Check Permission of Plugin Module */
			checkActionPermission(request, ACTION_ALIAS);
			
			QuotaManagementPluginForm quotaManagementPluginForm = (QuotaManagementPluginForm)form;
			
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
		
			PluginBLManager pluginBLManager = new PluginBLManager();
			PluginInstData pluginInstData = new PluginInstData();
			QuotaMgtPluginData quotaMgtPluginData = new QuotaMgtPluginData();
			CreatePluginConfig pluginConfig = new CreatePluginConfig();
				
			if("create".equals(quotaManagementPluginForm.getAction())){
			
				try{
				
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					convertFromFormToData(quotaManagementPluginForm,pluginInstData);
					
					String quotaMgtJSON = quotaManagementPluginForm.getQuotaMgtJson();

					Logger.getLogger().info(MODULE, "Received JSON String from UI is : "+ quotaMgtJSON);
					
					if (quotaMgtJSON != null && quotaMgtJSON.length() > 0) {
						
						 JSONArray quotaMgtPluginArray = JSONArray.fromObject(quotaMgtJSON);
						
						for (Object obj : quotaMgtPluginArray) {
						 
							 Map<String,Class> configObj = new HashMap<String, Class>();
							 configObj.put("pluginsData", QuotaManagementData.class);
							 
							 JSONObject pluginJSONObj = (JSONObject) obj;
							 JSONArray jsonArray = pluginJSONObj.getJSONArray("pluginsData");
							 pluginJSONObj.remove("pluginsData");
							 pluginJSONObj.put("pluginsData", EliteUtility.convertJsonFieldValueIntoHtmlEntity(
									 jsonArray, "ruleset"));
							
							 QuotaManagementPluginConfiguration quotaManagentPlugins = (QuotaManagementPluginConfiguration) JSONObject.toBean((JSONObject) pluginJSONObj, QuotaManagementPluginConfiguration.class, configObj);
							 quotaManagentPlugins.setStatus(pluginInstData.getStatus());	
							 
							 JAXBContext jaxbContext = JAXBContext.newInstance(QuotaManagementPluginConfiguration.class);
							 Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
							 jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
							 java.io.StringWriter xmlObj = new StringWriter();
							 jaxbMarshaller.marshal(quotaManagentPlugins,xmlObj);
								
							 /* Print Generated XML in Console (Info Mode) */
							 Logger.getLogger().info(MODULE, "Following is Generated XML from QuotaManagentPlugins Bean, ");
							   
							 String xmlDatas = xmlObj.toString().trim();
							 quotaMgtPluginData.setPluginData(xmlDatas.getBytes());
							    
							 Logger.getLogger().info(MODULE, xmlDatas.toString());
						}
					}

					pluginInstData.setCreatedByStaffId(currentUser);        	
					pluginInstData.setLastModifiedDate(getCurrentTimeStemp());
					pluginInstData.setLastModifiedByStaffId(currentUser);

					pluginConfig.setPluginInstData(pluginInstData);
					pluginConfig.setQuotaMgtPluginData(quotaMgtPluginData);

					/*Create plugin Code*/
					pluginBLManager.createQuotaMgtPlugin(pluginConfig,staffData);
					
					Logger.getLogger().info(MODULE, "Plugin [" + pluginInstData.getName() + "] Created Successfully");
					
					request.setAttribute("responseUrl", "/searchPlugin.do");
					ActionMessage message = new ActionMessage("plugin.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request, messages);
					
					return mapping.findForward(SUCCESS);
				}catch(DuplicateParameterFoundExcpetion dpf){
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,dpf);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("plugin.create.duplicate.failure",pluginInstData.getName());
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}catch(Exception e) {					
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("plugin.create.failure");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}

			}else{
				Logger.getLogger().info(MODULE, "Enter in else method of Create Quota Management Plugin method : ");
					
				if(quotaManagementPluginForm.getPluginType() == null || quotaManagementPluginForm.getPluginName() == null || quotaManagementPluginForm.getDescription() == null){
					quotaManagementPluginForm.setPluginName((String)request.getAttribute("name"));
					quotaManagementPluginForm.setDescription((String)request.getAttribute("description"));
					String pluginTypeId =(String)request.getAttribute("pluginTypeId");
					quotaManagementPluginForm.setPluginType(pluginTypeId.toString());
					quotaManagementPluginForm.setStatus((String)request.getAttribute("status"));	
				}
					
				return mapping.findForward(CREATE_FORWARD);
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
			ActionMessage message = new ActionMessage("plugin.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}

	private void convertFromFormToData( QuotaManagementPluginForm quotaManagementPluginForm, PluginInstData pluginInstData) {
		pluginInstData.setName(quotaManagementPluginForm.getPluginName());
		pluginInstData.setDescription(quotaManagementPluginForm.getDescription());
		pluginInstData.setPluginTypeId(quotaManagementPluginForm.getPluginType());		
		pluginInstData.setCreateDate(getCurrentTimeStemp());
		
		if(BaseConstant.SHOW_STATUS.equals(quotaManagementPluginForm.getStatus())){
			pluginInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			pluginInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}
	}
}
