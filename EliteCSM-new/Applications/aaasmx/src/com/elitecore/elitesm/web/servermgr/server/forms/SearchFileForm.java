package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class SearchFileForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;
    private String filedName;
    private String deviceId;
    private String dateFrom;
    private String dateTo;
    private String status;
    private String reason;
    private String location;
    private String serverId;
    
    
    public String getServerId( ) {
        return serverId;
    }

    
    public void setServerId( String serverId ) {
        this.serverId = serverId;
    }

    public String getDateFrom( ) {
        return dateFrom;
    }
    
    public void setDateFrom( String dateFrom ) {
        this.dateFrom = dateFrom;
    }
    
    public String getDateTo( ) {
        return dateTo;
    }
    
    public void setDateTo( String dateTo ) {
        this.dateTo = dateTo;
    }
    
    public String getDeviceId( ) {
        return deviceId;
    }
    
    public void setDeviceId( String deviceId ) {
        this.deviceId = deviceId;
    }
    
    public String getFiledName( ) {
        return filedName;
    }
    
    public void setFiledName( String filedName ) {
        this.filedName = filedName;
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
    
    public String getStatus( ) {
        return status;
    }
    
    public void setStatus( String status ) {
        this.status = status;
    }

}
