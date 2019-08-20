package com.elitecore.elitesm.web.diameter.routingconfig;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupRelData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm;

public class UpdateDiameterPeerAction extends BaseWebAction {
	private static final String MODULE = "DIAMETER-ROUTING-CONF";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_ROUTING_TABLE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			DiameterRoutingConfForm diameterRoutingConfForm = (DiameterRoutingConfForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			DiameterRoutingConfData diameterRoutingConfData = new DiameterRoutingConfData();

			diameterRoutingConfData=diameterRoutingConfBLManager.getDiameterRoutingConfData(diameterRoutingConfForm.getRoutingConfigId());
			
			diameterRoutingConfData.setLastModifiedByStaffId(currentUser);
			diameterRoutingConfData.setLastModifiedDate(getCurrentTimeStemp());
			diameterRoutingConfData.setRoutingConfigId(diameterRoutingConfForm.getRoutingConfigId());
			
			String strPeerGroupCount = request.getParameter("peerGroupCount");
			int peerGroupCount = Integer.parseInt(strPeerGroupCount);
			
			DiameterPeerGroupData diameterPeerGroupData = null;
			DiameterPeerGroupRelData diameterPeerGroupRelData = null;

			Set<DiameterPeerGroupData> diameterPeerGroupDataSet = new LinkedHashSet<DiameterPeerGroupData>(); 
			for(int i=0;i<peerGroupCount ;i++){
				int index = i+1;
				String ruleSet = request.getParameter("peerGroupRuleSet"+index);
				String[] peers = request.getParameterValues("peer"+index);
				String[] loadFactor = request.getParameterValues("loadFactor"+index);
				
				if(ruleSet != null){					
					diameterPeerGroupData = new DiameterPeerGroupData();
					diameterPeerGroupData.setRuleset(ruleSet);
					diameterPeerGroupDataSet.add(diameterPeerGroupData);
					Set<DiameterPeerGroupRelData> diameterPeerGroupRelDataSet =  new LinkedHashSet<DiameterPeerGroupRelData>(); 
					Long orderNumber = 1L;
					for (int j = 0; j < peers.length; j++) {
						diameterPeerGroupRelData = new DiameterPeerGroupRelData();
						diameterPeerGroupRelData.setPeerUUID(peers[j]);
						diameterPeerGroupRelData.setLoadFector(Long.parseLong(loadFactor[j]));
						
						String peerName = diameterRoutingConfBLManager.getDiameterPeerNameById(peers[j]);
						
						diameterPeerGroupRelData.setPeerName(peerName);
						diameterPeerGroupRelData.setOrderNumber( orderNumber );
						orderNumber++;
						diameterPeerGroupRelDataSet.add(diameterPeerGroupRelData);
					}	
					diameterPeerGroupData.setDiameterPeerGroupRelDataSet(diameterPeerGroupRelDataSet);
				}
			}
			diameterRoutingConfData.setDiameterPeerGroupDataSet(diameterPeerGroupDataSet);
			
			staffData.setAuditName(diameterRoutingConfData.getName());
			staffData.setAuditId(diameterRoutingConfData.getAuditUId());
			
			diameterRoutingConfBLManager.updateDiameterPeer(diameterRoutingConfData,staffData,ACTION_ALIAS);
			
			request.setAttribute("responseUrl", "/viewDiameterRoutingTable?routingConfigId="+diameterRoutingConfData.getRoutingConfigId());
			ActionMessage message = new ActionMessage("diameter.routingconf.peer.update.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveMessages(request, messages);
			return mapping.findForward(SUCCESS);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.routingconf.peer.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}
}