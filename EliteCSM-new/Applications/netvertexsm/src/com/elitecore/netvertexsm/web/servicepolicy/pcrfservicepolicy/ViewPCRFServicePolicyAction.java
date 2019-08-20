package com.elitecore.netvertexsm.web.servicepolicy.pcrfservicepolicy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFPolicyCDRDriverRelData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData;
import com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicySyGatewayRelData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class ViewPCRFServicePolicyAction extends BaseWebAction {
	private static final String VIEW_FORWARD = "viewPCRFServicePolicy";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_PCRF_POLICY;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				long pcrfPolicyId = Long.parseLong(request.getParameter("pcrfPolicyId"));
				PCRFServicePolicyData pcrfPolicyData = new PCRFServicePolicyData();
				ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
				GatewayBLManager gatewayBLManager =  new GatewayBLManager();
				
				List<PCRFPolicyCDRDriverRelData> pcrfPolicyCDRDriverRelDataList = new ArrayList<PCRFPolicyCDRDriverRelData>();
				List<PCRFServicePolicySyGatewayRelData> pcrfServicePolicySyGatewayRelDataList 	= new LinkedList<PCRFServicePolicySyGatewayRelData>();
				
				List<DriverInstanceData> 	driverList 	= new ArrayList<DriverInstanceData>();
				List<DriverInstanceData> cdrDriverList	= new ArrayList<DriverInstanceData>();
				List<GatewayData> 	 syGatewayDataList	= new ArrayList<GatewayData>();
				
				DriverInstanceData driverData=new DriverInstanceData();
				
				pcrfPolicyData.setPcrfPolicyId(pcrfPolicyId);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				pcrfPolicyData = servicePolicyBLManager.getPCRFServicePolicyData(pcrfPolicyData,staffData,ACTION_ALIAS);
				pcrfPolicyCDRDriverRelDataList = servicePolicyBLManager.getPCRFPolicyCDRDriverList(pcrfPolicyId);
				pcrfServicePolicySyGatewayRelDataList = servicePolicyBLManager.getPCRFPolicySyGatewayList(pcrfPolicyId);
				pcrfPolicyData.setPcrfServicePolicySyGatewayRelDataList(pcrfServicePolicySyGatewayRelDataList);
				for(int i=0;i<pcrfPolicyCDRDriverRelDataList.size();i++){
					driverData=servicePolicyBLManager.getDriverInstanceData(pcrfPolicyCDRDriverRelDataList.get(i).getDriverInstanceId());
					cdrDriverList.add(driverData);
				}
				
				GatewayData syDatewayData = null;
				for(int i=0;i<pcrfServicePolicySyGatewayRelDataList.size();i++){
					syDatewayData = new GatewayData();
					syDatewayData.setGatewayId(pcrfServicePolicySyGatewayRelDataList.get(i).getSyGatewayId());
					syDatewayData = gatewayBLManager.getGatewayData(syDatewayData);
					syGatewayDataList.add(syDatewayData);
				}
				if(Strings.isNullOrBlank(pcrfPolicyData.getPkgId()) == false){
					List<PkgData> pkgDataList = servicePolicyBLManager.getPkgDataList();
					for(PkgData pkgData : pkgDataList){
						if(pkgData.getId().equals(pcrfPolicyData.getPkgId())){
							pcrfPolicyData.setPkgName(pkgData.getName());
							break;
						}
					}
				}
				
				request.setAttribute("driverList", driverList);
				request.setAttribute("cdrDriverList", cdrDriverList);				
				request.setAttribute("syGatewayDataList", syGatewayDataList);
				request.setAttribute("pcrfPolicyData", pcrfPolicyData);				
				
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("servicepolicy.pcrf.view.failed"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("servicepolicy.pcrf.error.heading","viewing");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage); 
	            return mapping.findForward(FAILURE_FORWARD);
			} 
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("servicepolicy.pcrf.error.heading","viewing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	
	}
}
