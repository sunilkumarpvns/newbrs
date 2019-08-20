package com.elitecore.corenetvertex.sm.pccservicepolicy;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity (name = "com.elitecore.corenetvertex.sm.pccservicepolicy.PccServicePolicyData")
@Table(name="TBLM_PCC_SERVICE_POLICY")
public class PccServicePolicyData extends ResourceData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String ruleset;
    private String action;

    private String subscriberLookupOn;
    private String identityAttribute;
    private String unknownUserAction;
    private ProductOfferData unknownUserProductOffer;

    private String unknownUserPkgId;
    private DiameterGatewayData syGateway;
    private String syGatewayId;
    private String syMode;
    private DriverData policyCdrDriver;
    private String policyCdrDriverId;
    private DriverData chargingCdrDriver;
    private String chargingCdrDriverId;
    private Integer orderNumber;
    private String status;

    public PccServicePolicyData(){
        super();
    }

    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="RULESET")
    public String getRuleset() {
        return ruleset;
    }

    public void setRuleset(String ruleset) {
        this.ruleset = ruleset;
    }

    @Column(name="ACTION")
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Column(name="SUB_LOOKUP_ON")
    public String getSubscriberLookupOn() {
        return subscriberLookupOn;
    }

    public void setSubscriberLookupOn(String subscriberLookupOn) {
        this.subscriberLookupOn = subscriberLookupOn;
    }

    @Column(name="IDENTITY_ATTRIBUTE")
    public String getIdentityAttribute() {
        return identityAttribute;
    }

    public void setIdentityAttribute(String identityAttribute) {
        this.identityAttribute = identityAttribute;
    }

    @Column(name="UNKNOWN_USER_ACTION")
    public String getUnknownUserAction() {
        return unknownUserAction;
    }

    public void setUnknownUserAction(String unknownUserAction) {
        this.unknownUserAction = unknownUserAction;
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name="UNKNOWN_USER_PACKAGE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    public ProductOfferData getUnknownUserProductOffer() {
        return unknownUserProductOffer;
    }

    public void setUnknownUserProductOffer(ProductOfferData unknownUserProductOffer) {
        this.unknownUserProductOffer = unknownUserProductOffer;
    }

    @Column(name = "UNKNOWN_USER_PACKAGE_ID")
    public String getUnknownUserPkgId() {
        return unknownUserPkgId;
    }

    public void setUnknownUserPkgId(String unknownUserPkgId) {
        this.unknownUserPkgId = unknownUserPkgId;
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name="SY_GATEWAY_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    public DiameterGatewayData getSyGateway() {
        return syGateway;
    }

    public void setSyGateway(DiameterGatewayData syGateway) {
        this.syGateway = syGateway;
    }

    @Column(name = "SY_GATEWAY_ID")
    public String getSyGatewayId() {
        return syGatewayId;
    }

    public void setSyGatewayId(String syGatewayId) {
        this.syGatewayId = syGatewayId;
    }

    @Column(name="SY_MODE")
    public String getSyMode() {
        return syMode;
    }

    public void setSyMode(String syMode) {
        this.syMode = syMode;
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "POLICY_CDR_DRIVER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    public DriverData getPolicyCdrDriver() {
        return policyCdrDriver;
    }

    public void setPolicyCdrDriver(DriverData policyCdrDriver) {
        this.policyCdrDriver = policyCdrDriver;
    }

    @Column(name = "POLICY_CDR_DRIVER_ID")
    public String getPolicyCdrDriverId() {
        return policyCdrDriverId;
    }

    public void setPolicyCdrDriverId(String policyCdrDriverId) {
        this.policyCdrDriverId = policyCdrDriverId;
    }

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "CHARGING_CDR_DRIVER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public DriverData getChargingCdrDriver() {
        return chargingCdrDriver;
    }

    public void setChargingCdrDriver(DriverData chargingCdrDriver) {
        this.chargingCdrDriver = chargingCdrDriver;
    }

    @Column(name = "CHARGING_CDR_DRIVER_ID")
    public String getChargingCdrDriverId() {
        return chargingCdrDriverId;
    }

    public void setChargingCdrDriverId(String chargingCdrDriverId) {
        this.chargingCdrDriverId = chargingCdrDriverId;
    }

    @JsonIgnore
    @Column(name="ORDER_NUMBER")
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    @Column(name="STATUS")
    public String getStatus() {
        return status;
    }

    @Override
    @Transient
    @JsonIgnore
    public String getGroups() {
        return super.getGroups();
    }

    @Override
    @Transient
    @JsonIgnore
    public String getGroupNames() {
        return super.getGroupNames();
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    @Transient
    public String getHierarchy() {
        return getId() + "<br>" + name;
    }

    @Override
    public JsonObject toJson() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME,name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION,description);
        jsonObject.addProperty("ruleSet",ruleset);
        jsonObject.addProperty("action",action);
        jsonObject.addProperty("subscriberLookupOn",subscriberLookupOn);
        jsonObject.addProperty("identityAttribute",identityAttribute);
        jsonObject.addProperty("unknownUserAction", unknownUserAction);

        if(getUnknownUserProductOffer() != null) {
            jsonObject.addProperty("unknownUserProductOffer", getUnknownUserProductOffer().getName());
        }

        jsonObject.addProperty("syMode",syMode);

        if(getChargingCdrDriver()!=null) {
            jsonObject.addProperty("chargingCdrDriver", chargingCdrDriver.getName());
        }

        if(getPolicyCdrDriver()!=null){
            jsonObject.addProperty("policyCdrDriver",policyCdrDriver.getName());
        }

        jsonObject.addProperty("orderNumber",orderNumber);
        jsonObject.addProperty("status", status);
        return jsonObject;
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }
}
