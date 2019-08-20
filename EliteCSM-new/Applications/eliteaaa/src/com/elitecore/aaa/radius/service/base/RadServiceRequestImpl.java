package com.elitecore.aaa.radius.service.base;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceRequestHash;
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

public abstract class RadServiceRequestImpl extends BaseUDPServiceRequest implements
		RadServiceRequest {
	private static final String MODULE = "RAD-SER-REQ";
	private RadiusPacket radiusRequestPacket;
	private AccountData accountData;		
	private Map<String, Object> parameterMap;
	private ArrayList<IRadiusAttribute> infoAttributeList;
	private volatile boolean stopCurrentOperatorExecution = false;
	private String currentOperator = Thread.currentThread().getName();
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
		this.currentOperator = Thread.currentThread().getName();
	}
	
	public byte[] getRequestBytes(boolean includeInfoAttr){
		if (!includeInfoAttr) {
			return super.getRequestBytes();
		}
		RadiusPacket requestPacket = null;
		try {
			requestPacket = (RadiusPacket) this.radiusRequestPacket.clone();
		} catch (CloneNotSupportedException e) {
			LogManager.getLogger().error(MODULE, "Error while creating clone of radius request packet. reason:" + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
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
				parentAttribute = null;
			}
			return childAttribute;
		}catch(InvalidAttributeIdException ex){
			return originalAttribute;
		}
	}
	
	@Override
	public AccountData getAccountData() {
		return accountData;
	}

	@Override
	public void setAccountData(AccountData accountdata) {
		this.accountData = accountdata;
	}

	@Override
	public IRadiusAttribute getRadiusAttribute(String radAttrID) {
		return radiusRequestPacket.getRadiusAttribute(radAttrID);
	}
	
	@Override
	public IRadiusAttribute getRadiusAttribute(String radAttrId,boolean bIncludeInfoAttr) {
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
	public IRadiusAttribute getRadiusAttribute(int radAttr,boolean bIncludeInfoAttr) {
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
		return radiusRequestPacket.getClientIP();
	}

	@Override
	public int getClientPort() {
		return radiusRequestPacket.getClientPort();
	}
	
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.print(this.radiusRequestPacket.toString());
		if(infoAttributeList.size() > 0)
			out.println("\t--Info Attributes");
		final int infoListSize = infoAttributeList.size();
		for(int i=0;i<infoListSize;i++) {
			IRadiusAttribute radiusAttribute = (IRadiusAttribute)infoAttributeList.get(i);
			if(radiusAttribute.getType() != RadiusAttributeConstants.VENDOR_SPECIFIC){
				out.print("\t" + Dictionary.getInstance().getAttributeName(radiusAttribute.getParentId()));
			}
			out.println(radiusAttribute.toString());
		}
		
		out.flush();
		out.close();
		return stringBuffer.toString();
	}

	//not cloning the list of attributes over here as packet itself does it
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes() {
		return radiusRequestPacket.getRadiusAttributes();
	}
	
	@SuppressWarnings("unchecked")
	public List<IRadiusAttribute> getInfoAttributes(){
		return (List<IRadiusAttribute>) this.infoAttributeList.clone();
	}
	
	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr) {
		List<IRadiusAttribute> attrList = (ArrayList<IRadiusAttribute>) radiusRequestPacket.getRadiusAttributes(bIncludeInfoAttr);
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
	public Collection<IRadiusAttribute> getRadiusAttributes(int radAttrID,boolean bIncludeInfoAttr) {
		return getRadiusAttributes(bIncludeInfoAttr, RadiusConstants.STANDARD_VENDOR_ID, radAttrID);
	}

	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(String radAttrID) {
		return radiusRequestPacket.getRadiusAttributes(radAttrID);
	}

	@Override
	public Collection<IRadiusAttribute> getRadiusAttributes(String radAttrID,boolean bIncludeInfoAttr) {
		AttributeId attrId;
		try {
			attrId = Dictionary.getInstance().getAttributeId(radAttrID);
			return getRadiusAttributes(bIncludeInfoAttr,attrId.getVendorId(),attrId.getAttrbuteId());
		} catch (InvalidAttributeIdException e) {
			return null;
		}
	}

	@Override
	public IRadiusAttribute getInfoAttribute(long lVendorId,int... attributeId) {			
		return this.getRadiusAttribute(this.infoAttributeList, lVendorId, attributeId);
	}
	@Override
	public Collection<IRadiusAttribute>	getInfoAttributes(long lVendorId,int... attributeId) {
		return this.getRadiusAttributes(this.infoAttributeList, lVendorId, attributeId);
	}
	private IRadiusAttribute getRadiusAttribute(ArrayList<IRadiusAttribute> attributeList,long lvendorId,int... attributeIds) {
		for(IRadiusAttribute radAttribute:attributeList){
			if(radAttribute.getVendorID() == lvendorId && radAttribute.getType() == attributeIds[0]){
				if(attributeIds.length == 1)
					return radAttribute;
				else{
					int newAttrIds[] = new int[attributeIds.length-1];
					System.arraycopy(attributeIds, 1, newAttrIds, 0, newAttrIds.length);
					IRadiusAttribute subAttribute = ((BaseRadiusAttribute)radAttribute).getSubAttribute(newAttrIds);
					if(subAttribute != null)
						return subAttribute;
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
					int newAttrIds[] = new int[attributeIds.length-1];
					System.arraycopy(attributeIds, 1, newAttrIds, 0, newAttrIds.length);
					IRadiusAttribute subAttr = ((BaseRadiusAttribute)radAttribute).getSubAttribute(newAttrIds);
					if (subAttr != null) {
					if(radAttributesList == null) {
						radAttributesList = new ArrayList<IRadiusAttribute>();
					}
						radAttributesList.add(subAttr);
					}
				}					
			}
		}
	return radAttributesList;
	}
	@Override
	public byte[] getAuthenticator() {
		return radiusRequestPacket.getAuthenticator();
	}
	@Override
	public int getIdentifier() {
		return radiusRequestPacket.getIdentifier();
	}
	
	public void stopFurtherExecution(){
		this.currentOperator = Thread.currentThread().getName();
		this.stopCurrentOperatorExecution = true;
	}
	
	public void startFurtherExecution() {
		this.stopCurrentOperatorExecution = false;
	}
	
	public boolean isFutherExecutionStopped() {
		if(this.currentOperator.equals(Thread.currentThread().getName())) {
			return stopCurrentOperatorExecution;
		}else {
			return false;
		}
	}
	@Override
	public int getPacketType() {
		return radiusRequestPacket.getPacketType();
	}
	@Override
	public void removeAttribute(IRadiusAttribute radiusAttribute,boolean includeRadiusAttribute) {
		if(!infoAttributeList.remove(radiusAttribute) && includeRadiusAttribute == true){
			radiusRequestPacket.removeAttribute(radiusAttribute);
		}
		
	}

	@Override
	public IPacketHash getPacketHash() {
		return new RadServiceRequestHash(getClientPort(), getIdentifier(), getAuthenticator());
	}
	
	@Override
	public RadServiceRequest clone() {
		try {
			RadServiceRequestImpl clone = (RadServiceRequestImpl)super.clone();
			clone.radiusRequestPacket = (RadiusPacket) radiusRequestPacket.clone();
			ArrayList<IRadiusAttribute> clonedInfoAttributes = new ArrayList<IRadiusAttribute>();
			for (IRadiusAttribute infoAttr : infoAttributeList) {
				clonedInfoAttributes.add((IRadiusAttribute)infoAttr.clone());
			}
			clone.infoAttributeList = clonedInfoAttributes;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	
	@Override
	public void clearInfoAttributes() {
		this.infoAttributeList.clear();
	}
	public SocketDetail getServerSocket() {
		return serverSocketDetail;
	}
}
