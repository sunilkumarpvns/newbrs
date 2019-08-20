package com.elitecore.corenetvertex.pd.lrn;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity(name = "com.elitecore.corenetvertex.pd.lrn.LrnData")
@Table(name = "TBLM_LRN")
public class LrnData extends DefaultGroupResourceData implements Serializable {
    private static final long serialVersionUID = 5518534212348970291L;
    private String lrn;
    private OperatorData operatorData;
    private NetworkData networkData;

    @Column(name = "LRN")
    public String getLrn() {
        return lrn;
    }

    public void setLrn(String lrn) {
        this.lrn = lrn;
    }

    @OneToOne
    @JoinColumn(name = "OPERATOR_ID", nullable = false)
    @JsonIgnore
    public OperatorData getOperatorData() {
        return operatorData;
    }

    public void setOperatorData(OperatorData operatorData) {
        this.operatorData = operatorData;
    }

    @OneToOne
    @JoinColumn(name = "NETWORK_ID")
    @JsonIgnore
    public NetworkData getNetworkData() {
        return networkData;
    }

    public void setNetworkData(NetworkData networkData) {
        this.networkData = networkData;
    }

    @Transient
    public String getOperatorId() {
        if (this.getOperatorData() != null) {
            return getOperatorData().getId();
        }
        return null;
    }

    public void setOperatorId(String operatorId) {
        if (Strings.isNullOrBlank(operatorId) == false) {
            OperatorData operatorData = new OperatorData();
            operatorData.setId(operatorId);
            this.operatorData = operatorData;
        }
    }

    @Transient
    public String getNetworkId() {
        if (this.getNetworkData() != null) {
            return getNetworkData().getId();
        }
        return null;
    }

    public void setNetworkId(String networkId) {
        if (Strings.isNullOrBlank(networkId) == false) {
            NetworkData networkData = new NetworkData();
            networkData.setId(networkId);
            this.networkData = networkData;
        }
    }

    @JsonIgnore
    @Transient
    @Override
    public String getResourceName() {
        return getLrn();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.LRN, lrn);
        jsonObject.addProperty(FieldValueConstants.OPERATOR_DATA, operatorData.getName());
        if(networkData!=null){
            jsonObject.addProperty(FieldValueConstants.NETWORK_DATA, networkData.getName());
        }
        jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
        jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
        return jsonObject;
    }
}
