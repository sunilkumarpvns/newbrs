package com.elitecore.diameterapi.diameter.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.util.MalformedNAIException;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DiameterUtility {

	private static final String MODULE = "DIA-UTILITY";

	public static final byte BIT_10000000 = (byte)(0x80 & 0xFF);

	public static final byte BIT_01000000 = 64;
	
	public static final byte BIT_00100000 = 32;
	
	public static final byte BIT_00010000 = 16;
	
	public static final byte BIT_00001000 = 8;
	
	public static final byte BIT_00000100 = 4;
	
	public static final byte BIT_00000010 = 2;
	
	public static final byte BIT_00000001 = 1;

	//private static final String MODULE = "Diameter Utility";
	
	public static final Splitter diaAVPIdSplitter = Splitter.on('.');

	public static Pattern realmRegx;
	public static Pattern userNameRegx;
	static{
		//compiling the regular expressions for NAI
		realmRegx = Pattern.compile("[0-9]*[a-zA-Z]([a-zA-Z0-9-]*[a-zA-Z0-9])*(\\.[a-zA-Z]([a-zA-Z0-9-]*[a-zA-Z0-9])*)+$");
		userNameRegx = Pattern.compile("[a-zA-Z0-9!_#$%&'*+-/=?^_`{}~|\\w &&[^.]]+[.]?[a-zA-Z0-9!_#$%&'*+-/=?^_`{}~|\\w &&[^.]]+");;
	}
	
	public static byte[] intToByteArray (int integer) {
		return intToByteArray(integer, 4);
	}
	
	/**
	 * Convert integer value to byte array.
	 * @param integer
	 */
	public static byte[] intToByteArray (int integer, int noOfBytes) {
		byte[] byteArray = new byte[noOfBytes];
		
		for (int n = 0; n < noOfBytes; n++)
			byteArray[(noOfBytes - 1) - n] = (byte) (integer >>> (n * 8));

		return (byteArray);
	}
	
	public static void intToByteArray (ByteBuffer out, int position, int integer, int noOfBytes) {
		out.position(position);
		for (int n = noOfBytes-1; n >= 0; n--){						
			out.put((byte) (integer >>> (n * 8)));
		}
	}

	public static boolean isBaseProtocolPacket(int intCommandCode) {
		return (intCommandCode == CommandCode.CAPABILITIES_EXCHANGE.code || intCommandCode == CommandCode.DEVICE_WATCHDOG.code || intCommandCode == CommandCode.DISCONNECT_PEER.code) ?  true :  false;
	}
	
    private static final char LHEX[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f'
    };

	
	public static String bytesToHex(byte buf[]){
	        int length = buf.length;
	        StringBuilder hexbuf = new StringBuilder(length << 1);
	        for(int i = 0; i < length; i++){
	            hexbuf.append(LHEX[buf[i] >> 4 & 0xf]);
	            hexbuf.append(LHEX[buf[i] & 0xf]);
	        }

	        return hexbuf.toString();
	}
	
	public static String getMacAddress(byte buf[]){
        int length = buf.length-1;
        if(buf.length == 6){// MAC Address length == 48 bits == 6 bytes
        	StringBuilder hexbuf = new StringBuilder(length << 1);
            int i=0;
            for(; i < length; i++){
                hexbuf.append(LHEX[buf[i] >> 4 & 0xf]);
                hexbuf.append(LHEX[buf[i] & 0xf]);
                hexbuf.append(":");
            }
            hexbuf.append(LHEX[buf[i] >> 4 & 0xf]);
            hexbuf.append(LHEX[buf[i] & 0xf]);
            return hexbuf.toString();
        }else{
        	System.out.println("ERROR: Invalid length of MAC address");
        	return bytesToHex(buf);
        }

	}
	
	/**   
	 *    @return     integer Array 
	 *    @arguments  strAttributeId
	 * 	  For getting the AttributesId from the String of AttributeIds
	 */
		
		public static int[] getAttributeIds(String strAttributeId) throws NumberFormatException {
			int ids[] = null;
			if(strAttributeId.contains(":")){
				String strIds[] = strAttributeId.split(":");
				ids = new int[strIds.length];
				for(int i=0; i<strIds.length; i++){
						ids[i] = Integer.parseInt(strIds[i ]);
				}
			}else{
				ids = new int[2];
				ids[0] = 0;
				ids[1] = Integer.parseInt(strAttributeId);
			}
			return ids;
		}

	
	public static boolean matches(String sourceString, String pattern){
		return matches(sourceString,pattern.toCharArray());
	}
	public static boolean matches(String sourceString, char[] pattern){
		int stringOffset = 0;
		char[] stringCharArray = sourceString.toCharArray();
		final int stringLen = stringCharArray.length;
		final int patternLen = pattern.length;
		int currentPos=0;
		try{
			for(currentPos=0;currentPos<patternLen;currentPos++,stringOffset++){
				if(stringOffset == stringLen){
					while(currentPos < patternLen){
						if(pattern[currentPos] != '*')
							return false;
						currentPos++;
					}
					return true;
				}
				
				
				if(pattern[currentPos]!=stringCharArray[stringOffset]){
					if(pattern[currentPos]=='\\'){
						currentPos++;
						if(pattern[currentPos]!=stringCharArray[stringOffset])
							return false;
						else
							continue;
					}
					if(pattern[currentPos]=='*'){
						boolean bStar = true;
						currentPos++;
						if(currentPos == patternLen)
							return true;
						while(bStar){
							int tmpCurrentPos = currentPos;
							//go to first matching occurrence
							while(stringCharArray[stringOffset]!=pattern[tmpCurrentPos]){
								stringOffset++;
								if(stringOffset == stringLen)
									return false;
							}
							//match whole string until next * come
							while(tmpCurrentPos < patternLen){
								if(pattern[tmpCurrentPos] != stringCharArray[stringOffset]){
									if(pattern[tmpCurrentPos] == '*'){
										bStar = false;
										currentPos = tmpCurrentPos - 1;
										stringOffset--;
										break;
									}else if(pattern[tmpCurrentPos] != '?'){
										break;
									}
								}
								tmpCurrentPos++;
								stringOffset++;
								if(stringOffset == stringLen){
									while(tmpCurrentPos < patternLen){
										if(pattern[tmpCurrentPos] != '*')
											return false;
										tmpCurrentPos++;
									}
									return true;
								}
							}
							if(stringOffset == stringLen && tmpCurrentPos == patternLen)
								return true;
						}
						continue;
					}
					if(pattern[currentPos]=='?'){
						continue;
					}
					return false;
				}
			}
		}catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
		if(currentPos == patternLen && stringOffset == stringLen)
			return true;
		else
			return false;
	}
	public static boolean isGroupAvpId(String avpId){
		return avpId.contains(".");
	}
	/**
	 * This method will create N level nested DiameterAvp
	 * from strAvp and will set avpValue to the leaf AVP
	 *   
	 * @param strAvp 
	 * @param avpValue	 
	 * @return IDiameterAVP Parent GroupedAvp will be returned.
	 * 
	 */
	public static IDiameterAVP createAvp(String strAvp, String avpValue){
		String avpIds[] = strAvp.split("\\.");
		IDiameterAVP avp;
		IDiameterAVP currentAvp = null;
		for(int i=avpIds.length-1;i>=0;i--){
			avp = DiameterDictionary.getInstance().getAttribute(avpIds[i]);
			if(avp.isGrouped()){				
				if(currentAvp !=null) {
					((AvpGrouped)avp).addSubAvp(currentAvp);
				} else {
					avp.setStringValue(avpValue);
				}
			}else{
				if(avpValue != null && avpValue.length()>0)
					avp.setStringValue(avpValue);
			}
			currentAvp = avp;
		}
		return currentAvp;		
	}

	public static IDiameterAVP createAvp(String strAvp){
		return createAvp(strAvp,null);		
	}
	public static void addOrReplaceAvp(String avpId,DiameterPacket packet){
		addOrReplaceAvp(avpId,packet,null);
	}
	public static void addOrReplaceAvp(String avpId,DiameterPacket packet,String value){
		if(DiameterUtility.isGroupAvpId(avpId)){
			IDiameterAVP avp = packet.getAVP(avpId);
			if(avp!=null){		
				if(value != null)
					avp.setStringValue(value);
				return;								
			}else{
				String avpIds[] = avpId.split("\\.");
				final int size = avpIds.length;
				AvpGrouped groupedAvp = (AvpGrouped)packet.getAVP(avpIds[0]);
				if(groupedAvp!=null){
					avp=null;
					int i =1;
					for(;i<size;i++){
						avp = groupedAvp.getSubAttribute(avpIds[i]);
						if(avp==null)
							break;
						if(avp.isGrouped())
							groupedAvp = (AvpGrouped)avp;
					}
					if(i!=size){
						StringBuilder grpAvpId = new StringBuilder();
						while(i<size){							
							grpAvpId.append(avpIds[i]);							
							i++;
							if(i!=size)
								grpAvpId.append(".");
						}
						IDiameterAVP subAvp = DiameterUtility.createAvp(grpAvpId.toString(),value);
						groupedAvp.addSubAvp(subAvp);
					}					
					packet.refreshPacketHeader();
				}else{
					IDiameterAVP subAvp = DiameterUtility.createAvp(avpId,value);
					packet.addAvp(subAvp);			
				}
			}			
		}else{
			IDiameterAVP avp = packet.getAVP(avpId);
			if(avp == null){
				avp = DiameterUtility.createAvp(avpId,value);
				packet.addAvp(avp);
			}else{
				avp.setStringValue(value);
			}
		}			
	}
	
	public static byte[] getBytesFromHexValue(String strValue){
		byte[] valueBytes = null;
		String hexValue = strValue.substring(2);
        if(hexValue.length() % 2 == 0) {
            int len  = hexValue.length()/2;
            valueBytes = new byte[len];
            for (int i = 0; i < len; i++) {
            	valueBytes[i] = (byte) Integer.parseInt(hexValue.substring(2*i, 2*i+2), 16);
            }
        }
        return valueBytes;
	}

	public static String getBytesAsString(String title, byte[] bytesToPrint){
		StringWriter strWriter = new StringWriter();
		PrintWriter out = new PrintWriter(strWriter);
		out.print(title);
		out.print("-->");
		for (int i = 0; i < bytesToPrint.length; i++) {
			byte b = bytesToPrint[i];
			out.print(Integer.toHexString((b & 0xFF)));
			out.print(" ");
		}
		out.flush();
		out.close();
		return strWriter.toString();
	}
	
	public static String getAVPIdFromName(String avpName){
		String avpId = "";
		if(avpName!=null){
		   if(isGroupAvpId(avpName)){
			   String avps[] = avpName.split("\\.");
			   int level = avps.length;
			   String currentAvpId;
			   for(int currentLevel=0;currentLevel<level;currentLevel++){
				   currentAvpId =  DiameterDictionary.getInstance().getStrAVPId(avps[currentLevel]);
				   if(currentAvpId==null){
					   avpId=null;
					   break;
				   }else {
					   if(currentLevel!=level-1)
						   avpId = avpId+currentAvpId+".";
					   else {
						   avpId = avpId+currentAvpId;
					   }
						   
				   }
			   }
			   
		   }else {
			 avpId  = DiameterDictionary.getInstance().getStrAVPId(avpName);
		   }
		}
		return avpId;
	}
	
	
	/**
	 * getDiameterAttributes create diameter attributes from argument provide argument should be key=value form
	 * @param arg
	 * @return ArrayList<IDiameterAVP>
	 */
	public static ArrayList<IDiameterAVP> getDiameterAttributes(String arg, ValueProvider valueProvider) throws Exception{
		ArrayList<IDiameterAVP> diameterAVPs = null;
		if(arg!=null && arg.trim().length() > 0){
			String [] args = arg.split(",");
			diameterAVPs = new ArrayList<IDiameterAVP>();
			if(args != null && args.length > 0){
				for(String str : args){
					String [] strs = ParserUtility.splitKeyAndValue(str);
					if(strs != null && strs.length == 3 && strs[0].trim().length()>0){
						IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(strs[0].trim());
						
						if(strs[2].startsWith("{")){
							ArrayList<IDiameterAVP> diametAvps = getGroupedAVPs(JSONObject.fromObject(strs[2]), valueProvider);
							if(diametAvps == null || diametAvps.isEmpty())
								continue;
							diameterAVP.setGroupedAvp(diametAvps);
						}else { 
							String val = valueProvider.getStringValue(strs[2]);
							if(val == null)
								continue;
							diameterAVP.setStringValue(val);
						}
						diameterAVPs.add(diameterAVP);
					}else
						throw new Exception("Invalid argument configured : "+str);
				}
			}else
				throw new Exception("Invalid argument , Reason : AVP not configured");
		}	
		return diameterAVPs;
	}
	
	/**
	 * getGroupedAVPs parse json objest and create array of grouped attribute
	 * @param json
	 * @return ArrayList<IDiameterAVP>
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<IDiameterAVP> getGroupedAVPs(JSONObject json, ValueProvider valueProvider) {
		Set<String> keySet = json.keySet();
		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
		for(String key : keySet){
			Object object = json.get(key);
			IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(key.trim());
			if(object instanceof JSONObject){
				ArrayList<IDiameterAVP> tempDiameterAVPs = getGroupedAVPs((JSONObject)object, valueProvider);
				if(tempDiameterAVPs == null || tempDiameterAVPs.isEmpty())
					continue;
				
				diameterAVP.setGroupedAvp(tempDiameterAVPs);
			}else if(object instanceof JSONArray){
				ArrayList<IDiameterAVP> diaAVP = getGroupedAVPs(key,(JSONArray)object, valueProvider);
				if(diaAVP != null && !diaAVP.isEmpty())
					diameterAVPs.addAll(getGroupedAVPs(key,(JSONArray)object, valueProvider));
				continue;
			}else{
				String  val = valueProvider.getStringValue((String)object);
				if(val == null)
					continue;
					
				diameterAVP.setStringValue(val);
			}
			
			diameterAVPs.add(diameterAVP);
		}
		
		return diameterAVPs;
	}
	
	
	/**
	 * getGroupedAVPs parse json array and create array of grouped attribute
	 * @param json
	 * @return ArrayList<IDiameterAVP>
	 */
	private static ArrayList<IDiameterAVP> getGroupedAVPs(String key,JSONArray json, ValueProvider valueProvider) {
		ArrayList<IDiameterAVP> diameterAVPs = new ArrayList<IDiameterAVP>();
		for(Object object : json){
			IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(key);
			if(object instanceof JSONObject){
				ArrayList<IDiameterAVP> tempDiameterAVPs = getGroupedAVPs((JSONObject)object, valueProvider);
				if(tempDiameterAVPs == null || tempDiameterAVPs.isEmpty())
					continue;
				
				diameterAVP.setGroupedAvp(tempDiameterAVPs);
			}else{
				String  val = valueProvider.getStringValue((String)object);
				if(val == null)
					continue;
					
				diameterAVP.setStringValue(val);
			}
			
			diameterAVPs.add(diameterAVP);
		}
		
		return diameterAVPs;
	}
	
	/***
	 * appends bytes of newArrayBytes to the oldArrayBytes. 
	 * if the oldArrayBytes is null, then it will assign newArrayBytes to the OldArrayBytes
	 * if the newArrayBytes is null, then it will do nothing, simply return OldArrayBytes 
	 * @param oldArrayBytes
	 * @param newArrayBytes
	 * @return
	 */
	public static byte[] appendBytes(byte[] oldArrayBytes,byte[] newArrayBytes)
	{
		if(oldArrayBytes == null)
			return(newArrayBytes);
		
		byte[] tempArrayBytes = oldArrayBytes;
		if(newArrayBytes != null)
		{
			tempArrayBytes = new byte[oldArrayBytes.length + newArrayBytes.length];
			System.arraycopy(oldArrayBytes,0,tempArrayBytes,0,oldArrayBytes.length);
			System.arraycopy(newArrayBytes,0,tempArrayBytes,oldArrayBytes.length,newArrayBytes.length);
		}
		return(tempArrayBytes);
	}
	public static boolean isNAIDecorated(String nai){
		return(nai.contains("@"));
	}

	public static boolean isValidRealmAccordingToABNF(String realm){
		return(realmRegx.matcher(realm).matches());
	}
	public static boolean isValidForProxy(String nai){
		return(nai.contains("!") && !(nai.indexOf('!') == 0));		
	}
	public static String getProxyRealm(String nai){
		if(nai.contains("!")&& !(nai.indexOf('!') == 0)){
			String otherRealm = nai.substring(0, nai.indexOf('!'));
			return otherRealm;
		}
		return null;
	}
	public static String transformNAI(String originalNAI) throws MalformedNAIException{
		StringBuffer buffer = new StringBuffer(); 
		String otherRealm =  originalNAI.substring(originalNAI.lastIndexOf("@")+1, originalNAI.length());
		int startIndex = 0;
		String[] tokens = originalNAI.split("!");
		for(int i=0;i<tokens.length-1;i++){
			if(!(tokens[i].trim().length() == 0)){
				startIndex += tokens[i].length()+1;
				otherRealm = tokens[i];
				if(!isValidRealmAccordingToABNF(otherRealm)){
					throw new  MalformedNAIException("Realm Name : " + otherRealm + " is Invalid according to RFC 4282");
				}
				break;
			}else{
				for(int j=i;j<tokens.length-1;j++){
					if(!(tokens[j].trim().length() == 0)){
						throw new MalformedNAIException("NAI is malformed.");
					}
				}
			}
		}
		buffer.append(originalNAI.substring(startIndex , originalNAI.lastIndexOf('@')+1) + otherRealm);
		return new String(buffer);
	}
	public static boolean isValidUserAccordingToABNF(String nai){
		String user = nai;
		if(nai.contains("@")){
			user = nai.substring(0,nai.indexOf('@'));
		}
		//TODO the check for the backslash is remaining
		return (userNameRegx.matcher(user).matches());
	}
	
	public static boolean isIPRange(String ipRange) {
		if (ipRange != null) {
			return ipRange.contains("-") || ipRange.contains("/");
		}
		return false;
	}
	
	public static List<String> getAvailableIPs(String ipRange) throws NumberFormatException{
		if(ipRange == null) { 
			return null;
		}

		if (ipRange.contains("-")) {
			return getAddressFromIPRange(ipRange);
		} else if (ipRange.contains("/")) {
			return getAddressFromNetworkMask(ipRange);
		}
		return null;
	}
	
	private static List<String> getAddressFromIPRange(String ipRange) throws NumberFormatException {
		String[] ips = ipRange.split("\\-");
		if(ips.length != 2)
			throw new NumberFormatException("Invalid IP range format '" + ipRange + "', should be: xx.xx.xx.xx-xx.xx.xx.xx");
		
		String[] strIP1 = ips[0].split("\\.");
		if(strIP1.length != 4)
			throw new NumberFormatException("Invalid IP format '" + ips[0] + "', should be: xx.xx.xx.xx");
		
		String[] strIP2 = ips[1].split("\\.");
		if(strIP2.length != 4)
			throw new NumberFormatException("Invalid IP format '" + ips[1] + "', should be: xx.xx.xx.xx");
		
		String tempString = "";
		int index = -1;
		for(int i=0; i<4; i++) {
			if(strIP1[i].equals(strIP2[i])) {
				tempString = tempString + strIP1[i] + ".";
				continue;
			} else {
				index = i;
				break;
			}
		}
		
		// only Class C network addresses allowed  
		if(index != 3) 
			throw new NumberFormatException("Only Class C Network Addresses Allowed");
		
		List<String> ipAddressList = new ArrayList<String>();
		int ip1 = Integer.parseInt(strIP1[index]);
		int ip2 = Integer.parseInt(strIP2[index]);
		
		if(ip1 < 0 || ip1 > 255 || ip2 <= 0 || ip2 > 255)
			throw new NumberFormatException("Invalid IP Range");
		
		for(int i=ip1; i <= ip2; i++) {
			String str = tempString + i;
			ipAddressList.add(str);
		}
		
		return ipAddressList;
	}

	private static List<String> getAddressFromNetworkMask(String ipInCIDRFormat) throws NumberFormatException {
		String[] st = ipInCIDRFormat.split("\\/");
		if (st.length != 2)
			throw new NumberFormatException("Invalid CIDR format '" + ipInCIDRFormat + "', should be: xx.xx.xx.xx/xx");
		
		String symbolicIP = st[0];
		String symbolicCIDR = st[1];
		Integer numericCIDR = new Integer(symbolicCIDR);
		
		if (numericCIDR < 24)
			throw new NumberFormatException("Netmask CIDR can not be less than 24, Reason: Only Class C Network Addresses Allowed");
		
		if (numericCIDR > 32)
			throw new NumberFormatException("CIDR can not be greater than 32");

		st = symbolicIP.split("\\.");
		if (st.length != 4)
			throw new NumberFormatException("Invalid IP address: " + symbolicIP);

		int i = 24;
		int baseIPnumeric = 0;
		for (int n = 0; n < st.length; n++) {
			int value = Integer.parseInt(st[n]);
			if (value != (value & 0xff))
				throw new NumberFormatException("Invalid IP address: " + symbolicIP);

			baseIPnumeric += value << i;
			i -= 8;
		}

		/* netmask from CIDR */
		int netmaskNumeric = 0xffffffff;
		netmaskNumeric = netmaskNumeric << (32 - numericCIDR);
		
		/* Now calculating IP addresses */
		ArrayList<String> ipAddressList = new ArrayList<String>();
		int numberOfBits;
		for (numberOfBits = 0; numberOfBits < 32; numberOfBits++) {
			if ((netmaskNumeric << numberOfBits) == 0)
				break;
		}
		Integer numberOfIPs = 0;
		for (int n = 0; n < (32 - numberOfBits); n++) {
			numberOfIPs = numberOfIPs << 1;
			numberOfIPs = numberOfIPs | 0x01;
		}

		Integer baseIP = baseIPnumeric & netmaskNumeric;
		for (int j = 1; j < (numberOfIPs); j++) {
			Integer ourIP = baseIP + j;

			String ip = convertNumericIpToSymbolic(ourIP);
			ipAddressList.add(ip);
		}
		
		return ipAddressList;
	}
	
	private static String convertNumericIpToSymbolic(Integer ip) {
		StringBuffer sb = new StringBuffer(15);
		for (int shift = 24; shift > 0; shift -= 8) {
			// process 3 bytes, from high order byte down.
			sb.append(Integer.toString((ip >>> shift) & 0xff));
			sb.append('.');
		}
		sb.append(Integer.toString(ip & 0xff));
		return sb.toString();
	}
	
	/**
	 * 
	 * This will create {@link ApplicationEnum} based on provided Vendor id, Application Id and ServiceType.
	 * 
	 * It will first try to fetch {@link ApplicationEnum} form {@link ApplicationIdentifier}s based on 
	 * applicationId. 
	 * 
	 * If found, then it will validate provided VendorId and ServiceType, 
	 * if any of the these does not matches, 
	 * it will create the {@link ApplicationEnum} with provided Vendor id, Application Id and ServiceType.
	 * 
	 * @param applicationId of desired {@link ApplicationEnum}
	 * @param vendorId of desired {@link ApplicationEnum}
	 * @param serviceType of desired {@link ApplicationEnum}
	 * @return {@link ApplicationEnum} with given criteria
	 */
	public static ApplicationEnum createApplicationEnumStrictly(final long applicationId, final long vendorId,
			final ServiceTypes serviceType) {

		/*
		 * The advantage of this lookup is ApplicationEnum can be retrieved if already present in predefined pool.
		 */
		final ApplicationEnum applicationEnum = ApplicationIdentifier.fromApplicationIdentifiers(applicationId);

		if(applicationEnum != null 
				&& applicationEnum.getApplicationType() == serviceType 
				&& applicationEnum.getVendorId() == vendorId){	
			return applicationEnum;
		}

		return createApplicationEnum(applicationId, vendorId, serviceType, 
				applicationEnum != null ? applicationEnum.getApplication() : Application.UNKNOWN);
	}
	
	/**
	 * 
	 * This will create {@link ApplicationEnum} based on provided Application Id.
	 * 
	 * It will first try to fetch {@link ApplicationEnum} from {@link ApplicationIdentifier}s based on 
	 * applicationId. 
	 * 
	 * if it is not found in {@link ApplicationIdentifier}s, then it will 
	 * create the {@link ApplicationEnum} with provided Vendor id, Application Id and ServiceType.
	 * 
	 * @param applicationId of desired {@link ApplicationEnum}
	 * @param vendorId 
	 * @param defaultServiceType, used if {@link ApplicationIdentifier} not found for applicationId
	 * @param defaultApplication, used if {@link ApplicationIdentifier} not found for applicationId
	 * @return {@link ApplicationEnum} 
	 */
	public static ApplicationEnum createApplicationEnumLeniently(final long applicationId, final long vendorId,
			final ServiceTypes defaultServiceType, final Application defaultApplication) {

		final ApplicationEnum applicationEnum = ApplicationIdentifier.fromApplicationIdentifiers(applicationId);

		if(applicationEnum != null) {
			return applicationEnum;
		}

		return createApplicationEnum(applicationId, vendorId, defaultServiceType, defaultApplication);
	}
	
	/**
	 * Create {@link ApplicationEnum} based on input parameters
	 * 
	 * @param applicationId
	 * @param vendorId
	 * @param serviceType
	 * @param application
	 * @return
	 */
	public static ApplicationEnum createApplicationEnum(final long applicationId, final long vendorId,
			final ServiceTypes serviceType, final Application application) {
		return new ApplicationEnum() {
			
			@Override
			public long getVendorId() {
				return vendorId;
			}
			
			@Override
			public ServiceTypes getApplicationType() {
				return serviceType;
			}
			
			@Override
			public long getApplicationId() {
				return applicationId;
			}
			
			@Override
			public Application getApplication() {
				return application;
			}
			
			@Override
			public String toString() {
				return new StringBuilder().append(getVendorId()).append(":").append(getApplicationId()).append(" [")
				.append(getApplication().getDisplayName()).append("]").toString();
			}
			
		};
	}


	
	public static void updateHeaderInfoAVPs(DiameterPacket diameterPacket) {
		
		IDiameterAVP diameterAVP = diameterPacket.getInfoAVP(DiameterAVPConstants.EC_COMMAND_FLAGES);
		if(diameterAVP != null && diameterAVP.isGrouped() && Collectionz.isNullOrEmpty(diameterAVP.getGroupedAvp())){
			AvpGrouped avpGrouped = (AvpGrouped)diameterAVP;
			updateHeaderRequestBitInfoAVP(diameterPacket, avpGrouped);
			updateHeaderProxyBitInfoAVP(diameterPacket, avpGrouped);
			updateHeaderErrorBitInfoAVP(diameterPacket, avpGrouped);
			updateHeaderReTransmitedBitInfoAVP(diameterPacket, avpGrouped);
		}
		updateHeaderCommandCodeInfoAVP(diameterPacket);
		updateHeaderApplicationInfoAVP(diameterPacket);
		
		diameterAVP = diameterPacket.getInfoAVP(DiameterAVPConstants.EC_HOP_BY_HOP_IDENTIFIER);
		if(diameterAVP != null){
			diameterAVP.setValueBytes(DiameterUtility.intToByteArray(diameterPacket.getHop_by_hopIdentifier()));
		}
		
		diameterAVP = diameterPacket.getInfoAVP(DiameterAVPConstants.EC_END_TO_END_IDENTIFIER);
		if(diameterAVP != null){			
			diameterAVP.setValueBytes(DiameterUtility.intToByteArray(diameterPacket.getEnd_to_endIdentifier()));
		}
	}

	public static void updateHeaderReTransmitedBitInfoAVP(
			DiameterPacket diameterPacket, AvpGrouped avpGrouped) {
		IDiameterAVP subAvp = avpGrouped.getSubAttribute(DiameterAVPConstants.EC_RE_TRANSMITTED);
		if(subAvp!=null){
			subAvp.setInteger(booleanToInt(diameterPacket.isReTransmitted()));
		}
	}

	public static void updateHeaderErrorBitInfoAVP(
			DiameterPacket diameterPacket, AvpGrouped avpGrouped) {
		IDiameterAVP subAvp = avpGrouped.getSubAttribute(DiameterAVPConstants.EC_ERROR);
		if(subAvp!=null){
			subAvp.setInteger(booleanToInt(diameterPacket.isError()));
		}
	}

	public static void updateHeaderProxyBitInfoAVP(
			DiameterPacket diameterPacket, AvpGrouped avpGrouped) {
		IDiameterAVP subAvp = avpGrouped.getSubAttribute(DiameterAVPConstants.EC_PROXY);
		if(subAvp!=null){
			subAvp.setInteger(booleanToInt(diameterPacket.isProxiable()));
		}
	}
	
	public static int booleanToInt(boolean flag) {
		return flag ? 1 : 0;
	}

	public static void updateHeaderRequestBitInfoAVP(
			DiameterPacket diameterPacket, AvpGrouped avpGrouped) {
		IDiameterAVP subAvp = avpGrouped.getSubAttribute(DiameterAVPConstants.EC_REQUEST);
		if(subAvp!=null){
			subAvp.setInteger(booleanToInt(diameterPacket.isRequest()));
		}
	}

	public static void updateHeaderCommandCodeInfoAVP(
			DiameterPacket diameterPacket) {
		IDiameterAVP diameterAVP;
		diameterAVP = diameterPacket.getInfoAVP(DiameterAVPConstants.EC_COMMAND_CODE);
		if(diameterAVP != null){
			diameterAVP.setStringValue(String.valueOf(diameterPacket.getCommandCode()));
		}
	}

	public static void updateHeaderApplicationInfoAVP(
			DiameterPacket diameterPacket) {
		IDiameterAVP diameterAVP = diameterPacket.getInfoAVP(DiameterAVPConstants.EC_APPLICATION_ID);
		if(diameterAVP != null){
			diameterAVP.setStringValue(String.valueOf(diameterPacket.getApplicationID()));
		}
	}
	
	/**
	 * Adds Failed-AVP if available in Dictionary
	 * 
	 * @param answer to which failed AVP is added
	 * @param offendingAvp
	 */
	public static void addFailedAVP (@Nonnull DiameterAnswer answer, @Nonnull IDiameterAVP offendingAvp) {
		AvpGrouped failedAVP = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.FAILED_AVP);
		if (failedAVP == null) {
			return;
		}
		failedAVP.addSubAvp(offendingAvp);
		answer.addAvp(failedAVP);
	}
	
	public static void addFailedAVPList (@Nonnull DiameterAnswer answer, @Nonnull List<IDiameterAVP> offendingAvps) {
		AvpGrouped failedAVP = (AvpGrouped) answer.getAVP(DiameterAVPConstants.FAILED_AVP);
		if (failedAVP == null) {
			failedAVP = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.FAILED_AVP);
			if (failedAVP == null) {
				return;
			} else {
				answer.addAvp(failedAVP);
			}
		}
		failedAVP.addSubAvps(offendingAvps);
	}
		
	

	public static DiameterAnswer newAnswerWithHeaderOf(DiameterRequest request) {
		DiameterAnswer diameterAnswer = new DiameterAnswer();
		diameterAnswer.setHeaderFrom(request);
		return diameterAnswer;
	}

	public static String formatMsisdn(String msisdn, int msisdnLength, String mcc) {
		if(msisdn == null) {
			return null;
		}
		//TODO verify with devang -monica.lulla
		int mncIndex = msisdn.length() - msisdnLength;
		if (mncIndex < 0) {
			return null;
		}
		if (mncIndex == 0) {
			return msisdn;
		}
		if (mcc == null){
			return msisdn.substring(mncIndex);
		}
		int mccIndex = mncIndex - mcc.length();
		if (mccIndex < 0) {
			return null;
		}
		if (msisdn.regionMatches(mccIndex, mcc, 0, mcc.length())) {
			return msisdn.substring(mncIndex);
		}
		return null;
	}

	public static String getIMSIFromIdentity(String identity){
		if (identity == null){
			return identity;
		}
		String imsi = null, domain = null, msin = identity;
		try {
			if (identity.indexOf('@') != -1){
				msin = identity.substring(0,identity.indexOf('@'));
				domain = identity.substring(identity.indexOf('@')+ 1 );
			}

			if (msin.length() == 16 && UserIdentityPrefixTypes.isPrefixed(msin)){
				imsi = msin.substring(1);
			} else if (msin.length() < 11 && domain != null){
				String mcc = "";
				String mnc = "";
				int mccIndex = domain.indexOf("mcc");
				if (mccIndex!=-1){
					mcc = domain.substring(mccIndex + 3, mccIndex + 6);
				}
				int mncIndex = domain.indexOf("mnc");
				if (mncIndex != -1){
					mnc = domain.substring(mncIndex + 3, mncIndex  +6);
					if (mnc.startsWith("0")){
						mnc = mnc.substring(1);
					}
				}
				imsi = mcc + mnc + msin;
				if (imsi.length() < 15){
					int noOfZeros = 15 - imsi.length();
					imsi = mcc + mnc;
					for (int i=0 ; i<noOfZeros ; i++){
						imsi += "0";
					}
					imsi += msin;
				}
			} else {
				imsi = msin;
			}
		} catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Failed to generate IMSI from Identity: " + identity + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return identity;
		}
		return imsi;
	}

	public enum UserIdentityPrefixTypes {

		AKA_PERMANENT("0"),
		SIM_PERMANENT("1"),
		AKA_PRIME_PERMANENT("6");

		public final String identifier;

		private UserIdentityPrefixTypes(String identifier) {
			this.identifier = identifier;
		}

		public static boolean isPrefixed(String userIdentity){
			if(Strings.isNullOrBlank(userIdentity)){
				return false;
			}
			return userIdentity.startsWith(AKA_PERMANENT.identifier) ||
					userIdentity.startsWith(SIM_PERMANENT.identifier) ||
					userIdentity.startsWith(AKA_PRIME_PERMANENT.identifier);
		}

	}

}
