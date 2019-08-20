package com.elitecore.ssp.web.promotional.forms;

import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class UnsubscribePromotionForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;

	private Long addOnPackageId;
	private String selectedAddonStatus;

	public Long getAddOnPackageId() {
		return addOnPackageId;
	}

	public void setAddOnPackageId(Long addOnPackageId) {
		this.addOnPackageId = addOnPackageId;
	}

	public String getSelectedAddonStatus() {
		return selectedAddonStatus;
	}

	public void setSelectedAddonStatus(String selectedAddonStatus) {
		this.selectedAddonStatus = selectedAddonStatus;
	}
	

	
}
