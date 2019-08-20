package com.elitecore.elitesm.web.diameter.subscriberroutingtable;

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
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.diameter.subscriberroutingtable.form.SubscriberRoutingTableForm;

public class SearchSubscriberAction extends BaseWebAction{
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SEARCH DIAMETER SESSION ACTION";
	private static final String SEARCH_FORWARD = "searchSubscriberRoutingTable";
	private static final String SEARCH_IMSI_ACTION_ALIAS = ConfigConstant.SEARCH_IMSI_BASED_ROUTING_TABLE;
	private static final String SEARCH_MSISDN_ACTION_ALIAS= ConfigConstant.SEARCH_MSISDN_BASED_ROUTING_TABLE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		Logger.logInfo(MODULE, "Enter in execute method of : "+ getClass());
		try {
			
			SubscriberRoutingTableForm subscriberRoutingTableForm = new SubscriberRoutingTableForm();
			String subscriberDetails = null, searchMode = null ;
			subscriberDetails = request.getParameter("subscriber");
			searchMode = request.getParameter("searchMode");
			
			if(subscriberDetails != null){
				subscriberDetails = subscriberDetails.trim();
			}else{
				subscriberDetails = "";
			}
			
			if( searchMode != null ){
				if( searchMode.equals("IMSI") ){
					
					checkActionPermission(request, SEARCH_IMSI_ACTION_ALIAS);
					
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
					IMSIBasedRoutingTableBLManager blManager = new IMSIBasedRoutingTableBLManager();
					
					Map infoMap= new HashedMap();
					infoMap.put("pageNo", requiredImsiPageNo);
					infoMap.put("pageSize", pageSize);
					    
					PageList pageList = blManager.searchSubscriberBasedOnImsi(subscriberDetails,infoMap); 
					    
					subscriberRoutingTableForm.setImsiMappingPageNumber(pageList.getCurrentPage());
					subscriberRoutingTableForm.setImsiMappingTotalPages(pageList.getTotalPages());
					subscriberRoutingTableForm.setImsiMappingTotalRecords(pageList.getTotalItems());
					subscriberRoutingTableForm.setImsiMappingDataList(pageList.getListData());
					subscriberRoutingTableForm.setAction(BaseConstant.LISTACTION);
					subscriberRoutingTableForm.setSubscriberDetails(subscriberDetails);
					subscriberRoutingTableForm.setSearchRange(true);
					subscriberRoutingTableForm.setSearchImsi(true);
					    
					 request.setAttribute("subscriberRoutingTableForm", subscriberRoutingTableForm);
					return mapping.findForward(SEARCH_FORWARD);
					
				}else if( searchMode.equals("MSISDN")){
					
					checkActionPermission(request, SEARCH_MSISDN_ACTION_ALIAS);
					
					Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
					int requiredMsisdnPageNo;
					if(request.getParameter("pageNo") != null){
						requiredMsisdnPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
					}else{
						requiredMsisdnPageNo = new Long(subscriberRoutingTableForm.getMsisdnPageNumber()).intValue();
					}
					
					if(requiredMsisdnPageNo == 0)
						requiredMsisdnPageNo = 1;
						
					/** MSISDN Based Routing table */
					MSISDNBasedRoutingTableBLManager msisdnBasedRoutingTableBLManager = new MSISDNBasedRoutingTableBLManager();
						
					Map infoMap= new HashedMap();
					infoMap.put("pageNo", requiredMsisdnPageNo);
				    infoMap.put("pageSize", pageSize);
				    
				    PageList pageList = msisdnBasedRoutingTableBLManager.searchSubscriberBasedOnMsisdn(subscriberDetails,infoMap); 
					
				    subscriberRoutingTableForm.setMsisdnMappingPageNumber(pageList.getCurrentPage());
				    subscriberRoutingTableForm.setMsisdnMappingTotalPages(pageList.getTotalPages());
				    subscriberRoutingTableForm.setMsisdnMappingTotalRecords(pageList.getTotalItems());
				    subscriberRoutingTableForm.setMsisdnMappingDataList(pageList.getListData());
				    subscriberRoutingTableForm.setAction(BaseConstant.LISTACTION);
				    subscriberRoutingTableForm.setSubscriberDetails(subscriberDetails);
				    subscriberRoutingTableForm.setSearchRange(true);
				    subscriberRoutingTableForm.setSearchMsisdn(true);
				    
				    request.setAttribute("subscriberRoutingTableForm", subscriberRoutingTableForm);
					return mapping.findForward(SEARCH_FORWARD);
				}
			}
			return mapping.findForward(SEARCH_FORWARD);
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
}
