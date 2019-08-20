package com.elitecore.netvertex.core.tls;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.util.FileUtil;
import com.elitecore.core.commons.tls.TrustedCAConfiguration;
import com.elitecore.core.commons.tls.TrustedCADetail;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.NetVertexServerContext;

public class TrustedCAConfigurable extends Configurable implements TrustedCAConfiguration {

	private static final String MODULE = "TRUST-CONFIGURABLE";
	private static final int FIRST_CA_CERTIFICATE_INDEX = 0;

	private List<TrustedCADetail> trustedCADetails;
	private String trustedCAPath;
	private String additionalCACertDirectory;
	private NetVertexServerContext netVertexServerContext;
	//Transient properties
	private List<X509Certificate> x509Certificates;
	private Map<String, TrustedCADetail> subjectNameToTrustedCADetails;

	public TrustedCAConfigurable(NetVertexServerContext netVertexServerContext) {
		this.netVertexServerContext = netVertexServerContext;
		trustedCADetails = new ArrayList<TrustedCADetail>();
		trustedCAPath = "system" + File.separator + "cert" + File.separator + "trustedcertificates";
		x509Certificates = new ArrayList<X509Certificate>();
		subjectNameToTrustedCADetails = new HashMap<String, TrustedCADetail>();
	
	}

	public void readFromDB() throws Exception {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet =  null;
		try{
			connection = NetVertexDBConnectionManager.getInstance().getConnection();
			String baseQuery = "select NAME,CERTIFICATE,CERTIFICATEFILENAME from TBLMTRUSTCERTIFICATE";
			prepareStatement = connection.prepareStatement(baseQuery);
			
			List<TrustedCADetail> caDetails = new ArrayList<TrustedCADetail>();
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				String name = resultSet.getString("NAME");
				String certificateFileName = resultSet.getString("CERTIFICATEFILENAME");
				try{

					byte[] byteCACertificates = resultSet.getBytes("CERTIFICATE");

					if(byteCACertificates != null) {
						
						TrustedCADetail trustedCADetail = createTrustedCADetail(byteCACertificates,certificateFileName,name);
						caDetails.add(trustedCADetail);
						
					} else {
						//TODO alert
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Unable to add CA certificate "+ name +". Reason: CA certificate not found");
						continue;
					}
				}catch(Exception ex){
					//TODO alert
					LogManager.getLogger().warn(MODULE, "Unable to add CA certificate "+ name +". Reason: " +  ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
					continue;
				}
			} 

			this.trustedCADetails = caDetails;
			postReadProcessing();
			
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(prepareStatement);
			DBUtility.closeQuietly(connection);
			
		}

	}


	private TrustedCADetail createTrustedCADetail(byte[] byteCACertificates,
			String certificateFileName, String name) throws Exception {
		
		TrustedCADetail trustedCADetails = new TrustedCADetail();
		trustedCADetails.setCertificatelFileBytes(byteCACertificates);
		trustedCADetails.setCertificateFileName(certificateFileName);
		trustedCADetails.setName(name);
		
		X509Certificate certificate = generateCerficate(byteCACertificates);
		trustedCADetails.setCertificate(certificate);
		trustedCADetails.setEncodedCertificateBytes(certificate.getEncoded());
		
		return trustedCADetails;
	}

	private X509Certificate generateCerficate(byte[] byteCACertificates) throws Exception {
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteCACertificates);
		Collection<? extends Certificate> certificateList = certificateFactory.generateCertificates(byteArrayInputStream);
		//FIXME: Remove the size check and logger after giving support in SM
		if(certificateList.size() >1){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Certificate contains chain, Trusted Certificate cannot be in chain");
		}
		return ((List<X509Certificate>)certificateList).get(FIRST_CA_CERTIFICATE_INDEX);
	}
	
	public String getTrustedCAPath() {
		return trustedCAPath;
	}

	public void setTrustedCAPath(String trustedCAPath) {
		if(trustedCAPath != null && trustedCAPath.trim().isEmpty())
			this.trustedCAPath = trustedCAPath.trim();
	}

	public String getAddingCACertPath() {
		return additionalCACertDirectory;
	}

	public void setAddingCACertPath(String addingCACertPath) {
		this.additionalCACertDirectory = addingCACertPath;
	}

	public void postReadProcessing() throws Exception {
		
		if(trustedCAPath == null){
			LogManager.getLogger().warn(MODULE, "Unable to read trusted CA certificate from trustedcertificates directory. Reason: trustedcertificates directory path not set");
			return;
		}
		List<TrustedCADetail> additionalCADetails =  scanTrustedCertificates( netVertexServerContext.getServerHome() + File.separator  + trustedCAPath);
		for(TrustedCADetail caDetail : additionalCADetails){
			if(this.trustedCADetails.contains((caDetail))){
				LogManager.getLogger().debug(MODULE, "Trusted Certificate with subject name: " + caDetail.getName() 
						+" already present in the list");
			} else {
				this.trustedCADetails.add(caDetail);
			}
		}

		storeInDataStructures();

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, String.valueOf(this));
		}

	}
	
	
	private void storeInDataStructures() {
		for(TrustedCADetail trustedCADetail : trustedCADetails) {
			x509Certificates.add(trustedCADetail.getCertificate());
			subjectNameToTrustedCADetails.put(trustedCADetail.getCertificate().getSubjectDN().getName(), trustedCADetail);
		}
	}

	private List<TrustedCADetail> scanTrustedCertificates(String trustedCertificateLocation) throws Exception{
		List<TrustedCADetail> caDetails = new ArrayList<TrustedCADetail>();
		List<File> files = FileUtil.getRecursiveFileFromPath(trustedCertificateLocation);

		for(File file : files){
			FileInputStream fisForCert = null;
			try{
				fisForCert = new FileInputStream(file);
				byte [] certificateFileBytes = new byte[fisForCert.available()];
				fisForCert.read(certificateFileBytes);
				
				TrustedCADetail trustedCADetails = createTrustedCADetail(certificateFileBytes, file.getName(), file.getName());
				caDetails.add(trustedCADetails);
				
			} catch(Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "unable to load certificate: " + file.getName() + ", Reason: " + e.getLocalizedMessage());
				LogManager.getLogger().trace(MODULE, e);
			} finally{
				FileUtil.closeQuietly(fisForCert);
			}
		}
		return caDetails;
	}

	public void postReloadProcessing(){

	}
	
	public void reloadTrustStoreConfiguration(){

	}

	@Override
	public List<X509Certificate> getCACertificates() {
		// TODO Auto-generated method stub
		return x509Certificates;
	}


	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Additional Trusted CA Certificate Directory: " + trustedCAPath);
		out.println("---Trusted Certificates---");
		out.println(trustedCADetails);
		out.close();
		return stringBuffer.toString();
	}

	public TrustedCADetail getCADetails(String issuer) {
		return subjectNameToTrustedCADetails.get(issuer);
	}

	public List<TrustedCADetail> getCADetails(){
		return trustedCADetails;
	}
}
