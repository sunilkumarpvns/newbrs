/**
 * Copyright (C) Elitecore Technologies Ltd. FileName UploadLicenseForm.java
 * ModualName Created on Oct 9, 2007 Last Modified on
 * 
 * @author : kaushikvira
 */

package com.elitecore.netvertexsm.web.servermgr.server.form;

import org.apache.struts.upload.FormFile;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

/**
 * @author kaushikvira
 */
public class UploadErrorProcessingFileForm extends BaseWebForm {
    
    private static final long serialVersionUID = -7311144217963382522L;
    private FormFile errorProcessingFile;
    private String netServerId;
    private String action;
    private String fileName;
    private String deviceName;
    private String serviceType;
      
    public String getServiceType( ) {
        return this.serviceType;
    }

    
    public void setServiceType( String serviceType ) {
        this.serviceType = serviceType;
    }

    public String getDeviceName( ) {
        return this.deviceName;
    }
    
    public void setDeviceName( String deviceName ) {
        this.deviceName = deviceName;
    }
    
    public String getAction( ) {
        return this.action;
    }
    
    public void setAction( String action ) {
        this.action = action;
    }
    
    public String getNetServerId( ) {
        return this.netServerId;
    }
    
    public void setNetServerId( String netServerId ) {
        this.netServerId = netServerId;
    }
    
    public FormFile getErrorProcessingFile( ) {
        return this.errorProcessingFile;
    }
    
    public void setErrorProcessingFile( FormFile errorProcessingFile ) {
        this.errorProcessingFile = errorProcessingFile;
    }
    
    public String getFileName( ) {
        return this.fileName;
    }
    
    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }
    
}
