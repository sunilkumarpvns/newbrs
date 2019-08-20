/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */

package com.elitecore.aaa.radius.service;

import java.util.Collection;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.core.servicex.UDPServiceRequest;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;


/**
 * 
 * @author baiju
 *
 */
public interface RadServiceRequest extends UDPServiceRequest, Cloneable {
	public AccountData getAccountData();
	public void setAccountData(AccountData accountdata);
	public String getClientIp();
	public int getClientPort();
	
	public IRadiusAttribute getRadiusAttribute(int radAttrID);
	public IRadiusAttribute getRadiusAttribute(int radAttr,boolean bIncludeInfoAttr);
	public IRadiusAttribute getRadiusAttribute(String radAttrID);
	public IRadiusAttribute getRadiusAttribute(String radAttrId,boolean bIncludeInfoAttr);
	public IRadiusAttribute getRadiusAttribute(long lVendorId,int ...attributeIds);
	public IRadiusAttribute getRadiusAttribute(boolean bIncludeInfoAttr,long lvendorId,int ...attributeIds);
	public Collection<IRadiusAttribute> getRadiusAttributes();
	
	/**
	 * Returns the list of all attributes present in request
	 * @param bIncludeInfoAttr specifies whether info level attributes are needed or not 
	 * @return Collection of all the attributes. <b>NOTE: Do not use this method to add new attribute to request.</b>
	 * @see RadServiceRequest#addInfoAttribute(IRadiusAttribute)
	 */
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr);
	
	/**
	 * Returns the collection of attributes of a vendor with specified attribute id
	 * @param lVendorId the vendor whose attributes are needed
	 * @param attributeIds the attribute-id of the attribute, of specified vendor 
	 * @return Collection of all attributes of specified vendor and attribute id <b>NOTE: Do not use this method to add new attribute to request.</b>
	 * @see RadServiceRequest#addInfoAttribute(IRadiusAttribute)
	 */
	public Collection<IRadiusAttribute> getRadiusAttributes(long lVendorId,int ...attributeIds);
	
	/**
	 * @see RadServiceRequest#addInfoAttribute(IRadiusAttribute)
	 * @param bIncludeInfoAttr specifies whether info level attributes are needed or not
	 * @return Collection of all attributes of specified vendor and attribute id <b>NOTE: Do not use this method to add new attribute to request.</b>
	 */
	public Collection<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr,long lVendorId,int ...attributeIds);
	
	/**
	 * @see RadServiceRequest#addInfoAttribute(IRadiusAttribute)
	 * @return Collection of all attributes of specified attribute id <b>NOTE: Do not use this method to add new attribute to request.</b>
	 */
	public Collection<IRadiusAttribute> getRadiusAttributes(int radAttrID);
	
	/**
	 * @see RadServiceRequest#addInfoAttribute(IRadiusAttribute)
	 * @param bIncludeInfoAttr specifies whether info level attributes are needed or not
	 * @return Collection of all attributes of specified attribute id <b>NOTE: Do not use this method to add new attribute to request.</b>
	 */
	public Collection<IRadiusAttribute> getRadiusAttributes(int radAttrID,boolean bIncludeInfoAttr);
	
	/**
	 * @see RadServiceRequest#addInfoAttribute(IRadiusAttribute)
	 * @return Collection of all attributes of specified attribute id <b>NOTE: Do not use this method to add new attribute to request.</b>
	 */
	public Collection<IRadiusAttribute> getRadiusAttributes(String radAttrID);
	
	/**
	 * @see RadServiceRequest#addInfoAttribute(IRadiusAttribute)
	 * @param bIncludeInfoAttr specifies whether info level attributes are needed or not
	 * @return Collection of all attributes of specified attribute id <b>NOTE: Do not use this method to add new attribute to request.</b>
	 */
	public Collection<IRadiusAttribute> getRadiusAttributes(String radAttrID,boolean bIncludeInfoAttr);
	
	/**
	 * Adds new attribute to request at <b>INFO</b> level
	 * @param attribute the attribute to be added to the request
	 */
	public void addInfoAttribute(IRadiusAttribute attribute);	
	public IRadiusAttribute getInfoAttribute(long lVendorId,int ...attributeId);
	public Collection<IRadiusAttribute>	getInfoAttributes(long lVendorId,int... attributeId);
	public Collection<IRadiusAttribute> getInfoAttributes();

	public void removeAttribute(IRadiusAttribute radiusAttribute, boolean includeRadiusAttribute);
	public byte[] getAuthenticator();		
	public int getIdentifier();
	public int getPacketType();
	
	public byte[] getRequestBytes(boolean bInfoAttribute);
	public void setRequestBytes(byte[] requestBytes);

	public void stopFurtherExecution();
	public void startFurtherExecution();
	
	public boolean isFutherExecutionStopped();
	
	public RadServiceRequest clone();
	public void clearInfoAttributes();
}
