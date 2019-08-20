package com.elitecore.elitesm.web.diameter.routingconfig;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.routingconf.DiameterRoutingConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm;

public class InitUpdateDiameterRoutingConfAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CGPOLICY";
	private static final String INITUPDATEDIAMETERROUTINGCONF = "initUpdateDiameterRoutingConf"; 
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_DIAMETER_ROUTING_TABLE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());		
		try {	
			checkActionPermission(request,ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			DiameterRoutingConfForm diameterRoutingConfForm = (DiameterRoutingConfForm)form;
			
			DiameterRoutingConfBLManager diamterRoutingConfBLManager = new DiameterRoutingConfBLManager();			 
			DiameterRoutingConfData diameterRoutingConfData = diamterRoutingConfBLManager.getDiameterRoutingConfData(diameterRoutingConfForm.getRoutingConfigId());
			
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.DIAMETER);
			diameterRoutingConfForm.setTranslationMappingConfDataList(translationMappingConfDataList);
			
			CopyPacketTransMapConfBLManager copyPacketMappingBLManager = new CopyPacketTransMapConfBLManager();
			List<CopyPacketTranslationConfData> copyPacketMapConfDataList = copyPacketMappingBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.DIAMETER);
			diameterRoutingConfForm.setCopyPacketMappingConfDataList(copyPacketMapConfDataList);
			
			IMSIBasedRoutingTableBLManager imsiBasedRoutingTableBLManager = new IMSIBasedRoutingTableBLManager();
			List<IMSIBasedRoutingTableData> imsiBasedRoutingTableDatas = imsiBasedRoutingTableBLManager.getIMSIBasedRoutingTableList();
			diameterRoutingConfForm.setImsiBasedRoutingTableDataList(imsiBasedRoutingTableDatas);
				
			MSISDNBasedRoutingTableBLManager msisdnBasedRoutingTableBLManager = new MSISDNBasedRoutingTableBLManager();
			List<MSISDNBasedRoutingTableData> msisdnRoutingTableDatas = msisdnBasedRoutingTableBLManager.getMSISDNBasedRoutingTableList();
			diameterRoutingConfForm.setMsisdnBasedRoutingTableDataList(msisdnRoutingTableDatas);
			
			if(diameterRoutingConfData != null) {
				diameterRoutingConfForm.setRoutingConfigId(diameterRoutingConfData.getRoutingConfigId());
				diameterRoutingConfForm.setName(diameterRoutingConfData.getName());
				diameterRoutingConfForm.setDescription(diameterRoutingConfData.getDescription());
				diameterRoutingConfForm.setRealmName(diameterRoutingConfData.getRealmName());
				diameterRoutingConfForm.setAppIds(diameterRoutingConfData.getAppIds());
				diameterRoutingConfForm.setOriginHost(diameterRoutingConfData.getOriginHost());
				diameterRoutingConfForm.setOriginRealm(diameterRoutingConfData.getOriginRealm());
				diameterRoutingConfForm.setRuleset(diameterRoutingConfData.getRuleset());
				if(diameterRoutingConfData.getTransMapConfId()!= null){
					diameterRoutingConfForm.setCopyPacketMapConfigId(null);
					diameterRoutingConfForm.setConfigId(ConfigConstant.TRANSLATION_MAPPING + diameterRoutingConfData.getTransMapConfId());
					diameterRoutingConfForm.setTranslationMapConfigId(diameterRoutingConfData.getTransMapConfId());
				}else if(diameterRoutingConfData.getCopyPacketMapId() != null){
					diameterRoutingConfForm.setTranslationMapConfigId(null);
					diameterRoutingConfForm.setConfigId(ConfigConstant.COPY_PACKET_MAPPING + diameterRoutingConfData.getCopyPacketMapId());
					diameterRoutingConfForm.setCopyPacketMapConfigId(diameterRoutingConfData.getCopyPacketMapId());

				}else{
					diameterRoutingConfForm.setConfigId(null);
					diameterRoutingConfForm.setCopyPacketMapConfigId(null);
					diameterRoutingConfForm.setTranslationMapConfigId(null);
				}
				diameterRoutingConfForm.setRoutingAction(diameterRoutingConfData.getRoutingAction());
				diameterRoutingConfForm.setStatefulRouting(diameterRoutingConfData.getStatefulRouting());
				diameterRoutingConfForm.setAttachedRedirection(Boolean.valueOf(diameterRoutingConfData.getAttachedRedirection()));
				diameterRoutingConfForm.setOrderNumber(diameterRoutingConfData.getOrderNumber());
				diameterRoutingConfForm.setRoutingTableId(diameterRoutingConfData.getRoutingTableId());
				diameterRoutingConfForm.setTransactionTimeout(diameterRoutingConfData.getTransactionTimeout());
				diameterRoutingConfForm.setProtocolFailureAction(diameterRoutingConfData.getProtocolFailureAction());
				diameterRoutingConfForm.setProtocolFailureArguments(diameterRoutingConfData.getProtocolFailureArguments());
				diameterRoutingConfForm.setTransientFailureAction(diameterRoutingConfData.getTransientFailureAction());
				diameterRoutingConfForm.setTransientFailureArguments(diameterRoutingConfData.getTransientFailureArguments());
				diameterRoutingConfForm.setPermanentFailureAction(diameterRoutingConfData.getPermanentFailureAction());
				diameterRoutingConfForm.setPermanentFailureArguments(diameterRoutingConfData.getPermanentFailureArguments());
				diameterRoutingConfForm.setTimeOutAction(diameterRoutingConfData.getTimeOutAction());
				diameterRoutingConfForm.setTimeOutArguments(diameterRoutingConfData.getTimeOutArguments());
				diameterRoutingConfForm.setAuditUId(diameterRoutingConfData.getAuditUId());
				
				if(diameterRoutingConfData.getSubsciberMode() != null){
					
					IMSIBasedRoutingTableBLManager imsBasedRoutingTableBLManager = new IMSIBasedRoutingTableBLManager();
					MSISDNBasedRoutingTableBLManager msiBasedRoutingTableBLManager = new MSISDNBasedRoutingTableBLManager();
					
					if(diameterRoutingConfData.getSubsciberMode().equals(CommonConstants.IMSI_MSISDN)){
						
						if(diameterRoutingConfData.getImsiBasedRoutingTableId() != null){
							IMSIBasedRoutingTableData imsiBasedRoutingTableData = imsBasedRoutingTableBLManager.getImsiBasedRoutingTableData(diameterRoutingConfData.getImsiBasedRoutingTableId());
							diameterRoutingConfForm.setSubscriberRouting1(imsiBasedRoutingTableData.getRoutingTableName());
						}else{
							diameterRoutingConfForm.setSubscriberRouting1("");
						}
						
						if(diameterRoutingConfData.getMsisdnBasedRoutingTableId() != null){
							MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = msiBasedRoutingTableBLManager.getMSISDNBasedRoutingTableData(diameterRoutingConfData.getMsisdnBasedRoutingTableId());
							diameterRoutingConfForm.setSubscriberRouting2(msisdnBasedRoutingTableData.getRoutingTableName());
						}else{
							diameterRoutingConfForm.setSubscriberRouting2("");
						}
						
					}else if(diameterRoutingConfData.getSubsciberMode().equals(CommonConstants.MSISDN_IMSI)){
						
						if(diameterRoutingConfData.getMsisdnBasedRoutingTableId() != null){
							MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = msiBasedRoutingTableBLManager.getMSISDNBasedRoutingTableData(diameterRoutingConfData.getMsisdnBasedRoutingTableId());
							diameterRoutingConfForm.setSubscriberRouting1(msisdnBasedRoutingTableData.getRoutingTableName());
						}else{
							diameterRoutingConfForm.setSubscriberRouting1("");
						}
						
						if(diameterRoutingConfData.getImsiBasedRoutingTableId() != null){
							IMSIBasedRoutingTableData imsiBasedRoutingTableData = imsBasedRoutingTableBLManager.getImsiBasedRoutingTableData(diameterRoutingConfData.getImsiBasedRoutingTableId());
							diameterRoutingConfForm.setSubscriberRouting2(imsiBasedRoutingTableData.getRoutingTableName());
						}else{
							diameterRoutingConfForm.setSubscriberRouting2("");
						}
					}
				}else{
					diameterRoutingConfForm.setSubscriberMode("");
					diameterRoutingConfForm.setSubscriberRouting1("");
					diameterRoutingConfForm.setSubscriberRouting2("");
				}
				
			}
			GenericBLManager genericBLManager = new GenericBLManager();
			PageList pageList = genericBLManager.getAllRecords(DiameterRoutingTableData.class,"routingTableName",true);
			diameterRoutingConfForm.setDiameterRoutingTablesList(pageList.getListData());
			diameterRoutingConfForm.setDefaultFailureArgument("0.0.0.0");
			//Set Failure Parameter
			diameterRoutingConfForm.setDefaultFailureActionMap();
			diameterRoutingConfForm.setDefaultFailureArgument("0.0.0.0");
			diameterRoutingConfForm.setDefaultFailureAction("4");
			
			request.setAttribute("diameterRoutingConfData",diameterRoutingConfData);
			request.setAttribute("diameterRoutingConfForm",diameterRoutingConfForm);
			return mapping.findForward(INITUPDATEDIAMETERROUTINGCONF);             
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (Exception e) {
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.routingconf.update.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}                    
		return mapping.findForward(FAILURE_FORWARD); 
	}
}