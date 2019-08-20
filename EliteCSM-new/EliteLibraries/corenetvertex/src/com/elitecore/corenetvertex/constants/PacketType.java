package com.elitecore.corenetvertex.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @author milan
 *Enum class to maintain constant on basis of command code
 */
public enum PacketType {
	//TODO both parameter is required to be made for the requst that are used for request and response -- ishani.dave
	//Diameter packet
	
	CREDIT_CONTROL_REQUEST("CCR" , ProtocolType.DIAMETER , ConversionType.GATEWAY_TO_PCC, true, false),
	CREDIT_CONTROL_RESPONSE("CCA" , ProtocolType.DIAMETER , ConversionType.PCC_TO_GATEWAY, false, true),
	
	RE_AUTH_REQUEST("RAR" , ProtocolType.DIAMETER , ConversionType.PCC_TO_GATEWAY, false, true),
	RE_AUTH_RESPONSE("RAA" , ProtocolType.DIAMETER,ConversionType.GATEWAY_TO_PCC, true, false),
	
	ABORT_SESSION_REQUEST("ASR" , ProtocolType.DIAMETER , ConversionType.PCC_TO_GATEWAY, false, true),
	ABORT_SESSION_RESPONSE("ASA" , ProtocolType.DIAMETER,ConversionType.GATEWAY_TO_PCC, true, false),
	
	
	SPENDING_LIMIT_REQUEST("SLR" , ProtocolType.DIAMETER , ConversionType.PCC_TO_GATEWAY, false, true),
	SPENDING_LIMIT_RESPONSE("SLA" , ProtocolType.DIAMETER,ConversionType.GATEWAY_TO_PCC, true, false),
	
	SPENDING_STATUS_NOTIFICATION_REQUEST("SNR" , ProtocolType.DIAMETER , ConversionType.GATEWAY_TO_PCC, true, false),
	SPENDING_STATUS_NOTIFICATION_RESPONSE("SNA" , ProtocolType.DIAMETER,ConversionType.PCC_TO_GATEWAY, false, true),
	
	AUTHENTICATE_AUTHORIZE_REQUEST("AAR" , ProtocolType.DIAMETER , ConversionType.GATEWAY_TO_PCC, true, false),
	AUTHENTICATE_AUTHORIZE_RESPONSE("AAA" , ProtocolType.DIAMETER , ConversionType.PCC_TO_GATEWAY, false, true),
	
	SESSION_TERMINATION_REQUEST("STR" , ProtocolType.DIAMETER , ConversionType.GATEWAY_TO_PCC, true, false),
	SESSION_TERMINATION_RESPONSE("STA" , ProtocolType.DIAMETER , ConversionType.PCC_TO_GATEWAY, false, true),
	
	//Radius packet

	ACCOUNTING_REQUEST("ACCOUNTING_REQUEST",ProtocolType.RADIUS , ConversionType.GATEWAY_TO_PCC, true, false),
	CHANGE_OF_AUTHORIZATION("CHANGE_OF_AUTHORIZATION",ProtocolType.RADIUS , ConversionType.PCC_TO_GATEWAY, false, true),
	DISCONNECT_REQUEST("DISCONNECT_REQUEST",ProtocolType.RADIUS , ConversionType.PCC_TO_GATEWAY, false, true),
		
	ACCESS_REQUEST("ACCESS_REQUEST",ProtocolType.RADIUS , ConversionType.GATEWAY_TO_PCC, true, false),
	ACCESS_ACCEPT("ACCESS_ACCEPT",ProtocolType.RADIUS , ConversionType.PCC_TO_GATEWAY, false, true);


	private static final PacketType[] types = values();
	private static final Map<String , PacketType> objectMap;
	private static final Map<Object,List<PacketType>> packetTypeMap;

	private final String type;
	private final ProtocolType protocolType;
	private final ConversionType conversionType;
	private boolean gatewayToPcc;
	private boolean pccToGateway;

	
	
	private PacketType(String packetName,
					   ProtocolType protocolType,
					   ConversionType conversionType,
					   boolean gatewayToPcc,
					   boolean pccToGateway
						){
		this.type =packetName;
		this.protocolType=protocolType;
		this.conversionType=conversionType;
		this.gatewayToPcc = gatewayToPcc;
		this.pccToGateway = pccToGateway;
	}
	


	
	
	static {
		objectMap = new HashMap<String ,PacketType>();
		packetTypeMap=new HashMap<Object, List<PacketType>>();
		
		for ( PacketType packetType : types){
			/**
			 * No need to provide Mapping in GUI for SL,SN,ST for sy Interface Until we IOT/POC for syInterface 
			 * 
			 * Date : 2nd Aug 2013
			 * 
			 * Harsh Patel
			 */
			if(packetType == SPENDING_LIMIT_REQUEST || packetType == SPENDING_LIMIT_RESPONSE 
					|| packetType == SPENDING_STATUS_NOTIFICATION_REQUEST || packetType == SPENDING_STATUS_NOTIFICATION_RESPONSE){
				continue;
			}
			objectMap.put(packetType.type, packetType);
			addPacketType(packetType);
		}
	}
	
	private static void addPacketType(PacketType packetType){
		List<PacketType> packetTypeList= packetTypeMap.get(packetType.protocolType);
		if(packetTypeList==null){
			packetTypeList = new ArrayList<PacketType>();
			packetTypeMap.put(packetType.protocolType, packetTypeList);
		}
		packetTypeList.add(packetType);
		
		packetTypeList= packetTypeMap.get(packetType.conversionType);
		if(packetTypeList==null){
			packetTypeList = new ArrayList<PacketType>();
			packetTypeMap.put(packetType.conversionType, packetTypeList);
		}
		packetTypeList.add(packetType);
		
		String protoConversion = packetType.protocolType.getProtocolType()+packetType.conversionType.getConversionType();
		packetTypeList= packetTypeMap.get(protoConversion);
		if(packetTypeList==null){
			packetTypeList = new ArrayList<PacketType>();
			packetTypeMap.put(protoConversion, packetTypeList);
		}
		packetTypeList.add(packetType);
	}

	public static PacketType fromPacketType(String packetType){
		return objectMap.get(packetType);
	}

	public String getPacketType(){
		return type;
	}
	
	public ConversionType getConversionType(){
		return conversionType;
	}
	
	public ProtocolType getProtocolType(){
		return protocolType;
	}
	
	public static List<PacketType> getPacketTypeList(ProtocolType protocolType){
		return packetTypeMap.get(protocolType);
	}
	
	public static List<PacketType> getPacketTypeList(ConversionType conversionType){
		return packetTypeMap.get(conversionType);
	}
	
	public static List<PacketType> getPacketTypeList(ProtocolType protocolType , ConversionType conversionType){
		return packetTypeMap.get(protocolType.getProtocolType()+conversionType.getConversionType());
	}

	public boolean isGatewayToPcc() {
		return gatewayToPcc;
	}

	public boolean isPccToGateway() {
		return pccToGateway;
	}
	
	
}
