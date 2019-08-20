package com.elitecore.aaa.diameter.subscriber;

import static com.elitecore.commons.base.Strings.padStart;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.radius.conf.impl.AdditionalDriverDetail;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.conf.impl.ProfileDriverDetails;
import com.elitecore.aaa.radius.conf.impl.SecondaryAndCacheDriverDetail;

@XmlType(propOrder = {})
public class DiameterProfileDriverDetails implements ProfileDriverDetails {
	
	private List<PrimaryDriverDetail> primaryDriverGroupList;
	private List<SecondaryAndCacheDriverDetail> secondaryDriverGroupList;
	private List<AdditionalDriverDetail> additionalDriverList;
	private String driverScript;

	public DiameterProfileDriverDetails(){
		//required by Jaxb.
		primaryDriverGroupList = new ArrayList<PrimaryDriverDetail>();
		secondaryDriverGroupList = new ArrayList<SecondaryAndCacheDriverDetail>();
		additionalDriverList = new ArrayList<AdditionalDriverDetail>();
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.aaa.radius.conf.impl.IProfileDriverDetails#getDriverScript()
	 */
	@Override
	@XmlElement(name = "driver-script", type = String.class)
	public String getDriverScript(){
		return this.driverScript;
	}
	
	public void setDriverScript(String driverScript){
		this.driverScript = driverScript;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.aaa.radius.conf.impl.IProfileDriverDetails#getPrimaryDriverGroup()
	 */
	@Override
	@XmlElementWrapper(name = "primary-group")
	@XmlElement(name = "primary-driver")
	public List<PrimaryDriverDetail> getPrimaryDriverGroup() {
		return primaryDriverGroupList;
	}
	
	public void setPrimaryDriverGroup(List<PrimaryDriverDetail> primaryDriverGroup) {
		this.primaryDriverGroupList = primaryDriverGroup;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.aaa.radius.conf.impl.IProfileDriverDetails#getSecondaryDriverGroup()
	 */
	@Override
	@XmlElementWrapper(name = "secondary-group")
	@XmlElement(name = "secondary-driver")
	public List<SecondaryAndCacheDriverDetail> getSecondaryDriverGroup() {
		return secondaryDriverGroupList;
	}
	
	public void setSecondaryDriverGroup(List<SecondaryAndCacheDriverDetail> secondaryDriverGroup) {
		this.secondaryDriverGroupList = secondaryDriverGroup;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.aaa.radius.conf.impl.IProfileDriverDetails#getAdditionalDriverList()
	 */
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
