package com.elitecore.diameterapi.diameter.common.packet;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.packet.MalformedPacketException;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.packet.ParseException;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.avps.BaseDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.UnknownAttribute;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;


public abstract class DiameterPacket extends Packet implements IDiameterPacket ,Cloneable{

	public static final int DEFAULT_DIAMETER_VERSION = 1;
	private static final String MODULE = "Diameter Packet";
	public static final int DEFAULT_DIAMETER_PACKET_LENGTH = 20;
	public static final boolean INCLUDE_INFO_ATTRIBUTE = true;
	public static final int UNKNOWN_STREAM = -1;
	public static final int DIA_PCKT_MAX_EXPECTED_LENGTH = 8192;
	/**
	 * RPETrrrr  --> 1 << 7 = 128
	 */
	public static final int COMMAND_FLAG_REQUEST_BIT = 128;
	
	private int rcvdOnStream;
	
	private byte bVersion; // Variable Stores Version field value of Packet
	private int intLength; // Variable Stores Length field value of Packet
	private byte bFlag = 0; // Variable Stores Falg field value of Packet
	private int intCommandCode; // Variable Stores CommandCode field value of
	// Packet
	private int intApplicationId; // Variable Stores Application Id field value
	// of Packet
	private int intHopByHopIdentifier; // Variable Stores Hop by Hop Identifier
	// field value of Packet
	private int intEndToEndIdentifier; // Variable Stores End to End Identifier
	// field value of Packet
	
	private int infoLength;

	private Map<String, ArrayList<IDiameterAVP>> avpmap;
	private ArrayList<IDiameterAVP> col;
	
	private Map<String, ArrayList<IDiameterAVP>> infoAVPMap;
	private List<IDiameterAVP> infoAVPList;
	
	private long lPacketCreationTimeMillis;
	private long sendTime;
	
 	
	private ByteBuffer header;
	private HashMap<String, Object> parameterMap;
	private PeerData peerData;
	private long queueTime;
	
	public DiameterPacket() {
		super();
		lPacketCreationTimeMillis = System.currentTimeMillis();
		initialize();
	}

	public DiameterPacket(TimeSource timeSource) {
		super();
		lPacketCreationTimeMillis = timeSource.currentTimeInMillis();
		initialize();
	}

	protected DiameterPacket(byte[] buffer) {
		this();
		if (buffer != null)
			setBytes(buffer);

	}

	private void  initialize() {
		parameterMap = new HashMap<String, Object>();
		avpmap = new HashMap<String, ArrayList<IDiameterAVP>>();
		col = new ArrayList<IDiameterAVP>();
		infoAVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		infoAVPList = new ArrayList<IDiameterAVP>();
		header = ByteBuffer.allocate(DEFAULT_DIAMETER_PACKET_LENGTH);
		setVersion(DEFAULT_DIAMETER_VERSION);
		this.rcvdOnStream = UNKNOWN_STREAM;
	}

