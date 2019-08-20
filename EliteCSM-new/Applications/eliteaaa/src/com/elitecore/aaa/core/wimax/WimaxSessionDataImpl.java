package com.elitecore.aaa.core.wimax;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class WimaxSessionDataImpl implements WimaxSessionData,Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String aaaSessionId = null;
	private byte[] ha_salt;
	private byte[] dhcp_salt;
	private byte[] ha_rk_key;
	private int ha_rk_spi;
	private long ha_rk_lifetime_in_seconds;
	private long ha_rk_generation_time_in_millis;
	private byte[] dhcp_rk;
	private int dhcp_rk_id;
	private long dhcp_rk_lifetime_in_seconds;
	private long dhcp_rk_generation_time;
	private String DHCPV4_SERVER;
	private byte[] mn_ha_key;
	private byte[] mn_salt;
	private long mn_ha_spi;
	private byte[] MSK;
	private byte[] EMSK;
	private String HA_IP_MIP4;
	private byte[] FA_RK_KEY;
	private long FA_RK_SPI;
	private String callingStationId = null;
	private String nasIp = null;
	private String nasIdentifier = null;
	private String nasPort = null;
	private String nasPortType = null;
	private String nasPortId = null;
	private long defaultSessionTimeoutInSeconds = 0;
	private String framedIPAddress;
	private long lastAccessTime;	// in milliseconds
	private long sessionLifetime;	// in milliseconds
	private String eapIdentity;
	private Integer hotliningCapabilities;
	private Integer accountingCapabilities;
	private byte[] wimaxRelease;
	private Integer idleModeNotificationCapabilities;
		
	private String CUI;
	
	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public long getSessionLifetime() {
		return sessionLifetime;
	}

	public void setSessionLifetime(long sessionLifetime) {
		this.sessionLifetime = sessionLifetime;
	}

	public long getDefaultSessionTimeoutInSeconds() {
		return defaultSessionTimeoutInSeconds;
	}

	public void setDefaultSessionTimeoutInSeconds(long defaultSessionTimeoutInSeconds) {
		this.defaultSessionTimeoutInSeconds = defaultSessionTimeoutInSeconds;
	}

	public String getStringAAASessionId(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(this.aaaSessionId);
		return strBuilder.toString();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAaaSessionId() {
		return aaaSessionId;
	}

	public void setAaaSessionId(String aaaSessionId) {
		this.aaaSessionId = aaaSessionId;
	}

	public void setHA_Salt(byte[] saltBytes) {
		this.ha_salt = saltBytes;
	}

	public void setMN_Salt(byte[] saltBytes) {
		this.mn_salt = saltBytes;
	}

	public String getStringAaaSessionId() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(this.aaaSessionId);
		return strBuilder.toString();
	}

	public void setHA_RK_KEY(byte[] encHA_RK_KEY) {
		this.ha_rk_key = encHA_RK_KEY;
	}

	public void setHA_RK_SPI(int ha_rk_spi) {
		this.ha_rk_spi = ha_rk_spi;
	}

	public void setHA_RK_Lifetime_In_Seconds(long lifetimeInSeconds) {
		this.ha_rk_lifetime_in_seconds = lifetimeInSeconds;
	}
	
	public byte[] getHA_RK_KEY() {
		return ha_rk_key;
	}
	public long getHA_RK_Lifetime_In_Seconds() {
		return ha_rk_lifetime_in_seconds;
	}
	public int getHA_RK_SPI() {
		return ha_rk_spi;
	}

	public int getSalt() {
//		salt = new Random().nextInt(65535);
//		return salt;
		return 32768;
	}

	public byte[] getHa_salt() {
		return ha_salt;
	}

	public void setHa_salt(byte[] ha_salt) {
		this.ha_salt = ha_salt;
	}

	public void setMN_HA_MIP4_KEY(byte[] encMN_HA_MIP4_KEY) {
		this.mn_ha_key = encMN_HA_MIP4_KEY;
	}
	public byte[] getMN_HA_MIP4_KEY() {
		return mn_ha_key;
	}

	public void setMN_HA_SPI(long mn_ha_mip4_spi) {
		this.mn_ha_spi = mn_ha_mip4_spi;
	}
	public long getMN_HA_MIP4_SPI() {
		return mn_ha_spi;
	}

	public byte[] getMn_salt() {
		return mn_salt;
	}

	public void setMn_salt(byte[] mn_salt) {
		this.mn_salt = mn_salt;
	}
	
	public void setMSK(byte[] msk) {
		if(msk != null){
			this.MSK = new byte[msk.length];
			System.arraycopy(msk, 0, this.MSK, 0, msk.length);
		}else{
			throw new IllegalArgumentException("MSK null");
		}		
	}
	public byte[] getMSK() {
		return this.MSK;
	}

	public void setEMSK(byte[] emsk) {
		if(emsk != null){
			this.EMSK = new byte[emsk.length];
			System.arraycopy(emsk, 0, this.EMSK, 0, emsk.length);
		}else{
			throw new IllegalArgumentException("EMSK null");
		}	
	}
	public byte[] getEMSK() {
		return this.EMSK;
	}

	public void setHA_IP_MIP4(String ha_ip_mip4) {
		this.HA_IP_MIP4 = ha_ip_mip4;
	}
	public String getHA_IP_MIP4() {
		return this.HA_IP_MIP4;
	}
