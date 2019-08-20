package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class AttributeSequencer {
	public static final char SEPERATOR = ',';
	private String[] supportedAttributeArray;
	private String supportedAttrsStr;
	private boolean isStandardExist;
	private ArrayList<AttributeHandler> attributeHandlers;
	private ArrayList<String> supportedVendorAttrList;
	
	public AttributeSequencer(String supportedAttrsStr) {
		this.supportedAttrsStr = supportedAttrsStr;
		this.attributeHandlers = new ArrayList<AttributeHandler>();
		this.supportedVendorAttrList = new ArrayList<String>();
	}
	
	public void init(){
		this.supportedAttributeArray = ParserUtility.splitString(supportedAttrsStr, SEPERATOR);
		supportedVendorAttrList = getVendorAndAttributeList(supportedAttributeArray);
		if(supportedAttributeArray != null) {
			for(int i=0;i<supportedAttributeArray.length;i++) {
				AttributeHandler attributeHandler=null;
				String currentAttr = supportedAttributeArray[i];
				if(currentAttr!=null && currentAttr.trim().length()!=0) {
					attributeHandler = getAttributeHandlers(currentAttr);
					if(attributeHandler != null) {
						this.attributeHandlers.add(attributeHandler);
					}
				}
			}
			
		}
			if(!isStandardExist) {
				this.attributeHandlers.add(0, new VendorOnlyHandler("0", supportedVendorAttrList));
			}
		}
	
	private AttributeHandler getAttributeHandlers(String currentAttr) {
		String [] avpAndValue = ParserUtility.splitKeyAndValue(currentAttr);
		AttributeHandler attributeHandler = null;
		if(isAttributeAndValueConfigured(avpAndValue)) {
				attributeHandler = new AttributeAndValueHandler(currentAttr, supportedVendorAttrList);
		} else { 
			if(!isVendorOnly(avpAndValue)) {
				if(isValidVendorAndAttribute(avpAndValue)) {
					attributeHandler = new VendorAndAttributeHandler(currentAttr, supportedVendorAttrList);	
				}
			} else {
				if(isValidVendorId(avpAndValue)) {
					attributeHandler = new VendorOnlyHandler(currentAttr, supportedVendorAttrList);	
				}
			}	
		}
		
		if(!isStandardExist && avpAndValue[0] !=null && avpAndValue[0].trim().length()!=0) {
			if(avpAndValue[0].contains(":")) {
				String id = getVendorId(avpAndValue[0]);
				if(id!=null && String.valueOf(RadiusConstants.STANDARD_VENDOR_ID).equalsIgnoreCase(id)) {
							isStandardExist=true;
				}
			} else {
				if(avpAndValue[0].equalsIgnoreCase("0")) {
					isStandardExist = true;
				}
			}
		}
		return attributeHandler;
	}
	private String getVendorId(String vendorAttr) {
		String[] vendorAndAttr = vendorAttr.trim().split(":");
		if((vendorAndAttr.length==1 && !vendorAttr.contains(":")) || vendorAndAttr.length==2) {
			return vendorAndAttr[0];
		} else {
			return null;	
		}
	}
	private boolean isValidVendorId(String[] avpAndValue) {
		String[] vendor = avpAndValue[0].trim().split(":");
		if(avpAndValue[0]!= null && avpAndValue[0].trim().length()!=0 && vendor.length == 1) {
			return true;
		} else {
			return false;	
		}
	}

	private boolean isValidVendorAndAttribute(String[] avpAndValue) {
		if(avpAndValue[0]!=null) {
			String[] vendorAndAttr = avpAndValue[0].trim().split(":");
			if(vendorAndAttr.length==2) {
				return true;
			} else {
				return false;	
			}	
		} else {
			return false;
		}
	}

	private boolean isVendorOnly(String[] avpAndValue) {
		if(avpAndValue[0]!=null) {
			String[] vendorAndAttr = avpAndValue[0].trim().split(":");
			if(avpAndValue[0]!=null && avpAndValue[0].contains(":") && vendorAndAttr.length == 2 && avpAndValue[1] == null) {
				return false;
			} else {
				return true;
			}	
		} else {
			return false;
		}
	}
	
	private boolean isAttributeAndValueConfigured(String[] avpAndValue) {
		if(avpAndValue[0] != null && avpAndValue[0].trim().length()!=0 
				&& avpAndValue[1] != null && avpAndValue[1].trim().length()!=0
				&& avpAndValue[2] != null && avpAndValue[2].trim().length()!=0
				&& isValidVendorAndAttribute(avpAndValue)) {
			return true;
		} else {
			return false;
		}
	}
	private ArrayList<String> getVendorAndAttributeList(String[] vendorAttrArray) {
		ArrayList<String> vendorAttrList = new ArrayList<String>();
		if(vendorAttrArray!=null) {
			for(int i=0;i<vendorAttrArray.length;i++) {
				if(vendorAttrArray[i]!=null) {
					String currentAttr = vendorAttrArray[i].trim();
					if(currentAttr!=null && currentAttr.trim().length()!=0) {
						String [] avpAndValue = ParserUtility.splitKeyAndValue(currentAttr);
						if(avpAndValue[0]!=null && avpAndValue[0].trim().length()!=0) {
							if(avpAndValue[2]!=null && avpAndValue[2].trim().length()!=0) {
								vendorAttrList.add(avpAndValue[0]);	
							} else if(avpAndValue[1]==null) {
								vendorAttrList.add(avpAndValue[0]);
							}
						}
					}	
				}
			}	
		}
		return vendorAttrList;
	}
	
	public void handleRequest(RadiusPacket originalRadiusPacket) {
		List<IRadiusAttribute> attributes = (ArrayList<IRadiusAttribute>)originalRadiusPacket.getRadiusAttributes(true);
		RadiusPacket sequencedRadiusPacket = new RadiusPacket();
		if(attributeHandlers!=null && !attributeHandlers.isEmpty()) {
			for(int handlerIndex=0 ; handlerIndex<attributeHandlers.size() ; handlerIndex++ ) {
				ArrayList<IRadiusAttribute> attributeList = attributeHandlers.get(handlerIndex).getAttributes(originalRadiusPacket);
				if(attributeList!=null && !attributeList.isEmpty()) {
					for(int attrIndex=0 ; attrIndex < attributeList.size() ; attrIndex++) {
						sequencedRadiusPacket.addAttribute(attributeList.get(attrIndex));
					}
				}
			}	
		}
		if(attributes!=null && !attributes.isEmpty()) {
			for(int attrIndex=0 ; attrIndex < attributes.size() ; attrIndex++) {
				if(Dictionary.getInstance().isUnknownVendor(attributes.get(attrIndex).getVendorID())) {
					sequencedRadiusPacket.addAttribute(attributes.get(attrIndex));
				}
			}	
		}
		sequencedRadiusPacket.setPacketType(originalRadiusPacket.getPacketType());
		sequencedRadiusPacket.setIdentifier(originalRadiusPacket.getIdentifier());
		sequencedRadiusPacket.setAuthenticator(originalRadiusPacket.getAuthenticator());
		sequencedRadiusPacket.refreshPacketHeader();
		sequencedRadiusPacket.refreshInfoPacketHeader();
		originalRadiusPacket.setBytes(sequencedRadiusPacket.getBytes());
	}
}
