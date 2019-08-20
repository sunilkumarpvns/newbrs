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
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFPolicyCDRDriverRelData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicySyGatewayRelData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServiceTypeConstants;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form.EditPCRFServicePolicyForm;

public class InitEditPCRFServicePolicyAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "initEditPCRFServicePolicy";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_PCRF_POLICY;
	private static final String MODULE = "EDIT_PCRF_SERVICE_POLICY";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());

		if(checkAccess(request, ACTION_ALIAS)){
			try{
				EditPCRFServicePolicyForm editPcrfServicePolicyForm = (EditPCRFServicePolicyForm) form;
				PCRFServicePolicyData 			     pcrfPolicyData = new PCRFServicePolicyData();
				
				ServicePolicyBLManager  servicePolicyBLManager = new ServicePolicyBLManager();
				SPInterfaceBLManager 			spInterfaceBLManager = new SPInterfaceBLManager();
				
				GatewayBLManager  gatewayBLManager = new GatewayBLManager();
				DriverBLManager csvDriverBLManager = new DriverBLManager();

				long pcrfPolicyId = Long.parseLong(request.getParameter("pcrfPolicyId"));
				pcrfPolicyData.setPcrfPolicyId(pcrfPolicyId);
				pcrfPolicyData = servicePolicyBLManager.getPCRFServicePolicyData(pcrfPolicyData);

				editPcrfServicePolicyForm.setGatewayList(gatewayBLManager.getGatewayList());
				editPcrfServicePolicyForm.setPcrfPolicyId(pcrfPolicyId);
				editPcrfServicePolicyForm.setName(pcrfPolicyData.getName());
				editPcrfServicePolicyForm.setDescription(pcrfPolicyData.getDescription());
				editPcrfServicePolicyForm.setRuleset(pcrfPolicyData.getRuleset());
				editPcrfServicePolicyForm.setAction(pcrfPolicyData.getAction());
				editPcrfServicePolicyForm.setUnknownUserAction(pcrfPolicyData.getUnknownUserAction());
				editPcrfServicePolicyForm.setPkgId(pcrfPolicyData.getPkgId());
				editPcrfServicePolicyForm.setIdentityAttribute(pcrfPolicyData.getIdentityAttribute());
				editPcrfServicePolicyForm.setCdrDriverInstanceDataList(csvDriverBLManager.getDriverTypeList(ServiceTypeConstants.PCRF_SERVICE));
				editPcrfServicePolicyForm.setSyMode(pcrfPolicyData.getSyMode());
				ServicePolicyBLManager blManager = new ServicePolicyBLManager();
				List<PkgData> pkgDataList=blManager.getPkgDataList();
				editPcrfServicePolicyForm.setPkgDataList(pkgDataList);
				
				if(BaseConstant.HIDE_STATUS_ID.equals(pcrfPolicyData.getStatus())){
					editPcrfServicePolicyForm.setStatus(BaseConstant.HIDE_STATUS);
				}else{
					editPcrfServicePolicyForm.setStatus(BaseConstant.SHOW_STATUS);
				}	

				editPcrfServicePolicyForm.setSessionMgrEnabled(pcrfPolicyData.getSessionMgrEnabled());
				List<PCRFPolicyCDRDriverRelData> pcrfPolicyCDRDriverRelDataList = servicePolicyBLManager.getPCRFPolicyCDRDriverList(pcrfPolicyId);
				List<PCRFServicePolicySyGatewayRelData> pcrfServicePolicySyGatewayRelDataList = servicePolicyBLManager.getPCRFPolicySyGatewayList(pcrfPolicyId);
				List<DriverInstanceData> driverInstanceList = spInterfaceBLManager.getDriverInstanceList();
				editPcrfServicePolicyForm.setDriverInstanceList(driverInstanceList);

				request.setAttribute("pcrfPolicyData", pcrfPolicyData);
				request.setAttribute("pcrfPolicyCDRDriverRelDataList", pcrfPolicyCDRDriverRelDataList);
				request.setAttribute("pcrfServicePolicySyGatewayRelDataList", pcrfServicePolicySyGatewayRelDataList);				
				request.setAttribute("editPCRFServiceForm",editPcrfServicePolicyForm);
				return mapping.findForward(VIEW_FORWARD);

			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("servicepolicy.pcrf.init.update.failed");
				messages.add("information",message1);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}

	}
}
