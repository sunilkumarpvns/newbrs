package com.elitecore.corenetvertex.pkg.dataservicetype;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.util.FlowAccessAdapter;
import com.elitecore.corenetvertex.util.ProtocolAdapter;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;


/**
 * @author aditya.shrivastava
 * 
 */
@Entity
@Table(name = "TBLM_DEF_SERVICE_DATA_FLOW")
public class DefaultServiceDataFlowData implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private String serviceDataFlowId;
	@SerializedName(FieldValueConstants.FLOW_ACCESS)@JsonAdapter(FlowAccessAdapter.class)private String flowAccess;
	@SerializedName(FieldValueConstants.PROTOCOL)@JsonAdapter(ProtocolAdapter.class)private String protocol;
	@SerializedName(FieldValueConstants.SOURCE_IP)private String sourceIP;
	@SerializedName(FieldValueConstants.SOURCE_PORT)private String sourcePort;
	@SerializedName(FieldValueConstants.DESTINATION_IP)private String destinationIP;
	@SerializedName(FieldValueConstants.DESTINATION_PORT)private String destinationPort;
	transient private DataServiceTypeData dataServiceTypeData;
	

	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "eliteSequenceGenerator")
	public String getServiceDataFlowId() {
		return serviceDataFlowId;
	}

	public void setServiceDataFlowId(String serviceDataFlowId) {
		this.serviceDataFlowId = serviceDataFlowId;
	}

	@Column(name = "FLOW_ACCESS")
	public String getFlowAccess() {
		return flowAccess;
	}

	public void setFlowAccess(String flowAccess) {
		this.flowAccess = flowAccess;
	}

	@Column(name = "PROTOCOL")
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Column(name = "SOURCE_IP")
	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	@Column(name = "SOURCE_PORT")
	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	@Column(name = "DESTINATION_IP")
	public String getDestinationIP() {
		return destinationIP;
	}

	public void setDestinationIP(String destinationIP) {
		this.destinationIP = destinationIP;
	}

	@Column(name = "DESTINATION_PORT")
	public String getDestinationPort() {
		return destinationPort;
	}

	public void setDestinationPort(String destinationPort) {
		this.destinationPort = destinationPort;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DATA_SERVICE_TYPE_ID")
	@JsonIgnore
	@XmlTransient
	public DataServiceTypeData getDataServiceTypeData() {
		return dataServiceTypeData;
	}

	public void setDataServiceTypeData(DataServiceTypeData dataServiceTypeData) {
		this.dataServiceTypeData = dataServiceTypeData;
	}
	
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.FLOW_ACCESS,flowAccess);
		jsonObject.addProperty(FieldValueConstants.PROTOCOL,protocol);
		jsonObject.addProperty(FieldValueConstants.SOURCE_IP,sourceIP);
		jsonObject.addProperty(FieldValueConstants.SOURCE_PORT,sourcePort);
		jsonObject.addProperty(FieldValueConstants.DESTINATION_IP,destinationIP);
		jsonObject.addProperty(FieldValueConstants.DESTINATION_PORT,destinationPort);
		return jsonObject;
	}
	
	public DefaultServiceDataFlowData deepClone() throws CloneNotSupportedException {
		DefaultServiceDataFlowData newData = (DefaultServiceDataFlowData) this.clone();
		newData.dataServiceTypeData = dataServiceTypeData;
		return newData;
	}
}
