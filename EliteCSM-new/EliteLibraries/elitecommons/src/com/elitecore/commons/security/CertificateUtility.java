package com.elitecore.commons.security;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.elitecore.commons.io.Files;

/**
 * 
 * A set of utility methods for X.509 certificates.
 * 
 * @author malav.desai
 * @author narendra.pathai
 *
 */
public class CertificateUtility {
	
	private static final String PLAINTEXT_FOR_VERIFICATION = "elitecoreelitecoreel";
	public static final String MODULE = "CERTIFICATE_UTILITY";
	public static final String X509_CERTIFICATE_TYPE = "X.509";
	
	/**
	 * Generate X.509 certificates from bytes. May return a list with single certificate. Use {@code size()} method to find 
	 * exact no of certificates generated. 
	 * 
	 * <p> Bytes can be DER encoded certificates or PKCS#7 certificate chain bytes.
	 * 
	 * @see CertificateFactory#generateCertificate(InputStream)
	 * @see CertificateFactory#generateCertificates(InputStream) 
	 * @param certificateBytes non-null bytes from which certificates are to be generated
	 * @return list of X509 certificates (possibly empty)
	 * @throws CertificateException if there is any parsing exception
	 */
	public static Collection<? extends X509Certificate> generateCertificates(byte[] certificateBytes) throws CertificateException {
		checkNotNull(certificateBytes, "certificateBytes are null");
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(certificateBytes);
		return generateCertificates(inputStream);
	}
	
	/**
	 * Generate X.509 certificates from input stream. May return a list with single certificate. Use {@code size()} method to find 
	 * exact no of certificates generated. 
	 * 
	 * <p> {@code certificateStream} may contain DER encoded certificates or PKCS#7 certificate chain bytes.
	 * 
	 * @see CertificateFactory#generateCertificate(InputStream)
	 * @see CertificateFactory#generateCertificates(InputStream) 
	 * @param certificateStream a non-null input stream from which certificates are to be generated
	 * @return list of X509 certificates (possibly empty)
	 * @throws CertificateException if there is any parsing exception
	 */
	@SuppressWarnings("unchecked")
	public static Collection<? extends X509Certificate> generateCertificates(InputStream certificateStream) throws CertificateException {
		checkNotNull(certificateStream, "certificateStream is null");
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
		return (Collection<? extends X509Certificate>) certificateFactory.generateCertificates(certificateStream);
	}
	
	/**
	 * Generate X.509 certificates from file. May return a list with single certificate. Use {@code size()} method to find 
	 * exact no of certificates generated. 
	 * 
	 * <p> {@code certificateFile} may contain DER encoded certificates or PKCS#7 certificate chain bytes.
	 * 
	 * @see CertificateFactory#generateCertificate(InputStream)
	 * @see CertificateFactory#generateCertificates(InputStream) 
	 * @param certificateFile a non-null file from which certificates are to be generated
	 * @return list of X509 certificates (possibly empty)
	 * @throws CertificateException if there is any parsing exception
	 * @throws FileNotFoundException if file does not exist
	 * @throws IllegalArgumentException if {@code certificateFile} is a directory
	 */
	public static Collection<? extends X509Certificate> generateCertificates(File certificateFile) throws CertificateException, FileNotFoundException {
		checkNotNull(certificateFile, "certificateFile is null");
		checkArgument(certificateFile.isFile(), certificateFile.getPath() + " is not a file");
		
		return generateCertificates(new FileInputStream(certificateFile));
	}
	
	/**
	 * Generate X.509 certificates from given path. May return a list with single certificate. Use {@code size()} method to find 
	 * exact no of certificates generated. 
	 * 
	 * @see CertificateFactory#generateCertificate(InputStream)
	 * @see CertificateFactory#generateCertificates(InputStream) 
	 * @param certificateFilePath a non-null path to file from which certificates are to be generated
	 * @return list of X509 certificates (possibly empty)
	 * @throws CertificateException if there is any parsing exception
	 * @throws FileNotFoundException if no file exists at given path
	 * @throws IllegalArgumentException if {@code certificateFile} is a directory
	 */
	public static Collection<? extends X509Certificate> generateCertificates(String certificateFilePath) throws CertificateException, FileNotFoundException {
		checkNotNull(certificateFilePath, "certificatePath is null");

		return generateCertificates(new File(certificateFilePath));
	}
	
	/**
	 * Checks whether the certificate is a self signed certificate. A self signed certificate
	 * is the one in which the issuer and the subject are same entities. 
	 * <p>
	 * No <i>Certificate Authority (CA)</i> is involved in signing.
	 * 
	 * @param certificate a non-null X509 certificate which is to be checked
	 * @return true if certificate is self signed, false otherwise
	 */
	public static boolean isSelfSigned(X509Certificate certificate) {
		checkNotNull(certificate, "certificate is null");
		
		return certificate.getSubjectDN().getName().equals(certificate.getIssuerDN().getName());
	}
	
