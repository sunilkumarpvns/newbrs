package com.elitecore.elitesm.web.rm.ippool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.rm.ippool.IPPoolBLManager;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolDetailData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

public class CheckIPPoolAction extends IPPoolBaseAction {
	private static final String MODULE = "CheckIPPoolAction";
	private static final String CHECK_IP_FORWARD = "checkIPPool";
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		try{
			Logger.logInfo(MODULE, "Inside execute method.");
			IPPoolForm ipPoolForm = (IPPoolForm) form;
			IPPoolBLManager ipPoolBLManager = new IPPoolBLManager();
			String nasIPAddress = ("null".equals(ipPoolForm.getNasIPAddress())) ? null : ipPoolForm.getNasIPAddress();
			String ipPoolId = null;
			if( Strings.isNullOrEmpty(ipPoolForm.getIpPoolId()) == false){
				ipPoolId = ipPoolForm.getIpPoolId();
			}

			List<IPPoolDetailData> ipPoolDetailDataList;
			/*set IP Pool Details*/
			if(isFileUpload(ipPoolForm.getFileUpload())){
				FormFile formFile = ipPoolForm.getFileUpload();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(formFile.getInputStream()));
				ipPoolDetailDataList = new ArrayList<IPPoolDetailData>(ipPoolBLManager.getIPPoolDetailByCSVFile(formFile.getFileName(),bufferedReader));
			}else{
				ipPoolDetailDataList = new ArrayList<IPPoolDetailData>(ipPoolBLManager.getIPPoolDetailByIPRange(ipPoolForm.getIpAddressRanges(), ipPoolForm.getRangeId(), null, null));
			}
			long totalItems = ipPoolDetailDataList.size();
			int pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			ipPoolForm.setPageNumber(ipPoolForm.getPageNumber());
			long pageNo = ipPoolForm.getPageNumber() == 0 ? 1 : ipPoolForm.getPageNumber();
			long totalPages = (long) Math.ceil(totalItems / pageSize);
			if(totalItems % pageSize == 0)
				totalPages-=1;

			int firstResult = (int) ((pageNo - 1) * pageSize);
			List<String> paginationIPPoolDetailList = new ArrayList<String>(pageSize);
			int records = (firstResult+pageSize) < totalItems ? (firstResult+pageSize) : (int)totalItems;  
			for(int index = firstResult ; index < records ; index++){
				paginationIPPoolDetailList.add(ipPoolDetailDataList.get(index).getIpAddress());
			}
			Map<String, Integer> ipPoolCheckAddressMap = ipPoolBLManager.getIPPoolCheckAddressMap(paginationIPPoolDetailList, nasIPAddress, ipPoolId);
			ipPoolForm.setTotalPages(totalPages);
			ipPoolForm.setTotalRecords(totalItems);
			ipPoolForm.setPageNumber(pageNo);
			request.setAttribute("ipPoolCheckAddressMap", ipPoolCheckAddressMap);
			return mapping.findForward(CHECK_IP_FORWARD);
		}catch (Exception e) {
			Logger.logError(MODULE, "error occurs, reason:"+e.getMessage());
			Logger.logTrace(MODULE, e);
			/* Handle By Ajax Response */
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}
	}
	
	
}
