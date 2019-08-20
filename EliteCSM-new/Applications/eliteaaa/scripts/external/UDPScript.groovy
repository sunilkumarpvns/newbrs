package external;


import com.elitecore.aaa.radius.plugins.script.ExternalRadiusScript;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.manager.scripts.ScriptContext;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

/**
 * For creating a script for Radius External System, extend ExternalRadiusScript class and implement
 * the required methods.
**/
class UDPScript extends ExternalRadiusScript {
	private static final String MODULE = "UDP_SCRIPT";
	
	public UDPScript(ScriptContext scriptContext){
		super(scriptContext);
	}
	
	/**
	 * This method gives access of the original Service Request received by AAA server as well
	 * the packet that will be forwarded to the external system.
	 * So any alteration code in request to external system will come here.
	**/
	@Override
	public void pre(RadServiceRequest originalRequest, RadiusPacket remoteRequest) {
		//Fetching any attribute from the request radius packet
		IRadiusAttribute userName = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.USER_NAME);
		
		//this shows how to add any attribute in packet
		//remoteRequest.addAttribute(userName);
		
		LogManager.getLogger().info(MODULE, "UDPScript pre method applied successfully...");
	}

	@Override
	public void post(RadServiceRequest originalRequest, RadiusPacket remoteRequest, RadServiceResponse originalResponse, RadiusPacket remoteResponse) {
		//access of the attributes can be got same as in pre method
		
		LogManager.getLogger().info(MODULE, "UDPScript post method applied successfully...");
	}
	
	/**
	 * NOTE: This is the most important method to be implemented. 
	 * The name given here will be used to identify the script, the same name MUST be
	 * configured in GUI for executing the script.
	 * Do not use spaces in the name.
	**/

	@Override
	public String getName(){
		return "UDPScript";
	}
}
