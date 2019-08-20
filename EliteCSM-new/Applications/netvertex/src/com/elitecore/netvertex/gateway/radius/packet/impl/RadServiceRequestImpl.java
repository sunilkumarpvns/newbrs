package com.elitecore.netvertex.gateway.radius.packet.impl;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.IPacketHash;
import com.elitecore.core.servicex.base.BaseUDPServiceRequest;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusGroupedAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequest;
import com.elitecore.netvertex.gateway.radius.packet.RadServiceRequestHash;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.*;

public class RadServiceRequestImpl extends BaseUDPServiceRequest implements RadServiceRequest {
	
	private ArrayList<IRadiusAttribute> infoAttributeList;
	private RadiusPacket radiusRequestPacket;
	private Map<String, Object> parameterMap;
	private final SocketDetail serverSocketDetail;
	
	public RadServiceRequestImpl(byte[] requestBytes, InetAddress sourceAddress, int sourcePort, SocketDetail serverSocketDetail) {
		super(requestBytes, sourceAddress, sourcePort);
		this.serverSocketDetail = serverSocketDetail;
		radiusRequestPacket = new RadiusPacket();
		radiusRequestPacket.setBytes(requestBytes);
		radiusRequestPacket.setClientIP(sourceAddress.getHostAddress());
		radiusRequestPacket.setClientPort(sourcePort);
		parameterMap= new HashMap<String, Object>();
		infoAttributeList = new ArrayList<IRadiusAttribute>();
	}
	
	public RadiusPacket getRadiusPacket() {
		return this.radiusRequestPacket;
	}
	
