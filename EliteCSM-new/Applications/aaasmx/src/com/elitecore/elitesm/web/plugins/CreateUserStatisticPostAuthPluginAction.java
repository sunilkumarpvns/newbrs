package com.elitecore.elitesm.web.plugins;

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

import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurationImpl;
import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurationImpl.AttributeDetail;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.usrstatpostauthplugin.data.UserStatPostAuthPluginData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.plugins.forms.UserStatisticPostAuthPluginForm;
import com.google.gson.Gson;

public class CreateUserStatisticPostAuthPluginAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_PLUGIN;
	private static final String MODULE = CreateUserStatisticPostAuthPluginAction.class.getSimpleName();
	private static final String CREATE_FORWARD = "userStatisticPostAuthPlugin";

	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Enter execute method of :- "+ getClass().getName());
		try {
			/* Check Permission of Plugin Module */
			checkActionPermission(request, ACTION_ALIAS);

			UserStatisticPostAuthPluginForm userStatisticPostAuthPluginForm = (UserStatisticPostAuthPluginForm) form;
			String currentUser = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			PluginBLManager pluginBLManager = new PluginBLManager();
			PluginInstData pluginInstData = new PluginInstData();
			UserStatPostAuthPluginData userStatPostAuthPluginData = new UserStatPostAuthPluginData();
			CreatePluginConfig pluginConfig = new CreatePluginConfig();
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			
			if ("create".equals(userStatisticPostAuthPluginForm.getAction())) {
				try {
					IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
					convertFormToBean(userStatisticPostAuthPluginForm,pluginInstData, userStatPostAuthPluginData);
					String userStatPostAuthJSON = userStatisticPostAuthPluginForm.getUserStatPostAuthJson();
					Logger.getLogger().info(MODULE,"Received JSON String from UI is : "+ userStatPostAuthJSON);

					if (userStatPostAuthJSON != null && userStatPostAuthJSON.length() > 0) {
						JSONArray userStatPostAuthPluginArray = JSONArray.fromObject(userStatPostAuthJSON);

						for (Object obj : userStatPostAuthPluginArray) {
							Map<String, Class> configObj = new HashMap<String, Class>();
							
							configObj.put("attributeList",AttributeDetail.class);
							
							UserStatisticPostAuthPluginConfigurationImpl userStatisticPostAuthPluginConfigurationImpl = (UserStatisticPostAuthPluginConfigurationImpl) JSONObject.toBean((JSONObject) obj,UserStatisticPostAuthPluginConfigurationImpl.class,configObj);
							userStatisticPostAuthPluginConfigurationImpl.setStatus(pluginInstData.getStatus());
							userStatisticPostAuthPluginConfigurationImpl.setDataSourceName(userStatPostAuthPluginData.getDataSourceName());
							userStatisticPostAuthPluginConfigurationImpl.setTableName(userStatPostAuthPluginData.getTableName());
							userStatisticPostAuthPluginConfigurationImpl.setDbQueryTimeoutInMs(userStatPostAuthPluginData.getDbQueryTimeout());
							userStatisticPostAuthPluginConfigurationImpl.setMaxQueryTimeoutCount(userStatPostAuthPluginData.getMaxQueryTimeoutCount());
							userStatisticPostAuthPluginConfigurationImpl.setBatchUpdateIntervalInMs(userStatPostAuthPluginData.getBatchUpdateInterval());

    						String xmlObj=ConfigUtil.serialize(UserStatisticPostAuthPluginConfigurationImpl.class,userStatisticPostAuthPluginConfigurationImpl);

							/* Print Generated XML in Console (Info Mode) */
							Logger.getLogger().info(MODULE,"Following is Generated XML from UserStatisticPostAuthPlugins Bean, ");

							userStatPostAuthPluginData.setPluginData(xmlObj.getBytes());
							Logger.getLogger().info(MODULE, xmlObj.toString());
						}
					}
					pluginInstData.setCreatedByStaffId(currentUser);
					pluginInstData.setLastModifiedDate(getCurrentTimeStemp());
					pluginInstData.setLastModifiedByStaffId(currentUser);
					pluginConfig.setPluginInstData(pluginInstData);
					pluginConfig.setUserStatPostAuthPluginData(userStatPostAuthPluginData);

					/* Create plugin Code */
					pluginBLManager.createUserStatPostAuthPlugin(pluginConfig,staffData);
					Logger.getLogger().info(MODULE,"Plugin [" + pluginInstData.getName()+ "] Created Successfully");
					request.setAttribute("responseUrl", "/searchPlugin.do");
					ActionMessage message = new ActionMessage("plugin.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request, messages);

					return mapping.findForward(SUCCESS);
				} catch (DuplicateParameterFoundExcpetion dpf) {
					Logger.logError(MODULE, "Returning error forward from "+ getClass().getName());
					Logger.logTrace(MODULE, dpf);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("plugin.create.duplicate.failure",pluginInstData.getName());
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveErrors(request, messages);
					return mapping.findForward(FAILURE);
				} catch (Exception e) {
					Logger.logError(MODULE, "Returning error forward from "+ getClass().getName());
					Logger.logTrace(MODULE, e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("plugin.create.failure");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveErrors(request, messages);
					return mapping.findForward(FAILURE);
				}

			} else {
				Logger.getLogger().info(MODULE,"Enter in else method of Create User Statistic Post Auth Plugin method : ");
				request.setAttribute("defaultMapping", userStatisticPostAuthPluginForm.getDefaultmapping());
				List<IDatabaseDSData> databaseList = databaseDSBLManager.getDatabaseDSList();
				userStatisticPostAuthPluginForm.setDatabaseDSList(databaseList);
				userStatisticPostAuthPluginForm.setTableName("TBLUSERSTATISTICS");
				userStatisticPostAuthPluginForm.setDbQueryTimeoutInMs(1000);
				userStatisticPostAuthPluginForm.setMaxQueryTimeoutCount(100);
				userStatisticPostAuthPluginForm.setBatchUpdateIntervalInMs(1000);
				String userStatPostAuthPluginJson = null;
				Gson gson = new Gson();
				userStatPostAuthPluginJson = gson.toJson(userStatisticPostAuthPluginForm.getDefaultmapping());
				if( userStatPostAuthPluginJson != null && userStatPostAuthPluginJson.length() > 0 ){
					userStatisticPostAuthPluginForm.setUserStatPostAuthJson(userStatPostAuthPluginJson);
				}
				
				if (userStatisticPostAuthPluginForm.getPluginType() == null || userStatisticPostAuthPluginForm.getPluginName() == null || userStatisticPostAuthPluginForm.getDescription() == null) {
					userStatisticPostAuthPluginForm.setPluginName((String) request.getAttribute("name"));
					userStatisticPostAuthPluginForm.setDescription((String) request.getAttribute("description"));
					String pluginTypeId = (String) request.getAttribute("pluginTypeId");
					userStatisticPostAuthPluginForm.setPluginType(pluginTypeId.toString());
				}
				request.getSession().setAttribute("userStatisticPostAuthPluginForm", userStatisticPostAuthPluginForm);
				return mapping.findForward(CREATE_FORWARD);
			}
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("plugin.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}

	/**
	 * This Method Is Used To Store Data Which Configured At GUI Side
	 * @param userStatisticPostAuthPluginForm contain GUI Side Configuration
	 * @param pluginInstData To Store Data Come From GUI Side
	 * @param userStatPostAuthPluginData To Store Data Come From GUI Side
	 * @throws DataManagerException
	 */
	private void convertFormToBean(UserStatisticPostAuthPluginForm userStatisticPostAuthPluginForm,PluginInstData pluginInstData, UserStatPostAuthPluginData userStatPostAuthPluginData) throws DataManagerException {
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		pluginInstData.setName(userStatisticPostAuthPluginForm.getPluginName());
		pluginInstData.setDescription(userStatisticPostAuthPluginForm.getDescription());
		pluginInstData.setPluginTypeId(userStatisticPostAuthPluginForm.getPluginType());
		pluginInstData.setCreateDate(getCurrentTimeStemp());
		userStatPostAuthPluginData.setDatabaseId(userStatisticPostAuthPluginForm.getDatabaseId());
		
		IDatabaseDSData databaseDSData = databaseDSBLManager.getDatabaseDSDataById(userStatisticPostAuthPluginForm.getDatabaseId());
		userStatPostAuthPluginData.setDataSourceName(databaseDSData.getName());
		
		userStatPostAuthPluginData.setTableName(userStatisticPostAuthPluginForm.getTableName());
		userStatPostAuthPluginData.setDbQueryTimeout(userStatisticPostAuthPluginForm.getDbQueryTimeoutInMs());
		userStatPostAuthPluginData.setMaxQueryTimeoutCount(userStatisticPostAuthPluginForm.getMaxQueryTimeoutCount());
		userStatPostAuthPluginData.setBatchUpdateInterval(userStatisticPostAuthPluginForm.getBatchUpdateIntervalInMs());

		if (BaseConstant.SHOW_STATUS.equals(userStatisticPostAuthPluginForm.getStatus())) {
			pluginInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
		} else {
			pluginInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}
	}
}