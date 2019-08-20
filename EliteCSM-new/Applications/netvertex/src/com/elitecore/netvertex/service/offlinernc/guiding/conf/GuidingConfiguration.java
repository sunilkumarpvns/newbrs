package com.elitecore.netvertex.service.offlinernc.guiding.conf;

import java.sql.Timestamp;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.service.offlinernc.guiding.Lob;
import com.elitecore.netvertex.service.offlinernc.guiding.Service;

public class GuidingConfiguration implements ToStringable{

	private Lob lob;
	private Service service;
	private String trafficType;
	private String accountIdentifierType;
	private String accountIdentifierValue;
	private String accountNumber;
	private Timestamp startDate;
	private Timestamp endDate;
	private String partnerName;


	public Lob getLob() {
		return lob;
	}

	public void setLob(Lob lob) {
		this.lob = lob;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getTrafficType() {
		return trafficType;
	}

	public void setTrafficType(String trafficType) {
		this.trafficType = trafficType;
	}

	public String getAccountIdentifierType() {
		return accountIdentifierType;
	}

	public void setAccountIdentifierType(String accountIdentifierType) {
		this.accountIdentifierType = accountIdentifierType;
	}

	public String getAccountIdentifierValue() {
		return accountIdentifierValue;
	}

	public void setAccountIdentifier(String accountIdentifierValue) {
		this.accountIdentifierValue = accountIdentifierValue;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountId(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
		
	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
		
	}
	
	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.incrementIndentation();
		builder.append("Lob", lob);
		builder.append("Service", service);
		builder.append("Traffic Type", trafficType);
		builder.append("Account Identifier Type", accountIdentifierType);
		builder.append("Account Identifier Value", accountIdentifierValue);
		builder.append("Account Number", accountNumber);
		builder.append("Start Date", startDate);
		builder.append("End Date", endDate);
		builder.append("Partner Name", partnerName);
		builder.decrementIndentation();
	}
}
