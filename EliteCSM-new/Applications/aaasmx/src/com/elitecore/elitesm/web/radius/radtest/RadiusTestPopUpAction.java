package com.elitecore.elitesm.web.radius.radtest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.radius.radtest.forms.RadiusTestPopUpForm;


public class RadiusTestPopUpAction extends BaseWebAction {
        private static final String SUCCESS_FORWARD = "success";               
        private static final String FAILURE_FORWARD = "failure";               
        private static final String MODULE = "RadiusTestPopUpAction";
        private static final String VIEW_FORWARD = "radiusPacketPopUp";

        public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
            Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
            RadiusTestPopUpForm radiusTestPopUpForm = (RadiusTestPopUpForm)form;
            ActionMessages messages = new ActionMessages();
            
            try {
                RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
                List dictionaryList = dictionaryBLManager.getDictionaryList();
                radiusTestPopUpForm.setDictionaryListInCombo(dictionaryList);
                request.getSession(true).setAttribute("dl", dictionaryList);
                return mapping.findForward(VIEW_FORWARD); 
                
            }catch(DataManagerException e) {
                Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
                Logger.logTrace(MODULE, e);
            	Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
            	request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("radius.radtest.datasource.failed");
                messages.add("information", message);
                saveErrors(request, messages);
                return mapping.findForward(FAILURE_FORWARD);
                
            } catch(Exception e) {
                Logger.logError(MODULE, "Error during Data Manager operation , reason : " + e.getMessage());
                Logger.logTrace(MODULE, e);
            	Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
            	request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("general.error");
                messages.add("information", message);   
                saveMessages(request, messages);
                return mapping.findForward(FAILURE_FORWARD);
            }    
            
        }
}
