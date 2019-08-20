package com.elitecore.netvertex.core.tls;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.tls.ServerCertificateProfile;
import com.elitecore.core.commons.tls.TrustedCADetail;
import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.NetVertexServerContext;

public class ServerCertificateConfigurable extends Configurable implements ServerCertificateConfiguration{


	private static final String MODULE = "SERVER-CERT-CONFIGURABLE";
	private static final int INDEX_OF_ENTITY_CERTIFICATE = 0;
	
	
	private Map<Long, ServerCertificateProfile> serverCertificateProfileMapById;
	
	
	private List<ServerCertificateProfile> serverCertificateProfiles;
	
	private NetVertexServerContext netVertexServerContext;
	public ServerCertificateConfigurable( NetVertexServerContext netVertexServerContext){
		this.netVertexServerContext = netVertexServerContext;
		this.serverCertificateProfiles =new ArrayList<ServerCertificateProfile>();
		serverCertificateProfileMapById = new HashMap<Long, ServerCertificateProfile>();
	}
	 
	public void readFromDB() throws Exception{
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet =  null;

		try {
			connection = NetVertexDBConnectionManager.getInstance().getConnection();
			
			String baseQuery = "select SERVERCERTIFICATEID, NAME,CERTIFICATEFILENAME,CERTIFICATE,PRIVATEKEY,PRIVATEKEYPASSWORD,PRIVATEKEYALGORITHM,PRIVATEKEYFILENAME  from TBLMSERVERCERTIFICATE";	
			
			prepareStatement = connection.prepareStatement(baseQuery);		
			List<ServerCertificateProfile> serverCertificateProfiles = new ArrayList<ServerCertificateProfile>();
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				long id = resultSet.getLong("SERVERCERTIFICATEID");
				String name = resultSet.getString("NAME");
				String certificateFileName = resultSet.getString("CERTIFICATEFILENAME");
				byte[] nonEncodedCertificateFileBytes = resultSet.getBytes("CERTIFICATE");
				byte[] bytePrivateCertificates = resultSet.getBytes("PRIVATEKEY");
				 
				String privateKeyAlgoStr = resultSet.getString("PRIVATEKEYALGORITHM");
				String privateKeyFileName = resultSet.getString("PRIVATEKEYFILENAME");
				if(privateKeyAlgoStr == null){
					LogManager.getLogger().warn(MODULE, "Unable to add server certificate profile "+ name +". Reason: PrivateKeyAlgo not configured");
					continue;
				}
				PrivateKeyAlgo privateKeyAlgo = PrivateKeyAlgo.fromAlgoName(privateKeyAlgoStr);
				if(privateKeyAlgo == null){
					LogManager.getLogger().warn(MODULE, "Unable to add server certificate profile "+ name +". Reason: Unsupported PrivateKeyAlgo: " + privateKeyAlgoStr);
					continue;
				}
				
				String privateKeyPassword = resultSet.getString("PRIVATEKEYPASSWORD");
				ServerCertificateProfileImpl serverCertificateProfile = new ServerCertificateProfileImpl();
				try{
					serverCertificateProfile.setId(id);
					serverCertificateProfile.setName(name);
					serverCertificateProfile.setCertificateName(certificateFileName);
					serverCertificateProfile.setCertificateFileBytes(nonEncodedCertificateFileBytes);
					serverCertificateProfile.setPrivateKeyAlgo(privateKeyAlgo);
					serverCertificateProfile.setPrivateKeyPassword(privateKeyPassword);
					serverCertificateProfile.setPrivatekeyBytes(bytePrivateCertificates);
					serverCertificateProfile.setPrivateKeyName(privateKeyFileName);
					serverCertificateProfiles.add(serverCertificateProfile);
				}catch(Exception ex){
					//TODO alert
					LogManager.getLogger().warn(MODULE, "Unable to add server certificate profile "+ name +". Reason: " +  ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
					continue;
				}
			}
			
			this.serverCertificateProfiles = serverCertificateProfiles;
			
			postReadProcessing();
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(prepareStatement);
			DBUtility.closeQuietly(connection);
		}
	
	}
	
