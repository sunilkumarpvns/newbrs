package com.elitecore.aaa.radius.translators.copypacket.parser;

import java.util.regex.Pattern;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.radius.translators.copypacket.RadiusHeaderFields;
import com.elitecore.aaa.radius.translators.copypacket.keyword.RadiusPacketKeywordValueProvider;
import com.elitecore.aaa.radius.translators.copypacket.keyword.RadiusServiceRequestKeywordValueProvider;
import com.elitecore.aaa.radius.translators.copypacket.keyword.RadiusSrcResKeyworValueProvider;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.diameterapi.diameter.translator.keyword.KeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.operations.data.HeaderFields;
import com.elitecore.diameterapi.diameter.translator.parser.CopyPacketParser;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

public abstract class RadiusCopyPacketParser extends CopyPacketParser<RadiusPacket> {

	private static RadiusCopyPacketParser radiusRequestCopyPacketParser;
	private static RadiusCopyPacketParser radiusResponseCopyPacketParser;
	private static String keywordPattern = "(\\$\\{(SRCREQ|DSTREQ|SRCRES)\\})(:(\\d+[:\\d+]*[\\.]?[a-zA-Z]*))";
	
	private RadiusCopyPacketParser(Pattern keywordPattern) {
		super(keywordPattern);
	}
	
	public static RadiusCopyPacketParser getRequestInstance(){
		if (radiusRequestCopyPacketParser == null) {
			radiusRequestCopyPacketParser = new RequestCopyPacketParser(Pattern.compile(keywordPattern));
		}
		return radiusRequestCopyPacketParser;
	}
	
	public static RadiusCopyPacketParser getResponseInstance(){
		if (radiusResponseCopyPacketParser == null) {
			radiusResponseCopyPacketParser = new ResponseCopyPacketParser(Pattern.compile(keywordPattern));
		}
		return radiusResponseCopyPacketParser;
	}

	@Override
	public HeaderFields<RadiusPacket> getHeaderField(String name) {
		return RadiusHeaderFields.getHeaderField(name);
	}
	
	private static class RequestCopyPacketParser extends RadiusCopyPacketParser {

		public RequestCopyPacketParser(Pattern pattern) {
			super(pattern);
		}

		@Override
		protected KeywordValueProvider createKeywordValueProvider(
				String keywordType, String argument) throws InvalidExpressionException {
			
			if (AAATranslatorConstants.SRCRES.equalsIgnoreCase(keywordType)) {
				return new RadiusSrcResKeyworValueProvider(argument);
			}
			
			throw new InvalidExpressionException("Keyword: " + keywordType + 
					" not supported for Request-Translation");
		}
	}
	
	private static class ResponseCopyPacketParser extends RadiusCopyPacketParser {
		
		public ResponseCopyPacketParser(Pattern pattern) {
			super(pattern);
		}

		@Override
		protected KeywordValueProvider createKeywordValueProvider(
				String keywordType, String argument) throws InvalidExpressionException {

			if (AAATranslatorConstants.SOURCE_REQUEST.equalsIgnoreCase(keywordType)){
				return new RadiusServiceRequestKeywordValueProvider(argument, AAATranslatorConstants.SOURCE_REQUEST);
			}  
			if (AAATranslatorConstants.DESTINATION_REQUEST.equalsIgnoreCase(keywordType)) {
				return new RadiusPacketKeywordValueProvider(argument, AAATranslatorConstants.DESTINATION_REQUEST);
			} 
			if (AAATranslatorConstants.SRCRES.equalsIgnoreCase(keywordType)) {
				return new RadiusSrcResKeyworValueProvider(argument);
			}
			throw new InvalidExpressionException("Keyword: " + keywordType + 
					" not supported for Response-Translation");
		}
	}

}