	@Override
	public void addInfoAttribute(IRadiusAttribute radiusAttribute){
		if(radiusAttribute.getLevel() > 1){
			radiusAttribute = wrapInParentAttributes(radiusAttribute);
		}
		infoAttributeList.add(radiusAttribute);
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

	@Override
	public IRadiusAttribute getRadiusAttribute(String radAttrID) {
		return radiusRequestPacket.getRadiusAttribute(radAttrID);
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,String radAttrId) {
		IRadiusAttribute radiusAttribute = radiusRequestPacket.getRadiusAttribute(radAttrId, bIncludeInfoAttr);
		if(!bIncludeInfoAttr){
			return radiusAttribute;
		}
		if(radiusAttribute == null){
			AttributeId attributeId;
			try {
				attributeId = Dictionary.getInstance().getAttributeId(radAttrId);
				return getInfoAttribute(attributeId.getVendorId(), attributeId.getAttrbuteId());
			} catch (InvalidAttributeIdException e) {
				LogManager.ignoreTrace(e);
				return null;				
			}
		}
		return radiusAttribute;
	}

	public Object getParameter(String str){
		return parameterMap.get(str);
		
		
	}

	public void setParameter(String key, Object parameterValue) {
		parameterMap.put(key, parameterValue);
	}

	@Override
	public IRadiusAttribute getRadiusAttribute(int radAttrID) {
		return radiusRequestPacket.getRadiusAttribute(radAttrID);
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,int radAttr) {
		return getRadiusAttribute(bIncludeInfoAttr,RadiusConstants.STANDARD_VENDOR_ID,radAttr);
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(long lVendorId,int ...attributeIds) {
		return radiusRequestPacket.getRadiusAttribute(lVendorId, attributeIds);
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,long lvendorId,int ...attributeIds) {
		IRadiusAttribute radiusAttribute = radiusRequestPacket.getRadiusAttribute(lvendorId, bIncludeInfoAttr, attributeIds);
		if(!bIncludeInfoAttr){
			return radiusAttribute;
		}
		if(radiusAttribute == null){
			return this.getInfoAttribute(lvendorId, attributeIds);
		}
		return radiusAttribute; 
	}

	@Override
	public String getClientIp() {
		return getRadiusPacket().getClientIP();
	}

	@Override
	public int getClientPort() {
		return getRadiusPacket().getClientPort();
	}
	
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.print(this.radiusRequestPacket.toString());
		if(infoAttributeList.isEmpty() == false) {
			out.println("\t--Info Attributes");
			final int infoListSize = infoAttributeList.size();
			for (int i = 0; i < infoListSize; i++) {
				IRadiusAttribute radiusAttribute = infoAttributeList.get(i);
				if (radiusAttribute.getType() != RadiusAttributeConstants.VENDOR_SPECIFIC) {
					out.print("\t" + Dictionary.getInstance().getAttributeName(radiusAttribute.getParentId()));
				}
				out.println(radiusAttribute.toString());
			}
		}
		out.flush();
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes() {
		return radiusRequestPacket.getRadiusAttributes();
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr) {
		List<IRadiusAttribute> attrList = radiusRequestPacket.getRadiusAttributes(bIncludeInfoAttr);
		if(!bIncludeInfoAttr)
			return attrList;
		
		if(attrList != null && !attrList.isEmpty()){
			List<IRadiusAttribute> infoArrayList = getInfoAttributes();
			if(infoArrayList != null && !infoArrayList.isEmpty()){
				infoArrayList.addAll(attrList);
				attrList = infoArrayList;
			}
		}else{
			attrList = getInfoAttributes(); 
		}
		return attrList;
	}

	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(long lVendorId,int... attributeIds) {
		return radiusRequestPacket.getRadiusAttributes(lVendorId, attributeIds);
	}

	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,long lVendorId, int... attributeIds) {
		Collection<IRadiusAttribute> radiusAttributes = radiusRequestPacket.getRadiusAttributes(lVendorId, bIncludeInfoAttr, attributeIds);
		if(!bIncludeInfoAttr){
			return radiusAttributes;
		}
		if(radiusAttributes != null && !radiusAttributes.isEmpty() ){
			Collection<IRadiusAttribute> radInfoCollection = this.getRadiusAttributes(this.infoAttributeList, lVendorId, attributeIds);
			if(radInfoCollection != null && !radInfoCollection.isEmpty()){
				radiusAttributes.addAll(radInfoCollection);
			}
		}else{
			return getRadiusAttributes(this.infoAttributeList, lVendorId, attributeIds);
		}
		return radiusAttributes;
	}

	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(int radAttrID) {
		return radiusRequestPacket.getRadiusAttributes(radAttrID);
	}

	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,int radAttrID) {
		return getRadiusAttributes(bIncludeInfoAttr, RadiusConstants.STANDARD_VENDOR_ID, radAttrID);
	}

	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(String radAttrID) {
		return radiusRequestPacket.getRadiusAttributes(radAttrID);
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
	public IRadiusAttribute getInfoAttribute(long lVendorId,int attributeId) {
		
		return this.getRadiusAttribute(this.infoAttributeList, lVendorId, attributeId);
	}

	@Override
	public byte[] getAuthenticator() {
		return radiusRequestPacket.getAuthenticator();
	}

	@Override
	public int getIdentifier() {
		return radiusRequestPacket.getIdentifier();
	}

	@Override
	public int getPacketType() {
		return radiusRequestPacket.getPacketType();
	}
	
	@Override
	public IPacketHash getPacketHash() {
		return new RadServiceRequestHash(getClientPort(), getIdentifier(), getAuthenticator());
	}

	@Override
	public IRadiusAttribute getInfoAttribute(long lVendorId, int... attributeId) {
		return this.getRadiusAttribute(this.infoAttributeList, lVendorId, attributeId);
	}

	@Override
	public Collection<IRadiusAttribute> getInfoAttributes(long lVendorId, int... attributeId) {
		return this.getRadiusAttributes(this.infoAttributeList, lVendorId, attributeId);
	}
	
	private IRadiusAttribute getRadiusAttribute(ArrayList<IRadiusAttribute> attributeList,long lvendorId,int... attributeIds) {
		for(IRadiusAttribute radAttribute : attributeList){
			if(radAttribute.getVendorID() == lvendorId && radAttribute.getType() == attributeIds[0]){
				if(attributeIds.length == 1)
					return radAttribute;
				else{
					int [] newAttrIds = new int[attributeIds.length-1];
					System.arraycopy(attributeIds, 1, newAttrIds, 0, newAttrIds.length);
					return ((BaseRadiusAttribute)radAttribute).getSubAttribute(newAttrIds);
				}
			}
		}
		return null;
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
					int [] newAttrIds = new int[attributeIds.length-1];
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

	public List<IRadiusAttribute> getInfoAttributes(){
		return (List<IRadiusAttribute>) this.infoAttributeList.clone();
	}

	@Override
	public byte[] getRequestBytes(boolean bInfoAttribute) {
		if (!bInfoAttribute) {
			return super.getRequestBytes();
		}
		RadiusPacket requestPacket = null;
		try {
			requestPacket = (RadiusPacket) this.radiusRequestPacket.clone();
		} catch (CloneNotSupportedException e) {
			LogManager.ignoreTrace(e);
		}
		requestPacket.addAttributes(infoAttributeList);
		requestPacket.refreshPacketHeader();
		return requestPacket.getBytes();			
	}
	
	public void setRequestBytes(byte[] requestBytes){
		if(requestBytes != null){
			super.setRequestBytes(requestBytes);
			this.radiusRequestPacket.setBytes(requestBytes);
		}
	}

	@Override
	public void removeAttribute(boolean includeRadiusAttribute, IRadiusAttribute radiusAttribute) {
		if(!infoAttributeList.remove(radiusAttribute) && includeRadiusAttribute == true){
			radiusRequestPacket.removeAttribute(radiusAttribute);
		}
	}

	@Override
	public SocketDetail getServerSocket() {
		return serverSocketDetail;
	}
}
