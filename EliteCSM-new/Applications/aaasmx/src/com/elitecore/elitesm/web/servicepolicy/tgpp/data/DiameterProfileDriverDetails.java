package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import static com.elitecore.commons.base.Strings.padStart;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AdditionalDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.PrimaryDriverDetail;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.ProfileDriverDetails;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.SecondaryAndCacheDriverDetail;

@XmlType(propOrder = {"primaryDriverGroup", "secondaryDriverGroup", "additionalDriverList", "driverScript"})
public class DiameterProfileDriverDetails implements ProfileDriverDetails {
	
	@Valid
	private List<PrimaryDriverDetail> primaryDriverGroup;
	private List<SecondaryAndCacheDriverDetail> secondaryDriverGroup;
	private List<AdditionalDriverDetail> additionalDriverList;
	private String driverScript;

	public DiameterProfileDriverDetails(){
		primaryDriverGroup = new ArrayList<PrimaryDriverDetail>();
		secondaryDriverGroup = new ArrayList<SecondaryAndCacheDriverDetail>();
		additionalDriverList = new ArrayList<AdditionalDriverDetail>();
	}

	@Override
	@XmlElement(name = "driver-script", type = String.class)
	public String getDriverScript(){
		return this.driverScript;
	}
	
	public void setDriverScript(String driverScript){
		this.driverScript = driverScript;
	}
	
	@Override
	@Valid
	@XmlElementWrapper(name = "primary-group")
	@XmlElement(name = "primary-driver")
	public List<PrimaryDriverDetail> getPrimaryDriverGroup() {
		return primaryDriverGroup;
	}
	
	public void setPrimaryDriverGroup(List<PrimaryDriverDetail> primaryDriverGroup) {
		this.primaryDriverGroup = primaryDriverGroup;
	}
	
	@Override
	@XmlElementWrapper(name = "secondary-group")
	@XmlElement(name = "secondary-driver")
	public List<SecondaryAndCacheDriverDetail> getSecondaryDriverGroup() {
		return secondaryDriverGroup;
	}
	
	public void setSecondaryDriverGroup(List<SecondaryAndCacheDriverDetail> secondaryDriverGroup) {
		this.secondaryDriverGroup = secondaryDriverGroup;
	}

	@Override
	@XmlElementWrapper(name = "additional-group")
	@XmlElement(name = "additional-driver")
	public List<AdditionalDriverDetail> getAdditionalDriverList() {
		return additionalDriverList;
	}
	
	public void setAdditionalDriverList(List<AdditionalDriverDetail> additionalDriverList) {
		this.additionalDriverList = additionalDriverList;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(padStart("Primary Drivers", 10, ' '));
		for (PrimaryDriverDetail primaryDriverDetail : getPrimaryDriverGroup()) {
			out.println(format("%-30s: %s", "Id", primaryDriverDetail.getDriverInstanceId()));
			out.println(format("%-30s: %s", "Weightage", primaryDriverDetail.getWeightage()));
		}
		out.println(padStart("Secondary Drivers", 10, ' '));
		for (SecondaryAndCacheDriverDetail secondaryDriverDetail : getSecondaryDriverGroup()) {
			out.println(format("%-30s: %s", "Id", secondaryDriverDetail.getSecondaryDriverId()));
		}
		out.println(padStart("Additional Drivers", 10, ' '));
		for (AdditionalDriverDetail additionalDriverDetail : getAdditionalDriverList()) {
			out.println(format("%-30s: %s", "Id", additionalDriverDetail.getDriverId()));
		}
		out.println(format("%-30s: %s", "Script", getDriverScript()));
		out.close();
		return writer.toString();
	}
}