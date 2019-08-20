/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ListNtradForm.java                 		
 * ModualName radius    			      		
 * Created on 2 April, 2008
 * Last Modified on                                     
 * @author :  SMCodeGen
 */
package com.elitecore.elitesm.web.radius.radtest.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ListRadiusTestForm extends BaseWebForm {
    private static final long serialVersionUID = 1L;
    
    private String ntradId;
    private String name;
    private String adminHost;
    private int    adminPort;
    private int   reTimeOut;
    private int    retries;
    private String scecretKey;
    private String userName;
    private String userPassword;
    private String isChap;
    private int   requestType;
    private String checkAction;
    
    
    public String getCheckAction( ) {
        return checkAction;
    }

    
    public void setCheckAction( String checkAction ) {
        this.checkAction = checkAction;
    }

    public String getNtradId( ) {
        return ntradId;
    }
    
    public void setNtradId( String ntradId ) {
        this.ntradId = ntradId;
    }
    
    public String getName( ) {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getAdminHost( ) {
        return adminHost;
    }
    
    public void setAdminHost( String adminHost ) {
        this.adminHost = adminHost;
    }
    
    public int getAdminPort( ) {
        return adminPort;
    }
    
    public void setAdminPort( int adminPort ) {
        this.adminPort = adminPort;
    }
    
    public int getReTimeOut( ) {
        return reTimeOut;
    }
    
    public void setReTimeOut( int reTimeOut ) {
        this.reTimeOut = reTimeOut;
    }
    
    public int getRetries( ) {
        return retries;
    }
    
    public void setRetries( int retries ) {
        this.retries = retries;
    }
    
    public String getScecretKey( ) {
        return scecretKey;
    }
    
    public void setScecretKey( String scecretKey ) {
        this.scecretKey = scecretKey;
    }
    
    public String getUserName( ) {
        return userName;
    }
    
    public void setUserName( String userName ) {
        this.userName = userName;
    }
    
    public String getUserPassword( ) {
        return userPassword;
    }
    
    public void setUserPassword( String userPassword ) {
        this.userPassword = userPassword;
    }
    
    public String getIsChap( ) {
        return isChap;
    }
    
    public void setIsChap( String isChap ) {
        this.isChap = isChap;
    }
    
    public int getRequestType( ) {
        return requestType;
    }
    
    public void setRequestType( int requestType ) {
        this.requestType = requestType;
    }
    
}
