/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SearchASMAction.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.diameter.sessionmanager; 
  
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.sessionmanager.form.SearchDiameterASMForm;
import com.google.gson.Gson;
                                                                               
public class ViewDiameterASMAction extends BaseWebAction { 
	
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SESSION;
	private static final String MODULE ="DownloadDiameterASMAction";
	 
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){	
			try{
				
				SearchDiameterASMForm searchDiameterASMForm = (SearchDiameterASMForm) form;
				String strActiveSessionId = request.getParameter("sessionId");
				String tablename = request.getParameter("tablename");
				
				Logger.logDebug(MODULE,"sessionId : "+strActiveSessionId);
				
				String activeSessionId="";
				if(Strings.isNullOrBlank(strActiveSessionId) == false){
					activeSessionId = strActiveSessionId;
				}else{
					activeSessionId = searchDiameterASMForm.getSessionManagerId();
				}
				
				searchDiameterASMForm.setSessionManagerId(activeSessionId);
				
				DiameterSessionManagerBLManager blManager = new DiameterSessionManagerBLManager();
				
				List customPageList  =  blManager.getASMDataByColumnName(activeSessionId,tablename);
				
				PrintWriter out = response.getWriter();
				String json = new Gson().toJson(customPageList);
				System.out.println(json);
				out.println(json);
				
				return null;
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
				Logger.logTrace(MODULE, managerExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
		        ActionMessage message = new ActionMessage("search.asm.operation.failure");
		        ActionMessages messages = new ActionMessages();
		        messages.add("information", message);
		        saveErrors(request, messages);

			}catch(Exception managerExp){
				Logger.logError(MODULE,"Error during operation, reason : "+ managerExp.getMessage());
				Logger.logTrace(MODULE, managerExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);
		        ActionMessage message = new ActionMessage("search.asm.operation.failure");
		        ActionMessages messages = new ActionMessages();
		        messages.add("information", message);
		        saveErrors(request, messages);
				
			}
			return mapping.findForward(FAILURE_FORWARD);
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