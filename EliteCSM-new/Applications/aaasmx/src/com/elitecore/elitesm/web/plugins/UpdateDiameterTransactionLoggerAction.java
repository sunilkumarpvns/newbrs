package com.elitecore.elitesm.web.plugins;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.FormatMappingData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.TransactionLoggerData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.plugins.forms.TransactionLoggerForm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author nayana.rathod
 *
 */
public class UpdateDiameterTransactionLoggerAction extends BaseWebAction{

	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_PLUGIN;
	private static final String MODULE = UpdateDiameterTransactionLoggerAction.class.getSimpleName();
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_PLUGIN;
	private static final String OPEN_FORWARD = "diameterTransactionLogger";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		String pluginBindedWith = new String();
		try{
			TransactionLoggerForm transactionLoggerForm = (TransactionLoggerForm)form;
			
			if("update".equals(transactionLoggerForm.getAction())) {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			} else {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			}
			
			PluginBLManager pluginBLManager = new PluginBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			PluginInstData pluginInstanceData = pluginBLManager.getListOfPluginInstanceByPluginInstanceId(transactionLoggerForm.getPluginInstanceId());
			TransactionLoggerData transactionLoggerData = pluginBLManager.getTransactionLoggerPluginDataByID(transactionLoggerForm.getPluginInstanceId());
			
			if(transactionLoggerData!=null){
				transactionLoggerForm.setPluginId(transactionLoggerData.getPluginId());
			}
				
			request.getSession().setAttribute("pluginInstanceData", pluginInstanceData);

			if(transactionLoggerForm.getAction() != null){
				
				if(transactionLoggerForm.getAction().equals("update")){
	
					List<FormatMapping> formatMappingList=new ArrayList<FormatMapping>();
					
					if( transactionLoggerForm.getFormatMappingsJson() != null && transactionLoggerForm.getFormatMappingsJson().length() > 0){
						JSONArray formatMappingArrayObj = JSONArray.fromObject(transactionLoggerForm.getFormatMappingsJson());
						for(Object  obj: formatMappingArrayObj){
							FormatMapping formatMapping = (FormatMapping)JSONObject.toBean((JSONObject)obj, FormatMapping.class);
							formatMappingList.add(formatMapping);
						}
					}

						
					Set<FormatMappingData> formatMappingDataSet = new LinkedHashSet<FormatMappingData>();
					if( formatMappingList!= null && formatMappingList.size() > 0){
							 
						for( FormatMapping formatMapping : formatMappingList ){
							
							FormatMappingData formatMappingData = new FormatMappingData();
							formatMappingData.setKey(formatMapping.getKey());
							formatMappingData.setFormat(formatMapping.getFormat());
							
							formatMappingDataSet.add(formatMappingData);
							
						}
					}
						
					TransactionLoggerData traLoggerData = new TransactionLoggerData();
					traLoggerData.setPluginId(transactionLoggerData.getPluginId());
					traLoggerData.setPluginInstanceId(transactionLoggerData.getPluginInstanceId());
					traLoggerData.setFormatMappingDataSet(formatMappingDataSet);
					traLoggerData.setLogFile(transactionLoggerForm.getLogFile());
					traLoggerData.setTimeBoundry(transactionLoggerForm.getTimeBoundry());
					traLoggerData.setRange(transactionLoggerForm.getRange());
					traLoggerData.setPattern(transactionLoggerForm.getPattern());
					traLoggerData.setGlobalization(transactionLoggerForm.getGlobalization());
					Set<TransactionLoggerData> transactionLoggerDataSet = new LinkedHashSet<TransactionLoggerData>();
					transactionLoggerDataSet.add(traLoggerData);
						
					pluginInstanceData.setTransactionLoggerDataSet(transactionLoggerDataSet);
						
					convertFromFormToData(transactionLoggerForm,pluginInstanceData);
					Set<String> policyNames = new LinkedHashSet<String>();
					if(pluginInstanceData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
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
					
					pluginInstanceData.setLastModifiedDate(getCurrentTimeStemp());
					pluginInstanceData.setLastModifiedByStaffId(currentUser);
						
					try{
						/* update data in DB */
						pluginBLManager.updateTransactionLoggerByID(pluginInstanceData,traLoggerData, staffData);
							
						/* doAudit */
							
						request.setAttribute("responseUrl", "/viewPluginInstance.do?pluginInstanceId=" + transactionLoggerForm.getPluginInstanceId());
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
			transactionLoggerForm.setPluginName(pluginInstanceData.getName());
			transactionLoggerForm.setDescription(pluginInstanceData.getDescription());
			transactionLoggerForm.setAuditUId(pluginInstanceData.getAuditUId());
			
			if(BaseConstant.HIDE_STATUS_ID.equals(pluginInstanceData.getStatus())){
				transactionLoggerForm.setStatus(BaseConstant.HIDE_STATUS);
			}else{
				transactionLoggerForm.setStatus(BaseConstant.SHOW_STATUS);
			}
			
			/* Setting Universal Auth Plugin Data */
			transactionLoggerForm.setPluginInstanceId(transactionLoggerData.getPluginInstanceId());
			transactionLoggerForm.setPluginId(transactionLoggerData.getPluginId());
			transactionLoggerForm.setLogFile(transactionLoggerData.getLogFile());
			transactionLoggerForm.setTimeBoundry(transactionLoggerData.getTimeBoundry());
			transactionLoggerForm.setRange(transactionLoggerData.getRange());
			transactionLoggerForm.setPattern(transactionLoggerData.getPattern());
			transactionLoggerForm.setGlobalization(transactionLoggerData.getGlobalization());
			transactionLoggerForm.setFormatMappingDataSet(transactionLoggerData.getFormatMappingDataSet());
			
			request.getSession().setAttribute("transactionLoggerForm", transactionLoggerForm);
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

	private void convertFromFormToData(TransactionLoggerForm transactionLoggerForm, PluginInstData pluginInstanceData) {
		pluginInstanceData.setName(transactionLoggerForm.getPluginName());
		pluginInstanceData.setDescription(transactionLoggerForm.getDescription());
		pluginInstanceData.setPluginInstanceId(transactionLoggerForm.getPluginInstanceId());
		pluginInstanceData.setAuditUId(transactionLoggerForm.getAuditUId());
		
		if(BaseConstant.SHOW_STATUS.equals(transactionLoggerForm.getStatus())){
			pluginInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			pluginInstanceData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}
	}
}
