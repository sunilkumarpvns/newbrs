package com.elitecore.corenetvertex.sm.notificationagents;



import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="com.elitecore.corenetvertex.sm.notificationagents.SMSAgentData")
@Table(name="TBLM_SMS_AGENT")
public class SMSAgentData extends DefaultGroupResourceData {
    @SerializedName(FieldValueConstants.NAME)private String name;
    private String serviceURL;
    private transient String password;


    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="SERVICE_URL")
    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    @Column(name="PASSWORD")
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
     }

    @Override
    @Column(name="STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name",getName());
        jsonObject.addProperty("Service Url",getServiceURL());
        return jsonObject;
    }
}
