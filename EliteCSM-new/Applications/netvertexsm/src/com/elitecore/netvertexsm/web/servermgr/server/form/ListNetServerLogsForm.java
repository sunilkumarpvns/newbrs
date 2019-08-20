package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.ArrayList;
import java.util.Map;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;
import com.elitecore.netvertexsm.web.servermgr.server.LogMonitorData;

public class ListNetServerLogsForm extends BaseWebForm {
    
    private static final long serialVersionUID = 1L;
    private Long            	netServerId;
    private String            action;
    private String            errorCode;
    private Map<String,Object> serverLogFileMap;
    private String fileData;
    private String fileLocation;
    
    private ArrayList<LogMonitorData> logMonitorDatas;
    
    
    
    
    public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}
     
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

	public Map<String, Object> getServerLogFileMap() {
		return serverLogFileMap;
	}

	public void setServerLogFileMap(Map<String, Object> serverLogFileMap) {
		this.serverLogFileMap = serverLogFileMap;
	}

	public ArrayList<LogMonitorData> getLogMonitorDatas() {
		return logMonitorDatas;
	}

	public void setLogMonitorDatas(ArrayList<LogMonitorData> logMonitorDatas) {
		this.logMonitorDatas = logMonitorDatas;
	}


    
}