	/**
	 * 
	 * @param data
	 */
	public void setPacketBytes(byte[] data) {
		InputStream in = new DataInputStream(new ByteArrayInputStream(data));
		try {
			readFrom(in);
		} catch (IOException ioException) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while reading packet data, reason : "+ ioException);
			setVersion(DEFAULT_DIAMETER_VERSION);
			setCommandCode(0);
			resetErrorBit(); 
			resetProxiableBit();
			resetReTransmittedBit();
			setApplicationID(0);
			setHop_by_hopIdentifier(0);
			setEnd_to_endIdentifier(0);
			avpmap = new HashMap<String, ArrayList<IDiameterAVP>>();
		}

	}

	public void parsePacketBytes(byte[] headerBytes, byte[] avpBytes) throws ParseException {
		try {
			parsePacketHeaderBytes(headerBytes);
			parsePacketAVPBytes(avpBytes);
		} catch (Exception e) {
			throw new ParseException("Exception occured while parsing packet with HbH-ID=" + getHop_by_hopIdentifier() + 
					" and EtE-ID=" + getEnd_to_endIdentifier(), e);
		}
	}

	public void parsePacketHeaderBytes (byte[] headerBytes) {
		header = ByteBuffer.wrap(headerBytes);
		this.bVersion = headerBytes[0];

		this.intLength = headerBytes[1]& 0xFF;
		this.intLength = (intLength << 8) | headerBytes[2] & 0xFF;
		this.intLength = (intLength << 8) | headerBytes[3] & 0xFF;

		this.bFlag = (byte) (headerBytes[4] & 0xFF);

		this.intCommandCode = headerBytes[5]& 0xFF;
		this.intCommandCode = (this.intCommandCode << 8) | headerBytes[6]& 0xFF;
		this.intCommandCode = (this.intCommandCode << 8) | headerBytes[7]& 0xFF;

		this.intApplicationId = headerBytes[8]& 0xFF;
		this.intApplicationId = (this.intApplicationId << 8)| headerBytes[9] & 0xFF;
		this.intApplicationId = (this.intApplicationId << 8)| headerBytes[10] & 0xFF;
		this.intApplicationId = (this.intApplicationId << 8)| headerBytes[11] & 0xFF;

		this.intHopByHopIdentifier = headerBytes[12]& 0xFF;
		this.intHopByHopIdentifier = (this.intHopByHopIdentifier << 8)| headerBytes[13] & 0xFF;
		this.intHopByHopIdentifier = (this.intHopByHopIdentifier << 8)| headerBytes[14] & 0xFF;
		this.intHopByHopIdentifier = (this.intHopByHopIdentifier << 8)| headerBytes[15] & 0xFF;

		this.intEndToEndIdentifier = headerBytes[16]& 0xFF;
		this.intEndToEndIdentifier = (this.intEndToEndIdentifier << 8)| headerBytes[17] & 0xFF;
		this.intEndToEndIdentifier = (this.intEndToEndIdentifier << 8)| headerBytes[18] & 0xFF;
		this.intEndToEndIdentifier = (this.intEndToEndIdentifier << 8)| headerBytes[19] & 0xFF;
	}

	public void parsePacketAVPBytes(byte[] avpBytes) throws IOException {

		parsePacketAVPBytes(new ByteArrayInputStream(avpBytes));
	}

	public int parsePacketAVPBytes(InputStream ipStream) throws IOException {
		col.clear();
		avpmap.clear();
		
		int intAVPCode = 0;
		int bAVPFlag = 0;
		int intAVPLength = 0;
		int intVendorId = 0;
		
		IDiameterAVP diameterAttribute;
		int totalBytes = 0;
		
		while (totalBytes != intLength && (intAVPCode = ipStream.read()) != -1) {
			intAVPCode = intAVPCode << 8;
			intAVPCode = (intAVPCode << 8) | (ipStream.read() & 0xFF);
			intAVPCode = (intAVPCode << 8) | (ipStream.read() & 0xFF);
			intAVPCode = (intAVPCode << 8) | (ipStream.read() & 0xFF);

			totalBytes += 4;

			bAVPFlag = ipStream.read() & 0xFF;

			intAVPLength = ipStream.read();
			intAVPLength = (intAVPLength << 8) | (ipStream.read() & 0xFF);
			intAVPLength = (intAVPLength << 8) | (ipStream.read() & 0xFF);

			totalBytes += 4;

			if ((byte) (((bAVPFlag & 0xFF) & DiameterUtility.BIT_10000000) & 0xFF) == -128) {
				intVendorId = ipStream.read();
				intVendorId = (intVendorId << 8) | (ipStream.read() & 0xFF);
				intVendorId = (intVendorId << 8) | (ipStream.read() & 0xFF);
				intVendorId = (intVendorId << 8) | (ipStream.read() & 0xFF);
				totalBytes += 4;
				diameterAttribute = DiameterDictionary.getInstance().getAttribute(intVendorId, intAVPCode);
			} else {
				diameterAttribute = DiameterDictionary.getInstance().getAttribute(intAVPCode);
			}

			// If Unknown Attribute is identified
			if (diameterAttribute == null) {
				BaseDiameterAVP temp = new UnknownAttribute();
				totalBytes += temp.readFlagOnwardsFrom(ipStream);
				continue;
			}

			diameterAttribute.setFlag(bAVPFlag);
			diameterAttribute.setLength(intAVPLength);

			/**
			 * As of now dictionary is giving AVP Object by setting that avp
			 * code
			 */
			try {
				totalBytes += diameterAttribute.readFlagOnwardsFrom(ipStream);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "error in reading attribute :: "+ diameterAttribute);
				LogManager.ignoreTrace(e);
			}
			ArrayList<IDiameterAVP> values = avpmap.get(diameterAttribute.getAVPId());

			if (values == null) {
				values = new ArrayList<IDiameterAVP>();
				avpmap.put(diameterAttribute.getAVPId(), values);
			}

			values.add(diameterAttribute);
			col.add(diameterAttribute);
		}
		return totalBytes;
	}

	public int readFrom(InputStream ips) throws IOException {

		int totalBytes = 0;
		byte[] header = new byte[20];
		totalBytes = ips.read(header);

		if (totalBytes <= 0) {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Connection has been terminated, No more communication");
			return -1;
		}
		parsePacketHeaderBytes(header);
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Reading contents for the length : "+ intLength);

		totalBytes += parsePacketAVPBytes(ips);
		return totalBytes;
	}


	/**
	 * write created packet in byte format
	 * 
	 * @param destinationStream to which packet bytes will be written.
	 * 
	 * @throws IOException 
	 */
	@Override
	public void writeTo(@Nonnull ByteArrayOutputStream destinationStream) throws IOException {
		writeTo(destinationStream, false);
	}
	
	public void writeTo(@Nonnull ByteArrayOutputStream out, boolean bIncludeInfoAttr) throws IOException {

		refreshPacketHeader();
		if (bIncludeInfoAttr) {
			refreshInfoPacketHeader();
		}
		// Writing Header
		header.position(0);
		out.write(header.array());

		// Writing all AVPs with its padding bytes.
		ArrayList<IDiameterAVP> col = this.getAVPList();
		final int listSize = col.size();
		for (int i = 0; i < listSize; i++) {
			IDiameterAVP diameterAvp =  col.get(i);
			diameterAvp.writeTo(out);
		}

		if (bIncludeInfoAttr) {
			final int numOfInfoAvps = this.infoAVPList.size();
			for (int currentAvpIndex = 0; currentAvpIndex < numOfInfoAvps; currentAvpIndex++) {
				IDiameterAVP diameterAvp = infoAVPList.get(currentAvpIndex);
				diameterAvp.writeTo(out);
			}
		}
	}
	
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public byte[] getBytes() {
		return getBytes(false);
	}
	
	public byte[] getBytes(boolean bIncludeInfoAttr) {
		refreshPacketHeader();
		if(bIncludeInfoAttr)
			refreshInfoPacketHeader();
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(getLength());
		try {
			writeTo(buffer, bIncludeInfoAttr);
		} catch (Exception ie) {
			LogManager.getLogger().trace(MODULE, ie);
		}
		return buffer.toByteArray();
	
		
	}

	public void setBytes(byte[] data) {
		try {
			readFrom(new DataInputStream(new ByteArrayInputStream(data)));
		} catch (IOException ioException) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while reading packet data, reason : "+ ioException);
			setVersion(0);
			setCommandCode(0);
			setApplicationID(0);
			resetErrorBit();
			resetProxiableBit();
			resetReTransmittedBit();
			setHop_by_hopIdentifier(0);
			setEnd_to_endIdentifier(0);
			avpmap.clear();
		}

	}

	public ArrayList<IDiameterAVP> getAVPList() {
		return col;
	}
	
	public ArrayList<IDiameterAVP> getAVPList(String strAVPCode) {
		return getAVPListFromAVPMap(strAVPCode, avpmap);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<IDiameterAVP> getAVPListFromAVPMap(String strAVPCode, Map<String, ArrayList<IDiameterAVP>> avpMap) {
		ArrayList<IDiameterAVP> avpList  = avpMap.get(strAVPCode);
		if(Collectionz.isNullOrEmpty(avpList) == false){
			return (ArrayList<IDiameterAVP>)avpList.clone();
		}
		if(Strings.isNullOrBlank(strAVPCode) || DiameterUtility.isGroupAvpId(strAVPCode) == false){
			return null;
		}
		List<String> attributeIDs = DiameterUtility.diaAVPIdSplitter.split(strAVPCode);
		List<IDiameterAVP> parentAVPs = avpMap.get(attributeIDs.get(0));

		for(int i = 1 ; i < attributeIDs.size() ; i++){
			
			if(Collectionz.isNullOrEmpty(parentAVPs)){
				return null;
			}
			avpList = new ArrayList<IDiameterAVP>();
			for(IDiameterAVP avp : parentAVPs){
				
				if(avp.isGrouped() == false){
					return null;
				}
				avpList.addAll(((AvpGrouped)avp).getSubAttributeList(attributeIDs.get(i)));
			}
			parentAVPs = avpList;
		}
		if(avpList.isEmpty()){
			return null;
		}
		return (ArrayList<IDiameterAVP>)avpList.clone();
	}

	public ArrayList<IDiameterAVP> getAVPList(String strAVPCode,boolean bIncludeInfoAvp) {
		ArrayList<IDiameterAVP> avpList = getAVPList(strAVPCode);
		if(!bIncludeInfoAvp)
			return avpList;
		if(avpList!=null && avpList.isEmpty() == false){
			ArrayList<IDiameterAVP> tempInfoAvps= (ArrayList<IDiameterAVP>)getInfoAVPList(strAVPCode);
			if(tempInfoAvps!=null && tempInfoAvps.isEmpty() == false){
				ArrayList<IDiameterAVP> finalAvpList = new ArrayList<IDiameterAVP>();
				finalAvpList.addAll(avpList);
				finalAvpList.addAll(tempInfoAvps);
				avpList = finalAvpList;
		}
		}else {
			avpList = (ArrayList<IDiameterAVP>)getInfoAVPList(strAVPCode);
		}
		return avpList;
	}

	/**
	 * return packet code. E.g For CER (Capabilities-Exchange-Request) Packet code = 257.
	 */
	public int getCommandCode() {
		return intCommandCode;
	}

	/**
	 * Set Application ID.
	 * 
	 * @param applicationId
	 */
	public void setApplicationID(long applicationId) {
		this.intApplicationId = (int)applicationId;
		DiameterUtility.intToByteArray(this.header, 8, (int)(applicationId & 0xFFFFFFFF), 4);
	}

	/**
	 * return application Id. 
	 * For Base = 0 
	 * NASREQ = 1 MOBILE IP = 2 
	 * ACCOUTING =3 
	 * Vendor Specific = ?
	 */
	public long getApplicationID() {
		return intApplicationId;
	}

	/**
	 * set hop by hop identifier.
	 * 
	 * @param hopByHopIdentifier
	 */
	public void setHop_by_hopIdentifier(int hopByHopIdentifier) {
		this.intHopByHopIdentifier = hopByHopIdentifier;
		DiameterUtility.intToByteArray(this.header, 12, hopByHopIdentifier, 4);
	}

	/**
	 * return hop_by_hop Identifier.
	 * 
	 * @param void
	 */
	public int getHop_by_hopIdentifier() {
		return intHopByHopIdentifier;
	}

	/**
	 * set hop by hop identifier.
	 * 
	 * @param endToEndIdentifier
	 */
	public void setEnd_to_endIdentifier(int endToEndIdentifier) {
		this.intEndToEndIdentifier = endToEndIdentifier;
		DiameterUtility.intToByteArray(this.header, 16, endToEndIdentifier, 4);
	}

	/**
	 * return end_to_end Identifier.
	 */
	public int getEnd_to_endIdentifier() {
		return intEndToEndIdentifier;
	}

	/**
	 * return AVP Map. This Map contain AVP code as a key and that AVP Object as
	 * data.
	 * 
	 * @param void
	 */
	@Override
	public Map<String, ArrayList<IDiameterAVP>> getAvpmap() {
		return avpmap;
	}

	/**
	 * Add AVP in list of packet.
	 * 
	 * @param diameterAvp
	 */
	public void addAvp(IDiameterAVP diameterAvp) {
		if(diameterAvp!=null){
			String strAvpId = diameterAvp.getAVPId();
			ArrayList<IDiameterAVP> values = avpmap.get(strAvpId);
	
			if (values == null) {
				values = new ArrayList<IDiameterAVP>();
				avpmap.put(strAvpId, values);
			}
			values.add(diameterAvp);
			if (DiameterAVPConstants.SESSION_ID_INT == diameterAvp.getAVPCode()) {
				col.add(0, diameterAvp);
			} else {
				col.add(diameterAvp);
			}
		}	
	}
	

	@Override
	public void addInfoAvp(IDiameterAVP diameterAvp){
		if(diameterAvp!=null){
			String strAvpId = diameterAvp.getAVPId();
			ArrayList<IDiameterAVP> values = infoAVPMap.get(strAvpId);
	
			if (values == null) {
				values = new ArrayList<IDiameterAVP>();
				infoAVPMap.put(strAvpId, values);
			}
			values.add(diameterAvp);
			infoAVPList.add(diameterAvp);
			
		}
	}

	public void addAvp(String strAvpCode, String value) {
		if (value != null) {
			IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(strAvpCode);
			if(avp!=null){
				avp.setStringValue(value);
				addAvp(avp);
			}	
		}
	}

	public void addAvp(String strAvpCode, List<String> valueList) {
		Iterator<String> itr = valueList.iterator();
		while (itr.hasNext()) {
			addAvp(strAvpCode, itr.next());
		}
	}

	
	public void addAvp(String strAvpCode, long value) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(strAvpCode);
		if(avp!=null){
			avp.setInteger(value);
			addAvp(avp);
		}
	}

	public void addInfoAvp(String strAVPCode, @Nonnull String value) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(strAVPCode);
		if(avp!=null){
			avp.setStringValue(value);
			addInfoAvp(avp);
		}
	}
	
	public void addInfoAvp(String strAvpCode, @Nonnull List<String> valueList) {
		Iterator<String> itr = valueList.iterator();
		while (itr.hasNext()) {
			addInfoAvp(strAvpCode, itr.next());
		}
	}
	
	public void addInfoAvp(String strAVPCode, long value) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getKnownAttribute(strAVPCode);
		if(avp!=null){
			avp.setInteger(value);
			addInfoAvp(avp);
		}
	}

	public void addAvps(List<IDiameterAVP> avpList) {
		if (avpList != null) {
			Iterator<IDiameterAVP> itr = avpList.iterator();
			while (itr.hasNext()) {
				IDiameterAVP diameterAVP = itr.next();
				addAvp(diameterAVP);
			}
		}
	}

	/**
	 * Set version field of packet.
	 * 
	 * @param version
	 */
	public void setVersion(int version) {
		this.bVersion = (byte) version;
		DiameterUtility.intToByteArray(this.header, 0, bVersion, 1);
	}

	/**
	 * return version field of packet.
	 */
	public int getVersion() {
		return bVersion;
	}

	/**
	 * set length field of packet.
	 * 
	 * @param length
	 */
	public void setLength(int length) {
		this.intLength = length;
		DiameterUtility.intToByteArray(this.header, 1, intLength, 3);		
	}

	/**
	 * return length field value of packet.
	 */
	public int getLength() {
		// we should only return only current value of intLength rather than calculating it every time this function call  
		int intLength = 20;
		if (col != null) {
			IDiameterAVP attr;
			final int listSize = col.size();
			for (int i = 0; i < listSize; i++) {
				attr = col.get(i);
				intLength += attr.getLength();
				intLength += attr.getPaddingLength();
			}
		}
		return intLength;
	}
	
	public int getInfoLength(){
		return infoLength;
	}

	/**
	 * Return Command Flag field of Packet.
	 */
	public int getCommandFlag() {
		return bFlag;
	}

	/**
	 * Set packet Code field.
	 * 
	 * @param type
	 */
	public void setCommandCode(int type) {
		this.intCommandCode = type;
		DiameterUtility.intToByteArray(this.header, 5, type, 3);
	}
	
	public boolean isServerInitiated() {
		@Nonnull CommandCode commandCode = CommandCode.getCommandCode(getCommandCode());
		return commandCode.isServerInitiated;
	}

	/**
	 * return collection of vendor specific attributes for vendorparameterid of
	 * specified vendorid
	 * 
	 * @param vendorID
	 * @param attributeID
	 * @return
	 */
	public List<IDiameterAVP> getVendorSpeficAvps(long vendorID, int avpCode) {
		return getAVPList(vendorID+":"+avpCode);
	}

	/**
	 * Return AVP Object as per passed parameter which can be Avp-Name or Avp-id.
	 * 
	 * @param strAvpCode
	 */	
	public IDiameterAVP getAVP(String strAvpId) {
		IDiameterAVP diameterAVP = getAVPFFromID(strAvpId,this.avpmap);
		
		if(diameterAVP==null){
			String avpId = DiameterUtility.getAVPIdFromName(strAvpId);
			if(avpId!=null)
				 diameterAVP = getAVPFFromID(avpId,this.avpmap);
		}
		
		return diameterAVP;
	}
	
	@Override
	public IDiameterAVP getAVP(String strAvpId,boolean bIncludeInfoAttr){
		IDiameterAVP diameterAVP = getAVP(strAvpId);
		if(!bIncludeInfoAttr)
			return diameterAVP;
		if(diameterAVP==null)
			diameterAVP = getInfoAVP(strAvpId);
		return diameterAVP;
			
			
	}
	@Override
	public IDiameterAVP getInfoAVP(String strAvpId) {
		IDiameterAVP diameterAVP = getAVPFFromID(strAvpId,this.infoAVPMap);
		
		if(diameterAVP==null){
			String avpId = DiameterUtility.getAVPIdFromName(strAvpId);
			if(avpId!=null)
				 diameterAVP = getAVPFFromID(avpId,infoAVPMap);
		}
		
		return diameterAVP;
	}
	
	@Override
	public String getInfoAVPValue(String strAvpId) {
		IDiameterAVP diameterAVP =  getInfoAVP(strAvpId);
		return diameterAVP !=null ? diameterAVP.getStringValue() : null;
	}
	
	@Override
	public List<IDiameterAVP> getInfoAVPList(String strAVPCode) {
		return getAVPListFromAVPMap(strAVPCode, infoAVPMap);
	}

	private IDiameterAVP getAVPFFromID(String strAvpId,Map<String, ArrayList<IDiameterAVP>> avpMap) {

		List<IDiameterAVP> avps = avpMap.get(strAvpId);
		if (avps != null) {
			return avps.get(0);
		}

		// In case of any group attribute key.  
		if(Strings.isNullOrBlank(strAvpId) || DiameterUtility.isGroupAvpId(strAvpId) == false){
			return null;
		}
		List<String> avpIds = DiameterUtility.diaAVPIdSplitter.split(strAvpId);
		IDiameterAVP currentAvp = null;
		List<IDiameterAVP> avpList = avpMap.get(avpIds.get(0));

		if(Collectionz.isNullOrEmpty(avpList)){
			return null;
		}
		for(int j=0 ; j < avpList.size() ; j++){				
			currentAvp = avpList.get(j); 
			for(int i=1 ; i< avpIds.size() ; i++){
				if(currentAvp != null){
					if(currentAvp.isGrouped() == false){
						return null;
					}
					currentAvp = ((AvpGrouped)currentAvp).getSubAttribute(avpIds.get(i));
				}									
			}
			if(currentAvp != null){
				return currentAvp;
			}
		}
		return null;
	}

	public int removeAVP(IDiameterAVP avp){
		return remove(avp, avpmap, col);
	}

	private int remove(IDiameterAVP avp, Map<String, ArrayList<IDiameterAVP>> avpMap, List<IDiameterAVP> avpList) {
		if(avp == null){
			return 0;
		}
		IDiameterAVP diameterAvp = avp;

		ArrayList<IDiameterAVP> avps = avpMap.get(diameterAvp.getAVPId());
		if(Collectionz.isNullOrEmpty(avps)){
			return 0;
		}
		boolean exists = avps.remove(diameterAvp);
		if(exists == false){
			return 0;
		}
		if(avps.isEmpty()){
			avpMap.remove(String.valueOf(diameterAvp.getAVPId()));
		}
		avpList.remove(diameterAvp);
		return 1;
	}
	
	public int removeAVP(IDiameterAVP avp, boolean  bIncludeInfoAttribute){
		
		int removed = 0;
		removed += removeAVP(avp);
		if(bIncludeInfoAttribute){
			removed += removeInfoAVP(avp);
		}
		return removed;
	}
	
	public int removeInfoAVP(IDiameterAVP avp){
		return remove(avp, infoAVPMap, infoAVPList);
	}
	
	public boolean containsAVP(IDiameterAVP avp){
		return col.contains(avp);
	}
	
	public boolean containsAVP(IDiameterAVP avp, boolean bincludeInfoAVP){
		
		boolean contains = containsAVP(avp);
		
		if(!bincludeInfoAVP)
			return contains;
		
		if(!contains)
			contains = containsInfoAVP(avp);
		return contains;
	}

	public boolean containsInfoAVP(IDiameterAVP avp){
		return infoAVPList.contains(avp);
	}

	/**
	 * Return true if first bit of command code field of packet has been set,
	 * else return false.
	 */
	public boolean isRequest() {
		return (this.bFlag & 0x80) != 0;
	}

	/**
	 * Return true if first bit of command code field of packet has not been set,
	 * else return false.
	 */
	public boolean isResponse() {
		return (this.bFlag & 0x80) == 0;
	}

	public boolean isProxiable() {
		return (this.bFlag & 0x40) != 0;
	}

	public boolean isError() {
		return (this.bFlag & 0x20) != 0;
	}

	public boolean isReTransmitted() {
		return (this.bFlag & 0x10) != 0;
	}

	public void setRequestBit() {
		this.bFlag |= 0x80;
		this.header.put(4, bFlag);
	}

	public void setProxiableBit() {
		this.bFlag |= 0x40;
		this.header.put(4, bFlag);
	}

	public void setErrorBit() {
		this.bFlag |= 0x20;
		this.header.put(4, bFlag);
	}

	public void setReTransmittedBit() {
		this.bFlag |= 0x10;
		this.header.put(4, bFlag);
	}

	public long creationTimeMillis() {
		return lPacketCreationTimeMillis;
	}


	/*
	 * Methods added By Dhruvang
	 */
	private int resetBit(int commandFlag, int position) {
		int mask = 1;
		mask = mask << (position - 1);
		return (~((~commandFlag) | mask));
	}

	public void resetRequestBit() {
		bFlag = (byte) resetBit(bFlag, 8);
		this.header.put(4, bFlag);
	}

	public void resetProxiableBit() {
		bFlag = (byte) resetBit(bFlag, 7);
		this.header.put(4, bFlag);
	}

	public void resetErrorBit() {
		bFlag = (byte) resetBit(bFlag, 6);
		this.header.put(4, bFlag);
	}

	public void resetReTransmittedBit() {
		bFlag = (byte) resetBit(bFlag, 5);
		this.header.put(4,bFlag);
	}

	public void setResponsePacketHeader(IDiameterPacket requestPacket) {
		intCommandCode = requestPacket.getCommandCode();
		intApplicationId = (int)requestPacket.getApplicationID();
		intHopByHopIdentifier = requestPacket.getHop_by_hopIdentifier();
		intEndToEndIdentifier = requestPacket.getEnd_to_endIdentifier();
	}

	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
 
		out.print("\tVer=" + getVersion());
		out.printf(", Len=%04d", getLength());
		out.print(", Flags=[R=" + (isRequest()?1:0) + " P="+ (isProxiable()?1:0) + " E=" + (isError()?1:0) + " T="+ (isReTransmitted()?1:0) + "]");
		out.print(", CMD="+ CommandCode.getDisplayName(getCommandCode()) );
		if(isRequest())
			out.print("R");
		else
			out.print("A");
		out.print("(" + getCommandCode() + ")");
		out.print(", App="+ApplicationIdentifier.getDisplayName(getApplicationID()));
		out.print("(" + getApplicationID() + ")");
		out.print(", H2H=" + DiameterUtility.bytesToHex(DiameterUtility.intToByteArray(getHop_by_hopIdentifier())));
		out.print(", E2E=" + DiameterUtility.bytesToHex(DiameterUtility.intToByteArray(getEnd_to_endIdentifier())));
		out.println();
		out.println("\tAVPs : ");
		
		final int listSize = col.size();
		for (int i = 0; i < listSize; i++) {
			out.println(col.get(i).toString());
		}
		int numOfInfoAvp = infoAVPList.size();
		if(numOfInfoAvp>0){
			out.println("\t--Info AVPs");
			for (int j = 0; j < numOfInfoAvp; j++) {
				out.println(infoAVPList.get(j).toString());
			}
		}
		out.flush();
		out.close();
		return stringBuffer.toString();
	}

	public void resetDiameterPacket() {
		avpmap = new HashMap<String, ArrayList<IDiameterAVP>>();
		col = new ArrayList<IDiameterAVP>();
		infoAVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		infoAVPList = new ArrayList<IDiameterAVP>();
		header = ByteBuffer.allocate(DEFAULT_DIAMETER_PACKET_LENGTH);
		touch();
		setVersion(DEFAULT_DIAMETER_VERSION);
		setRequestBit();
	}

	public void refreshPacketHeader() {
		int length=DEFAULT_DIAMETER_PACKET_LENGTH;
		if (col != null) {
			IDiameterAVP attr;
			for (int i = 0; i < col.size(); i++) {
				attr = col.get(i);
				length += attr.getLength() + attr.getPaddingLength();
			}
		}
		setLength(length);
	}
	
	public void refreshInfoPacketHeader(){
		int length=0;
		if (this.infoAVPList != null) {
			IDiameterAVP infoAvp;
			int numberOfInfoAvps = this.infoAVPList.size(); 
			for (int i = 0; i < numberOfInfoAvps; i++) {
				infoAvp = infoAVPList.get(i); 
				length += infoAvp.getLength() + infoAvp.getPaddingLength();
			}
		}
		this.infoLength = length;
	}
	
	public String getDestinationHost() {
		ArrayList<IDiameterAVP> diameterAVPs = avpmap.get(DiameterAVPConstants.DESTINATION_HOST);
		if (diameterAVPs != null) {
			IDiameterAVP avp = diameterAVPs.get(0);
			if(avp != null) {
				return avp.getStringValue();
			}
		}
		return null;
	}

	
	public String getAVPValue(String avpIdentifier) {
		return getAVPValue(avpIdentifier, false);
	}
	public String getAVPValue(String avpIdentifier,boolean bIncludeInfoAttr ) {
		IDiameterAVP diameterAVP = getAVP(avpIdentifier,bIncludeInfoAttr);
		if (diameterAVP != null) {
			return diameterAVP.getStringValue();
		}
		return null;
	}
	
	// Note: This method will not clone the parameter map as it may contain any Object which may be not clonnable.
	
	public Object clone() throws CloneNotSupportedException {
		DiameterPacket clonePacket = null;
		clonePacket = (DiameterPacket) super.clone();
		clonePacket.col = new ArrayList<IDiameterAVP>();
		clonePacket.avpmap = new HashMap<String, ArrayList<IDiameterAVP>>();
		clonePacket.infoAVPList = new ArrayList<IDiameterAVP>();
		clonePacket.infoAVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		clonePacket.parameterMap = new HashMap<String, Object>();
		
		clonePacket.rcvdOnStream = this.rcvdOnStream;

		if (this.header != null) {
			byte[] headerBytes = header.array();
			clonePacket.header = ByteBuffer.wrap(Arrays.copyOf(headerBytes, headerBytes.length));
		}
		
		final int numOfAvp = col.size();
		for(int i=0;i<numOfAvp;i++) {
			IDiameterAVP diameterAVP = (IDiameterAVP) this.col.get(i).clone(); 
			clonePacket.col.add(diameterAVP);
			ArrayList<IDiameterAVP> values = clonePacket.avpmap.get(diameterAVP.getAVPId());
			if (values == null){
				values = new ArrayList<IDiameterAVP>();
				clonePacket.avpmap.put(diameterAVP.getAVPId(),values);
			}
			values.add(diameterAVP);
		}

		final int numOfInfoAvps = infoAVPList.size();
		for(int i=0;i<numOfInfoAvps;i++) {
			IDiameterAVP diameterAVP = (IDiameterAVP) this.infoAVPList.get(i).clone(); 
			clonePacket.infoAVPList.add(diameterAVP);
			ArrayList<IDiameterAVP> values = clonePacket.infoAVPMap.get(diameterAVP.getAVPId());
			if (values == null){
				values = new ArrayList<IDiameterAVP>();
				clonePacket.infoAVPMap.put(diameterAVP.getAVPId(),values);
			}
			values.add(diameterAVP);
		}
		return clonePacket;
	}
	
	/**
	 * Updates the Creation Time to the Current TimeStamp
	 */
	public void touch(){
		lPacketCreationTimeMillis = System.currentTimeMillis();
	}
	
	public void setParameter(String key, Object parameterValue) {			
		parameterMap.put(key, parameterValue);
	}
	
	public Object getParameter(String str) {			
		return parameterMap.get(str);
	}
	
	public Object removeParameter(String key) {
		return parameterMap.remove(key);
	}
	
	public Map<String, Object> getParameters() {
		return parameterMap;
	}
	
	public List<IDiameterAVP> getInfoAVPList() {
		return infoAVPList;
	}
	
	public long getSendTime() {
		return sendTime;
	}

	public abstract DiameterRequest getAsDiameterRequest();
	public abstract DiameterAnswer getAsDiameterAnswer();
	public void removeAllAVPs(List<IDiameterAVP> avps, boolean includeInfoAVPs) {
		for (IDiameterAVP avp : avps) {
			removeAVP(avp, includeInfoAVPs);
		}
	}
	
	public @Nullable String getSessionID() {
		return getAVPValue(DiameterAVPConstants.SESSION_ID);
	}
	
	public PeerData getPeerData() {
		return peerData;
	}
	
	public void setPeerData(PeerData peerData) {
		this.peerData = peerData;
	}
	
	/**
	 * Retains all the AVPs that pass the predicate {@code retainFilter}, rest of the AVP's are filtered.
	 * <b>This method doesn't process info AVPs.</b>
	 * 
	 * <p> NOTE: Size of retainable avp list is 75% of original avp list. This has been decided
	 * based on current usage, which may change in future.
	 */
	public void retain(@Nonnull Predicate<IDiameterAVP> retainFilter) {
		
		ArrayList<IDiameterAVP> avplist = this.getAVPList();
		Iterator<IDiameterAVP> avpIterator = avplist.iterator();
		
		ArrayList<IDiameterAVP> retainableAvpList = new ArrayList<IDiameterAVP>((int) (avplist.size() * 0.75));
		while (avpIterator.hasNext()) {
			IDiameterAVP avp = avpIterator.next();
			if (retainFilter.apply(avp)) {
				retainableAvpList.add(avp);
			} else { 
				avpmap.remove(avp.getAVPId());
			}
		}
		this.col = retainableAvpList;
	}

	public void addAvp(String strAvpCode, Date time) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(strAvpCode);
		if(avp!=null){
			avp.setTime(time);
			addAvp(avp);
		}
	}

	public long getQueueTime() {
		return queueTime;
	}

	public void setQueueTime(long queueTime) {
		this.queueTime = queueTime;
	}

	public int getRcvdOnStream() {
		return rcvdOnStream;
	}

	public void setRcvdOnStream(int rcvdOnStream) {
		this.rcvdOnStream = rcvdOnStream;
	}
	
	/**
	 * 
	 * @param headerBytes 
	 * @return {@link DiameterPacket} ({@link DiameterAnswer} or {@link DiameterRequest}) on the basis of received bytes types 
	 * @throws MalformedPacketException when </br>
	 * 		1) headerBytes length is shorte than 20 </br>
	 * 		2) diameter packet version is not 1 </br>
	 * 		3) packet size is more than 8192 bytes </br>
	 */
	
	public static DiameterPacket createPacket(byte[] headerBytes) throws MalformedPacketException {
		
		if (headerBytes.length < DEFAULT_DIAMETER_PACKET_LENGTH) {
			LogManager.getLogger().error(MODULE, "Illegal header length: " + headerBytes.length);
			throw new MalformedPacketException("Illeagal header length: " + headerBytes.length);
		}
		if (headerBytes[0] != DiameterPacket.DEFAULT_DIAMETER_VERSION) {
			LogManager.getLogger().error(MODULE, "Malformed hex bytes recieved: " + DiameterUtility.bytesToHex(headerBytes));
			throw new MalformedPacketException("Unsupported diameter version: " + headerBytes[0]);
		}
		
		int messageLength = headerBytes[1];
		messageLength = (messageLength << 8) | headerBytes[2] & 0xFF;
		messageLength = (messageLength << 8) | headerBytes[3] & 0xFF;
		
		if (messageLength < DEFAULT_DIAMETER_PACKET_LENGTH || 
				messageLength > DIA_PCKT_MAX_EXPECTED_LENGTH) {
			LogManager.getLogger().error(MODULE, "Malformed Hex bytes recieved: " + DiameterUtility.bytesToHex(headerBytes));
			throw new MalformedPacketException("Unsupported Diameter Message length: " + messageLength);
		}
		
		DiameterPacket diameterPacket;
		boolean isRequest = ((headerBytes[4] & COMMAND_FLAG_REQUEST_BIT) != 0);
		if (isRequest){
			diameterPacket = new DiameterRequest(false);
		} else {
			diameterPacket = new DiameterAnswer();
		}
		diameterPacket.parsePacketHeaderBytes(headerBytes);
		return diameterPacket;
	}
	
	public int getRcvdLength() {
		return this.intLength;
	}
}