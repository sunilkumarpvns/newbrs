package com.elitecore.netvertex.gateway.file.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringUtils;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.netvertex.core.constant.OfflineRnCConstants;
import com.elitecore.netvertex.gateway.file.FileGatewayConfiguration;
import com.googlecode.ipv6.IPv6Address;

/**
 * The Class Utilities.
 *
 * @author jayshah
 */
public class Utilities {

	private static final String MESSAGE = "Problem occured while closing stream, Reason : ";

	private static final String MODULE = "OFCS-UTILITIES";

	private static final String IPV4ADDRESSPATTERN =
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private static final String IPV6ADDRESSPATTERN = "^([\\dA-Fa-f]{1,4}:|((?=.*(::))(?!.*\\3.+\\3))\\3?)([\\dA-Fa-f]{1,4}(\\3|:\\b)|\\2){5}(([\\dA-Fa-f]{1,4}(\\3|:\\b|$)|\\2){2})\\z";

	private static final String HEX = "HEX";

	private static final String HEXREV = "HEXREV";

	private static final String DEC = "DEC";

	private static final String DECREV = "DECREV";

	private static final String ASCII = "ASCII";

	private static final String CHAR = "CHAR";

	/**
	 * 
	 */
	private Utilities() {
	}

	/**
	 * Gets the value for spcefic type.
	 *
	 * @param b the b
	 * @param type the type
	 * @return the value for spcefic type
	 */
	public static String getValueForSpceficType(byte[] b, String type) {
		String value = null;
		try {
			if (type.equalsIgnoreCase(HEX)) {
				value = bytesToHex(b);
			} else if (type.equalsIgnoreCase(HEXREV)) {
				value = bytesToHexReverse(b);
			} else if (type.equalsIgnoreCase(DEC)) {
				value = Long.toString(bytesToDec(b));
			} else if (type.equalsIgnoreCase(DECREV)) {
				value = Long.toString(bytesToDecReverse(b));
			} else if (type.equalsIgnoreCase(ASCII)) {
				value = new String(b, "US-ASCII");
			} else if (type.equalsIgnoreCase(CHAR)) {
				value = bytesToChar(b);
			}
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		if (value != null)
			return value.trim();
		return value;
	}

	/**
	 * Bytes to char.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String bytesToChar(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			char tempChar = (char) (b & 0xFF);
			buf.append(tempChar);
		}
		return buf.toString();
	}

	/**
	 * Bytes to hex.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String bytesToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			buf.append(byteToHex(data[i]));
		}
		return buf.toString();
	}

	/**
	 * Bytes to hex reverse.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String bytesToHexReverse(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			buf.append(byteToHexReverse(data[i]));
		}
		return buf.toString();
	}

	/**
	 * Byte to hex.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String byteToHex(byte data) {
		char tempChar = (char) (data & 0xFF);
		StringBuilder buf = new StringBuilder();
		buf.append(toHexChar((tempChar >>> 4) & 0x0F));
		buf.append(toHexChar(tempChar & 0x0F));
		return buf.toString();
	}

	/**
	 * Byte to hex reverse.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String byteToHexReverse(byte data) {
		char tempChar = (char) (data & 0xFF);
		StringBuilder buf = new StringBuilder();
		buf.append(toHexChar((tempChar >>> 4) & 0x0F));
		buf.append(toHexChar(tempChar & 0x0F));
		buf.reverse();
		return buf.toString();
	}

	/**
	 * To hex char.
	 *
	 * @param i the i
	 * @return the char
	 */
	public static char toHexChar(int i) {
		if ((0 <= i) && (i <= 9)) {
			return (char) ('0' + i);
		} else {
			return (char) ('A' + (i - 10));
		}
	}

	/**
	 * Bytes to dec.
	 *
	 * @param b the b
	 * @return the long
	 */
	public static long bytesToDec(byte[] b) {
		long total = 0;
		for(int i = 0 ; i < b.length ; i++) {
			int temp = b[i];
			if(temp < 0) {
				total += (128 + (b[i] & 0x7f)) * Math.pow(2, (b.length-1-i) * 8.0);
			} else {
				total += ((b[i] & 0x7f) * Math.pow(2, (b.length-1-i) * 8.0));
			}
		}
		return total;
	}

	/**
	 * Bytes to ip.
	 *
	 * @param b the b
	 * @return the string
	 */
	public static String bytesToIP(byte[] b) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			byte[] b1 = new byte[1];
			b1[0] = b[i];
			String tempString = Long.toString(bytesToDec(b1));
			stringBuilder.append(tempString);
			if (i != 3)
				stringBuilder.append(".");
		}
		return stringBuilder.toString();
	}

	/**
	 * Bytes to ip v6.
	 *
	 * @param b the b
	 * @return the string
	 */
	public static String bytesToIPV6(byte[] b) {
		IPv6Address iPv6Address = IPv6Address.fromByteArray(b);
		return iPv6Address.toLongString();
	}

	/**
	 * Bytes to ip v6.
	 *
	 * @param b the b
	 * @return the string
	 */
	public static String bytesToIPV6UniversalFormat(byte[] b) {
		IPv6Address iPv6Address = IPv6Address.fromByteArray(b);
		return iPv6Address.toString();
	}

	/**
	 * Bytes to dec reverse.
	 *
	 * @param b the b
	 * @return the long
	 */
	public static long bytesToDecReverse(byte[] b) {
		long total = 0;
		for(int i = 0 ; i < b.length ; i++) {
			int temp = b[i];
			if(temp < 0) {
				total += (128 + (b[i] & 0x7f)) * Math.pow(2, (i) * 8.0);
			} else {
				total += ((b[i] & 0x7f) * Math.pow(2, (i) * 8.0));
			}
		}
		return total;
	}

	/**
	 * Gets the ip address range.
	 *
	 * @param startIpAddress the start ip address
	 * @param endIpAddress the end ip address
	 * @return the ip address range
	 * @throws UnknownHostException the unknown host exception
	 */
	public static List<String> getIpAddressRange(String startIpAddress, String endIpAddress) throws UnknownHostException {
		List<String> ipRangeList = new ArrayList<>();

		InetAddress inetAddress = InetAddress.getByName(startIpAddress);
		byte[] ipAddressByteArray = inetAddress.getAddress();

		if(ipAddressByteArray.length == 4) {
			ipRangeList.add(startIpAddress);
			Long decimal = bytesToDec(ipAddressByteArray);
			if(!startIpAddress.equals(endIpAddress)){

				while(!startIpAddress.equals(endIpAddress)) {
					decimal++;
					ByteBuffer byteBuffer = ByteBuffer.allocate(8);
					byteBuffer.putLong(decimal);
					byte[] byteArray = new byte[4];
					byteArray[0] = byteBuffer.get(4);
					byteArray[1] = byteBuffer.get(5);
					byteArray[2] = byteBuffer.get(6);
					byteArray[3] = byteBuffer.get(7);
					InetAddress nextAddress = InetAddress.getByAddress(byteArray);
					startIpAddress = nextAddress.getHostAddress();
					ipRangeList.add(nextAddress.getHostAddress());
				}
			}
			return ipRangeList;
		} else {
			IPv6Address startIPv6Address = IPv6Address.fromByteArray(ipAddressByteArray);
			IPv6Address endIPv6Address = IPv6Address.fromString(endIpAddress);
			ipRangeList.add(startIPv6Address.toLongString());
			while(!startIPv6Address.equals(endIPv6Address)) {
				startIPv6Address = startIPv6Address.add(1);
				ipRangeList.add(startIPv6Address.toLongString());
			}
			return ipRangeList;
		}
	}

	/**
	 * Gets the hash code.
	 *
	 * @param ipAdd the ip add
	 * @param partition the partition
	 * @return the hash code
	 */
	public static int getHashCode(String ipAdd, int partition) {
		int originalHashCode = ipAdd.hashCode();
		originalHashCode = originalHashCode < 0 ? -originalHashCode  : originalHashCode ;
		return (originalHashCode) % partition;
	}

	/**
	 * 
	 * This Method convert Hex Data recieved from ASN Data to Numeric Value(Decimal)
	 * @param inputData
	 * @return
	 */
	public static String hextoDecimal(String inputString) {

		try{
			if(inputString != null ){
				return String.valueOf(Long.parseLong(inputString, 16));
			}
		}catch(NumberFormatException exp){
			LogManager.getLogger().error(MODULE, "Error occured while parsing the date, reason : " + exp.getMessage());
		}
		return "";

	}

	/**
	 * 
	 * This Method convert Hex Data recieved from ASN Data to IPv4 Address
	 * @param inputData
	 * @return
	 */
	public static String hextoIPv4(String inputString) {
		try {
			if(inputString != null){
				long ip = Long.parseLong(inputString, 16);
				return InetAddress.getByName(Long.toString(ip)).getHostAddress();
			}
		} catch (UnknownHostException e) {
			LogManager.getLogger().error(MODULE, "Improper value : " + inputString);
			LogManager.getLogger().trace(MODULE, e);
		}
		return "";
	}

	/**
	 * 
	 * This Method convert Hex Data recieved from ASN Data to IPv6 Address
	 * @param inputData
	 * @return
	 */
	public static String hextoIPv6(String inputString) {

		if (inputString != null && inputString.length() == 32) {

			StringBuilder stringBuilder = new StringBuilder();
			int i = 0;
			while (i < 32) {
				stringBuilder.append(inputString.substring(i, i + 4) + ":");
				i = i + 4;
			}
			stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");

			return stringBuilder.toString();
		}

		return "";
	}

	/**
	 * 
	 * This Method convert Hex Data recieved from ASN Data to text format.
	 * @param inputData
	 * @return text values
	 */
	public static String hextoString(String inputString) {

		if (inputString != null) {

			StringBuilder stringBuilder = new StringBuilder();
			int i = 0;
			while (i < inputString.length()) {
				String temp = inputString.substring(i, i + 2) ;
				stringBuilder.append((char)Integer.parseInt(hextoDecimal(temp)));
				i = i + 2;
			}
			return stringBuilder.toString();
		}
		return "";

	}

		public static String ipv4toHex(String ipv4String){

		String[] ipv4StringTokens = ipv4String.split("\\.", ipv4String.length());
		if( ipv4StringTokens != null && ipv4StringTokens.length == 4){
			StringBuilder stringBuilder = new StringBuilder();
			for(String token : ipv4StringTokens){
				String convertedToken = Integer.toHexString(Integer.parseInt(token));
				if(convertedToken.length() == 1){
					stringBuilder.append("0");
				}
				stringBuilder.append(convertedToken);

			}
			return stringBuilder.toString();
		}
		return "";
	}

	public static final String fillChar(String input, int length, char chr){
		StringBuilder stringBuilder = new StringBuilder();
		if (input != null){
			stringBuilder.append(input);
			for(int i = input.length(); i<=length; i++){
				stringBuilder.append(chr);
			}
		}
		return stringBuilder.toString();
	}

	public static final String getLastLineFromFile(File file){
		String line = null;
		String lastLine = null;
		try (FileInputStream fileInputStream = new FileInputStream(file);
				GZIPInputStream inputStream = new GZIPInputStream(new BufferedInputStream(fileInputStream));
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()), (int)file.length())) {

			while((line = bufferedReader.readLine()) != null){
				if(line.trim().length() > 0){
					lastLine = line;
				}
			}
		} catch(IOException ioe){
			LogManager.getLogger().error(MODULE, "Error occured while reading the statistics file - "+ file.getAbsolutePath()+", Reason : "+ ioe.getMessage());
			LogManager.getLogger().trace(MODULE	,ioe);
		}
		return lastLine;
	}

	public static final String getLastLineFromFile(File file,String listContainsText){

		BufferedReader bufferedReader = null;
		InputStream inputStream = null;
		String line = null;
		String lastLine = null;
		FileInputStream fileInputStream = null;
		try{
			fileInputStream = new FileInputStream(file);
			inputStream = new GZIPInputStream(new BufferedInputStream(fileInputStream));
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()), (int)file.length());
			while((line = bufferedReader.readLine()) != null){
				if(line.trim().length() > 0 && line.contains(listContainsText)){
					lastLine = line;
				}
			}
		}catch(IOException ioe){

			LogManager.getLogger().error(MODULE, "Error occured while reading the statistics file - "+ file.getAbsolutePath()+", Reason : "+ ioe.getMessage());
			LogManager.getLogger().trace(MODULE	,ioe);
		}finally{
			if(fileInputStream != null){
				try {
					fileInputStream.close();
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
				}

			}

			if(bufferedReader != null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
		return lastLine;
	}

	/**
	 * Parses the file name.
	 *
	 * @param fileName the file name
	 * @return the string
	 */
	public static String parseFileName(String fileName) {
		int curlyBraceIndex = -1;
		String timestampFormat = "";
		String fileExtension = null;
		String baseFilenamePrefix = null;
		String baseFilenameSuffix = null;
		String fileTimestampFormat = null;

		char[] fileNameArray = fileName.toCharArray();
		String newFileName = "";
		try {
			for (int i = 0; i < fileNameArray.length; i++) {
				if (fileNameArray[i] == '\\') {
					if (fileNameArray.length > i + 1) {
						i++;
						if (fileNameArray[i] != '\\') {
							newFileName += Character.toString(fileNameArray[i]);
						}
					}
				} else if (fileNameArray[i] == '{') {
					boolean hasClosingBrace = false;
					curlyBraceIndex = newFileName.length();
					i++;
					for (; i < fileNameArray.length; i++) {
						if (fileNameArray[i] != '}') {
							if (fileNameArray[i] != '\\') {
								timestampFormat += Character.toString(fileNameArray[i]);
							}
						} else {
							hasClosingBrace = true;
							break;
						}
					}
					if (hasClosingBrace) {
						fileTimestampFormat = timestampFormat;
					} else {
						curlyBraceIndex = -1;
						newFileName += "{" + timestampFormat;
					}
				} else {
					newFileName = newFileName + fileNameArray[i];
				}
			}
			fileName = newFileName;

			if (curlyBraceIndex == -1) {
				if (fileName.indexOf('.') != -1) {
					curlyBraceIndex = fileName.lastIndexOf('.');
				} else {
					curlyBraceIndex = fileName.length();
				}
			}
			if (fileName.indexOf('.') != -1) {
				baseFilenamePrefix = fileName.substring(0, curlyBraceIndex);
				baseFilenameSuffix = fileName.substring(curlyBraceIndex,fileName.lastIndexOf('.'));
				fileExtension = fileName.substring(fileName.lastIndexOf('.'));
			} else {
				baseFilenamePrefix = fileName.substring(0, curlyBraceIndex);
				baseFilenameSuffix = fileName.substring(curlyBraceIndex);
				fileExtension = "";
			}
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Error in file name parsing. Reason: " + e);
			LogManager.getLogger().trace(MODULE, e);
			String str = fileName;
			char[] charArray = str.toCharArray();
			str = "";
			for (int i = 0; i < charArray.length; i++) {
				if (charArray[i] != '\\') {
					str += Character.toString(charArray[i]);
				}
			}
			fileName = str;

			if (fileName.indexOf('{') != -1) {
				newFileName = fileName.substring(0, fileName.indexOf('{'));
				if (fileName.indexOf('.') != -1) {
					newFileName += fileName.substring(fileName.lastIndexOf('.') + 1);
				}
			} else {
				newFileName = fileName;
			}

			fileName = newFileName;
			LogManager.getLogger().info(MODULE, "File Name: " + fileName + " will be used");
			baseFilenamePrefix = fileName;
			baseFilenameSuffix = "";

			if (fileName.indexOf('.') != -1) {
				fileExtension = fileName.substring(fileName.lastIndexOf('.'));
			} else {
				fileExtension = "";
			}
		}
		if (fileTimestampFormat == null || "".equals(fileTimestampFormat)) {
			fileTimestampFormat = "yyyy-MM-dd_HHmmss_S";
		}
		SimpleDateFormat formatter = null;
		try {
			formatter = new SimpleDateFormat(fileTimestampFormat);
		} catch (IllegalArgumentException e) {
			LogManager.getLogger().warn(MODULE,"Timestamp Format is invalid. Reason: " + e + " Format: " + fileTimestampFormat);
			LogManager.getLogger().warn(MODULE,"Default format will be used. Format: yyyy-MM-dd_HHmmss_S");
			LogManager.getLogger().trace(MODULE, e);
			fileTimestampFormat = "yyyy-MM-dd_HHmmss_S";
			formatter = new SimpleDateFormat(fileTimestampFormat);
		}
		Date date = new Date();
		String strDate = formatter.format(date);

		String newFilename;
		if (baseFilenameSuffix != null && baseFilenameSuffix.length() > 0) {
			newFilename = baseFilenamePrefix + "_" + strDate + "_" + baseFilenameSuffix + fileExtension;
		} else {
			newFilename = baseFilenamePrefix + "_" + strDate + fileExtension;
		}
		return newFilename;
	}

	public static boolean createDirIfNotExist(String dirPath){
		if(dirPath != null && dirPath.trim().length() > 0){
			File dir = new File(dirPath);
			if(!dir.exists() && !dir.mkdirs()){
				return false;
			}
			return true;
		}
		return false;
	}
	
	public static Timestamp getFileDateFromFileName(FileGatewayConfiguration pathDataTmp,String inputFileName){
		Timestamp timestamp;
		SimpleDateFormat simpleDateFormat = null;
		File sourceFile =  new File(inputFileName);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(86400000));
		try {
			if(pathDataTmp.isDateAvailableInFile()) {
				simpleDateFormat = new SimpleDateFormat(pathDataTmp.getFileDateFormat());
				String fileDate = null;
				if(pathDataTmp.getFileDatePosition().equalsIgnoreCase(OfflineRnCConstants.LEFT)) {
					fileDate = sourceFile.getName().substring(pathDataTmp.getFileDateStartIndex()-1, pathDataTmp.getFileDateEndIndex());
				}else if(pathDataTmp.getFileDatePosition().equalsIgnoreCase(OfflineRnCConstants.RIGHT)) {
					fileDate = sourceFile.getName().substring(sourceFile.getName().length()-pathDataTmp.getFileDateStartIndex(), (sourceFile.getName().length()-pathDataTmp.getFileDateEndIndex())+1);
				}
				if(fileDate != null)
					cal.setTime(simpleDateFormat.parse(fileDate));
			}
		} catch(Exception e) {
			LogManager.getLogger().debug(MODULE, "Problem while getting date from file name, Reason :" + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		timestamp = new Timestamp(cal.getTimeInMillis());
		return timestamp;
	}

	public static String escapeCharactersForEncode(String str) {
		try {
			return String.format("%s",
					str.replaceAll("%20", " "))
					.replaceAll("%3A", ":")
					.replaceAll("%2F", "/")
					.replaceAll("%3B", ";")
					.replaceAll("%40", "@")
					.replaceAll("%3C", "<")
					.replaceAll("%3E", ">")
					.replaceAll("%3D", "=")
					.replaceAll("%26", "&")
					.replaceAll("%25", "%")
					.replaceAll("%24", "$")
					.replaceAll("%23", "#")
					.replaceAll("%2B", "+")
					.replaceAll("%2C", ",")
					.replaceAll("%3F", "?");
		} catch (Exception ex) {
			LogManager.getLogger().trace(MODULE, ex);
			return str;
		}
	}

	public static boolean isIPv4(String ipAddress) {
		if(ipAddress != null && !ipAddress.isEmpty()  && !ipAddress.contains(" ")){
			Pattern ipv4Pattern = Pattern.compile(IPV4ADDRESSPATTERN);
			Matcher ipv4Matcher = ipv4Pattern.matcher(ipAddress);
			return ipv4Matcher.matches();
		}
		else{
			return false;
		}
	}

	public static boolean isIPv6(String ipAddress) {
		if(ipAddress != null && !ipAddress.isEmpty() &&  !ipAddress.contains(" ")){
			Pattern ipv6Pattern = Pattern.compile(IPV6ADDRESSPATTERN);
			Matcher ipv6Matcher = ipv6Pattern.matcher(ipAddress);
			return ipv6Matcher.matches();
		}
		else{
			return false;
		}
	}

	public static final void closeStream(Closeable closeable, String module) {
		if (closeable != null){
			try {
				closeable.close();
			} catch (IOException e) {
				LogManager.getLogger().error(module, MESSAGE + e.getMessage());
				LogManager.getLogger().trace(module, e);
			}
		}
	}
	
	public static final void flushAndCloseOutputStream(OutputStream outputStream, String module) {
		if (outputStream != null){
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {//NOSONAR
				LogManager.getLogger().warn(module, MESSAGE + e.getMessage());
				//LogManager.getLogger().trace(module, e);
			}
		}
	}
	
	public static boolean isGzipFile(File inProcessFile, String module) {
		byte[] bFileBytes = new byte[2];
		FileInputStream fileInputStream = null;
		try{
			fileInputStream = new FileInputStream(inProcessFile);
			fileInputStream.read(bFileBytes, 0, 2);
			int head = ((int) bFileBytes[0] & 0xff) | ((bFileBytes[1] << 8) & 0xff00);
			return (GZIPInputStream.GZIP_MAGIC == head);
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
			return false;
		}finally{
			closeStream(fileInputStream, module);
			
		}
	}
	
	public static boolean isZipFile(File file, String module){
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "r");
			int head = raf.readInt();
			return (head == 0x504B0304);
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			return false;
		}finally{
			closeStream(raf, module);
		}
	}
	
	public static long getNumberOfHoursInMinuteUnit(Calendar  startDate , Calendar endDate){
		
		unsetTimeFromDate(startDate);
		unsetTimeFromDate(endDate);
		long diff = endDate.getTimeInMillis() - startDate.getTimeInMillis();
		if( diff == 0 )
			return diff;
		return diff / (1000 * 60);
	}
	
	public static void unsetTimeFromDate(Calendar date){
		if(date != null){
			date.set(Calendar.MILLISECOND, 0);
			date.set(Calendar.SECOND,0);
			date.set(Calendar.MINUTE,0);
		}
	}
	
	public static void unsetTimeFromDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MINUTE,0);
		date = cal.getTime();
	}
	
	public static Date convertStringToDate(String dateStr, String dateFormat) {
		if(StringUtils.isNotEmpty(dateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			try {
				return sdf.parse(dateStr);
			} catch (ParseException e) {
				LogManager.getLogger().error(MODULE, "Error while parsing String " + dateStr + " to Date with format: " + dateFormat);
			}
		}
		return null;
	}
}