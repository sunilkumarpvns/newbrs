package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

/**
 * A class that defines import operation on Package Data from Web Service
 */
public class PackageManagementRequest {
	private PkgData pkgData;
	private String action;
	private String parameter1;
	private String parameter2;
	private ACLModules aclModule;
	public PackageManagementRequest(PkgData pkgData,String action,String parameter1,String parameter2,ACLModules aclModule){
		this.pkgData = pkgData;
		this.action = action;
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;
		this.aclModule = aclModule;
	}
	public PkgData getPkgData() {
		return pkgData;
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

	public ACLModules getAclModule(){
		return aclModule;
	}

	public String toString(){
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);

		builder.append("Action ", action);
		builder.append("Parameter 1 ", getParameter1());
		builder.append("Parameter 2 ", getParameter2());

		return builder.toString();
	}

}
