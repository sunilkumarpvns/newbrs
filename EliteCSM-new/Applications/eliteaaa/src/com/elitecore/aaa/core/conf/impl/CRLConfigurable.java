package com.elitecore.aaa.core.conf.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.elitecore.core.commons.tls.CRLConfiguration;
import com.elitecore.core.commons.tls.CRLDetail;
import com.elitecore.commons.base.DBUtility;


@ConfigurationProperties(moduleName ="CRL-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(configDirectories = "conf",name=CRLConfigurable.NAME,schemaDirectories={"system","schema"})
@XmlRootElement(name=CRLConfigurable.NAME)
@XmlType(propOrder={})
public class CRLConfigurable extends Configurable implements CRLConfiguration{
	private static final String MODULE = "CRL-CONFIGURABLE";
	protected static final String NAME = "certificate-revocation-list";
	private static final String ADDITIONAL = "additional";
	private List<CRLDetail> crlDetails;
	private List<CRLDetail> globalCRLDetails;
	private List<X509CRL> crls;
	private final String CRL_PATH = "system" + File.separator + "cert" + File.separator + "crl";
	
	//OCSP parameters
	private boolean ocspEnabled = false;
	private String ocspURL = null;
	
	public CRLConfigurable(){
		crls = new ArrayList<X509CRL>();
		crlDetails = new ArrayList<CRLDetail>();
		globalCRLDetails = new ArrayList<CRLDetail>();
	}
	
	@SuppressWarnings("unchecked")
	@DBRead
	public void readFromDB() throws Exception {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet =  null;
		

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String baseQuery = "select NAME,CERTIFICATE,CERTIFICATEFILENAME from TBLMCRLCERTIFICATE";	
			preparedStatement = connection.prepareStatement(baseQuery);		
			if(preparedStatement ==  null){
				throw new SQLException("Problem in reading CRL store configuration, reason: prepared statement is null");
 
			}
			
			List<CRLDetail> crlDetails = new ArrayList<CRLDetail>();
			List<X509CRL> crlCerts = new ArrayList<X509CRL>();
			resultSet = preparedStatement.executeQuery();
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			while (resultSet.next()) {
				String name = resultSet.getString("NAME");
				byte[] byteCRL = resultSet.getBytes("CERTIFICATE");
				String fileName = resultSet.getString("CERTIFICATEFILENAME");
				
				try{
					if(byteCRL != null) {
						List<X509CRL> crls = (List<X509CRL>) certificateFactory.generateCRLs(new ByteArrayInputStream(byteCRL));
						
						CRLDetail crlDetail = new CRLDetail();
						crlDetail.setCrl(crls);
						crlDetail.setName(name);
						crlDetail.setCrlFileName(fileName);
						crlDetail.setCrlFileBytes(byteCRL);
						crlDetails.add(crlDetail);
						crlCerts.addAll(crls);
					} else {
						//TODO alert
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Unable to add CRL certificate  "+ name +". Reason: CRL certificate not found");
						continue;
					}
				}catch(Exception ex){
					//alert
					LogManager.getLogger().warn(MODULE, "Unable to add CRL certificate detail "+ name +". Reason: " +  ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
					continue;
				}
			} 
			
			//readOCSPConfig(connection);
			this.crlDetails = crlDetails;
			this.crls = crlCerts;
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	private void readOCSPConfig(Connection connection) throws Exception {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet =  null;
		
		try {
			String baseQuery = "select OCSPENABLED, URL from TBLMCRLOCSP";	
			
			preparedStatement = connection.prepareStatement(baseQuery);		
			if(preparedStatement ==  null){
				throw new SQLException("Problem in reading OCSP configuration, reason: prepared statement is null");
			}
			
			resultSet = preparedStatement.executeQuery();
			boolean ocspEnabled = false;
			String ocspURL = null;
			while (resultSet.next()) {
				ocspEnabled = resultSet.getBoolean("OCSPENABLED");
				ocspURL = resultSet.getString("URL");
			} 
			
			if(ocspURL == null){
				ocspEnabled = false;
			}
			
			this.ocspEnabled = ocspEnabled;
			this.ocspURL = ocspURL;
			
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
		}
	}
	
	
	
	@DBReload
	public void reloadCRLConfiguration(){
	}
	
	@PostRead
	public void postReadProcessing() throws Exception{
		AAAConfigurationContext aaaConfigurationContext = (AAAConfigurationContext) getConfigurationContext();
		String pathToloadcrl = null;
		if(aaaConfigurationContext.state() != AAAConfigurationState.NORMAL){
			/*
			 * When Last known good configuration is read, in crlDetails list each CrlDetail will contain crl name 
			 * but the crl file bytes will be null as bytes can not be stored in xml file.
			 * 
			 * Due to this, in scanCRLs(), CrlDetail seems to be existing in the list even though that CrlDetail 
			 * is having no crl file bytes and so whenever we try to get crl, it will return null.   
			 * 
			 * So, We will clear the CRL detail list and in scanCRLs() all crls from the back end are loaded properly with file bytes.  
			 *     
			 */
			crlDetails.clear();
			pathToloadcrl = aaaConfigurationContext.getServerContext().getServerHome() 
							+ File.separator + CRL_PATH;
		} else {
			pathToloadcrl = aaaConfigurationContext.getServerContext().getServerHome() 
							+ File.separator + CRL_PATH 
							+ File.separator + ADDITIONAL;
		}
		
		this.globalCRLDetails = new ArrayList<CRLDetail>(crlDetails);
		
		List<CRLDetail> crlDetails = scanCRLs(pathToloadcrl);
		
		for(CRLDetail crlDetail : crlDetails){
			if(this.crlDetails.contains(crlDetail)){
				LogManager.getLogger().debug(MODULE, "CRL with name: " + crlDetail.getName() +" already in the crl list");
			} else {
				this.crlDetails.add(crlDetail);
				this.crls.addAll(crlDetail.getCRLs());
			}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, String.valueOf(this));
	}
	
	@PostReload
	public void postReloadProcessing(){
	}

	
	@PostWrite
	public void postWriteProcessing() {
		cleanCRLDirectory();
		
		for (CRLDetail crlDetail : globalCRLDetails) {
			try {
				write(((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome() + File.separator + CRL_PATH + File.separator + crlDetail.getCrlFileName(), crlDetail.getCrlFileBytes());
			} catch (Exception ex) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Unable to write CRL in crl directory: "+ CRL_PATH + ". Reason: " + ex.getMessage() );
				}
				LogManager.getLogger().trace(MODULE, ex);
			}
		}
	}

	private void cleanCRLDirectory() {
		Predicate<File> allExceptAdditional = new Predicate<File>() {
			@Override
			public boolean apply(File input) {
				return (ADDITIONAL.equals(input.getName()) ? false : true);
			}
		};
		
		String crlDirectoryPath = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getServerHome() + File.separator + CRL_PATH;
		File crlDirectory = new File(crlDirectoryPath);

		try {
			FileUtil.cleanDirectory(crlDirectory, allExceptAdditional);
		} catch (IOException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Unable to clean CRL directory: " + crlDirectoryPath  +". Reason: " +  e.getMessage());	
			}
			LogManager.getLogger().trace(MODULE, e);
		}
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
	
	@SuppressWarnings("unchecked")
	private List<CRLDetail> scanCRLs(String crlPath) throws Exception{
		List<CRLDetail> crlDetails = new ArrayList<CRLDetail>();
		try {
			List<File> files = FileUtil.getRecursiveFileFromPath(crlPath);
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			for (File file : files) {
				FileInputStream fisForCRL = null;
				try {
					fisForCRL = new FileInputStream(file);
					Collection<X509CRL> crls = (Collection<X509CRL>) certificateFactory.generateCRLs(fisForCRL);
					CRLDetail crlDetail = new CRLDetail();
					crlDetail.setCrl(new ArrayList<X509CRL>(crls));
					
					byte[] crlFileBytes = Files.readFully(file);
					crlDetail.setCrlFileBytes(crlFileBytes);
					crlDetail.setCrlFileName(file.getName());
					crlDetail.setName(file.getName());
					crlDetails.add(crlDetail);
				} catch (Exception e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Unable to load crl: " + file.getName() + ", Reason: " + e.getLocalizedMessage());
					}
					LogManager.getLogger().trace(MODULE, e);
				} finally {
					FileUtil.closeQuietly(fisForCRL);
				}
			}
		} catch (FileNotFoundException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Skipping the CRL loading process for location: " + crlPath + ", Reason: The specified file not found");
			}
		}
		
		return crlDetails;
	}

	@XmlElementWrapper(name = "crl-details")
	@XmlElement(name = "crl-detail")
	public List<CRLDetail> getGlobalCRLDetails() {
		return this.globalCRLDetails;
	}
	
	public List<CRLDetail> getCRLDetails(){
		return crlDetails;
	}
	
	public boolean getOCSPEnabled(){
		return ocspEnabled;
	}
	
	public String getOCSPURL(){
		return ocspURL;
	}

	@XmlTransient
	@Override
	public List<X509CRL> getCRLs() {
		return crls;
	}
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Additional CRL Directory: " + CRL_PATH + File.separator + ADDITIONAL);
		out.println("----Certificate Revocation List----");
		for (X509CRL crl : crls) {
			out.println("CRL : " + crl.getIssuerDN());
		}
		out.close();
		return stringBuffer.toString();
	}
}
