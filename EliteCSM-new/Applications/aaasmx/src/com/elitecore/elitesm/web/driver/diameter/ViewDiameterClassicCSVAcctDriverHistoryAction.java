package com.elitecore.elitesm.web.driver.diameter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterClassicCSVAcctDriverForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.passwordutil.PasswordEncryption;

public class ViewDiameterClassicCSVAcctDriverHistoryAction extends BaseWebAction{

	private static final String FAILURE_FORWARD = "failure";
	private static final String VIEW_HISTORY_FORWARD = "viewDiameterClassicCSVAcctDriverHistory";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		try{
			UpdateDiameterClassicCSVAcctDriverForm updatDiameterClassicCSVAcctDriverForm = (UpdateDiameterClassicCSVAcctDriverForm)form;
			if("view".equals(updatDiameterClassicCSVAcctDriverForm.getAction())) {
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}

			DriverBLManager driverBlManager = new DriverBLManager();
			ClassicCSVAcctDriverData classicCsvDriverData = driverBlManager.getClassicCsvDriverByDriverInstanceId(updatDiameterClassicCSVAcctDriverForm.getDriverInstanceId());
			DriverInstanceData driverInstanceData = driverBlManager.getDriverInstanceByDriverInstanceId(updatDiameterClassicCSVAcctDriverForm.getDriverInstanceId());
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			updatDiameterClassicCSVAcctDriverForm.setAllocatingprotocol(classicCsvDriverData.getAllocatingprotocol());
			updatDiameterClassicCSVAcctDriverForm.setArchivelocation(classicCsvDriverData.getArchivelocation());
			updatDiameterClassicCSVAcctDriverForm.setAvpairseparator(classicCsvDriverData.getAvpairseparator());
			updatDiameterClassicCSVAcctDriverForm.setCreateBlankFile(classicCsvDriverData.getCreateBlankFile());
			updatDiameterClassicCSVAcctDriverForm.setDefaultdirname(classicCsvDriverData.getDefaultdirname());
			updatDiameterClassicCSVAcctDriverForm.setDelimeter(classicCsvDriverData.getDelimeter());
			updatDiameterClassicCSVAcctDriverForm.setEventdateformat(classicCsvDriverData.getEventdateformat());
			updatDiameterClassicCSVAcctDriverForm.setFailovertime(classicCsvDriverData.getFailovertime());
			updatDiameterClassicCSVAcctDriverForm.setFilename(classicCsvDriverData.getFilename());
			updatDiameterClassicCSVAcctDriverForm.setTimeBoundry(classicCsvDriverData.getTimeBoundry());
			updatDiameterClassicCSVAcctDriverForm.setSizeBasedRollingUnit(classicCsvDriverData.getSizeBasedRollingUnit());
			updatDiameterClassicCSVAcctDriverForm.setTimeBasedRollingUnit(classicCsvDriverData.getTimeBasedRollingUnit());
			updatDiameterClassicCSVAcctDriverForm.setRecordBasedRollingUnit(classicCsvDriverData.getRecordBasedRollingUnit());
			updatDiameterClassicCSVAcctDriverForm.setFoldername(classicCsvDriverData.getFoldername());
			updatDiameterClassicCSVAcctDriverForm.setGlobalization(classicCsvDriverData.getGlobalization());
			updatDiameterClassicCSVAcctDriverForm.setHeader(classicCsvDriverData.getHeader());
			updatDiameterClassicCSVAcctDriverForm.setIpaddress(classicCsvDriverData.getIpaddress());
			updatDiameterClassicCSVAcctDriverForm.setLocation(classicCsvDriverData.getLocation());
			updatDiameterClassicCSVAcctDriverForm.setMultivaluedelimeter(classicCsvDriverData.getMultivaluedelimeter());
			updatDiameterClassicCSVAcctDriverForm.setPattern(classicCsvDriverData.getPattern());
			updatDiameterClassicCSVAcctDriverForm.setPostoperation(classicCsvDriverData.getPostoperation());
			updatDiameterClassicCSVAcctDriverForm.setPrefixfilename(classicCsvDriverData.getPrefixfilename());
			updatDiameterClassicCSVAcctDriverForm.setCdrtimestampFormat(classicCsvDriverData.getCdrtimestampFormat());
			updatDiameterClassicCSVAcctDriverForm.setRange(classicCsvDriverData.getRange());
			updatDiameterClassicCSVAcctDriverForm.setRemotelocation(classicCsvDriverData.getRemotelocation());
			updatDiameterClassicCSVAcctDriverForm.setUsedictionaryvalue(classicCsvDriverData.getUsedictionaryvalue());
			updatDiameterClassicCSVAcctDriverForm.setUsername(classicCsvDriverData.getUsername());
			updatDiameterClassicCSVAcctDriverForm.setPassword(classicCsvDriverData.getPassword());
			updatDiameterClassicCSVAcctDriverForm.setEnclosingCharacter(classicCsvDriverData.getEnclosingCharacter());
			//updateClassicCsvDriverForm.setWriteattributes(classicCsvDriverData.getWriteattributes());

			updatDiameterClassicCSVAcctDriverForm.setDriverInstanceName(driverInstanceData.getName());
			updatDiameterClassicCSVAcctDriverForm.setDriverInstanceDesp(driverInstanceData.getDescription());
			updatDiameterClassicCSVAcctDriverForm.setDriverRelatedId(String.valueOf(driverInstanceData.getDriverInstanceId()));
			updatDiameterClassicCSVAcctDriverForm.setAuditUId(driverInstanceData.getAuditUId());	
			
			/* Decrypt server password */
			String decryptedPassword = PasswordEncryption.getInstance().decrypt(updatDiameterClassicCSVAcctDriverForm.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			updatDiameterClassicCSVAcctDriverForm.setPassword(decryptedPassword);
				
			HistoryBLManager historyBlManager= new HistoryBLManager();
			String strDriverInstanceId = request.getParameter("driverInstanceId");
			String driverInstanceId = strDriverInstanceId;
			if(driverInstanceId == null){
				driverInstanceId = updatDiameterClassicCSVAcctDriverForm.getDriverInstanceId();
			}

			String strAuditUid = request.getParameter("auditUid");
			String strSytemAuditId=request.getParameter("systemAuditId");
			String name=request.getParameter("name");
				
			if(strSytemAuditId != null){
				request.setAttribute("systemAuditId", strSytemAuditId);
			}
				
			if(driverInstanceId != null && Strings.isNullOrBlank(strAuditUid) == false){
					
				staffData.setAuditName(driverInstanceData.getName());
				staffData.setAuditId(driverInstanceData.getAuditUId());
				
				List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
					
				request.setAttribute("name", name);
				request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
			}
				
			request.getSession().setAttribute("driverInstance",driverInstanceData);
			request.getSession().setAttribute("classicCsvAttrRelSet", classicCsvDriverData.getCsvAttrRelList());
			request.getSession().setAttribute("classicCsvPattRelSet", classicCsvDriverData.getCsvPattRelList());
			
			return mapping.findForward(VIEW_HISTORY_FORWARD);

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException e){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}catch(Exception dme){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}		
	}
}