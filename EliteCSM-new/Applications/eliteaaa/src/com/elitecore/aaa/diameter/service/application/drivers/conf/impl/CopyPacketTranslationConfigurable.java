package com.elitecore.aaa.diameter.service.application.drivers.conf.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
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
import com.elitecore.diameterapi.core.translator.policy.data.impl.DummyResponseDetail;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketMappingDataImpl;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketTranslationConfigDataImpl;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketTranslatorPolicyDataImpl;
import com.elitecore.diameterapi.diameter.translator.operations.PacketOperations;

@XmlType(propOrder = {})
@XmlRootElement(name = "copy-packet-mapping-configuration-data") 
@ConfigurationProperties(moduleName ="CPY-PKT-MAPPING-CONFIGURABLE",readWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey ="")
@XMLProperties(name = "copy-packet-mapping-conf", schemaDirectories = {"system","schema"}, configDirectories = {"conf","db"})
public class CopyPacketTranslationConfigurable extends Configurable{

	private static final String MODULE = "CPY-PKT-MAPPING-CONFIGURABLE";
	private static final String REQUEST_PARAMETERS="TMI0001";
	private static final String RESPONSE_PARAMETERS="TMI0002";

	private List<CopyPacketTranslatorPolicyDataImpl> copyPacketMappingList;
	
	public CopyPacketTranslationConfigurable() {
		copyPacketMappingList = new ArrayList<CopyPacketTranslatorPolicyDataImpl>();
	}
	
	@XmlElement(name = "copy-packet-mapping-configurataion")
	public List<CopyPacketTranslatorPolicyDataImpl> getCopyPacketMappingList() {
		return copyPacketMappingList;
	}

