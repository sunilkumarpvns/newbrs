package com.elitecore.corenetvertex.sm.gateway;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
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
import java.io.Serializable;

@Entity(name="com.elitecore.corenetvertex.sm.gateway.AttributeMappingData")
@Table(name = "TBLM_ATTRIBUTE_MAPPING")
public class AttributeMappingData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private  String[] mappings;
    private transient PacketMappingData packetMappingData;
    private transient PCCRuleMappingData pccRuleMappingData;
    private Integer orderNumber;
    private transient String type;

    public AttributeMappingData(){
        this.mappings = new String[CommonConstants.DEFAULT_MAPPING_COUNT];
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "ORDER_NUMBER")
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }


    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="PCCRULE_MAP_ID")
    @JsonIgnore
    public PCCRuleMappingData getPccRuleMappingData() {
        return pccRuleMappingData;
    }

    public void setPccRuleMappingData(PCCRuleMappingData pccRuleMappingData) {
        this.pccRuleMappingData = pccRuleMappingData;
    }

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="PACKET_MAP_ID")
    @JsonIgnore
    public PacketMappingData getPacketMappingData() {
        return packetMappingData;
    }

    public void setPacketMappingData(PacketMappingData packetMappingData) {
        this.packetMappingData = packetMappingData;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        if(mappings != null && mappings.length > 0){
            int index = 0;
            for(String mapping : mappings){
                if(Strings.isNullOrBlank(mapping) == false){
                    jsonObject.addProperty("mapping["+index+"]",mapping);
                    index++;
                }
            }
        }
        jsonObject.addProperty(FieldValueConstants.ORDER_NUMBER, orderNumber);
        return jsonObject;
    }

    @Column(name = "MAPPINGS")
    public String[] getMappings() {
        return mappings;
    }

    public void setMappings(String[] mappings) {
        this.mappings = mappings;
    }

    @Column(name="MAPPING_TYPE")
    @JsonIgnore
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
