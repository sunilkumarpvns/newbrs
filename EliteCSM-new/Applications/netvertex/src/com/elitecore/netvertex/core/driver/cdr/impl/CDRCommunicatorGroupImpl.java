package com.elitecore.netvertex.core.driver.cdr.impl;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;
import com.elitecore.netvertex.core.driver.cdr.CDRCommunicatorGroup;
import com.elitecore.netvertex.core.driver.cdr.ValueProviderExtImpl;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class CDRCommunicatorGroupImpl extends ESCommunicatorGroupImpl<CDRDriver<ValueProviderExtImpl>> implements CDRCommunicatorGroup {

	private static final String MODULE = "CDR-COMM-GRP";
	
	public CDRCommunicatorGroupImpl() {
		super();
	}

	@Override
	public void handleRequest(ValueProviderExtImpl valueProvider) {

		CDRDriver<ValueProviderExtImpl> communicator = getCommunicator();

		if (communicator == null) {
			LogManager.getLogger().error(MODULE, "No CDR Communicator found in group");
			return;
		}

		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Processing request in CDR driver: " + communicator.getName());
		}
		
		try {
			communicator.handleRequest(valueProvider);
			return;
		} catch (DriverProcessFailedException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, "Driver processing failed for driver " + communicator.getDriverName() + ". Reason: "
						+ e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		}

		// using secondary communicator
		CDRDriver<ValueProviderExtImpl> secondaryCommunicator = getSecondaryCommunicator(communicator);
		if (secondaryCommunicator == null) {
			LogManager.getLogger().error(MODULE, "No secondary CDR Communicator found in group");
			return;
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Trying secondary CDR driver: " + secondaryCommunicator.getName());
		}

		try {
			secondaryCommunicator.handleRequest(valueProvider);
		} catch (DriverProcessFailedException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Secondary Driver processing failed " + communicator.getDriverName() + ". Reason: "
						+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

	}
	
}
