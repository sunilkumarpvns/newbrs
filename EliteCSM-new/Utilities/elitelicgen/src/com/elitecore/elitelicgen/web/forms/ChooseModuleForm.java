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
package com.elitecore.elitelicgen.web.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.elitecore.elitelicgen.base.forms.BaseWebForm;

/**
 * @author kaushikvira
 *
 */
public class ChooseModuleForm extends BaseWebForm {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            choice;
    private String            status;
    private String            additionalKey;
    private String            version;
    private Object            licMap;
    private FormFile          pubkeyFile;
    
    
    public FormFile getPubkeyFile( ) {
        return pubkeyFile;
    }

    
    public void setPubkeyFile( FormFile pubkeyFile ) {
        this.pubkeyFile = pubkeyFile;
    }

    public Object getLicMap( ) {
        return licMap;
    }
    
    public void setLicMap( Object licMap ) {
        this.licMap = licMap;
    }
    
    public String getAdditionalKey( ) {
        return additionalKey;
    }
    
    public void setAdditionalKey( String additionalKey ) {
        this.additionalKey = additionalKey;
    }
    
    public String getStatus( ) {
        return status;
    }
    
    public void setStatus( String status ) {
        this.status = status;
    }
    
    public String getVersion( ) {
        return version;
    }
    
    public void setVersion( String version ) {
        this.version = version;
    }
    
    public String getChoice( ) {
        return choice;
    }
    
    public void setChoice( String choice ) {
        this.choice = choice;
    }
    
    @Override
    public ActionErrors validate( ActionMapping mapping ,
                                  HttpServletRequest request ) {
        
        ActionErrors errors = new ActionErrors();
        if (getChoice() == null || getChoice().equalsIgnoreCase("")) {
            
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("choose.select.option"));
            return errors;
        }
        if (getVersion() == null || getVersion().equalsIgnoreCase("")) {
            
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("choose.version.option"));
            return errors;
        }
        if (getStatus() == null || getStatus().equalsIgnoreCase("")) {
            
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("choose.status.option"));
            return errors;
        }
        if (getAdditionalKey() == null || getAdditionalKey().equalsIgnoreCase("")) {
            
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("choose.additionalkey.option"));
            return errors;
        }
         
        if (getPubkeyFile() == null || getPubkeyFile().getFileName().equalsIgnoreCase("")) {
            System.out.println("Contant"+getPubkeyFile().getContentType());
            errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("choose.browse.file"));
            return errors;
        }
        return errors;
        
    }
    
}
