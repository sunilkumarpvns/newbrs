package com.elitecore.elitesm.web.rm.ippool;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.rm.ippool.IPPoolBLManager;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolDetailData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

public class ViewIPPoolCheckAction extends BaseWebAction {
	private static final String VIEW_IP_CHECK_FORWARD = "viewIPCheck";

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try{
			Logger.logInfo(MODULE, "Inside execute method.");
			IPPoolForm ipPoolForm = (IPPoolForm) form;
			if(ipPoolForm.getIpAddress() != null && ipPoolForm.getIpAddress().trim().length() > 0) {
				IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
				IIPPoolDetailData ipPoolDetailData = new IPPoolDetailData();
				ipPoolDetailData.setIpAddress(ipPoolForm.getIpAddress());
				ipPoolDetailData.setIpPoolId(ipPoolForm.getIpPoolId());
				String nasIPAddress = ("null".equals(ipPoolForm.getNasIPAddress())) ? null : ipPoolForm.getNasIPAddress();
				ipPoolDetailData.setNasIPAddress(nasIPAddress);
				List<IPPoolData> ipPoolDataList = ipPoolBLManager.getIPPoolData(ipPoolDetailData);
				request.setAttribute("ipPoolDataList", ipPoolDataList);
			}
			return mapping.findForward(VIEW_IP_CHECK_FORWARD);
		}catch (Exception e) {
			Logger.logError(MODULE, "error occurs, reason:"+e.getMessage());
			Logger.logTrace(MODULE, e);
			return mapping.findForward(FAILURE);
		}
	}
}
