package com.elitecore.ssp.web.parentalcontrol.forms;

import java.util.List;

import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicy;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class ChildAccountManageForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	
	private AddOnPackage[] promotionalData;
	private List<ParentalPolicy> parentalPolicyData;
	private String daysName[]={"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	private String colorsName[]={"black","yellow","green","blue","cyan","red"};
		
	
	public String[] getDaysName() {
		return daysName;
	}

	public String[] getColorsName() {
		return colorsName;
	}

	public void setColorsName(String[] colorsName) {
		this.colorsName = colorsName;
	}

	public AddOnPackage[] getPromotionalData() {
		return promotionalData;
	}

	public void setPromotionalData(AddOnPackage[] promotionalData2) {
		this.promotionalData = promotionalData2;
	}

	public List<ParentalPolicy> getParentalPolicyData() {
		return parentalPolicyData;
	}

	public void setParentalPolicyData(List<ParentalPolicy> parentalPolicyData) {
		this.parentalPolicyData = parentalPolicyData;
	}

 
}
