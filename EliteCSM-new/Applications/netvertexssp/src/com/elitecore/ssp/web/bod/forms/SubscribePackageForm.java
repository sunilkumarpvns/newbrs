package com.elitecore.ssp.web.bod.forms;

import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDPackage;
import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class SubscribePackageForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;

	private Long bodPackageId;
	BoDPackage bodPackageData;

	public Long getBodPackageId() {
		return bodPackageId;
	}
	public void setBodPackageId(Long bodPackageId) {
		this.bodPackageId = bodPackageId;
	}
	public BoDPackage getBodPackageData() {
		return bodPackageData;
	}
	public void setBodPackageData(BoDPackage bodPackageData) {
		this.bodPackageData = bodPackageData;
	}
	 
}
