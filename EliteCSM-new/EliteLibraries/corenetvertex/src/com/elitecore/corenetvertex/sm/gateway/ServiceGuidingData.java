package com.elitecore.corenetvertex.sm.gateway;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.service.ServiceData;
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
import javax.persistence.Transient;
import java.io.Serializable;

@Entity(name="com.elitecore.corenetvertex.sm.gateway.ServiceGuidingData")
@Table(name = "TBLD_GATEWAY_SERVICE_GUIDING")
public class ServiceGuidingData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private transient DiameterGatewayProfileData diameterGatewayProfileData;
    private transient RadiusGatewayProfileData radiusGatewayProfileData;
    private transient ServiceData serviceData;
    private transient String condition;
    private int orderNumber;
    private String serviceId;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "CONDITION")
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Column(name = "ORDER_NUMBER")
    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Transient
    public String getServiceId() {
        if(this.getServiceData()!=null) {
            this.serviceId = getServiceData().getId();
        }
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        if(Strings.isNullOrBlank(serviceId) == false){
            ServiceData serviceData = new ServiceData();
            serviceData.setId(serviceId);
            this.serviceData = serviceData;
        }
        this.serviceId = serviceId;
    }

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="DIAMETER_PROFILE_ID")
    @JsonIgnore
    public DiameterGatewayProfileData getDiameterGatewayProfileData() {
        return diameterGatewayProfileData;
    }

    public void setDiameterGatewayProfileData(DiameterGatewayProfileData diameterGatewayProfileData) {
        this.diameterGatewayProfileData = diameterGatewayProfileData;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="RADIUS_PROFILE_ID")
    @JsonIgnore
    public RadiusGatewayProfileData getRadiusGatewayProfileData() {
        return radiusGatewayProfileData;
    }

    public void setRadiusGatewayProfileData(RadiusGatewayProfileData radiusGatewayProfileData) {
        this.radiusGatewayProfileData = radiusGatewayProfileData;
    }

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "SERVICE_ID")
    @JsonIgnore
    public ServiceData getServiceData() {
        return serviceData;
    }

    public void setServiceData(ServiceData serviceData) {
        this.serviceData = serviceData;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.CONDITION, condition);
        jsonObject.addProperty(FieldValueConstants.ORDER_NUMBER, orderNumber);
        return jsonObject;
    }
}
