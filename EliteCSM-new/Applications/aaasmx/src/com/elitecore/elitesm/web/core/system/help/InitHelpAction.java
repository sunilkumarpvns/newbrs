package com.elitecore.elitesm.web.core.system.help;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.help.forms.HelpForm;


public class InitHelpAction extends BaseWebAction{
    
        private static final String VIEW_FORWARD = "viewHelpPage";
        private static final String SUCCESS_FORWARD = "success";
        private static final String FAILURE_FORWARD = "failure";
        private static final String MODULE = "INIT HELP ACTION";
        
        
        public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
            Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
            ActionMessages messages = new ActionMessages();
            try {
            	HelpForm helpForm =  (HelpForm) form;
                
                String licensePath = getServlet().getServletContext().getRealPath("docs");
                File licenseDirectory = new File(licensePath);
                FileFilter pdfFileFilter = new FileFilter(){
            		private final String pdfExtension = ".pdf";    
                	 public boolean accept(File file){
                	  if (file.getName().toLowerCase().endsWith(pdfExtension)) {
                    			return true;
                  	  }
            		  return false;
                	}
                };  
                File[] helpFiles = licenseDirectory.listFiles(pdfFileFilter);
                List<String> lstHelpFiles = new ArrayList<String>();
                if (helpFiles != null && helpFiles.length>0) {
                	for (int i = 0; i < helpFiles.length; i++) {
                		lstHelpFiles.add(helpFiles[i].getName());
					}
                }
                helpForm.setLstHelpFile(lstHelpFiles);
                request.setAttribute("lstHelpFiles", lstHelpFiles);
                return mapping.findForward(VIEW_FORWARD);
                
            }
            catch (Exception e) {
                Logger.logError(MODULE, "Problem while getting help file list, reason : " + e.getMessage());
                Logger.logTrace(MODULE, e);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("sm.help.filelist.failed");
                messages.add("information", message);
                saveErrors(request, messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        }
        
        
}
