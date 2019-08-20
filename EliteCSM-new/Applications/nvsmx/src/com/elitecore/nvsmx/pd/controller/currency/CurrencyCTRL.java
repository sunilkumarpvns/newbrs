package com.elitecore.nvsmx.pd.controller.currency;


import static com.opensymphony.xwork2.Action.SUCCESS;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pd.currency.CurrencyData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;

@ParentPackage(value = "pd")
@Namespace("/pd/currency")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "currency" }),

})
public class CurrencyCTRL extends RestGenericCTRL<CurrencyData> {

	private static final long serialVersionUID = -1624092454880224392L;
	

	@Override
	public ACLModules getModule() {
		return ACLModules.CURRENCY;
	}

	@Override
	public CurrencyData createModel() {
		return new CurrencyData();
	}

	
	@Override
	public void validate() {
		//override this method to remove default feature
	}

	
}
