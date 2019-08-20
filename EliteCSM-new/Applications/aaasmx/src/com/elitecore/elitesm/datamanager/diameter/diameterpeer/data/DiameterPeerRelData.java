/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyData.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.diameterpeer.data;
import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.Range;

import com.elitecore.aaa.util.constants.HSSAuthDriverConstant;
import com.elitecore.elitesm.blmanager.diameter.diameterpeer.DiameterPeerBLManager;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@ValidObject
@XmlType(propOrder = { "peerName", "weightage" })
@XmlRootElement(name = "diameter-peer")
public class DiameterPeerRelData extends BaseData implements Validator {
	    
	private String hhsPeerGroupRelId;
	private String peerUUID;
	
    @Range(min = 0, max = 10, message = "Invalid value of weightage [${validatedValue}]")
    private Long weightage;
    private String hssauthdriverid;
    private String peerName;
    private DiameterPeerData diameterPeerData;
    private Integer orderNumber;
    
    public DiameterPeerRelData() {
	this.weightage = HSSAuthDriverConstant.WIGHTAGE;
	}
    
    @XmlTransient
	public String getHhsPeerGroupRelId() {
		return hhsPeerGroupRelId;
	}
	public void setHhsPeerGroupRelId(String hhsPeerGroupRelId) {
		this.hhsPeerGroupRelId = hhsPeerGroupRelId;
	}
	
	@XmlTransient
	public String getPeerUUID() {
		return peerUUID;
	}

	public void setPeerUUID(String peerUUID) {
		this.peerUUID = peerUUID;
	}
	
	@XmlElement(name = "weightage")
	public Long getWeightage() {
		return weightage;
	}
	public void setWeightage(Long weightage) {
		this.weightage = weightage;
	}
	
	@XmlTransient
	public String getHssauthdriverid() {
		return hssauthdriverid;
	}
	public void setHssauthdriverid(String hssauthdriverid) {
		this.hssauthdriverid = hssauthdriverid;
	}
	
	@XmlTransient
	public DiameterPeerData getDiameterPeerData() {
		return diameterPeerData;
	}
	public void setDiameterPeerData(DiameterPeerData diameterPeerData) {
		this.diameterPeerData = diameterPeerData;
	}
	
	@XmlElement(name = "peer-name")
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
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		try{
			DiameterPeerBLManager diameterPeerBlManager = new DiameterPeerBLManager();
			DiameterPeerData diameterData = diameterPeerBlManager.getDiameterPeerByName(peerName.trim());
			this.peerUUID = diameterData.getPeerUUID();
			return true;
		} catch (Exception e) {
			if(peerName != null){
				RestUtitlity.setValidationMessage(context, "Invalid Peer name :- "+peerName);
			}else{
				RestUtitlity.setValidationMessage(context, "Peer name must be specified.");
			}
			e.printStackTrace();
			return false;
		}
	}
}