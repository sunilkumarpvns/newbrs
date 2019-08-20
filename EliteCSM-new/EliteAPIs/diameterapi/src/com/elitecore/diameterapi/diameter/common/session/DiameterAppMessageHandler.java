/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.session.IApplicationListener;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * @author harsh.patel
 * @author narendra.pathai
 */
public class DiameterAppMessageHandler {

	private final String MODULE = "DIAMETER-APP-HANDLER";
	private final Map<Long, IApplicationListener> appListeners;
	private final IStackContext stackContext;

	public DiameterAppMessageHandler(IStackContext stackContext) {
		this.appListeners = new ConcurrentHashMap<Long, IApplicationListener>();
		this.stackContext = stackContext;
	}

	public void addApplicationListener(ApplicationListener diameterApplicationListener) {
		for (ApplicationEnum applicationEnum : diameterApplicationListener.getApplicationEnum()) {
			appListeners.put(applicationEnum.getApplicationId(), diameterApplicationListener);
		}
	}

	public void handleReceivedRequest(DiameterRequest request,  Session diameterSession) throws UnsupportedApplicationException {

		IApplicationListener diameterApplicationListener = appListeners.get(request.getApplicationID());

		if (diameterApplicationListener == null) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Application Listener not registered for App-ID=" + 
						request.getApplicationID());
			}

			throw new UnsupportedApplicationException(request.getApplicationID());
		}

		try {
			diameterApplicationListener.handleApplicationRequest(diameterSession, request);
		} catch(Exception e) {
			LogManager.getLogger().error(MODULE, "Unknown Exception, While processing request of Application : " + request.getApplicationID() + " Error: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			DiameterAnswer answer = new DiameterAnswer(request, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
			try {
				sendDiameterAnswer(request, answer);
			} catch (CommunicationException ex) {
				LogManager.getLogger().error(MODULE, "Could not send Error-Answer, Reason: " + ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
			}

		}
	}

	private void sendDiameterAnswer(DiameterRequest request, DiameterAnswer answer) throws CommunicationException {
		stackContext.getPeerCommunicator(request.getRequestingHost()).sendAnswer(request, answer);
	}
}
