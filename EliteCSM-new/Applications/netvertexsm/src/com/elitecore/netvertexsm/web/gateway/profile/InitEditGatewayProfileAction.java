
package com.elitecore.netvertexsm.web.gateway.profile;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.VendorData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.gateway.profile.form.EditGatewayProfileForm;

public class InitEditGatewayProfileAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_GATEWAY_PROFILE;
	private static final String MODULE = "EDIT_GATEWAY_PROFILE";
	private String FORWARD_PAGE = "editGatewayProfile";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try {
				EditGatewayProfileForm gatewayProfileForm = (EditGatewayProfileForm) form;
				GatewayBLManager gatewayBLManager = new GatewayBLManager();
				GatewayProfileData gatewayProfileData = new GatewayProfileData();
				
				long profileId = Long.parseLong(request.getParameter("profileId"));
				gatewayProfileData.setProfileId(profileId);

				gatewayProfileData = gatewayBLManager.getGatewayProfileData(profileId);
				request.setAttribute("gatewayProfileData", gatewayProfileData);
				
				gatewayProfileForm.setProfileId(profileId);
				gatewayProfileForm.setGatewayProfileName(gatewayProfileData.getProfileName());
				gatewayProfileForm.setGatewayType(gatewayProfileData.getGatewayType());
				gatewayProfileForm.setBufferBW(gatewayProfileData.getBufferBW());
				gatewayProfileForm.setCommProtocolId(CommunicationProtocol.fromId(gatewayProfileData.getCommProtocolId()).name);
				gatewayProfileForm.setDescription(gatewayProfileData.getDescription());
				gatewayProfileForm.setFirmware(gatewayProfileData.getFirmware());
				gatewayProfileForm.setMaxThroughtput(gatewayProfileData.getMaxThroughput());
				gatewayProfileForm.setMaxIPCANSession(gatewayProfileData.getMaxIPCANSession());
				gatewayProfileForm.setVendorId(gatewayProfileData.getVedorId());
				gatewayProfileForm.setSupportedStandard(gatewayProfileData.getSupportedStandard());
				gatewayProfileForm.setUsageReportingTime(gatewayProfileData.getUsageReportingTime());
				List<DatabaseDSData> datasourceList = gatewayBLManager.getDataSorceList();
				gatewayProfileForm.setDataSourceList(datasourceList);
				List<VendorData> vendorList = gatewayBLManager.getVendorList();
				gatewayProfileForm.setVendorList(vendorList);		    	
				gatewayProfileForm.setRevalidationMode(gatewayProfileData.getRevalidationMode());				
				
				request.setAttribute("editGatewayProfileForm", gatewayProfileForm);
			    return mapping.findForward(FORWARD_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information",new ActionMessage("gateway.update.init.failed"));
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
