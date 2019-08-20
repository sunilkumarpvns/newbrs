package com.elitecore.diameterapi.diameter.common.routerx.failure;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.TypeNotSupportedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.FailureActionResultCodes;

public class RecordFailureAction implements RoutingFailureAction {

	private static final String MODULE = "RECORD-FLR-ACT";
	private  String failureArgs;
	private List<String> warnings;
	private RouterContext routerContext;
	private CDRDriver<DiameterPacket> cdrDriver;
	public RecordFailureAction(RouterContext routerContext, String failureArgs){
		this.failureArgs = failureArgs;
		this.routerContext = routerContext;
		warnings = new ArrayList<String>();
	}
	
	
	
	
	@Override
	public void init() {
		if(Strings.isNullOrEmpty(failureArgs)){
			warnings.add("No CDR Driver Name found for " + DiameterFailureConstants.RECORD + "  Failure Action");
			return;
		}
		
		failureArgs = failureArgs.trim();
		try{
			cdrDriver = routerContext.getDiameterCDRDriver(failureArgs);
		}catch(DriverInitializationFailedException exception){
			LogManager.getLogger().warn(MODULE, "Error while initializing Driver : " + failureArgs + 
					". Reason : "+exception.getMessage());
			LogManager.getLogger().trace(MODULE, exception);
			warnings.add("Error: " + exception.getMessage() + " initializing CDR Driver: " + failureArgs + 
					" in " + DiameterFailureConstants.RECORD + " Failure Action");
		}catch(DriverNotFoundException e){
			LogManager.getLogger().warn(MODULE, "Error while getting Driver : " + failureArgs + 
					". Reason : "+e.getMessage());
			warnings.add("CSV Driver Configuration with Name: " + failureArgs + 
				" is not found");
		}catch(TypeNotSupportedException ex){
			LogManager.getLogger().warn(MODULE, "Driver type is not supported for  : " + failureArgs + 
					". Driver Type Should be Classic CSV Driver. Reason : "+ex.getMessage());
			warnings.add("CSV Driver Type mismatches for  Name: " + failureArgs + 
				" . Driver Type should be Classic CSV Driver");
		}
	}
	
	@Override
	public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession,
			DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity,
			String originPeerName) {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Performing " + DiameterFailureConstants.RECORD + 
			" Failure Action with  Driver : " + cdrDriver.getDriverName() + 
			" for Session-ID="+failureAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID) + 
			" HbH-ID=" + failureAnswer.getHop_by_hopIdentifier() );
		}
		try{
			cdrDriver.handleRequest(originRequest);
		}catch(DriverProcessFailedException exception){
			LogManager.getLogger().error(MODULE, "Error while recording Diameter Request with HbH-ID=" 
					+ originRequest.getHop_by_hopIdentifier() + ".");
			LogManager.getLogger().trace(MODULE, exception);
		}
		return new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, failureAnswer);
	}

	@Override
	public List<String> getWarnings() {
		return warnings;
	}
	
}
