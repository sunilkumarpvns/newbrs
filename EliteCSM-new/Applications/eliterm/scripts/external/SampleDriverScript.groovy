package external;

import com.elitecore.core.commons.plugins.script.DriverScript
import com.elitecore.core.serverx.manager.scripts.ScriptContext
import com.elitecore.core.servicex.ServiceRequest
import com.elitecore.core.servicex.ServiceResponse

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
	}
	
	@Override
	protected void pre(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		//apply business logic here
	}

}
