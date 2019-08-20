package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

/**
 * A class that defines import operation on Service Type from Web Service
 */
public class ServiceTypeManagementRequest {
	private DataServiceTypeDataExt dataServiceTypeDataExt;
	private String action;
	private String parameter1;
	private String parameter2;

	public ServiceTypeManagementRequest(DataServiceTypeDataExt dataServiceTypeDataExt, String action, String parameter1, String parameter2){
		this.dataServiceTypeDataExt = dataServiceTypeDataExt;
		this.action = action;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
	}
	public DataServiceTypeDataExt getDataServiceTypeDataExt() {
		return dataServiceTypeDataExt;
	}

	public String getParameter1() {
		return parameter1;
	}

	public String getParameter2() {
		return parameter2;
	}

	public String getAction() {
		return action;
	}

	public String toString(){
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);

		builder.append("Action ", action);
		builder.append("Parameter 1 ", getParameter1());
		builder.append("Parameter 2 ", getParameter2());

		return builder.toString();
	}

}
