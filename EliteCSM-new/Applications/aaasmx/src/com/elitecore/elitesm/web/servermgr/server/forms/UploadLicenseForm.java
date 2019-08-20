/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   UploadLicenseForm.java                             
 * ModualName                                     
 * Created on Oct 9, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.elitesm.web.servermgr.server.forms;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


/**
 * @author kaushikvira
 *
 */
public class UploadLicenseForm extends BaseWebForm {
    
    private FormFile licenseFile;
    private  String netServerId;
    private String action;
 
    
    public String getAction() {
        return action;
    }



    
    public void setAction( String action ) {
        this.action = action;
    }



    public String getNetServerId() {
        return netServerId;
    }


    
    public void setNetServerId( String netServerId ) {
        this.netServerId = netServerId;
    }


    public FormFile getLicenseFile() {
        return licenseFile;
    }

    
    public void setLicenseFile( FormFile dictionaryFile ) {
        this.licenseFile = dictionaryFile;
    }
    
    

}
