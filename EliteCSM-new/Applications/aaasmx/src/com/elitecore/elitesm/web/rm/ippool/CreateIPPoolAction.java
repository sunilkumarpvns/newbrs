package com.elitecore.elitesm.web.rm.ippool;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.blmanager.rm.ippool.IPPoolBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

public class CreateIPPoolAction extends IPPoolBaseAction {
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_IP_POOL_ACTION;
	private static final String INIT_CREATE_FORWARD = "initCreateIPPool";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logInfo(MODULE, "Enter the execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)) {
			try {
				IPPoolForm ipPoolForm = (IPPoolForm) form;
				if(ipPoolForm.getAction() == null) {
					ipPoolForm.setDescription(getDefaultDescription(request));
					ipPoolForm.setInputMode(IPPoolForm.NO_OF_IP_ADDRESS);
					return mapping.findForward(INIT_CREATE_FORWARD); 
				} else if("create".equalsIgnoreCase(ipPoolForm.getAction())) {
					IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
					IPPoolData ipData=new IPPoolData();
					IIPPoolData ipPoolData = convertFormToBean(ipPoolForm,ipData);
					
					/*set IP Pool Details*/
					if(isFileUpload(ipPoolForm.getFileUpload())){
						FormFile formFile = ipPoolForm.getFileUpload();
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(formFile.getInputStream()));
						ipPoolData.setIpPoolDetail(ipPoolBLManager.getIPPoolDetailByCSVFile(formFile.getFileName(),bufferedReader));
					}else{
						ipPoolData.setIpPoolDetail(ipPoolBLManager.getIPPoolDetailByIPRange(ipPoolForm.getIpAddressRanges(), ipPoolForm.getRangeId(), null, null));
					}
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					ipPoolBLManager.create(ipPoolData,staffData);
					
					request.setAttribute("responseUrl","/searchIPPool");
					ActionMessage message = new ActionMessage("ippool.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request,messages);
				}
				return mapping.findForward(SUCCESS);
			}catch (Exception e) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);
			}
		} else {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}