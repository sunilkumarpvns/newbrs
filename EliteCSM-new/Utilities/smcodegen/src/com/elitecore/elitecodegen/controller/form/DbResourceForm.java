/**
 * 
 */
package com.elitecore.elitecodegen.controller.form;

import org.apache.struts.action.ActionForm;

/**
 * @author kaushikvira
 */
public class DbResourceForm extends ActionForm {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5383770500596213234L;
    
    private String            strJdbcUrl;
    
    private String            strDriverName;
    
    private String            strUserName;
    
    private String            strPassword;
    
    private String            strSchmaName;
    
    private String            strTableName;
    
    public String getStrSchmaName() {
        return this.strSchmaName;
    }
    
    public void setStrSchmaName( String strSchmaName ) {
        this.strSchmaName = strSchmaName;
    }
    
    public String getStrTableName() {
        return this.strTableName;
    }
    
    public void setStrTableName( String strTableName ) {
        this.strTableName = strTableName;
    }
    
    public String getStrDriverName() {
        return this.strDriverName;
    }
    
    public void setStrDriverName( String strDriverName ) {
        this.strDriverName = strDriverName;
    }
    
    public String getStrJdbcUrl() {
        return this.strJdbcUrl;
    }
    
    public void setStrJdbcUrl( String strJdbcUrl ) {
        this.strJdbcUrl = strJdbcUrl;
    }
    
    public String getStrPassword() {
        return this.strPassword;
    }
    
    public void setStrPassword( String strPassword ) {
        this.strPassword = strPassword;
    }
    
    public String getStrUserName() {
        return this.strUserName;
    }
    
    public void setStrUserName( String strUserName ) {
        this.strUserName = strUserName;
    }
    
}
