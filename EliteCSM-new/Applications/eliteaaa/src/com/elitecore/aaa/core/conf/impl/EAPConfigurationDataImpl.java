/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 1st Sept 2010 by Pankit Shah
 *  
 */
package com.elitecore.aaa.core.conf.impl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.elitecore.aaa.core.conf.EAPConfigurationData;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.tls.ServerCertificateProfile;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.certificate.CertificateTypeConstants;

@XmlType(propOrder = {})
public class EAPConfigurationDataImpl  implements EAPConfigurationData{
	public static final String MODULE = "EAP-CONF-IMPL";
	private String name ="Default" ;
	private String eapId;
	private int defaultNegotiationMethod=EapTypeConstants.MD5_CHALLENGE.typeId;
	private int iDefaultPEAPNegotiationMethod = EapTypeConstants.MSCHAPv2.typeId;
	private int iDefaultTTLSNegotiationMethod = EapTypeConstants.MSCHAPv2.typeId;
	private boolean treatInvalidPacketAsFatal = true;
	private boolean notificationSuccess;
	private boolean notificationFailure;
	private long sessionCleanupInterval = 86400;
	private long sessionDurationForCleanup = 86400;
	private long sessionTimeout = 3000;
	private long mskRevalidationTime = 0;
	private int maxEapPacketSize = 1024;
	private boolean ttlsCertificateRequest;
	private boolean peapCertificateRequest;
	private List<Integer> enableAuthMethods;
	private TLSConfigurationImpl tlsConfiguartionImpl ;
	private AKAPrimeConfigurationImpl akaPrimeConfigurationImpl;
	private AKAConfigurationImpl akaConfigurationImpl;
	private SIMConfigurationImpl simConfigurationImpl;
	private String strEnabledAuthMethods;
	private PeapMethodDetail peapMethodDetail;
	private TtlsMethodDetail ttlsMethodDetail;



	public EAPConfigurationDataImpl(ServerContext serverContext,String eapId) {
		this.eapId = eapId;
		enableAuthMethods = new ArrayList<Integer>();
		enableAuthMethods.add(EapTypeConstants.NO_ALTERNATIVE.typeId);
		tlsConfiguartionImpl = new TLSConfigurationImpl();
		akaPrimeConfigurationImpl = new AKAPrimeConfigurationImpl();
		akaConfigurationImpl = new AKAConfigurationImpl();
		simConfigurationImpl = new SIMConfigurationImpl();
		peapMethodDetail = new PeapMethodDetail();
		ttlsMethodDetail = new TtlsMethodDetail();
		
	}
	public EAPConfigurationDataImpl(){
		//required By Jaxb.
		enableAuthMethods = new ArrayList<Integer>();
		enableAuthMethods.add(EapTypeConstants.NO_ALTERNATIVE.typeId);
		tlsConfiguartionImpl = new TLSConfigurationImpl();
		akaPrimeConfigurationImpl = new AKAPrimeConfigurationImpl();
		akaConfigurationImpl = new AKAConfigurationImpl();
		simConfigurationImpl = new SIMConfigurationImpl();
		peapMethodDetail = new PeapMethodDetail();
		ttlsMethodDetail = new TtlsMethodDetail();
	}

	//@XmlTransient
	@XmlElement(name = "eap-id",type = String.class)
	public String getEapId() {
		return eapId;
	}
	public void setEapId(String eapId) {
		this.eapId = eapId;
	}

	@XmlElement(name = "enable-auth-method",type = String.class)
	public String getStrEnabledAuthMethods() {
		return strEnabledAuthMethods;
	}
	public void setStrEnabledAuthMethods(String strEnabledAuthMethods) {
		this.strEnabledAuthMethods = strEnabledAuthMethods;
	}

	@XmlElement(name = "peap-conf")
	public PeapMethodDetail getPeapMethodDetail() {
		return peapMethodDetail;
	}
	public void setPeapMethodDetail(PeapMethodDetail peapMethodDetail) {
		this.peapMethodDetail = peapMethodDetail;
	}

	@XmlElement(name = "ttls-conf")
	public TtlsMethodDetail getTtlsMethodDetail() {
		return ttlsMethodDetail;
	}
	public void setTtlsMethodDetail(TtlsMethodDetail ttlsMethodDetail) {
		this.ttlsMethodDetail = ttlsMethodDetail;
	}

	public void readConfiguration() throws LoadConfigurationException {}

	public Node getConfigurationNode(Document document) {return null;}

	public void setName(String name){
		this.name = name;
	}
	@XmlElement(name ="name",type = String.class,defaultValue ="Default")
	public String getName(){
		return name;
	}
	@XmlElement(name ="tls-conf")
	public TLSConfigurationImpl getTLSConfiguration() {
		return tlsConfiguartionImpl;
	}

	public void setTLSConfiguration(TLSConfigurationImpl tlsConfiguartionImpl){
		this.tlsConfiguartionImpl = tlsConfiguartionImpl;
	}
	
	@XmlElement(name = "aka-conf")
	public AKAConfigurationImpl getAKAConfiguration(){
		return akaConfigurationImpl;
	}
	public void setAKAConfiguration(AKAConfigurationImpl akaConfigurationImpl){
		this.akaConfigurationImpl =  akaConfigurationImpl;
	}

	@XmlElement(name = "aka-prime-conf")
	public AKAPrimeConfigurationImpl getAKAPrimeConfiguration() {
		return this.akaPrimeConfigurationImpl;
	}
	
	public void setAkaPrimeConfiguration(AKAPrimeConfigurationImpl akaPrimeConfigurationImpl) {
		this.akaPrimeConfigurationImpl = akaPrimeConfigurationImpl;
	}
	
	@XmlElement(name = "sim-conf")
	public SIMConfigurationImpl getSIMConfiguration() {
		return this.simConfigurationImpl;
	}
	public void setSIMConfiguration(SIMConfigurationImpl simConfigurationImpl){
		this.simConfigurationImpl =  simConfigurationImpl;
	}

	public void setSessionCleanupInterval(long sessionCleanupInterval) {
		this.sessionCleanupInterval = sessionCleanupInterval;
	}
	@XmlElement(name = "session-cleanup-inteval",type = long.class,defaultValue ="86400")
	public long getSessionCleanupInterval() {
		return sessionCleanupInterval;
	}

	public void setSessionDurationForCleanup(long sessionDurationForCleanup) {
		this.sessionDurationForCleanup = sessionDurationForCleanup;
	}
	@XmlElement(name = "session-cleanup-duration",type = long.class,defaultValue ="86400")
	public long getSessionDurationForCleanup() {
		return sessionDurationForCleanup;
	}

	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	@XmlElement(name = "session-timeout",type = long.class,defaultValue ="3000") 
	public long getSessionTimeout() {
		return sessionTimeout;
	}

