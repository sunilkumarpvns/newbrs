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
public class VendorListForm extends BaseWebForm {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            id;
    private String            name;
    private String 			  List;
    
    public String getId() {
    	return id;
    }

    public void setId(String id) {
    	this.id = id;
    }

    public String getName() {
    	return name;
    }

    public void setName(String name) {
    	this.name = name;
    } 
  
    
    @Override
    public ActionErrors validate( ActionMapping mapping ,
                                  HttpServletRequest request ) {
        
        ActionErrors errors = new ActionErrors();
//        if (getChoice() == null || getChoice().equalsIgnoreCase("")) {
//            
//            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("choose.select.option"));
//            return errors;
//        }
//        if (getVersion() == null || getVersion().equalsIgnoreCase("")) {
//            
//            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("choose.version.option"));
//            return errors;
//        }
//        if (getStatus() == null || getStatus().equalsIgnoreCase("")) {
//            
//            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("choose.status.option"));
//            return errors;
//        }
//        if (getAdditionalKey() == null || getAdditionalKey().equalsIgnoreCase("")) {
//            
//            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("choose.additionalkey.option"));
//            return errors;
//        }
//         
//        if (getPubkeyFile() == null || getPubkeyFile().getFileName().equalsIgnoreCase("")) {
//            System.out.println("Contant"+getPubkeyFile().getContentType());
//            errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("choose.browse.file"));
//            return errors;
//        }
        return errors;
        
    }


    
}
