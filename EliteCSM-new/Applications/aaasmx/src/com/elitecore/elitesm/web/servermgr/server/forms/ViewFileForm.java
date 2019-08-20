package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class ViewFileForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;
    private String fileId;
    private String fileName;
    private String deviceId;
    private String dateAndTime;
    private String status;
    private String reason;
    private String location;
    private String serverId;
    
    public String getDateAndTime( ) {
        return dateAndTime;
    }
    
    public void setDateAndTime( String dateAndTime ) {
        this.dateAndTime = dateAndTime;
    }
    
    public String getDeviceId( ) {
        return deviceId;
    }
    
    public void setDeviceId( String deviceId ) {
        this.deviceId = deviceId;
    }
    
    public String getFileId( ) {
        return fileId;
    }
    
    public void setFileId( String fileId ) {
        this.fileId = fileId;
    }
    
    public String getFileName( ) {
        return fileName;
    }
    
    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }
    
    public String getLocation( ) {
        return location;
    }
    
    public void setLocation( String location ) {
        this.location = location;
    }
    
    public String getReason( ) {
        return reason;
    }
    
    public void setReason( String reason ) {
        this.reason = reason;
    }
    
    public String getServerId( ) {
        return serverId;
    }
    
    public void setServerId( String serverId ) {
        this.serverId = serverId;
    }
    
    public String getStatus( ) {
        return status;
    }
    
    public void setStatus( String status ) {
        this.status = status;
    }
}
