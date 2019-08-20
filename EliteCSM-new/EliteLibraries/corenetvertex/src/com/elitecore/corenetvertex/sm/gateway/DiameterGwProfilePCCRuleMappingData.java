package com.elitecore.corenetvertex.sm.gateway;


import com.elitecore.commons.base.Strings;
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
import javax.persistence.Transient;
import java.io.Serializable;

@Entity(name="com.elitecore.corenetvertex.sm.gateway.DiameterGwProfilePCCRuleMappingData")
@Table( name = "TBLM_DIA_GW_PROFILE_RULE_MAP")
public class DiameterGwProfilePCCRuleMappingData implements Serializable{
    private static final long serialVersionUID = 1L;
    private String id;
    private transient PCCRuleMappingData pccRuleMappingData;
    private transient String condition;
    private int orderNumber;
    private transient DiameterGatewayProfileData diameterGatewayProfileData;
    private String pccRuleMappingId;

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

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "PCCRULE_MAPPING_ID")
    @JsonIgnore
    public PCCRuleMappingData getPccRuleMappingData() {
        return pccRuleMappingData;
    }

    public void setPccRuleMappingData(PCCRuleMappingData pccRuleMappingData) {
        this.pccRuleMappingData = pccRuleMappingData;
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

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.CONDITION, condition);
        jsonObject.addProperty(FieldValueConstants.ORDER_NUMBER, orderNumber);
        return jsonObject;
    }

    @Transient
    public String getPccRuleMappingId() {
        if(this.getPccRuleMappingData()!=null) {
            this.pccRuleMappingId = getPccRuleMappingData().getId();
        }
        return pccRuleMappingId;
    }

    public void setPccRuleMappingId(String pccRuleMappingId) {
        if(Strings.isNullOrBlank(pccRuleMappingId) == false){
            PCCRuleMappingData pccRuleMappingData = new PCCRuleMappingData();
            pccRuleMappingData.setId(pccRuleMappingId);
            this.pccRuleMappingData = pccRuleMappingData;
        }
        this.pccRuleMappingId = pccRuleMappingId;
    }
}