	public void reloadServerCertificateConfiguration(){
		
	}

	public void postReadProcessing(){
		
		Iterator<ServerCertificateProfile> serverCertificateProfileItr = serverCertificateProfiles.iterator();
		while(serverCertificateProfileItr.hasNext()){
			ServerCertificateProfileImpl serverCertificateProfileImpl = (ServerCertificateProfileImpl) serverCertificateProfileItr.next();
			try{	
				//Jack logic is here
				postReadProcessingForCertificatesAndPrivateKey(serverCertificateProfileImpl);
				
				//forming the chain of server certificate
				postReadProcessingForServerCertificateChain(serverCertificateProfileImpl);

				storeInDataStructures(serverCertificateProfileImpl);
			}catch(Exception ex){
				LogManager.getLogger().warn(MODULE, "Unable to add server certificate in server certificate profile  " + serverCertificateProfileImpl.getName()  +". Reason: " +  ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
				serverCertificateProfileItr.remove();
			}
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, String.valueOf(this));
		}
	}
	
	private void postReadProcessingForCertificatesAndPrivateKey(ServerCertificateProfileImpl serverCertificateProfile) throws Exception {

		generateServerCertificate(serverCertificateProfile);
		generateServerPrivateKey(serverCertificateProfile);
			
	}

	private void generateServerCertificate(ServerCertificateProfileImpl serverCertificateProfileImpl) throws Exception {
		Collection<? extends Certificate> certificates = generateCerficates(serverCertificateProfileImpl.getCertificateFileBytes());
		serverCertificateProfileImpl.setCertificates(certificates);
	}

	private void generateServerPrivateKey(ServerCertificateProfileImpl serverCertificateProfileImpl) throws Exception {
		PrivateKey privateKey = generatePrivateKey(serverCertificateProfileImpl.getPrivatekeyFileBytes(), serverCertificateProfileImpl.getPlainTextPrivateKeyPassword(), serverCertificateProfileImpl.getPrivateKeyAlgo());
		serverCertificateProfileImpl.setPrivateKey(privateKey);
	}
	
	private void postReadProcessingForServerCertificateChain(ServerCertificateProfileImpl serverCertificateProfile) throws Exception {
		
		TrustedCAConfigurable trustedCAConfiguration = getConfigurationContext().get(TrustedCAConfigurable.class);

		serverCertificateProfile.setCertificateChainBytes(generateServerCertificateChain(serverCertificateProfile.getCertificates(), trustedCAConfiguration));
	}

	private void storeInDataStructures(ServerCertificateProfileImpl serverCertificateProfile) {
		serverCertificateProfileMapById.put(serverCertificateProfile.getId(), serverCertificateProfile);
	}

	public void postReloadProcessing(){
		postReadProcessing();
	}
	
	
	
	/*
	 * It will create CACertificate Chain for both default entity Certificate and also for vendor specific entity certificate
	 * if server certificate itself contains the chain then it will not look up for the chain, 
	 * it will look up only when server certificate will be only entity certificate
	 */
	private List<byte[]> generateServerCertificateChain(Collection<? extends Certificate> certificateList, TrustedCAConfigurable trustedCAConfig) throws CertificateEncodingException {
		List<byte[]> certificateChainList = null;
		
		if(isChain(certificateList)) {
			certificateChainList = certificateChainToBytes(certificateList);
		} else {
			certificateChainList = lookUpChain(certificateList, trustedCAConfig);
		}
		return certificateChainList;
	}
	
	private boolean isChain(Collection<? extends Certificate> certificateList){
		return certificateList.size() > 1;
	}
	
	private List<byte[]> certificateChainToBytes(Collection<? extends Certificate> certificateList) throws CertificateEncodingException {
		List<byte[]> certificateChainList = new ArrayList<byte[]>();
		for(int index = 0 ; index < certificateList.size() ; index ++){
				certificateChainList.add(((List<X509Certificate>)certificateList).get(index).getEncoded());
		}
		return certificateChainList;
	}
	
