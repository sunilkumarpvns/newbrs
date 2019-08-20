/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */

package com.elitecore.netvertex.gateway.radius.packet;

import java.util.Collection;

import com.elitecore.core.servicex.UDPServiceResponse;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.netvertex.gateway.radius.RadiusGateway;

/**
 * 
 * @author baiju
 *
 */
public interface RadServiceResponse extends UDPServiceResponse {
	public boolean isFurtherProcessingRequired();
	public void setFurtherProcessingRequired(boolean value);
	public void addAttribute(IRadiusAttribute radiusAttribute);
	public void addInfoAttribute(IRadiusAttribute radiusAttribute);
	public IRadiusAttribute getRadiusAttribute(String radAttrID);
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,String radAttrID);
	public IRadiusAttribute getRadiusAttribute(int radAttrID);
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,int radAttrID);
	public IRadiusAttribute getRadiusAttribute(long lvendorId,int ... attributeIds);
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,long vendorId, int ...attrId);
	public Collection<IRadiusAttribute> getRadiusAttributes();
	public Collection<IRadiusAttribute> getRadiusInfoAttributes();
	public Collection<IRadiusAttribute> getRadiusAttributes(long lVendorId,int ...attributeIds);
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,long lVendorId,int ...attributeIds);
	public Collection<IRadiusAttribute> getRadiusAttributes(int radAttrID);
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,int radAttrID);
	public Collection<IRadiusAttribute> getRadiusAttributes(String radAttrID);
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,String radAttrID);
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr);
	public void setPacketType(int id);
	public void setResponseMessage(String responseMessage);
	public int getPacketType();
	public String getResponseMessege();
	void setRadiusGateway(RadiusGateway radiusGateway);
	String getClintIp();
	RadiusGateway getRadiusGateway();
	String getNASAddress();
	void setNASAddress(String nasAddress);
	public void removeAttribute(IRadiusAttribute radiusAttribute, boolean includeRadiusAttribute);
	public int getIdentifier();
}
