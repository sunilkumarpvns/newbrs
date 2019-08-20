package external;

import com.elitecore.aaa.core.data.AccountData
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.serverx.manager.scripts.ScriptContext;
import com.elitecore.core.servicex.ServiceResponse;

/*
 * This is sample for Authentication service driver
 */
class SampleDriverScript extends DriverScript{
	
	private static final String NAME = "SAMPLE_DRIVER_SCRIPT";
	
	public SampleDriverScript(ScriptContext scriptContext){
		super(scriptContext);
	}
	
	
	/*
	* NOTE: This is the most important method to be implemented.
	* The name given here will be used to identify the script, the same name MUST be
	* configured in GUI for executing the script.
	* Do not use spaces in the name.
    */
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected void post(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		//apply business logic here
		/*RadAuthRequest authRequest = (RadAuthRequest)serviceRequest;
		AccountData accountData = authRequest.getAccountData();
		if(accountData != null){
			//this means that the account data is found so changing existing account data
			accountData.setUserIdentity("CHANGED_USERNAME");
			accountData.setUserName("CHANGED_USERNAME");
			accountData.setAccountStatus("Active");
		}else{
			//this means that the account data was not found so forming new account data and setting in request
			AccountData accountDataNew = new AccountData();
			accountDataNew.setUserIdentity("CHANGED_USERNAME");
			accountDataNew.setUserName("CHANGED_USERNAME");
			//this is most important in case when forming new account data
			authRequest.setAccountData(accountDataNew);
		}*/
	}
	
	@Override
	protected void pre(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		//apply business logic here
		/*RadAuthRequest authRequest = (RadAuthRequest)serviceRequest;
		AccountData accountDataNew = new AccountData();
		accountDataNew.setUserIdentity("CHANGED_USERNAME");
		accountDataNew.setUserName("CHANGED_USERNAME");
		//this is most important after forming new account data don't forget to set in request
		authRequest.setAccountData(accountDataNew);*/
	}
}
