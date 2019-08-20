package com.elitecore.netvertexsm.web.core.system.license;

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


import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.license.forms.LicenseForm;


public class InitLicenseAction extends BaseWebAction{
    
        private static final String VIEW_FORWARD = "viewLicensePage";
        private static final String SUCCESS_FORWARD = "success";
        private static final String FAILURE_FORWARD = "failure";
        private static final String MODULE = "INIT LICENSE ACTION";
        
        
        public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
            Logger.logInfo(MODULE, "Enter execute method of " + getClass().getName());
            ActionMessages messages = new ActionMessages();
            try {
            	LicenseForm licenseForm =  (LicenseForm) form;
                String licensePath = getServlet().getServletContext().getRealPath("license/notes");
                File licenseDirectory = new File(licensePath);
                FileFilter pdfFileFilter = new FileFilter(){
            		private final String pdfExtension = ".pdf";    
                	 public boolean accept(File file){
                	  if (file.getName().toLowerCase().endsWith(pdfExtension) && file.getName().toLowerCase().startsWith("license") ) {
                    			return true;
                  	  }
            		  return false;
                	}
                };  
                File[] licenseFiles = licenseDirectory.listFiles(pdfFileFilter);
                List<String> lstLicenseFiles = new ArrayList<String>();
                if (licenseFiles != null && licenseFiles.length>0) {
                	for (int i = 0; i < licenseFiles.length; i++) {
                		lstLicenseFiles.add(licenseFiles[i].getName());
					}
                }
                licenseForm.setLstLicenseFile(lstLicenseFiles);
                request.setAttribute("lstLicenseFiles", lstLicenseFiles);
                return mapping.findForward(VIEW_FORWARD);
                
            }
            catch (Exception e) {
            	Logger.logError(MODULE, "Problem while getting license file list, reason : " + e.getMessage());
                Logger.logTrace(MODULE, e);
    			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
    			request.setAttribute("errorDetails", errorElements);
    			 ActionMessage message = new ActionMessage("sm.license.filelist.failed");
                messages.add("information", message);
                saveErrors(request, messages);
                return mapping.findForward(FAILURE_FORWARD);
            }
        }
        
        
}
