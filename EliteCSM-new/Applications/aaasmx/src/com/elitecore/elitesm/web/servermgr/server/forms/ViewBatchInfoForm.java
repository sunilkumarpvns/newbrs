package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class ViewBatchInfoForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;
    private String serverId;
    
    public String getServerId( ) {
        return serverId;
    }
    
    public void setServerId( String serverId ) {
        this.serverId = serverId;
    }
    
}
