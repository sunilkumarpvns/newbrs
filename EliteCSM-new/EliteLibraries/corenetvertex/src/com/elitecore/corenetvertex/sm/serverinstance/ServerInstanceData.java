package com.elitecore.corenetvertex.sm.serverinstance;


import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.notificationagents.EmailAgentData;
import com.elitecore.corenetvertex.sm.notificationagents.SMSAgentData;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupServerInstanceRelData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirpalsinh on 29/7/17.
 */
@Entity ( name = "com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData" )
@Table(name = "TBLM_SERVER_INSTANCE")
public class ServerInstanceData extends ResourceData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String jmxUrl;
    private String javaHome;
    private String serverHome;

    private String snmpUrl;
    private String restApiUrl;

    private Boolean diameterEnabled;
    private String diameterUrl;
    private String diameterOriginHost;
    private String diameterOriginRealm;

    private Boolean radiusEnabled;
    private String radiusUrl;

    private EmailAgentData emailAgentData;
    private SMSAgentData smsAgentData;


    private String status;

    private ServerGroupServerInstanceRelData serverGroupServerInstanceRelData;
    private String serverGroupId;

    private Boolean offlineRncService;
    private String fileLocation;
    private List<ServerInstanceGroovyScriptData> groovyScriptDatas;


    public ServerInstanceData() {//Default No-Arg constructor if used as pojo with rest services
        super();
        groovyScriptDatas = new ArrayList<>();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "JMX_URL")
    @JsonIgnore
    public String getJmxUrl() {
        return jmxUrl;
    }

    public void setJmxUrl(String jmxUrl) {
        this.jmxUrl = jmxUrl;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "SERVER_HOME")
    public String getServerHome() {
        return serverHome;
    }

    public void setServerHome(String serverHome) {
        this.serverHome = serverHome;
    }

    @Column(name = "JAVA_HOME")
    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    @Override
    @Column(name = "STATUS")
    @JsonIgnore
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }


    @Transient
    public String getEmailAgentId() {
        if (this.emailAgentData != null) {
            return emailAgentData.getId();
        }
        return null;
    }

    public void setEmailAgentId(String emailAgentId) {
        if (Strings.isNullOrBlank(emailAgentId) == false) {
            EmailAgentData emailAgentData = new EmailAgentData();
            emailAgentData.setId(emailAgentId);
            this.emailAgentData = emailAgentData;
        }
    }

    @Transient
    public String getSmsAgentId() {
        if (this.smsAgentData != null) {
            return this.smsAgentData.getId();
        }
        return null;
    }

    public void setSmsAgentId(String smsAgentId) {
        if (Strings.isNullOrBlank(smsAgentId) == false) {
            SMSAgentData smsAgentData = new SMSAgentData();
            smsAgentData.setId(smsAgentId);
            this.smsAgentData = smsAgentData;
        }
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "EMAIL_AGENT_ID")
    public EmailAgentData getEmailAgentData() {
        return emailAgentData;
    }

    public void setEmailAgentData(EmailAgentData emailAgentData) {
        this.emailAgentData = emailAgentData;
    }


    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "SMS_AGENT_ID")
    public SMSAgentData getSmsAgentData() {
        return smsAgentData;
    }

    public void setSmsAgentData(SMSAgentData smsAgentData) {
        this.smsAgentData = smsAgentData;
    }

    @Column(name = "SNMP_URL")
    public String getSnmpUrl() {
        return snmpUrl;
    }

    public void setSnmpUrl(String snmpUrl) {
        this.snmpUrl = snmpUrl;
    }

    @Column(name = "REST_API_URL")
    public String getRestApiUrl() {
        return restApiUrl;
    }

    public void setRestApiUrl(String restApiUrl) {
        this.restApiUrl = restApiUrl;
    }

    @Column(name = "DIA_IS_ENABLE")
    public Boolean getDiameterEnabled() {
        return diameterEnabled;
    }

    public void setDiameterEnabled(Boolean diameterEnabled) {
        this.diameterEnabled = diameterEnabled;
    }

    @Column(name = "DIA_URL")
    public String getDiameterUrl() {
        return diameterUrl;
    }

    public void setDiameterUrl(String diameterUrl) {
        this.diameterUrl = diameterUrl;
    }

    @Column(name = "DIA_ORIGIN_HOST")
    public String getDiameterOriginHost() {
        return diameterOriginHost;
    }

    public void setDiameterOriginHost(String diameterOriginHost) {
        this.diameterOriginHost = diameterOriginHost;
    }

    @Column(name = "DIA_ORIGIN_REALM")
    public String getDiameterOriginRealm() {
        return diameterOriginRealm;
    }

    public void setDiameterOriginRealm(String diameterOriginRealm) {
        this.diameterOriginRealm = diameterOriginRealm;
    }

    @Column(name = "RAD_IS_ENABLE")
    public Boolean getRadiusEnabled() {
        return radiusEnabled;
    }

    public void setRadiusEnabled(Boolean radiusEnabled) {
        this.radiusEnabled = radiusEnabled;
    }

    @Column(name = "RAD_URL")
    public String getRadiusUrl() {
        return radiusUrl;
    }

    public void setRadiusUrl(String radiusUrl) {
        this.radiusUrl = radiusUrl;
    }

    @Column(name = "OFFLINE_RNC_SERVICE")
    public Boolean getOfflineRncService() {
        return offlineRncService;
    }

    public void setOfflineRncService(Boolean offlineRncService) {
        this.offlineRncService = offlineRncService;
    }

    @Column(name = "FILE_LOCATION")
    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Override
    @Transient
    @JsonIgnore
    public String getGroups() {
        if(getServerGroupServerInstanceRelData() != null) {
            return getServerGroupServerInstanceRelData().getServerGroupData().getGroups();
        }
        return super.getGroups();

    }

    @Transient
    @JsonIgnore
    @Override
    public String getGroupNames() {
        if(getServerGroupServerInstanceRelData() != null) {
            return getServerGroupServerInstanceRelData().getServerGroupData().getGroupNames();
        }
        return super.getGroupNames();
    }

    @JsonIgnore
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "serverInstanceData", orphanRemoval = true)
    public ServerGroupServerInstanceRelData getServerGroupServerInstanceRelData() {
        return serverGroupServerInstanceRelData;
    }

    public void setServerGroupServerInstanceRelData(ServerGroupServerInstanceRelData serverGroupServerInstanceRelData) {
        this.serverGroupServerInstanceRelData = serverGroupServerInstanceRelData;
    }

    @OneToMany(cascade = { CascadeType.ALL },mappedBy = "serverInstanceData" , fetch = FetchType.LAZY, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy(clause="ORDER_NUMBER ASC")
    public List<ServerInstanceGroovyScriptData> getGroovyScriptDatas() {
        return groovyScriptDatas;
    }

    public void setGroovyScriptDatas(List<ServerInstanceGroovyScriptData> groovyScriptDatas) {
        this.groovyScriptDatas = groovyScriptDatas;
    }

    @Transient
    @Override
    public String getResourceName() {
        return getName();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.JMX_URL,jmxUrl);
        jsonObject.addProperty(FieldValueConstants.SNMP_URL, snmpUrl);
        jsonObject.addProperty(FieldValueConstants.REST_API_URL, restApiUrl);

        jsonObject.addProperty(FieldValueConstants.DIAMETER_ENABLED, diameterEnabled);
        jsonObject.addProperty(FieldValueConstants.DIAMETER_URL, diameterUrl);
        jsonObject.addProperty(FieldValueConstants.ORIGIN_HOST, diameterOriginHost);
        jsonObject.addProperty(FieldValueConstants.REALM, diameterOriginRealm);

        jsonObject.addProperty(FieldValueConstants.RADIUS_ENABLED, radiusEnabled);
        jsonObject.addProperty(FieldValueConstants.RADIUS_URL,radiusUrl);
        if(emailAgentData != null) {
            jsonObject.addProperty("Email Agent", emailAgentData.getName());
        }
        if(smsAgentData != null) {
            jsonObject.addProperty("SMS Agent", smsAgentData.getName());
        }
        if(Collectionz.isNullOrEmpty(groovyScriptDatas) == false){
            groovyScriptDatas.forEach(groovyScriptData -> jsonObject.add(groovyScriptData.getScriptName(),groovyScriptData.toJson()));
        }
        return jsonObject;
    }

    @Transient
    public String getServerGroupId() {
        if (Strings.isNullOrBlank(serverGroupId)) {
            if (getServerGroupServerInstanceRelData() != null) {
                return getServerGroupServerInstanceRelData().getServerGroupData().getId();
            }
        }
        return serverGroupId;
    }

    public void setServerGroupId(String serverGroupId) {
        this.serverGroupId = serverGroupId;
    }

    @Transient
    @Override
    public String getAuditableId() {
        return getServerGroupServerInstanceRelData().getServerGroupData().getId();
    }

    @Transient
    @Override
    public ResourceData getAuditableResource() {
        return getServerGroupServerInstanceRelData().getServerGroupData();
    }

    @Override
    @Transient
    public String getHierarchy() {
        return getServerGroupServerInstanceRelData().getServerGroupData().getHierarchy() + "<br>" + getId() + "<br>" + name;
    }

    @Override
    public String toString() {
        return "ServerInstanceData{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", jmxUrl='" + jmxUrl + '\'' +
                ", javaHome='" + javaHome + '\'' +
                ", serverHome='" + serverHome + '\'' +
                ", snmpUrl='" + snmpUrl + '\'' +
                ", restApiUrl='" + restApiUrl + '\'' +
                ", diameterEnabled=" + diameterEnabled +
                ", diameterUrl='" + diameterUrl + '\'' +
                ", diameterOriginHost='" + diameterOriginHost + '\'' +
                ", diameterOriginRealm='" + diameterOriginRealm + '\'' +
                ", radiusEnabled=" + radiusEnabled +
                ", radiusUrl='" + radiusUrl + '\'' +
                ", emailAgent='" + getEmailAgentId() + '\'' +
                ", smsAgent='" + getSmsAgentId() + '\'' +
                ", status='" + status + '\'' +
                ", serverGroupServerInstanceRelData=" + serverGroupServerInstanceRelData + '\'' +
                ", offlineRncService=" + offlineRncService +
                '}';
    }
}