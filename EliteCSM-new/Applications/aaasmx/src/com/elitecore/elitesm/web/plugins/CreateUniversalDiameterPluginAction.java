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

import com.elitecore.diameterapi.plugins.DiameterParamDetail;
import com.elitecore.diameterapi.plugins.DiameterUniversalPluginPolicyDetail;
import com.elitecore.diameterapi.plugins.universal.conf.DiameterUniversalPluginDetails;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.UniversalPluginData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.plugins.forms.CreateUniversalDiameterPluginForm;

/**
 * @author nayana.rathod
 *
 */
public class CreateUniversalDiameterPluginAction extends BaseWebAction{

	private static final String ACTION_ALIAS = ConfigConstant.CREATE_PLUGIN;
	private static final String MODULE = CreateUniversalDiameterPluginAction.class.getSimpleName();
	private static final String CREATE_FORWARD = "cUniversalDiameterPlugin";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of :- "+getClass().getName());
		try{
			/* Check Permission of Plugin Module */
			checkActionPermission(request, ACTION_ALIAS);
			
			CreateUniversalDiameterPluginForm createUniversalDiameterPluginForm = (CreateUniversalDiameterPluginForm)form;
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			
			PluginBLManager pluginBLManager = new PluginBLManager();
			PluginInstData pluginInstData = new PluginInstData();
			UniversalPluginData universalPluginData = new UniversalPluginData();
			CreatePluginConfig pluginConfig = new CreatePluginConfig();
		
			if("create".equals(createUniversalDiameterPluginForm.getAction())){
				try{
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
					String universalPluginPolicyJSON=createUniversalDiameterPluginForm.getUniversalPluginPolicyJson();

					Logger.getLogger().info(MODULE, "Received JSON String from UI is : "+ universalPluginPolicyJSON);
					
					if (universalPluginPolicyJSON != null && universalPluginPolicyJSON.length() > 0) {
						
						JSONArray universalPluginArray = JSONArray.fromObject(universalPluginPolicyJSON);
						
						for (Object universalObject : universalPluginArray) {
							
							Map<String,Class> configObj = new HashMap<String, Class>();
							configObj.put("inPluginList", DiameterUniversalPluginPolicyDetail.class);
							configObj.put("outPluginList",DiameterUniversalPluginPolicyDetail.class);
							configObj.put("parameterDetailsForPlugin", DiameterParamDetail.class);
							
							DiameterUniversalPluginDetails diameterUniversalPluginDetails = (DiameterUniversalPluginDetails)JSONObject.toBean((JSONObject) universalObject, DiameterUniversalPluginDetails.class, configObj); 
							
							/* Convert UniversalPluginConfigurationImpl Bean to XML Format */
							JAXBContext jaxbContext = JAXBContext.newInstance(DiameterUniversalPluginDetails.class);
						    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
						    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
						    java.io.StringWriter xmlObj = new StringWriter();
						    jaxbMarshaller.marshal(diameterUniversalPluginDetails,xmlObj);
							
						    /* Print Generated XML in Console (Info Mode) */
						    Logger.getLogger().info(MODULE, "Following is Generated XML from DiameterUniversalPluginDetails Bean, ");
						   
						    String xmlDatas = xmlObj.toString().trim();
						    universalPluginData.setPluginData(xmlDatas.getBytes());
						    
						    Logger.getLogger().info(MODULE, xmlDatas.toString());
						}
					}
					
					convertFromFormToData(createUniversalDiameterPluginForm,pluginInstData);

					pluginInstData.setCreatedByStaffId(currentUser);        	
					pluginInstData.setLastModifiedDate(getCurrentTimeStemp());
					pluginInstData.setLastModifiedByStaffId(currentUser);

					pluginConfig.setPluginInstData(pluginInstData);
					pluginConfig.setUniversalPluginData(universalPluginData);

					/*Create plugin Code*/
					pluginBLManager.createUniversalPlugin(pluginConfig,staffData);
					
					Logger.getLogger().info(MODULE, "Plugin [" + pluginInstData.getName() + "] Created Successfully");
					
					request.setAttribute("responseUrl", "/initSearchPlugin");
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
				Logger.getLogger().info(MODULE, "Enter in Else method of Create Universal Auth Plugin method : ");
				
				if(createUniversalDiameterPluginForm.getPluginType() == null || createUniversalDiameterPluginForm.getPluginName() == null || createUniversalDiameterPluginForm.getDescription() == null){
					createUniversalDiameterPluginForm.setPluginName((String)request.getAttribute("name"));
					createUniversalDiameterPluginForm.setDescription((String)request.getAttribute("description"));
					String pluginTypeId =(String)request.getAttribute("pluginTypeId");
					createUniversalDiameterPluginForm.setPluginType(pluginTypeId.toString());
					createUniversalDiameterPluginForm.setStatus((String)request.getAttribute("status"));	
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

	private void convertFromFormToData( CreateUniversalDiameterPluginForm createUniversalDiameterPluginForm, PluginInstData pluginInstData) {
		pluginInstData.setName(createUniversalDiameterPluginForm.getPluginName());
		pluginInstData.setDescription(createUniversalDiameterPluginForm.getDescription());
		pluginInstData.setPluginTypeId(createUniversalDiameterPluginForm.getPluginType());		
		pluginInstData.setCreateDate(getCurrentTimeStemp());
		if(BaseConstant.SHOW_STATUS.equals(createUniversalDiameterPluginForm.getStatus())){
			pluginInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			pluginInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}	
	}

}