	@XmlElement(name = "msk-revalidation-time", type = long.class, defaultValue ="0")
	public long getMskRevalidationTime() {
		return mskRevalidationTime;
	}
	
	public void setMskRevalidationTime(long mskRevalidationTime) {
		this.mskRevalidationTime = mskRevalidationTime;
	}
	
	public void setIsTreatInvalidPacketAsFatal(boolean treatInvalidPacketAsFatal) {
		this.treatInvalidPacketAsFatal = treatInvalidPacketAsFatal;
	}

	@XmlElement(name = "treat-invalid-packet-as-fatal",type = boolean.class,defaultValue ="true")
	public boolean getIsTreatInvalidPacketAsFatal() {
		return treatInvalidPacketAsFatal;
	}

	public void setDefaultNegotiationMethod(int defaultNegotiationMethod) {
		this.defaultNegotiationMethod = defaultNegotiationMethod;
	}

	@XmlElement(name ="default-negotiation-method",type = int.class,defaultValue="4")
	public int getDefaultNegotiationMethod() {
		return defaultNegotiationMethod;
	}

	@XmlTransient
	public int getDefaultPEAPNegotiationMethod() {
		return peapMethodDetail.getDefaultPEAPNegotiationMethod();
	}

	public void setDefaultPEAPNegotiationMethod(int iDefaultPEAPNegotiationMethod) {
		peapMethodDetail.setDefaultPEAPNegotiationMethod(iDefaultPEAPNegotiationMethod);
	}

	@XmlTransient
	public int getDefaultTTLSNegotiationMethod() {
		return ttlsMethodDetail.getDefaultTTLSNegotiationMethod();
	}

	public void setDefaultTTLSNegotiationMethod(int iDefaultTTLSNegotiationMethod) {
		ttlsMethodDetail.setDefaultTTLSNegotiationMethod(iDefaultTTLSNegotiationMethod);
	}

	public void setEnableAuthMethods(List<Integer> enableAuthMethods) {
		this.enableAuthMethods = enableAuthMethods;
	}

	@XmlTransient
	public List<Integer> getEnableAuthMethods() {
		return enableAuthMethods;
	}

	public void setIsNotificationSuccess(boolean notificationSuccess) {
		this.notificationSuccess = notificationSuccess;
	}
	@XmlElement(name = "notification-success",type = boolean.class)
	public boolean getIsNotificationSuccess() {
		return notificationSuccess;
	}

	public void setIsNotificationFailure(boolean notificationFailure) {
		this.notificationFailure = notificationFailure;
	}
	@XmlElement(name = "notification-failure",type = boolean.class)
	public boolean getIsNotificationFailure() {
		return notificationFailure;
	}

	public void setMaxEapPacketSize(int maxEapPacketSize) {
		this.maxEapPacketSize = maxEapPacketSize;
	}
	@XmlElement(name = "max-eap-packet-size",type = int.class,defaultValue= "1024")
	public int getMaxEapPacketSize() {
		return maxEapPacketSize;
	}
	public void setTTLSCertificateRequest(boolean ttlsCertificateRequest) {
		ttlsMethodDetail.setIsTTLSCertificateRequest(ttlsCertificateRequest);
	}

	@XmlTransient
	public boolean getIsTTLSCertificateRequest() {
		return ttlsMethodDetail.getIsTTLSCertificateRequest();
	}
	@XmlTransient
	public boolean getIsPEAPCertificateRequest() {
		return peapMethodDetail.getIsPEAPCertificateRequest();
	}
	public void setIsPEAPCertificateRequest(boolean peapCertifications){
		peapMethodDetail.setIsPEAPCertificateRequest(peapCertifications);
	}


	public String getCertificateHome(){
		return System.getenv("ELITEAAA_HOME")+ File.separator + "system" + File.separator +"cert" + File.separator + "server";
	} 

	public String getPrivateKeyHome(){
		return System.getenv("ELITEAAA_HOME")+ File.separator + "system" +File.separator +"cert" + File.separator + "private";
	}

	public String getTrustedCertificateHome(){
		return System.getenv("ELITEAAA_HOME")+ File.separator + "system" +File.separator +"cert" + File.separator + "trustedcertificates";
	}

