package com.elitecore.coreradius.commons.packet;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.*;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusPacketTypeConstant;

import java.io.*;
import java.util.*;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

public class RadiusPacket extends BaseRadiusPacket{
	
	private static final long serialVersionUID = 1L;
	private String clientIP;
	private int clientPort;
	 
	private int packetType;
	private int identifier;
	private int length;
	private int infoLength;
	private byte[] authenticator;
	private LinkedHashMap<String,ArrayList<IRadiusAttribute>> attributeMap;
	private LinkedHashMap<String,ArrayList<IRadiusAttribute>> infoAttributeMap;
	private ArrayList<IRadiusAttribute> attributeList;
	private ArrayList<IRadiusAttribute> infoAttributeList;
	
	private static final String MODULE = "Radius Packet";
	
	private static final int DEFAULT_ATTRIBUTE_MAP_SIZE = 60;
	public static final int AUTHENTICATOR_LENGTH = 16;
	public static final int DEFAULT_RADIUS_PACKET_LENGTH = 20;
	
	public RadiusPacket() {
		authenticator = new byte[16];
		attributeMap = new  LinkedHashMap<String,ArrayList<IRadiusAttribute>>(DEFAULT_ATTRIBUTE_MAP_SIZE);
		infoAttributeMap = new  LinkedHashMap<String,ArrayList<IRadiusAttribute>>(DEFAULT_ATTRIBUTE_MAP_SIZE);
		attributeList = new ArrayList<IRadiusAttribute>(DEFAULT_ATTRIBUTE_MAP_SIZE);
		infoAttributeList = new ArrayList<IRadiusAttribute>(DEFAULT_ATTRIBUTE_MAP_SIZE);
	}
	
	/**
	 * 
	 * 
	 * @return total bytes read from the stream. May not return the actual bytes read in 
	 * 		   case the stream is continuous, means the stream contains more than one packet.
	 * 
	 */
	public int readFrom(InputStream sourceStream) throws IOException {
		packetType = sourceStream.read() & 0xFF;
		identifier = sourceStream.read();
		length = sourceStream.read();
		length = (length << 8) | (sourceStream.read() & 0xFF);
		sourceStream.read(authenticator);
		int attributeType = 0;
		int totalBytes = DEFAULT_RADIUS_PACKET_LENGTH;
		
		// Greater than 0 condition handles both cases - end of stream and end of packet data.
		while ((attributeType = sourceStream.read()) != -1 && totalBytes < length) {
			IRadiusAttribute radiusAttribute = Dictionary.getInstance().getAttribute(attributeType);
			radiusAttribute.setType(attributeType);
			totalBytes++; // to increase the attribute type read.
			totalBytes+= radiusAttribute.readLengthOnwardsFrom(sourceStream);
			
			ArrayList<IRadiusAttribute> values = attributeMap.get(String.valueOf(attributeType));
			if (values == null){
				values = new ArrayList<IRadiusAttribute>();
				attributeMap.put(String.valueOf(attributeType),values);
			}
			values.add(radiusAttribute);
			attributeList.add(radiusAttribute);
		}
		//added the header part size also.
		return totalBytes;
	}
	
	public void writeTo(OutputStream destinationStream) throws IOException {
		writeTo(destinationStream,false);
	}

	public void writeTo(OutputStream destinationStream,boolean bIncludeInfoAttr) throws IOException {
		destinationStream.write((byte)packetType);
		destinationStream.write((byte)identifier);
		int totalLength = this.length; 
		if(bIncludeInfoAttr)
			totalLength +=infoLength;
		byte [] lengthArray = { (byte)((totalLength >>> 8) & 0xFF), ((byte)(totalLength & 0xFF))};
		destinationStream.write(lengthArray);
		destinationStream.write(authenticator);
		
		final int listSize = attributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute = attributeList.get(i);
			destinationStream.write(radiusAttribute.getBytes());
		}
		
