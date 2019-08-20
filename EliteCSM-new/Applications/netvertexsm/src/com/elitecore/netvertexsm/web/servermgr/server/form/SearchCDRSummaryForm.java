package com.elitecore.netvertexsm.web.servermgr.server.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


public class SearchCDRSummaryForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;
    
    private String groupField;
    private String checkAction;
    private Long serverId;
    
    
    public Long getServerId( ) {
        return serverId;
    }

    
    public void setServerId( Long serverId ) {
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
