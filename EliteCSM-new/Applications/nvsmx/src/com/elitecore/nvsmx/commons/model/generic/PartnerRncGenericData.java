package com.elitecore.nvsmx.commons.model.generic;

import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.nvsmx.pd.controller.PartnerRnCModules;

public class PartnerRncGenericData extends ResourceData {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private transient PartnerRnCModules partnerRnCModules;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PartnerRnCModules getPartnerRnCModules() {
		return partnerRnCModules;
	}

	public void setPartnerRnCModules(PartnerRnCModules partnerRnCModules) {
		this.partnerRnCModules = partnerRnCModules;
	}

	@Override
	public String getResourceName() {
		return name;
	}
}
