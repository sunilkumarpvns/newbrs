package com.elitecore.corenetvertex.sm.gateway;

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

@Entity(name="com.elitecore.corenetvertex.sm.gateway.GroovyScriptData")
@Table(name = "TBLD_GATEWAY_GROOVY_SCRIPT")
public class GroovyScriptData implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private int orderNumber;
	private String scriptName;
	private String argument;
	private transient DiameterGatewayProfileData diameterGatewayProfileData;
	private transient RadiusGatewayProfileData radiusGatewayProfileData;

	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "eliteSequenceGenerator")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column (name = "ORDER_NUMBER")
	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Column (name = "SCRIPT_NAME")
	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	@Column (name = "ARGUMENT")
	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(name="DIAMETER_GW_PROFILE_ID")
	@JsonIgnore
	public DiameterGatewayProfileData getDiameterGatewayProfileData() {
		return diameterGatewayProfileData;
	}

	public void setDiameterGatewayProfileData(DiameterGatewayProfileData diameterGatewayProfileData) {
		this.diameterGatewayProfileData = diameterGatewayProfileData;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="RADIUS_GW_PROFILE_ID")
	@JsonIgnore
	public RadiusGatewayProfileData getRadiusGatewayProfileData() {
		return radiusGatewayProfileData;
	}

	public void setRadiusGatewayProfileData(RadiusGatewayProfileData radiusGatewayProfileData) {
		this.radiusGatewayProfileData = radiusGatewayProfileData;
	}


	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.ARGUMENT, argument);
		jsonObject.addProperty(FieldValueConstants.ORDER_NUMBER, orderNumber);
		return jsonObject;
	}
}
