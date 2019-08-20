package com.elitecore.elitesm.datamanager.servermgr.alert.data;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.NumericAdapter;
import com.elitecore.elitesm.ws.rest.adapter.RollingTypeAdapter;
import com.elitecore.elitesm.ws.rest.adapter.RollingUnitAdapter;
import com.elitecore.elitesm.ws.rest.serverconfig.alertconfiguration.AlertConfigurationConstant;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "file-listener")
@XmlType(propOrder = {"fileName", "rollingType", "rollingUnitValue", "maxRollingUnit", "compRollingUnit", "repeatedMessageReduction"})
@ValidObject
public class AlertFileListenerData extends BaseAlertListener implements Validator {
	
	private String fileListenerId;
	
	@Expose
	@SerializedName("File Name")
	@NotEmpty(message = "File Name must be specified.")
	private String fileName;
	
	@Expose
	@SerializedName("Rolling Type")
	private Long rollingType;
	
	@Expose
	@SerializedName("Rolling Unit")
	private Long rollingUnit;
	
	private Long maxRollingUnit;
	
	@Expose
	@SerializedName("Compress Rolled Unit")
	@NotNull(message = "Compress Rolled Unit  must be specified. It can be true or false.")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE, message = RestValidationMessages.PARAMETER_ERR_MESSAGE + "Compress Rolled Unit. It can be true or false." )
	private String compRollingUnit;
	
	@Expose
	@SerializedName("Repeated Message Reduction")
	@NotNull(message = "Repeated Message Reduction must be specified. It can be true or false")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE, message = "Invalid Repeated Message Reduction. It can be true or false." )
	private String repeatedMessageReduction;
	
	/**
	 * parameter only used for REST API implementation
	 */
	private String rollingUnitValue;
	
	@XmlTransient
	public String getFileListenerId() {
		return fileListenerId;
	}
	public void setFileListenerId(String fileListenerId) {
		this.fileListenerId = fileListenerId;
	}
	
	@XmlElement(name = "rolling-unit")
	@XmlJavaTypeAdapter(RollingUnitAdapter.class)
	public String getRollingUnitValue() {
		return rollingUnitValue;
	}
	public void setRollingUnitValue(String rollingUnitValue) {
		this.rollingUnitValue = rollingUnitValue;
	}
	@XmlElement(name = "file-name")
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@XmlElement(name = "rolling-type")
	@XmlJavaTypeAdapter(RollingTypeAdapter.class)
	public Long getRollingType() {
		return rollingType;
	}
	public void setRollingType(Long rollingType) {
		this.rollingType = rollingType;
	}
	
	@XmlTransient
	public Long getRollingUnit() {
		return rollingUnit;
	}
	public void setRollingUnit(Long rollingUnit) {
		this.rollingUnit = rollingUnit;
	}
	
	@XmlElement(name = "max-rolling-unit")
	@XmlJavaTypeAdapter(value = NumericAdapter.class)
	public Long getMaxRollingUnit() {
		return maxRollingUnit;
	}
	public void setMaxRollingUnit(Long maxRollingUnit) {
		this.maxRollingUnit = maxRollingUnit;
	}
	
	@XmlElement(name = "compress-rolled-unit")
	public String getCompRollingUnit() {
		return compRollingUnit;
	}
	public void setCompRollingUnit(String compRollingUnit) {
		this.compRollingUnit = compRollingUnit.toLowerCase();
	}
	
	@XmlElement(name = "repeated-message-reduction")
	public String getRepeatedMessageReduction() {
		return repeatedMessageReduction;
	}
	public void setRepeatedMessageReduction(String repeatedMessageReduction) {
		this.repeatedMessageReduction = repeatedMessageReduction.toLowerCase();
	}
    
	@Override
	public JSONObject toJson() {
		JSONObject object=new JSONObject();
		object.put("File Name", fileName);
		object.put("Rolling Type", (rollingType == 1) ? "Time-Based" : "Size-Based");
		
		if( rollingType == 1 ){
			object.put("Rolling Unit", getRollingUnitName(rollingUnit));
		}else if( rollingType == 2){
			object.put("Rolling Unit", rollingUnit);
			object.put("Max Rolled Unit ", maxRollingUnit);
		}
		
		object.put("Compress Rolled Unit", compRollingUnit);
		if(repeatedMessageReduction.equalsIgnoreCase("true")){
			object.put("Repeated Message Reduction", "True");
		}else{
			object.put("Repeated Message Reduction", "False");
		}
		return object;
	}
	
	private String getRollingUnitName(Long rollingUnitData) {
		String rollingUnitString = "";
		
		if( rollingUnitData != null ){
			if( rollingUnitData == 3L){
				rollingUnitString = "Minute";
			}else if( rollingUnitData == 4L ){
				rollingUnitString = "Hour";
			}else if( rollingUnitData == 5L ){
				rollingUnitString = "Daily";
			}
		}
		
		return rollingUnitString;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		if (rollingUnitValue != null) {
			setRollingUnit(Long.parseLong(rollingUnitValue));;
		}

		if (rollingType == null) {
			RestUtitlity.setValidationMessage(context, "Rolling Type must be specified. It can be Time-Based or Size-Based only.");
			isValid = false;
		} else if (rollingType == AlertConfigurationConstant.INVALID_TYPE) {
			RestUtitlity.setValidationMessage(context, RestValidationMessages.PARAMETER_ERR_MESSAGE + "Rolling Type. It can be Time-Based or Size-Based only.");
			isValid = false;
		} else if(rollingType == AlertConfigurationConstant.TIME_BASED_VALUE) {

			/**
			 * maxRollingUnit is only applicable for Size-Based rolling type, 
			 * so in case of Time-Based rolling type this field value must be Zero 
			 * according to GUI configuration.
			 ***/

			if (maxRollingUnit == null) {
				setMaxRollingUnit(0l);
			}
			if (maxRollingUnit != 0) {
				RestUtitlity.setValidationMessage(context, "Max Rolled Unit must not be specified when rolling type is Time-Based.");
				isValid = false;
			} if (rollingUnit == null) {
				RestUtitlity.setValidationMessage(context, "Rolled Unit must be specified. When rolling type is Time-Based, it can be Daily, Hour or Minute only.");
				isValid = false; 
			} else if (rollingUnit.intValue() != AlertConfigurationConstant.MINUTE_LONG && rollingUnit.intValue() != AlertConfigurationConstant.HOUR_LONG && rollingUnit.intValue() != AlertConfigurationConstant.DAILY_LONG) {
				RestUtitlity.setValidationMessage(context, RestValidationMessages.PARAMETER_ERR_MESSAGE + "Rolling Unit. When rolling type is Time-Based, it can be Daily, Hour or Minute only.");
				isValid = false;
			}
		} else if(rollingType == AlertConfigurationConstant.SIZE_BASED_VALUE) {
			if (rollingUnit == null) {
				RestUtitlity.setValidationMessage(context, "Rolled Unit must be specified. When rolling type is Size-Based, it must be Numeric.");
				isValid = false;
			} else if(rollingUnit == AlertConfigurationConstant.INVALID_TYPE || rollingUnit < 0) {
				RestUtitlity.setValidationMessage(context, RestValidationMessages.PARAMETER_ERR_MESSAGE + "Rolling Unit. When rolling type is Size-Based, it must be Numeric.");
				isValid = false;
			}
			if (maxRollingUnit == null) {
				RestUtitlity.setValidationMessage(context, "Max Rolled Unit must be specified and it must be Numeric.");
				isValid = false; 
			} else if (maxRollingUnit == AlertConfigurationConstant.INVALID_TYPE || maxRollingUnit < 0) {
				RestUtitlity.setValidationMessage(context, RestValidationMessages.PARAMETER_ERR_MESSAGE + "Max Rolled Unit. It must be Numeric.");
				isValid = false; 
			}
		}

		return isValid;
}}