package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.pm.pkg.factory.FactoryUtils;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.sm.serverprofile.ServerProfileData;
import com.elitecore.netvertex.gateway.radius.conf.impl.RadiusListenerConfigurationImpl;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RadiusListenerConfigurationFactory {

	private static final String MODULE = "RAD-FCTRY";
	private static final int POSITIVE_INT_MIN_VALUE = 1;
	private static final int MAX_THREADS = 500;
	private static final int SOCKET_RECEIVE_BUFFER_SIZE = 32767;
	private static final int SOCKET_SEND_BUFFER_SIZE = 32767;
	private static final int MAIN_THREAD_PRIORITY = 1;
	private static final int WORKER_THREAD_PRIORITY = 1;
	
	public RadiusListenerConfigurationImpl create(ServerInstanceData serverInstanceData, ServerProfileData serverProfileData) {
		
		Boolean radiusListnerEnabled = serverInstanceData.getRadiusEnabled();
		if (radiusListnerEnabled == null) {
			radiusListnerEnabled = false;
		}

		String radiusServiceURL = serverInstanceData.getRadiusUrl();
		String radiusIPAddress = "0.0.0.0";
		int radiusPort = 2813;

		if (radiusServiceURL != null) {
			try {
				URLData urlData = URLParser.parse(radiusServiceURL);
				radiusIPAddress = urlData.getHost();
				if (radiusIPAddress == null) {
					radiusIPAddress = CommonConstants.UNIVERSAL_IP;
					getLogger().warn(MODULE, "Using default radius listener address " + radiusIPAddress + ". Reason: Invalid IP configured");
				}
				if (urlData.getPort() == URLParser.UNKNOWN_PORT) {
					getLogger().warn(MODULE, "Using default RADIUS listener port " + radiusPort + ". Reason: RADIUS listener port not configured");
				} else if (urlData.getPort() > CommonConstants.MAX_PORT) {
					getLogger().warn(MODULE, "Using default RADIUS listener port " + radiusPort + ". Reason: Configured port exceeds range("
							+ CommonConstants.MAX_PORT + ")");
				} else {
					radiusPort = urlData.getPort();
				}
			} catch (InvalidURLException ex) {
				getLogger().warn(MODULE, "Using default RADIUS listener address " + radiusIPAddress + ":" + radiusPort
						+ ". Reason: Error while parsing URL: " + radiusServiceURL);
				LogManager.ignoreTrace(ex);
			}
		} else {
			getLogger().warn(MODULE, "Using default RADIUS listener address " + radiusIPAddress + ":" + radiusPort
					+ ". Reason: RADIUS listener address not configured");
		}

		int radiusQueueSize = serverProfileData.getRadiusQueueSize();
		if (serverProfileData.getRadiusQueueSize() == null) {
			radiusQueueSize = 150000;
		}

		int radiusMinimumThread = FactoryUtils
				.validateRange("radiusMinimumThread", serverProfileData.getRadiusMinThreads(), 1, POSITIVE_INT_MIN_VALUE, MAX_THREADS);
		int radiusMaximumThread = FactoryUtils
				.validateRange("radiusMaximumThread", serverProfileData.getRadiusMaxThreads(), 3, POSITIVE_INT_MIN_VALUE, MAX_THREADS);

		Boolean radiusDuplicateRequestCheckEnabled = serverProfileData.getDiameterDuplicateReqCheckEnabled();
		
		if (radiusDuplicateRequestCheckEnabled == null) {
			radiusDuplicateRequestCheckEnabled = false;
		}
		
		int radiusDuplicateRequestQueuePurgeInterval = 15;
		if (serverProfileData.getRadiusDuplicateReqPurgeInterval() != null) {

			radiusDuplicateRequestQueuePurgeInterval = FactoryUtils.validateRange("radiusDuplicateRequestQueuePurgeInterval"
					, serverProfileData.getRadiusDuplicateReqPurgeInterval(), radiusDuplicateRequestQueuePurgeInterval
					, 5, 25);
		}

	    if (radiusMinimumThread > radiusMaximumThread) {

	    	getLogger().warn(MODULE, "Considering minimum thread: " + radiusMaximumThread + ". Reason: Minimum no. of threads(" + radiusMinimumThread
				+ ") cannot be greater than maximum no. of threads(" + radiusMaximumThread + ")");
				radiusMinimumThread = radiusMaximumThread;
		}
	    
	    return new RadiusListenerConfigurationImpl(radiusListnerEnabled, radiusIPAddress, radiusPort
				, SOCKET_RECEIVE_BUFFER_SIZE, SOCKET_SEND_BUFFER_SIZE, radiusQueueSize
				, radiusMinimumThread, radiusMaximumThread, MAIN_THREAD_PRIORITY, WORKER_THREAD_PRIORITY
				, radiusDuplicateRequestCheckEnabled, radiusDuplicateRequestQueuePurgeInterval);
	}
}
