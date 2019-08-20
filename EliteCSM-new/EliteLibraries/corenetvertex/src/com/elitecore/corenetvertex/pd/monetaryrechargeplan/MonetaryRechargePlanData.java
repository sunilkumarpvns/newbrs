package com.elitecore.corenetvertex.pd.monetaryrechargeplan;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "com.elitecore.corenetvertex.pd.monetaryrechargeplan.MonetaryRechargePlanData")
@Table(name = "TBLM_RECHARGE_PLAN")
public class MonetaryRechargePlanData extends ResourceData implements Serializable{
    private BigDecimal price;
    private BigDecimal amount;
    private Integer validity;
    private ValidityPeriodUnit validityPeriodUnit = ValidityPeriodUnit.DAY;
    private String description;
    private String name;
    @SerializedName(FieldValueConstants.MODE)private String packageMode;
    @SerializedName(FieldValueConstants.PARAM1) private String param1;
    @SerializedName(FieldValueConstants.PARAM2)private String param2;

    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "PRICE")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "AMOUNT")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "VALIDITY")
    public Integer getValidity() {
        return validity;
    }

    public void setValidity(Integer validity) {
        this.validity = validity;
    }

    @Column(name="VALIDITY_PERIOD_UNIT")
    public ValidityPeriodUnit getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    public void setValidityPeriodUnit(ValidityPeriodUnit validityPeriodUnit) {
        this.validityPeriodUnit = validityPeriodUnit;
    }

    @Column(name="PACKAGE_MODE")
    public String getPackageMode() {
        return packageMode;
    }

    public void setPackageMode(String packageMode) {
        this.packageMode = packageMode;
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

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    @Override
    public String getResourceName() {
        return this.getName();
    }

    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME,name);
        jsonObject.addProperty(FieldValueConstants.PRICE,price);
        jsonObject.addProperty(FieldValueConstants.AMOUNT,amount);
        jsonObject.addProperty(FieldValueConstants.VALIDITIY_PERIOD,validity);
        jsonObject.addProperty(FieldValueConstants.VALIDITY_PERIOD_UNIT, validityPeriodUnit.displayValue);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION,description);
        jsonObject.addProperty(FieldValueConstants.MODE, packageMode);
        jsonObject.addProperty(FieldValueConstants.GROUPS, getGroupNames());
        jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
        return jsonObject;
    }
}
