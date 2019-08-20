package com.elitecore.elitesm.web.servermgr.service.forms;


import com.elitecore.elitesm.web.servermgr.BaseUpdateConfigurationForm;

public class UpdateServiceConfigurationForm extends BaseUpdateConfigurationForm {

    private static final long serialVersionUID = -2805437832841352190L;
    
    String configurationName;
    String serverName;
    String serviceName;
    String action ;
    String nodeParameterId;
    String nodeInstanceId;
    String confInstanceId;
    String netServiceId;
    String childTotalInstanceVal;

    public String getNetServiceId() {
        return netServiceId;
    }

    public void setNetServiceId(String netServiceId) {
        this.netServiceId = netServiceId;
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
