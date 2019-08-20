package com.elitecore.netvertexsm.web.devicemgmt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.io.Closeables;
import com.elitecore.netvertexsm.blmanager.devicemgmt.DeviceMgmtBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.MessageData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.devicemgmt.data.TACDetailData;
import com.elitecore.netvertexsm.util.EliteUtility;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.exception.InvalidFileException;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.base.ICreateAction;
import com.elitecore.netvertexsm.web.core.base.IDeleteAction;
import com.elitecore.netvertexsm.web.core.base.ISearchAction;
import com.elitecore.netvertexsm.web.core.base.IUpdateAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.devicemgmt.form.DeviceManagementForm;

public class DeviceManagementAction extends BaseWebDispatchAction implements ICreateAction, IUpdateAction, ISearchAction, IDeleteAction{
	private static final String MODULE = "DEVICE-MANAGEMENT";
	
	private static final String SEARCH_PAGE = "searchDeviceMgmtPage";
	private static final String CREATE_PAGE = "createDeviceMgmtPage";
	private static final String VIEW_PAGE = "viewDeviceMgmtPage";
	private static final String EDIT_PAGE = "editDeviceMgmtPage";
	private static final String UPLOAD_PAGE = "uploadDeviceMgmtPage";
	
	
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_DEVICE_MANAGEMENT;
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_DEVICE_MANAGEMENT;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DEVICE_MANAGEMENT;
	private static final String UPLOAD_CSV_ACTION_ALIAS = ConfigConstant.UPLOAD_DEVICE_MANAGEMENT;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_DEVICE_MANAGEMENT;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DEVICE_MANAGEMENT;
	private static final String EXTENSION = ".csv";
	private static final String PREFIX_FILE_NAME = "TAC_";
	private static final String EXPORT_CSV_ACTION_ALIAS = ConfigConstant.EXPORT_DEVICE_MANAGEMENT;

	@Override
	public ActionForward initSearch(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Search Method");
		return search( mapping,  form,  request,  response);
	}

