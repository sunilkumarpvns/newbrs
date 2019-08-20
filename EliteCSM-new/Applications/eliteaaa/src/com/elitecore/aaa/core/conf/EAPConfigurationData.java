/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 1st Sept 2010 by Pankit Shah
 *  
 */

package com.elitecore.aaa.core.conf;

import java.security.PrivateKey;
import java.util.List;

import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public interface EAPConfigurationData {
	
	public static final String LOCAL_HOST = "eap-ulticom-localhost";
	public static final String REMOTE_HOST = "eap-ulticom-remotehost";

	public TLSConfiguration getTLSConfiguration();
	public AKAConfiguration getAKAConfiguration();
	public AKAPrimeConfiguration getAKAPrimeConfiguration();
	public SIMConfiguration getSIMConfiguration();
	
	public long getSessionCleanupInterval();
	public long getSessionDurationForCleanup(); 
	public long getSessionTimeout();
	public long getMskRevalidationTime();
	public boolean getIsTreatInvalidPacketAsFatal(); 
	public int getDefaultNegotiationMethod();
	public int getDefaultPEAPNegotiationMethod();
	public int getDefaultTTLSNegotiationMethod(); 
	public List<Integer> getEnableAuthMethods();
	public boolean getIsNotificationSuccess(); 
	public boolean getIsNotificationFailure(); 
	public int getMaxEapPacketSize(); 
	
	
	
    interface TLSConfiguration{
		public ProtocolVersion getMinProtocolVersion();
		public ProtocolVersion getMaxProtocolVersion();
		public boolean getIsTlsCertificateRequest();
		public int getSessionResumptionDuration();
		public int getSessionResumptionLimit();
		public int getDefaultCompressionMethod();
		public CertificateConfiguration getCertificateConfiguration();
		public List<Integer> getCipherSuiteIDs();
		public boolean getValidateCertificateExpiry();
		public boolean getValidateCertificateRevocation();
		public boolean getValidateMac();
		public boolean getValidateClientCertificate();
    }
    
    interface CertificateConfiguration{
		public List<VendorSpecificCertificate> getVendorSpecificCertList(); 
		public List<Integer> getCertificateTypes();
		public PrivateKey getPrivateKey();
		public List<byte[]> getServerCertificateChain();
	}
    
    public interface VendorSpecificCertificate {
    	public String getOui(); 
    	public PrivateKey getVendorPrivateKey();
    	public List<byte[]> getServerCertificateChain();
    }

	public boolean getIsTTLSCertificateRequest();
	public boolean getIsPEAPCertificateRequest();
	
	 interface AKAConfiguration{
	    public String getLocalHost();
	    public String getRemoteHost();
	    public int getQuintupletDS();
	    public String getPseudonymPrefix();
		public boolean getIsPseudoHexEncoding();
		public int getPseudonymGenMethod();
	 }
	 
	 interface AKAPrimeConfiguration{
		    public String getLocalHost();
		    public String getRemoteHost();
		    public int getQuintupletDS();
		    
		    public String getPseudonymPrefix();
			public boolean getIsPseudoHexEncoding();
			public int getPseudonymGenMethod();
			public boolean getPseudonymIsRootNai();
			public String getPseudonymAAAIdInRootNai();
			
			public String getFastReauthPrefix();
			public boolean getIsFastReauthHexEncoding();
			public int getFastReauthGenMethod();
			public boolean getFastIsRootNai();
			public String getFastAAAIdInRootNai();
	 }
	 
	 interface SIMConfiguration{
		public int getTripletDS();
		public int getNumberOfTriplet();
	    public String getLocalHost();
	    public String getRemoteHost();
	    public String getPseudoPrefix();
		public boolean getIsPseudoHexEncoding();
		public int getPseudoGenMethod();
		public String getFastReauthPrefix();
		public boolean getIsFastReauthHexEncoding();
		public int getFastReauthGenMethod();
	 }
}