	public String getCRLHome(){
		return System.getenv("ELITEAAA_HOME")+ File.separator + "system" +File.separator +"cert" + File.separator + "crl";
	}

	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" --- EAP Configuration For EAP-ID = " +this.eapId +" ---");
		out.println();		
		out.println("    Default Negotiation Method = "+this.defaultNegotiationMethod);
		out.println("    Default TTLS Negotiation Method = "+this.iDefaultTTLSNegotiationMethod);
		out.println("    Default PEAP Negotiation Method = "+this.iDefaultPEAPNegotiationMethod);
		out.println("    Treat Invalid Packet As Fatal = " + this.treatInvalidPacketAsFatal);
		out.println("    Notification Success= " +  this.notificationSuccess);
		out.println("    Notification Failure = "+this.notificationFailure); 
		out.println("    Session Cleanup Interval = " + this.sessionCleanupInterval); 
		out.println("    Session Duration For Cleanup = " + this.sessionDurationForCleanup);
		out.println("    Session Timeout = " + this.sessionTimeout);
		out.println("    MaxEap Packet Size = " + this.maxEapPacketSize);
		out.println("    TTLS Certificate Request= " + this.ttlsCertificateRequest);
		out.println("    PEAP Certificate Request= " + this.peapCertificateRequest);
		out.println("    Enable Auth Methods = "+this.enableAuthMethods);
		out.println();
		out.println(tlsConfiguartionImpl);
		out.println(akaConfigurationImpl);
		out.println(simConfigurationImpl);
		out.close();
		return stringBuffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EAPConfigurationDataImpl))
			return false;
		
		EAPConfigurationDataImpl other = (EAPConfigurationDataImpl) obj;
		return this.eapId == other.eapId;
	}
	
	
	
	
	/** Inner classes for sub configuration **/
	@XmlType(propOrder = {})
	static class PeapMethodDetail{
		private boolean peapCertificateRequest;
		private int iDefaultPEAPNegotiationMethod = EapTypeConstants.MSCHAPv2.typeId;

		public PeapMethodDetail(){
			//required by Jaxb.
		}
		@XmlElement(name = "certificate-request",type = boolean.class)
		public boolean getIsPEAPCertificateRequest() {
			return peapCertificateRequest;
		}
		public void setIsPEAPCertificateRequest(boolean peapCertifications){
			this.peapCertificateRequest = peapCertifications;
		}

		@XmlElement(name = "negotiation-method",type = int.class,defaultValue="26")
		public int getDefaultPEAPNegotiationMethod() {
			return iDefaultPEAPNegotiationMethod;
		}

		public void setDefaultPEAPNegotiationMethod(int iDefaultPEAPNegotiationMethod) {
			this.iDefaultPEAPNegotiationMethod = iDefaultPEAPNegotiationMethod;
		}
	}
	
	@XmlType(propOrder = {})
	static class TtlsMethodDetail{

		private boolean ttlsCertificateRequest;
		private int iDefaultTTLSNegotiationMethod= EapTypeConstants.MSCHAPv2.typeId;

		public TtlsMethodDetail(){
			//required by Jaxb.
		}
		public void setIsTTLSCertificateRequest(boolean ttlsCertificateRequest) {
			this.ttlsCertificateRequest = ttlsCertificateRequest;
		}

		@XmlElement(name = "ttls-certificate-request",type = boolean.class)
		public boolean getIsTTLSCertificateRequest() {
			return ttlsCertificateRequest;
		}

		@XmlElement(name = "negotiation-method",type = int.class,defaultValue="26")
		public int getDefaultTTLSNegotiationMethod() {
			return iDefaultTTLSNegotiationMethod;
		}

		public void setDefaultTTLSNegotiationMethod(int iDefaultTTLSNegotiationMethod) {
			this.iDefaultTTLSNegotiationMethod = iDefaultTTLSNegotiationMethod;
		}
	}
	
	
	@XmlType(propOrder = {})
	public static class TLSConfigurationImpl implements TLSConfiguration{

		ProtocolVersion minProtocolVersion = ProtocolVersion.TLS1_0;
		ProtocolVersion maxProtocolVersion = ProtocolVersion.TLS1_0;
		boolean tlsCertificateRequest = true;
		int sessionResumptionDuration = 5000;
		int sessionResumptionLimit = 2;
		int defaultCompressionMethod ;
		private List<Integer> cipherSuiteIDs ;
		/*
		 * enabledCipherSuites contains comma-separated integer values of ciphersuites
		 */
		private String enabledCipherSuites = CipherSuites.TLS_RSA_WITH_3DES_EDE_CBC_SHA.code + "," + CipherSuites.TLS_RSA_WITH_AES_128_CBC_SHA.code;
		private CertificateConfigurationImpl certificateConfiguration;
		
		boolean validateCertificateExpiry;
		boolean validateCertificateRevocation;
		boolean validateMac;
		boolean validateClientCertificate;
		
		public TLSConfigurationImpl(){
			cipherSuiteIDs = CipherSuites.getSupportedCiphersuiteIDs(ProtocolVersion.TLS1_0, ProtocolVersion.TLS1_0);
			certificateConfiguration = new CertificateConfigurationImpl();
			validateCertificateExpiry = true;
			validateCertificateRevocation = true;
			validateMac = true;
			validateClientCertificate = true;
		}

		public void setIsTlsCertificateRequest(boolean tlsCertificateRequest) {
			this.tlsCertificateRequest = tlsCertificateRequest;
		}
		@XmlElement(name = "tls-certificate-request",type = boolean.class,defaultValue ="true")
		public boolean getIsTlsCertificateRequest() {
			return tlsCertificateRequest;
		}
		public void setSessionResumptionDuration(int sessionResumptionDuration) {
			this.sessionResumptionDuration = sessionResumptionDuration;
		}
		@XmlElement(name = "session-resumption-duration",type = int.class,defaultValue ="5000")
		public int getSessionResumptionDuration() {
			return sessionResumptionDuration;
		}
		public void setSessionResumptionLimit(int sessionResumptionLimit) {
			this.sessionResumptionLimit = sessionResumptionLimit;
		}
		@XmlElement(name = "session-resumption-limit",type = int.class,defaultValue ="2")
		public int getSessionResumptionLimit() {
			return sessionResumptionLimit;
		}
		public void setDefaultCompressionMethod(int defaultCompressionMethod) {
			this.defaultCompressionMethod = defaultCompressionMethod;
		}
		@XmlTransient
		public int getDefaultCompressionMethod() {
			return defaultCompressionMethod;
		}
		public void setCertificateConfiguration(CertificateConfigurationImpl certificateConfiguration) {
			this.certificateConfiguration = certificateConfiguration;
		}
		@XmlElement(name = "certificate")
		public CertificateConfigurationImpl getCertificateConfiguration() {
			return certificateConfiguration;
		}
		public void setCipherSuiteIDs(List<Integer> cipherSuiteIDs) {
			this.cipherSuiteIDs = cipherSuiteIDs;
		}
		@XmlTransient
		public List<Integer> getCipherSuiteIDs() {
			return cipherSuiteIDs;
		}
		
		/*
		 * These are kept transient just for branch 6.4.4 and will be serialized to XML from 6.6
		 * These configurations are required for doing TLS version negotiation and the older configuration
		 * of Major and Minor Protocol version will be removed.
		 */
		@XmlElement(name = "min-tls-version",defaultValue ="TLSv1")
		public ProtocolVersion getMinProtocolVersion() {
			return minProtocolVersion;
		}
		public void setMinProtocolVersion(ProtocolVersion minProtocolVersion) {
			this.minProtocolVersion = minProtocolVersion;
		}
		
		@XmlElement(name = "max-tls-version",defaultValue ="TLSv1")
		public ProtocolVersion getMaxProtocolVersion() {
			return maxProtocolVersion;
		}
		public void setMaxProtocolVersion(ProtocolVersion maxProtocolVersion) {
			this.maxProtocolVersion = maxProtocolVersion;
		}

		@XmlElement(name = "enabled-ciphersuites",type = String.class)
		public String getEnabledCipherSuites() {
			return enabledCipherSuites;
		}

		public void setEnabledCipherSuites(String enabledCipherSuites) {
			this.enabledCipherSuites = enabledCipherSuites;
		}

		@XmlElement(name="validate-certificate-expiry",type = boolean.class, defaultValue="true")
		public boolean getValidateCertificateExpiry() {
			return validateCertificateExpiry;
		}
		
		public void setValidateCertificateExpiry(boolean validateCertificateExpiry) {
			this.validateCertificateExpiry = validateCertificateExpiry;
		}
		
		@XmlElement(name="validate-certificate-revocation",type = boolean.class, defaultValue="true")
		public boolean getValidateCertificateRevocation() {
			return validateCertificateRevocation;
		}
		
		public void setValidateCertificateRevocation(boolean validateCertificateRevocation) {
			this.validateCertificateRevocation = validateCertificateRevocation;
		}
		
		@XmlElement(name="validate-mac",type = boolean.class, defaultValue="true")
		public boolean getValidateMac() {
			return validateMac;
		}

		public void setValidateMac(boolean validateMac) {
			this.validateMac = validateMac;
		}
		
		@XmlElement(name="validate-client-certificate", type = boolean.class, defaultValue="true")
		public boolean getValidateClientCertificate() {
			return validateClientCertificate;
		}

		public void setValidateClientCertificate(boolean validateClientCertificate) {
			this.validateClientCertificate = validateClientCertificate;
		}
		
		public String toString() {

			StringWriter stringBuffer = new StringWriter();
			PrintWriter out = new PrintWriter(stringBuffer);

			out.println(" --- TLS Configuration --- ");
			out.println();
			out.println("    Minimum TLS Version  = "+this.minProtocolVersion);
			out.println("    Maximum TLS Version  = "+this.maxProtocolVersion);
			out.println("    Tls Certificate Request = "+this.tlsCertificateRequest);
			out.println("    Session Resumption Duration = " + this.sessionResumptionDuration);
			out.println("    Session Resumption Limit = " +  this.sessionResumptionLimit);
			out.println("    Default Compression Method = "+this.defaultCompressionMethod); 
			out.println("    Cipher Suite List = " + this.getCipherSuiteIDs());
			out.println("    Validate Certificate Expiry = " + this.validateCertificateExpiry);
			out.println("    Validate Certificate Revocation = " + this.validateCertificateRevocation);
			out.println("    Validate Mac = " + this.validateMac);
			out.println("    Validate Client Certificate = " + this.validateClientCertificate);
			out.println();
			out.println(this.certificateConfiguration);
			out.close();
			return stringBuffer.toString();
		}
	}  

	
	@XmlType(propOrder = {})
	public static class AKAConfigurationImpl implements AKAConfiguration {

		private int localHostPort = 0;
		private String localHostIp = "";
		private String localHostId = "";
		private int remoteHostPort = 0;
		private String remoteHostIp = "";
		private String remoteHostId = "";
		private int quintupletDS = 0;
		private AKAPseudoDetail akaPseudoDetail;
		private AKAFastReAuthDetail akaFastReAuthDetail;
		private int fastReauthGenMethod = 0; 

		public AKAConfigurationImpl(){
			//required By Jaxb.
			akaPseudoDetail = new AKAPseudoDetail();
			akaFastReAuthDetail = new AKAFastReAuthDetail();
		}

		@XmlTransient
		public int getFastReauthGenMethod() {
			return fastReauthGenMethod;
		}

		public void setFastReauthGenMethod(int fastReauthGenMethod) {
			this.fastReauthGenMethod = fastReauthGenMethod;
		}

		@XmlTransient
		public String getFastReauthPrefix() {
			return akaFastReAuthDetail.getFastReauthPrefix();
		}
		public void setFastReauthPrefix(String fastReauthPrefix) {
			this.akaFastReAuthDetail.setFastReauthPrefix(fastReauthPrefix);
		}
		@XmlTransient
		public boolean getIsFastReauthHexEncoding() {
			return akaFastReAuthDetail.getIsFastReauthHexEncoding();
		}
		public void setIsFastReauthHexEncoding(boolean fastReauthHexEncoding) {
			this.akaFastReAuthDetail.setIsFastReauthHexEncoding(fastReauthHexEncoding);
		}

		@XmlElement(name="fast-reauth-conf")
		public AKAFastReAuthDetail getAkaFastReAuthDetail() {
			return akaFastReAuthDetail;
		}

		public void setAkaFastReAuthDetail(AKAFastReAuthDetail akaFastReAuthConfig) {
			this.akaFastReAuthDetail = akaFastReAuthConfig;
		}

		@XmlElement(name = "pseudonym-conf")
		public AKAPseudoDetail getAkaPseudoDetail() {
			return akaPseudoDetail;
		}

		public void setAkaPseudoDetail(AKAPseudoDetail akaPseudoConfig) {
			this.akaPseudoDetail = akaPseudoConfig;
		}

		public void setPseudonymPrefix(String pseudonymPrefix) {
			akaPseudoDetail.setPseudonymPrefix(pseudonymPrefix);
		}
		@XmlTransient
		public String getPseudonymPrefix() {
			return akaPseudoDetail.getPseudonymPrefix();
		}
		@XmlTransient
		public boolean getIsPseudoHexEncoding() {
			return akaPseudoDetail.getIsPseudoHexEncoding();
		}

		public void setHexEncoding(boolean hexEncoding) {
			akaPseudoDetail.setIsPseudoHexEncoding(hexEncoding);
		}

		public void setPseudonymGenMethod(int pseudonymGenMethod) {
			akaPseudoDetail.setPseudonymGenMethod(pseudonymGenMethod);
		}
		@XmlTransient
		public int getPseudonymGenMethod() {
			return akaPseudoDetail.getPseudonymGenMethod();
		}
		public void setQuintupletDS(int quintupletDS){
			this.quintupletDS = quintupletDS;
		}

		public void setLocalHostPort(int localHostPort) {
			this.localHostPort = localHostPort;
		}

		public void setLocalHostIp(String localHostIp) {
			this.localHostIp = localHostIp;
		}

		public void setLocalHostId(String localHostId) {
			this.localHostId = localHostId;
		}

		public void setRemoteHostPort(int remoteHostPort) {
			this.remoteHostPort = remoteHostPort;
		}

		public void setRemoteHostIp(String remoteHostIp) {
			this.remoteHostIp = remoteHostIp;
		}

		public void setRemoteHostId(String remoteHostId) {
			this.remoteHostId = remoteHostId;
		}

		@Override
		@XmlTransient
		public String getLocalHost() {
			return " LOCAL_HOST " + this.localHostId + ":" + this.localHostPort + " [" + this.localHostIp +"]";
		}

		@Override
		@XmlTransient
		public String getRemoteHost() {
			return " REMOTE_HOST " + this.remoteHostId + ":" + this.remoteHostPort + " [" + this.remoteHostIp +"]";
		}

		@Override
		@XmlTransient
		public int getQuintupletDS() {
			return quintupletDS;
		}
	}

	@XmlType(propOrder = {})
	public static class AKAPrimeConfigurationImpl implements AKAPrimeConfiguration {

		private int localHostPort = 0;
		private String localHostIp = "";
		private String localHostId = "";
		private int remoteHostPort = 0;
		private String remoteHostIp = "";
		private String remoteHostId = "";
		private int quintupletDS = 0;
		private AKAPrimePseudoDetail akaPrimePseudoDetail;
		private AKAPrimeFastReAuthDetail akaPrimeFastReAuthDetail;

		public AKAPrimeConfigurationImpl() {
			//required By Jaxb.
			akaPrimePseudoDetail = new AKAPrimePseudoDetail();
			akaPrimeFastReAuthDetail = new AKAPrimeFastReAuthDetail();
		}

		@XmlTransient
		public int getFastReauthGenMethod() {
			return akaPrimeFastReAuthDetail.getFastReauthGenMethod();
		}

		public void setFastReauthGenMethod(int fastReauthGenMethod) {
			this.akaPrimeFastReAuthDetail.setFastReauthGenMethod(fastReauthGenMethod);
		}

		@XmlTransient
		public String getFastReauthPrefix() {
			return akaPrimeFastReAuthDetail.getFastReauthPrefix();
		}
		public void setFastReauthPrefix(String fastReauthPrefix) {
			this.akaPrimeFastReAuthDetail.setFastReauthPrefix(fastReauthPrefix);
		}
		@XmlTransient
		public boolean getIsFastReauthHexEncoding() {
			return akaPrimeFastReAuthDetail.getIsFastReauthHexEncoding();
		}
		public void setIsFastReauthHexEncoding(boolean fastReauthHexEncoding) {
			this.akaPrimeFastReAuthDetail.setIsFastReauthHexEncoding(fastReauthHexEncoding);
		}

		@XmlElement(name="fast-reauth-conf")
		public AKAPrimeFastReAuthDetail getAkaPrimeFastReAuthDetail() {
			return akaPrimeFastReAuthDetail;
		}

		public void setAkaPrimeFastReAuthDetail(AKAPrimeFastReAuthDetail akaPrimeFastReAuthConfig) {
			this.akaPrimeFastReAuthDetail = akaPrimeFastReAuthConfig;
		}

		@XmlElement(name = "pseudonym-conf")
		public AKAPrimePseudoDetail getAkaPrimePseudoDetail() {
			return akaPrimePseudoDetail;
		}

		public void setAkaPrimePseudoDetail(AKAPrimePseudoDetail akaPrimePseudoConfig) {
			this.akaPrimePseudoDetail = akaPrimePseudoConfig;
		}

		public void setPseudonymPrefix(String pseudonymPrefix) {
			akaPrimePseudoDetail.setPseudonymPrefix(pseudonymPrefix);
		}
		@XmlTransient
		public String getPseudonymPrefix() {
			return akaPrimePseudoDetail.getPseudonymPrefix();
		}
		@XmlTransient
		public boolean getIsPseudoHexEncoding() {
			return akaPrimePseudoDetail.getIsPseudoHexEncoding();
		}

		public void setHexEncoding(boolean hexEncoding) {
			akaPrimePseudoDetail.setIsPseudoHexEncoding(hexEncoding);
		}

		public void setPseudonymGenMethod(int pseudonymGenMethod) {
			akaPrimePseudoDetail.setPseudonymGenMethod(pseudonymGenMethod);
		}
		@XmlTransient
		public int getPseudonymGenMethod() {
			return akaPrimePseudoDetail.getPseudonymGenMethod();
		}
		
		@XmlTransient
		public boolean getPseudonymIsRootNai() {
			return akaPrimePseudoDetail.getIsRootNai();
		}
		
		public void setPseudonymIsRootNai(boolean rootNai) {
			akaPrimePseudoDetail.setIsRootNai(rootNai);
		}
		
		@XmlTransient
		public String getPseudonymAAAIdInRootNai() {
			return akaPrimePseudoDetail.getAAAIdInRootNai();
		}
		
		public void setPseudonymAAAIdInRootNai(String aaaIdInRootNai) {
			akaPrimePseudoDetail.setAAAIdInRootNai(aaaIdInRootNai);
		}
		
		@XmlTransient
		public boolean getFastIsRootNai() {
			return akaPrimeFastReAuthDetail.getIsRootNai();
		}
		
		public void setFastIsRootNai(boolean rootNai) {
			akaPrimeFastReAuthDetail.setIsRootNai(rootNai);
		}
		
		@XmlTransient
		public String getFastAAAIdInRootNai() {
			return akaPrimeFastReAuthDetail.getAAAIdInRootNai();
		}
		
		public void setFastAAAIdInRootNai(String aaaIdInRootNai) {
			akaPrimeFastReAuthDetail.setAAAIdInRootNai(aaaIdInRootNai);
		}
		
		public void setQuintupletDS(int quintupletDS){
			this.quintupletDS = quintupletDS;
		}

		public void setLocalHostPort(int localHostPort) {
			this.localHostPort = localHostPort;
		}

		public void setLocalHostIp(String localHostIp) {
			this.localHostIp = localHostIp;
		}

		public void setLocalHostId(String localHostId) {
			this.localHostId = localHostId;
		}

		public void setRemoteHostPort(int remoteHostPort) {
			this.remoteHostPort = remoteHostPort;
		}

		public void setRemoteHostIp(String remoteHostIp) {
			this.remoteHostIp = remoteHostIp;
		}

		public void setRemoteHostId(String remoteHostId) {
			this.remoteHostId = remoteHostId;
		}

		@Override
		@XmlTransient
		public String getLocalHost() {
			return " LOCAL_HOST " + this.localHostId + ":" + this.localHostPort + " [" + this.localHostIp +"]";
		}

		@Override
		@XmlTransient
		public String getRemoteHost() {
			return " REMOTE_HOST " + this.remoteHostId + ":" + this.remoteHostPort + " [" + this.remoteHostIp +"]";
		}

		@Override
		@XmlTransient
		public int getQuintupletDS() {
			return quintupletDS;
		}
		
		@Override
		public String toString() {
			StringWriter stringBuffer = new StringWriter();
			PrintWriter out = new PrintWriter(stringBuffer);
			
			out.println(" --- AKA Prime Configuration --- ");
			out.println();
			out.println("    Local Host Port  = "+this.localHostPort);
			out.println("    Local Host Ip = "+this.localHostIp);
			out.println("    Local Host Id = "+this.localHostId);
			out.println("    Remote Host Port = " + this.remoteHostPort);
			out.println("    Remote Host Ip = " +  this.remoteHostIp);
			out.println("    Remote Host Id = "+this.remoteHostId); 
			out.println("    Quintuplet DS = " + this.quintupletDS);
			out.println("    Pseudonym gen method = " + this.akaPrimePseudoDetail.getPseudonymGenMethod());
			out.println("    Pseudonym Prefix = " + this.akaPrimePseudoDetail.getPseudonymPrefix());
			out.println("    Pseudonym Hex Encoding = " + this.akaPrimePseudoDetail.getIsPseudoHexEncoding());
			out.println("    Pseudonym Root NAI = " + this.akaPrimePseudoDetail.getIsRootNai());
			out.println("    Pseudonym AAA Identity in Root NAI = " + this.akaPrimePseudoDetail.getAAAIdInRootNai());
			out.println("    Fast Reauth gen Method = " + this.akaPrimeFastReAuthDetail.getFastReauthGenMethod());
			out.println("    Fast Reauth Prefix = " + this.akaPrimeFastReAuthDetail.getFastReauthPrefix());
			out.println("    Fast Reauth Hex Encoding = " + this.akaPrimeFastReAuthDetail.getIsFastReauthHexEncoding());
			out.println("    Fast Reauth Root NAI = " + this.akaPrimeFastReAuthDetail.getIsRootNai());
			out.println("    Fast Reauth AAA Identity in Root NAI = " + this.akaPrimeFastReAuthDetail.getAAAIdInRootNai());			out.println();
			out.close();
			return stringBuffer.toString();
		}
	}
	
	@XmlType(propOrder = {})
	public static class CertificateConfigurationImpl implements CertificateConfiguration{

//		private String defaultServerCertificateName = "elitecore-server-cert.pem";
//		private String defaultServerPrivateKey = "elitecore-server-key.der";
//		private String caCertificateName = "elitecore-ca-cert.pem";
		private List<Integer> certificateTypes;
		private String certificateType;
		private List<VendorSpecificCertificate> vendorSpecificCertList ;
		private VendorSpecificCertificateDetail vendorSpecificCertificateDetail ;
		private String serverCertificateProfileId;
		private ServerCertificateProfile serverCertificateProfile;

		public CertificateConfigurationImpl(){
			certificateTypes = new ArrayList<Integer>();
			certificateTypes.add(CertificateTypeConstants.RSA.getID());
			vendorSpecificCertificateDetail = new VendorSpecificCertificateDetail();
			vendorSpecificCertList = new ArrayList<VendorSpecificCertificate>();
		}

		@XmlElement(name = "certificate-types")
		public String getCertificateType() {
			return certificateType;
		}

		public void setCertificateType(String certificateType) {
			this.certificateType = certificateType;
		}
		@XmlElement(name = "vendor-specific-certificates")
		public VendorSpecificCertificateDetail getVendorSpecificCertificateImpl() {
			return vendorSpecificCertificateDetail;
		}

		public void setVendorSpecificCertificateImpl(
				VendorSpecificCertificateDetail vendorSpecificCertificateImpl) {
			this.vendorSpecificCertificateDetail = vendorSpecificCertificateImpl;
		}

		public void setVendorSpecificCertList(List<VendorSpecificCertificate> vendorSpecificCertList) {
			this.vendorSpecificCertList = vendorSpecificCertList;
		}
		@XmlTransient
		public List<VendorSpecificCertificate> getVendorSpecificCertList() {
			return vendorSpecificCertList;
		}

		public void setCertificateTypes(List<Integer> certificateTypes) {
			this.certificateTypes = certificateTypes;
		}
		@XmlTransient
		public List<Integer> getCertificateTypes() {
			return certificateTypes;
		}
		
		@XmlElement(name = "server-certificate-id",type = String.class)
		public String getServerCertificateId() {
			return serverCertificateProfileId;
		}


		public void setServerCertificateId(String serverCertificateId) {
			this.serverCertificateProfileId = serverCertificateId;
		}
			
		@Override
		@XmlTransient
		public PrivateKey getPrivateKey() {
			if(serverCertificateProfile != null)
				return serverCertificateProfile.getPrivateKey();
			else 
				return null;
		}

		public void setServerCertificateProfile(ServerCertificateProfile serverCertificateProfile) {
			this.serverCertificateProfile = serverCertificateProfile;
		}
		
		@Override
		@XmlTransient
		public List<byte[]> getServerCertificateChain() {
			if(serverCertificateProfile != null)
				return serverCertificateProfile.getCertificateChainBytes();
			else 
				return Collections.emptyList();
		}
		
		public String toString() {

			StringWriter stringBuffer = new StringWriter();
			PrintWriter out = new PrintWriter(stringBuffer);

			out.println(" --- Certication Configuration --- ");
			out.println();
			out.println("    Certificate Types = " + this.certificateTypes);
			out.println("    Server Certificate Profile = " + ((serverCertificateProfile==null) ? "--Not Applicable--" : this.serverCertificateProfile.getName()));
			out.println();

			out.close();
			return stringBuffer.toString();
		}
	}
	@XmlType(propOrder = {})
	public static class VendorSpecificCertificateImpl implements VendorSpecificCertificate{

		private String oui="000000*";
		private String serverCertificateProfileId;
		private ServerCertificateProfile serverCertificateProfile;

		public VendorSpecificCertificateImpl(){
			//required By Jaxb.
		}

		public void setOui(String oui) {
			this.oui = oui;
		}
		@XmlElement(name = "vendor-identifier",type = String.class,defaultValue ="000000*")
		public String getOui() {
			return oui;
		}

		@XmlElement(name = "server-certificate-id",type = String.class)
		public String getServerCertificateId() {
			return serverCertificateProfileId;
		}

		public void setServerCertificateId(String serverCertificateId) {
			this.serverCertificateProfileId = serverCertificateId;
		}

		@XmlTransient
		public PrivateKey getVendorPrivateKey() {
			if(serverCertificateProfile != null)
				return serverCertificateProfile.getPrivateKey();
			else 
				return null;
		}

		public void setServerCertificateProfile(ServerCertificateProfile serverCertificateProfile) {
			this.serverCertificateProfile = serverCertificateProfile;
		}
		
		@Override
		@XmlTransient
		public List<byte[]> getServerCertificateChain() {
			if(serverCertificateProfile != null)
				return serverCertificateProfile.getCertificateChainBytes();
			else 
				return Collections.emptyList();
		}
		
		public String toString() {

			StringWriter stringBuffer = new StringWriter();
			PrintWriter out = new PrintWriter(stringBuffer);

			out.println();
			out.println(" --- Vendor Specific Certificate --- ");
			out.println();
			out.println("    OUI = " +this.oui);
			out.println("    Server Certificate Proflie = " + ((serverCertificateProfile==null) ? "--Not Applicable--" : this.serverCertificateProfile.getName()));
			out.println();
			out.close();
			return stringBuffer.toString();
		}

	}

	@XmlType(propOrder = {})
	static class SIMFastReAuthDetail{

		private int fastReauthGenMethod = 0; 
		private String fastReauthPrefix = "1888";
		private boolean fastReauthHexEncoding = true;

		public SIMFastReAuthDetail(){
			//required by Jaxb.
		}

		public void setFastReauthGenMethod(int method) {
			fastReauthGenMethod = method;
		}
		public void setIsFastReauthHexEncoding(boolean encoding) {
			fastReauthHexEncoding = encoding;
		}
		public void setFastReauthPrefix(String prefix) {
			fastReauthPrefix = prefix;
		}

		@XmlElement(name = "prefix",type = String.class)
		public String getFastReauthPrefix() {
			return fastReauthPrefix;
		}

		@XmlElement(name = "encoding",type = boolean.class)
		public boolean getIsFastReauthHexEncoding() {
			return fastReauthHexEncoding;
		}

		@XmlElement(name = "method",type = int.class)
		public int getFastReauthGenMethod() {
			return fastReauthGenMethod;
		}

	}

	@XmlType(propOrder = {})
	static class SIMPseudoDetail{

		public SIMPseudoDetail(){
			//required By Jaxb.
		}
		private int pseudonymGenMethod = 0; 
		private String pseudonymPrefix = "1999";
		private boolean hexEncoding = true;

		public void setPseudonymPrefix(String pseudonymPrefix) {
			this.pseudonymPrefix = pseudonymPrefix;
		}

		public void setIsPseudoHexEncoding(boolean hexEncoding) {
			this.hexEncoding = hexEncoding;
		}

		public void setPseudoGenMethod(int pseudonymGenMethod) {
			this.pseudonymGenMethod = pseudonymGenMethod;
		}	

		@XmlElement(name = "prefix",type = String.class)
		public String getPseudonymPrefix() {
			return pseudonymPrefix;
		}

		@XmlElement(name = "encoding",type = boolean.class)
		public boolean getIsPseudoHexEncoding() {
			return hexEncoding;
		}

		@XmlElement(name = "method",type = int.class)
		public int getPseudoGenMethod() {
			return pseudonymGenMethod;
		}

	}

	@XmlType(propOrder = {})
	static class AKAPseudoDetail{

		public AKAPseudoDetail(){
			// required By Jaxb.

		}
		private int pseudonymGenMethod = 0; 
		private String pseudonymPrefix = "0999";
		private boolean hexEncoding = true;


		public void setPseudonymPrefix(String pseudonymPrefix) {
			this.pseudonymPrefix = pseudonymPrefix;
		}
		@XmlElement(name = "prefix",type = String.class)
		public String getPseudonymPrefix() {
			return pseudonymPrefix;
		}
		@XmlElement(name = "encoding",type = boolean.class)
		public boolean getIsPseudoHexEncoding() {
			return hexEncoding;
		}

		public void setIsPseudoHexEncoding(boolean hexEncoding) {
			this.hexEncoding = hexEncoding;
		}

		public void setPseudonymGenMethod(int pseudonymGenMethod) {
			this.pseudonymGenMethod = pseudonymGenMethod;
		}
		@XmlElement(name = "method",type = int.class)
		public int getPseudonymGenMethod() {
			return pseudonymGenMethod;
		}
	}

	@XmlType(propOrder = {})
	static class AKAFastReAuthDetail{

		public AKAFastReAuthDetail(){
			//required by Jaxb.
		}
		private int fastReauthGenMethod = 0; 
		private String fastReauthPrefix = "0888";
		private boolean fastReauthHexEncoding = true;

		public void setFastReauthGenMethod(int method) {
			fastReauthGenMethod = method;
		}
		public void setIsFastReauthHexEncoding(boolean encoding) {
			fastReauthHexEncoding = encoding;
		}
		public void setFastReauthPrefix(String prefix) {
			fastReauthPrefix = prefix;
		}

		@XmlElement(name = "prefix",type = String.class)
		public String getFastReauthPrefix() {
			return fastReauthPrefix;
		}

		@XmlElement(name = "encoding",type = boolean.class)
		public boolean getIsFastReauthHexEncoding() {
			return fastReauthHexEncoding;
		}

		@XmlElement(name = "method",type = int.class)
		public int getFastReauthGenMethod() {
			return fastReauthGenMethod;
		}

	}
	@XmlType(propOrder = {})
	static class AKAPrimePseudoDetail{

		public AKAPrimePseudoDetail() {
			// required by Jaxb
		}
		private int pseudonymGenMethod = 0; 
		private String pseudonymPrefix = "6888";
		private boolean hexEncoding = true;
		private boolean rootNai = false;
		private String aaaIdInRootNai = "";

		public void setPseudonymPrefix(String pseudonymPrefix) {
			this.pseudonymPrefix = pseudonymPrefix;
		}
		@XmlElement(name = "prefix",type = String.class)
		public String getPseudonymPrefix() {
			return pseudonymPrefix;
		}
		@XmlElement(name = "encoding",type = boolean.class)
		public boolean getIsPseudoHexEncoding() {
			return hexEncoding;
		}

		public void setIsPseudoHexEncoding(boolean hexEncoding) {
			this.hexEncoding = hexEncoding;
		}

		public void setPseudonymGenMethod(int pseudonymGenMethod) {
			this.pseudonymGenMethod = pseudonymGenMethod;
		}
		@XmlElement(name = "method",type = int.class)
		public int getPseudonymGenMethod() {
			return pseudonymGenMethod;
		}
		
		@XmlElement(name = "root-nai",type = boolean.class)
		public boolean getIsRootNai() {
			return rootNai;
		}
		
		public void setIsRootNai(boolean rootNai) {
			this.rootNai = rootNai;
		}
		
		@XmlElement(name = "aaa-id-in-root-nai",type = String.class)
		public String getAAAIdInRootNai() {
			return aaaIdInRootNai;
		}
		
		public void setAAAIdInRootNai(String aaaIdInRootNai) {
			this.aaaIdInRootNai = aaaIdInRootNai;
		}
	}

	@XmlType(propOrder = {})
	static class AKAPrimeFastReAuthDetail{

		public AKAPrimeFastReAuthDetail() {
			// required by Jaxb
		}
		
		private int fastReauthGenMethod = 0; 
		private String fastReauthPrefix = "6999";
		private boolean fastReauthHexEncoding = true;
		private boolean rootNai = false;
		private String aaaIdInRootNai = "";


		public void setFastReauthGenMethod(int method) {
			fastReauthGenMethod = method;
		}
		public void setIsFastReauthHexEncoding(boolean encoding) {
			fastReauthHexEncoding = encoding;
		}
		public void setFastReauthPrefix(String prefix) {
			fastReauthPrefix = prefix;
		}

		@XmlElement(name = "prefix",type = String.class)
		public String getFastReauthPrefix() {
			return fastReauthPrefix;
		}

		@XmlElement(name = "encoding",type = boolean.class)
		public boolean getIsFastReauthHexEncoding() {
			return fastReauthHexEncoding;
		}

		@XmlElement(name = "method",type = int.class)
		public int getFastReauthGenMethod() {
			return fastReauthGenMethod;
		}

		@XmlElement(name = "root-nai",type = boolean.class)
		public boolean getIsRootNai() {
			return rootNai;
		}
		
		public void setIsRootNai(boolean rootNai) {
			this.rootNai = rootNai;
		}
		
		@XmlElement(name = "aaa-id-in-root-nai",type = String.class)
		public String getAAAIdInRootNai() {
			return aaaIdInRootNai;
		}
		
		public void setAAAIdInRootNai(String aaaIdInRootNai) {
			this.aaaIdInRootNai = aaaIdInRootNai;
		}
	}
	
	@XmlType(propOrder = {})
	static class VendorSpecificCertificateDetail{

		private List<VendorSpecificCertificateImpl> vendorSpecificCertificateList;

		public VendorSpecificCertificateDetail(){
			// required By Jaxb.
			vendorSpecificCertificateList = new ArrayList<VendorSpecificCertificateImpl>();
		}

		@XmlElement(name = "vendor-specific-certificate")
		public List<VendorSpecificCertificateImpl> getVendorSpecificCertificateList() {
			return vendorSpecificCertificateList;
		}

		public void setVendorSpecificCertificateList(List<VendorSpecificCertificateImpl> vendorCertificateList) {
			this.vendorSpecificCertificateList = vendorCertificateList;
		}
	}
	
	@XmlType(propOrder = {})
	public static class SIMConfigurationImpl implements SIMConfiguration {

		private int tripletDS = 0;
		private int noOfTriplet =3;
		private int localHostPort = 0;
		private String localHostIp = "";
		private String localHostId = "";
		private int remoteHostPort = 0;
		private String remoteHostIp = "";
		private String remoteHostId = "";
		private SIMPseudoDetail pseudoDetail;
		private SIMFastReAuthDetail simFastReAuthDetail;

		public SIMConfigurationImpl(){
			pseudoDetail = new SIMPseudoDetail();
			simFastReAuthDetail = new SIMFastReAuthDetail();
		}
		@XmlElement(name ="pseudonym-conf")
		public SIMPseudoDetail getPseudoDetail() {
			return pseudoDetail;
		}
		public void setPseudoDetail(SIMPseudoDetail pseudoDetail) {
			this.pseudoDetail = pseudoDetail;
		}
		@XmlElement(name = "fast-reauth-conf")
		public SIMFastReAuthDetail getSimFastReAuthDetail() {
			return simFastReAuthDetail;
		}
		public void setSimFastReAuthDetail(SIMFastReAuthDetail simFastReAuthDetail) {
			this.simFastReAuthDetail = simFastReAuthDetail;
		}

		public void setPseudonymPrefix(String pseudonymPrefix) {
			this.pseudoDetail.setPseudonymPrefix(pseudonymPrefix);
		}

		public void setIsPseudoHexEncoding(boolean hexEncoding) {
			this.pseudoDetail.setIsPseudoHexEncoding(hexEncoding);
		}

		public void setPseudonymGenMethod(int pseudonymGenMethod) {
			this.pseudoDetail.setPseudoGenMethod(pseudonymGenMethod);
		}

		public void setTripletDS (int tripletDS){
			this.tripletDS = tripletDS;
		}

		public void setNoOFTriplet(int noOfTriplet){
			this.noOfTriplet = noOfTriplet;
		}

		public void setLocalHostPort(int localHostPort) {
			this.localHostPort = localHostPort;
		}

		public void setLocalHostIp(String localHostIp) {
			this.localHostIp = localHostIp;
		}

		public void setLocalHostId(String localHostId) {
			this.localHostId = localHostId;
		}

		public void setRemoteHostPort(int remoteHostPort) {
			this.remoteHostPort = remoteHostPort;
		}

		public void setRemoteHostIp(String remoteHostIp) {
			this.remoteHostIp = remoteHostIp;
		}

		public void setRemoteHostId(String remoteHostId) {
			this.remoteHostId = remoteHostId;
		}

		@Override
		@XmlTransient
		public String getLocalHost() {
			return " LOCAL_HOST " + this.localHostId + ":" + this.localHostPort + " [" + this.localHostIp +"]";
		}

		@Override
		@XmlTransient
		public String getRemoteHost() {
			return " REMOTE_HOST " + this.remoteHostId + ":" + this.remoteHostPort + " [" + this.remoteHostIp +"]";
		}

		@Override
		@XmlElement(name ="triplet-datasource",type =int.class,defaultValue ="0")
		public int getTripletDS() {
			return tripletDS;
		}

		@Override
		@XmlTransient
		public int getNumberOfTriplet() {
			return noOfTriplet;
		}

		public void setFastReauthGenMethod(int method) {
			this.simFastReAuthDetail.setFastReauthGenMethod(method);
		}
		public void setIsFastReauthHexEncoding(boolean encoding) {
			this.simFastReAuthDetail.setIsFastReauthHexEncoding(encoding);
		}
		public void setFastReauthPrefix(String prefix) {
			this.simFastReAuthDetail.setFastReauthPrefix(prefix);
		}

		@Override
		@XmlTransient
		public String getFastReauthPrefix() {
			return simFastReAuthDetail.getFastReauthPrefix();
		}

		@Override
		@XmlTransient
		public boolean getIsFastReauthHexEncoding() {
			return simFastReAuthDetail.getIsFastReauthHexEncoding();
		}

		@Override
		@XmlTransient
		public int getFastReauthGenMethod() {
			return simFastReAuthDetail.getFastReauthGenMethod();
		}
		@Override
		@XmlTransient
		public String getPseudoPrefix() {
			return pseudoDetail.getPseudonymPrefix();
		}
		@Override
		@XmlTransient
		public boolean getIsPseudoHexEncoding() {
			return pseudoDetail.getIsPseudoHexEncoding();
		}
		@Override
		@XmlTransient
		public int getPseudoGenMethod() {
			return pseudoDetail.getPseudoGenMethod();
		}
	}
	
}


