package com.elitecore.elitesm.web.plugins;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurationImpl;
import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurationImpl.AttributeDetail;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
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

public class UpdateUserStatisticPostAuthPluginAction extends BaseWebAction {

	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_PLUGIN;
	private static final String MODULE = UpdateUserStatisticPostAuthPluginAction.class.getSimpleName();
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_PLUGIN;
	private static final String OPEN_FORWARD = "userStatisticPostAuthPlugin";
	private static String DB_ID = "";
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Enter execute method of "+ getClass().getName());
		String pluginBindedWith = new String();
		try {
			UserStatisticPostAuthPluginForm userStatisticPostAuthPluginForm = (UserStatisticPostAuthPluginForm) form;

			if ("update".equals(userStatisticPostAuthPluginForm.getAction())) {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			} else {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			}
			
			PluginBLManager pluginBLManager = new PluginBLManager();
			PluginInstData pluginInstanceData = pluginBLManager.getListOfPluginInstanceByPluginInstanceId(userStatisticPostAuthPluginForm.getPluginInstanceId());
			UserStatPostAuthPluginData userStatPostAuthPluginData = pluginBLManager.getUserStatPostAuthPluginDataByPluginInstanceId(userStatisticPostAuthPluginForm.getPluginInstanceId());
			UserStatisticPostAuthPluginConfigurationImpl userStatisticPostAuthPluginsConfigurationImpl;
			
			if ("update".equals(userStatisticPostAuthPluginForm.getAction())) {
				SystemLoginForm systemLoginForm= ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm"));
				String currentUser = systemLoginForm.getUserId();
				IStaffData staffData = getStaffObject(systemLoginForm);
				
				userStatisticPostAuthPluginsConfigurationImpl = new UserStatisticPostAuthPluginConfigurationImpl();
				
				convertFormToBean(userStatisticPostAuthPluginForm,pluginInstanceData, userStatPostAuthPluginData);
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
				String userStatPostAuthJSON = userStatisticPostAuthPluginForm.getUserStatPostAuthJson();
				Logger.getLogger().info(MODULE,"Received JSON String from UI is : "+ userStatPostAuthJSON);

				UserStatisticPostAuthPluginConfigurationImpl userStatPostAuthPluginOldObj = new UserStatisticPostAuthPluginConfigurationImpl();
				UserStatisticPostAuthPluginConfigurationImpl userStatPostAuthPluginNewObj = new UserStatisticPostAuthPluginConfigurationImpl();

				if (userStatPostAuthJSON != null && userStatPostAuthJSON.length() > 0) {

					JSONArray userStatPostAuthPluginArray = JSONArray.fromObject(userStatPostAuthJSON);
					for (Object obj : userStatPostAuthPluginArray) {

						Map<String, Class> configObj = new HashMap<String, Class>();
						configObj.put("attributeList",AttributeDetail.class);

						UserStatisticPostAuthPluginConfigurationImpl userStatisticPostAuthPluginConfImpl = (UserStatisticPostAuthPluginConfigurationImpl) JSONObject.toBean((JSONObject) obj,UserStatisticPostAuthPluginConfigurationImpl.class,configObj);
						userStatisticPostAuthPluginsConfigurationImpl.setStatus(pluginInstanceData.getStatus());
						userStatisticPostAuthPluginConfImpl.setStatus(pluginInstanceData.getStatus());
						userStatisticPostAuthPluginConfImpl.setDataSourceName(userStatPostAuthPluginData.getDataSourceName());
						userStatisticPostAuthPluginConfImpl.setTableName(userStatPostAuthPluginData.getTableName());
						userStatisticPostAuthPluginConfImpl.setDbQueryTimeoutInMs(userStatPostAuthPluginData.getDbQueryTimeout());
						userStatisticPostAuthPluginConfImpl.setMaxQueryTimeoutCount(userStatPostAuthPluginData.getMaxQueryTimeoutCount());
						userStatisticPostAuthPluginConfImpl.setBatchUpdateIntervalInMs(userStatPostAuthPluginData.getBatchUpdateInterval());

						String xmlObj = ConfigUtil.serialize(UserStatisticPostAuthPluginConfigurationImpl.class,userStatisticPostAuthPluginConfImpl);
						JSONObject pluginJSONObj = (JSONObject) obj;

						userStatisticPostAuthPluginConfImpl = (UserStatisticPostAuthPluginConfigurationImpl) JSONObject.toBean((JSONObject) pluginJSONObj,UserStatisticPostAuthPluginConfigurationImpl.class,configObj);
						userStatisticPostAuthPluginConfImpl.setStatus(pluginInstanceData.getStatus());

						/* Print Generated XML in Console (Info Mode) */
						Logger.getLogger().info(MODULE,"Following is Generated XML from UserStatisticPostAuthPlugins Bean, ");

						userStatPostAuthPluginOldObj = ConfigUtil.deserialize(new String(userStatPostAuthPluginData.getPluginData()),UserStatisticPostAuthPluginConfigurationImpl.class);
						userStatPostAuthPluginNewObj = ConfigUtil.deserialize(xmlObj,UserStatisticPostAuthPluginConfigurationImpl.class);
						userStatPostAuthPluginData.setPluginData(xmlObj.getBytes());
						Logger.getLogger().info(MODULE, xmlObj.toString());
					}
				} 

				pluginInstanceData.setLastModifiedDate(getCurrentTimeStemp());
				pluginInstanceData.setLastModifiedByStaffId(currentUser);

				try {
					staffData.setAuditId(pluginInstanceData.getAuditUId());
					staffData.setAuditName(pluginInstanceData.getName());
					pluginBLManager.updateUserStatPostAuthPlugin(pluginInstanceData, userStatPostAuthPluginData,staffData, UPDATE_ACTION_ALIAS,userStatPostAuthPluginOldObj,userStatPostAuthPluginNewObj);
					doAuditing(staffData, UPDATE_ACTION_ALIAS);

					request.setAttribute("responseUrl","/viewPluginInstance.do?pluginInstanceId="+ userStatisticPostAuthPluginForm.getPluginInstanceId());
					ActionMessage message = new ActionMessage("plugin.update.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);

				} catch (DataManagerException e) {

					Logger.logError(MODULE, "Returning error forward from "+ getClass().getName());
					Logger.logTrace(MODULE, e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
					ActionMessage message = new ActionMessage("plugin.update.failure");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveErrors(request, messages);
					return mapping.findForward(FAILURE);
				}
			} else {
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				List<IDatabaseDSData> databaseList = databaseDSBLManager.getDatabaseDSList();
				
				String userStatPostAuthPluginJson = null;
				
				if (userStatPostAuthPluginData != null) {
					userStatisticPostAuthPluginForm.setPluginId(userStatPostAuthPluginData.getPluginId());
				}
				
				request.getSession().setAttribute("pluginInstanceData",pluginInstanceData);

				try {
					String xmlDatas = new String(userStatPostAuthPluginData.getPluginData());
					userStatisticPostAuthPluginsConfigurationImpl = ConfigUtil.deserialize(xmlDatas, UserStatisticPostAuthPluginConfigurationImpl.class);
					Gson gson = new Gson();
					userStatPostAuthPluginJson = gson.toJson(userStatisticPostAuthPluginsConfigurationImpl);

					JSONObject jsonObject = JSONObject.fromObject(userStatPostAuthPluginJson);
					String dbName = (String) jsonObject.get("dataSourceName");
				
					
					if (databaseList != null) {
						for (int i = 0; i < databaseList.size(); i++) {
							IDatabaseDSData tempDatabaseDsData = databaseList.get(i);

							if (tempDatabaseDsData.getName().equals(dbName)) {
								DB_ID = tempDatabaseDsData.getDatabaseId();
							}
						}
					}
					jsonObject.put("databaseId", DB_ID);
					userStatPostAuthPluginJson = jsonObject.toString();
					userStatisticPostAuthPluginForm.setDataSourceName(dbName);

					Logger.getLogger().info(MODULE,"UserStatisticPostAuthPlugins JSON String is :"+ userStatPostAuthPluginJson);
				} catch (Exception e) {
					Logger.getLogger().error(MODULE,"Error occured while converting byte to json object : "+ e.getMessage());
				}
				
				/* Setting PluginInstanceData */
				userStatisticPostAuthPluginForm.setPluginName(pluginInstanceData.getName());
				userStatisticPostAuthPluginForm.setDescription(pluginInstanceData.getDescription());
				userStatisticPostAuthPluginForm.setAuditUId(pluginInstanceData.getAuditUId());
				userStatisticPostAuthPluginForm.setDatabaseDSList(databaseList);
				userStatisticPostAuthPluginForm.setDatabaseId(DB_ID);

				if (BaseConstant.HIDE_STATUS_ID.equals(pluginInstanceData.getStatus())) {
					userStatisticPostAuthPluginForm.setStatus(BaseConstant.HIDE_STATUS);
				} else {
					userStatisticPostAuthPluginForm.setStatus(BaseConstant.SHOW_STATUS);
				}

				/* Setting User Statistic Post Auth Plugin Data */
				userStatisticPostAuthPluginForm.setPluginInstanceId(userStatPostAuthPluginData.getPluginInstanceId());
				userStatisticPostAuthPluginForm.setPluginId(userStatPostAuthPluginData.getPluginId());

				if (userStatPostAuthPluginJson != null && userStatPostAuthPluginJson.length() > 0) {
					userStatisticPostAuthPluginForm.setUserStatPostAuthJson(userStatPostAuthPluginJson);
				}
				request.getSession().setAttribute("userStatisticPostAuthPluginForm",userStatisticPostAuthPluginForm);
				request.getSession().setAttribute("pluginInstance",pluginInstanceData);
				
				return mapping.findForward(OPEN_FORWARD);
			}
		} catch (ConstraintViolationException e) {

			Logger.logError(MODULE, "Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message ;
			if(pluginBindedWith != null && pluginBindedWith.isEmpty() == false){
				message = new ActionMessage("plugin.statuschange.failure",pluginBindedWith);
			}else{
				message = new ActionMessage("plugin.statuschange.failure","");
			}
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);

		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		} catch (Exception e) {
			Logger.logError(MODULE, "Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("plugin.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		}
	}

	/**
	 * This Method Is Used To Store Data Which Configured At GUI Side
	 * @param userStatisticPostAuthPluginForm contain GUI Side Configuration
	 * @param pluginInstData To Store Data Come From GUI Side
	 * @param userStatPostAuthPluginData To Store Data Come From GUI Side
	 * @throws DataManagerException
	 */
	private void convertFormToBean(UserStatisticPostAuthPluginForm userStatisticPostAuthPluginForm,PluginInstData pluginInstanceData,UserStatPostAuthPluginData userStatPostAuthPluginData)throws DataManagerException {
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		List<IDatabaseDSData> databaseList = databaseDSBLManager.getDatabaseDSList();

		pluginInstanceData.setName(userStatisticPostAuthPluginForm.getPluginName());
		pluginInstanceData.setDescription(userStatisticPostAuthPluginForm.getDescription());
		pluginInstanceData.setPluginInstanceId(userStatisticPostAuthPluginForm.getPluginInstanceId());
		pluginInstanceData.setAuditUId(userStatisticPostAuthPluginForm.getAuditUId());
		if (databaseList != null && userStatPostAuthPluginData != null) {
			for (int i = 0; i < databaseList.size(); i++) {
				IDatabaseDSData tempDatabaseDsData = databaseList.get(i);

				if (tempDatabaseDsData.getDatabaseId().equals(userStatisticPostAuthPluginForm.getDatabaseId())) {
					userStatPostAuthPluginData.setDataSourceName(tempDatabaseDsData.getName());
				}
			}
		}
		userStatPostAuthPluginData.setTableName(userStatisticPostAuthPluginForm.getTableName());
		userStatPostAuthPluginData.setDbQueryTimeout(userStatisticPostAuthPluginForm.getDbQueryTimeoutInMs());
		userStatPostAuthPluginData.setMaxQueryTimeoutCount(userStatisticPostAuthPluginForm.getMaxQueryTimeoutCount());
		userStatPostAuthPluginData.setBatchUpdateInterval(userStatisticPostAuthPluginForm.getBatchUpdateIntervalInMs());

		if (BaseConstant.SHOW_STATUS.equals(userStatisticPostAuthPluginForm.getStatus())) {
			pluginInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);
		} else {
			pluginInstanceData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}
	}
}
