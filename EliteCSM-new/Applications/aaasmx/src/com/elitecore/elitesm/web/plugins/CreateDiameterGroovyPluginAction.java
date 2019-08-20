package com.elitecore.elitesm.web.plugins;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
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
import com.elitecore.elitesm.web.plugins.forms.CreateDiameterGroovyPluginForm;

/**
 * @author nayana.rathod
 *
 */
public class CreateDiameterGroovyPluginAction extends BaseWebAction{

	private static final String ACTION_ALIAS = ConfigConstant.CREATE_PLUGIN;
	private static final String MODULE = CreateDiameterGroovyPluginAction.class.getSimpleName();
	private static final String CREATE_FORWARD = "diameterGroovyPlugin";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of :- "+getClass().getName());
		
		try{
			
			/* Check Permission of Plugin Module */
			checkActionPermission(request, ACTION_ALIAS);
			
			CreateDiameterGroovyPluginForm createDiameterGroovyPluginForm = (CreateDiameterGroovyPluginForm)form;
			
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
		
			PluginBLManager pluginBLManager = new PluginBLManager();
			PluginInstData pluginInstData = new PluginInstData();
			CreatePluginConfig pluginConfig = new CreatePluginConfig();
				
			if("create".equals(createDiameterGroovyPluginForm.getAction())){
			
				try{
				
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					
					List<FileDetails> uploadedFileList=new ArrayList<FileDetails>();
						
					if( createDiameterGroovyPluginForm.getGroovyDatas() != null && createDiameterGroovyPluginForm.getGroovyDatas().length() > 0){
							 
						JSONArray uploadFileObject = JSONArray.fromObject( createDiameterGroovyPluginForm.getGroovyDatas());
						 for(Object  obj: uploadFileObject){
							 FileDetails fileUploader = (FileDetails) JSONObject.toBean((JSONObject) obj, FileDetails.class);
							 uploadedFileList.add(fileUploader);
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
						
					/* Setting Data to Groovy Plugin */
					GroovyPluginData groovyPluginData = new GroovyPluginData();
					groovyPluginData.setGroovyPluginFileSet(groovyPluginFileSet);
					
					convertFromFormToData(createDiameterGroovyPluginForm,pluginInstData,groovyPluginData);

					pluginInstData.setCreatedByStaffId(currentUser);        	
					pluginInstData.setLastModifiedDate(getCurrentTimeStemp());
					pluginInstData.setLastModifiedByStaffId(currentUser);

					pluginConfig.setPluginInstData(pluginInstData);
					pluginConfig.setGroovyPluginData(groovyPluginData);

					/*Create plugin Code*/
					pluginBLManager.createGroovyPlugin(pluginConfig, staffData);
					
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
				Logger.getLogger().info(MODULE, "Enter in Else method of Create Diameter Groovy Plugin method : ");
					
				if(createDiameterGroovyPluginForm.getPluginType() == null || createDiameterGroovyPluginForm.getPluginName() == null || createDiameterGroovyPluginForm.getDescription() == null){
					createDiameterGroovyPluginForm.setPluginName((String)request.getAttribute("name"));
					createDiameterGroovyPluginForm.setDescription((String)request.getAttribute("description"));
					String pluginTypeId =(String)request.getAttribute("pluginTypeId");
					createDiameterGroovyPluginForm.setPluginType(pluginTypeId.toString());
					createDiameterGroovyPluginForm.setStatus((String)request.getAttribute("status"));	
						
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

	private void convertFromFormToData( CreateDiameterGroovyPluginForm createDiameterGroovyPluginForm, PluginInstData pluginInstData,GroovyPluginData groovyPluginData) {
		pluginInstData.setName(createDiameterGroovyPluginForm.getPluginName());
		pluginInstData.setDescription(createDiameterGroovyPluginForm.getDescription());
		pluginInstData.setPluginTypeId(createDiameterGroovyPluginForm.getPluginType());		
		pluginInstData.setCreateDate(getCurrentTimeStemp());
		if(BaseConstant.SHOW_STATUS.equals(createDiameterGroovyPluginForm.getStatus())){
			pluginInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
			groovyPluginData.setPluginStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			pluginInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
			groovyPluginData.setPluginStatus(BaseConstant.HIDE_STATUS_ID);
		}
		
		groovyPluginData.setPluginName(createDiameterGroovyPluginForm.getPluginName());
		groovyPluginData.setPluginDescription(createDiameterGroovyPluginForm.getDescription());
	}

}
