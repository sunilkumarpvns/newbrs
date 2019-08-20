package com.elitecore.netvertexsm.web.servermgr.drivers.csvdriver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.drivers.csvdriver.form.CSVDriverForm;

public class InitCreateCSVDriverAction extends BaseWebAction{
	private static final String FAILURE_FORWARD = "failure";
	private static final String CREATE_FORWARD = "createCSVDriver";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				CSVDriverForm csvDriverForm = (CSVDriverForm) form;
				setDefaultValues(csvDriverForm);              			
				
				String [] headerArray = new String[0];						               
				String [] pcrfKeyArray = new String[0];				
				
				request.setAttribute("headerArray", headerArray);
				request.setAttribute("pcrfKeyArray",pcrfKeyArray);
				
				return mapping.findForward(CREATE_FORWARD);
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
			}
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("driver.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);

			return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("driver.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	private void setDefaultValues(CSVDriverForm form){
		
		form.setAllocatingProtocol("LOCAL");
		form.setAddress("127.0.0.1:22");
		form.setPostOperation("rename");
		form.setArchiveLocation("data/csvfiles/archive");
		form.setFailOvertime(3L);
		form.setFileName("CDRs.csv");
		form.setLocation("data/csvfiles");
		form.setFileRollingType(0L);
		form.setCreateBlankFile("false");
		form.setRollingUnit(1L);
		//form.setPrefixFileName("0:4");
		//form.setFolderName("0:4");
		form.setUseDictionaryValue("false");
		form.setAvpairSeparator("=");
		form.setCdrTimestampFormat("EEE dd MMM,yyyy,hh:mm:ss aaa");
		form.setDelimiter(",");
		form.setSequencePosition("suffix");
		form.setGlobalization("false");
	}
}