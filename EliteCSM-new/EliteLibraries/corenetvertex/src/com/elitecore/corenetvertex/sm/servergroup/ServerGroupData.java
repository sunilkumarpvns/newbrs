package com.elitecore.corenetvertex.sm.servergroup;

import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * Created by kirpalsinh on 2/8/17.
 */
@Entity(name = "com.elitecore.corenetvertex.sm.servergroup.ServerGroupData")
@Table(name="TBLM_SERVER_INSTANCE_GROUP")
public class ServerGroupData  extends ResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private Integer orderNo;
    private Boolean sessionSynchronization = true;
    private List<ServerInstanceData> serverInstances;
    private DatabaseData databaseData;



    private DatabaseData notificationDataSourceData;
    private String sessionDataSourceId;
    private String notificationDataSourceId;
    private List<String> serverInstanceIds;
    
    private String serverGroupType;
    

    public ServerGroupData() {//Default No-Arg constructor if used as pojo with rest services
        super();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Column(name="GROUPS")
    public String getGroups() {
        return super.getGroups();
    }

    @Override
    public void setGroups(String groups) {
        super.setGroups(groups);
    }

    @JsonIgnore
    @Column(name="ORDER_NO")
    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @JsonIgnore
    @Column(name="SESSION_SYNCHRONIZATION")
    public Boolean getSessionSynchronization() {
        return sessionSynchronization   ;
    }

    public void setSessionSynchronization(Boolean sessionSynchronization) {
        if(sessionSynchronization == null) {
            sessionSynchronization = true;
        }
        this.sessionSynchronization = sessionSynchronization;
    }

    @JsonIgnore
    @OneToOne (cascade = {CascadeType.DETACH,CascadeType.MERGE})
    @JoinColumn(name = "SESSION_DATA_SOURCE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public DatabaseData getDatabaseData() {
        return databaseData;
    }

    public void setDatabaseData(DatabaseData databaseData) {
        this.databaseData = databaseData;
    }

    @JsonIgnore
    @OneToOne (cascade = {CascadeType.DETACH,CascadeType.MERGE})
    @JoinColumn(name = "NOTIFICATION_DATA_SOURCE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public DatabaseData getNotificationDataSourceData() {
        return notificationDataSourceData;
    }

    public void setNotificationDataSourceData(DatabaseData notificationDataSourceData) {
        this.notificationDataSourceData = notificationDataSourceData;
    }

    @Column(name="NOTIFICATION_DATA_SOURCE_ID")
    public String getNotificationDataSourceId() {
        return notificationDataSourceId;
    }

    public void setNotificationDataSourceId(String notificationDataSourceId) {
        this.notificationDataSourceId = notificationDataSourceId;
    }

    @Column(name="SESSION_DATA_SOURCE_ID")
    public String getSessionDataSourceId() {
        return sessionDataSourceId;
    }

    public void setSessionDataSourceId(String sessionDataSourceId) {
        this.sessionDataSourceId = sessionDataSourceId;
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }


    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = false)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "TBLM_SERVER_INSTANCE_GROUP_REL",  joinColumns = { @JoinColumn(name = "SERVER_INSTANCE_GROUP_ID", nullable = false) },
            inverseJoinColumns = { @JoinColumn(name = "SERVER_INSTANCE_ID", nullable = false) })
    public List<ServerInstanceData> getServerInstances() {
        return serverInstances;
    }

    public void setServerInstances(List<ServerInstanceData> serverInstances) {
        this.serverInstances = serverInstances;
    }

    
    @Column(name="SERVER_GROUP_TYPE",updatable = false)
    public String getServerGroupType() {
		return serverGroupType;
	}

	public void setServerGroupType(String serverGroupType) {
		this.serverGroupType = serverGroupType;
	}

	@Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name" , name);
        jsonObject.addProperty("Type", serverGroupType);
        jsonObject.addProperty("Groups", getGroupNames());
        if(databaseData != null) {
            jsonObject.addProperty("Session Datasource", databaseData.getName());
        }
        if(notificationDataSourceData != null) {
            jsonObject.addProperty("Notification Datasource", notificationDataSourceData.getName());
        }
        if(serverInstances != null) {
            JsonArray serverInstanceArray = new JsonArray();
            for (ServerInstanceData serverInstance : serverInstances) {
                serverInstanceArray.add(serverInstance.toJson());
            }
            jsonObject.add("Server Instance", serverInstanceArray);
        }
        return jsonObject;
    }

    @Transient
    public List<String> getServerInstanceIds() {
        return serverInstanceIds;
    }

    public void setServerInstanceIds(List<String> serverInstanceIds) {
        this.serverInstanceIds = serverInstanceIds;
    }

    @Override
    @JsonIgnore
    @Transient
    public String getGroupNames(){
        return super.getGroupNames();
    }

    @Override
    @Transient
    public String getHierarchy() {
        return getId() + "<br>" + name;
    }


    @Override
    public String toString() {
        return "ServerGroupData{" +
                "name='" + name + '\'' +
                ", orderNo=" + orderNo +
                ", sessionSynchronization=" + sessionSynchronization +
                ", serverInstances=" + serverInstances +
                ", databaseData=" + databaseData +
                ", sessionDataSourceId='" + sessionDataSourceId + '\'' +
                ", serverInstanceIds=" + serverInstanceIds +
                '}';
    }
}
