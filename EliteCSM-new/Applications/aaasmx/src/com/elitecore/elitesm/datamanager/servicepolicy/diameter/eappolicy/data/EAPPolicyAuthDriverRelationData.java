package com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data;

import java.io.Serializable;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.util.constants.ServiceTypeConstants;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@ValidObject
public class EAPPolicyAuthDriverRelationData extends BaseData implements Serializable, Validator {
	
	private static final long serialVersionUID = 1L;
	private String  policyId; 
	private String driverInstanceId;
	private Integer weightage;
	private DriverInstanceData driverData;
	
	private String driverName;
	
	@XmlTransient
	public String getPolicyId() {
		return policyId;
	}
	
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	
	@XmlTransient
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

	@XmlElement(name = "driver-instance-name")
	public String getDriverName() {
		return driverName;
	}
	
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if (Strings.isNullOrBlank(this.driverName) == false) {
			DriverBLManager blManager = new DriverBLManager();
			try {
				DriverInstanceData driverData = blManager.getDriverInstanceByName(getDriverName());
				DriverTypeData driverTypeData = driverData.getDriverTypeData();
				if ((ServiceTypeConstants.NAS_AUTH_APPLICATION == driverTypeData.getServiceTypeId()) == false) {
					RestUtitlity.setValidationMessage(context, "Specify driver: " +getDriverName() + " in primary group is not Auth Type driver");
					isValid = false;
				} else {
					this.setDriverInstanceId(driverData.getDriverInstanceId());
				}
			} catch (DataManagerException e) {
				e.printStackTrace();
				RestUtitlity.setValidationMessage(context, "Specify driver: " +getDriverName() + " in primary group does not exist");
				isValid = false;
			}
		} else {
			if(this.driverInstanceId == null){
				RestUtitlity.setValidationMessage(context,"Primary driver must be specified");
				isValid = false;
			}
		}	
		return isValid;
	}	
}