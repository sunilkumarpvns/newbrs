package com.elitecore.aaa.core.conf.impl;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.context.AAAConfigurationState;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.io.Files;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.config.util.FileUtil;
import com.elitecore.core.commons.tls.TrustedCAConfiguration;
import com.elitecore.core.commons.tls.TrustedCADetail;
import com.elitecore.commons.base.DBUtility;


@ConfigurationProperties(moduleName ="TRUST-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(configDirectories = "conf",name="trusted-certificates",schemaDirectories={"system","schema"})
@XmlRootElement(name="trusted-certificates")
@XmlType(propOrder={})
public class TrustedCAConfigurable extends Configurable implements TrustedCAConfiguration{
	private static final String MODULE = "TRUST-CONFIGURABLE";
	private static final String ADDITIONAL = "additional";

	private List<TrustedCADetail> trustedCADetails;
	private List<TrustedCADetail> globalCADetails;
	private final String TRUSTED_CA_PATH  = "system" + File.separator + "cert" + File.separator + "trustedcertificates";
	private String additionalCACertDirectory;

	//Transient properties
	private List<X509Certificate> x509Certificates;
	private Map<String, TrustedCADetail> subjectNameToTrustedCADetails;


	public TrustedCAConfigurable(){
		x509Certificates = new ArrayList<X509Certificate>();
		subjectNameToTrustedCADetails = new HashMap<String, TrustedCADetail>();
		trustedCADetails = new ArrayList<TrustedCADetail>();
		globalCADetails = new ArrayList<TrustedCADetail>();
	}


	@DBRead
	public void readFromDB() throws Exception {
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet =  null;

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String baseQuery = "select NAME,CERTIFICATE,CERTIFICATEFILENAME from TBLMTRUSTCERTIFICATE";	

			prepareStatement = connection.prepareStatement(baseQuery);		
			if(prepareStatement ==  null){
				throw new SQLException("Problem in reading trust store configuration, reason: prepared statement is null");

			}

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
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(prepareStatement);
			DBUtility.closeQuietly(connection);
		}
	}

	private TrustedCADetail createTrustedCADetail(byte[] byteCACertificates,String certificateFileName,String name) throws Exception {
		X509Certificate certificate = generateCerficate(byteCACertificates);

		TrustedCADetail caDetail = new TrustedCADetail();
		caDetail.setCertificateFileName(certificateFileName);
		caDetail.setCertificate(certificate);
		caDetail.setEncodedCertificateBytes(certificate.getEncoded());
		caDetail.setCertificatelFileBytes(byteCACertificates);
		caDetail.setName(name);
		return caDetail;
	}

	public String getAddingCACertPath() {
		return additionalCACertDirectory;
	}

	public void setAddingCACertPath(String addingCACertPath) {
		this.additionalCACertDirectory = addingCACertPath;
	}

	@PostRead
	public void postReadProcessing() throws Exception{
		AAAConfigurationContext aaaConfigurationContext = (AAAConfigurationContext) getConfigurationContext();
		String pathToloadTrustedCA = null;
		if (aaaConfigurationContext.state() != AAAConfigurationState.NORMAL) {
			/*
			 * When Last known good configuration is read, in trustedCADetails list each TrustedCADetail will contain 
			 * certificate name but the certificate will be null as certificate and its bytes can not be stored in xml file.
			 * 
			 * Due to this, in scanTrustedCertificates(), TrustedCADetail seems to be existing in the list 
			 * even though that TrustedCADetail is having no certificate and its bytes and so whenever we try 
			 * to get certificate, it will return null.
			 *    
			 * So, We will clear the trustedCADetails list and in scanTrustedCertificates() all certificate 
			 * from the back end are loaded properly with certificate and its file bytes.  
			 *     
			 */
			trustedCADetails.clear();
			pathToloadTrustedCA = aaaConfigurationContext.getServerContext().getServerHome() 
									+ File.separator + TRUSTED_CA_PATH;
		} else {
			pathToloadTrustedCA = aaaConfigurationContext.getServerContext().getServerHome() 
									+ File.separator + TRUSTED_CA_PATH 
									+ File.separator + ADDITIONAL;
		}
		
		this.globalCADetails = new ArrayList<TrustedCADetail>(trustedCADetails);
		
		List<TrustedCADetail> caDetails =  scanTrustedCertificates(pathToloadTrustedCA);

		for (TrustedCADetail caDetail : caDetails) {
			if (this.trustedCADetails.contains((caDetail))) {
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


	@PostReload
	public void postReloadProcessing(){

	}

	@PostWrite
	public void postWriteProcessing() {
		cleanTrustedCADirectory();
		
		for (TrustedCADetail caDetail : globalCADetails) {
			try {
				write(((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome() + File.separator + TRUSTED_CA_PATH + File.separator + caDetail.getCertificateFileName(), caDetail.getCertificateFileBytes());
			} catch(Exception ex) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Unable to write CA Certificate in trustedcertificates directory: "+ TRUSTED_CA_PATH + ". Reason: " + ex.getMessage() );
				}
				LogManager.getLogger().trace(MODULE, ex);
			}
		}
	}


	private void cleanTrustedCADirectory() {
		Predicate<File> allExceptAdditionalPredicate = new Predicate<File>() {
			@Override
			public boolean apply(File input) {
				return (ADDITIONAL.equals(input.getName()) ? false : true);
			}
		};
		
		String trustedCADirectoryPath = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome() + File.separator + TRUSTED_CA_PATH;
		File trustedCADirectory = new File(trustedCADirectoryPath);
		try {
			FileUtil.cleanDirectory(trustedCADirectory, allExceptAdditionalPredicate);
		} catch (IOException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Unable to clean Trusted CA directory: " + trustedCADirectoryPath  +". Reason: " +  e.getMessage());	
			}
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	@DBReload
	public void reloadTrustStoreConfiguration(){

	}

	public void  write(String path, byte [] bytes) throws Exception{
		FileOutputStream fileOutputStream = null;
		try{
			fileOutputStream = new FileOutputStream(path);
			fileOutputStream.write(bytes);
		}finally{
			FileUtil.closeQuietly(fileOutputStream);
		}
	}

	private List<TrustedCADetail> scanTrustedCertificates(String trustedCertificateLocation) throws Exception {
		List<TrustedCADetail> caDetails = new ArrayList<TrustedCADetail>();
		try {
			List<File> files = FileUtil.getRecursiveFileFromPath(trustedCertificateLocation);

			for (File file : files) {
				FileInputStream fisForCert = null;
				try {
					byte [] certificateFileBytes = Files.readFully(file);

					TrustedCADetail trustedCADetail = createTrustedCADetail(certificateFileBytes, file.getName(), file.getName());
					caDetails.add(trustedCADetail);

				} catch (Exception e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Unable to load certificate: " + file.getName() + ", Reason: " + e.getLocalizedMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				} finally {
					FileUtil.closeQuietly(fisForCert);
				}
			}
		} catch (FileNotFoundException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Skipping the certificate loading process for location: " + trustedCertificateLocation + ", Reason: The specified file not found");
			}
		}
		return caDetails;
	}


	private X509Certificate generateCerficate(byte [] certificateBytes) throws Exception{
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		ByteArrayInputStream inputStream = new ByteArrayInputStream(certificateBytes);
		Collection<? extends Certificate> certificateList = certificateFactory.generateCertificates(inputStream);

		//FIXME: Remove the size check and logger after giving support in SM
		if(certificateList.size() > 1) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Certificate contains chain, Trusted Certificate cannot be in chain");
		}

		return (X509Certificate) certificateList.iterator().next();
	}



	@XmlTransient
	@Override
	public List<X509Certificate> getCACertificates() {
		return x509Certificates;
	}



	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Additional Trusted CA Certificate Directory: " + TRUSTED_CA_PATH + File.separator + ADDITIONAL);
		out.println("---Trusted Certificates---");
		for (TrustedCADetail trustedCADetail : trustedCADetails) {
			out.println("Trusted certificate name: " + trustedCADetail.getName());
			out.println("Subject DN : " + trustedCADetail.getCertificate().getSubjectDN());
			out.println("Issuer DN  : " + trustedCADetail.getCertificate().getIssuerDN());
			out.println("Validity   : " + trustedCADetail.getCertificate().getNotBefore() + " to " + trustedCADetail.getCertificate().getNotAfter());
			out.println("--------------------------------------------------------------------------");
		}
		out.close();
		return stringBuffer.toString();
	}


	public TrustedCADetail getCADetails(String issuer) {
		return subjectNameToTrustedCADetails.get(issuer);
	}

	@XmlElementWrapper(name = "trusted-ca-details")
	@XmlElement(name = "trusted-ca-detail")
	public List<TrustedCADetail> getGlobalCADetails() {
		return this.globalCADetails;
	}
	
	public List<TrustedCADetail> getCADetails(){
		return trustedCADetails;
	}
}
