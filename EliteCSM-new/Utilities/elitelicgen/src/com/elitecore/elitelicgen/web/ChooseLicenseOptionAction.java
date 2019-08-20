package com.elitecore.elitelicgen.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitelicgen.base.BaseWebAction;
import com.elitecore.elitelicgen.util.Logger;
import com.elitecore.elitelicgen.web.forms.ChooseLicenseOptionForm;


public class ChooseLicenseOptionAction extends BaseWebAction {
    private static String MODULE          = "CHOOSELICENSEOPTION";
    private static String VIEW_FORWARD    = "";
    
    public ActionForward execute( ActionMapping mapping , ActionForm form ,HttpServletRequest request ,HttpServletResponse response ) throws Exception {
            Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
            ChooseLicenseOptionForm chooseLicenseOptionForm = (ChooseLicenseOptionForm) form;
            
            if(chooseLicenseOptionForm.getChoice().equalsIgnoreCase("new")) {
                VIEW_FORWARD = "ViewChooseModual";
            } else {
                VIEW_FORWARD = "UploadLicenseFile";
            }
            return mapping.findForward(VIEW_FORWARD);
     }
}
