/*
 *	EAP Project
 *	Elitecore Technologies Ltd.
 *	904, Silicon Tower, Law Garden
 *	Ahmedabad, India - 380009
 *
 *	Created on Nov 7, 2008
 *	Created By Devang Adeshara
 */
package com.elitecore.coreeap.packet;

import java.util.HashMap;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.util.constants.EapPacketConstants;



/**
 *It will create the EAPPacket object of the specified type.
 *Any class will create an instance of EAPPacket by using this class.
 */
public class EAPPacketFactory {
 
	private static final String MODULE = "EAP PACKET FACTORY";
	static private EAPPacketFactory eapPacketFactory;
	 
	/**
	 * Contains PacketTypeCode-EAPPacket pair.
	 */
	private HashMap<Integer, EAPPacket> eapCodeInstanceMap;
	
	private EAPPacketFactory(){
		loadEAPPacketInstances();
	}
	
	private void loadEAPPacketInstances(){
		eapCodeInstanceMap = new HashMap<Integer, EAPPacket>(6);
		
		//FOR REQUEST
		EAPPacket eapPacket = new EAPPacket(EapPacketConstants.REQUEST.packetId);
		eapCodeInstanceMap.put(new Integer(EapPacketConstants.REQUEST.packetId), eapPacket);

		//FOR RESPONSE
		eapPacket = new EAPPacket(EapPacketConstants.RESPONSE.packetId);
		eapCodeInstanceMap.put(new Integer(EapPacketConstants.RESPONSE.packetId), eapPacket);
		
		//FOR SUCCESS
		eapPacket = new EAPPacket(EapPacketConstants.SUCCESS.packetId);
		eapCodeInstanceMap.put(new Integer(EapPacketConstants.SUCCESS.packetId), eapPacket);
		
		//FOR FAILURE
		eapPacket = new EAPPacket(EapPacketConstants.FAILURE.packetId);
		eapCodeInstanceMap.put(new Integer(EapPacketConstants.FAILURE.packetId), eapPacket);
	}	
	
	public static EAPPacketFactory getInstance() {
		if (eapPacketFactory==null){
			eapPacketFactory = new EAPPacketFactory();
		}
		return eapPacketFactory;
	}
	
	public EAPPacket createEAPPacket(int eapPacketCode) {
		EAPPacket packet = eapCodeInstanceMap.get(new Integer(eapPacketCode));
		EAPPacket newPacket = null;
		if (packet!=null){
			try {
				newPacket = (EAPPacket)packet.clone();
			} catch (CloneNotSupportedException e) {				
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Unable to create EAPPacket instance for code = "+eapPacketCode +" ,reason :"+e.getMessage());
			}
		}
		return newPacket;
	}
	
	public EAPPacket createEAPPacket(int eapPacketCode, int eapTypeCode) throws EAPException {
		EAPPacket newPacket = createEAPPacket(eapPacketCode);
		if (newPacket!=null){
			try {
				newPacket.setEAPType(EAPTypeDictionary.getInstance().createEAPType(eapTypeCode));
			} catch (InvalidEAPPacketException e) {			
//				Logger.logTrace(MODULE,e.getMessage());
//				Logger.logTrace(MODULE,e);
				throw new EAPException("Error During Creating EAPPacket. Reason: " + e.getMessage(), e);
			}
		}
		return newPacket;
	}
	
	public EAPPacket createEAPExpandedPacket(int eapPacketCode, int vendorID, int vendorType) throws EAPException {
		EAPPacket newPacket = createEAPPacket(eapPacketCode);
		if (newPacket!=null){
			try {
				newPacket.setEAPType(EAPTypeDictionary.getInstance().createEAPExpandedType(
						vendorID, vendorType));
			} catch (InvalidEAPPacketException e) {		
//				Logger.logTrace(MODULE,e.getMessage());
//				Logger.logTrace(MODULE,e);
				throw new EAPException("Error During Creating EAPPacket. Reason: " + e.getMessage(), e);
			}
		}
		return newPacket;
	}	
}
 
