package com.elitecore.diameterapi.diameter.translator.parser;

import java.util.regex.Pattern;

import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.translator.keyword.DBSessionRequestKeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.keyword.DBSessionResponseKeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.keyword.DiameterKeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.keyword.KeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.operations.data.DiameterHeaderFields;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

public abstract class DiameterCopyPacketParser extends CopyPacketParser<DiameterPacket> {
	/*
	 * For Pattern,
	 * (A(B)C)(D(E))
	 * 
	 * Group 0 -> ABCDE
	 * Group 1 -> ABC
	 * Group 2 -> B
	 * Group 3 -> DE
	 * Group 4 -> E
	 * 
	 */
	private static final String keywordPattern = "(\\$\\{(SRCREQ|SRCRES|DSTREQ|DBSESSION)\\})(:(\\d+:\\d+[\\.\\d+:\\d+]*|[\\w]+))";
	
	public DiameterCopyPacketParser(Pattern pattern) {
		super(pattern);
	}
	
	@Override
	public DiameterHeaderFields getHeaderField(String name) {
		return DiameterHeaderFields.getHeaderField(name);
	}
	
	private static RequestCopyPacketParser diameterRequestCopyPacketParser;
	private static ResponseCopyPacketParser diameterResponseCopyPacketParser;
	
	public static DiameterCopyPacketParser getRequestInstance() {
		if(diameterRequestCopyPacketParser == null){
			diameterRequestCopyPacketParser = new RequestCopyPacketParser(Pattern.compile(keywordPattern));
		}
		return diameterRequestCopyPacketParser;
	}
	
	public static DiameterCopyPacketParser getResponseInstance() {
		if(diameterResponseCopyPacketParser == null){
			diameterResponseCopyPacketParser = new ResponseCopyPacketParser(Pattern.compile(keywordPattern));
		}
		return diameterResponseCopyPacketParser;
	}

	private static class RequestCopyPacketParser extends DiameterCopyPacketParser {

		public RequestCopyPacketParser(Pattern pattern) {
			super(pattern);
		}

		@Override
		protected KeywordValueProvider createKeywordValueProvider(
				String keywordType, String argument) throws InvalidExpressionException {

			if (TranslatorConstants.DBSESSION.equals(keywordType)) {
				return new DBSessionRequestKeywordValueProvider(argument);
			}
			
			if (TranslatorConstants.SRCRES.equals(keywordType)) {
				return new DiameterKeywordValueProvider(keywordType, argument);
			}
			
			throw new InvalidExpressionException("Keyword: " + keywordType + 
					" not supported for Request-Translation");
		}
	}
	
	private static class ResponseCopyPacketParser extends DiameterCopyPacketParser {
		
		public ResponseCopyPacketParser(Pattern pattern) {
			super(pattern);
		}

		@Override
		protected KeywordValueProvider createKeywordValueProvider(
				String keywordType, String argument) throws InvalidExpressionException {

			if (TranslatorConstants.SOURCE_REQUEST.equals(keywordType)
					|| TranslatorConstants.DESTINATION_REQUEST.equals(keywordType)) {
				return new DiameterKeywordValueProvider(keywordType, argument);
			}
			if (TranslatorConstants.DBSESSION.equals(keywordType)) {
				return new DBSessionResponseKeywordValueProvider(argument);
			} 
			throw new InvalidExpressionException("Keyword: " + keywordType + 
					" not supported for Response-Translation");
		}
	}
}
