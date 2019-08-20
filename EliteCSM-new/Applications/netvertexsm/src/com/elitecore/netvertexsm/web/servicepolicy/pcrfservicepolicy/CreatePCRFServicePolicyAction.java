package com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFPolicyCDRDriverRelData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicySyGatewayRelData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy.form.CreatePCRFServicePolicyForm;

public class CreatePCRFServicePolicyAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_PCRF_POLICY;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());

		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			try{
				CreatePCRFServicePolicyForm createPcrfServicePolicyForm = (CreatePCRFServicePolicyForm) form;
				ServicePolicyBLManager 			 servicePolicyBLManager = new ServicePolicyBLManager();
				
				PCRFServicePolicyData pcrfPolicyData = new PCRFServicePolicyData();				
				
				List<PCRFPolicyCDRDriverRelData> 			   pcrfPolicyCDRDriverRelDataList = new  LinkedList<PCRFPolicyCDRDriverRelData>();
				List<PCRFServicePolicySyGatewayRelData> pcrfServicePolicySyGatewayRelDataList = new LinkedList<PCRFServicePolicySyGatewayRelData>();
				
				String[] strCDRDriverInstance = request.getParameterValues("cdrDriverInstance");
				String[] 		 strWeightage = request.getParameterValues("weightage");
				String[] strSyGatewayInstance = request.getParameterValues("syGatewayInstance");
				String[]   syGatewayWeightage = request.getParameterValues("syGatewayWeightage");
				
				
				if(strCDRDriverInstance != null){
					for (int i = 0; i < strCDRDriverInstance.length; i++) {
						PCRFPolicyCDRDriverRelData pcrfPolicyCDRDriverRelData = new PCRFPolicyCDRDriverRelData();
						pcrfPolicyCDRDriverRelData.setDriverInstanceId(Long.parseLong(strCDRDriverInstance[i]));
						pcrfPolicyCDRDriverRelData.setWeightage(Long.parseLong(strWeightage[i]));
						pcrfPolicyCDRDriverRelDataList.add(pcrfPolicyCDRDriverRelData);
					}
				}
				pcrfPolicyData.setPcrfPolicyCDRDriverRelDataList(pcrfPolicyCDRDriverRelDataList);
				
				if(strSyGatewayInstance!=null){
					for(int x=0; x<strSyGatewayInstance.length;	x++){
						PCRFServicePolicySyGatewayRelData  pcrfServicePolicySyGatewayRelData = new PCRFServicePolicySyGatewayRelData();
						pcrfServicePolicySyGatewayRelData.setSyGatewayId(Long.parseLong(strSyGatewayInstance[x]));
						pcrfServicePolicySyGatewayRelData.setWeightage(Long.parseLong(syGatewayWeightage[x]));
						pcrfServicePolicySyGatewayRelData.setOrderNo(x+1);
						pcrfServicePolicySyGatewayRelDataList.add(pcrfServicePolicySyGatewayRelData);
					}
				}
				pcrfPolicyData.setPcrfServicePolicySyGatewayRelDataList(pcrfServicePolicySyGatewayRelDataList);
				
				String statusCheckBox = request.getParameter("status");
				if(statusCheckBox == null || statusCheckBox.equalsIgnoreCase("")) {
					createPcrfServicePolicyForm.setStatus("0");
				} else {
					createPcrfServicePolicyForm.setStatus("1");   
				}

				if(BaseConstant.HIDE_STATUS.equals(createPcrfServicePolicyForm.getStatus())){
					pcrfPolicyData.setStatus(BaseConstant.HIDE_STATUS_ID);
				}else{
					pcrfPolicyData.setStatus(BaseConstant.SHOW_STATUS_ID);
				}
				

				pcrfPolicyData.setName(createPcrfServicePolicyForm.getName());
				pcrfPolicyData.setDescription(createPcrfServicePolicyForm.getDescription());
				pcrfPolicyData.setIdentityAttribute(createPcrfServicePolicyForm.getIdentityAttribute());
				pcrfPolicyData.setRuleset(createPcrfServicePolicyForm.getRuleset());
				pcrfPolicyData.setAction(createPcrfServicePolicyForm.getAction());
				pcrfPolicyData.setUnknownUserAction(createPcrfServicePolicyForm.getUnknownUserAction());
				pcrfPolicyData.setPkgId(createPcrfServicePolicyForm.getPkgId());				
				pcrfPolicyData.setOrderNumber(1);
				pcrfPolicyData.setSyMode(createPcrfServicePolicyForm.getSyMode());
				
				
				pcrfPolicyData.setSessionMgrEnabled(createPcrfServicePolicyForm.getSessionMgrEnabled());
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				servicePolicyBLManager.create(pcrfPolicyData,staffData,ACTION_ALIAS);

				ActionMessage message = new ActionMessage("servicepolicy.pcrf.create.success",pcrfPolicyData.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
				request.setAttribute("responseUrl","/initSearchPCRFService.do");
				return mapping.findForward(SUCCESS_FORWARD);
			}catch (Exception e) {
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("servicepolicy.pcrf.create.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("servicepolicy.pcrf.error.heading","creating");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage); 
	            return mapping.findForward(FAILURE_FORWARD);
			}
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("servicepolicy.pcrf.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}