package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class SearchCDRSummaryForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;
    
    private String groupField;
    private String checkAction;
    private String serverId;
    
    
    public String getServerId( ) {
        return serverId;
    }

    
    public void setServerId( String serverId ) {
        this.serverId = serverId;
    }

    public String getCheckAction( ) {
        return checkAction;
    }
    
    public void setCheckAction( String checkAction ) {
        this.checkAction = checkAction;
    }

    public String getGroupField( ) {
        return groupField;
    }
    
    public void setGroupField( String groupField ) {
        this.groupField = groupField;
    }
}
