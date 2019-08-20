package com.elitecore.aaa.diameter.service.application.handlers.conf;

import static com.elitecore.commons.base.Strings.padStart;
import static com.elitecore.commons.base.Strings.repeat;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONObject;

import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAuthenticationHandler;
import com.elitecore.aaa.diameter.subscriber.DiameterSubscriberProfileRepositoryDetails;
import com.elitecore.commons.base.Collectionz;

@XmlRootElement(name = "authentication-handler")
public class DiameterAuthenticationHandlerData extends DiameterApplicationHandlerDataSupport 
implements SubscriberProfileRepositoryDetailsAware {
	
	private List<Integer> supportedAuthenticationMethods = new ArrayList<Integer>();
	private String eapConfigId;
	
	/* Transient properties */
	private DiameterSubscriberProfileRepositoryDetails sprDetails;
	
	@XmlElement(name = "eap-config")
	public String getEapConfigId() {
		return eapConfigId;
	}

	public void setEapConfigId(String eapConfigId) {
		this.eapConfigId = eapConfigId;
	}

	@Override
	public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(DiameterServiceContext context) {
		return new DiameterAuthenticationHandler(context, this);
	}

	@Override
	@XmlTransient
	public DiameterSubscriberProfileRepositoryDetails getSubscriberProfileRepositoryDetails() {
		return sprDetails;
	}

	@Override
	public void setSubscriberProfileRepositoryDetails(DiameterSubscriberProfileRepositoryDetails sprDetails) {
		this.sprDetails = sprDetails;
	}

	@XmlElementWrapper(name = "supported-methods")
	@XmlElement(name = "method")
	public List<Integer> getAutheMethodHandlerTypes() {
		return supportedAuthenticationMethods;
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.println(repeat("-", 70));
		out.println(format(padStart("Authentication Handler | Enabled: %s", 10, ' '), isEnabled()));
		out.println(repeat("-", 70));
		out.println(format("%-30s: %s", "Supported Methods", getAutheMethodHandlerTypes()));
		out.println(format("%-30s: %s", "EAP Configuration Id", getEapConfigId()));
		out.println(repeat("-", 70));
		out.close();
		return writer.toString();
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		
		object.put("Enabled", isEnabled());
		
		List<String> authMethodList = new ArrayList<String>();
		int size = supportedAuthenticationMethods.size();
		for (int i = 0; i < size; i++) {
			Integer method = supportedAuthenticationMethods.get(i);
			switch (method.intValue()) {
			case AuthMethods.PAP:
				authMethodList.add("PAP");
				break;
				
			case AuthMethods.CHAP:
				authMethodList.add("CHAP");
				break;

			case AuthMethods.EAP:
				authMethodList.add("EAP");
				break;
			}
		}
		
		if(Collectionz.isNullOrEmpty(authMethodList) == false){
			String authMethodString = authMethodList.toString().replaceAll("[\\s\\[\\]]", "");
			object.put("Supported Authentication Methods", authMethodString);
		}
		object.put("EAP Config", eapConfigId);

		return object;
	}
}
