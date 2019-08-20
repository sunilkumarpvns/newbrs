package com.elitecore.elitesm.web.rm.ippool;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.rm.ippool.IPPoolBLManager;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

public class DeleteIPPoolDetailByRange extends BaseWebAction {

		@Override
		public ActionForward execute(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			PrintWriter out = response.getWriter();
			try{
				Logger.logInfo(MODULE, "Inside execute method.");
				IPPoolForm ipPoolForm = (IPPoolForm) form;
				IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
				String[] ipAddresRanges = ipPoolForm.getIpAddressRanges();
				if(ipAddresRanges != null && ipAddresRanges.length > 0){
					ipPoolBLManager.deleteIPPoolDetailByRange(ipPoolForm.getIpPoolId(), ipAddresRanges[0]);
				}
				
			}catch (Exception e) {
				Logger.logError(MODULE, "error occurs, reason:"+e.getMessage());
				Logger.logTrace(MODULE, e);
				/* Handle By Ajax Response */
				out.write(e.getMessage());
			}	
			return null;
		}
}
