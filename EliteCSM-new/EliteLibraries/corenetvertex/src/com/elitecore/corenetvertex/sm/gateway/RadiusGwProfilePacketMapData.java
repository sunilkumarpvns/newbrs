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

@Entity(name="com.elitecore.corenetvertex.sm.gateway.RadiusGwProfilePacketMapData")
@Table(name = "TBLM_RAD_GW_PROFILE_PACKET_MAP")
public class RadiusGwProfilePacketMapData implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private transient RadiusGatewayProfileData radiusGatewayProfileData;
	private transient PacketMappingData packetMappingData;
	private transient String condition;
	private int orderNumber;
	private String packetMappingId;

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

	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(name = "PACKET_MAP_ID")
	@JsonIgnore
	public PacketMappingData getPacketMappingData() {
		return packetMappingData;
	}

	public void setPacketMappingData(PacketMappingData packetMappingData) {
		this.packetMappingData = packetMappingData;
	}


	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(name = "RADIUS_PROFILE_ID")
	@JsonIgnore
	public RadiusGatewayProfileData getRadiusGatewayProfileData() {
		return radiusGatewayProfileData;
	}

	public void setRadiusGatewayProfileData(RadiusGatewayProfileData radiusGatewayProfileData) {
		this.radiusGatewayProfileData = radiusGatewayProfileData;
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.CONDITION, condition);
		jsonObject.addProperty(FieldValueConstants.ORDER_NUMBER, orderNumber);
		return jsonObject;
	}

	@Transient
	public String getPacketMappingId() {
		if(this.getPacketMappingData()!=null) {
			this.packetMappingId = getPacketMappingData().getId();
		}
		return packetMappingId;
	}

	public void setPacketMappingId(String packetMappingId) {
		if(Strings.isNullOrBlank(packetMappingId) == false) {
			PacketMappingData packetMapData = new PacketMappingData();
			packetMapData.setId(packetMappingId);
			this.packetMappingData = packetMapData;
		}
		this.packetMappingId = packetMappingId;
	}

}
