package com.elitecore.aaa.radius.sessionx.conf.impl;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

public class ConcurrentLoginPolicyData {

	private String name;
	private String description;
	private String concurrentLoginId;
	private String policyType;
	private String policyMode;
	private String serviceTypeAttributeValue;
	private Long maxLogins;
	private List<ServceWiseLogin> serviceWiseLoginList;

	public ConcurrentLoginPolicyData() {
		serviceWiseLoginList = new ArrayList<ServceWiseLogin>();
	}

	@XmlElement(name ="name",type =String.class)
	public String getName() {
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	@XmlTransient
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name = "id",type = String.class)
	public String getConcurrentLoginId() {
		return concurrentLoginId;
	}
	public void setConcurrentLoginId(String concurrentLoginId){
		this.concurrentLoginId = concurrentLoginId;
	}
	@XmlElement(name = "policy-type",type = String.class)
	public String getPolicyType() {
		return policyType;
	}
	public void setPolicyType(String policyType){
		this.policyType = policyType;
	}
	@XmlElement(name = "policy-mode",type = String.class)
	public String getPolicyMode() {
		return policyMode;
	}
	public void setPolicyMode(String policyMode){
		this.policyMode = policyMode;
	}
	@XmlElement(name = "service-type",type = String.class)
	public String getServiceType() {
		return serviceTypeAttributeValue;
	}
	public void setServiceType(String serviceTypeAttributeValue){
		this.serviceTypeAttributeValue = serviceTypeAttributeValue;
	}
	@XmlElementWrapper(name = "service-wise-login-list")
	@XmlElement(name = "service-wise-login")
	public List<ServceWiseLogin> getServiceWiseLoginList() {
		return serviceWiseLoginList;
	}
	public void setServiceWiseLoginList(List<ServceWiseLogin> serviceWiseLoginList ){
		this.serviceWiseLoginList = serviceWiseLoginList;
	}

	@XmlElement(name = "maximum-login",type = Long.class)
	public Long getMaxLogins() {
		return maxLogins;
	}
	public void setMaxLogins(Long maxLogins){
		this.maxLogins = maxLogins;
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ConcurrentLoginPolicyData))
			return false;
		
		ConcurrentLoginPolicyData other = (ConcurrentLoginPolicyData) obj;
		return this.concurrentLoginId == other.concurrentLoginId;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(padStart("Concurrent Login Policy", 10, ' '));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Name", getName()));
		out.println(format("%-30s: %s", "Mode", getPolicyMode()));
		out.println(format("%-30s: %s", "Type", getPolicyType()));
		out.println(format("%-30s: %s", "Max-Login", getMaxLogins()));
		out.println(format("%-30s: %s", "Service Type", getServiceType() != null ? getServiceType() : ""));
		
		for (ServceWiseLogin servceWiseLogin : getServiceWiseLoginList()) {
			out.println(repeat("-", 70));
			out.println(padStart("Service Wise Login", 10, ' '));
			out.println(format("%-30s: %s", "Max Service Session", servceWiseLogin.getMaxServiceWiseSessions()));
			out.println(format("%-30s: %s", "Service Type Value", servceWiseLogin.getServiceTypeValue()));
			out.println(repeat("-", 70));
		}
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}
}
