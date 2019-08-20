package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class InitNetServerCLIForm extends BaseWebForm {
    
    private static final long serialVersionUID = 1L;
    private Long            	netServerId;
    private String            action;
    private String            errorCode;
    
    public String getAction( ) {
        return action;
    }
    
    public void setAction( String action ) {
        this.action = action;
    }
    
    public Long getNetServerId( ) {
        return netServerId;
    }
    
    public void setNetServerId( Long netServerId ) {
        this.netServerId = netServerId;
    }
    
    public String getErrorCode( ) {
        return errorCode;
    }
    
    public void setErrorCode( String errorCode ) {
        this.errorCode = errorCode;
    }
}
