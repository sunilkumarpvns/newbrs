package com.elitecore.diameterapi.diameter.common.routerx.agent;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.routerx.RouterContext;
import com.elitecore.diameterapi.diameter.common.routerx.RoutingEntry;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;

/**
 * 
 * Similar to relays, proxy agents route Diameter messages using the
 * Diameter routing table. However, they differ since they modify
 * messages to implement policy enforcement(via translation mapping).
 *  							
 *  						2. Translate Request
 *  								+---+
 *  								|	+-->
 *      +------+    --------->     +-------+     --------->    +------+
 *      | Dia  |    1. Request     | Elite |     3. Request    | Dia  |
 *      | Peer |                   |  DPA  |                   | Peer |
 *      |      |    6. Answer      |       |     4. Answer     |      |
 *      +------+    <---------     +-------+     <---------    +------+
 *      						 <--+	|
 *      							+---+
 *      					5. Translate Answer
 *      
 *     example.net                example.net                example.com
 * 
 * 
 * @author monica.lulla
 *
 */
public class ProxyAgent extends RelayAgent {

	private static final String MODULE = "PROXY-AGNT";
	private ITranslationAgent translationAgent;

	public ProxyAgent(RouterContext routerContext, ITranslationAgent translationAgent, 
			IDiameterSessionManager diameterSessionManager) {
		super(routerContext, diameterSessionManager);
		this.translationAgent = translationAgent;
	}

	
	/**
	 *  Proxy Agent will perform packet translation additionally.
	 * @throws RoutingFailedException 
	 */
	@Override
	protected DiameterRequest buildRequest(DiameterSession diameterSession,
			DiameterRequest originRequest, RoutingEntry routingEntry) throws RoutingFailedException {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Building Diameter Request for Session-Id=" + 
			originRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
		}

		String translationName = routingEntry.getTranslationMapping();
		if (translationName != null) {
			return translateRequest(originRequest, diameterSession, translationName);
		} 
		return super.buildRequest(diameterSession, originRequest, routingEntry);

	}

	/**
	 * 
	 * 
	 * @param diameterRequest
	 * @param diameterSession
	 * @param translatorName is the Translation Mapping Name
	 * @return Translated Diameter Request
	 * @throws RoutingFailedException 
	 */
	protected DiameterRequest translateRequest(DiameterRequest diameterRequest, 
			DiameterSession diameterSession, String translatorName) throws RoutingFailedException {
		DiameterRequest translatedRequest = null;
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Translating packet before proxy using translation policy: " 
			+ translatorName);

		translatedRequest = new DiameterRequest();
		translatedRequest.setCommandCode(diameterRequest.getCommandCode());
		translatedRequest.setApplicationID(diameterRequest.getApplicationID());
		translatedRequest.setEnd_to_endIdentifier(diameterRequest.getEnd_to_endIdentifier());

		if (diameterRequest.isProxiable()){
			translatedRequest.setProxiableBit();
		}
		if (diameterRequest.isError()){
			translatedRequest.setErrorBit();
		}
		if (diameterRequest.isReTransmitted()){
			translatedRequest.setReTransmittedBit();
		}

		TranslatorParams translatorParams = new TranslatorParamsImpl(diameterRequest, translatedRequest);
		translatorParams.setParam(TranslatorConstants.DIAMETER_SESSION, diameterSession);
		try {
			translationAgent.translate(translatorName, translatorParams, TranslatorConstants.REQUEST_TRANSLATION);
			translatedRequest = (DiameterRequest) translatorParams.getParam(TranslatorConstants.TO_PACKET);
			translatedRequest.setHop_by_hopIdentifier(HopByHopPool.get());
		} catch (TranslationFailedException e) {
			throw new RoutingFailedException(RoutingActions.PROXY, 
					DiameterErrorMessageConstants.translationFailed(translatorName));
		}
		
		
		if((Boolean.parseBoolean(String.valueOf(translatorParams.getParam(TranslatorConstants.DUMMY_MAPPING))))){
			translatedRequest.setParameter(TranslatorConstants.DUMMY_MAPPING, Boolean.TRUE);
			translatedRequest.setParameter(TranslatorConstants.SELECTED_TRANSLATION_POLICY, translatorName);
		}
		diameterRequest.setParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING, 
				translatorParams.getParam(TranslatorConstants.SELECTED_REQUEST_MAPPING));
		return translatedRequest;
	}
	
	@Override
	protected DiameterAnswer buildAnswer(DiameterRequest originRequest, 
			DiameterAnswer diameterAnswer,
			RoutingEntry routingEntry,
			DiameterSession routingSession, 
			DiameterRequest translatedRequest) {
		
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Building Diameter Answer for Session-ID=" + 
			diameterAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID));
		String translationName = routingEntry.getTranslationMapping();
		if (translationName != null) {
			diameterAnswer = translateAnswer(translationName, diameterAnswer, routingSession, originRequest, translatedRequest);
		}
		return diameterAnswer;
	}
	
	/**
	 * 
	 * @param translatorName
	 * @param diameterAnswer
	 * @param session
	 * @param originRequest
	 * @param translatedRequest 
	 * @return Translated Answer
	 */
	protected DiameterAnswer translateAnswer(String translatorName, DiameterAnswer diameterAnswer, 
			DiameterSession session, DiameterRequest originRequest, DiameterRequest translatedRequest){

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Translating packet before routing Answer using translation policy: " 
			+ translatorName);
		DiameterAnswer translatedAnswer = new DiameterAnswer(originRequest);
		if (diameterAnswer.isProxiable()){
			translatedAnswer.setProxiableBit();
		}
		if (diameterAnswer.isError()){
			translatedAnswer.setErrorBit();
		}
		if (diameterAnswer.isReTransmitted()){
			translatedAnswer.setReTransmittedBit();
		}

		TranslatorParams translatorParam = new TranslatorParamsImpl(diameterAnswer, translatedAnswer, 
				originRequest, translatedRequest);
		translatorParam.setParam(TranslatorConstants.DIAMETER_SESSION, 
				session);
		translatorParam.setParam(TranslatorConstants.SELECTED_REQUEST_MAPPING, 
				originRequest.getParameter(TranslatorConstants.SELECTED_REQUEST_MAPPING));
		try {
			translationAgent.translate(translatorName, translatorParam, 
					TranslatorConstants.RESPONSE_TRANSLATION);
			diameterAnswer = (DiameterAnswer) translatorParam.getParam(TranslatorConstants.TO_PACKET);
		} catch (TranslationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error while translating Diameter Answer with HbH-ID=" + 
			diameterAnswer.getHop_by_hopIdentifier() + ". Passthrough DiameterAnswer without Traslation");
		}
		return diameterAnswer;
	}


}

