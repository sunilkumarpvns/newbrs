package com.elitecore.client.configuration;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.config.util.FileUtil;
import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;

@ConfigurationProperties(moduleName = "EAP-CONFIG", readWith = XMLReader.class, writeWith = XMLWriter.class, reloadWith = XMLReader.class, synchronizeKey = "")
@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "eap-config")
//TODO remove all other configurations
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "cert")


//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttPap")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttPapU")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttPapP")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttChap")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttChapU")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttChapP")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttMschap")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttMschapU")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttMschapP")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttMschap2")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttMschap2U")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttMschap2P")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ppMd5")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ppMd5P")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ppTls")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ppMschap2")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ppMschap2P")



//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttMschap2")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttMschap2U")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttMschap2P")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ppTls")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ppMschap2")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ppMschap2P")


//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "tls11")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "tls12")


//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "tls")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ttPap")
//@XMLProperties(configDirectories = {"conf"}, schemaDirectories = {"schema"}, name = "ppMd5")

/**
 * @author Kuldeep Panchal
 * @author Malav Desai
 */
@XmlRootElement(name = "eap-config")
@XmlType(propOrder = {})
public class EapConfiguration extends Configurable{
	private static final String MODULE = "EAP_CONFIGURATION";
	private String serverIp;
	private String clietnIp;
	private int port;
	private String secret;
	private String username;
	private int requestTimeoutInSecs;
	private String eapMethod;
	private String identity;
	private TlsConfiguration tls;
	private PeapConfiguration peap;
	private TtlsConfiguration ttls;
	
	public EapConfiguration() {
		
	}
	
	@XmlElement(name = "server-address")
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	
	@XmlElement(name = "client-address")
	public String getClietnIp() {
		return clietnIp;
	}

	public void setClietnIp(String clietnIp) {
		this.clietnIp = clietnIp;
	}
	
	//TODO rename before commit
	@XmlElement(name = "server-port")
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	@XmlElement(name = "secret")
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	@XmlElement(name = "user-name")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@XmlElement(name = "request-timeout-in-secs", type = int.class)
	public int getRequestTimeoutInSecs() {
		return requestTimeoutInSecs;
	}

	public void setRequestTimeoutInSecs(int requestTimeoutInSecs) {
		this.requestTimeoutInSecs = requestTimeoutInSecs;
	}

	
	@XmlElement(name = "eap-method")
	public String getEapMethod() {
		return eapMethod;
	}
	public void setEapMethod(String eapMethod) {
		this.eapMethod = eapMethod;
	}
	
	@XmlElement(name = "tls")
	public TlsConfiguration getTls() {
		return tls;
	}
	public void setTls(TlsConfiguration tls) {
		this.tls = tls;
	}
	
	@XmlElement(name = "peap")
	public PeapConfiguration getPeap() {
		return peap;
	}
	public void setPeap(PeapConfiguration peap) {
		this.peap = peap;
	}
	
	@XmlElement(name = "ttls")
	public TtlsConfiguration getTtls(){
		return ttls;
	}
	public void setTtls(TtlsConfiguration ttls){
		this.ttls = ttls;
	}
	
