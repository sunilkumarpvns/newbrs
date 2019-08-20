package com.elitecore.corenetvertex.sm.gateway;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Gateway configuration that will allow to configure properties for diameter gateway
 */
@Entity(name="com.elitecore.corenetvertex.sm.DiameterGatewayData")
@Table(name = "TBLM_DIAMETER_GATEWAY")
public class DiameterGatewayData extends DefaultGroupResourceData implements Serializable {
    private static final long serialVersionUID = 1L;
    @SerializedName(FieldValueConstants.NAME) private String name;
    @SerializedName(FieldValueConstants.DESCRIPTION)private String description;
    private transient DiameterGatewayProfileData diameterGatewayProfileData;
    @SerializedName(FieldValueConstants.POLICY_ENFORCEMENT_METHOD)private String policyEnforcementMethod;
    @SerializedName(FieldValueConstants.CONNECTION_URL)private String connectionURL = "127.0.0.1";
    @SerializedName(FieldValueConstants.HOST_IDENTITY)private String hostIdentity;
    @SerializedName(FieldValueConstants.REALM)private String realm;
    @SerializedName(FieldValueConstants.LOCAL_ADDRESS)private String localAddress;
    @SerializedName(FieldValueConstants.ALTERNATE_HOST_ID)private String alternateHostId;
    @SerializedName(FieldValueConstants.DIAMETER_GATEWAY_PROFILE_ID)private String diameterGatewayProfileId;

    @SerializedName(FieldValueConstants.REQUEST_TIMEOUT)private Integer requestTimeout = 3000;
    @SerializedName(FieldValueConstants.RETRANSMISSION_COUNT)private Integer retransmissionCount = 0;
    private transient DiameterGatewayData alternateHost;

    @Column( name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column( name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="DIAMETER_PROFILE_ID")
    @XmlTransient
    @JsonIgnore
    public DiameterGatewayProfileData getDiameterGatewayProfileData() {
        return diameterGatewayProfileData;
    }

    public void setDiameterGatewayProfileData(DiameterGatewayProfileData diameterGatewayProfileData) {
        if(diameterGatewayProfileData != null){
            setDiameterGatewayProfileId(diameterGatewayProfileData.getId());
        }
        this.diameterGatewayProfileData = diameterGatewayProfileData;
    }


    @Column( name = "POLICY_ENFORCEMENT_METHOD")
    public String getPolicyEnforcementMethod() {
        return policyEnforcementMethod;
    }

    public void setPolicyEnforcementMethod(String policyEnforcementMethod) {
        this.policyEnforcementMethod = policyEnforcementMethod;
    }

    @Column( name = "CONNECTION_URL")
    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
            this.connectionURL = connectionURL;
    }

    @Column( name = "HOST_IDENTITY")
    public String getHostIdentity() {
        return hostIdentity;
    }

    public void setHostIdentity(String diameterHostIdentity) {
        this.hostIdentity = diameterHostIdentity;
    }

    @Column( name = "REALM")
    public String getRealm() {
        return realm;
    }

    public void setRealm(String diameterRealm) {
        this.realm = diameterRealm;
    }

    @Column( name = "LOCAL_ADDRESS")
    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String diameterLocalAddress) {
        this.localAddress = diameterLocalAddress;
    }

    @Column( name = "REQUEST_TIMEOUT")
    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        if(requestTimeout == null){
            this.requestTimeout = 3000;
        }else {
            this.requestTimeout = requestTimeout;
        }
    }

    @Column( name = "RETRANSMISSION_COUNT")
    public Integer getRetransmissionCount() {
        return retransmissionCount;
    }

    public void setRetransmissionCount(Integer retransmissionCount) {
        if(retransmissionCount == null){
            this.retransmissionCount = 0;
        }else {
            this.retransmissionCount = retransmissionCount;
        }
    }

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn( name = "ALTERNATE_HOST_ID")
    @JsonIgnore
    public DiameterGatewayData getAlternateHost() {
        return alternateHost;
    }

    public void setAlternateHost(DiameterGatewayData diameterAlternateHost) {
        if(diameterAlternateHost != null){
            setAlternateHostId(diameterAlternateHost.getId());
        }
        this.alternateHost = diameterAlternateHost;
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject =  new JsonObject();
        jsonObject.addProperty("Name", name);
        jsonObject.addProperty("Description", description);
        jsonObject.addProperty("Policy Enforcement Method",policyEnforcementMethod);
        jsonObject.addProperty("Connection URL", connectionURL);
        jsonObject.addProperty("Realm", realm);
        jsonObject.addProperty("Diameter Gateway Profile Name",diameterGatewayProfileData != null ? diameterGatewayProfileData.getName() : null);
        jsonObject.addProperty("Request Timeout", requestTimeout);
        jsonObject.addProperty("Retransmission Count", retransmissionCount);
        jsonObject.addProperty("Status", getStatus());
        jsonObject.addProperty("Groups", getGroupNames());
        jsonObject.addProperty("Alternate Host Name",alternateHost != null ? alternateHost.getName() : null);
        return jsonObject;
    }

    @Transient
    public String getAlternateHostId() {
        return alternateHostId;
    }

    public void setAlternateHostId(String alternateHostId) {
        this.alternateHostId = alternateHostId;
    }

    @Transient
    public String getDiameterGatewayProfileId() {
        return diameterGatewayProfileId;
    }

    public void setDiameterGatewayProfileId(String diameterGatewayProfileId) {
        this.diameterGatewayProfileId = diameterGatewayProfileId;
    }
}
