package com.elitecore.core.commons.fileio.loactionalloactor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.FTPSSocketFactory;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class FTPSFileAllocator extends FTPFileAllocator {

	public static final String MODULE = "FTPS_FILE_ALLOCATOR";
	private FTPSClient ftpsClient;
	private String protocol = "TLS"; // SSL/TLS
	private static final String KEYSTORE_PASS = "complexinc";
	private static final String KEYSTORE_FILE_NAME ="/home/rudradutt/certs/key1.keystore";// "/home/rudradutt/vsftpd.pem";
	
	
	public FTPSClient getSocketClient() {
		return ftpsClient;
	}
	
	@Override
	public boolean connect() throws FileAllocatorException {
		
		if(getSocketClient() == null){
			try {
				ftpsClient = new FTPSClient(protocol, false);
				
				ftpsClient.setRemoteVerificationEnabled(false);
				SSLContext sslContext = getSSLContext();
				FTPSSocketFactory sf = new FTPSSocketFactory(sslContext);
				ftpsClient.setSocketFactory(sf);
				ftpsClient.setBufferSize(1000);
				KeyManager keyManager = getKeyManagers()[0];
				TrustManager trustManager = getTrustManagers()[0];
				ftpsClient.setControlEncoding("UTF-8");
	 
				ftpsClient.setKeyManager(keyManager);
				ftpsClient.setTrustManager(trustManager);
							
				ftpsClient.addProtocolCommandListener(new PrintCommandListener(
						new PrintWriter(System.out)));
	 
				ftpsClient.connect(getAddress(), getPort());
	 
				LogManager.getLogger().info(MODULE, "Connected to " + getAddress());
	 
				if (!FTPReply.isPositiveCompletion(ftpsClient.getReplyCode())) {
					ftpsClient.disconnect();
					LogManager.getLogger().error(MODULE, "FTP server refused connection.");
					return false;
				}
				if (!ftpsClient.login(getUser(), getPassword())) {
					ftpsClient.logout();
					return false;
				}

				
				
//				ftpsClient.connect(getAddress(),getPort());
//				ftpsClient.login(getUser(), getPassword());
			} catch (SocketException e) {
				LogManager.getLogger().trace(MODULE, "Error getting Connection. Reason : "+e);
				throw new FileAllocatorException("Error in getting Connection : reason : "+e);
			} catch (IOException e) {
				LogManager.getLogger().trace(MODULE, "Error getting Connection. Reason : "+e);
				throw new FileAllocatorException("Error in getting Connection : reason : "+e);
			} catch (NoSuchAlgorithmException e) {
				LogManager.getLogger().trace(MODULE, "Error getting Connection. Reason : "+e);
				throw new FileAllocatorException("Error in getting Connection : reason : "+e);
			} catch (KeyManagementException e) {
				LogManager.getLogger().trace(MODULE, "Error getting Connection. Reason : "+e);
				throw new FileAllocatorException("Error in getting Connection : reason : "+e);
			} catch (UnrecoverableKeyException e) {
				LogManager.getLogger().trace(MODULE, "Error getting Connection. Reason : "+e);
				throw new FileAllocatorException("Error in getting Connection : reason : "+e);
			} catch (KeyStoreException e) {
				LogManager.getLogger().trace(MODULE, "Error getting Connection. Reason : "+e);
				throw new FileAllocatorException("Error in getting Connection : reason : "+e);
			} catch (CertificateException e) {
				LogManager.getLogger().trace(MODULE, "Error getting Connection. Reason : "+e);
				throw new FileAllocatorException("Error in getting Connection : reason : "+e);
			}
		}
		if(!getPermission()) {
			disconnect();
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "FTP server : "+getAddress()+":"+getPort()+" refused connection. Reason : "+ftpsClient.getReplyString());
			LogManager.getLogger().trace(MODULE, "FTP server refused connection. Reason : "+ftpsClient.getReplyString());
			return false;
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "FTPS server : "+getAddress()+":"+getPort()+" connected. Reason : "+ftpsClient.getReplyString());
			return true;
		}
	}
	
	
	private static SSLContext getSSLContext() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, UnrecoverableKeyException, IOException {
		TrustManager[] tm = getTrustManagers();
        System.out.println("Init SSL Context");
        SSLContext sslContext = SSLContext.getInstance("SSLv3");
		sslContext.init(null, tm, null);
 
		return sslContext;
	}
	private static KeyManager[] getKeyManagers() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException {
		KeyStore ks = KeyStore.getInstance("jks");
        ks.load(new FileInputStream(KEYSTORE_FILE_NAME), KEYSTORE_PASS.toCharArray());
 
		KeyManagerFactory tmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        tmf.init(ks, KEYSTORE_PASS.toCharArray());
 
		return tmf.getKeyManagers();
	}
	private static TrustManager[] getTrustManagers() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException {
		KeyStore ks = KeyStore.getInstance("jks");
		ks.load(null, KEYSTORE_PASS.toCharArray());
		
//        ks.load(new FileInputStream(KEYSTORE_FILE_NAME), KEYSTORE_PASS.toCharArray());
 
		ks.store(new FileOutputStream ( KEYSTORE_FILE_NAME  ),
                KEYSTORE_PASS.toCharArray());
        ks.load(new FileInputStream ( KEYSTORE_FILE_NAME ),
        		KEYSTORE_PASS.toCharArray());
		
        FileInputStream fis = null;
		X509Certificate x509Cert = null;
		try {
			fis = new FileInputStream(new File("/home/rudradutt/certs/vsftpd.pem"));
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			x509Cert = (X509Certificate)cf.generateCertificate(fis);
		} catch (Exception e) {
			e.printStackTrace();
//			Logger.logTrace(MODULE, "Error while generating X509Certificate, reason: " + e.getMessage());
//			Logger.logTrace(MODULE, e);
		}finally{
			fis.close();
		}
        
		ks.setCertificateEntry("rudradutt", x509Cert);
		ks.store(new FileOutputStream ( KEYSTORE_FILE_NAME  ),KEYSTORE_PASS.toCharArray());
		
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
 
		return tmf.getTrustManagers();
	}
}
