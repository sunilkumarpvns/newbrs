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

import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
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
public class CreateDiameterTransactionLoggerAction extends BaseWebAction{

	private static final String ACTION_ALIAS = ConfigConstant.CREATE_PLUGIN;
	private static final String MODULE = CreateDiameterTransactionLoggerAction.class.getSimpleName();
	private static final String CREATE_FORWARD = "diameterTransactionLogger";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of :- "+getClass().getName());
		
		try{
			
			/* Check Permission of Plugin Module */
			checkActionPermission(request, ACTION_ALIAS);
			
			TransactionLoggerForm transactionLoggerForm = (TransactionLoggerForm)form;
			
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
		
			PluginBLManager pluginBLManager = new PluginBLManager();
			PluginInstData pluginInstData = new PluginInstData();
			CreatePluginConfig pluginConfig = new CreatePluginConfig();
				
			if("create".equals(transactionLoggerForm.getAction())){
			
				try{
				
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
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
						
					/* Setting Data to Groovy Plugin */
					TransactionLoggerData transactionLoggerData = new TransactionLoggerData();
					transactionLoggerData.setFormatMappingDataSet(formatMappingDataSet);
					transactionLoggerData.setLogFile(transactionLoggerForm.getLogFile());
					transactionLoggerData.setTimeBoundry(transactionLoggerForm.getTimeBoundry());
					transactionLoggerData.setRange(transactionLoggerForm.getRange());
					transactionLoggerData.setPattern(transactionLoggerForm.getPattern());
					transactionLoggerData.setGlobalization(transactionLoggerForm.getGlobalization());
					convertFromFormToData(transactionLoggerForm, pluginInstData);

					pluginInstData.setCreatedByStaffId(currentUser);        	
					pluginInstData.setLastModifiedDate(getCurrentTimeStemp());
					pluginInstData.setLastModifiedByStaffId(currentUser);

					pluginConfig.setPluginInstData(pluginInstData);
					pluginConfig.setTransactionLoggerData(transactionLoggerData);

					/*Create plugin Code*/
					pluginBLManager.createTransactionLogger(pluginConfig,staffData);
					
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
				Logger.getLogger().info(MODULE, "Enter in Else method of Create Diameter Transaction Logger method : ");
					
				if(transactionLoggerForm.getPluginType() == null || transactionLoggerForm.getPluginName() == null || transactionLoggerForm.getDescription() == null){
					transactionLoggerForm.setPluginName((String)request.getAttribute("name"));
					transactionLoggerForm.setDescription((String)request.getAttribute("description"));
					String pluginTypeId =(String)request.getAttribute("pluginTypeId");
					transactionLoggerForm.setPluginType(pluginTypeId.toString());
					transactionLoggerForm.setStatus((String)request.getAttribute("status"));	
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

	private void convertFromFormToData(TransactionLoggerForm transactionLoggerForm, PluginInstData pluginInstData) {
		pluginInstData.setName(transactionLoggerForm.getPluginName());
		pluginInstData.setDescription(transactionLoggerForm.getDescription());
		pluginInstData.setPluginTypeId(transactionLoggerForm.getPluginType());		
		pluginInstData.setCreateDate(getCurrentTimeStemp());
		if(BaseConstant.SHOW_STATUS.equals(transactionLoggerForm.getStatus())){
			pluginInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			pluginInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}	
	}

}
