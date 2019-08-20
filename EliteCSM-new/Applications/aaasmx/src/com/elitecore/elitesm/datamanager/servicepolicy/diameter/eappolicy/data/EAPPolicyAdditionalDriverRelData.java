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
public class EAPPolicyAdditionalDriverRelData extends BaseData implements Serializable, Validator {
	
	private static final long serialVersionUID = 1L;
	private String  eapPolicyId; 
	private String driverInstanceId;
	private Long orderNumber;
	private DriverInstanceData driverInstanceData;
	
	private String driverName;
	
	@XmlTransient
	public String getEapPolicyId() {
		return eapPolicyId;
	}
	
	public void setEapPolicyId(String eapPolicyId) {
		this.eapPolicyId = eapPolicyId;
	}
	
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlTransient
	public Long getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@XmlTransient
	public DriverInstanceData getDriverInstanceData() {
		return driverInstanceData;
	}
	
	public void setDriverInstanceData(DriverInstanceData driverInstanceData) {
		this.driverInstanceData = driverInstanceData;
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
		if (Strings.isNullOrBlank(driverName) == false) {
			DriverBLManager blManager = new DriverBLManager();
			try {
				DriverInstanceData driverData = blManager.getDriverInstanceByName(getDriverName());
				DriverTypeData driverTypeData = driverData.getDriverTypeData();
				if ((ServiceTypeConstants.NAS_AUTH_APPLICATION == driverTypeData.getServiceTypeId()) == false) {
					RestUtitlity.setValidationMessage(context, "Specify driver: " + getDriverName() + " in additional group is not of Auth Type driver");
					isValid = false;
				} else {
					this.setDriverInstanceId(driverData.getDriverInstanceId());
				}
			} catch (DataManagerException e) {
				e.printStackTrace();
				RestUtitlity.setValidationMessage(context, "Specify driver: " + getDriverName() + " in additional group does not exist");
				isValid = false;
			}
		} else if (driverName != null && driverName.length() == 0){
			RestUtitlity.setValidationMessage(context, "Additional driver must be specify");
			isValid = false;
		}
		return isValid;
	}
}