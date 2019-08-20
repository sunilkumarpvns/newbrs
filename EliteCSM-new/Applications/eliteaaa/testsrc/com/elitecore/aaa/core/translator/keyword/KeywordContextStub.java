package com.elitecore.aaa.core.translator.keyword;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.elitecore.aaa.diameter.translators.TranslationKeyword;
import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordEvaluator;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public class KeywordContextStub implements KeywordContext {
	
	private KeywordEvaluator evaluator;
	private Map<String, TranslationKeyword> requestKeywords;
	private Map<String, TranslationKeyword> responseKeywords;
	
	public KeywordContextStub() {
		requestKeywords = new HashMap<String, TranslationKeyword>();
		responseKeywords = new HashMap<String, TranslationKeyword>();
		evaluator = new KeywordEvaluator(requestKeywords, responseKeywords, new Random());
	}
	
	@Override
	public String getKeywordValue(String strkeyword, TranslatorParams params, boolean isRequest,
			ValueProvider valueProvider) {
		return evaluator.getKeywordValue(strkeyword, params, isRequest, valueProvider);
	}

	@Override
	public boolean isKeyword(String val) {
		return evaluator.isKeyword(val);
	}

	@Override
	public ILogger getLogger() {
		return LogManager.getLogger();
	}
	
	public KeywordContextStub addRequestKeyword(TranslationKeyword keyword) {
		requestKeywords.put(keyword.getName(), keyword);
		return this;
	}
	
	public KeywordContextStub addResponseKeyword(TranslationKeyword keyword) {
		responseKeywords.put(keyword.getName(), keyword);
		return this;
	}
}
