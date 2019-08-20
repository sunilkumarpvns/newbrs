package com.elitecore.elitesm.web.diameter.subscriberroutingtable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.diameter.subscriberroutingtable.form.SubscriberRoutingTableForm;

public class SubscriberRoutingTableAction extends BaseDispatchAction { 

	private static final String MODULE = "SUBSCRIBER_ROUTING_TABLE";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String SEARCH_FORWARD = "searchSubscriberRoutingTable";
	private static final String INIT_CRETAE_IMSI_FORWARD="initCreateIMSIRoutingTable";
	private static final String INIT_CRETAE_MSISDN_FORWARD="initCreateMSISDNRoutingTable";
	private static final String DELETE_IMSI_ALIAS=ConfigConstant.DELETE_IMSI_BASED_ROUTING_TABLE;
	private static final String DELETE_MSISDN_ALIAS=ConfigConstant.DELETE_MSISDN_BASED_ROUTING_TABLE;
	private static final String SEARCH_SUBSCRIBER_ROUTING_TABLE_ALIAS = ConfigConstant.SEARCH_SUBSCRIBER_ROUTING_TABLE;
	
	public ActionForward initSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE, "Entered initSearch method of " + getClass().getName());
		return searchSubscriberRoutingTable(mapping, form, request, response);
	}
	
	private ActionForward searchSubscriberRoutingTable(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE, "Enter in search Subscriber table action");
		try {
			checkActionPermission(request, SEARCH_SUBSCRIBER_ROUTING_TABLE_ALIAS);
			SubscriberRoutingTableForm subscriberRoutingTableForm = new SubscriberRoutingTableForm();
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredImsiPageNo;
			if(request.getParameter("pageNo") != null){
				requiredImsiPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredImsiPageNo = new Long(subscriberRoutingTableForm.getImsiPageNumber()).intValue();
			}
			if(requiredImsiPageNo == 0)
				requiredImsiPageNo = 1;
			
			/** IMSI Based Routing table */
			IMSIBasedRoutingTableData imsiBasedRoutingTable = new  IMSIBasedRoutingTableData();
			IMSIBasedRoutingTableBLManager blManager = new IMSIBasedRoutingTableBLManager();
			
			String strRoutingTableName = subscriberRoutingTableForm.getSubscriberDetails();
			if(strRoutingTableName!=null)
				imsiBasedRoutingTable.setRoutingTableName(strRoutingTableName);
			else
				imsiBasedRoutingTable.setRoutingTableName("");
			
			Map infoMap= new HashedMap();
		    infoMap.put("pageNo", requiredImsiPageNo);
		    infoMap.put("pageSize", pageSize);
		    
		    List<IMSIBasedRoutingTableData> imsiBasedRoutingTableList = blManager.searchImsiBasedRoutingTable(); 
			
		    subscriberRoutingTableForm.setListIMSIBasedRoutingTable(imsiBasedRoutingTableList);
		    subscriberRoutingTableForm.setAction(BaseConstant.LISTACTION);
			
			/** MSISDN Based Routing table */
		    MSISDNBasedRoutingTableBLManager msisdnBasedRoutingTableBLManager = new MSISDNBasedRoutingTableBLManager();
		    
			int requiredMsisdnPageNo;
			if(request.getParameter("pageNo") != null){
				requiredMsisdnPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredMsisdnPageNo = new Long(subscriberRoutingTableForm.getMsisdnPageNumber()).intValue();
			}
			if(requiredMsisdnPageNo == 0)
				requiredMsisdnPageNo =1;
			
			MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = new MSISDNBasedRoutingTableData();
			String strMsisdnRoutingTableName = subscriberRoutingTableForm.getMsisdnBasedRoutingTableName();
			if(strMsisdnRoutingTableName!=null)
				msisdnBasedRoutingTableData.setRoutingTableName(strMsisdnRoutingTableName);
			else
				msisdnBasedRoutingTableData.setRoutingTableName("");
			
		    List<MSISDNBasedRoutingTableData> msisdnBasedRoutingTableDataList = msisdnBasedRoutingTableBLManager.searchMSISDNBasedRoutingTable(); 
		    subscriberRoutingTableForm.setListMSISDNBasedRoutingTable(msisdnBasedRoutingTableDataList);
		    subscriberRoutingTableForm.setAction(BaseConstant.LISTACTION);
		    
			request.setAttribute("subscriberRoutingTableForm", subscriberRoutingTableForm);
			return mapping.findForward(SEARCH_FORWARD);
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		} catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("subscriberroutingtable.search.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE, "Enter in delete method of Subscriber table");
		try {
			SubscriberRoutingTableForm subscriberRoutingTableForm = new SubscriberRoutingTableForm();
			String deleteParam = request.getParameter("deleteParam");
			if( deleteParam != null ){
				if( deleteParam.equals("IMSI") ){
					
					checkActionPermission(request, DELETE_IMSI_ALIAS);
					Logger.logInfo(MODULE, "enter in IMSI delete method");
					IMSIBasedRoutingTableBLManager blManager = new  IMSIBasedRoutingTableBLManager();
					String[] strSelectedIds = request.getParameterValues("imsiselect");
					List<String> listSelectedIDs = new ArrayList<String>();
					  if(strSelectedIds != null){
						 for(int i=0;i<strSelectedIds.length;i++){
							listSelectedIDs.add(strSelectedIds[i]);
						 }
					 }
					  
					 blManager.deleteRoutingTable(listSelectedIDs);
					 request.setAttribute("responseUrl", "/searchSubscriberRoutingTable.do?method=initSearch");
					 ActionMessage message = new ActionMessage("imsibased.routingtable.delete.success");
					 ActionMessages messages = new ActionMessages();
					 messages.add("information",message);
					 saveMessages(request, messages);
					 return mapping.findForward(SUCCESS_FORWARD); 
				}else if( deleteParam.equals("MSISDN")){
					Logger.logInfo(MODULE, "enter in MSISDN delete method");
					
					checkActionPermission(request, DELETE_MSISDN_ALIAS);
					MSISDNBasedRoutingTableBLManager blManager = new  MSISDNBasedRoutingTableBLManager();
					String[] strSelectedIds = request.getParameterValues("msisdnselect");
					 List<String> listSelectedIDs = new ArrayList<String>();
					  if(strSelectedIds != null){
						 for(int i=0;i<strSelectedIds.length;i++){
							listSelectedIDs.add(strSelectedIds[i]);
						 }
					 } 
					blManager.deleteRoutingTable(listSelectedIDs);
					request.setAttribute("responseUrl", "/searchSubscriberRoutingTable.do?method=initSearch");
					ActionMessage message = new ActionMessage("msisdnbased.routingtable.delete.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS_FORWARD);
				}
			}
		    
			request.setAttribute("subscriberRoutingTableForm", subscriberRoutingTableForm);
			return mapping.findForward(SEARCH_FORWARD);
		}catch(ActionNotPermitedException e){
				Logger.logError(MODULE,"Error :-" + e.getMessage());
				printPermitedActionAlias(request);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("general.user.restricted"));
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(Exception exp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
			ActionMessage message = new ActionMessage("subscriberroutingtable.delete.failure");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages); 
			return mapping.findForward(FAILURE_FORWARD); 
		}
	}
	
	public ActionForward initCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
		Logger.logInfo(MODULE, "Entered initCreate method of " + getClass().getName());
		String subscriberMode = request.getParameter("subscriberMode");
		
		if( subscriberMode != null ){
			if( subscriberMode.equals("IMSI")){
				return mapping.findForward(INIT_CRETAE_IMSI_FORWARD);
			}else if( subscriberMode.equals("MSISDN")){
				return mapping.findForward(INIT_CRETAE_MSISDN_FORWARD);
			}
		}
		}catch( Exception exp ){
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("subscriberroutingtable.search.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
}