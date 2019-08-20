package com.elitecore.coreeap.commons.configuration;

import java.security.PrivateKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.commons.util.TemporaryIdentityGenerator;
import com.elitecore.coreeap.commons.util.cipher.ICipherProvider;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.util.tls.KeyExchangeFactory;
import com.elitecore.coreeap.util.tls.SignatureFactory;

public interface IEapConfigurationContext extends IBaseEapContext{

	public boolean isTLSSendCertificateRequest();
	public boolean isTTLSSendCertificateRequest();
	public boolean isPEAPSendCertificateRequest();
	public List<byte[]> getServerCertificateChain();
	public List<byte[]> getServerCertificateChain(String oui);
	public PrivateKey getServerPrivateKey();
	public PrivateKey getServerPrivateKey(String oui);
	public int[] getSupportedCertificateTypes();
	public List<X509Certificate> getTrustedX509CertificatesList();
	public List<X509CRL> getCRLList();
	public List<Integer> getSupportedCiphersuiteIDs(); 
	public ICipherProvider getCipherProvider(CipherSuites cipherSuite, ProtocolVersion protocolVersion);
	public int getDefaultNegotiationMethod();
	public int getPEAPDefaultNegotiationMethod();
	public int getTTLSDefaultNegotiationMethod();
	public List<Integer> getEnabledAuthMethods();
	public int getFragmentSize();
	public boolean isTestMode();
	public int getSessionResumptionLimit();
	public int getNoOfSIMTriplets();
	public int getTripletDS();
	public int getTripletDSForAKA();
	public String getLocalHostForSIM();
	public String getRemoteHostForSIM();
	public String getLocalHostForAKA();
	public String getRemoteHostForAKA();
	public long getRequestTimeoutForSIM();
	public long getRequestTimeoutForAKA();
	public int getMAPGWConnectionPoolSizeForSIM();
	public int getMAPGWConnectionPoolSizeForAKA();
	public TemporaryIdentityGenerator getTemporaryIdentityGenerator();
	public int getPseudoIdentityMethod(int eapMethod);
	public String getPseudoIdentityPrefix(int eapMethod);
	public boolean isPseudoHexEncodingSupported(int eapMethod);
	public int getFastReauthIdentityMethod(int eapMethod);
	public String getFastReauthIdentityPrefix(int eapMethod);
	public boolean isFastReauthHexEncodingSupported(int eapMethod);
	public int getEAPAttributeID();
	public ProtocolVersion getMinProtocolVersion();
	public ProtocolVersion getMaxProtocolVersion();
	public KeyExchangeFactory getKeyExchangeFactory();
	public SignatureFactory getSignatureFactory();
	public boolean isValidateCertificateExpiry();
	public boolean isValidateCertificateRevocation();
	public boolean isValidateMac();
	public boolean isValidateClientCertificate();
	public int getMaxSIMReauthCount();
}	