		if(bIncludeInfoAttr){
			final int infolistSize = infoAttributeList.size();
			for(int i=0;i<infolistSize;i++) {
				IRadiusAttribute radiusAttribute = infoAttributeList.get(i);
				destinationStream.write(radiusAttribute.getBytes());
			}
		}
	}

	public byte[] getBytes() {
		return getBytes(false);
	}

	public byte[] getBytes(boolean bIncludeInfoAttr) {
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			writeTo(buffer,bIncludeInfoAttr);
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
		}
		
		return buffer.toByteArray();
	}

	public void setBytes(byte[] value) {
		attributeList = new ArrayList<IRadiusAttribute>(DEFAULT_ATTRIBUTE_MAP_SIZE);
		attributeMap = new  LinkedHashMap<String, ArrayList<IRadiusAttribute>>(DEFAULT_ATTRIBUTE_MAP_SIZE);
		ByteArrayInputStream in = new ByteArrayInputStream(value);
		try {
			readFrom(in);
		}catch(IOException ioExp){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Error while reading packet data, reason : " + ioExp);
			LogManager.getLogger().trace(MODULE, ioExp);
			this.packetType = 0;
			this.identifier = 0;
			this.length = 0;
			this.authenticator = new byte [16];
			this.attributeMap = new  LinkedHashMap<String, ArrayList<IRadiusAttribute>>(DEFAULT_ATTRIBUTE_MAP_SIZE);
			attributeList = new ArrayList<IRadiusAttribute>(DEFAULT_ATTRIBUTE_MAP_SIZE);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<IRadiusAttribute> getRadiusAttributes() {
		return (ArrayList<IRadiusAttribute>) attributeList.clone();
	}

	
	@SuppressWarnings("unchecked")
	public ArrayList<IRadiusAttribute> getRadiusInfoAttributes() {
		return (ArrayList<IRadiusAttribute>) infoAttributeList.clone();
	}

	public ArrayList<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr) {
		ArrayList<IRadiusAttribute> attrList = getRadiusAttributes();
		if(!bIncludeInfoAttr)
			return attrList;
		
		if(attrList != null && !attrList.isEmpty()){
			ArrayList<IRadiusAttribute> infoArrayList = getRadiusInfoAttributes();
			if(infoArrayList != null && !infoArrayList.isEmpty())
				attrList.addAll(infoArrayList);
		}else{
			attrList = getRadiusInfoAttributes();
		}
		
		return attrList;
	}

	
    public Map<String, ArrayList<IRadiusAttribute>> getAttributeMap(){
        return attributeMap;
    }

	public IRadiusAttribute getRadiusAttribute(int id) {
		return getRadiusAttribute(RadiusConstants.STANDARD_VENDOR_ID,id);
	}

	public IRadiusAttribute getRadiusInfoAttribute(int id) {
		return getRadiusInfoAttribute(RadiusConstants.STANDARD_VENDOR_ID,id);
	}

	public IRadiusAttribute getRadiusAttribute(long vendorId, int ...attrId){
		if(vendorId == 0){
			if(attrId != null && attrId.length > 0){
				ArrayList<IRadiusAttribute> attributeCollection = attributeMap.get(String.valueOf(attrId[0]));
				if (attributeCollection != null && !attributeCollection.isEmpty()) {
					IRadiusAttribute attribute = attributeCollection.get(0);
					if (attribute != null)
						return attribute;
				}
				return null;
			}else
				return null;
		}
		else{
			return getVendorSpecificAttribute(vendorId, attrId);
		}
	}

	public IRadiusAttribute getRadiusInfoAttribute(long vendorId, int ...attrId){
		if(vendorId == 0){
			if(attrId != null && attrId.length > 0){
				ArrayList<IRadiusAttribute> attributeCollection = infoAttributeMap.get(String.valueOf(attrId[0]));
				if (attributeCollection != null && !attributeCollection.isEmpty()) {
					return attributeCollection.get(0);
				}
				return null;
			}else
				return null;
		}
		else{
			return getVendorSpecificInfoAttribute(vendorId, attrId);
		}
	}

	public IRadiusAttribute getRadiusAttribute(int id,boolean bIncludeInfoAttr) {
		IRadiusAttribute radiusAttr = getRadiusAttribute(RadiusConstants.STANDARD_VENDOR_ID,id);
		if(!bIncludeInfoAttr)
			return radiusAttr;
		if(radiusAttr == null)
			radiusAttr = getRadiusInfoAttribute(RadiusConstants.STANDARD_VENDOR_ID,id);
		return radiusAttr;
	}

	public IRadiusAttribute getRadiusAttribute(long vendorId,boolean bIncludeInfoAttr, int ...attrId){
		IRadiusAttribute radiusAttr = getRadiusAttribute(vendorId,attrId);
		if(!bIncludeInfoAttr)
			return radiusAttr;
		if(radiusAttr == null)
			radiusAttr = getRadiusInfoAttribute(vendorId,attrId);
		return radiusAttr;
	}


	public ArrayList<IRadiusAttribute> getRadiusAttributes(long vendorId, int ...attrIds ){
		if(vendorId == 0)
			return getRadiusAttributes(attrIds[0]);
		else
			return getVendorSpecificAttributes(vendorId, attrIds);
	}

	public ArrayList<IRadiusAttribute> getRadiusInfoAttributes(long vendorId, int ...attrIds ){
		if(vendorId == 0)
			return getRadiusInfoAttributes(attrIds[0]);
		else
			return getVendorSpecificInfoAttributes(vendorId, attrIds);
	}

	public ArrayList<IRadiusAttribute> getRadiusAttributes(long vendorId,boolean bIncludeInfoAttr, int ...attrIds ){
		ArrayList<IRadiusAttribute> collection = getRadiusAttributes(vendorId,attrIds);
		if(!bIncludeInfoAttr)
			return collection;
		
		if(collection != null && !collection.isEmpty()){
			Collection<IRadiusAttribute> infoCollection = getRadiusInfoAttributes(vendorId,attrIds);
			if(infoCollection != null && !infoCollection.isEmpty())
				collection.addAll(infoCollection);
		}else{
			collection = getRadiusInfoAttributes(vendorId,attrIds);
		}
		return collection;
	}

	public IRadiusAttribute getRadiusAttribute(String id) {
		return getRadiusAttribute(id,false);
	}
	
	public IRadiusAttribute getRadiusAttribute(String id,boolean bIncludeInfoAttr) {
		AttributeId attrId;
		try {
			attrId = Dictionary.getInstance().getAttributeId(id);
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().trace(MODULE, e);
			return null;
		}
		return getRadiusAttribute(attrId.getVendorId(),bIncludeInfoAttr, attrId.getAttrbuteId());
	}

	@SuppressWarnings("unchecked")
	public ArrayList<IRadiusAttribute> getRadiusAttributes(int id){
		ArrayList<IRadiusAttribute> attributeCollection = attributeMap.get(String.valueOf(id));
		if (attributeCollection != null && !attributeCollection.isEmpty()) {
			return (ArrayList<IRadiusAttribute>) attributeCollection.clone();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<IRadiusAttribute> getRadiusInfoAttributes(int id){
		ArrayList<IRadiusAttribute> attributeCollection = infoAttributeMap.get(String.valueOf(id));
		if (attributeCollection != null && !attributeCollection.isEmpty()) {
			return (ArrayList<IRadiusAttribute>) attributeCollection.clone();
		}
		return null;
	}

	public ArrayList<IRadiusAttribute> getRadiusAttributes(int id,boolean bIncludeInfoAttr){		
		ArrayList<IRadiusAttribute> attributeCollection = getRadiusAttributes(id);
		if(!bIncludeInfoAttr)
			return attributeCollection;
		
		if (attributeCollection != null && !attributeCollection.isEmpty()) {
			Collection<IRadiusAttribute> infoAttributeCollection = getRadiusInfoAttributes(id);
			if(infoAttributeCollection != null && !infoAttributeCollection.isEmpty()){
				attributeCollection.addAll(infoAttributeCollection);
			}
			return attributeCollection; 
		}else{
			attributeCollection = getRadiusInfoAttributes(id);
		}
		
		return attributeCollection;
	}

	public ArrayList<IRadiusAttribute> getRadiusAttributes(String id){
		AttributeId attrId = null;
		try {
			attrId = Dictionary.getInstance().getAttributeId(id);
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		if(attrId != null){
			if(attrId.getVendorId() == RadiusConstants.STANDARD_VENDOR_ID){
				return getRadiusAttributes(attrId.getAttrbuteId()[0]);
			}else{
				return getVendorSpecificAttributes(attrId.getVendorId(), attrId.getAttrbuteId());
			}
		}
		return null;	
	}
	
	public ArrayList<IRadiusAttribute> getRadiusInfoAttributes(String id){
		AttributeId attrId = null;
		try {
			attrId = Dictionary.getInstance().getAttributeId(id);
		} catch (InvalidAttributeIdException e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		if(attrId != null){
			if(attrId.getVendorId() == RadiusConstants.STANDARD_VENDOR_ID){
				return getRadiusInfoAttributes(attrId.getAttrbuteId()[0]);
			}else{
				return getVendorSpecificInfoAttributes(attrId.getVendorId(), attrId.getAttrbuteId());
			}
		}
		return null;	
	}

	public ArrayList<IRadiusAttribute> getRadiusAttributes(String id,boolean bIncludeInfoAttr){
		ArrayList<IRadiusAttribute> collection = getRadiusAttributes(id);
		if(!bIncludeInfoAttr)
			return collection;
		
		if(collection != null && !collection.isEmpty()){
			Collection<IRadiusAttribute> infoCollection = getRadiusInfoAttributes(id);
			if(infoCollection != null && !infoCollection.isEmpty()){
				collection.addAll(infoCollection);
			}
		}else{
			collection = getRadiusInfoAttributes(id);
		}
		return collection;	
	}

	/**
	 * parameter to be provided vendorid:vendorparameterid in <code>id</code>
	 * return collection of vendor specific attributes for  
	 * vendorparameterid of specified vendorid
	 * <code>id</code> 
	 * @param id  id will contain string id in 
	 * form of  vendorid:vendorparameterid
	 * example for Resource-Manager AVPair of vendor 
	 * Elitecore can be represented as 21067:5
	 * @return 
	 */
	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(String id) throws NumberFormatException {
		ArrayList<IRadiusAttribute> retVSAAttributeList =  null;
		if(id != null && id.indexOf(':')!= -1){
			StringTokenizer stringTokenizer = null;
			String strVendorID = null;
			String strVendorAttributeID = null;
			long lVendorID = -1;
			int iVendorAttributeID = -1;
			stringTokenizer = new StringTokenizer(id,":");
			strVendorID = stringTokenizer.nextToken();
			if(stringTokenizer.hasMoreTokens()){
				strVendorAttributeID = stringTokenizer.nextToken();
			}
			lVendorID          = Long.parseLong(strVendorID);
			iVendorAttributeID = Integer.parseInt(strVendorAttributeID);
			retVSAAttributeList = getVendorSpeficAttributes(lVendorID,iVendorAttributeID);
		}	
		return retVSAAttributeList;
	}

	public ArrayList<IRadiusAttribute> getVendorSpeficInfoAttributes(String id) throws NumberFormatException {
		ArrayList<IRadiusAttribute> retVSAAttributeList =  null;
		if(id != null && id.indexOf(':')!= -1){
			StringTokenizer stringTokenizer = null;
			String strVendorID = null;
			String strVendorAttributeID = null;
			long lVendorID = -1;
			int iVendorAttributeID = -1;

			stringTokenizer = new StringTokenizer(id,":");
			strVendorID = stringTokenizer.nextToken();
			if(stringTokenizer.hasMoreTokens()){
				strVendorAttributeID = stringTokenizer.nextToken();
			}
			lVendorID          = Long.parseLong(strVendorID);
			iVendorAttributeID = Integer.parseInt(strVendorAttributeID);
			retVSAAttributeList = getVendorSpeficInfoAttributes(lVendorID,iVendorAttributeID);
		}	
		return retVSAAttributeList;
	}

	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(String id, boolean bIncludeInfoAttr) throws NumberFormatException {
		ArrayList<IRadiusAttribute> collection = getVendorSpeficAttributes(id);
		if(!bIncludeInfoAttr) {
			return collection;
		}

		if(collection != null && !collection.isEmpty()){
			Collection<IRadiusAttribute> infoCollection = getVendorSpeficInfoAttributes(id);
			if(infoCollection != null && !infoCollection.isEmpty()) {
				collection.addAll(infoCollection);
			}
		}else{
			collection = getVendorSpeficInfoAttributes(id);
		}
		return collection;
	}

	/**
	 * return collection of vendor specific attributes for  
	 * vendorparameterid of specified vendorid
	 * 
	 * @param strVendorID strVendorID should be vendorId String 
	 * like "21067"
	 * @param strAttributeID strAttributeID should be Attribute Id String 
	 * like "5"
	 * @return
	 */
	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(String strVendorID, String strAttributeID) throws NumberFormatException{
		ArrayList<IRadiusAttribute> retVSAAttributeList =  null;
		long lVendorID = -1;
		int iVendorAttributeID = -1;
		lVendorID          = Long.parseLong(strVendorID);
		iVendorAttributeID = Integer.parseInt(strAttributeID);
		retVSAAttributeList = getVendorSpeficAttributes(lVendorID, iVendorAttributeID);
		return retVSAAttributeList;
	}

	public ArrayList<IRadiusAttribute> getVendorSpeficInfoAttributes(String strVendorID, String strAttributeID) throws NumberFormatException{
		ArrayList<IRadiusAttribute> retVSAAttributeList =  null;
		long lVendorID = -1;
		int iVendorAttributeID = -1;
		lVendorID          = Long.parseLong(strVendorID);
		iVendorAttributeID = Integer.parseInt(strAttributeID);
		retVSAAttributeList = getVendorSpeficInfoAttributes(lVendorID, iVendorAttributeID);
		return retVSAAttributeList;
	}

	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(String strVendorID, String strAttributeID, boolean bIncludeInfoAttr) throws NumberFormatException{
		ArrayList<IRadiusAttribute> collection = getVendorSpeficAttributes(strVendorID,strAttributeID);
		if(!bIncludeInfoAttr)
			return collection;
		if(collection != null && !collection.isEmpty()){
			Collection<IRadiusAttribute> infoCollection = getVendorSpeficInfoAttributes(strVendorID,strAttributeID);
			if(infoCollection != null && !infoCollection.isEmpty())
				collection.addAll(infoCollection);
		}else{
			collection = getVendorSpeficInfoAttributes(strVendorID,strAttributeID);
		}
		return collection;
	}

	/**
	 * return collection of vendor specific attributes for  
	 * vendorparameterid of specified vendorid
	 * 
	 * @param vendorID 
	 * @param attributeID
	 * @return
	 */
	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(long vendorID, int attributeID){
		ArrayList<IRadiusAttribute> retVSAAttributeList =  null;
		ArrayList<IRadiusAttribute> vsaArrayList = null;
		ArrayList<IRadiusAttribute> vsaAttributeList =  null;
		vsaArrayList = attributeMap.get(String.valueOf(RadiusAttributeConstants.VENDOR_SPECIFIC));
		if(vsaArrayList != null){
			final int listSize = vsaArrayList.size();
			for(int i=0;i<listSize;i++) {
				VendorSpecificAttribute vendorSpecificAttribute = (VendorSpecificAttribute)vsaArrayList.get(i);
				if(vendorSpecificAttribute.getVendorID() == vendorID ){
					vsaAttributeList = (ArrayList<IRadiusAttribute>)vendorSpecificAttribute.getAttributes(attributeID);
					if(vsaAttributeList !=null){
						if(retVSAAttributeList == null){
							retVSAAttributeList = new ArrayList<IRadiusAttribute>();
						}
						retVSAAttributeList.addAll(vsaAttributeList);
					}
				}
			}
		}
		return retVSAAttributeList;
	}

	public ArrayList<IRadiusAttribute> getVendorSpeficInfoAttributes(long vendorID, int attributeID){
		ArrayList<IRadiusAttribute> retVSAAttributeList =  null;
		ArrayList<IRadiusAttribute> vsaArrayList = null;
		ArrayList<IRadiusAttribute> vsaAttributeList =  null;
		vsaArrayList = infoAttributeMap.get(String.valueOf(RadiusAttributeConstants.VENDOR_SPECIFIC));
		if(vsaArrayList != null){
			final int listSize = vsaArrayList.size();
			for(int i=0;i<listSize;i++) {
				VendorSpecificAttribute vendorSpecificAttribute = (VendorSpecificAttribute)vsaArrayList.get(i);
				if(vendorSpecificAttribute.getVendorID() == vendorID ){
					vsaAttributeList = (ArrayList<IRadiusAttribute>)vendorSpecificAttribute.getAttributes(attributeID);
					if(vsaAttributeList !=null){
						if(retVSAAttributeList == null){
							retVSAAttributeList = new ArrayList<IRadiusAttribute>();
						}
						retVSAAttributeList.addAll(vsaAttributeList);
					}
				}
			}
		}
		return retVSAAttributeList;
	}

	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(long vendorID, int attributeID, boolean bIncludeInfoAttr){
		ArrayList<IRadiusAttribute> collection = getVendorSpeficAttributes(vendorID,attributeID);
		if(!bIncludeInfoAttr)
			return collection;
		if(collection != null && !collection.isEmpty()){
			ArrayList<IRadiusAttribute> infoCollection = getVendorSpeficInfoAttributes(vendorID,attributeID);
			if(infoCollection != null && !infoCollection.isEmpty())
				collection.addAll(infoCollection);
		}else{
			collection = getVendorSpeficInfoAttributes(vendorID,attributeID);
		}
		return collection;
	}

	public IRadiusAttribute getVendorSpecificAttribute(long vendorID, int ... attributeIds){
		ArrayList<IRadiusAttribute> vsaArrayList = null;
		IRadiusAttribute radiusAttribute = null;
		vsaArrayList = attributeMap.get(String.valueOf(RadiusAttributeConstants.VENDOR_SPECIFIC));
		if(vsaArrayList != null){
			final int listSize = vsaArrayList.size();
			for(int i=0;i<listSize;i++) {
				VendorSpecificAttribute vendorSpecificAttribute = (VendorSpecificAttribute)vsaArrayList.get(i);
				if(vendorSpecificAttribute.getVendorID() == vendorID ){
					radiusAttribute = vendorSpecificAttribute.getAttribute(attributeIds);
					if(radiusAttribute != null)
						break;
				}
			}
		}
		return radiusAttribute;
	}

	public IRadiusAttribute getVendorSpecificInfoAttribute(long vendorID, int ... attributeIds){
		ArrayList<IRadiusAttribute> vsaArrayList = null;
		IRadiusAttribute radiusAttribute = null;
		vsaArrayList = infoAttributeMap.get(String.valueOf(RadiusAttributeConstants.VENDOR_SPECIFIC));
		if(vsaArrayList != null){
			final int listSize = vsaArrayList.size();
			for(int i=0;i<listSize;i++) {
				VendorSpecificAttribute vendorSpecificAttribute = (VendorSpecificAttribute)vsaArrayList.get(i);
				if(vendorSpecificAttribute.getVendorID() == vendorID ){
					radiusAttribute = vendorSpecificAttribute.getAttribute(attributeIds);
					if(radiusAttribute != null)
						break;
				}
			}
		}
		return radiusAttribute;
	}

	public IRadiusAttribute getVendorSpecificAttribute(long vendorID, boolean bIncludeInfoAttr, int ... attributeIds){
		IRadiusAttribute radiusAttr = getVendorSpecificAttribute(vendorID,attributeIds);
		if(!bIncludeInfoAttr)
			return radiusAttr;
		if(radiusAttr == null)
			radiusAttr = getVendorSpecificInfoAttribute(vendorID,attributeIds);
		return radiusAttr;
	}

	public ArrayList<IRadiusAttribute> getVendorSpecificAttributes(long vendorID, int ... attributeIds){
		ArrayList<IRadiusAttribute> retVSAAttributeList = null;
		ArrayList<IRadiusAttribute> vsaArrayList = null;
		ArrayList<IRadiusAttribute> radiusAttributeList = null;
		vsaArrayList = attributeMap.get(String.valueOf(RadiusAttributeConstants.VENDOR_SPECIFIC));
		if(vsaArrayList != null){
			final int listSize = vsaArrayList.size();
			for(int i=0;i<listSize;i++) {
				VendorSpecificAttribute vendorSpecificAttribute = (VendorSpecificAttribute)vsaArrayList.get(i);
				if(vendorSpecificAttribute.getVendorID() == vendorID ){
					radiusAttributeList = (ArrayList<IRadiusAttribute>)vendorSpecificAttribute.getAttributes(attributeIds);
					if(radiusAttributeList !=null){
						if(retVSAAttributeList == null){
							retVSAAttributeList = new ArrayList<IRadiusAttribute>();
						}
						retVSAAttributeList.addAll(radiusAttributeList);
					}
				}
			}
		}
		return retVSAAttributeList;
	}

	public ArrayList<IRadiusAttribute> getVendorSpecificInfoAttributes(long vendorID, int ... attributeIds){
		ArrayList<IRadiusAttribute> retVSAAttributeList = null;
		ArrayList<IRadiusAttribute> vsaArrayList = null;
		ArrayList<IRadiusAttribute> radiusAttributeList = null;
		vsaArrayList = infoAttributeMap.get(String.valueOf(RadiusAttributeConstants.VENDOR_SPECIFIC));
		if(vsaArrayList != null){
			final int listSize = vsaArrayList.size();
			for(int i=0;i<listSize;i++) {
				VendorSpecificAttribute vendorSpecificAttribute = (VendorSpecificAttribute)vsaArrayList.get(i);
				if(vendorSpecificAttribute.getVendorID() == vendorID ){
					radiusAttributeList = (ArrayList<IRadiusAttribute>)vendorSpecificAttribute.getAttributes(attributeIds);
					if(radiusAttributeList !=null){
						if(retVSAAttributeList == null){
							retVSAAttributeList = new ArrayList<IRadiusAttribute>();
						}
						retVSAAttributeList.addAll(radiusAttributeList);
					}
				}
			}
		}
		return retVSAAttributeList;
	}

	public ArrayList<IRadiusAttribute> getVendorSpecificAttributes(long vendorID,boolean bIncludeInfoAttr, int ... attributeIds){
		ArrayList<IRadiusAttribute> collection = getVendorSpecificAttributes(vendorID,attributeIds);
		if(!bIncludeInfoAttr)
			return collection;
		
		if(collection != null && !collection.isEmpty()){
			ArrayList<IRadiusAttribute> infoCollection = getVendorSpecificAttributes(vendorID,attributeIds);
			if(infoCollection != null && !infoCollection.isEmpty())
				collection.addAll(infoCollection);
		}else{
			collection = getVendorSpecificInfoAttributes(vendorID,attributeIds);			
		}
		return collection;
	}

	/**
	 * return collection of vendor specific attributes 
	 * 
	 * @param vendorID 
	 * @param attributeID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(){
		 ArrayList<IRadiusAttribute> vendorSpecificAttributes = attributeMap.get(String.valueOf(RadiusAttributeConstants.VENDOR_SPECIFIC));
		 if(vendorSpecificAttributes != null){
			 vendorSpecificAttributes = (ArrayList<IRadiusAttribute>) vendorSpecificAttributes.clone();
	}
		 return vendorSpecificAttributes;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<IRadiusAttribute> getVendorSpeficInfoAttributes(){
		ArrayList<IRadiusAttribute> vendorSpecificAttributes = infoAttributeMap.get(String.valueOf(RadiusAttributeConstants.VENDOR_SPECIFIC));
		if(vendorSpecificAttributes != null){
			 vendorSpecificAttributes = (ArrayList<IRadiusAttribute>) vendorSpecificAttributes.clone();
	}
		 return vendorSpecificAttributes;
	}

	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(boolean bIncludeInfoAttr){
		ArrayList<IRadiusAttribute> collection = getVendorSpeficAttributes();
		if(!bIncludeInfoAttr)
			return collection;
		if(collection != null && !collection.isEmpty()){
			ArrayList<IRadiusAttribute> infoCollection = getVendorSpeficInfoAttributes();
			if(infoCollection != null && !infoCollection.isEmpty())
				collection.addAll(infoCollection);
		}else{
			collection = getVendorSpeficInfoAttributes();
		}
			
		return collection;
	}

	public int getLength() {
		return this.length;
	}
	
	public byte[] getAuthenticator() {
		return authenticator;
	}
	
	public void setAuthenticator(byte[] authenticator) {
		if(authenticator != null){
			if(authenticator.length >= AUTHENTICATOR_LENGTH){
				this.authenticator = new byte[AUTHENTICATOR_LENGTH];
				System.arraycopy(authenticator, 0, this.authenticator, 0, AUTHENTICATOR_LENGTH);
			}else{
				this.authenticator = new byte[AUTHENTICATOR_LENGTH];
				System.arraycopy(authenticator, 0, this.authenticator, 0, authenticator.length);				
			}
		}else{
			this.authenticator = new byte[AUTHENTICATOR_LENGTH];
		}
	}
	
	public String getClientIP() {
		return clientIP;
	}
	
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	
	public int getClientPort() {
		return clientPort;
	}
	
	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}
	
	public int getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}
	
	public int getPacketType() {
		return packetType;
	}
	
	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	private IRadiusAttribute wrapIntoVendorSpecificAttribute(IRadiusAttribute attribute, long vendorId){		
		// Received attribute is vendor specific attribute, need to wrap that attribute into VendorSpecific attribute			
		VendorSpecificAttribute vendorSpecificAttr = (VendorSpecificAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC);
		vendorSpecificAttr.setVendorID(vendorId);
		IVendorSpecificAttribute vsaType = Dictionary.getInstance().getVendorAttributeType(vendorId);
		vsaType.addAttribute(attribute);
		vsaType.refreshAttributeHeader();
		vendorSpecificAttr.setVendorTypeAttribute(vsaType);
		vendorSpecificAttr.refreshAttributeHeader();
		return vendorSpecificAttr;
	}
	
	public void addAttribute(IRadiusAttribute attribute){		
		addAttribute(attribute,attribute.getVendorID());
	}

	public void addAttribute(IRadiusAttribute attribute,long vendorId){
		IRadiusAttribute tempAttribute = attribute;
		
		if(tempAttribute.getLevel() > 1){
			tempAttribute = wrapInParentAttributes(tempAttribute);
		}
		
		//check for the vendorSpecific attribute
		if(vendorId != 0 && !attribute.isVendorSpecific()){
			tempAttribute = wrapIntoVendorSpecificAttribute(tempAttribute, vendorId);
		}
		String  id       = String.valueOf(tempAttribute.getType());
		ArrayList<IRadiusAttribute> attributeCollection = attributeMap.get(id);
		if (attributeCollection == null ) {
			attributeCollection = new ArrayList<IRadiusAttribute>();
			attributeMap.put(id,attributeCollection);
		}
		attributeCollection.add(tempAttribute);
		attributeList.add(tempAttribute);
	}
	
	private IRadiusAttribute wrapInParentAttributes(IRadiusAttribute childAttribute){
		IRadiusAttribute originalAttribute = null;
		try {
			 originalAttribute = (IRadiusAttribute) childAttribute.clone();
		} catch (CloneNotSupportedException e) {
			LogManager.ignoreTrace(e);
			return childAttribute;
		}
		
		try{
			for(int i = childAttribute.getLevel() - 1; i > 0 ; i--){
				AttributeId attributeId = Dictionary.getInstance().getAttributeId(childAttribute.getParentId());
				int[] parentAttributeID = Arrays.copyOf(attributeId.getAttrbuteId(), attributeId.getAttrbuteId().length - 1);
				IRadiusGroupedAttribute parentAttribute = (IRadiusGroupedAttribute)Dictionary.getInstance().getKnownAttribute(childAttribute.getVendorID(),parentAttributeID);
				if(parentAttribute == null){
					return originalAttribute;
				}
				parentAttribute.addTLVAttribute(childAttribute);
				childAttribute = parentAttribute;
			}
			return childAttribute;
		}catch(InvalidAttributeIdException ex){
			LogManager.ignoreTrace(ex);
			return originalAttribute;
		}
	}
	public void removeAttribute(IRadiusAttribute attribute){
		
		if(attribute!=null){
			IRadiusAttribute radiusAttribute = attribute;
			if (attribute.getLevel() > 1) {
				radiusAttribute = wrapInParentAttributes(radiusAttribute);
			}
			
			if(attribute.getVendorID() !=0 && !attribute.isVendorSpecific()){
				radiusAttribute = wrapIntoVendorSpecificAttribute(radiusAttribute, radiusAttribute.getVendorID());
			}
			if(attributeList.contains(radiusAttribute)){
				attributeList.remove(radiusAttribute);
				
				ArrayList<IRadiusAttribute> attributeList = attributeMap.get(String.valueOf(radiusAttribute.getType()));
				if(attributeList !=null && !attributeList.isEmpty()){
					attributeList.remove(radiusAttribute);
					if(attributeList.isEmpty())
						attributeMap.remove(String.valueOf(radiusAttribute.getType()));
				}
			}
		}
	}
	
	public void removeInfoAttribute(IRadiusAttribute attribute){
		if(attribute!=null){
			IRadiusAttribute radiusAttribute = attribute;
			
			if(attribute.getVendorID() !=0 && !attribute.isVendorSpecific()){
				radiusAttribute = wrapIntoVendorSpecificAttribute(attribute, attribute.getVendorID());
			}
			if(infoAttributeList.contains(radiusAttribute)){
				infoAttributeList.remove(radiusAttribute);
				
				ArrayList<IRadiusAttribute> attributeList = infoAttributeMap.get(String.valueOf(radiusAttribute.getType()));
				if(attributeList !=null && !attributeList.isEmpty()){
					attributeList.remove(radiusAttribute);
					if(attributeList.isEmpty())
						infoAttributeMap.remove(String.valueOf(radiusAttribute.getType()));
				}
			}
		}
	}
	
	public void addInfoAttribute(IRadiusAttribute attribute){
		addInfoAttribute(attribute,attribute.getVendorID());
	}

	public void addInfoAttribute(IRadiusAttribute attribute,long vendorId){
		IRadiusAttribute tempAttribute = attribute;
		
		//check for the vendorSpecific attribute
		if(vendorId != 0 && !attribute.isVendorSpecific()){
			tempAttribute = wrapIntoVendorSpecificAttribute(attribute, vendorId);
		}

		String  id       = String.valueOf(tempAttribute.getType());
		ArrayList<IRadiusAttribute> attributeCollection = infoAttributeMap.get(id);
		if (attributeCollection == null ) {
			attributeCollection = new ArrayList<IRadiusAttribute>();
			infoAttributeMap.put(id,attributeCollection);
		}
		attributeCollection.add(tempAttribute);
		infoAttributeList.add(tempAttribute);
	}
	
	public void addAttributes(Collection<IRadiusAttribute> attributeCollection){
		if(attributeCollection != null){
			Iterator<IRadiusAttribute> itr = attributeCollection.iterator();
			while(itr.hasNext()){
				IRadiusAttribute attribute = itr.next();
				addAttribute(attribute);
			}
		}
	}

	public void addInfoAttributes(Collection<IRadiusAttribute> attributeCollection){
		if(attributeCollection != null){
			Iterator<IRadiusAttribute> itr = attributeCollection.iterator();
			while(itr.hasNext()){
				IRadiusAttribute attribute = itr.next();
				addInfoAttribute(attribute);
			}
		}
	}

	
	public void setAttribute(IRadiusAttribute attribute){
		String id = String.valueOf(attribute.getType());
		if(attributeMap.containsKey(id)){
			ArrayList<IRadiusAttribute> existingAttrColl = attributeMap.get(id);
			Iterator<IRadiusAttribute> existingAttrCollItr = existingAttrColl.iterator();
			while(existingAttrCollItr.hasNext()){
				IRadiusAttribute oldAttribute = existingAttrCollItr.next();
				if(oldAttribute.getType() == attribute.getType()){
					existingAttrColl.remove(oldAttribute);
					break;
				}
			}
		}
		
		Iterator<IRadiusAttribute> listItr = attributeList.iterator();
		while(listItr.hasNext()){
			IRadiusAttribute oldAttribute = listItr.next();
			if(oldAttribute.getType() == attribute.getType()){
				attributeList.remove(oldAttribute);
				break;
			}
		}
		
		addAttribute(attribute);
	}

	public void setInfoAttribute(IRadiusAttribute attribute){
		String id = String.valueOf(attribute.getType());
		if(infoAttributeMap.containsKey(id)){
			ArrayList<IRadiusAttribute> existingAttrColl = infoAttributeMap.get(id);
			Iterator<IRadiusAttribute> existingAttrCollItr = existingAttrColl.iterator();
			while(existingAttrCollItr.hasNext()){
				IRadiusAttribute oldAttribute = existingAttrCollItr.next();
				if(oldAttribute.getType() == attribute.getType()){
					existingAttrColl.remove(oldAttribute);
					break;
				}
			}
		}
		
		Iterator<IRadiusAttribute> listItr = infoAttributeList.iterator();
		while(listItr.hasNext()){
			IRadiusAttribute oldAttribute = listItr.next();
			if(oldAttribute.getType() == attribute.getType()){
				infoAttributeList.remove(oldAttribute);
				break;
			}
		}
		
		addInfoAttribute(attribute);
	}

	public void setAttribute(Collection<IRadiusAttribute> collection){
		Iterator<IRadiusAttribute> iterator = collection.iterator(); 
		while(iterator.hasNext()){
			IRadiusAttribute rAttribute = iterator.next();
			ArrayList<IRadiusAttribute> attributeCollection = attributeMap.get(String.valueOf(rAttribute.getType()));
			if(attributeCollection == null){
				attributeCollection = new ArrayList<IRadiusAttribute>();
				attributeMap.put(String.valueOf(rAttribute.getType()),attributeCollection);
			}
			attributeCollection.add(rAttribute);
			attributeList.add(rAttribute);
		}
	}

	public byte[] getAttributeBytes(){
		byte [] valueBytes = new byte[getLength() - DEFAULT_RADIUS_PACKET_LENGTH];
		int currentPosition = 0;
		final int listSize = attributeList.size();
		for(int i=0;i<listSize;i++) {
			byte [] attributeBytes = (attributeList.get(i)).getBytes();
			System.arraycopy( attributeBytes ,0, valueBytes, currentPosition, attributeBytes.length);
			currentPosition += attributeBytes.length; 
		}
		return valueBytes;
	}
	
	public void refreshPacketHeader(){
		int length = 0;

		for(Map.Entry<String, ArrayList<IRadiusAttribute>> entrySet:attributeMap.entrySet()) {
			for(IRadiusAttribute attribute:entrySet.getValue()) {
				if(attribute.isVendorSpecific()){
					((VendorSpecificAttribute)attribute).refreshAttributeHeader();
				}
				length = length + attribute.getLength();
			}
		}
		setLength(20 + length);
}

	public void refreshInfoPacketHeader(){
		Iterator<ArrayList<IRadiusAttribute>> iterator = infoAttributeMap.values().iterator();
		int length = 0;
		while(iterator.hasNext()){
			Iterator<IRadiusAttribute> innerIterator = iterator.next().iterator();
			while(innerIterator.hasNext()){
				IRadiusAttribute radiusAttribute = innerIterator.next();
				if(radiusAttribute.isVendorSpecific()){
					((VendorSpecificAttribute)radiusAttribute).refreshAttributeHeader();
				}
				length = length + radiusAttribute.getLength();
			}
		}
		this.infoLength = length;
	}
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.println();
		out.println("\t" + "Packet Type: " + RadiusPacketTypeConstant.from(getPacketType()));
		out.println("\t" + "Identifier: " + getIdentifier());
		out.println("\t" + "Length: " + getLength());
		out.println("\t" + "Authenticator: " + RadiusUtility.bytesToHex(authenticator));
		
		final int listSize = attributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute = attributeList.get(i);
			if(radiusAttribute.getType() != RadiusAttributeConstants.VENDOR_SPECIFIC){
				out.print("\t" + Dictionary.getInstance().getAttributeName(radiusAttribute.getType()));
			}
			out.println(radiusAttribute.toString());
		}
		if(getInfoLength() > 0)
			out.println("\t--Info Attributes");
		final int infoListSize = infoAttributeList.size();
		for(int i=0;i<infoListSize;i++) {
			IRadiusAttribute radiusAttribute = infoAttributeList.get(i);
			if(radiusAttribute.getType() != RadiusAttributeConstants.VENDOR_SPECIFIC){
				out.print("\t" + Dictionary.getInstance().getAttributeName(radiusAttribute.getType()));
			}
			out.println(radiusAttribute.toString());
		}
		out.flush();
		out.close();
		return stringBuffer.toString();
	}
	
	public IRadiusAttribute getRadAttribute(String id){
		final int listSize = attributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute = attributeList.get(i);
			if(radiusAttribute.getType() == Integer.parseInt(id)){
				return radiusAttribute;
			}
		}
		return null;
	}

	public IRadiusAttribute getRadInfoAttribute(String id){
		final int listSize = infoAttributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute = infoAttributeList.get(i);
			if(radiusAttribute.getType() == Integer.parseInt(id)){
				return radiusAttribute;
			}
		}
		return null;
	}

	public IRadiusAttribute getRadAttribute(String id, boolean bIncludeInfoAttr){
		IRadiusAttribute radiusAttr = getRadAttribute(id);
		if(!bIncludeInfoAttr)
			return radiusAttr;
		if(radiusAttr == null)
			radiusAttr = getRadInfoAttribute(id);
		return radiusAttr;
	}

	public Object clone() throws CloneNotSupportedException {
		RadiusPacket clonePacket = null;
		clonePacket = (RadiusPacket) super.clone();
		clonePacket.attributeList = new ArrayList<IRadiusAttribute>();
		clonePacket.attributeMap = new LinkedHashMap<String, ArrayList<IRadiusAttribute>>();
		clonePacket.infoAttributeList = new ArrayList<IRadiusAttribute>();
		clonePacket.infoAttributeMap = new LinkedHashMap<String, ArrayList<IRadiusAttribute>>();

		final int listSize = attributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute = (IRadiusAttribute) this.attributeList.get(i).clone(); 
			clonePacket.attributeList.add(radiusAttribute);
			ArrayList<IRadiusAttribute> values = clonePacket.attributeMap.get(String.valueOf(radiusAttribute.getType()));
			if (values == null){
				values = new ArrayList<IRadiusAttribute>();
				clonePacket.attributeMap.put(String.valueOf(radiusAttribute.getType()),values);
			}
			values.add(radiusAttribute);
		}

		final int infoListSize = infoAttributeList.size();
		for(int i=0;i<infoListSize;i++) {
			IRadiusAttribute radiusAttribute = (IRadiusAttribute) this.infoAttributeList.get(i).clone(); 
			clonePacket.infoAttributeList.add(radiusAttribute);
			ArrayList<IRadiusAttribute> values = clonePacket.infoAttributeMap.get(String.valueOf(radiusAttribute.getType()));
			if (values == null){
				values = new ArrayList<IRadiusAttribute>();
				clonePacket.infoAttributeMap.put(String.valueOf(radiusAttribute.getType()),values);
			}
			values.add(radiusAttribute);
		}

		System.arraycopy(this.authenticator, 0, clonePacket.authenticator, 0, this.authenticator.length);
		return clonePacket;
	}

	public int getInfoLength() {
		return infoLength;
	}

	public void setInfoLength(int infoLength) {
		this.infoLength = infoLength;
	}
	
	public void reencryptAttributes(byte[] oldAuthenticator, String oldSecret, byte[] newAuthenticator, String newSecret){
		List<IRadiusAttribute> attributes = getRadiusAttributes();
		if(attributes != null && !attributes.isEmpty()){
			final int size = attributes.size();
			for(int i=0; i<size; i++){
				IRadiusAttribute attr = attributes.get(i);
				attr.reencryptValue(oldSecret, oldAuthenticator, newSecret, newAuthenticator);
			}
		}
	}

	public void clearInfoAttributes() {
		this.infoAttributeList.clear();
		this.infoAttributeMap.clear();
	}

	/**
	 * Returns the packet type by parsing the header information from the provided
	 * packetBytes
	 * 
	 * @param packetBytes non-null bytes from which packet type is to be parsed
	 * @return packet type
	 * @throws NullPointerException if packetBytes are null
	 * @throws IllegalArgumentException if insufficient bytes are passed
	 */
	public static int parsePacketType(byte[] packetBytes) {
		checkNotNull(packetBytes, "packetBytes are null");
		checkArgument(packetBytes.length > 0, "Insufficient bytes to parse packet type");
		return packetBytes[0] & 0xFF;
	}

	public void addAttribute(String strAvpCode, String value) {
		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(strAvpCode);
		if(attribute != null){
			attribute.setStringValue(value);
			addAttribute(attribute);
		}
	}

	public void addAvp(String strAvpCode, List<String> valueList) {
		Iterator<String> itr = valueList.iterator();
		while (itr.hasNext()) {
			addAttribute(strAvpCode, itr.next());
		}
	}


	public void addAttribute(String strAvpCode, long value) {
		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(strAvpCode);
		if(attribute != null){
			attribute.setLongValue(value);
			addAttribute(attribute);
		}
	}

	public void addAttribute(String strAvpCode, int value) {
		IRadiusAttribute attribute = Dictionary.getInstance().getKnownAttribute(strAvpCode);
		if(attribute != null){
			attribute.setIntValue(value);
			addAttribute(attribute);
		}
	}
}
