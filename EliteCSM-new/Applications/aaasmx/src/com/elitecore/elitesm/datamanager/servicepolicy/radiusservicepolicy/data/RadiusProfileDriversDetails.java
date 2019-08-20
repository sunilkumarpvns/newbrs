package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static com.elitecore.commons.base.Strings.padStart;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@ValidObject
@XmlType(propOrder = {"primaryDriverGroup", "secondaryDriverGroup", 
		"additionalDriverList", "driverScript"})
public class RadiusProfileDriversDetails implements ProfileDriverDetails, Validator {

	private List<PrimaryDriverDetail> primaryDriverGroup;
	private List<SecondaryAndCacheDriverDetail> secondaryDriverGroup;
	private List<AdditionalDriverDetail> additionalDriverList;
	private String driverScript;

	public RadiusProfileDriversDetails(){
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
	@XmlElementWrapper(name = "primary-group")
	@XmlElement(name = "primary-driver")
	@Size(min = 1, message = "At least one primary driver is required in Profile Lookup Handler")
	@Valid
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
			out.println(format("%-30s: %s", "Cache Driver Id", Strings.valueOf(secondaryDriverDetail.getCacheDriverId())));
		}
		out.println(padStart("Additional Drivers", 10, ' '));
		for (AdditionalDriverDetail additionalDriverDetail : getAdditionalDriverList()) {
			out.println(format("%-30s: %s", "Id", additionalDriverDetail.getDriverId()));
		}
		out.println(format("%-30s: %s", "Script", getDriverScript()));
		out.close();
		return writer.toString();
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		boolean isValid = true;
		for (PrimaryDriverDetail primaryDriver : getPrimaryDriverGroup()) {
	
			for (SecondaryAndCacheDriverDetail secondaryDriver : getSecondaryDriverGroup()) {
	
				if (Strings.isNullOrBlank(secondaryDriver.getSecondaryDriverId()) == false &&
						primaryDriver.getDriverInstanceId().equals(secondaryDriver.getSecondaryDriverId())) {
					
					RestUtitlity.setValidationMessage(context, "Primary Group and Secondary Group of Profile Lookup handler does not have same driver");
					isValid = false;
				}
	
				for (AdditionalDriverDetail additionalDriver : getAdditionalDriverList()) {
	
					if (Strings.isNullOrBlank(additionalDriver.getDriverId()) == false &&
							primaryDriver.getDriverInstanceId().equals(additionalDriver.getDriverId())) {
						
						RestUtitlity.setValidationMessage(context, "Primary Group and Additional Group of Profile Lookup handler does not have same driver");
						isValid = false;
					}
	
					if (Strings.isNullOrBlank(secondaryDriver.getSecondaryDriverId()) == false && 
							secondaryDriver.getSecondaryDriverId().equals(additionalDriver.getDriverId())) {
						RestUtitlity.setValidationMessage(context, "Secondary Group and Additional Group of Profile Lookup handler does not have same driver");
						isValid = false;
					}
				}
			}
	
		}
		return isValid;
	}
}
