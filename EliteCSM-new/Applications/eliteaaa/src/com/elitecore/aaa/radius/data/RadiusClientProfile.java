
package com.elitecore.aaa.radius.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.data.Vendor;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.xml.OptionalIntegerAdapter;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.Constants3GPP2;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;

@XmlType(propOrder = {})
public class RadiusClientProfile {

	private String profileID;
	private String strProfileName = null;	
	private String strVendorName = null;
	private long lVendorId ;
	private Integer iVendorType = 0;	
	private String strPrepaidStandard = null;	
	private String strClientPolicy = null;
	private String strHotlinePolicy = null;
	private Optional<Integer> dynauthPort = Optional.absent();
	private String supportedAttributeStrCOA = null;
	private String unsupportedAttributeStrCOA = null;
	private String supportedAttributeStrDM = null;
	private String unsupportedAttributeStrDM = null;
	
	//setting the default values to reserved IP address
	private String strDHCPAddress = CommonConstants.RESERVED_IPV_4_ADDRESS;
	private String strHAAddress = CommonConstants.RESERVED_IPV_4_ADDRESS;
	
	private String dnsAddresses = null;
	
	private String userIdentities = null;
	private boolean bIsMultipleClassAttributeSupported =false;
	private boolean bIsFilterUnsupportedVSA;

	private List<Vendor> supportedVendorsList = null;
	private List<byte[]> dnsList = null;
	private List<IRadiusAttribute> wimaxDnsList = null;
	private List<IRadiusAttribute> dns3gpp2List = null;
	private List<String> userIdentityList = null;
	
	// Required by JAXB.
	public RadiusClientProfile(){
		supportedVendorsList = new ArrayList<Vendor>(0);
		dnsList = new ArrayList<byte[]>(0);
		dns3gpp2List = new ArrayList<IRadiusAttribute>(0);
		userIdentityList = new ArrayList<String>(0);
	}
	
	public RadiusClientProfile(String profileId){
		this.profileID = profileId;
		this.supportedVendorsList = new ArrayList<Vendor>();
		this.dnsList = new ArrayList<byte[]>();
		this.wimaxDnsList = new ArrayList<IRadiusAttribute>();
		this.dns3gpp2List = new ArrayList<IRadiusAttribute>();
		this.userIdentityList = new ArrayList<String>();
	}
	
