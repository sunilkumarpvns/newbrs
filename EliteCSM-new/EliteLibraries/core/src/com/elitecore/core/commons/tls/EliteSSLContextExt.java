package com.elitecore.core.commons.tls;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.tls.constant.TLSConnectionMode;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.*;

public class EliteSSLContextExt {
	
	private static final String MODULE = "ELITE-SSL-CNTX";
	private EliteSSLParameter sslParameter;
	private KeyStore keyStore;
	private SSLContext sslContext;
	private EliteTrustManager eliteTrustManager;
	private List<TLSVersion> tlsVersions;
	
	public EliteSSLContextExt(EliteSSLParameter sslParameter, EliteTrustManager eliteTrustManager){
		this.sslParameter = sslParameter;
		this.eliteTrustManager = eliteTrustManager;
		this.tlsVersions = new ArrayList<TLSVersion>();
	}
	
	public void init() throws InitializationFailedException{
		
		if(sslParameter.getMinTlsVersion() == null){
			throw new InitializationFailedException("Min TLS Version not found");
		}
		
		if(sslParameter.getMaxTlsVersion() == null){
			throw new InitializationFailedException("Max TLS Version not found");
		}
		
		for(TLSVersion tlsVersion : TLSVersion.values()){
			if(tlsVersion.compareTo(sslParameter.getMinTlsVersion()) >=0 &&
					tlsVersion.compareTo(sslParameter.getMaxTlsVersion()) <=0){
				tlsVersions.add(tlsVersion);
			}	
		}
		if(tlsVersions.size() == 0){
			throw new InitializationFailedException(
					"No TLS version recorded for Min-TLS-Version: " + sslParameter.getMinTlsVersion() + 
					" and Max-TLS-Version: " + sslParameter.getMaxTlsVersion());
		}
		
		Collections.reverse(tlsVersions);

		loadKeystore();
		loadServerCertificate();
		initSSLContext();
	}
	
	private void loadKeystore() throws InitializationFailedException{
		try {
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(null, "elitecore".toCharArray());
		}catch(Exception ex){
			throw new InitializationFailedException("Error loading keystore" , ex);
		}
	}
	
	private void loadServerCertificate() throws InitializationFailedException{
		try {
			
			ServerCertificateProfile serverCertificateProfile = sslParameter.getServerCertificateProfile();
			if(serverCertificateProfile == null){
				return;
			}
			
			PrivateKey privateKey = serverCertificateProfile.getPrivateKey();
			
			
			Collection<? extends Certificate> certificates = sslParameter.getServerCertificateProfile().getCertificates();
		
			
			Certificate [] serverCertificate = new Certificate[certificates.size()];
			
			serverCertificate = certificates.toArray(serverCertificate);
			
			keyStore.setKeyEntry("Pk_" + privateKey.hashCode(), privateKey, "elitecore".toCharArray(), serverCertificate);
			
		} catch (Exception e) {
			throw new InitializationFailedException("Error loading servercertificate in keystore", e);
		}
	}
	
	
	private void initSSLContext() throws InitializationFailedException{
		try{
			sslContext = SSLContext.getInstance(sslParameter.getMaxTlsVersion().version);
			
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, "elitecore".toCharArray());
			
			sslContext.init(keyManagerFactory.getKeyManagers(),  new EliteTrustManager[]{eliteTrustManager}, new SecureRandom());
			
		}catch (Exception e) {
			throw new InitializationFailedException("Error creating ssl context", e);
		}
	}
	
	public SSLServerSocketFactory getSSLServerSocketFactory(){ return sslContext.getServerSocketFactory(); }
	
	public SSLSocketFactory getSSLSocketFactory(){ return sslContext.getSocketFactory(); }
	
	public SSLSessionContext getClientSessionContext(){ return sslContext.getClientSessionContext(); }
	
	public SSLSessionContext getServerSessionContext(){ return sslContext.getServerSessionContext(); }
	
	public List<String> getEnabledCiphersuites() { return sslParameter.getEnabledCiphersuites(tlsVersions);}
	
	public boolean isClientCertificateRequestRequired() { return sslParameter.isClientCertificateRequestRequired(); }
	
	public EliteSSLParameter getEliteSSLParameter(){return sslParameter;}
	
	public EliteTrustManager getTrustManager() { return eliteTrustManager; }
	
	public List<TLSVersion> getEnabledTLSVersion() { return tlsVersions; }
	
	public SSLSession getSSLSession(TLSConnectionMode tlsConnectionMode){
		
		SSLSessionContext sslSessionContext = null;
		if(TLSConnectionMode.CLIENT == tlsConnectionMode){
			sslSessionContext = sslContext.getClientSessionContext();
		} else if(TLSConnectionMode.SERVER == tlsConnectionMode){
			sslSessionContext = sslContext.getServerSessionContext();
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to provide SSLSession. Reason: Invalid TLSConnection Mode");
			return null;
		}
		
		if(sslSessionContext == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Unable to provide SSLSession. Reason: SSL session context creation disabled by the SSLSocket");
			
			return null;
		}
		
		
		Enumeration<byte []> sessionIds = sslSessionContext.getIds();
		
		if(sessionIds == null || !sessionIds.hasMoreElements()){
			LogManager.getLogger().warn(MODULE, "Session-Id list is empty");
			return null;
		}
		
		return sslSessionContext.getSession(sessionIds.nextElement());
	}

	public SSLContext getSslContext() {
		return sslContext;
	}

	
	
}
