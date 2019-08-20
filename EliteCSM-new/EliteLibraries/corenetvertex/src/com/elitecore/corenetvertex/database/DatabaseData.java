package com.elitecore.corenetvertex.database;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Used to manage Database Datasource related information with DB
 * Created by dhyani on 22/8/17.
 */
@Entity
@Table(name = "TBLM_DATABASE_DS")
public class DatabaseData extends DefaultGroupResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String connectionUrl = "jdbc:oracle:thin:@127.0.0.1:1521/orcl";
    private String userName;
    private transient String password;
    private Integer minimumPool = 1;
    private Integer maximumPool = 1;
    private Integer queryTimeout = 1000;
    private Integer statusCheckDuration = 120;

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "CONNECTION_URL")
    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    @Column(name = "USERNAME")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    @Column(name = "PASSWORD")
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "MINIMUM_POOL")
    public Integer getMinimumPool() {
        return minimumPool;
    }

    public void setMinimumPool(Integer minimumPool) {
        this.minimumPool = minimumPool;
    }

    @Column(name = "MAXIMUM_POOL")
    public Integer getMaximumPool() {
        return maximumPool;
    }

    public void setMaximumPool(Integer maximumPool) {
        this.maximumPool = maximumPool;
    }

    @Column(name = "QUERY_TIMEOUT")
    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    @Column(name = "STATUS_CHECK_DURATION")
    public Integer getStatusCheckDuration() {
        return statusCheckDuration;
    }

    public void setStatusCheckDuration(Integer statusCheckDuration) {
        this.statusCheckDuration = statusCheckDuration;
    }

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Transient
    @XmlTransient
    @Override
    public String getHierarchy() {
        return getId() + "<br>" + name;
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
        jsonObject.addProperty(FieldValueConstants.CONNECTION_URL, connectionUrl);
        jsonObject.addProperty(FieldValueConstants.USERNAME, userName);
        jsonObject.addProperty(FieldValueConstants.STATUS_CHECK_DURATION, statusCheckDuration);
        jsonObject.addProperty(FieldValueConstants.MINIMUM_POOL, minimumPool);
        jsonObject.addProperty(FieldValueConstants.MAXIMUM_POOL, maximumPool);
        jsonObject.addProperty(FieldValueConstants.QUERY_TIMEOUT, queryTimeout);
        return jsonObject;
    }

    @Override
    public String toString() {
        return "DatabaseData{" +
                "name='" + name + '\'' +
                ", connectionUrl='" + connectionUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", minimumPool=" + minimumPool +
                ", maximumPool=" + maximumPool +
                ", queryTimeout=" + queryTimeout +
                ", statusCheckDuration=" + statusCheckDuration +
                '}';
    }
}
