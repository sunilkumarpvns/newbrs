package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.service.acct.RadAcctServiceContext;
import com.elitecore.aaa.radius.service.acct.handlers.RadAcctServiceHandler;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

/**
 * 
 * @author narendra.pathai
 *
 */
@XmlRootElement(name = "cdr-handler-entry")
@XmlType(propOrder = {"ruleset", "driverDetails"})
public class CdrHandlerEntryData extends ServicePolicyHandlerDataSupport implements AuthServicePolicyHandlerData, AcctServicePolicyHandlerData {

	private String ruleset;
	private RadiusProfileDriversDetails driverDetails;
	private String wait;
	
	@XmlAttribute(name = "wait")
	@Pattern(regexp = RestValidationMessages.BOOLEAN_REGEX, message = "Invalid value of Wait for Response of CDR Handler, It could be 'true' and 'false' ")
	public String getWait() {
		return wait;
	}

	public void setWait(String wait) {
		this.wait = wait;
	}

	@XmlElement(name = "ruleset")
	public String getRuleset() {
		return ruleset;
	}
	
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}

	@XmlElement(name = "cdr-drivers")
	public RadiusProfileDriversDetails getDriverDetails() {
		return driverDetails;
	}

	public void setDriverDetails(RadiusProfileDriversDetails driverDetails) {
		this.driverDetails = driverDetails;
	}

	@Override
	public RadAuthServiceHandler createHandler(RadAuthServiceContext serviceContext) {
		return null;
	}

	@Override
	public RadAcctServiceHandler createHandler(RadAcctServiceContext serviceContext) {
		return null;
	}
	
	@Override
	public void postRead() {

	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(format("%-30s: %s", "Ruleset", getRuleset() != null ? getRuleset() : ""));
		out.print(getDriverDetails());
		out.println(format("%-30s: %s", "Wait", getWait()));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Rule Set", ruleset);
		
		List<PrimaryDriverDetail> primaryDrivers = driverDetails.getPrimaryDriverGroup();
		List<String> primaryDriversList = new ArrayList<String>();
		if(Collectionz.isNullOrEmpty(primaryDrivers) == false){
			int primaryDriverSize = primaryDrivers.size();
			
			for (int i = 0; i < primaryDriverSize; i++) {
				PrimaryDriverDetail primaryDriverDetail = primaryDrivers.get(i);
				primaryDriversList.add(primaryDriverDetail.getDriverInstanceId());
			}
			
			if( Collectionz.isNullOrEmpty(primaryDriversList) == false ){
				String primaryDriverString = primaryDriversList.toString().replaceAll("[\\s\\[\\]]", "");
				object.put("Primary Driver", primaryDriverString);
			}
		}
		
		List<SecondaryAndCacheDriverDetail> secondaryDrivers = driverDetails.getSecondaryDriverGroup();
		List<String> secondaryDriversList = new ArrayList<String>();
		if(Collectionz.isNullOrEmpty(secondaryDrivers) == false){
			int secondaryDriverSize = secondaryDrivers.size();
			
			for (int i = 0; i < secondaryDriverSize; i++) {
				SecondaryAndCacheDriverDetail secondaryDriverDetail = secondaryDrivers.get(i);
				secondaryDriversList.add(secondaryDriverDetail.getSecondaryDriverId());
			}
			
			if( Collectionz.isNullOrEmpty(secondaryDriversList) == false ){
				String secondaryDriverString = secondaryDriversList.toString().replaceAll("[\\s\\[\\]]", "");
				object.put("Secondary Driver", secondaryDriverString);
			}
		}
		
		object.put("Wait For CDR To Dump", wait);
		object.put("Driver Script", driverDetails.getDriverScript());
		return object;
	}
}