	@XmlElement(name = "identity")
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}

	@PostRead
	public void postReadProcessing() throws Exception{
		postReadProcessingForTLS();
	}
	
	public void postReadProcessingForTLS() throws Exception {

		final String certificateFileName = getTls().getCertificateName().trim();
		final String privateKeyFileName = getTls().getPrivateKeyName().trim();
		final String privateKeyPassword = getTls().getPrivateKeyPassword().trim();
	
		final List<X509Certificate> caCertificateList = generateCACertificates(getAbsolutePath("cert" + File.separator + "trustedcertificates"));
		final List<X509CRL> crlList = generateCRLs(getAbsolutePath("cert" + File.separator + "crl"));
		
		String certificatePath = getAbsolutePath("cert" + File.separator + "client" + File.separator + certificateFileName);
		String privateKeyPath = getAbsolutePath("cert" + File.separator + "private" + File.separator + privateKeyFileName);

		final byte[] clientCertificateBytes = FileUtil.readBytesFully(certificatePath);
		final byte[] privateKeyBytes = FileUtil.readBytesFully(privateKeyPath);

		final List<Certificate> certificates = generateCerficates(clientCertificateBytes);
		LogManager.getLogger().info(MODULE, "Adding server certificate: " + certificateFileName);

		PrivateKeyAlgo privateKeyAlgo = PrivateKeyAlgo.fromAlgoName(certificates.get(0).getPublicKey().getAlgorithm());
		if(privateKeyAlgo == null){
			LogManager.getLogger().warn(MODULE, "Problem in fetching private key algorithm, Reason: privateKeyAlgo is null");
			throw new IllegalArgumentException("Private key algorithm must not be null");
		}
		final PrivateKey privateKey = generatePrivateKey(privateKeyBytes, privateKeyPassword, privateKeyAlgo);
		LogManager.getLogger().info(MODULE, "Adding private key: " + privateKeyFileName);

		getTls().setPrivatekeyFileBytes(privateKeyBytes);
		getTls().setPrivateKeyAlgo(privateKeyAlgo);
		getTls().setPrivateKey(privateKey);
		getTls().setCertificates(certificates);
		getTls().setCertificateFileBytes(clientCertificateBytes);
		getTls().setCACertificates(caCertificateList);
		getTls().setCRLs(crlList);
	}
	
	private String getAbsolutePath(String relativePath) {
		return System.getenv().get("UDP_CLIENT_HOME") + File.separator + relativePath;
	}
	
	private List<Certificate> generateCerficates(byte [] certificateBytes) throws CertificateException {
		
		if(certificateBytes == null){
			throw new IllegalArgumentException("certificate bytes are null");
		}
		
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			ByteArrayInputStream inputStream = new ByteArrayInputStream(certificateBytes);
			return (new ArrayList<Certificate>(cf.generateCertificates(inputStream)));
		} catch (CertificateException e) {
			LogManager.getLogger().trace(MODULE, "Problem in loading client certificate, Reason: " + e.getMessage());
			throw e;
		}
	}

	private PrivateKey generatePrivateKey(byte [] privateKeyBytes, String password, PrivateKeyAlgo algo) throws Exception {

		if(privateKeyBytes == null){
			throw new IllegalArgumentException("private key is null");
		}

		PrivateKey priKey = null;
		PKCS8EncodedKeySpec keysp = null;
		try {
			if(password != null && !"".equals(password)){
				javax.crypto.EncryptedPrivateKeyInfo encPriKey = new javax.crypto.EncryptedPrivateKeyInfo(privateKeyBytes);
				Cipher cipher = Cipher.getInstance(encPriKey.getAlgName());
				PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
				SecretKeyFactory secKeyFactory = SecretKeyFactory.getInstance(encPriKey.getAlgName());
				cipher.init(Cipher.DECRYPT_MODE, secKeyFactory.generateSecret(pbeKeySpec), encPriKey.getAlgParameters());
				keysp = encPriKey.getKeySpec(cipher);
			}else{
				keysp = new PKCS8EncodedKeySpec(privateKeyBytes);
			}

			KeyFactory kf = KeyFactory.getInstance(algo.name);
			priKey = kf.generatePrivate (keysp);
			
		} catch(Exception e) {
			LogManager.getLogger().error(MODULE, "Error in creating private key, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
			throw e;
		}
		
		return priKey;

	}
	
	public List<X509Certificate> generateCACertificates(String trustedCAPath){
		List<X509Certificate> certificateList = new ArrayList<X509Certificate>();

		try {
			List<File> files = FileUtil.getRecursiveFileFromPath(trustedCAPath);
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			
			for(File file : files){
				FileInputStream fisForCert = null;
				try{
					fisForCert = new FileInputStream(file);
					X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(fisForCert);
					certificateList.add(certificate);
					LogManager.getLogger().info(MODULE, "Adding CA Certificate with subject name: " + certificate.getSubjectDN().getName() +" trusted certificate list");
				} catch (CertificateException e) {
					LogManager.getLogger().error(MODULE, "Problem in loading Trusted CA Certificate: " + file.getName() + " , Reason: " + e.getMessage());
					LogManager.getLogger().trace(e);
				} finally{
					FileUtil.closeQuietly(fisForCert);
				}
			}
		} catch(Exception e) {
			LogManager.getLogger().error(MODULE, "Problem in scanning Trusted CA Certificates, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		} 
		
		return certificateList;
	}

	@SuppressWarnings("unchecked")
	public List<X509CRL> generateCRLs(String crlPath) {
		List<X509CRL> crlList = new ArrayList<X509CRL>();
		try {
			List<File> files = FileUtil.getRecursiveFileFromPath(crlPath);
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			
			for(File file : files){
				FileInputStream fisForCRL = null;
				try{
					fisForCRL = new FileInputStream(file);
					Collection<X509CRL> crls = (Collection<X509CRL>) certificateFactory.generateCRLs(fisForCRL);
					crlList.addAll(crls);
					LogManager.getLogger().debug(MODULE, "Adding CRL with name: " + file.getName());
				} catch (CRLException e) {
					LogManager.getLogger().error(MODULE, "Problem in loading CRL: " + file.getName() + " , Reason: " + e.getMessage());
					LogManager.getLogger().trace(e);
				} finally{
					FileUtil.closeQuietly(fisForCRL);
				}
			}
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Problem in scanning CRL, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
		
		return crlList;
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}
	
	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		
		out.println("\n\tServer IP              : " + serverIp);
		out.println("\n\tClient IP              : " + clietnIp);
		out.println("\n\tServer Port            : " + port);
		out.println("\n\tUser Name              : " + username);
		out.println("\n\tSecret                 : " + secret);
		out.println("\n\tRequest Timeout (secs) : " + requestTimeoutInSecs);
		out.println("\n\tEAP Method             : " + eapMethod);
		out.println("\n\tIdentity               : " + identity);
		out.println("\nTLS Configuration:" + tls);
		out.println("PEAP Configuration:" + peap);
		out.println("TTLS Configuration:" + ttls);
		out.close();

		return writer.toString();
	}

	//TODO NARENDRA - decide location of below
	private int identifier;

	/*
	 * Below getter setters are used to store the identifier of the outer EAP packet.
	 * This required in the EAP MD5-Challenge.
	 */
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}
}
