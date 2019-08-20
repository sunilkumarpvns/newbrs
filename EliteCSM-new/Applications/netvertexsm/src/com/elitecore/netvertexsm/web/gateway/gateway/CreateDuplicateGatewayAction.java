package com.elitecore.netvertexsm.web.gateway.gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.DiameterGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.RadiusGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.gateway.form.GatewayForm;

public class CreateDuplicateGatewayAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_GATEWAY;
	private static final String DUPLICATE_GATEWAY = "createDuplicateGateway";
	private static final String RADIUS_FORWARD = "initRadiusGateway";
	private static final String DIAMETER_FORWARD = "initDiameterGateway";
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception {
		 try{
			 checkAccess(request, ACTION_ALIAS);
			 
			 Logger.logInfo(ACTION_ALIAS, "Enter execute method of :"+getClass().getName());
			 GatewayBLManager gatewayBLManager = new GatewayBLManager();
			 GatewayForm gatewayForm = (GatewayForm) form;
			 GatewayData gatewayData = null;
			 IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
 			 if("duplicate".equals(gatewayForm.getAction())){
 				Logger.logInfo(MODULE, " Creating Duplicate Gateway ");
 			    String gatewayInterface = gatewayForm.getCommProtocolId();
 				gatewayData = (GatewayData) request.getSession().getAttribute("gatewayData");
 				
 				gatewayData.setAreaName(gatewayForm.getAreaName());
 				if(gatewayForm.getLocationId()==0){
 					gatewayData.setLocationId(null);
 				}else{
 					gatewayData.setLocationId(gatewayForm.getLocationId());	
 				}
 				gatewayData.setDescription(gatewayForm.getDescription());
 				gatewayData.setCommProtocol(gatewayForm.getCommProtocolId());
 				gatewayData.setGatewayName(gatewayForm.getGatewayName());
 				
 				
 				setChildData(gatewayData);
 				request.setAttribute("action", "duplicate");
 				
 				request.getSession().setAttribute("gatewayData",gatewayData);
 				if(gatewayInterface.equalsIgnoreCase(CommunicationProtocol.RADIUS.id)) {
 			    	gatewayData.setProfileId(gatewayForm.getRadiusGatewayProfileId());
 			    	return mapping.findForward(RADIUS_FORWARD);
 			    	
 			    }else if(gatewayInterface.equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)) {
 			    	gatewayData.setProfileId(gatewayForm.getDiameterGatewayProfileId());
 			    	return mapping.findForward(DIAMETER_FORWARD); 			    	
 			    }
 				
			 }else{
				 
				 gatewayData = new GatewayData();
				 gatewayData.setGatewayId(gatewayForm.getGatewayId());	
				 gatewayData = gatewayBLManager.getGatewayData(gatewayData,staffData,ACTION_ALIAS);
				 gatewayForm.setConnectionUrl(gatewayData.getConnectionUrl());
				 gatewayForm.setCommProtocolId(gatewayData.getCommProtocol());
				 gatewayForm.setGatewayName(gatewayData.getGatewayName());
				 gatewayForm.setDescription(gatewayData.getDescription());
				 gatewayForm.setLocationId(gatewayData.getLocationId());
				 gatewayForm.setLocationList(gatewayBLManager.getLocationList());
				 gatewayForm.setAreaName(gatewayData.getAreaName());
				 gatewayForm.setGatewayProfileId(gatewayData.getProfileId());
	 				if(gatewayData.getCommProtocol().equalsIgnoreCase(CommunicationProtocol.RADIUS.id)) {
	 			    	gatewayForm.setRadiusGatewayProfileId((int)gatewayData.getProfileId());
	 			    	
	 			    }else if(gatewayData.getCommProtocol().equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)) {
	 			    	gatewayForm.setDiameterGatewayProfileId((int)gatewayData.getProfileId());
	 			    	
	 			    }
				 List<GatewayProfileData> profileList = gatewayBLManager.getProfileList();
				 List<GatewayProfileData> radiusProfileList = new ArrayList<GatewayProfileData>();
				 List<GatewayProfileData> diameterProfileList = new ArrayList<GatewayProfileData>();

				 for(GatewayProfileData profileData : profileList) {
					 String commProtocolId = profileData.getCommProtocolId();
					if(commProtocolId.equalsIgnoreCase(CommunicationProtocol.RADIUS.id)){
						 radiusProfileList.add(profileData);
					}
					else if(commProtocolId.equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)){
						 diameterProfileList.add(profileData);
					}
				 }
				 gatewayForm.setRadiusProfileList(radiusProfileList);
				 gatewayForm.setDiameterProfileList(diameterProfileList);
				 gatewayForm.setGatewayProfileId(gatewayData.getProfileId());
				 request.getSession().setAttribute("gatewayData", gatewayData);
				 
				 return mapping.findForward(DUPLICATE_GATEWAY);
			 }
		 }catch(ActionNotPermitedException e){
			 
		 }catch(Exception e){
			Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("gateway.create.failure"));
			saveErrors(request, messages);
		 }
		return mapping.findForward(FAILURE);
	}
	
	private void setChildData(GatewayData gatewayData){
		if(gatewayData.getCommProtocol().equalsIgnoreCase(CommunicationProtocol.RADIUS.id)) {
			RadiusGatewayData radiusGatewayData = new RadiusGatewayData();
			if(Collectionz.isNullOrEmpty(gatewayData.getRadiusGatewayDataSet()) == false){
				Set<RadiusGatewayData> radiusGatewayDataSet = gatewayData.getRadiusGatewayDataSet();
				for(RadiusGatewayData dbRadiusGatewayData : radiusGatewayDataSet){
					radiusGatewayData.setSharedSecret(dbRadiusGatewayData.getSharedSecret());
					radiusGatewayData.setMinLocalPort(dbRadiusGatewayData.getMinLocalPort());
				}
				gatewayData.setRadiusGatewayData(radiusGatewayData);
			}
			
		}else if(gatewayData.getCommProtocol().equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)) {
			DiameterGatewayData diameterGatewayData = new  DiameterGatewayData();
			
			if(Collectionz.isNullOrEmpty(gatewayData.getDiameterGatewayDataSet()) == false){
				Set<DiameterGatewayData> diameterGatewayDataSet = gatewayData.getDiameterGatewayDataSet();
				for(DiameterGatewayData dbDiameterGatewayData : diameterGatewayDataSet){
					String hostId = dbDiameterGatewayData.getHostId();
					if(Strings.isNullOrBlank(hostId) == false){
						hostId = "copy_of_" + hostId;
					}
					diameterGatewayData.setHostId(hostId);
					diameterGatewayData.setLocalAddress(dbDiameterGatewayData.getLocalAddress());
					diameterGatewayData.setRealm(dbDiameterGatewayData.getRealm());
					diameterGatewayData.setRequestTimeout(dbDiameterGatewayData.getRequestTimeout());
					diameterGatewayData.setRetransmissionCount(dbDiameterGatewayData.getRetransmissionCount());
					diameterGatewayData.setAlternateHostId(dbDiameterGatewayData.getAlternateHostId());
				}
				gatewayData.setDiameterGatewayData(diameterGatewayData);
			}
		}
		/*remove persistent object*/
		gatewayData.setDiameterGatewayDataSet(null);
		gatewayData.setRadiusGatewayDataSet(null);
	}

}
