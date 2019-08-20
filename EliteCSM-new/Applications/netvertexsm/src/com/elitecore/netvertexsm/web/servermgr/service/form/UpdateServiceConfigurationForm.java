package com.elitecore.netvertexsm.web.servermgr.service.form;


import java.util.List;

import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData;
import com.elitecore.netvertexsm.web.servermgr.BaseUpdateConfigurationForm;

public class UpdateServiceConfigurationForm extends BaseUpdateConfigurationForm {

    private static final long serialVersionUID = -2805437832841352190L;
    
    private String configurationName;
    private String serverName;
    private String serviceName;
    private String action ;
    private String nodeParameterId;
    private String nodeInstanceId;
    private Long confInstanceId;
    private Long netServiceId;
    private String childTotalInstanceVal;
    private List startUpModeList;
    private List<DriverInstanceData> driverInstanceList;
    private List<SPRData> sprList;

	public List getStartUpModeList() {
		return startUpModeList;
	}

	public void setStartUpModeList(List startUpModeList) {
		this.startUpModeList = startUpModeList;
	}

	public Long getNetServiceId() {
        return netServiceId;
    }

    public void setNetServiceId(Long netServiceId) {
        this.netServiceId = netServiceId;
    }

    public Long getConfInstanceId() {
        return confInstanceId;
    }

    public void setConfInstanceId(Long confInstanceId) {
        this.confInstanceId = confInstanceId;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getChildTotalInstanceVal() {
        return childTotalInstanceVal;
    }

    public void setChildTotalInstanceVal(String childTotalInstanceVal) {
        this.childTotalInstanceVal = childTotalInstanceVal;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNodeParameterId() {
        return nodeParameterId;
    }

    public void setNodeParameterId(String nodeParameterId) {
        this.nodeParameterId = nodeParameterId;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstatnceId) {
        this.nodeInstanceId = nodeInstatnceId;
    }

	public List<DriverInstanceData> getDriverInstanceList() {
		return driverInstanceList;
	}

	public void setDriverInstanceList(List<DriverInstanceData> driverInstanceList) {
		this.driverInstanceList = driverInstanceList;
	}

	public List<SPRData> getSprList() {
		return sprList;
	}

	public void setSprList(List<SPRData> sprList) {
		this.sprList = sprList;
	}
	

}
