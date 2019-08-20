package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;

public class AttributeRemover {
	
	private SupportedVendor supportedVendor;
	private UnsupportedVendor unsupportedVendor;
	
	public AttributeRemover(String supportedAttrsStr, String unsupportedAttrsStr) {
		this.supportedVendor = new SupportedVendor(supportedAttrsStr);
		this.unsupportedVendor = new UnsupportedVendor(unsupportedAttrsStr);
	}

	public void init(){
		this.supportedVendor.init();
		this.unsupportedVendor.init();
		}
		
	public void handleRequest(RadiusPacket originalRadiusPacket) {
		List<IRadiusAttribute> attributes = (ArrayList<IRadiusAttribute>)originalRadiusPacket.getRadiusAttributes(true);
		int numOfAttributes = attributes.size();
		IRadiusAttribute currentAttribute;
		for(int currentAttrIndex=0; currentAttrIndex<numOfAttributes;currentAttrIndex++){
			currentAttribute = attributes.get(currentAttrIndex);
			if(!supportedVendor.isSupportedAttribute(currentAttribute)) {
				originalRadiusPacket.removeAttribute(currentAttribute);
					}
				}
		for(int currentAttrIndex=0; currentAttrIndex<numOfAttributes;currentAttrIndex++){
			currentAttribute = attributes.get(currentAttrIndex);
			if(!unsupportedVendor.isSupportedAttribute(currentAttribute)) {
				originalRadiusPacket.removeAttribute(currentAttribute);
			}
		}
	}
							}
