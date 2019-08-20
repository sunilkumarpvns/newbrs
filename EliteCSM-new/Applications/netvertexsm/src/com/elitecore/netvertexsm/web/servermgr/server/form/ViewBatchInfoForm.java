package com.elitecore.netvertexsm.web.servermgr.server.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


public class ViewBatchInfoForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;
    private Long serverId;
    
    public Long getServerId( ) {
        return serverId;
    }
    
    public void setServerId( Long serverId ) {
        this.serverId = serverId;
    }
    
}
