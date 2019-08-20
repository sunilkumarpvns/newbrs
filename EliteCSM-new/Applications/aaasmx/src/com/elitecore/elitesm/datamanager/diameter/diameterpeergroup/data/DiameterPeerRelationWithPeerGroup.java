package com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

@XmlRootElement(name="peer-entry")
@XmlType(propOrder = { "peerName", "weightage"})
public class DiameterPeerRelationWithPeerGroup extends BaseData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String peerRelationId;
	private String peerGroupId;
	private String peerId;
	
	@NotEmpty(message="Peer Name must be specified")
	private String peerName;
	
	@NotNull(message="Weightage must be specified")
	@Range(min = 1, max = 10, message = "Invalid Range of weightage, It must be between 1-10")
	private Integer weightage;
	
	private Integer orderNumber;

	@XmlTransient
	public String getPeerRelationId() {
		return peerRelationId;
	}
	public void setPeerRelationId(String peerRelationId) {
		this.peerRelationId = peerRelationId;
	}
	
	@XmlTransient
	public String getPeerGroupId() {
		return peerGroupId;
	}
	public void setPeerGroupId(String peerGroupId) {
		this.peerGroupId = peerGroupId;
	}
	
	@XmlTransient
	public String getPeerId() {
		return peerId;
	}
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	
	@XmlElement(name="weightage")
	public Integer getWeightage() {
		return weightage;
	}
	public void setWeightage(Integer weightage) {
		this.weightage = weightage;
	}
	
	@XmlElement(name="peer-name")
	public String getPeerName() {
		return peerName;
	}
	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}
	
	@XmlTransient
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
