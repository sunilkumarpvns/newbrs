package com.elitecore.elitesm.web.servermgr.server.forms;

import java.util.Map;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ListNetServerLogsForm extends BaseWebForm {
    
    private static final long serialVersionUID = 1L;
    private String            	netServerId;
    private String            action;
    private String            errorCode;
    private Map<String,Object> serverLogFileMap;
    private Map<String,Object> serviceLogFileMap;
    private Map<String,Object> cdrFileMap;
    
     
    public String getAction( ) {
        return action;
    }
    
    public void setAction( String action ) {
        this.action = action;
    }
    
    public String getNetServerId( ) {
        return netServerId;
    }
    
    public void setNetServerId( String netServerId ) {
        this.netServerId = netServerId;
    }
    
    public String getErrorCode( ) {
        return errorCode;
    }
    
    public void setErrorCode( String errorCode ) {
        this.errorCode = errorCode;
    }

	public Map<String, Object> getServerLogFileMap() {
		return serverLogFileMap;
	}

	public void setServerLogFileMap(Map<String, Object> serverLogFileMap) {
		this.serverLogFileMap = serverLogFileMap;
	}

	public Map<String, Object> getServiceLogFileMap() {
		return serviceLogFileMap;
	}

	public void setServiceLogFileMap(Map<String, Object> serviceLogFileMap) {
		this.serviceLogFileMap = serviceLogFileMap;
	}

	public Map<String, Object> getCdrFileMap() {
		return cdrFileMap;
	}

	public void setCdrFileMap(Map<String, Object> cdrFileMap) {
		this.cdrFileMap = cdrFileMap;
	}

	

    
}
