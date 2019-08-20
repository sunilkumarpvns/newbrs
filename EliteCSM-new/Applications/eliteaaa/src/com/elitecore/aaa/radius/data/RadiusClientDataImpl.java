package com.elitecore.aaa.radius.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.elitecore.aaa.core.data.Vendor;
import com.elitecore.aaa.radius.conf.impl.ClientsConfigurable.ClientDetail;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.net.AddressResolver;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadiusClientDataImpl implements RadClientData {
	private static final String MODULE = "RADIUS CLIENT DATA";
	private String strClientIp = null;
	private String strSharedSecret;
	private String authSharedSecret = "";
	private String acctSharedSecret = "";
	private String dynaAuthSharedSecret = "";
	private String strFramedPoolName=null;
	private Long lTimeout = -1L;
	private RadiusClientProfile clientProfile = null;

	/*
	 * These variables are used when the client type is a network client
	 * in which the configuration is containing subnet mask
	 */
	private Integer subnetMask = null;
	private Long startAddress = null;
	private Long endAddress = null;

	private RadiusClientDataImpl(){
		//suppressing the use of the default constructor
	}

	private RadiusClientDataImpl(String strClientIP, String strSharedSecret, String strFramedPoolName, Long lTimeout, RadiusClientProfile profile,Integer mask,Long startAddress,Long endAddress){
		this.strClientIp = strClientIP;
		this.strSharedSecret = strSharedSecret;
		this.strFramedPoolName = strFramedPoolName;
		this.lTimeout = lTimeout;
		this.clientProfile = profile;
		this.subnetMask = mask;
		this.startAddress = startAddress;
		this.endAddress = endAddress;
		parseSharedSecret(strSharedSecret);
	}

	public String getClientIp() {
		return this.strClientIp;
	}	

	public Long getTimeout() {
		return lTimeout;
	}

	public String getSharedSecret(int packetType) {
		switch (packetType) {
		case RadiusConstants.ACCESS_REQUEST_MESSAGE:
		case RadiusConstants.ACCESS_ACCEPT_MESSAGE:
		case RadiusConstants.ACCESS_CHALLENGE_MESSAGE:
		case RadiusConstants.ACCESS_REJECT_MESSAGE:
			return authSharedSecret;
		case RadiusConstants.ACCOUNTING_MESSAGE:
		case RadiusConstants.ACCOUNTING_REQUEST_MESSAGE:
		case RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE:
			return acctSharedSecret;
		case RadiusConstants.COA_REQUEST_MESSAGE:
		case RadiusConstants.COA_ACK_MESSAGE:
		case RadiusConstants.COA_NAK_MESSAGE:
		case RadiusConstants.DISCONNECTION_REQUEST_MESSAGE:
		case RadiusConstants.DISCONNECTION_ACK_MESSAGE:
		case RadiusConstants.DISCONNECTION_NAK_MESSAGE:
			return dynaAuthSharedSecret;
		default:
			return authSharedSecret;
		}
	}
	public String getFramedPoolName() {
		return strFramedPoolName;
	}

	public String getProfileName() {
		return clientProfile.getProfileName();
	}
	public String getClientPolicy() {
		return clientProfile.getClientPolicy();
	}	
	public String getDHCPAddress() {
		return clientProfile.getDHCPAddress();
	}

	public String getHAAddress() {
		return clientProfile.getHAAddress();
	}

	public String getHotlinePolicy() {
		return clientProfile.getHotlinePolicy();
	}

	public Integer getVendorType() {
		return clientProfile.getVendorType();
	}

	public String getPrepaidStandard() {
		return clientProfile.getPrepaidStandard();
	}

	public String getVendorName() {
		return clientProfile.getVendorName();
	}

	public List<byte[]> getDnsList() {
		return clientProfile.getDnsList();
	}

	public List<Vendor> getSupportedVendorsList() {
		return clientProfile.getSupportedVendorsList();
	}

	public List<String> getUserIdentities() {
		return clientProfile.getUserIdentityList();
	}	

	public Long getVendorId() {
		return clientProfile.getVendorId();
	}


	public boolean isSupportedVendorId(long vendorId) {
		return clientProfile.isSupportedVendorId(vendorId);
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Client Ip           : " + strClientIp);
		out.println("Shared Secret       : " + ((strSharedSecret!=null)?strSharedSecret:""));
		out.println("Timeout             : " + lTimeout);
		out.println("Client Profile      : " + ((clientProfile!=null)? "\n" + clientProfile: ""));

		return stringBuffer.toString();
	}
	@Override
	public List<IRadiusAttribute> get3GPP2DnsList() {
		return clientProfile.getDns3gpp2List();
	}
	@Override
	public List<IRadiusAttribute> getWimaxDnsList() {
		return clientProfile.getWimaxDnsList();
	}
	@Override
	public boolean isMultipleClassAttributeSupported() {
		return clientProfile.getIsMultipleClassAttributeSupported();
	}
	@Override
	public boolean isFilterUnsupportedVSA() {
		return clientProfile.getIsFilterUnsupportedVSA();
	}

	/**
	 * This method is a static factory method which parses the client IP sent in the argument and returns client instances
	 * 
	 * @param strClientIP can be (,) separated value or a range of IPs (-) separated or can have a subnetwork mask with it
	 * @param strSharedSecret
	 * @param strFramedPoolName
	 * @param lTimeout
	 * @param profile
	 * @return List of client data based on client IP if it is comma separated or (-) separated. Returned List may be Immutable.
	 * <code>null</code> if the client IP syntax is invalid
	 */
	public static List<RadClientData> parseClientIP(ClientDetail client, RadiusClientProfile profile) {
		return parseClientIP(client, profile, AddressResolver.defaultAddressResolver());
	}

	public static List<RadClientData> parseClientIP(ClientDetail client, RadiusClientProfile profile, AddressResolver addressResolver) {

		String strClientIP = client.getStrClientIp();
		String strSharedSecret = client.getStrSharedSecret();
		String strFramedPoolName = client.getStrFramedPoolName();
		Long lTimeout = client.getlTimeout();

		if(strClientIP == null){
			return Collections.emptyList();
		}
		List<RadClientData> clientDataList = new ArrayList<RadClientData>();
		StringTokenizer stk = new StringTokenizer(strClientIP, ",");
		while(stk.hasMoreTokens()){
			String ip = stk.nextToken().trim();
			if(ip.length() == 0){
				continue;
			}
			try {
				if(ip.contains("-")){
					createClientDataFromIPRange(ip, strSharedSecret, strFramedPoolName, lTimeout, profile, clientDataList, addressResolver);
				}else if(ip.contains("/")){
					createClientDataFromMaskedAddress(ip, strSharedSecret, strFramedPoolName, lTimeout, profile, clientDataList, addressResolver);
				}else{
					createClientDataFromHostName(profile, strSharedSecret, strFramedPoolName, lTimeout, clientDataList, ip, addressResolver);
				}
			} catch(UnknownHostException ex){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Client IP: " + ip + " is invalid, Reason: " + ex.getMessage());
				}
				LogManager.getLogger().trace(MODULE, ex);
			}
		}
		return clientDataList;
	}
	
	private static void createClientDataFromHostName(RadiusClientProfile profile,
			String strSharedSecret, String strFramedPoolName, Long lTimeout,
			List<RadClientData> clientDataList, String ip, AddressResolver addressResolver)
					throws UnknownHostException {
		
		InetAddress[] addresses = addressResolver.allByName(ip);
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Hostname: " + ip + " resolved IPs: " + Arrays.toString(addresses));
		}
		
		for ( InetAddress address : addresses) {
			clientDataList.add(new RadiusClientDataImpl(address.getHostAddress(),strSharedSecret,strFramedPoolName,lTimeout,profile,null,null,null));
		}
	}

	private static void createClientDataFromIPRange(String strClientIP, String strSharedSecret, String strFramedPoolName, Long lTimeout, RadiusClientProfile profile, List<RadClientData> clientDataList, AddressResolver addressResolver) throws UnknownHostException{
		String[] ips = strClientIP.split("-");
		if(ips.length <= 1 || ips.length > 2){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Client IP: " + strClientIP + " is invalid, Reason: IP rang must contain only two IP address");
			}
			return;
		}
		InetAddress ip1 = addressResolver.byName(ips[0].trim());
		InetAddress ip2 = addressResolver.byName(ips[1].trim());
		Long longIP1 = RadiusUtility.bytesToLong(ip1.getAddress());
		Long longIP2 = RadiusUtility.bytesToLong(ip2.getAddress());
		if(longIP1 != null && longIP2 != null){

			if(longIP2 <= longIP1){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Client IP: " + strClientIP + " is invalid, Reason: In Ip rang end address (" + ips[1] + ") must be greater than start address (" + ips[0] + ")");
				}
				return;
			}

			if(longIP2 > longIP1){
				byte[] b = new byte[4];
				InetAddress add;
				for(long i=longIP1;i<=longIP2;i++){
					b[0] = (byte) ((i >> 24) & 0xFF);
					b[1] = (byte) ((i >> 16) & 0xFF);
					b[2] = (byte) ((i >> 8) & 0xFF);
					b[3] = (byte) (i & 0xFF);
					add = addressResolver.byAddress(b);
					clientDataList.add(new RadiusClientDataImpl(add.getHostAddress(),strSharedSecret,strFramedPoolName,lTimeout,profile,null,null,null));
				}
			}
		}
	}

	private static void createClientDataFromMaskedAddress(String strClientIP, String strSharedSecret, String strFramedPoolName, Long lTimeout, RadiusClientProfile profile, List<RadClientData> clientDataList, AddressResolver addressResolver) throws UnknownHostException{
		String[] ipAndMask = strClientIP.split("/");
		if(ipAndMask.length != 2){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Client IP: " + strClientIP + " is invalid");
			}
			return;
		}

		if(ipAndMask[0].trim().length() > 0){
			try{
				int subNet = Integer.parseInt(ipAndMask[1].trim());
				if(subNet >= 32){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "Client IP: " + strClientIP + " is invalid, Reason: Subnet mask should be less than 32");
					}
					return;
				}

				InetAddress address = addressResolver.byName(ipAndMask[0].trim());
				byte[] addressBytes = address.getAddress();
				Long longAddress = RadiusUtility.bytesToLong(addressBytes);

				if(longAddress == null){
					throw new UnknownHostException();
				}

				int clientPart = 32-subNet;
				Long lMask = (long) ((1<<clientPart) -1);
				lMask = (~lMask) & 0x00000000FFFFFFFFL;
				Long startAddress = longAddress & lMask;
				lMask = (long) ((1<<clientPart) -1);
				Long endAddress = startAddress | lMask;
				clientDataList.add(new RadiusClientDataImpl(address.getHostAddress(), strSharedSecret, strFramedPoolName, lTimeout, profile,subNet,startAddress,endAddress));
			}catch (NumberFormatException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Subnet mask for client IP: " + strClientIP + " is invalid.");
				}
				LogManager.getLogger().trace(MODULE, e);
			}
		}
	}

	private void parseSharedSecret(String strSharedSecret) {
		try {
			/*
			 * spaces are added to keep shared secret blank when the last character of strSharedSecret in configuration is comma or semicolon 
			 * and not to assign the last service specific secret. 
			 */
			strSharedSecret = " " + strSharedSecret + " ";
			String[] secrets =  ParserUtility.splitString(strSharedSecret, ',', ';');

			if(secrets != null && secrets.length > 0) {
				this.authSharedSecret = secrets[0].trim();
				if(secrets.length == 1 && secrets[0] != null) {
					this.acctSharedSecret = secrets[0].trim();
					this.dynaAuthSharedSecret = secrets[0].trim();
				} else if(secrets.length == 2 && secrets[1] != null) {
					this.acctSharedSecret = secrets[1].trim();
					this.dynaAuthSharedSecret = secrets[1].trim();
				} else {
					if(secrets[1] != null)
						this.acctSharedSecret = secrets[1].trim();
					if(secrets[2] != null)
						this.dynaAuthSharedSecret = secrets[2].trim();	
				} 
			}
		} catch (Exception e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) 
				LogManager.getLogger().trace(MODULE, e.getLocalizedMessage());
		}
	}

	@Override
	public Integer getSubnetMask() {
		return this.subnetMask;
	}

	@Override
	public Long getStartAddress() {
		return this.startAddress;
	}

	@Override
	public Long getEndAddress() {
		return this.endAddress;
	}

	@Override
	public boolean containsNetworkAddress() {
		return (subnetMask != null);
	}

	@Override
	public Optional<Integer> getDynauthPort() {
		return clientProfile.getDynauthPort();
	}

	@Override
	public String getSupportedAttributesStrCOA() {
		return clientProfile.getSupportedAttributeStrCOA();
	}

	@Override
	public String getUnsupportedAttributesStrCOA() {
		return clientProfile.getUnsupportedAttributeStrCOA();
	}

	@Override
	public String getSupportedAttributesStrDM() {
		return clientProfile.getSupportedAttributeStrDM();
	}

	@Override
	public String getUnsupportedAttributesStrDM() {
		return clientProfile.getUnsupportedAttributeStrDM();
	}
}
