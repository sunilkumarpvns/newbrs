package com.elitecore.elitesm.web.servermgr.server.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.license.base.LicenseData;

public class InitNetServerLicenseForm extends BaseWebForm {
    
    private static final long serialVersionUID = 1L;
    private String            	netServerId;
    private String            action;
    private String            errorCode;
    private List<LicenseData> licenseData;
    
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

	public List<LicenseData> getLicenseData() {
		return licenseData;
	}

	public void setLicenseData(List<LicenseData> licenseData) {
		this.licenseData = licenseData;
	}
}
