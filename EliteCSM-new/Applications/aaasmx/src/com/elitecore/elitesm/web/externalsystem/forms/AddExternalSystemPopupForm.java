package com.elitecore.elitesm.web.externalsystem.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class AddExternalSystemPopupForm  extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private boolean weightageSelection = true;
	
	
	private List<ExternalSystemInterfaceInstanceData> extenalSystemList;

	public List<ExternalSystemInterfaceInstanceData> getExtenalSystemList() {
		return extenalSystemList;
	}

	public void setExtenalSystemList(
			List<ExternalSystemInterfaceInstanceData> extenalSystemList) {
		this.extenalSystemList = extenalSystemList;
	}

	public boolean isWeightageSelection() {
		return weightageSelection;
	}

	public void setWeightageSelection(boolean weightageSelection) {
		this.weightageSelection = weightageSelection;
	}
	
	
	
}
