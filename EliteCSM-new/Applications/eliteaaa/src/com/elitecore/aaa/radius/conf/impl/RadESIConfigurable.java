package com.elitecore.aaa.radius.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.radius.conf.RadESConfiguration;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadiusExternalSystemData;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.base.Optional;
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
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.udp.StatusCheckMethod;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;

@XmlType(propOrder = {})
@XmlRootElement(name = "rad-esi-config")
@ConfigurationProperties(moduleName ="RAD_ES_CNFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "rad-esi-config", schemaDirectories = {"system","schema"}, configDirectories = {"conf"})
public class RadESIConfigurable extends Configurable implements RadESConfiguration{

	private static final String MODULE = "RAD-ES-CNFIGURABLE";
	
	/*
	 * Data structures for storing the configurations
	 */
	private List<DefaultExternalSystemData> radESIConfigurationList;
	
	private Map<String, DefaultExternalSystemData> esiIDMap;
	private Map<String, DefaultExternalSystemData> esiNameMap;
	
	private Map<Integer, List<DefaultExternalSystemData>> esiTypeMap;
	
	/*
	 * End of data structures for storing the configurations
	 */
	

	public RadESIConfigurable(){
		radESIConfigurationList = new ArrayList<DefaultExternalSystemData>();
		esiIDMap = new HashMap<String, DefaultExternalSystemData>();
		esiNameMap = new HashMap<String, DefaultExternalSystemData>();
		esiTypeMap = new HashMap<Integer, List<DefaultExternalSystemData>>();
	}
	
	@XmlElement(name = "external-system")
	public List<DefaultExternalSystemData> getRadESIConfigurationImplList() {
		return radESIConfigurationList;
	}

	public void setRadESIConfigurationImplList(List<DefaultExternalSystemData> radESIConfigurationList) {
		this.radESIConfigurationList = radESIConfigurationList;
	}

	@DBRead
	public void readFromDB() throws Exception {

		List<DefaultExternalSystemData> radUdpExternalSystemList = new ArrayList<DefaultExternalSystemData>();
		
		Connection connection = null;
		PreparedStatement stmnt = null;
		ResultSet rs = null;
		try{
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			int communicationTimeout;
			int minLocalPort;
			int expiredRequestLimitCount ;
			String sharedSecret;
			int retryLimit;
			int statusCheckDuration;
			int esiType;
			String name;
			String supportedAttrsStr;
			String unsupportedAttrsStr;
			String realmName = "";
			StatusCheckMethod statusCheckMethod;
			String scannerPacket;

			stmnt = connection.prepareStatement("select * from tblmesiinstance");		
			if(stmnt == null){
				throw new SQLException("Problem reading Radius ESI configurations, Reason: prepared statement is null");
			}
			rs = stmnt.executeQuery();

			while(rs.next()){
				String instanceID = rs.getString("esiinstanceid");
				name = rs.getString("NAME");

				String strIPAddress = rs.getString("address");

				communicationTimeout = 1000;
				if(rs.getString("timeout")!=null && rs.getString("timeout").length()>0){
					communicationTimeout = Numbers.parseInt(rs.getString("timeout").trim(), communicationTimeout);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid configuration for Communication Timeout for ESI: " + name + ", Using default value: " + communicationTimeout);
				}
				minLocalPort = 10;  //this is the default value for the min local port
				if(rs.getString("minlocalport")!=null && rs.getString("minlocalport").length()>0){
					minLocalPort = Numbers.parseInt(rs.getString("minlocalport").trim(), minLocalPort);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid configuration for Minimum Local Ports for ESI: " + name + ", Using default value: " + minLocalPort);
				}

				if(minLocalPort <= 0){
					minLocalPort = 10; //if the value for min local port value is less than or equal to 0 default value 10
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid configuration for Minimum Local Ports for ESI: " + name + ", Using default value: " + minLocalPort);

				}else if(minLocalPort > 100){
					minLocalPort = 100;
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid configuration for Minimum Local Ports for ESI: " + name + ", Using default value: " + minLocalPort);
				}

				expiredRequestLimitCount = 50;
				if(rs.getString("expiredreqlimitcount")!=null && rs.getString("expiredreqlimitcount").length()>0){
					expiredRequestLimitCount = Numbers.parseInt(rs.getString("expiredreqlimitcount").trim(), expiredRequestLimitCount);
					if (expiredRequestLimitCount <= ESCommunicator.ALWAYS_ALIVE){
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "ESI: "  + name +"[" + strIPAddress + "] Will be treated as always alive as expired request limit count is configured: " + expiredRequestLimitCount + " (Always Alive)");
					}
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid configuration for Expired Request Limit Count for ESI: " + name + ", Using default value: " + expiredRequestLimitCount);
				}

				sharedSecret = "secret";
				if(rs.getString("sharedsecret")!=null && rs.getString("sharedsecret").length()>0){
					sharedSecret = rs.getString("sharedsecret");
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid configuration for Shared Secret for ESI: " + name + ", Using default value: " + sharedSecret);
				}
				retryLimit = 1;
				if(rs.getString("retrylimit")!=null && rs.getString("retrylimit").length()>0){
					retryLimit = Numbers.parseInt(rs.getString("retrylimit").trim(), retryLimit);
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid configuration for Retry for ESI: " + strIPAddress + ", Using default value: " + retryLimit);
				}
				statusCheckDuration  = 120;
				if(rs.getString("statuscheckduration")!=null && rs.getString("statuscheckduration").length()>0){
					statusCheckDuration = Numbers.parseInt(rs.getString("statuscheckduration").trim(), statusCheckDuration);
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid configuration for Status Check Duration for ESI: " + name + ", Using default value: " + statusCheckDuration);
				}

				esiType = 0;
				if(rs.getString("esitypeid")!=null && rs.getString("esitypeid").length()>0){
					esiType = Numbers.parseInt(rs.getString("esitypeid"), esiType);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid configuration for ESI Type for ESI: " + name + ", Using default value: " + esiType);
				}

				if(rs.getString("realmnames")!=null && rs.getString("realmnames").length()>0){
					realmName = rs.getString("realmnames");
				}
				supportedAttrsStr = rs.getString("SUPPORTEDATTRIBUTE");
				unsupportedAttrsStr = rs.getString("UNSUPPORTEDATTRIBUTE");
				
				statusCheckMethod = StatusCheckMethod.fromStatusCheckMethods(rs.getInt("STATUSCHECKMETHOD"));
				if(statusCheckMethod == null) {
					statusCheckMethod = StatusCheckMethod.ICMP_REQUEST;
					
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid configuration for Status Check Method for ESI: " + name + ", Using default value: " + statusCheckMethod.name);
				}
				scannerPacket = rs.getString("PACKETBYTES");
				DefaultExternalSystemData esi = new DefaultExternalSystemData(instanceID, strIPAddress, 
						expiredRequestLimitCount, communicationTimeout,minLocalPort, sharedSecret, 
						retryLimit, statusCheckDuration,name,realmName,esiType,
						supportedAttrsStr,unsupportedAttrsStr, 
						statusCheckMethod, scannerPacket);
				radUdpExternalSystemList.add(esi);
			}

			this.radESIConfigurationList = radUdpExternalSystemList;
		}finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(stmnt);
			DBUtility.closeQuietly(connection);
		}
	}

	@DBReload
	public void reloadESIConfiguration() throws Exception{
		int size = radESIConfigurationList.size();
		if(size == 0){
			return;
		}
		
		StringBuilder queryBuilder = new StringBuilder("select * from tblmesiinstance where ESIINSTANCEID IN (");
		
		for(int i = 0; i < size-1; i++){
			RadiusExternalSystemData radUDPExternalSystem = radESIConfigurationList.get(i);
			queryBuilder.append("'" + radUDPExternalSystem.getUUID() + "',");
		}
		
		queryBuilder.append("'" + radESIConfigurationList.get(size - 1).getUUID() + "')");
		String queryForReload = queryBuilder.toString();
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try{
			conn = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = conn.prepareStatement(queryForReload);
			rs = preparedStatement.executeQuery();
							
			while(rs.next()){
				
				String id = rs.getString("esiinstanceid");
				
				DefaultExternalSystemData radUDPExternalSystem = (DefaultExternalSystemData)esiIDMap.get(id);
				if(radUDPExternalSystem==null){
					continue;
				}
				
				int communicationTimeout = Numbers.parseInt(rs.getString("timeout").trim(), radUDPExternalSystem.getCommunicationTimeout());
				radUDPExternalSystem.setCommunicationTimeout(communicationTimeout);

				int expiredRequestLimitCount = Numbers.parseInt(rs.getString("expiredreqlimitcount").trim(), radUDPExternalSystem.getCommunicationTimeout());
				if (expiredRequestLimitCount <= ESCommunicator.ALWAYS_ALIVE){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "ESI: "  + radUDPExternalSystem.getName() +"[" + radUDPExternalSystem.getIPAddress() + "] Will be treated as always alive as expired request limit count is configured: " + expiredRequestLimitCount + " (Always Alive)");
				}

				int retryLimit = Numbers.parseInt(rs.getString("retrylimit").trim(), radUDPExternalSystem.getRetryLimit());
				radUDPExternalSystem.setRetryLimit(retryLimit);
				
				radUDPExternalSystem.setSupportedAttributesStr(rs.getString("SUPPORTEDATTRIBUTE"));
				radUDPExternalSystem.setUnsupportedAttributesStr(rs.getString("UNSUPPORTEDATTRIBUTE"));
			}
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(conn);
		}

	}
	
	@PostRead
	public void postReadProcessing() {
		if(radESIConfigurationList == null || radESIConfigurationList.size() == 0){
			return;
		}
		
		for(DefaultExternalSystemData radUDPExternalSystem : radESIConfigurationList){
			try{
				postReadProcessingForRealmNames(radUDPExternalSystem);

				postReadProcessingForIPAddressAndPort(radUDPExternalSystem);

				storeInDataStructures(radUDPExternalSystem);
			}catch (Exception e) {
				LogManager.getLogger().trace(e); 
			}
		}
	}
	
	
	private void storeInDataStructures(DefaultExternalSystemData radUDPExternalSystem) {
		esiIDMap.put(radUDPExternalSystem.getUUID(), radUDPExternalSystem);
		esiNameMap.put(radUDPExternalSystem.getName(), radUDPExternalSystem);
		
		int type = radUDPExternalSystem.getEsiType();
		List<DefaultExternalSystemData> list = esiTypeMap.get(type);
		if(list==null){
			list = new ArrayList<DefaultExternalSystemData>();
			esiTypeMap.put(type, list);
		}
		list.add(radUDPExternalSystem);
	}

	private void postReadProcessingForIPAddressAndPort(DefaultExternalSystemData radUDPExternalSystem) throws UnknownHostException, InvalidURLException {
		InetAddress IPAddress = null;
		int port = 0;
		String strIpAddress = radUDPExternalSystem.getStringIpAddress();
		URLData address;
		try {
			address = URLParser.parse(strIpAddress);
			try {
				if(address.getHost() != null){
					IPAddress = InetAddress.getByName(address.getHost());
				}
				port = address.getPort();
				radUDPExternalSystem.setIPAddress(IPAddress);
				radUDPExternalSystem.setPort(port);
			} catch (UnknownHostException e) {
				LogManager.getLogger().error(MODULE, "Error adding ESI : " + radUDPExternalSystem.getName() + ", Reason: Invalid IP Address configured " +strIpAddress);
				throw e;
			}
		} catch (InvalidURLException e1) {
			LogManager.getLogger().error(MODULE, "Error adding ESI : " + radUDPExternalSystem.getName() + ", Reason: Invalid Address is configured "+strIpAddress);
			throw e1;
		}
	}

	private void postReadProcessingForRealmNames(DefaultExternalSystemData radUDPExternalSystem) {
		List<String> realmNames = new ArrayList<String>();
		if(radUDPExternalSystem.getStrRealmName() !=null && radUDPExternalSystem.getStrRealmName().trim().length() >0){
			StringTokenizer stk = new StringTokenizer(radUDPExternalSystem.getStrRealmName(),",");
			while(stk.hasMoreTokens()){
				realmNames.add(stk.nextToken());
			}
			radUDPExternalSystem.setRealmNames(realmNames);
		}
	}

	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		//blank as no data that is reloaded requires post processing
	}
	
	@Override
	public Optional<DefaultExternalSystemData> getESData(String esId) {
		return Optional.of(esiIDMap.get(esId));
	}

	@Override
	public List<DefaultExternalSystemData> getESListByType(int typeID) {
		return esiTypeMap.get(typeID);
	}
	
	@Override
	public Map<String, DefaultExternalSystemData> getAllESI() {
		return esiIDMap;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println();
		out.println("-- Radius ESI Configuration -Start --");

		if (esiIDMap != null && !esiIDMap.isEmpty()) {
			for(Entry<String,DefaultExternalSystemData> entry:esiIDMap.entrySet()){
				out.println(entry.getValue());
			}
			out.println("    ------------------");
			out.println("    TOTAL COUNT: " + esiIDMap.size());
		} else {
			out.println("      No configuration found");
		}
		out.println(" -- Radius ESI Configuration - End -- ");
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public Optional<DefaultExternalSystemData> getESDataByName(String esName) {
		return Optional.of(esiNameMap.get(esName));
	}
}


