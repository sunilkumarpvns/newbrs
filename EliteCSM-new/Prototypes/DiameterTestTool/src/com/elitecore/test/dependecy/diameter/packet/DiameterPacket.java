package com.elitecore.test.dependecy.diameter.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.dependecy.diameter.ApplicationIdentifier;
import com.elitecore.test.dependecy.diameter.DiameterAVPConstants;
import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.DiameterUtility;
import com.elitecore.test.dependecy.diameter.packet.avps.BaseDiameterAVP;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;
import com.elitecore.test.dependecy.diameter.packet.avps.UnknownAttribute;
import com.elitecore.test.dependecy.diameter.packet.avps.grouped.AvpGrouped;



public abstract class DiameterPacket extends Packet implements IDiameterPacket ,Cloneable{

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

	private ArrayList<String> packetAVPCodeList;
	
	private Map<String, ArrayList<IDiameterAVP>> AVPMap;
	private ArrayList<IDiameterAVP> col;
	
	private Map<String, ArrayList<IDiameterAVP>> infoAVPMap;
	private List<IDiameterAVP> infoAVPList;
	
	private long lPacketCreationTimeMillis;
	

	private final static String MODULE = "Diameter Packet";
	public static final int DEFAULT_DIAMETER_PACKET_LENGTH = 20;
	public static final boolean INCLUDE_INFO_ATTRIBUTE = true;
	

	public DiameterPacket() {
		super();
		bVersion = 1;
		lPacketCreationTimeMillis = System.currentTimeMillis();
		AVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		col = new ArrayList<IDiameterAVP>();
		packetAVPCodeList = new ArrayList<String>();
		infoAVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		infoAVPList = new ArrayList<IDiameterAVP>();
	}

	protected DiameterPacket(byte[] buffer) {
		this();
		if (buffer != null)
			setPacketBytes(buffer);

	}

