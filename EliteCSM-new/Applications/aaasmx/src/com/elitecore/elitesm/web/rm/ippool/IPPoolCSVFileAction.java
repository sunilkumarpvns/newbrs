package com.elitecore.elitesm.web.rm.ippool;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.util.constants.IPPoolConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

public class IPPoolCSVFileAction extends BaseWebAction{
	
	private static final String IPPOOL_CSV_FORWARD = "ipPoolCSVFormatFile";
	private static final String ACTION_ALIAS = "CREATE_IP_POOL_ACTION";
	private static final String[] csvSmapleData = {"192.168.13.180","192.168.13.181","192.168.13.182","192.168.13.183","192.168.13.184"}; 
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logInfo(MODULE, "Enter the execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)) {
			try{
				IPPoolForm ipPoolForm = (IPPoolForm) form;
				if(ipPoolForm.getAction() == null) {
					request.setAttribute("csvFileData", csvSmapleData);
					return mapping.findForward(IPPOOL_CSV_FORWARD);
				}else{
					response.setContentType("application/octet-stream");
				    response.setHeader("Content-Disposition","attachment;filename=UploadIPPool.csv");
				    ServletOutputStream out = response.getOutputStream();
				    StringBuilder csvFormatFileData = new StringBuilder(IPPoolConstant.CSV_FILE_HEADERROW.length()+80);
				    csvFormatFileData.append(IPPoolConstant.CSV_FILE_HEADERROW).append(System.getProperty("line.separator"));
				    for(int i=0;i<csvSmapleData.length;i++){
				    	csvFormatFileData.append(i+1).append(",").append(csvSmapleData[i]);
				    	if((i+1)<csvSmapleData.length){
				    		csvFormatFileData.append(System.getProperty("line.separator"));
				    	}
				    }
				    out.write(csvFormatFileData.toString().getBytes());
				    out.flush();
				    out.close();
					return null;
				}
				
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
