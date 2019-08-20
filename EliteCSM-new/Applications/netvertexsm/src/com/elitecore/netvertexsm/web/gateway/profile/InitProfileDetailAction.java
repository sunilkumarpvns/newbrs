package com.elitecore.netvertexsm.web.gateway.profile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.blmanager.gateway.MappingBLManager;
import com.elitecore.netvertexsm.blmanager.gateway.pccrulemapping.RuleMappingBlManager;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DefaultAttributeMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DefaultValueMappingData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.gateway.profile.form.CreateProfileForm;

public class InitProfileDetailAction extends BaseWebAction {
	private static final String RADIUS_FORWARD = "radiusProfile";
	private static final String DIA_FORWARD = "diameterProfile";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_GATEWAY_PROFILE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
			try {
			    CreateProfileForm createProfileForm = (CreateProfileForm) form;
			    String gatewayType =request.getParameter("gatewayType");
			    String gatewayInterface = createProfileForm.getCommProtocol();
			    GatewayBLManager blManager = new GatewayBLManager();
			    MappingBLManager mappingBLManager = new MappingBLManager();
			    RuleMappingBlManager ruleMappingBlManager=new RuleMappingBlManager();
			    
			   if(gatewayInterface.equalsIgnoreCase(CommunicationProtocol.RADIUS.id)) {
			    	createProfileForm.setMaxRequestTimeout(100l);
			    	createProfileForm.setTimeout(1000l);
			    	createProfileForm.setStatusCheckDuration(120l);
			    	createProfileForm.setPcrfToRadiusPacketMappingList(mappingBLManager.getPCRFToRadiusPacketMappingList());
			    	createProfileForm.setRadiusToPCRFPacketMappingList(mappingBLManager.getRadiusToPCRFPacketMappingList());
			    	createProfileForm.setRuleMappingList(ruleMappingBlManager.getRuleMappingDataList());
			    	return mapping.findForward(RADIUS_FORWARD);
			    }else if(gatewayInterface.equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)) {
			    	createProfileForm.setPcrfToDiameterPacketMappingList(mappingBLManager.getPCRFToDiameterPacketMappingList());
			    	createProfileForm.setDiameterToPCRFPacketMappingList(mappingBLManager.getDiameterToPCRFPacketMappingList());
			    	createProfileForm.setTimeout(3000l);
					createProfileForm.setRetransmissionCnt(0l);
					createProfileForm.setGxApplicationId("10415:16777238");
					createProfileForm.setGyApplicationId("10415:4");
					createProfileForm.setRxApplicationId("10415:16777236");
					createProfileForm.setS9ApplicationId("10415:16777267");
					createProfileForm.setSyApplicationId("10415:16777302");
					createProfileForm.setDwrInterval(60);
					createProfileForm.setSessionCleanUpCER(Boolean.TRUE);
					createProfileForm.setSessionCleanUpDPR(Boolean.TRUE);
					createProfileForm.setSocketReceiveBufferSize(-1);
					createProfileForm.setSocketSendBufferSize(-1);
					createProfileForm.setInitConnectionDuration(60L);
					createProfileForm.setRuleMappingList(ruleMappingBlManager.getRuleMappingDataList());
					createProfileForm.setSessionLookupKey(PCRFKeyConstants.CS_SESSION_IPV4.val+","+PCRFKeyConstants.CS_SESSION_IPV6.val);
					request.setAttribute("gatewayType", gatewayType);
			    	return mapping.findForward(DIA_FORWARD);
			    }
			}catch (Exception e) {
				e.printStackTrace();
			}   
		}else{
	        Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	void getValueMapping(List<DefaultAttributeMappingData> defaultAttributeMappingDatalist){
		Iterator<DefaultAttributeMappingData> iterator=defaultAttributeMappingDatalist.iterator();
    	while(iterator.hasNext()){
    		DefaultAttributeMappingData defaultAttributeMappingData=iterator.next();
    		String valueMapping=defaultAttributeMappingData.getValueMapping();
    		List<DefaultValueMappingData> defaultValueMappingDataList=new ArrayList<DefaultValueMappingData>();
			defaultAttributeMappingData.setDefaultValueMapList(defaultValueMappingDataList);
    		if(!(valueMapping==null)){
    			JSONObject jsonValueMapping =JSONObject.fromObject(valueMapping);
    			Set<String> set =jsonValueMapping.keySet();
    			Iterator<String> jsonitIterator=set.iterator();
    			while(jsonitIterator.hasNext()){
    				String diameterAttribute=jsonitIterator.next();
    				String policyKey=(String)jsonValueMapping.get(diameterAttribute);
    				DefaultValueMappingData defaultValueMappingData=new DefaultValueMappingData();
    				defaultValueMappingData.setPolicyKey(policyKey);
    				defaultValueMappingData.setDiameterAttribute(diameterAttribute);
    				defaultValueMappingDataList.add(defaultValueMappingData);
    			}
    		}
    	}
	}
}