	@SuppressWarnings(value = { "unchecked" })
	private List<byte[]> lookUpChain(Collection<? extends Certificate> certificateList, TrustedCAConfigurable trustedCAConfig) throws CertificateEncodingException {
		String subject = ((List<X509Certificate>)certificateList).get(INDEX_OF_ENTITY_CERTIFICATE).getSubjectDN().getName();
		String issuer = ((List<X509Certificate>)certificateList).get(INDEX_OF_ENTITY_CERTIFICATE).getIssuerDN().getName();
		
		List<byte[]> certificateChainList = new ArrayList<byte[]>();
		
		certificateChainList.add(((List<X509Certificate>)certificateList).get(INDEX_OF_ENTITY_CERTIFICATE).getEncoded());
		TrustedCADetail trustedCADetail = null;
		do {
			 trustedCADetail = trustedCAConfig.getCADetails(issuer);
			 
			if(trustedCADetail == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Certificate of, " + issuer  + " not found while generating certificate chain, incomplete chain loaded for " + subject);
				break;
			}
			
			certificateChainList.add(trustedCADetail.getEncodedCertificateBytes());
			issuer = trustedCADetail.getCertificate().getIssuerDN().getName();

		} while(!isRootCertificate(trustedCADetail.getCertificate()));
			
		return certificateChainList;
	}
	
	private boolean isRootCertificate(X509Certificate certificate) {
		return certificate.getSubjectDN().getName().equals(certificate.getIssuerDN().getName());
	}
	
	@Override
	public ServerCertificateProfile getServerCertificateProfileByName(String name) {
		if(name == null){
			return null;
		}

		for(ServerCertificateProfile profile : serverCertificateProfiles){
			if(profile.getName().equalsIgnoreCase(name)){
				return profile;
			}
		}
		
		return null;
	}
	
	@Override
	public ServerCertificateProfile getServerCertificateProfileById(Long id) {
		return serverCertificateProfileMapById.get(id);
	}
	
	private Collection<? extends Certificate> generateCerficates(byte [] certificateBytes) throws Exception{
		
		if(certificateBytes == null){
			throw new Exception("Certificate bytes not found in DB");
		}
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		ByteArrayInputStream inputStream = new ByteArrayInputStream(certificateBytes);
		return certificateFactory.generateCertificates(inputStream);

	}

	public byte[] read(String path) throws Exception{
		FileInputStream fis = null;
		DataInputStream dis = null;
		try{
			File file = new File(path);
			if(!file.isAbsolute()){
				file = new File(netVertexServerContext.getServerHome() + File.separator + path);
			}
			
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			byte[] bytes = new byte[dis.available()];
			dis.readFully(bytes);
			
			return bytes;
		}finally{
			
			Closeables.closeQuietly(dis);
		}
	}
	
	
	
	
	private PrivateKey generatePrivateKey(byte [] certificateBytes, String password, PrivateKeyAlgo algo) throws Exception{
		
		if(certificateBytes == null){
			throw new Exception("private key is null");
		}
		
		PrivateKey priKey = null;
		PKCS8EncodedKeySpec keysp = null;
		if(password != null && !"".equals(password)){
			javax.crypto.EncryptedPrivateKeyInfo encPriKey = new javax.crypto.EncryptedPrivateKeyInfo(certificateBytes);
			Cipher cipher = Cipher.getInstance(encPriKey.getAlgName());
			PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
			SecretKeyFactory secKeyFactory = SecretKeyFactory.getInstance(encPriKey.getAlgName());
			cipher.init(Cipher.DECRYPT_MODE, secKeyFactory.generateSecret(pbeKeySpec), encPriKey.getAlgParameters());
			keysp = encPriKey.getKeySpec(cipher);
			
		}else{
			keysp = new PKCS8EncodedKeySpec(certificateBytes);
		}
		
		KeyFactory kf = KeyFactory.getInstance(algo.name);
		priKey = kf.generatePrivate (keysp);
		return priKey;

	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println(String.valueOf(serverCertificateProfiles));
		out.println();
		out.close();
		return stringBuffer.toString();
	}

}