//	public String getStringHA_IP_MIP4() {
//		try {
//			return InetAddress.getByAddress(this.HA_IP_MIP4).getHostAddress();
//		} catch (UnknownHostException e) {
//			return "0.0.0.0";
//		}
//	}
	public byte[] getHaIpAddressInBytes(){
		try {
			return InetAddress.getByName(this.HA_IP_MIP4).getAddress();
		} catch (UnknownHostException e) {
			LogManager.getLogger().trace("WimaxSession", "Cannot convert String Ha-Ip-Address to bytes.");
			LogManager.getLogger().warn("WimaxSession", "So setting default value to 0.0.0.0");
			return new byte[4];
		}		
	}
	
	public void setFA_RK_KEY(byte[] fa_rk_key) {
		this.FA_RK_KEY = fa_rk_key;		
	}
	
	public byte[] getFA_RK_KEY() {
		return FA_RK_KEY;
	}

	public void setFA_RK_SPI(long fa_rk_spi) {
		this.FA_RK_SPI = fa_rk_spi;		
	}

	public long getFA_RK_SPI() {		
		return FA_RK_SPI;
	}

	public void setCallingStationId(String strCallingStationId) {
		this.callingStationId = strCallingStationId;
	}
	
	public String getCallingStationId(){
		return this.callingStationId;
	}

	public void setNasIP(String nasIP) {
		this.nasIp = nasIP;
	}
	
	public String getNasIP(){
		return this.nasIp;
	}
	
	public void setNasIdentifier(String nasIdentifier) {
		this.nasIdentifier = nasIdentifier;
	}
	
	public String getNasIdentifier(){
		return this.nasIdentifier ;
	}
	
	public void setNasPort(String nasPort) {
		this.nasPort = nasPort;
	}
	
	public String getNasPort(){
		return this.nasPort  ;
	}
	
	public void setNasPortType(String nasPortType) {
		this.nasPortType  = nasPortType;
	}
	
	public String getNasPortType(){
		return this.nasPortType;
	}
	
	public void setNasPortId(String nasPortId) {
		this.nasPortId  = nasPortId;
	}
	
	public String getNasPortId(){
		return this.nasPortId;
	}
	public String getFramedIPAddress() {
		return framedIPAddress;
	}

	public void setFramedIPAddress(String framedIPAddress) {
		this.framedIPAddress = framedIPAddress;
	}


	public byte[] getDhcp_rk() {
		return dhcp_rk;
	}

	public void setDhcp_rk(byte[] dhcp_rk) {
		this.dhcp_rk = dhcp_rk;
	}

	public int getDhcp_rk_id() {
		return dhcp_rk_id;
	}

	public void setDhcp_rk_id(int dhcp_rk_id) {
		this.dhcp_rk_id = dhcp_rk_id;
	}

	public long getDhcp_rk_lifetime_in_seconds() {
		return dhcp_rk_lifetime_in_seconds;
	}

	public void setDhcp_rk_lifetime_in_seconds(long dhcp_rk_lifetime_in_seconds) {
		this.dhcp_rk_lifetime_in_seconds = dhcp_rk_lifetime_in_seconds;
	}

	
	public String getDHCPV4_SERVER() {
		return DHCPV4_SERVER;
	}

	public void setDHCPV4_SERVER(String dhcpv4_server) {
		DHCPV4_SERVER = dhcpv4_server;
	}
	
	public long getDhcp_rk_generation_time() {
		return dhcp_rk_generation_time;
	}

	public void setDhcp_rk_generation_time_in_millis(long dhcp_rk_generation_time) {
		this.dhcp_rk_generation_time = dhcp_rk_generation_time;
	}
	
	public void setDhcp_Salt(byte[] saltBytes) {
		this.dhcp_salt = saltBytes;
	}
	
	public byte[] getDhcp_salt() {
		return dhcp_salt;
	}

	public String getAAASessionId() {
		return aaaSessionId;
	}
	
	public String getEapIdentity() {
		return eapIdentity;
	}

	public void setEapIdentity(String eapIdentity) {
		this.eapIdentity = eapIdentity;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		WimaxSessionDataImpl wimaxSesionData = null;
		wimaxSesionData = (WimaxSessionDataImpl)super.clone();
		
		return wimaxSesionData;
	}

	@Override
	public Integer getAccountingCapabilities() {
		return this.accountingCapabilities;
	}
	
	public void setAccountingCapabilities(int accountingCapabilities){
		this.accountingCapabilities = accountingCapabilities;
	}

	@Override
	public Integer getHotliningCapabilities() {
		return this.hotliningCapabilities;
	}

	public void setHotliningCapabilities(int hotliningCapabilities){
		this.hotliningCapabilities = hotliningCapabilities;
	}
	
	@Override
	public byte[] getWimaxRelease() {
		return this.wimaxRelease;
	}

	public void setWimaxRelease(byte[] wimaxRelease){
		System.arraycopy(wimaxRelease, 0, this.wimaxRelease, 0, wimaxRelease.length);
	}
	
	@Override
	public Integer getIdleModeNotificationCapabilities() {
		return this.idleModeNotificationCapabilities;
	}
	
	public void setIdleModeNotificationCapabilities(int idleModeNotificationCapabilities){
		this.idleModeNotificationCapabilities = idleModeNotificationCapabilities;
	}

	@Override
	public String getCUI() {
		return this.CUI;
	}
	
	public void setCUI(String CUI){
		this.CUI = CUI;
	}

	public String toString(){
		StringBuilder strSessionBuilder=new StringBuilder();
		strSessionBuilder.append(("\n"+getAAASessionId()!=null)?(StringUtility.fillChar("\nAAA-Session-ID", 25)+StringUtility.fillChar(":", 5) + getStringAAASessionId()+"\n"):"");
		strSessionBuilder.append((getCUI()!=null)?(StringUtility.fillChar("CUI", 24)+StringUtility.fillChar(":", 5) + getCUI()+"\n"):"");
		strSessionBuilder.append((getUsername()!=null)?(StringUtility.fillChar("Username", 24)+StringUtility.fillChar(":", 5) + getUsername()+"\n"):"");
		strSessionBuilder.append((getLastAccessTime()!=0)?(StringUtility.fillChar("Last Access Time", 24)+StringUtility.fillChar(":", 5) + getLastAccessTime()+"\n"):"");
		strSessionBuilder.append((getSessionLifetime()!=0)?(StringUtility.fillChar("Session Lifetime", 24)+StringUtility.fillChar(":", 5) + getSessionLifetime()+"\n"):"");
		strSessionBuilder.append((getDefaultSessionTimeoutInSeconds()!=0)?(StringUtility.fillChar("Default Session Timeout", 24)+StringUtility.fillChar(":", 5) + getDefaultSessionTimeoutInSeconds()+"\n"):"");
		strSessionBuilder.append((getHA_RK_KEY()!=null)?(StringUtility.fillChar("HA_RK_KEY", 24)+StringUtility.fillChar(":", 5) +RadiusUtility.bytesToHex(getHA_RK_KEY())+"\n"):"");
		strSessionBuilder.append((getHA_RK_Lifetime_In_Seconds()!=0)?(StringUtility.fillChar("HA_RK_Lifetime", 24)+StringUtility.fillChar(":", 5) + getHA_RK_Lifetime_In_Seconds()+"\n"):"");
		strSessionBuilder.append((getHA_RK_SPI()!=0)?(StringUtility.fillChar("HA_RK_SPI", 24)+StringUtility.fillChar(":", 5) +(getHA_RK_SPI())+"\n"):"");
		strSessionBuilder.append((getSalt()!=0)?(StringUtility.fillChar("Salt", 24)+StringUtility.fillChar(":", 5) + getSalt()+"\n"):"");
		strSessionBuilder.append((getHa_salt()!=null)?(StringUtility.fillChar("HA_Salt", 24)+StringUtility.fillChar(":", 5) +RadiusUtility.bytesToHex(getHa_salt())+"\n"):"");
		strSessionBuilder.append((getMN_HA_MIP4_KEY()!=null)?(StringUtility.fillChar("MN_HA_MIP4_KEY", 24)+StringUtility.fillChar(":", 5) +RadiusUtility.bytesToHex(getMN_HA_MIP4_KEY())+"\n"):"");
		strSessionBuilder.append((getMN_HA_MIP4_SPI()!=0)?(StringUtility.fillChar("MN_HA_MIP4_SPI", 24)+StringUtility.fillChar(":", 5) + getMN_HA_MIP4_SPI()+"\n"):"");
		strSessionBuilder.append((getMn_salt()!=null)?(StringUtility.fillChar("MN_Salt", 24)+StringUtility.fillChar(":", 5) + RadiusUtility.bytesToHex(getMn_salt())+"\n"):"");
		strSessionBuilder.append(( getMSK()!=null)?(StringUtility.fillChar("MSK", 24)+StringUtility.fillChar(":", 5) +RadiusUtility.bytesToHex(getMSK())+"\n"):"");
		strSessionBuilder.append((getEMSK()!=null)?(StringUtility.fillChar("EMSK", 24)+StringUtility.fillChar(":", 5) +RadiusUtility.bytesToHex(getEMSK())+"\n"):"");
		strSessionBuilder.append((getHA_IP_MIP4()!=null && getHA_IP_MIP4().length()>0)?(StringUtility.fillChar("HA_IP_MIP4", 24)+StringUtility.fillChar(":", 5) + getHA_IP_MIP4()+"\n"):"");
		strSessionBuilder.append((getFA_RK_KEY()!=null)?(StringUtility.fillChar("FA_RK_KEY", 24)+StringUtility.fillChar(":", 5) + RadiusUtility.bytesToHex(getFA_RK_KEY())+"\n"):"");
		strSessionBuilder.append((getFA_RK_SPI()!=0)?(StringUtility.fillChar("FA_RK_SPI", 24)+StringUtility.fillChar(":", 5) +getFA_RK_SPI()+"\n"):"");
		strSessionBuilder.append((getCallingStationId()!=null && getCallingStationId().length() >0 )?(StringUtility.fillChar("Calling Station Id", 24)+StringUtility.fillChar(":", 5) + getCallingStationId()+"\n"):"");
		strSessionBuilder.append((getNasIP()!=null && getNasIP().length()>0 )?(StringUtility.fillChar("NAS IP", 24)+StringUtility.fillChar(":", 5) +getNasIP()+"\n"):"");
		strSessionBuilder.append((getNasIdentifier()!=null && getNasIdentifier().length()>0 )?(StringUtility.fillChar("Nas Identifier", 24)+StringUtility.fillChar(":", 5) +getNasIdentifier()+"\n"):"");
		strSessionBuilder.append((getNasPort()!=null && getNasPort().length()>0 )?(StringUtility.fillChar("NAS Port", 24)+StringUtility.fillChar(":", 5) + getNasPort()+"\n"):"");
		strSessionBuilder.append((getNasPortType()!=null && getNasPortType().length()>0 )?(StringUtility.fillChar("NAS Port Type", 24)+StringUtility.fillChar(":", 5) + getNasPortType()+"\n"):"");
		strSessionBuilder.append((getNasPortId()!=null && getNasPortId().length()>0 )?(StringUtility.fillChar("NAS Port Id", 24)+StringUtility.fillChar(":", 5) +getNasPortId()+"\n"):"");
		strSessionBuilder.append((getFramedIPAddress()!=null && getFramedIPAddress().length()>0 )?(StringUtility.fillChar("Framed IP Address", 24)+StringUtility.fillChar(":", 5) + getFramedIPAddress()+"\n"):"");
		strSessionBuilder.append((getDhcp_rk()!=null)?(StringUtility.fillChar("DHCP_RK", 24)+StringUtility.fillChar(":", 5) +RadiusUtility.bytesToHex(getDhcp_rk())+"\n"):"");
		strSessionBuilder.append((getDhcp_rk_id()!=0)?(StringUtility.fillChar("DHCP_RK_ID", 24)+StringUtility.fillChar(":", 5) +getDhcp_rk_id()+"\n"):"");
		strSessionBuilder.append((getDhcp_rk_lifetime_in_seconds()!=0)?(StringUtility.fillChar("DHCP_RK_lifetime", 24)+StringUtility.fillChar(":", 5) +getDhcp_rk_lifetime_in_seconds()+"\n"):"");
		strSessionBuilder.append((getDHCPV4_SERVER()!=null && getDHCPV4_SERVER().length()>0)?(StringUtility.fillChar("DHCPV4_SERVER", 24)+StringUtility.fillChar(":", 5) + getDHCPV4_SERVER()+"\n"):"");
		strSessionBuilder.append((getDhcp_rk_generation_time()!=0)?(StringUtility.fillChar("DHCP_RK generation time", 24)+StringUtility.fillChar(":", 5) + getDhcp_rk_generation_time()+"\n"):"");
		strSessionBuilder.append((getDhcp_salt()!=null)?(StringUtility.fillChar("DHCP_Salt", 24)+StringUtility.fillChar(":", 5) +RadiusUtility.bytesToHex(getDhcp_salt())+"\n"):"");
		strSessionBuilder.append((getEapIdentity()!=null && getEapIdentity().length()>0 )?(StringUtility.fillChar("Eap Identity", 24)+StringUtility.fillChar(":", 5) +getEapIdentity()+"\n"):"");
		
		return strSessionBuilder.toString();
	}

	@Override
	public long getHA_RK_Generation_Time_In_Millis() {
		return ha_rk_generation_time_in_millis;
	}
	
	public void setHA_RK_Generation_Time_In_Millis(long ha_rk_generation_time_in_millis) {
		this.ha_rk_generation_time_in_millis = ha_rk_generation_time_in_millis;
	}
	
}

