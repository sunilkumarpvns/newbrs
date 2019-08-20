package com.elitecore.aaa.diameter.translators;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public class ServerSequenceKeyword extends TranslationKeyword{

	private static final String MODULE = "GLOBAL-SEQ-KEYWORD";
	private IStackContext stackContext;
	
	public ServerSequenceKeyword(String name,KeywordContext context, IStackContext stackContext) {
		super(name, context);
		this.stackContext = stackContext;
	}

	public ServerSequenceKeyword(String name,KeywordContext context) {
		this(name, context, null);
	}
	
	@Override
	public String getKeywordValue(TranslatorParams params, String strKeyword,
			boolean isRequest, ValueProvider valueProvider) {
		IStackContext stackContext = this.stackContext;
		if (stackContext == null)
			stackContext = (IStackContext)params.getParam(TranslatorConstants.STACK_CONTEXT);
		
		if(stackContext!=null){
			String serverSeq = String.valueOf(stackContext.getNextServerSequence());
			return serverSeq;
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Can't evaluate keyword :"+strKeyword+" Reason StackContext not found");
			return null;	
		}
		
	}

}
