package com.elitecore.ssp.web.promotional.forms;

import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class SubscribePromotionForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;

	private Long addOnPackageId;

	public Long getAddOnPackageId() {
		return addOnPackageId;
	}

	public void setAddOnPackageId(Long addOnPackageId) {
		this.addOnPackageId = addOnPackageId;
	}
}