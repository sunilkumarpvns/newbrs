/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   SynchronizeNetServerInstanceVersionForm.java                            
 * ModualName com.elitecore.elitesm.web.servermgr.server.forms                                      
 * Created on Mar 5, 2008
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class SynchronizeNetServerInstanceVersionForm extends BaseWebForm {
    
    private static final long serialVersionUID = -1141089709705245138L;
    
    private String netServerInstanceId;
    private String action;
    private boolean netServerStatus;
    private boolean syncServerVesionStatus;
    private String  liveServerVersion;
    private String  upgradeServerVersion;
    
    
    
    public boolean isSyncServerVesionStatus( ) {
        return syncServerVesionStatus;
    }
    
    public String getLiveServerVersion( ) {
        return liveServerVersion;
    }
    
    public void setLiveServerVersion( String liveServerVersion ) {
        this.liveServerVersion = liveServerVersion;
    }
    
    
    public void setSyncServerVesionStatus( boolean syncServerVesionStatus ) {
        this.syncServerVesionStatus = syncServerVesionStatus;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction( String action ) {
        this.action = action;
    }
    
    public String getNetServerInstanceId() {
        return netServerInstanceId;
    }
    
    public void setNetServerInstanceId( String netServerInstanceId ) {
        this.netServerInstanceId = netServerInstanceId;
    }
    
    
    public boolean getNetServerStatus( ) {
        return netServerStatus;
    }
    
    
    public void setNetServerStatus( boolean netServerStatus ) {
        this.netServerStatus = netServerStatus;
    }
    
    
    public static long getSerialVersionUID( ) {
        return serialVersionUID;
    }

    
    public String getUpgradeServerVersion( ) {
        return upgradeServerVersion;
    }

    
    public void setUpgradeServerVersion( String upgradeServerVersion ) {
        this.upgradeServerVersion = upgradeServerVersion;
    }
    
}