	@Override
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Search Method");
		if(checkAccess(request, SEARCH_ACTION_ALIAS)) {
			try{
				DeviceManagementForm deviceManagementForm = (DeviceManagementForm)form;
				DeviceMgmtBLManager deviceMgmtBLManager = DeviceMgmtBLManager.getInstance();
				
				TACDetailData tacDetailData = new TACDetailData();
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(deviceManagementForm.getPageNumber()).intValue();
				}
				if (requiredPageNo == 0)
					requiredPageNo = 1;		

				convertFormToBean(deviceManagementForm, tacDetailData);
				String strModel = deviceManagementForm.getModel();
	           
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = deviceMgmtBLManager.search(tacDetailData, requiredPageNo, pageSize,staffData, SEARCH_ACTION_ALIAS);
				
				deviceManagementForm.setTacDetailDataList(pageList.getListData());
				deviceManagementForm.setPageNumber(pageList.getCurrentPage());
				deviceManagementForm.setTotalPages(pageList.getTotalPages());
				deviceManagementForm.setTotalRecords(pageList.getTotalItems());
				deviceManagementForm.setTac(deviceManagementForm.getTac());
				deviceManagementForm.setActionName(BaseConstant.LISTACTION);
				if(deviceManagementForm.getTac()!=null && deviceManagementForm.getTac().longValue()==0){
					deviceManagementForm.setTac(null);
				}
			    request.setAttribute("deviceManagementForm", deviceManagementForm);
			    
				return mapping.findForward(SEARCH_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("device.management.search.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("device.management.error.heading","searching");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            
			}
			return mapping.findForward(FAILURE);
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);

            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("device.management.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);            
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	
	@Override
	public ActionForward initCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Create Method");
		if(checkAccess(request, CREATE_ACTION_ALIAS)){
			
			return mapping.findForward(CREATE_PAGE);
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("device.management.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
		
	}

	@Override
	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Create Method");
		if((checkAccess(request, CREATE_ACTION_ALIAS))){
			DeviceManagementForm deviceManagementForm = (DeviceManagementForm)form;
			
			try{
				DeviceMgmtBLManager deviceMgmtBLManager = DeviceMgmtBLManager.getInstance();
				TACDetailData tacDetailData = new TACDetailData();
				convertFormToBean(deviceManagementForm, tacDetailData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				deviceMgmtBLManager.create(tacDetailData,staffData, CREATE_ACTION_ALIAS);
               	
               	ActionMessage message = new ActionMessage("device.management.create.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/deviceMgmt.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("device.management.create.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("device.management.error.heading","creating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("device.management.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	            	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	@Override
	public ActionForward delete(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Delete Method");
		if((checkAccess(request, DELETE_ACTION_ALIAS))){
			try{
				DeviceMgmtBLManager deviceMgmtBLManager = DeviceMgmtBLManager.getInstance();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				String[] strTacDetailIds = (String[])request.getParameterValues("tacDetailIds");
				Long[] tacDetailIds = convertStringIdsToLong(strTacDetailIds);  
				deviceMgmtBLManager.delete(tacDetailIds,staffData,DELETE_ACTION_ALIAS);

				ActionMessage message = new ActionMessage("device.management.delete.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/deviceMgmt.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("device.management.delete.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("device.management.error.heading","deleting");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            	            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("device.management.error.heading","deleting");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	@Override
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Update Method");
		if((checkAccess(request,UPDATE_ACTION_ALIAS))){
			DeviceManagementForm deviceManagementForm = (DeviceManagementForm)form;
			
			try{
				DeviceMgmtBLManager deviceMgmtBLManager = DeviceMgmtBLManager.getInstance();
				TACDetailData tacDetailData = deviceMgmtBLManager.getTACDetailData(deviceManagementForm.getTacDetailId());
				convertBeanToForm(deviceManagementForm, tacDetailData);
				request.setAttribute("tacDetailData", tacDetailData);
				request.setAttribute("deviceManagementForm", deviceManagementForm);
               	return mapping.findForward(EDIT_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("device.management.update.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("device.management.error.heading","updating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("device.management.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);		        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
	}

	@Override
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Update Method");
		if((checkAccess(request, UPDATE_ACTION_ALIAS))){
			DeviceManagementForm deviceManagementForm = (DeviceManagementForm)form;
			
			try{
				DeviceMgmtBLManager deviceMgmtBLManager = DeviceMgmtBLManager.getInstance();
				TACDetailData tacDetailData = new TACDetailData();
				convertFormToBean(deviceManagementForm, tacDetailData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				deviceMgmtBLManager.update(tacDetailData,staffData, UPDATE_ACTION_ALIAS);
               	
               	ActionMessage message = new ActionMessage("device.management.update.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/deviceMgmt.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("device.management.update.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("device.management.error.heading","updating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);		            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("device.management.error.heading","updating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);		                        	       
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in View Method");
		if((checkAccess(request, VIEW_ACTION_ALIAS))){
			DeviceManagementForm deviceManagementForm = (DeviceManagementForm)form;
			
			try{
				DeviceMgmtBLManager deviceMgmtBLManager = DeviceMgmtBLManager.getInstance();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				TACDetailData tacDetailData = deviceMgmtBLManager.getTACDetailData(deviceManagementForm.getTacDetailId(),staffData,VIEW_ACTION_ALIAS);
				convertBeanToForm(deviceManagementForm, tacDetailData);
				request.setAttribute("tacDetailData", tacDetailData);
				request.setAttribute("deviceManagementForm", deviceManagementForm);
				return mapping.findForward(VIEW_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("general.view.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("device.management.error.heading","viewing");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);			            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("device.management.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);			        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
	}
	public ActionForward uploadCSV(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in Upload CSV Method");
		if((checkAccess(request, UPLOAD_CSV_ACTION_ALIAS))){
			DeviceManagementForm deviceManagementForm = (DeviceManagementForm)form;
			File file = null;
			FileWriter fw = null;
			try{
				DeviceMgmtBLManager deviceMgmtBLManager = DeviceMgmtBLManager.getInstance();
				if(deviceManagementForm.getFormFile()!=null && deviceManagementForm.getFormFile().getFileName().toLowerCase().endsWith(".csv")){
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					List<MessageData> messageList= deviceMgmtBLManager.uploadCSV(deviceManagementForm.getFormFile(),staffData,UPLOAD_CSV_ACTION_ALIAS);
					request.setAttribute("messages", messageList);
					byte[] csvData = deviceManagementForm.getFormFile().getFileData();
					String fileName = "uploadedDeviceInfo.csv";
					file = new File(EliteUtility.getSMHome() , fileName);
					fw = new FileWriter(file);
					for(int i=0 ; i < csvData.length ; i++){
						fw.write(csvData[i]);
					}
					
					ActionMessage messageStatus = new ActionMessage("general.upload.statusreport");
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", messageStatus);
		            saveMessages(request,messages);
	               	request.setAttribute("responseUrl","/deviceMgmt.do?method=initSearch");
	               	return mapping.findForward(STATUS_REPORT);
				}else{
					throw new InvalidFileException("Invalid CSV File");
				}
			}catch(InvalidFileException e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("general.csv.invalidfile"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("device.management.upload.csv.failure");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            
	            
	            return mapping.findForward(FAILURE);
	        }catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("general.upload.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("device.management.upload.csv.failure");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	          	            
	            return mapping.findForward(FAILURE);
	        }finally{
	        	Closeables.closeQuietly(fw);
	        }
			
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("device.management.upload.csv.failure");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);	          	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	public ActionForward initUploadCSV(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(UPLOAD_PAGE);
	}

	public ActionForward exportCSV(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(Logger.getLogger().isDebugLogLevel()){
			Logger.logDebug(MODULE, "Enter exportCSV method of "
				+ getClass().getName());
		}
		if ((checkAccess(request, EXPORT_CSV_ACTION_ALIAS))) {
			try {
				DeviceMgmtBLManager deviceMgmtBLManager = DeviceMgmtBLManager.getInstance();
				List<TACDetailData> tacDetailData;
				String fileName = PREFIX_FILE_NAME
						+ EliteUtility.dateToString(new Date(),
								ConfigManager.get(ConfigConstant.DATE_FORMAT))
						+ EXTENSION;
				String[] strTacDetailIds = (String[]) request
						.getParameterValues("tacDetailIds");
				Long[] tacDetailIds = convertStringIdsToLong(strTacDetailIds);
				tacDetailData = deviceMgmtBLManager.getTacDetails(tacDetailIds);
				if (tacDetailData != null && tacDetailData.size() > 0) {
					exportData(response, tacDetailData, fileName);
					return null;
				} else {
					throw new Exception("Can not Find Device Data for Export");
				}
			} catch (Exception e) {
				Logger.logError(MODULE, "Returning error forward from "
						+ getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils
						.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage(
						"general.export.failure"));
				saveErrors(request, messages);

				ActionMessages errorHeadingMessage = new ActionMessages();
				ActionMessage message = new ActionMessage(
						"device.management.export.csv.failure");
				errorHeadingMessage.add("errorHeading", message);
				saveMessages(request, errorHeadingMessage);
				return mapping.findForward(FAILURE);
			}
		} else {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			ActionMessages errorHeadingMessage = new ActionMessages();
			message = new ActionMessage("device.management.export.csv.failure");
			errorHeadingMessage.add("errorHeading", message);
			saveMessages(request, errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}

	}

	public ActionForward exportAllCSV(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(Logger.getLogger().isDebugLogLevel()){
			Logger.logDebug(MODULE, "Enter exportAllCSV method of "
				+ getClass().getName());
		}
		if ((checkAccess(request, EXPORT_CSV_ACTION_ALIAS))) {
			try {
				DeviceMgmtBLManager deviceMgmtBLManager = DeviceMgmtBLManager.getInstance();
				String fileName = PREFIX_FILE_NAME
						+ EliteUtility.dateToString(new Date(),
								ConfigManager.get(ConfigConstant.DATE_FORMAT))
						+ EXTENSION;
				List<TACDetailData> tacDetailDatas = deviceMgmtBLManager
						.getTACDetailData();
				if (tacDetailDatas != null && tacDetailDatas.size() > 0) {
					exportData(response, tacDetailDatas, fileName);
					return null;
				} else {
					throw new Exception("Can not Find Device Data for Export");
				}

			} catch (Exception e) {
				Logger.logError(MODULE, "Returning error forward from "
						+ getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils
						.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage(
						"general.export.failure"));
				saveErrors(request, messages);

				ActionMessages errorHeadingMessage = new ActionMessages();
				ActionMessage message = new ActionMessage(
						"device.management.export.csv.failure");
				errorHeadingMessage.add("errorHeading", message);
				saveMessages(request, errorHeadingMessage);
				return mapping.findForward(FAILURE);
			}
		} else {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			ActionMessages errorHeadingMessage = new ActionMessages();
			message = new ActionMessage("device.management.export.csv.failure");
			errorHeadingMessage.add("errorHeading", message);
			saveMessages(request, errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}

	}

	private void exportData(HttpServletResponse response,
			List<TACDetailData> tacDetailDatas, String fileName)
			throws IOException {
		OutputStream out = null;
		response.setContentType("application/ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + "\"");
		out = response.getOutputStream();
		out.write(getCSVHeader().getBytes());
		for (TACDetailData tacDetailData : tacDetailDatas) {
			out.write(tacDetailData.getCSVData().getBytes());
		}
		out.flush();
		Closeables.closeQuietly(out);
	}

	private void convertBeanToForm(DeviceManagementForm form,TACDetailData data){
		form.setModel(data.getModel());
		form.setHardwareType(data.getHardwareType());
		form.setOperatingSystem(data.getOperatingSystem());
		form.setBrand(data.getBrand());
		form.setAdditionalInfo(data.getAdditionalInfo());
		if(data.getYear()!=null){
			form.setYear(data.getYear());
		}
		if(data.getTac()!=null && data.getTac().longValue() > 0){
			form.setTac(data.getTac());
		}
		if(data.getTacDetailId()!=null && data.getTacDetailId().longValue() > 0){
			form.setTacDetailId(form.getTacDetailId());
		}
	}
	
	private void convertFormToBean(DeviceManagementForm form,TACDetailData data){
	    data.setModel(form.getModel());
		data.setHardwareType(form.getHardwareType());
		data.setOperatingSystem(form.getOperatingSystem());	
		data.setBrand(form.getBrand());
		data.setAdditionalInfo(form.getAdditionalInfo());
		if(form.getYear()!=null && form.getYear().intValue() > 0){
			data.setYear(form.getYear());
		}
		if(form.getTac()!=null && form.getTac().longValue() > 0){
			data.setTac(form.getTac());
		}
		if(form.getTacDetailId()!=null && form.getTacDetailId().longValue() > 0){
			data.setTacDetailId(form.getTacDetailId());
		}
	}
	private String getCSVHeader(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.print("TAC,");
		out.print("BRAND,");
		out.print("Model,");
		out.print("HWType,");
		out.print("OS,");
		out.print("Year,");
		out.print("AdditionalInfo");
		out.println();
		
		return stringWriter.toString();
	}
	
}