	public void setPacketBytes(byte[] data) {
		/*
		 * AVPMap = new HashMap<String,ArrayList<IDiameterAVP>>(); 
		 * col = new ArrayList<IDiameterAVP>();
		 * packetAVPCodeList = new ArrayList<Integer>();
		 */
		InputStream in = new DataInputStream(new ByteArrayInputStream(data));
		try {
			readFrom(in);
		} catch (IOException ioException) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while reading packet data, reason : "+ ioException);
			this.bVersion = 0;
			this.intCommandCode = 0;
			this.bFlag = 0;
			this.intApplicationId = 0;
			this.intHopByHopIdentifier = 0;
			this.intEndToEndIdentifier = 0;
			AVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		}

	}

	public void parsePacketBytes(byte[] headerBytes, byte[] avpBytes)throws ParseException, IOException {
		parsePacketHeaderBytes(headerBytes);

		parsePacketAVPBytes(avpBytes);
	}

	private void parsePacketHeaderBytes(byte[] headerBytes)throws ParseException {
		try {
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
		} catch (Exception e) {
			throw new ParseException("", e);
		}

	}

	private void parsePacketAVPBytes(byte[] avpBytes) throws ParseException,IOException {
		int totalBytes = 0;
		col = new ArrayList<IDiameterAVP>();
		IDiameterAVP diameterAttribute;
		int intAVPCode = 0;

		InputStream ipStream = new DataInputStream(new ByteArrayInputStream(avpBytes));

		while (totalBytes != intLength && (intAVPCode = ipStream.read()) != -1) {
			int bAVPFlag = 0;
			int intAVPLength = 0;
			int intVendorId = 0;

			intAVPCode = intAVPCode << 8;
			intAVPCode = (intAVPCode << 8) | (ipStream.read() & 0xFF);
			intAVPCode = (intAVPCode << 8) | (ipStream.read() & 0xFF);
			intAVPCode = (intAVPCode << 8) | (ipStream.read() & 0xFF);

			totalBytes = totalBytes + 4;

			bAVPFlag = bAVPFlag | (ipStream.read() & 0xFF);// (byte)(sourceStream.read()// & 0xFF);

			intAVPLength = ipStream.read();
			intAVPLength = (intAVPLength << 8) | (ipStream.read() & 0xFF);
			intAVPLength = (intAVPLength << 8) | (ipStream.read() & 0xFF);

			totalBytes = totalBytes + 4;

			if ((byte) (((bAVPFlag & 0xFF) & DiameterUtility.BIT_10000000) & 0xFF) == -128) {
				intVendorId = ipStream.read();
				intVendorId = (intVendorId << 8) | (ipStream.read() & 0xFF);
				intVendorId = (intVendorId << 8) | (ipStream.read() & 0xFF);
				intVendorId = (intVendorId << 8) | (ipStream.read() & 0xFF);
				totalBytes = totalBytes + 4;
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

			/*
			 * As of now dictionary is giving AVP Object by setting that avp
			 * code
			 */
			// diameterAttribute.setAVPCode(intAVPCode);
			try {
				totalBytes += diameterAttribute.readFlagOnwardsFrom(ipStream);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "error in reading attribute :: "+ diameterAttribute);
			}
			ArrayList<IDiameterAVP> values = (ArrayList<IDiameterAVP>) AVPMap.get(diameterAttribute.getAVPId());

			if (values == null) {
				values = new ArrayList<IDiameterAVP>();
				AVPMap.put(diameterAttribute.getAVPId(), values);
			}

			values.add(diameterAttribute);
			col.add(diameterAttribute);
			packetAVPCodeList.add(diameterAttribute.getAVPId());
		}
	}

	public int readFrom(InputStream ips) throws IOException {

		DataInputStream sourceStream = (DataInputStream) ips;

		int totalBytes = 0;
		byte[] header = new byte[20];
		totalBytes = sourceStream.read(header);
		col = null;

		if (totalBytes > 0) {
			setHeaderData(header);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Reading contents for the length : "+ intLength);

			byte[] content = new byte[intLength - 20];
			sourceStream.readFully(content);

			InputStream ipStream = new DataInputStream(
					new ByteArrayInputStream(content));

			col = new ArrayList<IDiameterAVP>();
			IDiameterAVP diameterAttribute;
			int intAVPCode = 0;

			while (totalBytes != intLength
					&& (intAVPCode = ipStream.read()) != -1) {
				int bAVPFlag = 0;
				int intAVPLength = 0;
				int intVendorId = 0;

				intAVPCode = intAVPCode << 8;
				intAVPCode = (intAVPCode << 8) | (ipStream.read() & 0xFF);
				intAVPCode = (intAVPCode << 8) | (ipStream.read() & 0xFF);
				intAVPCode = (intAVPCode << 8) | (ipStream.read() & 0xFF);

				totalBytes = totalBytes + 4;

				bAVPFlag = bAVPFlag | (ipStream.read() & 0xFF);// (byte)(sourceStream.read()// & 0xFF);

				intAVPLength = ipStream.read();
				intAVPLength = (intAVPLength << 8) | (ipStream.read() & 0xFF);
				intAVPLength = (intAVPLength << 8) | (ipStream.read() & 0xFF);

				totalBytes = totalBytes + 4;

				if ((byte) (((bAVPFlag & 0xFF) & DiameterUtility.BIT_10000000) & 0xFF) == -128) {
					intVendorId = ipStream.read();
					intVendorId = (intVendorId << 8) | (ipStream.read() & 0xFF);
					intVendorId = (intVendorId << 8) | (ipStream.read() & 0xFF);
					intVendorId = (intVendorId << 8) | (ipStream.read() & 0xFF);
					totalBytes = totalBytes + 4;
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

				/*
				 * As of now dictionary is giving AVP Object by setting that avp
				 * code
				 */
				// diameterAttribute.setAVPCode(intAVPCode);
				try {
					totalBytes += diameterAttribute.readFlagOnwardsFrom(ipStream);
				} catch (Exception e) {
					LogManager.getLogger().error(MODULE, "error in reading attribute :: "+ diameterAttribute);
				}

				ArrayList<IDiameterAVP> values = (ArrayList<IDiameterAVP>) AVPMap.get(diameterAttribute.getAVPId());

				if (values == null) {
					values = new ArrayList<IDiameterAVP>();
					AVPMap.put(diameterAttribute.getAVPId(), values);
				}
				values.add(diameterAttribute);

				col.add(diameterAttribute);
				packetAVPCodeList.add(diameterAttribute.getAVPId());
			}
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Connection has been terminated, No more communication");
			return -1;
		}
		return totalBytes;
	}

	public void setHeaderData(byte[] data) {
		this.bVersion = data[0];

		this.intLength = data[1];
		this.intLength = (intLength << 8) | data[2] & 0xFF;
		this.intLength = (intLength << 8) | data[3] & 0xFF;

		this.bFlag = (byte) (data[4] & 0xFF);

		this.intCommandCode = data[5];
		this.intCommandCode = (this.intCommandCode << 8) | data[6] & 0xFF;
		this.intCommandCode = (this.intCommandCode << 8) | data[7] & 0xFF;

		this.intApplicationId = data[8];
		this.intApplicationId = (this.intApplicationId << 8) | data[9] & 0xFF;
		this.intApplicationId = (this.intApplicationId << 8) | data[10] & 0xFF;
		this.intApplicationId = (this.intApplicationId << 8) | data[11] & 0xFF;

		this.intHopByHopIdentifier = data[12];
		this.intHopByHopIdentifier = (this.intHopByHopIdentifier << 8)| data[13] & 0xFF;
		this.intHopByHopIdentifier = (this.intHopByHopIdentifier << 8)| data[14] & 0xFF;
		this.intHopByHopIdentifier = (this.intHopByHopIdentifier << 8)| data[15] & 0xFF;

		this.intEndToEndIdentifier = data[16];
		this.intEndToEndIdentifier = (this.intEndToEndIdentifier << 8)| data[17] & 0xFF;
		this.intEndToEndIdentifier = (this.intEndToEndIdentifier << 8)| data[18] & 0xFF;
		this.intEndToEndIdentifier = (this.intEndToEndIdentifier << 8)| data[19] & 0xFF;

	}

	/**
	 * write created packet in byte format in OutPut Stream of Peer.
	 * 
	 * @param destinationStream
	 * @throws IOException 
	 */
	public void writeTo(OutputStream destinationStream) throws IOException {
		writeTo(destinationStream, false);
	}
	
	public void writeTo(OutputStream destinationStream,boolean bIncludeInfoAttr) throws IOException {
		
		destinationStream.write(getBytes(bIncludeInfoAttr));
	
	}

	/**
	 * write created packet in byte format in OutPut Stream of Peer.
	 * 
	 * @param destinationStream
	 */
	public void writeTo(Writer destinationStream) {
	}

	public byte[] getBytes() {
		return getBytes(false);
	}
	
	public byte[] getBytes(boolean bIncludeInfoAttr){
		
		refreshPacketHeader();
		if(bIncludeInfoAttr)
			refreshInfoPacketHeader();
		
		int length;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			// Writing Version
			buffer.write(bVersion);

			// Writing Length			
			length = this.getLength();
			byte[] bLength = DiameterUtility.intToByteArray(length, 3);
			buffer.write(bLength);

			// Writing Flag
			buffer.write(bFlag);

			// Writing CommandCode
			byte[] bCommandCode = DiameterUtility.intToByteArray(
					intCommandCode, 3);
			buffer.write(bCommandCode);

			// Writing Application Id
			byte[] bApplicationId = DiameterUtility.intToByteArray(intApplicationId);
			buffer.write(bApplicationId);

			// Writing HopByHopIdentifier
			byte[] bHopByHopIdentified = DiameterUtility.intToByteArray(intHopByHopIdentifier);
			buffer.write(bHopByHopIdentified);

			// Writing EndToEndIdentifier
			byte[] bEndToEndIdentifier = DiameterUtility.intToByteArray(intEndToEndIdentifier);
			buffer.write(bEndToEndIdentifier);

			// Writing all AVPs with its padding bytes.
			ArrayList col = this.getAVPList();
			final int listSize = col.size();
			for (int i = 0; i < listSize; i++) {
				IDiameterAVP diameterAvp = (IDiameterAVP) col.get(i);
				buffer.write(diameterAvp.getBytes());
			}
			
			if(bIncludeInfoAttr){
				final int numOfInfoAvps = this.infoAVPList.size();
				for (int currentAvpIndex = 0; currentAvpIndex < numOfInfoAvps; currentAvpIndex++) {
					IDiameterAVP diameterAvp = infoAVPList.get(currentAvpIndex);
					buffer.write(diameterAvp.getBytes());
				}
				
			}
		} catch (Exception ie) {
			LogManager.getLogger().trace(MODULE, ie);
		}
		return buffer.toByteArray();
	
		
	}

	public void setBytes(byte[] data) {
		AVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		col = new ArrayList<IDiameterAVP>();
		packetAVPCodeList = new ArrayList<String>();
		InputStream in = new DataInputStream(new ByteArrayInputStream(data));
		try {
			readFrom(in);
		} catch (IOException ioException) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while reading packet data, reason : "+ ioException);
			this.bVersion = 0;
			this.intCommandCode = 0;
			this.bFlag = 0;
			this.intApplicationId = 0;
			this.intHopByHopIdentifier = 0;
			this.intEndToEndIdentifier = 0;
			AVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		}

	}

	public ArrayList<IDiameterAVP> getAVPList() {
		return (ArrayList<IDiameterAVP>) col;
	}
	
	public ArrayList<IDiameterAVP> getAVPList(String strAVPCode) {
		return getAVPListFromAVPMap(strAVPCode, AVPMap);
	}

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
		if(avpList!=null && avpList.size()>0){
			ArrayList<IDiameterAVP> tempInfoAvps= (ArrayList<IDiameterAVP>)getInfoAVPList(strAVPCode);
			if(tempInfoAvps!=null && tempInfoAvps.size()>0){
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
	 * @param hop_by_hopIdentifier
	 */
	public void setHop_by_hopIdentifier(int hop_by_hopIdentifier) {
		this.intHopByHopIdentifier = hop_by_hopIdentifier;
	}


	public int getHop_by_hopIdentifier() {
		return intHopByHopIdentifier;
	}

	/**
	 * set hop by hop identifier.
	 * 
	 * @param end_to_endIdentifier
	 */
	public void setEnd_to_endIdentifier(int end_to_endIdentifier) {
		this.intEndToEndIdentifier = end_to_endIdentifier;
	}

	/**
	 * return end_to_end Identifier.
	 */
	public int getEnd_to_endIdentifier() {
		return intEndToEndIdentifier;
	}


	public Map getAVPMap() {
		return AVPMap;
	}

	/**
	 * Add AVP in list of packet.
	 * 
	 * @param diameterAvp
	 */
	public void addAvp(IDiameterAVP diameterAvp) {
		if(diameterAvp!=null){
			String strAvpId = diameterAvp.getAVPId();
			ArrayList<IDiameterAVP> values = AVPMap.get(strAvpId);
	
			if (values == null) {
				values = new ArrayList<IDiameterAVP>();
				AVPMap.put(strAvpId, values);
			}
			values.add(diameterAvp);
			if (DiameterAVPConstants.SESSION_ID_INT == diameterAvp.getAVPCode()) {
				col.add(0, diameterAvp);
			} else {
				col.add(diameterAvp);
			}
			// col.add(diameterAvp);
			packetAVPCodeList.add(diameterAvp.getAVPId());
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
				attr = (IDiameterAVP) col.get(i);
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
	}

	/**
	 * Return AVP Objectas as per AvpCode passed as parameter.
	 * 
	 * @param iAvpCode
	 */
	
	/**
	 * return collection of vendor specific attributes for vendorparameterid of
	 * specified vendorid
	 * 
	 * @param vendorID
	 * @return
	 */
	public List<IDiameterAVP> getVendorSpeficAvps(long vendorID, int avpCode) {
		return getAVPList(vendorID+":"+avpCode);
	}


	public IDiameterAVP getAVP(String strAvpId) {
		IDiameterAVP diameterAVP = getAVPFFromID(strAvpId,this.AVPMap);
		
		if(diameterAVP==null){
			String avpId = DiameterUtility.getAVPIdFromName(strAvpId);
			if(avpId!=null)
				 diameterAVP = getAVPFFromID(avpId,this.AVPMap);
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
		return remove(avp, AVPMap, col);
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


	public ArrayList getAttributes() {
		return null;
	}

	// /**
	// * @deprecated This method is deprecated.
	// * @see getAvp(int)
	// */
	// public IPacketAttribute getAttribute(int id) {
	// IPacketAttribute ia = null;
	// return ia;
	// }
	//	
	// /**
	// * @deprecated Thid method is deprecated.
	// * @see getAvp(String)
	// */
	// public IPacketAttribute getAttribute(String id) {
	// IPacketAttribute ia = null;
	// return ia;
	// }

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
	}

	public void setProxiableBit() {
		this.bFlag |= 0x40;
	}

	public void setErrorBit() {
		this.bFlag |= 0x20;
	}

	public void setReTransmittedBit() {
		this.bFlag |= 0x10;
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
	}

	public void resetProxiableBit() {
		bFlag = (byte) resetBit(bFlag, 7);
	}

	public void resetErrorBit() {
		bFlag = (byte) resetBit(bFlag, 6);
	}

	public void resetReTransmittedBit() {
		bFlag = (byte) resetBit(bFlag, 5);
	}

	public ArrayList getPacketAVPCodeList() {
		return packetAVPCodeList;
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
 
		out.print("\tVer=" + bVersion);
		out.printf(", Len=%04d", intLength);
		out.print(", Flags=[R=" + (isRequest()?1:0) + " P="+ (isProxiable()?1:0) + " E=" + (isError()?1:0) + " T="+ (isReTransmitted()?1:0) + "]");
		out.print(", CMD="+ CommandCode.getDisplayName(intCommandCode) );
		if(isRequest())
			out.print("R");
		else
			out.print("A");
		out.print("(" + intCommandCode + ")");
		out.print(", App="+ ApplicationIdentifier.getDisplayName(intApplicationId));
		out.print("(" + intApplicationId + ")");
		out.print(", H2H=" + DiameterUtility.bytesToHex(DiameterUtility.intToByteArray(intHopByHopIdentifier)));
		out.print(", E2E=" + DiameterUtility.bytesToHex(DiameterUtility.intToByteArray(intEndToEndIdentifier)));
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

/*	public Object clone() throws CloneNotSupportedException {
		DiameterPacket clonePacket = null;
		clonePacket = (DiameterPacket) super.clone();
		clonePacket.packetAVPCodeList = new ArrayList<String>();
		clonePacket.AVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		clonePacket.col = new ArrayList<IDiameterAVP>();
		final int listSize = this.col.size();
		IDiameterAVP diameterAttribute;
		ArrayList<IDiameterAVP> values;
		for (int i = 0; i < listSize; i++) {
			diameterAttribute= (IDiameterAVP) this.col.get(i).clone();
			clonePacket.col.add(diameterAttribute);
			
			values = (ArrayList<IDiameterAVP>) clonePacket.AVPMap.get(diameterAttribute.getAVPId());

			if (values == null) {
				values = new ArrayList<IDiameterAVP>();
				clonePacket.AVPMap.put(diameterAttribute.getAVPId(), values);
			}
			clonePacket.addSubAvp(diameterAttribute, diameterAttribute.getAVPId());
			values.add(diameterAttribute);
			clonePacket.packetAVPCodeList.add(diameterAttribute.getAVPId());
			
		}

		return clonePacket;
	}
*/
	public void resetDiameterPacket() {
		AVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		col = new ArrayList<IDiameterAVP>();
		packetAVPCodeList = new ArrayList<String>();
		bVersion = 1;

		// setProxiableBit();
		setRequestBit();
	}

	public void refreshPacketHeader() {
		int length=DEFAULT_DIAMETER_PACKET_LENGTH;
		if (col != null) {
			IDiameterAVP attr;
			for (int i = 0; i < col.size(); i++) {
				attr = col.get(i);
				attr.refreshAVPHeader();
				length += attr.getLength();
				length += attr.getPaddingLength();
			}
		}
		this.intLength = length;
	}
	
	public void refreshInfoPacketHeader(){
		int length=0;
		if (this.infoAVPList != null) {
			IDiameterAVP infoAvp;
			int numberOfInfoAvps = this.infoAVPList.size(); 
			for (int i = 0; i < numberOfInfoAvps; i++) {
				infoAvp = infoAVPList.get(i); 
				infoAvp.refreshAVPHeader();
				length += infoAvp.getLength();
				length += infoAvp.getPaddingLength();
			}
		}
		this.infoLength = length;
	}
	
	public String getDestinationHost() {
		ArrayList<IDiameterAVP> diameterAVPs = AVPMap.get(DiameterAVPConstants.DESTINATION_HOST);
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
	
	public Object clone() throws CloneNotSupportedException {
		DiameterPacket clonePacket = null;
		clonePacket = (DiameterPacket) super.clone();
		clonePacket.col = new ArrayList<IDiameterAVP>();
		clonePacket.AVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();
		clonePacket.infoAVPList = new ArrayList<IDiameterAVP>();
		clonePacket.infoAVPMap = new HashMap<String, ArrayList<IDiameterAVP>>();

		final int numOfAvp = col.size();
		for(int i=0;i<numOfAvp;i++) {
			IDiameterAVP diameterAVP = (IDiameterAVP) this.col.get(i).clone(); 
			clonePacket.col.add(diameterAVP);
			ArrayList<IDiameterAVP> values = clonePacket.AVPMap.get(diameterAVP.getAVPId());
			if (values == null){
				values = new ArrayList<IDiameterAVP>();
				clonePacket.AVPMap.put(diameterAVP.getAVPId(),values);
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
	
	public abstract void setParameter(String key, Object parameterValue);
	
	public abstract Object getParameter(String str);

	public List<IDiameterAVP> getInfoAVPList() {
		return infoAVPList;
	}
	
}