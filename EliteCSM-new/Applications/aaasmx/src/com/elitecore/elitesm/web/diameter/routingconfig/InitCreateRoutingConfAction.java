package com.elitecore.elitesm.web.diameter.routingconfig;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.diameter.routingconfig.forms.DiameterRoutingConfForm;

public class InitCreateRoutingConfAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="DIAMETER-ROUTING-CONF";
	private static final String INITCREATEDIAMETERROUTINGCONF = "initCreateDiameterRoutingConf"; 
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_ROUTING_TABLE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());			
			DiameterRoutingConfForm diameterRoutingConfForm = (DiameterRoutingConfForm)form; 
			setDefaultValues(diameterRoutingConfForm,request);
			
			TranslationMappingConfBLManager translationMappingConfBLManager = new TranslationMappingConfBLManager();
			List<TranslationMappingConfData> translationMappingConfDataList = translationMappingConfBLManager.getTranslationMappingConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.DIAMETER);
			diameterRoutingConfForm.setTranslationMappingConfDataList(translationMappingConfDataList);
			
			CopyPacketTransMapConfBLManager copyPacketTranslationConfBLManager = new CopyPacketTransMapConfBLManager();
			List<CopyPacketTranslationConfData> copyPacketMappingConfDataList = copyPacketTranslationConfBLManager.getCopyPacketTransMapConfigList(TranslationMappingConfigConstants.DIAMETER, TranslationMappingConfigConstants.DIAMETER);
			diameterRoutingConfForm.setCopyPacketMappingConfDataList(copyPacketMappingConfDataList);
			
			DiameterPeerBLManager diameterPeerBLManager = new DiameterPeerBLManager();
			List<DiameterPeerData> diameterPeerDataList = diameterPeerBLManager.getDiameterPeerList();
			diameterRoutingConfForm.setDiameterPeersList(diameterPeerDataList);
			
			GenericBLManager genericBLManager = new GenericBLManager();
			PageList pageList = genericBLManager.getAllRecords(DiameterRoutingTableData.class,"routingTableName",true);
			diameterRoutingConfForm.setDiameterRoutingTablesList(pageList.getListData());
			
			IMSIBasedRoutingTableBLManager imsiBasedRoutingTableBLManager = new IMSIBasedRoutingTableBLManager();
			List<IMSIBasedRoutingTableData> imsiBasedRoutingTableDatas = imsiBasedRoutingTableBLManager.getIMSIBasedRoutingTableList();
			diameterRoutingConfForm.setImsiBasedRoutingTableDataList(imsiBasedRoutingTableDatas);
			
			MSISDNBasedRoutingTableBLManager msisdnBasedRoutingTableBLManager = new MSISDNBasedRoutingTableBLManager();
			List<MSISDNBasedRoutingTableData> msisdnRoutingTableDatas = msisdnBasedRoutingTableBLManager.getMSISDNBasedRoutingTableList();
			diameterRoutingConfForm.setMsisdnBasedRoutingTableDataList(msisdnRoutingTableDatas);
			
			request.setAttribute("diameterRoutingConfForm", diameterRoutingConfForm);
			return mapping.findForward(INITCREATEDIAMETERROUTINGCONF);
		}catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
		}catch(Exception e){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}                                                                                           
		return mapping.findForward(FAILURE_FORWARD); 
	}
	
	private void setDefaultValues(DiameterRoutingConfForm form, HttpServletRequest request){
		form.setDescription(getDefaultDescription(request));
		/*form.setProtocolFailureAction((long)4);
		form.setTransientFailureAction((long)4);
		form.setPermanentFailureAction((long)4);
		form.setTimeOutAction((long)4);
		form.setProtocolFailureArguments("0.0.0.0");
		form.setTransientFailureArguments("0.0.0.0");
		form.setPermanentFailureArguments("0.0.0.0");
		form.setTimeOutArguments("0.0.0.0");*/
		form.setDefaultFailureAction("4");
		form.setDefaultFailureArgument("0.0.0.0");
		form.setStatefulRouting(1L);
		form.setTransactionTimeout(3000L);
		form.setDefaultFailureActionMap();
	}
}
