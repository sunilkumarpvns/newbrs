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


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.packet.types.EAPExpandedType;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;

/**
 * Represents an EAP packet as per RFC 3748.
 */
public class EAPPacket implements Cloneable{

	private static final String MODULE = "EAP PACKET";
	private int code;
	private int identifier; 
	private int length;
	private List<EAPType> eapType = new ArrayList<EAPType>();

	public EAPPacket(int code){
		this.code = code;
		this.identifier = 0;
		this.length = 0;
	}
	
    /**
     * Constructs EAP packet by parsing the byte array.
     * @param packetBytes Raw content of EAP packet to be parsed.
     * @throws InvalidEAPPacketException if the packet data is not as per the RFC.
     */
	public EAPPacket(byte[] packetBytes) throws InvalidEAPPacketException {
		parsePacket(packetBytes);
	}
	   
	private void parsePacket(byte[] packetBytes) throws InvalidEAPPacketException {
        
		/*
		 * 	The EAP Packet Code MUST be one of the following 
		 *	1 (EAP Request) or 
		 *	2 (EAP Response) or 
		 *	3 (EAP Success)or 
		 *	4 (EAP Failure).
		 *
		 */  
		
		if(packetBytes != null){
			if(packetBytes.length >= 4){
				
				this.code = (int)(packetBytes[0] & 0xFF);
				this.identifier = (int)(packetBytes[1] & 0xFF);
				this.length = packetBytes[2] & 0xFF;
				this.length = this.length << 8;
				this.length = this.length | (int)(packetBytes[3] & 0xFF);
				
				if(this.length <= packetBytes.length){
					if(this.code <= 4 && this.code >= 1){
						if(this.length > 4){
							if(this.code == 3 || this.code == 4){
						    	throw new InvalidEAPPacketException("Illegal Code");
						    }
							int type = packetBytes[4] & 0xFF;
							if(type == 254){
							
								/* Packet Format having an Expanded EAP Type is as follows
								 * 
								 *	0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
								 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
								 *	| Code 	        | Identifier 	|   Length 					    |
								 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
								 *	| Type 	    	| 	Vendor-Id 				    				|
								 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
								 *	| 			Vendor-Type 									    |
								 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
								 *	| 		Vendor data...
								 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
								 *
								 * If the EAP Type is Expanded then minimum length of the packet is supposed to be 12 
								 * (Code(1), Identifier(1), Length(2), Type(1), Vendor-Id(3), Vendor-Type(4), Vendor-Data(0 or more)).
								 */

								if (packetBytes.length < 12 ){
									throw new InvalidEAPPacketException("Invalid EAP Packet. Length...");
								}else{
									// 3 bytes Vendor-Id
									int vendorId = packetBytes[5];
									vendorId = vendorId << 8;
									vendorId = vendorId | ( packetBytes[6] & 0xFF);
									vendorId = vendorId << 8;
									vendorId = vendorId | ( packetBytes[7] & 0xFF);
									// 4 bytes Vendor-Type					
									long vendorType = packetBytes[4 + 4];
									vendorType = vendorType << 8;
									vendorType = vendorType | ( packetBytes[4 + 5] & 0xFF);
									vendorType = vendorType << 8;
									vendorType = vendorType | ( packetBytes[4 + 6] & 0xFF);
									vendorType = vendorType << 8;
									vendorType = vendorType | ( packetBytes[4 + 7] & 0xFF);
									
									EAPExpandedType expandedType = EAPTypeDictionary.getInstance().createEAPExpandedType(
											vendorId, vendorType);
									if (expandedType==null){
										throw new InvalidEAPPacketException(
												"Unsupported Packet-Type: vendorID="+vendorId+
												", vendorType="+vendorType);
									}
									
									if (packetBytes.length > 12){
										byte[] dataBytes = new byte[packetBytes.length - 12];
										System.arraycopy(packetBytes, 12, dataBytes, 0, dataBytes.length);
										try {
											expandedType.readDataBytes(dataBytes);
										} catch (InvalidEAPTypeException e) {
											if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
												LogManager.getLogger().trace(MODULE, "Problem in parsing EAP Bytes,reason :"+e.getMessage());
											LogManager.getLogger().trace(MODULE, e);
										}
									}
									this.eapType.add(expandedType);
								}
							
							}else{
								EAPType eapType = EAPTypeDictionary.getInstance().createEAPType(type);
								if(this.length > 5){
									byte[] dataBytes = new byte[this.length - 5];
									System.arraycopy(packetBytes, 5, dataBytes, 0, this.length - 5);
									eapType.setData(dataBytes);
									this.eapType.add(eapType);
									resetLength();
								}else{
									throw new InvalidEAPPacketException("Invalid length. Value of the length field is not valid for EAP Packet code "+this.code);
								}
							}
							
						}else{
							if(this.code == 1 || this.code == 2){
						    	throw new InvalidEAPPacketException("Illegal Code");
						    }
						}
					}else{
						throw new InvalidEAPPacketException("Invalid EAP Packet Code : " + this.code);
					}
				}else{
					throw new InvalidEAPPacketException("Invalid EAP Packet. Value of length field more than actual length of the packet.");
				}
				
			
				/*if((int)(packetBytes[0] & 0xFF) < 1 || (int)(packetBytes[0] & 0xFF) > 4){
					throw new InvalidEAPPacketException("Invalid EAP Packet Code : " + (int)(packetBytes[0] & 0xFF));
				}else{
					
					this.code = (int)(packetBytes[0] & 0xFF);
					
					this.identifier = (int)(packetBytes[1] & 0xFF);
					
					this.length = packetBytes[2] & 0xFF;
					this.length = this.length << 8;
					this.length = this.length | (int)(packetBytes[3] & 0xFF);
					
					// If the packet contains EAP Type. Eg. EAP Request and Response Packet always contains EAP Type.
					// But EAP Success and Failure Packets does not contain EAP Type. They are exactly of length 4.
					if (this.length > 4){
					    if(this.code == 3 || this.code == 4){
					    	throw new InvalidEAPPacketException("Illegal Code");
					    }
						
						// Take the rest of the bytes as EAP Type bytes and create an EAP Type using those bytes.
			             The fifth element packetBytes[4] represents the Type of Request or Response. 
						if ((packetBytes [4] & 0xFF) == 254){
							
							 Packet Format having an Expanded EAP Type is as follows
							 * 
							 *	0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
							 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
							 *	| Code 	        | Identifier 	|   Length 					    |
							 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
							 *	| Type 	    	| 	Vendor-Id 				    				|
							 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
							 *	| 			Vendor-Type 									    |
							 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
							 *	| 		Vendor data...
							 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
							 *
							 * If the EAP Type is Expanded then minimum length of the packet is supposed to be 12 
							 * (Code(1), Identifier(1), Length(2), Type(1), Vendor-Id(3), Vendor-Type(4), Vendor-Data(0 or more)).
							 

							if (packetBytes.length < 12 ){
								throw new InvalidEAPPacketException(
										"Invalid Packet Type Length...");
							}else{
								// 3 bytes Vendor-Id
								int vendorId = packetBytes[5];
								vendorId = vendorId << 8;
								vendorId = vendorId | ( packetBytes[6] & 0xFF);
								vendorId = vendorId << 8;
								vendorId = vendorId | ( packetBytes[7] & 0xFF);
								// 4 bytes Vendor-Type					
								long vendorType = packetBytes[4 + 4];
								vendorType = vendorType << 8;
								vendorType = vendorType | ( packetBytes[4 + 5] & 0xFF);
								vendorType = vendorType << 8;
								vendorType = vendorType | ( packetBytes[4 + 6] & 0xFF);
								vendorType = vendorType << 8;
								vendorType = vendorType | ( packetBytes[4 + 7] & 0xFF);
								
								EAPExpandedType expandedType = EAPTypeFactory.getInstance().createEAPExpandedType(
										vendorId, vendorType);
								if (expandedType==null){
									throw new InvalidEAPPacketException(
											"Unsupported Packet-Type: vendorID="+vendorId+
											", vendorType="+vendorType);
								}
								
								if (packetBytes.length > 12){
									byte[] dataBytes = new byte[packetBytes.length - 12];
									System.arraycopy(packetBytes, 12, dataBytes, 0, dataBytes.length);
									try {
										expandedType.readDataBytes(dataBytes);
									} catch (InvalidEAPPacketException e) {
										e.printStackTrace();
									}
								}
								this.eapType = expandedType;
							}
						}else{
							
						   * 
						   	 *
						   	 * 		EAP Packet Format is as follows
							 * 
							 *		0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
							 *		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
							 *		| Code 	        | Identifier 	|   Length 					    |
							 *		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
							 *		| Type 	    	| 	Type-Data...
							 *		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
							 *
							 * 		The minimum length of the packet is supposed to be 4 
							 * 		(Code(1), Identifier(1), Length(2), Type(1), Type-Data(one or more bytes).
							 * 
							 * 
							 * 
							
			                // packetBytes[4] represents type of Request or Response
							EAPType eapType = EAPTypeFactory.getInstance().createEAPType((int)(packetBytes[4] & 0xFF));
							if (packetBytes.length > 5){
								if(this.length > packetBytes.length){
									throw new InvalidEAPPacketException("Value of length field is more than the actual length");
								}else{
									byte[] dataBytes = new byte[this.length - 5];
									System.arraycopy(packetBytes, 5, dataBytes, 0, this.length - 5);
									eapType.setData(dataBytes);
								}
							}else{
								throw new InvalidEAPPacketException("Invalid number of bytes.");
							}
							this.eapType = eapType;
							resetLength();
						}
					}else if(this.length == 4){
						if(this.code != 3 && this.code != 4){
							throw new InvalidEAPPacketException("Invalid code : Not a SUCCESS or FAILUTE Code");
						}
					}
				}
				*/
			}else{
				throw new InvalidEAPPacketException("Bytes less than minimum required");
			}
		}else{
			throw new InvalidEAPPacketException("Bytes null");
		}
		
		
	}
	
