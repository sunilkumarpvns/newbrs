package com.elitecore.netvertex.gateway.radius.packet.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.base.BaseUDPServiceResponse;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusGroupedAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusPacketTypeConstant;
import com.elitecore.netvertex.gateway.radius.RadiusGateway;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceResponse;

public class RadServiceResponseImpl extends BaseUDPServiceResponse implements RadServiceResponse {
	
	private static final String MODULE = "RAD-RES-IMPL";
	private static final String SHARED_SECRET = "secret";
	
	private boolean isFurtherProcessingRequired;
	private boolean isPacketDirty;
	private byte[] requestAuthenticator;
	
	private int packetType;
	private int identifier;
	
	private Map<String, Object> responseParameterMap;
	private ArrayList<IRadiusAttribute> attributeList;
	private ArrayList<IRadiusAttribute> infoAttributeList;
	
	private String responseMessage;
	private RadiusPacket responsePacket;
	private RadiusGateway radiusGateway;
	private String nasAddress;

	public RadServiceResponseImpl(byte[] authenticator,int identifier) {
		this.requestAuthenticator = authenticator;
		this.identifier = identifier;
		attributeList = new ArrayList<IRadiusAttribute>();
		infoAttributeList = new ArrayList<IRadiusAttribute>();
		isFurtherProcessingRequired = true;
		isPacketDirty = true;
		responseParameterMap = new HashMap<String, Object>();
	}
	@Override
	public void addAttribute(IRadiusAttribute radiusAttribute) {
		if(radiusAttribute.getLevel() > 1){
			radiusAttribute = wrapInParentAttributes(radiusAttribute);
		}
		
		attributeList.add(radiusAttribute);
		isPacketDirty = true;
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
	
	public void addInfoAttribute(IRadiusAttribute radiusAttribute) {
		if(radiusAttribute.getLevel() > 1){
			radiusAttribute = wrapInParentAttributes(radiusAttribute);
		}
		
		infoAttributeList.add(radiusAttribute);
		isPacketDirty = true;
		infoAttributeList.add(radiusAttribute);
		isPacketDirty = true;
	}	
		
	@Override
	public byte[] getResponseBytes() {
		
		if (isPacketDirty) {
			responsePacket = generatePacket();
			isPacketDirty = false;
		}
		return responsePacket.getBytes();
	}
	@Override
	public boolean isFurtherProcessingRequired() {
		return isFurtherProcessingRequired;
	}

	@Override
	public void setFurtherProcessingRequired(boolean value) {
		this.isFurtherProcessingRequired = value;
	}
	
	@Override
	public void setPacketType(int id) {			
		this.packetType = id;
		isPacketDirty = true;
	}

	@Override

	public int getPacketType() {
		return packetType;
	}

	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.println();
		out.println("\t" + "Packet Type: " + RadiusPacketTypeConstant.from(getPacketType()));
		out.println("\t" + "Identifier: " + identifier);
		if(responseMessage != null) {
			out.println("\t" + "Reply Message: " + responseMessage);
		}
		final int listSize = attributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute = attributeList.get(i);
			if(radiusAttribute.getVendorID() != RadiusConstants.STANDARD_VENDOR_ID){
				out.print("\t" + Dictionary.getInstance().getVendorName(radiusAttribute.getVendorID()));
			}
			out.print("\t" + Dictionary.getInstance().getAttributeName(radiusAttribute.getVendorID(), radiusAttribute.getType()));
			out.println(radiusAttribute.toString());
		}
		if(infoAttributeList.isEmpty() == false)
			out.println("\t--Info Attributes");
		final int infoListSize = infoAttributeList.size();
		for(int i=0;i<infoListSize;i++) {
			IRadiusAttribute radiusAttribute = infoAttributeList.get(i);
			if(radiusAttribute.getType() != RadiusAttributeConstants.VENDOR_SPECIFIC){
				out.print("\t" + Dictionary.getInstance().getAttributeName(radiusAttribute.getVendorID(),radiusAttribute.getType()));
			}
			out.println(radiusAttribute.toString());
		}
		out.flush();
		out.close();
		return stringBuffer.toString();
	}
	
	
	public Object getParameter(String str){
		return responseParameterMap.get(str);
	}

	public void setParameter(String key, Object parameterValue) {
		responseParameterMap.put(key, parameterValue);
	}

	@Override
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(int radAttrID) {
		return getRadiusAttribute(false,radAttrID);			
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,int radAttrID) {
		return getRadiusAttribute(bIncludeInfoAttr,RadiusConstants.STANDARD_VENDOR_ID,radAttrID);
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(String radAttrID) {
		return getRadiusAttribute(false,radAttrID);
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,String radAttrID) {
		AttributeId attrId;
		try {
			attrId = Dictionary.getInstance().getAttributeId(radAttrID);
			return getRadiusAttribute(bIncludeInfoAttr,attrId.getVendorId(),attrId.getAttrbuteId()); 
			
		} catch (InvalidAttributeIdException e) {
			LogManager.ignoreTrace(e);
			return null;
		}
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(long lvendorId,int... attributeIds) {
		return getRadiusAttribute(false,lvendorId,attributeIds);
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,long lVendorId,int ... attributeIds) {
		IRadiusAttribute radAttribute = getRadiusAttribute(getAttributeList(),lVendorId,attributeIds);
		if(!bIncludeInfoAttr)
			return radAttribute;
		else if(radAttribute == null)
			radAttribute = getRadiusAttribute(getInfoAttributeList(),lVendorId,attributeIds);
		return radAttribute;
	}
	
	private IRadiusAttribute getRadiusAttribute(ArrayList<IRadiusAttribute> attributeList,long lvendorId,int... attributeIds) {
		for(IRadiusAttribute radAttribute:attributeList){
			if(radAttribute.getVendorID() == lvendorId && radAttribute.getType() == attributeIds[0]){
				if(attributeIds.length == 1)
					return radAttribute;
				else{
					int[] newAttrIds = new int[attributeIds.length-1];
					System.arraycopy(attributeIds, 1, newAttrIds, 0, newAttrIds.length);
					return ((BaseRadiusAttribute)radAttribute).getSubAttribute(newAttrIds);
				}
			}
		}
		return null;
	}
	
	public RadiusPacket generatePacket() {
		RadiusPacket responsePacket = new RadiusPacket();			
		responsePacket.setPacketType(getPacketType());
		responsePacket.setIdentifier(getIdentifier());
		responsePacket.addAttributes(getAttributeList());
		
		if(getResponseMessege() != null) {
			IRadiusAttribute radAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.REPLY_MESSAGE);
			radAttribute.setStringValue(getResponseMessege());
			responsePacket.addAttribute(radAttribute);
		}
		IRadiusAttribute msgAuthenticatorAttribute = responsePacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		if (responsePacket.getRadiusAttribute(RadiusAttributeConstants.EAP_MESSAGE)!=null || msgAuthenticatorAttribute!=null) {
			if(msgAuthenticatorAttribute == null){
				msgAuthenticatorAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);				
				responsePacket.addAttribute(msgAuthenticatorAttribute);
			}
			msgAuthenticatorAttribute.setValueBytes(new byte[16]);
			responsePacket.refreshPacketHeader();
			
			responsePacket.setAuthenticator(getRequestAuthenticator());
			msgAuthenticatorAttribute.setValueBytes(RadiusUtility.HMAC("MD5", responsePacket.getBytes(), getSharedSecret()));
		}
		responsePacket.refreshPacketHeader();
		responsePacket.refreshInfoPacketHeader();
		responsePacket.setAuthenticator(RadiusUtility.generateRFC2865ResponseAuthenticator(responsePacket,getRequestAuthenticator(),getSharedSecret()));
		return responsePacket;
	}
	
	private String getSharedSecret() {
		if(radiusGateway != null && radiusGateway.getSharedSecret() != null) {
			return radiusGateway.getSharedSecret();
		} else {
			if(radiusGateway != null)
				LogManager.getLogger().warn(MODULE, "Shared secret getting null for gateway: " + radiusGateway.getIPAddress() + " So taking default.");
			return SHARED_SECRET;
		}
	}
	
	public byte[] getRequestAuthenticator(){
		return requestAuthenticator;
	}

	@Override
	public int getIdentifier(){
		return identifier;
	}
	
	@Override
	public String getResponseMessege(){
		return responseMessage;
	}
	
	@Override
	public String getClintIp() {
		if(radiusGateway != null){
			return radiusGateway.getIPAddress();
		}
		return null;
	}
	
	@Override
	public void setRadiusGateway(RadiusGateway radiusGateway) {
		this.radiusGateway = radiusGateway;
	}
	
	public ArrayList<IRadiusAttribute> getAttributeList() {
		return this.attributeList;
	}
	
	public ArrayList<IRadiusAttribute> getInfoAttributeList() {
		return this.infoAttributeList;
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes() {
		return getRadiusAttributes(false);
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusInfoAttributes() {
		return getInfoAttributeList();
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(int radAttrID) {
		return getRadiusAttributes(false,radAttrID);
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,int radAttrID) {
		return getRadiusAttributes(bIncludeInfoAttr, RadiusConstants.STANDARD_VENDOR_ID, radAttrID);
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(String radAttrID) {
		return getRadiusAttributes(false,radAttrID);
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,String radAttrID) {
		AttributeId attrId;
		try {
			attrId = Dictionary.getInstance().getAttributeId(radAttrID);
			return getRadiusAttributes(bIncludeInfoAttr,attrId.getVendorId(),attrId.getAttrbuteId()); 
			
		} catch (InvalidAttributeIdException e) {
			LogManager.ignoreTrace(e);
			return null;
		}
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(long lVendorId,int... attributeIds) {
		return getRadiusAttributes(false,lVendorId,attributeIds);
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr, long lVendorId, int... attributeIds) {
		Collection<IRadiusAttribute> radAttributesCollection = getRadiusAttributes(getAttributeList(),lVendorId,attributeIds);
		if(!bIncludeInfoAttr)
			return radAttributesCollection;
		else if(radAttributesCollection != null && !radAttributesCollection.isEmpty()) {
			Collection<IRadiusAttribute> infoCollection = getRadiusAttributes(getInfoAttributeList(),lVendorId,attributeIds);
			if(infoCollection != null && !infoCollection.isEmpty())
				radAttributesCollection.addAll(infoCollection);
		}
		else
			radAttributesCollection = getRadiusAttributes(getInfoAttributeList(),lVendorId,attributeIds);
	return radAttributesCollection;
	}
	
	private Collection<IRadiusAttribute> getRadiusAttributes(ArrayList<IRadiusAttribute> attributeList,long lvendorId,int... attributeIds) {
		ArrayList<IRadiusAttribute> radAttributesList = null;
		for(IRadiusAttribute radAttribute:attributeList){
			if(radAttribute.getVendorID() == lvendorId && radAttribute.getType() == attributeIds[0]) {
				if(attributeIds.length == 1) {
					if(radAttributesList == null) {
						radAttributesList = new ArrayList<IRadiusAttribute>();
					}
				radAttributesList.add(radAttribute);
				} else {
					int[] newAttrIds = new int[attributeIds.length-1];
					System.arraycopy(attributeIds, 1, newAttrIds, 0, newAttrIds.length);
					if(radAttributesList == null) {
						radAttributesList = new ArrayList<IRadiusAttribute>();
					}
					radAttributesList.add(((BaseRadiusAttribute)radAttribute).getSubAttribute(newAttrIds));
				}					
			}
		}
	return radAttributesList;
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr) {
		ArrayList<IRadiusAttribute> radAttributesList = (ArrayList<IRadiusAttribute>)getRadiusAttributes();
		ArrayList<IRadiusAttribute> newRadAttributesList = new ArrayList<IRadiusAttribute>();
		newRadAttributesList.addAll(radAttributesList);
		
		if(!bIncludeInfoAttr)
			return newRadAttributesList;
		else if(newRadAttributesList != null && !newRadAttributesList.isEmpty()) {
			ArrayList<IRadiusAttribute> infoArrayList = (ArrayList<IRadiusAttribute>) getRadiusInfoAttributes();
			if(infoArrayList != null && !infoArrayList.isEmpty())
				newRadAttributesList.addAll(infoArrayList);
		}else{
			newRadAttributesList = (ArrayList<IRadiusAttribute>) getRadiusInfoAttributes(); 
		}
	return newRadAttributesList;
	}
	@Override
	public RadiusGateway getRadiusGateway() {
		return radiusGateway;
	}
	@Override
	public String getNASAddress() {
		return nasAddress;
	}
	@Override
	public void setNASAddress(String nasAddress) {
		this.nasAddress = nasAddress;
	}
	@Override
	public void removeAttribute(IRadiusAttribute radiusAttribute, boolean includeRadiusAttribute) {
		infoAttributeList.remove(radiusAttribute);
		if(includeRadiusAttribute){
			attributeList.remove(radiusAttribute);
		}
		
	}
}
