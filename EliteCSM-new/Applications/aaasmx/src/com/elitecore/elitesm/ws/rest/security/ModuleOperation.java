package com.elitecore.elitesm.ws.rest.security;

/**
 * Define special kind of rights for module specific, it contains either <br>
 * specific operation <b>(e.g search,create,delete,update)</b> or <b>none</b>. <br>
 * The main purpose of the class is to restrict such illegal operation which
 * does not define in module <br>
 * <br><b>http://ip:port/aaasmx/cxfservices/restful/v1/server/elitecsmserver/cli/eliteAAA </b>
 * <br> in above URL there is a request for getting appropriate o/p for inputted command<br>
 * so there is not make sense to perform any specific override operation on that as we generally
 * doing with other module. now in future if you want to configure this module for allow to override
 * any specific operation.One can configure with specific operation with below code.
 * 
 * 
 * 
 * 
 * @author Tejas.P.Shah
 *
 */
public enum ModuleOperation {
	cli("NONE");
	
	String[] opreations;

	ModuleOperation(String... operations) {
		this.opreations = operations;
	}

	public String[] getModuleOperation() {
		return opreations;
	}

}
