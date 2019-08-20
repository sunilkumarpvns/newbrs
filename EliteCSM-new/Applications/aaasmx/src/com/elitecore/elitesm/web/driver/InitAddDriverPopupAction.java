package com.elitecore.elitesm.web.driver;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.driver.forms.AddDriverPopupForm;

public class InitAddDriverPopupAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "addDriverPopup";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "InitAddDriverPopupAction";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		try{
			AddDriverPopupForm addDriverPopupForm = (AddDriverPopupForm)form;
			DriverBLManager driveBLManager = new DriverBLManager();
			String strServiceTypeId = request.getParameter("serviceTypeId");
			String strWeightageSelection = request.getParameter("weightageSelection");
			List<DriverInstanceData> driverInstanceDataList = new ArrayList<DriverInstanceData>(1); 
			if(strServiceTypeId!=null){
				long  serviceTypeId = Long.parseLong(strServiceTypeId);
				Logger.logDebug(MODULE, " Service Type Id  :"+serviceTypeId);
				driverInstanceDataList = driveBLManager.getDriverInstanceList(serviceTypeId);
				if(driverInstanceDataList!=null){
					Logger.logDebug(MODULE, " Driver Instance Size  :"+driverInstanceDataList.size());
				}
				addDriverPopupForm.setDriverInstanceDataList(driverInstanceDataList);
				if(strWeightageSelection!=null && strWeightageSelection.length()>0){
					addDriverPopupForm.setWeightageSelection(new Boolean(strWeightageSelection));
				}
			}
			request.setAttribute("driverInstanceDataList",driverInstanceDataList);
			request.setAttribute("addDriverPopupForm",addDriverPopupForm);
			return mapping.findForward(SUCCESS_FORWARD);
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}