	public int getCode() {
		return this.code;
	}
	
	public void setCode(int code) throws InvalidEAPPacketException {
		if(code >= 1 && code <= 4){
			this.code = code;
		}else{
			throw new InvalidEAPPacketException("Invalid code : "+code);
		}
	}
	
	public int getIdentifier() {
		return this.identifier;
	}
	
	public void setIdentifier(int identifier) throws InvalidEAPPacketException {
		if(identifier >= 0 && identifier <= 255){
			this.identifier = identifier;
		}else{
			throw new InvalidEAPPacketException("Invalid identifier : "+identifier);
		}
	}
	
	public int getLength() {
		return this.length;
	}
	
	public void setLength(int length){
		this.length = length;
	}
	
	public EAPType getEAPType() {
		if(this.eapType.size() > 0)
		return this.eapType.get(0);
		return new EAPType(EapTypeConstants.NO_ALTERNATIVE.typeId);
	}

	public List<EAPType> getEAPTypes() {
		return this.eapType;
	}

	public void setEAPType(EAPType eapType) throws InvalidEAPPacketException {
		if(eapType != null){
			this.eapType.clear();
			this.eapType.add(eapType);
		}else{
			throw new InvalidEAPPacketException("EAPType null");
		}
	}

	public void setEAPType(List<EAPType> eapType) throws InvalidEAPPacketException {
		this.eapType = eapType;
	}

