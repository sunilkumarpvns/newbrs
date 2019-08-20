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

import com.elitecore.elitelicgen.base.forms.BaseWebForm;

/**
 * @author kaushikvira
 *
 */
public class LoginForm extends BaseWebForm {
    
    private static final long serialVersionUID = 1L;
    private String            userName;
    private String            password;
    
    public String getPassword( ) {
        return password;
    }
    
    public void setPassword( String password ) {
        this.password = password;
    }
    
    public String getUserName( ) {
        return userName;
    }
    
    public void setUserName( String userName ) {
        this.userName = userName;
    }
    
    @Override
    public ActionErrors validate( ActionMapping mapping ,
                                  HttpServletRequest request ) {
        
        ActionErrors erros = new ActionErrors();
        if (getPassword().equalsIgnoreCase("") && getUserName().equalsIgnoreCase("")) {
            erros.add(ActionErrors.GLOBAL_ERROR, new ActionError("login.invliad.input"));
            
        } else if (getUserName().equalsIgnoreCase("")) {
            erros.add(ActionErrors.GLOBAL_ERROR, new ActionError("login.invliad.username"));
            
        } else if (getPassword().equalsIgnoreCase("")) {
            erros.add(ActionErrors.GLOBAL_ERROR, new ActionError("login.invliad.password"));
            
        }
        return erros;
    }
    
    public void reset( ActionMapping arg0 ,
                       HttpServletRequest arg1 ) {
        
        userName = "";
        password = "";
    }
    
}
