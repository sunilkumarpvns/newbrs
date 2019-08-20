package com.elitecore.aaa.diameter.service.application.handlers.conf;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONObject;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterCDRHandler;
import com.elitecore.aaa.diameter.subscriber.DiameterProfileDriverDetails;
import com.elitecore.aaa.radius.conf.impl.AdditionalDriverDetail;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.conf.impl.SecondaryAndCacheDriverDetail;
import com.elitecore.commons.base.Collectionz;

@XmlRootElement(name = "cdr-handler-entry")
public class DiameterCDRHandlerEntryData extends DiameterApplicationHandlerDataSupport {
	
	private String ruleset;
	private DiameterProfileDriverDetails driverDetails;
	private boolean wait;
	
	@XmlAttribute(name = "wait")
	public boolean isWait() {
		return wait;
	}

	public void setWait(boolean wait) {
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
	public DiameterProfileDriverDetails getDriverDetails() {
		return driverDetails;
	}

	public void setDriverDetails(DiameterProfileDriverDetails driverDetails) {
		this.driverDetails = driverDetails;
	}
	
	@Override
	public void postRead() {
		super.postRead();
		registerPrimaryDrivers();
		registerSecondaryDrivers();
		registerAdditionalDrivers();
	}
	
	private void registerAdditionalDrivers() {
		for (AdditionalDriverDetail additionalDriverDetail : driverDetails.getAdditionalDriverList()) {
			getPolicyData().registerRequiredDriverId(additionalDriverDetail.getDriverId());
		}
	}

	private void registerSecondaryDrivers() {
		for (SecondaryAndCacheDriverDetail secondaryDriverDetail : driverDetails.getSecondaryDriverGroup()) {
			getPolicyData().registerRequiredDriverId(secondaryDriverDetail.getSecondaryDriverId());
		}
	}

	private void registerPrimaryDrivers() {
		for (PrimaryDriverDetail primaryDriverDetail : driverDetails.getPrimaryDriverGroup()) {
			getPolicyData().registerRequiredDriverId(primaryDriverDetail.getDriverInstanceId());
		}
	}

	@Override
	public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(
			DiameterServiceContext context) {
		return new DiameterCDRHandler(context, this);
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(format("%-30s: %s", "Ruleset", getRuleset() != null ? getRuleset() : ""));
		out.println(getDriverDetails());
		out.println(format("%-30s: %s", "Wait", isWait()));
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
