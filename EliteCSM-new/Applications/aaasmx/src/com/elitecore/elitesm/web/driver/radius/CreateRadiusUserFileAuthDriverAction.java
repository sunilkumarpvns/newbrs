package com.elitecore.elitesm.web.driver.radius;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data.UserFileAuthDriverData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;
import com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusUserFileAuthDriverForm;

public class CreateRadiusUserFileAuthDriverAction extends BaseWebAction{



	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = CreateRadiusUserFileAuthDriverAction.class.getSimpleName();
	private static final String CREATE = "cRadiusUserFileAuthDriver";


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{

			checkActionPermission(request, ACTION_ALIAS);
			CreateRadiusUserFileAuthDriverForm driverForm = (CreateRadiusUserFileAuthDriverForm)form;
			DriverBLManager driverBlManager = new DriverBLManager();
			UserFileAuthDriverData userfileDriverData = new UserFileAuthDriverData();
			DriverInstanceData driverInstance = new DriverInstanceData();
			CreateDriverConfig driverConfig = new CreateDriverConfig();
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			if(driverForm.getAction() != null){
				if(driverForm.getAction().equals("create")){

					try{
						convertFromFormToData(driverForm,userfileDriverData,driverInstance);

						driverInstance.setCreatedByStaffId(currentUser);        	
						driverInstance.setLastModifiedDate(getCurrentTimeStemp());
						driverInstance.setLastModifiedByStaffId(currentUser);

						driverConfig.setUserFileAuthDriverData(userfileDriverData);
						driverConfig.setDriverInstanceData(driverInstance);
						driverBlManager.createUserFileAuthDriver(driverConfig,staffData);
						request.setAttribute("responseUrl", "/initSearchDriver");
						ActionMessage message = new ActionMessage("driver.create.success");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveMessages(request, messages);
						return mapping.findForward(SUCCESS_FORWARD);

					}catch(DuplicateParameterFoundExcpetion dpf){
						Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
						Logger.logTrace(MODULE,dpf);
						Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpf);
						request.setAttribute("errorDetails", errorElements);
						ActionMessage message = new ActionMessage("driver.create.duplicate.failure",driverInstance.getName());
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);
					}catch(Exception e){

						Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
						Logger.logTrace(MODULE,e);
						Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
						request.setAttribute("errorDetails", errorElements);
						ActionMessage message = new ActionMessage("driver.create.failure");
						ActionMessages messages = new ActionMessages();
						messages.add("information",message);
						saveErrors(request,messages);
						return mapping.findForward(FAILURE_FORWARD);

					}


				}else{

					//setting from request context to form values ......

					driverForm.setDriverInstanceName((String)request.getAttribute("instanceName"));
					driverForm.setDriverDesp((String)request.getAttribute("desp"));
					Long driverId =(Long)request.getAttribute("driverId");
					driverForm.setDriverRelatedId(driverId.toString());
					return mapping.findForward(CREATE);
				}
			}

			return mapping.findForward(SUCCESS_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException dme){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}catch(Exception dme){

			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dme);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dme);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("driver.create.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}


	private void convertFromFormToData(CreateRadiusUserFileAuthDriverForm driverForm,UserFileAuthDriverData userfileDriverData,DriverInstanceData driverInstance) {

		driverInstance.setName(driverForm.getDriverInstanceName());
		driverInstance.setDescription(driverForm.getDriverDesp());
		driverInstance.setDriverTypeId(Long.parseLong(driverForm.getDriverRelatedId()));		
		driverInstance.setCreateDate(getCurrentTimeStemp());
		driverInstance.setStatus("CST01");

		userfileDriverData.setFileLocations(driverForm.getFileLocations());
		userfileDriverData.setExpiryDateFormat(driverForm.getExpiryDateFormat());


	}

}
