package com.elitecore.nvsmx.commons.model.generic;

import java.io.Serializable;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.nvsmx.pd.controller.PartnerRnCModules;
import com.elitecore.nvsmx.policydesigner.PolicyDesignerModules;
import com.google.gson.annotations.SerializedName;

/**
 * This class will used to search module wise entity. 
 * @author Dhyani.Raval
 *
 */
public class GenericSearchData implements Serializable {

	private static final long serialVersionUID = 1L;
	@SerializedName(FieldValueConstants.NAME)private String name;
	@SerializedName(FieldValueConstants.STATUS)private String status;
	private transient PolicyDesignerModules policyDesignerModules;
	private transient PartnerRnCModules partnerRnCModules;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public PolicyDesignerModules getPolicyDesignerModules() {
		return policyDesignerModules;
	}

	public void setPolicyDesignerModules(
			PolicyDesignerModules policyDesignerModules) {
		this.policyDesignerModules = policyDesignerModules;
	}

	public PartnerRnCModules getPartnerRnCModules() {
		return partnerRnCModules;
	}

	public void setPartnerRnCModules(PartnerRnCModules partnerRnCModules) {
		this.partnerRnCModules = partnerRnCModules;
	}

}
