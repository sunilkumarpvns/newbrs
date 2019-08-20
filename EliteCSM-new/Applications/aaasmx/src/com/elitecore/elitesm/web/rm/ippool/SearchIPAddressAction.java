package com.elitecore.elitesm.web.rm.ippool;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.rm.ippool.IPPoolBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolDetailData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

public class SearchIPAddressAction extends BaseWebAction {
	private static final String ACTION_ALIAS =ConfigConstant.SEARCH_IP_ADDRESS;
	private static final String SEARCH_FORWARD = "searchIPAddress";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logInfo(MODULE, "Enter execute method of "+getClass().getName());
			try{
				IPPoolForm ipPoolForm = (IPPoolForm) form;
				IPPoolBLManager blManager = new IPPoolBLManager();
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				IIPPoolData ipPoolData = blManager.getIPPoolById(ipPoolForm.getIpPoolId());
				
				request.setAttribute("ipPoolData", ipPoolData);
				
				if("search".equals(ipPoolForm.getAction())){
					IIPPoolDetailData ipPoolDetailData = new IPPoolDetailData();
					ipPoolDetailData.setIpPoolId(ipPoolForm.getIpPoolId());
					ipPoolDetailData.setIpAddress(ipPoolForm.getIpAddress());
					
					// pagination code
					long totalItems = blManager.getIPPoolDetailCount(ipPoolDetailData);
					int pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
					ipPoolForm.setPageNumber(ipPoolForm.getPageNumber());
					long pageNo = ipPoolForm.getPageNumber() == 0 ? 1 : ipPoolForm.getPageNumber();
					long totalPages = (long) Math.ceil(totalItems / pageSize);
					if(totalItems % pageSize == 0)
						totalPages-=1;
					List<IIPPoolDetailData> lstIPPoolDetail = blManager.getIPoolDetailDataList(ipPoolDetailData, (int)pageNo, pageSize);
					ipPoolForm.setTotalPages(totalPages);
					ipPoolForm.setTotalRecords(totalItems);
					ipPoolForm.setPageNumber(pageNo);
					doAuditing(staffData, ACTION_ALIAS);
					request.setAttribute("lstIPPoolDetail", lstIPPoolDetail);
				}else if("delete".equals(ipPoolForm.getAction())){
					String[] strSelectedIds = request.getParameterValues("select");
					blManager.deleteIPPoolDetailByIPAddress(Arrays.asList(strSelectedIds),ipPoolForm.getIpPoolId(),staffData);
					long currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIds.length,ipPoolForm.getPageNumber(),ipPoolForm.getTotalPages(),ipPoolForm.getTotalRecords());
					request.setAttribute("responseUrl","/searchIPAddress.do?action=search&ipPoolId="+ipPoolForm.getIpPoolId()+"&pageNumber="+currentPageNumber+"&ipAddress="+ipPoolForm.getIpAddress());
					ActionMessage message = new ActionMessage("rm.ipaddress.delete.success");
					ActionMessages messages1 = new ActionMessages();
					messages1.add("information",message);
					saveMessages(request,messages1);
					return mapping.findForward(SUCCESS);
				}
				return mapping.findForward(SEARCH_FORWARD);
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

}	
	