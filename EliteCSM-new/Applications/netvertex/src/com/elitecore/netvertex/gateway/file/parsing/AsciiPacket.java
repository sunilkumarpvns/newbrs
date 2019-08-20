package com.elitecore.netvertex.gateway.file.parsing;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.netvertex.core.constant.OfflineConstants;
import com.elitecore.netvertex.core.constant.OfflineRnCConstants;
import com.elitecore.netvertex.gateway.file.FileGatewayConfiguration;
import com.elitecore.netvertex.gateway.file.FileGatewayConstants;
import com.elitecore.netvertex.gateway.file.util.InetAddressUtil;
import com.elitecore.netvertex.gateway.file.util.Utilities;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCEvent;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;

/**
 * The Class AsciiPacket.
 */
public class AsciiPacket {

	private static final String MODULE = "ASCII-PACKET";
	private Map<String, String> fieldValueMap;
	private List<String> headerList;
	private String sourceUnitName;
	private AsciiParsingUnitInfo parsingUnitInfo;
	private boolean isValidPacket = true;
	protected boolean isMalformPacket;
	private byte[] rawData;
	private static final String DEFAULT_KEY = "DEFAULT_";
	private FileGatewayConfiguration fileGatewayConfiguration;

	/**
	 * Instantiates a new ascii packet.
	 *
	 * @param parsingUnitInfo the parsing unit info
	 */
	@SuppressWarnings("unchecked")
	public AsciiPacket(AsciiParsingUnitInfo parsingUnitInfo, FileGatewayConfiguration asciiParserProperties) {
		fileGatewayConfiguration = asciiParserProperties;
		fieldValueMap = new HashMap<>();
		headerList = (List<String>)parsingUnitInfo.getObjectValue(AsciiParsingUnitInfo.HEADER_LIST);
		sourceUnitName = (String)parsingUnitInfo.getObjectValue(AsciiParsingUnitInfo.FILE_NAME);
		this.parsingUnitInfo = parsingUnitInfo;
		
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.packet.IPacket#readFrom(java.io.InputStream)
	 */
	public int readFrom(InputStream sourceStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sourceStream,Charset.defaultCharset()));
		return readFrom(bufferedReader);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.packet.IPacket#readFrom(java.io.Reader)
	 */
	public int readFrom(Reader sourceReader) throws IOException {
		String line = ((BufferedReader)sourceReader).readLine();
		if(line == null) {
			return -1;
		}
		if(line.trim().length() == 0){
			isValidPacket = false;
		}
		boolean bKeyValueRecord = fileGatewayConfiguration.isKeyValueRecord();
		boolean bFooter = fileGatewayConfiguration.isFooterPresent();
		String fileFooterContains = fileGatewayConfiguration.getFileFooterContains();
		boolean bHeader = fileGatewayConfiguration.isHeaderPresent();
		boolean bRecordHeader = fileGatewayConfiguration.isRecordHeaderPresent();
		String fieldSeparator = fileGatewayConfiguration.getFieldSeparator();
		String[] findAndReplace = fileGatewayConfiguration.getFindAndReplace();

		if(bFooter && fileFooterContains != null && !"".equals(fileFooterContains) && line.contains(fileFooterContains))
			return -1;

		if((bFooter && line.startsWith("<VerazCDRFooter")) || (bFooter && line.startsWith("#CHECKSUM"))){
			return -1;
		}

		if(line.startsWith("<")){
			line = line.substring(line.indexOf('>')+1);
		}

		if(bRecordHeader) {
			String recordHeaderSeparator = fileGatewayConfiguration.getRecordHeaderSeparator();
			int length = fileGatewayConfiguration.getRecordHeaderLen();
			if(recordHeaderSeparator!=null && !"".equals(recordHeaderSeparator)) {
				LogManager.getLogger().trace(MODULE, "Record Header Separator: "+recordHeaderSeparator);
				line = line.split(recordHeaderSeparator)[1].trim();
			}else if(length > 0){
				line = line.substring(length);
			}
		}

		try{
			if(bHeader || bRecordHeader) {
				Pattern pat = fileGatewayConfiguration.getRegexPattern();
				String fieldSeperator = fileGatewayConfiguration.getFieldSeparator();

				/** Change has been provided for - ",EB1,AGG1,02052012150000,490.00,180,FUTURE,FnSys,BUY,TX0000000000003" */
				if(line.startsWith(fieldSeperator)){
					String newValue = " " + fieldSeparator;
					line = line.replaceFirst(fieldSeparator, newValue);
				}
				/** Change has been provided for String having "(double quotes) as value.MED-3535- cb58045e,,"Mr.Krishnamoorthi;Adanuarpost;Villupuramtaluk;Villupuramdistrict,IPv4 */
				if(line.contains("\"")){		
					line = line.replace("\"", "");
				}

				Matcher mat = pat.matcher(line);
				int index = 0;
				if(headerList != null){
					while(mat.find() && headerList.size() >= index) {
						String splitString = mat.group(1);
						if(splitString == null) {
							fieldValueMap.put(headerList.get(index), "");
						} else {
							if(splitString.contains(",")) {
								splitString = splitString.replace(",", "|");
							}
							fieldValueMap.put(headerList.get(index), splitString.trim());
						}
						index++;
					}
				} else {
					while(mat.find()) {
						String splitString = mat.group(1);
						if(splitString == null) {
							fieldValueMap.put(String.valueOf(index+1), "");
						}else if(splitString != null && splitString.contains("=")){ // Start of: https://jira.crestel.in/browse/MEDSUP-253
							String[] arrKeyValue = splitString.split("=",splitString.length());
							if(arrKeyValue != null){
								if(arrKeyValue.length > 1){
									fieldValueMap.put(arrKeyValue[0], arrKeyValue[1]);
								}else{
									fieldValueMap.put(String.valueOf(index+1), arrKeyValue[0]);
								}
							}
						}else {
							if(splitString.contains(",")) {
								splitString = splitString.replace(",", "|");
							}
							fieldValueMap.put(String.valueOf(index+1), splitString.trim());
						}
						index++;
					}
				}
			}else if(bKeyValueRecord){
				if(findAndReplace != null && findAndReplace.length > 1) {
					String strFindWhat = findAndReplace[0];
					String strReplaceWith = findAndReplace[1];
					if(strFindWhat != null && strFindWhat.length() > 0 && strReplaceWith != null && strReplaceWith.length() > 0) {
						line = line.replaceAll(strFindWhat, strReplaceWith);
					}
				}
				String keyValueSeparator = fileGatewayConfiguration.getKeyValueSeparator();
				StringTokenizer stringTokenizer = new StringTokenizer(line, fieldSeparator);
				int i = 1;
				while(stringTokenizer.hasMoreTokens()) {
					String field = stringTokenizer.nextToken();
					if(field != null){
						String[] keyValuePair;
						if(field.contains(keyValueSeparator)){
							keyValuePair = field.split(keyValueSeparator, -1);
						}else {
							keyValuePair = new String[2];
							keyValuePair[0] = DEFAULT_KEY + i++;
							keyValuePair[1] = field;
						}
						if(keyValuePair != null) {
							if(keyValuePair.length == 2){
								fieldValueMap.put(keyValuePair[0], keyValuePair[1]);
							}else if(keyValuePair.length==1){
								fieldValueMap.put(keyValuePair[0], "");
							}
						}
					}
				}
			}else{
				if(findAndReplace != null && findAndReplace.length > 1) {
					String strFindWhat = findAndReplace[0];
					String strReplaceWith = findAndReplace[1];
					if(strFindWhat != null && strFindWhat.length() > 0 && strReplaceWith != null && strReplaceWith.length() > 0) {
						line = line.replaceAll(strFindWhat, strReplaceWith);
					}
				}
				Pattern pat = fileGatewayConfiguration.getRegexPattern();
				Matcher mat = pat.matcher(line);
				int index = 0;
				while(mat.find()) {
					String splitString = mat.group(1);
					if(splitString == null) {
						fieldValueMap.put(String.valueOf(index+1), "");
					} else {
						if(splitString.contains(",")) {
							splitString = splitString.replace(",", "|");
						}
						fieldValueMap.put(String.valueOf(index+1), splitString.trim());
					}
					index++;
				}
			}
		}catch(Exception e){
			LogManager.getLogger().warn(MODULE, "Error occured while reading the record, reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			isValidPacket = false;
			line = line.concat(OfflineRnCConstants.NEW_LINE_SEPERATOR);
		}
		rawData = line.getBytes();
		return 1;
	}

	@Override
	public String toString() {
		StringBuilder stringBuffer = new StringBuilder();
		Iterator<String> iterator = fieldValueMap.keySet().iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			stringBuffer.append(key+"="+fieldValueMap.get(key)+"\n");
		}
		return stringBuffer.toString();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.packet.parsers.IMediationPacket#getUnifiedCSVPacket(java.text.SimpleDateFormat, java.text.SimpleDateFormat)
	 */
	public RnCRequest toRnCRequest(SimpleDateFormat sourceDateFormat, SimpleDateFormat mediationDateFormat) {
//		Set<String> headerSourceKeyAttributes = pluginConfiguration.getHeaderSourceKeyAttributes();
		RnCRequest rncRequest = null;
		List<FileMapping> mappings = fileGatewayConfiguration.getFileMapping();
		if(mappings != null && !mappings.isEmpty()){
			Iterator<FileMapping> iterator = mappings.iterator();
			rncRequest = new RnCRequest();
			rncRequest.setEventType(OfflineRnCEvent.CDR);
			rncRequest.setOriginalPacket(this);
			String dateFormat = fileGatewayConfiguration.getSourceDateFormat();
			Set<String> srcKeys=new HashSet<>(fieldValueMap.keySet());
			
			while(iterator.hasNext()) {
				FileMapping fileMapping = iterator.next();
				String csvFields = fileMapping.getSourceKey();
				String unifiedFieldName = fileMapping.getDestinationKey();
				String value = ""; 
				String configuredDefaultValue = fileMapping.getDefaultValue();
				String trimCharacterString = "";
				String trimCharacterPosition = "";
				String sourceFieldDateFormat = "";

				//				String trimCharacterString = (String)entry.get(AsciiParserPluginConfiguration.TRIM_CHARS);
				//				String trimCharacterPosition = (String)entry.get(AsciiParserPluginConfiguration.TRIM_POSITION);
				//				String splitIpPort = (String)entry.get(AsciiParserPluginConfiguration.SPLIT_IP_PORT);
				//				String seperator = (String)entry.get(AsciiParserPluginConfiguration.SEPERATOR);
				//				String sourceFieldDateFormat = (String)entry.get(AsciiParserPluginConfiguration.SOURCE_FIELD_FORMAT);
				//				Object isExclusiveOperator = entry.get(AsciiParserPluginConfiguration.EXCLUSIVE_SELECTIVE_OPERATOR);

				if(csvFields != null) {
					//for(int i=0;i<csvFields.length;i++) {
						if("${SourceUnitName}".equals(csvFields) || csvFields.equalsIgnoreCase(FileGatewayConstants.SOURCE_UNIT_NAME) ||
								csvFields.equalsIgnoreCase(FileGatewayConstants.UNIT_NAME)){
							value += sourceUnitName + "|";
						}else if(csvFields.equalsIgnoreCase(FileGatewayConstants.RECORDINDEX)){
							value += (parsingUnitInfo).getTotalRecords();
						}else if(csvFields.startsWith("\"") && csvFields.endsWith("\"")) {
							value += csvFields.substring(1,csvFields.length()-1)+"|";
						}else{
							if(csvFields.startsWith("\\\"") && csvFields.endsWith("\\\"")){
								csvFields = csvFields.replace("\\","");
							}
							if(fieldValueMap.get(csvFields) != null) {
								srcKeys.remove(csvFields);
								String tempValue =  fieldValueMap.get(csvFields);
								if(trimCharacterString != null && trimCharacterString.length()!=0){
									String[] trimCharacters = trimCharacterString.split(",");
									tempValue = getCharactersTrim(tempValue,trimCharacters,trimCharacterPosition);
								}
								if(tempValue != null && !"".equals(tempValue)){
									value += tempValue+"|";
								}
							}
						}
						/*if(isExclusiveOperator != null && value.length() > 0)
							break;*/
				//	}
					if(value.endsWith("|")) {
						value = value.substring(0, value.length()-1);
					}
					if(value.trim().length()==0){
						value = configuredDefaultValue;
					}

					/*	if((splitIpPort != null && seperator != null)
							&& (value.trim().length()!=0 && value.contains(seperator))) {
								try {
									unifiedFieldMethod.get(j++).invoke(rncRequest, value.substring(value.indexOf(seperator)+1,value.length()));
								} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
									LogManager.getLogger().error(MODULE, "Error occured while setting value , reason :"+e.getMessage());
									LogManager.getLogger().trace(MODULE, e);
								}
								value = value.substring(0,value.indexOf(seperator));
						}*/

				}else{
					value = configuredDefaultValue;
				}

				if(unifiedFieldName.equalsIgnoreCase(OfflineRnCConstants.START_DATE) ||
						unifiedFieldName.equalsIgnoreCase(OfflineRnCConstants.END_DATE) ||
						unifiedFieldName.equalsIgnoreCase(OfflineRnCConstants.INGRESS_START_DATE) ||
						unifiedFieldName.equalsIgnoreCase(OfflineRnCConstants.INGRESS_END_DATE) ||
						unifiedFieldName.equalsIgnoreCase(OfflineRnCConstants.EGRESS_START_DATE) ||
						unifiedFieldName.equalsIgnoreCase(OfflineRnCConstants.EGRESS_END_DATE)) {

					if(value!=null && !"".equals(value)){

						boolean isOverrideDateFormat = false;
						if(sourceFieldDateFormat != null && !"".equals(sourceFieldDateFormat)){
							isOverrideDateFormat = true;
						}
						//check if attribute wise date format is available then override dateFormat with sourceFieldDateFormat.
						if((!isOverrideDateFormat && dateFormat.equalsIgnoreCase(OfflineRnCConstants.UNIX_EPOCH_TIME))
								|| (isOverrideDateFormat && sourceFieldDateFormat.equalsIgnoreCase(OfflineRnCConstants.UNIX_EPOCH_TIME))){
							long timeStamp = 0;
							try{
								timeStamp = (long)(Double.parseDouble(value)*1000);
							}catch(NumberFormatException numberFormatException){
								LogManager.getLogger().trace(MODULE, numberFormatException);
							}
							Date javaDate = new Date(timeStamp);
							value = mediationDateFormat.format(javaDate);
						}else if((!isOverrideDateFormat && dateFormat.equalsIgnoreCase(OfflineRnCConstants.LONG))
								|| (isOverrideDateFormat && sourceFieldDateFormat.equalsIgnoreCase(OfflineRnCConstants.LONG))) {
							long timeStamp = 0;
							try{
								timeStamp = (long)Double.parseDouble(value);
							}catch(NumberFormatException numberFormatException){
								LogManager.getLogger().trace(MODULE, numberFormatException);
							}
							Date javaDate = new Date(timeStamp);
							value = mediationDateFormat.format(javaDate);
						}else{
							try{
								if(isOverrideDateFormat){
									SimpleDateFormat fieldDateFormat = new SimpleDateFormat(sourceFieldDateFormat);
									value =  mediationDateFormat.format(fieldDateFormat.parse(value));
								}else{
									value =  mediationDateFormat.format(sourceDateFormat.parse(value));
								}

							}catch(ParseException parseException){
								LogManager.getLogger().trace(MODULE, "Error occurred while parsing the date "+value +" ,reason:"+parseException.getMessage());
								LogManager.getLogger().trace(MODULE, parseException);
							}
						}
					}
				} else if (OfflineConstants.IPV6_DATA_TYPE.equalsIgnoreCase(sourceFieldDateFormat)
						&& Utilities.isIPv6(value)) {
					value = InetAddressUtil.bytesToIPV6UniversalFormat(value);
				} else if (OfflineConstants.IPV6_TO_IPV4
						.equalsIgnoreCase(sourceFieldDateFormat) && Utilities.isIPv6(value)) {//Added for Vodafone CGNAT Requirement
					value = InetAddressUtil.convertIPv6ToIPv4(value);
				}
				// Added by Jigish
				if(("duration".equalsIgnoreCase(unifiedFieldName))
						&& (value != null && value.length() > 0))	{
					try{
						double tempDuration = java.lang.Double.parseDouble(value);
						int finalDuration;
						finalDuration = (int)Math.ceil(tempDuration);
						value = String.valueOf(finalDuration);
					}catch (Exception e) {
						value = "";
						LogManager.getLogger().warn(MODULE,"Duration is found : " + value + ", which is in improper format.");
						LogManager.getLogger().trace(MODULE, e);
					}
				}

				if (Strings.isNullOrBlank(fileMapping.getValueMapping()) == false) {
					rncRequest.setAttribute(unifiedFieldName, fileMapping.getValue(value));
				} else {
					rncRequest.setAttribute(unifiedFieldName, value);
				}
				
				//unifiedFieldMethod.get(j++).invoke(rncRequest, new Object[]{value});

			}
			
			if (fileGatewayConfiguration.isPreservedSourcedField()) {
				for (String key : srcKeys) {
					rncRequest.setAttribute(key, fieldValueMap.get(key));
					//rncRequest.setSrcKeysMappedAttributes(srcKeysMappedAttributes);
				}
			}
			
//			String duration = rncRequest.getDuration();

			/* FOR STARTDATE,ENDDATE AND DURATION AUTO CORRECTION */
//			String startDate = rncRequest.getStartDate();
//			String endDate = rncRequest.getEndDate();
//			if((startDate != null && startDate.length() > 0) || (endDate != null && endDate.length() > 0))
//				setDateOrDuration(rncRequest,startDate,endDate,duration,1,mediationDateFormat);

			/* FOR INGRESSSTARTDATE,INGRESSENDDATE,EGRESSSTARTDATE,EGRESSENDDATE AND DURATION AUTO CORRECTION */
//			String ingressStartDate = rncRequest.getIngressStartDate();
//			String ingressEndDate = rncRequest.getIngressEndDate();
//			String egressStartDate = rncRequest.getEgressStartDate();
//			String egressEndDate = rncRequest.getEgressEndDate();
//			setDateOrDuration(rncRequest,ingressStartDate,ingressEndDate,egressStartDate,egressEndDate,duration,mediationDateFormat);
			rncRequest.setStatusMessage("Parsed Successfully.");
		}
		return rncRequest;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.packet.parsers.IMediationPacket#isValidPacket()
	 */
	public boolean isValidPacket() {
		return isValidPacket;
	}

	public byte[] getRawData() {
		return rawData;
	}
	
	/**
	 * Sets the date or duration.
	 *
	 * @param unifiedCSVPacket the unified csv packet
	 * @param ingressStartDate the ingress start date
	 * @param ingressEndDate the ingress end date
	 * @param egressStartDate the egress start date
	 * @param egressEndDate the egress end date
	 * @param strDuration the str duration
	 * @param mediationDateFormat the mediation date format
	 */
	/*protected void setDateOrDuration(UnifiedCSVPacket unifiedCSVPacket, String ingressStartDate, String ingressEndDate,
			String egressStartDate, String egressEndDate, String strDuration, SimpleDateFormat mediationDateFormat) {
		if(unifiedCSVPacket != null){
			if(strDuration == null || strDuration.length() <= 0){
				if((ingressStartDate != null && ingressStartDate.length() > 0)
						&& (ingressEndDate != null && ingressEndDate.length() > 0)){
					setDateOrDuration(unifiedCSVPacket,ingressStartDate,ingressEndDate,strDuration,2,mediationDateFormat);
				}else if((egressStartDate != null && egressStartDate.length() > 0)
						&& (egressEndDate != null && egressEndDate.length() > 0)){
					setDateOrDuration(unifiedCSVPacket, egressStartDate, egressEndDate, strDuration,3,mediationDateFormat);
				}
			}
			strDuration = unifiedCSVPacket.getDuration();
			if(strDuration != null && strDuration.length() > 0){
				setDateOrDuration(unifiedCSVPacket,ingressStartDate,ingressEndDate,strDuration,2,mediationDateFormat);
				setDateOrDuration(unifiedCSVPacket,egressStartDate,egressEndDate,strDuration,3,mediationDateFormat);
			}
		}else{
			LogManager.getLogger().warn(MODULE,"UnifiedPacket received null on auto-cortection of dates and duration.");
		}
	}

	*/
	/**
	 * Sets the date or duration.
	 *
	 * @param unifiedCSVPacket the unified csv packet
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param strDuration the str duration
	 * @param dateType the date type
	 * @param simpleDateFormat the simple date format
	 */
	/*
	protected void setDateOrDuration(UnifiedCSVPacket unifiedCSVPacket, String startDate, String endDate,
			String strDuration, int dateType, SimpleDateFormat simpleDateFormat) {
		if(unifiedCSVPacket != null){
			if((startDate!=null && startDate.length() > 0) && (strDuration != null && strDuration.length() > 0)
					&& (endDate != null && endDate.length() > 0)){
				return;
			}else if((startDate==null || startDate.length()<= 0) && (strDuration == null || strDuration.length()<= 0)
					&& (endDate == null || endDate.length() <= 0)){
				return;
			}

			if((startDate!=null && startDate.length() > 0) && (strDuration != null && strDuration.length() > 0)
					&& (endDate == null || "".equals(endDate))){
				// Calculating the end date
				try{
					Date date = simpleDateFormat.parse(startDate);
					long endTime = date.getTime()+ (long)(Math.ceil(Double.parseDouble(strDuration)))*1000;
					if(dateType == 1)
						unifiedCSVPacket.setEndDate(simpleDateFormat.format(new Date(endTime)));
					if(dateType == 2)
						unifiedCSVPacket.setIngressEndDate(simpleDateFormat.format(new Date(endTime)));
					if(dateType == 3)
						unifiedCSVPacket.setEgressEndDate(simpleDateFormat.format(new Date(endTime)));

					unifiedCSVPacket.setDuration(String.valueOf((long)Math.ceil(Double.parseDouble(strDuration))));
					unifiedCSVPacket.setAutoCorrectedCDR("Y");
				}catch (ParseException parseException) {
					if(dateType == 1)
						unifiedCSVPacket.setEndDate("00/00/00 00:00:00");
					if(dateType == 2)
						unifiedCSVPacket.setIngressEndDate("00/00/00 00:00:00");
					if(dateType == 3)
						unifiedCSVPacket.setEgressEndDate("00/00/00 00:00:00");
					LogManager.getLogger().trace(MODULE, parseException);
				}catch (NumberFormatException numberFormatException){
					if(dateType == 1)
						unifiedCSVPacket.setEndDate("00/00/00 00:00:00");
					if(dateType == 2)
						unifiedCSVPacket.setIngressEndDate("00/00/00 00:00:00");
					if(dateType == 3)
						unifiedCSVPacket.setEgressEndDate("00/00/00 00:00:00");

					LogManager.getLogger().trace(MODULE, numberFormatException);
				}
			}else if((endDate!=null && endDate.length()!=0) && (strDuration != null && strDuration.length() > 0)
					&& (startDate == null || "".equals(startDate))){
				try{
					Date date = simpleDateFormat.parse(endDate);
					long startTime = date.getTime()- (long)(Math.ceil(Double.parseDouble(strDuration)))*1000;
					if(dateType == 1)
						unifiedCSVPacket.setStartDate(simpleDateFormat.format(new Date(startTime)));
					if(dateType == 2)
						unifiedCSVPacket.setIngressStartDate(simpleDateFormat.format(new Date(startTime)));
					if(dateType == 3)
						unifiedCSVPacket.setEgressStartDate(simpleDateFormat.format(new Date(startTime)));

					unifiedCSVPacket.setDuration(String.valueOf((long)Math.ceil(Double.parseDouble(strDuration))));
					unifiedCSVPacket.setAutoCorrectedCDR("Y");
				}catch (ParseException parseException) {
					if(dateType == 1)
						unifiedCSVPacket.setStartDate("00/00/00 00:00:00");
					if(dateType == 2)
						unifiedCSVPacket.setIngressStartDate("00/00/00 00:00:00");
					if(dateType == 3)
						unifiedCSVPacket.setEgressStartDate("00/00/00 00:00:00");

					LogManager.getLogger().trace(MODULE, parseException);
				}catch (NumberFormatException numberFormatException){
					if(dateType == 1)
						unifiedCSVPacket.setStartDate("00/00/00 00:00:00");
					if(dateType == 2)
						unifiedCSVPacket.setIngressStartDate("00/00/00 00:00:00");
					if(dateType == 3)
						unifiedCSVPacket.setEgressStartDate("00/00/00 00:00:00");
					LogManager.getLogger().trace(MODULE, numberFormatException);
				}
			}else if((startDate!=null && startDate.length()!=0) && (endDate!=null && endDate.length()!=0)
					&& (strDuration == null || strDuration.length() <= 0)){
				try{
					Date date1 = simpleDateFormat.parse(startDate);
					Date date2 = simpleDateFormat.parse(endDate);
					long lDuration = (date2.getTime()-date1.getTime())/1000;
					unifiedCSVPacket.setDuration(String.valueOf(lDuration));
					unifiedCSVPacket.setAutoCorrectedCDR("Y");
				}catch (ParseException parseException) {
					unifiedCSVPacket.setDuration("0");
					LogManager.getLogger().trace(MODULE, parseException);
				}
			}
		}else{
			LogManager.getLogger().warn(MODULE,"UnifiedPacket received null on auto-cortection of dates and duration.");
		}
	}*/
	
	protected String getCharactersTrim(String tempValue,String[] trimCharacters,String trimCharacterPosition){
		String trimValueString = tempValue;
		if(trimCharacterPosition != null){ 
			if("Left".equalsIgnoreCase(trimCharacterPosition) ){
				for(int k=0;k<trimCharacters.length;k++){
					if(tempValue.startsWith(trimCharacters[k])) {
						trimValueString = tempValue.substring(tempValue.indexOf(trimCharacters[k])+trimCharacters[k].length(),tempValue.length());
					}
				}	
			}

			if("Right".equalsIgnoreCase(trimCharacterPosition) ){
				for(int k=0;k<trimCharacters.length;k++){
					if(tempValue.endsWith(trimCharacters[k])) {
						trimValueString = tempValue.substring(0,tempValue.lastIndexOf(trimCharacters[k]));
					}
				}	
			}

			if("Both".equalsIgnoreCase(trimCharacterPosition) ){
				for(int k=0;k<trimCharacters.length;k++){
					if(tempValue.startsWith(trimCharacters[k])) {
						trimValueString = tempValue.substring(tempValue.indexOf(trimCharacters[k])+trimCharacters[k].length(),tempValue.length());
					}
					if(tempValue.endsWith(trimCharacters[k])) {
						trimValueString = trimValueString.substring(0,trimValueString.lastIndexOf(trimCharacters[k]));
					}
				}
			}
		}else{
			//if trim position not selected then default behavior would be trim from both sides
			for(int k=0;k<trimCharacters.length;k++){
				if(tempValue.startsWith(trimCharacters[k])) {
					trimValueString = tempValue.substring(tempValue.indexOf(trimCharacters[k])+trimCharacters[k].length(),tempValue.length());
				}
				if(tempValue.endsWith(trimCharacters[k])) {
					trimValueString = trimValueString.substring(0,trimValueString.lastIndexOf(trimCharacters[k]));
				}
			}
		}
		
		if(StringUtils.EMPTY.equals(trimCharacterPosition)){
			LogManager.getLogger().error("BASE MEDIATION PACKET", "Trim-position is not selected, so characters will not get trim");
		}
		
		return trimValueString;
		
	}
	
	public boolean isMalformPacket(){
		return isMalformPacket;
	}
	
	public void setError(OfflineRnCErrorCodes errorCode, String errorMessage) {
		fieldValueMap.put(OfflineRnCConstants.ERROR_CODE_HEADER, errorCode.code());
		fieldValueMap.put(OfflineRnCConstants.ERROR_MESSAGE_HEADER, errorMessage);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(rawData, 0, rawData.length);
		out.write(CommonConstants.COMMA);
		try {
			out.write(errorCode.code().getBytes());
			out.write(CommonConstants.COMMA);
			out.write(errorMessage.getBytes());
			out.write(com.elitecore.core.commons.util.constants.CommonConstants.LINE_SEPARATOR.getBytes());
			this.rawData = out.toByteArray();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
}
