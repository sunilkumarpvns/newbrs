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

import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterPeerGroupRelData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfigFailureParam;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm;

public class CreateRoutingConfAction extends BaseWebAction {
	private static final String MODULE = "DIAMETER-ROUTING-CONF";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_ROUTING_TABLE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{

		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());
		try{
			checkActionPermission(request, ACTION_ALIAS);
			DiameterRoutingConfForm diameterRoutingConfForm = (DiameterRoutingConfForm)form;
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			DiameterRoutingConfData diameterRoutingConfData = new DiameterRoutingConfData();
			convertFromFormToBean(diameterRoutingConfForm,diameterRoutingConfData);
			diameterRoutingConfData.setCreatedByStaffId(currentUser);
			diameterRoutingConfData.setLastModifiedByStaffId(currentUser);
			
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
			
			// Set DiameterRoutingConfigFailureParams
			Set<DiameterRoutingConfigFailureParam> diameterRoutingConfigFailureParamSet = getDiameterRoutingConfigFailureParamSet(diameterRoutingConfForm);
			diameterRoutingConfData.setDiameterRoutingConfigFailureParamSet(diameterRoutingConfigFailureParamSet);
			
			diameterRoutingConfBLManager.createRoutingEntry(diameterRoutingConfData, staffData);
			
			request.setAttribute("responseUrl", "/initSearchDiameterRoutingConfig");
			ActionMessage message = new ActionMessage("diameter.routingconf.success");
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
			ActionMessage message = new ActionMessage("diameter.routingconf.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE);
	}

	private void convertFromFormToBean(DiameterRoutingConfForm diameterRoutingConfForm,DiameterRoutingConfData diameterRoutingConfData) throws DataManagerException{
		diameterRoutingConfData.setName(diameterRoutingConfForm.getName());
		diameterRoutingConfData.setDescription(diameterRoutingConfForm.getDescription());
		diameterRoutingConfData.setRealmName(diameterRoutingConfForm.getRealmName());
		diameterRoutingConfData.setAppIds(diameterRoutingConfForm.getAppIds());
		diameterRoutingConfData.setOriginHost(diameterRoutingConfForm.getOriginHost());
		diameterRoutingConfData.setOriginRealm(diameterRoutingConfForm.getOriginRealm());
		diameterRoutingConfData.setRuleset(diameterRoutingConfForm.getRuleset());
		if(Strings.isNullOrBlank(diameterRoutingConfForm.getConfigId())){
			diameterRoutingConfData.setTransMapConfId(null);
			diameterRoutingConfData.setCopyPacketMapId(null);
			
		}else{
			if(diameterRoutingConfForm.getConfigId().startsWith(ConfigConstant.COPY_PACKET_MAPPING)){
				diameterRoutingConfData.setCopyPacketMapId(diameterRoutingConfForm.getConfigId().substring(ConfigConstant.COPY_PACKET_MAPPING.length()));
				diameterRoutingConfData.setTransMapConfId(null);
				
			}else{
				diameterRoutingConfData.setCopyPacketMapId(null);
				diameterRoutingConfData.setTransMapConfId(diameterRoutingConfForm.getConfigId().substring(ConfigConstant.TRANSLATION_MAPPING.length()));
			}
			
		}
		diameterRoutingConfData.setRoutingAction(diameterRoutingConfForm.getRoutingAction());
		diameterRoutingConfData.setStatefulRouting(diameterRoutingConfForm.getStatefulRouting());
		diameterRoutingConfData.setAttachedRedirection(diameterRoutingConfForm.getAttachedRedirection().toString());
		diameterRoutingConfData.setCreateDate(getCurrentTimeStemp());
		diameterRoutingConfData.setLastModifiedDate(getCurrentTimeStemp());
		diameterRoutingConfData.setRoutingTableId(diameterRoutingConfForm.getRoutingTableId());
		
		if( Strings.isNullOrBlank(diameterRoutingConfForm.getRoutingTableId()) == true){
			diameterRoutingConfData.setRoutingTableId(null);
		}else{
			diameterRoutingConfData.setRoutingTableId(diameterRoutingConfForm.getRoutingTableId());
		}
		
		if(diameterRoutingConfForm.getSubscriberMode() != null && diameterRoutingConfForm.getSubscriberMode().length() > 0){
			
			MSISDNBasedRoutingTableBLManager blManager= new MSISDNBasedRoutingTableBLManager();
			IMSIBasedRoutingTableBLManager imsiBasedRoutingTableBLManager = new IMSIBasedRoutingTableBLManager();
			
			
			if( diameterRoutingConfForm.getSubscriberMode().equals(CommonConstants.IMSI_MSISDN )){
				
				if(diameterRoutingConfForm.getSubscriberRouting1() != null && diameterRoutingConfForm.getSubscriberRouting1().length() > 0){
					
					IMSIBasedRoutingTableData imsiBasedRoutingTableData = imsiBasedRoutingTableBLManager.getIMSIDataByName(diameterRoutingConfForm.getSubscriberRouting1());
					diameterRoutingConfData.setImsiBasedRoutingTableId(imsiBasedRoutingTableData.getRoutingTableId());
				}else{
					diameterRoutingConfData.setImsiBasedRoutingTableId(null);
				}
				
				if(diameterRoutingConfForm.getSubscriberRouting2() != null && diameterRoutingConfForm.getSubscriberRouting2().length() > 0){
					MSISDNBasedRoutingTableData msisdnBasedRoutingTableData =blManager.getMSISDNDataByName(diameterRoutingConfForm.getSubscriberRouting2());
					diameterRoutingConfData.setMsisdnBasedRoutingTableId(msisdnBasedRoutingTableData.getRoutingTableId());
				}else{
					diameterRoutingConfData.setMsisdnBasedRoutingTableId(null);
				}
				
				diameterRoutingConfData.setSubsciberMode(CommonConstants.IMSI_MSISDN);
			
			}else if( diameterRoutingConfForm.getSubscriberMode().equals(CommonConstants.MSISDN_IMSI) ){
				
				if(diameterRoutingConfForm.getSubscriberRouting1() != null && diameterRoutingConfForm.getSubscriberRouting1().length() > 0){
					MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = blManager.getMSISDNDataByName(diameterRoutingConfForm.getSubscriberRouting1());
					diameterRoutingConfData.setMsisdnBasedRoutingTableId(msisdnBasedRoutingTableData.getRoutingTableId());
				}else{
					diameterRoutingConfData.setMsisdnBasedRoutingTableId(null);
				}
				
				if(diameterRoutingConfForm.getSubscriberRouting2() != null && diameterRoutingConfForm.getSubscriberRouting2().length() > 0){
					IMSIBasedRoutingTableData imsiBasedRoutingTableData = imsiBasedRoutingTableBLManager.getIMSIDataByName(diameterRoutingConfForm.getSubscriberRouting2());
					diameterRoutingConfData.setImsiBasedRoutingTableId(imsiBasedRoutingTableData.getRoutingTableId());
				}else{
					diameterRoutingConfData.setImsiBasedRoutingTableId(null);
				}
				diameterRoutingConfData.setSubsciberMode(CommonConstants.MSISDN_IMSI);
			}
			
		}else{
			
			diameterRoutingConfData.setSubsciberMode(null);
			diameterRoutingConfData.setImsiBasedRoutingTableId(null);
			diameterRoutingConfData.setMsisdnBasedRoutingTableId(null);
		}
		
		/*if(diameterRoutingConfForm.getProtocolFailureAction() == 0){
			diameterRoutingConfData.setProtocolFailureAction(null);
		}else{
			diameterRoutingConfData.setProtocolFailureAction(diameterRoutingConfForm.getProtocolFailureAction());
		}
		diameterRoutingConfData.setProtocolFailureArguments(diameterRoutingConfForm.getProtocolFailureArguments());
		if(diameterRoutingConfForm.getTransientFailureAction() == 0){
			diameterRoutingConfData.setTransientFailureAction(null);
		}else{
			diameterRoutingConfData.setTransientFailureAction(diameterRoutingConfForm.getTransientFailureAction());
		}
		diameterRoutingConfData.setTransientFailureArguments(diameterRoutingConfForm.getTransientFailureArguments());
		if(diameterRoutingConfForm.getPermanentFailureAction() == 0){
			diameterRoutingConfData.setPermanentFailureAction(null);
		}else{
			diameterRoutingConfData.setPermanentFailureAction(diameterRoutingConfForm.getPermanentFailureAction());
		}
		diameterRoutingConfData.setPermanentFailureArguments(diameterRoutingConfForm.getPermanentFailureArguments());
		if(diameterRoutingConfForm.getTimeOutAction() == 0){
			diameterRoutingConfData.setTimeOutAction(null);
		}else{
			diameterRoutingConfData.setTimeOutAction(diameterRoutingConfForm.getTimeOutAction());
		}
		diameterRoutingConfData.setTimeOutArguments(diameterRoutingConfForm.getTimeOutArguments());*/
		diameterRoutingConfData.setTransactionTimeout(diameterRoutingConfForm.getTransactionTimeout());
	}
	
	private Set<DiameterRoutingConfigFailureParam> getDiameterRoutingConfigFailureParamSet(DiameterRoutingConfForm diameterRoutingConfForm) {
		Set<DiameterRoutingConfigFailureParam> diameterRoutingConfigFailureParamSet = new LinkedHashSet<DiameterRoutingConfigFailureParam>();
		String[] errorCode = diameterRoutingConfForm.getErrorCode();
		Short[] failureAction = diameterRoutingConfForm.getFailureAction();
		String[] failureArgument = diameterRoutingConfForm.getFailureArgument();
		if(errorCode != null && failureAction != null && failureArgument!=null) {
			int length = errorCode.length;
			if(length == failureAction.length && length == failureArgument.length ) {
				for(int i=0;i<length;i++) {
					DiameterRoutingConfigFailureParam diameterRoutingConfigFailureParam = new DiameterRoutingConfigFailureParam();
					diameterRoutingConfigFailureParam.setErrorCodes(errorCode[i]);
					diameterRoutingConfigFailureParam.setFailureAction(failureAction[i]);
					diameterRoutingConfigFailureParam.setFailureArgument(failureArgument[i]);
					diameterRoutingConfigFailureParamSet.add(diameterRoutingConfigFailureParam);
					diameterRoutingConfigFailureParamSet.add(diameterRoutingConfigFailureParam);
				}
			}
		}
		return diameterRoutingConfigFailureParamSet;
	}
	
}