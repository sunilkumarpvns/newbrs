package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.web.servermgr.BaseUpdateConfigurationForm;



public class UpdateNetServerConfigurationForm extends BaseUpdateConfigurationForm {

    private static final long serialVersionUID = -6178767424139164363L;

    String configurationName;
    String serverName;
    String serviceName;

    String action ;
    String nodeParameterId;
    String nodeInstanceId;
    Long confInstanceId;
    Long netServerId;
    String childTotalInstanceVal;
    
    List startUpModeList;
    List<DriverInstanceData> driverInstanceList;
    List<DatabaseDSData> datasourceList;
    List<GatewayData> gatewayDataLists;
    
    public List<GatewayData> getGatewayDataLists() {
		return gatewayDataLists;
	}

	public void setGatewayDataLists(List<GatewayData> gatewayDataLists) {
		this.gatewayDataLists = gatewayDataLists;
	}

	public List getStartUpModeList() {
		return startUpModeList;
	}

	public void setStartUpModeList(List startUpModeList) {
		this.startUpModeList = startUpModeList;
	}

	public Long getNetServerId() {
        return netServerId;
    }

    public void setNetServerId(Long netServerId) {
        this.netServerId = netServerId;
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

    public List<DriverInstanceData> getDriverInstanceList() {
		return driverInstanceList;
	}

	public void setDriverInstanceList(List<DriverInstanceData> driverInstanceList) {
		this.driverInstanceList = driverInstanceList;
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

	public List<DatabaseDSData> getDatasourceList() {
		return datasourceList;
	}

	public void setDatasourceList(List<DatabaseDSData> datasourceList) {
		this.datasourceList = datasourceList;
	}


}
