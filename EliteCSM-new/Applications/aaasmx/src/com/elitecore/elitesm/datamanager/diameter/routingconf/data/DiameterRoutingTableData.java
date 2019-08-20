package com.elitecore.elitesm.datamanager.diameter.routingconf.data;

import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "diameter-routing-table")
@ValidObject
@XmlType(propOrder = { "routingTableName", "overloadAction", "resultCode", "routingScript", "diameterRoutingTableEntries" })
public class DiameterRoutingTableData extends BaseData implements Comparable<DiameterRoutingTableData>,Validator{
	private String routingTableId;
	
	@Expose
	@SerializedName("Routing Table Name")
	@NotEmpty(message = "Routing Table Name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	@Length(max = 128, message = "Length of Routing Table Name must not greater than 128.")
	private String routingTableName;
	
	@Expose
	@SerializedName("Overload Action")
	@NotEmpty(message = "Overload Action must be specified.")
	@Pattern(regexp = "DROP|REJECT", message = "Invalid value of Overload Action. Value could be 'DROP' or 'REJECT'.")
	private String overloadAction;
	
	@Expose
	@SerializedName("Result Code")
	private Integer resultCode;
	
	@Expose
	@SerializedName("Routing Script")
	private String routingScript;
	
	private List<DiameterRoutingConfData> diameterRoutingTableEntries;
	
	@XmlTransient
	public String getRoutingTableId() {
		return routingTableId;
	}
	public void setRoutingTableId(String routingTableId) {
		this.routingTableId = routingTableId;
	}
	
	@XmlElement(name = "routing-table-name")
	public String getRoutingTableName() {
		return routingTableName;
	}
	public void setRoutingTableName(String routingTableName) {
		this.routingTableName = routingTableName;
	}
	
	@XmlElement(name = "overload-action")
	public String getOverloadAction() {
		return overloadAction;
	}
	public void setOverloadAction(String overloadAction) {
		this.overloadAction = overloadAction;
	}
	
	@XmlElement(name = "result-code")
	public Integer getResultCode() {
		return resultCode;
	}
	public void setResultCode(Integer resultCode) {
		this.resultCode = resultCode;
	}
	
	@XmlElement(name = "routing-script")
	public String getRoutingScript() {
		return routingScript;
	}
	public void setRoutingScript(String routingScript) {
		this.routingScript = routingScript;
	}
	
	@XmlElementWrapper(name = "diameter-routing-entries")
	@XmlElement(name = "routing-configuration")
	public List<DiameterRoutingConfData> getDiameterRoutingTableEntries() {
		return diameterRoutingTableEntries;
	}
	
	public void setDiameterRoutingTableEntries(List<DiameterRoutingConfData> diameterRoutingTableEntries) {
		this.diameterRoutingTableEntries = diameterRoutingTableEntries;
	}
	
	@Override
	public int compareTo(DiameterRoutingTableData diameterRoutingTableData) {
		int lastCmp = routingTableName.compareTo(diameterRoutingTableData.routingTableName);
        return lastCmp;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		boolean isValid = true;

		if (Strings.isNullOrBlank(overloadAction) == false) {
			if (overloadAction.equals("DROP")) {
				resultCode = 0;
			} else if (overloadAction.equals("REJECT")) {
				if (resultCode == null) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Result Code must be specified.");
				} else if (resultCode < 1000 || resultCode > 5999) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Result Code must be in Range of 1000 to 5999.");
				}
			}
		}
		
		return isValid;
		
	}
	
}