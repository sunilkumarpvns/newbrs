/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   NetServerStartStopForm.java                             
 * ModualName                                     
 * Created on Dec 11, 2007
 * Last Modified on                                     
 * @author :  kaushikvira
 */
package com.elitecore.elitesm.web.servermgr.server.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

/**
 * @author kaushikvira
 */
public class NetServerStartStopForm extends BaseWebForm {
    
    private static final long serialVersionUID       = 1L;
    private String            netServerId;
    private boolean           netSeverStatus         = false;
    private boolean           netServerRestartStatus = false;
    private boolean           configInSyncState = false;
    
    public static long getSerialVersionUID( ) {
        return serialVersionUID;
    }
    
    public boolean isNetServerRestartStatus( ) {
        return netServerRestartStatus;
    }
    
    public void setNetServerRestartStatus( boolean netServerRestartStatus ) {
        this.netServerRestartStatus = netServerRestartStatus;
    }
    
    public boolean isNetSeverStatus( ) {
        return netSeverStatus;
    }
    
    public void setNetSeverStatus( boolean netSeverStatus ) {
        this.netSeverStatus = netSeverStatus;
    }
    
    public String getNetServerId( ) {
        return netServerId;
    }
    
    public void setNetServerId( String netServerId ) {
        this.netServerId = netServerId;
    }
    
    public NetServerStartStopForm() {}
    
    @Override
    public void reset( ActionMapping arg0 ,
                       HttpServletRequest arg1 ) {
        netServerRestartStatus = false;
        netSeverStatus = false;
        configInSyncState = false;
    }

    
    public boolean getConfigInSyncState( ) {
        return configInSyncState;
    }

    
    public void setConfigInSyncState( boolean configInSyncState ) {
        this.configInSyncState = configInSyncState;
    }
    
    
}