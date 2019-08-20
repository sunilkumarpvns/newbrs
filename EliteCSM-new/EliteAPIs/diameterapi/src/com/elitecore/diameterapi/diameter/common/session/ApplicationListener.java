/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.session;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.session.IApplicationListener;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;
import com.elitecore.diameterapi.plugins.DiameterPlugin;
import com.elitecore.diameterapi.plugins.DiameterPluginManager;


/**
 * @author pulindani
 *
 */
public abstract class ApplicationListener implements IApplicationListener {

	protected IStackContext stackContext;
	private static final String MODULE = "APP-LSTNR";
	private CompositeSessionReleaseIndicator sessionReleaseIndicator;
	private ApplicationEnum[] applicationEnums;
	private boolean initialized;
	private DiameterPluginManager diameterPluginManager = DiameterPluginManager.NO_PLUGIN_MANAGER;

	public ApplicationListener(IStackContext stackContext) {
		this.stackContext = stackContext;
		this.sessionReleaseIndicator = new CompositeSessionReleaseIndicator();
	}

	/**
	 * @throws IllegalArgumentException if no application enums are provided 
	 */
	public ApplicationListener(IStackContext stackContext, 
			ApplicationEnum... applicationEnums) {
		this.stackContext = stackContext;
		this.applicationEnums = checkNotNull(applicationEnums, "applicationEnums is null");
		checkArgument(applicationEnums.length > 0, "at least one applicationEnum is required");
		this.sessionReleaseIndicator = new CompositeSessionReleaseIndicator();
	}

	public void init() throws AppListenerInitializationFaildException{

		if (initialized) {
			return;
		}

		ApplicationEnum[] applicationEnums = getApplicationEnum();

		for (ApplicationEnum applicationEnum : applicationEnums) {
			if (applicationEnum.getApplication() == null) {
				this.sessionReleaseIndicator.addIndicator(new AppDefaultSessionReleaseIndicator());
				continue;
			}

			SessionReleaseIndiactor sessionReleaseIndicator = createSessionReleaseIndicator(applicationEnum);
			if (sessionReleaseIndicator == null) {
				sessionReleaseIndicator = new AppDefaultSessionReleaseIndicator();
			}

			this.sessionReleaseIndicator.addIndicator(sessionReleaseIndicator);
		}

		initialized = true;
	}

	/*
        Pre processing hook. Hook will executed before submitting application to process request
         */
	public void preProcess(Session session, DiameterRequest diameterRequest) {

	}
	/**
	 * A hook method which is called before sending the response to the peer.
	 */
	public void preProcess(DiameterSession session, DiameterAnswer diameterAnswer) {
		
	}

	public void postProcess(DiameterSession session, DiameterAnswer diameterAnswer) {

	}

	public abstract String getApplicationIdentifier();

	protected final void registerPlugins(Map<String, DiameterPlugin> plugins, List<PluginEntryDetail> inPlugins,List<PluginEntryDetail> outPlugins){
		diameterPluginManager = new DiameterPluginManager(plugins);
		diameterPluginManager.registerInPlugins(inPlugins);
		diameterPluginManager.registerOutPlugins(outPlugins);
	}

	public void handleApplicationRequest(Session session, DiameterRequest diameterRequest){
		stackContext.updateRealmInputStatistics(diameterRequest, diameterRequest.getPeerData().getRealmName(), RoutingActions.LOCAL);
		
		diameterPluginManager.applyInPlugins(diameterRequest, null, session);
		preProcess(session, diameterRequest);
		processApplicationRequest(session, diameterRequest);
	}

	protected abstract void processApplicationRequest(Session session, DiameterRequest diameterRequest);

	public ApplicationEnum[] getApplicationEnum(){
		return applicationEnums;
	}

	protected boolean sessionEligibleToRelease(DiameterPacket diameterPacket){
		return sessionReleaseIndicator.isEligible(diameterPacket);
	}


	public boolean isSupportedApplication(long applicationId) {
		for (ApplicationEnum applicationEnum : applicationEnums) {
			if (applicationEnum.getApplicationId() == applicationId) {
				return true;
			}
		}
		return false;
	}

	protected abstract @Nullable SessionReleaseIndiactor createSessionReleaseIndicator(@Nonnull ApplicationEnum applicationEnum);

	public @Nonnull SessionReleaseIndiactor getSessionReleaseIndicator() {
		return sessionReleaseIndicator;
	}

	/**
	 * Wraps multiple indicators with a composite indicator implementing Composite pattern.
	 * 
	 * Session becomes eligible for removal if any of the registered indicators indicate removal
	 * of session.
	 * 
	 * @author narendra.pathai
	 *
	 */
	class CompositeSessionReleaseIndicator implements SessionReleaseIndiactor {
		private List<SessionReleaseIndiactor> sessionReleaseIndicators = new ArrayList<SessionReleaseIndiactor>(1);

		void addIndicator(@Nonnull SessionReleaseIndiactor sessionReleaseIndicator) {
			sessionReleaseIndicators.add(sessionReleaseIndicator);
		}

		@Override
		public boolean isEligible(DiameterPacket diameterPacket) {
			for (SessionReleaseIndiactor sessionReleaseIndicator : sessionReleaseIndicators) {
				if (sessionReleaseIndicator.isEligible(diameterPacket)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Sends answer to the requesting host
	 * @param session
	 * @param request
	 * @param answer
	 * @return
	 * @throws CommunicationException 
	 */
	protected boolean sendDiameterAnswer(@Nonnull DiameterSession session, 
			@Nonnull DiameterRequest request, 
			@Nonnull DiameterAnswer answer) throws CommunicationException {
		
		preProcess(session, answer);

		copyProxyInfo(request, answer);

		diameterPluginManager.applyOutPlugins(null, answer.getAsDiameterAnswer(), session);

			DiameterPeerCommunicator requestingCommunicator = stackContext.getPeerCommunicator(request.getRequestingHost());
			if (requestingCommunicator == null) {
			throw new CommunicationException("Diameter peer communicator not found for host: " + request.getRequestingHost());
			}
			
			requestingCommunicator.sendAnswer(request, answer);
			
			postProcess(session, answer);
			return true;
		}

	private void copyProxyInfo(DiameterRequest request, DiameterAnswer answer) {
		List<IDiameterAVP> diameterAvps = answer.getAVPList(DiameterAVPConstants.PROXY_INFO);
		if (Collectionz.isNullOrEmpty(diameterAvps)) {
			List<IDiameterAVP> iDiameterAvpList = request.getAVPList(DiameterAVPConstants.PROXY_INFO);
			if (Collectionz.isNullOrEmpty(iDiameterAvpList) == false) {
				answer.addAvps(iDiameterAvpList);
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Original Proxy Info AVPs added back to the Answer.");
			}
		} else {
			LogManager.getLogger().warn(MODULE, "Diameter Request Not Found for the Answer with Hop-By-Hop-ID = " 
					+answer.getHop_by_hopIdentifier());
		}
	}
}
