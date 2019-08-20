package com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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

@XmlRootElement(name = "driver-detail")
@ValidObject
public class NASPolicyAcctDriverRelData extends BaseData implements Serializable, Validator{
	
	private static final long serialVersionUID = 1L;
	private String  nasPolicyId; 
	private String driverInstanceId;
	private Integer weightage;
	private DriverInstanceData driverData;
	
	private String driverName;
	
	@XmlTransient
	public String getNasPolicyId() {
		return nasPolicyId;
	}

	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
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
	
	public String toString(){
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		writer.println();
		writer.println("------------ NASPolicyDriverRelData ------------");
		writer.println("nasPolicyId 	 :"+nasPolicyId);
		writer.println("driverInstanceId :"+driverInstanceId);
		writer.println("weightage :"+weightage);
		writer.println("-----------------------------------------------------");
		writer.println();
		writer.close();
		return out.toString() ;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		
		if (Strings.isNullOrBlank(this.driverName) == false) {
			DriverBLManager blManager = new DriverBLManager();
			try {
				DriverInstanceData driverData = blManager.getDriverInstanceByName(getDriverName());
				DriverTypeData driverTypeData = driverData.getDriverTypeData();
				if ((ServiceTypeConstants.NAS_ACCT_APPLICATION == driverTypeData.getServiceTypeId()) == false) {
					RestUtitlity.setValidationMessage(context, "Specify driver: " + getDriverName() + " in accounting group is not Acct Type driver");
					isValid = false;
				} else {
					this.setDriverInstanceId(driverData.getDriverInstanceId());
				}
			} catch (DataManagerException e) {
				e.printStackTrace();
				RestUtitlity.setValidationMessage(context, "Specify driver: " + getDriverName() + " in accounting group does not exist");
				isValid = false;
			}
		} else {
			if(this.driverInstanceId == null){
				RestUtitlity.setValidationMessage(context, "Accounting driver must be specified");
				isValid = false;
			}
		}
		return isValid;
	}
}