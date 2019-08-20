package com.elitecore.diameterapi.diameter.common.routerx.failure;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.core.translator.policy.data.impl.TranslatorParamsImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterFailureConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.FailureActionResultCodes;

/**
 * It is an implementation of {@link RoutingFailureAction} , handling TRANSLATE Failure Action
 * 
 * @author monica.lulla
 *
 */
public class TranslateFailureAction implements RoutingFailureAction {
	
	private static final String MODULE = "TRANSLATE-FLR-ACT";
	private String transMappName;
	private List<String> warnings;
	private ITranslationAgent translationAgent;
	public TranslateFailureAction(String failureArgs, ITranslationAgent translationAgent){
		this.transMappName = failureArgs;
		this.warnings = new ArrayList<String>();
		this.translationAgent = translationAgent;
	}
	
	@Override
	public void init() {
		
		if(transMappName == null || transMappName.trim().length() == 0){
			warnings.add("No Translation Mapping found for " + DiameterFailureConstants.TRANSLATE + "  Failure Action");
			return;
		}
		
		transMappName = transMappName.trim();
		
		if(translationAgent.isExists(transMappName) == false){
			warnings.add("Translation Mapping: " + transMappName + " in Translate failure action is not registered");
		}
	}
	
	/**
	 * 
	 * this method performs Translate Failure Action for Failed Answer.
	 * 
	 */
	@Override
	public FailureActionResult process(DiameterAnswer failureAnswer, DiameterSession routingSession,
			DiameterRequest originRequest, DiameterRequest remoteRequest, String remotePeerHostIdentity,
			String originPeerName) {		
		String sessionId = failureAnswer.getAVPValue(DiameterAVPConstants.SESSION_ID);
		int hopByHopKey = failureAnswer.getHop_by_hopIdentifier();
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Performing " + DiameterFailureConstants.TRANSLATE + 
			" Failure Action with Failure Argument " + transMappName + 
			" for Session-ID="+sessionId + " HbH-ID=" + hopByHopKey);

		DiameterAnswer translatedAnswer = new DiameterAnswer(originRequest);

		if (failureAnswer.isProxiable()){
			translatedAnswer.setProxiableBit();
		}
		if (failureAnswer.isError()){
			translatedAnswer.setErrorBit();
		}
		if (failureAnswer.isReTransmitted()){
			translatedAnswer.setReTransmittedBit();
		}

		
		
		TranslatorParams translatorParam = new TranslatorParamsImpl(failureAnswer, translatedAnswer, 
				originRequest, remoteRequest);
		
		translatorParam.setParam(TranslatorConstants.DIAMETER_SESSION, routingSession); 
		
		try {
			translationAgent.translate(transMappName, translatorParam, TranslatorConstants.RESPONSE_TRANSLATION);
			failureAnswer = (DiameterAnswer) translatorParam.getParam(TranslatorConstants.TO_PACKET);
		} catch (TranslationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error while traslating diameterResponse with HbH-ID=" 
			+ hopByHopKey + ". Sending Diameter Answer without Traslation");
			LogManager.getLogger().trace(MODULE, e);
		}
		return new FailureActionResult(FailureActionResultCodes.SEND_ANSWER_TO_ORIGINATOR, failureAnswer);
		
	}

	@Override
	public List<String> getWarnings() {
		return warnings;
	}
}
