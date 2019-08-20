package com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServiceTypeConstants;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form.CreatePCRFServicePolicyForm;

public class InitCreatePCRFServicePolicyAction extends BaseWebAction {
	private static final String INIT_FORWARD = "initCreatePCRFService";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_PCRF_POLICY;
	private static final String 	  MODULE = "INIT_CREATE_PCRF_POLICY";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				CreatePCRFServicePolicyForm createPcrfServicePolicyForm = (CreatePCRFServicePolicyForm) form;
				SPInterfaceBLManager driverBLManager = new SPInterfaceBLManager();
				DriverBLManager csvDriverBLManager = new DriverBLManager();
				GatewayBLManager  gatewayBLManager = new GatewayBLManager();
				
				setDefaultValues(createPcrfServicePolicyForm,request);
				
				createPcrfServicePolicyForm.setCdrDriverInstanceDataList(csvDriverBLManager.getDriverTypeList(ServiceTypeConstants.PCRF_SERVICE));
				createPcrfServicePolicyForm.setDriverInstanceList(driverBLManager.getDriverInstanceList());
				createPcrfServicePolicyForm.setGatewayList(gatewayBLManager.getGatewayList());
				createPcrfServicePolicyForm.setStatus("1");
				
				ServicePolicyBLManager blManager = new ServicePolicyBLManager();
				List<PkgData> pkgDataList=blManager.getPkgDataList();
				createPcrfServicePolicyForm.setPkgDataList(pkgDataList);
				request.setAttribute("createPCRFServiceForm",createPcrfServicePolicyForm);               			
				
				return mapping.findForward(INIT_FORWARD);
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
			}
			return mapping.findForward(FAILURE);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	private void setDefaultValues(CreatePCRFServicePolicyForm form,HttpServletRequest request){
		form.setDescription(getDefaultDescription(request));
	}	
}
