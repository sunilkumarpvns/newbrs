package com.elitecore.elitesm.web.script;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptTypeData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.history.HistoryData;
import com.elitecore.elitesm.web.plugins.FileDetails;
import com.elitecore.elitesm.web.plugins.GroovyHistroryData;
import com.elitecore.elitesm.web.script.form.ScriptInstanceForm;
import com.google.gson.Gson;

/**
 * @author Tejas.P.Shah
 *
 */
public class ScriptAction extends BaseDispatchAction {
	
	private static final String VIEW_SCRIPT = "viewscipt";
	private static final String VIEW_SCRIPT_HISTORY = "viewScriptHistory";
	private static final String ERROR_DETAILS = "errorDetails";
	private static final String INFORMATION = "information";
	private static final String RADIUS_LOGIN_FORM = "radiusLoginForm";
	private static final String SCRIPT_INSTANCE_DATA = "scriptInstanceData";
	private static final String SEARCH_FORWARD = "searchScript";	
	private static final String CREATE_FORWARD = "initCreateScript";
	private static final String UPDATE_FORWARD = "initUpdate";
	private static final String FAILURE = "failure";
	private static final String SUCCESS = "success";
	private static final String VIEW_SCRIPT_FILE = "viewScriptFile";
	private static final String UPDATE_SCRIPT_FILE = "updateScriptFile";
	private static final String UPDATE_STATUS = ConfigConstant.CHANGE_PLUGIN_STATUS;
	
	public ActionForward search(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)  {
			
		String actionMessage="script.search.failure";
		try{
			checkActionPermission(request, ConfigConstant.SEARCH_SCRIPT);
			ScriptInstanceForm searchScriptForm = (ScriptInstanceForm)form;
			String[] scriptInstanceIds = request.getParameterValues("select");
			List<String> scriptInstanceNamesList = new ArrayList<String>();
			List<String> scriptInstanceNameArrayList = new  ArrayList<String>();
			
			if( scriptInstanceIds != null && scriptInstanceIds.length > 0){
				for( String scriptId : scriptInstanceIds ){
					scriptInstanceNameArrayList.add(scriptId);
				}
			}
			
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			List<ScriptInstanceData> scriptInstanceDataList = scriptBLManager.getListOfScriptInstData(scriptInstanceNameArrayList);
			
			if( Collectionz.isNullOrEmpty(scriptInstanceDataList) == false){
				for( ScriptInstanceData scriptInstData : scriptInstanceDataList){
					scriptInstanceNamesList.add(scriptInstData.getName());
				}
			}
			
			/* requiredPageNo */
			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = (int) searchScriptForm.getPageNumber();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			
			if(request.getParameter("resultStatus")!= null){
				searchScriptForm.setStatus(request.getParameter("resultStatus"));
			}	
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute(RADIUS_LOGIN_FORM)));
			