	@XmlElement(name = "profile-id", type = String.class)
	public String getProfileID() {
		return profileID;
	}

	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}
	
	@XmlElement(name = "dns-addresses", type = String.class)
	public String getDnsAddresses() {
		return dnsAddresses;
	}

	public void setDnsAddresses(String dnsAddresses) {		
		this.dnsAddresses = dnsAddresses;
		setDnsList(getDnsList(dnsAddresses));
	}
	
	@XmlTransient
	public List<String> getUserIdentityList() {
		return userIdentityList;
	}

	public void setUserIdentityList(List<String> userIdentityList) {
		this.userIdentityList = userIdentityList;
		this.userIdentities = StringUtility.getDelimitirSeparatedString(userIdentityList, ",");
	}

	public void setUserIdentities(String userIdentities) {
		this.userIdentities = userIdentities;
		this.userIdentityList = getIdentityAttributeList(userIdentities);
	}
	
	@XmlElement(name = "user-identities", type = String.class)
	public String getUserIdentities() {
		return userIdentities;
	}
	
	@XmlElement(name = "profile-name", type = String.class)
	public String getProfileName() {
		return strProfileName;
	}
	public void setProfileName(String profileName){
		this.strProfileName = profileName;
	}
	
	@XmlElement(name = "client-policy", type = String.class)
	public String getClientPolicy() {
		return strClientPolicy;
	}
	public void setClientPolicy(String strClientPolicy) {
		this.strClientPolicy = strClientPolicy;
	}
	public void setIsMultipleClassAttributeSupported(boolean bIsMultipleClassAttributeSupported) {
		this.bIsMultipleClassAttributeSupported = bIsMultipleClassAttributeSupported;
	}
	
	@XmlElement(name = "dhcp-addresses", type = String.class, defaultValue = CommonConstants.RESERVED_IPV_4_ADDRESS)
	public String getDHCPAddress() {
		return strDHCPAddress;
	}
	
	@XmlElement(name = "multiple-class-attribute-support", type = boolean.class,defaultValue ="false")
	public boolean getIsMultipleClassAttributeSupported() {
		return this.bIsMultipleClassAttributeSupported ;
	}

	public void setIsFilterUnsupportedVSA(boolean bIsFilterUnsupportedVSA){
		this.bIsFilterUnsupportedVSA = bIsFilterUnsupportedVSA;
	}
	
	@XmlElement(name = "filter-unsupported-vsa", type = boolean.class)
	public boolean getIsFilterUnsupportedVSA(){
		return this.bIsFilterUnsupportedVSA;
	}

	public void setDHCPAddress(String strDHCPAddress) {
		if(strDHCPAddress != null && strDHCPAddress.trim().length() > 0){
			this.strDHCPAddress = strDHCPAddress;
		}
	}

	@XmlElement(name = "ha-ip-address", type = String.class, defaultValue = CommonConstants.RESERVED_IPV_4_ADDRESS)
	public String getHAAddress() {
		return strHAAddress;
	}
	public void setHAAddress(String strHAAddress) {
		if(strHAAddress != null && strHAAddress.trim().length() > 0){
			this.strHAAddress = strHAAddress;
		}
	}
	
	@XmlElement(name = "hotline-policy", type = String.class)
	public String getHotlinePolicy() {
		return strHotlinePolicy;
	}
	public void setHotlinePolicy(String strHotlinePolicy) {
		this.strHotlinePolicy = strHotlinePolicy;
	}	
	
	@XmlElement(name = "vendor-type", type = Integer.class,defaultValue ="0")
	public Integer getVendorType() {
		return iVendorType;
	}
	public void setVendorType(Integer vendorType) {
		iVendorType = vendorType;
	}
	
	@XmlElement(name = "prepaid-standard", type = String.class)
	public String getPrepaidStandard() {
		return strPrepaidStandard;
	}
	public void setPrepaidStandard(String strPrepaidStandard) {
		this.strPrepaidStandard = strPrepaidStandard;
	}
	@XmlElement(name = "vendor-name", type = String.class)
	public String getVendorName() {
		return strVendorName;
	}
	public void setVendorName(String strVendorName) {
		this.strVendorName = strVendorName;
	}
	
	@XmlTransient
	public List<byte[]> getDnsList() {
		return dnsList;
	}
	public void setDnsList(List<byte[]> dnsList) {
		this.dnsList = dnsList;		
		this.wimaxDnsList = buildWimaxDNSAttributeList(dnsList);
		this.dns3gpp2List = build3GPP2DNSAttributeList(dnsList);
		
	}		
	@XmlTransient
	public List<IRadiusAttribute> getWimaxDnsList() {
		return wimaxDnsList;
	}
	@XmlTransient
	public List<IRadiusAttribute> getDns3gpp2List() {
		return dns3gpp2List;
	}
	
	
	@XmlElementWrapper(name = "supported-vendors")
	@XmlElement(name= "supported-vendor")
	public List<Vendor> getSupportedVendorsList() {
		return supportedVendorsList;
		
	}
	
	public void setSupportedVendorsList(List<Vendor> supportedVendorList) {
		this.supportedVendorsList= supportedVendorList;
	}
	public boolean isSupportedVendorId(long vendorId){
		if(vendorId == 0) return true;
		if(lVendorId == vendorId )
			return true;
		Vendor v = new Vendor();
		v.setVendorID(vendorId);
		return this.supportedVendorsList.contains(v);
	}
	
	@XmlElement(name = "vendor-id", type = long.class)
	public long getVendorId() {
		return lVendorId;
	}

	public void setVendorId(long vendorId) {
		lVendorId = vendorId;
	}

	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Profile Name        		: " + strProfileName);
		out.println("Client Policy       		: " + ((strClientPolicy!=null)?strClientPolicy:""));
		out.println("Hotline Policy      		: " + ((strHotlinePolicy!=null)?strHotlinePolicy:""));
		out.println("DHCP Address        		: " + ((strDHCPAddress!=null)? strDHCPAddress: ""));
		out.println("HA Address          		: " + ((strHAAddress!=null)? strHAAddress: ""));
		out.println("Vendor Id           		: " +  lVendorId);
		out.println("Vendor Type         		: " +  iVendorType);
		out.println("Vendor Name         		: " + ((strVendorName!=null)? strVendorName: ""));		
		out.println("Prepaid Standard    		: " + ((strPrepaidStandard!=null)? strPrepaidStandard: ""));		
		out.println("Filter Unsupported VSA 	: " + bIsFilterUnsupportedVSA);
		out.println("DynAuth Port           	: " + (getDynauthPort().isPresent() ? getDynauthPort().get() : ""));
		out.println("CoA supported attributes 	: " 
				+ (getSupportedAttributeStrCOA() != null ? getSupportedAttributeStrCOA() : ""));
		out.println("CoA unsupported attributes : " 
				+ (getUnsupportedAttributeStrCOA() != null ? getUnsupportedAttributeStrCOA() : ""));
		out.println("DM supported attributes 	: " 
				+ (getSupportedAttributeStrDM() != null ? getSupportedAttributeStrDM() : ""));
		out.println("DM unsupported attributes  : " 
				+ (getUnsupportedAttributeStrDM() != null ? getUnsupportedAttributeStrDM() : ""));
		out.println("DNS List            		: " + ((dnsAddresses!=null)? dnsAddresses: ""));
		out.println("Supported Vendors   		: ");
		Iterator<?> iterator = supportedVendorsList.iterator();
		while(iterator.hasNext()){
			out.println("                   : " + iterator.next().toString());
		}	

		out.println("User Identities     	: ");
		iterator = userIdentityList.iterator();
		while(iterator.hasNext()){
			out.println("                   : " + iterator.next().toString());
		}		
		return stringBuffer.toString();
	}


	private List<IRadiusAttribute> buildWimaxDNSAttributeList(List<byte[]> clientDNSList) {
		List<IRadiusAttribute> dnsAttrList = new ArrayList<IRadiusAttribute>();
		if(dnsList != null && !dnsList.isEmpty()){
			dnsAttrList = new ArrayList<IRadiusAttribute>();
			for(int i=0;i<dnsList.size();i++){
				BaseRadiusAttribute dnsAttr =(BaseRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusConstants.WIMAX_VENDOR_ID,WimaxAttrConstants.DNS.getIntValue());
				dnsAttr.setValueBytes(dnsList.get(i));
				dnsAttrList.add(dnsAttr);
			}

		}
		return dnsAttrList;

	}

	private List<IRadiusAttribute> build3GPP2DNSAttributeList(List<byte[]> clientDNSList) {
		List<IRadiusAttribute> dnsAttrList = new ArrayList<IRadiusAttribute>();
		if(clientDNSList != null && !clientDNSList.isEmpty()){
			Iterator<byte[]> dnsItr = clientDNSList.iterator();
			IRadiusAttribute overrideVAAADNSAttr = Dictionary.getInstance().getAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.DNS_SERVER_IP_ADDRESS, Constants3GPP2.OVERRIDE_VAAA_DNS);
			overrideVAAADNSAttr.setStringValue("0");
			IRadiusAttribute entityTypeAttr = Dictionary.getInstance().getAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.DNS_SERVER_IP_ADDRESS, Constants3GPP2.ENTITY_TYPE);
			entityTypeAttr.setStringValue("1");
			while(dnsItr.hasNext()){
				VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary.getInstance().getAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC);
				vsaAttribute.setVendorID(RadiusConstants.VENDOR_3GPP2_ID);
				IVendorSpecificAttribute vsaType = Dictionary.getInstance().getVendorAttributeType(RadiusConstants.VENDOR_3GPP2_ID);
				vsaAttribute.setVendorTypeAttribute(vsaType);
				BaseRadiusAttribute dnsAttr = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.DNS_SERVER_IP_ADDRESS);
				IRadiusAttribute primaryDNSAttr = Dictionary.getInstance().getAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.DNS_SERVER_IP_ADDRESS, Constants3GPP2.PRIMARY_DNS);
				primaryDNSAttr.setValueBytes(dnsItr.next());
				dnsAttr.addTLVAttribute(primaryDNSAttr);
				if(dnsItr.hasNext()){
					IRadiusAttribute secDNSAttr = Dictionary.getInstance().getAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.DNS_SERVER_IP_ADDRESS, Constants3GPP2.SECONDARY_DNS);
					secDNSAttr.setValueBytes(dnsItr.next());
					dnsAttr.addTLVAttribute(secDNSAttr);
				}
				dnsAttr.addTLVAttribute(overrideVAAADNSAttr);
				dnsAttr.addTLVAttribute(entityTypeAttr);
				vsaType.addAttribute(dnsAttr);
				dnsAttrList.add(vsaAttribute);
			}
		}

		return dnsAttrList;
	}

	
	
	private List<String> getIdentityAttributeList(String strUserIdentity) {
		if(strUserIdentity == null || strUserIdentity.length() ==0){
			return new ArrayList<String>();
		}
		List<String> userIdList = new ArrayList<String>();
		StringTokenizer strMultipleAttributeTokenizer = null; 
		String strVendorAttribute=null;
		strMultipleAttributeTokenizer = new StringTokenizer(strUserIdentity,",;");
		while(strMultipleAttributeTokenizer.hasMoreTokens()){ 
			strVendorAttribute=strMultipleAttributeTokenizer.nextToken().trim();
			if(strVendorAttribute.length() > 0)
				userIdList.add(strVendorAttribute);
		}
		return userIdList;
	}
	
	
	private List<byte[]> getDnsList(String strDNS){
		List<byte[]> dnsServerList = new ArrayList<byte[]>();
		if(strDNS == null || strDNS.length() <= 0){
			return dnsServerList;
		}		
		String[] dnsValues = null;
		byte[] dns = null;
		InetAddress validDNS = null;
		if(strDNS.length() > 0)
			dnsValues = strDNS.split("[,;]");		
		if(dnsValues != null){
			for(int i=0; i<dnsValues.length; i++){
				try{
					validDNS = InetAddress.getByName(dnsValues[i]);
					dns = validDNS.getAddress();
					dnsServerList.add(dns);
				}catch(Exception e){					
				}
			}
		}
		return dnsServerList;		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof RadiusClientProfile))
			return false;
		
		RadiusClientProfile other = (RadiusClientProfile) obj;
		return this.strProfileName == other.strProfileName;
	}

	@XmlElement(name = "dynauth-port")
	@XmlJavaTypeAdapter(value = OptionalIntegerAdapter.class)
	public Optional<Integer> getDynauthPort() {
		return dynauthPort;
	}

	public void setDynauthPort(Optional<Integer> dynauthPort) {
		this.dynauthPort = dynauthPort;
	}

	@XmlElement(name = "coa-unsupported-attributes", type = String.class)
	public String getUnsupportedAttributeStrCOA() {
		return this.unsupportedAttributeStrCOA;
	}
	
	public void setUnsupportedAttributeStrCOA(String unsupportedAttributeStrCOA) {
		this.unsupportedAttributeStrCOA = unsupportedAttributeStrCOA;
	}

	@XmlElement(name = "coa-supported-attributes", type = String.class)
	public String getSupportedAttributeStrCOA() {
		return supportedAttributeStrCOA;
	}
	
	public void setSupportedAttributeStrCOA(String supportedAttributeStrCOA) {
		this.supportedAttributeStrCOA = supportedAttributeStrCOA;
	}
	
	@XmlElement(name = "dm-supported-attributes", type = String.class)
	public String getSupportedAttributeStrDM() {
		return supportedAttributeStrDM;
	}

	public void setSupportedAttributeStrDM(String supportedAttributeStrDM) {
		this.supportedAttributeStrDM = supportedAttributeStrDM;
	}

	@XmlElement(name = "dm-unsupported-attributes", type = String.class)
	public String getUnsupportedAttributeStrDM() {
		return unsupportedAttributeStrDM;
	}

	public void setUnsupportedAttributeStrDM(String unsupportedAttributeStrDM) {
		this.unsupportedAttributeStrDM = unsupportedAttributeStrDM;
	}
}

