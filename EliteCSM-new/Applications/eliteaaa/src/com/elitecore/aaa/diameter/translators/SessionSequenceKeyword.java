package com.elitecore.aaa.diameter.translators;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public class SessionSequenceKeyword extends TranslationKeyword{

	private static final String MODULE = "SESSION-SEQ-KEYWORD";
	private String sessionIdKey;
	private IStackContext stackContext;
	private String policyName;
	
	public SessionSequenceKeyword(String name, String policyName, KeywordContext context,String sessionIdKey, IStackContext stackContext) {
		super(name,context);
		this.policyName = policyName;
		this.sessionIdKey = sessionIdKey;
		this.stackContext = stackContext;
	}
	
	public SessionSequenceKeyword(String name, String policyName, KeywordContext context,String sessionIdKey) {
		this(name, policyName, context, sessionIdKey, null);
	}

	@Override
	public String getKeywordValue(TranslatorParams params, String strKeyword,
			boolean isRequest, ValueProvider valueProvider) {
		IStackContext stackContext = this.stackContext;
		if (stackContext == null)
			stackContext = (IStackContext)params.getParam(TranslatorConstants.STACK_CONTEXT);

		if(stackContext==null){
			LogManager.getLogger().warn(MODULE, "Can't evaluate keyword :"+strKeyword+" Reason StackContext not found");
			return null;
		}
		if(params.getParam(TranslatorConstants.APPLICATION_ID) == null) {
			LogManager.getLogger().warn(MODULE, "Can't evaluate keyword :" + strKeyword + ", Reason: mapping for APPLICATION_ID is missing.");
			return null;
		}
		long appId = (Long) params.getParam(TranslatorConstants.APPLICATION_ID);
		String arg1 = getKeywordArgument(strKeyword);
		if(arg1!=null){
			if(context.isKeyword(arg1)){
				return getNextSessionSequence(context.getKeywordValue(arg1, params, isRequest, valueProvider), appId);
			}else {
				return getNextSessionSequence(valueProvider.getStringValue(arg1), appId);
			}
		}else {
			return getNextSessionSequence(valueProvider.getStringValue(sessionIdKey), appId);
		}

	}

	private String getNextSessionSequence(String sessionId, long appId) {
		if (stackContext.hasSession(sessionId, appId) == false) {
			return "0";
		}
		Session session = stackContext.getOrCreateSession(sessionId, appId);
		Long presentSequence = (Long) session.getParameter(policyName);
		if (presentSequence == null) {
			presentSequence = 0L;
		}
		session.setParameter(policyName, ++presentSequence);
		// diameter packet has nothing to do with this method. So initially null has passed.
		// this should be updated when session management and concurrency will be done.
 		session.update(null);   
		return String.valueOf(presentSequence);
	}
}
