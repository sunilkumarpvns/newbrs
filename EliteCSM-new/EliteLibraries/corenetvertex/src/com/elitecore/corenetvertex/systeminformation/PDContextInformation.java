package com.elitecore.corenetvertex.systeminformation;


import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "TBLM_PD_CONTEXT_INFORMATION")
public class PDContextInformation implements Serializable {

    private String id;
    private String ipAddresses;
    private String port;
    private String contextPath;
    private String deploymentPath;
    private String hostName;
    private String status;
    private Timestamp lastUpdateTime;

    public PDContextInformation(String hostName, String ipAddresses, String port, String contextPath) {
        this.hostName = hostName;
        this.ipAddresses = ipAddresses;
        this.port = port;
        this.contextPath = contextPath;
    }

    public PDContextInformation() {
    }


    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "IP_ADDRESSES")
    public String getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(String ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    @Column(name = "PORT")
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Column(name = "CONTEXT_PATH")
    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Column(name = "DEPLOYMENT_PATH")
    public String getDeploymentPath() {
        return deploymentPath;
    }

    public void setDeploymentPath(String deploymentPath) {
        this.deploymentPath = deploymentPath;
    }


    @Column(name = "HOST_NAME")
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

    @Column(name="STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name="LAST_UPDATE_TIME")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

}
