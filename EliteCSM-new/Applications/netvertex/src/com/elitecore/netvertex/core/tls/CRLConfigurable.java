package com.elitecore.netvertex.core.tls;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.util.FileUtil;
import com.elitecore.core.commons.tls.CRLConfiguration;
import com.elitecore.core.commons.tls.CRLDetail;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.elitecore.netvertex.core.NetVertexServerContext;

public class CRLConfigurable extends Configurable implements CRLConfiguration {
	private static final String MODULE = "CRL-CONFIGURABLE";
	protected static final String NAME = "certificate-revocation-list";
	private List<CRLDetail> crlDetails;
	private List<X509CRL> crls;
	private String crlPath;
	private NetVertexServerContext netVertexServerContext;
	private boolean ocspEnabled = false;
	private String ocspURL = null;
	
	public CRLConfigurable(NetVertexServerContext netVertexServerContext){
		this.netVertexServerContext = netVertexServerContext;
		crls = new ArrayList<X509CRL>();
		crlDetails = new ArrayList<CRLDetail>();
		crlPath = "system" + File.separator + "cert" + File.separator + "crl";
	}
	
	public void readFromDB() throws Exception {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet =  null;
		

		try {
			connection = NetVertexDBConnectionManager.getInstance().getConnection();
			String baseQuery = "select NAME,CERTIFICATE,CERTIFICATEFILENAME from TBLMCRLCERTIFICATE";	
			preparedStatement = connection.prepareStatement(baseQuery);		
			
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
			
			this.crlDetails = crlDetails;
			this.crls = crlCerts;
			postReadProcessing();
			
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}

	public void reloadCRLConfiguration(){
	}
	
	public void postReadProcessing() throws Exception{
		if(crlPath == null){
			LogManager.getLogger().warn(MODULE, "Unable to read CRL from crl directory. Reason: crl directory path not set");
			return;
		}
		
		List<CRLDetail> crlDetails = scanCRLs(netVertexServerContext.getServerHome() + File.separator  + crlPath);
		
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
	
	public void postReloadProcessing(){
	}

	private List<CRLDetail> scanCRLs(String crlPath) throws Exception{
		List<CRLDetail> crlDetails = new ArrayList<CRLDetail>();
		List<File> files = FileUtil.getRecursiveFileFromPath(crlPath);
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		for(File file : files){
			FileInputStream fisForCRL = null;
			FileInputStream fisForReadByte = null;
			try{
				fisForCRL = new FileInputStream(file);
				fisForReadByte = new FileInputStream(file);
				Collection<X509CRL> crls = (Collection<X509CRL>) certificateFactory.generateCRLs(fisForCRL);
				CRLDetail crlDetail = new CRLDetail();
				crlDetail.setCrl(new ArrayList<X509CRL>(crls));
				
				byte [] crlFileBytes = new byte[fisForReadByte.available()];
				fisForReadByte.read(crlFileBytes, 0,crlFileBytes.length);
				crlDetail.setCrlFileBytes(crlFileBytes);
				crlDetail.setCrlFileName(file.getName());
				crlDetail.setName(file.getName());
				crlDetails.add(crlDetail);
			} catch(Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "unable to load crl: " + file.getName() + ", Reason: " + e.getLocalizedMessage());
				LogManager.getLogger().trace(MODULE, e);
			}finally{
				FileUtil.closeQuietly(fisForCRL);
				FileUtil.closeQuietly(fisForReadByte);
			}
		}
		
		return crlDetails;
	}

	
	public String getCrlPath() {
		return crlPath;
	}

	public void setCrlPath(String crlPath) {
		if(crlPath != null && !crlPath.trim().isEmpty()){
			this.crlPath = crlPath.trim();
		}
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

	@Override
	public List<X509CRL> getCRLs() {
		return crls;
	}
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Additional CRL Directory: " + crlPath);
		out.println("----Certificate Revocation List----");
		out.println(crls);
		out.close();
		return stringBuffer.toString();
	}
}
