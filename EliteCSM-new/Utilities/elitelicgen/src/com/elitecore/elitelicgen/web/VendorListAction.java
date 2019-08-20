/*
 *  License Generation Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on Nov 5, 2007
 *  Created By kaushikvira
 */
package com.elitecore.elitelicgen.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitelicgen.base.BaseWebAction;
import com.elitecore.elitelicgen.util.Logger;
import com.elitecore.elitelicgen.web.forms.VendorListForm;

/**
 * @author kaushikvira
 *
 */
public class VendorListAction extends BaseWebAction {
    
    private static String MODULE          = "VendorListAction";
    private static String VIEW_FORWARD    = "vendorlist";
    private static String VIEW_VENDORTYPE = "vendorTypelist";
    private static String FAILURE_FORWARD = "failure";  
    @Override
    public ActionForward execute( ActionMapping mapping ,
                                  ActionForm form , HttpServletRequest request ,
                                  HttpServletResponse response ) throws Exception {
        ActionErrors errors = null;
        try {
            
            Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
            
            errors = new ActionErrors();
            VendorListForm vendorListForm = (VendorListForm) form;
            String type = request.getParameter("type");
            if(type!=null && type.equalsIgnoreCase("vendors")){
            	return mapping.findForward(VIEW_FORWARD);	
            }else{
            	return mapping.findForward(VIEW_VENDORTYPE);
            }
        }
        catch (Exception e) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("license.genration.faild"));
            Logger.logError(MODULE, "Failed to get the vendor list");
            Logger.logTrace(MODULE,e);
        }
        saveErrors(request, errors);
        return mapping.findForward(FAILURE_FORWARD);
    }
    
    
}
