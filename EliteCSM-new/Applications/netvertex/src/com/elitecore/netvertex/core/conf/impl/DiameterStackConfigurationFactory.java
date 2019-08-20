package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.pm.pkg.factory.FactoryUtils;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.sm.serverprofile.ServerProfileData;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.netvertex.gateway.diameter.conf.impl.DiameterStackConfigurationImpl;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class DiameterStackConfigurationFactory {

	private static final String MODULE = "DIA-STACK-FCTRY";
	private static final int POSITIVE_INT_MIN_VALUE = 1;
	private static final int MAX_THREADS = 500;
	private static final int SOCKET_RECEIVE_BUFFER_SIZE = -1;
	private static final int SOCKET_SEND_BUFFER_SIZE = -1;
	private static final int MAIN_THREAD_PRIORITY = Thread.MAX_PRIORITY;
	private static final int WORKER_THREAD_PRIORITY = Thread.NORM_PRIORITY;

	public DiameterStackConfigurationImpl create(ServerInstanceData serverInstanceData, ServerProfileData serverProfileData) {
		
		Boolean diameterListnerEnabled = serverInstanceData.getDiameterEnabled();
		if (diameterListnerEnabled == null) {
			diameterListnerEnabled = true;
		}

		String diameterIPAddress = "0.0.0.0";
		int diameterPort = DiameterConstants.DIAMETER_SERVICE_PORT;

		String diameterServiceURL = serverInstanceData.getDiameterUrl();
		if (diameterServiceURL != null) {
			try {
				URLData urlData = URLParser.parse(diameterServiceURL);
				diameterIPAddress = urlData.getHost();
				if (diameterIPAddress == null) {
					diameterIPAddress = CommonConstants.UNIVERSAL_IP;
					getLogger().warn(MODULE, "Using default diameter address " + diameterIPAddress + ". Reason: Invalid IP configured");
				}
				if (urlData.getPort() == URLParser.UNKNOWN_PORT) {
					LogManager.getLogger().warn(MODULE, "Using default diameter port " + diameterPort + ". Reason: Diameter port not configured");
				} else if (urlData.getPort() > CommonConstants.MAX_PORT) {
					getLogger().warn(MODULE, "Using default diameter port " + diameterPort + ". Reason: Configured port exceeds range("
							+ CommonConstants.MAX_PORT + ")");
				} else {
					diameterPort = urlData.getPort();
				}
			} catch (InvalidURLException invalidURLException) {
				LogManager.getLogger().warn(MODULE, "Using default diameter address " + diameterIPAddress + ":" + diameterPort
						+ ". Reason: Error while parsing URL: " + diameterServiceURL);
				LogManager.ignoreTrace(invalidURLException);
			}
		} else {
			LogManager.getLogger().warn(MODULE, "Using default diameter address " + diameterIPAddress + ":" + diameterPort
					+ ". Reason: diameter address not configured");
		}

		int diameterQueueSize = serverProfileData.getDiameterQueueSize();
		if (diameterServiceURL == null) {
			diameterQueueSize = 150000;
		}

		int diameterMinimumThread = FactoryUtils
				.validateRange("diameterMinimumThread", serverProfileData.getDiameterMinThreads(), 1, POSITIVE_INT_MIN_VALUE, MAX_THREADS);
		int diameterMaximumThread = FactoryUtils
				.validateRange("diameterMaximumThread", serverProfileData.getDiameterMaxThreads(), 3, POSITIVE_INT_MIN_VALUE, MAX_THREADS);

		String diameterOriginHost = serverInstanceData.getDiameterOriginHost();
		if (diameterOriginHost == null) {
			diameterOriginHost = "netvertex.elitecore.com";
		}

		String diameterOriginRealm = serverInstanceData.getDiameterOriginRealm();
		if (diameterOriginRealm == null) {
			diameterOriginRealm = "elitecore.com";
		}

		Integer diameterDWInterval = serverProfileData.getDiameterDwInterval();
		if (diameterDWInterval == null) {
			diameterDWInterval = 60;
		}

		Boolean diameterDuplicateRequestCheckEnabled = serverProfileData.getDiameterDuplicateReqCheckEnabled();
		if (diameterDuplicateRequestCheckEnabled == null) {
			diameterDuplicateRequestCheckEnabled = false;
		}

		int diameterDuplicateRequestPurgeInterval = 15;
		if (serverProfileData.getDiameterDuplicateReqPurgeInterval() != null) {

			diameterDuplicateRequestPurgeInterval = FactoryUtils.validateRange("diameterDuplicateRequestPurgeInterval"
					, serverProfileData.getDiameterDuplicateReqPurgeInterval(), diameterDuplicateRequestPurgeInterval, 5, 25);
		}

		Integer diameterSessionCleanupInterval = serverProfileData.getDiameterSessionCleanupInterval(); // Seconds in a Day(24 hours)
		if (diameterSessionCleanupInterval == null) {
			diameterSessionCleanupInterval = 86400;
		}

		Integer diameterSessionTimeout = serverProfileData.getDiameterSessionTimeout(); // Seconds in a Day(24 hours)
		if (diameterSessionTimeout == null) {
			diameterSessionTimeout = 86400;
		}
		
		return new DiameterStackConfigurationImpl(diameterListnerEnabled, diameterIPAddress, diameterPort
				, SOCKET_RECEIVE_BUFFER_SIZE, SOCKET_SEND_BUFFER_SIZE
				, diameterQueueSize, diameterMinimumThread, diameterMaximumThread, MAIN_THREAD_PRIORITY, WORKER_THREAD_PRIORITY
				,  /*keepAliveTime*/ 2000, diameterOriginHost, diameterOriginRealm, diameterDWInterval, diameterSessionCleanupInterval
				, diameterSessionTimeout, diameterDuplicateRequestCheckEnabled, diameterDuplicateRequestPurgeInterval);
	}
}
