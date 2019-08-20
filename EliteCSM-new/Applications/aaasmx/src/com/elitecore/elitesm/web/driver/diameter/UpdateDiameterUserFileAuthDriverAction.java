package com.elitecore.elitesm.web.driver.diameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data.UserFileAuthDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.diameter.forms.UpdateDiameterUserFileAuthDriverForm;

public class UpdateDiameterUserFileAuthDriverAction extends BaseWebAction{

	private static final String UPDATE_FORWARD = "openUpdateDiameterUserFileAuthDriver";
	private static final String FAILURE_FORWARD = "failure";
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_DRIVER;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_DRIVER;
	private static final String MODULE=UpdateDiameterDBAuthDriverAction.class.getSimpleName();
	

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			UpdateDiameterUserFileAuthDriverForm updateUserFileDriverForm = (UpdateDiameterUserFileAuthDriverForm)form;
			if("view".equals(updateUserFileDriverForm.getAction())){
				checkActionPermission(request, VIEW_ACTION_ALIAS);
			} else {
				checkActionPermission(request, UPDATE_ACTION_ALIAS);
			}
			
			DriverBLManager driverBLManager = new DriverBLManager();

			DriverInstanceData driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(updateUserFileDriverForm.getDriverInstanceId());

			UserFileAuthDriverData userFileDriverData = driverBLManager.getUserFileDriverByDriverInstanceId(updateUserFileDriverForm.getDriverInstanceId());
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(updateUserFileDriverForm.getAction() != null){

				if(updateUserFileDriverForm.getAction().equals("Update")){

					// do update related things .....
					String driverInstanceId = updateUserFileDriverForm.getDriverInstanceId();

					UserFileAuthDriverData userFileAuthDriverData = new UserFileAuthDriverData();
					userFileAuthDriverData.setFileLocations(updateUserFileDriverForm.getFileLocations());
					userFileAuthDriverData.setExpiryDateFormat(updateUserFileDriverForm.getExpiryDateFormat());

					DriverInstanceData instanceData = new DriverInstanceData();
					instanceData.setDriverInstanceId(driverInstanceId);
					instanceData.setName(updateUserFileDriverForm.getDriverInstanceName());
					instanceData.setDescription(updateUserFileDriverForm.getDriverInstanceDesc());
					instanceData.setLastModifiedByStaffId(currentUser);
					instanceData.setLastModifiedDate(getCurrentTimeStemp());
					instanceData.setAuditUId(updateUserFileDriverForm.getAuditUId());
					
					driverBLManager.updateUserFileAuthDriverById(instanceData, userFileAuthDriverData,staffData);
					
					request.setAttribute("responseUrl", "/viewDriverInstance.do?driverInstanceId=" + updateUserFileDriverForm.getDriverInstanceId());
					ActionMessage message = new ActionMessage("driver.update.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
				}

				//// remaining
			}

			updateUserFileDriverForm.setFileLocations(userFileDriverData.getFileLocations());
			updateUserFileDriverForm.setExpiryDateFormat(userFileDriverData.getExpiryDateFormat());
			updateUserFileDriverForm.setUserFileDriverId(userFileDriverData.getUserFileDriverId());
			updateUserFileDriverForm.setDriverInstanceId(userFileDriverData.getDriverInstanceId());
			updateUserFileDriverForm.setDriverInstanceName(driverInstanceData.getName());
			updateUserFileDriverForm.setDriverInstanceDesc(driverInstanceData.getDescription());
			updateUserFileDriverForm.setAuditUId(driverInstanceData.getAuditUId());
			
			request.getSession().setAttribute("userfiledata",userFileDriverData);
			
			return mapping.findForward(UPDATE_FORWARD);
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
			ActionMessage message = new ActionMessage("driver.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}

}
