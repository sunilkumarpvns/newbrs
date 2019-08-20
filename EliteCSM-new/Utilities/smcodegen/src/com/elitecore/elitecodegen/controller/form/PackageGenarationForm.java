/**
 * Copyright (C) Elitecore Technologies Ltd.
 * PackageGenarationForm.java
 * Created on Jul 26, 2007
 * Last Modified on 
 * @author : Kaushik vira 
 */
package com.elitecore.elitecodegen.controller.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author kaushikvira
 */
public class PackageGenarationForm extends ActionForm {
    
    /**
     * 
     */
    private static final long serialVersionUID = -2878582192046926612L;
    
    private String            strApplicationName;
    
    private String            strModualName;
    
    private String            strSubMoudalName;
    
    private String            strTableName;
    
    private int               chkHibernet;
    
    private int               chkBlManager;
    
    private int               jspRadiol;
    
    private int               chkDataManager;
    
    private int               chkConfig;
    
    private int               chkStruts;
    
    @Override
    public ActionErrors validate( ActionMapping mapping ,
                                  HttpServletRequest request ) {
        
        ActionErrors errors = new ActionErrors();
        
        if (getStrApplicationName().equals("") || getStrModualName().equals("") || getStrTableName().equals(""))
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.invalid.input"));
        return errors;
    }
    
    public String getStrApplicationName() {
        return this.strApplicationName;
    }
    
    public void setStrApplicationName( String strApplicationName ) {
        this.strApplicationName = strApplicationName;
    }
    
    public int getChkBlManager() {
        return this.chkBlManager;
    }
    
    public void setChkBlManager( int chkBlManager ) {
    	System.out.println("chkBlManager stautus :"+ chkBlManager);
        this.chkBlManager = chkBlManager;
    }
    
    public int getChkConfig() {
        return this.chkConfig;
    }
    
    public void setChkConfig( int chkConfig ) {
        this.chkConfig = chkConfig;
    }
    
    public int getChkDataManager() {
        return this.chkDataManager;
    }
    
    public void setChkDataManager( int chkDataManager ) {
    	
    	System.out.println(" chkDataManager stautus :"+ chkDataManager);
        this.chkDataManager = chkDataManager;
    }
    
    public int getChkHibernet() {
        return this.chkHibernet;
    }
    
    public void setChkHibernet( int chkHibernet ) {
        this.chkHibernet = chkHibernet;
    }
    
    public int getChkStruts() {
        return this.chkStruts;
    }
    
    public void setChkStruts( int chkStruts ) {
        this.chkStruts = chkStruts;
    }
    
    public int getJspRadiol() {
        return this.jspRadiol;
    }
    
    public void setJspRadiol( int jspRadiol ) {
        this.jspRadiol = jspRadiol;
    }
    
    public String getStrModualName() {
        return this.strModualName;
    }
    
    public void setStrModualName( String strModualName ) {
        this.strModualName = strModualName;
    }
    
    public String getStrSubMoudalName() {
        return this.strSubMoudalName;
    }
    
    public void setStrSubMoudalName( String strSubMoudalName ) {
        this.strSubMoudalName = strSubMoudalName;
    }
    
    public String getStrTableName() {
        return this.strTableName;
    }
    
    public void setStrTableName( String strTableName ) {
        this.strTableName = strTableName;
    }
    
}
