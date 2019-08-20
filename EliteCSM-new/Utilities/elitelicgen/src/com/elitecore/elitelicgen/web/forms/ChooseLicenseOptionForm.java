package com.elitecore.elitelicgen.web.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitelicgen.base.forms.BaseWebForm;


public class ChooseLicenseOptionForm extends BaseWebForm {
    private static final long serialVersionUID = 1L;
    
    private String choice;

    
    public String getChoice( ) {
        return choice;
    }

    
    public void setChoice( String choice ) {
        this.choice = choice;
    }
    
    @Override
    public ActionErrors validate( ActionMapping arg0 , HttpServletRequest arg1 ) {
        ActionErrors errors = new ActionErrors();
        if (getChoice() == null || getChoice().equalsIgnoreCase("")) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("choose.select.option"));
        }
        return errors;
    }
}
