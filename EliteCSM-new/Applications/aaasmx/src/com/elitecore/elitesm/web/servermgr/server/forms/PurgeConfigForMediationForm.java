package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class PurgeConfigForMediationForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;
    
    private String purgeDate;
    private int purgeHour;
    private int purgeMinute;
    private String checkAction;
    private String serverId;
    
    public String getCheckAction( ) {
        return checkAction;
    }
    
    public void setCheckAction( String checkAction ) {
        this.checkAction = checkAction;
    }
    
    public String getPurgeDate( ) {
        return purgeDate;
    }
    
    public void setPurgeDate( String purgeDate ) {
        this.purgeDate = purgeDate;
    }
    
    public int getPurgeHour( ) {
        return purgeHour;
    }
    
    public void setPurgeHour( int purgeHour ) {
        this.purgeHour = purgeHour;
    }
    
    public int getPurgeMinute( ) {
        return purgeMinute;
    }
    
    public void setPurgeMinute( int purgeMinute ) {
        this.purgeMinute = purgeMinute;
    }
    
    public  String getServerId( ) {
        return serverId;
    }
    
    public void setServerId(  String serverId ) {
        this.serverId = serverId;
    }
    
}
