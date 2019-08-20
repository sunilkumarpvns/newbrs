package com.elitecore.exprlib.parser.expression.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionMac2Tgpp extends AbstractStringFunctionExpression {

	private static final long serialVersionUID = 1L;
	private final String MODULE = "MAC-TO-3GPP";
	private static final String IMEI="IMEI";
	private static final String IMEISV ="IMEISV";
	private static final String MSISDN ="MSISDN";
	private static final String IMSI = "IMSI";
	private static final String GENERAL ="GENERAL";
	private static HashMap<String, Mac2TgppRevision> mac2TgppRevisionMap;
	
	public FunctionMac2Tgpp(){
		mac2TgppRevisionMap = new HashMap<String, FunctionMac2Tgpp.Mac2TgppRevision>();
		mac2TgppRevisionMap.put(IMEISV,new MAC2IMEISV());
		mac2TgppRevisionMap.put(IMEI,new MAC2IMEI());
		mac2TgppRevisionMap.put(IMSI,new MAC2IMSI());
		mac2TgppRevisionMap.put(MSISDN,new MAC2MSISDN());
		mac2TgppRevisionMap.put(GENERAL,new MAC2GENERAL());
	}
	
	@Override
	public String getName() {
		return "mac2tgpp";
	}

	@Override
	public int getExpressionType() {
		return 0;
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException {

		if(argumentList == null || argumentList.size() < 4){
			throw new IllegalArgumentException("No. of Argument Provided for MAC to 3gpp Conversion is invalid.");
		}

		String revision = argumentList.get(0).getStringValue(valueProvider);
		if(revision != null && !(revision.equals(""))){
			Mac2TgppRevision macRevision = mac2TgppRevisionMap.get(revision);
			if(macRevision != null){
				return macRevision.handleMac2TgppConversion(argumentList, valueProvider);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Invalid revision : " + revision + " configured. so, default value/Attribute is returned.");
				try{
					String defaultValueForNoRevision = argumentList.get(2).getStringValue(valueProvider);
					if(defaultValueForNoRevision == null || defaultValueForNoRevision.trim().length() == 0){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Default value/Attribute is not provided for invalid revision : "+ revision +" configuration.");
						return null;
					}
					return defaultValueForNoRevision;
				}catch(Exception ex){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Default value/Attribute is null, for revision : " + revision);				
				}
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Revision value is not specify for Mac-to-3gpp conversion.");
			return null;
		}
		return null;
	}
	
	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, IllegalArgumentException,
			MissingIdentifierException {
		return 0;
	}
	
	
	public abstract class Mac2TgppRevision{

		protected String handleMac2TgppConversion(ArrayList<Expression> expressionList,ValueProvider valueProvider){

			String macAddress = getMacAddress(expressionList, valueProvider);
			if(macAddress == null || macAddress.trim().length() == 0){
				return getDefaulValue(expressionList, valueProvider);
			}else{	
				String convertedMacAddress = convertMac2tgpp(macAddress);
				macAddress = makeValidLengthString(convertedMacAddress);
				macAddress = generatePrefixString(macAddress,expressionList,valueProvider);
				if(expressionList.size() > 4){
					return makeStringAsPerConfiguredLength(macAddress,expressionList,valueProvider);
			}
				return macAddress;
		}
		}
		
		private String getMacAddress(ArrayList<Expression> expressionList,ValueProvider valueProvider){
			try {
				String macAddress = expressionList.get(1).getStringValue(valueProvider);
				return macAddress;
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "MAC Address is not provided. so Default value is returned.");
				return null;
			}
		}
		
		private String getDefaulValue(ArrayList<Expression> expressionList,ValueProvider valueProvider){
			try {
				String defaultValue = expressionList.get(2).getStringValue(valueProvider);
				return defaultValue;
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "MAC Address as well as Default value is null.");
				return null;
			}
		}

		protected String makeValidLengthString(String macAddress){
			return macAddress;
		}
		
		/**
		 * used for replacing the macAddress
		 * digit with Replacement (#-hash)
		 * character.
		 * 
		 * @param convertedMacAddress
		 * @param prefixValue
		 * @return MACAddress with replacement prefix
		 */
		
		protected String generatePrefixString(String convertedMacAddress ,ArrayList<Expression> expressionList,ValueProvider valueProvider){

			String prefixValue = null;
			try {
				prefixValue = expressionList.get(3).getStringValue(valueProvider);
				if(prefixValue == null || prefixValue.trim().length() == 0){
					return convertedMacAddress;
				}
			} catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "configured prefix : " + prefixValue + " is invalid.");
				}
				return convertedMacAddress;
			}

			int hascount=0;
			for(int i=0; i<prefixValue.length();i++){
				if(prefixValue.charAt(i) == '#')
					hascount++;
			}

			if(hascount > convertedMacAddress.length() || hascount == convertedMacAddress.length()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Invalid configured Prefix. Reason : prefix contains replacement(#) digit more than MAC Address length.");
				}
				return "";
			}

			String finalString ="";
			String hashString ="";
			
			if(prefixValue.startsWith("#")){
				hashString = prefixValue;
			}else{
				int indxHsh = prefixValue.indexOf("#");
				if(indxHsh != -1){
					finalString += prefixValue.subSequence(0,indxHsh);
					hashString = prefixValue.substring(indxHsh);
				}else{
					hashString = prefixValue;
				}
			}
			
			int matchIndex=0;
			for(matchIndex=0 ;matchIndex< convertedMacAddress.length();matchIndex++){
				if(hashString.length() != matchIndex){
					if(hashString.charAt(matchIndex) == '#' || hashString.charAt(matchIndex) == convertedMacAddress.charAt(matchIndex)){
						continue;
					}
					break;
				}else{
					break;
				}
			}
			
			finalString += convertedMacAddress.substring(matchIndex);
			return finalString;
		}
		
		/**
		 * Possible value for revision is 
		 * IMEISV,IMEI,MSISDN,IMSI,GENERAL
		 * except IMEI all have a same structure
		 * only length of Particular
		 * revision is changed. 
		 * @param macAddress
		 * @return
		 */

		protected String convertMac2tgpp(String macAddress){
			try{
				if(macAddress.startsWith("0x"))
					macAddress = macAddress.substring(2);
				macAddress = convertToPlainString(macAddress);
				BigInteger IMEISV = new BigInteger(macAddress, 16);
				String IMEISVString = IMEISV.toString();
				return IMEISVString;
			}catch(NumberFormatException ex){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Invalid MAC Address : "+macAddress+" formate.");
				return "";
			}
		}
		
		protected String convertToPlainString(String strValue){		
	    	String value = "";
	    	if(strValue == null)
	    		return "";    	    	
	    	
	    	for(int i=0;i< strValue.length() ; i++){
	    		
	    		if(isHexDigit(strValue.charAt(i))){
	    			value= value + strValue.charAt(i);    			
	    		}
	    	}
	    	return value.toLowerCase();
		}
		
		private boolean isHexDigit(int character){
			if(character >= '0' && character <= '9' ){
				return true;
			}else if(character >= 'A' && character <='F'){
				return true;
			}else if(character >= 'a' && character <='f'){
				return true;
			}
			return false;
		}
		
		protected String makeStringAsPerConfiguredLength(String macAddress,ArrayList<Expression> expressionList,ValueProvider valueProvider){
			
			String configuredLength = null;
			try {
				configuredLength = expressionList.get(4).getStringValue(valueProvider);
				
				if(configuredLength == null || configuredLength.trim().length() == 0 || configuredLength.equals("0")){
					return macAddress;
	}
			}catch(Exception e){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Invalid length argument is configured for expression, so generated MAC-Address: "+macAddress+" is returned.");
				return macAddress;
			}

			int configuredLengthValue = Integer.parseInt(configuredLength);
			int configurdLengthAbsValue = Math.abs(configuredLengthValue);
	
			if(configurdLengthAbsValue > macAddress.length()){
				int numOfDigitToAppend = configurdLengthAbsValue - macAddress.length();
				if(configuredLengthValue > 0){
					for(int i=0;i<numOfDigitToAppend;i++){
						macAddress = macAddress.concat("0");
					}
				}else{
					for(int i=0;i<numOfDigitToAppend;i++){
						macAddress = "0".concat(macAddress);
					}
				}
				return macAddress;
			}else{
				int lengthToTrim = macAddress.length() - configurdLengthAbsValue;
				if(configuredLengthValue > 0){
					macAddress = macAddress.substring(0,macAddress.length() - lengthToTrim);
				}else{
					macAddress = macAddress.substring(lengthToTrim);
				}
				return macAddress;
			}
		}
	}
	
	private class MAC2IMEISV extends Mac2TgppRevision{

		@Override
		protected String makeValidLengthString(String macAddress) {
		if(macAddress.length() > 16){
			macAddress = macAddress.substring(0, 16);
		}else{
			int len = 16 - macAddress.length();
			for(int i=0;i<len;i++){
				macAddress = "0" + macAddress;
			}
		}
		return macAddress;
		}
	}
	
	private class MAC2IMEI extends Mac2TgppRevision{

		@Override
		protected String convertMac2tgpp(String macAddress) {
			
			try{
				if(macAddress.startsWith("0x"))
					macAddress = macAddress.substring(2);
				String macaddress = convertToPlainString(macAddress);
				macaddress = "0F".concat(macaddress.substring(2));
				BigInteger IMEI = new BigInteger(macaddress, 16);
				String IMEIString = IMEI.toString();

				if(IMEIString.length() > 14){
					IMEIString = IMEIString.substring(0, 14);
				}else{
					int len = 14 - IMEIString.length();
					for(int i=0;i<len;i++){
						IMEIString = IMEIString.concat("0");
					}
				}
				return calculateCDBit(IMEIString);
			}catch(NumberFormatException ex){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid MAC Address :" + macAddress + " formate is configured.");
				return "";
			}
		}
			
		@Override
		protected String makeValidLengthString(String macAddress) {
			if(macAddress.length() > 15){
				macAddress = macAddress.substring(0, 15);
			}else {
				int len = 15 - macAddress.length();
				for(int i=0;i<len;i++){
					macAddress = "0" + macAddress;
				}
			}
			return macAddress;
		}
		
		private String calculateCDBit(String IMEIString){
				int totalsum=0;
				for(int i=0;i<IMEIString.length();i++){
					if(i%2==0){
						totalsum+= Integer.parseInt(String.valueOf(IMEIString.charAt(i)));
					}else{
						int oddPositionNum = Integer.parseInt(String.valueOf(IMEIString.charAt(i)));
						int multiplyOddByTwo = oddPositionNum *2;
					
						while(multiplyOddByTwo > 0){
							int oddSingleDigit = multiplyOddByTwo % 10;
							multiplyOddByTwo /= 10;
							totalsum += oddSingleDigit;
						}
					}
				}
				/**
				 * check digit(CD) calculation. digit-15.
				 */
				String checkDigit;
			int rem = totalsum % 10;
			if(rem == 0){
					checkDigit = "0";
				}else{
				checkDigit = String.valueOf(10-rem);
				}
				return IMEIString + checkDigit;
			}
		
		@Override
		protected String makeStringAsPerConfiguredLength(String macAddress,ArrayList<Expression> expressionList,ValueProvider valueProvider) {
			String strIMEI = super.makeStringAsPerConfiguredLength(macAddress, expressionList,valueProvider);
			return calculateCDBit(strIMEI.substring(0,strIMEI.length()-1));
				}
			}
	
	private class MAC2IMSI extends Mac2TgppRevision{

		@Override
		protected String makeValidLengthString(String macAddress) {
			if(macAddress.length() > 15){
				macAddress = macAddress.substring(0, 15);
			}else {
				int len = 15 - macAddress.length();
				for(int i=0;i<len;i++){
					macAddress = "0" + macAddress;
				}
			}			
			return macAddress;
		}
	}
	
	private class MAC2MSISDN extends Mac2TgppRevision{
			
	}
	
	private class MAC2GENERAL extends Mac2TgppRevision{

	}
}
