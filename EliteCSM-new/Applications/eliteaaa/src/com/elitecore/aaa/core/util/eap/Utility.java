package com.elitecore.aaa.core.util.eap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.elitecore.commons.logging.LogManager;

public class Utility {
	private static final String MODULE = "UTILITY";
	
	
	/**
	 * Generates bytes from a certificate.
	 * RFC 2459 - Internet X.509 Public Key Infrastructure Certificate and CRL Profile
	 * @param file
	 * @return
	 */
	
	public static  byte[] convertCertificateToBytes(String file) {
		byte [] result = null;
	
		if(file!=null){
			try {
				file = file.trim();
				FileInputStream fis = new FileInputStream(file);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				X509Certificate x509Cert = (X509Certificate)cf.generateCertificate(fis);
				fis.close();
				result = x509Cert.getEncoded();
			} catch (Exception e) {
				LogManager.getLogger().trace(MODULE, "Error while generating X509Certificate, reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}else{
			LogManager.getLogger().debug(MODULE, "Problen while generating X509Certificate, reason: file name not given");
		}
		
		return result;
	}
	
	public static  X509Certificate getX509Certificate(String file) {
		FileInputStream fis = null;
		X509Certificate x509Cert = null;
		if(file!=null){
			try {
				file = file.trim();
				fis = new FileInputStream(file);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				x509Cert = (X509Certificate)cf.generateCertificate(fis);
				fis.close();
			} catch (Exception e) {
				LogManager.getLogger().trace(MODULE, "Error while generating X509Certificate, reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}else{
			LogManager.getLogger().debug(MODULE, "Problem while generating X509Certificate, reason: file name not given");
		}
			
		return x509Cert;
	}
	
	public static PrivateKey getPrivatekeyFormCertificate(String privateKeyFile, String certificateFile, String signatureAlgorithm){
		PrivateKey priKey = null;
		if(privateKeyFile!=null){
			try {
				privateKeyFile = privateKeyFile.trim();
	            BufferedInputStream inFile = new BufferedInputStream(new FileInputStream(privateKeyFile));
	            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	            byte[] buffer = new byte[1024];
	            int bytesRead = 0;
	            
	            while((bytesRead = inFile.read(buffer)) != -1) {
	            	outStream.write(buffer,0,bytesRead);
	            }
	            inFile.close();
	            outStream.close();
	            
	            javax.crypto.EncryptedPrivateKeyInfo encPriKey = new javax.crypto.EncryptedPrivateKeyInfo(outStream.toByteArray());
	            Cipher cipher = Cipher.getInstance(encPriKey.getAlgName()); 
	            PBEKeySpec pbeKeySpec = new PBEKeySpec("elitecore".toCharArray());
	            SecretKeyFactory secKeyFactory = SecretKeyFactory.getInstance(encPriKey.getAlgName());
	            cipher.init(Cipher.DECRYPT_MODE, secKeyFactory.generateSecret(pbeKeySpec), encPriKey.getAlgParameters());
	            PKCS8EncodedKeySpec keysp = encPriKey.getKeySpec(cipher);
	            
	            /***
	             * 	The Signature algorithm used here is same as it is in public key.
	             * 	signatureAlgorithm parameter is taken from server certificate's public key.
	             */
	            
	            KeyFactory kf = KeyFactory.getInstance(signatureAlgorithm);
            	priKey = kf.generatePrivate (keysp);
	                        
	        } catch (Exception ex) {
				LogManager.getLogger().warn(MODULE, "Error getting private key from file " + privateKeyFile + ", reason : " + ex.getMessage());
	        }
		}else {
			LogManager.getLogger().debug(MODULE, "Problem while getting private key  reason : Private key file name not given");
		}

		return priKey;
	}

	public static X509CRL getX509CRL(String crlPath) {
		X509CRL crl = null;
		if(crlPath!=null){
			try {
				crlPath = crlPath.trim();
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				FileInputStream fis = new FileInputStream(crlPath);
				crl = (X509CRL)cf.generateCRL(fis);
				fis.close();
			} catch (Exception e) {
				LogManager.getLogger().trace(MODULE, "Error while generating X509CRL, reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}else {
			LogManager.getLogger().debug(MODULE, "Problem while generating X509CRL, reason: File name not given");
		}
		return crl;
	}
	
	
	public static long bytesToLong(byte[] bytes){
		if(bytes == null)
			return 0;
		// j is used to get only first 8 bytes from given byte array
		int j=0;
		long val = 0L;
		for(byte b : bytes){
			j++;
			if(j<=8){
				val <<= 8;
				val |= b & 0xFF;
			}	
		}
		return val;
	}

}
 