	/**
	 * Checks whether certificate list is a chain of certificates.
	 * 
	 * <p>
	 * NOTE: This method <i>does not check</i> validity of the chain.
	 * 
	 * @param certificateList a non-null list of certificates to be checked
	 * @return true if certificates represent a chain false otherwise
	 */
	public static boolean isChain(Collection<? extends Certificate> certificateList) {
		checkNotNull(certificateList, "certificateList is null");
		
		return certificateList.size() > 1;
	}
	
	/**
	 * Generates a PKCS8 encoded private key from given private key bytes.
	 * 
	 * <p>
	 * Private key can be generated from either encrypted or decrypted bytes.
	 * If the private key bytes represent already decrypted bytes then {@code password}
	 * should be either {@code null} or empty string.
	 * 
	 * @param privateKeyBytes non-null PKCS8 encoded bytes from which private key is to be generated
	 * @param password password of private key if encrypted. {@code null} or empty string if private key bytes are not encrypted. 
	 * @param privateKeyAlgo non-null string representing algorithm of the private key (RSA or DSA)
	 * @return {@link PrivateKey} object representing the {@code privateKeyBytes}
	 * 
	 * @throws IOException refer {@link EncryptedPrivateKeyInfo#EncryptedPrivateKeyInfo(byte[])}
	 * @throws NoSuchAlgorithmException refer {@link Cipher#getInstance(String)}
	 * @throws NoSuchPaddingException refer {@link Cipher#getInstance(String)}
	 * @throws InvalidKeyException refer {@link Cipher#init(int, java.security.Key, java.security.AlgorithmParameters)}
	 * @throws InvalidAlgorithmParameterException refer {@link Cipher#init(int, java.security.Key, java.security.AlgorithmParameters)}
	 * @throws InvalidKeySpecException refer {@link KeyFactory#generatePrivate(java.security.spec.KeySpec)}
	 */
	public static PrivateKey generatePrivateKey(byte [] privateKeyBytes, String password, String privateKeyAlgo) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {
		checkNotNull(privateKeyBytes, "privateKeyBytes are null");
		checkNotNull(privateKeyAlgo, "privateKeyAlgo is null");
		
		PKCS8EncodedKeySpec keysp = generatePKCS8EncodedKeySpec(privateKeyBytes, password);
		
		KeyFactory kf = KeyFactory.getInstance(privateKeyAlgo);
		return kf.generatePrivate(keysp);
	}
	
	/**
	 * Generates a PKCS8 encoded private key from given private key file.
	 * 
	 * <p>
	 * Private key can be generated from either encrypted or decrypted file.
	 * If the private key file represents an already decrypted private key file
	 * then {@code password} should be either {@code null} or empty string.
	 * 
	 * @param privateKeyFile non-null PKCS8 encoded file from which private key is to be generated
	 * @param password password of private key if encrypted. {@code null} or empty string if private key bytes are not encrypted. 
	 * @param privateKeyAlgo non-null string representing algorithm of the private key (RSA or DSA)
	 * @return {@link PrivateKey} object representing the {@code privateKeyBytes}
	 * 
	 * @throws IllegalArgumentException if {@code privateKeyFile} is not a file
	 * @throws IOException refer {@link EncryptedPrivateKeyInfo#EncryptedPrivateKeyInfo(byte[])}
	 * @throws NoSuchAlgorithmException refer {@link Cipher#getInstance(String)}
	 * @throws NoSuchPaddingException refer {@link Cipher#getInstance(String)}
	 * @throws InvalidKeyException refer {@link Cipher#init(int, java.security.Key, java.security.AlgorithmParameters)}
	 * @throws InvalidAlgorithmParameterException refer {@link Cipher#init(int, java.security.Key, java.security.AlgorithmParameters)}
	 * @throws InvalidKeySpecException refer {@link KeyFactory#generatePrivate(java.security.spec.KeySpec)}
	 */
	public static PrivateKey generatePrivateKey(File privateKeyFile, String password, String privateKeyAlgo) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException {
		
		checkNotNull(privateKeyFile, "privateKeyFile is null");
		checkArgument(privateKeyFile.isFile(), privateKeyFile.getPath() + " is not a file");
		
		byte[] privateKeyBytes = Files.readFully(privateKeyFile);
		
		return generatePrivateKey(privateKeyBytes, password, privateKeyAlgo);
	}
	
