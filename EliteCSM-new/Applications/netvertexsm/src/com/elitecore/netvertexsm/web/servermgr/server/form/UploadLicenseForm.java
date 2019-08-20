/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   UploadLicenseForm.java                             
 * ModualName                                     
 * Created on Oct 9, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */    
package com.elitecore.netvertexsm.web.servermgr.server.form;

import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


/**
 * @author kaushikvira
 *
 */
public class UploadLicenseForm extends BaseWebForm {
    
    private FormFile licenseFile;
    private  Long netServerId;
    private String action;
 
    
    public String getAction() {
        return action;
    }



    
    public void setAction( String action ) {
        this.action = action;
    }



    public Long getNetServerId() {
        return netServerId;
    }


    
    public void setNetServerId( Long netServerId ) {
        this.netServerId = netServerId;
    }


    public FormFile getLicenseFile() {
        return licenseFile;
    }

    
    public void setLicenseFile( FormFile dictionaryFile ) {
        this.licenseFile = dictionaryFile;
    }
    
    

}
