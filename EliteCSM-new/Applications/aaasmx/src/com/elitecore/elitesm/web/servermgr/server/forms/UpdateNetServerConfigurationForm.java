package com.elitecore.elitesm.web.servermgr.server.forms;


import com.elitecore.elitesm.web.servermgr.BaseUpdateConfigurationForm;

public class UpdateNetServerConfigurationForm extends BaseUpdateConfigurationForm {

    private static final long serialVersionUID = -6178767424139164363L;

    String configurationName;
    String serverName;
    String serviceName;

    String action ;
    String nodeParameterId;
    String nodeInstanceId;
    String confInstanceId;
    String netServerId;
    String childTotalInstanceVal;
    
    public String getNetServerId() {
        return netServerId;
    }

    public void setNetServerId(String netServerId) {
        this.netServerId = netServerId;
    }

    public String getConfInstanceId() {
        return confInstanceId;
    }

    public void setConfInstanceId(String confInstanceId) {
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


}
