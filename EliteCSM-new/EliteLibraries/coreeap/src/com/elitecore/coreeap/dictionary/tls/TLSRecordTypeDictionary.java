package com.elitecore.coreeap.dictionary.tls;

import java.util.HashMap;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.packet.types.tls.record.types.AlertRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.ApplicationDataRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.BaseTLSRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.ChangeCipherSpecRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.HandshakeMessageRecordType;
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;

public class TLSRecordTypeDictionary {
	private static final String MODULE = "TLSRECORDTYPE DICTIONARY";
	static private TLSRecordTypeDictionary tlsRecordDictionary;
	/**
	 * Contains TLSRecord Type Code - TLSRecord pair.
	 */
	private HashMap<Integer, BaseTLSRecordType> TLSRecordTypeInstanceMap;	 
			 
	private TLSRecordTypeDictionary() {
		loadTLSRecordInstances();		
	}
	 
	private void loadTLSRecordInstances() {
		TLSRecordTypeInstanceMap = new HashMap<Integer, BaseTLSRecordType>();
		
		BaseTLSRecordType tlsRecord = new ChangeCipherSpecRecordType();		
		TLSRecordTypeInstanceMap.put(new Integer(TLSRecordConstants.ChangeCipherSpec.value), tlsRecord);
		
		tlsRecord = new AlertRecordType();		
		TLSRecordTypeInstanceMap.put(new Integer(TLSRecordConstants.Alert.value), tlsRecord);
		
		tlsRecord = new HandshakeMessageRecordType();
		TLSRecordTypeInstanceMap.put(new Integer(TLSRecordConstants.Handshake.value), tlsRecord);
		
		tlsRecord = new ApplicationDataRecordType();		
		TLSRecordTypeInstanceMap.put(new Integer(TLSRecordConstants.ApplicationData.value), tlsRecord);
		
	}	 
	
	public static TLSRecordTypeDictionary getInstance() {
		if(tlsRecordDictionary == null){
			tlsRecordDictionary = new TLSRecordTypeDictionary();
		}
		return tlsRecordDictionary;
	}
	
	public BaseTLSRecordType createTLSRecord(int iTLSRecordType, ProtocolVersion protocolVersion) {
		BaseTLSRecordType tlsRecordType = null;
		
		try {
			tlsRecordType = (BaseTLSRecordType) TLSRecordTypeInstanceMap.get(new Integer(iTLSRecordType)).clone();
			tlsRecordType.setProtocolVersion(protocolVersion);
		} catch (CloneNotSupportedException e) {			
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error during creating TLS record, reason : "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		return tlsRecordType;
	}	
}

