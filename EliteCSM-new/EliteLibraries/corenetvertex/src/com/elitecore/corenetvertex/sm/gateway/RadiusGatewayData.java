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
 * Gateway configuration that will allow to configure properties for radius gateway
 */
@Entity
@Table(name = "TBLM_RADIUS_GATEWAY")
public class RadiusGatewayData extends DefaultGroupResourceData implements Serializable{

    private static final long serialVersionUID = 1L;
    @SerializedName(FieldValueConstants.NAME) private String name;
    @SerializedName(FieldValueConstants.DESCRIPTION)private String description;
    private transient RadiusGatewayProfileData radiusGatewayProfileData;
    @SerializedName(FieldValueConstants.POLICY_ENFORCEMENT_METHOD)private String policyEnforcementMethod;
    @SerializedName(FieldValueConstants.CONNECTION_URL)private String connectionURL = "127.0.0.1";
    @SerializedName(FieldValueConstants.SHARED_SECRET)private String sharedSecret;
    @SerializedName(FieldValueConstants.MINIMUM_LOCAL_PORT)private Integer minLocalPort;
    @SerializedName(FieldValueConstants.RADIUS_GATEWAY_PROFILE_ID)private String radiusGatewayProfileId;

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

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="RADIUS_PROFILE_ID")
    @XmlTransient
    @JsonIgnore
    public RadiusGatewayProfileData getRadiusGatewayProfileData() {
        return radiusGatewayProfileData;
    }

    public void setRadiusGatewayProfileData(RadiusGatewayProfileData radiusGatewayProfileData) {
        if(radiusGatewayProfileData != null){
            setRadiusGatewayProfileId(radiusGatewayProfileData.getId());
        }
        this.radiusGatewayProfileData = radiusGatewayProfileData;
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

    @Column( name = "SHARED_SECRET")
    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    @Column( name = "MIN_LOCAL_PORT")
    public Integer getMinLocalPort() {
        return minLocalPort;
    }

    public void setMinLocalPort(Integer minLocalPort) {
        this.minLocalPort = minLocalPort;
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
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name", name);
        jsonObject.addProperty("Description", description);
        jsonObject.addProperty("Policy Enforcement Method",policyEnforcementMethod);
        jsonObject.addProperty("Connection URL",connectionURL);
        jsonObject.addProperty("Shared Secret", sharedSecret);
        jsonObject.addProperty("Radius Gateway Profile Name" , (radiusGatewayProfileData == null ? "" : radiusGatewayProfileData.getName()));
        jsonObject.addProperty("Minimum Local Port", minLocalPort);
        jsonObject.addProperty("Status", getStatus());
        jsonObject.addProperty("Groups", getGroupNames());
        return jsonObject;
    }

    @Transient
    public String getRadiusGatewayProfileId() {
        return radiusGatewayProfileId;
    }

    public void setRadiusGatewayProfileId(String radiusGatewayProfileId) {
        this.radiusGatewayProfileId = radiusGatewayProfileId;
    }
}
