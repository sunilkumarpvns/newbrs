package com.elitecore.aaa.radius.sessionx.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class ServceWiseLogin {

	String serviceTypeValue;
	Long maxServiceWiseSessions;
	String concurrentLoginPolicyId;
	
	public ServceWiseLogin(){
		//required by Jaxb.
	}
	

	@XmlElement(name = "service-type-value",type = String.class)
	public String getServiceTypeValue() {
		return serviceTypeValue;
	}
	public void setServiceTypeValue(String serviceTypeValue){
		this.serviceTypeValue = serviceTypeValue;
	}
	@XmlElement(name = "maximum-service-sessions",type = Long.class)
	public Long getMaxServiceWiseSessions() {
		return maxServiceWiseSessions;
	}
	public void setMaxServiceWiseSessions(Long maxServiceWiseSessions){
		this.maxServiceWiseSessions = maxServiceWiseSessions;
	}
	@XmlElement(name = "attribute-id",type = String.class)
	public String getConcurrentLoginPolicyId() {
		return concurrentLoginPolicyId;
	}
	public void setConcurrentLoginPolicyId(String concurrentLoginPolicyId){
		this.concurrentLoginPolicyId = concurrentLoginPolicyId;
	}

}
