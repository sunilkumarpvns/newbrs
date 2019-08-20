package com.elitecore.netvertex.escommunication.data;

import com.elitecore.core.systemx.esix.configuration.EndPointConfiguration;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.license.base.commons.LicenseConstants;

import javax.persistence.Transient;
import java.sql.Timestamp;

public class PDInstanceConfiguration implements EndPointConfiguration {

    public static final String MODULE = "PD-INSTANCE-CONFIGURATION";

    private String id;
    private String ipAddresses;
    private String port;
    private String contextPath;
    private String deploymentPath;
    private String hostName;
    private String status;
    private Timestamp lastUpdateTime;

    public PDInstanceConfiguration() {
        //Sonar Ignore
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(String ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getDeploymentPath() {
        return deploymentPath;
    }

    public void setDeploymentPath(String deploymentPath) {
        this.deploymentPath = deploymentPath;
    }


    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public String toString() {

        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE)
                .append("ID", id)
                .append("IPAddresses", ipAddresses)
                .append("Host Name", hostName)
                .append("Port", port)
                .append("Context Path", contextPath)
                .append("Last Update Time",lastUpdateTime);
        return toStringBuilder.toString();
    }

    @Transient
    public String getName() {
        return "PD" + CommonConstants.AT_SIGN + hostName + contextPath;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getUsername(){
        return LicenseConstants.INTEGRATION_USER;
    }

    public String getPassword(){
        return LicenseConstants.INTEGRATION_SECRET;
    }

}
