package com.elitecore.elitesm.ws.rest.serverconfig.server.services.diameternas;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.ServiceRestUtility;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.data.ApplicationLoggerDetail;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.data.DiameterPluginsDetail;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
@XmlRootElement(name = "nas-application")
@XmlType(propOrder = {"strApplicationId", "servicePolicies", "logger", "diameterPluginsDetail"})
@ValidObject
public class DiaNasServiceData implements Validator {

	private static final String NAS_SERVICE_POLICY = "Nas Service Policy";
	
	@Valid
	private ApplicationLoggerDetail logger;
	private String strApplicationId="0:1";

	@Valid
	private DiameterPluginsDetail diameterPluginsDetail;
	
	@Size(min = 1, message = "Alteast one Service Policy must be specified")
	private List<String> servicePolicies;

	public DiaNasServiceData() {
		this.diameterPluginsDetail =new DiameterPluginsDetail();
		this.logger = new ApplicationLoggerDetail();
		this.servicePolicies = new ArrayList<String>();
	}

	@XmlElementWrapper(name="service-policies")
	@XmlElement(name="service-policy",type=String.class)
	public List<String> getServicePolicies() {
		return servicePolicies;
	}

	public void setServicePolicies(List<String> servicePolicies) {
		this.servicePolicies = servicePolicies;
	}

	@XmlElement(name = "logging")
	public ApplicationLoggerDetail getLogger() {
		return logger;
	}

	public void setLogger(ApplicationLoggerDetail logger) {
		this.logger = logger;
	}

	@XmlElement(name="application-id",type=String.class,defaultValue="0:1")
	public String getStrApplicationId() {
		return strApplicationId;
	}

	public void setStrApplicationId(String applicationId) {
		this.strApplicationId = applicationId;
	}

	@XmlElement(name ="plugin-list")
	public DiameterPluginsDetail getDiameterPluginsDetail() {
		return diameterPluginsDetail;
	}

	public void setDiameterPluginsDetail(DiameterPluginsDetail diameterPluginsDetail) {
		this.diameterPluginsDetail = diameterPluginsDetail;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {

		Boolean isValid = true;
		
		isValid = ServiceRestUtility.validateDiameterService(context, this.servicePolicies, this.diameterPluginsDetail, NASPolicyInstData.class, ServiceRestUtility.DIAMETER_NAS, NAS_SERVICE_POLICY, isValid );
		
		return isValid;
	}

}
