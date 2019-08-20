package com.elitecore.diameterapi.diameter.common.routerx.agent;
import javax.annotation.Nonnull;

import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;

public abstract class DiameterAgent {
	public static final String ROUTING_ENTRY = "ROUTING_ENTRY";
	protected RouterContext routerContext;
	
	protected DiameterAgent(RouterContext routerContext) {
		this.routerContext = routerContext;
	}
	
	/**
	 * 
	 * This method handles incoming Diameter Request
	 * 
	 * @param diameterRequest
	 * @param diameterSession
	 * @param routingEntry
	 */
	public abstract void routeRequest(DiameterRequest request, 
			DiameterSession diameterSession, RoutingEntry routingEntry) throws RoutingFailedException;
	
	
	
	protected void sendClientInitiatedRequest(DiameterSession session, DiameterRequest request, 
			@Nonnull ResponseListener listener, String destinationHost, RoutingActions routeAction) throws CommunicationException {

		DiameterPeerCommunicator peerCommunicator = routerContext.getPeerCommunicator(destinationHost);
		
		if(peerCommunicator == null) {
			throw new CommunicationException(destinationHost + " not found");
		}
		
		peerCommunicator.sendClientInitiatedRequest(session, request, listener);
		
		routerContext.updateRealmOutputStatistics(request, 
				routerContext.getPeerData(destinationHost).getRealmName(), 
				routeAction);
	}
	
	protected void sendServerInitiatedRequest(DiameterSession session, DiameterRequest request, 
			@Nonnull ResponseListener listener, String destinationHost, RoutingActions routeAction) throws CommunicationException {

		DiameterPeerCommunicator peerCommunicator = routerContext.getPeerCommunicator(destinationHost);
		
		if(peerCommunicator == null) {
			throw new CommunicationException(destinationHost + " not found");
		}
		
		peerCommunicator.sendServerInitiatedRequest(session, request, listener);
		
		routerContext.updateRealmOutputStatistics(request, 
				routerContext.getPeerData(destinationHost).getRealmName(), 
				routeAction);
	}
	
	protected void sendAnswer(Session session, DiameterRequest request, DiameterAnswer answer, String destinationHost, RoutingActions routeAction) throws CommunicationException{
		
		/**
		 * Before sending packet:
		 * 	IF it contains Elitecore's Result code THEN
		 * 		replace it with 3002(DIAMETER_UNABLE_TO_DELIVER)
		 */
		IDiameterAVP resultCodeAVP = answer.getAVP(DiameterAVPConstants.RESULT_CODE);
		if (resultCodeAVP != null){
			int resultCode = (int) resultCodeAVP.getInteger();
			if(ResultCode.fromCode(resultCode).vendorId == DiameterConstants.VENDOR_ELITECORE_ID){
				resultCodeAVP.setInteger(ResultCode.DIAMETER_UNABLE_TO_DELIVER.code);
			}
		}
		
		DiameterPeerCommunicator peerCommunicator = routerContext.getPeerCommunicator(destinationHost);
		
		if(peerCommunicator == null) {
			throw new CommunicationException(destinationHost + " not found");
		}
		
		peerCommunicator.sendAnswer(request, answer);
		
		routerContext.updateRealmOutputStatistics(answer, 
				routerContext.getPeerData(destinationHost).getRealmName(), 
				routeAction);
	}
	
}
