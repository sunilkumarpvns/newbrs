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

@Entity(name = "com.elitecore.corenetvertex.sm.notificationagents.EmailAgentData")
@Table(name="TBLM_EMAIL_AGENT")
public class EmailAgentData extends DefaultGroupResourceData {

    @SerializedName(FieldValueConstants.NAME)private String name;
    private String emailHost;
    private String userName;
    private transient String password;
    private String fromAddress;


    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="EMAIL_HOST")
    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    @Column(name="EMAIL_USERNAME")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name="EMAIL_PASSWORD")
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name="EMAIL_FROM_ADDRESS")
    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    @Column(name = "STATUS")
    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Transient
    @Override
    public String getResourceName() {
        return getName();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name",getName());
        jsonObject.addProperty("Email Host",getEmailHost());
        jsonObject.addProperty("UserName",getUserName());
        jsonObject.addProperty("From Address",getFromAddress());
        return jsonObject;
    }
}