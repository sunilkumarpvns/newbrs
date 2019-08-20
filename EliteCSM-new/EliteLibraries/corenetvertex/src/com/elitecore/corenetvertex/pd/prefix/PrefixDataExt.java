package com.elitecore.corenetvertex.pd.prefix;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.core.validator.PrefixValidator;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Used to prefix related information with DB Created by
 */

@Entity(name = "com.elitecore.corenetvertex.pd.prefix.PrefixDataExt")
@Table(name = "TBLM_PREFIX")
@Import(required = true, validatorClass = PrefixValidator.class, importClass = PrefixDataImportOperation.class)
public class PrefixDataExt extends DefaultGroupResourceData implements Serializable {

    private static final long serialVersionUID = 5518534212348970291L;
    private String prefix;
    private CountryData countryData;
    private OperatorData operatorData;
    private NetworkData networkData;

    @Column(name = "PREFIX")
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @OneToOne
    @JoinColumn(name = "COUNTRY_ID", nullable = false)
    @JsonIgnore
    public CountryData getCountryData() {
        return countryData;
    }

    public void setCountryData(CountryData countryData) {
        this.countryData = countryData;
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

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @JsonIgnore
    @Transient
    @Override
    public String getResourceName() {
        if (getPrefix() != null)
            return getPrefix();
        else
            return null;
    }

    @Transient
    public String getCountryId() {
        if (this.getCountryData() != null) {
            return getCountryData().getId();
        }
        return null;
    }

    public void setCountryId(String countryId) {
        if (Strings.isNullOrBlank(countryId) == false) {
            CountryData countryDataTemp = new CountryData();
            countryDataTemp.setId(countryId);
            this.countryData = countryDataTemp;
        }
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
            OperatorData operatorDataTemp = new OperatorData();
            operatorDataTemp.setId(operatorId);
            this.operatorData = operatorDataTemp;
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
            NetworkData networkDataTemp = new NetworkData();
            networkDataTemp.setId(networkId);
            this.networkData = networkDataTemp;
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.PREFIX, prefix);
        if(countryData!=null){
            jsonObject.addProperty(FieldValueConstants.COUNTRY_DATA,countryData.getName());
        }
        if(operatorData != null){
            jsonObject.addProperty(FieldValueConstants.OPERATOR_DATA, operatorData.getName());
        }
        if(networkData!=null){
            jsonObject.addProperty(FieldValueConstants.NETWORK_DATA, networkData.getName());
        }
        jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
        jsonObject.addProperty(FieldValueConstants.GROUPS, getGroups());
        return jsonObject;
    }

}