	public void addEAPType(EAPType eapType) throws InvalidEAPPacketException {
		if(eapType != null){			
			this.eapType.add(eapType);
		}else{
			throw new InvalidEAPPacketException("EAPType null");
		}
	}

	public void resetLength(){
		if (this.eapType == null){
			this.length = 4;
		}else{
			int typeLength = 0;
			for(int i=0; i < this.eapType.size() ; i ++){
				typeLength += this.eapType.get(i).toBytes().length;
			}
			this.length = 4 + typeLength;
		}
	}
	
    /**
     * Encodes the EAP packet into a new byte array.
     * @return the resultant byte array.
     */
	public byte[] getBytes() {
		byte[] packetBytes = new byte[this.length];
		
		packetBytes[0] = (byte)this.code;
		packetBytes[1] = (byte)this.identifier;
		
		packetBytes[3] = (byte)this.length;
		packetBytes[2] = (byte)(this.length >>> 8);
		
		if (this.eapType!=null){
			int typeLength = 0;
			for(int i=0; i < this.eapType.size() ; i ++){					
				System.arraycopy(this.eapType.get(i).toBytes(), 0, packetBytes, typeLength+4, this.eapType.get(i).toBytes().length);
				typeLength += this.eapType.get(i).toBytes().length;
			}		
		}
		
		return packetBytes;
	}
	
	/**
	 * 
	 */
	public String toString(){
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println(" Code = " + EapPacketConstants.getName(this.code) + "(" + this.code +")");
		out.println(" Identifier = " + this.identifier);
		out.println(" Length = " + this.length);
		if(eapType != null)
			out.println(eapType);

		out.close();
		
		return stringBuffer.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		EAPPacket eapPacket = (EAPPacket) super.clone();
		if (this.eapType!=null){
			eapPacket.eapType = new ArrayList<EAPType>();
			for(int i = 0 ; i < this.eapType.size() ; i ++){
				eapPacket.eapType.add((EAPType)this.eapType.get(i).clone());
			}
		}
		return eapPacket;
	}
	
}
 
