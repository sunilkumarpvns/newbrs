package com.elitecore.elitelicgen.web.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.elitecore.elitelicgen.base.forms.BaseWebForm;


public class UploadLicenseForm extends BaseWebForm{

    private static final long serialVersionUID = 1L;
    
    private FormFile licenseFile;

    
    public FormFile getLicenseFile( ) {
        return licenseFile;
    }

    
    public void setLicenseFile( FormFile licenseFile ) {
        this.licenseFile = licenseFile;
    }

    @Override
    public ActionErrors validate( ActionMapping arg0 , HttpServletRequest arg1 ) {
        ActionErrors errors  =  new ActionErrors();
        if(getLicenseFile() == null || getLicenseFile().getFileName().equalsIgnoreCase("")) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("choose.browse.file"));
        }
        return errors;
    }  
}
