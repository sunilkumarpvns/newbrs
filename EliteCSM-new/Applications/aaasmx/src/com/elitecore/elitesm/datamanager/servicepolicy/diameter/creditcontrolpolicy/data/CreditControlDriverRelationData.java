package com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data;

import java.io.Serializable;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.DriverInstanceNameAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlRootElement(name = "driver-detail")
@ValidObject
public class CreditControlDriverRelationData extends BaseData implements Serializable,Validator {
			
	private static final long serialVersionUID = 1L;
	private String  policyId; 

	private String driverInstanceId;
	
	private Integer weightage;
	private DriverInstanceData driverData;
	
	@XmlTransient
	public String getPolicyId() {
		return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	
	@XmlElement(name="name")
	@NotNull(message="Driver must be specified")
	@XmlJavaTypeAdapter(DriverInstanceNameAdapter.class)
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlElement(name = "weightage")
	public Integer getWeightage() {
		return weightage;
	}
	public void setWeightage(Integer weightage) {
		this.weightage = weightage;
	}
	
	@XmlTransient
	public DriverInstanceData getDriverData() {
		return driverData;
	}
	public void setDriverData(DriverInstanceData driverData) {
		this.driverData = driverData;
	}
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		DriverBLManager blManager = new DriverBLManager();

		boolean isValid = true;
		
		try {

			if(RestValidationMessages.INVALID.equals(this.driverInstanceId)){
				RestUtitlity.setValidationMessage(context, "Specified driver is not valid");
				isValid = false;
			}
			
			if(this.driverInstanceId != null){
				DriverInstanceData data = blManager.getDriverInstanceByDriverInstanceId(this.driverInstanceId);
				if(data != null ) {
					
					if(data.getDriverTypeId() != DriverTypeConstants.DIAMETER_CRESTEL_OCSv2_DRIVER && data.getDriverTypeId() != DriverTypeConstants.DIAMETER_RATING_TRANSLATION_DRIVER) {
						RestUtitlity.setValidationMessage(context, "Congifured driver must be either from Crestel OCSv2 Driver or Crestel Rating Translation Driver.");
						isValid = false;
						return isValid;
					}
				}
			}
		} catch (DataManagerException e) {
			e.printStackTrace();
		} 
		return isValid;
	}

	
	
}