	/**
	 * Generates a PKCS8 encoded private key from given private key path.
	 * 
	 * <p>
	 * Private key can be generated from either encrypted or decrypted file.
	 * If the path to private key represents an already decrypted private key file
	 * then {@code password} should be either {@code null} or empty string.
	 * 
	 * @param privateKeyFilePath non-null path to a PKCS8 encoded file from which private key is to be generated
	 * @param password password of private key if encrypted. {@code null} or empty string if private key bytes are not encrypted. 
	 * @param privateKeyAlgo non-null string representing algorithm of the private key (RSA or DSA)
	 * @return {@link PrivateKey} object representing the {@code privateKeyBytes}
	 * 
	 * @throws IllegalArgumentException if {@code privateKeyPath} is not a file
	 * @throws IOException refer {@link EncryptedPrivateKeyInfo#EncryptedPrivateKeyInfo(byte[])}
	 * @throws NoSuchAlgorithmException refer {@link Cipher#getInstance(String)}
	 * @throws NoSuchPaddingException refer {@link Cipher#getInstance(String)}
	 * @throws InvalidKeyException refer {@link Cipher#init(int, java.security.Key, java.security.AlgorithmParameters)}
	 * @throws InvalidAlgorithmParameterException refer {@link Cipher#init(int, java.security.Key, java.security.AlgorithmParameters)}
	 * @throws InvalidKeySpecException refer {@link KeyFactory#generatePrivate(java.security.spec.KeySpec)}
	 */
	public static PrivateKey generatePrivateKey(String privateKeyFilePath, String password, String privateKeyAlgo) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException {
		checkNotNull(privateKeyFilePath, "privateKeyPath is null");
		
		return generatePrivateKey(new File(privateKeyFilePath), password, privateKeyAlgo);
	}

	private static PKCS8EncodedKeySpec generatePKCS8EncodedKeySpec(byte[] privateKeyBytes, String password) throws IOException,NoSuchAlgorithmException, NoSuchPaddingException,InvalidKeyException, InvalidAlgorithmParameterException,InvalidKeySpecException {
		
		PKCS8EncodedKeySpec keysp = null;
		if(password != null && !"".equals(password)){
			EncryptedPrivateKeyInfo encPriKey = new EncryptedPrivateKeyInfo(privateKeyBytes);
			Cipher cipher = Cipher.getInstance(encPriKey.getAlgName());
			PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
			SecretKeyFactory secKeyFactory = SecretKeyFactory.getInstance(encPriKey.getAlgName());
			cipher.init(Cipher.DECRYPT_MODE, secKeyFactory.generateSecret(pbeKeySpec), encPriKey.getAlgParameters());
			keysp = encPriKey.getKeySpec(cipher);
		} else {
			keysp = new PKCS8EncodedKeySpec(privateKeyBytes);
		}
		return keysp;
	}
	
	/**
	 * Validate the public private key pair. Validation is done by signing a test string using
	 * private key and then verifying the signature using the public key provided.
	 * 
	 * @param publicKey a non-null public key or public certificate
	 * @param privateKey a non-null private key for the certificate provided
	 * @param signatureAlgorithm a non-null signature algorithm (either NONEWithRSA or NONEWithDSA)
	 * @return true if public private key form a valid pair
	 * @throws InvalidKeyException refer {@link Signature#initSign(String)} and {@link Signature#initVerify(PublicKey)}
	 * @throws NoSuchAlgorithmException refer {@link Signature#getInstance(String)}
	 * @throws SignatureException refer {@link Signature#verify(byte[])}
	 */
	public static boolean validate(Certificate publicKey,PrivateKey privateKey, String signatureAlgorithm) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		checkNotNull(publicKey, "publicKey is null");
		checkNotNull(privateKey, "privateKey is null");
		checkNotNull(signatureAlgorithm, "signatureAlgorithm is null");
		
		byte[] encryptedData = sign(PLAINTEXT_FOR_VERIFICATION.getBytes(), signatureAlgorithm, privateKey);
		return verify(encryptedData, PLAINTEXT_FOR_VERIFICATION.getBytes(), publicKey.getPublicKey(), signatureAlgorithm);
	}
	
	private static byte[] sign(byte[] plainBytes, String algorithm, PrivateKey serverPrivateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance(algorithm);
		signature.initSign(serverPrivateKey);
		signature.update(plainBytes);
		return signature.sign();
	}

	private static boolean verify(byte[] signedBytes, byte[] verifyBytes, PublicKey publicKey, String algorithm) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance(algorithm);
		signature.initVerify(publicKey);
		signature.update(verifyBytes);
		return signature.verify(signedBytes); 
	}
}
