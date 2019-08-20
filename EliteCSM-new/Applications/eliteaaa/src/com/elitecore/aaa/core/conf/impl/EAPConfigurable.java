package com.elitecore.aaa.core.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.EAPConfigurationData;
import com.elitecore.aaa.core.conf.EAPConfigurationData.VendorSpecificCertificate;
import com.elitecore.aaa.core.conf.EAPConfigurations;
import com.elitecore.aaa.core.conf.impl.EAPConfigurationDataImpl.AKAConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.EAPConfigurationDataImpl.AKAPrimeConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.EAPConfigurationDataImpl.CertificateConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.EAPConfigurationDataImpl.SIMConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.EAPConfigurationDataImpl.TLSConfigurationImpl;
import com.elitecore.aaa.core.conf.impl.EAPConfigurationDataImpl.VendorSpecificCertificateDetail;
import com.elitecore.aaa.core.conf.impl.EAPConfigurationDataImpl.VendorSpecificCertificateImpl;
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
import com.elitecore.core.commons.tls.ServerCertificateProfile;
import com.elitecore.core.commons.util.ConfigurationUtil;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;

@XmlType(propOrder = {})
@XmlRootElement(name = "eap-confs")
@ConfigurationProperties(moduleName ="EAP-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "eap-conf", schemaDirectories = {"system","schema"}, configDirectories = {"conf"})
public class EAPConfigurable extends Configurable implements EAPConfigurations{

	private static final String AKA_METHOD_ID = "23";
	private static final String SIM_METHOD_ID = "18";
	private static final String AKA_PRIME_METHOD_ID = "50";
	public static final String MODULE = "EAP-CONFIGURABLE";

	/*
	 * Data structures for storing the EAP configuration
	 */
	private List<EAPConfigurationDataImpl> eapConfigurationList;
	
	private Map<String, EAPConfigurationData> eapConfIDMap;
	
	/*
	 * End of data structures for storing the EAP configuration
	 */

	public EAPConfigurable(){
		eapConfigurationList = new ArrayList<EAPConfigurationDataImpl>();
		eapConfIDMap = new HashMap<String, EAPConfigurationData>();
	}
	@XmlElement(name = "eap-conf")
	public List<EAPConfigurationDataImpl> getEapConfiList() {
		return eapConfigurationList;
	}
	public void setEapConfiList(List<EAPConfigurationDataImpl> eapConfiList) {
		this.eapConfigurationList = eapConfiList;
	}
	
	@Override
	public EAPConfigurationData getEAPConfigurationDataForID(String eapID){
		return this.eapConfIDMap.get(eapID);
	}
	
	@DBRead
	public void readEAPConfiguration() throws Exception {

		Connection connection = null;

		String query = "";

		PreparedStatement psForEapConf = null;
		ResultSet rsForEapConf = null;		

		PreparedStatement psForTlsConf = null;
		ResultSet rsForTlsConf = null;

		PreparedStatement psForCertConf = null;
		ResultSet rsForCertConf = null;

		PreparedStatement psForAKAConfig = null;
		ResultSet rsForAKAConfig = null;

		PreparedStatement psForAKAPrimeConfig = null;
		ResultSet rsForAKAPrimeConfig = null;
		
		PreparedStatement psForSIMConfig = null;
		ResultSet rsForSIMConfig = null;

		PreparedStatement psForEapConfigurations = null;
		ResultSet rsForEapConfigurations = null;

		List<EAPConfigurationDataImpl> tempEapConfList=  new ArrayList<EAPConfigurationDataImpl>();

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String baseQuery = "SELECT EAP_ID FROM TBLMEAPCONFIG";
			psForEapConfigurations = connection.prepareStatement(baseQuery);
			if(psForEapConfigurations == null){
				throw new SQLException("Prepared Statement is null for EAPConfiguration");				
			}
			rsForEapConfigurations = psForEapConfigurations.executeQuery();

			EAPConfigurationDataImpl eapConfigurationImpl;
			while(rsForEapConfigurations.next()){
				String eapId = rsForEapConfigurations.getString("EAP_ID");
				eapConfigurationImpl = new EAPConfigurationDataImpl();

				query = getQueryForEapConfiguration();
				psForEapConf = connection.prepareStatement(query);
				if(psForEapConf == null){
					throw new SQLException("Prepared Statement is null for EAPConfiguration");
				}
				psForEapConf.setString(1, eapId);
				rsForEapConf = psForEapConf.executeQuery();
				if(rsForEapConf.next()){

					eapConfigurationImpl.setEapId(eapId);
					String name =rsForEapConf.getString("NAME");
					if(name!=null &&name.length()>0)
						eapConfigurationImpl.setName(name);

					if(rsForEapConf.getString("DEFAULT_NEGIOTATION_METHOD")!=null &&rsForEapConf.getString("DEFAULT_NEGIOTATION_METHOD").length()>0)
						eapConfigurationImpl.setDefaultNegotiationMethod(Numbers.parseInt(rsForEapConf.getString("DEFAULT_NEGIOTATION_METHOD"), eapConfigurationImpl.getDefaultNegotiationMethod()));
					else{
						
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, " Default Negotiation Method parameter for Eap Configuration :"+eapConfigurationImpl.getName()+" is not defined , using default value.");
					}
					if(rsForEapConf.getString("TTLSNEGOTIATIONMETHOD")!=null &&rsForEapConf.getString("TTLSNEGOTIATIONMETHOD").length()>0){
						int ttlsNegotiationMethod = Numbers.parseInt(rsForEapConf.getString("TTLSNEGOTIATIONMETHOD"), eapConfigurationImpl.getTtlsMethodDetail().getDefaultTTLSNegotiationMethod());
						if(ttlsNegotiationMethod != 0 )
							eapConfigurationImpl.setDefaultTTLSNegotiationMethod(ttlsNegotiationMethod);
						else 
							eapConfigurationImpl.setDefaultTTLSNegotiationMethod(eapConfigurationImpl.getTtlsMethodDetail().getDefaultTTLSNegotiationMethod());
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, " Default TTLS Negotiation Method parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					if(rsForEapConf.getString("PEAPNEGOTIATIONMETHOD")!=null &&rsForEapConf.getString("PEAPNEGOTIATIONMETHOD").length()>0){
						int peapNegotiationMethod = Numbers.parseInt(rsForEapConf.getString("PEAPNEGOTIATIONMETHOD"), eapConfigurationImpl.getPeapMethodDetail().getDefaultPEAPNegotiationMethod());
						if(peapNegotiationMethod != 0 )
							eapConfigurationImpl.setDefaultPEAPNegotiationMethod(peapNegotiationMethod);
						else 
							eapConfigurationImpl.setDefaultPEAPNegotiationMethod(eapConfigurationImpl.getPeapMethodDetail().getDefaultPEAPNegotiationMethod());
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, " Default PEAP Negotiation Method parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}

					if(rsForEapConf.getString("TREAT_INVALID_PACKET_AS_FATAL")!=null &&rsForEapConf.getString("TREAT_INVALID_PACKET_AS_FATAL").length()>0){
						eapConfigurationImpl.setIsTreatInvalidPacketAsFatal(Boolean.parseBoolean(rsForEapConf.getString("TREAT_INVALID_PACKET_AS_FATAL")));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Treat Invalid Packet as fatal parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					if(rsForEapConf.getString("NOTIFICATION_SUCCESS")!=null && rsForEapConf.getString("NOTIFICATION_SUCCESS").length()>0){
						eapConfigurationImpl.setIsNotificationSuccess(Boolean.parseBoolean(rsForEapConf.getString("NOTIFICATION_SUCCESS")));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Notification Success parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					if(rsForEapConf.getString("NOTIFICATION_FAILURE")!=null && rsForEapConf.getString("NOTIFICATION_FAILURE").length()>0){
						eapConfigurationImpl.setIsNotificationFailure(Boolean.parseBoolean(rsForEapConf.getString("NOTIFICATION_FAILURE")));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Notification Failure parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					if(rsForEapConf.getString("SESSION_CLEANUP_INTERVAL")!=null && rsForEapConf.getString("SESSION_CLEANUP_INTERVAL").length()>0)
						eapConfigurationImpl.setSessionCleanupInterval(Numbers.parseLong(rsForEapConf.getString("SESSION_CLEANUP_INTERVAL"),eapConfigurationImpl.getSessionCleanupInterval()));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Session Cleanup Interval parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					if(rsForEapConf.getString("SESSION_DURATION_FOR_CLEANUP")!=null && rsForEapConf.getString("SESSION_DURATION_FOR_CLEANUP").length()>0)
						eapConfigurationImpl.setSessionDurationForCleanup(Numbers.parseLong(rsForEapConf.getString("SESSION_DURATION_FOR_CLEANUP"),eapConfigurationImpl.getSessionDurationForCleanup()));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Session Duration for Cleanup parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					if(rsForEapConf.getString("SESSION_TIMEOUT")!=null && rsForEapConf.getString("SESSION_TIMEOUT").length()>0)
						eapConfigurationImpl.setSessionTimeout(Numbers.parseLong(rsForEapConf.getString("SESSION_TIMEOUT"),eapConfigurationImpl.getSessionTimeout()));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Session Timeout parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					readMSKRevalidationTime(rsForEapConf, eapConfigurationImpl, name);
					
					if(rsForEapConf.getString("MAX_EAP_PACKET_SIZE")!=null && rsForEapConf.getString("MAX_EAP_PACKET_SIZE").length()>0)
						eapConfigurationImpl.setMaxEapPacketSize(Numbers.parseInt(rsForEapConf.getString("MAX_EAP_PACKET_SIZE"),eapConfigurationImpl.getMaxEapPacketSize()));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Max EAP Packet Size parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}

					if(rsForEapConf.getString("EAP_TTLS_CERTIFICATE_REQUEST")!=null && rsForEapConf.getString("EAP_TTLS_CERTIFICATE_REQUEST").length()>0){
						eapConfigurationImpl.setTTLSCertificateRequest(Boolean.parseBoolean(rsForEapConf.getString("EAP_TTLS_CERTIFICATE_REQUEST")));
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "TTLS Certificate Request parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					if(rsForEapConf.getString("EAP_PEAP_CERTIFICATE_REQUEST")!=null && rsForEapConf.getString("EAP_PEAP_CERTIFICATE_REQUEST").length()>0){
						boolean peapCertificateRequest =Boolean.parseBoolean(rsForEapConf.getString("EAP_PEAP_CERTIFICATE_REQUEST"));
						eapConfigurationImpl.setIsPEAPCertificateRequest(peapCertificateRequest);
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "PEAP Certificate Request parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					if(rsForEapConf.getString("ENABLEDAUTHMETHODS")!=null && rsForEapConf.getString("ENABLEDAUTHMETHODS").length()>0){
						String enableAuthMethodsStr = rsForEapConf.getString("ENABLEDAUTHMETHODS");
						eapConfigurationImpl.setStrEnabledAuthMethods(enableAuthMethodsStr);
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Enable Auth Methods parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					query = getQueryForTLSConfiguration();
					psForTlsConf = connection.prepareStatement(query);
					if(psForTlsConf == null){
						throw new SQLException("Prepared Statement is null for TLSConfiguration");
					}
					psForTlsConf.setString(1, eapId);
					rsForTlsConf = psForTlsConf.executeQuery();

					TLSConfigurationImpl tlsConfiguartionImpl = new TLSConfigurationImpl();

					if(rsForTlsConf.next()){
						String eapTlsId = rsForTlsConf.getString("EAP_TLS_ID");
						
						readTLSVersionWindow(rsForTlsConf, name, tlsConfiguartionImpl);

						if(rsForTlsConf.getString("CERTIFICATE_REQUEST")!=null && rsForTlsConf.getString("CERTIFICATE_REQUEST").length()>0)
							tlsConfiguartionImpl.setIsTlsCertificateRequest(ConfigurationUtil.stringToBoolean(rsForTlsConf.getString("CERTIFICATE_REQUEST"), tlsConfiguartionImpl.tlsCertificateRequest));
						else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "TLS Certificate Request parameter for Eap Configuration :"+name+" is not defined , using default value.");
						}
						
						readValidationDetails(rsForTlsConf, name, tlsConfiguartionImpl);

						if(rsForTlsConf.getString("DEFAULT_COMPRESSION_METHOD")!=null && rsForTlsConf.getString("DEFAULT_COMPRESSION_METHOD").length()>0)
							tlsConfiguartionImpl.setDefaultCompressionMethod(Numbers.parseInt(rsForTlsConf.getString("DEFAULT_COMPRESSION_METHOD"), tlsConfiguartionImpl.defaultCompressionMethod));
						else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "TLS Default Compression Method parameter for Eap Configuration :"+name+" is not defined , using default value.");
						}
						if(rsForTlsConf.getString("SESSION_RESUMPTION_DURATION")!=null && rsForTlsConf.getString("SESSION_RESUMPTION_DURATION").length()>0)
							tlsConfiguartionImpl.setSessionResumptionDuration(Numbers.parseInt(rsForTlsConf.getString("SESSION_RESUMPTION_DURATION"), tlsConfiguartionImpl.sessionResumptionDuration));
						else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "TLS Session Resumption Duration parameter for Eap Configuration :"+name+" is not defined , using default value.");
						}
						if(rsForTlsConf.getString("SESSION_RESUMPTION_LIMIT")!=null && rsForTlsConf.getString("SESSION_RESUMPTION_LIMIT").length()>0)
							tlsConfiguartionImpl.setSessionResumptionLimit(Numbers.parseInt(rsForTlsConf.getString("SESSION_RESUMPTION_LIMIT"), tlsConfiguartionImpl.sessionResumptionLimit));
						else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "TLS Session Resumption Limit parameter for Eap Configuration :"+name+" is not defined , using default value.");
						}

						if(rsForTlsConf.getString("CIPHERSUITE_LIST")!=null && rsForTlsConf.getString("CIPHERSUITE_LIST").length()>0){
							String cipherCuiteTypesStr = rsForTlsConf.getString("CIPHERSUITE_LIST");
							tlsConfiguartionImpl.setEnabledCipherSuites(cipherCuiteTypesStr);
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "TLS CipherSuite List parameter for Eap Configuration :"+name+" is not defined , using default value.");
						}
						CertificateConfigurationImpl certificateConfiguration = new CertificateConfigurationImpl();

						certificateConfiguration.setServerCertificateId(rsForTlsConf.getString("SERVERCERTIFICATEID"));

						if(rsForTlsConf.getString("CERTIFICATE_TYPES_LIST")!=null && rsForTlsConf.getString("CERTIFICATE_TYPES_LIST").length()>0){
							String certificateTypesStr = rsForTlsConf.getString("CERTIFICATE_TYPES_LIST");
							certificateConfiguration.setCertificateType(certificateTypesStr);
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Certificate Types List parameter for Eap Configuration :"+name+" is not defined , using default value.");
						}
						query = getQueryForCertificateConfiguration();
						psForCertConf = connection.prepareStatement(query);
						if(psForCertConf == null){
							throw new SQLException("Prepared Statement is null for Certificate Configuration");
						}
						psForCertConf.setString(1, eapTlsId);
						rsForCertConf = psForCertConf.executeQuery();

						List<VendorSpecificCertificateImpl> vendorSpecificCertList = new ArrayList<VendorSpecificCertificateImpl>();
						VendorSpecificCertificateImpl vendorSpecificCertConfig = null;
						VendorSpecificCertificateDetail vendorList = new VendorSpecificCertificateDetail();

						while (rsForCertConf.next()) {
							vendorSpecificCertConfig = new VendorSpecificCertificateImpl();

							if(rsForCertConf.getString("OUI")!=null && rsForCertConf.getString("OUI").length()>0)
								vendorSpecificCertConfig.setOui(rsForCertConf.getString("OUI"));
							else{
								if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
									LogManager.getLogger().warn(MODULE, "Vendor Identifier parameter in vendor specific certificate for Eap Configuration :"+name+" is not defined , using default value: " + vendorSpecificCertConfig.getOui());
							}

							vendorSpecificCertConfig.setServerCertificateId(rsForCertConf.getString("SERVERCERTIFICATEID"));
							
							vendorSpecificCertList.add(vendorSpecificCertConfig);
						}
						
						vendorList.setVendorSpecificCertificateList(vendorSpecificCertList);
						certificateConfiguration.setVendorSpecificCertificateImpl(vendorList);
						tlsConfiguartionImpl.setCertificateConfiguration(certificateConfiguration);
					}

					eapConfigurationImpl.setTLSConfiguration(tlsConfiguartionImpl);

					query = getQueryForAKAConfiguration();
					psForAKAConfig = connection.prepareStatement(query);
					if(psForAKAConfig == null){
						throw new SQLException("Prepared Statement is null for AKAConfiguration");
					}
					psForAKAConfig.setString(1, eapId);
					rsForAKAConfig = psForAKAConfig.executeQuery();
					AKAConfigurationImpl akaConfigurationImpl = new AKAConfigurationImpl();

					if (rsForAKAConfig.next()){
						if (rsForAKAConfig.getString("DATASOURCE") != null && rsForAKAConfig.getString("DATASOURCE").trim().length() > 0){
							akaConfigurationImpl.setQuintupletDS(Numbers.parseInt(rsForAKAConfig.getString("DATASOURCE"), akaConfigurationImpl.getQuintupletDS()));
						}else{
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Triplet data source is not configured");
						} 					
						if (rsForAKAConfig.getString("LOCALHOSTID") != null && rsForAKAConfig.getString("LOCALHOSTID").trim().length() > 0){
							akaConfigurationImpl.setLocalHostId(rsForAKAConfig.getString("LOCALHOSTID"));
						}else{
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Local host id is not configured");
						} 
						if (rsForAKAConfig.getString("LOCALHOSTPORT") != null && rsForAKAConfig.getString("LOCALHOSTPORT").trim().length() > 0){
							akaConfigurationImpl.setLocalHostPort(Numbers.parseInt(rsForAKAConfig.getString("LOCALHOSTPORT"),0));
						}else{
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Local host port is not configured");
						}
						if (rsForAKAConfig.getString("LOCALHOSTIP") != null && rsForAKAConfig.getString("LOCALHOSTIP").trim().length() > 0){
							akaConfigurationImpl.setLocalHostIp(rsForAKAConfig.getString("LOCALHOSTIP"));
						}else{
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Local host ip is not configured");
						} 
						if (rsForAKAConfig.getString("REMOTEHOSTID") != null && rsForAKAConfig.getString("REMOTEHOSTID").trim().length() > 0){
							akaConfigurationImpl.setRemoteHostId(rsForAKAConfig.getString("REMOTEHOSTID"));
						}else{
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Remote host id is not configured");
						} 
						if (rsForAKAConfig.getString("REMOTEHOSTPORT") != null && rsForAKAConfig.getString("REMOTEHOSTPORT").trim().length() > 0){
							akaConfigurationImpl.setRemoteHostPort(Numbers.parseInt(rsForAKAConfig.getString("REMOTEHOSTPORT"),0));
						}else{
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Remote host port is not configured");
						} 
						if (rsForAKAConfig.getString("REMOTEHOSTIP") != null && rsForAKAConfig.getString("REMOTEHOSTIP").trim().length() > 0){
							akaConfigurationImpl.setRemoteHostIp(rsForAKAConfig.getString("REMOTEHOSTIP"));
						}else{
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Remote host ip is not configured");
						}

						if (rsForAKAConfig.getString("PSEUDONYMGENMETHOD") != null && rsForAKAConfig.getString("PSEUDONYMGENMETHOD").trim().length() > 0){
							String strPseudonymIdMethod = rsForAKAConfig.getString("PSEUDONYMGENMETHOD");
							int iPseudonymIdMethod = 0;
							if (("BASE16").equalsIgnoreCase(strPseudonymIdMethod)){
								iPseudonymIdMethod = 16;
							} else if (("BASE32").equalsIgnoreCase(strPseudonymIdMethod)){
								iPseudonymIdMethod = 32;
							} else if (("BASE64").equalsIgnoreCase(strPseudonymIdMethod)){
								iPseudonymIdMethod = 64;
							} else if (("ELITECRYPT").equalsIgnoreCase(strPseudonymIdMethod)){
								iPseudonymIdMethod = 3;
							} else if (("BASIC_ALPHA_1").equalsIgnoreCase(strPseudonymIdMethod)){
								iPseudonymIdMethod = 1;
							}
							akaConfigurationImpl.setPseudonymGenMethod(iPseudonymIdMethod);
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE, "Pseudonym generation method for AKA is not configured. Default is: " + akaConfigurationImpl.getPseudonymGenMethod());
							}
						} 
						if (rsForAKAConfig.getString("PSEUDONYMHEXENCODING") != null && rsForAKAConfig.getString("PSEUDONYMHEXENCODING").trim().length() > 0){
							if (!rsForAKAConfig.getString("PSEUDONYMHEXENCODING").equalsIgnoreCase("ENABLE")){
								akaConfigurationImpl.setHexEncoding(false);
							}
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE, "Pseudonym Hex Encoding for AKA is not configured. Default is: " + akaConfigurationImpl.getIsPseudoHexEncoding());
							}
						} 
						if (rsForAKAConfig.getString("PSEUDONYMPREFIX") != null && rsForAKAConfig.getString("PSEUDONYMPREFIX").trim().length() > 0){
							akaConfigurationImpl.setPseudonymPrefix(rsForAKAConfig.getString("PSEUDONYMPREFIX"));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE, "Pseudonym Prefix for AKA is not configured. Default is: " + akaConfigurationImpl.getPseudonymPrefix());
							}
						}

						if (rsForAKAConfig.getString("FASTREAUTHGENMETHOD") != null && rsForAKAConfig.getString("FASTREAUTHGENMETHOD").trim().length() > 0){
							String strFastReauthIdMethod = rsForAKAConfig.getString("FASTREAUTHGENMETHOD");
							int iFastReauthIdMethod = 0;
							if (("BASE16").equalsIgnoreCase(strFastReauthIdMethod)){
								iFastReauthIdMethod = 16;
							} else if (("BASE32").equalsIgnoreCase(strFastReauthIdMethod)){
								iFastReauthIdMethod = 32;
							} else if (("BASE64").equalsIgnoreCase(strFastReauthIdMethod)){
								iFastReauthIdMethod = 64;
							} else if (("ELITECRYPT").equalsIgnoreCase(strFastReauthIdMethod)){
								iFastReauthIdMethod = 3;
							} else if (("BASIC_ALPHA_1").equalsIgnoreCase(strFastReauthIdMethod)){
								iFastReauthIdMethod = 1;
							}
							akaConfigurationImpl.setFastReauthGenMethod(iFastReauthIdMethod);
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE, "FastReAuth generation method for AKA is not configured. Default is: " + akaConfigurationImpl.getPseudonymGenMethod());
							}
						} 

						if (rsForAKAConfig.getString("FASTREAUTHHEXENCODING") != null && rsForAKAConfig.getString("FASTREAUTHHEXENCODING").trim().length() > 0){
							if (!rsForAKAConfig.getString("FASTREAUTHHEXENCODING").equalsIgnoreCase("ENABLE")){
								akaConfigurationImpl.setIsFastReauthHexEncoding(false);
							}
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE, "FastReAuth Hex Encoding for AKA is not configured. Default is: " + akaConfigurationImpl.getIsPseudoHexEncoding());
							}
						} 
						if (rsForAKAConfig.getString("FASTREAUTHPREFIX") != null && rsForAKAConfig.getString("FASTREAUTHPREFIX").trim().length() > 0){
							akaConfigurationImpl.setFastReauthPrefix(rsForAKAConfig.getString("FASTREAUTHPREFIX"));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
								LogManager.getLogger().trace(MODULE, "FastReAuth Prefix for AKA is not configured. Default is: " + akaConfigurationImpl.getPseudonymPrefix());
							}
						} 

					} 
					eapConfigurationImpl.setAKAConfiguration(akaConfigurationImpl);

					query = getQueryForAKAPrimeConfiguration();
					psForAKAPrimeConfig = connection.prepareStatement(query);
					if(psForAKAPrimeConfig == null){
						throw new SQLException("Prepared Statement is null for AKA Prime Configuration");
					}
					psForAKAPrimeConfig.setString(1, eapId);
					rsForAKAPrimeConfig = psForAKAPrimeConfig.executeQuery();
					
					AKAPrimeConfigurationImpl akaPrimeConfigurationImpl = readAKAPrimeConfiguration(connection, rsForAKAPrimeConfig, name);
					eapConfigurationImpl.setAkaPrimeConfiguration(akaPrimeConfigurationImpl);
					
					query = getQueryForSIMConfiguration();
					psForSIMConfig = connection.prepareStatement(query);
					if(psForSIMConfig == null){
						throw new SQLException("Prepared Statement is null for SIMConfiguration");
					}
					psForSIMConfig.setString(1, eapId);
					rsForSIMConfig = psForSIMConfig.executeQuery();
					SIMConfigurationImpl simConfigurationImpl = new SIMConfigurationImpl();

					if (rsForSIMConfig.next()){
						if (rsForSIMConfig.getString("DATASOURCE") != null && rsForSIMConfig.getString("DATASOURCE").trim().length() > 0){
							simConfigurationImpl.setTripletDS(Numbers.parseInt(rsForSIMConfig.getString("DATASOURCE"), simConfigurationImpl.getTripletDS()));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Triplet data source is not configured");
						} 
						if (rsForSIMConfig.getString("NUMBEROFTRIPLETS") != null && rsForSIMConfig.getString("NUMBEROFTRIPLETS").trim().length() > 0){
							simConfigurationImpl.setNoOFTriplet(Numbers.parseInt( rsForSIMConfig.getString("NUMBEROFTRIPLETS"),3));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Number of triplets is not configured");
						} 
						if (rsForSIMConfig.getString("LOCALHOSTID") != null && rsForSIMConfig.getString("LOCALHOSTID").trim().length() > 0){
							simConfigurationImpl.setLocalHostId(rsForSIMConfig.getString("LOCALHOSTID"));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Local host id is not configured");
						} 
						if (rsForSIMConfig.getString("LOCALHOSTPORT") != null && rsForSIMConfig.getString("LOCALHOSTPORT").trim().length() > 0){
							simConfigurationImpl.setLocalHostPort(Numbers.parseInt(rsForSIMConfig.getString("LOCALHOSTPORT"),0));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Local host port is not configured");
						} 
						if (rsForSIMConfig.getString("LOCALHOSTIP") != null && rsForSIMConfig.getString("LOCALHOSTIP").trim().length() > 0){
							simConfigurationImpl.setLocalHostIp(rsForSIMConfig.getString("LOCALHOSTIP"));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Local host ip is not configured");
						} 
						if (rsForSIMConfig.getString("REMOTEHOSTID") != null && rsForSIMConfig.getString("REMOTEHOSTID").trim().length() > 0){
							simConfigurationImpl.setRemoteHostId(rsForSIMConfig.getString("REMOTEHOSTID"));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Remote host id is not configured");
						} 
						if (rsForSIMConfig.getString("REMOTEHOSTPORT") != null && rsForSIMConfig.getString("REMOTEHOSTPORT").trim().length() > 0){
							simConfigurationImpl.setRemoteHostPort(Numbers.parseInt(rsForSIMConfig.getString("REMOTEHOSTPORT"),0));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Remote host port  is not configured");
						} 
						if (rsForSIMConfig.getString("REMOTEHOSTIP") != null && rsForSIMConfig.getString("REMOTEHOSTIP").trim().length() > 0){
							simConfigurationImpl.setRemoteHostIp(rsForSIMConfig.getString("REMOTEHOSTIP"));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, "Remote host ip is not configured");
						} 
						if (rsForSIMConfig.getString("PSEUDONYMGENMETHOD") != null && rsForSIMConfig.getString("PSEUDONYMGENMETHOD").trim().length() > 0){
							String strPseudonymIdMethod = rsForSIMConfig.getString("PSEUDONYMGENMETHOD");
							int iPseudonymIdMethod = 0;
							if (("BASE16").equalsIgnoreCase(strPseudonymIdMethod)){
								iPseudonymIdMethod = 16;
							} else if (("BASE32").equalsIgnoreCase(strPseudonymIdMethod)){
								iPseudonymIdMethod = 32;
							} else if (("BASE64").equalsIgnoreCase(strPseudonymIdMethod)){
								iPseudonymIdMethod = 64;
							} else if (("ELITECRYPT").equalsIgnoreCase(strPseudonymIdMethod)){
								iPseudonymIdMethod = 3;
							} else if (("BASIC_ALPHA_1").equalsIgnoreCase(strPseudonymIdMethod)){
								iPseudonymIdMethod = 1;
							}
							simConfigurationImpl.setPseudonymGenMethod(iPseudonymIdMethod);
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Pseudonym generation method for SIM is not configured. Default is: " + simConfigurationImpl.getPseudoGenMethod());
							}
						} 
						if (rsForSIMConfig.getString("PSEUDONYMHEXENCODING") != null && rsForSIMConfig.getString("PSEUDONYMHEXENCODING").trim().length() > 0){
							if (!rsForSIMConfig.getString("PSEUDONYMHEXENCODING").equalsIgnoreCase("ENABLE")){
								simConfigurationImpl.setIsPseudoHexEncoding(false);
							}
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Pseudonym Hex Encoding for SIM is not configured. Default is: " + simConfigurationImpl.getIsPseudoHexEncoding());
							}
						} 
						if (rsForSIMConfig.getString("PSEUDONYMPREFIX") != null && rsForSIMConfig.getString("PSEUDONYMPREFIX").trim().length() > 0){
							simConfigurationImpl.setPseudonymPrefix(rsForSIMConfig.getString("PSEUDONYMPREFIX"));
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Pseudonym Prefix for SIM is not configured. Default is: " + simConfigurationImpl.getPseudoPrefix());
							}
						}

						if (rsForSIMConfig.getString("FASTREAUTHGENMETHOD") != null && rsForSIMConfig.getString("FASTREAUTHGENMETHOD").trim().length() > 0){
							String strFastReauthIdMethod = rsForSIMConfig.getString("FASTREAUTHGENMETHOD");
							int iFastReauthIdMethod = 0;
							if (("BASE16").equalsIgnoreCase(strFastReauthIdMethod)){
								iFastReauthIdMethod = 16;
							} else if (("BASE32").equalsIgnoreCase(strFastReauthIdMethod)){
								iFastReauthIdMethod = 32;
							} else if (("BASE64").equalsIgnoreCase(strFastReauthIdMethod)){
								iFastReauthIdMethod = 64;
							} else if (("ELITECRYPT").equalsIgnoreCase(strFastReauthIdMethod)){
								iFastReauthIdMethod = 3;
							} else if (("BASIC_ALPHA_1").equalsIgnoreCase(strFastReauthIdMethod)){
								iFastReauthIdMethod = 1;
							}
							simConfigurationImpl.setFastReauthGenMethod(iFastReauthIdMethod);
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Fast Reauth generation method for SIM is not configured. Default is: " + simConfigurationImpl.getFastReauthGenMethod());
							}
						}
						if (rsForSIMConfig.getString("FASTREAUTHHEXENCODING") != null && rsForSIMConfig.getString("FASTREAUTHHEXENCODING").trim().length() > 0){
							if (!rsForSIMConfig.getString("FASTREAUTHHEXENCODING").equalsIgnoreCase("ENABLE")){
								simConfigurationImpl.setIsFastReauthHexEncoding(false);
							}
						}else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Fast Reauth Hex Encoding for SIM is not configured. Default is: " + simConfigurationImpl.getIsFastReauthHexEncoding());
							}
						}
						if (rsForSIMConfig.getString("FASTREAUTHPREFIX") != null && rsForSIMConfig.getString("FASTREAUTHPREFIX").trim().length() > 0){
							simConfigurationImpl.setFastReauthPrefix(rsForSIMConfig.getString("FASTREAUTHPREFIX"));
						} 
					}else {
						if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, "Fast Reauth Prefix for SIM is not configured. Default is: " + simConfigurationImpl.getFastReauthPrefix());
						}
					}
					eapConfigurationImpl.setSIMConfiguration(simConfigurationImpl);
					tempEapConfList.add(eapConfigurationImpl);
				}
			}
			this.eapConfigurationList = tempEapConfList;
		} finally{
			
			DBUtility.closeQuietly(rsForCertConf);
			DBUtility.closeQuietly(psForCertConf);
			DBUtility.closeQuietly(rsForTlsConf);
			DBUtility.closeQuietly(psForTlsConf);
			DBUtility.closeQuietly(rsForEapConf);
			DBUtility.closeQuietly(psForEapConf);
			DBUtility.closeQuietly(psForAKAConfig);
			DBUtility.closeQuietly(rsForAKAConfig);
			DBUtility.closeQuietly(psForSIMConfig);
			DBUtility.closeQuietly(rsForSIMConfig);
			DBUtility.closeQuietly(psForAKAPrimeConfig);
			DBUtility.closeQuietly(rsForAKAPrimeConfig);
			DBUtility.closeQuietly(psForEapConfigurations);
			DBUtility.closeQuietly(rsForEapConfigurations);
			DBUtility.closeQuietly(connection);
		}
	}
	private void readMSKRevalidationTime(ResultSet rsForEapConf, EAPConfigurationDataImpl eapConfigurationImpl,
			String name) throws SQLException {
		if (DBUtility.isValueAvailable(rsForEapConf, "MSK_REVALIDATION_TIME")) {
			eapConfigurationImpl.setMskRevalidationTime(rsForEapConf.getLong("MSK_REVALIDATION_TIME"));
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "MSK revalidation time parameter for Eap Configuration :"+name+" is not defined , using default value: " + eapConfigurationImpl.getMskRevalidationTime());
		}
	}
	
	private AKAPrimeConfigurationImpl readAKAPrimeConfiguration(Connection connection, ResultSet rsForAKAPrimeConfig, String eapConfName) throws SQLException {
	
		AKAPrimeConfigurationImpl akaPrimeConfigurationImpl = new AKAPrimeConfigurationImpl();

		if (rsForAKAPrimeConfig.next()){
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "DATASOURCE")){
				akaPrimeConfigurationImpl.setQuintupletDS(Numbers.parseInt(rsForAKAPrimeConfig.getString("DATASOURCE"), akaPrimeConfigurationImpl.getQuintupletDS()));
			}else{
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Triplet data source is not configured for AKA Prime Configuration in " + eapConfName);
			} 					
			if(DBUtility.isValueAvailable(rsForAKAPrimeConfig, "LOCALHOSTID")) {
				akaPrimeConfigurationImpl.setLocalHostId(rsForAKAPrimeConfig.getString("LOCALHOSTID"));
			}else{
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Local host id is not configured for AKA Prime Configuration in " + eapConfName);
			} 
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "LOCALHOSTPORT")){
				akaPrimeConfigurationImpl.setLocalHostPort(Numbers.parseInt(rsForAKAPrimeConfig.getString("LOCALHOSTPORT"),0));
			}else{
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Local host port is not configured for AKA Prime Configuration in " + eapConfName);
			}
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "LOCALHOSTIP")){
				akaPrimeConfigurationImpl.setLocalHostIp(rsForAKAPrimeConfig.getString("LOCALHOSTIP"));
			}else{
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Local host ip is not configured for AKA Prime Configuration in " + eapConfName);
			} 
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "REMOTEHOSTID")){
				akaPrimeConfigurationImpl.setRemoteHostId(rsForAKAPrimeConfig.getString("REMOTEHOSTID"));
			}else{
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Remote host id is not configured for AKA Prime Configuration in " + eapConfName);
			} 
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "REMOTEHOSTPORT")){
				akaPrimeConfigurationImpl.setRemoteHostPort(Numbers.parseInt(rsForAKAPrimeConfig.getString("REMOTEHOSTPORT"),0));
			}else{
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Remote host port is not configured for AKA Prime Configuration in " + eapConfName);
			} 
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "REMOTEHOSTIP")){
				akaPrimeConfigurationImpl.setRemoteHostIp(rsForAKAPrimeConfig.getString("REMOTEHOSTIP"));
			}else{
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Remote host ip is not configured for AKA Prime Configuaration in " + eapConfName);
			}

			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "PSEUDONYMGENMETHOD")){
				String strPseudonymIdMethod = rsForAKAPrimeConfig.getString("PSEUDONYMGENMETHOD");
				int iPseudonymIdMethod = 0;
				if (("BASE16").equalsIgnoreCase(strPseudonymIdMethod)){
					iPseudonymIdMethod = 16;
				} else if (("BASE32").equalsIgnoreCase(strPseudonymIdMethod)){
					iPseudonymIdMethod = 32;
				} else if (("BASE64").equalsIgnoreCase(strPseudonymIdMethod)){
					iPseudonymIdMethod = 64;
				} else if (("ELITECRYPT").equalsIgnoreCase(strPseudonymIdMethod)){
					iPseudonymIdMethod = 3;
				} else if (("BASIC_ALPHA_1").equalsIgnoreCase(strPseudonymIdMethod)){
					iPseudonymIdMethod = 1;
				}
				akaPrimeConfigurationImpl.setPseudonymGenMethod(iPseudonymIdMethod);
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "Pseudonym generation method for AKA Prime is not configured in: " + eapConfName + ". Default is: " + akaPrimeConfigurationImpl.getPseudonymGenMethod());
				}
			} 
			
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "PSEUDONYMHEXENCODING")){
				if (!rsForAKAPrimeConfig.getString("PSEUDONYMHEXENCODING").equalsIgnoreCase("ENABLE")){
					akaPrimeConfigurationImpl.setHexEncoding(false);
				}
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "Pseudonym Hex Encoding for AKA Prime is not configured in: " + eapConfName + ". Default is: " + akaPrimeConfigurationImpl.getIsPseudoHexEncoding());
				}
			} 
			
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "PSEUDONYMPREFIX")){
				akaPrimeConfigurationImpl.setPseudonymPrefix(rsForAKAPrimeConfig.getString("PSEUDONYMPREFIX"));
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "Pseudonym Prefix for AKA Prime is not configured in: " + eapConfName + ". Default is: " + akaPrimeConfigurationImpl.getPseudonymPrefix());
				}
			}
			
			
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "PSEUDONYMROOTNAI")){
				if (!rsForAKAPrimeConfig.getString("PSEUDONYMROOTNAI").equalsIgnoreCase("DISABLE")){
					akaPrimeConfigurationImpl.setPseudonymIsRootNai(true);
				}
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "Pseudonym Root Nai for AKA Prime is not configured in: " + eapConfName + ". Default is: " + akaPrimeConfigurationImpl.getPseudonymIsRootNai());
				}
			}
			
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "PSEUDONYMAAAIDENTITYROOTNAI")){
				akaPrimeConfigurationImpl.setPseudonymAAAIdInRootNai(rsForAKAPrimeConfig.getString("PSEUDONYMAAAIDENTITYROOTNAI"));
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "Pseudonym AAA Identity Root Nai for AKA Prime is not configured in: " + eapConfName + ". Default is: " + akaPrimeConfigurationImpl.getPseudonymAAAIdInRootNai());
				}
			}
			
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "FASTREAUTHGENMETHOD")){
				String strFastReauthIdMethod = rsForAKAPrimeConfig.getString("FASTREAUTHGENMETHOD");
				int iFastReauthIdMethod = 0;
				if (("BASE16").equalsIgnoreCase(strFastReauthIdMethod)){
					iFastReauthIdMethod = 16;
				} else if (("BASE32").equalsIgnoreCase(strFastReauthIdMethod)){
					iFastReauthIdMethod = 32;
				} else if (("BASE64").equalsIgnoreCase(strFastReauthIdMethod)){
					iFastReauthIdMethod = 64;
				} else if (("ELITECRYPT").equalsIgnoreCase(strFastReauthIdMethod)){
					iFastReauthIdMethod = 3;
				} else if (("BASIC_ALPHA_1").equalsIgnoreCase(strFastReauthIdMethod)){
					iFastReauthIdMethod = 1;
				}
				akaPrimeConfigurationImpl.setFastReauthGenMethod(iFastReauthIdMethod);
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "FastReAuth generation method for AKA Prime is not configured in: " + eapConfName + ". Default is: " + akaPrimeConfigurationImpl.getPseudonymGenMethod());
				}
			} 

			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "FASTREAUTHHEXENCODING")){
				if (!rsForAKAPrimeConfig.getString("FASTREAUTHHEXENCODING").equalsIgnoreCase("ENABLE")){
					akaPrimeConfigurationImpl.setIsFastReauthHexEncoding(false);
				}
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "FastReAuth Hex Encoding for AKA Prime is not configured in: " + eapConfName + ". Default is: " + akaPrimeConfigurationImpl.getIsPseudoHexEncoding());
				}
			} 
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "FASTREAUTHPREFIX")){
				akaPrimeConfigurationImpl.setFastReauthPrefix(rsForAKAPrimeConfig.getString("FASTREAUTHPREFIX"));
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "FastReAuth Prefix for AKA Prime is not configured in: " + eapConfName + ". Default is: " + akaPrimeConfigurationImpl.getPseudonymPrefix());
				}
			} 

			
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "FASTREAUTHROOTNAI")){
				if (!rsForAKAPrimeConfig.getString("FASTREAUTHROOTNAI").equalsIgnoreCase("DISABLE")){
					akaPrimeConfigurationImpl.setFastIsRootNai(true);
				}
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "FastReAuth Root Nai for AKA Prime is not configured in: " + eapConfName + ". Default is: " + akaPrimeConfigurationImpl.getFastIsRootNai());
				}
			}
			
			if (DBUtility.isValueAvailable(rsForAKAPrimeConfig, "FASTREAUTHAAAIDENTITYROOTNAI")){
				akaPrimeConfigurationImpl.setFastAAAIdInRootNai(rsForAKAPrimeConfig.getString("FASTREAUTHAAAIDENTITYROOTNAI"));
			}else {
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					LogManager.getLogger().trace(MODULE, "FastReAuth AAA Identity Root Nai for AKA Prime is not configured in: " + eapConfName + ". Default is: " + akaPrimeConfigurationImpl.getFastAAAIdInRootNai());
				}
			}
		} 
		return akaPrimeConfigurationImpl;

		
	}
	private void readValidationDetails(ResultSet rsForTlsConf, String name, TLSConfigurationImpl tlsConfiguartionImpl) throws SQLException {
		if(DBUtility.isValueAvailable(rsForTlsConf, "VALIDATECERTIFICATEEXPIRY")) {
			tlsConfiguartionImpl.setValidateCertificateExpiry(ConfigurationUtil.stringToBoolean(rsForTlsConf.getString("VALIDATECERTIFICATEEXPIRY"), tlsConfiguartionImpl.validateCertificateExpiry));
		} else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Validate certificate expiry parameter for Eap Configuration :" + name + " is not defined , using default value: " + tlsConfiguartionImpl.validateCertificateExpiry);
		}
		
		if(DBUtility.isValueAvailable(rsForTlsConf, "VALIDATECERTIFICATEREVOCATION")) {
			tlsConfiguartionImpl.setValidateCertificateRevocation(ConfigurationUtil.stringToBoolean(rsForTlsConf.getString("VALIDATECERTIFICATEREVOCATION"), tlsConfiguartionImpl.validateCertificateRevocation));
		} else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Validate certificate revocation parameter for Eap Configuration :"+name+" is not defined , using default value: " + tlsConfiguartionImpl.validateCertificateRevocation);
		}
		
		if(DBUtility.isValueAvailable(rsForTlsConf, "VALIDATEMAC")) {
			tlsConfiguartionImpl.setValidateMac(ConfigurationUtil.stringToBoolean(rsForTlsConf.getString("VALIDATEMAC"), tlsConfiguartionImpl.validateMac));
		} else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Validate MAC parameter for Eap Configuration :"+name+" is not defined , using default value: " + tlsConfiguartionImpl.validateMac);
		}
		
		if(DBUtility.isValueAvailable(rsForTlsConf, "VALIDATECLIENTCERTIFICATE")) {
			tlsConfiguartionImpl.setValidateClientCertificate(ConfigurationUtil.stringToBoolean(rsForTlsConf.getString("VALIDATECLIENTCERTIFICATE"), tlsConfiguartionImpl.validateClientCertificate));
		} else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Validate Client Certificate parameter for Eap Configuration :"+name+" is not defined , using default value: " + tlsConfiguartionImpl.validateClientCertificate);
		}
	}
	
	private void readTLSVersionWindow(ResultSet rsForTlsConf, String name, TLSConfigurationImpl tlsConfiguartionImpl) throws SQLException {
		if(DBUtility.isValueUnavailable(rsForTlsConf,"MINTLSVERSION") ||  DBUtility.isValueUnavailable(rsForTlsConf, "MAXTLSVERSION")){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Minimum or Maximum TLS Version parameter for Eap Configuration :"+name+" is not defined , using default value " + ProtocolVersion.TLS1_0);
			return;
		}
		
		String minTlsVersion = rsForTlsConf.getString("MINTLSVERSION");
		String maxTlsVersion = rsForTlsConf.getString("MAXTLSVERSION");
		
		if(ProtocolVersion.isValid(minTlsVersion) && ProtocolVersion.isValid(maxTlsVersion)){
			tlsConfiguartionImpl.setMinProtocolVersion(ProtocolVersion.fromVersion(minTlsVersion));
			tlsConfiguartionImpl.setMaxProtocolVersion(ProtocolVersion.fromVersion(maxTlsVersion));
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Minimum TLS Version: " + minTlsVersion + "  or Maximum TLS Version: " + maxTlsVersion + " parameter for Eap Configuration :"+name+" is invalid , using default value " + ProtocolVersion.TLS1_0);
		}
	}
	
	@DBReload
	public void reloadEAPConfiguration() throws Exception{
		int size = eapConfigurationList.size();
		if(size == 0){
			return;
		}
		
		StringBuilder queryBuilder = new StringBuilder("select * from tblmeapconfig where eap_id IN (");
		
		for(int i = 0; i < size-1; i++){
			EAPConfigurationDataImpl eapConfigurationData = eapConfigurationList.get(i);
			queryBuilder.append("'" + eapConfigurationData.getEapId() + "',");
		}
		queryBuilder.append("'" + eapConfigurationList.get(size - 1).getEapId() + "')");
		String queryForReload = queryBuilder.toString();
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		PreparedStatement preparedStatementForTLS = null;
		ResultSet rsForTLS = null;
		
		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = conn.prepareStatement(queryForReload);
			rs = preparedStatement.executeQuery();
							
			while(rs.next()){
				EAPConfigurationDataImpl eapConfigurationData = (EAPConfigurationDataImpl) eapConfIDMap.get(rs.getInt("EAP_ID"));
				String name = eapConfigurationData.getName();
				
				if(rs.getString("DEFAULT_NEGIOTATION_METHOD")!=null &&rs.getString("DEFAULT_NEGIOTATION_METHOD").length()>0)
					eapConfigurationData.setDefaultNegotiationMethod(Numbers.parseInt(rs.getString("DEFAULT_NEGIOTATION_METHOD"), eapConfigurationData.getDefaultNegotiationMethod()));
				else{
					
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, " Default Negotiation Method parameter for Eap Configuration :"+eapConfigurationData.getName()+" is not defined , using default value.");
				}
				if(rs.getString("TTLSNEGOTIATIONMETHOD")!=null &&rs.getString("TTLSNEGOTIATIONMETHOD").length()>0){
					int ttlsNegotiationMethod = Numbers.parseInt(rs.getString("TTLSNEGOTIATIONMETHOD"), eapConfigurationData.getTtlsMethodDetail().getDefaultTTLSNegotiationMethod());
					if(ttlsNegotiationMethod != 0 )
						eapConfigurationData.setDefaultTTLSNegotiationMethod(ttlsNegotiationMethod);
					else 
						eapConfigurationData.setDefaultTTLSNegotiationMethod(eapConfigurationData.getTtlsMethodDetail().getDefaultTTLSNegotiationMethod());
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, " Default TTLS Negotiation Method parameter for Eap Configuration :"+name+" is not defined , using default value.");
				}
				if(rs.getString("PEAPNEGOTIATIONMETHOD")!=null &&rs.getString("PEAPNEGOTIATIONMETHOD").length()>0){
					int peapNegotiationMethod = Numbers.parseInt(rs.getString("PEAPNEGOTIATIONMETHOD"), eapConfigurationData.getPeapMethodDetail().getDefaultPEAPNegotiationMethod());
					if(peapNegotiationMethod != 0 )
						eapConfigurationData.setDefaultPEAPNegotiationMethod(peapNegotiationMethod);
					else 
						eapConfigurationData.setDefaultPEAPNegotiationMethod(eapConfigurationData.getPeapMethodDetail().getDefaultPEAPNegotiationMethod());
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, " Default PEAP Negotiation Method parameter for Eap Configuration :"+name+" is not defined , using default value.");
				}

				if(rs.getString("TREAT_INVALID_PACKET_AS_FATAL")!=null &&rs.getString("TREAT_INVALID_PACKET_AS_FATAL").length()>0){
					eapConfigurationData.setIsTreatInvalidPacketAsFatal(Boolean.parseBoolean(rs.getString("TREAT_INVALID_PACKET_AS_FATAL")));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Treat Invalid Packet as fatal parameter for Eap Configuration :"+name+" is not defined , using default value.");
				}
				if(rs.getString("NOTIFICATION_SUCCESS")!=null && rs.getString("NOTIFICATION_SUCCESS").length()>0){
					eapConfigurationData.setIsNotificationSuccess(Boolean.parseBoolean(rs.getString("NOTIFICATION_SUCCESS")));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Notification Success parameter for Eap Configuration :"+name+" is not defined , using default value.");
				}
				if(rs.getString("NOTIFICATION_FAILURE")!=null && rs.getString("NOTIFICATION_FAILURE").length()>0){
					eapConfigurationData.setIsNotificationFailure(Boolean.parseBoolean(rs.getString("NOTIFICATION_FAILURE")));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Notification Failure parameter for Eap Configuration :"+name+" is not defined , using default value.");
				}
				if(rs.getString("SESSION_TIMEOUT")!=null && rs.getString("SESSION_TIMEOUT").length()>0)
					eapConfigurationData.setSessionTimeout(Numbers.parseLong(rs.getString("SESSION_TIMEOUT"),eapConfigurationData.getSessionTimeout()));
				else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Session Timeout parameter for Eap Configuration :"+name+" is not defined , using default value.");
				}
				
				readMSKRevalidationTime(rs, eapConfigurationData, name);
				
				if(rs.getString("MAX_EAP_PACKET_SIZE")!=null && rs.getString("MAX_EAP_PACKET_SIZE").length()>0)
					eapConfigurationData.setMaxEapPacketSize(Numbers.parseInt(rs.getString("MAX_EAP_PACKET_SIZE"),eapConfigurationData.getMaxEapPacketSize()));
				else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Max EAP Packet Size parameter for Eap Configuration :"+name+" is not defined , using default value.");
				}

				if(rs.getString("EAP_TTLS_CERTIFICATE_REQUEST")!=null && rs.getString("EAP_TTLS_CERTIFICATE_REQUEST").length()>0){
					eapConfigurationData.setTTLSCertificateRequest(Boolean.parseBoolean(rs.getString("EAP_TTLS_CERTIFICATE_REQUEST")));
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "TTLS Certificate Request parameter for Eap Configuration :"+name+" is not defined , using default value.");
				}
				if(rs.getString("EAP_PEAP_CERTIFICATE_REQUEST")!=null && rs.getString("EAP_PEAP_CERTIFICATE_REQUEST").length()>0){
					boolean peapCertificateRequest =Boolean.parseBoolean(rs.getString("EAP_PEAP_CERTIFICATE_REQUEST"));
					eapConfigurationData.setIsPEAPCertificateRequest(peapCertificateRequest);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "PEAP Certificate Request parameter for Eap Configuration :"+name+" is not defined , using default value.");
				}
				
				//now reloading the TLS specific parameters
				preparedStatementForTLS = conn.prepareStatement(getQueryForTLSConfiguration());
				if(preparedStatementForTLS == null){
					throw new SQLException("Prepared Statement is null for TLSConfiguration");
				}
				
				preparedStatementForTLS.setString(1, eapConfigurationData.getEapId());
				rsForTLS = preparedStatementForTLS.executeQuery();
				
				if(rsForTLS.next()){
					
					readTLSVersionWindow(rsForTLS, name, eapConfigurationData.getTLSConfiguration());
					
					if(rsForTLS.getString("CERTIFICATE_REQUEST")!=null && rsForTLS.getString("CERTIFICATE_REQUEST").length()>0)
						eapConfigurationData.getTLSConfiguration().setIsTlsCertificateRequest(ConfigurationUtil.stringToBoolean(rsForTLS.getString("CERTIFICATE_REQUEST"), eapConfigurationData.getTLSConfiguration().tlsCertificateRequest));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "TLS Certificate Request parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}

					if(rsForTLS.getString("DEFAULT_COMPRESSION_METHOD")!=null && rsForTLS.getString("DEFAULT_COMPRESSION_METHOD").length()>0)
						eapConfigurationData.getTLSConfiguration().setDefaultCompressionMethod(Numbers.parseInt(rsForTLS.getString("DEFAULT_COMPRESSION_METHOD"), eapConfigurationData.getTLSConfiguration().defaultCompressionMethod));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "TLS Default Compression Method parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					if(rsForTLS.getString("SESSION_RESUMPTION_DURATION")!=null && rsForTLS.getString("SESSION_RESUMPTION_DURATION").length()>0)
						eapConfigurationData.getTLSConfiguration().setSessionResumptionDuration(Numbers.parseInt(rsForTLS.getString("SESSION_RESUMPTION_DURATION"), eapConfigurationData.getTLSConfiguration().sessionResumptionDuration));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "TLS Session Resumption Duration parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
					if(rsForTLS.getString("SESSION_RESUMPTION_LIMIT")!=null && rsForTLS.getString("SESSION_RESUMPTION_LIMIT").length()>0)
						eapConfigurationData.getTLSConfiguration().setSessionResumptionLimit(Numbers.parseInt(rsForTLS.getString("SESSION_RESUMPTION_LIMIT"), eapConfigurationData.getTLSConfiguration().sessionResumptionLimit));
					else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "TLS Session Resumption Limit parameter for Eap Configuration :"+name+" is not defined , using default value.");
					}
				}
			}
		} finally{
			DBUtility.closeQuietly(conn);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(preparedStatementForTLS);
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(rsForTLS);
		}
	}
	
	private String getQueryForEapConfiguration(){
		return "select * from tblmeapconfig where eap_id =? ";
	}
	private String getQueryForTLSConfiguration() {
		return "select * from tblmeaptlsconfig where eap_id = ?";
	}
	private String getQueryForAKAConfiguration(){
		return "SELECT * FROM tblmeapsimakaconfig WHERE eap_id = ? AND eapauthtype = " + AKA_METHOD_ID;
	}
	private String getQueryForSIMConfiguration(){
		return "SELECT * FROM tblmeapsimakaconfig WHERE eap_id = ? AND eapauthtype = " + SIM_METHOD_ID;
	}
	private String getQueryForAKAPrimeConfiguration(){
		return "SELECT * FROM tblmeapsimakaconfig WHERE eap_id = ? AND eapauthtype = " + AKA_PRIME_METHOD_ID;
	}
	private String getQueryForCertificateConfiguration() {
		return "select * from tblmvendorspecificcert where eap_tls_id = ?";
	}

	@PostRead
	@PostReload
	public void postReadProcessing() {
		List<EAPConfigurationDataImpl> eapConfList = getEapConfiList();
		if(eapConfList!=null){
			int noOfEapConf = eapConfList.size();
			EAPConfigurationDataImpl eapConfigurationImpl;

			ServerCertificateConfigurable serverCertificateConfigurable = getConfigurationContext().get(ServerCertificateConfigurable.class);
			for(int i=0;i<noOfEapConf;i++){
				eapConfigurationImpl = eapConfList.get(i);

				postReadProcessingForTlsVersionWindowAndCiphersuites(eapConfigurationImpl);
				
				postReadProcessingForEnabledEAPMethods(eapConfigurationImpl);
				
				postReadProcessingForCiphersuites(eapConfigurationImpl);
				
				postReadProcessingForCertificateTypes(eapConfigurationImpl);
				
				postReadProcessingForCertificates(eapConfigurationImpl,serverCertificateConfigurable);
				
				postReadProcessingVendorSpecificCertificates(eapConfigurationImpl,serverCertificateConfigurable);
				
				storeInDataStructures(eapConfigurationImpl);
			}
		}
	}

	private void postReadProcessingForTlsVersionWindowAndCiphersuites(EAPConfigurationDataImpl eapConfigurationImpl) {
		if(eapConfigurationImpl.getTLSConfiguration().getMinProtocolVersion().isGreater(eapConfigurationImpl.getTLSConfiguration().getMaxProtocolVersion())) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Minimum TLS version is greater than Maximum TLS version for EAP configuration: " + eapConfigurationImpl.getName() + ", using default version: " + ProtocolVersion.TLS1_0);
			}
			setDefaultTLSWindowConfiguration(eapConfigurationImpl);
		}
	}
	private void setDefaultTLSWindowConfiguration(EAPConfigurationDataImpl eapConfigurationImpl) {
		eapConfigurationImpl.getTLSConfiguration().setMinProtocolVersion(ProtocolVersion.TLS1_0);
		eapConfigurationImpl.getTLSConfiguration().setMaxProtocolVersion(ProtocolVersion.TLS1_0);
		eapConfigurationImpl.getTLSConfiguration().setCipherSuiteIDs(CipherSuites.getSupportedCiphersuiteIDs(ProtocolVersion.TLS1_0, ProtocolVersion.TLS1_0));
	}
	
	private void postReadProcessingForCertificates(EAPConfigurationDataImpl eapConfigurationImpl,ServerCertificateConfigurable serverCertificateConfigurable) {
		ServerCertificateProfile serverCertificateProfile = serverCertificateConfigurable.getServerCertificateProfileById(eapConfigurationImpl.getTLSConfiguration().getCertificateConfiguration().getServerCertificateId());
		
		eapConfigurationImpl.getTLSConfiguration().getCertificateConfiguration().setServerCertificateProfile(serverCertificateProfile);
	}
	
	
	
	private void storeInDataStructures(EAPConfigurationDataImpl eapConfigurationImpl) {
		eapConfIDMap.put(eapConfigurationImpl.getEapId(), eapConfigurationImpl);
	}
	
	private void postReadProcessingVendorSpecificCertificates(EAPConfigurationDataImpl eapConfigurationImpl, ServerCertificateConfigurable serverCertificateConfigurable) {
		VendorSpecificCertificateDetail vendorSpecificCertificateContainer = eapConfigurationImpl.getTLSConfiguration().getCertificateConfiguration().getVendorSpecificCertificateImpl();
		List<VendorSpecificCertificateImpl> vendorSpecificCertImplList = vendorSpecificCertificateContainer.getVendorSpecificCertificateList();
		List<VendorSpecificCertificate> vendorSpecificCertList = new ArrayList<EAPConfigurationData.VendorSpecificCertificate>();

		int numOFVendorSpecificCertificate = vendorSpecificCertImplList.size();
		for(int j=0;j<numOFVendorSpecificCertificate;j++){

			VendorSpecificCertificateImpl vendorSpecificCertificateImpl = vendorSpecificCertImplList.get(j);
			ServerCertificateProfile serverCertificateProfile = serverCertificateConfigurable.getServerCertificateProfileById(vendorSpecificCertificateImpl.getServerCertificateId());
			
			vendorSpecificCertificateImpl.setServerCertificateProfile(serverCertificateProfile);

			vendorSpecificCertList.add(vendorSpecificCertificateImpl);
		}
		eapConfigurationImpl.getTLSConfiguration().getCertificateConfiguration().setVendorSpecificCertList(vendorSpecificCertList);
	}
	
	private void postReadProcessingForCertificateTypes(EAPConfigurationDataImpl eapConfigurationImpl) {
		String certificateStr = eapConfigurationImpl.getTLSConfiguration().getCertificateConfiguration().getCertificateType();
		List<Integer> certificateList = getIntArrayFromString(certificateStr, "so skiping this certificate Type for EAP Configuration: " + eapConfigurationImpl.getName());
		if(certificateList != null && certificateList.size() >0){
			eapConfigurationImpl.getTLSConfiguration().getCertificateConfiguration().setCertificateTypes(certificateList);	
		}
	}
	
	private void postReadProcessingForCiphersuites(EAPConfigurationDataImpl eapConfigurationImpl) {
		String cipherSuitesStr = eapConfigurationImpl.getTLSConfiguration().getEnabledCipherSuites();
		List<Integer> cipherSuiteIDs = getIntArrayFromString(cipherSuitesStr, "so skiping this ciphersuite for EAP Configuration: " + eapConfigurationImpl.getName());
		if(cipherSuiteIDs != null && cipherSuiteIDs.size() >0){
			eapConfigurationImpl.getTLSConfiguration().setCipherSuiteIDs(cipherSuiteIDs);	
		}

	}
	
	private void postReadProcessingForEnabledEAPMethods(EAPConfigurationDataImpl eapConfigurationImpl) {
		String enableMethodStr = eapConfigurationImpl.getStrEnabledAuthMethods();
		if(enableMethodStr!=null && enableMethodStr.trim().length()>0){
			List<Integer> enableAuthMethods = getIntArrayFromString(enableMethodStr, "so skiping this enabled EAP method for EAP Configuration: " + eapConfigurationImpl.getName());
			eapConfigurationImpl.setEnableAuthMethods(enableAuthMethods);
		}	
	}
	
	@PostWrite
	public void postWriteProcessing() {

	}
	
	private List<Integer> getIntArrayFromString(String listStr, String nfeMessage) {
		List<Integer> convertedList =  new ArrayList<Integer>();
		if(listStr!=null && listStr.length()>0){
			String[] strArray = listStr.split(",");
			for(int i=0;i<strArray.length;i++){
				try{
					convertedList.add(Integer.parseInt(strArray[i].trim()));
				}catch (NumberFormatException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, strArray[i] + " is not an integer, " + nfeMessage);
					}
				}
			}
		}
		return convertedList;
	}
}
