package com.elitecore.corenetvertex.sm.serverinstance;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity(name = "com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceGroovyScriptData")
@Table(name = "TBLD_SRVR_INST_GROOVY_SCRIPT")
public class ServerInstanceGroovyScriptData implements Serializable {

    private String id;
    private int orderNumber;
    private String scriptName;
    private String argument;
    private transient ServerInstanceData serverInstanceData;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "ORDER_NUMBER")
    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Column(name = "SCRIPT_NAME")
    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    @Column(name = "ARGUMENT")
    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SERVER_INSTANCE_ID")
    @JsonIgnore
    public ServerInstanceData getServerInstanceData() {
        return serverInstanceData;
    }

    public void setServerInstanceData(ServerInstanceData serverInstanceData) {
        this.serverInstanceData = serverInstanceData;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.ARGUMENT, argument);
        jsonObject.addProperty(FieldValueConstants.ORDER_NUMBER, orderNumber);
        return jsonObject;
    }

}
