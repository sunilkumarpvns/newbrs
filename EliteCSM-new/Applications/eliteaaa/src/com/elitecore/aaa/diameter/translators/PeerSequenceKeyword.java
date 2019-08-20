package com.elitecore.aaa.diameter.translators;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class PeerSequenceKeyword extends TranslationKeyword{

	private static final String MODULE = "PEER-SEQ-KEYWORD";
	private IStackContext stackContext;
	public PeerSequenceKeyword(String name,KeywordContext keywordContext, IStackContext stackContext) {
		super(name,keywordContext);
		this.stackContext = stackContext;
	}
	
	public PeerSequenceKeyword(String name,KeywordContext keywordContext) {
		this(name, keywordContext, null);
	}

	@Override
	public String getKeywordValue(TranslatorParams params, String strKeyword,
			boolean isRequest, ValueProvider valueProvider) {
		
		IStackContext stackContext = this.stackContext;
		if (stackContext == null)
			stackContext = (IStackContext)params.getParam(TranslatorConstants.STACK_CONTEXT);
		
		if(stackContext!=null){
			String peerSeq = String.valueOf(stackContext.getNextPeerSequence(valueProvider.getStringValue(DiameterAVPConstants.ORIGIN_HOST)));
			return peerSeq;
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Can't evaluate keyword :"+strKeyword+" Reason StackContext not found");
			return null;	
		}
	}

}