			if(searchScriptForm.getAction() != null ){
				
				
				if(searchScriptForm.getAction().equals("delete")){
					actionMessage="script.delete.failure";
					checkActionPermission(request, ConfigConstant.DELETE_SCRIPT);

					scriptBLManager.deleteScriptById(Arrays.asList(scriptInstanceIds),staffData);
					  
					searchScriptForm.setAction("list");
					request.setAttribute("responseUrl", "/script.do?method=search");
					ActionMessage message = new ActionMessage("script.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS);
					
				}else if(searchScriptForm.getAction().equalsIgnoreCase("show")){
					actionMessage = "script.statuschange.failure";
					String actionAlias = UPDATE_STATUS;
					scriptBLManager.updateScriptStatus( Arrays.asList(scriptInstanceIds), BaseConstant.SHOW_STATUS_ID);
					doAuditing(staffData, actionAlias);
				}else if(searchScriptForm.getAction().equalsIgnoreCase("hide")){
					actionMessage = "script.statuschange.failure";
					checkActionPermission(request, UPDATE_STATUS);
					String actionAlias = UPDATE_STATUS;
					
					scriptBLManager.updateScriptStatus( Arrays.asList(scriptInstanceIds), BaseConstant.HIDE_STATUS_ID);
					doAuditing(staffData, actionAlias);
				}
			}
			
			ScriptInstanceData scriptInstanceData = new ScriptInstanceData();
			scriptInstanceData.setStatus("All");
			
			/* Script Status */
			if(searchScriptForm.getStatus() != null){
				if(searchScriptForm.getStatus().equals("Active")){				
					scriptInstanceData.setStatus("CST01");

				}else if(searchScriptForm.getStatus().equals("Inactive")){
					scriptInstanceData.setStatus("CST02");
				}
			}
			
			/* code of basis of action remaining*/
			
			/*name*/
			String strScriptName =request.getParameter("name");
			if(strScriptName != null){
				scriptInstanceData.setName(strScriptName);
			}else if(searchScriptForm.getScriptName() != null){
				scriptInstanceData.setName(searchScriptForm.getScriptName());
			}else{
				scriptInstanceData.setName("");
			}
			
			/*script type*/
			
			String strscriptType =request.getParameter("scriptType");
			if(strscriptType != null){
				scriptInstanceData.setScriptTypeId(strscriptType);
			}else if(searchScriptForm.getSelectedScript() != null){
				scriptInstanceData.setScriptTypeId(searchScriptForm.getSelectedScript());
			}else{
				scriptInstanceData.setScriptTypeId("");
			}
			
			PageList pageList = scriptBLManager.search(scriptInstanceData,requiredPageNo,pageSize,staffData);
			
			searchScriptForm.setPageNumber(pageList.getCurrentPage());
			searchScriptForm.setTotalPages(pageList.getTotalPages());
			searchScriptForm.setTotalRecords(pageList.getTotalItems());
			searchScriptForm.setScriptInstanceDataList(pageList.getListData());
			searchScriptForm.setScriptName(scriptInstanceData.getName());
			searchScriptForm.setSelectedScript(String.valueOf(scriptInstanceData.getScriptTypeId()));
			
			List<ScriptTypeData> serviceList = scriptBLManager.getListOfAllScriptTypesData();		
			searchScriptForm.setAction("list");
			searchScriptForm.setScriptTypeList(serviceList);
			request.getSession().setAttribute("searchScriptForm",searchScriptForm);
			
		} catch(ActionNotPermitedException e){
			
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
			
		}catch (ConstraintViolationException e) {
			
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message=null;
			
			ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE);
			
		} catch (DataManagerException e) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
            Logger.logTrace(MODULE,e);
			Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessage message = new ActionMessage(actionMessage);
            ActionMessages messages = new ActionMessages();
            messages.add("information",message);
            saveErrors(request,messages);
            return mapping.findForward(FAILURE);
			
		} 
		return mapping.findForward(SEARCH_FORWARD);
	}
	
	public ActionForward initCreate(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		ScriptInstanceForm createScriptForm = (ScriptInstanceForm)form;
		ScriptBLManager scriptBLManager = new ScriptBLManager();
		
		String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
		List<ScriptTypeData> serviceList = scriptBLManager.getListOfAllScriptTypesData();	
		createScriptForm.setScriptTypeList(serviceList);
		createScriptForm.setScriptType(request.getParameter("selectedScript"));
		createScriptForm.setDescription(getDefaultDescription(userName));
		request.getSession().setAttribute("createScriptForm",createScriptForm);
		
		return mapping.findForward(CREATE_FORWARD);
	}
	
	public ActionForward create(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)  {
		ScriptInstanceData scriptInstData = new ScriptInstanceData();
		try{
			checkActionPermission(request, ConfigConstant.CREATE_SCRIPT);
			ScriptInstanceForm scriptInstanceForm = (ScriptInstanceForm)form;
			
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute(RADIUS_LOGIN_FORM)).getUserId();
		
			ScriptBLManager scriptBLManager = new ScriptBLManager();
				
			if("create".equals(scriptInstanceForm.getAction())){
		
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute(RADIUS_LOGIN_FORM)));
				
				List<FileDetails> uploadedFileList=new ArrayList<FileDetails>();
					
				if(Strings.isNullOrBlank(scriptInstanceForm.getGroovyDatas()) == false){
						 
					JSONArray uploadFileObject = JSONArray.fromObject( scriptInstanceForm.getGroovyDatas());
					 for(Object  obj: uploadFileObject){
						 FileDetails fileUploader = (FileDetails) JSONObject.toBean((JSONObject) obj, FileDetails.class);
						 uploadedFileList.add(fileUploader);
					 }
				}
				
				List<ScriptData> scriptDataList = new ArrayList<ScriptData>();
				ScriptTypeData scriptTypeData = scriptBLManager.getScriptTypeDataById(scriptInstanceForm.getSelectedScript());
				
				if( Collectionz.isNullOrEmpty(uploadedFileList) == false){
						 
					for( FileDetails fileDetails : uploadedFileList ){
						ScriptData scriptData=new ScriptData();
						scriptData.setScriptFile(fileDetails.getFileContent().getBytes());
						scriptData.setScriptFileName(fileDetails.getFileName());
						scriptData.setLastUpdatedTime(new Timestamp(Long.parseLong(fileDetails.getLastModified())));
						scriptDataList.add(scriptData);
						
					}
				}
				scriptInstData.setScriptTypeData(scriptTypeData);
				scriptInstData.setScriptDataList(scriptDataList);
				/* Setting Data to Groovy script */
				
				convertFromFormToData(scriptInstanceForm,scriptInstData);

				scriptInstData.setCreatedByStaffId(currentUser);        	
				scriptInstData.setLastModifiedDate(getCurrentTimeStemp());
				scriptInstData.setLastModifiedByStaffId(currentUser);

				/*Create script Code*/
				scriptBLManager.createScriptInstance(scriptInstData, staffData);
				
				Logger.getLogger().info(MODULE, "script [" + scriptInstData.getName() + "] Created Successfully");
					
				request.setAttribute("responseUrl", "/script.do?method=search");
				ActionMessage message = new ActionMessage("script.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add(INFORMATION, message);
				saveMessages(request, messages);
					
				return mapping.findForward(SUCCESS);
		}
	}catch(DuplicateParameterFoundExcpetion dpf){
		Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
		Logger.logTrace(MODULE,dpf);
		Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
		request.setAttribute(ERROR_DETAILS, errorElements);
		ActionMessage message = new ActionMessage("script.create.duplicate.failure",scriptInstData.getName());
		ActionMessages messages = new ActionMessages();
		messages.add(INFORMATION,message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE);
	} catch(ActionNotPermitedException e){
        Logger.logError(MODULE,"Error :-" + e.getMessage());
        printPermitedActionAlias(request);
        ActionMessages messages = new ActionMessages();
        messages.add("information", new ActionMessage("general.user.restricted"));
        saveErrors(request, messages);
        return mapping.findForward(INVALID_ACCESS_FORWARD);
	} catch(Exception e){
		Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
		Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
		request.setAttribute(ERROR_DETAILS, errorElements);
		ActionMessage message = new ActionMessage("script.create.failure");
		ActionMessages messages = new ActionMessages();
		messages.add(INFORMATION, message);
		saveErrors(request, messages);
	}
		return mapping.findForward(CREATE_FORWARD);
	}
	
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) {
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			ScriptInstanceForm scriptInstanceForm = (ScriptInstanceForm)form;			
			if("update".equals(scriptInstanceForm.getAction())) {
				checkActionPermission(request, ConfigConstant.UPDATE_SCRIPT);
			} else {
				checkActionPermission(request, ConfigConstant.VIEW_SCRIPT);
			}
			
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			ScriptInstanceData scriptInstanceData = scriptBLManager.getScriptInstanceByScriptId(scriptInstanceForm.getScriptId());
			List<ScriptData> scriptData = scriptBLManager.getScriptDataByScriptInstanceId(scriptInstanceForm.getScriptId());
			
			scriptInstanceData.setScriptDataList(scriptData);
			for(ScriptData data : scriptInstanceData.getScriptDataList()) {
				data.setDate(data.getLastUpdatedTime().getTime());
			}
			request.getSession().setAttribute("scriptData", scriptData);

			scriptInstanceForm.setScriptName(scriptInstanceData.getName());
			scriptInstanceForm.setDescription(scriptInstanceData.getDescription());
			scriptInstanceForm.setAuditUId(scriptInstanceData.getAuditUId());
			
			if(BaseConstant.HIDE_STATUS_ID.equals(scriptInstanceData.getStatus())){
				scriptInstanceForm.setStatus(BaseConstant.HIDE_STATUS);
			}else{
				scriptInstanceForm.setStatus(BaseConstant.SHOW_STATUS);
			}
			scriptInstanceForm.setScriptId(scriptInstanceData.getScriptId());
			
			List<ScriptData> scriptDataList = scriptInstanceData.getScriptDataList();
			scriptInstanceForm.setScriptDataList(scriptDataList);
			
			List<ScriptTypeData> serviceList = scriptBLManager.getListOfAllScriptTypesData();		
			scriptInstanceForm.setScriptTypeList(serviceList);
			scriptInstanceForm.setSelectedScript(scriptInstanceData.getScriptTypeId());
			
			if("readgroovyfile".equals(scriptInstanceForm.getAction())){
				request.getSession().setAttribute("scriptInstanceForm", scriptInstanceForm);
				for(ScriptData data : scriptInstanceData.getScriptDataList()) {
					
					if( scriptInstanceForm.getScriptFileName().equals(data.getScriptFileName())){
						byte[] byteData = data.getScriptFile();
						
						Gson gson = new Gson();
						String jsonString = gson.toJson( new String(byteData));
						
						jsonString = jsonString.substring(1, jsonString.length() - 1);
						
						Logger.getLogger().info(MODULE, "Reading file is : " + data.getScriptFileName());
						Logger.getLogger().info(MODULE, "File details is : " + jsonString);
						
						data.setScriptFileText(jsonString);
					}
				}
				request.getSession().setAttribute(SCRIPT_INSTANCE_DATA, scriptInstanceData);
				return mapping.findForward(VIEW_SCRIPT_FILE);
			}
			
			List<ScriptData> scriptList = new ArrayList<ScriptData>();
			for(ScriptData data : scriptInstanceData.getScriptDataList()) {
				
				byte[] byteData = data.getScriptFile();
				
				Gson gson = new Gson();
				String jsonString = gson.toJson( new String(byteData));
				
				jsonString = jsonString.substring(1, jsonString.length() - 1);
				
				Logger.getLogger().info(MODULE, "Reading file is : " + data.getScriptFileName());
				Logger.getLogger().info(MODULE, "File details is : " + jsonString);
				
				scriptInstanceForm.setScriptFileName(data.getScriptFileName());
				data.setScriptFileText(jsonString);
				scriptList.add(data);
			}
			
			scriptInstanceForm.setScriptDataList(scriptList);
			
			request.getSession().setAttribute("scriptInstanceForm", scriptInstanceForm);
			request.getSession().setAttribute(SCRIPT_INSTANCE_DATA, scriptInstanceData);
			return mapping.findForward(UPDATE_FORWARD);
		}catch(Exception e){
			Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("script.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}

	public ActionForward updateScript(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		try{
			checkActionPermission(request, ConfigConstant.UPDATE_SCRIPT);
			
			ScriptInstanceForm scriptInstanceForm = (ScriptInstanceForm)form;
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute(RADIUS_LOGIN_FORM)).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute(RADIUS_LOGIN_FORM)));
			ScriptInstanceData scriptInstanceData = scriptBLManager.getScriptInstanceByScriptId(scriptInstanceForm.getScriptId());
				
			request.getSession().setAttribute(SCRIPT_INSTANCE_DATA, scriptInstanceData);

			if(scriptInstanceForm.getAction().equals("update")){
				
				List<FileDetails> uploadedFileList=new ArrayList<FileDetails>();
				
				if( Strings.isNullOrBlank(scriptInstanceForm.getGroovyDatas()) == false){
					JSONArray uploadFileObject = JSONArray.fromObject( scriptInstanceForm.getGroovyDatas());
					 for(Object  obj: uploadFileObject){
						 FileDetails authenticationHandler = (FileDetails) JSONObject.toBean((JSONObject) obj, FileDetails.class);
						 uploadedFileList.add(authenticationHandler);
					 }
				}	
				
				List<ScriptData> scriptDataList = new ArrayList<ScriptData>();
				if( Collectionz.isNullOrEmpty(uploadedFileList) == false){
					for( FileDetails fileDetails : uploadedFileList ){
						ScriptData data=new ScriptData();
						data.setScriptFile(fileDetails.getFileContent().getBytes());
						data.setScriptFileName(fileDetails.getFileName());
						data.setLastUpdatedTime(new Timestamp(Long.parseLong(fileDetails.getLastModified())));
						scriptDataList.add(data);
					}
				}
					
					
				scriptInstanceData.setScriptDataList(scriptDataList);
					
				convertFromFormToData(scriptInstanceForm,scriptInstanceData);
				scriptInstanceData.setLastModifiedDate(getCurrentTimeStemp());
				scriptInstanceData.setLastModifiedByStaffId(currentUser);
				try{
					/* update data in DB */
					scriptBLManager.update(scriptInstanceData, staffData);
						
					request.setAttribute("responseUrl", "/script.do?method=initUpdate&scriptId=" + scriptInstanceForm.getScriptId());
					ActionMessage message = new ActionMessage("script.update.success");
					ActionMessages messages = new ActionMessages();
					messages.add(INFORMATION,message);
					saveMessages(request, messages);
						
					return mapping.findForward(SUCCESS);
				}catch(DataManagerException e){
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,e);
					Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute(ERROR_DETAILS, errorElements);
					ActionMessage message = new ActionMessage("script.update.failure");
					ActionMessages messages = new ActionMessages();
					messages.add(INFORMATION,message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);
				}
				
			} else if (isView(scriptInstanceForm) || isRead(scriptInstanceForm)) {
				
				List<ScriptData> scriptList = new ArrayList<ScriptData>();
				for(ScriptData data : scriptInstanceData.getScriptDataList()) {
					
					if( scriptInstanceForm.getScriptFileName().equals(data.getScriptFileName())){
						
						byte[] byteData = data.getScriptFile();
						
						Gson gson = new Gson();
						String jsonString = gson.toJson( new String(byteData));
						
						jsonString = jsonString.substring(1, jsonString.length() - 1);
						
						Logger.getLogger().info(MODULE, "Reading file is : " + data.getScriptFileName());
						Logger.getLogger().info(MODULE, "File details is : " + jsonString);
						
						scriptInstanceForm.setScriptFileName(data.getScriptFileName());
						data.setScriptFileText(jsonString);
						scriptList.add(data);
					}
				}
				
				scriptInstanceForm.setScriptDataList(scriptList);
				request.getSession().setAttribute(SCRIPT_INSTANCE_DATA, scriptInstanceData);
				request.getSession().setAttribute("scriptInstanceForm", scriptInstanceForm);
				if(isView(scriptInstanceForm)) {
					return mapping.findForward(UPDATE_SCRIPT_FILE); 
				}else {
					return mapping.findForward(VIEW_SCRIPT_FILE);
				}
			} else if("updategroovyfile".equals(scriptInstanceForm.getAction())) {
				ScriptData data = scriptBLManager.getScriptFileByName(scriptInstanceForm.getScriptFileName(), scriptInstanceForm.getScriptId());
				convertFromFormToData(scriptInstanceForm, scriptInstanceData);
				
				List<FileDetails> uploadedFileList=new ArrayList<FileDetails>();
				
				if(Strings.isNullOrBlank(scriptInstanceForm.getGroovyDatas())  == false){
					JSONArray uploadFileObject = JSONArray.fromObject( scriptInstanceForm.getGroovyDatas());
					 for(Object  obj: uploadFileObject){
						 FileDetails fileDetail = (FileDetails) JSONObject.toBean((JSONObject) obj, FileDetails.class);
						 uploadedFileList.add(fileDetail);
					 }
				}	
				
				List<ScriptData> scriptDataList = new ArrayList<ScriptData>();
				ScriptData scriptDataNewObject = null;
				if( Collectionz.isNullOrEmpty(uploadedFileList) == false){
					for( FileDetails fileDetails : uploadedFileList ){
						scriptDataNewObject = new ScriptData();
						
						scriptDataNewObject.setScriptFile(fileDetails.getFileContent().getBytes());
						scriptDataNewObject.setLastUpdatedTime(new Timestamp(new Date().getTime()));
						scriptDataNewObject.setScriptFileName(fileDetails.getFileName());
						scriptDataNewObject.setScriptId(scriptInstanceData.getScriptId());
						scriptDataNewObject.setScriptDataId(data.getScriptDataId());
						
						scriptDataList.add(scriptDataNewObject);
					}
				}
				
				try{
					scriptBLManager.updateScriptFile(scriptDataNewObject,staffData,scriptInstanceData);
					request.setAttribute("responseUrl", "/script.do?method=viewScript&scriptId=" + scriptInstanceForm.getScriptId());
					ActionMessage message = new ActionMessage("script.update.success");
					ActionMessages messages = new ActionMessages();
					messages.add(INFORMATION,message);
					saveMessages(request, messages);
						
					return mapping.findForward(SUCCESS);
						
				}catch(DataManagerException e){

					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE,e);
					Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute(ERROR_DETAILS, errorElements);
					ActionMessage message = new ActionMessage("script.update.failure");
					ActionMessages messages = new ActionMessages();
					messages.add(INFORMATION,message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE);

				}
				
			}
		} catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
		
		return mapping.findForward(UPDATE_FORWARD);
	}
	
	public ActionForward viewScript(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		try{
			checkActionPermission(request, ConfigConstant.VIEW_SCRIPT);
			ScriptInstanceForm scriptInstanceForm = (ScriptInstanceForm)form;
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			
			ScriptInstanceData scriptInstanceData = scriptBLManager.getScriptInstanceByScriptId(scriptInstanceForm.getScriptId());
			List<ScriptData> scriptData = scriptBLManager.getScriptDataByScriptInstanceId(scriptInstanceForm.getScriptId());
			
			if(scriptData!=null){
				scriptInstanceForm.setScriptId(scriptInstanceData.getScriptId());
			}
				
			request.getSession().setAttribute(SCRIPT_INSTANCE_DATA, scriptInstanceData);
			
			scriptInstanceForm.setScriptName(scriptInstanceData.getName());
			scriptInstanceForm.setDescription(scriptInstanceData.getDescription());
			scriptInstanceForm.setAuditUId(scriptInstanceData.getAuditUId());
			
			if(BaseConstant.HIDE_STATUS_ID.equals(scriptInstanceData.getStatus())){
				scriptInstanceForm.setStatus(BaseConstant.HIDE_STATUS);
			}else{
				scriptInstanceForm.setStatus(BaseConstant.SHOW_STATUS);
			}
			
			List<ScriptData> newScriptDataList = new ArrayList<ScriptData>();
			
			for(ScriptData data : scriptInstanceData.getScriptDataList()) {
				
					ScriptData newScriptData = new ScriptData();
					
					newScriptData.setDate(data.getLastUpdatedTime().getTime());
					newScriptData.setLastUpdatedTime(data.getLastUpdatedTime());
					newScriptData.setScriptFile(data.getScriptFile());
					newScriptData.setScriptFileName(data.getScriptFileName());
					newScriptData.setScriptDataId(data.getScriptDataId());
					newScriptData.setScriptId(data.getScriptId());
					
					byte[] byteData = data.getScriptFile();
					
					Gson gson = new Gson();
					String jsonString = gson.toJson( new String(byteData));
					
					jsonString = jsonString.substring(1, jsonString.length()-1);
					
					Logger.getLogger().info(MODULE, "Reading file is : " + data.getScriptFileName());
					Logger.getLogger().info(MODULE, "File details is : " + jsonString);
					
					newScriptData.setScriptFileText((jsonString));
					newScriptDataList.add(newScriptData);
			}
			
			scriptInstanceForm.setScriptDataList(newScriptDataList);
			request.getSession().setAttribute("scriptInstanceForm",scriptInstanceForm);
			request.getSession().setAttribute(SCRIPT_INSTANCE_DATA, scriptInstanceData);
			return mapping.findForward(VIEW_SCRIPT);
		} catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		} catch(DataManagerException e){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("script.view.failure");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION,message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE);
		} catch(Exception e){
			Object [] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("script.view.failure");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		}
	}
	
	public ActionForward viewScriptHistory(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws DataManagerException {
		try{
			checkActionPermission(request, ConfigConstant.VIEW_SCRIPT);
			ScriptInstanceForm scriptInstanceForm = (ScriptInstanceForm)form;
			String strSmInstancId = request.getParameter("scriptId");
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String scriptId;
			if(strSmInstancId!=null){
				scriptId = strSmInstancId;
			}else{
				scriptId = scriptInstanceForm.getScriptId();
			}
			
			ScriptBLManager scriptBLManager = new ScriptBLManager();
			ScriptInstanceData scriptInstanceData = scriptBLManager.getScriptInstanceByScriptId(scriptInstanceForm.getScriptId());
			
			HistoryBLManager historyBlManager= new HistoryBLManager();
			
			String strAuditUid = request.getParameter("auditUid");
			String strSytemAuditId=request.getParameter("systemAuditId");
			String name=request.getParameter("name");
			
			if(strSytemAuditId != null){
				request.setAttribute("systemAuditId", strSytemAuditId);
			}
			
			if(Strings.isNullOrBlank(strAuditUid) == false){
				
				staffData.setAuditName(scriptInstanceData.getName());
				staffData.setAuditId(scriptInstanceData.getAuditUId());
				
				List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
				
				Gson gson = new Gson();
				String jsonString = gson.toJson(lstDatabaseDSHistoryDatas);
				
				Logger.getLogger().info(MODULE,"JSON String is : ");
				Logger.getLogger().info(MODULE,jsonString);
				
				request.setAttribute("name", name);
				request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
				
				List<GroovyHistroryData> historyDataList = new ArrayList<GroovyHistroryData>();
				
				for( DatabaseHistoryData databaseHistoryData : lstDatabaseDSHistoryDatas ){
					
					GroovyHistroryData groovyHistoryData = new  GroovyHistroryData();
					groovyHistoryData.setUserName(databaseHistoryData.getUserName());
					groovyHistoryData.setIpAddress(databaseHistoryData.getIpAddress());
					groovyHistoryData.setLastUpdatedTime(databaseHistoryData.getLastupdatetime());
					
					HistoryData historyDataObj =  databaseHistoryData.getHistoryData().get(0);
					
					String historyDataObjString= gson.toJson(historyDataObj);
					
					groovyHistoryData.setHistory(historyDataObjString);
				
					historyDataList.add(groovyHistoryData);
					
				}
				
				String historyDataString= gson.toJson(historyDataList);
				request.setAttribute("historyDataString", historyDataString);
				request.setAttribute(SCRIPT_INSTANCE_DATA, scriptInstanceData);
			}
			
			return mapping.findForward(VIEW_SCRIPT_HISTORY);
			
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch(DataManagerException managerExp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());            
			Object []errorElements = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("sessionmanager.view.failure"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		}catch(Exception e){
			Logger.logError(MODULE, "Error, reason : " + e.getMessage());            
			Object []errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("sessionmanager.view.failure"));
			saveErrors(request, messages);
			return mapping.findForward(FAILURE);
		}
	}
	private void convertFromFormToData( ScriptInstanceForm scriptInstanceForm, ScriptInstanceData scriptInstData) {
		scriptInstData.setName(scriptInstanceForm.getScriptName());
		scriptInstData.setDescription(scriptInstanceForm.getDescription());
		scriptInstData.setScriptTypeId(scriptInstanceForm.getSelectedScript());		
		scriptInstData.setScriptId(scriptInstanceForm.getScriptId());
		scriptInstData.setCreateDate(getCurrentTimeStemp());
		
		if(scriptInstanceForm.getAuditUId() != null) {
			scriptInstData.setAuditUId(scriptInstanceForm.getAuditUId());
		}
		
		if(BaseConstant.SHOW_STATUS.equals(scriptInstanceForm.getStatus())){
			scriptInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			scriptInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}
		
	}

	
	private boolean isView(ScriptInstanceForm scriptInstanceForm) {
		return  "viewgroovyfile".equals(scriptInstanceForm.getAction());
	}
	
	private boolean isRead(ScriptInstanceForm scriptInstanceForm) {
		return  "readgroovyfile".equals(scriptInstanceForm.getAction()) ;
	}
}