	@DBRead
	@DBReload
	public void readCopyPacketMappingConfiguration() throws Exception {
		LogManager.getLogger().info(MODULE, "Loading Copy Packet Mapping Translation Policies from database");
		
		List<CopyPacketTranslatorPolicyDataImpl> tempCopyPacketMappingList = new ArrayList<CopyPacketTranslatorPolicyDataImpl>();
		CopyPacketTranslatorPolicyDataImpl copyPacketTranslatorPolicyData;
		
		Connection connection = null;
		PreparedStatement psCopyPacketPolicyData = null;
		ResultSet rsCopyPacketPolicyData = null;
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			psCopyPacketPolicyData = connection.prepareStatement(getQueryForCopyPacketPolicyData());
			rsCopyPacketPolicyData = psCopyPacketPolicyData.executeQuery();
			
			while (rsCopyPacketPolicyData.next()) {
				
				String policyId = rsCopyPacketPolicyData.getString("COPYPACKETMAPCONFID");
				
				copyPacketTranslatorPolicyData = new CopyPacketTranslatorPolicyDataImpl();
				copyPacketTranslatorPolicyData.setCopyPacketMapConfId(policyId);

				String name = rsCopyPacketPolicyData.getString("NAME");
				if(Strings.isNullOrBlank(name)){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Skipping Translation Configuration, Reason: Name for Translation Configuration not found");
					}
					continue;
				}
				copyPacketTranslatorPolicyData.setName(name.trim());
				copyPacketTranslatorPolicyData.setToTranslatorId(rsCopyPacketPolicyData.getString("TOTYPE"));
				copyPacketTranslatorPolicyData.setFromTranslatorId(rsCopyPacketPolicyData.getString("FROMTYPE"));
				String script = rsCopyPacketPolicyData.getString("SCRIPT");
				if(Strings.isNullOrBlank(script)){
					script = null;
				} else {
					script = script.trim();
				}
				copyPacketTranslatorPolicyData.setScript(script);
				
				/* read copy packet mappings */
				readCopyPacketMappings(copyPacketTranslatorPolicyData, connection);
				
				/* read dummy response mapping detail*/
				readDummyResponseMappingDetails(copyPacketTranslatorPolicyData, connection);

				tempCopyPacketMappingList.add(copyPacketTranslatorPolicyData);
			}
			this.copyPacketMappingList = tempCopyPacketMappingList;
			
		} finally {
			DBUtility.closeQuietly(psCopyPacketPolicyData);
			DBUtility.closeQuietly(rsCopyPacketPolicyData);
			DBUtility.closeQuietly(connection);
		}
	}

	private void readCopyPacketMappings(CopyPacketTranslatorPolicyDataImpl copyPacketTranslatorPolicyData, Connection connection) 
			throws SQLException {
		
		List<CopyPacketTranslationConfigDataImpl> translationConfigDataList = new ArrayList<CopyPacketTranslationConfigDataImpl>();
		PreparedStatement psCopyPacketConfigurationData = null;
		ResultSet rsCopyPacketConfigurationData = null;
		try {
			psCopyPacketConfigurationData = connection.prepareStatement(getQueryForCopyPacketConfigurationData());
			psCopyPacketConfigurationData.setString(1, copyPacketTranslatorPolicyData.getCopyPacketMapConfId());
			
			rsCopyPacketConfigurationData = psCopyPacketConfigurationData.executeQuery();
			
			while (rsCopyPacketConfigurationData.next()) {
				
				CopyPacketTranslationConfigDataImpl copyPacketTranslationConfigDataImpl = new CopyPacketTranslationConfigDataImpl();
				
				String mappingName = rsCopyPacketConfigurationData.getString("MAPPINGNAME");
				if(Strings.isNullOrBlank(mappingName)){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Skipping Translation Mapping Policy in Translation Configuration: " + copyPacketTranslatorPolicyData.getName() + 
								", Reason: Name for Translation Mapping Policy not found");
					}
					continue;
				}
				String configId = rsCopyPacketConfigurationData.getString("INSTANCEID");
				copyPacketTranslationConfigDataImpl.setMappingName(mappingName.trim());
				copyPacketTranslationConfigDataImpl.setInExpression(rsCopyPacketConfigurationData.getString("INEXPRESSION"));
				boolean dummyResponse = false;
				dummyResponse = Strings.toBoolean(rsCopyPacketConfigurationData.getString("DUMMYRESPONSE"));
				copyPacketTranslationConfigDataImpl.setDummyMappingEnabled(dummyResponse);

				readMappingDetails(connection, translationConfigDataList, copyPacketTranslationConfigDataImpl,configId);
			}
			copyPacketTranslatorPolicyData.setTranslationConfigDataList(translationConfigDataList);
		} finally {
			DBUtility.closeQuietly(rsCopyPacketConfigurationData);
			DBUtility.closeQuietly(psCopyPacketConfigurationData);
		}
	}

	private void readMappingDetails(Connection connection,List<CopyPacketTranslationConfigDataImpl> translationConfigDataList,
			CopyPacketTranslationConfigDataImpl copyPacketTranslationConfigDataImpl, String configId) throws SQLException {
		
		List<CopyPacketMappingDataImpl> requestMappingDataList = new ArrayList<CopyPacketMappingDataImpl>();
		List<CopyPacketMappingDataImpl> responseMappingDataList = new ArrayList<CopyPacketMappingDataImpl>();

		PreparedStatement psCopyPacketMappingData = null;
		ResultSet rsCopyPacketMappingData = null;
		try {
			
			psCopyPacketMappingData = connection.prepareStatement(getQueryForMappingData());
			psCopyPacketMappingData.setString(1, configId);
			
			rsCopyPacketMappingData = psCopyPacketMappingData.executeQuery();
			
			while(rsCopyPacketMappingData.next()) {

				String mappingTypeId = rsCopyPacketMappingData.getString("MAPPINGTYPEID");
				if(Strings.isNullOrBlank(mappingTypeId)) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Can not determine Mapping Type, Reason: Mapping Type Id not found");
					}
					continue;
				}
				CopyPacketMappingDataImpl copyPacketMappingDataImpl = new CopyPacketMappingDataImpl();
				
				String operation = rsCopyPacketMappingData.getString("OPERATION");
				if(Strings.isNullOrBlank(operation)) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Skipping Mapping, Reason: Packet Operation Type not found");
					}
					continue;
				}
				PacketOperations packetOperation = PacketOperations.fromOperations(operation.trim());
				if(packetOperation == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Skipping Mapping, Reason: Can not determine Packet Operation Type: "+ operation);
					}
					continue;
				}
				copyPacketMappingDataImpl.setOperation(packetOperation);
				
				copyPacketMappingDataImpl.setCheckExpression(rsCopyPacketMappingData.getString("CHECKEXPRESSION"));
				copyPacketMappingDataImpl.setDefaultValue(rsCopyPacketMappingData.getString("DEFAULTVALUE"));
				copyPacketMappingDataImpl.setValueMapping(rsCopyPacketMappingData.getString("VALUEMAPPING"));
				
				copyPacketMappingDataImpl.setDestinationExpression(rsCopyPacketMappingData.getString("DESTINATIONEXPRESSION"));
				copyPacketMappingDataImpl.setSourceExpression(rsCopyPacketMappingData.getString("SOURCEEXPRESSION"));
				
				if(REQUEST_PARAMETERS.equalsIgnoreCase(mappingTypeId)) {
					requestMappingDataList.add(copyPacketMappingDataImpl);
				} else if(RESPONSE_PARAMETERS.equalsIgnoreCase(mappingTypeId)){
					responseMappingDataList.add(copyPacketMappingDataImpl);
				} else {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Can not determine Mapping Type, Reason: Invalid Mapping Type Id: " + mappingTypeId);
					}
					continue;
				}
			}
			copyPacketTranslationConfigDataImpl.setRequestMappingDataList(requestMappingDataList);
			copyPacketTranslationConfigDataImpl.setResponseMappingDataList(responseMappingDataList);
			
			translationConfigDataList.add(copyPacketTranslationConfigDataImpl);
		} finally {
			DBUtility.closeQuietly(rsCopyPacketMappingData);
			DBUtility.closeQuietly(psCopyPacketMappingData);
		}
	}

	private void readDummyResponseMappingDetails(CopyPacketTranslatorPolicyDataImpl copyPacketTranslatorPolicyData,
			Connection connection) throws SQLException {
		
		PreparedStatement psDummyMappingData = null;
		ResultSet rsDummyMappingData = null;
		List<DummyResponseDetail> dummyResponseList = new ArrayList<DummyResponseDetail>();
		
		try {
			psDummyMappingData = connection.prepareStatement(getQueryForDummyMapping());
			psDummyMappingData.setString(1, copyPacketTranslatorPolicyData.getCopyPacketMapConfId());
			rsDummyMappingData = psDummyMappingData.executeQuery();
			
			while (rsDummyMappingData.next()) {
				DummyResponseDetail dummyResponseDetail = new DummyResponseDetail();
				dummyResponseDetail.setOutfield(rsDummyMappingData.getString("OUTFIELD"));
				dummyResponseDetail.setValue(rsDummyMappingData.getString("PARAMVALUE"));
				dummyResponseList.add(dummyResponseDetail);
			}
			copyPacketTranslatorPolicyData.setDummyResponseList(dummyResponseList);
		} finally {
			DBUtility.closeQuietly(psDummyMappingData);
			DBUtility.closeQuietly(rsDummyMappingData);
		}
	}
	
	private String getQueryForDummyMapping() {
		return "select * from TBLMCOPYPACKETDUMMYRESPPARAM where COPYPACKETMAPCONFID = ? order by ORDERNUMBER";
	}

	private String getQueryForMappingData() {
		return "select * from TBLMCOPYPACKETINSTANCEDETAIL where INSTANCEID = ?  order by ORDERNO";
	}

	private String getQueryForCopyPacketConfigurationData() {
		return "select * from TBLMCOPYPACKETINSTANCEDATA where COPYPACKETMAPCONFID = ? order by ORDERNUMBER";
	}

	private String getQueryForCopyPacketPolicyData() {
		return "select * from TBLMCOPYPACKETTRANSMAPCONF";
	}

	@PostRead
	@PostReload
	public void postReadProcessing(){
		
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
}
