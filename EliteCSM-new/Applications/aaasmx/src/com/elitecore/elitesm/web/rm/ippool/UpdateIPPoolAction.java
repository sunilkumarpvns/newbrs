package com.elitecore.elitesm.web.rm.ippool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.blmanager.rm.ippool.IPPoolBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolDetailData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

public class UpdateIPPoolAction extends IPPoolBaseAction{
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_IP_POOL_ACTION;
	//private static final String STATUS_CHANGE_ACTION=ConfigConstant.CHANGE_IP_POOL_STATUS_ACTION;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logInfo(MODULE, "Enter the execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)) {
			try {
				IPPoolForm ipPoolForm = (IPPoolForm) form;
				
				String currentUser = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
				IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				IIPPoolData ipData = ipPoolBLManager.getIPPoolById(ipPoolForm.getIpPoolId());
				
				if ("changeStatus".equals(ipPoolForm.getAction())) {
					String status = (BaseConstant.SHOW_STATUS_ID.equalsIgnoreCase(ipPoolForm.getStatus())) ? BaseConstant.HIDE_STATUS_ID : BaseConstant.SHOW_STATUS_ID;
					
					staffData.setAuditName(ipPoolForm.getName());
					staffData.setAuditId(ipPoolForm.getAuditUId());
					
					ipPoolBLManager.updateStatus(ipPoolForm.getIpPoolId(), status,staffData,ACTION_ALIAS);
				} else {
					
					IIPPoolData ipPoolData = convertFormToBean(ipPoolForm,ipData);
					ipPoolData.setIpPoolId(ipPoolForm.getIpPoolId());
					ipPoolData.setCreateDate(ipPoolForm.getCreateDate());
					ipPoolData.setCreatedByStaffId(ipPoolForm.getCreatedByStaffId());
					ipPoolData.setStatusChangedDate(ipPoolForm.getStatusChangedDate());
					ipPoolData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
					ipPoolData.setLastModifiedByStaffId(currentUser);
					ipPoolData.setName(ipPoolForm.getName());
					ipPoolData.setAuditUId(ipPoolData.getAuditUId());
					ipPoolData.setDescription(ipPoolForm.getDescription());
					ipPoolData.setNasIPAddress(ipPoolForm.getNasIPAddress());
					ipPoolData.setSystemGenerated("N");
					ipPoolData.setEditable("Y");
					ipPoolData.setAdditionalAttributes(ipPoolForm.getAdditionalAttributes());
					ipPoolData.setCommonStatusId(ipPoolForm.getActiveStatus()!=null?BaseConstant.SHOW_STATUS_ID:BaseConstant.HIDE_STATUS_ID);
					
					/*set IP Pool Details*/
					boolean isDelete = false;
					if(isFileUpload(ipPoolForm.getFileUpload())){
						FormFile formFile = ipPoolForm.getFileUpload();
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(formFile.getInputStream()));
						ipPoolData.setIpPoolDetail(ipPoolBLManager.getIPPoolDetailByCSVFile(formFile.getFileName(),bufferedReader));
						isDelete = true;
					}else{
						ipPoolData.setIpPoolDetail(ipPoolBLManager.getIPPoolDetailByIPRange(ipPoolForm.getIpAddressRanges(), ipPoolForm.getRangeId(), request.getParameterValues("oldIPAddressRange"), request.getParameterValues("oldRangeId")));
						ipPoolData.setIpPoolDetailSet(getIPPoolDetails(ipPoolForm.getIpAddressRanges(), ipPoolForm.getRangeId(), request.getParameterValues("oldIPAddressRange"), request.getParameterValues("oldRangeId"), ipData.getIpPoolDetailSet()));
					}
					
					staffData.setAuditName(ipPoolData.getName());
					staffData.setAuditId(ipPoolData.getAuditUId());
					
					ipPoolBLManager.update(ipPoolData, isDelete,staffData,ACTION_ALIAS);
				}

				request.setAttribute("responseUrl","/viewIPPool.do?ipPoolId="+ipPoolForm.getIpPoolId());
				ActionMessage message = new ActionMessage("ippool.update.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveMessages(request,messages);
				return mapping.findForward(SUCCESS);
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
		} else {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	private Set<IPPoolDetailData> getIPPoolDetails(String[] ipAddressRanges, String[] rangeId, String[] parameterValues, String[] parameterValues2, Set<IPPoolDetailData> ipPoolDetailSet) {
		Set<IPPoolDetailData> ipPoolDetailDataSet = new LinkedHashSet<IPPoolDetailData>();
		
		for(IPPoolDetailData ipPoolDetailData : ipPoolDetailSet){
			ipPoolDetailDataSet.add(ipPoolDetailData);
		}
		
		if( ipAddressRanges != null && rangeId != null ){
			for(int i=0 ;i < ipAddressRanges.length ;i++){
				IPPoolDetailData ipPoolDetailData = new IPPoolDetailData();
				ipPoolDetailData.setIpAddressRange(ipAddressRanges[i]);
				ipPoolDetailData.setIpAddressRangeId(rangeId[i]);
				ipPoolDetailDataSet.add(ipPoolDetailData);
			}
		}
		return ipPoolDetailDataSet;
	}

}
