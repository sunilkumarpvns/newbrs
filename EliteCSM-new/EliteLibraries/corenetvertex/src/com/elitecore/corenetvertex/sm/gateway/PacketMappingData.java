package com.elitecore.corenetvertex.sm.gateway;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Entity(name="com.elitecore.corenetvertex.sm.gateway.PacketMappingData")
@Table(name = "TBLM_PACKET_MAPPING")
public class PacketMappingData extends DefaultGroupResourceData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private String commProtocol;
    private String packetType;
    private String type;
    private transient String applicationType;
    private AttributeMappingData attributeMappingData;
    private transient List<RadiusGwProfilePacketMapData> radiusGatewayProfilePacketMappings;
    private transient List<DiameterGwProfilePacketMapData> diameterGatewayProfilePacketMappings;

    public PacketMappingData(){
        this.radiusGatewayProfilePacketMappings = Collectionz.newArrayList();
        this.diameterGatewayProfilePacketMappings = Collectionz.newArrayList();
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

    @Column(name = "COMM_PROTOCOL")
    public String getCommProtocol() {
        return commProtocol;
    }

    public void setCommProtocol(String commProtocol) {
        this.commProtocol = commProtocol;
    }

    @Column(name = "PACKET_TYPE")
    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    @Column(name = "TYPE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    @Transient
    @JsonIgnore
    public String getResourceName() {
        return getName();
    }

    @Column(name="APPLICATION_TYPE")
    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    @Override
    @Column(name = "STATUS")
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        jsonObject.addProperty(FieldValueConstants.COMMUNICATION_PROTOCOL, commProtocol );
        jsonObject.addProperty(FieldValueConstants.TYPE, type != null ? ConversionType.valueOf(type).getConversionType() : "");
        jsonObject.addProperty(FieldValueConstants.PACKET_TYPE, packetType);
        if(attributeMappingData != null){
            jsonObject.add("Attribute Mapping Data", attributeMappingData.toJson());

        }
        return jsonObject;
    }

    @OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "packetMappingData")
    @Fetch(FetchMode.SELECT)
    public AttributeMappingData getAttributeMappingData() {
        return attributeMappingData;
    }

    public void setAttributeMappingData(AttributeMappingData attributeMappingData) {
        this.attributeMappingData = attributeMappingData;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "packetMappingData", orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    public List<RadiusGwProfilePacketMapData> getRadiusGatewayProfilePacketMappings() {
        return radiusGatewayProfilePacketMappings;
    }

    public void setRadiusGatewayProfilePacketMappings(List<RadiusGwProfilePacketMapData> radiusGatewayProfilePacketMappings) {
        this.radiusGatewayProfilePacketMappings = radiusGatewayProfilePacketMappings;
    }

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "packetMappingData", orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnore
    public List<DiameterGwProfilePacketMapData> getDiameterGatewayProfilePacketMappings() {
        return diameterGatewayProfilePacketMappings;
    }

    public void setDiameterGatewayProfilePacketMappings(List<DiameterGwProfilePacketMapData> diameterGatewayProfilePacketMappings) {
        this.diameterGatewayProfilePacketMappings = diameterGatewayProfilePacketMappings;
    }
}