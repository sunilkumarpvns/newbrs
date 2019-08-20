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
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfigFailureParam;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm;

public class UpdateDiameterRoutingConfAction extends BaseWebAction { 
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_ROUTING_TABLE;
	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-ROUTING-CONF";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			DiameterRoutingConfForm diameterRoutingConfForm = (DiameterRoutingConfForm)form;
			DiameterRoutingConfBLManager diameterRoutingConfBLManager = new DiameterRoutingConfBLManager();
			
			DiameterRoutingConfData diameterRoutingConfData = diameterRoutingConfBLManager.getDiameterRoutingConfData(diameterRoutingConfForm.getRoutingConfigId());
			
			convertFormToBean(diameterRoutingConfForm,diameterRoutingConfData);
			
			// Set DiameterRoutingConfigFailureParams
			Set<DiameterRoutingConfigFailureParam> diameterRoutingConfigFailureParamSet = getDiameterRoutingConfigFailureParamSet(diameterRoutingConfForm);
			diameterRoutingConfData.setDiameterRoutingConfigFailureParamSet(diameterRoutingConfigFailureParamSet);

			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
			diameterRoutingConfData.setLastModifiedByStaffId(currentUser);
			
			staffData.setAuditName(diameterRoutingConfData.getName());
			staffData.setAuditId(diameterRoutingConfData.getAuditUId());
			 
			diameterRoutingConfBLManager.update(diameterRoutingConfData,staffData,ACTION_ALIAS);
			
			request.setAttribute("diameterRoutingConfData",diameterRoutingConfData);
			request.setAttribute("diameterRoutingConfForm",diameterRoutingConfForm);
			request.setAttribute("responseUrl", "/viewDiameterRoutingTable?routingConfigId="+diameterRoutingConfData.getRoutingConfigId()); 
			ActionMessage message = new ActionMessage("diameter.routingconf.update.success");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);         				   
			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 
			return mapping.findForward(SUCCESS_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch (DuplicateInstanceNameFoundException dpfExp) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dpfExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.routingconf.duplicate");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);                                                                                               
			ActionMessage message = new ActionMessage("diameter.routingconf.update.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}

	private void convertFormToBean(DiameterRoutingConfForm diameterRoutingConfForm,DiameterRoutingConfData diameterRoutingConfData) throws DataManagerException {
		diameterRoutingConfData.setRoutingConfigId(diameterRoutingConfForm.getRoutingConfigId());
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
		diameterRoutingConfData.setLastModifiedDate(getCurrentTimeStemp());
		if(Strings.isNullOrBlank(diameterRoutingConfForm.getRoutingTableId()) == true){
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
		diameterRoutingConfData.setAuditUId(diameterRoutingConfForm.getAuditUId());
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