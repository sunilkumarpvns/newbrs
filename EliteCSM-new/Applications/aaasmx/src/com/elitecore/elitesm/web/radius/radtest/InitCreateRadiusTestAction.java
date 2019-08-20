/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   InitCreateNtradAction.java                 		
 * ModualName radius    			      		
 * Created on 2 April, 2008
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.radius.radtest; 
  
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.radtest.forms.CreateRadiusTestForm;
                                                                               
public class InitCreateRadiusTestAction extends BaseWebAction { 
	                                                                       
	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE = "InitCreateRadiusTestAction";
	private static final String VIEW_FORWARD = "createRadiusPacket"; 
 
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
	    Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
            CreateRadiusTestForm createRadiusTestForm = (CreateRadiusTestForm)form;

            InetAddress obj = InetAddress.getLocalHost();
            createRadiusTestForm.setAdminHost(obj.getHostAddress());
            createRadiusTestForm.setHostAddress(obj.getHostAddress());
            createRadiusTestForm.setIsChap("1");
            
            List radParamList = new ArrayList();
            request.getSession().setAttribute("radParamList", radParamList);

	    return mapping.findForward(VIEW_FORWARD); 
	}
}
