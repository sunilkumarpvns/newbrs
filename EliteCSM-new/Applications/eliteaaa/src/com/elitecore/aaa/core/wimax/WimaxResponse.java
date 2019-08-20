package com.elitecore.aaa.core.wimax;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.ResultCode;

public class WimaxResponse {
	private String AAA_Session_ID;
	private byte[] MSK;
	private String DNS;
	private Long sessionTimeoutInSeconds;
	private boolean bFurtherProcessingRequired = true;
	private String responseMessage;
	private List<byte[]> DNSList;
	private DHCP dhcp;
	private HA ha;
	private FA fa;
	private WimaxCapabilities wimaxCapabilities;
	private String sourceIP;
	private ClientData clientData;
	private ResultCode resultCode;
	private String CUI;
	
	public String getCUI() {
		return CUI;
	}

	public void setCUI(String CUI) {
		this.CUI = CUI;
	}

	public ResultCode getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}

	public String getSourceIP() {
		return this.sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public List<byte[]> getDNSList() {
		return DNSList;
	}

	public void setDNSList(List<byte[]> DNSList) {
		this.DNSList = DNSList;
	}
	
	public WimaxResponse(){
		dhcp = new DHCP();
		ha = new HA();
		fa = new FA();
		wimaxCapabilities = new WimaxCapabilities();
		clientData = new ClientData();
	}
	
	public ClientData getClientData() {
		return clientData;
	}

	public void setClientData(ClientData clientData) {
		this.clientData = clientData;
	}

	public DHCP getDHCP(){
		return dhcp;
	}
	
	public HA getHA(){
		return ha;
	}
	
	public FA getFA(){
		return fa;
	}
	
	public WimaxCapabilities getWimaxCapabilities(){
		return wimaxCapabilities;
	}
	
	public class DHCP{
		
		private byte[] DHCP_RK;
		private Integer DHCP_RK_KEY_ID;
		private long DHCP_RK_LIFETIME;
		private String DHCP_Server;
		
		public byte[] getDHCP_RK() {
			return this.DHCP_RK;
		}
		public void setDHCP_RK(byte[] DHCP_RK) {
			this.DHCP_RK = DHCP_RK;
		}
		
		public Integer getDHCP_RK_KEY_ID() {
			return this.DHCP_RK_KEY_ID;
		}
		public void setDHCP_RK_KEY_ID(int DHCP_RK_KEY_ID) {
			this.DHCP_RK_KEY_ID = DHCP_RK_KEY_ID;
		}
		
		public Long getDHCP_RK_LIFETIME() {
			return this.DHCP_RK_LIFETIME;
		}
		public void setDHCP_RK_LIFETIME(long DHCP_RK_LIFETIME) {
			this.DHCP_RK_LIFETIME = DHCP_RK_LIFETIME;
		}
		
		public String getDHCP_Server() {
			return this.DHCP_Server;
		}
		public void setDHCP_Server(String DHCP_Server) {
			this.DHCP_Server = DHCP_Server;
		}
	}
	
	public class HA{
		
		private byte[] HA_RK_KEY;
		private Integer HA_RK_SPI;
		private Long HA_RK_LIFETIME;
		private byte[] MN_HA_MIP4_KEY;
		private Long MN_HA_MIP4_SPI;
		private String HA_IP_MIP4;
		
		public byte[] getHA_RK_KEY() {
			return this.HA_RK_KEY;
		}
		public void setHA_RK_KEY(byte[] HA_RK_KEY) {
			this.HA_RK_KEY = HA_RK_KEY;
		}
		public Integer getHA_RK_SPI() {
			return this.HA_RK_SPI;
		}
		public void setHA_RK_SPI(int HA_RK_SPI) {
			this.HA_RK_SPI = HA_RK_SPI;
		}
		public Long getHA_RK_LIFETIME() {
			return this.HA_RK_LIFETIME;
		}
		public void setHA_RK_LIFETIME(long HA_RK_LIFETIME) {
			this.HA_RK_LIFETIME = HA_RK_LIFETIME;
		}
		public byte[] getMN_HA_MIP4_KEY() {
			return this.MN_HA_MIP4_KEY;
		}
		public void setMN_HA_MIP4_KEY(byte[] MN_HA_MIP4_KEY) {
			this.MN_HA_MIP4_KEY = MN_HA_MIP4_KEY;
		}
		public Long getMN_HA_MIP4_SPI() {
			return this.MN_HA_MIP4_SPI;
		}
		public void setMN_HA_MIP4_SPI(long MN_HA_MIP4_SPI) {
			this.MN_HA_MIP4_SPI = MN_HA_MIP4_SPI;
		}
		public String getHA_IP_MIP4() {
			return this.HA_IP_MIP4;
		}
		public void setHA_IP_MIP4(String HA_IP_MIP4) {
			this.HA_IP_MIP4 = HA_IP_MIP4;
		}
		public String getHAAddress(){
			return this.HA_IP_MIP4;
		}
	}
	
	public class FA{
		private byte[] FA_RK_KEY;
		private Long FA_RK_SPI;
		
		public byte[] getFA_RK_KEY() {
			return this.FA_RK_KEY;
		}
		public void setFA_RK_KEY(byte[] FA_RK_KEY) {
			this.FA_RK_KEY = FA_RK_KEY;
		}
		public Long getFA_RK_SPI() {
			return this.FA_RK_SPI;
		}
		public void setFA_RK_SPI(long FA_RK_SPI) {
			this.FA_RK_SPI = FA_RK_SPI;
		}
	}
	
	public class WimaxCapabilities{
		private byte[] wimaxRelease;
		private Integer accountingCapabilities = 1;
		private Integer hotliningCapabilities = null;
		private Integer idleModeNotificationCapabilities = 0;
		
		public byte[] getWimaxRelease() {
			return this.wimaxRelease;
		}
		public void setWimaxRelease(byte[] wimaxRelease) {
			this.wimaxRelease = wimaxRelease;
		}
		public Integer getAccountingCapabilities() {
			return this.accountingCapabilities;
		}
		public void setAccountingCapabilities(int accountingCapabilities) {
			this.accountingCapabilities = accountingCapabilities;
		}
		public Integer getHotliningCapabilities() {
			return this.hotliningCapabilities;
		}
		public void setHotliningCapabilities(int hotliningCapabilities) {
			this.hotliningCapabilities = hotliningCapabilities;
		}
		public Integer getIdleModeNotificationCapabilities() {
			return this.idleModeNotificationCapabilities;
		}
		public void setIdleModeNotificationCapabilities(int idleModeNotificationCapabilities) {
			this.idleModeNotificationCapabilities = idleModeNotificationCapabilities;
		}
		
	}
	
	public class ClientData{
		private String sharedSecret;
		private String HAAddress = CommonConstants.RESERVED_IPV_4_ADDRESS;
		private String DHCPAddress = CommonConstants.RESERVED_IPV_4_ADDRESS;
		private List<byte[]> dnsList = null; 
		
		public String getSharedSecret() {
			return sharedSecret;
		}
		public void setSharedSecret(String sharedSecret) {
			this.sharedSecret = sharedSecret;
		}
		public String getHAAddress() {
			return HAAddress;
		}
		public void setHAAddress(String hAAddress) {
			HAAddress = hAAddress;
		}
		public String getDHCPAddress() {
			return DHCPAddress;
		}
		public void setDHCPAddress(String dHCPAddress) {
			DHCPAddress = dHCPAddress;
		}
		
		
		public List<byte[]> getDNSList(){
			return dnsList;
		}
		public void setDNSList(List<byte[]> dnsList){
			this.dnsList = dnsList;
		}
		
	}
	
	public String getAAA_Session_ID() {
		return this.AAA_Session_ID;
	}

	public void setAAA_Session_ID(String AAA_Session_ID) {
		this.AAA_Session_ID = AAA_Session_ID;
	}

	public byte[] getMSK() {
		return this.MSK;
	}

	public void setMSK(byte[] MSK) {
		this.MSK = MSK;
	}
	
	public String getDNS() {
		return this.DNS;
	}

	public void setDNS(String DNS) {
		this.DNS = DNS;
	}

	

	public Long getSessionTimeoutInSeconds() {
		return this.sessionTimeoutInSeconds;
	}

	public void setSessionTimeoutInSeconds(long sessionTimeoutInSeconds) {
		this.sessionTimeoutInSeconds = sessionTimeoutInSeconds;
	}

	public void setResponseMessage(String responseMessage){
		this.responseMessage = responseMessage;
	}
	public String getResponseMessage(){
		return this.responseMessage;
	}
	
	public void setFurtherProcessingRequired(boolean bProcessingRequired){
		this.bFurtherProcessingRequired = bProcessingRequired;
	}
	public boolean isFurtherProcessingRequired(){
		return this.bFurtherProcessingRequired;
	}
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.println();
		out.print("----------WIMAX RESPONSE----------- ");
		out.println();
		out.print((getAAA_Session_ID()!=null)?("AAA-Session-ID  						: " + getAAA_Session_ID()+"\n"):"");
		out.print((getCUI()!=null)?("CUI  						: " + getCUI()+"\n"):"");
		out.print((getSourceIP()!=null)?("Source IP       						: " + getSourceIP()+"\n"):"");
		out.print((getDNS()!=null)?("DNS             						: " + getDNS()+"\n"):"");
		out.print((getSessionTimeoutInSeconds()!=null)?("Session Timeout 					: " + getSessionTimeoutInSeconds()+"\n"):"");
		out.print((getResponseMessage()!=null)?("Response Message						: " +  getResponseMessage()+"\n"):"");
		out.print((getDHCP().getDHCP_Server()!=null)?("DHCP Server     		 			: " + getDHCP().getDHCP_Server()+"\n"):"");
		out.print((getDHCP().getDHCP_RK_KEY_ID()!=null)?("DHCP_RK_KEY_ID         				: " + getDHCP().getDHCP_RK_KEY_ID()+"\n"):"");
		out.print((getDHCP().getDHCP_RK_LIFETIME()!=null)?("DHCP_RK_LIFETIME       				: " + getDHCP().getDHCP_RK_LIFETIME()+"\n"):"");
		out.print((getDHCP().getDHCP_RK()!=null)?("DHCP_RK                				: " + RadiusUtility.bytesToHex(getDHCP().getDHCP_RK())+"\n"):"");
		out.print((getFA().getFA_RK_SPI()!=null)?("FA_RK_SPI              				: " + getFA().getFA_RK_SPI()+"\n"):"");
		out.print((getFA().getFA_RK_KEY()!=null)?("FA_RK_KEY              				: " + RadiusUtility.bytesToHex(getFA().getFA_RK_KEY())+"\n"):"");
		out.print((getHA().getHA_IP_MIP4()!=null)?("HA_IP_MIP4             				: " + getHA().getHA_IP_MIP4()+"\n"):"");
		out.print((getHA().getHAAddress()!=null)?("HA Address             				: " + getHA().getHAAddress()+"\n"):"");
		out.print((getHA().getHA_RK_LIFETIME()!=null)?("HA_RK_LIFETIME         				: " + getHA().getHA_RK_LIFETIME()+"\n"):"");
		out.print((getHA().getHA_RK_SPI()!=null)?("HA_RK_SPI              				: " + getHA().getHA_RK_SPI()+"\n"):"");
		out.print((getHA().getMN_HA_MIP4_SPI()!=null)?("MN_HA_MIP4_SPI        			 	: " + getHA().getMN_HA_MIP4_SPI()+"\n"):"");
		out.print((getHA().getHA_RK_KEY()!=null)?("HA_RK_KEY              				: " + RadiusUtility.bytesToHex(getHA().getHA_RK_KEY())+"\n"):"");
		out.print((getHA().getMN_HA_MIP4_KEY()!=null)?("MN_HA_MIP4_KEY         				: " + RadiusUtility.bytesToHex(getHA().getMN_HA_MIP4_KEY())+"\n"):"");
		out.print((getMSK()!=null)?("MSK                   				: " + RadiusUtility.bytesToHex(getMSK())+"\n"):"");
		out.print((getWimaxCapabilities().getWimaxRelease()!=null)?("Wimax Release     				  	: " + getWimaxCapabilities().getWimaxRelease()+"\n"):"");
		out.print((getWimaxCapabilities().getAccountingCapabilities()!=null)?("Accounting Capabilities				: " + getWimaxCapabilities().getAccountingCapabilities()+"\n"):"");
		out.print((getWimaxCapabilities().getHotliningCapabilities()!=null)?("Hotlining Capabilities 				: " + getWimaxCapabilities().getHotliningCapabilities()+"\n"):"");
		out.print((getWimaxCapabilities().getIdleModeNotificationCapabilities()!=null)?("Idle Mode Notification Capabilities	: " + getWimaxCapabilities().getIdleModeNotificationCapabilities()+"\n"):"");
		
		
		out.flush();
		out.close();
		return stringBuffer.toString();
	}
	
	public String getOctetsToString(byte[] valueBytes){
		return octetsToString(valueBytes).toString();
	}
	
	private String octetsToString(byte[] valueBytes){
		StringBuilder strBuilder = new StringBuilder();
		
			byte[] saltBytes = new byte[2];
			byte[] keyBytes = new byte[valueBytes.length-2];
			if(valueBytes != null && valueBytes.length > 2){
				System.arraycopy(valueBytes, 0, saltBytes, 0, saltBytes.length);
				System.arraycopy(valueBytes, 2, keyBytes, 0, valueBytes.length-2);
			}
			strBuilder.append("Salt = ");
			strBuilder.append(bytesToHex(saltBytes));
			strBuilder.append(" Value = ");
			strBuilder.append(bytesToHex(keyBytes));	
		return strBuilder.toString();
	}

	public static String bytesToHex(byte buf[]){
       if(buf == null)
           return "";
       else
           return bytesToHex(buf, 0, buf.length);
	}

	public static String bytesToHex(byte buf[], int offset, int limit){
	   char[] hexbuf = new char[(((limit - offset)*2) + 2)];
	   hexbuf[0]='0';
	   hexbuf[1]='x';
	   for(int i = offset,k=2; i < limit; i++){
	       hexbuf[k++] = (HEX[buf[i] >> 4 & 0xf]);
	       hexbuf[k++] = (HEX[buf[i] & 0xf]);
	   }
	   return String.valueOf(hexbuf);
	}
	
	private static final char HEX[] = {
		   '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		   'a', 'b', 'c', 'd', 'e', 'f'
		};
	
}
