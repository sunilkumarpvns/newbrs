package com.elitecore.corenetvertex.sm.routing.mccmncroutingtable;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.routing.mccmncgroup.MccMncGroupData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
import java.util.List;

@Entity(name = "com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingTableData")
@Table(name = "TBLM_ROUTING_ENTRY")
public class RoutingTableData extends DefaultGroupResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private MccMncGroupData mccMncGroupData;
    private String roaming;
    private String action;
    private String realmCondition;
    private String type;
    private Integer orderNumber;

    private transient List<RoutingTableGatewayRelData> routingTableGatewayRelDataList;

    public RoutingTableData(){
        routingTableGatewayRelDataList = Collectionz.newArrayList();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @OneToOne
    @JoinColumn(name = "MCC_MNC_GROUP_ID")
    @JsonIgnore
    public MccMncGroupData getMccMncGroupData() {
        return mccMncGroupData;
    }

    public void setMccMncGroupData(MccMncGroupData mccMncGroupData) {
        this.mccMncGroupData = mccMncGroupData;
    }

    @Column(name = "ROAMING")
    public String getRoaming() {
        return roaming;
    }

    public void setRoaming(String roaming) {
        this.roaming = roaming;
    }

    @Column(name = "ACTION")
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Column(name = "REALM_CONDITION")
    public String getRealmCondition() {
        return realmCondition;
    }

    public void setRealmCondition(String realmCondition) {
        this.realmCondition = realmCondition;
    }

    @Column(name = "TYPE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "ORDER_NUMBER")
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "routingTableData", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    public List<RoutingTableGatewayRelData> getRoutingTableGatewayRelDataList() {
        return routingTableGatewayRelDataList;
    }

    public void setRoutingTableGatewayRelDataList(List<RoutingTableGatewayRelData> routingTableGatewayRelDataList) {
        this.routingTableGatewayRelDataList = routingTableGatewayRelDataList;
    }

    @Transient
    public String getMccMncGroupId() {
        if (this.getMccMncGroupData() != null) {
            return getMccMncGroupData().getId();
        }
        return null;
    }

    public void setMccMncGroupId(String mccMncGroupId) {
        if (Strings.isNullOrBlank(mccMncGroupId) == false) {
            MccMncGroupData mccMncGroupData = new MccMncGroupData();
            mccMncGroupData.setId(mccMncGroupId);
            this.mccMncGroupData = mccMncGroupData;
        }
    }

    @Override
    @Transient
    public String getResourceName() {
        return name;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name", name);
        jsonObject.addProperty("MCC MNC Group", mccMncGroupData != null ? mccMncGroupData.getName() : null);
        jsonObject.addProperty("Action", action);
        jsonObject.addProperty("Realm Condition", realmCondition);
        jsonObject.addProperty("Type", type);

        if (Collectionz.isNullOrEmpty(routingTableGatewayRelDataList) == false) {
            JsonObject gatewayJsonArray = new JsonObject();

            for (RoutingTableGatewayRelData routingTableGatewayRelData : routingTableGatewayRelDataList) {
                JsonObject weightage = new JsonObject();
                weightage.addProperty("Weightage", routingTableGatewayRelData.getWeightage());
                gatewayJsonArray.add(routingTableGatewayRelData.getDiameterGatewayData().getName(), weightage);
            }

            jsonObject.add("Gateways", gatewayJsonArray);
        }

        return jsonObject;
    }
}