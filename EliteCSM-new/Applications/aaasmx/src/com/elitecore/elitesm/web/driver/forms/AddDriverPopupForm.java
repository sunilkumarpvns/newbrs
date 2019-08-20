package com.elitecore.elitesm.web.driver.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class AddDriverPopupForm  extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private boolean weightageSelection = true;
	
	private List<DriverInstanceData> driverInstanceDataList;

	public List<DriverInstanceData> getDriverInstanceDataList() {
		return driverInstanceDataList;
	}

	public void setDriverInstanceDataList(
			List<DriverInstanceData> driverInstanceDataList) {
		this.driverInstanceDataList = driverInstanceDataList;
	}

	public boolean isWeightageSelection() {
		return weightageSelection;
	}

	public void setWeightageSelection(boolean weightageSelection) {
		this.weightageSelection = weightageSelection;
	}

	
	
}
