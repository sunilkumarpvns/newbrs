/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   MiscNtradAction.java                 		
 * ModualName radius    			      		
 * Created on 2 April, 2008
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.web.radius.radtest; 
  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.radtest.RadiusTestBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.radtest.forms.ListRadiusTestForm;
                                                                               
public class MiscRadiusTestAction extends BaseWebAction { 
        private static final String SUCCESS_FORWARD = "success";               
        private static final String FAILURE_FORWARD = "failure";               
        private static final String MODULE ="MiscRadiusTestAction";
        private static final String VIEW_FORWARD = "initListRadiusPacket"; 
        private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.LIST_RADIUS_PACKET;
        private static final String ACTIONALIASDELETE = ConfigConstant.DELETE_RADIUS_PACKET;
        public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
            Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
            try{
            	checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
	            ListRadiusTestForm lstRadiusTestForm = (ListRadiusTestForm)form;
	            RadiusTestBLManager radiusTestBLManager = new RadiusTestBLManager();
	            IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
	            if(lstRadiusTestForm.getCheckAction().equalsIgnoreCase("Delete")) {
	            	checkActionPermission(request, ACTIONALIASDELETE);
	                String[] strSelectedIds = request.getParameterValues("select");
	                for(int i=0;i<strSelectedIds.length;i++) {
	                	String ids = strSelectedIds[i];
	                    radiusTestBLManager.delete(ids);
	                    doAuditing(staffData, ACTIONALIASDELETE);
	                }
	            }
	            return mapping.findForward(VIEW_FORWARD);
            }catch(ActionNotPermitedException e){
                Logger.logError(MODULE,"Error :-" + e.getMessage());
                printPermitedActionAlias(request);
                ActionMessages messages = new ActionMessages();
                messages.add("information", new ActionMessage("general.user.restricted"));
                saveErrors(request, messages);
                return mapping.findForward(INVALID_ACCESS_FORWARD);
            }
        }     
}
