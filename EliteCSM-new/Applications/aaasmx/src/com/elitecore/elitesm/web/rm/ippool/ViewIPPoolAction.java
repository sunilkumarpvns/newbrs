package com.elitecore.elitesm.web.rm.ippool;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.rm.ippool.IPPoolBLManager;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolDetailData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

public class ViewIPPoolAction  extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewIPPoolDetail";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_IP_POOL_ACTION;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logInfo(MODULE, "Enter execute method of "+getClass().getName());
			try{
				IPPoolForm ipPoolForm = (IPPoolForm) form;
				IPPoolBLManager blManager = new IPPoolBLManager();

				String ipPoolId = ipPoolForm.getIpPoolId();
				
				IIPPoolData ipPoolData = blManager.getIPPoolById(ipPoolId);
				
				List<IIPPoolDetailData> lstIPPoolDetail = null;
				
				if("initUpdate".equals(ipPoolForm.getAction()) ){
					convertBeantoForm(ipPoolForm, ipPoolData);
					IIPPoolDetailData ipPoolDetailData = new IPPoolDetailData();
					ipPoolDetailData.setIpPoolId(ipPoolId);
					lstIPPoolDetail = blManager.getDistinctIPPoolDetailByRangeList(ipPoolDetailData);
				} else if("changeStatus".equals(ipPoolForm.getAction())){
					convertBeantoForm(ipPoolForm, ipPoolData);
					ipPoolForm.setStatus(ipPoolData.getCommonStatusId());
				}else {
					/*case of view page 
					  provide pagination for IP Pool Details*/
					IIPPoolDetailData ipPoolDetailData = new IPPoolDetailData();
					ipPoolDetailData.setIpPoolId(ipPoolId);
					long totalItems = blManager.getIPPoolDetailCount(ipPoolDetailData);
					int pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
					ipPoolForm.setPageNumber(ipPoolForm.getPageNumber());
					long pageNo = ipPoolForm.getPageNumber() == 0 ? 1 : ipPoolForm.getPageNumber();
					long totalPages = (long) Math.ceil(totalItems / pageSize);
					if(totalItems % pageSize == 0)
						totalPages-=1;
					lstIPPoolDetail =blManager.getIPoolDetailDataList(ipPoolDetailData, (int)pageNo, pageSize);
					ipPoolForm.setTotalPages(totalPages);
					ipPoolForm.setTotalRecords(totalItems);
					ipPoolForm.setPageNumber(pageNo);
					ipPoolForm.setIpPoolId(ipPoolId);
					
					/*int firstResult = (int) ((pageNo - 1) * pageSize);
					List<IPPoolDetailData> paginationIPPoolDetailList = new ArrayList<IPPoolDetailData>(pageSize);
					int records = (firstResult+pageSize) < totalItems ? (firstResult+pageSize) : (int)totalItems;  
					for(int index = firstResult ; index < records ; index++){
						paginationIPPoolDetailList.add(lstIPPoolDetail.get(index));
					}
					lstIPPoolDetail = paginationIPPoolDetailList;*/
				}
				request.setAttribute("ipPoolData", ipPoolData);
				request.setAttribute("lstIPPoolDetail", lstIPPoolDetail);
				
				return mapping.findForward(VIEW_FORWARD);
			}catch (Exception e) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);
			}
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	private void convertBeantoForm(IPPoolForm ipPoolForm, IIPPoolData ipPoolData){
		ipPoolForm.setIpPoolId(ipPoolData.getIpPoolId());
		ipPoolForm.setName(ipPoolData.getName());
		ipPoolForm.setDescription(ipPoolData.getDescription());
		ipPoolForm.setNasIPAddress(ipPoolData.getNasIPAddress());
		ipPoolForm.setRuleSet(ipPoolData.getRuleSet());
		ipPoolForm.setAdditionalAttributes(ipPoolData.getAdditionalAttributes());
		ipPoolForm.setInputMode(IPPoolForm.NO_OF_IP_ADDRESS);
		ipPoolForm.setActiveStatus(ipPoolData.getCommonStatusId());
		ipPoolForm.setCreateDate(ipPoolData.getCreateDate());
		ipPoolForm.setCreatedByStaffId(ipPoolData.getCreatedByStaffId());
		ipPoolForm.setStatusChangedDate(ipPoolData.getStatusChangedDate());
		ipPoolForm.setAuditUId(ipPoolData.getAuditUId());
	}
	
}
