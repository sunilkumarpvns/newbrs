package com.elitecore.corenetvertex.pd.guiding;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;

/**
 * Used to manage Guiding related information with DB 
 * Created by ajay pandey on 23/12/17.
 */

@Entity(name = "com.elitecore.corenetvertex.pd.guiding.GuidingData")
@Table(name = "TBLM_GUIDING")
public class GuidingData extends DefaultGroupResourceData implements Serializable {

	private static final long serialVersionUID = -9173903847327724345L;

	private String guidingName;
	private LobData lobData;
	private ServiceData serviceData;
	private String accountIdentifierType;
	private String accountIdentifierValue;
	private AccountData accountData;
	private String trafficType;
	private Timestamp startDate;
	private Timestamp endDate;
	
	
	@Column(name = "GUIDING_NAME")
	public String getGuidingName() {
		return guidingName;
	}

	public void setGuidingName(String guidingName) {
		this.guidingName = guidingName;
	}


	@JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOB_ID")
	@Fetch(FetchMode.JOIN)
	public LobData getLobData() {
		return lobData;
	}

	public void setLobData(LobData lobData) {
		this.lobData = lobData;
	}
	

	@JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_ID")
	@Fetch(FetchMode.JOIN)
	public ServiceData getServiceData() {
		return serviceData;
	}

	public void setServiceData(ServiceData serviceData) {
		this.serviceData = serviceData;
	}
	
	
	@Column(name = "IDENTIFIER_TYPE")
	public String getAccountIdentifierType() {
		return accountIdentifierType;
	}
	

	public void setAccountIdentifierType(String accountIdentifierType) {
		this.accountIdentifierType = accountIdentifierType;
	}

	@Column(name = "IDENTIFIER_VALUE")
	public String getAccountIdentifierValue() {
		return accountIdentifierValue;
	}

	public void setAccountIdentifierValue(String accountIdentifireValue) {
		this.accountIdentifierValue = accountIdentifireValue;
	}

	@JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_NUMBER")
	@Fetch(FetchMode.JOIN)
	public AccountData getAccountData() {
		return accountData;
	}

	public void setAccountData(AccountData accountData) {
		this.accountData = accountData;
	}
	
	@Column(name = "TRAFFIC_TYPE")
	public String getTrafficType() {
		return trafficType;
	}

	public void setTrafficType(String trafficType) {
		this.trafficType = trafficType;
	}

	@Column(name = "START_DATE")
	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE")
	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}
	
	@Transient
    public String getLobId() {
        if(this.getLobData()!=null){
            return getLobData().getId();
        }
        return null;
    }

    public void setLobId(String lobId) {
        if(Strings.isNullOrBlank(lobId) == false){
        	LobData lobDataObj =new LobData();
        	lobDataObj.setId(lobId);
        	this.lobData = lobDataObj;
        }
    }

    @Transient
    public String getServiceId(){
    	if(this.getServiceData() != null){
    		return getServiceData().getId();
    	}
    	return null;
    }
    
    public void setServiceId(String serviceId){
    	if(Strings.isNullOrBlank(serviceId) == false){
    		ServiceData serviceDataObj =new ServiceData();
    		serviceDataObj.setId(serviceId);
    		this.serviceData = serviceDataObj;
    	}
    }
    
    @Transient
    public String getAccountId(){
    	if(this.getAccountData() != null){
    		return getAccountData().getId();
    	}
    	return null;
    }
    
    public void setAccountId(String accountId){
    	if(Strings.isNullOrBlank(accountId) == false){
    		AccountData accountNumber = new AccountData();
    		accountNumber.setId(accountId);
    		this.accountData = accountNumber;
    	}
    }
    
	@Override
	@Column(name = "STATUS")
	public String getStatus() {
		return super.getStatus();
	}

	@Transient
	@Override
	@JsonIgnore
	public String getResourceName() {
		return getAccountIdentifierValue();
	}
	
	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.IDENTIFIER_TYPE, accountIdentifierType);
		jsonObject.addProperty(FieldValueConstants.IDENTIFIER_VALUE, accountIdentifierValue);
		if(accountData != null){
			jsonObject.addProperty(FieldValueConstants.ACCOUNT_NUMBER, accountData.getName());
		}
		if(lobData != null){
			jsonObject.addProperty(FieldValueConstants.LOB, lobData.getName());
		}
		if(lobData != null){
		jsonObject.addProperty(FieldValueConstants.SERVICE_DATA, serviceData.getName());
		}
		jsonObject.addProperty(FieldValueConstants.TRAFFICE_TYPE, trafficType);
		jsonObject.addProperty(FieldValueConstants.STATUS, getStatus());
		jsonObject.addProperty(FieldValueConstants.GROUPS, super.getGroups());

		return jsonObject;
	}

}
