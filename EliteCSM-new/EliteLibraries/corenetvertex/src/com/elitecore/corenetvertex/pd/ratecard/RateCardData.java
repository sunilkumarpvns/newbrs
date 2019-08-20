package com.elitecore.corenetvertex.pd.ratecard;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCard;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "com.elitecore.corenetvertex.pd.ratecard.RateCardData")
@Table(name = "TBLM_RATE_CARD")
public class RateCardData extends ResourceData implements Serializable{

    private static final long serialVersionUID = -9173903847327724345L;
    private String name;
    private String description;
    private String type;
    private transient RncPackageData rncPackageData;
    private MonetaryRateCardData monetaryRateCardData;
    private NonMonetaryRateCardData nonMonetaryRateCardData;
    private String rncPkgId;
    private String scope;
    private String chargingType;
    private String currency;

    public RateCardData(){
        monetaryRateCardData = new MonetaryRateCardData();
        nonMonetaryRateCardData = new NonMonetaryRateCardData();
    }



    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="TYPE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RNC_PACKAGE_ID")
    @XmlTransient
    @JsonIgnore
    public RncPackageData getRncPackageData() {
        return rncPackageData;
    }

    public void setRncPackageData(RncPackageData rncPackageData) {
        if (rncPackageData != null) {
            setRncPkgId(rncPackageData.getId());
        }
        this.rncPackageData = rncPackageData;
    }


    @Transient
    public String getRncPkgId() {
        return rncPkgId;
    }

    public void setRncPkgId(String rncPkgId) {
        this.rncPkgId = rncPkgId;
    }

    @OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "rateCardData")
    @Fetch(FetchMode.SELECT)
    public MonetaryRateCardData getMonetaryRateCardData() {
        return monetaryRateCardData;
    }

    public void setMonetaryRateCardData(MonetaryRateCardData monetaryRateCardData) {
        this.monetaryRateCardData = monetaryRateCardData;
    }

    @OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "rateCardData")
    @Fetch(FetchMode.SELECT)
    public NonMonetaryRateCardData getNonMonetaryRateCardData() {
        return nonMonetaryRateCardData;
    }

    public void setNonMonetaryRateCardData(NonMonetaryRateCardData nonMonetaryRateCardData) {
        this.nonMonetaryRateCardData = nonMonetaryRateCardData;
    }

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    @Column(name="GROUPS")
    public String getGroups() {
        return super.getGroups();
    }

    @Transient
    @Override
    @JsonIgnore
    public String getResourceName() {
        return getName();
    }

    public RateCardData copyModel() {
        RateCardData newrateCardData = new RateCardData();
        newrateCardData.setId(null);
        newrateCardData.setType(this.type);
        newrateCardData.setName(this.name);
        newrateCardData.setDescription(this.description);
        newrateCardData.setScope(this.scope);
        newrateCardData.setCurrency(this.currency);
        if (RateCardType.MONETARY.name().equalsIgnoreCase(this.type) && Objects.nonNull(monetaryRateCardData) ) {
            MonetaryRateCardData newmonetaryRateCardData =  this.monetaryRateCardData.copyModel();
            newmonetaryRateCardData.setRateCardData(newrateCardData);
            newrateCardData.setMonetaryRateCardData(newmonetaryRateCardData);
        }

        if (RateCardType.NON_MONETARY.name().equalsIgnoreCase(this.type) && Objects.nonNull(nonMonetaryRateCardData)) {
            NonMonetaryRateCardData newnonMonetaryRateCardData = (NonMonetaryRateCardData) this.nonMonetaryRateCardData.copyModel();
            newnonMonetaryRateCardData.setRateCardData(newrateCardData);
            newrateCardData.setNonMonetaryRateCardData(newnonMonetaryRateCardData);
        }
        return newrateCardData;
    }

    @Override
    public JsonObject toJson() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
        if(Objects.isNull(scope) == false && scope.equalsIgnoreCase(RateCardScope.GLOBAL.name())) {
            jsonObject.addProperty("Charging Type",chargingType);
        }
        jsonObject.addProperty(FieldValueConstants.CURRENCY, getCurrency());
        if(nonMonetaryRateCardData != null){
            mergeJsonValues(jsonObject, nonMonetaryRateCardData.toJson());
        }
        if(monetaryRateCardData != null){
            mergeJsonValues(jsonObject, monetaryRateCardData.toJson());
        }
        jsonObject.addProperty(FieldValueConstants.GROUPS, getGroupNames());
        return jsonObject;
    }

    private void mergeJsonValues(JsonObject val1, JsonObject val2){
        for(String key: val2.keySet()){
           if(val2.get(key).isJsonObject()){
               val1.add(key, val2.get(key).getAsJsonObject());
           } else {
               val1.add(key, val2.get(key));
           }
        }
    }

    @Column(name = "RATE_CARD_SCOPE")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Column(name="CHARGING_TYPE")
    public String getChargingType() {
        return chargingType;
    }

    public void setChargingType(String chargingType) {
        this.chargingType = chargingType;
    }

    @Column(name="CURRENCY")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    @Transient
    @Override
    public String getAuditableId() {
        if(scope.equalsIgnoreCase("LOCAL")) {
            return rncPackageData.getId();
        }
        return this.getId();
    }

    @Transient
    @Override
    public ResourceData getAuditableResource() {
        if(scope.equalsIgnoreCase("LOCAL")) {
            return rncPackageData;
        }
        return this;
    }

    @Override
    @Transient
    public String getHierarchy() {
        if(scope.equalsIgnoreCase("LOCAL")) {
            return rncPackageData.getHierarchy() + "<br>" + getId() + "<br>" + name;
        }
        return getId() + "<br>" + name;
    }

}

