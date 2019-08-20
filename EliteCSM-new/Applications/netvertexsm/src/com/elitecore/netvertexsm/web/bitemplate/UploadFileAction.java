package com.elitecore.netvertexsm.web.bitemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.blmanager.bitemplate.BITemplateBLManager;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BISubKeyData;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.bitemplate.form.BITemplateForm;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class UploadFileAction extends BaseWebAction {
	private static final String SUCCESS_FORWARD = "success";
	private static final String FORWARD = "uploadFilePage";
	private static final String FAILURE_FORWARD = "failure";	
	private static final String ACTION_ALIAS = ConfigConstant.UPLOAD_BICEA_CSV;
	private static final String MODULE = "BI-TEMPLATE";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			BITemplateForm biForm = null;
			try{
				biForm = (BITemplateForm) form;
				BITemplateBLManager biBLManager = new BITemplateBLManager();
				long id; 
				if(biForm.getBiId() > 0)
					id = biForm.getBiId();
				else
					id = Long.parseLong(request.getParameter("id"));
				
				if(request.getParameter("type")!=null && request.getParameter("type").equals("load")) {
					BITemplateData biData = new BITemplateData();
					
					biData.setId(id);
					biData = biBLManager.getBITemplateData(biData);
					
					biForm.setBiId(id);
					biForm.setDescription(biData.getDescription());
					biForm.setBikey(biData.getKey());
					biForm.setName(biData.getName());
					request.setAttribute("biTemplateForm", biForm);
					
					return mapping.findForward(FORWARD);
				}
				
				List<BISubKeyData> keyValueList = new ArrayList<BISubKeyData>();				
				FormFile file = biForm.getCsvFile();
				
				if(file.getContentType().toLowerCase().contains("csv") || file.getContentType().toLowerCase().contains("excel") || file.getFileName().toLowerCase().endsWith(".csv")){
					BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
					//reader.readLine();
					while(reader.ready()) {
						BISubKeyData subKeyData = new BISubKeyData();
						subKeyData.setBiTemplateId(id);
						String[] keyValues = reader.readLine().split(",");
						if(keyValues.length==2){
							subKeyData.setKey(keyValues[0]);
							subKeyData.setValue(keyValues[1]);
						keyValueList.add(subKeyData);
					}
					}
				}else{
					Logger.logInfo(MODULE,"Choosen file must be in .csv formt.");
					ActionMessages messages = new ActionMessages();
		            messages.add("information", new ActionMessage("bitemplate.update.invalidcsv.failure"));
		            saveErrors(request, messages);
		            return mapping.findForward(FAILURE_FORWARD);
				}
				
				List<BISubKeyData> bikeyValueList = biBLManager.getBISubKeyList(id);
				if(bikeyValueList==null){
					bikeyValueList=new ArrayList<BISubKeyData>();
				}
				//	Update existing keys
				
				if(keyValueList != null && keyValueList.size() > 0){
					for(BISubKeyData biKVData : keyValueList){
						boolean isFound = false;
						for(BISubKeyData data : bikeyValueList){
							if(biKVData.getKey().equalsIgnoreCase(data.getKey())) {								
								isFound = true;					
								if(biKVData.getValue()!=null && biKVData.getValue().trim().length()>0){
									data.setKey(biKVData.getKey().trim());
									data.setValue(biKVData.getValue().trim());
							}
								//break;
					}
				}
						if(!isFound && biKVData.getKey()!=null && biKVData.getValue()!=null){
							if(biKVData.getKey().trim().length()>0 && biKVData.getValue().trim().length()>0)
								bikeyValueList.add(biKVData);							
						}
					}
				}
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				biBLManager.uploadFile(bikeyValueList, staffData,ACTION_ALIAS);
                
               	ActionMessage message = new ActionMessage("bitemplate.upload.success",biForm.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/searchBITemplate.do");
				return mapping.findForward(SUCCESS_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("bitemplate.update.failure",biForm.getName()));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("general.upload.error.heading","uploading");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);	            
	            return mapping.findForward(FAILURE_FORWARD);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("general.upload.error.heading","uploading");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);		        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
