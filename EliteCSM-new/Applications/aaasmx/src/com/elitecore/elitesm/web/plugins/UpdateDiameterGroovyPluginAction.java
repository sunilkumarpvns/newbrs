package com.elitecore.elitesm.web.plugins;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginFile;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.plugins.forms.UpdateDiameterGroovyPluginForm;
import com.google.gson.Gson;

/**
 * @author nayana.rathod
 *
 */
public class UpdateDiameterGroovyPluginAction extends BaseWebAction{

	private static final String OPEN_FORWARD = "diameterGroovyPlugin";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_PLUGIN;
	private static final String MODULE = UpdateDiameterGroovyPluginAction.class.getSimpleName();
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_PLUGIN;
	private static final String UPDATE_GROOVY_FILE = "updateGroovyFile";
	private static final String VIEW_GROOVY_FILE = "viewGroovyFile";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		String pluginBindedWith = new String();
		try{
			UpdateDiameterGroovyPluginForm updateDiameterGroovyPluginForm = (UpdateDiameterGroovyPluginForm)form;
			
			if("update".equals(updateDiameterGroovyPluginForm.getAction())) {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			} else {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			}
			
			PluginBLManager pluginBLManager = new PluginBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			PluginInstData pluginInstanceData = pluginBLManager.getListOfPluginInstanceByPluginInstanceId(updateDiameterGroovyPluginForm.getPluginInstanceId());
			GroovyPluginData groovyPluginData = pluginBLManager.getGroovyPluginDataByPluginInstanceId(updateDiameterGroovyPluginForm.getPluginInstanceId());
			
			if(groovyPluginData!=null){
				updateDiameterGroovyPluginForm.setPluginId(groovyPluginData.getPluginId());
			}
			
			request.getSession().setAttribute("pluginInstanceData", pluginInstanceData);

			if(updateDiameterGroovyPluginForm.getAction() != null){
				
				if(updateDiameterGroovyPluginForm.getAction().equals("update")){
	
					List<FileDetails> uploadedFileList=new ArrayList<FileDetails>();
					
					if( updateDiameterGroovyPluginForm.getGroovyDatas() != null && updateDiameterGroovyPluginForm.getGroovyDatas().length() > 0){
							 
						JSONArray uploadFileObject = JSONArray.fromObject( updateDiameterGroovyPluginForm.getGroovyDatas());
						 for(Object  obj: uploadFileObject){
							 FileDetails authenticationHandler = (FileDetails) JSONObject.toBean((JSONObject) obj, FileDetails.class);
							 uploadedFileList.add(authenticationHandler);
						 }
					}	
					
					Set<GroovyPluginFile> groovyPluginFileSet = new LinkedHashSet<GroovyPluginFile>();
					if( uploadedFileList!= null && uploadedFileList.size() > 0){
							 
						for( FileDetails fileDetails : uploadedFileList ){
							GroovyPluginFile groovyPluginFile=new GroovyPluginFile();
							groovyPluginFile.setGroovyFile(fileDetails.getFileContent().getBytes());
							
							if( fileDetails.getLastModified() != null && fileDetails.getLastModified().length() > 0)
								groovyPluginFile.setLastUpdatedTime(new Timestamp(Long.parseLong(fileDetails.getLastModified())));
						
							groovyPluginFile.setGroovyFileName(fileDetails.getFileName());
							groovyPluginFileSet.add(groovyPluginFile);
						}
					}
					
					GroovyPluginData groovyPluginDataObject = new GroovyPluginData();
					groovyPluginDataObject.setGroovyPluginFileSet(groovyPluginFileSet);
					groovyPluginDataObject.setPluginId(groovyPluginData.getPluginId());
					groovyPluginDataObject.setPluginInstanceId(groovyPluginData.getPluginInstanceId());
						
					Set<GroovyPluginData> groovyPluginDataSet = new LinkedHashSet<GroovyPluginData>();
					groovyPluginDataSet.add(groovyPluginDataObject);
						
					pluginInstanceData.setGroovyPluginDataSet(groovyPluginDataSet);
						
					convertFromFormToData(updateDiameterGroovyPluginForm,pluginInstanceData, groovyPluginData);
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
						pluginBLManager.update(pluginInstanceData, groovyPluginDataObject, staffData, ConfigConstant.UPDATE_PLUGIN);
							
						request.setAttribute("responseUrl", "/viewPluginInstance.do?pluginInstanceId=" + updateDiameterGroovyPluginForm.getPluginInstanceId());
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
				}else if(updateDiameterGroovyPluginForm.getAction().equals("readgroovyfile")){
					
					convertFromFormToData(updateDiameterGroovyPluginForm,pluginInstanceData, groovyPluginData);
					Map<String,GroovyDetails> groovyDetailsMap = new LinkedHashMap<String, GroovyDetails>();
					
					if( updateDiameterGroovyPluginForm.getGroovyFileName() != null ){
						
						if( groovyPluginData.getGroovyPluginFileSet() != null && groovyPluginData.getGroovyPluginFileSet().isEmpty() == false ){
						
							for( GroovyPluginFile groovyPluginFile : groovyPluginData.getGroovyPluginFileSet() ){
								
								if( updateDiameterGroovyPluginForm.getGroovyFileName().equalsIgnoreCase(groovyPluginFile.getGroovyFileName())){
									
									GroovyDetails groovyDetails = new GroovyDetails();
									groovyDetails.setGroovyFileName(groovyPluginFile.getGroovyFileName());
									groovyDetails.setLastModifiedDate(groovyPluginFile.getLastUpdatedTime());
									groovyDetails.setDate(groovyPluginFile.getLastUpdatedTime().getTime());
									groovyDetails.setGroovyFileId(groovyPluginFile.getGroovyFileId());
									groovyDetails.setGroovyFileInstanceId(groovyPluginData.getPluginId());
									
									byte[] byteData = groovyPluginFile.getGroovyFile();
									
									Gson gson = new Gson();
									String jsonString = gson.toJson( new String(byteData));
									
									jsonString = jsonString.substring(1, jsonString.length() - 1);

									Logger.getLogger().info(MODULE, "Reading file is : " + groovyPluginFile.getGroovyFileName());
									Logger.getLogger().info(MODULE, "File details is : " + jsonString);
									
									groovyDetails.setGroovyData(jsonString);
									
									groovyDetails.setGroovyFileData(byteData);
									groovyDetailsMap.put(groovyPluginFile.getGroovyFileName(), groovyDetails);
									updateDiameterGroovyPluginForm.setGroovyDetailsMap(groovyDetailsMap);
								}
							}
						}
					}
					request.getSession().setAttribute("updateDiameterGroovyPluginForm", updateDiameterGroovyPluginForm);
					return mapping.findForward(VIEW_GROOVY_FILE);
				}else if(updateDiameterGroovyPluginForm.getAction().equals("viewgroovyfile")){
						
					convertFromFormToData(updateDiameterGroovyPluginForm,pluginInstanceData, groovyPluginData);
					Map<String,GroovyDetails> groovyDetailsMap = new LinkedHashMap<String, GroovyDetails>();
						
					if( updateDiameterGroovyPluginForm.getGroovyFileName() != null ){
							
							if( groovyPluginData.getGroovyPluginFileSet() != null && groovyPluginData.getGroovyPluginFileSet().isEmpty() == false ){
							
								for( GroovyPluginFile groovyPluginFile : groovyPluginData.getGroovyPluginFileSet() ){
									
									if( updateDiameterGroovyPluginForm.getGroovyFileName().equalsIgnoreCase(groovyPluginFile.getGroovyFileName())){
										
										GroovyDetails groovyDetails = new GroovyDetails();
										groovyDetails.setGroovyFileName(groovyPluginFile.getGroovyFileName());
										groovyDetails.setLastModifiedDate(groovyPluginFile.getLastUpdatedTime());
										groovyDetails.setDate(groovyPluginFile.getLastUpdatedTime().getTime());
										groovyDetails.setGroovyFileId(groovyPluginFile.getGroovyFileId());
										groovyDetails.setGroovyFileInstanceId(groovyPluginData.getPluginId());
										
										byte[] byteData = groovyPluginFile.getGroovyFile();
										
										Gson gson = new Gson();
										String jsonString = gson.toJson( new String(byteData));
										
										jsonString = jsonString.substring(1, jsonString.length() - 1);

										Logger.getLogger().info(MODULE, "Reading file is : " + groovyPluginFile.getGroovyFileName());
										Logger.getLogger().info(MODULE, "File details is : " + jsonString);
										
										groovyDetails.setGroovyData(jsonString);
										
										groovyDetails.setGroovyFileData(byteData);
										groovyDetailsMap.put(groovyPluginFile.getGroovyFileName(), groovyDetails);
										updateDiameterGroovyPluginForm.setGroovyDetailsMap(groovyDetailsMap);
									}
								}
							}
						}
						request.getSession().setAttribute("updateDiameterGroovyPluginForm", updateDiameterGroovyPluginForm);
						return mapping.findForward(UPDATE_GROOVY_FILE);
						
				}else if(updateDiameterGroovyPluginForm.getAction().equals("updategroovyfile")){
					
					GroovyPluginFile groovyPluginFileObj = pluginBLManager.getGroovyPluginFileByName(updateDiameterGroovyPluginForm.getGroovyFileName(), updateDiameterGroovyPluginForm.getPluginId()); 
					
					convertFromFormToData(updateDiameterGroovyPluginForm,pluginInstanceData, groovyPluginData);
					
					List<FileDetails> uploadedFileList=new ArrayList<FileDetails>();
					
					if( updateDiameterGroovyPluginForm.getGroovyDatas() != null && updateDiameterGroovyPluginForm.getGroovyDatas().length() > 0){
							 
						JSONArray uploadFileObject = JSONArray.fromObject( updateDiameterGroovyPluginForm.getGroovyDatas());
						 for(Object  obj: uploadFileObject){
							 FileDetails authenticationHandler = (FileDetails) JSONObject.toBean((JSONObject) obj, FileDetails.class);
							 uploadedFileList.add(authenticationHandler);
						 }
					}	
					
					//Retrive File based on groovy file name
					
					Set<GroovyPluginFile> groovyPluginFileSet = new LinkedHashSet<GroovyPluginFile>();
					if( uploadedFileList!= null && uploadedFileList.size() > 0){
							 
						for( FileDetails fileDetails : uploadedFileList ){
							GroovyPluginFile groovyPluginFile=new GroovyPluginFile();
							groovyPluginFile.setGroovyFile(fileDetails.getFileContent().getBytes());
							
							groovyPluginFile.setLastUpdatedTime(new Timestamp(new Date().getTime()));
						
							groovyPluginFile.setGroovyFileName(fileDetails.getFileName());
							groovyPluginFile.setPluginId(pluginInstanceData.getPluginInstanceId());
							groovyPluginFile.setGroovyFileId(groovyPluginFileObj.getGroovyFileId());
							groovyPluginFileSet.add(groovyPluginFile);
						}
					}
					
					GroovyPluginFile groovyPluginFileNewObject = new GroovyPluginFile();
					for( GroovyPluginFile groovyPluginFile :groovyPluginFileSet){
						groovyPluginFileNewObject = groovyPluginFile;
					}
					
					try{
						
						pluginBLManager.updateGroovyFile(groovyPluginFileNewObject,staffData,pluginInstanceData);
							
						request.setAttribute("responseUrl", "/updateDiameterGroovyPlugin.do?pluginInstanceId=" + updateDiameterGroovyPluginForm.getPluginInstanceId());
						ActionMessage message = new ActionMessage("groovy.update.success");
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
			updateDiameterGroovyPluginForm.setPluginName(pluginInstanceData.getName());
			updateDiameterGroovyPluginForm.setDescription(pluginInstanceData.getDescription());
			updateDiameterGroovyPluginForm.setAuditUId(pluginInstanceData.getAuditUId());
			
			if(BaseConstant.HIDE_STATUS_ID.equals(pluginInstanceData.getStatus())){
				updateDiameterGroovyPluginForm.setStatus(BaseConstant.HIDE_STATUS);
			}else{
				updateDiameterGroovyPluginForm.setStatus(BaseConstant.SHOW_STATUS);
			}
			
			/* Setting Universal Auth Plugin Data */
			updateDiameterGroovyPluginForm.setPluginInstanceId(groovyPluginData.getPluginInstanceId());
			updateDiameterGroovyPluginForm.setPluginId(groovyPluginData.getPluginId());
			updateDiameterGroovyPluginForm.setGroovyPluginFileSet(groovyPluginData.getGroovyPluginFileSet());
			
			Map<String,GroovyDetails> groovyDetailsMap = new LinkedHashMap<String, GroovyDetails>();
			
			if( groovyPluginData.getGroovyPluginFileSet() != null && groovyPluginData.getGroovyPluginFileSet().isEmpty() == false ){
				for( GroovyPluginFile groovyPluginFile : groovyPluginData.getGroovyPluginFileSet() ){
					
					GroovyDetails groovyDetails = new GroovyDetails();
					groovyDetails.setGroovyFileName(groovyPluginFile.getGroovyFileName());
					groovyDetails.setLastModifiedDate(groovyPluginFile.getLastUpdatedTime());
					groovyDetails.setDate(groovyPluginFile.getLastUpdatedTime().getTime());
					groovyDetails.setGroovyFileId(groovyPluginFile.getGroovyFileId());
					groovyDetails.setGroovyFileInstanceId(groovyPluginData.getPluginId());
					
					byte[] byteData = groovyPluginFile.getGroovyFile();
					
					Gson gson = new Gson();
					String jsonString = gson.toJson( new String(byteData));
					
					jsonString = jsonString.substring(1, jsonString.length()-1);
					
					Logger.getLogger().info(MODULE, "Reading file is : " + groovyPluginFile.getGroovyFileName());
					Logger.getLogger().info(MODULE, "File details is : " + jsonString);
					
					groovyDetails.setGroovyData(jsonString);
					
					groovyDetails.setGroovyFileData(byteData);
					groovyDetailsMap.put(groovyPluginFile.getGroovyFileName(), groovyDetails);
				}
			}
				
			updateDiameterGroovyPluginForm.setGroovyDetailsMap(groovyDetailsMap);
			request.getSession().setAttribute("updateDiameterGroovyPluginForm", updateDiameterGroovyPluginForm);
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

	private void convertFromFormToData( UpdateDiameterGroovyPluginForm updateDiameterGroovyPluginForm, PluginInstData pluginInstanceData,GroovyPluginData groovyPluginData) {
		pluginInstanceData.setName(updateDiameterGroovyPluginForm.getPluginName());
		pluginInstanceData.setDescription(updateDiameterGroovyPluginForm.getDescription());
		pluginInstanceData.setPluginInstanceId(updateDiameterGroovyPluginForm.getPluginInstanceId());
		pluginInstanceData.setAuditUId(updateDiameterGroovyPluginForm.getAuditUId());
	
		if(BaseConstant.SHOW_STATUS.equals(updateDiameterGroovyPluginForm.getStatus())){
			pluginInstanceData.setStatus(BaseConstant.SHOW_STATUS_ID);
			groovyPluginData.setPluginStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			pluginInstanceData.setStatus(BaseConstant.HIDE_STATUS_ID);
			groovyPluginData.setPluginStatus(BaseConstant.HIDE_STATUS_ID);
		}
		groovyPluginData.setPluginName(updateDiameterGroovyPluginForm.getPluginName());
		groovyPluginData.setPluginDescription(updateDiameterGroovyPluginForm.getDescription());
	}
}
