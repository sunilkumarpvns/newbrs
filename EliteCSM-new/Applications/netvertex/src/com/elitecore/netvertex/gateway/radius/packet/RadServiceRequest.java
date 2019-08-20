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

import com.elitecore.core.servicex.UDPServiceRequest;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

/**
 * 
 * @author baiju
 *
 */
public interface RadServiceRequest extends UDPServiceRequest {
	public String getClientIp();
	public int getClientPort();
	public byte[] getAuthenticator();
	public int getIdentifier();
	public int getPacketType();
	public IRadiusAttribute getRadiusAttribute(int radAttrID);
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,int radAttr);
	public IRadiusAttribute getRadiusAttribute(String radAttrID);
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,String radAttrId);
	public IRadiusAttribute getRadiusAttribute(long lVendorId,int ...attributeIds);
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,long lvendorId,int ...attributeIds);
	public Collection<IRadiusAttribute> getRadiusAttributes();
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr);
	public Collection<IRadiusAttribute> getRadiusAttributes(long lVendorId,int ...attributeIds);
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,long lVendorId,int ...attributeIds);
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,int radAttrID);
	public Collection<IRadiusAttribute> getRadiusAttributes(String radAttrID);
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,String radAttrID);
	public IRadiusAttribute getInfoAttribute(long lVendorId,int attributeId);
	
	/**
	 * Adds new attribute to request at <b>INFO</b> level
	 * @param attribute the attribute to be added to the request
	 */
	public void addInfoAttribute(IRadiusAttribute attribute);	
	public IRadiusAttribute getInfoAttribute(long lVendorId,int ...attributeId);
	public Collection<IRadiusAttribute>	getInfoAttributes(long lVendorId,int... attributeId);
	public Collection<IRadiusAttribute> getInfoAttributes();
	public byte[] getRequestBytes(boolean bInfoAttribute);
	public void setRequestBytes(byte[] requestBytes);
	
	/**
	 * @see RadServiceRequest#addInfoAttribute(IRadiusAttribute)
	 * @return Collection of all attributes of specified attribute id <b>NOTE: Do not use this method to add new attribute to request.</b>
	 */
	public Collection<IRadiusAttribute> getRadiusAttributes(int radAttrID);
	public void removeAttribute(boolean includeRadiusAttribute, IRadiusAttribute radiusAttribute);
}
